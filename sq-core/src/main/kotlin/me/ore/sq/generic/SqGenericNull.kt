package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericNull<JAVA: Any, DB: Any>(
    override val context: SqContext,
    override val type: SqType<JAVA?, DB>,
): SqNull<JAVA, DB> {
    companion object {
        val CONSTRUCTOR: SqNullConstructor = object : SqNullConstructor {
            override fun <JAVA : Any, DB : Any> createNull(context: SqContext, type: SqType<JAVA?, DB>): SqNull<JAVA, DB> {
                return SqGenericNull(context, type)
            }
        }
    }


    override val definitionItem: SqItem
        get() = this
}
