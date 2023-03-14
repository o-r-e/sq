package me.ore.sq.generic

import me.ore.sq.*


object SqGenericOperations {
    // region Base classes
    private abstract class OperationBase<JAVA: Any?, DB: Any>: SqExpression<JAVA, DB> {
        private var _nullable = false

        override val nullable: Boolean
            get() = this._nullable

        protected fun <JAVA: Any?, DB: Any, T: OperationBase<JAVA, DB>> getNullable(): T {
            this._nullable = true
            return SqUtil.uncheckedCast(this)
        }
    }

    private abstract class TestBase<JAVA: Boolean?>: OperationBase<JAVA, Boolean>()
    // endregion


    // region Boolean groups
    private class BoolGroup<JAVA: Boolean?>(
        override val context: SqContext,
        override val type: SqType<JAVA & Any>,
        override val values: List<SqItem>,
        val separator: String,
    ): TestBase<JAVA>(), SqMultiValueTest<JAVA> {
        override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
            val firstValueSpaceAllowed = if (asTextPart) {
                target.add("(", spaced = spaceAllowed)
                false
            } else {
                spaceAllowed
            }

            this.values.forEachIndexed { index, value ->
                val valueSpaceAllowed = if (index == 0) {
                    firstValueSpaceAllowed
                } else {
                    target.add(this.separator, spaced = true)
                    true
                }

                value.appendTo(target, asTextPart = true, spaceAllowed = valueSpaceAllowed)
            }

            if (asTextPart) {
                target.add(")", spaced = false)
            }
        }

        override fun nullable(): BoolGroup<JAVA?> = this.getNullable()
    }

    fun and(context: SqContext, type: SqType<Boolean>, values: Iterable<SqItem>): SqMultiValueTest<Boolean> = BoolGroup(context, type, values.toList(), "AND")
    fun or(context: SqContext, type: SqType<Boolean>, values: Iterable<SqItem>): SqMultiValueTest<Boolean> = BoolGroup(context, type, values.toList(), "OR")
    // endregion


    // region Not
    private class Not<JAVA: Boolean?>(
        override val context: SqContext,
        override val type: SqType<JAVA & Any>,
        override val value: SqItem,
    ): TestBase<JAVA>(), SqSingleValueTest<JAVA> {
        override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
            val prefixSpaceAllowed = if (asTextPart) {
                target.add("(", spaced = spaceAllowed)
                false
            } else {
                spaceAllowed
            }

            target.add("NOT", spaced = prefixSpaceAllowed)
            this.value.appendTo(target, asTextPart = true, spaceAllowed = true)

            if (asTextPart) {
                target.add(")", spaced = false)
            }
        }

        override fun nullable(): Not<JAVA?> = this.getNullable()
    }

    fun not(context: SqContext, type: SqType<Boolean>, value: SqItem): SqSingleValueTest<Boolean> = Not(context, type, value)
    // endregion


    // region Suffix tests
    private class SuffixTest<JAVA: Boolean?>(
        override val context: SqContext,
        override val type: SqType<JAVA & Any>,
        override val value: SqItem,
        val suffix: String,
    ): TestBase<JAVA>(), SqSingleValueTest<JAVA> {
        override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
            val valueSpaceAllowed = if (asTextPart) {
                target.add("(", spaced = spaceAllowed)
                false
            } else {
                spaceAllowed
            }

            this.value.appendTo(target, asTextPart = true, spaceAllowed = valueSpaceAllowed)
            target.add(this.suffix, spaced = true)

            if (asTextPart) {
                target.add(")", spaced = false)
            }
        }

        override fun nullable(): SuffixTest<JAVA?> = this.getNullable()
    }

    fun isNull(context: SqContext, type: SqType<Boolean>, value: SqItem): SqSingleValueTest<Boolean> = SuffixTest(context, type, value, "IS NULL")
    fun isNotNull(context: SqContext, type: SqType<Boolean>, value: SqItem): SqSingleValueTest<Boolean> = SuffixTest(context, type, value, "IS NOT NULL")
    // endregion


    // region Two value comparisons
    private class TwoValueComparison<JAVA: Boolean?>(
        override val context: SqContext,
        override val type: SqType<JAVA & Any>,
        override val firstValue: SqItem,
        override val secondValue: SqItem,
        val operand: String,
    ): TestBase<JAVA>(), SqTwoValueTest<JAVA> {
        override fun nullable(): TwoValueComparison<JAVA?> = this.getNullable()

        override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
            val firstValueSpaceAllowed = if (asTextPart) {
                target.add("(", spaced = spaceAllowed)
                false
            } else {
                spaceAllowed
            }

            this.firstValue.appendTo(target, asTextPart = true, spaceAllowed = firstValueSpaceAllowed)
            target.add(this.operand, spaced = true)
            this.secondValue.appendTo(target, asTextPart = true, spaceAllowed = true)

            if (asTextPart) {
                target.add(")", spaced = false)
            }
        }
    }

    fun equal(context: SqContext, type: SqType<Boolean>, firstValue: SqItem, secondValue: SqItem): SqTwoValueTest<Boolean> =
        TwoValueComparison(context, type, firstValue, secondValue, "=")
    fun notEqual(context: SqContext, type: SqType<Boolean>, firstValue: SqItem, secondValue: SqItem): SqTwoValueTest<Boolean> =
        TwoValueComparison(context, type, firstValue, secondValue, "<>")
    fun greaterThan(context: SqContext, type: SqType<Boolean>, firstValue: SqItem, secondValue: SqItem): SqTwoValueTest<Boolean> =
        TwoValueComparison(context, type, firstValue, secondValue, ">")
    fun greaterThanOrEqual(context: SqContext, type: SqType<Boolean>, firstValue: SqItem, secondValue: SqItem): SqTwoValueTest<Boolean> =
        TwoValueComparison(context, type, firstValue, secondValue, ">=")
    fun lessThan(context: SqContext, type: SqType<Boolean>, firstValue: SqItem, secondValue: SqItem): SqTwoValueTest<Boolean> =
        TwoValueComparison(context, type, firstValue, secondValue, "<")
    fun lessThanOrEqual(context: SqContext, type: SqType<Boolean>, firstValue: SqItem, secondValue: SqItem): SqTwoValueTest<Boolean> =
        TwoValueComparison(context, type, firstValue, secondValue, "<=")
    fun like(context: SqContext, type: SqType<Boolean>, firstValue: SqItem, secondValue: SqItem): SqTwoValueTest<Boolean> =
        TwoValueComparison(context, type, firstValue, secondValue, "LIKE")
    fun notLike(context: SqContext, type: SqType<Boolean>, firstValue: SqItem, secondValue: SqItem): SqTwoValueTest<Boolean> =
        TwoValueComparison(context, type, firstValue, secondValue, "NOT LIKE")
    // endregion


    // region Between
    private class Between<JAVA: Boolean?>(
        override val context: SqContext,
        override val type: SqType<JAVA & Any>,
        override val mainValue: SqItem,
        override val firstBoundsValue: SqItem,
        override val secondBoundsValue: SqItem,
        val negative: Boolean,
    ): TestBase<JAVA>(), SqBetweenTest<JAVA> {
        override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
            val firstValueSpaceAllowed = if (asTextPart) {
                target.add("(", spaced = spaceAllowed)
                false
            } else {
                spaceAllowed
            }

            this.mainValue.appendTo(target, asTextPart = true, spaceAllowed = firstValueSpaceAllowed)
            if (this.negative) {
                target.add("NOT BETWEEN", spaced = true)
            } else {
                target.add("BETWEEN", spaced = true)
            }
            this.firstBoundsValue.appendTo(target, asTextPart = true, spaceAllowed = true)
            target.add("AND", spaced = true)
            this.secondBoundsValue.appendTo(target, asTextPart = true, spaceAllowed = true)

            if (asTextPart) {
                target.add(")", spaced = false)
            }
        }

        override fun nullable(): Between<JAVA?> = this.getNullable()
    }

    fun between(context: SqContext, type: SqType<Boolean>, firstValue: SqItem, secondValue: SqItem, thirdValue: SqItem): SqBetweenTest<Boolean> =
        Between(context, type, firstValue, secondValue, thirdValue, negative = false)
    fun notBetween(context: SqContext, type: SqType<Boolean>, firstValue: SqItem, secondValue: SqItem, thirdValue: SqItem): SqBetweenTest<Boolean> =
        Between(context, type, firstValue, secondValue, thirdValue, negative = true)
    // endregion


    // region "IN" list tests
    private class ListTest<JAVA: Boolean?>(
        override val context: SqContext,
        override val type: SqType<JAVA & Any>,
        override val mainValue: SqItem,
        override val listValues: List<SqItem>,
        val negative: Boolean,
    ): TestBase<JAVA>(), SqInListTest<JAVA> {
        override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
            val mainValueSpaceAllowed = if (asTextPart) {
                target.add("(", spaced = spaceAllowed)
                false
            } else {
                spaceAllowed
            }

            this.mainValue.appendTo(target, asTextPart = true, spaceAllowed = mainValueSpaceAllowed)
            if (this.negative) {
                target.add("NOT IN", spaced = true)
            } else {
                target.add("IN", spaced = true)
            }

            target.add("(", spaced = true)
            this.listValues.forEachIndexed { index, listValue ->
                if (index > 0) {
                    target.add(", ", spaced = false)
                }
                listValue.appendTo(target, asTextPart = true, spaceAllowed = false)
            }
            target.add(")", spaced = false)

            if (asTextPart) {
                target.add(")", spaced = false)
            }
        }

        override fun nullable(): ListTest<JAVA?> = this.getNullable()
    }

    fun inList(context: SqContext, type: SqType<Boolean>, mainValue: SqItem, listValues: Array<out SqItem>): SqInListTest<Boolean> =
        ListTest(context, type, mainValue, listValues.toList(), negative = false)
    fun notInList(context: SqContext, type: SqType<Boolean>, mainValue: SqItem, listValues: Array<out SqItem>): SqInListTest<Boolean> =
        ListTest(context, type, mainValue, listValues.toList(), negative = true)
    // endregion


    // region Mathematical operations
    private class TwoOperandMathOperation<JAVA: Number?>(
        override val context: SqContext,
        override val type: SqType<JAVA & Any>,
        override val nullable: Boolean,
        override val firstOperand: SqExpression<*, Number>,
        override val operation: String,
        override val secondOperand: SqExpression<*, Number>,
    ): OperationBase<JAVA, Number>(), SqTwoOperandMathOperation<JAVA> {
        @Suppress("DuplicatedCode")
        override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
            val firstOperandSpaceAllowed = if (asTextPart) {
                target.add("(", spaced = spaceAllowed)
                false
            } else {
                spaceAllowed
            }

            this.firstOperand.appendTo(target, asTextPart = true, spaceAllowed = firstOperandSpaceAllowed)
            target.add(this.operation, spaced = true)
            this.secondOperand.appendTo(target, asTextPart = true, spaceAllowed = true)

            if (asTextPart) {
                target.add(")", spaced = false)
            }
        }

        override fun nullable(): TwoOperandMathOperation<JAVA?> = this.getNullable()
    }

    fun <JAVA: Number?> twoOperandMathOperation(
        context: SqContext,
        type: SqType<JAVA & Any>,
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        operation: String,
        secondOperand: SqExpression<*, Number>,
    ): SqTwoOperandMathOperation<JAVA> {
        return TwoOperandMathOperation(context, type, nullable, firstOperand, operation, secondOperand)
    }
    // endregion
}
