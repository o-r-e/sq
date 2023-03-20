package me.ore.sq.pg

import me.ore.sq.SqParameter
import me.ore.sq.SqSingleColSelect


open class SqPgConnSingleColUnionImpl<JAVA: Any?, DB: Any>(
    override val context: SqPgConnectedContext,
    unionAll: Boolean,
    selects: List<SqSingleColSelect<JAVA, DB>>,
): SqPgSingleColUnionImpl<JAVA, DB>(context, unionAll, selects), SqPgConnSingleColUnion<JAVA, DB> {
    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqPgConnSingleColUnionImpl<JAVA, DB> = this.apply {
        super.firstResultIndex(firstResultIndex)
    }
    override fun firstResultIndex(firstResultIndex: Long?): SqPgConnSingleColUnionImpl<JAVA, DB> = this.apply {
        super.firstResultIndex(firstResultIndex)
    }

    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqPgConnSingleColUnionImpl<JAVA, DB> = this.apply {
        super.resultCount(resultCount)
    }
    override fun resultCount(resultCount: Long?): SqPgConnSingleColUnionImpl<JAVA, DB> = this.apply {
        super.resultCount(resultCount)
    }

    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqPgConnSingleColUnionImpl<JAVA, DB> =
        this.resultCount(resultCount).firstResultIndex(firstResultIndex)
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqPgConnSingleColUnionImpl<JAVA, DB> =
        this.resultCount(resultCount).firstResultIndex(firstResultIndex)
}
