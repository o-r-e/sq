package me.ore.sq.pg

import me.ore.sq.*


open class SqPgConnMultiColUnion(
    override val context: SqContext.ConnContext,
    unionAll: Boolean,
    selects: List<SqSelect>,
    resultCountParam: SqParameter<Long, Number>? = null,
    firstResultIndexParam: SqParameter<Long, Number>? = null,
): SqPgMultiColUnion(
    context,
    unionAll,
    selects,
    resultCountParam,
    firstResultIndexParam,
), SqConnMultiColUnion {
    companion object {
        val CONSTRUCTOR: SqConnMultiColUnionConstructor = object : SqConnMultiColUnionConstructor {
            override fun createConnMultiColUnion(
                context: SqContext.ConnContext,
                unionAll: Boolean,
                selects: List<SqSelect>
            ): SqConnMultiColUnion {
                return SqPgConnMultiColUnion(
                    context, unionAll, selects,
                )
            }
        }
    }
}
