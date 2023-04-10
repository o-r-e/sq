package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericOrderBy(
    override val context: SqContext,
    override val column: SqColumn<*, *>,
    override val order: SqSortOrder,
): SqOrderBy {
    companion object {
        val CONSTRUCTOR: SqOrderByConstructor = object : SqOrderByConstructor {
            override fun createOrderBy(context: SqContext, column: SqColumn<*, *>, order: SqSortOrder): SqOrderBy {
                return SqGenericOrderBy(context, column, order)
            }
        }
    }


    override val definitionItem: SqItem
        get() = this
}
