package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericConnMultiColSelect(
    override val context: SqContext.ConnContext,
    override var distinct: Boolean,
    override var columns: List<SqColumn<*, *>>,
    override var from: List<SqColSet>? = null,
    override var where: SqExpression<*, Boolean>? = null,
    override var groupBy: List<SqColumn<*, *>>? = null,
    override var having: SqExpression<*, Boolean>? = null,
    override var orderBy: List<SqOrderBy>? = null,
): SqGenericSelectBase(), SqConnMultiColSelect {
    companion object {
        val CONSTRUCTOR: SqConnMultiColSelectConstructor = object : SqConnMultiColSelectConstructor {
            override fun createConnMultiColSelect(
                context: SqContext.ConnContext,
                distinct: Boolean,
                columns: List<SqColumn<*, *>>,
                from: List<SqColSet>?,
                where: SqExpression<*, Boolean>?,
                groupBy: List<SqColumn<*, *>>?,
                having: SqExpression<*, Boolean>?,
                orderBy: List<SqOrderBy>?
            ): SqConnMultiColSelect {
                return SqGenericConnMultiColSelect(context, distinct, columns, from, where, groupBy, having, orderBy)
            }
        }
    }
}
