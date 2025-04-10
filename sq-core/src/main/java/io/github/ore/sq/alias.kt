@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqAliasDefinitionImpl
import io.github.ore.sq.impl.SqColumnSourceAliasImpl
import io.github.ore.sq.impl.SqExpressionAliasImpl
import io.github.ore.sq.impl.SqExpressionColumnSourceAliasImpl
import io.github.ore.sq.util.SqItemPartConfig


// region Alias definition
interface SqAliasDefinition: SqItem {
    val original: SqItem
    val name: String

    override val isMultiline: Boolean
        get() = this.original.isMultiline

    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        this.original.addToBuilder(context, target, partConfig = SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)
        target.space().keyword("as").space().identifier(this.name)
    }
}

fun interface SqAliasDefinitionFactory {
    operator fun invoke(context: SqContext, original: SqItem, name: String): SqAliasDefinition
}

fun <T: SqSettingsBuilder> T.aliasDefinitionFactory(value: SqAliasDefinitionFactory?): T =
    this.setValue(SqAliasDefinitionFactory::class, value)
val SqSettings.aliasDefinitionFactory: SqAliasDefinitionFactory
    get() = this.getValue(SqAliasDefinitionFactory::class.java) ?: SqAliasDefinitionImpl.Factory.INSTANCE
// endregion


interface SqAlias: SqItem {
    val name: String
    override val definition: SqItem

    override val isMultiline: Boolean
        get() = false

    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        target.identifier(this.name)
    }
}


// region Expression alias
interface SqExpressionAlias<JAVA, DB: Any>: SqAlias, SqColumn<JAVA, DB> {
    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        super<SqAlias>.addToBuilderWithoutComments(context, target, partConfig)
    }
}

interface SqExpressionAliasFactory {
    fun <JAVA, DB: Any> create(
        context: SqContext,
        original: SqItem,
        reader: SqDataTypeReader<JAVA, DB>,
        name: String,
    ): SqExpressionAlias<JAVA, DB>
}

fun <T: SqSettingsBuilder> T.expressionAliasFactory(value: SqExpressionAliasFactory?): T =
    this.setValue(SqExpressionAliasFactory::class.java, value)
val SqSettings.expressionAliasFactory: SqExpressionAliasFactory
    get() = this.getValue(SqExpressionAliasFactory::class.java) ?: SqExpressionAliasImpl.Factory.INSTANCE


fun <JAVA, DB: Any> SqExpression<JAVA, DB>.alias(name: String, context: SqContext): SqExpressionAlias<JAVA, DB> =
    context.settings.expressionAliasFactory.create(context, this, this.reader, name)

fun <JAVA, DB: Any> SqExpression<JAVA, DB>.`as`(name: String, context: SqContext): SqExpressionAlias<JAVA, DB> =
    this.alias(name, context)

infix fun <JAVA, DB: Any> SqExpression<JAVA, DB>.alias(name: String): SqExpressionAlias<JAVA, DB> =
    this.alias(name, SqContext.last)

infix fun <JAVA, DB: Any> SqExpression<JAVA, DB>.`as`(name: String): SqExpressionAlias<JAVA, DB> =
    this.`as`(name, SqContext.last)
// endregion


// region Column source alias
interface SqColumnSourceAlias: SqAlias, SqColumnSource {
    operator fun <JAVA, DB: Any> get(original: SqColumn<JAVA, DB>): SqColumn<JAVA, DB>
}

fun interface SqColumnSourceAliasFactory {
    operator fun invoke(
        context: SqContext,
        name: String,
        original: SqItem,
        columns: List<SqColumn<*, *>>,
    ): SqColumnSourceAlias
}

fun <T: SqSettingsBuilder> T.columnSourceAliasFactory(value: SqColumnSourceAliasFactory?): T =
    this.setValue(SqColumnSourceAliasFactory::class.java, value)
val SqSettings.columnSourceAliasFactory: SqColumnSourceAliasFactory
    get() = this.getValue(SqColumnSourceAliasFactory::class.java) ?: SqColumnSourceAliasImpl.Factory.INSTANCE


fun SqColumnSource.alias(name: String, context: SqContext): SqColumnSourceAlias =
    context.settings.columnSourceAliasFactory.invoke(context, name, this, this.columns)

fun SqColumnSource.`as`(name: String, context: SqContext): SqColumnSourceAlias =
    this.alias(name, context)

infix fun SqColumnSource.alias(name: String): SqColumnSourceAlias =
    this.alias(name, SqContext.last)

infix fun SqColumnSource.`as`(name: String): SqColumnSourceAlias =
    this.`as`(name, SqContext.last)


interface SqExpressionColumnSourceAlias<JAVA, DB: Any>: SqColumnSourceAlias, SqExpressionColumnSource<JAVA, DB>

interface SqExpressionColumnSourceAliasFactory {
    fun <JAVA, DB: Any> create(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, DB>,
        name: String,
        original: SqItem,
        columns: List<SqColumn<*, *>>,
    ): SqExpressionColumnSourceAlias<JAVA, DB>
}

fun <T: SqSettingsBuilder> T.expressionColumnSourceAliasFactory(value: SqExpressionColumnSourceAliasFactory?): T =
    this.setValue(SqExpressionColumnSourceAliasFactory::class.java, value)
val SqSettings.expressionColumnSourceAliasFactory: SqExpressionColumnSourceAliasFactory
    get() = this.getValue(SqExpressionColumnSourceAliasFactory::class.java) ?: SqExpressionColumnSourceAliasImpl.Factory.INSTANCE


fun <JAVA, DB: Any> SqExpressionColumnSource<JAVA, DB>.alias(name: String, context: SqContext): SqExpressionColumnSourceAlias<JAVA, DB> =
    context.settings.expressionColumnSourceAliasFactory.create(context, this.reader, name, this, this.columns)

fun <JAVA, DB: Any> SqExpressionColumnSource<JAVA, DB>.`as`(name: String, context: SqContext): SqExpressionColumnSourceAlias<JAVA, DB> =
    this.alias(name, context)

infix fun <JAVA, DB: Any> SqExpressionColumnSource<JAVA, DB>.alias(name: String): SqExpressionColumnSourceAlias<JAVA, DB> =
    this.alias(name, SqContext.last)

infix fun <JAVA, DB: Any> SqExpressionColumnSource<JAVA, DB>.`as`(name: String): SqExpressionColumnSourceAlias<JAVA, DB> =
    this.`as`(name, SqContext.last)
// endregion
