package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericSingleColSelect<JAVA: Any?, DB: Any>(
    context: SqContext, distinct: Boolean, column: SqColumn<JAVA, DB>
): SqGenericSelectBase(context, distinct, listOf(column)), SqGenericReadStatement, SqSingleColSelect<JAVA, DB> {
    @Suppress("PropertyName")
    protected open var _nullable: Boolean = column.nullable

    override val nullable: Boolean
        get() = this._nullable

    override fun nullable(): SqGenericSingleColSelect<JAVA?, DB> {
        this._nullable = true
        return SqUtil.uncheckedCast(this)
    }


    @Suppress("CanBePrimaryConstructorProperty")
    override val column: SqColumn<JAVA, DB> = column

    override fun createColumnNotFoundException(column: SqColumn<*, *>): Exception {
        return super<SqGenericSelectBase>.createColumnNotFoundException(column)
    }


    override fun from(from: Iterable<SqColSet>): SqGenericSingleColSelect<JAVA, DB> = this.apply { super<SqGenericSelectBase>.from(from) }
    override fun where(condition: SqExpression<*, Boolean>?): SqGenericSingleColSelect<JAVA, DB> = this.apply { super.where(condition) }
    override fun groupBy(items: Iterable<SqColumn<*, *>>): SqGenericSingleColSelect<JAVA, DB> = this.apply { super<SqGenericSelectBase>.groupBy(items) }
    override fun having(condition: SqExpression<*, Boolean>?): SqGenericSingleColSelect<JAVA, DB> = this.apply { super.having(condition) }
    override fun orderBy(items: Iterable<SqOrderBy>): SqGenericSingleColSelect<JAVA, DB> = this.apply { super<SqGenericSelectBase>.orderBy(items) }


    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqGenericSingleColSelect<JAVA, DB> = this.apply { super.firstResultIndex(firstResultIndex) }
    override fun firstResultIndex(firstResultIndex: Long?): SqGenericSingleColSelect<JAVA, DB> = this.apply { super.firstResultIndex(firstResultIndex) }
    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqGenericSingleColSelect<JAVA, DB> = this.apply { super.resultCount(resultCount) }
    override fun resultCount(resultCount: Long?): SqGenericSingleColSelect<JAVA, DB> = this.apply { super.resultCount(resultCount) }
    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqGenericSingleColSelect<JAVA, DB> = this.apply {
        super<SqGenericReadStatement>.limit(resultCount, firstResultIndex)
    }
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqGenericSingleColSelect<JAVA, DB> = this.apply  {
        super<SqGenericReadStatement>.limit(resultCount, firstResultIndex)
    }
}
