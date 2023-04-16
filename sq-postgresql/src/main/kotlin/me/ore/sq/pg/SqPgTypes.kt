package me.ore.sq.pg

import me.ore.sq.SqValueReader
import me.ore.sq.SqValueWriterBase
import me.ore.sq.util.SqUtil
import org.postgresql.util.PGobject
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.*
import java.time.*


object SqPgTypes {
    const val BIG_INT: Int = Types.BIGINT
    const val BIG_INT__TYPE_NAME: String = "int8"

    const val BIT: Int = Types.BIT
    const val BIT__TYPE_NAME: String = "bit"

    const val BOOLEAN: Int = Types.BOOLEAN
    const val BOOLEAN__TYPE_NAME: String = "bool"

    const val BYTEA: Int = Types.BINARY
    const val BYTEA__TYPE_NAME: String = "bytea"

    const val CHAR: Int = Types.CHAR
    const val CHAR__TYPE_NAME: String = "char"

    const val CHARACTER: Int = Types.CHAR
    const val CHARACTER__TYPE_NAME: String = "bpchar"

    const val DATE: Int = Types.DATE
    const val DATE__TYPE_NAME: String = "date"

    const val DOUBLE: Int = Types.DOUBLE
    const val DOUBLE__TYPE_NAME: String = "float8"

    const val INTEGER: Int = Types.INTEGER
    const val INTEGER__TYPE_NAME: String = "int4"

    const val JSON: Int = Types.OTHER
    const val JSON__TYPE_NAME: String = "json"

    const val JSON_B: Int = Types.OTHER
    const val JSON_B__TYPE_NAME: String = "jsonb"

    const val NUMERIC: Int = Types.NUMERIC
    const val NUMERIC__TYPE_NAME: String = "numeric"

    const val REAL: Int = Types.REAL
    const val REAL__TYPE_NAME: String = "float4"

    const val SMALL_INT: Int = Types.SMALLINT
    const val SMALL_INT__TYPE_NAME: String = "int2"

    const val TEXT: Int = Types.VARCHAR
    const val TEXT__TYPE_NAME: String = "text"

    const val TIME: Int = Types.TIME
    const val TIME__TYPE_NAME: String = "time"

    const val TIME_TZ: Int = Types.TIME
    const val TIME_TZ__TYPE_NAME: String = "timetz"

    const val TIMESTAMP: Int = Types.TIMESTAMP
    const val TIMESTAMP__TYPE_NAME: String = "timestamp"

    const val TIMESTAMP_TZ: Int = Types.TIMESTAMP
    const val TIMESTAMP_TZ__TYPE_NAME: String = "timestamptz"

    const val VAR_BIT: Int = Types.OTHER
    const val VAR_BIT__TYPE_NAME: String = "varbit"

    const val VAR_CHAR: Int = Types.VARCHAR
    const val VAR_CHAR__TYPE_NAME: String = "varchar"

    const val XML: Int = Types.SQLXML
    const val XML__TYPE_NAME: String = "xml"
}


// region Boolean types
open class SqPgSingleBitReader: SqValueReader<Boolean> {
    override fun readNullable(source: ResultSet, columnIndex: Int): Boolean? {
        return source.getBoolean(columnIndex).takeUnless { source.wasNull() }
    }
}

open class SqPgSingleBitWriter: SqValueWriterBase<Boolean>() {
    override val sqlType: Int
        get() = SqPgTypes.BIT
    override val typeName: String?
        get() = SqPgTypes.BIT__TYPE_NAME

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Boolean) {
        target.setObject(
            parameterIndex,
            PGobject().apply {
                this.type = this@SqPgSingleBitWriter.typeName
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
        get() = SqPgTypes.BOOLEAN
    override val typeName: String?
        get() = SqPgTypes.BOOLEAN__TYPE_NAME

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
        get() = SqPgTypes.BYTEA
    override val typeName: String?
        get() = SqPgTypes.BYTEA__TYPE_NAME

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: ByteArray) {
        target.setBytes(parameterIndex, value)
    }

    override fun notNullValueToComment(value: ByteArray): String = "Byte array (${value.size} byte(s))"
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
        get() = SqPgTypes.DATE
    override val typeName: String?
        get() = SqPgTypes.DATE__TYPE_NAME

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
        get() = SqPgTypes.DATE
    override val typeName: String?
        get() = SqPgTypes.DATE__TYPE_NAME

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
        get() = SqPgTypes.TIME
    override val typeName: String?
        get() = SqPgTypes.TIME__TYPE_NAME

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
        get() = SqPgTypes.TIME
    override val typeName: String?
        get() = SqPgTypes.TIME__TYPE_NAME

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
        get() = SqPgTypes.TIME_TZ
    override val typeName: String?
        get() = SqPgTypes.TIME_TZ__TYPE_NAME

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
        get() = SqPgTypes.TIMESTAMP
    override val typeName: String?
        get() = SqPgTypes.TIMESTAMP__TYPE_NAME

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
        get() = SqPgTypes.TIMESTAMP
    override val typeName: String?
        get() = SqPgTypes.TIMESTAMP__TYPE_NAME

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
        get() = SqPgTypes.TIMESTAMP_TZ
    override val typeName: String?
        get() = SqPgTypes.TIMESTAMP_TZ__TYPE_NAME

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
        get() = SqPgTypes.BIG_INT
    override val typeName: String?
        get() = SqPgTypes.BIG_INT__TYPE_NAME

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
        get() = SqPgTypes.BIG_INT
    override val typeName: String?
        get() = SqPgTypes.BIG_INT__TYPE_NAME

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
        get() = SqPgTypes.DOUBLE
    override val typeName: String?
        get() = SqPgTypes.DOUBLE__TYPE_NAME

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
        get() = SqPgTypes.INTEGER
    override val typeName: String?
        get() = SqPgTypes.INTEGER__TYPE_NAME

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
        get() = SqPgTypes.NUMERIC
    override val typeName: String?
        get() = SqPgTypes.NUMERIC__TYPE_NAME

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
        get() = SqPgTypes.REAL
    override val typeName: String?
        get() = SqPgTypes.REAL__TYPE_NAME

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
        get() = SqPgTypes.SMALL_INT
    override val typeName: String?
        get() = SqPgTypes.SMALL_INT__TYPE_NAME

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

open class SqPgJavaStringWriter(
    override val sqlType: Int,
    override val typeName: String,
): SqValueWriterBase<String>() {
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
        get() = SqPgTypes.XML
    override val typeName: String?
        get() = SqPgTypes.XML__TYPE_NAME

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: SQLXML) {
        target.setSQLXML(parameterIndex, value)
    }

    override fun notNullValueToComment(value: SQLXML): String = "... SQL XML content ..."
}
// endregion


// region Misc Postgresql types
open class SqPgMultiBitReader: SqValueReader<BooleanArray> {
    override fun readNullable(source: ResultSet, columnIndex: Int): BooleanArray? {
        return source.getString(columnIndex)?.let { stringValue ->
            BooleanArray(stringValue.length) { index ->
                when (val char = stringValue[index]) {
                    '0' -> false
                    '1' -> true
                    else -> error(buildString {
                        this
                            .append("Column with index ")
                            .append(columnIndex)
                            .append(" has invalid value \"")
                            .append(stringValue)
                            .append("\" - found invalid character \"")
                            .append(char)
                            .append("\" (only \"0\" and \"1\" are allowed)")
                    })
                }
            }
        }
    }
}

open class SqPgMultiBitWriter(
    override val sqlType: Int,
    override val typeName: String,
): SqValueWriterBase<BooleanArray>() {
    protected open fun bitsToString(bits: BooleanArray): String {
        return bits
            .map {
                if (it) {
                    '1'
                } else {
                    '0'
                }
            }
            .joinToString("")
    }

    override fun notNullValueToComment(value: BooleanArray): String {
        val trimmedValue: BooleanArray
        val trimmed: Boolean
        if (value.size > 15) {
            trimmedValue = value.copyOf(12)
            trimmed = true
        } else {
            trimmedValue = value
            trimmed = false
        }

        val string = this.bitsToString(trimmedValue)
        return if (trimmed) {
            "$string... (${value.size - string.length} bit(s) more)"
        } else {
            string
        }
    }

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: BooleanArray) {
        val pgObject = PGobject().apply {
            this.type = this@SqPgMultiBitWriter.typeName
            this.value = this@SqPgMultiBitWriter.bitsToString(value)
        }
        target.setObject(parameterIndex, pgObject)
    }
}

open class SqPgPGObjectWriter(
    override val sqlType: Int,
    override val typeName: String?,
): SqValueWriterBase<String>() {
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: String) {
        val valueObject = PGobject().apply {
            this.type = this@SqPgPGObjectWriter.typeName
            this.value = value
        }
        target.setObject(parameterIndex, valueObject)
    }

    override fun notNullValueToComment(value: String): String = SqUtil.prepareStringValueForComment(value)
}
// endregion
