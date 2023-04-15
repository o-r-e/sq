package me.ore.sq.pg

import me.ore.sq.SqDbTypeBit
import me.ore.sq.SqType
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.Date
import java.sql.SQLXML
import java.sql.Time
import java.sql.Timestamp
import java.time.*


object SqPgTypeHolderImpl: SqPgTypeHolder {
    // region Boolean types
    override val pgSingleBit: SqType<Boolean, SqDbTypeBit> = run {
        SqType.notNull(
            Boolean::class.java,
            SqDbTypeBit::class.java,
            SqPgSingleBitReader(),
            SqPgSingleBitWriter(),
        )
    }

    override val pgBoolean: SqType<Boolean, Boolean> = run {
        SqType.notNull(
            Boolean::class.java,
            Boolean::class.java,
            SqPgBooleanReader(),
            SqPgBooleanWriter(),
        )
    }
    // endregion


    // region Byte array types
    override val pgBytea: SqType<ByteArray, ByteArray> = run {
        SqType.notNull(
            ByteArray::class.java,
            ByteArray::class.java,
            SqPgByteaReader(),
            SqPgByteaWriter(),
        )
    }
    // endregion


    // region Date/time types
    override val pgDate: SqType<LocalDate, Timestamp> = run {
        SqType.notNull(
            LocalDate::class.java,
            Timestamp::class.java,
            SqPgDateReader(),
            SqPgDateWriter(),
        )
    }

    override val pgDateAsDate: SqType<Date, Timestamp> = run {
        SqType.notNull(
            Date::class.java,
            Timestamp::class.java,
            SqPgSqlDateReader(),
            SqPgSqlDateWriter(),
        )
    }

    override val pgTime: SqType<LocalTime, Time> = run {
        SqType.notNull(
            LocalTime::class.java,
            Time::class.java,
            SqPgTimeReader(),
            SqPgTimeWriter(),
        )
    }

    override val pgTimeAsTime: SqType<Time, Time> = run {
        SqType.notNull(
            Time::class.java,
            Time::class.java,
            SqPgSqlTimeReader(),
            SqPgSqlTimeWriter(),
        )
    }

    override val pgTimeWithTimeZone: SqType<OffsetTime, Time> = run {
        SqType.notNull(
            OffsetTime::class.java,
            Time::class.java,
            SqPgTimeTZReader(),
            SqPgTimeTZWriter(),
        )
    }

    override val pgTimestamp: SqType<LocalDateTime, Timestamp> = run {
        SqType.notNull(
            LocalDateTime::class.java,
            Timestamp::class.java,
            SqPgTimestampReader(),
            SqPgTimestampWriter(),
        )
    }

    override val pgTimestampAsTimestamp: SqType<Timestamp, Timestamp> = run {
        SqType.notNull(
            Timestamp::class.java,
            Timestamp::class.java,
            SqPgSqlTimestampReader(),
            SqPgSqlTimestampWriter(),
        )
    }

    override val pgTimestampWithTimeZone: SqType<OffsetDateTime, Timestamp> = run {
        SqType.notNull(
            OffsetDateTime::class.java,
            Timestamp::class.java,
            SqPgTimestampTZReader(),
            SqPgTimestampTZWriter(),
        )
    }
    // endregion


    // region Number types
    override val pgBitInt: SqType<Long, Number> = run {
        SqType.notNull(
            Long::class.java,
            Number::class.java,
            SqPgBigIntReader(),
            SqPgBigIntWriter(),
        )
    }

    override val pgBitIntAsBigInteger: SqType<BigInteger, Number> = run {
        SqType.notNull(
            BigInteger::class.java,
            Number::class.java,
            SqPgMathBigIntReader(),
            SqPgMathBigIntWriter(),
        )
    }

    override val pgDoublePrecision: SqType<Double, Number> = run {
        SqType.notNull(
            Double::class.java,
            Number::class.java,
            SqPgDoublePrecisionReader(),
            SqPgDoublePrecisionWriter(),
        )
    }

    override val pgInteger: SqType<Int, Number> = run {
        SqType.notNull(
            Int::class.java,
            Number::class.java,
            SqPgIntegerReader(),
            SqPgIntegerWriter(),
        )
    }

    override val pgNumeric: SqType<BigDecimal, Number> = run {
        SqType.notNull(
            BigDecimal::class.java,
            Number::class.java,
            SqPgNumericReader(),
            SqPgNumericWriter(),
        )
    }

    override val pgReal: SqType<Float, Number> = run {
        SqType.notNull(
            Float::class.java,
            Number::class.java,
            SqPgRealReader(),
            SqPgRealWriter(),
        )
    }

    override val pgSmallInt: SqType<Short, Number> = run {
        SqType.notNull(
            Short::class.java,
            Number::class.java,
            SqPgSmallIntReader(),
            SqPgSmallIntWriter(),
        )
    }
    // endregion


    // region Text types
    val pgJavaStringReader: SqPgJavaStringReader = SqPgJavaStringReader()


    override val pgChar: SqType<String, String> = run {
        SqType.notNull(
            String::class.java,
            String::class.java,
            this.pgJavaStringReader,
            SqPgCharWriter(),
        )
    }

    override val pgCharacterVarying: SqType<String, String> = run {
        SqType.notNull(
            String::class.java,
            String::class.java,
            this.pgJavaStringReader,
            SqPgCharacterVaryingWriter(),
        )
    }
    // endregion


    // region Other JDBC types
    override val pgXml: SqType<SQLXML, String> = run {
        SqType.notNull(
            SQLXML::class.java,
            String::class.java,
            SqPgXmlReader(),
            SqPgXmlWriter(),
        )
    }
    // endregion
}
