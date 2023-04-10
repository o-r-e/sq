package me.ore.sq.generic

import me.ore.sq.SqType
import me.ore.sq.SqTypeHolder
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import java.sql.*
import java.time.*


@Suppress("MemberVisibilityCanBePrivate")
object SqGenericTypeHolder: SqTypeHolder {
    // region Boolean types
    private val booleanReader = SqGenericBooleanReader()

    override val bit: SqType<Boolean, Boolean> = run {
        SqType.notNull(
            Boolean::class.java,
            Boolean::class.java,
            this.booleanReader,
            SqGenericBooleanWriter(Types.BIT),
        )
    }
    override val boolean: SqType<Boolean, Boolean> = run {
        SqType.notNull(
            Boolean::class.java,
            Boolean::class.java,
            this.booleanReader,
            SqGenericBooleanWriter(Types.BOOLEAN),
        )
    }
    // endregion


    // region Byte array types
    private val byteArrayReader = SqGenericByteArrayReader()

    override val binary: SqType<ByteArray, ByteArray> = run {
        SqType.notNull(
            ByteArray::class.java,
            ByteArray::class.java,
            this.byteArrayReader,
            SqGenericByteArrayWriter(Types.BINARY),
        )
    }
    override val longVarBinary: SqType<ByteArray, ByteArray> = run {
        SqType.notNull(
            ByteArray::class.java,
            ByteArray::class.java,
            this.byteArrayReader,
            SqGenericByteArrayWriter(Types.LONGVARBINARY),
        )
    }
    override val varBinary: SqType<ByteArray, ByteArray> = run {
        SqType.notNull(
            ByteArray::class.java,
            ByteArray::class.java,
            this.byteArrayReader,
            SqGenericByteArrayWriter(Types.VARBINARY),
        )
    }
    // endregion


    // region Date/time types
    override val date: SqType<LocalDate, Timestamp> = run {
        SqType.notNull(
            LocalDate::class.java,
            Timestamp::class.java,
            SqGenericLocalDateReader(),
            SqGenericLocalDateWriter(Types.DATE),
        )
    }
    override val dateAsDate: SqType<Date, Timestamp> = run {
        SqType.notNull(
            Date::class.java,
            Timestamp::class.java,
            SqGenericDateReader(),
            SqGenericDateWriter(Types.DATE),
        )
    }
    override val time: SqType<LocalTime, Time> = run {
        SqType.notNull(
            LocalTime::class.java,
            Time::class.java,
            SqGenericLocalTimeReader(),
            SqGenericLocalTimeWriter(Types.TIME),
        )
    }
    override val timeAsTime: SqType<Time, Time> = run {
        SqType.notNull(
            Time::class.java,
            Time::class.java,
            SqGenericTimeReader(),
            SqGenericTimeWriter(Types.TIME),
        )
    }
    override val timeWithTimeZone: SqType<OffsetTime, Time> = run {
        SqType.notNull(
            OffsetTime::class.java,
            Time::class.java,
            SqGenericOffsetTimeReader(),
            SqGenericOffsetTimeWriter(Types.TIME_WITH_TIMEZONE),
        )
    }
    override val timestamp: SqType<LocalDateTime, Timestamp> = run {
        SqType.notNull(
            LocalDateTime::class.java,
            Timestamp::class.java,
            SqGenericLocalDateTimeReader(),
            SqGenericLocalDateTimeWriter(Types.TIMESTAMP),
        )
    }
    override val timestampAsTimestamp: SqType<Timestamp, Timestamp> = run {
        SqType.notNull(
            Timestamp::class.java,
            Timestamp::class.java,
            SqGenericTimestampReader(),
            SqGenericTimestampWriter(Types.TIMESTAMP),
        )
    }
    override val timestampWithTimeZone: SqType<OffsetDateTime, Timestamp> = run {
        SqType.notNull(
            OffsetDateTime::class.java,
            Timestamp::class.java,
            SqGenericOffsetDateTimeReader(),
            SqGenericOffsetDateTimeWriter(Types.TIMESTAMP_WITH_TIMEZONE),
        )
    }
    // endregion


    // region Number types
    private val bigDecimalReader = SqGenericBigDecimalReader()
    private val javaDoubleReader = SqGenericDoubleReader()


    override val bigInt: SqType<Long, Number> = run {
        SqType.notNull(
            Long::class.java,
            Number::class.java,
            SqGenericLongReader(),
            SqGenericLongWriter(Types.BIGINT),
        )
    }
    override val bigIntAsBigInteger: SqType<BigInteger, Number> = run {
        SqType.notNull(
            BigInteger::class.java,
            Number::class.java,
            SqGenericBigIntegerReader(),
            SqGenericBigIntegerWriter(Types.BIGINT),
        )
    }
    override val decimal: SqType<BigDecimal, Number> = run {
        SqType.notNull(
            BigDecimal::class.java,
            Number::class.java,
            this.bigDecimalReader,
            SqGenericBigDecimalWriter(Types.DECIMAL),
        )
    }
    override val double: SqType<Double, Number> = run {
        SqType.notNull(
            Double::class.java,
            Number::class.java,
            this.javaDoubleReader,
            SqGenericDoubleWriter(Types.DOUBLE),
        )
    }
    override val float: SqType<Double, Number> = run {
        SqType.notNull(
            Double::class.java,
            Number::class.java,
            this.javaDoubleReader,
            SqGenericDoubleWriter(Types.FLOAT),
        )
    }
    override val integer: SqType<Int, Number> = run {
        SqType.notNull(
            Int::class.java,
            Number::class.java,
            SqGenericIntReader(),
            SqGenericIntWriter(Types.INTEGER),
        )
    }
    override val numeric: SqType<BigDecimal, Number> = run {
        SqType.notNull(
            BigDecimal::class.java,
            Number::class.java,
            this.bigDecimalReader,
            SqGenericBigDecimalWriter(Types.NUMERIC),
        )
    }
    override val real: SqType<Float, Number> = run {
        SqType.notNull(
            Float::class.java,
            Number::class.java,
            SqGenericFloatReader(),
            SqGenericFloatWriter(Types.REAL),
        )
    }
    override val smallInt: SqType<Short, Number> = run {
        SqType.notNull(
            Short::class.java,
            Number::class.java,
            SqGenericShortReader(),
            SqGenericShortWriter(Types.SMALLINT),
        )
    }
    override val tinyInt: SqType<Byte, Number> = run {
        SqType.notNull(
            Byte::class.java,
            Number::class.java,
            SqGenericByteReader(),
            SqGenericByteWriter(Types.TINYINT),
        )
    }

    override val javaNumber: SqType<Number, Number> = run {
        SqType.notNull(
            Number::class.java,
            Number::class.java,
            SqGenericNumberReader(),
            SqGenericNumberWriter(Types.DOUBLE),
        )
    }
    // endregion


    // region Text types
    private val stringValueReader = SqGenericStringValueReader()
    private val nStringValueReader = SqGenericNStringValueReader()


    override val char: SqType<String, String> = run {
        SqType.notNull(
            String::class.java,
            String::class.java,
            this.stringValueReader,
            SqGenericStringValueWriter(Types.CHAR),
        )
    }

    override val longVarChar: SqType<String, String> = run {
        SqType.notNull(
            String::class.java,
            String::class.java,
            this.stringValueReader,
            SqGenericStringValueWriter(Types.LONGVARCHAR)
        )
    }

    override val varChar: SqType<String, String> = run {
        SqType.notNull(
            String::class.java,
            String::class.java,
            this.stringValueReader,
            SqGenericStringValueWriter(Types.VARCHAR),
        )
    }

    override val nChar: SqType<String, String> = run {
        SqType.notNull(
            String::class.java,
            String::class.java,
            this.nStringValueReader,
            SqGenericNStringValueWriter(Types.NCHAR),
        )
    }

    override val nVarChar: SqType<String, String> = run {
        SqType.notNull(
            String::class.java,
            String::class.java,
            this.nStringValueReader,
            SqGenericNStringValueWriter(Types.NVARCHAR),
        )
    }

    override val longNVarChar: SqType<String, String> = run {
        SqType.notNull(
            String::class.java,
            String::class.java,
            this.nStringValueReader,
            SqGenericNStringValueWriter(Types.LONGNVARCHAR),
        )
    }
    // endregion


    // region Blob/Clob types
    override val blob: SqType<Blob, Blob> = run {
        SqType.notNull(
            Blob::class.java,
            Blob::class.java,
            SqGenericBlobReader(),
            SqGenericBlobWriter(Types.BLOB),
        )
    }
    override val clob: SqType<Clob, Clob> = run {
        SqType.notNull(
            Clob::class.java,
            Clob::class.java,
            SqGenericClobReader(),
            SqGenericClobWriter(Types.CLOB),
        )
    }
    override val nClob: SqType<NClob, Clob> = run {
        SqType.notNull(
            NClob::class.java,
            Clob::class.java,
            SqGenericNClobReader(),
            SqGenericNClobWriter(Types.NCLOB),
        )
    }
    // endregion


    // region Other JDBC types
    override val dataLink: SqType<URL, String> = run {
        SqType.notNull(
            URL::class.java,
            String::class.java,
            SqGenericUrlReader(),
            SqGenericUrlWriter(Types.DATALINK),
        )
    }
    override val ref: SqType<Ref, Ref> = run {
        SqType.notNull(
            Ref::class.java,
            Ref::class.java,
            SqGenericRefReader(),
            SqGenericRefWriter(Types.REF),
        )
    }
    override val rowId: SqType<RowId, RowId> = run {
        SqType.notNull(
            RowId::class.java,
            RowId::class.java,
            SqGenericRowIdReader(),
            SqGenericRowIdWriter(Types.ROWID),
        )
    }
    override val sqlXml: SqType<SQLXML, String> = run {
        SqType.notNull(
            SQLXML::class.java,
            String::class.java,
            SqGenericSqlXmlReader(),
            SqGenericSqlXmlWriter(Types.SQLXML),
        )
    }
    // endregion
}
