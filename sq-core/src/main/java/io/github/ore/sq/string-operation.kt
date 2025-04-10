@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqCharacterLengthImpl
import io.github.ore.sq.impl.SqConcatenationImpl
import io.github.ore.sq.impl.SqStringConvertOperationImpl
import io.github.ore.sq.impl.SqSubstringImpl
import io.github.ore.sq.impl.SqTextPositionImpl
import io.github.ore.sq.util.SqItemPartConfig
import io.github.ore.sq.util.bracketsAreOptional


// region Concatenation
interface SqConcatenation<JAVA: String?>: SqExpression<JAVA, String> {
    val parameters: List<SqItem>

    override val isMultiline: Boolean
        get() = this.parameters.any { it.isMultiline }

    fun addToBuilder(context: SqContext, target: SqJdbcRequestDataBuilder, isMultiline: Boolean) {
        this.parameters.forEachIndexed { index, parameter ->
            if (index > 0) {
                if (isMultiline) {
                    target.spaceOrNewLine()
                } else {
                    target.space()
                }
                target.keyword("||")
                if (isMultiline) {
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
        partConfig: SqItemPartConfig?,
    ) {
        val isMultiline = this.isMultiline

        if (partConfig.bracketsAreOptional()) {
            this.addToBuilder(context, target, isMultiline)
        } else if (isMultiline) {
            target.bracketsWithIndent {
                this.addToBuilder(context, target, true)
            }
        } else {
            target.brackets {
                this.addToBuilder(context, target, false)
            }
        }
    }
}

interface SqConcatenationFactory {
    fun notNullReader(context: SqContext): SqDataTypeReader<String, String> =
        context.dataTypes.jString.notNullReader

    fun nullableReader(context: SqContext): SqDataTypeReader<String?, String> =
        context.dataTypes.jString.nullableReader

    fun <JAVA: String?> create(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, String>,
        parameters: List<SqItem>,
    ): SqConcatenation<JAVA>
}

fun <T: SqSettingsBuilder> T.concatenationFactory(value: SqConcatenationFactory?): T =
    this.setValue(SqConcatenationFactory::class.java, value)
val SqSettings.concatenationFactory: SqConcatenationFactory
    get() = this.getValue(SqConcatenationFactory::class.java) ?: SqConcatenationImpl.Factory.INSTANCE


@JvmName("concatenation__notNull")
fun SqContext.concatenation(parameters: List<SqExpression<out String, String>>): SqConcatenation<String> {
    val factory = this.settings.concatenationFactory
    val reader = factory.notNullReader(this)
    return factory.create(this, reader, parameters)
}

@JvmName("concatenation__notNull")
fun SqContext.concatenation(
    parameter: SqExpression<out String, String>,
    vararg moreParameters: SqExpression<out String, String>,
): SqConcatenation<String> {
    return this.concatenation(listOf(parameter, *moreParameters))
}

@JvmName("concatenation__nullable")
fun SqContext.concatenation(parameters: List<SqExpression<out String?, String>>): SqConcatenation<String?> {
    val factory = this.settings.concatenationFactory
    val reader = factory.nullableReader(this)
    return factory.create(this, reader, parameters)
}

@JvmName("concatenation__nullable")
fun SqContext.concatenation(
    parameter: SqExpression<out String?, String>,
    vararg moreParameters: SqExpression<out String?, String>,
): SqConcatenation<String?> {
    return this.concatenation(listOf(parameter, *moreParameters))
}
// endregion


// region SUBSTRING
interface SqSubstring<JAVA: String?>: SqExpression<JAVA, String> {
    val source: SqItem
    val offset: SqItem?
    val length: SqItem?

    override val isMultiline: Boolean
        get() = (this.source.isMultiline) || (this.offset?.isMultiline == true) || (this.length?.isMultiline == true)

    fun addToBuilder(context: SqContext, target: SqJdbcRequestDataBuilder, isMultiline: Boolean) {
        this.source.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)

        this.offset?.let { offset ->
            if (isMultiline) {
                target.spaceOrNewLine()
            } else {
                target.space()
            }

            target.keyword("from").space()
            offset.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)
        }

        this.length?.let { length ->
            if (isMultiline) {
                target.spaceOrNewLine()
            } else {
                target.space()
            }

            target.keyword("for").space()
            length.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)
        }
    }

    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?,
    ) {
        target.keyword("substring")

        val isMultiline = this.isMultiline
        if (isMultiline) {
            target.bracketsWithIndent {
                this.addToBuilder(context, target, true)
            }
        } else {
            target.brackets {
                this.addToBuilder(context, target, false)
            }
        }
    }
}

interface SqSubstringFactory {
    fun notNullReader(context: SqContext): SqDataTypeReader<String, String> =
        context.dataTypes.jString.notNullReader
    fun nullableReader(context: SqContext): SqDataTypeReader<String?, String> =
        context.dataTypes.jString.nullableReader

    fun <JAVA: String?> create(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, String>,
        source: SqItem,
        offset: SqItem?,
        length: SqItem?,
    ): SqSubstring<JAVA>
}

fun <T: SqSettingsBuilder> T.substringFactory(value: SqSubstringFactory?): T =
    this.setValue(SqSubstringFactory::class.java, value)
val SqSettings.substringFactory: SqSubstringFactory
    get() = this.getValue(SqSubstringFactory::class.java) ?: SqSubstringImpl.Factory.INSTANCE


@JvmName("substring__notNull")
fun SqContext.substring(
    source: SqExpression<out Any, String>,
    offset: SqExpression<*, Number>? = null,
    length: SqExpression<*, Number>? = null,
): SqSubstring<String> {
    val factory = this.settings.substringFactory
    val reader = factory.notNullReader(this)
    return factory.create(this, reader, source, offset, length)
}

@JvmName("substring__nullable")
fun SqContext.substring(
    source: SqExpression<out Any?, String>,
    offset: SqExpression<*, Number>? = null,
    length: SqExpression<*, Number>? = null,
): SqSubstring<String?> {
    val factory = this.settings.substringFactory
    val reader = factory.nullableReader(this)
    return factory.create(this, reader, source, offset, length)
}
// endregion


// region String convert function (UPPER, LOWER, TRIM, LTRIM, RTRIM)
interface SqStringConvertOperation<JAVA: String?>: SqExpression<JAVA, String> {
    val operationKeyword: String
    val target: SqItem

    override val isMultiline: Boolean
        get() = this.target.isMultiline

    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?,
    ) {
        target.keyword(this.operationKeyword).brackets {
            this.target.addToBuilder(context, target, null)
        }
    }
}

interface SqStringConvertOperationFactory {
    fun notNullReader(context: SqContext): SqDataTypeReader<String, String> =
        context.dataTypes.jString.notNullReader

    fun nullableReader(context: SqContext): SqDataTypeReader<String?, String> =
        context.dataTypes.jString.nullableReader

    fun <JAVA: String?> createUpper(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, String>,
        target: SqItem,
    ): SqStringConvertOperation<JAVA>

    fun <JAVA: String?> createLower(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, String>,
        target: SqItem,
    ): SqStringConvertOperation<JAVA>

    fun <JAVA: String?> createTrim(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, String>,
        target: SqItem,
    ): SqStringConvertOperation<JAVA>

    fun <JAVA: String?> createLTrim(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, String>,
        target: SqItem,
    ): SqStringConvertOperation<JAVA>

    fun <JAVA: String?> createRTrim(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, String>,
        target: SqItem,
    ): SqStringConvertOperation<JAVA>
}

fun <T: SqSettingsBuilder> T.textCaseFactory(value: SqStringConvertOperationFactory?): T =
    this.setValue(SqStringConvertOperationFactory::class.java, value)
val SqSettings.textCaseFactory: SqStringConvertOperationFactory
    get() = this.getValue(SqStringConvertOperationFactory::class.java) ?: SqStringConvertOperationImpl.Factory.INSTANCE


@JvmName("upper__notNull")
fun SqContext.upper(target: SqExpression<out Any, String>): SqStringConvertOperation<String> {
    val factory = this.settings.textCaseFactory
    val reader = factory.notNullReader(this)
    return factory.createUpper(this, reader, target)
}

@JvmName("upper__nullable")
fun SqContext.upper(target: SqExpression<out Any?, String>): SqStringConvertOperation<String?> {
    val factory = this.settings.textCaseFactory
    val reader = factory.nullableReader(this)
    return factory.createUpper(this, reader, target)
}

@JvmName("lower__notNull")
fun SqContext.lower(target: SqExpression<out Any, String>): SqStringConvertOperation<String> {
    val factory = this.settings.textCaseFactory
    val reader = factory.notNullReader(this)
    return factory.createLower(this, reader, target)
}

@JvmName("lower__nullable")
fun SqContext.lower(target: SqExpression<out Any?, String>): SqStringConvertOperation<String?> {
    val factory = this.settings.textCaseFactory
    val reader = factory.nullableReader(this)
    return factory.createLower(this, reader, target)
}

@JvmName("trim__notNull")
fun SqContext.trim(target: SqExpression<out Any, String>): SqStringConvertOperation<String> {
    val factory = this.settings.textCaseFactory
    val reader = factory.notNullReader(this)
    return factory.createTrim(this, reader, target)
}

@JvmName("trim__nullable")
fun SqContext.trim(target: SqExpression<out Any?, String>): SqStringConvertOperation<String?> {
    val factory = this.settings.textCaseFactory
    val reader = factory.nullableReader(this)
    return factory.createTrim(this, reader, target)
}

@JvmName("lTrim__notNull")
fun SqContext.lTrim(target: SqExpression<out Any, String>): SqStringConvertOperation<String> {
    val factory = this.settings.textCaseFactory
    val reader = factory.notNullReader(this)
    return factory.createLTrim(this, reader, target)
}

@JvmName("lTrim__nullable")
fun SqContext.lTrim(target: SqExpression<out Any?, String>): SqStringConvertOperation<String?> {
    val factory = this.settings.textCaseFactory
    val reader = factory.nullableReader(this)
    return factory.createLTrim(this, reader, target)
}

@JvmName("rTrim__notNull")
fun SqContext.rTrim(target: SqExpression<out Any, String>): SqStringConvertOperation<String> {
    val factory = this.settings.textCaseFactory
    val reader = factory.notNullReader(this)
    return factory.createRTrim(this, reader, target)
}

@JvmName("rTrim__nullable")
fun SqContext.rTrim(target: SqExpression<out Any?, String>): SqStringConvertOperation<String?> {
    val factory = this.settings.textCaseFactory
    val reader = factory.nullableReader(this)
    return factory.createRTrim(this, reader, target)
}
// endregion


// region CHARACTER_LENGTH
interface SqCharacterLength<JAVA: Number?>: SqExpression<JAVA, Number> {
    val parameter: SqItem

    override val isMultiline: Boolean
        get() = this.parameter.isMultiline

    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?,
    ) {
        target.keyword("character_length").brackets {
            this.parameter.addToBuilder(context, target, null)
        }
    }
}

interface SqCharacterLengthFactory {
    fun notNullReader(context: SqContext): SqDataTypeReader<Int, Number> =
        context.dataTypes.jInt.notNullReader

    fun nullableReader(context: SqContext): SqDataTypeReader<Int?, Number> =
        context.dataTypes.jInt.nullableReader

    fun <JAVA: Number?> create(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Number>,
        parameter: SqItem,
    ): SqCharacterLength<JAVA>
}

fun <T: SqSettingsBuilder> T.characterLengthFactory(value: SqCharacterLengthFactory?): T =
    this.setValue(SqCharacterLengthFactory::class.java, value)
val SqSettings.characterLengthFactory: SqCharacterLengthFactory
    get() = this.getValue(SqCharacterLengthFactory::class.java) ?: SqCharacterLengthImpl.Factory.INSTANCE


@JvmName("characterLength__notNull")
fun SqContext.characterLength(parameter: SqExpression<out Any, String>): SqCharacterLength<Int> {
    val factory = this.settings.characterLengthFactory
    val reader = factory.notNullReader(this)
    return factory.create(this, reader, parameter)
}

@JvmName("characterLength__nullable")
fun SqContext.characterLength(parameter: SqExpression<out Any?, String>): SqCharacterLength<Int?> {
    val factory = this.settings.characterLengthFactory
    val reader = factory.nullableReader(this)
    return factory.create(this, reader, parameter)
}
// endregion


// region POSITION
interface SqTextPosition<JAVA: Number?>: SqExpression<JAVA, Number> {
    val searchedText: SqItem
    val searchLocation: SqItem

    override val isMultiline: Boolean
        get() = (this.searchedText.isMultiline || this.searchLocation.isMultiline)

    fun addParametersToBuilder(context: SqContext, target: SqJdbcRequestDataBuilder, isMultiline: Boolean) {
        this.searchedText.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)

        if (isMultiline) {
            target.spaceOrNewLine()
        } else {
            target.space()
        }

        target.keyword("in")

        if (isMultiline) {
            target.spaceOrNewLine()
        } else {
            target.space()
        }

        this.searchLocation.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)
    }

    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        target.keyword("position")

        val isMultiline = this.isMultiline
        if (isMultiline) {
            target.bracketsWithIndent {
                this.addParametersToBuilder(context, target, true)
            }
        } else {
            target.brackets {
                this.addParametersToBuilder(context, target, false)
            }
        }
    }
}

interface SqTextPositionFactory {
    fun notNullReader(context: SqContext): SqDataTypeReader<Int, Number> =
        context.dataTypes.jInt.notNullReader

    fun nullableReader(context: SqContext): SqDataTypeReader<Int?, Number> =
        context.dataTypes.jInt.nullableReader

    fun <JAVA: Number?> create(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, Number>,
        searchedText: SqItem,
        searchLocation: SqItem,
    ): SqTextPosition<JAVA>
}

fun <T: SqSettingsBuilder> T.textPositionFactory(value: SqTextPositionFactory?): T =
    this.setValue(SqTextPositionFactory::class.java, value)
val SqSettings.textPositionFactory: SqTextPositionFactory
    get() = this.getValue(SqTextPositionFactory::class.java) ?: SqTextPositionImpl.Factory.INSTANCE


@JvmName("position__notNull")
fun SqContext.position(searchedText: SqExpression<out Any, String>, searchLocation: SqExpression<out Any, String>): SqTextPosition<Int> {
    val factory = this.settings.textPositionFactory
    val reader = factory.notNullReader(this)
    return factory.create(this, reader, searchedText, searchLocation)
}

@JvmName("position__nullable")
fun SqContext.position(searchedText: SqExpression<out Any?, String>, searchLocation: SqExpression<out Any?, String>): SqTextPosition<Int?> {
    val factory = this.settings.textPositionFactory
    val reader = factory.nullableReader(this)
    return factory.create(this, reader, searchedText, searchLocation)
}
// endregion
