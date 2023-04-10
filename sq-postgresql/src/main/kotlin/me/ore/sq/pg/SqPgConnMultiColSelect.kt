package me.ore.sq.pg

import me.ore.sq.*


open class SqPgConnMultiColSelect(
    override val context: SqContext.ConnContext,
    distinct: Boolean,
    columns: List<SqColumn<*, *>>,
    from: List<SqColSet>? = null,
    where: SqExpression<*, Boolean>? = null,
    groupBy: List<SqColumn<*, *>>? = null,
    having: SqExpression<*, Boolean>? = null,
    orderBy: List<SqOrderBy>? = null,
    resultCountParam: SqParameter<Long, Number>? = null,
    firstResultIndexParam: SqParameter<Long, Number>? = null,
): SqPgMultiColSelect(
    context,
    distinct,
    columns,
    from,
    where,
    groupBy,
    having,
    orderBy,
    resultCountParam,
    firstResultIndexParam,
), SqConnMultiColSelect {
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
                return SqPgConnMultiColSelect(
                    context, distinct, columns, from, where, groupBy, having, orderBy,
                )
            }
        }
    }
}
