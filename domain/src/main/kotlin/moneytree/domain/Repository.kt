package moneytree.domain

import java.util.UUID

interface Repository<T> {
    fun get(): List<T>
    fun get(uuid: UUID): T
}
