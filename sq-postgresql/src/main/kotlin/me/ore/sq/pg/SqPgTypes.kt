package me.ore.sq.pg

import me.ore.sq.SqTypeBase
import me.ore.sq.SqUtil
import java.sql.JDBCType
import java.sql.ResultSet
import java.sql.SQLType


object SqPgTypes {
    val BIG_INT = object : SqTypeBase<Long>() {
        override val sqlType: SQLType
            get() = JDBCType.BIGINT

        override fun readNullableImpl(source: ResultSet, columnIndex: Int): Long? {
            return when (val value = source.getObject(columnIndex)) {
                null -> null
                is Long -> value
                is Number -> value.toLong()
                else -> SqUtil.throwUnexpectedColumnValueClassException(columnIndex, value, Long::class.java, Number::class.java)
            }
        }
    }
}
