@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.util.SqItemPartConfig
import java.sql.ResultSet


interface SqItem {
    val definition: SqItem
        get() = this

    val isMultiline: Boolean

    var commentAtStart: String?
    var commentAtEnd: String?

    fun addToBuilderWithoutComments(context: SqContext, target: SqJdbcRequestDataBuilder, partConfig: SqItemPartConfig?)

    fun addToBuilder(context: SqContext, target: SqJdbcRequestDataBuilder, partConfig: SqItemPartConfig?) {
        this.commentAtStart?.let {
            if (target.commentsAllowed) {
                target.comment(it).space()
            }
        }

        this.addToBuilderWithoutComments(context, target, partConfig)

        this.commentAtEnd?.let {
            if (target.commentsAllowed) {
                target.space().comment(it)
            }
        }
    }

    fun createJdbcRequestData(
        context: SqContext = SqContext.last,
        partConfig: SqItemPartConfig? = null,
    ): SqJdbcRequestData {
        val self = this
        return context.jdbcRequestData { builder ->
            self.addToBuilder(context, builder, partConfig)
        }
    }
}

fun <T: SqItem> T.addTo(
    target: SqJdbcRequestDataBuilder,
    partConfig: SqItemPartConfig?,
    context: SqContext = SqContext.last,
): T = this.apply {
    this.addToBuilder(context, target, partConfig)
}

fun <T: SqItem> T.commentAtStart(value: String?): T = this.apply {
    this.commentAtStart = value
}

fun <T: SqItem> T.commentAtEnd(value: String?): T = this.apply {
    this.commentAtEnd = value
}

fun <T: SqItem> T.comments(commentAtStart: String?, commentAtEnd: String?): T = this.apply {
    this.commentAtStart = commentAtStart
    this.commentAtEnd = commentAtEnd
}


interface SqExpression<JAVA, DB: Any> : SqItem {
    val reader: SqDataTypeReader<JAVA, DB>

    fun read(source: ResultSet, index: Int): JAVA =
        this.reader[source, index]
}


interface SqColumn<JAVA, DB: Any> : SqExpression<JAVA, DB> {
    val name: String

    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?,
    ) {
        target.identifier(this.name)
    }
}

interface SqOwnedColumn<JAVA, DB: Any> : SqColumn<JAVA, DB> {
    val owner: SqItem

    override val isMultiline: Boolean
        get() = this.owner.isMultiline

    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?,
    ) {
        owner.addToBuilder(context, target, partConfig = SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)
        target.dot().identifier(this.name)
    }
}

interface SqColumnSource: SqItem {
    val columns: List<SqColumn<*, *>>
}

interface SqExpressionColumnSource<JAVA, DB: Any> : SqColumnSource, SqExpression<JAVA, DB>
