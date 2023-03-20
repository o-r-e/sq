package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericConnMultiColSelect(
    override val context: SqConnectedContext, distinct: Boolean, columns: Iterable<SqColumn<*, *>>
): SqGenericMultiColSelect(context, distinct, columns), SqConnMultiColSelect {
    override fun from(from: Iterable<SqColSet>): SqGenericConnMultiColSelect = this.apply { super<SqGenericMultiColSelect>.from(from) }
    override fun where(condition: SqExpression<*, Boolean>?): SqGenericConnMultiColSelect = this.apply { super.where(condition) }
    override fun groupBy(items: Iterable<SqColumn<*, *>>): SqGenericConnMultiColSelect = this.apply { super<SqGenericMultiColSelect>.groupBy(items) }
    override fun having(condition: SqExpression<*, Boolean>?): SqGenericConnMultiColSelect = this.apply { super.having(condition) }
    override fun orderBy(items: Iterable<SqOrderBy>): SqGenericConnMultiColSelect = this.apply { super<SqGenericMultiColSelect>.orderBy(items) }

    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqGenericConnMultiColSelect = this.apply {
        super.firstResultIndex(firstResultIndex)
    }
    override fun firstResultIndex(firstResultIndex: Long?): SqGenericConnMultiColSelect = this.apply {
        super.firstResultIndex(firstResultIndex)
    }
    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqGenericConnMultiColSelect = this.apply {
        super.resultCount(resultCount)
    }
    override fun resultCount(resultCount: Long?): SqGenericConnMultiColSelect = this.apply {
        super.resultCount(resultCount)
    }
    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqGenericConnMultiColSelect = this.apply {
        super<SqGenericMultiColSelect>.limit(resultCount, firstResultIndex)
    }
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqGenericConnMultiColSelect = this.apply {
        super<SqGenericMultiColSelect>.limit(resultCount, firstResultIndex)
    }
}
