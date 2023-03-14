package me.ore.sq


abstract class SqContextHolder<T: SqContext> {
    // region Default context
    protected abstract fun createDefaultContext(): T

    private val defaultContextLock = Object()

    @Volatile
    private var _defaultContext: T? = null

    private fun getDefaultContextImpl(): T {
        return this._defaultContext
            ?: synchronized(this.defaultContextLock) {
                this._defaultContext
                    ?: run {
                        val result = this.createDefaultContext()
                        this._defaultContext = result
                        result
                    }
            }
    }

    private fun setDefaultContextImpl(value: T) {
        synchronized(this.defaultContextLock) {
            this._defaultContext = value
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    open var defaultContext: T
        get() = this.getDefaultContextImpl()
        set(value) { this.setDefaultContextImpl(value) }
    // endregion

    // region Thread context queue
    private val threadContextQueue = ThreadLocal<ArrayList<T>>()

    private fun addToThreadQueue(context: T) {
        val queue = this.threadContextQueue.get() ?: run {
            val tmpQueue = ArrayList<T>(1)
            this.threadContextQueue.set(tmpQueue)
            tmpQueue
        }

        queue.add(context)
    }

    private fun removeFromThreadQueue(context: T): Boolean {
        val lastContextRemoved = this.threadContextQueue.get()?.let { queue ->
            val index = queue.lastIndexOf(context)
            if (index >= 0) {
                queue.removeAt(index)
                if (queue.isEmpty()) {
                    this.threadContextQueue.remove()
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }

        return (lastContextRemoved ?: false)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    val threadContext: T?
        get() = this.threadContextQueue.get()?.lastOrNull()
    // endregion

    @Suppress("PropertyName")
    val CONTEXT: T
        get() = threadContext ?: defaultContext


    // region Lifecycle
    protected open fun onLastContextFinished() {}

    protected open fun start(context: T) {
        this.addToThreadQueue(context)
    }

    protected open fun finish(context: T) {
        val lastContextFinished = this.removeFromThreadQueue(context)
        if (lastContextFinished) {
            this.onLastContextFinished()
        }
    }
    // endregion
}
