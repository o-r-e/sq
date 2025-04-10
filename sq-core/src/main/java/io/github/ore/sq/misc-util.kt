package io.github.ore.sq

import io.github.ore.sq.util.SqItemPartConfig
import io.github.ore.sq.util.bracketsAreOptional
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


typealias SqFunction<P, R> = (value: P) -> R


// region Extendable
interface SqExtendable {
    operator fun get(key: Any): Any?

    operator fun <T> get(key: Any, requiredType: Class<T>): T? {
        val result = this[key]
        return if (result == null) {
            null
        } else if (requiredType.isInstance(result)) {
            @Suppress("UNCHECKED_CAST")
            result as T
        } else {
            error("Value with key <$key> expected to be instance of type <$requiredType>, but it is instance of class ${result.javaClass.name}")
        }
    }
}

interface SqMutableExtendable: SqExtendable {
    operator fun set(key: Any, value: Any?)

    fun clear()
}

inline fun <reified T> SqExtendable.getValue(key: Any): T? =
    this[key, T::class.java]

fun <T: SqMutableExtendable> T.setValue(key: Any, value: Any?): T = this.apply {
    this[key] = value
}

inline fun <T: SqMutableExtendable, R> T.use(block: T.() -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return try {
        this.block()
    } finally {
        this.clear()
    }
}
// endregion


// region Fragment, Fragmented
interface SqFragment {
    fun addToBuilder(context: SqContext, owner: SqFragmented, target: SqJdbcRequestDataBuilder, partConfig: SqItemPartConfig?)
}

interface SqFragmented: SqItem {
    val fragments: List<SqFragment>

    fun addFragment(fragment: SqFragment)

    fun addFragmentsToBuilder(context: SqContext, target: SqJdbcRequestDataBuilder, partConfig: SqItemPartConfig?) {
        this.fragments.forEachIndexed { index, fragment ->
            if (index > 0) {
                target.spaceOrNewLine()
            }
            fragment.addToBuilder(context, this, target, partConfig)
        }
    }

    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        if (partConfig.bracketsAreOptional()) {
            this.addFragmentsToBuilder(context, target, partConfig)
        } else if (this.isMultiline) {
            target.bracketsWithIndent {
                this.addFragmentsToBuilder(context, target, partConfig)
            }
        } else {
            target.brackets {
                this.addFragmentsToBuilder(context, target, partConfig)
            }
        }
    }
}
// endregion
