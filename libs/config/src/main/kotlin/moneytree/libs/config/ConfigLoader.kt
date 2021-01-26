import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.typesafe.config.Config

val configMapper = jacksonObjectMapper()

inline fun <reified T : Any> Config.get(path: String): T {
    val mappedValues = mutableMapOf<String, Any>()
    val parsedConfig = this.getConfig(path)
    parsedConfig.entrySet().forEach {
        mappedValues[it.key] = it.value.unwrapped()
    }
    return configMapper.convertValue(mappedValues, T::class.java)
}
