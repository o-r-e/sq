package me.ore.sq.pg

import me.ore.sq.*


interface SqPgSelect: SqSelect {
    override fun from(from: Iterable<SqColSet>): SqPgSelect
    override fun from(first: SqColSet, vararg more: SqColSet): SqPgSelect
    override fun where(condition: SqExpression<*, Boolean>?): SqPgSelect
    override fun groupBy(items: Iterable<SqColumn<*, *>>): SqPgSelect
    override fun groupBy(first: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqPgSelect
    override fun having(condition: SqExpression<*, Boolean>?): SqPgSelect
    override fun orderBy(items: Iterable<SqOrderBy>): SqPgSelect
    override fun orderBy(first: SqOrderBy, vararg more: SqOrderBy): SqPgSelect

    override fun firstResultIndex(firstResultIndex: Int?): SqPgSelect
    override fun resultCount(resultCount: Int?): SqPgSelect
    override fun limit(resultCount: Int, firstResultIndex: Int?): SqPgSelect
}

interface SqPgMultiColSelect: SqMultiColSelect, SqPgSelect {
    override fun from(from: Iterable<SqColSet>): SqPgMultiColSelect
    override fun from(first: SqColSet, vararg more: SqColSet): SqPgMultiColSelect
    override fun where(condition: SqExpression<*, Boolean>?): SqPgMultiColSelect
    override fun groupBy(items: Iterable<SqColumn<*, *>>): SqPgMultiColSelect
    override fun groupBy(first: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqPgMultiColSelect
    override fun having(condition: SqExpression<*, Boolean>?): SqPgMultiColSelect
    override fun orderBy(items: Iterable<SqOrderBy>): SqPgMultiColSelect
    override fun orderBy(first: SqOrderBy, vararg more: SqOrderBy): SqPgMultiColSelect

    override fun firstResultIndex(firstResultIndex: Int?): SqPgMultiColSelect
    override fun resultCount(resultCount: Int?): SqPgMultiColSelect
    override fun limit(resultCount: Int, firstResultIndex: Int?): SqPgMultiColSelect
}

interface SqPgSingleColSelect<JAVA: Any?, DB: Any>: SqSingleColSelect<JAVA, DB>, SqPgSelect {
    override fun from(from: Iterable<SqColSet>): SqPgSingleColSelect<JAVA, DB>
    override fun from(first: SqColSet, vararg more: SqColSet): SqPgSingleColSelect<JAVA, DB>
    override fun where(condition: SqExpression<*, Boolean>?): SqPgSingleColSelect<JAVA, DB>
    override fun groupBy(items: Iterable<SqColumn<*, *>>): SqPgSingleColSelect<JAVA, DB>
    override fun groupBy(first: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqPgSingleColSelect<JAVA, DB>
    override fun having(condition: SqExpression<*, Boolean>?): SqPgSingleColSelect<JAVA, DB>
    override fun orderBy(items: Iterable<SqOrderBy>): SqPgSingleColSelect<JAVA, DB>
    override fun orderBy(first: SqOrderBy, vararg more: SqOrderBy): SqPgSingleColSelect<JAVA, DB>

    override fun firstResultIndex(firstResultIndex: Int?): SqPgSingleColSelect<JAVA, DB>
    override fun resultCount(resultCount: Int?): SqPgSingleColSelect<JAVA, DB>
    override fun limit(resultCount: Int, firstResultIndex: Int?): SqPgSingleColSelect<JAVA, DB>
}

interface SqPgConnSelect: SqConnSelect, SqPgSelect {
    override fun from(from: Iterable<SqColSet>): SqPgConnSelect
    override fun from(first: SqColSet, vararg more: SqColSet): SqPgConnSelect
    override fun where(condition: SqExpression<*, Boolean>?): SqPgConnSelect
    override fun groupBy(items: Iterable<SqColumn<*, *>>): SqPgConnSelect
    override fun groupBy(first: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqPgConnSelect
    override fun having(condition: SqExpression<*, Boolean>?): SqPgConnSelect
    override fun orderBy(items: Iterable<SqOrderBy>): SqPgConnSelect
    override fun orderBy(first: SqOrderBy, vararg more: SqOrderBy): SqPgConnSelect

    override fun firstResultIndex(firstResultIndex: Int?): SqPgConnSelect
    override fun resultCount(resultCount: Int?): SqPgConnSelect
    override fun limit(resultCount: Int, firstResultIndex: Int?): SqPgConnSelect
}

interface SqPgConnMultiColSelect: SqConnMultiColSelect, SqPgConnSelect, SqPgMultiColSelect {
    override fun from(from: Iterable<SqColSet>): SqPgConnMultiColSelect
    override fun from(first: SqColSet, vararg more: SqColSet): SqPgConnMultiColSelect
    override fun where(condition: SqExpression<*, Boolean>?): SqPgConnMultiColSelect
    override fun groupBy(items: Iterable<SqColumn<*, *>>): SqPgConnMultiColSelect
    override fun groupBy(first: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqPgConnMultiColSelect
    override fun having(condition: SqExpression<*, Boolean>?): SqPgConnMultiColSelect
    override fun orderBy(items: Iterable<SqOrderBy>): SqPgConnMultiColSelect
    override fun orderBy(first: SqOrderBy, vararg more: SqOrderBy): SqPgConnMultiColSelect

    override fun firstResultIndex(firstResultIndex: Int?): SqPgConnMultiColSelect
    override fun resultCount(resultCount: Int?): SqPgConnMultiColSelect
    override fun limit(resultCount: Int, firstResultIndex: Int?): SqPgConnMultiColSelect
}

interface SqPgConnSingleColSelect<JAVA: Any?, DB: Any>: SqConnSingleColSelect<JAVA, DB>, SqPgConnSelect, SqPgSingleColSelect<JAVA, DB> {
    override fun from(from: Iterable<SqColSet>): SqPgConnSingleColSelect<JAVA, DB>
    override fun from(first: SqColSet, vararg more: SqColSet): SqPgConnSingleColSelect<JAVA, DB>
    override fun where(condition: SqExpression<*, Boolean>?): SqPgConnSingleColSelect<JAVA, DB>
    override fun groupBy(items: Iterable<SqColumn<*, *>>): SqPgConnSingleColSelect<JAVA, DB>
    override fun groupBy(first: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqPgConnSingleColSelect<JAVA, DB>
    override fun having(condition: SqExpression<*, Boolean>?): SqPgConnSingleColSelect<JAVA, DB>
    override fun orderBy(items: Iterable<SqOrderBy>): SqPgConnSingleColSelect<JAVA, DB>
    override fun orderBy(first: SqOrderBy, vararg more: SqOrderBy): SqPgConnSingleColSelect<JAVA, DB>

    override fun firstResultIndex(firstResultIndex: Int?): SqPgConnSingleColSelect<JAVA, DB>
    override fun resultCount(resultCount: Int?): SqPgConnSingleColSelect<JAVA, DB>
    override fun limit(resultCount: Int, firstResultIndex: Int?): SqPgConnSingleColSelect<JAVA, DB>
}
