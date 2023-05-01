package me.ore.sq.generic

import me.ore.sq.SqValueReader
import me.ore.sq.SqValueWriterBase
import me.ore.sq.util.SqUtil
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import java.sql.*
import java.time.*


abstract class SqGenericValueReaderBase<JAVA: Any>: SqValueReader<JAVA> {
    protected abstract fun simpleRead(source: ResultSet, columnIndex: Int): JAVA?
    protected abstract val expectedClass: Class<JAVA>?
    protected abstract fun complexRead(expectedClassException: Exception?, source: ResultSet, columnIndex: Int, value: Any): JAVA?

    override fun readNullable(source: ResultSet, columnIndex: Int): JAVA? {
        val simpleReadResult = this.simpleRead(source, columnIndex)
        if (simpleReadResult != null) return simpleReadResult

        var expectedClassException: Exception? = null
        val expectedClass = this.expectedClass
        if (expectedClass != null) {
            try {
                val result = source.getObject(columnIndex, expectedClass)
                return if (source.wasNull()) {
                    null
                } else {
                    result
                        ?: error("Read by expected class - got NULL for column index $columnIndex and class ${expectedClass.name}")
                }
            } catch (e: Exception) {
                expectedClassException = e
            }
        }

        return try {
            source.getObject(columnIndex)?.let { value ->
                this.complexRead(expectedClassException, source, columnIndex, value)
            }
        } catch (e: Exception) {
            expectedClassException?.let { e.addSuppressed(it) }
            throw e
        }
    }
}


// region Boolean types
open class SqGenericBooleanReader: SqGenericValueReaderBase<Boolean>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): Boolean? {
        return source.getBoolean(columnIndex).takeUnless { source.wasNull() }
    }

    override val expectedClass: Class<Boolean>
        get() = Boolean::class.java

    override fun complexRead(expectedClassException: Exception?, source: ResultSet, columnIndex: Int, value: Any): Boolean {
        return if (value is Boolean) {
            value
        } else {
            SqUtil.throwUnexpectedClassException(columnIndex, value, Boolean::class.java)
        }
    }
}

open class SqGenericBooleanWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<Boolean>() {
    override fun notNullValueToComment(value: Boolean): String = value.toString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Boolean) {
        target.setBoolean(parameterIndex, value)
    }
}
// endregion


// region Byte array types
open class SqGenericByteArrayReader: SqGenericValueReaderBase<ByteArray>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): ByteArray? {
        return source.getBytes(columnIndex)
    }

    override val expectedClass: Class<ByteArray>
        get() = ByteArray::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): ByteArray {
        return when (value) {
            is ByteArray -> value
            else -> SqUtil.throwUnexpectedClassException(columnIndex, value, ByteArray::class.java)
        }
    }
}

open class SqGenericByteArrayWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<ByteArray>() {
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: ByteArray) {
        target.setBytes(parameterIndex, value)
    }

    override fun notNullValueToComment(value: ByteArray): String = "<${value.size} byte(s)>"
}
// endregion


// region Date/time types
open class SqGenericDateReader: SqGenericValueReaderBase<Date>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): Date? {
        return source.getDate(columnIndex)
    }

    override val expectedClass: Class<Date>
        get() = Date::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): Date {
        return when (value) {
            is Date -> value
            else -> SqUtil.throwUnexpectedClassException(columnIndex, value, Date::class.java)
        }
    }
}

open class SqGenericDateWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<Date>() {
    override fun notNullValueToComment(value: Date): String = value.toString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Date) {
        target.setDate(parameterIndex, value)
    }
}


open class SqGenericLocalDateReader: SqGenericValueReaderBase<LocalDate>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): LocalDate? = null

    override val expectedClass: Class<LocalDate>
        get() = LocalDate::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): LocalDate? {
        return when (value) {
            is java.util.Date -> LocalDateTime.ofInstant(value.toInstant(), ZoneId.systemDefault()).toLocalDate()
            is LocalDate -> value
            else -> SqUtil.throwUnexpectedClassException(columnIndex, value, java.util.Date::class.java, LocalDate::class.java)
        }
    }
}

open class SqGenericLocalDateWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<LocalDate>() {
    override fun notNullValueToComment(value: LocalDate): String = value.toString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: LocalDate) {
        target.setObject(parameterIndex, value)
    }
}


open class SqGenericLocalDateTimeReader: SqGenericValueReaderBase<LocalDateTime>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): LocalDateTime? = null

    override val expectedClass: Class<LocalDateTime>
        get() = LocalDateTime::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): LocalDateTime {
        return when (value) {
            is Timestamp -> LocalDateTime.ofInstant(value.toInstant(), ZoneId.systemDefault())
            is LocalDateTime -> value
            else -> SqUtil.throwUnexpectedClassException(columnIndex, value, Timestamp::class.java, LocalDateTime::class.java)
        }
    }
}

open class SqGenericLocalDateTimeWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<LocalDateTime>() {
    override fun notNullValueToComment(value: LocalDateTime): String = value.toString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: LocalDateTime) {
        target.setObject(parameterIndex, value)
    }
}


open class SqGenericLocalTimeReader: SqGenericValueReaderBase<LocalTime>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): LocalTime? = null

    override val expectedClass: Class<LocalTime>
        get() = LocalTime::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): LocalTime? {
        return when (value) {
            is Time -> LocalDateTime.ofInstant(value.toInstant(), ZoneId.systemDefault()).toLocalTime()
            is LocalTime -> value
            else -> SqUtil.throwUnexpectedClassException(columnIndex, value, Time::class.java, LocalTime::class.java)
        }
    }
}

open class SqGenericLocalTimeWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<LocalTime>() {
    override fun notNullValueToComment(value: LocalTime): String = value.toString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: LocalTime) {
        target.setObject(parameterIndex, value)
    }
}


open class SqGenericOffsetDateTimeReader: SqGenericValueReaderBase<OffsetDateTime>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): OffsetDateTime? = null

    override val expectedClass: Class<OffsetDateTime>
        get() = OffsetDateTime::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): OffsetDateTime {
        return when (value) {
            is java.util.Date -> value.toInstant().atOffset(OffsetTime.now().offset)
            is OffsetDateTime -> value
            else -> SqUtil.throwUnexpectedClassException(columnIndex, value, java.util.Date::class.java, OffsetDateTime::class.java)
        }
    }
}

open class SqGenericOffsetDateTimeWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<OffsetDateTime>() {
    override fun notNullValueToComment(value: OffsetDateTime): String = value.toString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: OffsetDateTime) {
        target.setObject(parameterIndex, value)
    }
}


open class SqGenericOffsetTimeReader: SqGenericValueReaderBase<OffsetTime>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): OffsetTime? = null

    override val expectedClass: Class<OffsetTime>
        get() = OffsetTime::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): OffsetTime {
        return when (value) {
            is java.util.Date -> value.toInstant().atOffset(OffsetTime.now().offset).toOffsetTime()
            is OffsetTime -> value
            else -> SqUtil.throwUnexpectedClassException(columnIndex, value, java.util.Date::class.java, OffsetTime::class.java)
        }
    }
}

open class SqGenericOffsetTimeWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<OffsetTime>() {
    override fun notNullValueToComment(value: OffsetTime): String = value.toString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: OffsetTime) {
        target.setObject(parameterIndex, value)
    }
}


open class SqGenericTimeReader: SqGenericValueReaderBase<Time>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): Time? {
        return source.getTime(columnIndex)
    }

    override val expectedClass: Class<Time>
        get() = Time::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): Time {
        return when (value) {
            is Time -> value
            else -> SqUtil.throwUnexpectedClassException(columnIndex, value, Time::class.java)
        }
    }
}

open class SqGenericTimeWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<Time>() {
    override fun notNullValueToComment(value: Time): String = value.toString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Time) {
        target.setTime(parameterIndex, value)
    }
}


open class SqGenericTimestampReader: SqGenericValueReaderBase<Timestamp>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): Timestamp? {
        return source.getTimestamp(columnIndex)
    }

    override val expectedClass: Class<Timestamp>
        get() = Timestamp::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): Timestamp {
        return when (value) {
            is Timestamp -> value
            else -> SqUtil.throwUnexpectedClassException(columnIndex, value, Timestamp::class.java)
        }
    }
}

open class SqGenericTimestampWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<Timestamp>() {
    override fun notNullValueToComment(value: Timestamp): String = value.toString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Timestamp) {
        target.setTimestamp(parameterIndex, value)
    }
}
// endregion


// region Number types
open class SqGenericBigDecimalReader: SqGenericValueReaderBase<BigDecimal>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): BigDecimal? = source.getBigDecimal(columnIndex)

    override val expectedClass: Class<BigDecimal>
        get() = BigDecimal::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): BigDecimal? {
        return when (value) {
            is BigDecimal -> value
            is Long -> BigDecimal.valueOf(value)
            is Double -> BigDecimal.valueOf(value)
            is Number -> BigDecimal.valueOf(value.toDouble())
            else -> SqUtil.throwUnexpectedClassException(columnIndex, value, BigDecimal::class.java, Number::class.java)
        }
    }
}

open class SqGenericBigDecimalWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<BigDecimal>() {
    override fun notNullValueToComment(value: BigDecimal): String = value.toPlainString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: BigDecimal) {
        target.setBigDecimal(parameterIndex, value)
    }
}


open class SqGenericBigIntegerReader: SqGenericValueReaderBase<BigInteger>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): BigInteger? = null

    override val expectedClass: Class<BigInteger>?
        get() = null

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): BigInteger? {
        return when (value) {
            is BigInteger -> value
            is Long -> BigInteger.valueOf(value)
            is Number -> BigInteger.valueOf(value.toLong())
            else -> SqUtil.throwUnexpectedClassException(columnIndex, value, BigInteger::class.java, Long::class.java, Number::class.java)
        }
    }
}

open class SqGenericBigIntegerWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<BigInteger>() {
    override fun notNullValueToComment(value: BigInteger): String = value.toString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: BigInteger) {
        target.setObject(parameterIndex, value)
    }
}


open class SqGenericByteReader: SqGenericValueReaderBase<Byte>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): Byte? {
        return source.getByte(columnIndex).takeUnless { source.wasNull() }
    }

    override val expectedClass: Class<Byte>
        get() = Byte::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): Byte {
        return when (value) {
            is Byte -> value
            is Number -> value.toByte()
            else -> SqUtil.throwUnexpectedClassException(columnIndex, value, Byte::class.java, Number::class.java)
        }
    }
}

open class SqGenericByteWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<Byte>() {
    override fun notNullValueToComment(value: Byte): String = value.toString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Byte) {
        target.setByte(parameterIndex, value)
    }
}


open class SqGenericDoubleReader: SqGenericValueReaderBase<Double>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): Double? {
        return source.getDouble(columnIndex).takeUnless { source.wasNull() }
    }

    override val expectedClass: Class<Double>
        get() = Double::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): Double {
        return when (value) {
            is Double -> value
            is Number -> value.toDouble()
            else -> SqUtil.throwUnexpectedClassException(columnIndex, value, Double::class.java, Number::class.java)
        }
    }
}

open class SqGenericDoubleWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<Double>() {
    override fun notNullValueToComment(value: Double): String = value.toString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Double) {
        target.setDouble(parameterIndex, value)
    }
}


open class SqGenericFloatReader: SqGenericValueReaderBase<Float>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): Float? {
        return source.getFloat(columnIndex).takeUnless { source.wasNull() }
    }

    override val expectedClass: Class<Float>
        get() = Float::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): Float {
        return when (value) {
            is Float -> value
            is Number -> value.toFloat()
            else -> SqUtil.throwUnexpectedClassException(columnIndex, value, Float::class.java, Number::class.java)
        }
    }
}

open class SqGenericFloatWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<Float>() {
    override fun notNullValueToComment(value: Float): String = value.toString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Float) {
        target.setFloat(parameterIndex, value)
    }
}


open class SqGenericIntReader: SqGenericValueReaderBase<Int>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): Int? {
        return source.getInt(columnIndex).takeUnless { source.wasNull() }
    }

    override val expectedClass: Class<Int>
        get() = Int::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): Int {
        return when (value) {
            is Int -> value
            is Number -> value.toInt()
            else -> SqUtil.throwUnexpectedClassException(columnIndex, value, Int::class.java, Number::class.java)
        }
    }
}

open class SqGenericIntWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<Int>() {
    override fun notNullValueToComment(value: Int): String = value.toString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Int) {
        target.setInt(parameterIndex, value)
    }
}


open class SqGenericLongReader: SqGenericValueReaderBase<Long>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): Long? {
        return source.getLong(columnIndex).takeUnless { source.wasNull() }
    }

    override val expectedClass: Class<Long>
        get() = Long::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): Long {
        return when (value) {
            is Long -> value
            is Number -> value.toLong()
            else -> SqUtil.throwUnexpectedClassException(columnIndex, value, Long::class.java, Number::class.java)
        }
    }
}

open class SqGenericLongWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<Long>() {
    override fun notNullValueToComment(value: Long): String = value.toString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Long) {
        target.setLong(parameterIndex, value)
    }
}


open class SqGenericNumberReader: SqGenericValueReaderBase<Number>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): Number? = null

    override val expectedClass: Class<Number>
        get() = Number::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): Number {
        return if (value is Number) {
            value
        } else {
            SqUtil.throwUnexpectedClassException(columnIndex, value, Number::class.java)
        }
    }
}

open class SqGenericNumberWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<Number>() {
    override fun notNullValueToComment(value: Number): String = value.toString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Number) {
        target.setObject(parameterIndex, value)
    }
}


open class SqGenericShortReader: SqGenericValueReaderBase<Short>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): Short? {
        return source.getShort(columnIndex).takeUnless { source.wasNull() }
    }

    override val expectedClass: Class<Short>
        get() = Short::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): Short {
        return when (value) {
            is Short -> value
            is Number -> value.toShort()
            else -> SqUtil.throwUnexpectedClassException(columnIndex, value, Short::class.java, Number::class.java)
        }
    }
}

open class SqGenericShortWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<Short>() {
    override fun notNullValueToComment(value: Short): String = value.toString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Short) {
        target.setShort(parameterIndex, value)
    }
}
// endregion


// region Text types
open class SqGenericNStringValueReader: SqGenericValueReaderBase<String>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): String? = source.getNString(columnIndex)

    override val expectedClass: Class<String>
        get() = String::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): String = value.toString()
}

open class SqGenericNStringValueWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<String>() {
    override fun notNullValueToComment(value: String): String = SqUtil.prepareStringValueForComment(value)

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: String) {
        target.setNString(parameterIndex, value)
    }
}


open class SqGenericStringValueReader: SqGenericValueReaderBase<String>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): String? = source.getString(columnIndex)

    override val expectedClass: Class<String>
        get() = String::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): String = value.toString()
}

open class SqGenericStringValueWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<String>() {
    override fun notNullValueToComment(value: String): String = SqUtil.prepareStringValueForComment(value)

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: String) {
        target.setString(parameterIndex, value)
    }
}
// endregion


// region Blob/Clob types
open class SqGenericBlobReader: SqGenericValueReaderBase<Blob>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): Blob? {
        return source.getBlob(columnIndex).takeUnless { source.wasNull() }
    }

    override val expectedClass: Class<Blob>
        get() = Blob::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): Blob {
        return if (value is Blob) {
            value
        } else {
            SqUtil.throwUnexpectedClassException(columnIndex, value, Blob::class.java)
        }
    }
}

open class SqGenericBlobWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<Blob>() {
    override fun notNullValueToComment(value: Blob): String = "BLOB (${value.length()} byte(s))"

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Blob) {
        target.setBlob(parameterIndex, value)
    }
}


open class SqGenericClobReader: SqGenericValueReaderBase<Clob>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): Clob? {
        return source.getClob(columnIndex).takeUnless { source.wasNull() }
    }

    override val expectedClass: Class<Clob>
        get() = Clob::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): Clob {
        return if (value is Clob) {
            value
        } else {
            SqUtil.throwUnexpectedClassException(columnIndex, value, Clob::class.java)
        }
    }
}

open class SqGenericClobWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<Clob>() {
    override fun notNullValueToComment(value: Clob): String = "CLOB (${value.length()} character(s))"

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Clob) {
        target.setClob(parameterIndex, value)
    }
}


open class SqGenericNClobReader: SqGenericValueReaderBase<NClob>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): NClob? {
        return source.getNClob(columnIndex).takeUnless { source.wasNull() }
    }

    override val expectedClass: Class<NClob>
        get() = NClob::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): NClob {
        return if (value is NClob) {
            value
        } else {
            SqUtil.throwUnexpectedClassException(columnIndex, value, NClob::class.java)
        }
    }
}

open class SqGenericNClobWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<NClob>() {
    override fun notNullValueToComment(value: NClob): String = "NCLOB (${value.length()} character(s))"

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: NClob) {
        target.setNClob(parameterIndex, value)
    }
}
// endregion


// region Other JDBC types
open class SqGenericRefReader: SqGenericValueReaderBase<Ref>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): Ref? {
        return source.getRef(columnIndex).takeUnless { source.wasNull() }
    }

    override val expectedClass: Class<Ref>
        get() = Ref::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): Ref {
        return if (value is Ref) {
            value
        } else {
            SqUtil.throwUnexpectedClassException(columnIndex, value, Ref::class.java)
        }
    }
}

open class SqGenericRefWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<Ref>() {
    override fun notNullValueToComment(value: Ref): String {
        return "REF to \"${value.baseTypeName}\": ${SqUtil.prepareStringValueForComment(value.toString())}"
    }

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Ref) {
        target.setRef(parameterIndex, value)
    }
}


open class SqGenericRowIdReader: SqGenericValueReaderBase<RowId>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): RowId? {
        return source.getRowId(columnIndex)
    }

    override val expectedClass: Class<RowId>
        get() = RowId::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): RowId {
        return if (value is RowId) {
            value
        } else {
            SqUtil.throwUnexpectedClassException(columnIndex, value, RowId::class.java)
        }
    }
}

open class SqGenericRowIdWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<RowId>() {
    override fun notNullValueToComment(value: RowId): String {
        return "ROW ID: ${SqUtil.prepareStringValueForComment(value.toString())}"
    }

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: RowId) {
        target.setRowId(parameterIndex, value)
    }
}


open class SqGenericSqlXmlReader: SqGenericValueReaderBase<SQLXML>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): SQLXML? {
        return source.getSQLXML(columnIndex).takeUnless { source.wasNull() }
    }

    override val expectedClass: Class<SQLXML>
        get() = SQLXML::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): SQLXML {
        return if (value is SQLXML) {
            value
        } else {
            SqUtil.throwUnexpectedClassException(columnIndex, value, SQLXML::class.java)
        }
    }
}

open class SqGenericSqlXmlWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<SQLXML>() {
    override fun notNullValueToComment(value: SQLXML): String {
        return "SQL XML: ${SqUtil.prepareStringValueForComment(value.toString())}"
    }

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: SQLXML) {
        target.setSQLXML(parameterIndex, value)
    }
}


open class SqGenericUrlReader: SqGenericValueReaderBase<URL>() {
    override fun simpleRead(source: ResultSet, columnIndex: Int): URL? {
        return source.getURL(columnIndex)
    }

    override val expectedClass: Class<URL>
        get() = URL::class.java

    override fun complexRead(
        expectedClassException: Exception?,
        source: ResultSet,
        columnIndex: Int,
        value: Any
    ): URL {
        return when (value) {
            is URL -> value
            is String -> URL(value)
            else -> SqUtil.throwUnexpectedClassException(columnIndex, value, URL::class.java, String::class.java)
        }
    }
}

open class SqGenericUrlWriter(
    override val sqlType: Int,
    override val typeName: String? = null,
): SqValueWriterBase<URL>() {
    override fun notNullValueToComment(value: URL): String {
        return "DATA LINK: ${SqUtil.prepareStringValueForComment(value.toString())}"
    }

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: URL) {
        target.setURL(parameterIndex, value)
    }
}
// endregion
