package me.ore.sq

import java.sql.ResultSet


open class SqReader(
    open val statement: SqReadStatement,
    open val resultSet: ResultSet,
) {
    open fun next(): Boolean {
        if (this.stopped) return false
        return this.resultSet.next()
    }


    protected open var _stopped: Boolean = false

    open val stopped: Boolean
        get() = this._stopped

    open fun stop() { this._stopped = true }


    fun getColumnIndex(column: SqColumn<*, *>): Int? {
        return this.statement.getColumnIndex(column)?.let { it + 1 }
    }

    fun requireColumnIndex(column: SqColumn<*, *>): Int {
        return (this.statement.requireColumnIndex(column) + 1)
    }


    operator fun <JAVA: Any?> get(column: SqColumn<JAVA, *>): JAVA {
        val index = this.requireColumnIndex(column)
        return column.read(this.resultSet, index)
    }

    fun <JAVA: Any?> getNullable(column: SqColumn<JAVA, *>): JAVA? {
        val index = this.requireColumnIndex(column)
        return column.type.nullable().read(this.resultSet, index)
    }
}
