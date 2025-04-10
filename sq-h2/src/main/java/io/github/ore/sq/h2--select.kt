@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqH2ExpressionSelectImpl
import io.github.ore.sq.impl.SqH2OffsetFragmentImpl
import io.github.ore.sq.impl.SqH2SelectFragmentFetchImpl
import io.github.ore.sq.impl.SqH2SelectImpl
import io.github.ore.sq.util.SqItemPartConfig


// region Request, factory
interface SqH2Select: SqSelect

fun interface SqH2SelectFactory: SqSelectFactory {
    override fun invoke(context: SqContext, distinct: Boolean, columns: List<SqColumn<*, *>>): SqH2Select
}

fun <T: SqSettingsBuilder> T.h2SelectFactory(value: SqH2SelectFactory?): T =
    this.setValue(SqH2SelectFactory::class.java, value)
val SqSettings.h2SelectFactory: SqH2SelectFactory
    get() = this.getValue(SqH2SelectFactory::class.java) ?: SqH2SelectImpl.Factory.INSTANCE


interface SqH2ExpressionSelect<JAVA, DB: Any>: SqH2Select, SqExpressionSelect<JAVA, DB>

interface SqH2ExpressionSelectFactory: SqExpressionSelectFactory {
    override fun <JAVA, DB : Any> create(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, DB>,
        distinct: Boolean,
        columns: List<SqColumn<*, *>>
    ): SqH2ExpressionSelect<JAVA, DB>
}

fun <T: SqSettingsBuilder> T.h2ExpressionSelectFactory(value: SqH2ExpressionSelectFactory?): T =
    this.setValue(SqH2ExpressionSelectFactory::class.java, value)
val SqSettings.h2ExpressionSelectFactory: SqH2ExpressionSelectFactory
    get() = this.getValue(SqH2ExpressionSelectFactory::class.java) ?: SqH2ExpressionSelectImpl.Factory.INSTANCE


fun SqH2Context.select(distinct: Boolean, columns: List<SqColumn<*, *>>): SqH2Select =
    this.settings.h2SelectFactory.invoke(this, distinct, columns)

fun SqH2Context.select(
    distinct: Boolean,
    firstColumn: SqColumn<*, *>,
    secondColumn: SqColumn<*, *>,
    vararg moreColumns: SqColumn<*, *>,
): SqH2Select {
    return this.select(distinct, listOf(firstColumn, secondColumn, *moreColumns))
}

fun <JAVA, DB: Any> SqH2Context.select(distinct: Boolean, column: SqColumn<JAVA, DB>): SqH2ExpressionSelect<JAVA, DB> =
    this.settings.h2ExpressionSelectFactory.create(this, column.reader, distinct, listOf(column))

fun SqH2Context.select(columns: List<SqColumn<*, *>>): SqH2Select =
    this.settings.h2SelectFactory.invoke(this, distinct = false, columns)

fun SqH2Context.select(
    firstColumn: SqColumn<*, *>,
    secondColumn: SqColumn<*, *>,
    vararg moreColumns: SqColumn<*, *>,
): SqH2Select {
    return this.select(distinct = false, listOf(firstColumn, secondColumn, *moreColumns))
}

fun <JAVA, DB: Any> SqH2Context.select(column: SqColumn<JAVA, DB>): SqH2ExpressionSelect<JAVA, DB> =
    this.settings.h2ExpressionSelectFactory.create(this, column.reader, distinct = false, listOf(column))

fun SqH2Context.selectDistinct(columns: List<SqColumn<*, *>>): SqH2Select =
    this.settings.h2SelectFactory.invoke(this, distinct = true, columns)

fun SqH2Context.selectDistinct(
    firstColumn: SqColumn<*, *>,
    secondColumn: SqColumn<*, *>,
    vararg moreColumns: SqColumn<*, *>,
): SqH2Select {
    return this.select(distinct = true, listOf(firstColumn, secondColumn, *moreColumns))
}

fun <JAVA, DB: Any> SqH2Context.selectDistinct(column: SqColumn<JAVA, DB>): SqH2ExpressionSelect<JAVA, DB> =
    this.settings.h2ExpressionSelectFactory.create(this, column.reader, distinct = true, listOf(column))
// endregion


// region Fragment "OFFSET"
interface SqH2SelectFragmentOffset: SqFragment {
    val value: SqItem

    override fun addToBuilder(
        context: SqContext,
        owner: SqFragmented,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?,
    ) {
        target.keyword("offset").indent {
            this.value.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)
            target.space().keyword("rows")
        }
    }
}

fun interface SqH2SelectFragmentOffsetFactory {
    operator fun invoke(context: SqContext, value: SqItem): SqH2SelectFragmentOffset
}

fun <T: SqSettingsBuilder> T.h2SelectFragmentOffsetFactory(value: SqH2SelectFragmentOffsetFactory?): T =
    this.setValue(SqH2SelectFragmentOffsetFactory::class.java, value)
val SqSettings.h2SelectFragmentOffsetFactory: SqH2SelectFragmentOffsetFactory
    get() = this.getValue(SqH2SelectFragmentOffsetFactory::class.java) ?: SqH2OffsetFragmentImpl.Factory.INSTANCE


fun <T: SqH2Select> T.offset(value: SqExpression<*, Number>, context: SqContext = SqContext.last): T = this.apply {
    val fragment = context.settings.h2SelectFragmentOffsetFactory.invoke(context, value)
    this.addFragment(fragment)
}
// endregion


// region Fragment "FETCH"
interface SqH2SelectFragmentFetch: SqFragment {
    /**
     * `FIRST` / `NEXT`
     */
    val fetchFirst: Boolean

    val value: SqItem

    /**
     * Optional `PERCENT`
     */
    val percent: Boolean

    /**
     * Optional `WITH TIES`; if `false`, then `ONLY` will be used
     */
    val withTies: Boolean

    override fun addToBuilder(
        context: SqContext,
        owner: SqFragmented,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?,
    ) {
        target.keyword("fetch").space()

        // "FIRST" / "NEXT"
        if (this.fetchFirst) {
            target.keyword("first")
        } else {
            target.keyword("next")
        }

        target.indent {
            this.value.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)

            // Optional "PERCENT"
            if (this.percent) {
                target.space().keyword("percent")
            }

            target.space().keyword("rows")

            // "WITH TIES" / "ONLY"
            if (this.withTies) {
                target.space().keyword("with ties")
            } else {
                target.space().keyword("only")
            }
        }
    }
}

fun interface SqH2SelectFragmentFetchFactory {
    operator fun invoke(
        context: SqContext,
        value: SqItem,
        fetchFirst: Boolean,
        percent: Boolean,
        withTies: Boolean,
    ): SqH2SelectFragmentFetch
}

fun <T: SqSettingsBuilder> T.h2SelectFragmentFetchFactory(value: SqH2SelectFragmentFetchFactory?): T =
    this.setValue(SqH2SelectFragmentFetchFactory::class.java, value)
val SqSettings.h2SelectFragmentFetchFactory: SqH2SelectFragmentFetchFactory
    get() = this.getValue(SqH2SelectFragmentFetchFactory::class.java) ?: SqH2SelectFragmentFetchImpl.Factory.INSTANCE


fun <T: SqH2Select> T.fetch(
    first: Boolean,
    value: SqExpression<*, Number>,
    percent: Boolean = false,
    withTies: Boolean = false,
    context: SqContext = SqContext.last,
): T {
    val fragment = context.settings.h2SelectFragmentFetchFactory.invoke(context, value, first, percent, withTies)
    this.addFragment(fragment)
    return this
}

fun <T: SqH2Select> T.fetchFirst(
    value: SqExpression<*, Number>,
    percent: Boolean = false,
    withTies: Boolean = false,
    context: SqContext = SqContext.last,
): T {
    return this.fetch(first = true, value, percent, withTies, context)
}

fun <T: SqH2Select> T.fetchNext(
    value: SqExpression<*, Number>,
    percent: Boolean = false,
    withTies: Boolean = false,
    context: SqContext = SqContext.last,
): T {
    return this.fetch(first = false, value, percent, withTies, context)
}
// endregion
