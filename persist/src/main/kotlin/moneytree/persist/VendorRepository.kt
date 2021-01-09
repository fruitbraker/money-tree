package moneytree.persist

import moneytree.domain.Vendor as VendorDomain
import java.util.UUID
import moneytree.domain.Repository
import moneytree.libs.commons.result.Result
import moneytree.libs.commons.result.resultTry
import moneytree.persist.db.generated.Tables.VENDOR
import moneytree.persist.db.generated.tables.daos.VendorDao
import moneytree.persist.db.generated.tables.pojos.Vendor
import org.jooq.Record

class VendorRepository(
    private val vendorDao: VendorDao
) : Repository<VendorDomain> {

    private fun Record.toDomain(): VendorDomain {
        return VendorDomain(
            id = this[VENDOR.ID],
            name = this[VENDOR.NAME]
        )
    }

    private fun Vendor.toDomain(): VendorDomain {
        return VendorDomain(
            id = this.id,
            name = this.name
        )
    }

    override fun get(): Result<List<VendorDomain>, Throwable> {
        return resultTry {
            val result = vendorDao.configuration().dsl()
                .select()
                .from(VENDOR)
                .limit(100)
                .fetch()

            result.mapNotNull { it.toDomain() }
        }
    }

    override fun getById(uuid: UUID): Result<VendorDomain?, Throwable> {
        return resultTry {
            vendorDao.fetchOneById(uuid)?.toDomain()
        }
    }

    override fun insert(newEntity: VendorDomain): Result<VendorDomain, Throwable> {
        return resultTry {
            val result = vendorDao.configuration().dsl()
                .insertInto(VENDOR)
                .columns(
                    VENDOR.ID,
                    VENDOR.NAME
                )
                .values(
                    newEntity.id ?: UUID.randomUUID(),
                    newEntity.name
                )
                .returning()
                .fetch()

            result.first().toDomain()
        }
    }

    override fun upsertById(updatedEntity: VendorDomain, uuid: UUID): Result<VendorDomain, Throwable> {
        return resultTry {
            vendorDao.configuration().dsl()
                .insertInto(VENDOR)
                .columns(
                    VENDOR.ID,
                    VENDOR.NAME
                )
                .values(
                    uuid,
                    updatedEntity.name
                )
                .onDuplicateKeyUpdate()
                .set(VENDOR.NAME, updatedEntity.name)
                .execute()

            updatedEntity.copy(id = uuid)
        }
    }
}
