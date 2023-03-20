package me.ore.sq.pg

import me.ore.sq.SqParameter
import me.ore.sq.SqSelect


open class SqPgConnMultiColUnionImpl(
    override val context: SqPgConnectedContext,
    unionAll: Boolean,
    selects: List<SqSelect>,
): SqPgMultiColUnionImpl(context, unionAll, selects), SqPgConnMultiColUnion {
    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqPgConnMultiColUnionImpl = this.apply {
        super.firstResultIndex(firstResultIndex)
    }
    override fun firstResultIndex(firstResultIndex: Long?): SqPgConnMultiColUnionImpl = this.apply {
        super.firstResultIndex(firstResultIndex)
    }
    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqPgConnMultiColUnionImpl = this.apply {
        super.resultCount(resultCount)
    }
    override fun resultCount(resultCount: Long?): SqPgConnMultiColUnionImpl = this.apply {
        super.resultCount(resultCount)
    }
    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqPgConnMultiColUnionImpl =
        this.resultCount(resultCount).firstResultIndex(firstResultIndex)

    override fun limit(resultCount: Long, firstResultIndex: Long?): SqPgConnMultiColUnionImpl =
        this.resultCount(resultCount).firstResultIndex(firstResultIndex)
}
