package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericUpdate<T: SqTable>(
    override val context: SqContext,
    override val table: T,
    override var set: Map<SqTableColumn<*, *>, SqExpression<*, *>>? = null,
    override var where: SqExpression<*, Boolean>? = null,
): SqUpdate<T> {
    companion object {
        val CONSTRUCTOR: SqUpdateConstructor = object : SqUpdateConstructor {
            override fun <T : SqTable> createUpdate(
                context: SqContext,
                table: T,
                set: Map<SqTableColumn<*, *>, SqExpression<*, *>>?,
                where: SqExpression<*, Boolean>?
            ): SqUpdate<T> {
                return SqGenericUpdate(context, table, set, where)
            }
        }
    }


    override val definitionItem: SqItem
        get() = this

    override fun createValueMapping(): SqColumnValueMapping<T> = SqGenericColumnValueMapping(this)
}
