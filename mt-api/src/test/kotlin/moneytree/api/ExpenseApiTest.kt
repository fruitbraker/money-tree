package moneytree.api

import org.http4k.client.OkHttp
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpenseApiTest {
    private val client = OkHttp()


}
