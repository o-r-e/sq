package me.ore.sq.pg

import me.ore.sq.*


open class SqPgMultiColSelect(
    override val context: SqContext,
    override var distinct: Boolean,
    override var columns: List<SqColumn<*, *>>,
    override var from: List<SqColSet>? = null,
    override var where: SqExpression<*, Boolean>? = null,
    override var groupBy: List<SqColumn<*, *>>? = null,
    override var having: SqExpression<*, Boolean>? = null,
    override var orderBy: List<SqOrderBy>? = null,
    override var resultCountParam: SqParameter<Long, Number>? = null,
    override var firstResultIndexParam: SqParameter<Long, Number>? = null,
): SqPgSelectBase(), SqMultiColSelect {
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
                return SqPgMultiColSelect(
                    context,
                    distinct,
                    columns,
                    from,
                    where,
                    groupBy,
                    having,
                    orderBy,
                )
            }
        }
    }


    override val definitionItem: SqItem
        get() = this
}
