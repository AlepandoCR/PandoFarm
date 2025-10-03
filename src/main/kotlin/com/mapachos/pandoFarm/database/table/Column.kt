package com.mapachos.pandoFarm.database.table

import java.util.*

data class Column<T>(
    val name: String,
    val type: Class<T>,
    val primaryKey: Boolean = false,
    val nullable: Boolean = false
) {
    private fun sqlType(): String = when (type) {
        String::class.java -> when(name) {
            "uuid" -> "VARCHAR(36)"
            "state" -> "TEXT"
            else -> "VARCHAR(255)"
        }
        Int::class.java, Integer::class.java -> "INT"
        Long::class.java, java.lang.Long::class.java -> "BIGINT"
        Double::class.java, java.lang.Double::class.java -> "DOUBLE"
        Float::class.java, java.lang.Float::class.java -> "FLOAT"
        Boolean::class.java, java.lang.Boolean::class.java -> "BOOLEAN"
        UUID::class.java -> "VARCHAR(36)"
        ByteArray::class.java -> "BLOB"
        else -> "JSON"
    }


    fun definition(): String {
        val nullStr = if (nullable) "" else "NOT NULL"
        val pkStr = if (primaryKey) "PRIMARY KEY" else ""
        return "`$name` ${sqlType()} $nullStr $pkStr".trim()
    }
}