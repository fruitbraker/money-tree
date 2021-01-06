package moneytree.domain

import java.util.UUID
import moneytree.libs.commons.result.Result

interface Repository<T> {
    fun get(): Result<List<T>, Throwable>
    fun getById(uuid: UUID): Result<T?, Throwable>

    fun insert(newEntity: T): Result<T, Throwable>
    fun updateById(updatedEntity: T): Result<T, Throwable>
}
