package io.github.ore.sq.impl

import io.github.ore.sq.SqContext
import io.github.ore.sq.SqContextClearListener
import io.github.ore.sq.SqContextFactory
import io.github.ore.sq.SqSettings
import io.github.ore.sq.util.SqUtil


open class SqContextImpl(
    override val settings: SqSettings,
    protected open val clearListenerSet: MutableSet<SqContextClearListener<SqContext>> = LinkedHashSet(),
    dataMap: MutableMap<Any, Any> = HashMap(),
) : SqMutableExtendableImpl(dataMap), SqContext {
    override fun addClearListener(listener: SqContextClearListener<SqContext>) {
        this.clearListenerSet.add(listener)
    }

    override fun removeClearListener(listener: SqContextClearListener<out SqContext>) {
        this.clearListenerSet.remove(listener)
    }

    override fun clear() {
        super.clear()

        val listeners = ArrayList(this.clearListenerSet)
        try {
            this.clearListenerSet.clear()
            listeners.forEach { listener ->
                try {
                    listener.invoke(this)
                } catch (e: Exception) {
                    SqUtil.handle(e)
                }
            }
        } finally {
            listeners.clear()
        }
    }


    open class Factory : SqContextFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(settings: SqSettings): SqContextImpl = SqContextImpl(settings)
    }
}
