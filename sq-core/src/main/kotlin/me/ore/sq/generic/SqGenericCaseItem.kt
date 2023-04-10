package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericCaseItem<JAVA: Any?, DB: Any>(
    override val context: SqContext,
    override val whenItem: SqExpression<*, Boolean>,
    override val thenItem: SqExpression<JAVA, DB>,
): SqCaseItem<JAVA, DB> {
    companion object {
        val CONSTRUCTOR: SqCaseItemConstructor = object : SqCaseItemConstructor {
            override fun <JAVA, DB : Any> createCaseItem(context: SqContext, whenItem: SqExpression<*, Boolean>, thenItem: SqExpression<JAVA, DB>): SqCaseItem<JAVA, DB> {
                return SqGenericCaseItem(context, whenItem, thenItem)
            }
        }
    }


    override val definitionItem: SqItem
        get() = this
}
