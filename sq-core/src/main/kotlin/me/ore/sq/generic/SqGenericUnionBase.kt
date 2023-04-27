package me.ore.sq.generic

import me.ore.sq.*


abstract class SqGenericUnionBase: SqGenericReadStatementBase(), SqUnion {
    override val definitionItem: SqItem
        get() = this

    @Suppress("DuplicatedCode")
    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) {
        if (asPart) target.add("(", spaced = spaced).ls()

        val unionSeparator = if (this.unionAll) "UNION ALL" else "UNION"
        this.selects.forEachIndexed { index, select ->
            if (index > 0) target.ls().add(unionSeparator).ls()
            select.appendSqlTo(target, asPart = true, spaced = false)
        }

        if (asPart) target.ls().add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.selects.forEach { it.appendParametersTo(target) }
    }
}
