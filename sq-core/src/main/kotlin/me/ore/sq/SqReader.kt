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

    operator fun <JAVA: Any?> get(column: SqColumn<JAVA, *>): JAVA {
        val index = (this.statement.requireColumnIndex(column) + 1)
        return column.read(this.resultSet, index)
    }
}
