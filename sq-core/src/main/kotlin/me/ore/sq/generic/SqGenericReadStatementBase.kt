package me.ore.sq.generic

import me.ore.sq.*


abstract class SqGenericReadStatementBase: SqReadStatement {
    // region Limits
    @Suppress("NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")
    protected inline fun throwLimitsUnsupportedException(): Nothing =
        error("Limits are unsupported in \"generic\" statements (like current ${this.javaClass.name})")


    override var firstResultIndexParam: SqParameter<Long, Number>?
        get() = null
        set(value) { this.throwLimitsUnsupportedException() }

    override fun setFirstResultIndexValue(firstResultIndex: Long?) { this.throwLimitsUnsupportedException() }


    override var resultCountParam: SqParameter<Long, Number>?
        get() = null
        set(value) { this.throwLimitsUnsupportedException() }

    override fun setResultCountValue(value: Long?) { this.throwLimitsUnsupportedException() }
    // endregion
}
