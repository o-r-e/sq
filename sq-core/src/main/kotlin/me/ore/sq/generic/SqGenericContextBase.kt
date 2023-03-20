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
    protected open fun SqType<Boolean>?.getActual(): SqType<Boolean> = this ?: SqGenericTypes.BOOLEAN

    override fun and(values: Iterable<SqExpression<*, Boolean>>, type: SqType<Boolean>?): SqMultiValueTest<Boolean> = SqGenericOperations.and(this, type.getActual(), values)
    override fun or(values: Iterable<SqExpression<*, Boolean>>, type: SqType<Boolean>?): SqMultiValueTest<Boolean> = SqGenericOperations.or(this, type.getActual(), values)
    override fun not(value: SqExpression<*, Boolean>, type: SqType<Boolean>?): SqSingleValueTest<Boolean> = SqGenericOperations.not(this, type.getActual(), value)
    override fun isNull(value: SqExpression<*, *>, type: SqType<Boolean>?, methodDiff: Boolean): SqSingleValueTest<Boolean> =
        SqGenericOperations.isNull(this, type.getActual(), value)
    override fun isNotNull(value: SqExpression<*, *>, type: SqType<Boolean>?, methodDiff: Boolean): SqSingleValueTest<Boolean> =
        SqGenericOperations.isNotNull(this, type.getActual(), value)
    // endregion


    // region Comparisons - "two value" tests
    override fun <DB : Any> eq(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean>?, methodDiff: Boolean): SqTwoValueTest<Boolean> =
        SqGenericOperations.equal(this, type.getActual(), firstValue, secondValue)
    override fun <DB : Any> neq(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean>?, methodDiff: Boolean): SqTwoValueTest<Boolean> =
        SqGenericOperations.notEqual(this, type.getActual(), firstValue, secondValue)
    override fun <DB : Any> gt(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean>?, methodDiff: Boolean): SqTwoValueTest<Boolean> =
        SqGenericOperations.greaterThan(this, type.getActual(), firstValue, secondValue)
    override fun <DB : Any> gte(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean>?, methodDiff: Boolean): SqTwoValueTest<Boolean> =
        SqGenericOperations.greaterThanOrEqual(this, type.getActual(), firstValue, secondValue)
    override fun <DB : Any> lt(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean>?, methodDiff: Boolean): SqTwoValueTest<Boolean> =
        SqGenericOperations.lessThan(this, type.getActual(), firstValue, secondValue)
    override fun <DB : Any> lte(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean>?, methodDiff: Boolean): SqTwoValueTest<Boolean> =
        SqGenericOperations.lessThanOrEqual(this, type.getActual(), firstValue, secondValue)
    override fun like(firstValue: SqExpression<*, String>, secondValue: SqExpression<*, String>, type: SqType<Boolean>?, methodDiff: Boolean): SqTwoValueTest<Boolean> =
        SqGenericOperations.like(this, type.getActual(), firstValue, secondValue)
    override fun notLike(firstValue: SqExpression<*, String>, secondValue: SqExpression<*, String>, type: SqType<Boolean>?, methodDiff: Boolean): SqTwoValueTest<Boolean> =
        SqGenericOperations.notLike(this, type.getActual(), firstValue, secondValue)
    // endregion


    // region Comparisons - between, not between, in [list], not in [list]
    override fun <DB : Any> between(
        mainValue: SqExpression<*, DB>,
        firstBoundsValue: SqExpression<*, DB>,
        secondBoundsValue: SqExpression<*, DB>,
        type: SqType<Boolean>?,
        methodDiff: Boolean
    ): SqBetweenTest<Boolean> =
        SqGenericOperations.between(this, type.getActual(), mainValue, firstBoundsValue, secondBoundsValue)
    override fun <DB : Any> notBetween(
        mainValue: SqExpression<*, DB>,
        firstBoundsValue: SqExpression<*, DB>,
        secondBoundsValue: SqExpression<*, DB>,
        type: SqType<Boolean>?,
        methodDiff: Boolean
    ): SqBetweenTest<Boolean> =
        SqGenericOperations.notBetween(this, type.getActual(), mainValue, firstBoundsValue, secondBoundsValue)

    override fun <DB : Any> inList(mainValue: SqExpression<*, DB>, listValues: Array<out SqExpression<*, DB>>, type: SqType<Boolean>?): SqInListTest<Boolean> =
        SqGenericOperations.inList(this, type.getActual(), mainValue, listValues)
    override fun <DB : Any> notInList(mainValue: SqExpression<*, DB>, listValues: Array<out SqExpression<*, DB>>, type: SqType<Boolean>?): SqInListTest<Boolean> =
        SqGenericOperations.notInList(this, type.getActual(), mainValue, listValues)
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


    override fun count(value: SqExpression<*, *>, type: SqType<Long>?): SqNamedFunction<Long, Number> {
        val actualType = type ?: SqGenericTypes.BIG_INT
        return this.function(actualType, false, SqUtil.FUNCTION_NAME__COUNT, false, value)
    }

    override fun exists(select: SqSelect, type: SqType<Boolean>?): SqNamedFunction<Boolean, Boolean> {
        return this.function(type.getActual(), false, SqUtil.FUNCTION_NAME__EXISTS, true, select)
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
    protected open fun <JAVA: Number?> SqType<JAVA & Any>?.mathOpType(): SqType<JAVA & Any> = this ?: SqGenericTypes.J_INEXACT_NUMBER_TYPE.sqCast()

    protected open fun <JAVA: Number?> mathOperandType(operandValue: JAVA): SqType<JAVA & Any> = SqGenericTypes.J_INEXACT_NUMBER_TYPE.sqCast()

    protected open fun <JAVA: Number?> mathOperandParam(operandValue: JAVA, type: SqType<JAVA & Any>? = null): SqParameter<JAVA, Number> =
        SqParameter(this.mathOperandType(operandValue), operandValue, this)

    override fun <JAVA : Number?> add(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperand: SqExpression<*, Number>,
        type: SqType<JAVA & Any>?
    ): SqTwoOperandMathOperation<JAVA> =
        SqGenericOperations.twoOperandMathOperation(this, type.mathOpType(), nullable, firstOperand, SqUtil.MATH_OPERATION__ADD, secondOperand)
    override fun <JAVA : Number?, PARAM : Number?> add(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>?,
        resultType: SqType<JAVA & Any>?
    ): SqTwoOperandMathOperation<JAVA> =
        this.add(nullable, firstOperand, this.mathOperandParam(secondOperandValue, secondOperandType), resultType)

    override fun <JAVA : Number?> sub(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperand: SqExpression<*, Number>,
        type: SqType<JAVA & Any>?
    ): SqTwoOperandMathOperation<JAVA> =
        SqGenericOperations.twoOperandMathOperation(this, type.mathOpType(), nullable, firstOperand, SqUtil.MATH_OPERATION__SUBTRACT, secondOperand)
    override fun <JAVA : Number?, PARAM : Number?> sub(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>?,
        resultType: SqType<JAVA & Any>?
    ): SqTwoOperandMathOperation<JAVA> =
        this.sub(nullable, firstOperand, this.mathOperandParam(secondOperandValue, secondOperandType), resultType)

    override fun <JAVA : Number?> mult(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperand: SqExpression<*, Number>,
        type: SqType<JAVA & Any>?
    ): SqTwoOperandMathOperation<JAVA> =
        SqGenericOperations.twoOperandMathOperation(this, type.mathOpType(), nullable, firstOperand, SqUtil.MATH_OPERATION__MULTIPLY, secondOperand)
    override fun <JAVA : Number?, PARAM : Number?> mult(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>?,
        resultType: SqType<JAVA & Any>?
    ): SqTwoOperandMathOperation<JAVA> =
        this.mult(nullable, firstOperand, this.mathOperandParam(secondOperandValue, secondOperandType), resultType)

    override fun <JAVA : Number?> div(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperand: SqExpression<*, Number>,
        type: SqType<JAVA & Any>?
    ): SqTwoOperandMathOperation<JAVA> =
        SqGenericOperations.twoOperandMathOperation(this, type.mathOpType(), nullable, firstOperand, SqUtil.MATH_OPERATION__DIVIDE, secondOperand)
    override fun <JAVA : Number?, PARAM : Number?> div(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>?,
        resultType: SqType<JAVA & Any>?
    ): SqTwoOperandMathOperation<JAVA> =
        this.div(nullable, firstOperand, this.mathOperandParam(secondOperandValue, secondOperandType), resultType)

    override fun <JAVA : Number?> mod(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperand: SqExpression<*, Number>,
        type: SqType<JAVA & Any>?
    ): SqTwoOperandMathOperation<JAVA> =
        SqGenericOperations.twoOperandMathOperation(this, type.mathOpType(), nullable, firstOperand, SqUtil.MATH_OPERATION__MODULO, secondOperand)
    override fun <JAVA : Number?, PARAM : Number?> mod(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>?,
        resultType: SqType<JAVA & Any>?
    ): SqTwoOperandMathOperation<JAVA> =
        this.mod(nullable, firstOperand, this.mathOperandParam(secondOperandValue, secondOperandType), resultType)

    override fun <JAVA : Number?> bitwiseAnd(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperand: SqExpression<*, Number>,
        type: SqType<JAVA & Any>?
    ): SqTwoOperandMathOperation<JAVA> =
        SqGenericOperations.twoOperandMathOperation(this, type.mathOpType(), nullable, firstOperand, SqUtil.MATH_OPERATION__BITWISE_AND, secondOperand)
    override fun <JAVA : Number?, PARAM : Number?> bitwiseAnd(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>?,
        resultType: SqType<JAVA & Any>?
    ): SqTwoOperandMathOperation<JAVA> =
        this.bitwiseAnd(nullable, firstOperand, this.mathOperandParam(secondOperandValue, secondOperandType), resultType)

    override fun <JAVA : Number?> bitwiseOr(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperand: SqExpression<*, Number>,
        type: SqType<JAVA & Any>?
    ): SqTwoOperandMathOperation<JAVA> =
        SqGenericOperations.twoOperandMathOperation(this, type.mathOpType(), nullable, firstOperand, SqUtil.MATH_OPERATION__BITWISE_OR, secondOperand)
    override fun <JAVA : Number?, PARAM : Number?> bitwiseOr(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>?,
        resultType: SqType<JAVA & Any>?
    ): SqTwoOperandMathOperation<JAVA> =
        this.bitwiseOr(nullable, firstOperand, this.mathOperandParam(secondOperandValue, secondOperandType), resultType)

    override fun <JAVA : Number?> bitwiseXor(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperand: SqExpression<*, Number>,
        type: SqType<JAVA & Any>?
    ): SqTwoOperandMathOperation<JAVA> =
        SqGenericOperations.twoOperandMathOperation(this, type.mathOpType(), nullable, firstOperand, SqUtil.MATH_OPERATION__BITWISE_XOR, secondOperand)

    override fun <JAVA : Number?, PARAM : Number?> bitwiseXor(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>?,
        resultType: SqType<JAVA & Any>?
    ): SqTwoOperandMathOperation<JAVA> =
        this.bitwiseXor(nullable, firstOperand, this.mathOperandParam(secondOperandValue, secondOperandType), resultType)
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
