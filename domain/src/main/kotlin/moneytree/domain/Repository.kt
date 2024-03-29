package moneytree.domain

import java.util.UUID
import moneytree.domain.entity.Filter
import moneytree.libs.commons.result.Result

interface Repository<T> {
    fun get(): Result<List<T>, Throwable>
    fun getById(uuid: UUID): Result<T?, Throwable>

    fun insert(newEntity: T): Result<T, Throwable>
    fun upsertById(updatedEntity: T, uuid: UUID): Result<T, Throwable>

    fun deleteById(uuid: UUID): Result<Unit, Throwable>
}

interface SummaryRepository<S, F : Filter> {
    fun getSummary(filter: F): Result<List<S>, Throwable>
}
