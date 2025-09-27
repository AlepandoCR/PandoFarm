
package com.mapachos.pandoFarm.util.yml

import com.mapachos.pandoFarm.PandoFarm
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.IOException
import java.io.Serializable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

/**
 * AutoYML is a utility class for automatically serializing and deserializing Kotlin data classes to and from YAML files.
 * It uses reflection to map data class properties to YAML keys and supports nested data classes, lists, and enums.
 *
 * @param T The type of the data class to be serialized/deserialized. Must implement Serializable.
 * @property clazz The KClass of the data
 * @property file The file where the YAML data will be stored.
 * @property header An optional header comment to be added at the top of the YAML file.
 */
class AutoYML<T: Serializable>(
    private val clazz: KClass<T>,
    private val file: File,
    private val header: String? = null
) {

    private val yaml: Yaml by lazy {
        val opts = DumperOptions().apply {
            defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
            isPrettyFlow = true
            indent = 2
        }
        Yaml(opts)
    }

    init { ensureParent() }

    private fun ensureParent() {
        file.parentFile?.let { if (!it.exists()) it.mkdirs() }
    }

    fun exists(): Boolean = file.exists()

    fun delete(): Boolean = file.delete()

    fun load(): T? {
        if(!file.exists()) return null
        return try {
            file.inputStream().use { input ->
                val raw = yaml.load<Any?>(input) ?: return null
                if(raw !is Map<*, *>) return null
                @Suppress("UNCHECKED_CAST")
                buildFromMap(raw as Map<String, Any?>)
            }
        } catch (ex: Exception){
            ex.printStackTrace()
            null
        }
    }

    fun loadOrCreate(defaultSupplier: () -> T): T = load() ?: defaultSupplier().also { save(it) }

    fun save(obj: T) {
        val map = toMap(obj)
        try {
            file.writer().use { w ->
                header?.let { h ->
                    h.lines().forEach { line -> w.write("# $line\n") }
                    w.write("\n")
                }
                yaml.dump(map, w)
            }
        } catch (ex: IOException){
            ex.printStackTrace()
        }
    }

    private fun buildFromMap(map: Map<String, Any?>): T {
        val constructor = clazz.primaryConstructor
            ?: error("${clazz.simpleName} needs a primaryConstructor for AutoYML")
        val args = constructor.parameters.associateWith { param ->
            val raw = map[param.name]
            convertValue(raw, param.type)
        }
        return constructor.callBy(args)
    }

    private fun convertValue(raw: Any?, type: KType): Any? {
        if(raw == null) return null
        val classifier = type.classifier as? KClass<*> ?: return raw
        return when {
            classifier == String::class -> raw.toString()
            classifier == Int::class -> (raw as? Number)?.toInt() ?: raw.toString().toIntOrNull()
            classifier == Long::class -> (raw as? Number)?.toLong() ?: raw.toString().toLongOrNull()
            classifier == Double::class -> (raw as? Number)?.toDouble() ?: raw.toString().toDoubleOrNull()
            classifier == Float::class -> (raw as? Number)?.toFloat() ?: raw.toString().toFloatOrNull()
            classifier == Boolean::class -> when(raw){
                is Boolean -> raw
                is String -> raw.equals("true", true)
                is Number -> raw.toInt() != 0
                else -> false
            }
            classifier.java.isEnum -> enumValue(classifier, raw)
            classifier.isData -> {
                if(raw !is Map<*, *>) null else {
                    val strMap: Map<String, Any?> = raw.entries.mapNotNull { (k,v) ->
                        (k as? String)?.let { it to v }
                    }.toMap()
                    buildNested(classifier, strMap)
                }
            }
            classifier == List::class -> {
                val argType = type.arguments.firstOrNull()?.type ?: String::class.createType()
                if(raw !is List<*>) return null
                raw.map { convertValue(it, argType) }
            }
            else -> raw
        }
    }

    private fun <N: Any> buildNested(nested: KClass<N>, raw: Map<String, Any?>): N {
        val ctor = nested.primaryConstructor ?: error("${nested.simpleName} needs a primaryConstructor for AutoYML")
        val args = ctor.parameters.associateWith { p ->
            val v = raw[p.name]
            convertValue(v, p.type)
        }
        return ctor.callBy(args)
    }

    private fun enumValue(enumClass: KClass<*>, raw: Any): Any? {
        val name = raw.toString()
        return enumClass.java.enumConstants.firstOrNull { (it as Enum<*>).name.equals(name, true) }
    }

    private fun toMap(obj: Any): Map<String, Any?> {
        val kClass = obj::class
        val result = linkedMapOf<String, Any?>()
        kClass.memberProperties.forEach { prop ->
            @Suppress("UNCHECKED_CAST")
            val p = prop as KProperty1<Any, *>
            val value = p.get(obj)
            result[prop.name] = serializeValue(value)
        }
        return result
    }

    private fun serializeValue(value: Any?): Any? = when(value) {
        null -> null
        is String, is Number, is Boolean -> value
        is Enum<*> -> value.name
        is List<*> -> value.map { serializeValue(it) }
        else -> if(value::class.isData) toMap(value) else value.toString()
    }

    companion object {
        fun <T: Serializable> create(clazz: KClass<T>, fileName: String, dataFolder: DataFolder, header: String? = null): AutoYML<T> {
            val folder = File(PandoFarm.getInstance().dataFolder, dataFolder.path)
            if(!folder.exists()) folder.mkdirs()
            val file = File(folder, if(fileName.endsWith(".yml")) fileName else "$fileName.yml")
            return AutoYML(clazz, file, header)
        }
    }
}