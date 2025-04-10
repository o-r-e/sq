@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqCastImpl
import io.github.ore.sq.impl.SqCoalesceBuilderImpl
import io.github.ore.sq.impl.SqCoalesceImpl
import io.github.ore.sq.impl.SqExpressionRequestAggregationImpl
import io.github.ore.sq.impl.SqOrderFragmentColumnImpl
import io.github.ore.sq.impl.SqOrderFragmentImpl
import io.github.ore.sq.util.SqItemPartConfig
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


// region ANY, ALL
interface SqExpressionRequestAggregation<JAVA, DB: Any>: SqExpression<JAVA, DB> {
    val operationKeyword: String
    val request: SqItem

    override val isMultiline: Boolean
        get() = this.request.isMultiline

    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        target.keyword(this.operationKeyword).space()
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

interface SqExpressionRequestAggregationFactory {
    fun <JAVA, DB: Any> createAny(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, DB>,
        request: SqItem,
    ): SqExpressionRequestAggregation<JAVA, DB>

    fun <JAVA, DB: Any> createAll(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, DB>,
        request: SqItem,
    ): SqExpressionRequestAggregation<JAVA, DB>
}

fun <T: SqSettingsBuilder> T.expressionRequestAggregationFactory(value: SqExpressionRequestAggregationFactory?): T =
    this.setValue(SqExpressionRequestAggregationFactory::class.java, value)
val SqSettings.expressionRequestAggregationFactory: SqExpressionRequestAggregationFactory
    get() = this.getValue(SqExpressionRequestAggregationFactory::class.java) ?: SqExpressionRequestAggregationImpl.Factory.INSTANCE


fun <JAVA, DB: Any> SqContext.any(request: SqExpressionReadRequest<JAVA, DB>): SqExpressionRequestAggregation<JAVA, DB> =
    this.settings.expressionRequestAggregationFactory.createAny(this, request.reader, request)
fun <JAVA, DB: Any> SqContext.all(request: SqExpressionReadRequest<JAVA, DB>): SqExpressionRequestAggregation<JAVA, DB> =
    this.settings.expressionRequestAggregationFactory.createAll(this, request.reader, request)
// endregion


// region COALESCE
interface SqCoalesce<JAVA, DB: Any>: SqExpression<JAVA, DB> {
    val operationKeyword: String
        get() = "coalesce"
    val parameters: List<SqItem>

    override val isMultiline: Boolean
        get() = (this.parameters.any { it.isMultiline })

    @Suppress("DuplicatedCode")
    fun addParametersToBuilder(context: SqContext, target: SqJdbcRequestDataBuilder) {
        val multiline = this.isMultiline
        this.parameters.forEachIndexed { index, parameter ->
            if (index > 0) {
                target.comma()
                if (multiline) {
                    target.spaceOrNewLine()
                } else {
                    target.space()
                }
            }
            parameter.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)
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

interface SqCoalesceFactory {
    fun jObjectNotNullReader(context: SqContext): SqDataTypeReader<Any, Any> =
        context.settings.dataTypes.jObjectReaderPair.first
    fun jObjectNullableReader(context: SqContext): SqDataTypeReader<Any?, Any> =
        context.settings.dataTypes.jObjectReaderPair.second

    fun <JAVA, DB: Any> create(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, DB>,
        parameters: List<SqItem>,
    ): SqCoalesce<JAVA, DB>
}

fun <T: SqSettingsBuilder> T.coalesceFactory(value: SqCoalesceFactory?): T =
    this.setValue(SqCoalesceFactory::class.java, value)
val SqSettings.coalesceFactory: SqCoalesceFactory
    get() = this.getValue(SqCoalesceFactory::class.java) ?: SqCoalesceImpl.Factory.INSTANCE


fun <JAVA, DB: Any> SqContext.coalesce(
    reader: SqDataTypeReader<JAVA, DB>,
    parameters: List<SqExpression<*, *>>,
): SqCoalesce<JAVA, DB> {
    return this.settings.coalesceFactory.create(this, reader, parameters)
}

fun <JAVA, DB: Any> SqContext.coalesce(
    reader: SqDataTypeReader<JAVA, DB>,
    parameter: SqExpression<*, *>,
    vararg moreParameters: SqExpression<*, *>,
): SqCoalesce<JAVA, DB> {
    return this.coalesce(reader, listOf(parameter, *moreParameters))
}

@JvmName("coalesce__notNull")
fun <JAVA: Any, DB: Any> SqContext.coalesce(vararg parameters: SqExpression<out JAVA?, *>, last: SqExpression<JAVA, DB>): SqCoalesce<JAVA, DB> =
    this.coalesce(last.reader, listOf(*parameters, last))

@JvmName("coalesce__nullable")
fun <JAVA: Any, DB: Any> SqContext.coalesce(vararg parameters: SqExpression<out JAVA?, *>, last: SqExpression<JAVA?, DB>): SqCoalesce<JAVA?, DB> =
    this.coalesce(last.reader, listOf(*parameters, last))


interface SqCoalesceEmptyBuilder {
    fun <JAVA, DB: Any> add(reader: SqDataTypeReader<JAVA, DB>, parameter: SqItem): SqCoalesceBuilder<JAVA, DB>
}

interface SqCoalesceBuilder<JAVA, DB: Any> {
    val reader: SqDataTypeReader<JAVA, DB>

    fun <JAVA_NEW, DB_NEW: Any> add(reader: SqDataTypeReader<JAVA_NEW, DB_NEW>, parameter: SqItem): SqCoalesceBuilder<JAVA_NEW, DB_NEW>

    fun end(context: SqContext): SqCoalesce<JAVA, DB>
}

fun interface SqCoalesceBuilderFactory {
    operator fun invoke(context: SqContext): SqCoalesceEmptyBuilder
}

fun <T: SqSettingsBuilder> T.coalesceBuilderFactory(value: SqCoalesceBuilderFactory?): T =
    this.setValue(SqCoalesceBuilderFactory::class.java, value)
val SqSettings.coalesceBuilderFactory: SqCoalesceBuilderFactory
    get() = this.getValue(SqCoalesceBuilderFactory::class.java) ?: SqCoalesceBuilderImpl.Factory.INSTANCE


fun <JAVA, DB: Any> SqContext.coalesce(block: (builder: SqCoalesceEmptyBuilder) -> SqCoalesceBuilder<JAVA, DB>): SqCoalesce<JAVA, DB> {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return block(this.settings.coalesceBuilderFactory.invoke(this)).end(this)
}


fun <JAVA, DB: Any> SqCoalesceEmptyBuilder.add(parameter: SqExpression<JAVA, DB>): SqCoalesceBuilder<JAVA, DB> =
    this.add(parameter.reader, parameter)

@JvmName("add__selfNullableReader")
fun <JAVA: Any, DB: Any> SqCoalesceBuilder<JAVA?, DB>.add(parameter: SqExpression<JAVA?, DB>): SqCoalesceBuilder<JAVA?, DB> =
    this.add(this.reader, parameter)
@JvmName("add__selfNotNullReader")
fun <JAVA: Any, DB: Any> SqCoalesceBuilder<JAVA, DB>.add(parameter: SqExpression<JAVA?, DB>): SqCoalesceBuilder<JAVA, DB> =
    this.add(this.reader, parameter)
@JvmName("add__nullable__parameterReader")
fun <JAVA: Any, DB: Any> SqCoalesceBuilder<JAVA?, DB>.add(parameter: SqExpression<JAVA, DB>): SqCoalesceBuilder<JAVA, DB> =
    this.add(parameter.reader, parameter)
@JvmName("add__notNull__parameterReader")
fun <JAVA: Any, DB: Any> SqCoalesceBuilder<JAVA, DB>.add(parameter: SqExpression<JAVA, DB>): SqCoalesceBuilder<JAVA, DB> =
    this.add(parameter.reader, parameter)
// endregion


// region CAST
interface SqCast<JAVA, DB: Any>: SqExpression<JAVA, DB> {
    val expression: SqItem
    val dbTypeText: String

    override val isMultiline: Boolean
        get() = this.expression.isMultiline

    fun addBracketsContentToBuilder(context: SqContext, target: SqJdbcRequestDataBuilder) {
        this.expression.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)

        if (this.isMultiline) {
            target.spaceOrNewLine()
        } else {
            target.space()
        }

        target.keyword("as").space().keyword(this.dbTypeText)
    }

    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        target.keyword("cast")
        if (this.isMultiline) {
            target.bracketsWithIndent {
                this.addBracketsContentToBuilder(context, target)
            }
        } else {
            target.brackets {
                this.addBracketsContentToBuilder(context, target)
            }
        }
    }
}

interface SqCastFactory {
    fun <JAVA, DB: Any> create(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, DB>,
        expression: SqItem,
        dbTypeText: String,
    ): SqCast<JAVA, DB>
}

fun <T: SqSettingsBuilder> T.castFactory(value: SqCastFactory?): T =
    this.setValue(SqCastFactory::class.java, value)
val SqSettings.castFactory: SqCastFactory
    get() = this.getValue(SqCastFactory::class.java) ?: SqCastImpl.Factory.INSTANCE

fun <JAVA, DB: Any> SqContext.cast(expression: SqExpression<*, *>, reader: SqDataTypeReader<JAVA, DB>, dbTypeText: String): SqCast<JAVA, DB> =
    this.settings.castFactory.create(this, reader, expression, dbTypeText)

fun <JAVA, DB: Any> SqExpression<*, *>.cast(reader: SqDataTypeReader<JAVA, DB>, dbTypeText: String, context: SqContext = SqContext.last): SqCast<JAVA, DB> =
    context.cast(this, reader, dbTypeText)

@JvmName("cast__notNull")
fun <JAVA: Any, DB: Any> SqExpression<out Any, *>.cast(pack: SqDataTypePack<JAVA, DB>, dbTypeText: String, context: SqContext = SqContext.last): SqCast<JAVA, DB> =
    this.cast(pack.notNullReader, dbTypeText, context)
@JvmName("cast__nullable")
fun <JAVA: Any, DB: Any> SqExpression<out Any?, *>.cast(pack: SqDataTypePack<JAVA, DB>, dbTypeText: String, context: SqContext = SqContext.last): SqCast<JAVA?, DB> =
    this.cast(pack.nullableReader, dbTypeText, context)
// endregion


// region Fragment "ORDER BY"
interface SqOrderFragment: SqFragment {
    val columns: List<SqOrderFragmentColumn>

    override fun addToBuilder(
        context: SqContext,
        owner: SqFragmented,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        val isMultiline = this.columns.any { it.column.isMultiline }

        target.keyword("order by").indent {
            this.columns.forEachIndexed { index, column ->
                if (index > 0) {
                    target.comma()
                    if (isMultiline) {
                        target.spaceOrNewLine()
                    } else {
                        target.space()
                    }
                }
                column.addToBuilder(context, target)
            }
        }
    }
}

fun interface SqOrderFragmentFactory {
    operator fun invoke(context: SqContext, columns: List<SqOrderFragmentColumn>): SqOrderFragment
}

fun <T: SqSettingsBuilder> T.orderFragmentFactory(value: SqOrderFragmentFactory?): T =
    this.setValue(SqOrderFragmentFactory::class.java, value)
val SqSettings.orderFragmentFactory: SqOrderFragmentFactory
    get() = this.getValue(SqOrderFragmentFactory::class.java) ?: SqOrderFragmentImpl.Factory.INSTANCE


interface SqOrderFragmentColumn {
    val column: SqItem
    val directionKeyword: String?

    fun addToBuilder(context: SqContext, target: SqJdbcRequestDataBuilder) {
        this.column.addToBuilder(context, target, SqItemPartConfig.INSTANCE__OPTIONAL_BRACKETS)
        this.directionKeyword?.let {
            target.space().keyword(it)
        }
    }
}

interface SqOrderFragmentColumnFactory {
    fun create(context: SqContext, column: SqItem): SqOrderFragmentColumn
    fun createAsc(context: SqContext, column: SqItem): SqOrderFragmentColumn
    fun createDesc(context: SqContext, column: SqItem): SqOrderFragmentColumn
}

fun <T: SqSettingsBuilder> T.orderFragmentColumnFactory(value: SqOrderFragmentColumnFactory?): T =
    this.setValue(SqOrderFragmentColumnFactory::class.java, value)
val SqSettings.orderFragmentColumnFactory: SqOrderFragmentColumnFactory
    get() = this.getValue(SqOrderFragmentColumnFactory::class.java) ?: SqOrderFragmentColumnImpl.Factory.INSTANCE


fun SqColumn<*, *>.asc(context: SqContext = SqContext.last): SqOrderFragmentColumn =
    context.settings.orderFragmentColumnFactory.createAsc(context, this)
fun SqColumn<*, *>.desc(context: SqContext = SqContext.last): SqOrderFragmentColumn =
    context.settings.orderFragmentColumnFactory.createDesc(context, this)
// endregion
