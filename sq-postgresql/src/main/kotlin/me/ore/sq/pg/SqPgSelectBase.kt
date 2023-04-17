package me.ore.sq.pg

import me.ore.sq.*


abstract class SqPgSelectBase: SqPgReadStatementBase(), SqSelect {
    @Suppress("DuplicatedCode")
    override fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean) {
        val internalSpaceAllowed = if (asPart) {
            target.add("(", spaced = spaceAllowed)
            false
        } else {
            spaceAllowed
        }

        target.add("SELECT ", spaced = internalSpaceAllowed)
        if (this.distinct) target.add("DISTINCT ")

        this.columns.forEachIndexed { index, column ->
            if (index > 0) target.add(", ")
            column.definitionItem.appendSqlTo(target, asPart = true, spaceAllowed = false)
        }

        this.from?.takeIf { it.isNotEmpty() }?.let { from ->
            target.ls().add("FROM ")
            from.forEachIndexed { index, fromItem ->
                if (index > 0) target.add(", ")
                fromItem.definitionItem.appendSqlTo(target, asPart = true, spaceAllowed = false)
            }
        }

        this.where?.let { where ->
            target.ls().add("WHERE ")
            where.appendSqlTo(target, asPart = true, spaceAllowed = false)
        }

        this.groupBy?.takeIf { it.isNotEmpty() }?.let { groupBy ->
            target.ls().add("GROUP BY ")
            groupBy.forEachIndexed { index, groupByItem ->
                if (index > 0) target.add(", ")
                groupByItem.appendSqlTo(target, asPart = true, spaceAllowed = false)
            }
        }

        this.having?.let { having ->
            target.ls().add("HAVING ")
            having.appendSqlTo(target, asPart = true, spaceAllowed = false)
        }

        this.orderBy?.takeIf { it.isNotEmpty() }?.let { orderBy ->
            target.ls().add("ORDER BY ")
            orderBy.forEachIndexed { index, orderByItem ->
                if (index > 0) target.add(", ")
                orderByItem.appendSqlTo(target, asPart = true, spaceAllowed = false)
            }
        }

        this.resultCountParam?.let { resultCountParam ->
            target.ls().add("LIMIT ")
            resultCountParam.appendSqlTo(target, asPart = true, spaceAllowed = false)
        }

        this.firstResultIndexParam?.let { firstResultIndexParam ->
            target.ls().add("OFFSET ")
            firstResultIndexParam.appendSqlTo(target, asPart = true, spaceAllowed = false)
        }

        if (asPart) target.add(")")
    }

    @Suppress("DuplicatedCode")
    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.columns.forEach { it.definitionItem.appendParametersTo(target) }
        this.from?.forEach { it.definitionItem.appendParametersTo(target) }
        this.where?.appendParametersTo(target)
        this.groupBy?.forEach { it.appendParametersTo(target) }
        this.having?.appendParametersTo(target)
        this.orderBy?.forEach { it.appendParametersTo(target) }
        this.resultCountParam?.appendParametersTo(target)
        this.firstResultIndexParam?.appendParametersTo(target)
    }
}
