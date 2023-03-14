package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericJoin(
    override val context: SqContext,
    override val type: SqJoinType,
    override val mainColSet: SqColSet,
    override val joinedColSet: SqColSet,
): SqJoin {
    // region "On" condition
    @Suppress("PropertyName")
    protected open var _on: SqExpression<*, Boolean>? = null

    override val on: SqExpression<*, Boolean>?
        get() = this._on

    override fun on(on: SqExpression<*, Boolean>?): SqGenericJoin = this.apply {
        this._on = on
    }
    // endregion
}
