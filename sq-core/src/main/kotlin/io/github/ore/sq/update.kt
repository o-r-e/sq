@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqUpdateColumnValueMapperImpl
import io.github.ore.sq.impl.SqUpdateImpl
import io.github.ore.sq.impl.SqUpdateSetFragmentImpl
import io.github.ore.sq.impl.SqUpdateStartFragmentImpl
import io.github.ore.sq.impl.SqUpdateWhereFragmentImpl
import io.github.ore.sq.util.SqItemPartConfig
import io.github.ore.sq.util.SqUtil
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


// region Request
interface SqUpdate: SqDataModificationRequest, SqFragmented {
    override val isMultiline: Boolean
        get() = true
}

fun interface SqUpdateFactory {
    operator fun invoke(context: SqContext, table: SqItem): SqUpdate
}

fun <T: SqSettingsBuilder> T.updateFactory(value: SqUpdateFactory?): T =
    this.setValue(SqUpdateFactory::class.java, value)
val SqSettings.updateFactory: SqUpdateFactory
    get() = this.getValue(SqUpdateFactory::class.java) ?: SqUpdateImpl.Factory.INSTANCE


fun SqContext.update(table: SqTable): SqUpdate =
    this.settings.updateFactory.invoke(this, table)
// endregion


// region Start fragment
interface SqUpdateStartFragment: SqFragment {
    companion object {
        fun <T: SqUpdate> addToRequest(context: SqContext, table: SqItem, target: T): T {
            val fragment = context.settings.updateStartFragmentFactory.invoke(context, table)
            target.addFragment(fragment)
            return target
        }
    }


    val table: SqItem

    override fun addToBuilder(
        context: SqContext,
        owner: SqFragmented,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?,
    ) {
        target.keyword("update").space()

        val table = this.table
        if (table.isMultiline) {
            target.spaceOrNewLine()
        } else {
            target.space()
        }

        table.addToBuilder(context, target, SqItemPartConfig.INSTANCE__OPTIONAL_BRACKETS)
    }
}

fun interface SqUpdateStartFragmentFactory {
    operator fun invoke(context: SqContext, table: SqItem): SqUpdateStartFragment
}

fun <T: SqSettingsBuilder> T.updateStartFragmentFactory(value: SqUpdateStartFragmentFactory?): T =
    this.setValue(SqUpdateStartFragmentFactory::class.java, value)
val SqSettings.updateStartFragmentFactory: SqUpdateStartFragmentFactory
    get() = this.getValue(SqUpdateStartFragmentFactory::class.java) ?: SqUpdateStartFragmentImpl.Factory.INSTANCE
// endregion


// region Fragment "SET"
interface SqUpdateSetFragment: SqFragment {
    val columnValueMap: Map<SqColumn<*, *>, SqItem>

    override fun addToBuilder(
        context: SqContext,
        owner: SqFragmented,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        target.keyword("set").indent {
            var first = true
            this.columnValueMap.forEach { (column, value) ->
                if (first) {
                    first = false
                } else {
                    target.comma().spaceOrNewLine()
                }

                target.identifier(column.name).space().keyword("=").space()
                value.addToBuilder(context, target, SqItemPartConfig.INSTANCE__REQUIRED_BRACKETS)
            }
        }
    }
}

fun interface SqUpdateSetFragmentFactory {
    operator fun invoke(context: SqContext, columnValueMap: Map<SqColumn<*, *>, SqItem>): SqUpdateSetFragment
}

fun <T: SqSettingsBuilder> T.updateSetFragmentFactory(value: SqUpdateSetFragmentFactory?): T =
    this.setValue(SqUpdateSetFragmentFactory::class.java, value)
val SqSettings.updateSetFragmentFactory: SqUpdateSetFragmentFactory
    get() = this.getValue(SqUpdateSetFragmentFactory::class.java) ?: SqUpdateSetFragmentImpl.Factory.INSTANCE


fun <T: SqUpdate> T.set(columnValueMap: Map<SqColumn<*, *>, SqExpression<*, *>>, context: SqContext = SqContext.last): T = this.apply {
    val fragment = context.settings.updateSetFragmentFactory.invoke(context, columnValueMap)
    this.addFragment(fragment)
}
// endregion


// region Column-value mapper
interface SqUpdateColumnValueMapper {
    val columnValueMap: Map<SqColumn<*, *>, SqItem>

    fun setValue(column: SqColumn<*, *>, value: SqItem)

    fun addToRequest(context: SqContext, target: SqUpdate) {
        val fragment = context.settings.updateSetFragmentFactory.invoke(context, columnValueMap)
        target.addFragment(fragment)
    }

    fun clear()
}

fun interface SqUpdateColumnValueMapperFactory {
    operator fun invoke(context: SqContext): SqUpdateColumnValueMapper
}

fun <T: SqSettingsBuilder> T.updateColumnValueMapperFactory(value: SqUpdateColumnValueMapperFactory?): T =
    this.setValue(SqUpdateColumnValueMapperFactory::class.java, value)
val SqSettings.updateColumnValueMapperFactory: SqUpdateColumnValueMapperFactory
    get() = this.getValue(SqUpdateColumnValueMapperFactory::class.java) ?: SqUpdateColumnValueMapperImpl.Factory.INSTANCE


inline fun <T: SqUpdate> T.set(context: SqContext = SqContext.last, block: (mapper: SqUpdateColumnValueMapper) -> Unit): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

    val mapper = context.settings.updateColumnValueMapperFactory.invoke(context)
    try {
        block(mapper)
        mapper.addToRequest(context, this)
    } finally {
        mapper.clear()
    }
    return this
}

operator fun <DB: Any, T: SqUpdateColumnValueMapper> T.set(column: SqColumn<*, DB>, value: SqExpression<*, DB>): T = this.apply {
    this.setValue(column, value)
}

fun <T: SqUpdateColumnValueMapper> T.set(record: SqRecord, context: SqContext = SqContext.last): T = this.apply {
    val parameters = context.parameters
    val recordClassInfo = SqRecordClassInfo[record.javaClass]
    recordClassInfo.delegateGetterMap.forEach { (propertyName, delegateGetter) ->
        val delegate = delegateGetter[record]
        if (delegate.isDataForUpdate) {
            SqUtil.processRecordFieldColumnParameterPair(
                parameters,
                record,
                recordClassInfo,
                propertyName,
                delegate,
            ) { column, parameter ->
                this[column] = parameter
            }
        }
    }
}
// endregion


// region Fragment "WHERE"
interface SqUpdateWhereFragment: SqFragment {
    val condition: SqItem

    override fun addToBuilder(
        context: SqContext,
        owner: SqFragmented,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?,
    ) {
        target.keyword("where").indent {
            this.condition.addToBuilder(context, target, SqItemPartConfig.INSTANCE__OPTIONAL_BRACKETS)
        }
    }
}

fun interface SqUpdateWhereFragmentFactory {
    operator fun invoke(context: SqContext, condition: SqItem): SqUpdateWhereFragment
}

fun <T: SqSettingsBuilder> T.updateWhereFragmentFactory(value: SqUpdateWhereFragmentFactory?): T =
    this.setValue(SqUpdateWhereFragmentFactory::class.java, value)
val SqSettings.updateWhereFragmentFactory: SqUpdateWhereFragmentFactory
    get() = this.getValue(SqUpdateWhereFragmentFactory::class.java) ?: SqUpdateWhereFragmentImpl.Factory.INSTANCE


fun <T: SqUpdate> T.where(condition: SqExpression<*, Boolean>, context: SqContext = SqContext.last): T = this.apply {
    val fragment = context.settings.updateWhereFragmentFactory.invoke(context, condition)
    this.addFragment(fragment)
}
// endregion


// region Records
fun <T: SqUpdate> T.useRecord(record: SqRecord, context: SqContext = SqContext.last): T = this.apply {
    val parameters = context.parameters
    val recordClassInfo = SqRecordClassInfo[record.javaClass]
    val conditionList = ArrayList<SqExpression<out Any?, Boolean>>()

    this.set(context) { mapper ->
        recordClassInfo.delegateGetterMap.forEach { (propertyName, delegateGetter) ->
            val delegate = delegateGetter[record]
            @Suppress("DuplicatedCode")
            if (delegate.isSearchKeyForUpdate) {
                SqUtil.processRecordFieldColumnParameterPair(
                    parameters,
                    record,
                    recordClassInfo,
                    propertyName,
                    delegate,
                ) { column, parameter ->
                    val condition = column.eq(parameter, context)
                    conditionList.add(condition)
                }
            } else if (delegate.isDataForUpdate) {
                SqUtil.processRecordFieldColumnParameterPair(
                    parameters,
                    record,
                    recordClassInfo,
                    propertyName,
                    delegate,
                ) { column, parameter ->
                    mapper[column] = parameter
                }
            }
        }
    }

    if (conditionList.isEmpty()) {
        error("No search keys for update (without search keys all table records will be updated); invalid record: $record")
    }

    this.where(context.and(conditionList), context)
}


interface SqUpdateRecordsPreparer {
    fun <T: SqRecord> prepare(context: SqContext, wrappedRequest: SqUpdate, record: T): SqRecordReloadRequest<T> {
        val parameters = context.parameters
        val config: SqRecordReloadRequest.ReloadableConfig<T>
        val conditionList = ArrayList<SqExpression<*, Boolean>>()

        wrappedRequest.set(context) { mapper ->
            config = SqRecordReloadRequest.ReloadableConfig
                .create(listOf(record)) { record, recordClassInfo, recordIndex, propertyName, delegate ->
                    @Suppress("DuplicatedCode")
                    if (delegate.isSearchKeyForUpdate) {
                        SqUtil.processRecordFieldColumnParameterPair(
                            parameters,
                            record,
                            recordClassInfo,
                            propertyName,
                            delegate,
                        ) { column, parameter ->
                            val condition = column.eq(parameter, context)
                            conditionList.add(condition)
                        }
                    } else if (delegate.isDataForUpdate) {
                        SqUtil.processRecordFieldColumnParameterPair(
                            parameters,
                            record,
                            recordClassInfo,
                            propertyName,
                            delegate,
                        ) { column, parameter ->
                            mapper[column] = parameter
                        }
                    }

                    delegate.isReloadedAfterUpdate
                }
        }

        if (conditionList.isNotEmpty()) {
            wrappedRequest.where(
                condition = context.and(conditionList),
                context = context,
            )
        }

        @Suppress("UNCHECKED_CAST")
        return context.settings.recordReloadRequestFactory.create(context, wrappedRequest, config)
    }


    object Impl: SqUpdateRecordsPreparer
}

fun <T: SqSettingsBuilder> T.updateRecordsPreparer(value: SqUpdateRecordsPreparer?): T =
    this.setValue(SqUpdateRecordsPreparer::class.java, value)
val SqSettings.updateRecordsPreparer: SqUpdateRecordsPreparer
    get() = this.getValue(SqUpdateRecordsPreparer::class.java) ?: SqUpdateRecordsPreparer.Impl


fun <T: SqRecord> SqUpdate.useAndReloadRecord(record: T, context: SqContext = SqContext.last): SqRecordReloadRequest<T> =
    context.settings.updateRecordsPreparer.prepare(context, this, record)
// endregion
