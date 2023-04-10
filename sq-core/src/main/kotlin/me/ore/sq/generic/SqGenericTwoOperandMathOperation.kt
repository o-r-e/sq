package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericTwoOperandMathOperation<JAVA: Number?>(
    override val context: SqContext,
    override val type: SqType<JAVA, Number>,
    override val firstOperand: SqExpression<*, Number>,
    override val operation: String,
    override val secondOperand: SqExpression<*, Number>,
) : SqTwoOperandMathOperation<JAVA> {
    companion object {
        val CONSTRUCTOR: SqTwoOperandMathOperationConstructor = object : SqTwoOperandMathOperationConstructor {
            override fun <JAVA : Number?> createTwoOperandMathOperation(
                context: SqContext,
                type: SqType<JAVA, Number>,
                firstOperand: SqExpression<*, Number>,
                operation: String,
                secondOperand: SqExpression<*, Number>
            ): SqTwoOperandMathOperation<JAVA> {
                return SqGenericTwoOperandMathOperation(context, type, firstOperand, operation, secondOperand)
            }
        }
    }


    override val definitionItem: SqItem
        get() = this
}
