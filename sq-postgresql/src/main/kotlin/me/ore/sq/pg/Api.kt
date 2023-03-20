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

    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqPgSelect
    override fun firstResultIndex(firstResultIndex: Long?): SqPgSelect
    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqPgSelect
    override fun resultCount(resultCount: Long?): SqPgSelect
    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqPgSelect
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqPgSelect
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

    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqPgMultiColSelect
    override fun firstResultIndex(firstResultIndex: Long?): SqPgMultiColSelect
    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqPgMultiColSelect
    override fun resultCount(resultCount: Long?): SqPgMultiColSelect
    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqPgMultiColSelect
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqPgMultiColSelect
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

    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqPgSingleColSelect<JAVA, DB>
    override fun firstResultIndex(firstResultIndex: Long?): SqPgSingleColSelect<JAVA, DB>
    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqPgSingleColSelect<JAVA, DB>
    override fun resultCount(resultCount: Long?): SqPgSingleColSelect<JAVA, DB>
    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqPgSingleColSelect<JAVA, DB>
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqPgSingleColSelect<JAVA, DB>
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

    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqPgConnSelect
    override fun firstResultIndex(firstResultIndex: Long?): SqPgConnSelect
    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqPgConnSelect
    override fun resultCount(resultCount: Long?): SqPgConnSelect
    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqPgConnSelect
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqPgConnSelect
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

    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqPgConnMultiColSelect
    override fun firstResultIndex(firstResultIndex: Long?): SqPgConnMultiColSelect
    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqPgConnMultiColSelect
    override fun resultCount(resultCount: Long?): SqPgConnMultiColSelect
    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqPgConnMultiColSelect
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqPgConnMultiColSelect
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

    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqPgConnSingleColSelect<JAVA, DB>
    override fun firstResultIndex(firstResultIndex: Long?): SqPgConnSingleColSelect<JAVA, DB>
    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqPgConnSingleColSelect<JAVA, DB>
    override fun resultCount(resultCount: Long?): SqPgConnSingleColSelect<JAVA, DB>
    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqPgConnSingleColSelect<JAVA, DB>
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqPgConnSingleColSelect<JAVA, DB>
}


interface SqPgUnion: SqUnion {
    override val context: SqPgContext

    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqPgUnion
    override fun firstResultIndex(firstResultIndex: Long?): SqPgUnion
    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqPgUnion
    override fun resultCount(resultCount: Long?): SqPgUnion
    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqPgUnion
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqPgUnion
}

interface SqPgMultiColUnion: SqMultiColUnion, SqPgUnion {
    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqPgMultiColUnion
    override fun firstResultIndex(firstResultIndex: Long?): SqPgMultiColUnion
    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqPgMultiColUnion
    override fun resultCount(resultCount: Long?): SqPgMultiColUnion
    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqPgMultiColUnion
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqPgMultiColUnion
}

interface SqPgSingleColUnion<JAVA: Any?, DB: Any>: SqSingleColUnion<JAVA, DB>, SqPgUnion {
    override fun nullable(): SqPgSingleColUnion<JAVA?, DB>

    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqPgSingleColUnion<JAVA, DB>
    override fun firstResultIndex(firstResultIndex: Long?): SqPgSingleColUnion<JAVA, DB>
    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqPgSingleColUnion<JAVA, DB>
    override fun resultCount(resultCount: Long?): SqPgSingleColUnion<JAVA, DB>
    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqPgSingleColUnion<JAVA, DB>
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqPgSingleColUnion<JAVA, DB>
}

interface SqPgConnUnion: SqConnUnion, SqPgUnion {
    override val context: SqPgConnectedContext

    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqPgConnUnion
    override fun firstResultIndex(firstResultIndex: Long?): SqPgConnUnion
    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqPgConnUnion
    override fun resultCount(resultCount: Long?): SqPgConnUnion
    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqPgConnUnion
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqPgConnUnion
}

interface SqPgConnMultiColUnion: SqConnMultiColUnion, SqPgConnUnion, SqPgMultiColUnion {
    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqPgConnMultiColUnion
    override fun firstResultIndex(firstResultIndex: Long?): SqPgConnMultiColUnion
    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqPgConnMultiColUnion
    override fun resultCount(resultCount: Long?): SqPgConnMultiColUnion
    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqPgConnMultiColUnion
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqPgConnMultiColUnion
}

interface SqPgConnSingleColUnion<JAVA: Any?, DB: Any>: SqConnSingleColUnion<JAVA, DB>, SqPgConnUnion, SqPgSingleColUnion<JAVA, DB> {
    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqPgConnSingleColUnion<JAVA, DB>
    override fun firstResultIndex(firstResultIndex: Long?): SqPgConnSingleColUnion<JAVA, DB>
    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqPgConnSingleColUnion<JAVA, DB>
    override fun resultCount(resultCount: Long?): SqPgConnSingleColUnion<JAVA, DB>
    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqPgConnSingleColUnion<JAVA, DB>
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqPgConnSingleColUnion<JAVA, DB>
}
