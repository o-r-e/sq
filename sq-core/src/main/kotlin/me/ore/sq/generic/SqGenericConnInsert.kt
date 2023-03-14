package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericConnInsert<T: SqTable>(override val context: SqConnectedContext, table: T): SqGenericInsert<T>(context, table), SqConnInsert<T> {
    override fun columns(columns: Iterable<SqColumn<*, *>>?): SqGenericConnInsert<T> = this.apply { super<SqGenericInsert>.columns(columns) }

    override fun values(values: Iterable<SqExpression<*, *>>?): SqGenericConnInsert<T> = this.apply { super<SqGenericInsert>.values(values) }

    override fun select(select: SqReadStatement?): SqGenericConnInsert<T> = this.apply { super<SqGenericInsert>.select(select) }
}
