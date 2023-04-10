package me.ore.sq.util

import me.ore.sq.SqContext
import kotlin.concurrent.getOrSet


open class SqContextHolder<T: SqContext> {
    private val queueHolder = ThreadLocal<MutableList<T>>()

    protected fun add(context: T) {
        queueHolder
            .getOrSet { ArrayList() }
            .add(context)
    }

    protected fun remove(context: T) {
        this.queueHolder.get()?.let { queue ->
            val indices = ArrayList<Int>(queue.size)

            // region Collect indices
            queue.forEachIndexed { index, queuedContext ->
                if (queuedContext == context) {
                    indices.add(index)
                }
            }
            // endregion

            // region Remove context
            indices.reverse()
            for (index in indices) queue.removeAt(index)
            // endregion

            // region Remove queue if empty
            if (queue.isEmpty()) this.queueHolder.remove()
            // endregion
        }
    }

    protected val optContext: T?
        get() = this.queueHolder.get()?.lastOrNull()
    protected val context: T
        get() = this.optContext ?: error("No current context in thread")

    val OPT_CONTEXT: T?
        get() = this.optContext
    val CONTEXT: T
        get() = this.context
}
