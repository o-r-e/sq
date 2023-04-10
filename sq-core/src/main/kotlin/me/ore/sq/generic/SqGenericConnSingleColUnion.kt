package me.ore.sq.generic

import me.ore.sq.SqConnSingleColUnion
import me.ore.sq.SqConnSingleColUnionConstructor
import me.ore.sq.SqContext
import me.ore.sq.SqSingleColSelect


open class SqGenericConnSingleColUnion<JAVA: Any?, DB: Any>(
    override val context: SqContext.ConnContext,
    override var unionAll: Boolean,
    override var selects: List<SqSingleColSelect<JAVA, DB>>,
): SqGenericUnionBase(), SqConnSingleColUnion<JAVA, DB> {
    companion object {
        val CONSTRUCTOR: SqConnSingleColUnionConstructor = object : SqConnSingleColUnionConstructor {
            override fun <JAVA, DB : Any> createConnSingleColUnion(
                context: SqContext.ConnContext,
                unionAll: Boolean,
                selects: List<SqSingleColSelect<JAVA, DB>>
            ): SqConnSingleColUnion<JAVA, DB> {
                return SqGenericConnSingleColUnion(context, unionAll, selects)
            }
        }
    }
}
