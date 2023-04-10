package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericConnUpdate<T: SqTable>(
    override val context: SqContext.ConnContext,
    override val table: T,
    override var set: Map<SqTableColumn<*, *>, SqExpression<*, *>>? = null,
    override var where: SqExpression<*, Boolean>? = null,
): SqGenericUpdate<T>(context, table, set, where), SqConnUpdate<T> {
    companion object {
        val CONSTRUCTOR: SqConnUpdateConstructor = object : SqConnUpdateConstructor {
            override fun <T : SqTable> createConnUpdate(
                context: SqContext.ConnContext,
                table: T,
                set: Map<SqTableColumn<*, *>, SqExpression<*, *>>?,
                where: SqExpression<*, Boolean>?
            ): SqConnUpdate<T> {
                return SqGenericConnUpdate(context, table, set, where)
            }
        }
    }
}
