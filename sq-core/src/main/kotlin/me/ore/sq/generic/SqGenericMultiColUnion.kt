package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericMultiColUnion(
    override val context: SqContext,
    override val unionAll: Boolean,
    selects: Iterable<SqSelect>,
): SqMultiColUnion, SqGenericReadStatement {
    override val selects: List<SqSelect> = selects.toList()

    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqGenericMultiColUnion = this.apply {
        super.firstResultIndex(firstResultIndex)
    }
    override fun firstResultIndex(firstResultIndex: Long?): SqGenericMultiColUnion = this.apply {
        super.firstResultIndex(firstResultIndex)
    }
    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqGenericMultiColUnion = this.apply {
        super.resultCount(resultCount)
    }
    override fun resultCount(resultCount: Long?): SqGenericMultiColUnion = this.apply {
        super.resultCount(resultCount)
    }
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqGenericMultiColUnion = this.apply {
        super.limit(resultCount, firstResultIndex)
    }
    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqGenericMultiColUnion = this.apply {
        super.limit(resultCount, firstResultIndex)
    }
}
