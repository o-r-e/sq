package me.ore.sq.pg

import me.ore.sq.SqParameter
import me.ore.sq.SqSelect
import me.ore.sq.SqSingleColSelect
import me.ore.sq.SqUtil


open class SqPgSingleColUnionImpl<JAVA: Any?, DB: Any>(
    override val context: SqPgContext,
    override val unionAll: Boolean,
    selects: List<SqSingleColSelect<JAVA, DB>>,
): SqPgUnionBase(), SqPgSingleColUnion<JAVA, DB> {
    @Suppress("CanBePrimaryConstructorProperty")
    override val selects: List<SqSingleColSelect<JAVA, DB>> = selects

    // region Nullable
    @Suppress("PropertyName")
    protected open var _nullable: Boolean = selects.first().nullable

    override val nullable: Boolean
        get() = this._nullable

    override fun nullable(): SqPgSingleColUnion<JAVA?, DB> {
        this._nullable = true
        return SqUtil.uncheckedCast(this)
    }
    // endregion

    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqPgSingleColUnionImpl<JAVA, DB> = this.apply {
        super.firstResultIndex(firstResultIndex)
    }
    override fun firstResultIndex(firstResultIndex: Long?): SqPgSingleColUnionImpl<JAVA, DB> = this.apply {
        super.firstResultIndex(firstResultIndex)
    }

    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqPgSingleColUnionImpl<JAVA, DB> = this.apply {
        super.resultCount(resultCount)
    }
    override fun resultCount(resultCount: Long?): SqPgSingleColUnionImpl<JAVA, DB> = this.apply {
        super.resultCount(resultCount)
    }

    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqPgSingleColUnionImpl<JAVA, DB> =
        this.resultCount(resultCount).firstResultIndex(firstResultIndex)
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqPgSingleColUnionImpl<JAVA, DB> =
        this.resultCount(resultCount).firstResultIndex(firstResultIndex)
}
