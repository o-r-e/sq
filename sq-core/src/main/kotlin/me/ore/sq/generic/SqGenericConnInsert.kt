package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericConnInsert<T: SqTable>(
    override val context: SqContext.ConnContext,
    override val table: T,
    override var columns: List<SqTableColumn<*, *>>? = null,
    override var values: List<SqExpression<*, *>>? = null,
    override var select: SqReadStatement? = null,
): SqGenericInsert<T>(context, table, columns, values, select), SqConnInsert<T> {
    companion object {
        val CONSTRUCTOR: SqConnInsertConstructor = object : SqConnInsertConstructor {
            override fun <T : SqTable> createConnInsert(
                context: SqContext.ConnContext,
                table: T,
                columns: List<SqTableColumn<*, *>>?,
                values: List<SqExpression<*, *>>?,
                select: SqReadStatement?
            ): SqConnInsert<T> {
                return SqGenericConnInsert(context, table, columns, values, select)
            }
        }
    }
}
