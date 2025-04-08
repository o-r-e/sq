@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqAggregatedRequestNumberFunctionImpl
import io.github.ore.sq.impl.SqNumberOperationReaderPairImpl
import io.github.ore.sq.impl.SqTwoValuesArithmeticOperationImpl
import io.github.ore.sq.util.SqItemPartConfig
import io.github.ore.sq.util.bracketsAreOptional


// region Reader pair
interface SqNumberOperationReaderPair {
    val notNullReader: SqDataTypeReader<Number, Number>
    val nullableReader: SqDataTypeReader<Number?, Number>
}

fun <T: SqSettingsBuilder> T.numberOperationReaderPair(value: SqNumberOperationReaderPair?): T =
    this.setValue(SqNumberOperationReaderPair::class.java, value)
val SqSettings.numberOperationReaderPair: SqNumberOperationReaderPair
    get() = this.getValue(SqNumberOperationReaderPair::class.java) ?: SqNumberOperationReaderPairImpl.INSTANCE
// endregion


// region Arithmetic operation with two values
interface SqTwoValuesArithmeticOperation<JAVA: Number?>: SqExpression<JAVA, Number> {
    val leftItem: SqItem
    val operationKeyword: String
    val rightItem: SqItem

    override val isMultiline: Boolean
        get() = (this.leftItem.isMultiline || this.rightItem.isMultiline)

    @Suppress("DuplicatedCode")
    fun addToBuilder(context: SqContext, target: SqJdbcRequestDataBuilder) {
        val multiline = this.isMultiline

        this.leftItem.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)

        if (multiline) {
            target.spaceOrNewLine()
        } else {
            target.space()
        }

        target.keyword(this.operationKeyword)

        if (multiline) {
            target.spaceOrNewLine()
        } else {
            target.space()
        }

        this.rightItem.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)
    }

    @Suppress("DuplicatedCode")
    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        if (partConfig.bracketsAreOptional()) {
            this.addToBuilder(context, target)
        } else if (this.isMultiline) {
            target.bracketsWithIndent {
                this.addToBuilder(context, target)
            }
        } else {
            target.brackets {
                this.addToBuilder(context, target)
            }
        }
    }
}

interface SqTwoValuesArithmeticOperationFactory {
    fun notNullReader(context: SqContext): SqDataTypeReader<Number, Number> =
        context.settings.numberOperationReaderPair.notNullReader
    fun nullableReader(context: SqContext): SqDataTypeReader<Number?, Number> =
        context.settings.numberOperationReaderPair.nullableReader

    fun <JAVA: Number?> createAdd(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Number>,
        leftItem: SqItem,
        rightItem: SqItem,
    ): SqTwoValuesArithmeticOperation<JAVA>

    fun <JAVA: Number?> createSub(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Number>,
        leftItem: SqItem,
        rightItem: SqItem,
    ): SqTwoValuesArithmeticOperation<JAVA>

    fun <JAVA: Number?> createMult(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Number>,
        leftItem: SqItem,
        rightItem: SqItem,
    ): SqTwoValuesArithmeticOperation<JAVA>

    fun <JAVA: Number?> createDiv(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Number>,
        leftItem: SqItem,
        rightItem: SqItem,
    ): SqTwoValuesArithmeticOperation<JAVA>

    fun <JAVA: Number?> createMod(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Number>,
        leftItem: SqItem,
        rightItem: SqItem,
    ): SqTwoValuesArithmeticOperation<JAVA>

    fun <JAVA: Number?> createBitwiseAnd(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Number>,
        leftItem: SqItem,
        rightItem: SqItem,
    ): SqTwoValuesArithmeticOperation<JAVA>

    fun <JAVA: Number?> createBitwiseOr(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Number>,
        leftItem: SqItem,
        rightItem: SqItem,
    ): SqTwoValuesArithmeticOperation<JAVA>

    fun <JAVA: Number?> createBitwiseXor(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Number>,
        leftItem: SqItem,
        rightItem: SqItem,
    ): SqTwoValuesArithmeticOperation<JAVA>
}

fun <T: SqSettingsBuilder> T.twoValuesArithmeticOperationFactory(value: SqTwoValuesArithmeticOperationFactory?): T =
    this.setValue(SqTwoValuesArithmeticOperationFactory::class.java, value)
val SqSettings.twoValuesArithmeticOperationFactory: SqTwoValuesArithmeticOperationFactory
    get() = this.getValue(SqTwoValuesArithmeticOperationFactory::class.java) ?: SqTwoValuesArithmeticOperationImpl.Factory.INSTANCE


@JvmName("add__notNull")
fun SqExpression<out Any, Number>.add(other: SqExpression<out Any, Number>, context: SqContext): SqTwoValuesArithmeticOperation<Number> {
    val factory = context.settings.twoValuesArithmeticOperationFactory
    return factory.createAdd(context, factory.notNullReader(context), this, other)
}

@JvmName("add__notNull")
infix fun SqExpression<out Any, Number>.add(other: SqExpression<out Any, Number>): SqTwoValuesArithmeticOperation<Number> =
    this.add(other, SqContext.last)

@JvmName("add__nullable")
fun SqExpression<out Any?, Number>.add(other: SqExpression<out Any?, Number>, context: SqContext): SqTwoValuesArithmeticOperation<Number?> {
    val factory = context.settings.twoValuesArithmeticOperationFactory
    return factory.createAdd(context, factory.nullableReader(context), this, other)
}

@JvmName("add__nullable")
infix fun SqExpression<out Any?, Number>.add(other: SqExpression<out Any?, Number>): SqTwoValuesArithmeticOperation<Number?> =
    this.add(other, SqContext.last)

@JvmName("sub__notNull")
fun SqExpression<out Any, Number>.sub(other: SqExpression<out Any, Number>, context: SqContext): SqTwoValuesArithmeticOperation<Number> {
    val factory = context.settings.twoValuesArithmeticOperationFactory
    return factory.createSub(context, factory.notNullReader(context), this, other)
}

@JvmName("sub__notNull")
infix fun SqExpression<out Any, Number>.sub(other: SqExpression<out Any, Number>): SqTwoValuesArithmeticOperation<Number> =
    this.sub(other, SqContext.last)

@JvmName("sub__nullable")
fun SqExpression<out Any?, Number>.sub(other: SqExpression<out Any?, Number>, context: SqContext): SqTwoValuesArithmeticOperation<Number?> {
    val factory = context.settings.twoValuesArithmeticOperationFactory
    return factory.createSub(context, factory.nullableReader(context), this, other)
}

@JvmName("sub__nullable")
infix fun SqExpression<out Any?, Number>.sub(other: SqExpression<out Any?, Number>): SqTwoValuesArithmeticOperation<Number?> =
    this.sub(other, SqContext.last)

@JvmName("mult__notNull")
fun SqExpression<out Any, Number>.mult(other: SqExpression<out Any, Number>, context: SqContext): SqTwoValuesArithmeticOperation<Number> {
    val factory = context.settings.twoValuesArithmeticOperationFactory
    return factory.createMult(context, factory.notNullReader(context), this, other)
}

@JvmName("mult__notNull")
infix fun SqExpression<out Any, Number>.mult(other: SqExpression<out Any, Number>): SqTwoValuesArithmeticOperation<Number> =
    this.mult(other, SqContext.last)

@JvmName("mult__nullable")
fun SqExpression<out Any?, Number>.mult(other: SqExpression<out Any?, Number>, context: SqContext): SqTwoValuesArithmeticOperation<Number?> {
    val factory = context.settings.twoValuesArithmeticOperationFactory
    return factory.createMult(context, factory.nullableReader(context), this, other)
}

@JvmName("mult__nullable")
infix fun SqExpression<out Any?, Number>.mult(other: SqExpression<out Any?, Number>): SqTwoValuesArithmeticOperation<Number?> =
    this.mult(other, SqContext.last)

@JvmName("div__notNull")
fun SqExpression<out Any, Number>.div(other: SqExpression<out Any, Number>, context: SqContext): SqTwoValuesArithmeticOperation<Number> {
    val factory = context.settings.twoValuesArithmeticOperationFactory
    return factory.createDiv(context, factory.notNullReader(context), this, other)
}

@JvmName("div__notNull")
infix fun SqExpression<out Any, Number>.div(other: SqExpression<out Any, Number>): SqTwoValuesArithmeticOperation<Number> =
    this.div(other, SqContext.last)

@JvmName("div__nullable")
fun SqExpression<out Any?, Number>.div(other: SqExpression<out Any?, Number>, context: SqContext): SqTwoValuesArithmeticOperation<Number?> {
    val factory = context.settings.twoValuesArithmeticOperationFactory
    return factory.createDiv(context, factory.nullableReader(context), this, other)
}

@JvmName("div__nullable")
infix fun SqExpression<out Any?, Number>.div(other: SqExpression<out Any?, Number>): SqTwoValuesArithmeticOperation<Number?> =
    this.div(other, SqContext.last)

@JvmName("mod__notNull")
fun SqExpression<out Any, Number>.mod(other: SqExpression<out Any, Number>, context: SqContext): SqTwoValuesArithmeticOperation<Number> {
    val factory = context.settings.twoValuesArithmeticOperationFactory
    return factory.createMod(context, factory.notNullReader(context), this, other)
}

@JvmName("mod__notNull")
infix fun SqExpression<out Any, Number>.mod(other: SqExpression<out Any, Number>): SqTwoValuesArithmeticOperation<Number> =
    this.mod(other, SqContext.last)

@JvmName("mod__nullable")
fun SqExpression<out Any?, Number>.mod(other: SqExpression<out Any?, Number>, context: SqContext): SqTwoValuesArithmeticOperation<Number?> {
    val factory = context.settings.twoValuesArithmeticOperationFactory
    return factory.createMod(context, factory.nullableReader(context), this, other)
}

@JvmName("mod__nullable")
infix fun SqExpression<out Any?, Number>.mod(other: SqExpression<out Any?, Number>): SqTwoValuesArithmeticOperation<Number?> =
    this.mod(other, SqContext.last)

@JvmName("bitwiseAnd__notNull")
fun SqExpression<out Any, Number>.bitwiseAnd(other: SqExpression<out Any, Number>, context: SqContext): SqTwoValuesArithmeticOperation<Number> {
    val factory = context.settings.twoValuesArithmeticOperationFactory
    return factory.createBitwiseAnd(context, factory.notNullReader(context), this, other)
}

@JvmName("bitwiseAnd__notNull")
infix fun SqExpression<out Any, Number>.bitwiseAnd(other: SqExpression<out Any, Number>): SqTwoValuesArithmeticOperation<Number> =
    this.bitwiseAnd(other, SqContext.last)

@JvmName("bitwiseAnd__nullable")
fun SqExpression<out Any?, Number>.bitwiseAnd(other: SqExpression<out Any?, Number>, context: SqContext): SqTwoValuesArithmeticOperation<Number?> {
    val factory = context.settings.twoValuesArithmeticOperationFactory
    return factory.createBitwiseAnd(context, factory.nullableReader(context), this, other)
}

@JvmName("bitwiseAnd__nullable")
infix fun SqExpression<out Any?, Number>.bitwiseAnd(other: SqExpression<out Any?, Number>): SqTwoValuesArithmeticOperation<Number?> =
    this.bitwiseAnd(other, SqContext.last)

@JvmName("bitwiseOr__notNull")
fun SqExpression<out Any, Number>.bitwiseOr(other: SqExpression<out Any, Number>, context: SqContext): SqTwoValuesArithmeticOperation<Number> {
    val factory = context.settings.twoValuesArithmeticOperationFactory
    return factory.createBitwiseOr(context, factory.notNullReader(context), this, other)
}

@JvmName("bitwiseOr__notNull")
infix fun SqExpression<out Any, Number>.bitwiseOr(other: SqExpression<out Any, Number>): SqTwoValuesArithmeticOperation<Number> =
    this.bitwiseOr(other, SqContext.last)

@JvmName("bitwiseOr__nullable")
fun SqExpression<out Any?, Number>.bitwiseOr(other: SqExpression<out Any?, Number>, context: SqContext): SqTwoValuesArithmeticOperation<Number?> {
    val factory = context.settings.twoValuesArithmeticOperationFactory
    return factory.createBitwiseOr(context, factory.nullableReader(context), this, other)
}

@JvmName("bitwiseOr__nullable")
infix fun SqExpression<out Any?, Number>.bitwiseOr(other: SqExpression<out Any?, Number>): SqTwoValuesArithmeticOperation<Number?> =
    this.bitwiseOr(other, SqContext.last)

@JvmName("bitwiseXor__notNull")
fun SqExpression<out Any, Number>.bitwiseXor(other: SqExpression<out Any, Number>, context: SqContext): SqTwoValuesArithmeticOperation<Number> {
    val factory = context.settings.twoValuesArithmeticOperationFactory
    return factory.createBitwiseXor(context, factory.notNullReader(context), this, other)
}

@JvmName("bitwiseXor__notNull")
infix fun SqExpression<out Any, Number>.bitwiseXor(other: SqExpression<out Any, Number>): SqTwoValuesArithmeticOperation<Number> =
    this.bitwiseXor(other, SqContext.last)

@JvmName("bitwiseXor__nullable")
fun SqExpression<out Any?, Number>.bitwiseXor(other: SqExpression<out Any?, Number>, context: SqContext): SqTwoValuesArithmeticOperation<Number?> {
    val factory = context.settings.twoValuesArithmeticOperationFactory
    return factory.createBitwiseXor(context, factory.nullableReader(context), this, other)
}

@JvmName("bitwiseXor__nullable")
infix fun SqExpression<out Any?, Number>.bitwiseXor(other: SqExpression<out Any?, Number>): SqTwoValuesArithmeticOperation<Number?> =
    this.bitwiseXor(other, SqContext.last)
// endregion


// region Aggregated request functions (COUNT, MIN, MAX, AVG, SUM)
interface SqAggregatedRequestNumberFunction<JAVA: Number?>: SqExpression<JAVA, Number> {
    val operationKeyword: String
    val parameters: List<SqItem>?

    override val isMultiline: Boolean
        get() {
            return this.parameters.let { parameters ->
                parameters?.any { it.isMultiline } == true
            }
        }

    fun addParametersToBuilder(context: SqContext, target: SqJdbcRequestDataBuilder) {
        val multiline = this.isMultiline
        this.parameters?.forEachIndexed { index, parameter ->
            if (index > 0) {
                target.comma()
                if (multiline) {
                    target.spaceOrNewLine()
                } else {
                    target.space()
                }
            }
            parameter.addToBuilder(context, target, SqItemPartConfig.INSTANCE__OPTIONAL_BRACKETS)
        }
    }

    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        target.keyword(this.operationKeyword)
        if (this.isMultiline) {
            target.bracketsWithIndent {
                this.addParametersToBuilder(context, target)
            }
        } else {
            target.brackets {
                this.addParametersToBuilder(context, target)
            }
        }
    }
}

interface SqAggregatedRequestNumberFunctionFactory {
    fun notNullReader(context: SqContext): SqDataTypeReader<Number, Number> =
        context.settings.numberOperationReaderPair.notNullReader
    fun nullableReader(context: SqContext): SqDataTypeReader<Number?, Number> =
        context.settings.numberOperationReaderPair.nullableReader

    fun <JAVA: Number?> createCount(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Number>,
        parameters: List<SqItem>?,
    ): SqAggregatedRequestNumberFunction<JAVA>

    fun <JAVA: Number?> createMin(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Number>,
        parameters: List<SqItem>?,
    ): SqAggregatedRequestNumberFunction<JAVA>

    fun <JAVA: Number?> createMax(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Number>,
        parameters: List<SqItem>?,
    ): SqAggregatedRequestNumberFunction<JAVA>

    fun <JAVA: Number?> createAvg(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Number>,
        parameters: List<SqItem>?,
    ): SqAggregatedRequestNumberFunction<JAVA>

    fun <JAVA: Number?> createSum(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Number>,
        parameters: List<SqItem>?,
    ): SqAggregatedRequestNumberFunction<JAVA>
}

fun <T: SqSettingsBuilder> T.aggregatedRequestNumberFunctionFactory(value: SqAggregatedRequestNumberFunctionFactory?): T =
    this.setValue(SqAggregatedRequestNumberFunctionFactory::class.java, value)
val SqSettings.aggregatedRequestNumberFunctionFactory: SqAggregatedRequestNumberFunctionFactory
    get() = this.getValue(SqAggregatedRequestNumberFunctionFactory::class.java) ?: SqAggregatedRequestNumberFunctionImpl.Factory.INSTANCE


fun <JAVA: Number?> SqContext.count(parameter: SqExpression<*, *>, reader: SqDataTypeReader<JAVA, Number>): SqAggregatedRequestNumberFunction<JAVA> =
    this.settings.aggregatedRequestNumberFunctionFactory.createCount(this, reader, listOf(parameter))

fun SqContext.count(parameter: SqExpression<*, *>): SqAggregatedRequestNumberFunction<Number> =
    this.count(parameter, this.settings.aggregatedRequestNumberFunctionFactory.notNullReader(this))

fun <JAVA: Number?> SqContext.min(parameter: SqExpression<JAVA, Number>): SqAggregatedRequestNumberFunction<JAVA> =
    this.settings.aggregatedRequestNumberFunctionFactory.createMin(this, parameter.reader, listOf(parameter))

fun <JAVA: Number?> SqContext.max(parameter: SqExpression<JAVA, Number>): SqAggregatedRequestNumberFunction<JAVA> =
    this.settings.aggregatedRequestNumberFunctionFactory.createMax(this, parameter.reader, listOf(parameter))

fun <JAVA: Number?> SqContext.avg(parameter: SqExpression<*, *>, reader: SqDataTypeReader<JAVA, Number>): SqAggregatedRequestNumberFunction<JAVA> =
    this.settings.aggregatedRequestNumberFunctionFactory.createAvg(this, reader, listOf(parameter))

@JvmName("avg__notNull")
fun SqContext.avg(parameter: SqExpression<out Any, *>): SqAggregatedRequestNumberFunction<Number> =
    this.avg(parameter, this.settings.aggregatedRequestNumberFunctionFactory.notNullReader(this))
@JvmName("avg__nullable")
fun SqContext.avg(parameter: SqExpression<out Any?, *>): SqAggregatedRequestNumberFunction<Number?> =
    this.avg(parameter, this.settings.aggregatedRequestNumberFunctionFactory.nullableReader(this))

fun <JAVA: Number?> SqContext.sum(parameter: SqExpression<*, *>, reader: SqDataTypeReader<JAVA, Number>): SqAggregatedRequestNumberFunction<JAVA> =
    this.settings.aggregatedRequestNumberFunctionFactory.createSum(this, reader, listOf(parameter))

@JvmName("sum__notNull")
fun SqContext.sum(parameter: SqExpression<out Any, *>): SqAggregatedRequestNumberFunction<Number> =
    this.sum(parameter, this.settings.aggregatedRequestNumberFunctionFactory.notNullReader(this))
@JvmName("sum__nullable")
fun SqContext.sum(parameter: SqExpression<out Any?, *>): SqAggregatedRequestNumberFunction<Number?> =
    this.sum(parameter, this.settings.aggregatedRequestNumberFunctionFactory.nullableReader(this))
// endregion
