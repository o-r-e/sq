package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericSingleColSelect<JAVA: Any?, DB: Any>(
    context: SqContext, distinct: Boolean, column: SqColumn<JAVA, DB>
): SqGenericSelectBase(context, distinct, listOf(column)), SqSingleColSelect<JAVA, DB> {
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

    override fun firstResultIndex(firstResultIndex: SqParameter<Int, Number>?): SqGenericSingleColSelect<JAVA, DB> = this.apply {
        super<SqGenericSelectBase>.firstResultIndex(firstResultIndex)
    }
    override fun firstResultIndex(firstResultIndex: Int?): SqGenericSingleColSelect<JAVA, DB> = this.apply {
        super<SqGenericSelectBase>.firstResultIndex(firstResultIndex)
    }

    override fun resultCount(resultCount: SqParameter<Int, Number>?): SqGenericSingleColSelect<JAVA, DB> = this.apply {
        super<SqGenericSelectBase>.resultCount(resultCount)
    }
    override fun resultCount(resultCount: Int?): SqGenericSingleColSelect<JAVA, DB> = this.apply {
        super<SqGenericSelectBase>.resultCount(resultCount)
    }

    override fun limit(resultCount: Int, firstResultIndex: Int?): SqGenericSingleColSelect<JAVA, DB> = this.apply {
        super<SqGenericSelectBase>.limit(resultCount, firstResultIndex)
    }
}
