package me.ore.sq

import java.sql.PreparedStatement
import java.sql.ResultSet


abstract class SqType<JAVA: Any> {
    open fun readNotNull(source: ResultSet, columnIndex: Int): JAVA {
        return this.readNullable(source, columnIndex)
            ?: throw IllegalStateException("Got NULL value for NOT-NULL column with index #$columnIndex")
    }

    open fun readNullable(source: ResultSet, columnIndex: Int): JAVA? {
        val result = this.readImpl(source, columnIndex)
        return if (source.wasNull()) {
            null
        } else {
            result
        }
    }

    protected abstract fun readImpl(source: ResultSet, columnIndex: Int): JAVA?


    open fun write(target: PreparedStatement, parameterIndex: Int, value: JAVA?) {
        if (value == null) {
            this.writeNull(target, parameterIndex)
        } else {
            this.writeNotNull(target, parameterIndex, value)
        }
    }

    protected abstract fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: JAVA)

    protected abstract fun writeNull(target: PreparedStatement, parameterIndex: Int)


    open fun prepareValueForComment(value: JAVA?): String {
        return if (value == null) {
            "<NULL>"
        } else {
            this.prepareNotNullValueForComment(value)
        }
    }

    protected abstract fun prepareNotNullValueForComment(value: JAVA): String


    @Suppress("NOTHING_TO_INLINE")
    inline fun <OPT: JAVA?> sqCast(): SqType<OPT & Any> = SqUtil.uncheckedCast(this)
}
