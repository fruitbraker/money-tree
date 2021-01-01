package moneytree.persist

import org.junit.jupiter.api.Test

class ExpenseRepositoryTest {

    private var persistHarness = PersistConnectorTestHarness()
    private var expenseRepository = ExpenseRepository(persistHarness.dslContext)

    @Test
    fun `foo`() {
        expenseRepository.get()
    }
}
