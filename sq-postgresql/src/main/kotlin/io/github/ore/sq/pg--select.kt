@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqPgExpressionSelectImpl
import io.github.ore.sq.impl.SqPgSelectFragmentLimitImpl
import io.github.ore.sq.impl.SqPgSelectFragmentOffsetImpl
import io.github.ore.sq.impl.SqPgSelectImpl
import io.github.ore.sq.util.SqItemPartConfig


// region Request, factory
interface SqPgSelect: SqSelect

fun interface SqPgSelectFactory: SqSelectFactory {
    override fun invoke(context: SqContext, distinct: Boolean, columns: List<SqColumn<*, *>>): SqPgSelect
}

fun <T: SqSettingsBuilder> T.pgSelectFactory(value: SqPgSelectFactory?): T =
    this.setValue(SqPgSelectFactory::class.java, value)
val SqSettings.pgSelectFactory: SqPgSelectFactory
    get() = this.getValue(SqPgSelectFactory::class.java) ?: SqPgSelectImpl.Factory.INSTANCE


interface SqPgExpressionSelect<JAVA, DB: Any>: SqPgSelect, SqExpressionSelect<JAVA, DB>

interface SqPgExpressionSelectFactory: SqExpressionSelectFactory {
    override fun <JAVA, DB : Any> create(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, DB>,
        distinct: Boolean,
        columns: List<SqColumn<*, *>>
    ): SqPgExpressionSelect<JAVA, DB>
}

fun <T: SqSettingsBuilder> T.pgExpressionSelectFactory(value: SqPgExpressionSelectFactory?): T =
    this.setValue(SqPgExpressionSelectFactory::class.java, value)
val SqSettings.pgExpressionSelectFactory: SqPgExpressionSelectFactory
    get() = this.getValue(SqPgExpressionSelectFactory::class.java) ?: SqPgExpressionSelectImpl.Factory.INSTANCE


fun SqPgContext.select(distinct: Boolean, columns: List<SqColumn<*, *>>): SqPgSelect =
    this.settings.pgSelectFactory.invoke(this, distinct, columns)

fun SqPgContext.select(
    distinct: Boolean,
    firstColumn: SqColumn<*, *>,
    secondColumn: SqColumn<*, *>,
    vararg moreColumns: SqColumn<*, *>,
): SqPgSelect {
    return this.select(distinct, listOf(firstColumn, secondColumn, *moreColumns))
}

fun <JAVA, DB: Any> SqPgContext.select(distinct: Boolean, column: SqColumn<JAVA, DB>): SqPgExpressionSelect<JAVA, DB> =
    this.settings.pgExpressionSelectFactory.create(this, column.reader, distinct, listOf(column))

fun SqPgContext.select(columns: List<SqColumn<*, *>>): SqPgSelect =
    this.settings.pgSelectFactory.invoke(this, distinct = false, columns)

fun SqPgContext.select(
    firstColumn: SqColumn<*, *>,
    secondColumn: SqColumn<*, *>,
    vararg moreColumns: SqColumn<*, *>,
): SqPgSelect {
    return this.select(distinct = false, listOf(firstColumn, secondColumn, *moreColumns))
}

fun <JAVA, DB: Any> SqPgContext.select(column: SqColumn<JAVA, DB>): SqPgExpressionSelect<JAVA, DB> =
    this.settings.pgExpressionSelectFactory.create(this, column.reader, distinct = false, listOf(column))

fun SqPgContext.selectDistinct(columns: List<SqColumn<*, *>>): SqPgSelect =
    this.settings.pgSelectFactory.invoke(this, distinct = true, columns)

fun SqPgContext.selectDistinct(
    firstColumn: SqColumn<*, *>,
    secondColumn: SqColumn<*, *>,
    vararg moreColumns: SqColumn<*, *>,
): SqPgSelect {
    return this.select(distinct = true, listOf(firstColumn, secondColumn, *moreColumns))
}

fun <JAVA, DB: Any> SqPgContext.selectDistinct(column: SqColumn<JAVA, DB>): SqPgExpressionSelect<JAVA, DB> =
    this.settings.pgExpressionSelectFactory.create(this, column.reader, distinct = true, listOf(column))
// endregion


// region Fragment "OFFSET"
interface SqPgSelectFragmentOffset: SqFragment {
    val value: SqItem

    override fun addToBuilder(
        context: SqContext,
        owner: SqFragmented,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?,
    ) {
        target.keyword("offset").space()
        this.value.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)
    }
}

fun interface SqPgSelectFragmentOffsetFactory {
    operator fun invoke(context: SqContext, value: SqItem): SqPgSelectFragmentOffset
}

fun <T: SqSettingsBuilder> T.pgSelectFragmentOffsetFactory(value: SqPgSelectFragmentOffsetFactory?): T =
    this.setValue(SqPgSelectFragmentOffsetFactory::class.java, value)
val SqSettings.pgSelectFragmentOffsetFactory: SqPgSelectFragmentOffsetFactory
    get() = this.getValue(SqPgSelectFragmentOffsetFactory::class.java) ?: SqPgSelectFragmentOffsetImpl.Factory.INSTANCE


fun <T: SqPgSelect> T.offset(value: SqExpression<*, Number>, context: SqContext = SqContext.last): T = this.apply {
    val fragment = context.settings.pgSelectFragmentOffsetFactory.invoke(context, value)
    this.addFragment(fragment)
}
// endregion


// region Fragment "LIMIT"
interface SqPgSelectFragmentLimit: SqFragment {
    val value: SqItem

    override fun addToBuilder(
        context: SqContext,
        owner: SqFragmented,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?,
    ) {
        target.keyword("limit").space()
        this.value.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)
    }
}

fun interface SqPgSelectFragmentLimitFactory {
    operator fun invoke(context: SqContext, value: SqItem): SqPgSelectFragmentLimit
}

fun <T: SqSettingsBuilder> T.pgSelectFragmentLimitFactory(value: SqPgSelectFragmentLimitFactory?): T =
    this.setValue(SqPgSelectFragmentLimitFactory::class.java, value)
val SqSettings.pgSelectFragmentLimitFactory: SqPgSelectFragmentLimitFactory
    get() = this.getValue(SqPgSelectFragmentLimitFactory::class.java) ?: SqPgSelectFragmentLimitImpl.Factory.INSTANCE


fun <T: SqPgSelect> T.limit(value: SqExpression<*, Number>, context: SqContext = SqContext.last): T = this.apply {
    val fragment = context.settings.pgSelectFragmentLimitFactory.invoke(context, value)
    this.addFragment(fragment)
}
// endregion
