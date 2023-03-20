package me.ore.sq

import java.sql.PreparedStatement
import java.sql.SQLType


abstract class SqTypeBase<JAVA: Any>: SqType<JAVA>() {
    // region Type info
    protected abstract val sqlType: SQLType

    protected open val vendorTypeNumber: Int
        get() {
            val sqlType = this.sqlType
            return sqlType.vendorTypeNumber
                ?: error("SQL type $sqlType has NULL \"vendorTypeNumber\" property")
        }

    protected open val sqlTypeName: String?
        get() = this.sqlType.name
    // endregion


    // region Writing
    override fun writeNull(target: PreparedStatement, parameterIndex: Int) {
        val vendorTypeNumber = this.vendorTypeNumber
        val sqlTypeName = this.sqlTypeName
        if (sqlTypeName == null) {
            target.setNull(parameterIndex, vendorTypeNumber)
        } else {
            target.setNull(parameterIndex, vendorTypeNumber, sqlTypeName)
        }
    }
    // endregion
}
