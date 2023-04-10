package me.ore.sq.pg

import me.ore.sq.*


open class SqPgSingleColUnion<JAVA: Any?, DB: Any>(
    override val context: SqContext,
    override var unionAll: Boolean,
    override var selects: List<SqSingleColSelect<JAVA, DB>>,
    override var resultCountParam: SqParameter<Long, Number>? = null,
    override var firstResultIndexParam: SqParameter<Long, Number>? = null,
): SqPgUnionBase(), SqSingleColUnion<JAVA, DB> {
    companion object {
        val CONSTRUCTOR: SqSingleColUnionConstructor = object : SqSingleColUnionConstructor {
            override fun <JAVA, DB : Any> createSingleColUnion(
                context: SqContext,
                unionAll: Boolean,
                selects: List<SqSingleColSelect<JAVA, DB>>
            ): SqSingleColUnion<JAVA, DB> {
                return SqPgSingleColUnion(
                    context, unionAll, selects,
                )
            }
        }
    }


    override val definitionItem: SqItem
        get() = this
}
