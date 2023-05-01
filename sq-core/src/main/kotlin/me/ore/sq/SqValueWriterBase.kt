package me.ore.sq

import java.sql.PreparedStatement
import java.sql.Types


/** Base implementation for [SqValueWriter] */
abstract class SqValueWriterBase<JAVA: Any>: SqValueWriter<JAVA> {
    /** SQL type; used in [writeNull] as parameter for [PreparedStatement.setNull] */
    protected abstract val sqlType: Int

    /** DB type name; if not `null` then used in [writeNull] as parameter for [PreparedStatement.setNull] */
    protected abstract val typeName: String?

    /**
     * Sets the parameter value to "NULL"
     *
     * @param target statement in which to store the parameter
     * @param parameterIndex parameter index; the index of the first parameter is `1`
     */
    protected open fun writeNull(target: PreparedStatement, parameterIndex: Int) {
        val typeName = this.typeName
        if (typeName == null) {
            target.setNull(parameterIndex, this.sqlType)
        } else {
            target.setNull(parameterIndex, this.sqlType, typeName)
        }
    }

    /**
     * Sets the parameter value
     *
     * @param target statement in which to store the parameter
     * @param parameterIndex parameter index; the index of the first parameter is `1`
     * @param value parameter value
     */
    protected abstract fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: JAVA)

    override fun write(target: PreparedStatement, parameterIndex: Int, value: JAVA?) {
        if (value == null) {
            this.writeNull(target, parameterIndex)
        } else {
            this.writeNotNull(target, parameterIndex, value)
        }
    }


    /**
     * @return text for "NULL" parameter value; реализация по умолчанию возвращает `"<NULL>"`
     */
    protected open fun nullToComment(): String = "<NULL>"

    /**
     * Converts [value] to text for SQL comment
     *
     * @param value value to be converted
     *
     * @return [value] as text for comment
     */
    protected abstract fun notNullValueToComment(value: JAVA): String

    override fun valueToComment(value: JAVA?): String {
        return if (value == null) {
            this.nullToComment()
        } else {
            this.notNullValueToComment(value)
        }
    }
}
