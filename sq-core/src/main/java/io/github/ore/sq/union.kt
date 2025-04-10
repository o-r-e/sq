@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqExpressionUnionImpl
import io.github.ore.sq.impl.SqUnionImpl
import io.github.ore.sq.impl.SqUnionMainFragmentImpl
import io.github.ore.sq.util.SqItemPartConfig


// region Request
interface SqUnion: SqReadRequest, SqFragmented {
    override val isMultiline: Boolean
        get() = true
}

interface SqUnionFactory {
    fun createUnion(context: SqContext, columns: List<SqColumn<*, *>>, requests: List<SqItem>): SqUnion
    fun createUnionAll(context: SqContext, columns: List<SqColumn<*, *>>, requests: List<SqItem>): SqUnion
}

fun <T: SqSettingsBuilder> T.unionFactory(value: SqUnionFactory?): T =
    this.setValue(SqUnionFactory::class.java, value)
val SqSettings.unionFactory: SqUnionFactory
    get() = this.getValue(SqUnionFactory::class.java) ?: SqUnionImpl.Factory.INSTANCE


interface SqExpressionUnion<JAVA, DB: Any>: SqUnion, SqExpressionReadRequest<JAVA, DB>

interface SqExpressionUnionFactory {
    fun <JAVA, DB: Any> createUnion(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, DB>,
        columns: List<SqColumn<*, *>>,
        requests: List<SqItem>,
    ): SqExpressionUnion<JAVA, DB>

    fun <JAVA, DB: Any> createUnionAll(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, DB>,
        columns: List<SqColumn<*, *>>,
        requests: List<SqItem>,
    ): SqExpressionUnion<JAVA, DB>
}

fun <T: SqSettingsBuilder> T.expressionUnionFactory(value: SqExpressionUnionFactory?): T =
    this.setValue(SqExpressionUnionFactory::class.java, value)
val SqSettings.expressionUnionFactory: SqExpressionUnionFactory
    get() = this.getValue(SqExpressionUnionFactory::class.java) ?: SqExpressionUnionImpl.Factory.INSTANCE


fun SqContext.union(requests: List<SqColumnSource>): SqUnion {
    if (requests.isEmpty()) {
        error("Request list is empty")
    }
    return this.settings.unionFactory.createUnion(this, requests.first().columns, requests)
}

fun SqContext.union(request: SqColumnSource, vararg moreRequests: SqColumnSource): SqUnion =
    this.settings.unionFactory.createUnion(this, request.columns, listOf(request, *moreRequests))

fun SqContext.unionAll(requests: List<SqColumnSource>): SqUnion {
    if (requests.isEmpty()) {
        error("Request list is empty")
    }
    return this.settings.unionFactory.createUnionAll(this, requests.first().columns, requests)
}

fun SqContext.unionAll(request: SqColumnSource, vararg moreRequests: SqColumnSource): SqUnion =
    this.settings.unionFactory.createUnionAll(this, request.columns, listOf(request, *moreRequests))


@JvmName("union__notNull")
fun <JAVA: Any, DB: Any> SqContext.union(requests: List<SqExpressionColumnSource<JAVA, DB>>): SqExpressionUnion<JAVA, DB> {
    if (requests.isEmpty()) {
        error("Request list is empty")
    }
    val firstRequest = requests.first()
    return this.settings.expressionUnionFactory.createUnion(this, firstRequest.reader, firstRequest.columns, requests)
}

@JvmName("union__nullable")
fun <JAVA: Any, DB: Any> SqContext.union(requests: List<SqExpressionColumnSource<out JAVA?, DB>>): SqExpressionUnion<out JAVA?, DB> {
    if (requests.isEmpty()) {
        error("Request list is empty")
    }
    val firstRequest = requests.first()
    val reader = firstRequest.reader.nullableReader
    return this.settings.expressionUnionFactory.createUnion(this, reader, firstRequest.columns, requests)
}

@JvmName("union__notNull")
fun <JAVA: Any, DB: Any> SqContext.union(
    request: SqExpressionColumnSource<JAVA, DB>,
    vararg moreRequests: SqExpressionColumnSource<JAVA, DB>,
): SqExpressionUnion<JAVA, DB> {
    return this.settings.expressionUnionFactory.createUnion(this, request.reader, request.columns, listOf(request, *moreRequests))
}

@JvmName("union__nullable")
fun <JAVA: Any, DB: Any> SqContext.union(
    request: SqExpressionColumnSource<out JAVA?, DB>,
    vararg moreRequests: SqExpressionColumnSource<out JAVA?, DB>,
): SqExpressionUnion<out JAVA?, DB> {
    return this.settings.expressionUnionFactory.createUnion(this, request.reader.nullableReader, request.columns, listOf(request, *moreRequests))
}

@JvmName("unionAll__notNull")
fun <JAVA: Any, DB: Any> SqContext.unionAll(requests: List<SqExpressionColumnSource<JAVA, DB>>): SqExpressionUnion<JAVA, DB> {
    if (requests.isEmpty()) {
        error("Request list is empty")
    }
    val firstRequest = requests.first()
    return this.settings.expressionUnionFactory.createUnionAll(this, firstRequest.reader, firstRequest.columns, requests)
}

@JvmName("unionAll__nullable")
fun <JAVA: Any, DB: Any> SqContext.unionAll(requests: List<SqExpressionColumnSource<out JAVA?, DB>>): SqExpressionUnion<out JAVA?, DB> {
    if (requests.isEmpty()) {
        error("Request list is empty")
    }
    val firstRequest = requests.first()
    val reader = firstRequest.reader.nullableReader
    return this.settings.expressionUnionFactory.createUnionAll(this, reader, firstRequest.columns, requests)
}

@JvmName("unionAll__notNull")
fun <JAVA: Any, DB: Any> SqContext.unionAll(
    request: SqExpressionColumnSource<JAVA, DB>,
    vararg moreRequests: SqExpressionColumnSource<JAVA, DB>,
): SqExpressionUnion<JAVA, DB> {
    return this.settings.expressionUnionFactory.createUnionAll(this, request.reader, request.columns, listOf(request, *moreRequests))
}

@JvmName("unionAll__nullable")
fun <JAVA: Any, DB: Any> SqContext.unionAll(
    request: SqExpressionColumnSource<out JAVA?, DB>,
    vararg moreRequests: SqExpressionColumnSource<out JAVA?, DB>,
): SqExpressionUnion<out JAVA?, DB> {
    return this.settings.expressionUnionFactory.createUnionAll(this, request.reader.nullableReader, request.columns, listOf(request, *moreRequests))
}
// endregion


// region Main fragment
interface SqUnionMainFragment: SqFragment {
    val separatorKeyword: String
    val requests: List<SqItem>

    override fun addToBuilder(
        context: SqContext,
        owner: SqFragmented,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        val separatorKeyword = this.separatorKeyword
        this.requests.forEachIndexed { index, request ->
            if (index > 0) {
                target.spaceOrNewLine().keyword(separatorKeyword).spaceOrNewLine()
            }
            request.definition.addToBuilder(context, target, SqItemPartConfig.INSTANCE__OPTIONAL_BRACKETS)
        }
    }
}

interface SqUnionMainFragmentFactory {
    fun createUnion(context: SqContext, requests: List<SqItem>): SqUnionMainFragment
    fun createUnionAll(context: SqContext, requests: List<SqItem>): SqUnionMainFragment
}

fun <T: SqSettingsBuilder> T.unionMainFragmentFactory(value: SqUnionMainFragmentFactory?): T =
    this.setValue(SqUnionMainFragmentFactory::class.java, value)
val SqSettings.unionMainFragmentFactory: SqUnionMainFragmentFactory
    get() = this.getValue(SqUnionMainFragmentFactory::class.java) ?: SqUnionMainFragmentImpl.Factory.INSTANCE
// endregion


// region Fragment "ORDER BY"
fun <T: SqUnion> T.orderBy(columns: List<SqOrderFragmentColumn>, context: SqContext = SqContext.last): T = this.apply {
    val fragment = context.settings.orderFragmentFactory.invoke(context, columns)
    this.addFragment(fragment)
}

fun <T: SqUnion> T.orderBy(column: SqOrderFragmentColumn, vararg moreColumns: SqOrderFragmentColumn, context: SqContext = SqContext.last): T =
    this.orderBy(listOf(column, *moreColumns), context)
// endregion
