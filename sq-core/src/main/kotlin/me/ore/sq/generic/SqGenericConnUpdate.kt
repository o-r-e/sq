package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericConnUpdate<T: SqTable>(override val context: SqConnectedContext, table: T): SqGenericUpdate<T>(context, table), SqConnUpdate<T> {
    override fun set(columnValueMap: Map<SqColumn<*, *>, SqExpression<*, *>>): SqGenericConnUpdate<T> = this.apply { super<SqGenericUpdate>.set(columnValueMap) }

    override fun where(condition: SqExpression<*, Boolean>?): SqGenericConnUpdate<T> = this.apply { super.where(condition) }


    override fun applyValueMapping(mapping: SqValueMapping<T>): SqGenericConnUpdate<T> = this.apply { super<SqGenericUpdate>.applyValueMapping(mapping) }
}
