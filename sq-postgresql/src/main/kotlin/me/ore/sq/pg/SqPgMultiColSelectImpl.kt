package me.ore.sq.pg

import me.ore.sq.*


open class SqPgMultiColSelectImpl(
    context: SqPgContext,
    distinct: Boolean,
    columns: Iterable<SqColumn<*, *>>,
): SqPgSelectBase(context, distinct, columns), SqPgMultiColSelect {
    override fun from(from: Iterable<SqColSet>): SqPgMultiColSelectImpl = this.apply { super.from(from) }
    override fun from(first: SqColSet, vararg more: SqColSet): SqPgMultiColSelectImpl = this.apply { super.from(first, *more) }
    override fun where(condition: SqExpression<*, Boolean>?): SqPgMultiColSelectImpl = this.apply { super.where(condition) }
    override fun groupBy(items: Iterable<SqColumn<*, *>>): SqPgMultiColSelectImpl = this.apply { super.groupBy(items) }
    override fun groupBy(first: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqPgMultiColSelectImpl = this.apply { super.groupBy(first, *more) }
    override fun having(condition: SqExpression<*, Boolean>?): SqPgMultiColSelectImpl = this.apply { super.having(condition) }
    override fun orderBy(items: Iterable<SqOrderBy>): SqPgMultiColSelectImpl = this.apply { super.orderBy(items) }
    override fun orderBy(first: SqOrderBy, vararg more: SqOrderBy): SqPgMultiColSelectImpl = this.apply { super.orderBy(first, *more) }

    override fun firstResultIndex(firstResultIndex: Int?): SqPgMultiColSelectImpl = this.apply { super.firstResultIndex(firstResultIndex) }
    override fun firstResultIndex(firstResultIndex: SqParameter<Int, Number>?): SqPgMultiColSelectImpl = this.apply { super.firstResultIndex(firstResultIndex) }

    override fun resultCount(resultCount: Int?): SqPgMultiColSelectImpl = this.apply { super.resultCount(resultCount) }
    override fun resultCount(resultCount: SqParameter<Int, Number>?): SqPgMultiColSelectImpl = this.apply { super.resultCount(resultCount) }

    override fun limit(resultCount: SqParameter<Int, Number>, firstResultIndex: SqParameter<Int, Number>?): SqPgMultiColSelectImpl =
        this.apply { super<SqPgSelectBase>.limit(resultCount, firstResultIndex) }
    override fun limit(resultCount: Int, firstResultIndex: Int?): SqPgMultiColSelectImpl =
        this.apply { super<SqPgSelectBase>.limit(resultCount, firstResultIndex) }

    override fun createColumnNotFoundException(column: SqColumn<*, *>): Exception = super<SqPgSelectBase>.createColumnNotFoundException(column)
}
