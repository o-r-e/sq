package io.github.ore.sq.impl

import io.github.ore.sq.*


// region Reader pair
open class SqNumberOperationReaderPairImpl(
    override val notNullReader: SqDataTypeReader<Number, Number>,
    override val nullableReader: SqDataTypeReader<Number?, Number>,
): SqNumberOperationReaderPair {
    companion object {
        val INSTANCE: SqNumberOperationReaderPairImpl = SqNumberOperationReaderPairImpl(
            notNullReader = SqDataTypeReaderNotNullImpl { source, index ->
                source.getObject(index, Number::class.java)
            },
            nullableReader = SqDataTypeReaderNullableImpl { source, index ->
                source.getObject(index, Number::class.java)
            },
        )
    }
}
// endregion


// region Arithmetic operation with two values
open class SqTwoValuesArithmeticOperationImpl<JAVA: Number?>(
    override val reader: SqDataTypeReader<JAVA, Number>,
    override val leftItem: SqItem,
    override val operationKeyword: String,
    override val rightItem: SqItem,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqTwoValuesArithmeticOperation<JAVA> {
    open class Factory: SqTwoValuesArithmeticOperationFactory {
        companion object {
            val INSTANCE: Factory = Factory()
            const val OPERATION__ADD = "+"
            const val OPERATION__SUB = "-"
            const val OPERATION__MULT = "*"
            const val OPERATION__DIV = "/"
            const val OPERATION__MOD = "%"
            const val OPERATION__BITWISE_AND = "&"
            const val OPERATION__BITWISE_OR = "|"
            const val OPERATION__BITWISE_XOR = "^"
        }


        override fun <JAVA : Number?> createAdd(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Number>,
            leftItem: SqItem,
            rightItem: SqItem
        ): SqTwoValuesArithmeticOperationImpl<JAVA> {
            return SqTwoValuesArithmeticOperationImpl(reader, leftItem, OPERATION__ADD, rightItem)
        }

        override fun <JAVA : Number?> createSub(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Number>,
            leftItem: SqItem,
            rightItem: SqItem
        ): SqTwoValuesArithmeticOperationImpl<JAVA> {
            return SqTwoValuesArithmeticOperationImpl(reader, leftItem, OPERATION__SUB, rightItem)
        }

        override fun <JAVA : Number?> createMult(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Number>,
            leftItem: SqItem,
            rightItem: SqItem
        ): SqTwoValuesArithmeticOperationImpl<JAVA> {
            return SqTwoValuesArithmeticOperationImpl(reader, leftItem, OPERATION__MULT, rightItem)
        }

        override fun <JAVA : Number?> createDiv(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Number>,
            leftItem: SqItem,
            rightItem: SqItem
        ): SqTwoValuesArithmeticOperationImpl<JAVA> {
            return SqTwoValuesArithmeticOperationImpl(reader, leftItem, OPERATION__DIV, rightItem)
        }

        override fun <JAVA : Number?> createMod(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Number>,
            leftItem: SqItem,
            rightItem: SqItem
        ): SqTwoValuesArithmeticOperationImpl<JAVA> {
            return SqTwoValuesArithmeticOperationImpl(reader, leftItem, OPERATION__MOD, rightItem)
        }

        override fun <JAVA : Number?> createBitwiseAnd(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Number>,
            leftItem: SqItem,
            rightItem: SqItem
        ): SqTwoValuesArithmeticOperationImpl<JAVA> {
            return SqTwoValuesArithmeticOperationImpl(reader, leftItem, OPERATION__BITWISE_AND, rightItem)
        }

        override fun <JAVA : Number?> createBitwiseOr(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Number>,
            leftItem: SqItem,
            rightItem: SqItem
        ): SqTwoValuesArithmeticOperationImpl<JAVA> {
            return SqTwoValuesArithmeticOperationImpl(reader, leftItem, OPERATION__BITWISE_OR, rightItem)
        }

        override fun <JAVA : Number?> createBitwiseXor(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Number>,
            leftItem: SqItem,
            rightItem: SqItem
        ): SqTwoValuesArithmeticOperationImpl<JAVA> {
            return SqTwoValuesArithmeticOperationImpl(reader, leftItem, OPERATION__BITWISE_XOR, rightItem)
        }
    }
}
// endregion


// region Aggregated request functions (COUNT, MIN, MAX, AVG, SUM)
open class SqAggregatedRequestNumberFunctionImpl<JAVA: Number?>(
    override val reader: SqDataTypeReader<JAVA, Number>,
    override val operationKeyword: String,
    override val parameters: List<SqItem>?,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqAggregatedRequestNumberFunction<JAVA> {
    open class Factory: SqAggregatedRequestNumberFunctionFactory {
        companion object {
            val INSTANCE: Factory = Factory()
            const val OPERATION_KEYWORD__COUNT = "count"
            const val OPERATION_KEYWORD__MIN = "min"
            const val OPERATION_KEYWORD__MAX = "max"
            const val OPERATION_KEYWORD__AVG = "avg"
            const val OPERATION_KEYWORD__SUM = "sum"
        }


        override fun <JAVA : Number?> createCount(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Number>,
            parameters: List<SqItem>?
        ): SqAggregatedRequestNumberFunctionImpl<JAVA> {
            return SqAggregatedRequestNumberFunctionImpl(reader, OPERATION_KEYWORD__COUNT, parameters)
        }

        override fun <JAVA : Number?> createMin(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Number>,
            parameters: List<SqItem>?
        ): SqAggregatedRequestNumberFunctionImpl<JAVA> {
            return SqAggregatedRequestNumberFunctionImpl(reader, OPERATION_KEYWORD__MIN, parameters)
        }

        override fun <JAVA : Number?> createMax(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Number>,
            parameters: List<SqItem>?
        ): SqAggregatedRequestNumberFunctionImpl<JAVA> {
            return SqAggregatedRequestNumberFunctionImpl(reader, OPERATION_KEYWORD__MAX, parameters)
        }

        override fun <JAVA : Number?> createAvg(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Number>,
            parameters: List<SqItem>?
        ): SqAggregatedRequestNumberFunctionImpl<JAVA> {
            return SqAggregatedRequestNumberFunctionImpl(reader, OPERATION_KEYWORD__AVG, parameters)
        }

        override fun <JAVA : Number?> createSum(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Number>,
            parameters: List<SqItem>?
        ): SqAggregatedRequestNumberFunctionImpl<JAVA> {
            return SqAggregatedRequestNumberFunctionImpl(reader, OPERATION_KEYWORD__SUM, parameters)
        }
    }
}
// endregion
