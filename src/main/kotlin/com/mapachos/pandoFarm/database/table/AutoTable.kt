package gg.cloudworld.map.database.table

import com.google.gson.reflect.TypeToken
import gg.cloudworld.map.database.MySQLManager
import gg.cloudworld.map.util.async
import java.lang.reflect.Type
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties

abstract class AutoTable<T : Any>(
    mysql: MySQLManager,
    private val clazz: KClass<T>,
    private val tableName: String,
    private val primaryKey: String = "uuid"
) : AbstractTable(mysql) {

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

    override fun tableName() = tableName

    override fun createTable() {
        val sql = buildString {
            append("CREATE TABLE IF NOT EXISTS `$tableName` (\n")
            append(columns.joinToString(",\n") { it.definition() })
            append("\n);")
        }

        connection.createStatement().use {
            it.execute(sql)
        }
    }

    fun insertOrUpdate(obj: T) {
        val props = clazz.memberProperties
        val names = props.joinToString(",") { "`${it.name}`" }
        val placeholders = props.joinToString(",") { "?" }

        val sql = "REPLACE INTO `$tableName` ($names) VALUES ($placeholders);"

        async {
            connection.prepareStatement(sql).use { statement ->
                val gson = mysql.gson.newBuilder().serializeSpecialFloatingPointValues().create()
                props.forEachIndexed { index, prop ->
                    val value = prop.get(obj)
                    val finalValue = when {
                        value is UUID -> value.toString()
                        value is List<*> || value is Map<*, *> -> gson.toJson(value)
                        value != null && !isPrimitiveOrString(value::class) -> gson.toJson(value)
                        else -> value
                    }
                    statement.setObject(index + 1, finalValue)
                }
                statement.executeUpdate()
            }
        }
    }


    fun findBy(field: String, value: Any): T? {
        val columnExists = columns.any { it.name.equals(field, ignoreCase = true) }
        if (!columnExists) {
            throw IllegalArgumentException("Invalid field name: $field")
        }

        val sql = "SELECT * FROM `$tableName` WHERE `$field` = ? LIMIT 1;"

        connection.prepareStatement(sql).use { statement ->
            val finalValue = when (value) {
                is UUID -> value.toString()
                else -> value
            }
            statement.setObject(1, finalValue)
            val rs = statement.executeQuery()

            return if (rs.next()) {
                val constructor = clazz.constructors.first()
                val args = constructor.parameters.map { param ->
                    val name = param.name ?: error("Missing parameter name for constructor")
                    val raw = rs.getObject(name)

                    val prop = clazz.memberProperties.find { it.name == param.name }!!
                    val type = prop.returnType
                    when {
                        raw == null -> null
                        prop.returnType.classifier == UUID::class -> UUID.fromString(raw.toString())
                        prop.returnType.classifier == List::class -> {
                            val listType = getType(type)
                            mysql.gson.fromJson(raw.toString(), listType)
                        }
                        !isPrimitiveOrString(prop.returnType.classifier as KClass<*>) -> mysql.gson.fromJson(raw.toString(), getType(type))
                        else -> raw
                    }
                }
                constructor.call(*args.toTypedArray())
            } else null
        }
    }

    private fun isPrimitiveOrString(kClass: KClass<*>): Boolean {
        return kClass == String::class ||
                kClass.java.isPrimitive ||
                kClass == Int::class || kClass == Long::class ||
                kClass == Double::class || kClass == Float::class ||
                kClass == Boolean::class || kClass == Char::class
    }

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

        if (type.arguments.isEmpty()) {
            return javaType
        }

        val innerType = getType(type.arguments.first().type!!)
        return TypeToken.getParameterized(javaType, innerType).type
    }

    fun getAll(): List<T> {
        val sql = "SELECT * FROM `$tableName`;"
        val results = mutableListOf<T>()

        connection.prepareStatement(sql).use { statement ->
            val rs = statement.executeQuery()

            while (rs.next()) {
                val constructor = clazz.constructors.first()
                val args = constructor.parameters.map { param ->
                    val name = param.name ?: error("Missing parameter name for constructor")
                    val raw = rs.getObject(name)

                    val prop = clazz.memberProperties.find { it.name == param.name }!!
                    val type = prop.returnType
                    when {
                        raw == null -> null
                        prop.returnType.classifier == UUID::class -> UUID.fromString(raw.toString())
                        prop.returnType.classifier == List::class -> {
                            val listType = getType(type)
                            mysql.gson.fromJson(raw.toString(), listType)
                        }
                        !isPrimitiveOrString(prop.returnType.classifier as KClass<*>) -> mysql.gson.fromJson(raw.toString(), getType(type))
                        else -> raw
                    }
                }
                results.add(constructor.call(*args.toTypedArray()))
            }
        }
        return results
    }
}