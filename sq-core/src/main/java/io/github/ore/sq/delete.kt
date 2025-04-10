@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqDeleteImpl
import io.github.ore.sq.impl.SqDeleteStartFragmentImpl
import io.github.ore.sq.impl.SqDeleteWhereFragmentImpl
import io.github.ore.sq.util.SqItemPartConfig
import io.github.ore.sq.util.SqUtil


// region Request
interface SqDelete: SqDataModificationRequest, SqFragmented {
    override val isMultiline: Boolean
        get() = true
}

fun interface SqDeleteFactory {
    operator fun invoke(context: SqContext, table: SqItem): SqDelete
}

fun <T: SqSettingsBuilder> T.deleteFactory(value: SqDeleteFactory?): T =
    this.setValue(SqDeleteFactory::class.java, value)
val SqSettings.deleteFactory: SqDeleteFactory
    get() = this.getValue(SqDeleteFactory::class.java) ?: SqDeleteImpl.Factory.INSTANCE


fun SqContext.deleteFrom(table: SqTable): SqDelete =
    this.settings.deleteFactory.invoke(this, table)
// endregion


// region Start fragment
interface SqDeleteStartFragment: SqFragment {
    companion object {
        fun <T: SqDelete> addToRequest(context: SqContext, table: SqItem, target: T): T {
            val fragment = context.settings.deleteStartFragmentFactory.invoke(context, table)
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
        target.keyword("delete from").space()
        this.table.addToBuilder(context, target, SqItemPartConfig.INSTANCE__OPTIONAL_BRACKETS)
    }
}

fun interface SqDeleteStartFragmentFactory {
    operator fun invoke(context: SqContext, table: SqItem): SqDeleteStartFragment
}

fun <T: SqSettingsBuilder> T.deleteStartFragmentFactory(value: SqDeleteStartFragmentFactory?): T =
    this.setValue(SqDeleteStartFragmentFactory::class.java, value)
val SqSettings.deleteStartFragmentFactory: SqDeleteStartFragmentFactory
    get() = this.getValue(SqDeleteStartFragmentFactory::class.java) ?: SqDeleteStartFragmentImpl.Factory.INSTANCE
// endregion


// region Fragment "WHERE"
interface SqDeleteWhereFragment: SqFragment {
    val condition: SqItem

    override fun addToBuilder(
        context: SqContext,
        owner: SqFragmented,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        target.keyword("where").indent {
            this.condition.addToBuilder(context, target, SqItemPartConfig.INSTANCE__OPTIONAL_BRACKETS)
        }
    }
}

fun interface SqDeleteWhereFragmentFactory {
    operator fun invoke(context: SqContext, condition: SqItem): SqDeleteWhereFragment
}

fun <T: SqSettingsBuilder> T.deleteWhereFragmentFactory(value: SqDeleteWhereFragmentFactory?): T =
    this.setValue(SqDeleteWhereFragmentFactory::class.java, value)
val SqSettings.deleteWhereFragmentFactory: SqDeleteWhereFragmentFactory
    get() = this.getValue(SqDeleteWhereFragmentFactory::class.java) ?: SqDeleteWhereFragmentImpl.Factory.INSTANCE


fun <T: SqDelete> T.where(condition: SqExpression<*, Boolean>, context: SqContext = SqContext.last): T = this.apply {
    val fragment = context.settings.deleteWhereFragmentFactory.invoke(context, condition)
    this.addFragment(fragment)
}
// endregion


// region Records
fun <T: SqDelete> T.useRecords(records: List<SqRecord>, context: SqContext = SqContext.last): T = this.apply {
    if (records.isEmpty()) {
        error("Record list is empty")
    }

    val parameters = context.parameters
    val conditionList = ArrayList<SqExpression<out Any?, Boolean>>()

    records.forEachIndexed { recordIndex, record ->
        val recordConditionList = ArrayList<SqExpression<out Any?, Boolean>>()
        val recordClassInfo = SqRecordClassInfo[record.javaClass]

        recordClassInfo.delegateGetterMap.forEach { (propertyName, delegateGetter) ->
            val delegate = delegateGetter[record]
            if (delegate.isSearchKeyForDelete) {
                SqUtil.processRecordFieldColumnParameterPair(
                    parameters,
                    record,
                    recordClassInfo,
                    propertyName,
                    delegate,
                ) { column, parameter ->
                    val condition = column.eq(parameter, context)
                    recordConditionList.add(condition)
                }
            }
        }

        if (recordConditionList.isEmpty()) {
            error("No search keys for delete (without search keys all table records will be deleted); invalid record index and record: #$recordIndex, $record")
        }

        conditionList.add(context.and(recordConditionList))
    }

    this.where(context.or(conditionList), context)
}

fun <T: SqDelete> T.useRecords(record: SqRecord, vararg moreRecords: SqRecord, context: SqContext = SqContext.last): T =
    this.useRecords(listOf(record, *moreRecords), context)


interface SqDeleteRecordsPreparer {
    fun <T: SqRecord> prepare(context: SqContext, wrappedRequest: SqDelete, record: T): SqRecordReloadRequest<T> {
        val parameters = context.parameters
        val conditionList = ArrayList<SqExpression<*, Boolean>>()

        val config = SqRecordReloadRequest.ReloadableConfig
            .create(listOf(record)) { record, recordClassInfo, recordIndex, propertyName, delegate ->
                if (delegate.isSearchKeyForDelete) {
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
                }

                delegate.isReloadedAfterDelete
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


    object Impl: SqDeleteRecordsPreparer
}

fun <T: SqSettingsBuilder> T.deleteRecordsPreparer(value: SqDeleteRecordsPreparer?): T =
    this.setValue(SqDeleteRecordsPreparer::class.java, value)
val SqSettings.deleteRecordsPreparer: SqDeleteRecordsPreparer
    get() = this.getValue(SqDeleteRecordsPreparer::class.java) ?: SqDeleteRecordsPreparer.Impl


fun <T: SqRecord> SqDelete.useAndReloadRecord(record: T, context: SqContext = SqContext.last): SqRecordReloadRequest<T> =
    context.settings.deleteRecordsPreparer.prepare(context, this, record)
// endregion
