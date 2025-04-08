@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqH2ContextImpl
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


interface SqH2Context: SqContext

fun interface SqH2ContextFactory: SqContextFactory {
    override fun invoke(settings: SqSettings): SqH2Context
}

fun <T: SqSettingsBuilder> T.h2ContextFactory(value: SqH2ContextFactory?): T =
    this.setValue(SqH2ContextFactory::class.java, value)
val SqSettings.h2ContextFactory: SqH2ContextFactory
    get() = this.getValue(SqH2ContextFactory::class.java) ?: SqH2ContextImpl.Factory.INSTANCE


inline fun <T> sqH2(settings: SqSettings = SqH2.defaultSettings, block: SqH2Context.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return settings.h2ContextFactory.invoke(settings).use(block)
}
