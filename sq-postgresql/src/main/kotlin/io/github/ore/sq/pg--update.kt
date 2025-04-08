@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqPgUpdateImpl


// region Request
interface SqPgUpdate: SqUpdate, SqDataModificationReturnableRequest

fun interface SqPgUpdateFactory: SqUpdateFactory {
    override fun invoke(context: SqContext, table: SqItem): SqPgUpdate
}

fun <T: SqSettingsBuilder> T.pgUpdateFactory(value: SqPgUpdateFactory?): T =
    this.setValue(SqPgUpdateFactory::class.java, value)
val SqSettings.pgUpdateFactory: SqPgUpdateFactory
    get() = this.getValue(SqPgUpdateFactory::class.java) ?: SqPgUpdateImpl.Factory.INSTANCE


fun SqPgContext.update(table: SqTable): SqPgUpdate =
    this.settings.pgUpdateFactory.invoke(this, table)
// endregion
