package io.github.ore.sq.impl

import io.github.ore.sq.SqContext
import io.github.ore.sq.SqContextClearListener
import io.github.ore.sq.SqH2Context
import io.github.ore.sq.SqH2ContextFactory
import io.github.ore.sq.SqSettings


open class SqH2ContextImpl(
    settings: SqSettings,
    clearListenerSet: MutableSet<SqContextClearListener<SqContext>> = LinkedHashSet(),
    dataMap: MutableMap<Any, Any> = HashMap(),
): SqContextImpl(settings, clearListenerSet, dataMap), SqH2Context {
    open class Factory: SqH2ContextFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(settings: SqSettings): SqH2ContextImpl =
            SqH2ContextImpl(settings)
    }
}
