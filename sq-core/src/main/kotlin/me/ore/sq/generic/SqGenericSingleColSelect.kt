package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericSingleColSelect<JAVA: Any?, DB: Any>(
    override val context: SqContext,
    override var distinct: Boolean,
    override var column: SqColumn<JAVA, DB>,
    override var from: List<SqColSet>? = null,
    override var where: SqExpression<*, Boolean>? = null,
    override var groupBy: List<SqColumn<*, *>>? = null,
    override var having: SqExpression<*, Boolean>? = null,
    override var orderBy: List<SqOrderBy>? = null,
    override val columns: List<SqColumn<*, *>> = listOf(column),
): SqGenericSelectBase(), SqSingleColSelect<JAVA, DB> {
    companion object {
        val CONSTRUCTOR: SqSingleColSelectConstructor = object : SqSingleColSelectConstructor {
            override fun <JAVA, DB : Any> createSingleColSelect(
                context: SqContext,
                distinct: Boolean,
                column: SqColumn<JAVA, DB>,
                from: List<SqColSet>?,
                where: SqExpression<*, Boolean>?,
                groupBy: List<SqColumn<*, *>>?,
                having: SqExpression<*, Boolean>?,
                orderBy: List<SqOrderBy>?
            ): SqSingleColSelect<JAVA, DB> {
                return SqGenericSingleColSelect(context, distinct, column, from, where, groupBy, having, orderBy)
            }
        }
    }
}
