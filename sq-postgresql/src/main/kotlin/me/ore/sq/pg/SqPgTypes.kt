package me.ore.sq.pg

import me.ore.sq.SqValueReader
import me.ore.sq.SqValueWriterBase
import me.ore.sq.util.SqUtil
import org.postgresql.util.PGobject
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.*
import java.time.*


// region Boolean types
open class SqPgSingleBitReader: SqValueReader<Boolean> {
    override fun readNullable(source: ResultSet, columnIndex: Int): Boolean? {
        return source.getBoolean(columnIndex).takeUnless { source.wasNull() }
    }
}

open class SqPgSingleBitWriter: SqValueWriterBase<Boolean>() {
    companion object {
        protected const val TYPE_NAME = "bit"
    }


    override val sqlType: Int
        get() = Types.BIT
    override val typeName: String
        get() = TYPE_NAME

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Boolean) {
        target.setObject(
            parameterIndex,
            PGobject().apply {
                this.type = TYPE_NAME
                this.value = if (value) "1" else "0"
            }
        )
    }

    override fun notNullValueToComment(value: Boolean): String = value.toString()
}

open class SqPgBooleanReader: SqValueReader<Boolean> {
    override fun readNullable(source: ResultSet, columnIndex: Int): Boolean? {
        return source.getBoolean(columnIndex).takeUnless { source.wasNull() }
    }
}

open class SqPgBooleanWriter: SqValueWriterBase<Boolean>() {
    override val sqlType: Int
        get() = Types.BOOLEAN
    override val typeName: String
        get() = "bool"

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Boolean) {
        target.setBoolean(parameterIndex, value)
    }

    override fun notNullValueToComment(value: Boolean): String = value.toString()
}
// endregion


// region Byte array types
open class SqPgByteaReader: SqValueReader<ByteArray> {
    override fun readNullable(source: ResultSet, columnIndex: Int): ByteArray? {
        return source.getBytes(columnIndex)
    }
}

open class SqPgByteaWriter: SqValueWriterBase<ByteArray>() {
    override val sqlType: Int
        get() = Types.BINARY
    override val typeName: String?
        get() = "bytea"

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: ByteArray) {
        target.setBytes(parameterIndex, value)
    }

    override fun notNullValueToComment(value: ByteArray): String = "ByteArray (${value.size} byte(s))"
}
// endregion


// region Date/time types
open class SqPgDateReader: SqValueReader<LocalDate> {
    override fun readNullable(source: ResultSet, columnIndex: Int): LocalDate? {
        return source.getObject(1, LocalDate::class.java)
    }
}

open class SqPgDateWriter: SqValueWriterBase<LocalDate>() {
    override val sqlType: Int
        get() = Types.DATE
    override val typeName: String?
        get() = "date"

    override fun notNullValueToComment(value: LocalDate): String = value.toString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: LocalDate) {
        target.setObject(parameterIndex, value)
    }
}

open class SqPgSqlDateReader: SqValueReader<Date> {
    override fun readNullable(source: ResultSet, columnIndex: Int): Date? {
        return source.getDate(columnIndex)
    }
}

open class SqPgSqlDateWriter: SqValueWriterBase<Date>() {
    override val sqlType: Int
        get() = Types.DATE
    override val typeName: String?
        get() = "date"

    override fun notNullValueToComment(value: Date): String = value.toString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Date) {
        target.setDate(parameterIndex, value)
    }
}

open class SqPgTimeReader: SqValueReader<LocalTime> {
    override fun readNullable(source: ResultSet, columnIndex: Int): LocalTime? {
        return source.getObject(columnIndex, LocalTime::class.java)
    }
}

open class SqPgTimeWriter: SqValueWriterBase<LocalTime>() {
    override val sqlType: Int
        get() = Types.TIME
    override val typeName: String?
        get() = "time"

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: LocalTime) {
        target.setObject(parameterIndex, value)
    }

    override fun notNullValueToComment(value: LocalTime): String = value.toString()
}

open class SqPgSqlTimeReader: SqValueReader<Time> {
    override fun readNullable(source: ResultSet, columnIndex: Int): Time? {
        return source.getTime(columnIndex)
    }
}

open class SqPgSqlTimeWriter: SqValueWriterBase<Time>() {
    override val sqlType: Int
        get() = Types.TIME
    override val typeName: String?
        get() = "time"

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Time) {
        target.setTime(parameterIndex, value)
    }

    override fun notNullValueToComment(value: Time): String = value.toString()
}

open class SqPgTimeTZReader: SqValueReader<OffsetTime> {
    override fun readNullable(source: ResultSet, columnIndex: Int): OffsetTime? {
        return source.getObject(columnIndex, OffsetTime::class.java)
    }
}

open class SqPgTimeTZWriter: SqValueWriterBase<OffsetTime>() {
    override val sqlType: Int
        get() = Types.TIME
    override val typeName: String?
        get() = "timetz"

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: OffsetTime) {
        target.setObject(parameterIndex, value)
    }

    override fun notNullValueToComment(value: OffsetTime): String = value.toString()
}

open class SqPgTimestampReader: SqValueReader<LocalDateTime> {
    override fun readNullable(source: ResultSet, columnIndex: Int): LocalDateTime? {
        return source.getObject(columnIndex, LocalDateTime::class.java)
    }
}

open class SqPgTimestampWriter: SqValueWriterBase<LocalDateTime>() {
    override val sqlType: Int
        get() = Types.TIMESTAMP
    override val typeName: String?
        get() = "timestamp"

    override fun notNullValueToComment(value: LocalDateTime): String = value.toString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: LocalDateTime) {
        target.setObject(parameterIndex, value)
    }
}

open class SqPgSqlTimestampReader: SqValueReader<Timestamp> {
    override fun readNullable(source: ResultSet, columnIndex: Int): Timestamp? {
        return source.getTimestamp(columnIndex)
    }
}

open class SqPgSqlTimestampWriter: SqValueWriterBase<Timestamp>() {
    override val sqlType: Int
        get() = Types.TIMESTAMP
    override val typeName: String?
        get() = "timestamp"

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Timestamp) {
        target.setTimestamp(parameterIndex, value)
    }

    override fun notNullValueToComment(value: Timestamp): String = value.toString()
}

open class SqPgTimestampTZReader: SqValueReader<OffsetDateTime> {
    override fun readNullable(source: ResultSet, columnIndex: Int): OffsetDateTime? {
        return source.getObject(columnIndex, OffsetDateTime::class.java)
    }
}

open class SqPgTimestampTZWriter: SqValueWriterBase<OffsetDateTime>() {
    override val sqlType: Int
        get() = Types.TIMESTAMP
    override val typeName: String?
        get() = "timestamptz"

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: OffsetDateTime) {
        target.setObject(parameterIndex, value)
    }

    override fun notNullValueToComment(value: OffsetDateTime): String = value.toString()
}
// endregion


// region Number types
open class SqPgBigIntReader: SqValueReader<Long> {
    override fun readNullable(source: ResultSet, columnIndex: Int): Long? {
        return source.getLong(columnIndex).takeUnless { source.wasNull() }
    }
}

open class SqPgBigIntWriter: SqValueWriterBase<Long>() {
    override val sqlType: Int
        get() = Types.BIGINT
    override val typeName: String?
        get() = "int8"

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Long) {
        target.setLong(parameterIndex, value)
    }

    override fun notNullValueToComment(value: Long): String = value.toString()
}

open class SqPgMathBigIntReader: SqValueReader<BigInteger> {
    override fun readNullable(source: ResultSet, columnIndex: Int): BigInteger? {
        return source.getObject(columnIndex, BigInteger::class.java)
    }
}

open class SqPgMathBigIntWriter: SqValueWriterBase<BigInteger>() {
    override val sqlType: Int
        get() = Types.BIGINT
    override val typeName: String?
        get() = "int8"

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: BigInteger) {
        target.setObject(parameterIndex, value)
    }

    override fun notNullValueToComment(value: BigInteger): String = value.toString()
}

open class SqPgDoublePrecisionReader: SqValueReader<Double> {
    override fun readNullable(source: ResultSet, columnIndex: Int): Double? {
        return source.getDouble(columnIndex).takeUnless { source.wasNull() }
    }
}

open class SqPgDoublePrecisionWriter: SqValueWriterBase<Double>() {
    override val sqlType: Int
        get() = Types.DOUBLE
    override val typeName: String?
        get() = "float8"

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Double) {
        target.setDouble(parameterIndex, value)
    }

    override fun notNullValueToComment(value: Double): String = value.toString()
}

open class SqPgIntegerReader: SqValueReader<Int> {
    override fun readNullable(source: ResultSet, columnIndex: Int): Int? {
        return source.getInt(columnIndex).takeUnless { source.wasNull() }
    }
}

open class SqPgIntegerWriter: SqValueWriterBase<Int>() {
    override val sqlType: Int
        get() = Types.INTEGER
    override val typeName: String?
        get() = "int4"

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Int) {
        target.setInt(parameterIndex, value)
    }

    override fun notNullValueToComment(value: Int): String = value.toString()
}

open class SqPgNumericReader: SqValueReader<BigDecimal> {
    override fun readNullable(source: ResultSet, columnIndex: Int): BigDecimal? {
        return source.getBigDecimal(columnIndex)
    }
}

open class SqPgNumericWriter: SqValueWriterBase<BigDecimal>() {
    override val sqlType: Int
        get() = Types.NUMERIC
    override val typeName: String?
        get() = "numeric"

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: BigDecimal) {
        target.setBigDecimal(parameterIndex, value)
    }

    override fun notNullValueToComment(value: BigDecimal): String = value.toPlainString()
}

open class SqPgRealReader: SqValueReader<Float> {
    override fun readNullable(source: ResultSet, columnIndex: Int): Float? {
        return source.getFloat(columnIndex).takeUnless { source.wasNull() }
    }
}

open class SqPgRealWriter: SqValueWriterBase<Float>() {
    override val sqlType: Int
        get() = Types.REAL
    override val typeName: String?
        get() = "float4"

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Float) {
        target.setFloat(parameterIndex,value)
    }

    override fun notNullValueToComment(value: Float): String = value.toString()
}

open class SqPgSmallIntReader: SqValueReader<Short> {
    override fun readNullable(source: ResultSet, columnIndex: Int): Short? {
        return source.getShort(columnIndex).takeUnless { source.wasNull() }
    }
}

open class SqPgSmallIntWriter: SqValueWriterBase<Short>() {
    override val sqlType: Int
        get() = Types.SMALLINT
    override val typeName: String?
        get() = "int2"

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Short) {
        target.setShort(parameterIndex, value)
    }

    override fun notNullValueToComment(value: Short): String = value.toString()
}
// endregion


// region Text types
open class SqPgJavaStringReader: SqValueReader<String> {
    override fun readNullable(source: ResultSet, columnIndex: Int): String? {
        return source.getString(columnIndex)
    }
}

open class SqPgCharWriter: SqValueWriterBase<String>() {
    override val sqlType: Int
        get() = Types.CHAR
    override val typeName: String?
        get() = "char"

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: String) {
        target.setString(parameterIndex, value)
    }

    override fun notNullValueToComment(value: String): String = SqUtil.prepareStringValueForComment(value)
}

open class SqPgCharacterVaryingWriter: SqValueWriterBase<String>() {
    override val sqlType: Int
        get() = Types.VARCHAR
    override val typeName: String?
        get() = "varchar"

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: String) {
        target.setString(parameterIndex, value)
    }

    override fun notNullValueToComment(value: String): String = SqUtil.prepareStringValueForComment(value)
}
// endregion


// region Other JDBC types
open class SqPgXmlReader: SqValueReader<SQLXML> {
    override fun readNullable(source: ResultSet, columnIndex: Int): SQLXML? {
        return source.getSQLXML(columnIndex).takeUnless { source.wasNull() }
    }
}

open class SqPgXmlWriter: SqValueWriterBase<SQLXML>() {
    override val sqlType: Int
        get() = Types.SQLXML
    override val typeName: String?
        get() = "xml"

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: SQLXML) {
        target.setSQLXML(parameterIndex, value)
    }

    override fun notNullValueToComment(value: SQLXML): String = "... SQL XML content ..."
}
// endregion
