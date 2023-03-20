package me.ore.sq.generic

import me.ore.sq.*
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import java.sql.Blob
import java.sql.Clob
import java.sql.Date
import java.sql.JDBCType
import java.sql.NClob
import java.sql.Ref
import java.sql.ResultSet
import java.sql.RowId
import java.sql.SQLType
import java.sql.SQLXML
import java.sql.Time
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZoneId
import java.util.*


object SqGenericTypes {
    // region Simple text types
    private class StringType(override val sqlType: SQLType): SqTypeBase<String>() {
        override fun readNullableImpl(source: ResultSet, columnIndex: Int): String? {
            return when (val value = source.getObject(columnIndex)) {
                null -> null
                is String -> value
                else -> SqUtil.throwUnexpectedColumnValueClassException(columnIndex, value, String::class.java)
            }
        }

        override fun prepareNotNullValueForComment(value: String): String = SqUtil.prepareStringValueForComment(value)
    }

    val CHAR: SqType<String> = StringType(JDBCType.CHAR)
    val VAR_CHAR: SqType<String> = StringType(JDBCType.VARCHAR)
    val LONG_VAR_CHAR: SqType<String> = StringType(JDBCType.LONGVARCHAR)
    val N_CHAR: SqType<String> = StringType(JDBCType.NCHAR)
    val N_VAR_CHAR: SqType<String> = StringType(JDBCType.NVARCHAR)
    val LONG_N_VAR_CHAR: SqType<String> = StringType(JDBCType.LONGNVARCHAR)
    // endregion


    // region Number types
    private class BigDecimalType(override val sqlType: SQLType): SqTypeBase<BigDecimal>() {
        override fun readNullableImpl(source: ResultSet, columnIndex: Int): BigDecimal? {
            return when (val value = source.getObject(columnIndex)) {
                null -> null
                is BigDecimal -> value
                is BigInteger -> value.toBigDecimal()
                is Number -> BigDecimal.valueOf(value.toDouble())
                else -> SqUtil.throwUnexpectedColumnValueClassException(columnIndex, value, BigDecimal::class.java, BigInteger::class.java, Number::class.java)
            }
        }
    }

    val NUMERIC: SqType<BigDecimal> = BigDecimalType(JDBCType.NUMERIC)
    val DECIMAL: SqType<BigDecimal> = BigDecimalType(JDBCType.DECIMAL)


    private class BigIntegerType(override val sqlType: SQLType): SqTypeBase<BigInteger>() {
        override fun readNullableImpl(source: ResultSet, columnIndex: Int): BigInteger? {
            return when (val value = source.getObject(columnIndex)) {
                null -> null
                is BigInteger -> value
                is BigDecimal -> value.toBigInteger()
                is Number -> BigInteger.valueOf(value.toLong())
                else -> SqUtil.throwUnexpectedColumnValueClassException(columnIndex, value, BigInteger::class.java, BigDecimal::class.java, Number::class.java)
            }
        }
    }

    val BIG_INT__AS__BIG_INTEGER: SqType<BigInteger> = BigIntegerType(JDBCType.BIGINT)


    private class ByteType(override val sqlType: SQLType): SqTypeBase<Byte>() {
        override fun readNullableImpl(source: ResultSet, columnIndex: Int): Byte? {
            return when (val value = source.getObject(columnIndex)) {
                null -> null
                is Byte -> value
                is Number -> value.toByte()
                else -> SqUtil.throwUnexpectedColumnValueClassException(columnIndex, value, Byte::class.java, Number::class.java)
            }
        }
    }

    val TINY_INT: SqType<Byte> = ByteType(JDBCType.TINYINT)


    private class ShortType(override val sqlType: SQLType): SqTypeBase<Short>() {
        override fun readNullableImpl(source: ResultSet, columnIndex: Int): Short? {
            return when (val value = source.getObject(columnIndex)) {
                null -> null
                is Short -> value
                is Number -> value.toShort()
                else -> SqUtil.throwUnexpectedColumnValueClassException(columnIndex, value, Short::class.java, Number::class.java)
            }
        }
    }

    val SMALL_INT: SqType<Short> = ShortType(JDBCType.SMALLINT)


    private class IntType(override val sqlType: SQLType): SqTypeBase<Int>() {
        override fun readNullableImpl(source: ResultSet, columnIndex: Int): Int? {
            return when (val value = source.getObject(columnIndex)) {
                null -> null
                is Int -> value
                is Number -> value.toInt()
                else -> SqUtil.throwUnexpectedColumnValueClassException(columnIndex, value, Int::class.java, Number::class.java)
            }
        }
    }

    val INTEGER: SqType<Int> = IntType(JDBCType.INTEGER)


    private class LongType(override val sqlType: SQLType): SqTypeBase<Long>() {
        override fun readNullableImpl(source: ResultSet, columnIndex: Int): Long? {
            return when (val value = source.getObject(columnIndex)) {
                null -> null
                is Long -> value
                is Number -> value.toLong()
                else -> SqUtil.throwUnexpectedColumnValueClassException(columnIndex, value, Long::class.java, Number::class.java)
            }
        }
    }

    val BIG_INT: SqType<Long> = LongType(JDBCType.BIGINT)


    private class FloatType(override val sqlType: SQLType): SqTypeBase<Float>() {
        override fun readNullableImpl(source: ResultSet, columnIndex: Int): Float? {
            return when (val value = source.getObject(columnIndex)) {
                null -> null
                is Float -> value
                is Number -> value.toFloat()
                else -> SqUtil.throwUnexpectedColumnValueClassException(columnIndex, value, Float::class.java, Number::class.java)
            }
        }
    }

    val REAL: SqType<Float> = FloatType(JDBCType.REAL)


    private class DoubleType(override val sqlType: SQLType): SqTypeBase<Double>() {
        override fun readNullableImpl(source: ResultSet, columnIndex: Int): Double? {
            return when (val value = source.getObject(columnIndex)) {
                null -> null
                is Double -> value
                is Number -> value.toDouble()
                else -> SqUtil.throwUnexpectedColumnValueClassException(columnIndex, value, Double::class.java, Number::class.java)
            }
        }
    }

    val FLOAT: SqType<Double> = DoubleType(JDBCType.FLOAT)
    val DOUBLE: SqType<Double> = DoubleType(JDBCType.DOUBLE)


    private class InexactNumberType: SqTypeBase<Number>() {
        override val sqlType: SQLType
            get() = JDBCType.DOUBLE

        override fun readNullableImpl(source: ResultSet, columnIndex: Int): Number? {
            return when (val value = source.getObject(columnIndex)) {
                null -> null
                is Number -> value
                else -> SqUtil.throwUnexpectedColumnValueClassException(columnIndex, value, Number::class.java)
            }
        }
    }

    val J_INEXACT_NUMBER_TYPE: SqType<Number> = InexactNumberType()
    // endregion


    // region Boolean types
    private class BooleanType(override val sqlType: SQLType): SqTypeBase<Boolean>() {
        override fun readNullableImpl(source: ResultSet, columnIndex: Int): Boolean? {
            return when (val value = source.getObject(columnIndex)) {
                null -> null
                is Boolean -> value
                else -> SqUtil.throwUnexpectedColumnValueClassException(columnIndex, value, Boolean::class.java)
            }
        }
    }

    val BIT: SqType<Boolean> = BooleanType(JDBCType.BIT)
    val BOOLEAN: SqType<Boolean> = BooleanType(JDBCType.BOOLEAN)
    // endregion


    // region Byte array types
    private class ByteArrayType(override val sqlType: SQLType): SqTypeBase<SqByteArray>() {
        override fun readNullableImpl(source: ResultSet, columnIndex: Int): SqByteArray? {
            return when (val value = source.getObject(columnIndex)) {
                null -> null
                is ByteArray -> SqByteArray(value)
                else -> SqUtil.throwUnexpectedColumnValueClassException(columnIndex, value, ByteArray::class.java)
            }
        }
    }

    val BINARY: SqType<SqByteArray> = ByteArrayType(JDBCType.BINARY)
    val VAR_BINARY: SqType<SqByteArray> = ByteArrayType(JDBCType.VARBINARY)
    val LONG_VAR_BINARY: SqType<SqByteArray> = ByteArrayType(JDBCType.LONGVARBINARY)
    // endregion


    // region Temporal types
    private fun LocalDateTime.epochMillis(): Long = this.atOffset(OffsetDateTime.now().offset).toEpochSecond() * 1000


    private class SqlDateType: SqTypeBase<Date>() {
        override val sqlType: SQLType
            get() = JDBCType.DATE

        override fun readNullableImpl(source: ResultSet, columnIndex: Int): Date? {
            return when (val value = source.getObject(columnIndex)) {
                null -> null
                is Date -> value
                is LocalDate -> Date(value.atStartOfDay().epochMillis())
                else -> SqUtil.throwUnexpectedColumnValueClassException(columnIndex, value, Date::class.java, LocalDate::class.java)
            }
        }
    }

    private class LocalDateType: SqTypeBase<LocalDate>() {
        override val sqlType: SQLType
            get() = JDBCType.DATE

        override fun readNullableImpl(source: ResultSet, columnIndex: Int): LocalDate? {
            try {
                return source.getObject(columnIndex, LocalDate::class.java)
            } catch (e: Exception) { /* No action required */ }

            return when (val value = source.getObject(columnIndex)) {
                null -> null
                is LocalDate -> value
                is Date -> value.toLocalDate()!!
                else -> SqUtil.throwUnexpectedColumnValueClassException(columnIndex, value, LocalDate::class.java, Date::class.java)
            }
        }
    }

    val DATE__AS__SQL_DATE: SqType<Date> = SqlDateType()
    val DATE: SqType<LocalDate> = LocalDateType()


    private class SqlTimeType: SqTypeBase<Time>() {
        override val sqlType: SQLType
            get() = JDBCType.TIME

        override fun readNullableImpl(source: ResultSet, columnIndex: Int): Time? {
            return when (val value = source.getObject(columnIndex)) {
                null -> null
                is Time -> value
                is LocalTime -> Time(value.atDate(LocalDate.now()).epochMillis())
                else -> SqUtil.throwUnexpectedColumnValueClassException(columnIndex, value, Time::class.java, LocalTime::class.java)
            }
        }
    }

    private class LocalTimeType: SqTypeBase<LocalTime>() {
        override val sqlType: SQLType
            get() = JDBCType.TIME

        override fun readNullableImpl(source: ResultSet, columnIndex: Int): LocalTime? {
            try {
                return source.getObject(columnIndex, LocalTime::class.java)
            } catch (e: Exception) { /* No action required */ }

            return when (val value = source.getObject(columnIndex)) {
                null -> null
                is LocalTime -> value
                is Time -> value.toLocalTime()!!
                else -> SqUtil.throwUnexpectedColumnValueClassException(columnIndex, value, LocalTime::class.java, Time::class.java)
            }
        }
    }

    val TIME__AS__SQL_TIME: SqType<Time> = SqlTimeType()
    val TIME: SqType<LocalTime> = LocalTimeType()


    private class SqlTimestampType: SqTypeBase<Timestamp>() {
        override val sqlType: SQLType
            get() = JDBCType.TIMESTAMP

        override fun readNullableImpl(source: ResultSet, columnIndex: Int): Timestamp? {
            return when (val value = source.getObject(columnIndex)) {
                null -> null
                is Timestamp -> value
                is LocalDateTime -> {
                    value
                        .atZone(ZoneId.systemDefault()) // ZonedDateTime
                        .toEpochSecond()
                        .let { Timestamp(it * 1000) }
                }
                is java.util.Date -> Timestamp(value.time)
                is Calendar -> Timestamp(value.timeInMillis)
                else -> {
                    SqUtil.throwUnexpectedColumnValueClassException(
                        columnIndex, value,
                        Timestamp::class.java, LocalDateTime::class.java, java.util.Date::class.java, Calendar::class.java,
                    )
                }
            }
        }
    }
    private class CalendarType: SqTypeBase<Calendar>() {
        companion object {
            private fun calendarFromEpochMillis(time: Long): Calendar = Calendar.getInstance(TimeZone.getDefault()).apply { this.timeInMillis = time }
        }

        override val sqlType: SQLType
            get() = JDBCType.TIMESTAMP

        override fun readNullableImpl(source: ResultSet, columnIndex: Int): Calendar? {
            return when (val value = source.getObject(columnIndex)) {
                null -> null
                is Calendar -> value
                is Timestamp -> calendarFromEpochMillis(value.time)
                is LocalDateTime -> calendarFromEpochMillis(value.epochMillis())
                is java.util.Date -> calendarFromEpochMillis(value.time)
                else -> {
                    SqUtil.throwUnexpectedColumnValueClassException(
                        columnIndex, value,
                        Calendar::class.java, Timestamp::class.java, LocalDateTime::class.java, java.util.Date::class.java,
                    )
                }
            }
        }
    }

    private class UtilDateType: SqTypeBase<java.util.Date>() {
        override val sqlType: SQLType
            get() = JDBCType.TIMESTAMP

        override fun readNullableImpl(source: ResultSet, columnIndex: Int): java.util.Date? {
            return when (val value = source.getObject(columnIndex)) {
                null -> null
                is java.util.Date -> value
                is LocalDateTime -> java.util.Date(value.epochMillis())
                is Calendar -> value.time
                else -> SqUtil.throwUnexpectedColumnValueClassException(columnIndex, value, java.util.Date::class.java, LocalDateTime::class.java, Calendar::class.java)
            }
        }
    }

    private class LocalDateTimeType: SqTypeBase<LocalDateTime>() {
        override val sqlType: SQLType
            get() = JDBCType.TIMESTAMP

        override fun readNullableImpl(source: ResultSet, columnIndex: Int): LocalDateTime? {
            try {
                return source.getObject(columnIndex, LocalDateTime::class.java)
            } catch (e: Exception) { /* No action required */ }

            return when (val value = source.getObject(columnIndex)) {
                null -> null
                is LocalDateTime -> value
                is Timestamp -> value.toLocalDateTime()
                is java.util.Date -> LocalDateTime.ofInstant(value.toInstant(), ZoneId.systemDefault())
                is Calendar -> LocalDateTime.ofInstant(value.toInstant(), ZoneId.systemDefault())
                else -> {
                    SqUtil.throwUnexpectedColumnValueClassException(
                        columnIndex, value,
                        LocalDateTime::class.java, Timestamp::class.java, java.util.Date::class.java, Calendar::class.java,
                    )
                }
            }
        }
    }

    val TIMESTAMP__AS__SQL_TIMESTAMP: SqType<Timestamp> = SqlTimestampType()
    val TIMESTAMP__AS__CALENDAR: SqType<Calendar> = CalendarType()
    val TIMESTAMP__AS__UTIL_DATE: SqType<java.util.Date> = UtilDateType()
    val TIMESTAMP: SqType<LocalDateTime> = LocalDateTimeType()


    private class OffsetTimeType: SqTypeBase<OffsetTime>() {
        override val sqlType: SQLType
            get() = JDBCType.TIME_WITH_TIMEZONE

        override fun readNullableImpl(source: ResultSet, columnIndex: Int): OffsetTime? {
            try {
                return source.getObject(columnIndex, OffsetTime::class.java)
            } catch (e: Exception) { /* No action required */ }

            return when (val value = source.getObject(columnIndex)) {
                null -> null
                is OffsetTime -> value
                is Time -> OffsetTime.ofInstant(value.toInstant(), ZoneId.systemDefault())
                is LocalTime -> value.atOffset(OffsetTime.now().offset)
                else -> SqUtil.throwUnexpectedColumnValueClassException(columnIndex, value, OffsetTime::class.java, Time::class.java, LocalTime::class.java)
            }
        }
    }

    val TIME_WITH_TIME_ZONE: SqType<OffsetTime> = OffsetTimeType()


    private class OffsetDateTimeType: SqTypeBase<OffsetDateTime>() {
        override val sqlType: SQLType
            get() = JDBCType.TIMESTAMP_WITH_TIMEZONE

        override fun readNullableImpl(source: ResultSet, columnIndex: Int): OffsetDateTime? {
            try {
                return source.getObject(columnIndex, OffsetDateTime::class.java)
            } catch (e: Exception) { /* No action required */ }

            return when (val value = source.getObject(columnIndex)) {
                null -> null
                is OffsetDateTime -> value
                is Timestamp -> value.toLocalDateTime().atOffset(OffsetDateTime.now().offset)
                is LocalDateTime -> value.atOffset(OffsetDateTime.now().offset)
                is Calendar -> value.toInstant().atOffset(OffsetDateTime.now().offset)
                is java.util.Date -> value.toInstant().atOffset(OffsetDateTime.now().offset)
                else -> {
                    SqUtil.throwUnexpectedColumnValueClassException(
                        columnIndex, value,
                        OffsetDateTime::class.java, Timestamp::class.java, LocalDateTime::class.java, Calendar::class.java, java.util.Date::class.java,
                    )
                }
            }
        }
    }

    val TIMESTAMP_WITH_TIME_ZONE: SqType<OffsetDateTime> = OffsetDateTimeType()
    // endregion


    // region Other types
    private class OtherType<JAVA: Any>(override val sqlType: SQLType, val requiredClass: Class<JAVA>): SqTypeBase<JAVA>() {
        override fun readNullableImpl(source: ResultSet, columnIndex: Int): JAVA? {
            val value = source.getObject(columnIndex)
            @Suppress("UNCHECKED_CAST")
            return when {
                (value == null) -> null
                (this.requiredClass.isAssignableFrom(value.javaClass)) -> value as JAVA
                else -> SqUtil.throwUnexpectedColumnValueClassException(columnIndex, value, this.requiredClass)
            }
        }
    }

    private inline fun <reified T: Any> otherType(sqlType: SQLType): OtherType<T> = OtherType(sqlType, T::class.java)

    val CLOB: SqType<Clob> = otherType(JDBCType.CLOB)
    val BLOB: SqType<Blob> = otherType(JDBCType.BLOB)
    val REF: SqType<Ref> = otherType(JDBCType.REF)
    val DATA_LINK: SqType<URL> = otherType(JDBCType.DATALINK)
    val ROW_ID: SqType<RowId> = otherType(JDBCType.ROWID)
    val N_CLOB: SqType<NClob> = otherType(JDBCType.NCLOB)
    val SQL_XML: SqType<SQLXML> = otherType(JDBCType.SQLXML)
    // endregion
}
