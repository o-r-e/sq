package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericConnSingleColSelect<JAVA: Any?, DB: Any>(
    override val context: SqContext.ConnContext,
    override var distinct: Boolean,
    override var column: SqColumn<JAVA, DB>,
    override var from: List<SqColSet>? = null,
    override var where: SqExpression<*, Boolean>? = null,
    override var groupBy: List<SqColumn<*, *>>? = null,
    override var having: SqExpression<*, Boolean>? = null,
    override var orderBy: List<SqOrderBy>? = null,
    override val columns: List<SqColumn<*, *>> = listOf(column),
): SqGenericSelectBase(), SqConnSingleColSelect<JAVA, DB> {
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
                columns: List<SqColumn<*, *>>
            ): SqConnSingleColSelect<JAVA, DB> {
                return SqGenericConnSingleColSelect(
                    context, distinct, column, from, where, groupBy, having, orderBy, columns
                )
            }
        }
    }
}
