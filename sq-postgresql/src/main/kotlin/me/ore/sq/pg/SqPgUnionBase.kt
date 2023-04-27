package me.ore.sq.pg

import me.ore.sq.*


abstract class SqPgUnionBase: SqPgReadStatementBase(), SqUnion {
    @Suppress("DuplicatedCode")
    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) {
        if (asPart) target.add("(", spaced = spaced).ls()

        val unionSeparator = if (this.unionAll) "UNION ALL" else "UNION"
        this.selects.forEachIndexed { index, select ->
            if (index > 0) target.ls().add(unionSeparator).ls()
            select.appendSqlTo(target, asPart = true, spaced = false)
        }

        this.resultCountParam?.let { resultCountParam ->
            target.ls().add("LIMIT ")
            resultCountParam.appendSqlTo(target, asPart = true, spaced = false)
        }

        this.firstResultIndexParam?.let { firstResultIndexParam ->
            target.ls().add("OFFSET ")
            firstResultIndexParam.appendSqlTo(target, asPart = true, spaced = false)
        }

        if (asPart) target.ls().add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.selects.forEach { it.appendParametersTo(target) }
        this.resultCountParam?.appendParametersTo(target)
        this.firstResultIndexParam?.appendParametersTo(target)
    }
}
