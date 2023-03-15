package me.ore.sq

import java.sql.PreparedStatement
import java.sql.SQLType


abstract class SqDetailedType<JAVA: Any>: SqType<JAVA>() {
    abstract val sqlType: SQLType
    open val sqlTypeName: String?
        get() = this.sqlType.name
    open val vendorTypeNumber: Int
        get() = this.sqlType.vendorTypeNumber ?: throw IllegalStateException("SQL type ${this.sqlType} has NULL \"vendorTypeNumber\" property")

    override fun writeNull(target: PreparedStatement, parameterIndex: Int) {
        val vendorTypeNumber = this.vendorTypeNumber
        val sqlTypeName = this.sqlTypeName
        if (sqlTypeName == null) {
            target.setNull(parameterIndex, vendorTypeNumber)
        } else {
            target.setNull(parameterIndex, vendorTypeNumber, sqlTypeName)
        }
    }
}
