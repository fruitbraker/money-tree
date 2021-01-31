package moneytree.api

import io.mockk.mockkClass
import java.util.UUID
import moneytree.api.contracts.RoutesTest
import moneytree.domain.entity.Vendor
import moneytree.libs.testcommons.randomString
import moneytree.persist.repository.VendorRepository
import moneytree.validator.VendorValidator

class VendorApiTest : RoutesTest<Vendor>() {

    private val randomUUID = UUID.randomUUID()
    private val randomString = randomString()

    override val entity = Vendor(
        id = randomUUID,
        name = randomString
    )

    override val entityRepository = mockkClass(VendorRepository::class)
    override val entityValidator = VendorValidator()
    override val entityApi = VendorApi(entityRepository, entityValidator)

    override val entityPath = "/vendor"
}
