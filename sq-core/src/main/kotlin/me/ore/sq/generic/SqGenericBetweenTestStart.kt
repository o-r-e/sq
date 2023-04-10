package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericBetweenTestStart<JAVA: Any?, DB: Any>(
    override val context: SqContext,
    override val type: SqType<JAVA, DB>,
    override val negation: Boolean,
    override val testedValue: SqExpression<*, DB>,
    override val firstRangeValue: SqExpression<*, DB>
) : SqBetweenTestStart<JAVA, DB> {
    companion object {
        val CONSTRUCTOR: SqBetweenTestStartConstructor = object : SqBetweenTestStartConstructor {
            override fun <JAVA, DB : Any> createBetweenTestStart(
                context: SqContext,
                type: SqType<JAVA, DB>,
                negation: Boolean,
                testedValue: SqExpression<*, DB>,
                firstRangeValue: SqExpression<*, DB>
            ): SqBetweenTestStart<JAVA, DB> {
                return SqGenericBetweenTestStart(context, type, negation, testedValue, firstRangeValue)
            }
        }
    }
}
