import com.google.inject.Guice
import com.google.inject.Injector
import moneytree.persist.PersistModules
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

open class PersistTestModule {
    companion object {
        val injector: Injector = Guice.createInjector(
            PersistModules("test")
        )
    }

    @BeforeAll
    private fun setupPostgres() {
        print("build build build!")
    }

    @AfterAll
    private fun cleanPostgres() {
        print("clean clean clean!")
    }
}
