package me.ore.sq

import me.ore.sq.generic.*
import me.ore.sq.util.SqUtil
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


// region Utils
fun SqContext.writer(): SqWriter = this[SqWriterConstructor::class.java, SqGenericWriter.CONSTRUCTOR].createWriter(this)

fun <T: SqWriter> T.ls(): T = this.apply { this.addLineSeparator() }
fun <T: SqWriter> T.add(text: String, spaced: Boolean = false): T = this.apply { this.addText(text, spaced) }
fun <T: SqWriter> T.addSpaced(text: String): T = this.apply { this.addText(text, spaced = true) }
fun <T: SqWriter> T.clear(): T = this.apply { this.clearData() }

fun <T: SqColumnValueMapping<*>> T.clear(): T = this.apply { this.clearData() }
// endregion


// region Context data / context start
inline fun <T, C: SqContext> sq(context: C, block: C.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return block.invoke(context)
}

inline fun <T> sq(data: SqContextData, block: SqContext.Context.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return SqContext.Context(data).start().use(block)
}

inline fun <T> sq(data: SqContextData, connection: Connection, block: SqContext.ConnContext.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return SqContext.ConnContext(data, connection).start().use(block)
}

inline fun <T> sq(template: SqContextTemplate, block: SqContext.Context.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return template.create().start().use(block)
}

inline fun <T> sq(template: SqContextTemplate, connection: Connection, block: SqContext.ConnContext.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return template.create(connection).start().use(block)
}

inline fun <T> sq(block: SqContext.Context.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return SqContextTemplate.defaultTemplate.create().start().use(block)
}

inline fun <T> sq(connection: Connection, block: SqContext.ConnContext.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return SqContextTemplate.defaultTemplate.create(connection).start().use(block)
}
// endregion


// region Base items
fun <JAVA: Any?, DB: Any, ORIG: SqExpression<JAVA, DB>> SqContext.expressionAlias(original: ORIG, alias: String): SqExpressionAlias<JAVA, DB, ORIG> {
    return this[SqExpressionAliasConstructor::class.java, SqGenericExpressionAlias.CONSTRUCTOR]
        .createExpressionAlias(this, original, alias)
}
infix fun <JAVA: Any?, DB: Any, ORIG: SqExpression<JAVA, DB>> ORIG.alias(alias: String): SqExpressionAlias<JAVA, DB, ORIG> = this.context.expressionAlias(this, alias)


fun <JAVA: Any, DB: Any> SqContext.nullItem(type: SqType<JAVA?, DB>): SqNull<JAVA, DB> {
    return this[SqNullConstructor::class.java, SqGenericNull.CONSTRUCTOR]
        .createNull(this, type)
}
fun <JAVA: Any, DB: Any> SqContext.nullItem(): SqNull<JAVA, DB> {
    return this[SqUntypedNullConstructor::class.java, SqGenericUntypedNull.CONSTRUCTOR]
        .createUntypedNull(this)
}


fun <JAVA: Any?, DB: Any> SqContext.param(type: SqType<JAVA, DB>, value: JAVA): SqParameter<JAVA, DB> {
    return this[SqParameterConstructor::class.java, SqGenericParameter.CONSTRUCTOR]
        .createParameter(this, type, value)
}
fun <T: PreparedStatement> Iterable<SqParameter<*, *>>?.setTo(target: T): T {
    this?.forEachIndexed { index, parameter ->
        parameter.write(target, index + 1)
    }
    return target
}


fun <JAVA: Any?> SqColSet.read(source: ResultSet, column: SqColumn<JAVA, *>): JAVA {
    val index = this.requireColumnIndex(column) + 1
    return column.read(source, index)
}


fun <ORIG: SqMultiColSet> SqContext.multiColSetAlias(original: ORIG, alias: String): SqMultiColSetAlias<ORIG> {
    return this[SqMultiColSetAliasConstructor::class.java, SqGenericMultiColSetAlias.CONSTRUCTOR]
        .createMultiColSetAlias(this, original, alias)
}
infix fun <ORIG: SqMultiColSet> ORIG.alias(alias: String): SqMultiColSetAlias<ORIG> = this.context.multiColSetAlias(this, alias)

fun <JAVA: Any?, DB: Any, ORIG: SqSingleColSet<JAVA, DB>> SqContext.singleColSetAlias(original: ORIG, alias: String): SqSingleColSetAlias<JAVA, DB, ORIG> {
    return this[SqSingleColSetAliasConstructor::class.java, SqGenericSingleColSetAlias.CONSTRUCTOR]
        .createSingleColSetAlias(this, original, alias)
}
infix fun <JAVA: Any?, DB: Any, ORIG: SqSingleColSelect<JAVA, DB>> ORIG.alias(alias: String): SqSingleColSetAlias<JAVA, DB, SqSingleColSelect<JAVA, DB>> {
    return this.context.singleColSetAlias(this, alias)
}

fun <JAVA: Any?, DB: Any> SqContext.colSetAliasColumn(alias: SqColSetAlias<*>, column: SqColumn<JAVA, DB>): SqColSetAliasColumn<JAVA, DB> {
    return this[SqColSetAliasColumnConstructor::class.java, SqGenericColSetAliasColumn.CONSTRUCTOR]
        .createColSetAliasColumn(this, alias, column)
}
operator fun <JAVA: Any?, DB: Any> SqMultiColSetAlias<*>.get(originalColumn: SqColumn<JAVA, DB>): SqColSetAliasColumn<JAVA, DB> = this.getColumn(originalColumn)
// endregion


// region Boolean groups, "single value" tests
fun <JAVA: Boolean?> SqContext.booleanGroup(type: SqType<JAVA, Boolean>, groupType: SqBooleanGroupType, items: List<SqExpression<*, Boolean>>): SqBooleanGroup<JAVA> {
    return this[SqBooleanGroupConstructor::class.java, SqGenericBooleanGroup.CONSTRUCTOR]
        .createBooleanGroup(this, type, groupType, items)
}

fun <JAVA: Boolean?> SqContext.and(type: SqType<JAVA, Boolean>, items: List<SqExpression<*, Boolean>>): SqBooleanGroup<JAVA> =
    this.booleanGroup(type, SqBooleanGroupType.AND, items)
@JvmName("and__not_null")
fun SqContext.and(items: List<SqExpression<out Any, Boolean>>): SqBooleanGroup<Boolean> =
    this.and(this.booleanGroupType(), items)
@JvmName("and__nullable")
fun SqContext.and(items: List<SqExpression<out Any?, Boolean>>): SqBooleanGroup<Boolean?> =
    this.and(this.booleanGroupType().nullable(), items)
@JvmName("and__not_null")
fun SqContext.and(firstItem: SqExpression<out Any, Boolean>, vararg moreItems: SqExpression<out Any, Boolean>): SqBooleanGroup<Boolean> =
    this.and(listOf(firstItem, *moreItems))
@JvmName("and__nullable")
fun SqContext.and(firstItem: SqExpression<out Any?, Boolean>, vararg moreItems: SqExpression<out Any?, Boolean>): SqBooleanGroup<Boolean?> =
    this.and(listOf(firstItem, *moreItems))

fun <JAVA: Boolean?> SqContext.or(type: SqType<JAVA, Boolean>, items: List<SqExpression<*, Boolean>>): SqBooleanGroup<JAVA> =
    this.booleanGroup(type, SqBooleanGroupType.OR, items)
@JvmName("or__not_null")
fun SqContext.or(items: List<SqExpression<out Any, Boolean>>): SqBooleanGroup<Boolean> =
    this.or(this.booleanGroupType(), items)
@JvmName("or__nullable")
fun SqContext.or(items: List<SqExpression<out Any?, Boolean>>): SqBooleanGroup<Boolean?> =
    this.or(this.booleanGroupType().nullable(), items)
@JvmName("or__not_null")
fun SqContext.or(firstItem: SqExpression<out Any, Boolean>, vararg moreItems: SqExpression<out Any, Boolean>): SqBooleanGroup<Boolean> =
    this.or(listOf(firstItem, *moreItems))
@JvmName("or__nullable")
fun SqContext.or(firstItem: SqExpression<out Any?, Boolean>, vararg moreItems: SqExpression<out Any?, Boolean>): SqBooleanGroup<Boolean?> =
    this.or(listOf(firstItem, *moreItems))


fun <JAVA: Boolean?> SqContext.not(type: SqType<JAVA, Boolean>, expression: SqExpression<*, Boolean>): SqNot<JAVA> {
    return this[SqNotConstructor::class.java, SqGenericNot.CONSTRUCTOR]
        .createNot(this, type, expression)
}
fun <JAVA: Boolean?> SqExpression<*, Boolean>.not(type: SqType<JAVA, Boolean>): SqNot<JAVA> =
    this.context.not(type, this)
@JvmName("not__not_null")
fun SqExpression<out Any, Boolean>.not(): SqNot<Boolean> =
    this.not(this.context.notType())
@JvmName("not__nullable")
fun SqExpression<out Any?, Boolean>.not(): SqNot<Boolean?> =
    this.not(this.context.notType().nullable())


fun SqContext.nullTest(type: SqType<Boolean, Boolean>, negation: Boolean, expression: SqExpression<*, *>): SqNullTest {
    return this[SqNullTestConstructor::class.java, SqGenericNullTest.CONSTRUCTOR]
        .createNullTest(this, type, negation, expression)
}
fun SqExpression<*, *>.nullTest(type: SqType<Boolean, Boolean>, negation: Boolean): SqNullTest =
    this.context.nullTest(type, negation, this)
fun SqExpression<*, *>.isNull(type: SqType<Boolean, Boolean> = this.context.nullTestType()): SqNullTest = this.nullTest(type, negation = false)
fun SqExpression<*, *>.isNotNull(type: SqType<Boolean, Boolean> = this.context.nullTestType()): SqNullTest = this.nullTest(type, negation = true)
// endregion


// region Comparisons
fun <JAVA: Boolean?, DB: Any> SqContext.comparison(
    type: SqType<JAVA, Boolean>,
    firstOperand: SqExpression<*, DB>,
    secondOperand: SqExpression<*, DB>,
    operation: String,
): SqComparison<JAVA> {
    return this[SqComparisonConstructor::class.java, SqGenericComparison.CONSTRUCTOR]
        .createComparison(this, type, firstOperand, secondOperand, operation)
}

fun <JAVA: Boolean?, DB: Any> SqContext.eq(type: SqType<JAVA, Boolean>, firstOperand: SqExpression<*, DB>, secondOperand: SqExpression<*, DB>): SqComparison<JAVA> =
    this.comparison(type, firstOperand, secondOperand, SqUtil.COMPARISON__EQUAL)
fun <JAVA: Boolean?, DB: Any> SqExpression<*, DB>.eq(type: SqType<JAVA, Boolean>, other: SqExpression<*, DB>): SqComparison<JAVA> =
    this.context.eq(type, this, other)
@JvmName("eq__not_null")
infix fun <DB: Any> SqExpression<out Any, DB>.eq(other: SqExpression<out Any, DB>): SqComparison<Boolean> =
    this.eq(this.context.comparisonType(), other)
@JvmName("eq__nullable")
infix fun <DB: Any> SqExpression<out Any?, DB>.eq(other: SqExpression<out Any?, DB>): SqComparison<Boolean?> =
    this.eq(this.context.comparisonType().nullable(), other)
@JvmName("eq__not_null")
infix fun <JAVA: Any, DB: Any> SqExpression<JAVA, DB>.eq(value: JAVA): SqComparison<Boolean> =
    this.eq(this.context.param(this.type, value))
@JvmName("eq__nullable")
infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.eq(value: JAVA?): SqComparison<Boolean?> =
    this.eq(this.context.param(this.type.nullable(), value))
@JvmName("eq__not_null")
infix fun <JAVA: Any, DB: Any> JAVA.eq(expression: SqExpression<JAVA, DB>): SqComparison<Boolean> =
    expression.context.param<JAVA, DB>(expression.type, this).eq(expression)
@JvmName("eq__nullable")
infix fun <JAVA: Any?, DB: Any> JAVA?.eq(expression: SqExpression<JAVA, DB>): SqComparison<Boolean?> =
    expression.context.param<JAVA?, DB>(expression.type.nullable(), this).eq(expression)

fun <JAVA: Boolean?, DB: Any> SqContext.neq(type: SqType<JAVA, Boolean>, firstOperand: SqExpression<*, DB>, secondOperand: SqExpression<*, DB>): SqComparison<JAVA> =
    this.comparison(type, firstOperand, secondOperand, SqUtil.COMPARISON__NOT_EQUAL)
fun <JAVA: Boolean?, DB: Any> SqExpression<*, DB>.neq(type: SqType<JAVA, Boolean>, other: SqExpression<*, DB>): SqComparison<JAVA> =
    this.context.neq(type, this, other)
@JvmName("neq__not_null")
infix fun <DB: Any> SqExpression<out Any, DB>.neq(other: SqExpression<out Any, DB>): SqComparison<Boolean> =
    this.neq(this.context.comparisonType(), other)
@JvmName("neq__nullable")
infix fun <DB: Any> SqExpression<out Any?, DB>.neq(other: SqExpression<out Any?, DB>): SqComparison<Boolean?> =
    this.neq(this.context.comparisonType().nullable(), other)
@JvmName("neq__not_null")
infix fun <JAVA: Any, DB: Any> SqExpression<JAVA, DB>.neq(value: JAVA): SqComparison<Boolean> =
    this.neq(this.context.param(this.type, value))
@JvmName("neq__nullable")
infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.neq(value: JAVA?): SqComparison<Boolean?> =
    this.neq(this.context.param(this.type.nullable(), value))
@JvmName("neq__not_null")
infix fun <JAVA: Any, DB: Any> JAVA.neq(expression: SqExpression<JAVA, DB>): SqComparison<Boolean> =
    expression.context.param<JAVA, DB>(expression.type, this).neq(expression)
@JvmName("neq__nullable")
infix fun <JAVA: Any?, DB: Any> JAVA?.neq(expression: SqExpression<JAVA, DB>): SqComparison<Boolean?> =
    expression.context.param<JAVA?, DB>(expression.type.nullable(), this).neq(expression)

fun <JAVA: Boolean?, DB: Any> SqContext.gt(type: SqType<JAVA, Boolean>, firstOperand: SqExpression<*, DB>, secondOperand: SqExpression<*, DB>): SqComparison<JAVA> =
    this.comparison(type, firstOperand, secondOperand, SqUtil.COMPARISON__GREATER)
fun <JAVA: Boolean?, DB: Any> SqExpression<*, DB>.gt(type: SqType<JAVA, Boolean>, other: SqExpression<*, DB>): SqComparison<JAVA> =
    this.context.gt(type, this, other)
@JvmName("gt__not_null")
infix fun <DB: Any> SqExpression<out Any, DB>.gt(other: SqExpression<out Any, DB>): SqComparison<Boolean> =
    this.gt(this.context.comparisonType(), other)
@JvmName("gt__nullable")
infix fun <DB: Any> SqExpression<out Any?, DB>.gt(other: SqExpression<out Any?, DB>): SqComparison<Boolean?> =
    this.gt(this.context.comparisonType().nullable(), other)
@JvmName("gt__not_null")
infix fun <JAVA: Any, DB: Any> SqExpression<JAVA, DB>.gt(value: JAVA): SqComparison<Boolean> =
    this.gt(this.context.param(this.type, value))
@JvmName("gt__nullable")
infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.gt(value: JAVA?): SqComparison<Boolean?> =
    this.gt(this.context.param(this.type.nullable(), value))
@JvmName("gt__not_null")
infix fun <JAVA: Any, DB: Any> JAVA.gt(expression: SqExpression<JAVA, DB>): SqComparison<Boolean> =
    expression.context.param<JAVA, DB>(expression.type, this).gt(expression)
@JvmName("gt__nullable")
infix fun <JAVA: Any?, DB: Any> JAVA?.gt(expression: SqExpression<JAVA, DB>): SqComparison<Boolean?> =
    expression.context.param<JAVA?, DB>(expression.type.nullable(), this).gt(expression)

fun <JAVA: Boolean?, DB: Any> SqContext.gte(type: SqType<JAVA, Boolean>, firstOperand: SqExpression<*, DB>, secondOperand: SqExpression<*, DB>): SqComparison<JAVA> =
    this.comparison(type, firstOperand, secondOperand, SqUtil.COMPARISON__GREATER_OR_EQUAL)
fun <JAVA: Boolean?, DB: Any> SqExpression<*, DB>.gte(type: SqType<JAVA, Boolean>, other: SqExpression<*, DB>): SqComparison<JAVA> =
    this.context.gte(type, this, other)
@JvmName("gte__not_null")
infix fun <DB: Any> SqExpression<out Any, DB>.gte(other: SqExpression<out Any, DB>): SqComparison<Boolean> =
    this.gte(this.context.comparisonType(), other)
@JvmName("gte__nullable")
infix fun <DB: Any> SqExpression<out Any?, DB>.gte(other: SqExpression<out Any?, DB>): SqComparison<Boolean?> =
    this.gte(this.context.comparisonType().nullable(), other)
@JvmName("gte__not_null")
infix fun <JAVA: Any, DB: Any> SqExpression<JAVA, DB>.gte(value: JAVA): SqComparison<Boolean> =
    this.gte(this.context.param(this.type, value))
@JvmName("gte__nullable")
infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.gte(value: JAVA?): SqComparison<Boolean?> =
    this.gte(this.context.param(this.type.nullable(), value))
@JvmName("gte__not_null")
infix fun <JAVA: Any, DB: Any> JAVA.gte(expression: SqExpression<JAVA, DB>): SqComparison<Boolean> =
    expression.context.param<JAVA, DB>(expression.type, this).gte(expression)
@JvmName("gte__nullable")
infix fun <JAVA: Any?, DB: Any> JAVA?.gte(expression: SqExpression<JAVA, DB>): SqComparison<Boolean?> =
    expression.context.param<JAVA?, DB>(expression.type.nullable(), this).gte(expression)

fun <JAVA: Boolean?, DB: Any> SqContext.lt(type: SqType<JAVA, Boolean>, firstOperand: SqExpression<*, DB>, secondOperand: SqExpression<*, DB>): SqComparison<JAVA> =
    this.comparison(type, firstOperand, secondOperand, SqUtil.COMPARISON__LESS)
fun <JAVA: Boolean?, DB: Any> SqExpression<*, DB>.lt(type: SqType<JAVA, Boolean>, other: SqExpression<*, DB>): SqComparison<JAVA> =
    this.context.lt(type, this, other)
@JvmName("lt__not_null")
infix fun <DB: Any> SqExpression<out Any, DB>.lt(other: SqExpression<out Any, DB>): SqComparison<Boolean> =
    this.lt(this.context.comparisonType(), other)
@JvmName("lt__nullable")
infix fun <DB: Any> SqExpression<out Any?, DB>.lt(other: SqExpression<out Any?, DB>): SqComparison<Boolean?> =
    this.lt(this.context.comparisonType().nullable(), other)
@JvmName("lt__not_null")
infix fun <JAVA: Any, DB: Any> SqExpression<JAVA, DB>.lt(value: JAVA): SqComparison<Boolean> =
    this.lt(this.context.param(this.type, value))
@JvmName("lt__nullable")
infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.lt(value: JAVA?): SqComparison<Boolean?> =
    this.lt(this.context.param(this.type.nullable(), value))
@JvmName("lt__not_null")
infix fun <JAVA: Any, DB: Any> JAVA.lt(expression: SqExpression<JAVA, DB>): SqComparison<Boolean> =
    expression.context.param<JAVA, DB>(expression.type, this).lt(expression)
@JvmName("lt__nullable")
infix fun <JAVA: Any?, DB: Any> JAVA?.lt(expression: SqExpression<JAVA, DB>): SqComparison<Boolean?> =
    expression.context.param<JAVA?, DB>(expression.type.nullable(), this).lt(expression)

fun <JAVA: Boolean?, DB: Any> SqContext.lte(type: SqType<JAVA, Boolean>, firstOperand: SqExpression<*, DB>, secondOperand: SqExpression<*, DB>): SqComparison<JAVA> =
    this.comparison(type, firstOperand, secondOperand, SqUtil.COMPARISON__LESS_OR_EQUAL)
fun <JAVA: Boolean?, DB: Any> SqExpression<*, DB>.lte(type: SqType<JAVA, Boolean>, other: SqExpression<*, DB>): SqComparison<JAVA> =
    this.context.lte(type, this, other)
@JvmName("lte__not_null")
infix fun <DB: Any> SqExpression<out Any, DB>.lte(other: SqExpression<out Any, DB>): SqComparison<Boolean> =
    this.lte(this.context.comparisonType(), other)
@JvmName("lte__nullable")
infix fun <DB: Any> SqExpression<out Any?, DB>.lte(other: SqExpression<out Any?, DB>): SqComparison<Boolean?> =
    this.lte(this.context.comparisonType().nullable(), other)
@JvmName("lte__not_null")
infix fun <JAVA: Any, DB: Any> SqExpression<JAVA, DB>.lte(value: JAVA): SqComparison<Boolean> =
    this.lte(this.context.param(this.type, value))
@JvmName("lte__nullable")
infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.lte(value: JAVA?): SqComparison<Boolean?> =
    this.lte(this.context.param(this.type.nullable(), value))
@JvmName("lte__not_null")
infix fun <JAVA: Any, DB: Any> JAVA.lte(expression: SqExpression<JAVA, DB>): SqComparison<Boolean> =
    expression.context.param<JAVA, DB>(expression.type, this).lte(expression)
@JvmName("lte__nullable")
infix fun <JAVA: Any?, DB: Any> JAVA?.lte(expression: SqExpression<JAVA, DB>): SqComparison<Boolean?> =
    expression.context.param<JAVA?, DB>(expression.type.nullable(), this).lte(expression)


fun <JAVA: Boolean?> SqContext.between(
    type: SqType<JAVA, Boolean>,
    negation: Boolean,
    testedValue: SqExpression<*, *>,
    firstRangeValue: SqExpression<*, *>,
    secondRangeValue: SqExpression<*, *>,
): SqBetweenTest<JAVA> {
    return this[SqBetweenTestConstructor::class.java, SqGenericBetweenTest.CONSTRUCTOR]
        .createBetweenTest(this, type, negation, testedValue, firstRangeValue, secondRangeValue)
}

fun <JAVA: Boolean?, DB: Any> SqExpression<*, DB>.between(
    type: SqType<JAVA, Boolean>,
    negation: Boolean,
    firstRangeValue: SqExpression<*, DB>,
    secondRangeValue: SqExpression<*, DB>,
): SqBetweenTest<JAVA> =
    this.context.between(type, negation, this, firstRangeValue, secondRangeValue)

@JvmName("between__not_null")
fun <DB: Any> SqExpression<out Any, DB>.between(
    firstRangeValue: SqExpression<out Any, DB>,
    secondRangeValue: SqExpression<out Any, DB>,
): SqBetweenTest<Boolean> =
    this.between(this.context.comparisonType(), negation = false, firstRangeValue, secondRangeValue)

@JvmName("between__nullable")
fun <DB: Any> SqExpression<out Any?, DB>.between(
    firstRangeValue: SqExpression<out Any?, DB>,
    secondRangeValue: SqExpression<out Any?, DB>,
): SqBetweenTest<Boolean?> =
    this.between(this.context.comparisonType().nullable(), negation = false, firstRangeValue, secondRangeValue)

@JvmName("notBetween__not_null")
fun <DB: Any> SqExpression<out Any, DB>.notBetween(
    firstRangeValue: SqExpression<out Any, DB>,
    secondRangeValue: SqExpression<out Any, DB>,
): SqBetweenTest<Boolean> =
    this.between(this.context.comparisonType(), negation = true, firstRangeValue, secondRangeValue)

@JvmName("notBetween__nullable")
fun <DB: Any> SqExpression<out Any?, DB>.notBetween(
    firstRangeValue: SqExpression<out Any?, DB>,
    secondRangeValue: SqExpression<out Any?, DB>,
): SqBetweenTest<Boolean?> =
    this.between(this.context.comparisonType().nullable(), negation = true, firstRangeValue, secondRangeValue)

@JvmName("between__not_null")
fun <JAVA: Any, DB: Any> SqExpression<JAVA, DB>.between(firstRangeValue: JAVA, secondRangeValue: JAVA): SqBetweenTest<Boolean> =
    this.between(this.context.param(this.type, firstRangeValue), this.context.param(this.type, secondRangeValue))
@JvmName("between__nullable")
fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.between(firstRangeValue: JAVA?, secondRangeValue: JAVA?): SqBetweenTest<Boolean?> =
    this.between(this.context.param(this.type.nullable(), firstRangeValue), this.context.param(this.type.nullable(), secondRangeValue))
@JvmName("notBetween__not_null")
fun <JAVA: Any, DB: Any> SqExpression<JAVA, DB>.notBetween(firstRangeValue: JAVA, secondRangeValue: JAVA): SqBetweenTest<Boolean> =
    this.notBetween(this.context.param(this.type, firstRangeValue), this.context.param(this.type, secondRangeValue))
@JvmName("notBetween__nullable")
fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.notBetween(firstRangeValue: JAVA?, secondRangeValue: JAVA?): SqBetweenTest<Boolean?> =
    this.notBetween(this.context.param(this.type.nullable(), firstRangeValue), this.context.param(this.type.nullable(), secondRangeValue))

fun <JAVA: Any?, DB: Any> SqContext.betweenTestStart(
    type: SqType<JAVA, DB>,
    negation: Boolean,
    testedValue: SqExpression<*, DB>,
    firstRangeValue: SqExpression<*, DB>,
): SqBetweenTestStart<JAVA, DB> {
    return this[SqBetweenTestStartConstructor::class.java, SqGenericBetweenTestStart.CONSTRUCTOR]
        .createBetweenTestStart(this, type, negation, testedValue, firstRangeValue)
}

@JvmName("between__not_null")
infix fun <JAVA: Any, DB: Any> SqExpression<JAVA, DB>.between(firstRangeValue: SqExpression<out Any, DB>): SqBetweenTestStart<JAVA, DB> =
    this.context.betweenTestStart(this.type, negation = false, this, firstRangeValue)
@JvmName("between__nullable")
infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.between(firstRangeValue: SqExpression<out Any?, DB>): SqBetweenTestStart<JAVA?, DB> =
    this.context.betweenTestStart(this.type.nullable(), negation = false, this, firstRangeValue)
@JvmName("notBetween__not_null")
infix fun <JAVA: Any, DB: Any> SqExpression<JAVA, DB>.notBetween(firstRangeValue: SqExpression<out Any, DB>): SqBetweenTestStart<JAVA, DB> =
    this.context.betweenTestStart(this.type, negation = true, this, firstRangeValue)
@JvmName("notBetween__nullable")
infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.notBetween(firstRangeValue: SqExpression<out Any?, DB>): SqBetweenTestStart<JAVA?, DB> =
    this.context.betweenTestStart(this.type.nullable(), negation = true, this, firstRangeValue)
@JvmName("between__not_null")
infix fun <JAVA: Any, DB: Any> SqExpression<JAVA, DB>.between(firstRangeValue: JAVA): SqBetweenTestStart<JAVA, DB> =
    this.between(this.context.param(this.type, firstRangeValue))
@JvmName("between__nullable")
infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.between(firstRangeValue: JAVA?): SqBetweenTestStart<JAVA?, DB> =
    this.between(this.context.param(this.type.nullable(), firstRangeValue))
@JvmName("notBetween__not_null")
infix fun <JAVA: Any, DB: Any> SqExpression<JAVA, DB>.notBetween(firstRangeValue: JAVA): SqBetweenTestStart<JAVA, DB> =
    this.between(this.context.param(this.type, firstRangeValue))
@JvmName("notBetween__nullable")
infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.notBetween(firstRangeValue: JAVA?): SqBetweenTestStart<JAVA?, DB> =
    this.notBetween(this.context.param(this.type.nullable(), firstRangeValue))
@JvmName("and__not_null")
infix fun <JAVA: Any, DB: Any> SqBetweenTestStart<JAVA, DB>.and(secondRangeValue: SqExpression<out Any, DB>): SqBetweenTest<Boolean> =
    this.context.between(this.context.comparisonType(), this.negation, this.testedValue, this.firstRangeValue, secondRangeValue)
@JvmName("and__nullable")
infix fun <JAVA: Any?, DB: Any> SqBetweenTestStart<JAVA, DB>.and(secondRangeValue: SqExpression<out Any?, DB>): SqBetweenTest<Boolean?> =
    this.context.between(this.context.comparisonType().nullable(), this.negation, this.testedValue, this.firstRangeValue, secondRangeValue)
@JvmName("and__not_null")
infix fun <JAVA: Any, DB: Any> SqBetweenTestStart<JAVA, DB>.and(secondRangeValue: JAVA): SqBetweenTest<Boolean> =
    this.and(this.context.param(this.type, secondRangeValue))
@JvmName("and__nullable")
infix fun <JAVA: Any?, DB: Any> SqBetweenTestStart<JAVA, DB>.and(secondRangeValue: JAVA?): SqBetweenTest<Boolean?> =
    this.and(this.context.param(this.type.nullable(), secondRangeValue))


fun <JAVA: Boolean?> SqContext.inList(
    type: SqType<JAVA, Boolean>,
    negation: Boolean,
    testedValue: SqExpression<*, *>,
    listValues: List<SqExpression<*, *>>,
): SqInListTest<JAVA> {
    return this[SqInListTestConstructor::class.java, SqGenericInListTest.CONSTRUCTOR]
        .createInListTest(this, type, negation, testedValue, listValues)
}

@JvmName("inList__expressions__not_null")
infix fun <DB: Any> SqExpression<out Any, DB>.inList(listValues: List<SqExpression<out Any, DB>>): SqInListTest<Boolean> =
    this.context.inList(this.context.comparisonType(), negation = false, this, listValues)
@JvmName("inList__expressions__nullable")
infix fun <DB: Any> SqExpression<out Any?, DB>.inList(listValues: List<SqExpression<out Any?, DB>>):SqInListTest<Boolean?> =
    this.context.inList(this.context.comparisonType().nullable(), negation = false, this, listValues)
@JvmName("notInList__expressions__not_null")
infix fun <DB: Any> SqExpression<out Any, DB>.notInList(listValues: List<SqExpression<out Any, DB>>): SqInListTest<Boolean> =
    this.context.inList(this.context.comparisonType(), negation = true, this, listValues)
@JvmName("notInList__expressions__nullable")
infix fun <DB: Any> SqExpression<out Any?, DB>.notInList(listValues: List<SqExpression<out Any?, DB>>): SqInListTest<Boolean?> =
    this.context.inList(this.context.comparisonType().nullable(), negation = true, this, listValues)
@JvmName("inList__not_null")
fun <DB: Any> SqExpression<out Any, DB>.inList(listValue: SqExpression<out Any, DB>, vararg moreListValues: SqExpression<out Any, DB>): SqInListTest<Boolean> =
    this.inList(listOf(listValue, *moreListValues))
@JvmName("inList__nullable")
fun <DB: Any> SqExpression<out Any?, DB>.inList(listValue: SqExpression<out Any?, DB>, vararg moreListValues: SqExpression<out Any?, DB>): SqInListTest<Boolean?> =
    this.inList(listOf(listValue, *moreListValues))
@JvmName("notInList__not_null")
fun <DB: Any> SqExpression<out Any, DB>.notInList(listValue: SqExpression<out Any, DB>, vararg moreListValues: SqExpression<out Any, DB>): SqInListTest<Boolean> =
    this.notInList(listOf(listValue, *moreListValues))
@JvmName("notInList__nullable")
fun <DB: Any> SqExpression<out Any?, DB>.notInList(listValue: SqExpression<out Any?, DB>, vararg moreListValues: SqExpression<out Any?, DB>): SqInListTest<Boolean?> =
    this.notInList(listOf(listValue, *moreListValues))

@JvmName("inList__values__not_null")
infix fun <JAVA: Any, DB: Any> SqExpression<JAVA, DB>.inList(listValues: List<JAVA>): SqInListTest<Boolean> {
    return this.inList(listValues.map { listValue ->
        this.context.param(this.type, listValue)
    })
}

@JvmName("inList__values__nullable")
infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.inList(listValues: List<JAVA?>): SqInListTest<Boolean?> {
    return this.inList(listValues.map { listValue ->
        this.context.param(this.type.nullable(), listValue)
    })
}

@JvmName("notInList__values__not_null")
infix fun <JAVA: Any, DB: Any> SqExpression<JAVA, DB>.notInList(listValues: List<JAVA>): SqInListTest<Boolean> {
    return this.notInList(listValues.map { listValue ->
        this.context.param(this.type, listValue)
    })
}

@JvmName("notInList__values__nullable")
infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.notInList(listValues: List<JAVA?>): SqInListTest<Boolean?> {
    return this.notInList(listValues.map { listValue ->
        this.context.param(this.type.nullable(), listValue)
    })
}

@JvmName("inList__not_null")
fun <JAVA: Any> SqExpression<JAVA, *>.inList(listValue: JAVA, vararg moreListValues: JAVA): SqInListTest<Boolean> =
    this.inList(listOf(listValue, *moreListValues))
@JvmName("inList__nullable")
fun <JAVA: Any?> SqExpression<JAVA, *>.inList(listValue: JAVA?, vararg moreListValues: JAVA?): SqInListTest<Boolean?> =
    this.inList(listOf(listValue, *moreListValues))
@JvmName("notInList__not_null")
fun <JAVA: Any> SqExpression<JAVA, *>.notInList(listValue: JAVA, vararg moreListValues: JAVA): SqInListTest<Boolean> =
    this.notInList(listOf(listValue, *moreListValues))
@JvmName("notInList__nullable")
fun <JAVA: Any?> SqExpression<JAVA, *>.notInList(listValue: JAVA?, vararg moreListValues: JAVA?): SqInListTest<Boolean?> =
    this.notInList(listOf(listValue, *moreListValues))
// endregion


// region Named functions
fun <JAVA: Any?, DB: Any> SqContext.namedFunction(type: SqType<JAVA, DB>, name: String, values: List<SqItem>, nameSpaced: Boolean? = null): SqNamedFunction<JAVA, DB> {
    return this[SqNamedFunctionConstructor::class.java, SqGenericNamedFunction.CONSTRUCTOR]
        .createNamedFunction(this, type, name, nameSpaced, values)
}

fun <JAVA: Any?, DB: Any> SqContext.all(select: SqSingleColReadStatement<JAVA, DB>): SqNamedFunction<JAVA, DB> =
    this.namedFunction(select.type, SqUtil.FUNCTION_NAME__ALL, listOf(select))
fun <JAVA: Any?, DB: Any> SqContext.any(select: SqSingleColReadStatement<JAVA, DB>): SqNamedFunction<JAVA, DB> =
    this.namedFunction(select.type, SqUtil.FUNCTION_NAME__ANY, listOf(select))
fun <JAVA: Number?> SqContext.avg(expression: SqExpression<JAVA, Number>): SqNamedFunction<JAVA, Number> =
    this.namedFunction(expression.type, SqUtil.FUNCTION_NAME__AVG, listOf(expression))
fun <JAVA: Any?, DB: Any> SqContext.coalesce(values: Iterable<SqExpression<out JAVA?, DB>>, lastValue: SqExpression<JAVA, DB>): SqNamedFunction<JAVA, DB> {
    return this.namedFunction(lastValue.type, SqUtil.FUNCTION_NAME__COALESCE, buildList {
        this.addAll(values)
        this.add(lastValue)
    })
}
fun <JAVA: Any?, DB: Any> SqContext.coalesce(vararg values: SqExpression<out JAVA?, DB>, lastValue: SqExpression<JAVA, DB>): SqNamedFunction<JAVA, DB> =
    this.coalesce(values.toList(), lastValue)
fun <JAVA: Number?> SqContext.count(type: SqType<JAVA, Number>, value: SqExpression<*, *>): SqNamedFunction<JAVA, Number> =
    this.namedFunction(type, SqUtil.FUNCTION_NAME__COUNT, listOf(value))
fun SqContext.count(value: SqExpression<*, *>): SqNamedFunction<Long, Number> = this.count(this.javaLongType(), value)
fun <JAVA: Boolean?> SqContext.exists(type: SqType<JAVA, Boolean>, select: SqSelect): SqNamedFunction<JAVA, Boolean> =
    this.namedFunction(type, SqUtil.FUNCTION_NAME__EXISTS, listOf(select))
fun SqContext.exists(select: SqSelect): SqNamedFunction<Boolean, Boolean> = this.exists(this.javaBooleanType(), select)
fun <JAVA: Any?, DB: Any> SqContext.min(value: SqExpression<JAVA, DB>): SqNamedFunction<JAVA, DB> =
    this.namedFunction(value.type, SqUtil.FUNCTION_NAME__MIN, listOf(value))
fun <JAVA: Any?, DB: Any> SqContext.max(value: SqExpression<JAVA, DB>): SqNamedFunction<JAVA, DB> =
    this.namedFunction(value.type, SqUtil.FUNCTION_NAME__MAX, listOf(value))
fun <JAVA: Number?> SqContext.sum(expression: SqExpression<JAVA, Number>): SqNamedFunction<JAVA, Number> =
    this.namedFunction(expression.type, SqUtil.FUNCTION_NAME__SUM, listOf(expression))
// endregion


// region Mathematical operations
fun <JAVA: Number?> SqContext.mathOperation(
    type: SqType<JAVA, Number>,
    firstOperand: SqExpression<*, Number>,
    operation: String,
    secondOperand: SqExpression<*, Number>,
): SqTwoOperandMathOperation<JAVA> {
    return this[SqTwoOperandMathOperationConstructor::class.java, SqGenericTwoOperandMathOperation.CONSTRUCTOR]
        .createTwoOperandMathOperation(this, type, firstOperand, operation, secondOperand)
}

fun <JAVA: Number?> SqContext.add(type: SqType<JAVA, Number>, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
    this.mathOperation(type, firstOperand, SqUtil.MATH_OPERATION__ADD, secondOperand)
@JvmName("add__not_null")
infix fun SqExpression<out Any, Number>.add(other: SqExpression<out Any, Number>): SqTwoOperandMathOperation<Number> =
    this.context.add(this.context.mathOperationType(), this, other)
@JvmName("add__nullable")
infix fun SqExpression<out Any?, Number>.add(other: SqExpression<out Any?, Number>): SqTwoOperandMathOperation<Number?> =
    this.context.add(this.context.mathOperationType().nullable(), this, other)
@JvmName("add__not_null__typed")
infix fun <JAVA: Number> SqExpression<JAVA, Number>.add(other: SqExpression<JAVA, Number>): SqTwoOperandMathOperation<JAVA> =
    this.context.add(this.type, this, other)
@JvmName("add__nullable__typed")
infix fun <JAVA: Number?> SqExpression<JAVA, Number>.add(other: SqExpression<out JAVA?, Number>): SqTwoOperandMathOperation<JAVA?> =
    this.context.add(this.type.nullable(), this, other)
@JvmName("add__not_null")
infix fun <JAVA: Number> SqExpression<JAVA, Number>.add(value: JAVA): SqTwoOperandMathOperation<JAVA> =
    this.context.add(this.type, this, this.context.param(this.type, value))
@JvmName("add__nullable")
infix fun <JAVA: Number?> SqExpression<JAVA, Number>.add(value: JAVA?): SqTwoOperandMathOperation<JAVA?> =
    this.context.add(this.type.nullable(), this, this.context.param(this.type.nullable(), value))

fun <JAVA: Number?> SqContext.sub(type: SqType<JAVA, Number>, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
    this.mathOperation(type, firstOperand, SqUtil.MATH_OPERATION__SUBTRACT, secondOperand)
@JvmName("sub__not_null")
infix fun SqExpression<out Any, Number>.sub(other: SqExpression<out Any, Number>): SqTwoOperandMathOperation<Number> =
    this.context.sub(this.context.mathOperationType(), this, other)
@JvmName("sub__nullable")
infix fun SqExpression<out Any?, Number>.sub(other: SqExpression<out Any?, Number>): SqTwoOperandMathOperation<Number?> =
    this.context.sub(this.context.mathOperationType().nullable(), this, other)
@JvmName("sub__not_null__typed")
infix fun <JAVA: Number> SqExpression<JAVA, Number>.sub(other: SqExpression<JAVA, Number>): SqTwoOperandMathOperation<JAVA> =
    this.context.sub(this.type, this, other)
@JvmName("sub__nullable__typed")
infix fun <JAVA: Number?> SqExpression<JAVA, Number>.sub(other: SqExpression<out JAVA?, Number>): SqTwoOperandMathOperation<JAVA?> =
    this.context.sub(this.type.nullable(), this, other)
@JvmName("sub__not_null")
infix fun <JAVA: Number> SqExpression<JAVA, Number>.sub(value: JAVA): SqTwoOperandMathOperation<JAVA> =
    this.context.sub(this.type, this, this.context.param(this.type, value))
@JvmName("sub__nullable")
infix fun <JAVA: Number?> SqExpression<JAVA, Number>.sub(value: JAVA?): SqTwoOperandMathOperation<JAVA?> =
    this.context.sub(this.type.nullable(), this, this.context.param(this.type.nullable(), value))

fun <JAVA: Number?> SqContext.mult(type: SqType<JAVA, Number>, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
    this.mathOperation(type, firstOperand, SqUtil.MATH_OPERATION__MULTIPLY, secondOperand)
@JvmName("mult__not_null")
infix fun SqExpression<out Any, Number>.mult(other: SqExpression<out Any, Number>): SqTwoOperandMathOperation<Number> =
    this.context.mult(this.context.mathOperationType(), this, other)
@JvmName("mult__nullable")
infix fun SqExpression<out Any?, Number>.mult(other: SqExpression<out Any?, Number>): SqTwoOperandMathOperation<Number?> =
    this.context.mult(this.context.mathOperationType().nullable(), this, other)
@JvmName("mult__not_null__typed")
infix fun <JAVA: Number> SqExpression<JAVA, Number>.mult(other: SqExpression<JAVA, Number>): SqTwoOperandMathOperation<JAVA> =
    this.context.mult(this.type, this, other)
@JvmName("mult__nullable__typed")
infix fun <JAVA: Number?> SqExpression<JAVA, Number>.mult(other: SqExpression<out JAVA?, Number>): SqTwoOperandMathOperation<JAVA?> =
    this.context.mult(this.type.nullable(), this, other)
@JvmName("mult__not_null")
infix fun <JAVA: Number> SqExpression<JAVA, Number>.mult(value: JAVA): SqTwoOperandMathOperation<JAVA> =
    this.context.mult(this.type, this, this.context.param(this.type, value))
@JvmName("mult__nullable")
infix fun <JAVA: Number?> SqExpression<JAVA, Number>.mult(value: JAVA?): SqTwoOperandMathOperation<JAVA?> =
    this.context.mult(this.type.nullable(), this, this.context.param(this.type.nullable(), value))

fun <JAVA: Number?> SqContext.div(type: SqType<JAVA, Number>, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
    this.mathOperation(type, firstOperand, SqUtil.MATH_OPERATION__DIVIDE, secondOperand)
@JvmName("div__not_null")
infix fun SqExpression<out Any, Number>.div(other: SqExpression<out Any, Number>): SqTwoOperandMathOperation<Number> =
    this.context.div(this.context.mathOperationType(), this, other)
@JvmName("div__nullable")
infix fun SqExpression<out Any?, Number>.div(other: SqExpression<out Any?, Number>): SqTwoOperandMathOperation<Number?> =
    this.context.div(this.context.mathOperationType().nullable(), this, other)
@JvmName("div__not_null__typed")
infix fun <JAVA: Number> SqExpression<JAVA, Number>.div(other: SqExpression<JAVA, Number>): SqTwoOperandMathOperation<JAVA> =
    this.context.div(this.type, this, other)
@JvmName("div__nullable__typed")
infix fun <JAVA: Number?> SqExpression<JAVA, Number>.div(other: SqExpression<out JAVA?, Number>): SqTwoOperandMathOperation<JAVA?> =
    this.context.div(this.type.nullable(), this, other)
@JvmName("div__not_null")
infix fun <JAVA: Number> SqExpression<JAVA, Number>.div(value: JAVA): SqTwoOperandMathOperation<JAVA> =
    this.context.div(this.type, this, this.context.param(this.type, value))
@JvmName("div__nullable")
infix fun <JAVA: Number?> SqExpression<JAVA, Number>.div(value: JAVA?): SqTwoOperandMathOperation<JAVA?> =
    this.context.div(this.type.nullable(), this, this.context.param(this.type.nullable(), value))

fun <JAVA: Number?> SqContext.mod(type: SqType<JAVA, Number>, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
    this.mathOperation(type, firstOperand, SqUtil.MATH_OPERATION__MODULO, secondOperand)
@JvmName("mod__not_null")
infix fun SqExpression<out Any, Number>.mod(other: SqExpression<out Any, Number>): SqTwoOperandMathOperation<Number> =
    this.context.mod(this.context.mathOperationType(), this, other)
@JvmName("mod__nullable")
infix fun SqExpression<out Any?, Number>.mod(other: SqExpression<out Any?, Number>): SqTwoOperandMathOperation<Number?> =
    this.context.mod(this.context.mathOperationType().nullable(), this, other)
@JvmName("mod__not_null__typed")
infix fun <JAVA: Number> SqExpression<JAVA, Number>.mod(other: SqExpression<JAVA, Number>): SqTwoOperandMathOperation<JAVA> =
    this.context.mod(this.type, this, other)
@JvmName("mod__nullable__typed")
infix fun <JAVA: Number?> SqExpression<JAVA, Number>.mod(other: SqExpression<out JAVA?, Number>): SqTwoOperandMathOperation<JAVA?> =
    this.context.mod(this.type.nullable(), this, other)
@JvmName("mod__not_null")
infix fun <JAVA: Number> SqExpression<JAVA, Number>.mod(value: JAVA): SqTwoOperandMathOperation<JAVA> =
    this.context.mod(this.type, this, this.context.param(this.type, value))
@JvmName("mod__nullable")
infix fun <JAVA: Number?> SqExpression<JAVA, Number>.mod(value: JAVA?): SqTwoOperandMathOperation<JAVA?> =
    this.context.mod(this.type.nullable(), this, this.context.param(this.type.nullable(), value))

fun <JAVA: Number?> SqContext.bitwiseAnd(type: SqType<JAVA, Number>, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
    this.mathOperation(type, firstOperand, SqUtil.MATH_OPERATION__BITWISE_AND, secondOperand)
@JvmName("bitwiseAnd__not_null")
infix fun SqExpression<out Any, Number>.bitwiseAnd(other: SqExpression<out Any, Number>): SqTwoOperandMathOperation<Number> =
    this.context.bitwiseAnd(this.context.mathOperationType(), this, other)
@JvmName("bitwiseAnd__nullable")
infix fun SqExpression<out Any?, Number>.bitwiseAnd(other: SqExpression<out Any?, Number>): SqTwoOperandMathOperation<Number?> =
    this.context.bitwiseAnd(this.context.mathOperationType().nullable(), this, other)
@JvmName("bitwiseAnd__not_null__typed")
infix fun <JAVA: Number> SqExpression<JAVA, Number>.bitwiseAnd(other: SqExpression<JAVA, Number>): SqTwoOperandMathOperation<JAVA> =
    this.context.bitwiseAnd(this.type, this, other)
@JvmName("bitwiseAnd__nullable__typed")
infix fun <JAVA: Number?> SqExpression<JAVA, Number>.bitwiseAnd(other: SqExpression<out JAVA?, Number>): SqTwoOperandMathOperation<JAVA?> =
    this.context.bitwiseAnd(this.type.nullable(), this, other)
@JvmName("bitwiseAnd__not_null")
infix fun <JAVA: Number> SqExpression<JAVA, Number>.bitwiseAnd(value: JAVA): SqTwoOperandMathOperation<JAVA> =
    this.context.bitwiseAnd(this.type, this, this.context.param(this.type, value))
@JvmName("bitwiseAnd__nullable")
infix fun <JAVA: Number?> SqExpression<JAVA, Number>.bitwiseAnd(value: JAVA?): SqTwoOperandMathOperation<JAVA?> =
    this.context.bitwiseAnd(this.type.nullable(), this, this.context.param(this.type.nullable(), value))

fun <JAVA: Number?> SqContext.bitwiseOr(type: SqType<JAVA, Number>, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
    this.mathOperation(type, firstOperand, SqUtil.MATH_OPERATION__BITWISE_OR, secondOperand)
@JvmName("bitwiseOr__not_null")
infix fun SqExpression<out Any, Number>.bitwiseOr(other: SqExpression<out Any, Number>): SqTwoOperandMathOperation<Number> =
    this.context.bitwiseOr(this.context.mathOperationType(), this, other)
@JvmName("bitwiseOr__nullable")
infix fun SqExpression<out Any?, Number>.bitwiseOr(other: SqExpression<out Any?, Number>): SqTwoOperandMathOperation<Number?> =
    this.context.bitwiseOr(this.context.mathOperationType().nullable(), this, other)
@JvmName("bitwiseOr__not_null__typed")
infix fun <JAVA: Number> SqExpression<JAVA, Number>.bitwiseOr(other: SqExpression<JAVA, Number>): SqTwoOperandMathOperation<JAVA> =
    this.context.bitwiseOr(this.type, this, other)
@JvmName("bitwiseOr__nullable__typed")
infix fun <JAVA: Number?> SqExpression<JAVA, Number>.bitwiseOr(other: SqExpression<out JAVA?, Number>): SqTwoOperandMathOperation<JAVA?> =
    this.context.bitwiseOr(this.type.nullable(), this, other)
@JvmName("bitwiseOr__not_null")
infix fun <JAVA: Number> SqExpression<JAVA, Number>.bitwiseOr(value: JAVA): SqTwoOperandMathOperation<JAVA> =
    this.context.bitwiseOr(this.type, this, this.context.param(this.type, value))
@JvmName("bitwiseOr__nullable")
infix fun <JAVA: Number?> SqExpression<JAVA, Number>.bitwiseOr(value: JAVA?): SqTwoOperandMathOperation<JAVA?> =
    this.context.bitwiseOr(this.type.nullable(), this, this.context.param(this.type.nullable(), value))

fun <JAVA: Number?> SqContext.bitwiseXor(type: SqType<JAVA, Number>, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
    this.mathOperation(type, firstOperand, SqUtil.MATH_OPERATION__BITWISE_XOR, secondOperand)
@JvmName("bitwiseXor__not_null")
infix fun SqExpression<out Any, Number>.bitwiseXor(other: SqExpression<out Any, Number>): SqTwoOperandMathOperation<Number> =
    this.context.bitwiseXor(this.context.mathOperationType(), this, other)
@JvmName("bitwiseXor__nullable")
infix fun SqExpression<out Any?, Number>.bitwiseXor(other: SqExpression<out Any?, Number>): SqTwoOperandMathOperation<Number?> =
    this.context.bitwiseXor(this.context.mathOperationType().nullable(), this, other)
@JvmName("bitwiseXor__not_null__typed")
infix fun <JAVA: Number> SqExpression<JAVA, Number>.bitwiseXor(other: SqExpression<JAVA, Number>): SqTwoOperandMathOperation<JAVA> =
    this.context.bitwiseXor(this.type, this, other)
@JvmName("bitwiseXor__nullable__typed")
infix fun <JAVA: Number?> SqExpression<JAVA, Number>.bitwiseXor(other: SqExpression<out JAVA?, Number>): SqTwoOperandMathOperation<JAVA?> =
    this.context.bitwiseXor(this.type.nullable(), this, other)
@JvmName("bitwiseXor__not_null")
infix fun <JAVA: Number> SqExpression<JAVA, Number>.bitwiseXor(value: JAVA): SqTwoOperandMathOperation<JAVA> =
    this.context.bitwiseXor(this.type, this, this.context.param(this.type, value))
@JvmName("bitwiseXor__nullable")
infix fun <JAVA: Number?> SqExpression<JAVA, Number>.bitwiseXor(value: JAVA?): SqTwoOperandMathOperation<JAVA?> =
    this.context.bitwiseXor(this.type.nullable(), this, this.context.param(this.type.nullable(), value))
// endregion


// region Case
fun <JAVA: Any?, DB: Any> SqContext.caseItem(whenItem: SqExpression<*, Boolean>, thenItem: SqExpression<JAVA, DB>): SqCaseItem<JAVA, DB> {
    return this[SqCaseItemConstructor::class.java, SqGenericCaseItem.CONSTRUCTOR]
        .createCaseItem(this, whenItem, thenItem)
}

fun <JAVA: Any?, DB: Any> SqContext.case(type: SqType<JAVA, DB>, items: List<SqCaseItem<out JAVA, DB>>, elseItem: SqExpression<out JAVA, DB>?): SqCase<JAVA, DB> {
    return this[SqCaseConstructor::class.java, SqGenericCase.CONSTRUCTOR]
        .createCase(this, type, items, elseItem)
}

fun <JAVA: Any?, DB: Any> SqContext.case(
    type: SqType<JAVA, DB>,
    vararg items: SqCaseItem<out JAVA, DB>,
    elseItem: SqExpression<out JAVA, DB>?,
): SqCase<JAVA, DB> {
    return this.case(type, items.toList(), elseItem)
}

fun <JAVA: Any?, DB: Any> SqContext.case(firstItem: SqCaseItem<JAVA, DB>, vararg moreItems: SqCaseItem<out JAVA, DB>): SqCase<JAVA, DB> {
    return this.case(firstItem.thenItem.type, listOf(firstItem, *moreItems), null)
}

fun <JAVA: Any?, DB: Any> SqContext.case(items: List<SqCaseItem<out JAVA, DB>>, elseItem: SqExpression<JAVA, DB>): SqCase<JAVA, DB> {
    return this.case(elseItem.type, items, elseItem)
}

fun <JAVA: Any?, DB: Any> SqContext.case(vararg items: SqCaseItem<out JAVA, DB>, elseItem: SqExpression<JAVA, DB>): SqCase<JAVA, DB> {
    return this.case(elseItem.type, items.toList(), elseItem)
}


fun SqContext.case(): SqCaseBuildStartUntyped {
    return this[SqCaseBuildStartUntypedConstructor::class.java, SqGenericCaseBuilder.CONSTRUCTOR__START_UNTYPED]
        .createCaseBuildStartUntyped(this)
}
infix fun SqCaseBuildStartUntyped.`when`(condition: SqExpression<*, Boolean>): SqCaseBuildItemStartUntyped =
    this.startWhen(condition)
infix fun <JAVA: Any?, DB: Any> SqCaseBuildStartUntyped.`else`(value: SqExpression<JAVA, DB>): SqCaseBuildEnd<JAVA, DB> =
    this.startElse(value)

infix fun <JAVA: Any?, DB: Any> SqCaseBuildItemStartUntyped.then(value: SqExpression<JAVA, DB>): SqCaseBuildMiddle<JAVA, DB> =
    this.addThen(value)

@JvmName("addThen__not_null")
infix fun <JAVA: Any?, DB: Any> SqCaseBuildItemStartTyped<JAVA, DB>.addThen(value: SqExpression<out JAVA, DB>): SqCaseBuildMiddle<JAVA, DB> =
    this.addThenNotNull(value)
@JvmName("addThen__nullable")
infix fun <JAVA: Any?, DB: Any> SqCaseBuildItemStartTyped<JAVA, DB>.addThen(value: SqExpression<out JAVA?, DB>): SqCaseBuildMiddle<JAVA?, DB> =
    this.addThenNullable(value)
@JvmName("then__not_null")
infix fun <JAVA: Any?, DB: Any> SqCaseBuildItemStartTyped<JAVA, DB>.then(value: SqExpression<out JAVA, DB>): SqCaseBuildMiddle<JAVA, DB> =
    this.addThenNotNull(value)
@JvmName("then__nullable")
infix fun <JAVA: Any?, DB: Any> SqCaseBuildItemStartTyped<JAVA, DB>.then(value: SqExpression<out JAVA?, DB>): SqCaseBuildMiddle<JAVA?, DB> =
    this.addThenNullable(value)
@JvmName("addThen__not_null")
infix fun <JAVA: Any?, DB: Any> SqCaseBuildItemStartTyped<JAVA, DB>.addThen(value: JAVA): SqCaseBuildMiddle<JAVA, DB> =
    this.addThen(this.context.param(this.type, value))
@JvmName("addThen__nullable")
infix fun <JAVA: Any?, DB: Any> SqCaseBuildItemStartTyped<JAVA, DB>.addThen(value: JAVA?): SqCaseBuildMiddle<JAVA?, DB> =
    this.addThen(this.context.param(this.type.nullable(), value))
@JvmName("then__not_null")
infix fun <JAVA: Any?, DB: Any> SqCaseBuildItemStartTyped<JAVA, DB>.then(value: JAVA): SqCaseBuildMiddle<JAVA, DB> =
    this.then(this.context.param(this.type, value))
@JvmName("then__nullable")
infix fun <JAVA: Any?, DB: Any> SqCaseBuildItemStartTyped<JAVA, DB>.then(value: JAVA?): SqCaseBuildMiddle<JAVA?, DB> =
    this.then(this.context.param(this.type.nullable(), value))

fun <JAVA: Any?, DB: Any> SqContext.case(type: SqType<JAVA, DB>): SqCaseBuildMiddle<JAVA, DB> {
    return this[SqCaseBuildMiddleConstructor::class.java, SqGenericCaseBuilder.CONSTRUCTOR__MIDDLE]
        .createCaseBuildMiddle(this, type)
}
infix fun <JAVA: Any?, DB: Any> SqCaseBuildMiddle<JAVA, DB>.`when`(condition: SqExpression<*, Boolean>): SqCaseBuildItemStartTyped<JAVA, DB> =
    this.startWhen(condition)
@JvmName("startElse__not_null")
infix fun <JAVA: Any?, DB: Any> SqCaseBuildMiddle<JAVA, DB>.startElse(value: SqExpression<out JAVA, DB>): SqCaseBuildEnd<JAVA, DB> =
    this.startElseNotNull(value)
@JvmName("startElse__nullable")
infix fun <JAVA: Any?, DB: Any> SqCaseBuildMiddle<JAVA, DB>.startElse(value: SqExpression<out JAVA?, DB>): SqCaseBuildEnd<JAVA?, DB> =
    this.startElseNullable(value)
@JvmName("else__not_null")
infix fun <JAVA: Any?, DB: Any> SqCaseBuildMiddle<JAVA, DB>.`else`(value: SqExpression<out JAVA, DB>): SqCaseBuildEnd<JAVA, DB> =
    this.startElseNotNull(value)
@JvmName("else__nullable")
infix fun <JAVA: Any?, DB: Any> SqCaseBuildMiddle<JAVA, DB>.`else`(value: SqExpression<out JAVA?, DB>): SqCaseBuildEnd<JAVA?, DB> =
    this.startElseNullable(value)
@JvmName("startElse__not_null")
infix fun <JAVA: Any?, DB: Any> SqCaseBuildMiddle<JAVA, DB>.startElse(value: JAVA): SqCaseBuildEnd<JAVA, DB> =
    this.startElse(this.context.param(this.types, value))
@JvmName("startElse__nullable")
infix fun <JAVA: Any?, DB: Any> SqCaseBuildMiddle<JAVA, DB>.startElse(value: JAVA?): SqCaseBuildEnd<JAVA?, DB> =
    this.startElse(this.context.param(this.types.nullable(), value))
@JvmName("else__not_null")
infix fun <JAVA: Any?, DB: Any> SqCaseBuildMiddle<JAVA, DB>.`else`(value: JAVA): SqCaseBuildEnd<JAVA, DB> =
    this.`else`(this.context.param(this.types, value))
@JvmName("else__nullable")
infix fun <JAVA: Any?, DB: Any> SqCaseBuildMiddle<JAVA, DB>.`else`(value: JAVA?): SqCaseBuildEnd<JAVA?, DB> =
    this.`else`(this.context.param(this.types.nullable(), value))
// endregion


// region Statements - base
fun <T: SqReadStatement> T.firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): T = this.apply { this.firstResultIndexParam = firstResultIndex }
fun <T: SqReadStatement> T.firstResultIndex(firstResultIndex: Long?): T = this.apply { this.firstResultIndex = firstResultIndex }
fun <T: SqReadStatement> T.resultCount(resultCount: SqParameter<Long, Number>?): T = this.apply { this.resultCountParam = resultCount }
fun <T: SqReadStatement> T.resultCount(resultCount: Long?): T = this.apply { this.resultCount = resultCount }
fun <T: SqReadStatement> T.limits(resultCount: SqParameter<Long, Number>?, firstResultIndex: SqParameter<Long, Number>? = null): T =
    this.resultCount(resultCount).firstResultIndex(firstResultIndex)
fun <T: SqReadStatement> T.limits(resultCount: Long?, firstResultIndex: Long? = null): T =
    this.resultCount(resultCount).firstResultIndex(firstResultIndex)


inline fun <RS: SqReadStatement> RS.load(
    connection: Connection,
    initStatement: (RS.(statement: PreparedStatement) -> Any?) = {},
    block: RS.(reader: SqReader) -> Unit,
) {
    contract {
        callsInPlace(initStatement, InvocationKind.EXACTLY_ONCE)
        callsInPlace(block, InvocationKind.UNKNOWN)
    }

    this.prepareStatement(connection).use { statement ->
        initStatement.invoke(this, statement)
        statement.executeQuery().use { resultSet ->
            val reader = SqReader(this, resultSet)
            while (reader.next()) {
                block.invoke(this, reader)
            }
        }
    }
}

inline fun <RS: SqReadStatement, T: Any?> RS.loadAndMap(
    connection: Connection,
    initStatement: (RS.(statement: PreparedStatement) -> Any?) = {},
    block: RS.(reader: SqReader) -> T,
): List<T> {
    contract {
        callsInPlace(initStatement, InvocationKind.EXACTLY_ONCE)
        callsInPlace(block, InvocationKind.UNKNOWN)
    }

    return this.prepareStatement(connection).use { statement ->
        initStatement.invoke(this, statement)
        statement.executeQuery().use { resultSet ->
            buildList {
                val reader = SqReader(this@loadAndMap, resultSet)
                while (reader.next()) {
                    val item = block.invoke(this@loadAndMap, reader)
                    this.add(item)
                }
            }
        }
    }
}

inline fun <RS: SqConnReadStatement> RS.load(
    initStatement: (RS.(statement: PreparedStatement) -> Any?) = {},
    block: RS.(reader: SqReader) -> Unit,
) {
    contract {
        callsInPlace(initStatement, InvocationKind.EXACTLY_ONCE)
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    this.prepareStatement().use { statement ->
        initStatement.invoke(this, statement)
        statement.executeQuery().use { resultSet ->
            val reader = SqReader(this, resultSet)
            while (reader.next()) {
                block.invoke(this, reader)
            }
        }
    }
}

inline fun <RS: SqConnReadStatement, T: Any?> RS.loadAndMap(
    initStatement: (RS.(statement: PreparedStatement) -> Any?) = {},
    block: RS.(reader: SqReader) -> T,
): List<T> {
    contract {
        callsInPlace(initStatement, InvocationKind.EXACTLY_ONCE)
        callsInPlace(block, InvocationKind.UNKNOWN)
    }

    return this.prepareStatement().use { statement ->
        initStatement.invoke(this, statement)
        statement.executeQuery().use { resultSet ->
            val reader = SqReader(this, resultSet)
            buildList {
                while (reader.next()) {
                    val item = block.invoke(this@loadAndMap, reader)
                    this.add(item)
                }
            }
        }
    }
}


inline fun <T: SqTable, S: SqTableWriteStatement<T>> S.set(block: (mapping: SqColumnValueMapping<T>) -> Unit): S {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

    val mapping = this.createValueMapping()
    block.invoke(mapping)
    this.applyValueMapping(mapping)
    mapping.clearData()

    return this
}
// endregion


// region Statements - join, order by, select, union
fun SqContext.join(joinType: SqJoinType, mainColSet: SqColSet, joinedColSet: SqColSet): SqJoin {
    return this[SqJoinConstructor::class.java, SqGenericJoin.CONSTRUCTOR]
        .createJoin(this, joinType, mainColSet, joinedColSet)
}
fun SqColSet.join(type: SqJoinType, joined: SqColSet): SqJoin = this.context.join(type, this, joined)
infix fun SqColSet.innerJoin(joined: SqColSet): SqJoin = this.join(SqJoinType.INNER, joined)
infix fun SqColSet.leftJoin(joined: SqColSet): SqJoin = this.join(SqJoinType.LEFT, joined)
infix fun SqColSet.rightJoin(joined: SqColSet): SqJoin = this.join(SqJoinType.RIGHT, joined)
infix fun SqColSet.fullJoin(joined: SqColSet): SqJoin = this.join(SqJoinType.FULL, joined)


fun SqContext.orderBy(column: SqColumn<*, *>, order: SqSortOrder): SqOrderBy {
    return this[SqOrderByConstructor::class.java, SqGenericOrderBy.CONSTRUCTOR]
        .createOrderBy(this, column, order)
}
infix fun SqColumn<*, *>.orderBy(order: SqSortOrder): SqOrderBy = this.context.orderBy(this, order)
fun SqColumn<*, *>.asc(): SqOrderBy = this.orderBy(SqSortOrder.ASC)
fun SqColumn<*, *>.desc(): SqOrderBy = this.orderBy(SqSortOrder.DESC)


fun SqContext.select(distinct: Boolean, columns: List<SqColumn<*, *>>): SqMultiColSelect {
    return this[SqMultiColSelectConstructor::class.java, SqGenericMultiColSelect.CONSTRUCTOR]
        .createMultiColSelect(this, distinct, columns)
}
fun SqContext.select(distinct: Boolean, first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqMultiColSelect =
    this.select(distinct, listOf(first, second, *more))
fun SqContext.select(columns: List<SqColumn<*, *>>): SqMultiColSelect = this.select(distinct = false, columns)
fun SqContext.select(first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqMultiColSelect =
    this.select(distinct = false, first, second, *more)
fun SqContext.selectDistinct(columns: List<SqColumn<*, *>>): SqMultiColSelect = this.select(distinct = true, columns)
fun SqContext.selectDistinct(first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqMultiColSelect =
    this.select(distinct = true, first, second, *more)

fun <JAVA: Any?, DB: Any> SqContext.select(distinct: Boolean, column: SqColumn<JAVA, DB>): SqSingleColSelect<JAVA, DB> {
    return this[SqSingleColSelectConstructor::class.java, SqGenericSingleColSelect.CONSTRUCTOR]
        .createSingleColSelect(this, distinct, column)
}
fun <JAVA: Any?, DB: Any> SqContext.select(column: SqColumn<JAVA, DB>): SqSingleColSelect<JAVA, DB> = this.select(distinct = false, column)
fun <JAVA: Any?, DB: Any> SqContext.selectDistinct(column: SqColumn<JAVA, DB>): SqSingleColSelect<JAVA, DB> = this.select(distinct = true, column)

fun <T: SqSelect> T.from(from: List<SqColSet>): T = this.apply { this.from = from }
fun <T: SqSelect> T.from(first: SqColSet, vararg more: SqColSet): T = this.from(listOf(first, *more))
fun <T: SqSelect> T.where(where: SqExpression<*, Boolean>?): T = this.apply { this.where = where }
fun <T: SqSelect> T.groupBy(items: List<SqColumn<*, *>>): T = this.apply { this.groupBy = items }
fun <T: SqSelect> T.groupBy(first: SqColumn<*, *>, vararg more: SqColumn<*, *>): T = this.groupBy(listOf(first, *more))
fun <T: SqSelect> T.having(having: SqExpression<*, Boolean>?): T = this.apply { this.having = having }
fun <T: SqSelect> T.orderBy(items: List<SqOrderBy>): T = this.apply { this.orderBy = items }
fun <T: SqSelect> T.orderBy(first: SqOrderBy, vararg more: SqOrderBy): T = this.orderBy(listOf(first, *more))

fun <T: SqMultiColSelect> T.columns(columns: List<SqColumn<*, *>>): T = this.apply { this.columns = columns }

fun <JAVA: Any?, DB: Any, T: SqSingleColSelect<JAVA, DB>> T.column(column: SqColumn<JAVA, DB>): T = this.apply { this.column = column }


fun SqContext.union(unionAll: Boolean, selects: List<SqSelect>): SqMultiColUnion {
    return this[SqMultiColUnionConstructor::class.java, SqGenericMultiColUnion.CONSTRUCTOR]
        .createMultiColUnion(this, unionAll, selects)
}
fun SqContext.union(unionAll: Boolean, first: SqSelect, second: SqSelect, vararg more: SqSelect): SqMultiColUnion = this.union(unionAll, listOf(first, second, *more))
fun SqContext.union(selects: List<SqSelect>): SqMultiColUnion = this.union(unionAll = false, selects)
fun SqContext.union(first: SqSelect, second: SqSelect, vararg more: SqSelect): SqMultiColUnion = this.union(unionAll = false, first, second, *more)
fun SqContext.unionAll(selects: List<SqSelect>): SqMultiColUnion = this.union(unionAll = true, selects)
fun SqContext.unionAll(first: SqSelect, second: SqSelect, vararg more: SqSelect): SqMultiColUnion = this.union(unionAll = true, first, second, *more)

fun <JAVA: Any?, DB: Any> SqContext.union(unionAll: Boolean, selects: List<SqSingleColSelect<JAVA, DB>>): SqSingleColUnion<JAVA, DB> {
    return this[SqSingleColUnionConstructor::class.java, SqGenericSingleColUnion.CONSTRUCTOR]
        .createSingleColUnion(this, unionAll, selects)
}
fun <JAVA: Any?, DB: Any> SqContext.union(
    unionAll: Boolean,
    first: SqSingleColSelect<JAVA, DB>,
    second: SqSingleColSelect<JAVA, DB>,
    vararg more: SqSingleColSelect<JAVA, DB>,
): SqSingleColUnion<JAVA, DB> = this.union(unionAll, listOf(first, second, *more))
fun <JAVA: Any?, DB: Any> SqContext.union(selects: List<SqSingleColSelect<JAVA, DB>>): SqSingleColUnion<JAVA, DB> =
    this.union(unionAll = false, selects)
fun <JAVA: Any?, DB: Any> SqContext.union(
    first: SqSingleColSelect<JAVA, DB>,
    second: SqSingleColSelect<JAVA, DB>,
    vararg more: SqSingleColSelect<JAVA, DB>,
): SqSingleColUnion<JAVA, DB> = this.union(unionAll = false, first, second, *more)
fun <JAVA: Any?, DB: Any> SqContext.unionAll(selects: List<SqSingleColSelect<JAVA, DB>>): SqSingleColUnion<JAVA, DB> =
    this.union(unionAll = true, selects)
fun <JAVA: Any?, DB: Any> SqContext.unionAll(
    first: SqSingleColSelect<JAVA, DB>,
    second: SqSingleColSelect<JAVA, DB>,
    vararg more: SqSingleColSelect<JAVA, DB>,
): SqSingleColUnion<JAVA, DB> = this.union(unionAll = true, first, second, *more)


fun SqContext.ConnContext.select(distinct: Boolean, columns: List<SqColumn<*, *>>): SqConnMultiColSelect {
    return this[SqConnMultiColSelectConstructor::class.java, SqGenericConnMultiColSelect.CONSTRUCTOR]
        .createConnMultiColSelect(this, distinct, columns)
}
fun SqContext.ConnContext.select(distinct: Boolean, first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqConnMultiColSelect =
    this.select(distinct, listOf(first, second, *more))
fun SqContext.ConnContext.select(columns: List<SqColumn<*, *>>): SqConnMultiColSelect = this.select(distinct = false, columns)
fun SqContext.ConnContext.select(first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqConnMultiColSelect =
    this.select(distinct = false, first, second, *more)
fun SqContext.ConnContext.selectDistinct(columns: List<SqColumn<*, *>>): SqConnMultiColSelect = this.select(distinct = true, columns)
fun SqContext.ConnContext.selectDistinct(first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqConnMultiColSelect =
    this.select(distinct = true, first, second, *more)

fun <JAVA: Any?, DB: Any> SqContext.ConnContext.select(distinct: Boolean, column: SqColumn<JAVA, DB>): SqConnSingleColSelect<JAVA, DB> {
    return this[SqConnSingleColSelectConstructor::class.java, SqGenericConnSingleColSelect.CONSTRUCTOR]
        .createConnSingleColSelect(this, distinct, column)
}
fun <JAVA: Any?, DB: Any> SqContext.ConnContext.select(column: SqColumn<JAVA, DB>): SqConnSingleColSelect<JAVA, DB> = this.select(distinct = false, column)
fun <JAVA: Any?, DB: Any> SqContext.ConnContext.selectDistinct(column: SqColumn<JAVA, DB>): SqConnSingleColSelect<JAVA, DB> = this.select(distinct = true, column)


fun SqContext.ConnContext.union(unionAll: Boolean, selects: List<SqSelect>): SqConnMultiColUnion {
    return this[SqConnMultiColUnionConstructor::class.java, SqGenericConnMultiColUnion.CONSTRUCTOR]
        .createConnMultiColUnion(this, unionAll, selects)
}
fun SqContext.ConnContext.union(unionAll: Boolean, first: SqSelect, second: SqSelect, vararg more: SqSelect): SqConnMultiColUnion = this.union(unionAll, listOf(first, second, *more))
fun SqContext.ConnContext.union(selects: List<SqSelect>): SqConnMultiColUnion = this.union(unionAll = false, selects)
fun SqContext.ConnContext.union(first: SqSelect, second: SqSelect, vararg more: SqSelect): SqConnMultiColUnion = this.union(unionAll = false, first, second, *more)
fun SqContext.ConnContext.unionAll(selects: List<SqSelect>): SqConnMultiColUnion = this.union(unionAll = true, selects)
fun SqContext.ConnContext.unionAll(first: SqSelect, second: SqSelect, vararg more: SqSelect): SqConnMultiColUnion = this.union(unionAll = true, first, second, *more)

fun <JAVA: Any?, DB: Any> SqContext.ConnContext.union(unionAll: Boolean, selects: List<SqSingleColSelect<JAVA, DB>>): SqConnSingleColUnion<JAVA, DB> {
    return this[SqConnSingleColUnionConstructor::class.java, SqGenericConnSingleColUnion.CONSTRUCTOR]
        .createConnSingleColUnion(this, unionAll, selects)
}
fun <JAVA: Any?, DB: Any> SqContext.ConnContext.union(
    unionAll: Boolean,
    first: SqSingleColSelect<JAVA, DB>,
    second: SqSingleColSelect<JAVA, DB>,
    vararg more: SqSingleColSelect<JAVA, DB>,
): SqConnSingleColUnion<JAVA, DB> = this.union(unionAll, listOf(first, second, *more))
fun <JAVA: Any?, DB: Any> SqContext.ConnContext.union(selects: List<SqSingleColSelect<JAVA, DB>>): SqConnSingleColUnion<JAVA, DB> =
    this.union(unionAll = false, selects)
fun <JAVA: Any?, DB: Any> SqContext.ConnContext.union(
    first: SqSingleColSelect<JAVA, DB>,
    second: SqSingleColSelect<JAVA, DB>,
    vararg more: SqSingleColSelect<JAVA, DB>,
): SqConnSingleColUnion<JAVA, DB> = this.union(unionAll = false, first, second, *more)
fun <JAVA: Any?, DB: Any> SqContext.ConnContext.unionAll(selects: List<SqSingleColSelect<JAVA, DB>>): SqConnSingleColUnion<JAVA, DB> =
    this.union(unionAll = true, selects)
fun <JAVA: Any?, DB: Any> SqContext.ConnContext.unionAll(
    first: SqSingleColSelect<JAVA, DB>,
    second: SqSingleColSelect<JAVA, DB>,
    vararg more: SqSingleColSelect<JAVA, DB>,
): SqConnSingleColUnion<JAVA, DB> = this.union(unionAll = true, first, second, *more)
// endregion


// region Statements - modification
fun <T: SqTable> SqContext.insertInto(table: T): SqInsert<T> {
    return this[SqInsertConstructor::class.java, SqGenericInsert.CONSTRUCTOR]
        .createInsert(this, table)
}

fun <T : SqTable> SqContext.ConnContext.insertInto(table: T): SqConnInsert<T> {
    return this[SqConnInsertConstructor::class.java, SqGenericConnInsert.CONSTRUCTOR]
        .createConnInsert(this, table)
}

fun <T: SqInsert<*>> T.columns(columns: List<SqTableColumn<*, *>>?): T = this.apply { this.columns = columns }
fun <T: SqInsert<*>> T.columns(first: SqTableColumn<*, *>, vararg more: SqTableColumn<*, *>): T = this.columns(listOf(first, *more))
fun <T: SqInsert<*>> T.values(values: List<SqExpression<*, *>>?): T = this.apply { this.values = values }
fun <T: SqInsert<*>> T.values(first: SqExpression<*, *>, vararg more: SqExpression<*, *>): T = this.values(listOf(first, *more))
fun <T: SqInsert<*>> T.select(select: SqReadStatement?): T = this.apply { this.select = select }


fun <T: SqTable> SqContext.update(table: T): SqUpdate<T> {
    return this[SqUpdateConstructor::class.java, SqGenericUpdate.CONSTRUCTOR]
        .createUpdate(this, table)
}

fun <T : SqTable> SqContext.ConnContext.update(table: T): SqConnUpdate<T> {
    return this[SqConnUpdateConstructor::class.java, SqGenericConnUpdate.CONSTRUCTOR]
        .createConnUpdate(this, table)
}

fun <T: SqUpdate<*>> T.set(columnValueMap: Map<SqTableColumn<*, *>, SqExpression<*, *>>): T = this.apply { this.set = columnValueMap }
fun <T: SqUpdate<*>> T.set(vararg columnValuePairs: Pair<SqTableColumn<*, *>, SqExpression<*, *>>): T = this.set(mapOf(*columnValuePairs))
fun <T: SqUpdate<*>> T.where(where: SqExpression<*, Boolean>?): T = this.apply { this.where = where }


fun <T: SqTable> SqContext.deleteFrom(table: T): SqDelete<T> {
    return this[SqDeleteConstructor::class.java, SqGenericDelete.CONSTRUCTOR]
        .createDelete(this, table)
}

fun <T : SqTable> SqContext.ConnContext.deleteFrom(table: T): SqConnDelete<T> {
    return this[SqConnDeleteConstructor::class.java, SqGenericConnDelete.CONSTRUCTOR]
        .createConnDelete(this, table)
}

fun <T: SqDelete<*>> T.where(where: SqExpression<*, Boolean>?): T = this.apply { this.where = where }
// endregion


// region Tables
fun <JAVA: Any?, DB: Any> SqTable.column(
    type: SqType<JAVA, DB>,
    columnName: String,
    safeColumnName: String = SqUtil.makeIdentifierSafeIfNeeded(columnName)
): SqTableColumn<JAVA, DB> {
    return this.addColumn(SqGenericTableColumn(this, type, columnName, safeColumnName))
}
// endregion
