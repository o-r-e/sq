package me.ore.sq.generic

import me.ore.sq.SqConnMultiColUnion
import me.ore.sq.SqConnMultiColUnionConstructor
import me.ore.sq.SqContext
import me.ore.sq.SqSelect


open class SqGenericConnMultiColUnion(
    override val context: SqContext.ConnContext,
    override var unionAll: Boolean,
    override var selects: List<SqSelect>,
): SqGenericUnionBase(), SqConnMultiColUnion {
    companion object {
        val CONSTRUCTOR: SqConnMultiColUnionConstructor = object : SqConnMultiColUnionConstructor {
            override fun createConnMultiColUnion(
                context: SqContext.ConnContext,
                unionAll: Boolean,
                selects: List<SqSelect>
            ): SqConnMultiColUnion {
                return SqGenericConnMultiColUnion(context, unionAll, selects)
            }
        }
    }
}
