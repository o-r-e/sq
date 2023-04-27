package me.ore.sq.pg

import me.ore.sq.*


abstract class SqPgReadStatementBase: SqReadStatement {
    override fun setFirstResultIndexValue(firstResultIndex: Long?) {
        this.firstResultIndexParam = if (firstResultIndex == null) {
            null
        } else {
            this.context.bigIntParam(firstResultIndex)
        }
    }

    override fun setResultCountValue(resultCount: Long?) {
        this.resultCountParam = if (resultCount == null) {
            null
        } else {
            this.context.bigIntParam(resultCount)
        }
    }
}
