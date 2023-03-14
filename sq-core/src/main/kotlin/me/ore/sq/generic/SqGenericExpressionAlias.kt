package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericExpressionAlias<JAVA: Any?, DB: Any, ORIG: SqExpression<JAVA, DB>>(
    override val context: SqContext,
    original: ORIG,
    override val alias: String,
): SqExpressionAlias<JAVA, DB, ORIG> {
    // region Nullable
    @Suppress("PropertyName")
    protected open var _nullable: Boolean = original.nullable

    override val nullable: Boolean
        get() = this._nullable

    override fun nullable(): SqExpression<JAVA?, DB> {
        this._nullable = true
        return SqUtil.uncheckedCast(this)
    }
    // endregion

    @Suppress("CanBePrimaryConstructorProperty")
    override val original: ORIG = original
}
