@file:Suppress("unused")

package io.github.ore.sq.util


open class SqItemPartConfig(
    open val isBracketsAreOptional: Boolean = false,
) {
    companion object {
        val INSTANCE__OPTIONAL_BRACKETS: SqItemPartConfig = SqItemPartConfig(isBracketsAreOptional = true)
        val INSTANCE__REQUIRED_BRACKETS: SqItemPartConfig = SqItemPartConfig(isBracketsAreOptional = false)
    }
}

fun SqItemPartConfig?.bracketsAreOptional(): Boolean =
    (this?.isBracketsAreOptional != false)
