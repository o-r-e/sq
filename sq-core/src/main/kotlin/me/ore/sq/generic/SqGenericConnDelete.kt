package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericConnDelete<T: SqTable>(
    override val context: SqContext.ConnContext,
    override val table: T,
    override var where: SqExpression<*, Boolean>? = null,
): SqGenericDelete<T>(context, table, where), SqConnDelete<T> {
    companion object {
        val CONSTRUCTOR: SqConnDeleteConstructor = object : SqConnDeleteConstructor {
            override fun <T : SqTable> createConnDelete(
                context: SqContext.ConnContext,
                table: T,
                where: SqExpression<*, Boolean>?
            ): SqConnDelete<T> {
                return SqGenericConnDelete(context, table, where)
            }
        }
    }
}
