package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericJoin(
    override val context: SqContext,
    override val joinType: SqJoinType,
    override val mainColSet: SqColSet,
    override val joinedColSet: SqColSet,
    on: SqExpression<*, Boolean>? = null,
    override val columns: List<SqColumn<*, *>> = mainColSet.columns.plus(joinedColSet.columns),
): SqJoin {
    companion object {
        val CONSTRUCTOR: SqJoinConstructor = object : SqJoinConstructor {
            override fun createJoin(
                context: SqContext,
                joinType: SqJoinType,
                mainColSet: SqColSet,
                joinedColSet: SqColSet
            ): SqJoin {
                return SqGenericJoin(context, joinType, mainColSet, joinedColSet)
            }
        }
    }


    // region "On" item
    @Suppress("PropertyName")
    protected open var _on: SqExpression<*, Boolean>? = on

    override val on: SqExpression<*, Boolean>?
        get() = this._on

    override fun setOn(on: SqExpression<*, Boolean>?) { this._on = on }
    // endregion


    override val definitionItem: SqItem
        get() = this
}
