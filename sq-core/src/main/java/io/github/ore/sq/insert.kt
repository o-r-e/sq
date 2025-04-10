@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.*
import io.github.ore.sq.util.SqItemPartConfig
import io.github.ore.sq.util.SqUtil
import java.sql.Connection
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


// region Request
interface SqInsert: SqDataModificationRequest, SqFragmented {
    override val isMultiline: Boolean
        get() = true
}

fun interface SqInsertFactory {
    operator fun invoke(context: SqContext, table: SqItem): SqInsert
}

fun <T: SqSettingsBuilder> T.insertFactory(value: SqInsertFactory?): T =
    this.setValue(SqInsertFactory::class.java, value)
val SqSettings.insertFactory: SqInsertFactory
    get() = this.getValue(SqInsertFactory::class.java) ?: SqInsertImpl.Factory.INSTANCE


fun SqContext.insertInto(table: SqTable): SqInsert =
    this.settings.insertFactory.invoke(this, table)
// endregion


// region Start fragment
interface SqInsertStartFragment: SqFragment {
    companion object {
        fun <T: SqInsert> addToRequest(context: SqContext, table: SqItem, target: T): T {
            val fragment = context.settings.insertStartFragmentFactory.invoke(context, table)
            target.addFragment(fragment)
            return target
        }
    }


    val table: SqItem

    override fun addToBuilder(
        context: SqContext,
        owner: SqFragmented,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        target.keyword("insert into").space()
        this.table.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)
    }
}

fun interface SqInsertStartFragmentFactory {
    operator fun invoke(context: SqContext, table: SqItem): SqInsertStartFragment
}

fun <T: SqSettingsBuilder> T.insertStartFragmentFactory(value: SqInsertStartFragmentFactory?): T =
    this.setValue(SqInsertStartFragmentFactory::class.java, value)
val SqSettings.insertStartFragmentFactory: SqInsertStartFragmentFactory
    get() = this.getValue(SqInsertStartFragmentFactory::class.java) ?: SqInsertStartFragmentImpl.Factory.INSTANCE
// endregion


// region Fragment with columns
interface SqInsertColumnsFragment: SqFragment {
    val columns: List<SqColumn<*, *>>

    fun addColumnsToBuilder(
        context: SqContext,
        owner: SqFragmented,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?,
        columns: List<SqColumn<*, *>>,
        isMultiline: Boolean,
    ) {
        @Suppress("DuplicatedCode")
        columns.forEachIndexed { index, column ->
            if (index > 0) {
                target.comma()
                if (isMultiline) {
                    target.spaceOrNewLine()
                } else {
                    target.space()
                }
            }
            target.identifier(column.name)
        }
    }

    override fun addToBuilder(
        context: SqContext,
        owner: SqFragmented,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?,
    ) {
        val columns = this.columns
        val isMultiline = columns.any { it.isMultiline }
        target.indent {
            if (isMultiline) {
                target.bracketsWithIndent {
                    this.addColumnsToBuilder(context, owner, target, partConfig, columns, true)
                }
            } else {
                target.brackets {
                    this.addColumnsToBuilder(context, owner, target, partConfig, columns, false)
                }
            }
        }
    }
}

fun interface SqInsertColumnsFragmentFactory {
    operator fun invoke(context: SqContext, columns: List<SqColumn<*, *>>): SqInsertColumnsFragment
}

fun <T: SqSettingsBuilder> T.insertColumnsFragmentFactory(value: SqInsertColumnsFragmentFactory?): T =
    this.setValue(SqInsertColumnsFragmentFactory::class.java, value)
val SqSettings.insertColumnsFragmentFactory: SqInsertColumnsFragmentFactory
    get() = this.getValue(SqInsertColumnsFragmentFactory::class.java) ?: SqInsertColumnsFragmentImpl.Factory.INSTANCE


fun <T: SqInsert> T.columns(columns: List<SqColumn<*, *>>, context: SqContext = SqContext.last): T = this.apply {
    val fragment = context.settings.insertColumnsFragmentFactory.invoke(context, columns)
    this.addFragment(fragment)
}

fun <T: SqInsert> T.columns(column: SqColumn<*, *>, vararg moreColumns: SqColumn<*, *>, context: SqContext = SqContext.last): T =
    this.columns(listOf(column, *moreColumns), context)
// endregion


// region Fragment "VALUES"
interface SqInsertValuesFragment: SqFragment {
    val valueRows: List<List<SqItem>>

    fun addRowToBuilder(
        context: SqContext,
        owner: SqFragmented,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?,
        row: List<SqItem>,
        isMultiline: Boolean,
    ) {
        @Suppress("DuplicatedCode")
        row.forEachIndexed { index, parameter ->
            if (index > 0) {
                target.comma()
                if (isMultiline) {
                    target.spaceOrNewLine()
                } else {
                    target.space()
                }
            }
            parameter.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)
        }
    }

    override fun addToBuilder(
        context: SqContext,
        owner: SqFragmented,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?,
    ) {
        target.keyword("values").space().indent {
            this.valueRows.forEachIndexed { index, row ->
                if (index > 0) {
                    target.comma().spaceOrNewLine()
                }

                val isMultiline = row.any { it.isMultiline }
                if (isMultiline) {
                    target.bracketsWithIndent {
                        this.addRowToBuilder(context, owner, target, partConfig, row, true)
                    }
                } else {
                    target.brackets {
                        this.addRowToBuilder(context, owner, target, partConfig, row, false)
                    }
                }
            }
        }
    }
}

fun interface SqInsertValuesFragmentFactory {
    operator fun invoke(context: SqContext, valueRows: List<List<SqItem>>): SqInsertValuesFragment
}

fun <T: SqSettingsBuilder> T.insertValuesFragmentFactory(value: SqInsertValuesFragmentFactory?): T =
    this.setValue(SqInsertValuesFragmentFactory::class.java, value)
val SqSettings.insertValuesFragmentFactory: SqInsertValuesFragmentFactory
    get() = this.getValue(SqInsertValuesFragmentFactory::class.java) ?: SqInsertValuesFragmentImpl.Factory.INSTANCE


@JvmName("values__valueRows")
fun <T: SqInsert> T.values(valueRows: List<List<SqExpression<*, *>>>, context: SqContext = SqContext.last): T = this.apply {
    val fragment = context.settings.insertValuesFragmentFactory.invoke(context, valueRows)
    this.addFragment(fragment)
}

@JvmName("values__valueRows")
fun <T: SqInsert> T.values(
    firstValueRow: List<SqExpression<*, *>>,
    secondValueRow: List<SqExpression<*, *>>,
    vararg moreValueRows: List<SqExpression<*, *>>,
    context: SqContext = SqContext.last,
): T {
    return this.values(listOf(firstValueRow, secondValueRow, *moreValueRows), context)
}

@JvmName("values__valueRow")
fun <T: SqInsert> T.values(valueRow: List<SqExpression<*, *>>, context: SqContext = SqContext.last): T =
    this.values(listOf(valueRow), context)

@JvmName("values__valueRow")
fun <T: SqInsert> T.values(
    value: SqExpression<*, *>,
    vararg moreValues: SqExpression<*, *>,
    context: SqContext = SqContext.last,
): T {
    return this.values(listOf(listOf(value, *moreValues)), context)
}
// endregion


// region Column-value mapper
interface SqInsertColumnValueMapper {
    val columns: Collection<SqColumn<*, *>>
    val valueRows: Collection<Map<SqItem, SqItem>>

    fun addToRequest(context: SqContext, target: SqInsert) {
        val columns = ArrayList(this.columns)
        val values = this.valueRows.map { valueRow ->
            columns.map { column ->
                valueRow[column]
                    ?: context.nulls.jObject()
            }
        }

        val columnFragment = context.settings.insertColumnsFragmentFactory.invoke(context, columns)
        val valuesFragment = context.settings.insertValuesFragmentFactory.invoke(context, values)

        target.addFragment(columnFragment)
        target.addFragment(valuesFragment)
    }

    fun setValue(rowIndex: Int, column: SqColumn<*, *>, value: SqItem)

    fun clear()
}

fun interface SqInsertColumnValueMapperFactory {
    operator fun invoke(context: SqContext): SqInsertColumnValueMapper
}

fun <T: SqSettingsBuilder> T.insertColumnValueMapperFactory(value: SqInsertColumnValueMapperFactory?): T =
    this.setValue(SqInsertColumnValueMapperFactory::class.java, value)
val SqSettings.insertColumnValueMapperFactory: SqInsertColumnValueMapperFactory
    get() = this.getValue(SqInsertColumnValueMapperFactory::class.java) ?: SqInsertColumnValueMapperImpl.Factory.INSTANCE


operator fun <DB: Any, T: SqInsertColumnValueMapper> T.set(
    rowIndex: Int,
    column: SqColumn<*, DB>,
    value: SqExpression<*, DB>,
): T {
    this.setValue(rowIndex, column, value)
    return this
}

operator fun <T: SqInsertColumnValueMapper> T.set(rowIndex: Int, context: SqContext, record: SqRecord): T = this.apply {
    val parameters = context.parameters
    val recordClassInfo = SqRecordClassInfo[record.javaClass]
    recordClassInfo.delegateGetterMap.forEach { (propertyName, delegateGetter) ->
        val delegate = delegateGetter[record]
        if (delegate.isDataForInsert) {
            SqUtil.processRecordFieldColumnParameterPair(
                parameters,
                record,
                recordClassInfo,
                propertyName,
                delegate,
            ) { column, parameter ->
                this[rowIndex, column] = parameter
            }
        }
    }
}

operator fun <T: SqInsertColumnValueMapper> T.set(rowIndex: Int, record: SqRecord): T =
    this.set(rowIndex, SqContext.last, record)


inline fun <T: SqInsert> T.columnValues(context: SqContext = SqContext.last, block: (mapper: SqInsertColumnValueMapper) -> Unit): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

    val columns: List<SqItem>
    val valueRows: List<List<SqItem>>

    val mapper = context.settings.insertColumnValueMapperFactory.invoke(context)
    try {
        block(mapper)

        columns = ArrayList(mapper.columns)
        valueRows = mapper.valueRows.map { valueRow ->
            columns.map { column ->
                valueRow[column]
                    ?: context.nulls.jObject()
            }
        }
    } finally {
        mapper.clear()
    }

    val columnsFragment: SqInsertColumnsFragment = context.settings.insertColumnsFragmentFactory.invoke(context, columns)
    val valuesFragment: SqInsertValuesFragment = context.settings.insertValuesFragmentFactory.invoke(context, valueRows)

    this.addFragment(columnsFragment)
    this.addFragment(valuesFragment)

    return this
}
// endregion


// region Fragment "SELECT"
interface SqInsertSelectFragment: SqFragment {
    val select: SqItem

    override fun addToBuilder(
        context: SqContext,
        owner: SqFragmented,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        this.select.addToBuilder(context, target, null)
    }
}

fun interface SqInsertSelectFragmentFactory {
    operator fun invoke(context: SqContext, select: SqItem): SqInsertSelectFragment
}

fun <T: SqSettingsBuilder> T.insertSelectFragmentFactory(value: SqInsertSelectFragmentFactory?): T =
    this.setValue(SqInsertSelectFragmentFactory::class.java, value)
val SqSettings.insertSelectFragmentFactory: SqInsertSelectFragmentFactory
    get() = this.getValue(SqInsertSelectFragmentFactory::class.java) ?: SqInsertSelectFragmentImpl.Factory.INSTANCE


fun <T: SqInsert> T.select(select: SqSelect, context: SqContext = SqContext.last): T = this.apply {
    val fragment = context.settings.insertSelectFragmentFactory.invoke(context, select)
    this.addFragment(fragment)
}

inline fun <T: SqInsert> T.select(
    distinct: Boolean,
    insertColumnToSelectColumnMap: Map<SqColumn<*, *>, SqColumn<*, *>>,
    context: SqContext = SqContext.last,
    block: (select: SqSelect) -> Unit
): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

    val insertColumns = ArrayList<SqColumn<*, *>>(insertColumnToSelectColumnMap.size)
    val selectColumns = ArrayList<SqColumn<*, *>>(insertColumnToSelectColumnMap.size)
    insertColumnToSelectColumnMap.forEach { (insertColumn, selectColumn) ->
        insertColumns.add(insertColumn)
        selectColumns.add(selectColumn)
    }

    val select = context.select(distinct, selectColumns)
    block(select)

    return this.columns(insertColumns).select(select)
}

inline fun <T: SqInsert> T.select(
    insertColumnToSelectColumnMap: Map<SqColumn<*, *>, SqColumn<*, *>>,
    context: SqContext = SqContext.last,
    block: (select: SqSelect) -> Unit
): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return this.select(false, insertColumnToSelectColumnMap, context, block)
}

inline fun <T: SqInsert> T.selectDistinct(
    insertColumnToSelectColumnMap: Map<SqColumn<*, *>, SqColumn<*, *>>,
    context: SqContext = SqContext.last,
    block: (select: SqSelect) -> Unit
): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return this.select(true, insertColumnToSelectColumnMap, context, block)
}

inline fun <T: SqInsert> T.select(
    distinct: Boolean,
    buildInsertColumnToSelectColumnMap: (map: MutableMap<SqColumn<*, *>, SqColumn<*, *>>) -> Unit,
    context: SqContext = SqContext.last,
    block: (select: SqSelect) -> Unit
): T {
    contract {
        callsInPlace(buildInsertColumnToSelectColumnMap, InvocationKind.EXACTLY_ONCE)
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val insertColumnToSelectColumnMap = buildMap(buildInsertColumnToSelectColumnMap)
    return this.select(distinct, insertColumnToSelectColumnMap, context, block)
}

inline fun <T: SqInsert> T.select(
    buildInsertColumnToSelectColumnMap: (map: MutableMap<SqColumn<*, *>, SqColumn<*, *>>) -> Unit,
    context: SqContext = SqContext.last,
    block: (select: SqSelect) -> Unit
): T {
    contract {
        callsInPlace(buildInsertColumnToSelectColumnMap, InvocationKind.EXACTLY_ONCE)
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return this.select(false, buildInsertColumnToSelectColumnMap, context, block)
}

inline fun <T: SqInsert> T.selectDistinct(
    buildInsertColumnToSelectColumnMap: (map: MutableMap<SqColumn<*, *>, SqColumn<*, *>>) -> Unit,
    context: SqContext = SqContext.last,
    block: (select: SqSelect) -> Unit
): T {
    contract {
        callsInPlace(buildInsertColumnToSelectColumnMap, InvocationKind.EXACTLY_ONCE)
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return this.select(true, buildInsertColumnToSelectColumnMap, context, block)
}
// endregion


// region Records
fun <T: SqInsert> T.useRecords(records: List<SqRecord>, startIndex: Int = 0, context: SqContext = SqContext.last): T = this.apply {
    this.columnValues(context) { mapper ->
        records.forEachIndexed { recordIndex, record ->
            mapper[recordIndex + startIndex, context] = record
        }
    }
}

fun <T: SqInsert> T.useRecords(record: SqRecord, vararg moreRecords: SqRecord, startIndex: Int = 0, context: SqContext = SqContext.last): T =
    this.useRecords(listOf(record, *moreRecords), startIndex, context)


interface SqInsertRecordPreparer {
    fun <T: SqRecord> prepare(context: SqContext, wrappedRequest: SqInsert, records: List<T>): SqRecordReloadRequest<T> {
        if (records.isEmpty()) {
            error("Record list is empty")
        }

        val parameters = context.parameters
        val config: SqRecordReloadRequest.ReloadableConfig<T>

        wrappedRequest.columnValues(context) { mapper ->
            config = SqRecordReloadRequest.ReloadableConfig
                .create(records) { record, recordClassInfo, recordIndex, propertyName, delegate ->
                    if (delegate.isDataForInsert) {
                        SqUtil.processRecordFieldColumnParameterPair(
                            parameters,
                            record,
                            recordClassInfo,
                            propertyName,
                            delegate,
                        ) { column, parameter ->
                            mapper[recordIndex, column] = parameter
                        }
                    }

                    delegate.isReloadedAfterInsert
                }
        }

        @Suppress("UNCHECKED_CAST")
        return context.settings.recordReloadRequestFactory.create(context, wrappedRequest, config)
    }

    object Impl: SqInsertRecordPreparer
}

fun <T: SqSettingsBuilder> T.insertRecordPreparer(value: SqInsertRecordPreparer?): T =
    this.setValue(SqInsertRecordPreparer::class.java, value)
val SqSettings.insertRecordPreparer: SqInsertRecordPreparer
    get() = this.getValue(SqInsertRecordPreparer::class.java) ?: SqInsertRecordPreparer.Impl


fun <T: SqRecord> SqInsert.useAndReloadRecords(records: List<T>, context: SqContext = SqContext.last): SqRecordReloadRequest<T> =
    context.settings.insertRecordPreparer.prepare(context, this, records)

fun <T: SqRecord> SqInsert.useAndReloadRecords(record: T, vararg moreRecords: T, context: SqContext = SqContext.last): SqRecordReloadRequest<T> =
    this.useAndReloadRecords(listOf(record, *moreRecords), context)
// endregion
