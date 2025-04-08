@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqPgDeleteImpl


// region Request
interface SqPgDelete: SqDelete, SqDataModificationReturnableRequest

fun interface SqPgDeleteFactory: SqDeleteFactory {
    override fun invoke(context: SqContext, table: SqItem): SqPgDelete
}

fun <T: SqSettingsBuilder> T.pgDeleteFactory(value: SqPgDeleteFactory?): T =
    this.setValue(SqPgDeleteFactory::class.java, value)
val SqSettings.pgDeleteFactory: SqPgDeleteFactory
    get() = this.getValue(SqPgDeleteFactory::class.java) ?: SqPgDeleteImpl.Factory.INSTANCE


fun SqPgContext.deleteFrom(table: SqTable): SqPgDelete =
    this.settings.pgDeleteFactory.invoke(this, table)
// endregion
