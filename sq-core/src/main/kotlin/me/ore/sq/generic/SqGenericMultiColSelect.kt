package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericMultiColSelect(
    override val context: SqContext,
    override var distinct: Boolean = false,
    override var columns: List<SqColumn<*, *>> = emptyList(),
    override var from: List<SqColSet>? = null,
    override var where: SqExpression<*, Boolean>? = null,
    override var groupBy: List<SqColumn<*, *>>? = null,
    override var having: SqExpression<*, Boolean>? = null,
    override var orderBy: List<SqOrderBy>? = null,
): SqGenericSelectBase(), SqMultiColSelect {
    companion object {
        val CONSTRUCTOR: SqMultiColSelectConstructor = object : SqMultiColSelectConstructor {
            override fun createMultiColSelect(
                context: SqContext,
                distinct: Boolean,
                columns: List<SqColumn<*, *>>,
                from: List<SqColSet>?,
                where: SqExpression<*, Boolean>?,
                groupBy: List<SqColumn<*, *>>?,
                having: SqExpression<*, Boolean>?,
                orderBy: List<SqOrderBy>?
            ): SqMultiColSelect {
                return SqGenericMultiColSelect(context, distinct, columns, from, where, groupBy, having, orderBy)
            }
        }
    }
}
