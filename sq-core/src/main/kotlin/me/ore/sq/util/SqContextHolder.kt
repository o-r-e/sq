package me.ore.sq.util

import me.ore.sq.SqContext
import kotlin.concurrent.getOrSet


/** Holder of the "queue" of the contexts of the current thread */
open class SqContextHolder<T: SqContext> {
    /** Holder of the "queue" of the contexts */
    private val queueHolder = ThreadLocal<MutableList<T>>()

    /**
     * Adds a context to the current thread's "queue" of contexts
     *
     * @param context context to add
     */
    protected fun add(context: T) {
        queueHolder
            .getOrSet { ArrayList() }
            .add(context)
    }

    /**
     * Removes a context from the current thread's "queue" of contexts
     *
     * @param context context to be removed
     */
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

    /** Last context in the current thread's "queue" of contexts */
    protected val optContext: T?
        get() = this.queueHolder.get()?.lastOrNull()

    /**
     * Last context in the current thread's "queue" of contexts
     *
     * @throws IllegalStateException if the current thread's "queue" of contexts is empty
     */
    protected val context: T
        get() = this.optContext ?: error("No current context in thread")

    /** Last context in the current thread's "queue" of contexts */
    val OPT_CONTEXT: T?
        get() = this.optContext

    /**
     * Last context in the current thread's "queue" of contexts
     *
     * @throws IllegalStateException if the current thread's "queue" of contexts is empty
     */
    val CONTEXT: T
        get() = this.context
}
