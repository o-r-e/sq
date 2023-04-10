package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericDelete<T: SqTable>(
    override val context: SqContext,
    override val table: T,
    override var where: SqExpression<*, Boolean>? = null,
): SqDelete<T> {
    companion object {
        val CONSTRUCTOR: SqDeleteConstructor = object : SqDeleteConstructor {
            override fun <T : SqTable> createDelete(
                context: SqContext,
                table: T,
                where: SqExpression<*, Boolean>?
            ): SqDelete<T> {
                return SqGenericDelete(context, table, where)
            }
        }
    }


    override val definitionItem: SqItem
        get() = this
}
