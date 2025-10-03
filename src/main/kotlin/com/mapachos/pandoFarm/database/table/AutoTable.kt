package com.mapachos.pandoFarm.database.table

import com.google.gson.reflect.TypeToken
import com.mapachos.pandoFarm.database.MySQLManager
import com.mapachos.pandoFarm.util.async
import java.lang.reflect.Type
import java.sql.ResultSet
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.memberProperties

abstract class AutoTable<T : Any>(
    private val mysqlManager: MySQLManager,
    private val clazz: KClass<T>,
    private val tableName: String,
    private val primaryKey: String = "uuid"
) : AbstractTable(mysqlManager) {

    private val columns: List<Column<*>> = clazz.memberProperties.map {
        val isNullable = it.returnType.isMarkedNullable
        val isPrimaryKey = it.name == primaryKey
        val javaType = (it.returnType.classifier as? KClass<*>)?.java
            ?: error("Cannot resolve Java type for property: ${it.name}")

        Column(
            name = it.name,
            type = javaType,
            primaryKey = isPrimaryKey,
            nullable = isNullable
        )
    }

    private val constructor = clazz.primaryConstructor ?: clazz.constructors.minByOrNull { it.parameters.size } ?: clazz.constructors.first()
    private val gson = mysqlManager.gson

    override fun tableName() = tableName

    override fun createTable() {
        val sql = buildString {
            append("CREATE TABLE IF NOT EXISTS `$tableName` (\n")
            append(columns.joinToString(",\n") { it.definition() })
            append("\n);")
        }

        mysqlManager.withConnection { conn ->
            conn.createStatement().use { it.execute(sql) }
            // Create simple secondary indexes (except primary key) to improve lookups
            columns.filter { !it.primaryKey }.forEach { col ->
                try {
                    conn.createStatement().use { st -> st.execute("CREATE INDEX IF NOT EXISTS `${col.name}_idx` ON `$tableName`(`${col.name}`);") }
                } catch (_: Exception) {}
            }
        }
    }

    fun insertOrUpdate(obj: T) {
        val props = clazz.memberProperties
        val names = props.joinToString(",") { "`${it.name}`" }
        val placeholders = props.joinToString(",") { "?" }
        val updates = props.filter { it.name != primaryKey }.joinToString(",") { "`${it.name}` = VALUES(`${it.name}`)" }
        val sql = "INSERT INTO `$tableName` ($names) VALUES ($placeholders) ON DUPLICATE KEY UPDATE $updates;"
        async { executeSingle(sql, props, obj) }
    }

    fun insertBatch(objs: Collection<T>) {
        if(objs.isEmpty()) return
        val props = clazz.memberProperties
        val names = props.joinToString(",") { "`${it.name}`" }
        val placeholders = props.joinToString(",") { "?" }
        val updates = props.filter { it.name != primaryKey }.joinToString(",") { "`${it.name}` = VALUES(`${it.name}`)" }
        val sql = "INSERT INTO `$tableName` ($names) VALUES ($placeholders) ON DUPLICATE KEY UPDATE $updates;"
        async {
            mysqlManager.withConnection { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    objs.forEach { obj ->
                        props.forEachIndexed { index, prop ->
                            stmt.setObject(index + 1, serializeValue(prop.get(obj)))
                        }
                        stmt.addBatch()
                    }
                    stmt.executeBatch()
                }
            }
        }
    }

    private fun executeSingle(sql: String, props: Collection<kotlin.reflect.KProperty1<T, *>>, obj: T) {
        mysqlManager.withConnection { conn ->
            conn.prepareStatement(sql).use { statement ->
                props.forEachIndexed { index, prop ->
                    statement.setObject(index + 1, serializeValue(prop.get(obj)))
                }
                statement.executeUpdate()
            }
        }
    }

    fun findBy(field: String, value: Any): T? {
        if (columns.none { it.name.equals(field, true) }) {
            throw IllegalArgumentException("Invalid field name: $field")
        }
        val sql = "SELECT * FROM `$tableName` WHERE `$field` = ? LIMIT 1;"
        return mysqlManager.withConnection { conn ->
            conn.prepareStatement(sql).use { st ->
                st.setObject(1, if (value is UUID) value.toString() else value)
                val rs = st.executeQuery()
                if (rs.next()) buildFromResultSet(rs) else null
            }
        }
    }

    fun getAll(limit: Int? = null): List<T> {
        val base = "SELECT * FROM `$tableName`"
        val sql = if(limit != null) "$base LIMIT ${limit}" else base
        return queryList(sql)
    }

    fun getPage(offset: Int, pageSize: Int): List<T> {
        val sql = "SELECT * FROM `$tableName` LIMIT $pageSize OFFSET $offset;"
        return queryList(sql)
    }

    private fun queryList(sql: String): List<T> {
        val results = mutableListOf<T>()
        return mysqlManager.withConnection { conn ->
            conn.prepareStatement(sql).use { st ->
                val rs = st.executeQuery()
                while (rs.next()) {
                    buildFromResultSet(rs)?.let { results.add(it) }
                }
            }
            results
        }
    }

    private fun buildFromResultSet(rs: ResultSet): T? {
        val args = constructor.parameters.map { param ->
            val name = param.name ?: return null
            val prop = clazz.memberProperties.find { it.name == name } ?: return null
            val raw = rs.getObject(name)
            if(raw == null) return@map null
            val kClass = prop.returnType.classifier as? KClass<*>
            when {
                kClass == UUID::class -> UUID.fromString(raw.toString())
                kClass == List::class -> gson.fromJson(raw.toString(), getType(prop.returnType))
                kClass != null && !isPrimitiveOrString(kClass) && kClass != String::class -> gson.fromJson(raw.toString(), getType(prop.returnType))
                else -> raw
            }
        }
        return constructor.call(*args.toTypedArray())
    }

    private fun serializeValue(value: Any?): Any? = when(value) {
        null -> null
        is UUID -> value.toString()
        is List<*> , is Map<*,*> -> gson.toJson(value)
        else -> {
            val clazz = value::class
            if(!isPrimitiveOrString(clazz)) gson.toJson(value) else value
        }
    }

    private fun isPrimitiveOrString(kClass: KClass<*>): Boolean = kClass == String::class || kClass.java.isPrimitive ||
            kClass == Int::class || kClass == Long::class || kClass == Double::class ||
            kClass == Float::class || kClass == Boolean::class || kClass == Char::class

    private fun getType(type: KType): Type {
        val classifier = type.classifier as? KClass<*> ?: error("Unknown classifier: $type")
        val javaType = when (classifier) {
            Double::class -> java.lang.Double::class.java
            Float::class -> java.lang.Float::class.java
            Int::class -> Integer::class.java
            Long::class -> java.lang.Long::class.java
            Boolean::class -> java.lang.Boolean::class.java
            else -> classifier.java
        }
        if (type.arguments.isEmpty()) return javaType
        val innerType = getType(type.arguments.first().type!!)
        return TypeToken.getParameterized(javaType, innerType).type
    }
}