package me.ore.sq.generic

import me.ore.sq.SqContext
import me.ore.sq.SqSingleColSelect
import me.ore.sq.SqSingleColUnion
import me.ore.sq.SqSingleColUnionConstructor


open class SqGenericSingleColUnion<JAVA: Any?, DB: Any>(
    override val context: SqContext,
    override var unionAll: Boolean,
    override var selects: List<SqSingleColSelect<JAVA, DB>>,
): SqGenericUnionBase(), SqSingleColUnion<JAVA, DB> {
    companion object {
        val CONSTRUCTOR: SqSingleColUnionConstructor = object : SqSingleColUnionConstructor {
            override fun <JAVA, DB : Any> createSingleColUnion(
                context: SqContext,
                unionAll: Boolean,
                selects: List<SqSingleColSelect<JAVA, DB>>
            ): SqSingleColUnion<JAVA, DB> {
                return SqGenericSingleColUnion(context, unionAll, selects)
            }
        }
    }
}
