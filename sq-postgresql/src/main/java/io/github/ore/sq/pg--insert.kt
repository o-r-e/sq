@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqPgInsertImpl


// region Request
interface SqPgInsert: SqInsert, SqDataModificationReturnableRequest

fun interface SqPgInsertFactory: SqInsertFactory {
    override fun invoke(context: SqContext, table: SqItem): SqPgInsert
}

fun <T: SqSettingsBuilder> T.pgInsertFactory(value: SqPgInsertFactory?): T =
    this.setValue(SqPgInsertFactory::class.java, value)
val SqSettings.pgInsertFactory: SqPgInsertFactory
    get() = this.getValue(SqPgInsertFactory::class.java) ?: SqPgInsertImpl.Factory.INSTANCE


fun SqPgContext.insertInto(table: SqTable): SqPgInsert =
    this.settings.pgInsertFactory.invoke(this, table)
// endregion
