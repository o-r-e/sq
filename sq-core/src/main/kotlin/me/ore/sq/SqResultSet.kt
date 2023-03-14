package me.ore.sq

import java.sql.ResultSet


open class SqResultSet(
    val statement: SqReadStatement,
    val resultSet: ResultSet,
) {
    fun next(): Boolean = resultSet.next()

    operator fun <JAVA: Any?> get(column: SqColumn<JAVA, *>): JAVA {
        val columnIndex = this.statement.requireColumnIndex(column)
        val columnPosition = columnIndex + 1
        val result = column.read(this.resultSet, columnPosition)
        return result
    }
}
