@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.*
import io.github.ore.sq.util.SqItemPartConfig
import io.github.ore.sq.util.bracketsAreOptional


// region Reader pair
interface SqBooleanOperationReaderPair {
    val notNullReader: SqDataTypeReader<Boolean, Boolean>
    val nullableReader: SqDataTypeReader<Boolean?, Boolean>
}

fun <T: SqSettingsBuilder> T.booleanOperationReaderPair(value: SqBooleanOperationReaderPair?): T =
    this.setValue(SqBooleanOperationReaderPair::class.java, value)
val SqSettings.booleanOperationReaderPair: SqBooleanOperationReaderPair
    get() = this.getValue(SqBooleanOperationReaderPair::class.java) ?: SqBooleanOperationReaderPairImpl.INSTANCE
// endregion


// region Boolean group (AND, OR)
interface SqBooleanGroup<JAVA: Boolean?>: SqExpression<JAVA, Boolean> {
    val items: List<SqItem>
    val operationKeyword: String

    override val isMultiline: Boolean
        get() {
            val items = this.items
            return when (items.size) {
                0 -> false
                1 -> items.first().isMultiline
                else -> true
            }
        }

    fun addToBuilder(context: SqContext, target: SqJdbcRequestDataBuilder) {
        this.items.forEachIndexed { index, item ->
            if (index > 0) {
                target.spaceOrNewLine().keyword(this.operationKeyword).spaceOrNewLine()
            }
            item.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)
        }
    }

    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        if (partConfig.bracketsAreOptional()) {
            this.addToBuilder(context, target)
        } else {
            if (this.items.size <= 1) {
                target.brackets {
                    this.addToBuilder(context, target)
                }
            } else {
                target.bracketsWithIndent {
                    this.addToBuilder(context, target)
                }
            }
        }
    }
}

interface SqBooleanGroupFactory {
    fun notNullReader(context: SqContext): SqDataTypeReader<Boolean, Boolean> =
        context.settings.booleanOperationReaderPair.notNullReader
    fun nullableReader(context: SqContext): SqDataTypeReader<Boolean?, Boolean> =
        context.settings.booleanOperationReaderPair.nullableReader

    fun <JAVA: Boolean?> createAnd(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Boolean>,
        items: List<SqItem>,
    ): SqBooleanGroup<JAVA>

    fun <JAVA: Boolean?> createOr(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Boolean>,
        items: List<SqItem>,
    ): SqBooleanGroup<JAVA>
}

fun <T: SqSettingsBuilder> T.booleanGroupFactory(value: SqBooleanGroupFactory?): T =
    this.setValue(SqBooleanGroupFactory::class.java, value)
val SqSettings.booleanGroupFactory: SqBooleanGroupFactory
    get() = this.getValue(SqBooleanGroupFactory::class.java) ?: SqBooleanGroupImpl.Factory.INSTANCE


@JvmName("and__notNull")
fun SqContext.and(items: List<SqExpression<out Any, Boolean>>): SqBooleanGroup<Boolean> {
    val factory = this.settings.booleanGroupFactory
    return factory.createAnd(this, factory.notNullReader(this), items)
}

@JvmName("and__notNull")
fun SqContext.and(item: SqExpression<out Any, Boolean>, vararg moreItems: SqExpression<out Any, Boolean>): SqBooleanGroup<Boolean> =
    this.and(listOf(item, *moreItems))

@JvmName("and__nullable")
fun SqContext.and(items: List<SqExpression<out Any?, Boolean>>): SqBooleanGroup<Boolean?> {
    val factory = this.settings.booleanGroupFactory
    return factory.createAnd(this, factory.nullableReader(this), items)
}

@JvmName("and__nullable")
fun SqContext.and(item: SqExpression<out Any?, Boolean>, vararg moreItems: SqExpression<out Any?, Boolean>): SqBooleanGroup<Boolean?> =
    this.and(listOf(item, *moreItems))

@JvmName("or__notNull")
fun SqContext.or(items: List<SqExpression<out Any, Boolean>>): SqBooleanGroup<Boolean> {
    val factory = this.settings.booleanGroupFactory
    return factory.createOr(this, factory.notNullReader(this), items)
}

@JvmName("or__notNull")
fun SqContext.or(item: SqExpression<out Any, Boolean>, vararg moreItems: SqExpression<out Any, Boolean>): SqBooleanGroup<Boolean> =
    this.or(listOf(item, *moreItems))

@JvmName("or__nullable")
fun SqContext.or(items: List<SqExpression<out Any?, Boolean>>): SqBooleanGroup<Boolean?> {
    val factory = this.settings.booleanGroupFactory
    return factory.createOr(this, factory.nullableReader(this), items)
}

@JvmName("or__nullable")
fun SqContext.or(item: SqExpression<out Any?, Boolean>, vararg moreItems: SqExpression<out Any?, Boolean>): SqBooleanGroup<Boolean?> =
    this.or(listOf(item, *moreItems))
// endregion


// region IS NULL, IS NOT NULL
interface SqIsNullTest: SqExpression<Boolean, Boolean> {
    val testedItem: SqItem
    val negative: Boolean

    override val isMultiline: Boolean
        get() = this.testedItem.isMultiline

    fun addToBuilder(context: SqContext, target: SqJdbcRequestDataBuilder) {
        this.testedItem.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)
        target.space()
        if (this.negative) {
            target.keyword("is not null")
        } else {
            target.keyword("is null")
        }
    }

    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        if (partConfig.bracketsAreOptional()) {
            this.addToBuilder(context, target)
        } else if (this.testedItem.isMultiline) {
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

fun interface SqIsNullTestFactory {
    operator fun invoke(context: SqContext, testedItem: SqItem, negative: Boolean): SqIsNullTest
}

fun <T: SqSettingsBuilder> T.isNullTestFactory(value: SqIsNullTestFactory?): T =
    this.setValue(SqIsNullTestFactory::class.java, value)
val SqSettings.isNullTestFactory: SqIsNullTestFactory
    get() = this.getValue(SqIsNullTestFactory::class.java) ?: SqIsNullTestImpl.Factory.INSTANCE


fun SqExpression<*, *>.isNull(context: SqContext = SqContext.last): SqIsNullTest =
    context.settings.isNullTestFactory.invoke(context, this, negative = false)
fun SqExpression<*, *>.isNotNull(context: SqContext = SqContext.last): SqIsNullTest =
    context.settings.isNullTestFactory.invoke(context, this, negative = true)
// endregion


// region NOT
interface SqNot<JAVA: Boolean?>: SqExpression<JAVA, Boolean> {
    val testedItem: SqItem

    override val isMultiline: Boolean
        get() = this.testedItem.isMultiline

    fun addToBuilder(context: SqContext, target: SqJdbcRequestDataBuilder) {
        target.keyword("not").space()
        this.testedItem.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)
    }

    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        if (partConfig.bracketsAreOptional()) {
            this.addToBuilder(context, target)
        } else if (this.testedItem.isMultiline) {
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

interface SqNotFactory {
    fun notNullReader(context: SqContext): SqDataTypeReader<Boolean, Boolean> =
        context.settings.booleanOperationReaderPair.notNullReader
    fun nullableReader(context: SqContext): SqDataTypeReader<Boolean?, Boolean> =
        context.settings.booleanOperationReaderPair.nullableReader

    fun <JAVA: Boolean?> create(context: SqContext, reader: SqDataTypeReader<JAVA, Boolean>, testedItem: SqItem): SqNot<JAVA>
}

fun <T: SqSettingsBuilder> T.notFactory(value: SqNotFactory?): T =
    this.setValue(SqNotFactory::class.java, value)
val SqSettings.notFactory: SqNotFactory
    get() = this.getValue(SqNotFactory::class.java) ?: SqNotImpl.Factory.INSTANCE


@JvmName("not__notNull")
fun SqContext.not(testedItem: SqExpression<out Any, Boolean>): SqNot<Boolean> {
    val factory = this.settings.notFactory
    return factory.create(this, factory.notNullReader(this), testedItem)
}

@JvmName("not__nullable")
fun SqContext.not(testedItem: SqExpression<out Any?, Boolean>): SqNot<Boolean?> {
    val factory = this.settings.notFactory
    return factory.create(this, factory.nullableReader(this), testedItem)
}
// endregion


// region Comparison of two values
interface SqTwoValuesComparison<JAVA: Boolean?>: SqExpression<JAVA, Boolean> {
    val leftItem: SqItem
    val operationKeyword: String
    val rightItem: SqItem

    override val isMultiline: Boolean
        get() = (this.leftItem.isMultiline) || (this.rightItem.isMultiline)

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

    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        if (partConfig.bracketsAreOptional()) {
            this.addToBuilder(context, target)
        } else if ((this.leftItem.isMultiline) || (this.rightItem.isMultiline)) {
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

interface SqTwoValuesComparisonFactory {
    fun notNullReader(context: SqContext): SqDataTypeReader<Boolean, Boolean> =
        context.settings.booleanOperationReaderPair.notNullReader
    fun nullableReader(context: SqContext): SqDataTypeReader<Boolean?, Boolean> =
        context.settings.booleanOperationReaderPair.nullableReader

    fun <JAVA: Boolean?> createEq(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Boolean>,
        leftItem: SqItem,
        rightItem: SqItem,
    ): SqTwoValuesComparison<JAVA>

    fun <JAVA: Boolean?> createNeq(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Boolean>,
        leftItem: SqItem,
        rightItem: SqItem,
    ): SqTwoValuesComparison<JAVA>

    fun <JAVA: Boolean?> createGt(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Boolean>,
        leftItem: SqItem,
        rightItem: SqItem,
    ): SqTwoValuesComparison<JAVA>

    fun <JAVA: Boolean?> createGte(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Boolean>,
        leftItem: SqItem,
        rightItem: SqItem,
    ): SqTwoValuesComparison<JAVA>

    fun <JAVA: Boolean?> createLt(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Boolean>,
        leftItem: SqItem,
        rightItem: SqItem,
    ): SqTwoValuesComparison<JAVA>

    fun <JAVA: Boolean?> createLte(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Boolean>,
        leftItem: SqItem,
        rightItem: SqItem,
    ): SqTwoValuesComparison<JAVA>

    fun <JAVA: Boolean?> createLike(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Boolean>,
        leftItem: SqItem,
        rightItem: SqItem,
    ): SqTwoValuesComparison<JAVA>

    fun <JAVA: Boolean?> createNotLike(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Boolean>,
        leftItem: SqItem,
        rightItem: SqItem,
    ): SqTwoValuesComparison<JAVA>
}

fun <T: SqSettingsBuilder> T.twoValuesComparisonFactory(value: SqTwoValuesComparisonFactory?): T =
    this.setValue(SqTwoValuesComparisonFactory::class.java, value)
val SqSettings.twoValuesComparisonFactory: SqTwoValuesComparisonFactory
    get() = this.getValue(SqTwoValuesComparisonFactory::class.java) ?: SqTwoValuesComparisonImpl.Factory.INSTANCE


@JvmName("eq__notNull")
fun <DB: Any> SqExpression<out Any, DB>.eq(other: SqExpression<out Any, DB>, context: SqContext): SqTwoValuesComparison<Boolean> {
    val factory = context.settings.twoValuesComparisonFactory
    return factory.createEq(context, factory.notNullReader(context), this, other)
}

@JvmName("eq__notNull")
infix fun <DB: Any> SqExpression<out Any, DB>.eq(other: SqExpression<out Any, DB>): SqTwoValuesComparison<Boolean> =
    this.eq(other, SqContext.last)

@JvmName("eq__nullable")
fun <DB: Any> SqExpression<out Any?, DB>.eq(other: SqExpression<out Any?, DB>, context: SqContext): SqTwoValuesComparison<Boolean?> {
    val factory = context.settings.twoValuesComparisonFactory
    return factory.createEq(context, factory.nullableReader(context), this, other)
}

@JvmName("eq__nullable")
infix fun <DB: Any> SqExpression<out Any?, DB>.eq(other: SqExpression<out Any?, DB>): SqTwoValuesComparison<Boolean?> =
    this.eq(other, SqContext.last)

@JvmName("neq__notNull")
fun <DB: Any> SqExpression<out Any, DB>.neq(other: SqExpression<out Any, DB>, context: SqContext): SqTwoValuesComparison<Boolean> {
    val factory = context.settings.twoValuesComparisonFactory
    return factory.createNeq(context, factory.notNullReader(context), this, other)
}

@JvmName("neq__notNull")
infix fun <DB: Any> SqExpression<out Any, DB>.neq(other: SqExpression<out Any, DB>): SqTwoValuesComparison<Boolean> =
    this.neq(other, SqContext.last)

@JvmName("neq__nullable")
fun <DB: Any> SqExpression<out Any?, DB>.neq(other: SqExpression<out Any?, DB>, context: SqContext): SqTwoValuesComparison<Boolean?> {
    val factory = context.settings.twoValuesComparisonFactory
    return factory.createNeq(context, factory.nullableReader(context), this, other)
}

@JvmName("neq__nullable")
infix fun <DB: Any> SqExpression<out Any?, DB>.neq(other: SqExpression<out Any?, DB>): SqTwoValuesComparison<Boolean?> =
    this.neq(other, SqContext.last)

@JvmName("gt__notNull")
fun <DB: Any> SqExpression<out Any, DB>.gt(other: SqExpression<out Any, DB>, context: SqContext): SqTwoValuesComparison<Boolean> {
    val factory = context.settings.twoValuesComparisonFactory
    return factory.createGt(context, factory.notNullReader(context), this, other)
}

@JvmName("gt__notNull")
infix fun <DB: Any> SqExpression<out Any, DB>.gt(other: SqExpression<out Any, DB>): SqTwoValuesComparison<Boolean> =
    this.gt(other, SqContext.last)

@JvmName("gt__nullable")
fun <DB: Any> SqExpression<out Any?, DB>.gt(other: SqExpression<out Any?, DB>, context: SqContext): SqTwoValuesComparison<Boolean?> {
    val factory = context.settings.twoValuesComparisonFactory
    return factory.createGt(context, factory.nullableReader(context), this, other)
}

@JvmName("gt__nullable")
infix fun <DB: Any> SqExpression<out Any?, DB>.gt(other: SqExpression<out Any?, DB>): SqTwoValuesComparison<Boolean?> =
    this.gt(other, SqContext.last)

@JvmName("gte__notNull")
fun <DB: Any> SqExpression<out Any, DB>.gte(other: SqExpression<out Any, DB>, context: SqContext): SqTwoValuesComparison<Boolean> {
    val factory = context.settings.twoValuesComparisonFactory
    return factory.createGte(context, factory.notNullReader(context), this, other)
}

@JvmName("gte__notNull")
infix fun <DB: Any> SqExpression<out Any, DB>.gte(other: SqExpression<out Any, DB>): SqTwoValuesComparison<Boolean> =
    this.gte(other, SqContext.last)

@JvmName("gte__nullable")
fun <DB: Any> SqExpression<out Any?, DB>.gte(other: SqExpression<out Any?, DB>, context: SqContext): SqTwoValuesComparison<Boolean?> {
    val factory = context.settings.twoValuesComparisonFactory
    return factory.createGte(context, factory.nullableReader(context), this, other)
}

@JvmName("gte__nullable")
infix fun <DB: Any> SqExpression<out Any?, DB>.gte(other: SqExpression<out Any?, DB>): SqTwoValuesComparison<Boolean?> =
    this.gte(other, SqContext.last)

@JvmName("lt__notNull")
fun <DB: Any> SqExpression<out Any, DB>.lt(other: SqExpression<out Any, DB>, context: SqContext): SqTwoValuesComparison<Boolean> {
    val factory = context.settings.twoValuesComparisonFactory
    return factory.createLt(context, factory.notNullReader(context), this, other)
}

@JvmName("lt__notNull")
infix fun <DB: Any> SqExpression<out Any, DB>.lt(other: SqExpression<out Any, DB>): SqTwoValuesComparison<Boolean> =
    this.lt(other, SqContext.last)

@JvmName("lt__nullable")
fun <DB: Any> SqExpression<out Any?, DB>.lt(other: SqExpression<out Any?, DB>, context: SqContext): SqTwoValuesComparison<Boolean?> {
    val factory = context.settings.twoValuesComparisonFactory
    return factory.createLt(context, factory.nullableReader(context), this, other)
}

@JvmName("lt__nullable")
infix fun <DB: Any> SqExpression<out Any?, DB>.lt(other: SqExpression<out Any?, DB>): SqTwoValuesComparison<Boolean?> =
    this.lt(other, SqContext.last)

@JvmName("lte__notNull")
fun <DB: Any> SqExpression<out Any, DB>.lte(other: SqExpression<out Any, DB>, context: SqContext): SqTwoValuesComparison<Boolean> {
    val factory = context.settings.twoValuesComparisonFactory
    return factory.createLte(context, factory.notNullReader(context), this, other)
}

@JvmName("lte__notNull")
infix fun <DB: Any> SqExpression<out Any, DB>.lte(other: SqExpression<out Any, DB>): SqTwoValuesComparison<Boolean> =
    this.lte(other, SqContext.last)

@JvmName("lte__nullable")
fun <DB: Any> SqExpression<out Any?, DB>.lte(other: SqExpression<out Any?, DB>, context: SqContext): SqTwoValuesComparison<Boolean?> {
    val factory = context.settings.twoValuesComparisonFactory
    return factory.createLte(context, factory.nullableReader(context), this, other)
}

@JvmName("lte__nullable")
infix fun <DB: Any> SqExpression<out Any?, DB>.lte(other: SqExpression<out Any?, DB>): SqTwoValuesComparison<Boolean?> =
    this.lte(other, SqContext.last)

@JvmName("like__notNull")
fun SqExpression<out Any, String>.like(other: SqExpression<out Any, String>, context: SqContext): SqTwoValuesComparison<Boolean> {
    val factory = context.settings.twoValuesComparisonFactory
    return factory.createLike(context, factory.notNullReader(context), this, other)
}

@JvmName("like__notNull")
infix fun SqExpression<out Any, String>.like(other: SqExpression<out Any, String>): SqTwoValuesComparison<Boolean> =
    this.like(other, SqContext.last)

@JvmName("like__nullable")
fun SqExpression<out Any?, String>.like(other: SqExpression<out Any?, String>, context: SqContext): SqTwoValuesComparison<Boolean?> {
    val factory = context.settings.twoValuesComparisonFactory
    return factory.createLike(context, factory.nullableReader(context), this, other)
}

@JvmName("like__nullable")
infix fun SqExpression<out Any?, String>.like(other: SqExpression<out Any?, String>): SqTwoValuesComparison<Boolean?> =
    this.like(other, SqContext.last)

@JvmName("notLike__notNull")
fun SqExpression<out Any, String>.notLike(other: SqExpression<out Any, String>, context: SqContext): SqTwoValuesComparison<Boolean> {
    val factory = context.settings.twoValuesComparisonFactory
    return factory.createNotLike(context, factory.notNullReader(context), this, other)
}

@JvmName("notLike__notNull")
infix fun SqExpression<out Any, String>.notLike(other: SqExpression<out Any, String>): SqTwoValuesComparison<Boolean> =
    this.notLike(other, SqContext.last)

@JvmName("notLike__nullable")
fun SqExpression<out Any?, String>.notLike(other: SqExpression<out Any?, String>, context: SqContext): SqTwoValuesComparison<Boolean?> {
    val factory = context.settings.twoValuesComparisonFactory
    return factory.createNotLike(context, factory.nullableReader(context), this, other)
}

@JvmName("notLike__nullable")
infix fun SqExpression<out Any?, String>.notLike(other: SqExpression<out Any?, String>): SqTwoValuesComparison<Boolean?> =
    this.notLike(other, SqContext.last)
// endregion


// region BETWEEN, NOT BETWEEN
interface SqBetween<JAVA: Boolean?>: SqExpression<JAVA, Boolean> {
    val testedItem: SqItem
    val negative: Boolean
    val rangeStart: SqItem
    val rangeEnd: SqItem

    override val isMultiline: Boolean
        get() = (this.testedItem.isMultiline) || (this.rangeStart.isMultiline) || (this.rangeEnd.isMultiline)

    fun addToBuilder(context: SqContext, target: SqJdbcRequestDataBuilder) {
        val multiline = this.isMultiline

        // Tested item
        this.testedItem.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)

        // Space
        if (multiline) {
            target.spaceOrNewLine()
        } else {
            target.space()
        }

        // [NOT] BETWEEN
        if (this.negative) {
            target.keyword("not between")
        } else {
            target.keyword("between")
        }

        // Space
        if (multiline) {
            target.spaceOrNewLine()
        } else {
            target.space()
        }

        // Range start
        this.rangeStart.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)

        // Space
        if (multiline) {
            target.spaceOrNewLine()
        } else {
            target.space()
        }

        // AND
        target.keyword("and")

        // Space
        if (multiline) {
            target.spaceOrNewLine()
        } else {
            target.space()
        }

        // Range end
        this.rangeEnd.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)
    }

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

interface SqBetweenFactory {
    fun notNullReader(context: SqContext): SqDataTypeReader<Boolean, Boolean> =
        context.settings.booleanOperationReaderPair.notNullReader
    fun nullableReader(context: SqContext): SqDataTypeReader<Boolean?, Boolean> =
        context.settings.booleanOperationReaderPair.nullableReader

    fun <JAVA: Boolean?> create(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Boolean>,
        testedItem: SqItem,
        negative: Boolean,
        rangeStart: SqItem,
        rangeEnd: SqItem,
    ): SqBetween<JAVA>
}

fun <T: SqSettingsBuilder> T.betweenFactory(value: SqBetweenFactory?): T =
    this.setValue(SqBetweenFactory::class.java, value)
val SqSettings.betweenFactory: SqBetweenFactory
    get() = this.getValue(SqBetweenFactory::class.java) ?: SqBetweenImpl.Factory.INSTANCE


@JvmName("between__notNull")
fun <DB: Any> SqExpression<out Any, DB>.between(
    rangeStart: SqExpression<out Any, DB>,
    rangeEnd: SqExpression<out Any, DB>,
    context: SqContext = SqContext.last,
): SqBetween<Boolean> {
    val factory = context.settings.betweenFactory
    return factory.create(context, factory.notNullReader(context), this, negative = false, rangeStart, rangeEnd)
}

@JvmName("between__nullable")
fun <DB: Any> SqExpression<out Any?, DB>.between(
    rangeStart: SqExpression<out Any?, DB>,
    rangeEnd: SqExpression<out Any?, DB>,
    context: SqContext = SqContext.last,
): SqBetween<Boolean?> {
    val factory = context.settings.betweenFactory
    return factory.create(context, factory.nullableReader(context), this, negative = false, rangeStart, rangeEnd)
}

@JvmName("notBetween__notNull")
fun <DB: Any> SqExpression<out Any, DB>.notBetween(
    rangeStart: SqExpression<out Any, DB>,
    rangeEnd: SqExpression<out Any, DB>,
    context: SqContext = SqContext.last,
): SqBetween<Boolean> {
    val factory = context.settings.betweenFactory
    return factory.create(context, factory.notNullReader(context), this, negative = true, rangeStart, rangeEnd)
}

@JvmName("notBetween__nullable")
fun <DB: Any> SqExpression<out Any?, DB>.notBetween(
    rangeStart: SqExpression<out Any?, DB>,
    rangeEnd: SqExpression<out Any?, DB>,
    context: SqContext = SqContext.last,
): SqBetween<Boolean?> {
    val factory = context.settings.betweenFactory
    return factory.create(context, factory.nullableReader(context), this, negative = true, rangeStart, rangeEnd)
}
// endregion


// region IN, NOT IN
interface SqIn<JAVA: Boolean?>: SqExpression<JAVA, Boolean> {
    val testedItem: SqItem
    val negative: Boolean
    val values: List<SqItem>

    override val isMultiline: Boolean
        get() {
            if (this.testedItem.isMultiline) {
                return true
            }

            val values = this.values
            return when (values.size) {
                0 -> false
                1 -> values[0].isMultiline
                else -> (values.any { it.isMultiline })
            }
        }

    @Suppress("DuplicatedCode")
    fun addItemsToBuilder(context: SqContext, target: SqJdbcRequestDataBuilder) {
        val multiline = this.isMultiline
        this.values.forEachIndexed { index, value ->
            if (index > 0) {
                target.comma()
                if (multiline) {
                    target.spaceOrNewLine()
                } else {
                    target.space()
                }
            }
            value.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)
        }
    }

    fun addToBuilder(context: SqContext, target: SqJdbcRequestDataBuilder) {
        val multiline = this.isMultiline

        // Tested item
        this.testedItem.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)

        // Space
        if (multiline) {
            target.spaceOrNewLine()
        } else {
            target.space()
        }

        // Operation keyword
        if (this.negative) {
            target.keyword("not in")
        } else {
            target.keyword("in")
        }

        // Space
        if (multiline) {
            target.spaceOrNewLine()
        } else {
            target.space()
        }

        // Values
        if (multiline) {
            target.bracketsWithIndent {
                this.addItemsToBuilder(context, target)
            }
        } else {
            target.brackets {
                this.addItemsToBuilder(context, target)
            }
        }
    }

    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?,
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

interface SqInFactory {
    fun notNullReader(context: SqContext): SqDataTypeReader<Boolean, Boolean> =
        context.settings.booleanOperationReaderPair.notNullReader
    fun nullableReader(context: SqContext): SqDataTypeReader<Boolean?, Boolean> =
        context.settings.booleanOperationReaderPair.nullableReader

    fun <JAVA: Boolean?> create(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Boolean>,
        testedItem: SqItem,
        negative: Boolean,
        values: List<SqItem>,
    ): SqIn<JAVA>
}

fun <T: SqSettingsBuilder> T.inFactory(value: SqInFactory?): T =
    this.setValue(SqInFactory::class.java, value)
val SqSettings.inFactory: SqInFactory
    get() = this.getValue(SqInFactory::class.java) ?: SqInImpl.Factory.INSTANCE


@JvmName("inList__notNull")
fun <DB: Any> SqExpression<out Any, DB>.inList(values: List<SqExpression<out Any, DB>>, context: SqContext): SqIn<Boolean> {
    val factory = context.settings.inFactory
    return factory.create(context, factory.notNullReader(context), this, negative = false, values)
}

@JvmName("inList__notNull")
infix fun <DB: Any> SqExpression<out Any, DB>.inList(values: List<SqExpression<out Any, DB>>): SqIn<Boolean> =
    this.inList(values, SqContext.last)

@JvmName("inList__notNull")
fun <DB: Any> SqExpression<out Any, DB>.inList(
    value: SqExpression<out Any, DB>,
    vararg moreValues: SqExpression<out Any, DB>,
    context: SqContext = SqContext.last,
): SqIn<Boolean> {
    return this.inList(listOf(value, *moreValues), context)
}

@JvmName("inList__nullable")
fun <DB: Any> SqExpression<out Any?, DB>.inList(values: List<SqExpression<out Any?, DB>>, context: SqContext): SqIn<Boolean?> {
    val factory = context.settings.inFactory
    return factory.create(context, factory.nullableReader(context), this, negative = false, values)
}

@JvmName("inList__nullable")
infix fun <DB: Any> SqExpression<out Any?, DB>.inList(values: List<SqExpression<out Any?, DB>>): SqIn<Boolean?> =
    this.inList(values, SqContext.last)

@JvmName("inList__nullable")
fun <DB: Any> SqExpression<out Any?, DB>.inList(
    value: SqExpression<out Any?, DB>,
    vararg moreValues: SqExpression<out Any?, DB>,
    context: SqContext = SqContext.last,
): SqIn<Boolean?> {
    return this.inList(listOf(value, *moreValues), context)
}

@JvmName("notInList__notNull")
fun <DB: Any> SqExpression<out Any, DB>.notInList(values: List<SqExpression<out Any, DB>>, context: SqContext): SqIn<Boolean> {
    val factory = context.settings.inFactory
    return factory.create(context, factory.notNullReader(context), this, negative = true, values)
}

@JvmName("notInList__notNull")
infix fun <DB: Any> SqExpression<out Any, DB>.notInList(values: List<SqExpression<out Any, DB>>): SqIn<Boolean> =
    this.notInList(values, SqContext.last)

@JvmName("notInList__notNull")
fun <DB: Any> SqExpression<out Any, DB>.notInList(
    value: SqExpression<out Any, DB>,
    vararg moreValues: SqExpression<out Any, DB>,
    context: SqContext = SqContext.last,
): SqIn<Boolean> {
    return this.notInList(listOf(value, *moreValues), context)
}

@JvmName("notInList__nullable")
fun <DB: Any> SqExpression<out Any?, DB>.notInList(values: List<SqExpression<out Any?, DB>>, context: SqContext): SqIn<Boolean?> {
    val factory = context.settings.inFactory
    return factory.create(context, factory.nullableReader(context), this, negative = true, values)
}

@JvmName("notInList__nullable")
infix fun <DB: Any> SqExpression<out Any?, DB>.notInList(values: List<SqExpression<out Any?, DB>>): SqIn<Boolean?> =
    this.notInList(values, SqContext.last)

@JvmName("notInList__nullable")
fun <DB: Any> SqExpression<out Any?, DB>.notInList(
    value: SqExpression<out Any?, DB>,
    vararg moreValues: SqExpression<out Any?, DB>,
    context: SqContext = SqContext.last,
): SqIn<Boolean?> {
    return this.notInList(listOf(value, *moreValues), context)
}
// endregion


// region EXISTS
interface SqExists: SqExpression<Boolean, Boolean> {
    val request: SqItem

    override val isMultiline: Boolean
        get() = this.request.isMultiline

    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        target.keyword("exists").space()
        this.request.let { request ->
            if (request.isMultiline) {
                target.bracketsWithIndent {
                    this.request.addToBuilder(context, target, null)
                }
            } else {
                target.brackets {
                    this.request.addToBuilder(context, target, null)
                }
            }
        }
    }
}

fun interface SqExistsFactory {
    operator fun invoke(context: SqContext, request: SqItem): SqExists
}

fun <T: SqSettingsBuilder> T.existsFactory(value: SqExistsFactory?): T =
    this.setValue(SqExistsFactory::class.java, value)
val SqSettings.existsFactory: SqExistsFactory
    get() = this.getValue(SqExistsFactory::class.java) ?: SqExistsImpl.Factory.INSTANCE


fun SqContext.exists(request: SqReadRequest): SqExists =
    this.settings.existsFactory.invoke(this, request)
// endregion
