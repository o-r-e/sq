@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqPgContextImpl
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


interface SqPgContext: SqContext

fun interface SqPgContextFactory: SqContextFactory {
    override fun invoke(settings: SqSettings): SqPgContext
}

fun <T: SqSettingsBuilder> T.pgContextFactory(value: SqPgContextFactory?): T =
    this.setValue(SqPgContextFactory::class.java, value)
val SqSettings.pgContextFactory: SqPgContextFactory
    get() = this.getValue(SqPgContextFactory::class.java) ?: SqPgContextImpl.Factory.INSTANCE


inline fun <T> sqPg(settings: SqSettings = SqPg.defaultSettings, block: SqPgContext.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return settings.pgContextFactory.invoke(settings).use(block)
}
