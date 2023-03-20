package me.ore.sq

import java.sql.PreparedStatement
import java.sql.ResultSet


abstract class SqType<JAVA: Any> {
    // region Reading
    open fun readNotNull(source: ResultSet, columnIndex: Int): JAVA {
        return this.readNullable(source, columnIndex)
            ?: error("Got NULL value for NOT-NULL column with index #$columnIndex")
    }

    open fun readNullable(source: ResultSet, columnIndex: Int): JAVA? {
        val result = this.readNullableImpl(source, columnIndex)
        return if (source.wasNull()) {
            null
        } else {
            result
        }
    }

    protected abstract fun readNullableImpl(source: ResultSet, columnIndex: Int): JAVA?
    // endregion


    // region Writing
    open fun write(target: PreparedStatement, parameterIndex: Int, value: JAVA?) {
        if (value == null) {
            this.writeNull(target, parameterIndex)
        } else {
            this.writeNotNull(target, parameterIndex, value)
        }
    }

    protected open fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: JAVA) { target.setObject(parameterIndex, value) }

    protected abstract fun writeNull(target: PreparedStatement, parameterIndex: Int)
    // endregion


    // region Preparing value for comment
    open fun prepareValueForComment(value: JAVA?): String {
        return if (value == null) {
            "<NULL>"
        } else {
            this.prepareNotNullValueForComment(value)
        }
    }

    protected open fun prepareNotNullValueForComment(value: JAVA): String = value.toString()
    // endregion


    @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
    inline fun <OPT: JAVA?> sqCast(): SqType<OPT & Any> = this as SqType<OPT & Any>


    fun <DB: Any> createNull(context: SqContext? = null): SqNull<JAVA, DB> = SqNull(this, context)
    fun <DB: Any, OPT_JAVA: JAVA?> createParam(value: OPT_JAVA, context: SqContext? = null): SqParameter<OPT_JAVA, DB> = SqParameter(this.sqCast(), value, context)
}
