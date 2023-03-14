package me.ore.sq.pg

import me.ore.sq.*


open class SqPgSingleColSelectImpl<JAVA: Any?, DB: Any>(
    context: SqPgContext,
    distinct: Boolean,
    override val column: SqColumn<JAVA, DB>,
    nullable: Boolean = column.nullable,
    columns: Iterable<SqColumn<*, *>> = listOf(column),
): SqPgSelectBase(context, distinct, columns), SqPgSingleColSelect<JAVA, DB> {
    override fun from(from: Iterable<SqColSet>): SqPgSingleColSelectImpl<JAVA, DB> = this.apply { super.from(from) }
    override fun from(first: SqColSet, vararg more: SqColSet): SqPgSingleColSelectImpl<JAVA, DB> = this.apply { super.from(first, *more) }
    override fun where(condition: SqExpression<*, Boolean>?): SqPgSingleColSelectImpl<JAVA, DB> = this.apply { super.where(condition) }
    override fun groupBy(items: Iterable<SqColumn<*, *>>): SqPgSingleColSelectImpl<JAVA, DB> = this.apply { super.groupBy(items) }
    override fun groupBy(first: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqPgSingleColSelectImpl<JAVA, DB> = this.apply { super.groupBy(first, *more) }
    override fun having(condition: SqExpression<*, Boolean>?): SqPgSingleColSelectImpl<JAVA, DB> = this.apply { super.having(condition) }
    override fun orderBy(items: Iterable<SqOrderBy>): SqPgSingleColSelectImpl<JAVA, DB> = this.apply { super.orderBy(items) }
    override fun orderBy(first: SqOrderBy, vararg more: SqOrderBy): SqPgSingleColSelectImpl<JAVA, DB> = this.apply { super.orderBy(first, *more) }

    override fun firstResultIndex(firstResultIndex: SqParameter<Int, Number>?): SqPgSingleColSelectImpl<JAVA, DB> = this.apply { super.firstResultIndex(firstResultIndex) }
    override fun firstResultIndex(firstResultIndex: Int?): SqPgSingleColSelectImpl<JAVA, DB> = this.apply { super.firstResultIndex(firstResultIndex) }

    override fun resultCount(resultCount: SqParameter<Int, Number>?): SqPgSingleColSelectImpl<JAVA, DB> = this.apply { super.resultCount(resultCount) }
    override fun resultCount(resultCount: Int?): SqPgSingleColSelectImpl<JAVA, DB> = this.apply { super.resultCount(resultCount) }

    override fun limit(resultCount: SqParameter<Int, Number>, firstResultIndex: SqParameter<Int, Number>?): SqPgSingleColSelectImpl<JAVA, DB> =
        this.apply { super<SqPgSelectBase>.limit(resultCount, firstResultIndex) }
    override fun limit(resultCount: Int, firstResultIndex: Int?): SqPgSingleColSelectImpl<JAVA, DB> =
        this.apply { super<SqPgSelectBase>.limit(resultCount, firstResultIndex) }


    // region Nullable
    @Suppress("MemberVisibilityCanBePrivate", "PropertyName")
    protected var _nullable: Boolean = nullable

    override val nullable: Boolean
        get() = this._nullable

    override fun nullable(): SqPgSingleColSelectImpl<JAVA?, DB> {
        this._nullable = true
        return SqUtil.uncheckedCast(this)
    }
    // endregion

    override fun createColumnNotFoundException(column: SqColumn<*, *>): Exception = super<SqPgSelectBase>.createColumnNotFoundException(column)
}
