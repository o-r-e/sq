package me.ore.sq.pg

import me.ore.sq.*


open class SqPgSingleColSelect<JAVA: Any?, DB: Any>(
    override val context: SqContext,
    override var distinct: Boolean,
    column: SqColumn<JAVA, DB>,
    override var from: List<SqColSet>? = null,
    override var where: SqExpression<*, Boolean>? = null,
    override var groupBy: List<SqColumn<*, *>>? = null,
    override var having: SqExpression<*, Boolean>? = null,
    override var orderBy: List<SqOrderBy>? = null,
    override var resultCountParam: SqParameter<Long, Number>? = null,
    override var firstResultIndexParam: SqParameter<Long, Number>? = null,
): SqPgSelectBase(), SqSingleColSelect<JAVA, DB> {
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
                return SqPgSingleColSelect(
                    context, distinct, column, from, where, groupBy, having, orderBy,
                )
            }
        }
    }


    override val definitionItem: SqItem
        get() = this

    override var column: SqColumn<JAVA, DB> = column
        set(value) {
            field = value
            this._columns = listOf(value)
        }

    @Suppress("PropertyName")
    protected open var _columns: List<SqColumn<*, *>> = listOf(column)

    override val columns: List<SqColumn<*, *>>
        get() = this._columns
}
