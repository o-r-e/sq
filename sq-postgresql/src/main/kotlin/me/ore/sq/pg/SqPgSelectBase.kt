package me.ore.sq.pg

import me.ore.sq.*
import me.ore.sq.generic.SqGenericSelectBase


open class SqPgSelectBase(
    override val context: SqPgContext,
    distinct: Boolean,
    columns: Iterable<SqColumn<*, *>>,
): SqGenericSelectBase(context, distinct, columns), SqPgSelect {
    override fun from(from: Iterable<SqColSet>): SqPgSelectBase = this.apply { super.from(from) }
    override fun from(first: SqColSet, vararg more: SqColSet): SqPgSelectBase = this.apply { super.from(first, *more) }
    override fun where(condition: SqExpression<*, Boolean>?): SqPgSelectBase = this.apply { super.where(condition) }
    override fun groupBy(items: Iterable<SqColumn<*, *>>): SqPgSelectBase = this.apply { super.groupBy(items) }
    override fun groupBy(first: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqPgSelectBase = this.apply { super.groupBy(first, *more) }
    override fun having(condition: SqExpression<*, Boolean>?): SqPgSelectBase = this.apply { super.having(condition) }
    override fun orderBy(items: Iterable<SqOrderBy>): SqPgSelectBase = this.apply { super.orderBy(items) }
    override fun orderBy(first: SqOrderBy, vararg more: SqOrderBy): SqPgSelectBase = this.apply { super.orderBy(first, *more) }


    // region Limiting results - first result index, result count
    @Suppress("MemberVisibilityCanBePrivate", "PropertyName")
    protected var _firstResultIndex: SqParameter<Int, Number>? = null
    override val firstResultIndex: SqParameter<Int, Number>?
        get() = this._firstResultIndex

    override fun firstResultIndex(firstResultIndex: SqParameter<Int, Number>?): SqPgSelectBase = this.apply { this._firstResultIndex = firstResultIndex }
    override fun firstResultIndex(firstResultIndex: Int?): SqPgSelectBase = this.apply { super.firstResultIndex(firstResultIndex) }


    @Suppress("MemberVisibilityCanBePrivate", "PropertyName")
    protected var _resultCount: SqParameter<Int, Number>? = null
    override val resultCount: SqParameter<Int, Number>?
        get() = this._resultCount

    override fun resultCount(resultCount: SqParameter<Int, Number>?): SqPgSelectBase = this.apply { this._resultCount = resultCount }
    override fun resultCount(resultCount: Int?): SqPgSelectBase = this.apply { super.resultCount(resultCount) }


    override fun limit(resultCount: SqParameter<Int, Number>, firstResultIndex: SqParameter<Int, Number>?): SqPgSelectBase =
        this.apply { super<SqGenericSelectBase>.limit(resultCount, firstResultIndex) }
    override fun limit(resultCount: Int, firstResultIndex: Int?): SqPgSelectBase =
        this.apply { super<SqGenericSelectBase>.limit(resultCount, firstResultIndex) }
    // endregion


    override fun parameters(): List<SqParameter<*, *>>? {
        val resultCount = this.resultCount
        val firstResultIndex = this.firstResultIndex
        val parameters = super.parameters()

        return if ((resultCount == null) && (firstResultIndex == null)) {
            parameters
        } else {
            val resultCapacity = (parameters?.size ?: 0) + 2
            buildList(resultCapacity) {
                if (!parameters.isNullOrEmpty()) {
                    this.addAll(parameters)
                }
                if (resultCount != null) {
                    this.add(resultCount)
                }
                if (firstResultIndex != null) {
                    this.add(firstResultIndex)
                }
            }
        }
    }


    override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
        val resultCount = this.resultCount
        val firstResultIndex = this.firstResultIndex
        if ((resultCount == null) && (firstResultIndex == null)) {
            SqUtil.appendSelect(this, target, asTextPart, spaceAllowed)
        } else {
            SqUtil.appendSelect(this, target, asTextPart, spaceAllowed) {
                target.ls()
                if (resultCount != null) {
                    target.add("LIMIT ", spaced = false)
                    resultCount.appendTo(target, asTextPart = true, spaceAllowed = false)
                }
                if (firstResultIndex != null) {
                    target.add("OFFSET ", spaced = true)
                    firstResultIndex.appendTo(target, asTextPart = true, spaceAllowed = false)
                }
            }
        }
    }
}
