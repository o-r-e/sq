package me.ore.sq.generic

import me.ore.sq.*


abstract class SqGenericContextBase: SqContextBase(), SqGenericContext {
    // region Utils
    override fun createWriter(): SqWriter = SqGenericWriter()

    override fun start() {
        super<SqContextBase>.start()
        super<SqGenericContext>.start()
    }

    override fun finish() {
        super<SqGenericContext>.finish()
        super<SqContextBase>.finish()
    }
    // endregion


    // region Base items
    override fun <JAVA: Any, DB: Any> nullItem(type: SqType<JAVA>): SqNull<JAVA, DB> = SqGenericNull(this, type)

    override fun <JAVA, DB: Any> rawParameter(type: SqType<JAVA & Any>, nullable: Boolean, value: JAVA): SqParameter<JAVA, DB> = SqGenericParameter(this, type, nullable, value)

    override fun <JAVA, DB: Any, ORIG : SqExpression<JAVA, DB>> expressionAlias(original: ORIG, alias: String): SqExpressionAlias<JAVA, DB, ORIG> {
        return SqGenericExpressionAlias(this, original, alias)
    }


    override fun <ORIG : SqMultiColSet> multiColSetAlias(original: ORIG, alias: String): SqMultiColSetAlias<ORIG> {
        return SqGenericMultiColSetAlias(this, original, alias)
    }

    override fun <JAVA, DB: Any, ORIG : SqSingleColSet<JAVA?, DB>> singleColSetAlias(original: ORIG, alias: String): SqSingleColSetAlias<JAVA, DB, ORIG> {
        return SqGenericSingleColSetAlias(this, original, alias)
    }

    override fun <JAVA, DB: Any> colSetAliasColumn(alias: SqColSetAlias<*>, column: SqColumn<JAVA, DB>): SqColSetAliasColumn<JAVA, DB> {
        return SqGenericColSetAliasColumn(this, alias, column)
    }
    // endregion


    // region Comparisons - groups and "single value" tests
    override fun and(type: SqType<Boolean>, values: Iterable<SqExpression<*, Boolean>>): SqMultiValueTest<Boolean> = SqGenericOperations.and(this, type, values)
    override fun or(type: SqType<Boolean>, values: Iterable<SqExpression<*, Boolean>>): SqMultiValueTest<Boolean> = SqGenericOperations.or(this, type, values)

    override fun not(type: SqType<Boolean>, value: SqExpression<*, Boolean>): SqSingleValueTest<Boolean> = SqGenericOperations.not(this, type, value)

    override fun isNull(type: SqType<Boolean>, value: SqExpression<*, *>): SqSingleValueTest<Boolean> = SqGenericOperations.isNull(this, type, value)
    override fun isNotNull(type: SqType<Boolean>, value: SqExpression<*, *>): SqSingleValueTest<Boolean> = SqGenericOperations.isNotNull(this, type, value)
    // endregion


    // region Comparisons - "two value" tests
    override fun <DB: Any> eq(type: SqType<Boolean>, firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>): SqTwoValueTest<Boolean> =
        SqGenericOperations.equal(this, type, firstValue, secondValue)
    override fun <DB: Any> neq(type: SqType<Boolean>, firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>): SqTwoValueTest<Boolean> =
        SqGenericOperations.notEqual(this, type, firstValue, secondValue)
    override fun <DB: Any> gt(type: SqType<Boolean>, firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>): SqTwoValueTest<Boolean> =
        SqGenericOperations.greaterThan(this, type, firstValue, secondValue)
    override fun <DB: Any> gte(type: SqType<Boolean>, firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>): SqTwoValueTest<Boolean> =
        SqGenericOperations.greaterThanOrEqual(this, type, firstValue, secondValue)
    override fun <DB: Any> lt(type: SqType<Boolean>, firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>): SqTwoValueTest<Boolean> =
        SqGenericOperations.lessThan(this, type, firstValue, secondValue)
    override fun <DB: Any> lte(type: SqType<Boolean>, firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>): SqTwoValueTest<Boolean> =
        SqGenericOperations.lessThanOrEqual(this, type, firstValue, secondValue)
    override fun like(type: SqType<Boolean>, firstValue: SqExpression<*, String>, secondValue: SqExpression<*, String>): SqTwoValueTest<Boolean> =
        SqGenericOperations.like(this, type, firstValue, secondValue)
    override fun notLike(type: SqType<Boolean>, firstValue: SqExpression<*, String>, secondValue: SqExpression<*, String>): SqTwoValueTest<Boolean> =
        SqGenericOperations.notLike(this, type, firstValue, secondValue)
    // endregion


    // region Comparisons - between, not between, in [list], not in [list]
    override fun <DB: Any> between(
        type: SqType<Boolean>,
        mainValue: SqExpression<*, DB>,
        firstBoundsValue: SqExpression<*, DB>,
        secondBoundsValue: SqExpression<*, DB>,
    ): SqBetweenTest<Boolean> =
        SqGenericOperations.between(this, type, mainValue, firstBoundsValue, secondBoundsValue)
    override fun <DB: Any> notBetween(
        type: SqType<Boolean>,
        mainValue: SqExpression<*, DB>,
        firstBoundsValue: SqExpression<*, DB>,
        secondBoundsValue: SqExpression<*, DB>,
    ): SqBetweenTest<Boolean> =
        SqGenericOperations.notBetween(this, type, mainValue, firstBoundsValue, secondBoundsValue)


    override fun <DB: Any> inList(type: SqType<Boolean>, mainValue: SqExpression<*, DB>, listValues: Array<out SqExpression<*, DB>>): SqInListTest<Boolean> =
        SqGenericOperations.inList(this, type, mainValue, listValues)
    override fun <DB: Any> notInList(type: SqType<Boolean>, mainValue: SqExpression<*, DB>, listValues: Array<out SqExpression<*, DB>>): SqInListTest<Boolean> =
        SqGenericOperations.notInList(this, type, mainValue, listValues)
    // endregion


    // region Functions
    override fun <JAVA, DB: Any> function(
        type: SqType<JAVA & Any>,
        nullable: Boolean,
        name: String,
        nameSeparated: Boolean,
        values: Iterable<SqItem>
    ): SqNamedFunction<JAVA, DB> {
        return SqGenericNamedFunction(this, type, nullable, name, nameSeparated, values.toList())
    }
    // endregion


    // region Case
    override fun caseWhen(whenItem: SqExpression<*, Boolean>): SqCaseItemStart = SqGenericCaseItemStart(this, whenItem)

    override fun <JAVA, DB: Any> caseItem(whenItem: SqExpression<*, Boolean>, thenItem: SqExpression<JAVA, DB>): SqCaseItem<JAVA, DB> {
        return SqGenericCaseItem(this, whenItem, thenItem)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected open fun <JAVA, DB: Any> caseImpl(forceNullable: Boolean, items: Iterable<SqCaseItem<out JAVA, DB>>, elseItem: SqExpression<out JAVA, DB>?): SqCase<out JAVA, DB> {
        val anyItem = items.firstOrNull()?.thenItem
            ?: elseItem
            ?: throw IllegalStateException("\"Case\" has no item - common item list is empty and \"else\" item is null")
        val type = anyItem.type

        val nullable = forceNullable || run {
            var nullable = false

            for (item in items) {
                if (item.thenItem.nullable) {
                    nullable = true
                    break
                }
            }

            if ((!nullable) && (elseItem != null) && (elseItem.nullable)) {
                nullable = true
            }

            nullable
        }

        val result = SqGenericCase<JAVA, DB>(
            this,
            SqUtil.uncheckedCast(type),
            nullable,
            SqUtil.uncheckedCast(items),
            SqUtil.uncheckedCast(elseItem)
        )
        return SqUtil.uncheckedCast(result)
    }

    override fun <JAVA, DB: Any> case(forceNullable: Boolean, items: Iterable<SqCaseItem<JAVA, DB>>): SqCase<JAVA?, DB> {
        return SqUtil.uncheckedCast(this.caseImpl(forceNullable, items, null))
    }

    override fun <JAVA, DB: Any> case(forceNullable: Boolean, items: Iterable<SqCaseItem<out JAVA, DB>>, elseItem: SqExpression<out JAVA, DB>): SqCase<out JAVA, DB> {
        return this.caseImpl(forceNullable, items, elseItem)
    }
    // endregion


    // region Mathematical operations
    override fun <JAVA : Number?> twoOperandMathOperation(
        type: SqType<JAVA & Any>,
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        operation: String,
        secondOperand: SqExpression<*, Number>
    ): SqTwoOperandMathOperation<JAVA> {
        return SqGenericOperations.twoOperandMathOperation(this, type, nullable, firstOperand, operation, secondOperand)
    }
    // endregion


    // region Statements - join, order by, select, union
    override fun createJoin(type: SqJoinType, mainColSet: SqColSet, joinedColSet: SqColSet): SqJoin = SqGenericJoin(this, type, mainColSet, joinedColSet)
    override fun createOrderBy(column: SqColumn<*, *>, order: SqSortOrder): SqOrderBy = SqGenericOrderBy(this, column, order)

    override fun select(distinct: Boolean, columns: Iterable<SqColumn<*, *>>): SqMultiColSelect = SqGenericMultiColSelect(this, distinct, columns)
    override fun <JAVA: Any?, DB: Any> select(distinct: Boolean, column: SqColumn<JAVA, DB>): SqSingleColSelect<JAVA, DB> = SqGenericSingleColSelect(this, distinct, column)

    override fun union(unionAll: Boolean, selects: Iterable<SqSelect>): SqMultiColUnion = SqGenericMultiColUnion(this, unionAll, selects)
    override fun <JAVA: Any?, DB: Any> union(unionAll: Boolean, selects: Iterable<SqSingleColSelect<JAVA, DB>>): SqSingleColUnion<JAVA, DB> =
        SqGenericSingleColUnion(this, unionAll, selects)
    // endregion


    // region Statements - other
    override fun <T: SqTable> insertInto(table: T): SqInsert<T> = SqGenericInsert(this, table)
    override fun <T : SqTable> update(table: T): SqUpdate<T> = SqGenericUpdate(this, table)
    override fun <T : SqTable> deleteFrom(table: T): SqDelete<T> = SqGenericDelete(this, table)
    // endregion
}
