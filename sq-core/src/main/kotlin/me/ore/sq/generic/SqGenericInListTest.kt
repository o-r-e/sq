package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericInListTest<JAVA: Boolean?>(
    override val context: SqContext,
    override val type: SqType<JAVA, Boolean>,
    override val negative: Boolean,
    override val testedValue: SqExpression<*, *>,
    override val listValues: List<SqExpression<*, *>>,
): SqInListTest<JAVA> {
    companion object {
        val CONSTRUCTOR: SqInListTestConstructor = object : SqInListTestConstructor {
            override fun <JAVA : Boolean?> createInListTest(
                context: SqContext,
                type: SqType<JAVA, Boolean>,
                negative: Boolean,
                testedValue: SqExpression<*, *>,
                listValues: List<SqExpression<*, *>>
            ): SqInListTest<JAVA> {
                return SqGenericInListTest(context, type, negative, testedValue, listValues)
            }
        }
    }


    override val definitionItem: SqItem
        get() = this
}
