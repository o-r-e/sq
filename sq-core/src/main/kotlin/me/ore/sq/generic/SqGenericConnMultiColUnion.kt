package me.ore.sq.generic

import me.ore.sq.SqConnMultiColUnion
import me.ore.sq.SqConnectedContext
import me.ore.sq.SqParameter
import me.ore.sq.SqSelect


open class SqGenericConnMultiColUnion(
    override val context: SqConnectedContext,
    unionAll: Boolean,
    selects: Iterable<SqSelect>,
): SqGenericMultiColUnion(context, unionAll, selects), SqConnMultiColUnion {
    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqGenericConnMultiColUnion = this.apply {
        super.firstResultIndex(firstResultIndex)
    }
    override fun firstResultIndex(firstResultIndex: Long?): SqGenericConnMultiColUnion = this.apply {
        super.firstResultIndex(firstResultIndex)
    }
    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqGenericConnMultiColUnion = this.apply {
        super.resultCount(resultCount)
    }
    override fun resultCount(resultCount: Long?): SqGenericConnMultiColUnion = this.apply {
        super.resultCount(resultCount)
    }
    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqGenericConnMultiColUnion = this.apply {
        super.limit(resultCount, firstResultIndex)
    }
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqGenericConnMultiColUnion = this.apply {
        super.limit(resultCount, firstResultIndex)
    }
}
