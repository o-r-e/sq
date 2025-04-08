@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqContextImpl
import kotlin.concurrent.getOrSet
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


fun interface SqContextClearListener<T: SqContext> {
    operator fun invoke(context: T)
}

interface SqContext: SqMutableExtendable {
    companion object {
        private val THREAD_LIST_HOLDER = ThreadLocal<ArrayList<SqContext>>()

        fun addToThreadList(context: SqContext) {
            THREAD_LIST_HOLDER
                .getOrSet { ArrayList() }
                .add(context)
        }

        fun removeLastFromThreadList(context: SqContext) {
            val list = THREAD_LIST_HOLDER.get()
            val last = list?.lastOrNull()
            if (last != context) {
                error("SQ context <$context> is not last SQ context in thread SQ context list")
            }

            list.removeAt(list.lastIndex)
            if (list.isEmpty()) {
                THREAD_LIST_HOLDER.remove()
            }
        }

        val optionalLast: SqContext?
            get() = THREAD_LIST_HOLDER.get()?.lastOrNull()

        val last: SqContext
            get() {
                return this.optionalLast
                    ?: error("Current thread has no SQ context")
            }
    }

    val settings: SqSettings
    fun addClearListener(listener: SqContextClearListener<SqContext>)
    fun removeClearListener(listener: SqContextClearListener<out SqContext>)
}

fun interface SqContextFactory {
    operator fun invoke(settings: SqSettings): SqContext
}

fun <T : SqSettingsBuilder> T.contextFactory(value: SqContextFactory?): T =
    this.setValue(SqContextFactory::class.java, value)
val SqSettings.contextFactory: SqContextFactory
    get() = this.getValue(SqContextFactory::class.java) ?: SqContextImpl.Factory.INSTANCE


inline fun <T> sq(settings: SqSettings = SqSettings.default, block: SqContext.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return settings.contextFactory.invoke(settings).use(block)
}

fun <T : SqContext> T.addClearListener(listener: SqContextClearListener<T>): T = this.apply {
    @Suppress("UNCHECKED_CAST")
    val typedListener = listener as SqContextClearListener<SqContext>
    this.addClearListener(typedListener)
}

inline fun <T : SqContext, R> T.use(block: T.() -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return try {
        SqContext.addToThreadList(this)
        this.block()
    } finally {
        SqContext.removeLastFromThreadList(this)
        this.clear()
    }
}
