package me.ore.sq.generic

import me.ore.sq.*


abstract class SqGenericUnionBase: SqGenericReadStatementBase(), SqUnion {
    override val definitionItem: SqItem
        get() = this

    override fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean) {
        val firstSelectSpaceAllowed = if (asPart) {
            target.add("(", spaced = spaceAllowed).ls()
            false
        } else {
            spaceAllowed
        }

        val unionAll = this.unionAll

        this.selects.forEachIndexed { index, select ->
            if (index == 0) {
                select.appendSqlTo(target, asPart = true, spaceAllowed = firstSelectSpaceAllowed)
            } else {
                target.ls()
                if (unionAll) target.add("UNION ALL")
                else target.add("UNION")
                target.ls()

                select.appendSqlTo(target, asPart = true, spaceAllowed = false)
            }
        }

        if (asPart) target.ls().add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.selects.forEach { it.appendParametersTo(target) }
    }
}
