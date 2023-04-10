package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericBetweenTest<JAVA: Boolean?>(
    override val context: SqContext,
    override val type: SqType<JAVA, Boolean>,
    override val negation: Boolean,
    override val testedValue: SqExpression<*, *>,
    override val firstRangeValue: SqExpression<*, *>,
    override val secondRangeValue: SqExpression<*, *>,
): SqBetweenTest<JAVA> {
    companion object {
        val CONSTRUCTOR: SqBetweenTestConstructor = object : SqBetweenTestConstructor {
            override fun <JAVA : Boolean?> createBetweenTest(
                context: SqContext,
                type: SqType<JAVA, Boolean>,
                negation: Boolean,
                testedValue: SqExpression<*, *>,
                firstRangeValue: SqExpression<*, *>,
                secondRangeValue: SqExpression<*, *>
            ): SqBetweenTest<JAVA> {
                return SqGenericBetweenTest(context, type, negation, testedValue, firstRangeValue, secondRangeValue)
            }
        }
    }


    override val definitionItem: SqItem
        get() = this
}
