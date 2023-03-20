package me.ore.sq.generic

import me.ore.sq.SqConnSingleColUnion
import me.ore.sq.SqConnectedContext
import me.ore.sq.SqParameter
import me.ore.sq.SqSingleColSelect


open class SqGenericConnSingleColUnion<JAVA: Any?, DB: Any>(
    override val context: SqConnectedContext,
    unionAll: Boolean,
    selects: Iterable<SqSingleColSelect<JAVA, DB>>,
): SqGenericSingleColUnion<JAVA, DB>(context, unionAll, selects), SqConnSingleColUnion<JAVA, DB> {
    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqGenericConnSingleColUnion<JAVA, DB> = this.apply {
        super.firstResultIndex(firstResultIndex)
    }
    override fun firstResultIndex(firstResultIndex: Long?): SqGenericConnSingleColUnion<JAVA, DB> = this.apply {
        super.firstResultIndex(firstResultIndex)
    }
    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqGenericConnSingleColUnion<JAVA, DB> = this.apply {
        super.resultCount(resultCount)
    }
    override fun resultCount(resultCount: Long?): SqGenericConnSingleColUnion<JAVA, DB> = this.apply {
        super.resultCount(resultCount)
    }
    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqGenericConnSingleColUnion<JAVA, DB> = this.apply {
        super.limit(resultCount, firstResultIndex)
    }
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqGenericConnSingleColUnion<JAVA, DB> = this.apply {
        super.limit(resultCount, firstResultIndex)
    }
}
