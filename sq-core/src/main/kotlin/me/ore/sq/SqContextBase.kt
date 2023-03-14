package me.ore.sq

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write


abstract class SqContextBase: SqContext {
    // region Utils
    override fun finish() {
        this.clearCachedData()
        super.finish()
    }

    protected open fun clearCachedData() {
        this.clearPrintParameterValuesByThread()
    }


    @Suppress("MemberVisibilityCanBePrivate")
    protected val printParameterValuesByDefaultLock: ReentrantReadWriteLock = ReentrantReadWriteLock()

    override var printParameterValuesByDefault: Boolean = false
        get() = this.printParameterValuesByDefaultLock.read { field }
        set(value) {
            this.printParameterValuesByDefaultLock.write {
                field = value
            }
        }

    @Suppress("MemberVisibilityCanBePrivate")
    protected val printParameterValuesByThreadHolder: ThreadLocal<Boolean> = ThreadLocal()

    override var printParameterValuesByThread: Boolean?
        get() = this.printParameterValuesByThreadHolder.get()
        set(value) {
            if (value == null) {
                this.printParameterValuesByThreadHolder.remove()
            } else {
                this.printParameterValuesByThreadHolder.set(value)
            }
        }

    protected open fun clearPrintParameterValuesByThread() { this.printParameterValuesByThreadHolder.remove() }
    // endregion


    // region Base items
    protected abstract fun <JAVA: Any?, DB: Any> rawParameter(type: SqType<JAVA & Any>, nullable: Boolean, value: JAVA): SqParameter<JAVA, DB>
    override fun <JAVA: Any?, DB: Any> param(type: SqType<JAVA & Any>, nullable: Boolean, value: JAVA): SqParameter<JAVA, DB> {
        return this.rawParameter(SqUtil.uncheckedCast(type), nullable, value)
    }
    // endregion
}
