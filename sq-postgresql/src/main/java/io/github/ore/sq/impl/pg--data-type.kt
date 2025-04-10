package io.github.ore.sq.impl

import io.github.ore.sq.SqDataTypePack
import io.github.ore.sq.SqDataTypeReadAction
import io.github.ore.sq.SqPgDataTypes
import io.github.ore.sq.util.SqPgBit
import io.github.ore.sq.util.SqUtil
import org.postgresql.util.PGobject
import java.math.BigDecimal
import java.sql.Date
import java.sql.ResultSet
import java.sql.SQLXML
import java.sql.Time
import java.sql.Timestamp
import java.sql.Types
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime


// region Type collection
open class SqPgDataTypesImpl: SqDataTypesImpl(), SqPgDataTypes {
    companion object {
        val INSTANCE: SqPgDataTypesImpl = SqPgDataTypesImpl()


        fun localDateTypePack(jdbcType: Int, dbTypeName: String?): SqDataTypePack<LocalDate, Timestamp> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = LocalDateReadAction,
                write = { target, index, value ->
                    target.setObject(index, value, jdbcType)
                }
            )
        }

        fun localDateListTypePack(jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<LocalDate?>, List<Timestamp>> {
            return sqDataTypeArrayPack(
                nullFlag = null,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = LocalDate::class.java,
                read = LocalDateReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun localDateTimeTypePack(jdbcType: Int, dbTypeName: String?): SqDataTypePack<LocalDateTime, Timestamp> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = LocalDateTimeReadAction,
                write = { target, index, value ->
                    target.setObject(index, value, jdbcType)
                }
            )
        }

        fun localDateTimeListTypePack(jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<LocalDateTime?>, List<Timestamp>> {
            return sqDataTypeArrayPack(
                nullFlag = null,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = LocalDateTime::class.java,
                read = LocalDateTimeReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun localTimeTypePack(jdbcType: Int, dbTypeName: String?): SqDataTypePack<LocalTime, Time> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = LocalTimeReadAction,
                write = { target, index, value ->
                    target.setObject(index, value, jdbcType)
                }
            )
        }

        fun localTimeListTypePack(jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<LocalTime?>, List<Time>> {
            return sqDataTypeArrayPack(
                nullFlag = null,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = LocalTime::class.java,
                read = LocalTimeReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun offsetDateTimeTypePack(jdbcType: Int, dbTypeName: String?): SqDataTypePack<OffsetDateTime, Timestamp> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = OffsetDateTimeReadAction,
                write = { target, index, value ->
                    target.setObject(index, value, jdbcType)
                }
            )
        }

        fun offsetDateTimeListTypePack(jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<OffsetDateTime?>, List<Timestamp>> {
            return sqDataTypeArrayPack(
                nullFlag = null,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = OffsetDateTime::class.java,
                read = OffsetDateTimeReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun offsetTimeTypePack(jdbcType: Int, dbTypeName: String?): SqDataTypePack<OffsetTime, Time> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = OffsetTimeReadAction,
                write = { target, index, value ->
                    target.setObject(index, value, jdbcType)
                }
            )
        }

        fun offsetTimeListTypePack(jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<OffsetTime?>, List<Time>> {
            return sqDataTypeArrayPack(
                nullFlag = null,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = OffsetTime::class.java,
                read = OffsetTimeReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun pgBitTypePack(jdbcType: Int, dbTypeName: String?): SqDataTypePack<SqPgBit, SqPgBit> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = PgBitReadAction,
                write = { target, index, value ->
                    target.setObject(index, value.toPgObject(dbTypeName ?: SqPgDataTypes.DB_TYPE_NAME__BIT))
                }
            )
        }

        fun pgBitListTypePack(jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<SqPgBit?>, List<SqPgBit>> {
            return sqDataTypeArrayPack(
                nullFlag = (null as Any?),
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = PGobject::class.java,
                read = PgBitReadAction,
                write = { _, value ->
                    value?.toPgObject(elementDbTypeName)
                }
            )
        }

        fun pgObjectStringTypePack(jdbcType: Int, dbTypeName: String?): SqDataTypePack<String, String> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = PgObjectStringReadAction,
                write = { target, index, value ->
                    val objectValue = PGobject()
                    objectValue.value = value

                    if (dbTypeName != null) {
                        objectValue.type = dbTypeName
                    }

                    target.setObject(index, objectValue, jdbcType)
                }
            )
        }

        fun pgObjectStringListTypePack(jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<String?>, List<String>> {
            return sqDataTypeArrayPack(
                nullFlag = null,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = PGobject::class.java,
                read = PgObjectStringReadAction,
                write = { target, value ->
                    value?.let {
                        val result = PGobject()
                        result.value = value

                        if (dbTypeName != null) {
                            result.type = dbTypeName
                        }

                        result
                    }
                }
            )
        }

        fun sqlXmlListTypePack(jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<SQLXML?>, List<String>> {
            return sqDataTypeArrayPack(
                nullFlag = null,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = String::class.java,
                read = SqlXmlReadAction,
                write = { target, value -> value?.string },
            )
        }
    }


    // region Boolean
    object PgBitReadAction: SqDataTypeReadAction<SqPgBit> {
        override fun get(source: ResultSet, index: Int): SqPgBit? {
            return source.getObject(index, PGobject::class.java)?.let {
                SqPgBit(it)
            }
        }
    }

    override val pgBit: SqDataTypePack<SqPgBit, SqPgBit> =
        pgBitTypePack(Types.BIT, SqPgDataTypes.DB_TYPE_NAME__BIT)
    override val pgBitArray: SqDataTypePack<List<SqPgBit?>, List<SqPgBit>> =
        pgBitListTypePack(Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__BIT_ARRAY, SqPgDataTypes.DB_TYPE_NAME__BIT)
    override val pgBitVarying: SqDataTypePack<SqPgBit, SqPgBit> =
        pgBitTypePack(Types.OTHER, SqPgDataTypes.DB_TYPE_NAME__BIT_VARYING)
    override val pgBitVaryingArray: SqDataTypePack<List<SqPgBit?>, List<SqPgBit>> =
        pgBitListTypePack(Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__BIT_VARYING_ARRAY, SqPgDataTypes.DB_TYPE_NAME__BIT_VARYING)
    override val pgBoolean: SqDataTypePack<Boolean, Boolean> =
        booleanTypePack(Types.BIT, SqPgDataTypes.DB_TYPE_NAME__BOOLEAN)
    override val pgBooleanArray: SqDataTypePack<List<Boolean?>, List<Boolean>> =
        booleanListTypePack(null, Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__BOOLEAN_ARRAY, SqPgDataTypes.DB_TYPE_NAME__BOOLEAN)

    override val boolean: SqDataTypePack<Boolean, Boolean>
        get() = this.pgBoolean
    // endregion


    // region Byte array
    override val pgByteA: SqDataTypePack<ByteArray, ByteArray> =
        byteArrayTypePack(Types.BINARY, SqPgDataTypes.DB_TYPE_NAME__BYTE_A)
    override val pgByteAArray: SqDataTypePack<List<ByteArray?>, List<ByteArray>> =
        byteArrayListTypePack(null, Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__BYTE_A_ARRAY, SqPgDataTypes.DB_TYPE_NAME__BYTE_A)

    override val binary: SqDataTypePack<ByteArray, ByteArray>
        get() = this.pgByteA
    override val longVarBinary: SqDataTypePack<ByteArray, ByteArray>
        get() = this.pgByteA
    override val varBinary: SqDataTypePack<ByteArray, ByteArray>
        get() = this.pgByteA
    // endregion


    // region Number
    override val pgBigInt: SqDataTypePack<Long, Number> =
        longTypePack(Types.BIGINT, SqPgDataTypes.DB_TYPE_NAME__BIG_INT)
    override val pgBigIntArray: SqDataTypePack<List<Long?>, List<Number>> =
        longListTypePack(null, Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__BIG_INT_ARRAY, SqPgDataTypes.DB_TYPE_NAME__BIG_INT)
    override val pgDoublePrecision: SqDataTypePack<Double, Number> =
        doubleTypePack(Types.DOUBLE, SqPgDataTypes.DB_TYPE_NAME__DOUBLE_PRECISION)
    override val pgDoublePrecisionArray: SqDataTypePack<List<Double?>, List<Number>> =
        doubleListTypePack(null, Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__DOUBLE_PRECISION_ARRAY, SqPgDataTypes.DB_TYPE_NAME__DOUBLE_PRECISION)
    override val pgInteger: SqDataTypePack<Int, Number> =
        intTypePack(Types.INTEGER, SqPgDataTypes.DB_TYPE_NAME__INTEGER)
    override val pgIntegerArray: SqDataTypePack<List<Int?>, List<Number>> =
        intListTypePack(null, Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__INTEGER_ARRAY, SqPgDataTypes.DB_TYPE_NAME__INTEGER)
    override val pgNumeric: SqDataTypePack<BigDecimal, Number> =
        bigDecimalTypePack(Types.NUMERIC, SqPgDataTypes.DB_TYPE_NAME__NUMERIC)
    override val pgNumericArray: SqDataTypePack<List<BigDecimal?>, List<Number>> =
        bigDecimalListTypePack(null, Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__NUMERIC_ARRAY, SqPgDataTypes.DB_TYPE_NAME__NUMERIC)
    override val pgReal: SqDataTypePack<Float, Number> =
        floatTypePack(Types.REAL, SqPgDataTypes.DB_TYPE_NAME__REAL)
    override val pgRealArray: SqDataTypePack<List<Float?>, List<Number>> =
        floatListTypePack(null, Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__REAL_ARRAY, SqPgDataTypes.DB_TYPE_NAME__REAL)
    override val pgSmallInt: SqDataTypePack<Short, Number> =
        shortTypePack(Types.SMALLINT, SqPgDataTypes.DB_TYPE_NAME__SMALL_INT)
    override val pgSmallIntArray: SqDataTypePack<List<Short?>, List<Number>> =
        shortListTypePack(null, Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__SMALL_INT_ARRAY, SqPgDataTypes.DB_TYPE_NAME__SMALL_INT)

    override val bigInt: SqDataTypePack<Long, Number>
        get() = this.pgBigInt
    override val double: SqDataTypePack<Double, Number>
        get() = this.pgDoublePrecision
    override val float: SqDataTypePack<Double, Number>
        get() = this.pgDoublePrecision
    override val integer: SqDataTypePack<Int, Number>
        get() = this.pgInteger
    override val decimal: SqDataTypePack<BigDecimal, Number>
        get() = this.pgNumeric
    override val numeric: SqDataTypePack<BigDecimal, Number>
        get() = this.pgNumeric
    override val real: SqDataTypePack<Float, Number>
        get() = this.pgReal
    override val smallInt: SqDataTypePack<Short, Number>
        get() = this.pgSmallInt
    // endregion


    // region String
    override val pgChar: SqDataTypePack<Char, String> =
        charTypePack(Types.CHAR, SqPgDataTypes.DB_TYPE_NAME__CHAR)
    override val pgCharArray: SqDataTypePack<List<Char?>, List<String>> =
        charListTypePack(null, Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__CHAR_ARRAY, SqPgDataTypes.DB_TYPE_NAME__CHAR)
    override val pgCharacter: SqDataTypePack<String, String> =
        stringTypePack(Types.CHAR, SqPgDataTypes.DB_TYPE_NAME__CHARACTER)
    override val pgCharacterArray: SqDataTypePack<List<String?>, List<String>> =
        stringListTypePack(null, Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__CHARACTER_ARRAY, SqPgDataTypes.DB_TYPE_NAME__CHARACTER)
    override val pgCharacterVarying: SqDataTypePack<String, String> =
        stringTypePack(Types.VARCHAR, SqPgDataTypes.DB_TYPE_NAME__CHARACTER_VARYING)
    override val pgCharacterVaryingArray: SqDataTypePack<List<String?>, List<String>> =
        stringListTypePack(null, Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__CHARACTER_VARYING_ARRAY, SqPgDataTypes.DB_TYPE_NAME__CHARACTER_VARYING)
    override val pgText: SqDataTypePack<String, String> =
        stringTypePack(Types.VARCHAR, SqPgDataTypes.DB_TYPE_NAME__TEXT)
    override val pgTextArray: SqDataTypePack<List<String?>, List<String>> =
        stringListTypePack(null, Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__TEXT_ARRAY, SqPgDataTypes.DB_TYPE_NAME__TEXT)

    override val char: SqDataTypePack<String, String>
        get() = this.pgCharacter
    override val nChar: SqDataTypePack<String, String>
        get() = this.pgCharacter
    override val longNVarChar: SqDataTypePack<String, String>
        get() = this.pgCharacterVarying
    override val longVarChar: SqDataTypePack<String, String>
        get() = this.pgCharacterVarying
    override val nVarChar: SqDataTypePack<String, String>
        get() = this.pgCharacterVarying
    override val varChar: SqDataTypePack<String, String>
        get() = this.pgCharacterVarying
    // endregion


    // region Temporal
    object LocalDateReadAction: SqDataTypeReadAction<LocalDate> {
        override fun get(source: ResultSet, index: Int): LocalDate? =
            source.getObject(index, LocalDate::class.java)
    }

    object LocalDateTimeReadAction: SqDataTypeReadAction<LocalDateTime> {
        override fun get(source: ResultSet, index: Int): LocalDateTime? =
            source.getObject(index, LocalDateTime::class.java)
    }

    object LocalTimeReadAction: SqDataTypeReadAction<LocalTime> {
        override fun get(source: ResultSet, index: Int): LocalTime? =
            source.getObject(index, LocalTime::class.java)
    }

    object OffsetDateTimeReadAction: SqDataTypeReadAction<OffsetDateTime> {
        override fun get(source: ResultSet, index: Int): OffsetDateTime? =
            source.getObject(index, OffsetDateTime::class.java)?.withOffsetSameInstant(SqUtil.defaultTimeOffset())
    }

    object OffsetTimeReadAction: SqDataTypeReadAction<OffsetTime> {
        override fun get(source: ResultSet, index: Int): OffsetTime? =
            source.getObject(index, OffsetTime::class.java)
    }


    override val pgDate: SqDataTypePack<LocalDate, Timestamp> =
        localDateTypePack(Types.DATE, SqPgDataTypes.DB_TYPE_NAME__DATE)
    override val pgDateJdbc: SqDataTypePack<Date, Timestamp> =
        dateJdbcTypePack(Types.DATE, SqPgDataTypes.DB_TYPE_NAME__DATE)
    override val pgDateArray: SqDataTypePack<List<LocalDate?>, List<Timestamp>> =
        localDateListTypePack(Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__DATE_ARRAY, SqPgDataTypes.DB_TYPE_NAME__DATE)
    override val pgDateArrayJdbc: SqDataTypePack<List<Date?>, List<Timestamp>> =
        dateListJdbcTypePack(null, Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__DATE_ARRAY, SqPgDataTypes.DB_TYPE_NAME__DATE)
    override val pgTimestamp: SqDataTypePack<LocalDateTime, Timestamp> =
        localDateTimeTypePack(Types.TIMESTAMP, SqPgDataTypes.DB_TYPE_NAME__TIMESTAMP)
    override val pgTimestampJdbc: SqDataTypePack<Timestamp, Timestamp> =
        timestampJdbcTypePack(Types.TIMESTAMP, SqPgDataTypes.DB_TYPE_NAME__TIMESTAMP)
    override val pgTimestampArray: SqDataTypePack<List<LocalDateTime?>, List<Timestamp>> =
        localDateTimeListTypePack(Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__TIMESTAMP_ARRAY, SqPgDataTypes.DB_TYPE_NAME__TIMESTAMP)
    override val pgTimestampArrayJdbc: SqDataTypePack<List<Timestamp?>, List<Timestamp>> =
        timestampJdbcListTypePack(null, Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__TIMESTAMP_ARRAY, SqPgDataTypes.DB_TYPE_NAME__TIMESTAMP)
    override val pgTimestampTz: SqDataTypePack<OffsetDateTime, Timestamp> =
        offsetDateTimeTypePack(Types.TIMESTAMP_WITH_TIMEZONE, SqPgDataTypes.DB_TYPE_NAME__TIMESTAMP_TZ)
    override val pgTimestampTzJdbc: SqDataTypePack<Timestamp, Timestamp> =
        timestampJdbcTypePack(Types.TIMESTAMP_WITH_TIMEZONE, SqPgDataTypes.DB_TYPE_NAME__TIMESTAMP_TZ)
    override val pgTimestampTzArray: SqDataTypePack<List<OffsetDateTime?>, List<Timestamp>> =
        offsetDateTimeListTypePack(Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__TIMESTAMP_TZ_ARRAY, SqPgDataTypes.DB_TYPE_NAME__TIMESTAMP_TZ)
    override val pgTimestampTzArrayJdbc: SqDataTypePack<List<Timestamp?>, List<Timestamp>> =
        timestampJdbcListTypePack(null, Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__TIMESTAMP_TZ_ARRAY, SqPgDataTypes.DB_TYPE_NAME__TIMESTAMP_TZ)
    override val pgTime: SqDataTypePack<LocalTime, Time> =
        localTimeTypePack(Types.TIME, SqPgDataTypes.DB_TYPE_NAME__TIME)
    override val pgTimeJdbc: SqDataTypePack<Time, Time> =
        timeJdbcTypePack(Types.TIME, SqPgDataTypes.DB_TYPE_NAME__TIME)
    override val pgTimeArray: SqDataTypePack<List<LocalTime?>, List<Time>> =
        localTimeListTypePack(Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__TIME_ARRAY, SqPgDataTypes.DB_TYPE_NAME__TIME)
    override val pgTimeArrayJdbc: SqDataTypePack<List<Time?>, List<Time>> =
        timeJdbcListTypePack(null, Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__TIME_ARRAY, SqPgDataTypes.DB_TYPE_NAME__TIME)
    override val pgTimeTz: SqDataTypePack<OffsetTime, Time> =
        offsetTimeTypePack(Types.TIME, SqPgDataTypes.DB_TYPE_NAME__TIME_TZ)
    override val pgTimeTzJdbc: SqDataTypePack<Time, Time> =
        timeJdbcTypePack(Types.TIME, SqPgDataTypes.DB_TYPE_NAME__TIME_TZ)
    override val pgTimeTzArray: SqDataTypePack<List<OffsetTime?>, List<Time>> =
        offsetTimeListTypePack(Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__TIME_TZ_ARRAY, SqPgDataTypes.DB_TYPE_NAME__TIME_TZ)
    override val pgTimeTzArrayJdbc: SqDataTypePack<List<Time?>, List<Time>> =
        timeJdbcListTypePack(null, Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__TIME_TZ_ARRAY, SqPgDataTypes.DB_TYPE_NAME__TIME_TZ)

    override val date: SqDataTypePack<LocalDate, Timestamp>
        get() = this.pgDate
    override val dateJdbc: SqDataTypePack<Date, Timestamp>
        get() = this.pgDateJdbc
    override val timestamp: SqDataTypePack<LocalDateTime, Timestamp>
        get() = this.pgTimestamp
    override val timestampJdbc: SqDataTypePack<Timestamp, Timestamp>
        get() = this.pgTimestampJdbc
    override val timestampTz: SqDataTypePack<OffsetDateTime, Timestamp>
        get() = this.pgTimestampTz
    override val timestampTzJdbc: SqDataTypePack<Timestamp, Timestamp>
        get() = this.pgTimestampTzJdbc
    override val time: SqDataTypePack<LocalTime, Time>
        get() = this.pgTime
    override val timeJdbc: SqDataTypePack<Time, Time>
        get() = this.pgTimeJdbc
    override val timeTz: SqDataTypePack<OffsetTime, Time>
        get() = this.pgTimeTz
    override val timeTzJdbc: SqDataTypePack<Time, Time>
        get() = this.pgTimeTzJdbc
    // endregion


    // region Other
    object PgObjectStringReadAction: SqDataTypeReadAction<String> {
        override fun get(source: ResultSet, index: Int): String? =
            source.getObject(index, PGobject::class.java)?.value
    }


    override val pgJson: SqDataTypePack<String, String> =
        pgObjectStringTypePack(Types.OTHER, SqPgDataTypes.DB_TYPE_NAME__JSON)
    override val pgJsonArray: SqDataTypePack<List<String?>, List<String>> =
        pgObjectStringListTypePack(Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__JSON_ARRAY, SqPgDataTypes.DB_TYPE_NAME__JSON)
    override val pgJsonB: SqDataTypePack<String, String> =
        pgObjectStringTypePack(Types.OTHER, SqPgDataTypes.DB_TYPE_NAME__JSON_B)
    override val pgJsonBArray: SqDataTypePack<List<String?>, List<String>> =
        pgObjectStringListTypePack(Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__JSON_B_ARRAY, SqPgDataTypes.DB_TYPE_NAME__JSON_B)
    override val pgXml: SqDataTypePack<SQLXML, String> =
        sqlXmlTypePack(Types.SQLXML, SqPgDataTypes.DB_TYPE_NAME__XML)
    override val pgXmlArray: SqDataTypePack<List<SQLXML?>, List<String>> =
        sqlXmlListTypePack(Types.ARRAY, SqPgDataTypes.DB_TYPE_NAME__XML_ARRAY, SqPgDataTypes.DB_TYPE_NAME__XML)

    override val sqlXml: SqDataTypePack<SQLXML, String>
        get() = this.pgXml
    // endregion
}
// endregion
