package moneytree.persist

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import java.util.UUID
import moneytree.domain.Vendor
import moneytree.libs.commons.result.onOk
import moneytree.libs.commons.result.shouldBeOk
import moneytree.libs.test.commons.randomString
import moneytree.persist.db.generated.tables.daos.VendorDao
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VendorRepositoryTest {

    private var persistHarness = PersistConnectorTestHarness()
    private var vendorRepository = VendorRepository(
        VendorDao(persistHarness.dslContext.configuration())
    )

    @AfterAll
    fun clean() {
        persistHarness.close()
    }

    @Test
    fun `get with unknown UUID returns null`() {
        val result = vendorRepository.getById(UUID.randomUUID())

        result.shouldBeOk()
        result.onOk { it shouldBe null }
    }

    @Test
    fun `insert and getById happy path`() {
        val randomUUID = UUID.randomUUID()
        val randomString = randomString()

        val vendor = Vendor(
            id = randomUUID,
            name = randomString
        )

        val insertResult = vendorRepository.insert(vendor)
        insertResult.shouldBeOk()
        insertResult.onOk { it shouldBe vendor }

        val retrieveResult = vendorRepository.getById(randomUUID)
        retrieveResult.shouldBeOk()
        retrieveResult.onOk { it shouldBe vendor }
    }

    @Test
    fun `generic get returns a list`() {
        val randomUUID = UUID.randomUUID()
        val randomString = randomString()

        val vendor = Vendor(
            id = randomUUID,
            name = randomString
        )

        vendorRepository.insert(vendor)

        val result = vendorRepository.get()
        result.shouldBeOk()
        result.onOk {
            it.size shouldBeGreaterThanOrEqual 1
            it shouldContain vendor
        }
    }

    @Test
    fun `updates entity happy path`() {
        val randomUUID = UUID.randomUUID()
        val randomString = randomString()

        val vendor = Vendor(
            id = randomUUID,
            name = randomString
        )

        vendorRepository.insert(vendor)

        val newRandomString = randomString()

        val updatedVendor = Vendor(
            id = randomUUID,
            name = newRandomString
        )

        val result = vendorRepository.updateById(updatedVendor, randomUUID)
        result.shouldBeOk()
        result.onOk { it shouldBe updatedVendor }
    }
}
