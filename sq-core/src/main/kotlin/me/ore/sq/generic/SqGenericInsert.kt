package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericInsert<T: SqTable>(
    override val context: SqContext,
    override val table: T,
    override var columns: List<SqTableColumn<*, *>>? = null,
    override var values: List<SqExpression<*, *>>? = null,
    override var select: SqReadStatement? = null,
): SqInsert<T> {
    companion object {
        val CONSTRUCTOR: SqInsertConstructor = object : SqInsertConstructor {
            override fun <T : SqTable> createInsert(
                context: SqContext,
                table: T,
                columns: List<SqTableColumn<*, *>>?,
                values: List<SqExpression<*, *>>?,
                select: SqReadStatement?,
            ): SqInsert<T> {
                return SqGenericInsert(context, table, columns, values, select)
            }
        }
    }


    override val definitionItem: SqItem
        get() = this

    override fun createValueMapping(): SqColumnValueMapping<T> = SqGenericColumnValueMapping(this)
}
