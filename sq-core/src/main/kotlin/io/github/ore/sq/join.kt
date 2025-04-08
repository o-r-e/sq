@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqJoinImpl
import io.github.ore.sq.util.SqItemPartConfig


interface SqJoin: SqColumnSource {
    val leftItem: SqItem
    val operationKeyword: String
    val rightItem: SqItem
    var condition: SqItem?

    override val isMultiline: Boolean
        get() = true

    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        this.leftItem.definition.addToBuilder(context, target, SqItemPartConfig.INSTANCE__OPTIONAL_BRACKETS)

        target.spaceOrNewLine().keyword(this.operationKeyword).space()

        this.rightItem.definition.addToBuilder(context, target, SqItemPartConfig.INSTANCE__OPTIONAL_BRACKETS)

        this.condition?.let { condition ->
            target.indent {
                target.keyword("on").space()

                condition.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)
            }
        }
    }
}

interface SqJoinFactory {
    fun createInner(context: SqContext, leftItem: SqItem, rightItem: SqItem, columns: List<SqColumn<*, *>>): SqJoin
    fun createLeft(context: SqContext, leftItem: SqItem, rightItem: SqItem, columns: List<SqColumn<*, *>>): SqJoin
    fun createRight(context: SqContext, leftItem: SqItem, rightItem: SqItem, columns: List<SqColumn<*, *>>): SqJoin
    fun createFull(context: SqContext, leftItem: SqItem, rightItem: SqItem, columns: List<SqColumn<*, *>>): SqJoin
}

fun <T: SqSettingsBuilder> T.joinFactory(value: SqJoinFactory?): T =
    this.setValue(SqJoinFactory::class.java, value)
val SqSettings.joinFactory: SqJoinFactory
    get() = this.getValue(SqJoinFactory::class.java) ?: SqJoinImpl.Factory.INSTANCE


private fun collectColumns(leftColumnSource: SqColumnSource, rightColumnSource: SqColumnSource): List<SqColumn<*, *>> {
    val leftColumns = leftColumnSource.columns
    val rightColumns = rightColumnSource.columns
    return buildList<SqColumn<*, *>>(leftColumns.size + rightColumns.size) {
        this.addAll(leftColumns)
        this.addAll(rightColumns)
    }
}

fun SqColumnSource.innerJoin(other: SqColumnSource, context: SqContext): SqJoin =
    context.settings.joinFactory.createInner(context, this, other, collectColumns(this, other))
infix fun SqColumnSource.innerJoin(other: SqColumnSource): SqJoin =
    this.innerJoin(other, SqContext.last)

fun SqColumnSource.leftJoin(other: SqColumnSource, context: SqContext): SqJoin =
    context.settings.joinFactory.createLeft(context, this, other, collectColumns(this, other))
infix fun SqColumnSource.leftJoin(other: SqColumnSource): SqJoin =
    this.leftJoin(other, SqContext.last)

fun SqColumnSource.rightJoin(other: SqColumnSource, context: SqContext): SqJoin =
    context.settings.joinFactory.createRight(context, this, other, collectColumns(this, other))
infix fun SqColumnSource.rightJoin(other: SqColumnSource): SqJoin =
    this.rightJoin(other, SqContext.last)

fun SqColumnSource.fullJoin(other: SqColumnSource, context: SqContext): SqJoin =
    context.settings.joinFactory.createFull(context, this, other, collectColumns(this, other))
infix fun SqColumnSource.fullJoin(other: SqColumnSource): SqJoin =
    this.fullJoin(other, SqContext.last)


fun <T: SqJoin> T.on(condition: SqExpression<*, Boolean>): T = this.apply {
    this.condition = condition
}
