package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericSingleColUnion<JAVA: Any?, DB: Any>(
    override val context: SqContext,
    override val unionAll: Boolean,
    selects: Iterable<SqSingleColSelect<JAVA, DB>>,
): SqSingleColUnion<JAVA, DB>, SqGenericReadStatement {
    override val selects: List<SqSingleColSelect<JAVA, DB>> = selects.toList()


    // region Nullable
    @Suppress("PropertyName")
    protected open var _nullable: Boolean = selects.first().nullable

    override val nullable: Boolean
        get() = this._nullable

    override fun nullable(): SqGenericSingleColUnion<JAVA?, DB> {
        this._nullable = true
        return SqUtil.uncheckedCast(this)
    }
    // endregion


    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqGenericSingleColUnion<JAVA, DB> = this.apply {
        super.firstResultIndex(firstResultIndex)
    }
    override fun firstResultIndex(firstResultIndex: Long?): SqGenericSingleColUnion<JAVA, DB> = this.apply {
        super.firstResultIndex(firstResultIndex)
    }
    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqGenericSingleColUnion<JAVA, DB> = this.apply {
        super.resultCount(resultCount)
    }
    override fun resultCount(resultCount: Long?): SqGenericSingleColUnion<JAVA, DB> = this.apply {
        super.resultCount(resultCount)
    }
    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqGenericSingleColUnion<JAVA, DB> = this.apply {
        super.limit(resultCount, firstResultIndex)
    }
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqGenericSingleColUnion<JAVA, DB> = this.apply {
        super.limit(resultCount, firstResultIndex)
    }
}
