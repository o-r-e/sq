package me.ore.sq.generic

import me.ore.sq.*
import me.ore.sq.util.SqUtil


open class SqGenericNamedFunction<JAVA: Any?, DB: Any>(
    override val context: SqContext,
    override val type: SqType<JAVA, DB>,
    override val name: String,
    override val nameSpaced: Boolean,
    override val values: List<SqItem>,
) : SqNamedFunction<JAVA, DB> {
    companion object {
        protected val SPACED_FUNCTION_NAMES: Set<String> = setOf(
            SqUtil.FUNCTION_NAME__ALL.uppercase(),
            SqUtil.FUNCTION_NAME__ANY.uppercase(),
        )

        val CONSTRUCTOR: SqNamedFunctionConstructor = object : SqNamedFunctionConstructor {
            override fun <JAVA, DB : Any> createNamedFunction(
                context: SqContext,
                type: SqType<JAVA, DB>,
                name: String,
                nameSpaced: Boolean?,
                values: List<SqItem>
            ): SqNamedFunction<JAVA, DB> {
                val actualNameSpaced = nameSpaced ?: SPACED_FUNCTION_NAMES.contains(name.uppercase())
                return SqGenericNamedFunction(context, type, name, actualNameSpaced, values)
            }
        }
    }


    override val definitionItem: SqItem
        get() = this
}
