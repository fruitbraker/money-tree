package moneytree.libs.config

import com.typesafe.config.ConfigFactory
import get
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ConfigLoaderTest {

    @Test
    fun `it loads config from classpath`() {
        val config = ConfigFactory.load("TestConfig.conf")
        val mappedConfig = config.get<TestConfig>("configTest")

        val testConfig = TestConfig(
            integer = 1,
            string = "blah",
            boolean = true
        )

        mappedConfig shouldBe testConfig
    }
}

data class TestConfig(
    val integer: Int,
    val string: String,
    val boolean: Boolean
)
