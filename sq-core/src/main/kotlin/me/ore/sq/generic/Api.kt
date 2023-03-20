package me.ore.sq.generic

import me.ore.sq.SqParameter
import me.ore.sq.SqReadStatement


@Suppress("MemberVisibilityCanBePrivate")
private fun throwResultLimitError(): Nothing = throw IllegalStateException("First result index and result count are not supported in \"generic\" read statement")

interface SqGenericReadStatement: SqReadStatement {
    // region Limiting results - first result index, result count
    override val firstResultIndex: SqParameter<Long, Number>?
        get() = throwResultLimitError()
    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqGenericReadStatement = throwResultLimitError()
    override fun firstResultIndex(firstResultIndex: Long?): SqGenericReadStatement = throwResultLimitError()

    override val resultCount: SqParameter<Long, Number>?
        get() = throwResultLimitError()
    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqGenericReadStatement = throwResultLimitError()
    override fun resultCount(resultCount: Long?): SqGenericReadStatement = throwResultLimitError()


    override fun limit(resultCount: Long, firstResultIndex: Long?): SqGenericReadStatement = throwResultLimitError()
    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqGenericReadStatement = throwResultLimitError()
    // endregion
}
