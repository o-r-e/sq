@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.*
import io.github.ore.sq.util.SqItemPartConfig


// region Request, factory
interface SqSelect: SqReadRequest, SqFragmented {
    override val isMultiline: Boolean
        get() = true
}

fun interface SqSelectFactory {
    operator fun invoke(context: SqContext, distinct: Boolean, columns: List<SqColumn<*, *>>): SqSelect
}

fun <T: SqSettingsBuilder> T.selectFactory(value: SqSelectFactory?): T =
    this.setValue(SqSelectFactory::class.java, value)
val SqSettings.selectFactory: SqSelectFactory
    get() = this.getValue(SqSelectFactory::class.java) ?: SqSelectImpl.Factory.INSTANCE


interface SqExpressionSelect<JAVA, DB: Any> : SqSelect, SqExpressionReadRequest<JAVA, DB>

interface SqExpressionSelectFactory {
    fun <JAVA, DB: Any> create(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, DB>,
        distinct: Boolean,
        columns: List<SqColumn<*, *>>,
    ): SqExpressionSelect<JAVA, DB>
}

fun <T: SqSettingsBuilder> T.expressionSelectFactory(value: SqExpressionSelectFactory?): T =
    this.setValue(SqExpressionSelectFactory::class.java, value)
val SqSettings.expressionSelectFactory: SqExpressionSelectFactory
    get() = this.getValue(SqExpressionSelectFactory::class.java) ?: SqExpressionSelectImpl.Factory.INSTANCE


fun SqContext.select(distinct: Boolean, columns: List<SqColumn<*, *>>): SqSelect =
    this.settings.selectFactory.invoke(this, distinct, columns)

fun SqContext.select(
    distinct: Boolean,
    firstColumn: SqColumn<*, *>,
    secondColumn: SqColumn<*, *>,
    vararg moreColumns: SqColumn<*, *>,
): SqSelect {
    return this.select(distinct, listOf(firstColumn, secondColumn, *moreColumns))
}

fun <JAVA, DB: Any> SqContext.select(distinct: Boolean, column: SqColumn<JAVA, DB>): SqExpressionSelect<JAVA, DB> =
    this.settings.expressionSelectFactory.create(this, column.reader, distinct, listOf(column))

fun SqContext.select(columns: List<SqColumn<*, *>>): SqSelect =
    this.settings.selectFactory.invoke(this, distinct = false, columns)

fun SqContext.select(
    firstColumn: SqColumn<*, *>,
    secondColumn: SqColumn<*, *>,
    vararg moreColumns: SqColumn<*, *>,
): SqSelect {
    return this.select(distinct = false, listOf(firstColumn, secondColumn, *moreColumns))
}

fun <JAVA, DB: Any> SqContext.select(column: SqColumn<JAVA, DB>): SqExpressionSelect<JAVA, DB> =
    this.settings.expressionSelectFactory.create(this, column.reader, distinct = false, listOf(column))

fun SqContext.selectDistinct(columns: List<SqColumn<*, *>>): SqSelect =
    this.settings.selectFactory.invoke(this, distinct = true, columns)

fun SqContext.selectDistinct(
    firstColumn: SqColumn<*, *>,
    secondColumn: SqColumn<*, *>,
    vararg moreColumns: SqColumn<*, *>,
): SqSelect {
    return this.select(distinct = true, listOf(firstColumn, secondColumn, *moreColumns))
}

fun <JAVA, DB: Any> SqContext.selectDistinct(column: SqColumn<JAVA, DB>): SqExpressionSelect<JAVA, DB> =
    this.settings.expressionSelectFactory.create(this, column.reader, distinct = true, listOf(column))
// endregion


// region Start fragment
interface SqSelectStartFragment: SqFragment {
    companion object {
        fun <T: SqSelect> addToRequest(context: SqContext, distinct: Boolean, columns: List<SqColumn<*, *>>, target: T): T {
            val fragment = context.settings.selectStartFragmentFactory.invoke(context, distinct, columns)
            target.addFragment(fragment)
            return target
        }
    }


    val distinct: Boolean
    val columns: List<SqItem>

    override fun addToBuilder(
        context: SqContext,
        owner: SqFragmented,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?,
    ) {
        target.keyword("select")
        if (this.distinct) {
            target.space().keyword("distinct")
        }
        target.indent {
            this.columns.forEachIndexed { index, column ->
                if (index > 0) {
                    target.comma().spaceOrNewLine()
                }
                column.definition.addToBuilder(context, target, partConfig = SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)
            }
        }
    }
}

fun interface SqSelectStartFragmentFactory {
    operator fun invoke(context: SqContext, distinct: Boolean, columns: List<SqItem>): SqSelectStartFragment
}

fun <T: SqSettingsBuilder> T.selectStartFragmentFactory(value: SqSelectStartFragmentFactory?): T =
    this.setValue(SqSelectStartFragmentFactory::class.java, value)
val SqSettings.selectStartFragmentFactory: SqSelectStartFragmentFactory
    get() = this.getValue(SqSelectStartFragmentFactory::class.java) ?: SqSelectStartFragmentImpl.Factory.INSTANCE
// endregion


// region Fragment "FROM"
interface SqSelectFromFragment: SqFragment {
    val sources: List<SqItem>

    override fun addToBuilder(
        context: SqContext,
        owner: SqFragmented,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?,
    ) {
        target.keyword("from").indent {
            this.sources.forEachIndexed { index, source ->
                if (index > 0) {
                    target.comma().spaceOrNewLine()
                }
                source.definition.addToBuilder(context, target, partConfig = SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)
            }
        }
    }
}

fun interface SqSelectFromFragmentFactory {
    operator fun invoke(context: SqContext, sources: List<SqItem>): SqSelectFromFragment
}

fun <T: SqSettingsBuilder> T.selectFromFragmentFactory(value: SqSelectFromFragmentFactory?): T =
    this.setValue(SqSelectFromFragmentFactory::class.java, value)
val SqSettings.selectFromFragmentFactory: SqSelectFromFragmentFactory
    get() = this.getValue(SqSelectFromFragmentFactory::class.java) ?: SqSelectFromFragmentImpl.Factory.INSTANCE


fun <T: SqSelect> T.from(sources: List<SqColumnSource>, context: SqContext = SqContext.last): T = this.apply {
    val fragment = context.settings.selectFromFragmentFactory.invoke(context, sources)
    this.addFragment(fragment)
}

fun <T: SqSelect> T.from(source: SqColumnSource, vararg moreSources: SqColumnSource, context: SqContext = SqContext.last): T =
    this.from(listOf(source, *moreSources), context)
// endregion


// region Fragment "WHERE"
interface SqSelectWhereFragment: SqFragment {
    val condition: SqItem

    override fun addToBuilder(
        context: SqContext,
        owner: SqFragmented,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        target.keyword("where").indent {
            this.condition.addToBuilder(context, target, partConfig = SqItemPartConfig.INSTANCE__OPTIONAL_BRACKETS)
        }
    }
}

fun interface SqSelectWhereFragmentFactory {
    operator fun invoke(context: SqContext, condition: SqItem): SqSelectWhereFragment
}

fun <T: SqSettingsBuilder> T.selectWhereFragmentFactory(value: SqSelectWhereFragmentFactory?): T =
    this.setValue(SqSelectWhereFragmentFactory::class.java, value)
val SqSettings.selectWhereFragmentFactory: SqSelectWhereFragmentFactory
    get() = this.getValue(SqSelectWhereFragmentFactory::class.java) ?: SqSelectWhereFragmentImpl.Factory.INSTANCE


fun <T: SqSelect> T.where(condition: SqExpression<*, Boolean>, context: SqContext = SqContext.last): T = this.apply {
    val fragment = context.settings.selectWhereFragmentFactory.invoke(context, condition)
    this.addFragment(fragment)
}
// endregion


// region Fragment "GROUP BY"
interface SqSelectGroupFragment: SqFragment {
    val columns: List<SqItem>

    override fun addToBuilder(
        context: SqContext,
        owner: SqFragmented,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        val columns = this.columns
        val isMultiline = columns.any { it.isMultiline }
        target.keyword("group by").indent {
            columns.forEachIndexed { index, column ->
                if (index > 0) {
                    target.comma()
                    if (isMultiline) {
                        target.spaceOrNewLine()
                    } else {
                        target.space()
                    }
                }
                column.addToBuilder(context, target, SqItemPartConfig.INSTANCE__OPTIONAL_BRACKETS)
            }
        }
    }
}

fun interface SqSelectGroupFragmentFactory {
    operator fun invoke(context: SqContext, columns: List<SqItem>): SqSelectGroupFragment
}

fun <T: SqSettingsBuilder> T.selectGroupFragmentFactory(value: SqSelectGroupFragmentFactory?): T =
    this.setValue(SqSelectGroupFragmentFactory::class.java, value)
val SqSettings.selectGroupFragmentFactory: SqSelectGroupFragmentFactory
    get() = this.getValue(SqSelectGroupFragmentFactory::class.java) ?: SqSelectGroupFragmentImpl.Factory.INSTANCE


fun <T: SqSelect> T.groupBy(columns: List<SqColumn<*, *>>, context: SqContext = SqContext.last): T = this.apply {
    val fragment = context.settings.selectGroupFragmentFactory.invoke(context, columns)
    this.addFragment(fragment)
}

fun <T: SqSelect> T.groupBy(column: SqColumn<*, *>, vararg moreColumns: SqColumn<*, *>, context: SqContext = SqContext.last): T =
    this.groupBy(listOf(column, *moreColumns), context)
// endregion


// region Fragment "HAVING"
interface SqSelectHavingFragment: SqFragment {
    val condition: SqItem

    override fun addToBuilder(
        context: SqContext,
        owner: SqFragmented,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        target.keyword("having").indent {
            this.condition.addToBuilder(context, target, SqItemPartConfig.INSTANCE__OPTIONAL_BRACKETS)
        }
    }
}

fun interface SqSelectHavingFragmentFactory {
    operator fun invoke(context: SqContext, condition: SqItem): SqSelectHavingFragment
}

fun <T: SqSettingsBuilder> T.selectHavingFragmentFactory(value: SqSelectHavingFragmentFactory?): T =
    this.setValue(SqSelectHavingFragmentFactory::class.java, value)
val SqSettings.selectHavingFragmentFactory: SqSelectHavingFragmentFactory
    get() = this.getValue(SqSelectHavingFragmentFactory::class.java) ?: SqSelectHavingFragmentImpl.Factory.INSTANCE


fun <T: SqSelect> T.having(condition: SqExpression<*, Boolean>, context: SqContext = SqContext.last): T = this.apply {
    val fragment = context.settings.selectHavingFragmentFactory.invoke(context, condition)
    this.addFragment(fragment)
}
// endregion


// region Fragment "ORDER BY"
fun <T: SqSelect> T.orderBy(columns: List<SqOrderFragmentColumn>, context: SqContext = SqContext.last): T = this.apply {
    val fragment = context.settings.orderFragmentFactory.invoke(context, columns)
    this.addFragment(fragment)
}

fun <T: SqSelect> T.orderBy(column: SqOrderFragmentColumn, vararg moreColumns: SqOrderFragmentColumn, context: SqContext = SqContext.last): T =
    this.orderBy(listOf(column, *moreColumns), context)
// endregion
