package moneytree.persist.repository

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import java.util.UUID
import moneytree.domain.entity.Vendor
import moneytree.libs.commons.result.onOk
import moneytree.libs.commons.result.shouldBeOk
import moneytree.libs.commons.result.toOkValue
import moneytree.libs.testcommons.randomString
import moneytree.persist.PersistConnectorTestHarness
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VendorRepositoryTest : PersistConnectorTestHarness() {

    @AfterAll
    fun clean() {
        super.close()
    }

    @Test
    fun `get with unknown UUID returns null`() {
        val getResult = vendorRepository.getById(UUID.randomUUID())

        getResult.shouldBeOk()
        getResult.onOk { it shouldBe null }
    }

    @Test
    fun `insert and getById happy path`() {
        val randomVendor = insertRandomVendor().toOkValue()

        val getResult = vendorRepository.getById(
            checkNotNull(randomVendor.id)
        )
        getResult.shouldBeOk()
        getResult.onOk { it shouldBe randomVendor }
    }

    @Test
    fun `generic get returns a list`() {
        val randomVendor = insertRandomVendor().toOkValue()

        val getResult = vendorRepository.get()
        getResult.shouldBeOk()
        getResult.onOk {
            it.size shouldBeGreaterThanOrEqual 1
            it shouldContain randomVendor
        }
    }

    @Test
    fun `upsertById updates existing entity`() {
        val randomVendor = insertRandomVendor().toOkValue()
        val randomVendorId = checkNotNull(randomVendor.id)

        val updatedVendor = randomVendor.copy(
            name = randomString()
        )

        val upsertResult = vendorRepository.upsertById(updatedVendor, randomVendorId)
        upsertResult.shouldBeOk()
        upsertResult.onOk { it shouldBe updatedVendor }

        val retrieveResult = vendorRepository.getById(randomVendorId)
        retrieveResult.shouldBeOk()
        retrieveResult.onOk { it shouldBe updatedVendor }
    }

    @Test
    fun `upsertById adds a new entity`() {
        val randomUUID = UUID.randomUUID()
        val randomString = randomString()

        val vendor = Vendor(
            id = randomUUID,
            name = randomString
        )

        val nullResult = vendorRepository.getById(randomUUID)
        nullResult.shouldBeOk()
        nullResult.onOk { it shouldBe null }

        val upsertResult = vendorRepository.upsertById(vendor, randomUUID)
        upsertResult.shouldBeOk()
        upsertResult.onOk { it shouldBe vendor }

        val retrieveResult = vendorRepository.getById(randomUUID)
        retrieveResult.shouldBeOk()
        retrieveResult.onOk { it shouldBe vendor }
    }

    @Test
    fun `upsertById upserts with id parameter`() {
        val parameterId = UUID.randomUUID()
        val randomUUID = UUID.randomUUID()
        val randomString = randomString()

        val vendor = Vendor(
            id = randomUUID,
            name = randomString
        )

        val idParameterVendor = vendor.copy(id = parameterId)

        val upsertResult = vendorRepository.upsertById(vendor, parameterId)
        upsertResult.shouldBeOk()
        upsertResult.onOk { it shouldBe idParameterVendor }

        val getResult = vendorRepository.getById(parameterId)
        getResult.shouldBeOk()
        getResult.onOk { it shouldBe idParameterVendor }

        val nullResult = vendorRepository.getById(randomUUID)
        nullResult.shouldBeOk()
        nullResult.onOk { it shouldBe null }
    }

    @Test
    fun `deleteById successfully deletes`() {
        val randomVendor = insertRandomVendor().toOkValue()
        val randomVendorId = checkNotNull(randomVendor.id)

        val deleteResult = vendorRepository.deleteById(randomVendorId)
        deleteResult.shouldBeOk()

        val nullResult = vendorRepository.getById(randomVendorId)
        nullResult.shouldBeOk()
        nullResult.onOk { it shouldBe null }
    }

    @Test
    fun `deleteById does not error on nonexistent uuid`() {
        val randomUUID = UUID.randomUUID()

        val deleteResult = vendorRepository.deleteById(randomUUID)
        deleteResult.shouldBeOk()
    }
}
