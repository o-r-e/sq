package me.ore.sq.generic

import me.ore.sq.SqConnDelete
import me.ore.sq.SqConnectedContext
import me.ore.sq.SqExpression
import me.ore.sq.SqTable


open class SqGenericConnDelete<T: SqTable>(override val context: SqConnectedContext, table: T): SqGenericDelete<T>(context, table), SqConnDelete<T> {
    override fun where(condition: SqExpression<*, Boolean>?): SqGenericConnDelete<T> = this.apply { super.where(condition) }
}
