package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericNot<JAVA: Boolean?>(
    override val context: SqContext,
    override val type: SqType<JAVA, Boolean>,
    override val expression: SqExpression<*, Boolean>,
): SqNot<JAVA> {
    companion object {
        val CONSTRUCTOR: SqNotConstructor = object : SqNotConstructor {
            override fun <JAVA : Boolean?> createNot(context: SqContext, type: SqType<JAVA, Boolean>, expression: SqExpression<*, Boolean>): SqNot<JAVA> {
                return SqGenericNot(context, type, expression)
            }
        }
    }


    override val definitionItem: SqItem
        get() = this
}
