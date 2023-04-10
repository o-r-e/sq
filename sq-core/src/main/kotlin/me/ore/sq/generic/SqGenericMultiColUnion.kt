package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericMultiColUnion(
    override val context: SqContext,
    override var unionAll: Boolean,
    override var selects: List<SqSelect>,
): SqGenericUnionBase(), SqMultiColUnion {
    companion object {
        val CONSTRUCTOR: SqMultiColUnionConstructor = object : SqMultiColUnionConstructor {
            override fun createMultiColUnion(
                context: SqContext,
                unionAll: Boolean,
                selects: List<SqSelect>
            ): SqMultiColUnion {
                return SqGenericMultiColUnion(context, unionAll, selects)
            }
        }
    }
}
