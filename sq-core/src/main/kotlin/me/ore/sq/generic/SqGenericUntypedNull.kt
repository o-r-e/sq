package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericUntypedNull<JAVA: Any, DB: Any>(
    override val context: SqContext,
): SqNull<JAVA, DB> {
    companion object {
        val CONSTRUCTOR: SqUnsafeNullConstructor = object : SqUnsafeNullConstructor {
            override fun <JAVA : Any, DB : Any> createUnsafeNull(context: SqContext): SqNull<JAVA, DB> {
                return SqGenericUntypedNull(context)
            }
        }
    }


    override val type: SqType<JAVA?, DB>
        get() = error("\"Untyped\" NULL item has no type")
    override val definitionItem: SqItem
        get() = this
}
