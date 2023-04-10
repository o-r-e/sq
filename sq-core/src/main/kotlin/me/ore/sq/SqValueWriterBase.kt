package me.ore.sq

import java.sql.PreparedStatement
import java.sql.Types


abstract class SqValueWriterBase<JAVA: Any>: SqValueWriter<JAVA> {
    protected abstract val sqlType: Int
    protected abstract val typeName: String?

    protected open fun writeNull(target: PreparedStatement, parameterIndex: Int) {
        val typeName = this.typeName
        if (typeName == null) {
            target.setNull(parameterIndex, this.sqlType)
        } else {
            target.setNull(parameterIndex, this.sqlType, typeName)
        }
    }

    protected abstract fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: JAVA)

    override fun write(target: PreparedStatement, parameterIndex: Int, value: JAVA?) {
        if (value == null) {
            this.writeNull(target, parameterIndex)
        } else {
            this.writeNotNull(target, parameterIndex, value)
        }
    }


    protected open fun nullToComment(): String = "<NULL>"
    protected abstract fun notNullValueToComment(value: JAVA): String

    override fun valueToComment(value: JAVA?): String {
        return if (value == null) {
            this.nullToComment()
        } else {
            this.notNullValueToComment(value)
        }
    }
}
