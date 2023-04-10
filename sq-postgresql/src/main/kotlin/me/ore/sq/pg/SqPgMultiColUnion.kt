package me.ore.sq.pg

import me.ore.sq.*


open class SqPgMultiColUnion(
    override val context: SqContext,
    override var unionAll: Boolean,
    override var selects: List<SqSelect>,
    override var resultCountParam: SqParameter<Long, Number>? = null,
    override var firstResultIndexParam: SqParameter<Long, Number>? = null,
): SqPgUnionBase(), SqMultiColUnion {
    companion object {
        val CONSTRUCTOR: SqMultiColUnionConstructor = object : SqMultiColUnionConstructor {
            override fun createMultiColUnion(
                context: SqContext,
                unionAll: Boolean,
                selects: List<SqSelect>
            ): SqMultiColUnion {
                return SqPgMultiColUnion(
                    context, unionAll, selects,
                )
            }
        }
    }


    override val definitionItem: SqItem
        get() = this
}
