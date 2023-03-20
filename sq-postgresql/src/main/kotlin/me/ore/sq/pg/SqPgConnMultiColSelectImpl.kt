package me.ore.sq.pg

import me.ore.sq.*


open class SqPgConnMultiColSelectImpl(
    override val context: SqPgConnectedContext,
    distinct: Boolean,
    columns: Iterable<SqColumn<*, *>>,
): SqPgMultiColSelectImpl(context, distinct, columns), SqPgConnMultiColSelect {
    override fun from(from: Iterable<SqColSet>): SqPgConnMultiColSelectImpl = this.apply { super.from(from) }
    override fun from(first: SqColSet, vararg more: SqColSet): SqPgConnMultiColSelectImpl = this.apply { super.from(first, *more) }
    override fun where(condition: SqExpression<*, Boolean>?): SqPgConnMultiColSelectImpl = this.apply { super.where(condition) }
    override fun groupBy(items: Iterable<SqColumn<*, *>>): SqPgConnMultiColSelectImpl = this.apply { super.groupBy(items) }
    override fun groupBy(first: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqPgConnMultiColSelectImpl = this.apply { super.groupBy(first, *more) }
    override fun having(condition: SqExpression<*, Boolean>?): SqPgConnMultiColSelectImpl = this.apply { super.having(condition) }
    override fun orderBy(items: Iterable<SqOrderBy>): SqPgConnMultiColSelectImpl = this.apply { super.orderBy(items) }
    override fun orderBy(first: SqOrderBy, vararg more: SqOrderBy): SqPgConnMultiColSelectImpl = this.apply { super.orderBy(first, *more) }

    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqPgConnMultiColSelectImpl = this.apply { super.firstResultIndex(firstResultIndex) }
    override fun firstResultIndex(firstResultIndex: Long?): SqPgConnMultiColSelectImpl = this.apply { super.firstResultIndex(firstResultIndex) }

    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqPgConnMultiColSelectImpl = this.apply { super.resultCount(resultCount) }
    override fun resultCount(resultCount: Long?): SqPgConnMultiColSelectImpl = this.apply { super.resultCount(resultCount) }

    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqPgConnMultiColSelectImpl =
        this.apply { super.limit(resultCount, firstResultIndex) }
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqPgConnMultiColSelectImpl =
        this.apply { super.limit(resultCount, firstResultIndex) }
}
