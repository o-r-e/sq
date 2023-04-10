package me.ore.sq.pg

import me.ore.sq.*


open class SqPgConnSingleColUnion<JAVA: Any?, DB: Any>(
    override val context: SqContext.ConnContext,
    unionAll: Boolean,
    selects: List<SqSingleColSelect<JAVA, DB>>,
    resultCountParam: SqParameter<Long, Number>? = null,
    firstResultIndexParam: SqParameter<Long, Number>? = null,
): SqPgSingleColUnion<JAVA, DB>(
    context,
    unionAll,
    selects,
    resultCountParam,
    firstResultIndexParam,
), SqConnSingleColUnion<JAVA, DB> {
    companion object {
        val CONSTRUCTOR: SqConnSingleColUnionConstructor = object : SqConnSingleColUnionConstructor {
            override fun <JAVA, DB : Any> createConnSingleColUnion(
                context: SqContext.ConnContext,
                unionAll: Boolean,
                selects: List<SqSingleColSelect<JAVA, DB>>
            ): SqConnSingleColUnion<JAVA, DB> {
                return SqPgConnSingleColUnion(
                    context, unionAll, selects,
                )
            }
        }
    }
}
