package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericComparison<JAVA: Boolean?>(
    override val context: SqContext,
    override val type: SqType<JAVA, Boolean>,
    override val firstOperand: SqExpression<*, *>,
    override val secondOperand: SqExpression<*, *>,
    override val operation: String,
): SqComparison<JAVA> {
    companion object {
        val CONSTRUCTOR: SqComparisonConstructor = object : SqComparisonConstructor {
            override fun <JAVA : Boolean?> createComparison(
                context: SqContext,
                type: SqType<JAVA, Boolean>,
                firstOperand: SqExpression<*, *>,
                secondOperand: SqExpression<*, *>,
                operation: String
            ): SqComparison<JAVA> {
                return SqGenericComparison(context, type, firstOperand, secondOperand, operation)
            }
        }
    }


    override val definitionItem: SqItem
        get() = this
}
