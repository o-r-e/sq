package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericCaseItem<JAVA: Any?, DB: Any>(
    override val context: SqContext,
    override val whenItem: SqExpression<*, Boolean>,
    override val thenItem: SqExpression<JAVA, DB>,
): SqCaseItem<JAVA, DB> {
    override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
        target.add("WHEN", spaced = spaceAllowed)
        this.whenItem.appendTo(target, asTextPart = true, spaceAllowed = true)
        target.add("THEN", spaced = true)
        this.thenItem.appendTo(target, asTextPart = true, spaceAllowed = true)
    }

    override fun parameters(): List<SqParameter<*, *>>? = SqUtil.collectParameters(this.whenItem, this.thenItem)
}
