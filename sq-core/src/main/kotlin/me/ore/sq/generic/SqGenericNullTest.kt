package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericNullTest(
    override val context: SqContext,
    override val type: SqType<Boolean, Boolean>,
    override val negation: Boolean,
    override val expression: SqExpression<*, *>,
): SqNullTest {
    companion object {
        val CONSTRUCTOR: SqNullTestConstructor = object : SqNullTestConstructor {
            override fun createNullTest(context: SqContext, type: SqType<Boolean, Boolean>, negation: Boolean, expression: SqExpression<*, *>): SqNullTest {
                return SqGenericNullTest(context, type, negation, expression)
            }
        }
    }


    override val definitionItem: SqItem
        get() = this
}
