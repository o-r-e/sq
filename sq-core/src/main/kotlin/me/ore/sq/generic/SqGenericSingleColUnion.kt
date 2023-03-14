package me.ore.sq.generic

import me.ore.sq.SqContext
import me.ore.sq.SqSingleColSelect
import me.ore.sq.SqSingleColUnion
import me.ore.sq.SqUtil


open class SqGenericSingleColUnion<JAVA: Any?, DB: Any>(
    override val context: SqContext,
    override val unionAll: Boolean,
    selects: Iterable<SqSingleColSelect<JAVA, DB>>,
): SqSingleColUnion<JAVA, DB> {
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
}
