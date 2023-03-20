package me.ore.sq

import java.sql.PreparedStatement
import java.sql.ResultSet


abstract class SqArrayTypeBase<JAVA: Any>: SqTypeBase<List<JAVA?>>() {
    companion object {
        protected const val ARRAY_RESULT_SET_VALUE_COLUMN_INDEX = 2
    }


    protected abstract val arrayElementType: SqType<JAVA>
    protected abstract val arrayElementDbTypeName: String

    override fun readNullableImpl(source: ResultSet, columnIndex: Int): List<JAVA?>? {
        return when (val value = source.getObject(columnIndex)) {
            null -> null
            is java.sql.Array -> {
                try {
                    value.resultSet.use { arrayResultSet ->
                        val result = ArrayList<JAVA?>()

                        while (arrayResultSet.next()) {
                            try {
                                val arrayElement = this.arrayElementType.readNullable(arrayResultSet, ARRAY_RESULT_SET_VALUE_COLUMN_INDEX)
                                result.add(arrayElement)
                            } catch (e: Exception) {
                                throw IllegalStateException("Error while reading array from column with index $columnIndex")
                            }
                        }

                        result
                    }
                } finally {
                    value.free()
                }
            }
            else -> SqUtil.throwUnexpectedColumnValueClassException(columnIndex, value, java.sql.Array::class.java)
        }
    }

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: List<JAVA?>) {
        val arrayValue = target.connection.createArrayOf(this.arrayElementDbTypeName, value.toTypedArray<Any?>())
        target.setObject(parameterIndex, arrayValue)
    }
}
