@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqCaseBuilderImpl
import io.github.ore.sq.impl.SqCaseFactoryImpl
import io.github.ore.sq.util.SqItemPartConfig
import io.github.ore.sq.util.bracketsAreOptional
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


// region Main classes
interface SqCaseItem<JAVA, DB: Any> {
    val reader: SqDataTypeReader<JAVA, DB>
    val multiline: Boolean
    fun addToBuilder(context: SqContext, target: SqJdbcRequestDataBuilder, multiline: Boolean)
}

interface SqCaseWhenThen<JAVA, DB: Any>: SqCaseItem<JAVA, DB> {
    val condition: SqItem
    val result: SqItem

    override val multiline: Boolean
        get() = (this.condition.isMultiline || this.result.isMultiline)

    override fun addToBuilder(context: SqContext, target: SqJdbcRequestDataBuilder, multiline: Boolean) {
        target.keyword("when")

        if (multiline) {
            target.incrementIndent().spaceOrNewLine()
        } else {
            target.space()
        }

        this.condition.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)

        if (multiline) {
            target.spaceOrNewLine()
        } else {
            target.space()
        }

        target.keyword("then")

        if (multiline) {
            target.incrementIndent().spaceOrNewLine()
        } else {
            target.space()
        }

        this.result.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)

        if (multiline) {
            target.decrementIndent().decrementIndent()
        }
    }
}

interface SqCaseElse<JAVA, DB: Any>: SqCaseItem<JAVA, DB> {
    val result: SqItem

    override val multiline: Boolean
        get() = this.result.isMultiline

    override fun addToBuilder(context: SqContext, target: SqJdbcRequestDataBuilder, multiline: Boolean) {
        target.keyword("else")

        if (multiline) {
            target.indent {
                this.result.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)
            }
        } else {
            target.space()
            this.result.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)
        }
    }
}

interface SqCase<JAVA, DB: Any>: SqExpression<JAVA, DB> {
    val items: List<SqCaseItem<*, *>>

    override val isMultiline: Boolean
        get() = true

    fun addToBuilder(context: SqContext, target: SqJdbcRequestDataBuilder) {
        val items = this.items
        val multilineItems = items.any { it.multiline }
        target
            .keyword("case")
            .indent {
                items.forEachIndexed { index, item ->
                    if (index > 0) {
                        target.spaceOrNewLine()
                    }
                    item.addToBuilder(context, target, multilineItems)
                }
            }
            .keyword("end")
    }

    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        if (partConfig.bracketsAreOptional()) {
            this.addToBuilder(context, target)
        } else {
            target.bracketsWithIndent {
                this.addToBuilder(context, target)
            }
        }
    }
}

interface SqCaseFactory {
    fun <JAVA, DB: Any> createWhenThen(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, DB>,
        condition: SqItem,
        result: SqItem,
    ): SqCaseWhenThen<JAVA, DB>

    fun <JAVA, DB: Any> createElse(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, DB>,
        result: SqItem,
    ): SqCaseElse<JAVA, DB>

    fun <JAVA, DB: Any> createCase(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, DB>,
        items: List<SqCaseItem<*, *>>,
    ): SqCase<JAVA, DB>
}

fun <T: SqSettingsBuilder> T.caseFactory(value: SqCaseFactory?): T =
    this.setValue(SqCaseFactory::class.java, value)
val SqSettings.caseFactory: SqCaseFactory
    get() = this.getValue(SqCaseFactory::class.java) ?: SqCaseFactoryImpl.INSTANCE


fun <JAVA, DB: Any> SqContext.caseWhenThen(condition: SqExpression<*, Boolean>, result: SqExpression<JAVA, DB>): SqCaseWhenThen<JAVA, DB> =
    this.settings.caseFactory.createWhenThen(this, result.reader, condition, result)

fun <JAVA, DB: Any> SqContext.caseElse(result: SqExpression<JAVA, DB>): SqCaseElse<JAVA, DB> =
    this.settings.caseFactory.createElse(this, result.reader, result)

fun <JAVA, DB: Any> SqContext.case(
    reader: SqDataTypeReader<JAVA, DB>,
    whenThenItems: List<SqCaseWhenThen<*, *>>,
    elseItem: SqCaseElse<*, *>? = null,
): SqCase<JAVA, DB> {
    val items = if (elseItem == null) {
        whenThenItems as List<SqCaseItem<*, *>>
    } else {
        buildList(whenThenItems.size + 1) {
            this.addAll(whenThenItems)
            this.add(elseItem)
        }
    }

    return this.settings.caseFactory.createCase(this, reader, items)
}

fun <JAVA, DB: Any> SqContext.case(
    reader: SqDataTypeReader<JAVA, DB>,
    vararg whenThenItems: SqCaseWhenThen<*, *>,
    elseItem: SqCaseElse<*, *>? = null,
): SqCase<JAVA, DB> {
    return this.case(reader, listOf(*whenThenItems), elseItem)
}

fun <JAVA, DB: Any> SqContext.case(
    whenThenItems: List<SqCaseWhenThen<JAVA, DB>>,
    elseItem: SqCaseElse<JAVA, DB>,
): SqCase<JAVA, DB> {
    return this.case(elseItem.reader, whenThenItems, elseItem)
}

fun <JAVA, DB: Any> SqContext.case(
    vararg whenThenItems: SqCaseWhenThen<JAVA, DB>,
    elseItem: SqCaseElse<JAVA, DB>,
): SqCase<JAVA, DB> {
    return this.case(listOf(*whenThenItems), elseItem)
}

fun <JAVA, DB: Any> SqContext.case(
    whenThenItems: List<SqCaseWhenThen<JAVA, DB>>,
): SqCase<JAVA?, DB> {
    if (whenThenItems.isEmpty()) {
        error("List of \"WHEN...THEN...\" items is empty")
    }
    val reader = whenThenItems.first().reader.nullableReader
    return this.case(reader, whenThenItems, null)
}

fun <JAVA, DB: Any> SqContext.case(
    vararg whenThenItems: SqCaseWhenThen<JAVA, DB>,
): SqCase<JAVA?, DB> {
    return this.case(listOf(*whenThenItems))
}
// endregion


// region Builder
interface SqCaseEmptyBuilder {
    fun start(condition: SqItem): SqCaseEmptyBuilderWhenItem
}

interface SqCaseBuilder<JAVA, DB: Any> {
    val reader: SqDataTypeReader<JAVA, DB>

    fun end(context: SqContext): SqCase<JAVA, DB>
}

interface SqCaseIncompleteBuilder<JAVA, DB: Any>: SqCaseBuilder<JAVA?, DB> {
    fun start(condition: SqItem): SqCaseBuilderWhenItem<JAVA, DB>

    fun <JAVA_NEW, DB_NEW: Any> complete(
        reader: SqDataTypeReader<JAVA_NEW, DB_NEW>,
        elseItemResult: SqItem,
    ): SqCaseCompleteBuilder<JAVA_NEW, DB_NEW>
}

interface SqCaseCompleteBuilder<JAVA, DB: Any>: SqCaseBuilder<JAVA, DB>

interface SqCaseEmptyBuilderWhenItem {
    fun <JAVA, DB: Any> end(reader: SqDataTypeReader<JAVA?, DB>, result: SqItem): SqCaseIncompleteBuilder<JAVA, DB>
}

interface SqCaseBuilderWhenItem<JAVA, DB: Any> {
    val reader: SqDataTypeReader<JAVA?, DB>

    fun <JAVA_NEW, DB_NEW: Any> end(
        reader: SqDataTypeReader<JAVA_NEW?, DB_NEW>,
        result: SqItem,
    ): SqCaseIncompleteBuilder<JAVA_NEW, DB_NEW>
}

fun interface SqCaseBuilderFactory {
    operator fun invoke(context: SqContext): SqCaseEmptyBuilder
}

fun <T: SqSettingsBuilder> T.caseBuilderFactory(value: SqCaseBuilderFactory?): T =
    this.setValue(SqCaseBuilderFactory::class.java, value)
val SqSettings.caseBuilderFactory: SqCaseBuilderFactory
    get() = this.getValue(SqCaseBuilderFactory::class.java) ?: SqCaseBuilderImpl.Factory.INSTANCE


inline fun <JAVA, DB: Any> SqContext.case(block: (builder: SqCaseEmptyBuilder) -> SqCaseBuilder<JAVA, DB>): SqCase<JAVA, DB> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return block(this.settings.caseBuilderFactory.invoke(this)).end(this)
}


infix fun SqCaseEmptyBuilder.addWhen(condition: SqExpression<*, Boolean>): SqCaseEmptyBuilderWhenItem =
    this.start(condition)
infix fun SqCaseEmptyBuilder.`when`(condition: SqExpression<*, Boolean>): SqCaseEmptyBuilderWhenItem =
    this.addWhen(condition)

infix fun <JAVA, DB: Any> SqCaseEmptyBuilderWhenItem.then(result: SqExpression<JAVA, DB>): SqCaseIncompleteBuilder<JAVA, DB> =
    this.end(result.reader.nullableReader, result)

infix fun <JAVA, DB: Any> SqCaseIncompleteBuilder<JAVA, DB>.addWhen(condition: SqExpression<*, Boolean>): SqCaseBuilderWhenItem<JAVA, DB> =
    this.start(condition)
infix fun <JAVA, DB: Any> SqCaseIncompleteBuilder<JAVA, DB>.`when`(condition: SqExpression<*, Boolean>): SqCaseBuilderWhenItem<JAVA, DB> =
    this.addWhen(condition)

@JvmName("whenThen__notNull")
fun <JAVA: Any, DB: Any> SqCaseEmptyBuilder.whenThen(condition: SqExpression<*, Boolean>, result: SqExpression<JAVA, DB>): SqCaseIncompleteBuilder<JAVA, DB> =
    this.addWhen(condition).then(result)
@JvmName("whenThen__nullable")
fun <JAVA: Any, DB: Any> SqCaseEmptyBuilder.whenThen(condition: SqExpression<*, Boolean>, result: SqExpression<JAVA?, DB>): SqCaseIncompleteBuilder<JAVA?, DB> =
    this.addWhen(condition).then(result)

@JvmName("then__notNull")
fun <JAVA: Any, DB: Any> SqCaseBuilderWhenItem<JAVA, DB>.then(result: SqExpression<JAVA, DB>): SqCaseIncompleteBuilder<JAVA, DB> =
    this.end(this.reader, result)
@JvmName("then__leftNullable_rightNotNull")
fun <JAVA: Any, DB: Any> SqCaseBuilderWhenItem<JAVA?, DB>.then(result: SqExpression<JAVA, DB>): SqCaseIncompleteBuilder<JAVA?, DB> =
    this.end(this.reader, result)
@JvmName("then__leftNotNull_rightNullable")
fun <JAVA: Any, DB: Any> SqCaseBuilderWhenItem<JAVA, DB>.then(result: SqExpression<JAVA?, DB>): SqCaseIncompleteBuilder<JAVA?, DB> =
    this.end(this.reader, result)
@JvmName("then__nullable")
fun <JAVA: Any, DB: Any> SqCaseBuilderWhenItem<JAVA?, DB>.then(result: SqExpression<JAVA?, DB>): SqCaseIncompleteBuilder<JAVA?, DB> =
    this.end(this.reader, result)

@JvmName("whenThen__notNull")
fun <JAVA: Any, DB: Any> SqCaseIncompleteBuilder<JAVA, DB>.whenThen(condition: SqExpression<*, Boolean>, result: SqExpression<JAVA, DB>): SqCaseIncompleteBuilder<JAVA, DB> =
    this.addWhen(condition).then(result)
@JvmName("whenThen__leftNullable_rightNotNull")
fun <JAVA: Any, DB: Any> SqCaseIncompleteBuilder<JAVA?, DB>.whenThen(condition: SqExpression<*, Boolean>, result: SqExpression<JAVA, DB>): SqCaseIncompleteBuilder<JAVA?, DB> =
    this.addWhen(condition).then(result)
@JvmName("whenThen__leftNotNull_rightNullable")
fun <JAVA: Any, DB: Any> SqCaseIncompleteBuilder<JAVA, DB>.whenThen(condition: SqExpression<*, Boolean>, result: SqExpression<JAVA?, DB>): SqCaseIncompleteBuilder<JAVA?, DB> =
    this.addWhen(condition).then(result)
@JvmName("whenThen__nullable")
fun <JAVA: Any, DB: Any> SqCaseIncompleteBuilder<JAVA?, DB>.whenThen(condition: SqExpression<*, Boolean>, result: SqExpression<JAVA?, DB>): SqCaseIncompleteBuilder<JAVA?, DB> =
    this.addWhen(condition).then(result)

@JvmName("addElse__notNull")
fun <JAVA: Any, DB: Any> SqCaseIncompleteBuilder<JAVA, DB>.addElse(result: SqExpression<JAVA, DB>): SqCaseCompleteBuilder<JAVA, DB> =
    this.complete(this.reader.notNullReader, result)
@JvmName("addElse__leftNullable_rightNotNull")
fun <JAVA: Any, DB: Any> SqCaseIncompleteBuilder<JAVA?, DB>.addElse(result: SqExpression<JAVA, DB>): SqCaseCompleteBuilder<JAVA?, DB> =
    this.complete(this.reader.nullableReader, result)
@JvmName("addElse__leftNotNull_rightNullable")
fun <JAVA: Any, DB: Any> SqCaseIncompleteBuilder<JAVA, DB>.addElse(result: SqExpression<JAVA?, DB>): SqCaseCompleteBuilder<JAVA?, DB> =
    this.complete(this.reader.nullableReader, result)
@JvmName("addElse__nullable")
fun <JAVA: Any, DB: Any> SqCaseIncompleteBuilder<JAVA?, DB>.addElse(result: SqExpression<JAVA?, DB>): SqCaseCompleteBuilder<JAVA?, DB> =
    this.complete(this.reader.nullableReader, result)
@JvmName("else__notNull")
fun <JAVA: Any, DB: Any> SqCaseIncompleteBuilder<JAVA, DB>.`else`(result: SqExpression<JAVA, DB>): SqCaseCompleteBuilder<JAVA, DB> =
    this.addElse(result)
@JvmName("else__leftNullable_rightNotNull")
fun <JAVA: Any, DB: Any> SqCaseIncompleteBuilder<JAVA?, DB>.`else`(result: SqExpression<JAVA, DB>): SqCaseCompleteBuilder<JAVA?, DB> =
    this.addElse(result)
@JvmName("else__leftNotNull_rightNullable")
fun <JAVA: Any, DB: Any> SqCaseIncompleteBuilder<JAVA, DB>.`else`(result: SqExpression<JAVA?, DB>): SqCaseCompleteBuilder<JAVA?, DB> =
    this.addElse(result)
@JvmName("else__nullable")
fun <JAVA: Any, DB: Any> SqCaseIncompleteBuilder<JAVA?, DB>.`else`(result: SqExpression<JAVA?, DB>): SqCaseCompleteBuilder<JAVA?, DB> =
    this.addElse(result)
// endregion
