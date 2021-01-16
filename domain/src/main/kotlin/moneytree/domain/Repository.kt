package moneytree.domain

import java.util.UUID
import moneytree.domain.entity.ExpenseSummary
import moneytree.libs.commons.result.Result

interface Repository<T> {
    fun get(): Result<List<T>, Throwable>
    fun getById(uuid: UUID): Result<T?, Throwable>

    fun insert(newEntity: T): Result<T, Throwable>
    fun upsertById(updatedEntity: T, uuid: UUID): Result<T, Throwable>
}

interface ExpenseSummaryRepository {
    fun getExpenseSummary(): Result<List<ExpenseSummary>, Throwable>
    fun getExpenseSummaryById(uuid: UUID): Result<ExpenseSummary?, Throwable>
}
