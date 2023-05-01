package me.ore.sq

import java.sql.ResultSet


/**
 * Object for reading data from the query result, wrapper around [ResultSet]
 *
 * @param statement reading statement ("select", "union")
 * @param resultSet query result
 */
open class SqReader(
    open val statement: SqReadStatement,
    open val resultSet: ResultSet,
) {
    /**
     * Move to next row of results
     *
     * @return if the current object is [stopped], returns `false`;
     * otherwise - returns the result of calling [resultSet].[next][ResultSet.next]
     */
    open fun next(): Boolean {
        if (this.stopped) return false
        return this.resultSet.next()
    }

    /** Mutable backing field for [stopped] */
    protected open var _stopped: Boolean = false

    /** `true`, if current reader is stopped */
    open val stopped: Boolean
        get() = this._stopped

    /**
     * Stops current reader.
     *
     * After execution [stopped] is `true`.
     *
     * Does not close [resultSet], only needed to stop the loop on [resultSet].
     */
    open fun stop() { this._stopped = true }


    /**
     * Getting the index of a column in a [statement]; numbering starts from `0`
     *
     * @param column column to get index
     *
     * @return the index of [column] in the [statement], if the column belongs to this set; otherwise - `null`
     */
    fun getColumnIndex(column: SqColumn<*, *>): Int? {
        return this.statement.getColumnIndex(column)?.let { it + 1 }
    }

    /**
     * Getting the index of a column in a [statement]; numbering starts from `0`
     *
     * @param column column to get index
     *
     * @return the index of [column] in the [statement]
     *
     * @throws Exception if [column] does not belong to [statement]
     */
    fun requireColumnIndex(column: SqColumn<*, *>): Int {
        return (this.statement.requireColumnIndex(column) + 1)
    }


    /**
     * Read value for [column] from query result
     *
     * @return value for [column] read from current row in [resultSet]
     *
     * @throws Exception various errors, for example:
     * [column] does not belong to [statement],
     * [resultSet] is closed,
     * error reading data from column
     *
     * @see getNullable
     */
    operator fun <JAVA: Any?> get(column: SqColumn<JAVA, *>): JAVA {
        val index = this.requireColumnIndex(column)
        return column.read(this.resultSet, index)
    }

    /**
     * Read value for [column] from query result
     *
     * Designed to be able to read "NULLABLE" values if the column type is not "NULLABLE"
     *
     * @return value for [column] read from current row in [resultSet], may be `null`
     *
     * @throws Exception various errors, for example:
     * [column] does not belong to [statement],
     * [resultSet] is closed,
     * error reading data from column
     *
     * @see get
     */
    fun <JAVA: Any?> getNullable(column: SqColumn<JAVA, *>): JAVA? {
        val index = this.requireColumnIndex(column)
        return column.type.nullable().read(this.resultSet, index)
    }
}
