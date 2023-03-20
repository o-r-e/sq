package me.ore.sq.pg

import me.ore.sq.SqParameter
import me.ore.sq.SqSelect


open class SqPgMultiColUnionImpl(
    override val context: SqPgContext,
    override val unionAll: Boolean,
    override val selects: List<SqSelect>,
): SqPgUnionBase(), SqPgMultiColUnion {
    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqPgMultiColUnionImpl = this.apply {
        super.firstResultIndex(firstResultIndex)
    }
    override fun firstResultIndex(firstResultIndex: Long?): SqPgMultiColUnionImpl = this.apply {
        super.firstResultIndex(firstResultIndex)
    }

    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqPgMultiColUnionImpl = this.apply {
        super.resultCount(resultCount)
    }
    override fun resultCount(resultCount: Long?): SqPgMultiColUnionImpl = this.apply {
        super.resultCount(resultCount)
    }

    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqPgMultiColUnionImpl =
        this.resultCount(resultCount).firstResultIndex(firstResultIndex)
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqPgMultiColUnionImpl =
        this.resultCount(resultCount).firstResultIndex(firstResultIndex)
}
