package me.ore.sq.generic

import me.ore.sq.*
import java.util.Collections


abstract class SqGenericSelectBase(
    override val context: SqContext,
    override val distinct: Boolean,
    columns: Iterable<SqColumn<*, *>>,
): SqSelect {
    // region Columns
    override val columns: List<SqColumn<*, *>> = columns.toList()

    override fun createColumnNotFoundException(column: SqColumn<*, *>): Exception {
        return IllegalArgumentException("Cannot find column $column in \"select\" $this")
    }
    // endregion


    // region From
    protected open fun createFromList(): MutableList<SqColSet> = ArrayList()

    @Suppress("MemberVisibilityCanBePrivate", "PropertyName")
    protected var _from: MutableList<SqColSet>? = null

    override val from: List<SqColSet>?
        get() = this._from?.let { Collections.unmodifiableList(it) }

    override fun from(from: Iterable<SqColSet>): SqGenericSelectBase = this.apply {
        val tmpFrom = this
            ._from
            ?.also { it.clear() }
            ?: run {
                val tmpFrom = this.createFromList()
                this._from = tmpFrom
                tmpFrom
            }

        tmpFrom.addAll(from)
    }
    // endregion


    // region Where
    @Suppress("PropertyName", "MemberVisibilityCanBePrivate")
    protected var _where: SqExpression<*, Boolean>? = null

    override val where: SqExpression<*, Boolean>?
        get() = this._where

    override fun where(condition: SqExpression<*, Boolean>?): SqGenericSelectBase = this.apply { this._where = condition }
    // endregion


    // region Group by
    protected open fun createGroupByList(): MutableList<SqColumn<*, *>> = ArrayList()

    @Suppress("PropertyName")
    protected open var _groupBy: MutableList<SqColumn<*, *>>? = null

    override val groupBy: List<SqColumn<*, *>>?
        get() = this._groupBy?.let { Collections.unmodifiableList(it) }

    override fun groupBy(items: Iterable<SqColumn<*, *>>): SqGenericSelectBase = this.apply {
        val tmpGroupBy = this
            ._groupBy
            ?.also { it.clear() }
            ?: run {
                val tmpGroupBy = this.createGroupByList()
                this._groupBy = tmpGroupBy
                tmpGroupBy
            }

        tmpGroupBy.addAll(items)
    }
    // endregion


    // region Having
    @Suppress("PropertyName", "MemberVisibilityCanBePrivate")
    protected var _having: SqExpression<*, Boolean>? = null

    override val having: SqExpression<*, Boolean>?
        get() = this._having

    override fun having(condition: SqExpression<*, Boolean>?): SqGenericSelectBase = this.apply { this._having = condition }
    // endregion


    // region Order by
    protected open fun createOrderByList(): MutableList<SqOrderBy> = ArrayList()

    @Suppress("MemberVisibilityCanBePrivate", "PropertyName")
    protected open var _orderBy: MutableList<SqOrderBy>? = null

    override val orderBy: List<SqOrderBy>?
        get() = this._orderBy?.let { Collections.unmodifiableList(it) }

    override fun orderBy(items: Iterable<SqOrderBy>): SqGenericSelectBase = this.apply {
        val tmpOrderBy = this
            ._orderBy
            ?.also { it.clear() }
            ?: run {
                val tmpOrderBy = this.createOrderByList()
                this._orderBy = tmpOrderBy
                tmpOrderBy
            }

        tmpOrderBy.addAll(items)
    }
    // endregion


    override fun parameters(): List<SqParameter<*, *>>? {
        val self = this
        return buildList {
            self.columns.forEach { column ->
                column.possibleDefinitionParameters()?.let { parameters ->
                    this.addAll(parameters)
                }
            }
            self.from?.forEach { fromItem ->
                fromItem.possibleDefinitionParameters()?.let { parameters ->
                    this.addAll(parameters)
                }
            }
            self.where?.let { where ->
                where.parameters()?.let { parameters ->
                    this.addAll(parameters)
                }
            }
            self.groupBy?.forEach { groupByItem ->
                groupByItem.parameters()?.let { parameters ->
                    this.addAll(parameters)
                }
            }
            self.having?.let { having ->
                having.parameters()?.let { parameters ->
                    this.addAll(parameters)
                }
            }
            self.orderBy?.forEach { orderByItem ->
                orderByItem.parameters()?.let { parameters ->
                    this.addAll(parameters)
                }
            }
        }
            .takeIf { it.isNotEmpty() }
    }
}
