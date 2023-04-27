package me.ore.sq.generic

import me.ore.sq.*


abstract class SqGenericSelectBase: SqGenericReadStatementBase(), SqSelect {
    override val definitionItem: SqItem
        get() = this


    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) {
        val internalSpaceAllowed = if (asPart) {
            target.add("(", spaced = spaced)
            false
        } else {
            spaced
        }

        target.add("SELECT ", spaced = internalSpaceAllowed)
        if (this.distinct) target.add("DISTINCT ")

        this.columns.forEachIndexed { index, column ->
            if (index > 0) target.add(", ")
            column.definitionItem.appendSqlTo(target, asPart = true, spaced = false)
        }

        this.from?.takeIf { it.isNotEmpty() }?.let { from ->
            target.ls().add("FROM ")
            from.forEachIndexed { index, colSet ->
                if (index > 0) target.add(", ")
                colSet.definitionItem.appendSqlTo(target, asPart = true, spaced = false)
            }
        }

        this.where?.let { where ->
            target.ls().add("WHERE ")
            where.appendSqlTo(target, asPart = true, spaced = false)
        }

        this.groupBy?.takeIf { it.isNotEmpty() }?.let { groupBy ->
            target.ls().add("GROUP BY ")
            groupBy.forEachIndexed { index, column ->
                if (index > 0) target.add(", ")
                column.appendSqlTo(target, asPart = true, spaced = false)
            }
        }

        this.having?.let { having ->
            target.ls().add("HAVING ")
            having.appendSqlTo(target, asPart = true, spaced = false)
        }

        this.orderBy?.takeIf { it.isNotEmpty() }?.let { orderBy ->
            target.ls().add("ORDER BY ")
            orderBy.forEachIndexed { index, orderByItem ->
                if (index > 0) target.add(", ")
                orderByItem.appendSqlTo(target, asPart = true, spaced = false)
            }
        }

        if (asPart) target.add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.columns.forEach { it.definitionItem.appendParametersTo(target) }
        this.from?.forEach { it.definitionItem.appendParametersTo(target) }
        this.where?.appendParametersTo(target)
        this.groupBy?.forEach { it.appendParametersTo(target) }
        this.having?.appendParametersTo(target)
    }
}
