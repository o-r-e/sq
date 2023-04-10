package me.ore.sq.pg

import me.ore.sq.*


open class SqPgConnSingleColSelect<JAVA: Any?, DB: Any>(
    override val context: SqContext.ConnContext,
    distinct: Boolean,
    column: SqColumn<JAVA, DB>,
    from: List<SqColSet>? = null,
    where: SqExpression<*, Boolean>? = null,
    groupBy: List<SqColumn<*, *>>? = null,
    having: SqExpression<*, Boolean>? = null,
    orderBy: List<SqOrderBy>? = null,
    resultCountParam: SqParameter<Long, Number>? = null,
    firstResultIndexParam: SqParameter<Long, Number>? = null,
): SqPgSingleColSelect<JAVA, DB>(
    context,
    distinct,
    column,
    from,
    where,
    groupBy,
    having,
    orderBy,
    resultCountParam,
    firstResultIndexParam,
), SqConnSingleColSelect<JAVA, DB> {
    companion object {
        val CONSTRUCTOR: SqConnSingleColSelectConstructor = object : SqConnSingleColSelectConstructor {
            override fun <JAVA, DB : Any> createConnSingleColSelect(
                context: SqContext.ConnContext,
                distinct: Boolean,
                column: SqColumn<JAVA, DB>,
                from: List<SqColSet>?,
                where: SqExpression<*, Boolean>?,
                groupBy: List<SqColumn<*, *>>?,
                having: SqExpression<*, Boolean>?,
                orderBy: List<SqOrderBy>?,
            ): SqConnSingleColSelect<JAVA, DB> {
                return SqPgConnSingleColSelect(
                    context, distinct, column, from, where, groupBy, having, orderBy,
                )
            }
        }
    }
}
