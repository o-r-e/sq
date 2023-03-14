package me.ore.sq.generic

import me.ore.sq.SqCaseItemStart
import me.ore.sq.SqContext
import me.ore.sq.SqExpression


open class SqGenericCaseItemStart(
    override val context: SqContext,
    override val whenItem: SqExpression<*, Boolean>,
): SqCaseItemStart
