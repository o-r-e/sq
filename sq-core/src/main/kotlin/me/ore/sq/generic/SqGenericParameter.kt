package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericParameter<JAVA: Any?, DB: Any>(
    override val context: SqContext,
    override val type: SqType<JAVA, DB>,
    override val value: JAVA,
): SqParameter<JAVA, DB> {
    companion object {
        val CONSTRUCTOR: SqParameterConstructor = object : SqParameterConstructor {
            override fun <JAVA, DB : Any> createParameter(context: SqContext, type: SqType<JAVA, DB>, value: JAVA): SqParameter<JAVA, DB> {
                return SqGenericParameter(context, type, value)
            }
        }
    }


    override val definitionItem: SqItem
        get() = this
}
