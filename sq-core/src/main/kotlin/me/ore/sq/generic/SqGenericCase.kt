package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericCase<JAVA: Any?, DB: Any>(
    override val context: SqContext,
    override val type: SqType<JAVA, DB>,
    override val items: List<SqCaseItem<out JAVA, DB>>,
    override val elseItem: SqExpression<out JAVA, DB>?,
) : SqCase<JAVA, DB> {
    companion object {
        val CONSTRUCTOR: SqCaseConstructor = object : SqCaseConstructor {
            override fun <JAVA, DB : Any> createCase(
                context: SqContext,
                type: SqType<JAVA, DB>,
                items: List<SqCaseItem<out JAVA, DB>>,
                elseItem: SqExpression<out JAVA, DB>?
            ): SqCase<JAVA, DB> {
                return SqGenericCase(context, type, items, elseItem)
            }
        }
    }


    override val definitionItem: SqItem
        get() = this
}
