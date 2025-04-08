@file:Suppress("unused")

package io.github.ore.sq.impl

import io.github.ore.sq.SqContext
import io.github.ore.sq.SqFunction
import io.github.ore.sq.SqJdbcRequestData
import io.github.ore.sq.SqJdbcRequestDataBuilder
import io.github.ore.sq.SqJdbcRequestDataBuilderFactory
import io.github.ore.sq.SqParameter
import io.github.ore.sq.SqSettingsBuilder
import io.github.ore.sq.jdbcRequestDataBuilderFactory


abstract class SqJdbcRequestDataBuilderBase(
    protected open val sqlBuilder: StringBuilder = StringBuilder(),
    protected open val parameters: MutableList<SqParameter<*, *>> = ArrayList(),
    protected open val scheduledWhitespaceBuilder: StringBuilder = StringBuilder(),
) : SqJdbcRequestDataBuilder {
    companion object {
        val COMMENT_PREPARER: SqFunction<String, String> = { buildString(it.length) {
            var previousWasAsterisk = false
            for (char in it) {
                when (char) {
                    '*' -> {
                        previousWasAsterisk = true
                    }
                    '/' -> {
                        if (previousWasAsterisk) {
                            this.append(' ')
                            previousWasAsterisk = false
                        }
                    }
                    else -> {
                        previousWasAsterisk = false
                    }
                }
                this.append(char)
            }
        } }
    }


    protected open var mutableData: SqJdbcRequestData? = null

    override val data: SqJdbcRequestData
        get() {
            return this.mutableData ?: run {
                val result = SqJdbcRequestData(
                    sql = this.sqlBuilder.toString(),
                    parameters = ArrayList(this.parameters),
                )
                this.mutableData = result
                result
            }
        }

    protected open fun resetData() {
        this.mutableData = null
    }


    override val commentsAllowed: Boolean
        get() = (this.commentPreparer != null)


    protected open fun appendScheduledWhitespace(value: String): SqJdbcRequestDataBuilderBase = this.apply {
        this.scheduledWhitespaceBuilder.append(value)
    }

    protected open fun setScheduledWhitespace(value: String?): SqJdbcRequestDataBuilderBase = this.apply {
        this.scheduledWhitespaceBuilder.clear()
        if (value != null) {
            this.appendScheduledWhitespace(value)
        }
    }

    protected open fun applyScheduledWhitespace(): SqJdbcRequestDataBuilderBase = this.apply {
        if (!this.scheduledWhitespaceBuilder.isEmpty()) {
            this.sqlBuilder.append(this.scheduledWhitespaceBuilder)
            this.scheduledWhitespaceBuilder.clear()
        }
    }


    protected abstract val keywordConverter: SqFunction<String, String>
    protected abstract val identifierConverter: SqFunction<String, String>
    protected abstract val parameterMark: String
    protected abstract val commentPreparer: SqFunction<String, String>?


    override fun text(value: String): SqJdbcRequestDataBuilderBase = this.apply {
        this.resetData()
        this.applyScheduledWhitespace()
        this.sqlBuilder.append(value)
    }

    override fun text(value: Char): SqJdbcRequestDataBuilderBase = this.apply {
        this.resetData()
        this.applyScheduledWhitespace()
        this.sqlBuilder.append(value)
    }

    override fun keyword(value: String): SqJdbcRequestDataBuilderBase =
        this.text(this.keywordConverter.invoke(value))

    override fun identifier(value: String): SqJdbcRequestDataBuilderBase =
        this.text(this.identifierConverter.invoke(value))

    override fun parameter(value: SqParameter<*, *>): SqJdbcRequestDataBuilderBase = this.apply {
        this.text(this.parameterMark)
        this.parameters.add(value)
    }

    override fun space(): SqJdbcRequestDataBuilderBase =
        this.setScheduledWhitespace(" ")

    override fun comment(value: String): SqJdbcRequestDataBuilderBase = this.apply {
        this.commentPreparer?.let { commentPreparer ->
            this.text("/* ").text(commentPreparer.invoke(value)).text(" */")
        }
    }


    override fun clear(): SqJdbcRequestDataBuilderBase = this.apply {
        this.sqlBuilder.clear()
        this.parameters.clear()
        this.scheduledWhitespaceBuilder.clear()
        this.mutableData = null
    }
}


open class SqJdbcRequestDataBuilderFlatImpl(
    override val keywordConverter: SqFunction<String, String>,
    override val identifierConverter: SqFunction<String, String>,
    override val parameterMark: String,
    override val commentPreparer: SqFunction<String, String>?,
    sqlBuilder: StringBuilder = StringBuilder(),
    parameters: MutableList<SqParameter<*, *>> = ArrayList(),
    scheduledWhitespaceBuilder: StringBuilder = StringBuilder(),
) : SqJdbcRequestDataBuilderBase(sqlBuilder, parameters, scheduledWhitespaceBuilder) {
    override fun space(): SqJdbcRequestDataBuilderFlatImpl = this.apply {
        super.space()
    }


    override fun nothingOrSpace(): SqJdbcRequestDataBuilderFlatImpl =
        this

    override fun nothingOrNewLine(): SqJdbcRequestDataBuilderFlatImpl =
        this

    override fun spaceOrNothing(): SqJdbcRequestDataBuilderFlatImpl =
        this.space()

    override fun spaceOrNewLine(): SqJdbcRequestDataBuilderFlatImpl =
        this.space()

    override fun incrementIndent(): SqJdbcRequestDataBuilderFlatImpl =
        this

    override fun decrementIndent(): SqJdbcRequestDataBuilderFlatImpl =
        this


    open class Factory(
        open val keywordConverter: SqFunction<String, String> = SqJdbcRequestDataBuilder.KEYWORD_CONVERTER__TO_UPPER_CASE,
        open val identifierConverter: SqFunction<String, String> = SqJdbcRequestDataBuilder.IDENTIFIER__CONVERTER,
        open val commentPreparer: SqFunction<String, String>? = null,
        open val parameterMark: String = SqJdbcRequestDataBuilder.PARAMETER_MARK,
    ) : SqJdbcRequestDataBuilderFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext): SqJdbcRequestDataBuilderFlatImpl {
            return SqJdbcRequestDataBuilderFlatImpl(
                keywordConverter = this.keywordConverter,
                identifierConverter = this.identifierConverter,
                parameterMark = this.parameterMark,
                commentPreparer = this.commentPreparer,
            )
        }
    }
}


open class SqJdbcRequestDataBuilderPrettyImpl(
    override val keywordConverter: SqFunction<String, String>,
    override val identifierConverter: SqFunction<String, String>,
    override val parameterMark: String,
    override val commentPreparer: SqFunction<String, String>?,
    protected open val indentPart: String,
    protected open val lineSeparator: String,
    sqlBuilder: StringBuilder = StringBuilder(),
    parameters: MutableList<SqParameter<*, *>> = ArrayList(),
    scheduledWhitespaceBuilder: StringBuilder = StringBuilder(),
) : SqJdbcRequestDataBuilderBase(sqlBuilder, parameters, scheduledWhitespaceBuilder) {
    override fun setScheduledWhitespace(value: String?): SqJdbcRequestDataBuilderPrettyImpl = this.apply {
        super.setScheduledWhitespace(value)
    }

    override fun appendScheduledWhitespace(value: String): SqJdbcRequestDataBuilderPrettyImpl = this.apply {
        super.appendScheduledWhitespace(value)
    }

    override fun text(value: String): SqJdbcRequestDataBuilderPrettyImpl = this.apply {
        super.text(value)
    }

    override fun space(): SqJdbcRequestDataBuilderPrettyImpl = this.apply {
        super.space()
    }


    protected open var indentLevel: Int = 0
    protected open var indent: String = ""

    override fun incrementIndent(): SqJdbcRequestDataBuilderPrettyImpl = this.apply {
        this.indentLevel++
        this.indent = this.indentPart.repeat(this.indentLevel)
    }

    override fun decrementIndent(): SqJdbcRequestDataBuilderPrettyImpl = this.apply {
        if (this.indentLevel > 0) {
            this.indentLevel--
            this.indent = this.indentPart.repeat(this.indentLevel)
        }
    }

    protected open fun indent(): SqJdbcRequestDataBuilderPrettyImpl =
        this.setScheduledWhitespace(this.indent)


    open fun newLine(): SqJdbcRequestDataBuilderPrettyImpl =
        this.setScheduledWhitespace(this.lineSeparator).appendScheduledWhitespace(this.indent)


    override fun nothingOrSpace(): SqJdbcRequestDataBuilderPrettyImpl =
        this.space()

    override fun nothingOrNewLine(): SqJdbcRequestDataBuilderPrettyImpl =
        this.newLine()

    override fun spaceOrNothing(): SqJdbcRequestDataBuilderPrettyImpl =
        this

    override fun spaceOrNewLine(): SqJdbcRequestDataBuilderPrettyImpl =
        this.newLine()


    open class Factory(
        protected open val keywordConverter: SqFunction<String, String> = SqJdbcRequestDataBuilder.KEYWORD_CONVERTER__TO_UPPER_CASE,
        protected open val identifierConverter: SqFunction<String, String> = SqJdbcRequestDataBuilder.IDENTIFIER__CONVERTER,
        protected open val parameterMark: String = SqJdbcRequestDataBuilder.PARAMETER_MARK,
        protected open val commentPreparer: SqFunction<String, String>? = null,
        protected open val indentPart: String = SqJdbcRequestDataBuilder.INDENT_PART,
        protected open val lineSeparator: String = SqJdbcRequestDataBuilder.LINE_SEPARATOR,
    ) : SqJdbcRequestDataBuilderFactory {
        override fun invoke(context: SqContext): SqJdbcRequestDataBuilderPrettyImpl {
            return SqJdbcRequestDataBuilderPrettyImpl(
                keywordConverter = this.keywordConverter,
                identifierConverter = this.identifierConverter,
                parameterMark = this.parameterMark,
                commentPreparer = this.commentPreparer,
                indentPart = this.indentPart,
                lineSeparator = this.lineSeparator,
            )
        }
    }
}


fun <T : SqSettingsBuilder> T.jdbcRequestDataBuilderFactory(
    pretty: Boolean = false,
    keywordConverter: SqFunction<String, String> = SqJdbcRequestDataBuilder.KEYWORD_CONVERTER__TO_UPPER_CASE,
    identifierConverter: SqFunction<String, String> = SqJdbcRequestDataBuilder.IDENTIFIER__CONVERTER,
    parameterMark: String = SqJdbcRequestDataBuilder.PARAMETER_MARK,
    allowComments: Boolean = false,
    indentPart: String = SqJdbcRequestDataBuilder.INDENT_PART,
    lineSeparator: String = SqJdbcRequestDataBuilder.LINE_SEPARATOR,
    commentPreparer: SqFunction<String, String>? = if (allowComments) { SqJdbcRequestDataBuilderBase.COMMENT_PREPARER } else { null },
): T {
    val factory = if (pretty) {
        SqJdbcRequestDataBuilderPrettyImpl.Factory(
            keywordConverter = keywordConverter,
            identifierConverter = identifierConverter,
            parameterMark = parameterMark,
            commentPreparer = commentPreparer,
            indentPart = indentPart,
            lineSeparator = lineSeparator,
        )
    } else {
        SqJdbcRequestDataBuilderFlatImpl.Factory(
            keywordConverter = keywordConverter,
            identifierConverter = identifierConverter,
            parameterMark = parameterMark,
            commentPreparer = commentPreparer,
        )
    }
    return this.jdbcRequestDataBuilderFactory(factory)
}
