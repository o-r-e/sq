package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericMultiColSelect(
    context: SqContext, distinct: Boolean, columns: Iterable<SqColumn<*, *>>,
): SqGenericSelectBase(context, distinct, columns), SqGenericReadStatement, SqMultiColSelect {
    override fun createColumnNotFoundException(column: SqColumn<*, *>): Exception {
        return super<SqGenericSelectBase>.createColumnNotFoundException(column)
    }

    override fun from(from: Iterable<SqColSet>): SqGenericMultiColSelect = this.apply { super<SqGenericSelectBase>.from(from) }
    override fun where(condition: SqExpression<*, Boolean>?): SqGenericMultiColSelect = this.apply { super.where(condition) }
    override fun groupBy(items: Iterable<SqColumn<*, *>>): SqGenericMultiColSelect = this.apply { super<SqGenericSelectBase>.groupBy(items) }
    override fun having(condition: SqExpression<*, Boolean>?): SqGenericMultiColSelect = this.apply { super.having(condition) }
    override fun orderBy(items: Iterable<SqOrderBy>): SqGenericMultiColSelect = this.apply { super<SqGenericSelectBase>.orderBy(items) }


    // region Limiting results - first result index, result count
    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqGenericMultiColSelect = this.apply { super.firstResultIndex(firstResultIndex) }
    override fun firstResultIndex(firstResultIndex: Long?): SqGenericMultiColSelect = this.apply { super.firstResultIndex(firstResultIndex) }
    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqGenericMultiColSelect = this.apply { super.resultCount(resultCount) }
    override fun resultCount(resultCount: Long?): SqGenericMultiColSelect = this.apply { super.resultCount(resultCount) }
    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqGenericMultiColSelect = this.apply {
        super<SqGenericReadStatement>.limit(resultCount, firstResultIndex)
    }
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqGenericMultiColSelect = this.apply {
        super<SqGenericReadStatement>.limit(resultCount, firstResultIndex)
    }
    // endregion
}
