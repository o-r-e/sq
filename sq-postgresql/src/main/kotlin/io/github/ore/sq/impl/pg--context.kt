package io.github.ore.sq.impl

import io.github.ore.sq.SqContext
import io.github.ore.sq.SqContextClearListener
import io.github.ore.sq.SqPgContext
import io.github.ore.sq.SqPgContextFactory
import io.github.ore.sq.SqSettings


open class SqPgContextImpl(
    settings: SqSettings,
    clearListenerSet: MutableSet<SqContextClearListener<SqContext>> = LinkedHashSet(),
    dataMap: MutableMap<Any, Any> = HashMap(),
): SqContextImpl(settings, clearListenerSet, dataMap), SqPgContext {
    open class Factory: SqPgContextFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(settings: SqSettings): SqPgContextImpl =
            SqPgContextImpl(settings)
    }
}
