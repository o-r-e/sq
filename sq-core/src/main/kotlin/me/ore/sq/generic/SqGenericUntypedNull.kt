package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericUntypedNull<JAVA: Any, DB: Any>(
    override val context: SqContext,
): SqNull<JAVA, DB> {
    companion object {
        val CONSTRUCTOR: SqUntypedNullConstructor = object : SqUntypedNullConstructor {
            override fun <JAVA : Any, DB : Any> createUntypedNull(context: SqContext): SqNull<JAVA, DB> {
                return SqGenericUntypedNull(context)
            }
        }
    }


    override val type: SqType<JAVA?, DB>
        get() = error("\"Untyped\" NULL item has no type")
    override val definitionItem: SqItem
        get() = this
}
