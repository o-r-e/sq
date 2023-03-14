package me.ore.sq.pg

import me.ore.sq.*


open class SqPgConnSingleColSelectImpl<JAVA: Any?, DB: Any>(
    override val context: SqPgConnectedContext,
    distinct: Boolean,
    column: SqColumn<JAVA, DB>,
    nullable: Boolean = column.nullable,
    columns: Iterable<SqColumn<*, *>> = listOf(column),
): SqPgSingleColSelectImpl<JAVA, DB>(context, distinct, column, nullable, columns), SqPgConnSingleColSelect<JAVA, DB> {
    override fun from(from: Iterable<SqColSet>): SqPgConnSingleColSelectImpl<JAVA, DB> = this.apply { super.from(from) }
    override fun from(first: SqColSet, vararg more: SqColSet): SqPgConnSingleColSelectImpl<JAVA, DB> = this.apply { super.from(first, *more) }
    override fun where(condition: SqExpression<*, Boolean>?): SqPgConnSingleColSelectImpl<JAVA, DB> = this.apply { super.where(condition) }
    override fun groupBy(items: Iterable<SqColumn<*, *>>): SqPgConnSingleColSelectImpl<JAVA, DB> = this.apply { super.groupBy(items) }
    override fun groupBy(first: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqPgConnSingleColSelectImpl<JAVA, DB> = this.apply { super.groupBy(first, *more) }
    override fun having(condition: SqExpression<*, Boolean>?): SqPgConnSingleColSelectImpl<JAVA, DB> = this.apply { super.having(condition) }
    override fun orderBy(items: Iterable<SqOrderBy>): SqPgConnSingleColSelectImpl<JAVA, DB> = this.apply { super.orderBy(items) }
    override fun orderBy(first: SqOrderBy, vararg more: SqOrderBy): SqPgConnSingleColSelectImpl<JAVA, DB> = this.apply { super.orderBy(first, *more) }

    override fun firstResultIndex(firstResultIndex: SqParameter<Int, Number>?): SqPgConnSingleColSelectImpl<JAVA, DB> = this.apply { super.firstResultIndex(firstResultIndex) }
    override fun firstResultIndex(firstResultIndex: Int?): SqPgConnSingleColSelectImpl<JAVA, DB> = this.apply { super.firstResultIndex(firstResultIndex) }

    override fun resultCount(resultCount: SqParameter<Int, Number>?): SqPgConnSingleColSelectImpl<JAVA, DB> = this.apply { super.resultCount(resultCount) }
    override fun resultCount(resultCount: Int?): SqPgConnSingleColSelectImpl<JAVA, DB> = this.apply { super.resultCount(resultCount) }

    override fun limit(resultCount: SqParameter<Int, Number>, firstResultIndex: SqParameter<Int, Number>?): SqPgConnSingleColSelectImpl<JAVA, DB> =
        this.apply { super<SqPgSingleColSelectImpl>.limit(resultCount, firstResultIndex) }
    override fun limit(resultCount: Int, firstResultIndex: Int?): SqPgConnSingleColSelectImpl<JAVA, DB> =
        this.apply { super<SqPgSingleColSelectImpl>.limit(resultCount, firstResultIndex) }
}
