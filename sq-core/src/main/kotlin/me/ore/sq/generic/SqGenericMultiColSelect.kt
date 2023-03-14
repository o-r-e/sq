package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericMultiColSelect(context: SqContext, distinct: Boolean, columns: Iterable<SqColumn<*, *>>): SqGenericSelectBase(context, distinct, columns), SqMultiColSelect {
    override fun createColumnNotFoundException(column: SqColumn<*, *>): Exception {
        return super<SqGenericSelectBase>.createColumnNotFoundException(column)
    }

    override fun from(from: Iterable<SqColSet>): SqGenericMultiColSelect = this.apply { super<SqGenericSelectBase>.from(from) }
    override fun where(condition: SqExpression<*, Boolean>?): SqGenericMultiColSelect = this.apply { super.where(condition) }
    override fun groupBy(items: Iterable<SqColumn<*, *>>): SqGenericMultiColSelect = this.apply { super<SqGenericSelectBase>.groupBy(items) }
    override fun having(condition: SqExpression<*, Boolean>?): SqGenericMultiColSelect = this.apply { super.having(condition) }
    override fun orderBy(items: Iterable<SqOrderBy>): SqGenericMultiColSelect = this.apply { super<SqGenericSelectBase>.orderBy(items) }


    override fun firstResultIndex(firstResultIndex: SqParameter<Int, Number>?): SqGenericMultiColSelect = this.apply {
        super<SqGenericSelectBase>.firstResultIndex(firstResultIndex)
    }
    override fun firstResultIndex(firstResultIndex: Int?): SqGenericMultiColSelect = this.apply {
        super<SqGenericSelectBase>.firstResultIndex(firstResultIndex)
    }

    override fun resultCount(resultCount: SqParameter<Int, Number>?): SqGenericMultiColSelect = this.apply {
        super<SqGenericSelectBase>.resultCount(resultCount)
    }
    override fun resultCount(resultCount: Int?): SqGenericMultiColSelect = this.apply {
        super<SqGenericSelectBase>.resultCount(resultCount)
    }

    override fun limit(resultCount: Int, firstResultIndex: Int?): SqGenericMultiColSelect = this.apply {
        super<SqGenericSelectBase>.limit(resultCount, firstResultIndex)
    }
}
