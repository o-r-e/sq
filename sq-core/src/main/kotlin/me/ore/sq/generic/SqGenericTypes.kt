package me.ore.sq.generic

import me.ore.sq.SqByteArray
import me.ore.sq.SqDetailedType
import me.ore.sq.SqType
import me.ore.sq.toSqArray
import java.math.BigDecimal
import java.net.URL
import java.sql.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime


// region Common
private fun prepareStringValueForComment(value: String): String {
    return if (value.length > 15) {
        "\"${value.take(12)}...\""
    } else {
        "\"$value\""
    }
}


abstract class SqGenericJCastingType<JAVA: Any>: SqDetailedType<JAVA>() {
    abstract val valueClass: Class<JAVA>

    override fun readImpl(source: ResultSet, columnIndex: Int): JAVA? = source.getObject(columnIndex, this.valueClass)
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: JAVA) { target.setObject(parameterIndex, value, this.sqlType) }
}
// endregion


// region Boolean - BIT, BOOLEAN
abstract class SqGenericJBooleanTypeBase: SqDetailedType<Boolean>() {
    override fun readImpl(source: ResultSet, columnIndex: Int): Boolean = source.getBoolean(columnIndex)
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Boolean) { target.setBoolean(parameterIndex, value) }
}

open class SqGenericBitType: SqGenericJBooleanTypeBase() {
    override val sqlType: SQLType
        get() = JDBCType.BIT

    override fun prepareNotNullValueForComment(value: Boolean): String {
        return if (value) {
            "true (1)"
        } else {
            "false (0)"
        }
    }
}

open class SqGenericBooleanType: SqGenericJBooleanTypeBase() {
    override val sqlType: SQLType
        get() = JDBCType.BOOLEAN

    override fun prepareNotNullValueForComment(value: Boolean): String = value.toString()
}
// endregion

// region ByteArray - BINARY, VARBINARY, LONGVARBINARY
abstract class SqGenericJByteArrayTypeBase: SqDetailedType<SqByteArray>() {
    override fun readImpl(source: ResultSet, columnIndex: Int): SqByteArray? = source.getBytes(columnIndex)?.toSqArray()
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: SqByteArray) { target.setBytes(parameterIndex, value.value) }

    override fun prepareNotNullValueForComment(value: SqByteArray): String = buildString {
        this.append(this@SqGenericJByteArrayTypeBase.sqlType).append(" (").append(value.value.size).append(" byte(s))")
    }
}

open class SqGenericBinaryType: SqGenericJByteArrayTypeBase() {
    override val sqlType: SQLType
        get() = JDBCType.BINARY
}

open class SqGenericVarBinaryType: SqGenericJByteArrayTypeBase() {
    override val sqlType: SQLType
        get() = JDBCType.VARBINARY
}

open class SqGenericLongVarBinaryType: SqGenericJByteArrayTypeBase() {
    override val sqlType: SQLType
        get() = JDBCType.LONGVARBINARY
}
// endregion

// region Number - DOUBLE, FLOAT, REAL, BIGINT, INTEGER, DECIMAL, NUMERIC
abstract class SqGenericJNumberTypeBase<JAVA: Number>: SqDetailedType<JAVA>() {
    override fun prepareNotNullValueForComment(value: JAVA): String = value.toString()
}


abstract class SqGenericJDoubleTypeBase: SqGenericJNumberTypeBase<Double>() {
    override fun readImpl(source: ResultSet, columnIndex: Int): Double = source.getDouble(columnIndex)
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Double) { target.setDouble(parameterIndex, value) }
}

open class SqGenericDoubleType: SqGenericJDoubleTypeBase() {
    override val sqlType: SQLType
        get() = JDBCType.DOUBLE
}

open class SqGenericFloatType: SqGenericJDoubleTypeBase() {
    override val sqlType: JDBCType
        get() = JDBCType.FLOAT
}

open class SqGenericRealType: SqGenericJNumberTypeBase<Float>() {
    override val sqlType: JDBCType
        get() = JDBCType.REAL

    override fun readImpl(source: ResultSet, columnIndex: Int): Float = source.getFloat(columnIndex)
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Float) { target.setFloat(parameterIndex, value) }
}

open class SqGenericBigIntType: SqGenericJNumberTypeBase<Long>() {
    override val sqlType: JDBCType
        get() = JDBCType.BIGINT

    override fun readImpl(source: ResultSet, columnIndex: Int): Long = source.getLong(columnIndex)
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Long) { target.setLong(parameterIndex, value) }
}

open class SqGenericIntegerType: SqGenericJNumberTypeBase<Int>() {
    override val sqlType: JDBCType
        get() = JDBCType.INTEGER

    override fun readImpl(source: ResultSet, columnIndex: Int): Int = source.getInt(columnIndex)
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Int) { target.setInt(parameterIndex, value) }
}

open class SqGenericSmallIntType: SqGenericJNumberTypeBase<Short>() {
    override val sqlType: JDBCType
        get() = JDBCType.SMALLINT

    override fun readImpl(source: ResultSet, columnIndex: Int): Short? = source.getShort(columnIndex)
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Short) { target.setShort(parameterIndex, value) }
}

open class SqGenericTinyIntType: SqGenericJNumberTypeBase<Byte>() {
    override val sqlType: JDBCType
        get() = JDBCType.TINYINT

    override fun readImpl(source: ResultSet, columnIndex: Int): Byte? = source.getByte(columnIndex)
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Byte) { target.setByte(parameterIndex, value) }
}


abstract class SqGenericJBigDecimalType: SqGenericJNumberTypeBase<BigDecimal>() {
    override fun readImpl(source: ResultSet, columnIndex: Int): BigDecimal? = source.getBigDecimal(columnIndex)
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: BigDecimal) { target.setBigDecimal(parameterIndex, value) }
    override fun prepareNotNullValueForComment(value: BigDecimal): String = value.toPlainString()
}

open class SqGenericDecimalType: SqGenericJBigDecimalType() {
    override val sqlType: JDBCType
        get() = JDBCType.DECIMAL
}

open class SqGenericNumericType: SqGenericJBigDecimalType() {
    override val sqlType: JDBCType
        get() = JDBCType.NUMERIC
}
// endregion

// region String - CHAR, VARCHAR, LONGVARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR
abstract class SqGenericJStringType: SqDetailedType<String>() {
    override fun readImpl(source: ResultSet, columnIndex: Int): String? = source.getString(columnIndex)
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: String) { target.setString(parameterIndex, value) }
    override fun prepareNotNullValueForComment(value: String): String = prepareStringValueForComment(value)
}

open class SqGenericCharType: SqGenericJStringType() {
    override val sqlType: JDBCType
        get() = JDBCType.CHAR
}

open class SqGenericVarCharType: SqGenericJStringType() {
    override val sqlType: JDBCType
        get() = JDBCType.VARCHAR
}

open class SqGenericLongVarCharType: SqGenericJStringType() {
    override val sqlType: JDBCType
        get() = JDBCType.LONGVARCHAR
}


abstract class SqGenericJStringNType: SqGenericJStringType() {
    override fun readImpl(source: ResultSet, columnIndex: Int): String? = source.getNString(columnIndex)
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: String) { target.setNString(parameterIndex, value) }
}

open class SqGenericNCharType: SqGenericJStringNType() {
    override val sqlType: JDBCType
        get() = JDBCType.NCHAR
}

open class SqGenericNVarCharType: SqGenericJStringNType() {
    override val sqlType: JDBCType
        get() = JDBCType.NVARCHAR
}

open class SqGenericLongNVarCharType: SqGenericJStringNType() {
    override val sqlType: JDBCType
        get() = JDBCType.LONGNVARCHAR
}
// endregion

// region Temporal - TIME, DATE, TIMESTAMP
open class SqGenericTimeType: SqDetailedType<Time>() {
    override val sqlType: JDBCType
        get() = JDBCType.TIME

    override fun readImpl(source: ResultSet, columnIndex: Int): Time? = source.getTime(columnIndex)
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Time) { target.setTime(parameterIndex, value) }
    override fun prepareNotNullValueForComment(value: Time): String = value.toString()
}

open class SqGenericDateType: SqDetailedType<Date>() {
    override val sqlType: JDBCType
        get() = JDBCType.DATE

    override fun readImpl(source: ResultSet, columnIndex: Int): Date? = source.getDate(columnIndex)
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Date) { target.setDate(parameterIndex, value) }
    override fun prepareNotNullValueForComment(value: Date): String = value.toString()
}

open class SqGenericTimestampType: SqDetailedType<Timestamp>() {
    override val sqlType: JDBCType
        get() = JDBCType.TIMESTAMP

    override fun readImpl(source: ResultSet, columnIndex: Int): Timestamp? = source.getTimestamp(columnIndex)
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Timestamp) { target.setTimestamp(parameterIndex, value) }
    override fun prepareNotNullValueForComment(value: Timestamp): String = value.toString()
}


abstract class SqGenericJLocalTimeType: SqGenericJCastingType<LocalTime>() {
    override val valueClass: Class<LocalTime>
        get() = LocalTime::class.java
    override val sqlType: JDBCType
        get() = JDBCType.TIME

    override fun prepareNotNullValueForComment(value: LocalTime): String = value.toString()
}

abstract class SqGenericJOffsetTimeType: SqGenericJCastingType<OffsetTime>() {
    override val valueClass: Class<OffsetTime>
        get() = OffsetTime::class.java
    override val sqlType: JDBCType
        get() = JDBCType.TIME

    override fun prepareNotNullValueForComment(value: OffsetTime): String = value.toString()
}

abstract class SqGenericJLocalDateType: SqGenericJCastingType<LocalDate>() {
    override val valueClass: Class<LocalDate>
        get() = LocalDate::class.java
    override val sqlType: JDBCType
        get() = JDBCType.DATE

    override fun prepareNotNullValueForComment(value: LocalDate): String = value.toString()
}

abstract class SqGenericJLocalDateTimeType: SqGenericJCastingType<LocalDateTime>() {
    override val valueClass: Class<LocalDateTime>
        get() = LocalDateTime::class.java
    override val sqlType: JDBCType
        get() = JDBCType.TIMESTAMP

    override fun prepareNotNullValueForComment(value: LocalDateTime): String = value.toString()
}

abstract class SqGenericJOffsetDateTimeType: SqGenericJCastingType<OffsetDateTime>() {
    override val valueClass: Class<OffsetDateTime>
        get() = OffsetDateTime::class.java
    override val sqlType: JDBCType
        get() = JDBCType.TIMESTAMP_WITH_TIMEZONE

    override fun prepareNotNullValueForComment(value: OffsetDateTime): String = value.toString()
}
// endregion

// region Large objects - BLOB, CLOB, NCLOB
open class SqGenericBlobType: SqDetailedType<Blob>() {
    override val sqlType: JDBCType
        get() = JDBCType.BLOB

    override fun readImpl(source: ResultSet, columnIndex: Int): Blob? = source.getBlob(columnIndex)
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Blob) { target.setBlob(parameterIndex, value) }

    override fun prepareNotNullValueForComment(value: Blob): String = buildString {
        this.append("BLOB, ").append(value.length()).append(" byte(s)")
    }
}

open class SqGenericClobType: SqDetailedType<Clob>() {
    override val sqlType: JDBCType
        get() = JDBCType.CLOB

    override fun readImpl(source: ResultSet, columnIndex: Int): Clob? = source.getClob(columnIndex)
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Clob) { target.setClob(parameterIndex, value) }

    override fun prepareNotNullValueForComment(value: Clob): String = buildString {
        this.append("CLOB, ").append(value.length()).append(" character(s)")
    }
}

open class SqGenericNClobType: SqDetailedType<NClob>() {
    override val sqlType: JDBCType
        get() = JDBCType.NCLOB

    override fun readImpl(source: ResultSet, columnIndex: Int): NClob? = source.getNClob(columnIndex)
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: NClob) { target.setNClob(parameterIndex, value) }

    override fun prepareNotNullValueForComment(value: NClob): String = buildString {
        this.append("NCLOB, ").append(value.length()).append(" character(s)")
    }
}
// endregion

// region Various - DATALINK, REF, ROWID, SQLXML, MATH_OP_NUMBER
open class SqGenericDataLinkType: SqDetailedType<URL>() {
    override val sqlType: JDBCType
        get() = JDBCType.DATALINK

    override fun readImpl(source: ResultSet, columnIndex: Int): URL? = source.getURL(columnIndex)
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: URL) { target.setURL(parameterIndex, value) }

    override fun prepareNotNullValueForComment(value: URL): String = prepareStringValueForComment(value.toString())
}

open class SqGenericRefType: SqDetailedType<Ref>() {
    override val sqlType: JDBCType
        get() = JDBCType.REF

    override fun readImpl(source: ResultSet, columnIndex: Int): Ref? = source.getRef(columnIndex)
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Ref) { target.setRef(parameterIndex, value) }

    override fun prepareNotNullValueForComment(value: Ref): String = "Ref to instance of \"${value.baseTypeName}\""
}

open class SqGenericRowIdType: SqDetailedType<RowId>() {
    override val sqlType: JDBCType
        get() = JDBCType.ROWID

    override fun readImpl(source: ResultSet, columnIndex: Int): RowId? = source.getRowId(columnIndex)
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: RowId) { target.setRowId(parameterIndex, value) }

    override fun prepareNotNullValueForComment(value: RowId): String = "<ROWID>"
}

open class SqGenericSqlXmlType: SqDetailedType<SQLXML>() {
    override val sqlType: JDBCType
        get() = JDBCType.SQLXML

    override fun readImpl(source: ResultSet, columnIndex: Int): SQLXML? = source.getSQLXML(columnIndex)
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: SQLXML) { target.setSQLXML(parameterIndex, value) }

    override fun prepareNotNullValueForComment(value: SQLXML): String = "<SQLXML>"
}

open class SqGenericJNumberType: SqDetailedType<Number>() {
    override val sqlType: SQLType
        get() = JDBCType.DOUBLE

    override fun readImpl(source: ResultSet, columnIndex: Int): Number? {
        return when (val result = source.getObject(columnIndex)) {
            null -> null
            is Number -> result
            else -> {
                val message = buildString {
                    this
                        .append("Column with index ")
                        .append(columnIndex)
                        .append(" contains value, which is not instance of class ")
                        .append(Number::class.java.name)
                        .append(" (value is instance of class ")
                        .append(result.javaClass.name)
                        .append(')')
                }
                throw IllegalStateException(message)
            }
        }
    }

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Number) {
        when (value) {
            is Int -> target.setInt(parameterIndex, value)
            is Long -> target.setLong(parameterIndex, value)
            is Double -> target.setDouble(parameterIndex, value)
            is Byte -> target.setByte(parameterIndex, value)
            is Float -> target.setFloat(parameterIndex, value)
            is Short -> target.setShort(parameterIndex, value)
            is BigDecimal -> target.setBigDecimal(parameterIndex, value)
            else -> target.setObject(parameterIndex, value, this.sqlType)
        }
    }

    override fun prepareNotNullValueForComment(value: Number): String = value.toString()
}
// endregion


object SqGenericTypes {
    // region Boolean - BIT, BOOLEAN
    val BIT: SqType<Boolean> = SqGenericBitType()
    val BOOLEAN: SqType<Boolean> = SqGenericBooleanType()
    // endregion

    // region ByteArray - BINARY, VARBINARY, LONGVARBINARY
    val BINARY: SqType<SqByteArray> = SqGenericBinaryType()
    val VAR_BINARY: SqType<SqByteArray> = SqGenericVarBinaryType()
    val LONG_VAR_BINARY: SqType<SqByteArray> = SqGenericLongVarBinaryType()
    // endregion

    // region Number - DOUBLE, FLOAT, REAL, BIGINT, INTEGER, DECIMAL, NUMERIC, J_NUMBER
    val DOUBLE: SqType<Double> = SqGenericDoubleType()
    val FLOAT: SqType<Double> = SqGenericFloatType()
    val REAL: SqType<Float> = SqGenericRealType()
    val BIG_INT: SqType<Long> = SqGenericBigIntType()
    val INTEGER: SqType<Int> = SqGenericIntegerType()
    val SMALL_INT: SqType<Short> = SqGenericSmallIntType()
    val TINY_INT: SqType<Byte> = SqGenericTinyIntType()
    val DECIMAL: SqType<BigDecimal> = SqGenericDecimalType()
    val NUMERIC: SqType<BigDecimal> = SqGenericNumericType()
    val J_NUMBER: SqType<Number> = SqGenericJNumberType()
    // endregion

    // region String - CHAR, VARCHAR, LONGVARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR
    val CHAR: SqType<String> = SqGenericCharType()
    val VAR_CHAR: SqType<String> = SqGenericVarCharType()
    val LONG_VAR_CHAR: SqType<String> = SqGenericLongVarCharType()
    val N_CHAR: SqType<String> = SqGenericNCharType()
    val N_VAR_CHAR: SqType<String> = SqGenericNVarCharType()
    val LONG_N_VAR_CHAR: SqType<String> = SqGenericLongNVarCharType()
    // endregion

    // region Temporal - TIME, DATE, TIMESTAMP
    val TIME: SqType<Time> = SqGenericTimeType()
    val DATE: SqType<Date> = SqGenericDateType()
    val TIMESTAMP: SqType<Timestamp> = SqGenericTimestampType()
    // endregion

    // region Large objects - BLOB, CLOB, NCLOB
    val BLOB: SqType<Blob> = SqGenericBlobType()
    val CLOB: SqType<Clob> = SqGenericClobType()
    val N_CLOB: SqType<NClob> = SqGenericNClobType()
    // endregion

    // region Various - DATALINK, REF, ROWID, SQLXML
    val DATA_LINK: SqType<URL> = SqGenericDataLinkType()
    val REF: SqType<Ref> = SqGenericRefType()
    val ROW_ID: SqType<RowId> = SqGenericRowIdType()
    val SQL_XML: SqType<SQLXML> = SqGenericSqlXmlType()
    // endregion
}
