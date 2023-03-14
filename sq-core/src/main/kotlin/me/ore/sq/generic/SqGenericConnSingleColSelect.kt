package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericConnSingleColSelect<JAVA: Any?, DB: Any>(
    override val context: SqConnectedContext, distinct: Boolean, column: SqColumn<JAVA, DB>
): SqGenericSingleColSelect<JAVA, DB>(context, distinct, column), SqConnSingleColSelect<JAVA, DB> {
    override fun from(from: Iterable<SqColSet>): SqGenericConnSingleColSelect<JAVA, DB> = this.apply { super<SqGenericSingleColSelect>.from(from) }
    override fun where(condition: SqExpression<*, Boolean>?): SqGenericConnSingleColSelect<JAVA, DB> = this.apply { super.where(condition) }
    override fun groupBy(items: Iterable<SqColumn<*, *>>): SqGenericConnSingleColSelect<JAVA, DB> = this.apply { super<SqGenericSingleColSelect>.groupBy(items) }
    override fun having(condition: SqExpression<*, Boolean>?): SqGenericConnSingleColSelect<JAVA, DB> = this.apply { super.having(condition) }
    override fun orderBy(items: Iterable<SqOrderBy>): SqGenericConnSingleColSelect<JAVA, DB> = this.apply { super<SqGenericSingleColSelect>.orderBy(items) }

    override fun firstResultIndex(firstResultIndex: SqParameter<Int, Number>?): SqGenericConnSingleColSelect<JAVA, DB> = this.apply {
        super<SqGenericSingleColSelect>.firstResultIndex(firstResultIndex)
    }
    override fun firstResultIndex(firstResultIndex: Int?): SqGenericConnSingleColSelect<JAVA, DB> = this.apply {
        super<SqGenericSingleColSelect>.firstResultIndex(firstResultIndex)
    }

    override fun resultCount(resultCount: SqParameter<Int, Number>?): SqGenericConnSingleColSelect<JAVA, DB> = this.apply {
        super<SqGenericSingleColSelect>.resultCount(resultCount)
    }
    override fun resultCount(resultCount: Int?): SqGenericConnSingleColSelect<JAVA, DB> = this.apply {
        super<SqGenericSingleColSelect>.resultCount(resultCount)
    }

    override fun limit(resultCount: Int, firstResultIndex: Int?): SqGenericConnSingleColSelect<JAVA, DB> = this.apply {
        super<SqGenericSingleColSelect>.limit(resultCount, firstResultIndex)
    }
}
