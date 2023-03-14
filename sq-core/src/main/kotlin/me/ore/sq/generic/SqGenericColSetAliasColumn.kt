package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericColSetAliasColumn<JAVA: Any?, DB: Any>(
    override val context: SqContext,
    override val alias: SqColSetAlias<*>,
    override val column: SqColumn<JAVA, DB>,
    nullable: Boolean = column.nullable
): SqColSetAliasColumn<JAVA, DB> {
    // region Nullable
    @Suppress("PropertyName")
    protected open var _nullable: Boolean = nullable

    override val nullable: Boolean
        get() = this._nullable

    override fun nullable(): SqGenericColSetAliasColumn<JAVA?, DB> {
        this._nullable = true
        return SqUtil.uncheckedCast(this)
    }
    // endregion
}
