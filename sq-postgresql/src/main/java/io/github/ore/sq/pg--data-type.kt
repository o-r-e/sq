@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqPgDataTypesImpl
import io.github.ore.sq.util.SqPgBit
import java.math.BigDecimal
import java.sql.Date
import java.sql.SQLXML
import java.sql.Time
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime


// region Type collection
interface SqPgDataTypes: SqDataTypes {
    companion object {
        const val DB_TYPE_NAME__BIG_INT: String = "int8"
        const val DB_TYPE_NAME__BIG_INT_ARRAY: String = "_int8"
        const val DB_TYPE_NAME__BIT: String = "bit"
        const val DB_TYPE_NAME__BIT_ARRAY: String = "_bit"
        @Suppress("SpellCheckingInspection")
        const val DB_TYPE_NAME__BIT_VARYING: String = "varbit"
        @Suppress("SpellCheckingInspection")
        const val DB_TYPE_NAME__BIT_VARYING_ARRAY: String = "_varbit"
        const val DB_TYPE_NAME__BOOLEAN: String = "bool"
        const val DB_TYPE_NAME__BOOLEAN_ARRAY: String = "_bool"
        const val DB_TYPE_NAME__BYTE_A: String = "bytea"
        const val DB_TYPE_NAME__BYTE_A_ARRAY: String = "_bytea"
        const val DB_TYPE_NAME__CHAR: String = "char"
        const val DB_TYPE_NAME__CHAR_ARRAY: String = "_char"
        const val DB_TYPE_NAME__CHARACTER: String = "bpchar"
        const val DB_TYPE_NAME__CHARACTER_ARRAY: String = "_bpchar"
        const val DB_TYPE_NAME__CHARACTER_VARYING: String = "varchar"
        const val DB_TYPE_NAME__CHARACTER_VARYING_ARRAY: String = "_varchar"
        const val DB_TYPE_NAME__DATE: String = "date"
        const val DB_TYPE_NAME__DATE_ARRAY: String = "_date"
        const val DB_TYPE_NAME__DOUBLE_PRECISION: String = "float8"
        const val DB_TYPE_NAME__DOUBLE_PRECISION_ARRAY: String = "_float8"
        const val DB_TYPE_NAME__INTEGER: String = "int4"
        const val DB_TYPE_NAME__INTEGER_ARRAY: String = "_int4"
        const val DB_TYPE_NAME__JSON: String = "json"
        const val DB_TYPE_NAME__JSON_ARRAY: String = "_json"
        const val DB_TYPE_NAME__JSON_B: String = "jsonb"
        const val DB_TYPE_NAME__JSON_B_ARRAY: String = "_jsonb"
        const val DB_TYPE_NAME__NUMERIC: String = "numeric"
        const val DB_TYPE_NAME__NUMERIC_ARRAY: String = "_numeric"
        const val DB_TYPE_NAME__REAL: String = "float4"
        const val DB_TYPE_NAME__REAL_ARRAY: String = "_float4"
        const val DB_TYPE_NAME__SMALL_INT: String = "int2"
        const val DB_TYPE_NAME__SMALL_INT_ARRAY: String = "_int2"
        const val DB_TYPE_NAME__TEXT: String = "text"
        const val DB_TYPE_NAME__TEXT_ARRAY: String = "_text"
        const val DB_TYPE_NAME__TIMESTAMP: String = "timestamp"
        const val DB_TYPE_NAME__TIMESTAMP_ARRAY: String = "_timestamp"
        const val DB_TYPE_NAME__TIMESTAMP_TZ: String = "timestamptz"
        const val DB_TYPE_NAME__TIMESTAMP_TZ_ARRAY: String = "_timestamptz"
        const val DB_TYPE_NAME__TIME: String = "time"
        const val DB_TYPE_NAME__TIME_ARRAY: String = "_time"
        const val DB_TYPE_NAME__TIME_TZ: String = "timetz"
        const val DB_TYPE_NAME__TIME_TZ_ARRAY: String = "_timetz"
        const val DB_TYPE_NAME__XML: String = "xml"
        const val DB_TYPE_NAME__XML_ARRAY: String = "_xml"
    }


    // region Boolean
    val pgBit: SqDataTypePack<SqPgBit, SqPgBit>
    val pgBitArray: SqDataTypePack<List<SqPgBit?>, List<SqPgBit>>
    val pgBitVarying: SqDataTypePack<SqPgBit, SqPgBit>
    val pgBitVaryingArray: SqDataTypePack<List<SqPgBit?>, List<SqPgBit>>
    val pgBoolean: SqDataTypePack<Boolean, Boolean>
    val pgBooleanArray: SqDataTypePack<List<Boolean?>, List<Boolean>>

    val jBooleanList: SqDataTypePack<List<Boolean?>, List<Boolean>>
        get() = this.pgBooleanArray
    val jPgBit: SqDataTypePack<SqPgBit, SqPgBit>
        get() = this.pgBitVarying
    val jPgBitList: SqDataTypePack<List<SqPgBit?>, List<SqPgBit>>
        get() = this.pgBitVaryingArray
    // endregion


    // region Byte array
    val pgByteA: SqDataTypePack<ByteArray, ByteArray>
    val pgByteAArray: SqDataTypePack<List<ByteArray?>, List<ByteArray>>

    val jByteArrayList: SqDataTypePack<List<ByteArray?>, List<ByteArray>>
        get() = this.pgByteAArray
    // endregion


    // region Number
    val pgBigInt: SqDataTypePack<Long, Number>
    val pgBigIntArray: SqDataTypePack<List<Long?>, List<Number>>
    val pgDoublePrecision: SqDataTypePack<Double, Number>
    val pgDoublePrecisionArray: SqDataTypePack<List<Double?>, List<Number>>
    val pgInteger: SqDataTypePack<Int, Number>
    val pgIntegerArray: SqDataTypePack<List<Int?>, List<Number>>
    val pgNumeric: SqDataTypePack<BigDecimal, Number>
    val pgNumericArray: SqDataTypePack<List<BigDecimal?>, List<Number>>
    val pgReal: SqDataTypePack<Float, Number>
    val pgRealArray: SqDataTypePack<List<Float?>, List<Number>>
    val pgSmallInt: SqDataTypePack<Short, Number>
    val pgSmallIntArray: SqDataTypePack<List<Short?>, List<Number>>

    val jBigDecimalList: SqDataTypePack<List<BigDecimal?>, List<Number>>
        get() = this.pgNumericArray
    val jDoubleList: SqDataTypePack<List<Double?>, List<Number>>
        get() = this.pgDoublePrecisionArray
    val jFloatList: SqDataTypePack<List<Float?>, List<Number>>
        get() = this.pgRealArray
    val jIntList: SqDataTypePack<List<Int?>, List<Number>>
        get() = this.pgIntegerArray
    val jLongList: SqDataTypePack<List<Long?>, List<Number>>
        get() = this.pgBigIntArray
    val jShortList: SqDataTypePack<List<Short?>, List<Number>>
        get() = this.pgSmallIntArray
    // endregion


    // region String
    val pgChar: SqDataTypePack<Char, String>
    val pgCharArray: SqDataTypePack<List<Char?>, List<String>>
    val pgCharacter: SqDataTypePack<String, String>
    val pgCharacterArray: SqDataTypePack<List<String?>, List<String>>
    val pgCharacterVarying: SqDataTypePack<String, String>
    val pgCharacterVaryingArray: SqDataTypePack<List<String?>, List<String>>
    val pgText: SqDataTypePack<String, String>
    val pgTextArray: SqDataTypePack<List<String?>, List<String>>

    val jChar: SqDataTypePack<Char, String>
        get() = this.pgChar
    val jCharList: SqDataTypePack<List<Char?>, List<String>>
        get() = this.pgCharArray
    val jStringList: SqDataTypePack<List<String?>, List<String>>
        get() = this.pgCharacterVaryingArray
    // endregion


    // region Temporal
    val pgDate: SqDataTypePack<LocalDate, Timestamp>
    val pgDateJdbc: SqDataTypePack<Date, Timestamp>
    val pgDateArray: SqDataTypePack<List<LocalDate?>, List<Timestamp>>
    val pgDateArrayJdbc: SqDataTypePack<List<Date?>, List<Timestamp>>
    val pgTimestamp: SqDataTypePack<LocalDateTime, Timestamp>
    val pgTimestampJdbc: SqDataTypePack<Timestamp, Timestamp>
    val pgTimestampArray: SqDataTypePack<List<LocalDateTime?>, List<Timestamp>>
    val pgTimestampArrayJdbc: SqDataTypePack<List<Timestamp?>, List<Timestamp>>
    val pgTimestampTz: SqDataTypePack<OffsetDateTime, Timestamp>
    val pgTimestampTzJdbc: SqDataTypePack<Timestamp, Timestamp>
    val pgTimestampTzArray: SqDataTypePack<List<OffsetDateTime?>, List<Timestamp>>
    val pgTimestampTzArrayJdbc: SqDataTypePack<List<Timestamp?>, List<Timestamp>>
    val pgTime: SqDataTypePack<LocalTime, Time>
    val pgTimeJdbc: SqDataTypePack<Time, Time>
    val pgTimeArray: SqDataTypePack<List<LocalTime?>, List<Time>>
    val pgTimeArrayJdbc: SqDataTypePack<List<Time?>, List<Time>>
    val pgTimeTz: SqDataTypePack<OffsetTime, Time>
    val pgTimeTzJdbc: SqDataTypePack<Time, Time>
    val pgTimeTzArray: SqDataTypePack<List<OffsetTime?>, List<Time>>
    val pgTimeTzArrayJdbc: SqDataTypePack<List<Time?>, List<Time>>

    val jDateList: SqDataTypePack<List<Date?>, List<Timestamp>>
        get() = this.pgDateArrayJdbc
    val jLocalDateList: SqDataTypePack<List<LocalDate?>, List<Timestamp>>
        get() = this.pgDateArray
    val jLocalDateTimeList: SqDataTypePack<List<LocalDateTime?>, List<Timestamp>>
        get() = this.pgTimestampArray
    val jLocalTimeList: SqDataTypePack<List<LocalTime?>, List<Time>>
        get() = this.pgTimeArray
    val jOffsetDateTimeList: SqDataTypePack<List<OffsetDateTime?>, List<Timestamp>>
        get() = this.pgTimestampTzArray
    val jOffsetTimeList: SqDataTypePack<List<OffsetTime?>, List<Time>>
        get() = this.pgTimeTzArray
    val jTimeList: SqDataTypePack<List<Time?>, List<Time>>
        get() = this.pgTimeArrayJdbc
    val jTimestampList: SqDataTypePack<List<Timestamp?>, List<Timestamp>>
        get() = this.pgTimestampArrayJdbc
    // endregion


    // region Other
    val pgJson: SqDataTypePack<String, String>
    val pgJsonArray: SqDataTypePack<List<String?>, List<String>>
    val pgJsonB: SqDataTypePack<String, String>
    val pgJsonBArray: SqDataTypePack<List<String?>, List<String>>
    val pgXml: SqDataTypePack<SQLXML, String>
    val pgXmlArray: SqDataTypePack<List<SQLXML?>, List<String>>

    val jSqlXmlList: SqDataTypePack<List<SQLXML?>, List<String>>
        get() = this.pgXmlArray
    // endregion
}

fun <T: SqSettingsBuilder> T.pgDataTypes(value: SqPgDataTypes?): T =
    this.setValue(SqPgDataTypes::class.java, value)
val SqSettings.pgDataTypes: SqPgDataTypes
    get() = this.getValue(SqPgDataTypes::class.java) ?: SqPgDataTypesImpl.INSTANCE
val SqPgContext.dataTypes: SqPgDataTypes
    get() = this.settings.pgDataTypes
// endregion


// region NULLs / boolean
fun SqPgNullNs.pgBit(): SqNull<SqPgBit, SqPgBit> =
    this.nullItem(this.dataTypes.pgBit)
fun SqPgNullNs.pgBitArray(): SqNull<List<SqPgBit?>, List<SqPgBit>> =
    this.nullItem(this.dataTypes.pgBitArray)
fun SqPgNullNs.pgBitVarying(): SqNull<SqPgBit, SqPgBit> =
    this.nullItem(this.dataTypes.pgBitVarying)
fun SqPgNullNs.pgBitVaryingArray(): SqNull<List<SqPgBit?>, List<SqPgBit>> =
    this.nullItem(this.dataTypes.pgBitVaryingArray)
fun SqPgNullNs.pgBoolean(): SqNull<Boolean, Boolean> =
    this.nullItem(this.dataTypes.pgBoolean)
fun SqPgNullNs.pgBooleanArray(): SqNull<List<Boolean?>, List<Boolean>> =
    this.nullItem(this.dataTypes.pgBooleanArray)

fun SqPgNullNs.jBooleanList(): SqNull<List<Boolean?>, List<Boolean>> =
    this.nullItem(this.dataTypes.jBooleanList)
fun SqPgNullNs.jPgBit(): SqNull<SqPgBit, SqPgBit> =
    this.nullItem(this.dataTypes.jPgBit)
fun SqPgNullNs.jPgBitList(): SqNull<List<SqPgBit?>, List<SqPgBit>> =
    this.nullItem(this.dataTypes.jPgBitList)
// endregion


// region NULLs / byte array
fun SqPgNullNs.pgByteA(): SqNull<ByteArray, ByteArray> =
    this.nullItem(this.dataTypes.pgByteA)
fun SqPgNullNs.pgByteAArray(): SqNull<List<ByteArray?>, List<ByteArray>> =
    this.nullItem(this.dataTypes.pgByteAArray)

fun SqPgNullNs.jByteArrayList(): SqNull<List<ByteArray?>, List<ByteArray>> =
    this.nullItem(this.dataTypes.jByteArrayList)
// endregion


// region NULLs / number
fun SqPgNullNs.pgBigInt(): SqNull<Long, Number> =
    this.nullItem(this.dataTypes.pgBigInt)
fun SqPgNullNs.pgBigIntArray(): SqNull<List<Long?>, List<Number>> =
    this.nullItem(this.dataTypes.pgBigIntArray)
fun SqPgNullNs.pgDoublePrecision(): SqNull<Double, Number> =
    this.nullItem(this.dataTypes.pgDoublePrecision)
fun SqPgNullNs.pgDoublePrecisionArray(): SqNull<List<Double?>, List<Number>> =
    this.nullItem(this.dataTypes.pgDoublePrecisionArray)
fun SqPgNullNs.pgInteger(): SqNull<Int, Number> =
    this.nullItem(this.dataTypes.pgInteger)
fun SqPgNullNs.pgIntegerArray(): SqNull<List<Int?>, List<Number>> =
    this.nullItem(this.dataTypes.pgIntegerArray)
fun SqPgNullNs.pgNumeric(): SqNull<BigDecimal, Number> =
    this.nullItem(this.dataTypes.pgNumeric)
fun SqPgNullNs.pgNumericArray(): SqNull<List<BigDecimal?>, List<Number>> =
    this.nullItem(this.dataTypes.pgNumericArray)
fun SqPgNullNs.pgReal(): SqNull<Float, Number> =
    this.nullItem(this.dataTypes.pgReal)
fun SqPgNullNs.pgRealArray(): SqNull<List<Float?>, List<Number>> =
    this.nullItem(this.dataTypes.pgRealArray)
fun SqPgNullNs.pgSmallInt(): SqNull<Short, Number> =
    this.nullItem(this.dataTypes.pgSmallInt)
fun SqPgNullNs.pgSmallIntArray(): SqNull<List<Short?>, List<Number>> =
    this.nullItem(this.dataTypes.pgSmallIntArray)

fun SqPgNullNs.jBigDecimalList(): SqNull<List<BigDecimal?>, List<Number>> =
    this.nullItem(this.dataTypes.jBigDecimalList)
fun SqPgNullNs.jDoubleList(): SqNull<List<Double?>, List<Number>> =
    this.nullItem(this.dataTypes.jDoubleList)
fun SqPgNullNs.jFloatList(): SqNull<List<Float?>, List<Number>> =
    this.nullItem(this.dataTypes.jFloatList)
fun SqPgNullNs.jIntList(): SqNull<List<Int?>, List<Number>> =
    this.nullItem(this.dataTypes.jIntList)
fun SqPgNullNs.jLongList(): SqNull<List<Long?>, List<Number>> =
    this.nullItem(this.dataTypes.jLongList)
fun SqPgNullNs.jShortList(): SqNull<List<Short?>, List<Number>> =
    this.nullItem(this.dataTypes.jShortList)
// endregion


// region NULLs / string
fun SqPgNullNs.pgChar(): SqNull<Char, String> =
    this.nullItem(this.dataTypes.pgChar)
fun SqPgNullNs.pgCharArray(): SqNull<List<Char?>, List<String>> =
    this.nullItem(this.dataTypes.pgCharArray)
fun SqPgNullNs.pgCharacter(): SqNull<String, String> =
    this.nullItem(this.dataTypes.pgCharacter)
fun SqPgNullNs.pgCharacterArray(): SqNull<List<String?>, List<String>> =
    this.nullItem(this.dataTypes.pgCharacterArray)
fun SqPgNullNs.pgCharacterVarying(): SqNull<String, String> =
    this.nullItem(this.dataTypes.pgCharacterVarying)
fun SqPgNullNs.pgCharacterVaryingArray(): SqNull<List<String?>, List<String>> =
    this.nullItem(this.dataTypes.pgCharacterVaryingArray)
fun SqPgNullNs.pgText(): SqNull<String, String> =
    this.nullItem(this.dataTypes.pgText)
fun SqPgNullNs.pgTextArray(): SqNull<List<String?>, List<String>> =
    this.nullItem(this.dataTypes.pgTextArray)

fun SqPgNullNs.jChar(): SqNull<Char, String> =
    this.nullItem(this.dataTypes.jChar)
fun SqPgNullNs.jCharList(): SqNull<List<Char?>, List<String>> =
    this.nullItem(this.dataTypes.jCharList)
fun SqPgNullNs.jStringList(): SqNull<List<String?>, List<String>> =
    this.nullItem(this.dataTypes.jStringList)
// endregion


// region NULLs / temporal
fun SqPgNullNs.pgDate(): SqNull<LocalDate, Timestamp> =
    this.nullItem(this.dataTypes.pgDate)
fun SqPgNullNs.pgDateJdbc(): SqNull<Date, Timestamp> =
    this.nullItem(this.dataTypes.pgDateJdbc)
fun SqPgNullNs.pgDateArray(): SqNull<List<LocalDate?>, List<Timestamp>> =
    this.nullItem(this.dataTypes.pgDateArray)
fun SqPgNullNs.pgDateArrayJdbc(): SqNull<List<Date?>, List<Timestamp>> =
    this.nullItem(this.dataTypes.pgDateArrayJdbc)
fun SqPgNullNs.pgTimestamp(): SqNull<LocalDateTime, Timestamp> =
    this.nullItem(this.dataTypes.pgTimestamp)
fun SqPgNullNs.pgTimestampJdbc(): SqNull<Timestamp, Timestamp> =
    this.nullItem(this.dataTypes.pgTimestampJdbc)
fun SqPgNullNs.pgTimestampArray(): SqNull<List<LocalDateTime?>, List<Timestamp>> =
    this.nullItem(this.dataTypes.pgTimestampArray)
fun SqPgNullNs.pgTimestampArrayJdbc(): SqNull<List<Timestamp?>, List<Timestamp>> =
    this.nullItem(this.dataTypes.pgTimestampArrayJdbc)
fun SqPgNullNs.pgTimestampTz(): SqNull<OffsetDateTime, Timestamp> =
    this.nullItem(this.dataTypes.pgTimestampTz)
fun SqPgNullNs.pgTimestampTzJdbc(): SqNull<Timestamp, Timestamp> =
    this.nullItem(this.dataTypes.pgTimestampTzJdbc)
fun SqPgNullNs.pgTimestampTzArray(): SqNull<List<OffsetDateTime?>, List<Timestamp>> =
    this.nullItem(this.dataTypes.pgTimestampTzArray)
fun SqPgNullNs.pgTimestampTzArrayJdbc(): SqNull<List<Timestamp?>, List<Timestamp>> =
    this.nullItem(this.dataTypes.pgTimestampTzArrayJdbc)
fun SqPgNullNs.pgTime(): SqNull<LocalTime, Time> =
    this.nullItem(this.dataTypes.pgTime)
fun SqPgNullNs.pgTimeJdbc(): SqNull<Time, Time> =
    this.nullItem(this.dataTypes.pgTimeJdbc)
fun SqPgNullNs.pgTimeArray(): SqNull<List<LocalTime?>, List<Time>> =
    this.nullItem(this.dataTypes.pgTimeArray)
fun SqPgNullNs.pgTimeArrayJdbc(): SqNull<List<Time?>, List<Time>> =
    this.nullItem(this.dataTypes.pgTimeArrayJdbc)
fun SqPgNullNs.pgTimeTz(): SqNull<OffsetTime, Time> =
    this.nullItem(this.dataTypes.pgTimeTz)
fun SqPgNullNs.pgTimeTzJdbc(): SqNull<Time, Time> =
    this.nullItem(this.dataTypes.pgTimeTzJdbc)
fun SqPgNullNs.pgTimeTzArray(): SqNull<List<OffsetTime?>, List<Time>> =
    this.nullItem(this.dataTypes.pgTimeTzArray)
fun SqPgNullNs.pgTimeTzArrayJdbc(): SqNull<List<Time?>, List<Time>> =
    this.nullItem(this.dataTypes.pgTimeTzArrayJdbc)

fun SqPgNullNs.jDateList(): SqNull<List<Date?>, List<Timestamp>> =
    this.nullItem(this.dataTypes.jDateList)
fun SqPgNullNs.jLocalDateList(): SqNull<List<LocalDate?>, List<Timestamp>> =
    this.nullItem(this.dataTypes.jLocalDateList)
fun SqPgNullNs.jLocalDateTimeList(): SqNull<List<LocalDateTime?>, List<Timestamp>> =
    this.nullItem(this.dataTypes.jLocalDateTimeList)
fun SqPgNullNs.jLocalTimeList(): SqNull<List<LocalTime?>, List<Time>> =
    this.nullItem(this.dataTypes.jLocalTimeList)
fun SqPgNullNs.jOffsetDateTimeList(): SqNull<List<OffsetDateTime?>, List<Timestamp>> =
    this.nullItem(this.dataTypes.jOffsetDateTimeList)
fun SqPgNullNs.jOffsetTimeList(): SqNull<List<OffsetTime?>, List<Time>> =
    this.nullItem(this.dataTypes.jOffsetTimeList)
fun SqPgNullNs.jTimeList(): SqNull<List<Time?>, List<Time>> =
    this.nullItem(this.dataTypes.jTimeList)
fun SqPgNullNs.jTimestampList(): SqNull<List<Timestamp?>, List<Timestamp>> =
    this.nullItem(this.dataTypes.jTimestampList)
// endregion


// region NULLs / other
fun SqPgNullNs.pgJson(): SqNull<String, String> =
    this.nullItem(this.dataTypes.pgJson)
fun SqPgNullNs.pgJsonArray(): SqNull<List<String?>, List<String>> =
    this.nullItem(this.dataTypes.pgJsonArray)
fun SqPgNullNs.pgJsonB(): SqNull<String, String> =
    this.nullItem(this.dataTypes.pgJsonB)
fun SqPgNullNs.pgJsonBArray(): SqNull<List<String?>, List<String>> =
    this.nullItem(this.dataTypes.pgJsonBArray)
fun SqPgNullNs.pgXml(): SqNull<SQLXML, String> =
    this.nullItem(this.dataTypes.pgXml)
fun SqPgNullNs.pgXmlArray(): SqNull<List<SQLXML?>, List<String>> =
    this.nullItem(this.dataTypes.pgXmlArray)

fun SqPgNullNs.jSqlXmlList(): SqNull<List<SQLXML?>, List<String>> =
    this.nullItem(this.dataTypes.jSqlXmlList)
// endregion


// region Parameters / boolean
@JvmName("pgBit__notNull")
fun SqPgParameterNs.pgBit(value: SqPgBit): SqParameter<SqPgBit, SqPgBit> =
    this.parameter(this.dataTypes.pgBit, value)
@JvmName("pgBit__nullable")
fun SqPgParameterNs.pgBit(value: SqPgBit?): SqParameter<SqPgBit?, SqPgBit> =
    this.parameter(this.dataTypes.pgBit, value)

@JvmName("pgBitArray__notNull")
fun SqPgParameterNs.pgBitArray(value: List<SqPgBit?>): SqParameter<List<SqPgBit?>, List<SqPgBit>> =
    this.parameter(this.dataTypes.pgBitArray, value)
@JvmName("pgBitArray__nullable")
fun SqPgParameterNs.pgBitArray(value: List<SqPgBit?>?): SqParameter<List<SqPgBit?>?, List<SqPgBit>> =
    this.parameter(this.dataTypes.pgBitArray, value)

@JvmName("pgBitVarying__notNull")
fun SqPgParameterNs.pgBitVarying(value: SqPgBit): SqParameter<SqPgBit, SqPgBit> =
    this.parameter(this.dataTypes.pgBitVarying, value)
@JvmName("pgBitVarying__nullable")
fun SqPgParameterNs.pgBitVarying(value: SqPgBit?): SqParameter<SqPgBit?, SqPgBit> =
    this.parameter(this.dataTypes.pgBitVarying, value)

@JvmName("pgBitVaryingArray__notNull")
fun SqPgParameterNs.pgBitVaryingArray(value: List<SqPgBit?>): SqParameter<List<SqPgBit?>, List<SqPgBit>> =
    this.parameter(this.dataTypes.pgBitVaryingArray, value)
@JvmName("pgBitVaryingArray__nullable")
fun SqPgParameterNs.pgBitVaryingArray(value: List<SqPgBit?>?): SqParameter<List<SqPgBit?>?, List<SqPgBit>> =
    this.parameter(this.dataTypes.pgBitVaryingArray, value)

@JvmName("pgBoolean__notNull")
fun SqPgParameterNs.pgBoolean(value: Boolean): SqParameter<Boolean, Boolean> =
    this.parameter(this.dataTypes.pgBoolean, value)
@JvmName("pgBoolean__nullable")
fun SqPgParameterNs.pgBoolean(value: Boolean?): SqParameter<Boolean?, Boolean> =
    this.parameter(this.dataTypes.pgBoolean, value)

@JvmName("pgBooleanArray__notNull")
fun SqPgParameterNs.pgBooleanArray(value: List<Boolean?>): SqParameter<List<Boolean?>, List<Boolean>> =
    this.parameter(this.dataTypes.pgBooleanArray, value)
@JvmName("pgBooleanArray__nullable")
fun SqPgParameterNs.pgBooleanArray(value: List<Boolean?>?): SqParameter<List<Boolean?>?, List<Boolean>> =
    this.parameter(this.dataTypes.pgBooleanArray, value)


@JvmName("parameter__booleanList__notNull")
fun SqPgParameterNs.parameter(value: List<Boolean?>): SqParameter<List<Boolean?>, List<Boolean>> =
    this.parameter(this.dataTypes.jBooleanList, value)
@JvmName("parameter__booleanList__nullable")
fun SqPgParameterNs.parameter(value: List<Boolean?>?): SqParameter<List<Boolean?>?, List<Boolean>> =
    this.parameter(this.dataTypes.jBooleanList, value)

@JvmName("parameter__notNull")
fun SqPgParameterNs.parameter(value: SqPgBit): SqParameter<SqPgBit, SqPgBit> =
    this.parameter(this.dataTypes.jPgBit, value)
@JvmName("parameter__nullable")
fun SqPgParameterNs.parameter(value: SqPgBit?): SqParameter<SqPgBit?, SqPgBit> =
    this.parameter(this.dataTypes.jPgBit, value)

@JvmName("parameter__pgBitList__notNull")
fun SqPgParameterNs.parameter(value: List<SqPgBit?>): SqParameter<List<SqPgBit?>, List<SqPgBit>> =
    this.parameter(this.dataTypes.jPgBitList, value)
@JvmName("parameter__pgBitList__nullable")
fun SqPgParameterNs.parameter(value: List<SqPgBit?>?): SqParameter<List<SqPgBit?>?, List<SqPgBit>> =
    this.parameter(this.dataTypes.jPgBitList, value)
// endregion


// region Parameters / byte array
@JvmName("pgByteA__notNull")
fun SqPgParameterNs.pgByteA(value: ByteArray): SqParameter<ByteArray, ByteArray> =
    this.parameter(this.dataTypes.pgByteA, value)
@JvmName("pgByteA__nullable")
fun SqPgParameterNs.pgByteA(value: ByteArray?): SqParameter<ByteArray?, ByteArray> =
    this.parameter(this.dataTypes.pgByteA, value)

@JvmName("pgByteAArray__notNull")
fun SqPgParameterNs.pgByteAArray(value: List<ByteArray?>): SqParameter<List<ByteArray?>, List<ByteArray>> =
    this.parameter(this.dataTypes.pgByteAArray, value)
@JvmName("pgByteAArray__nullable")
fun SqPgParameterNs.pgByteAArray(value: List<ByteArray?>?): SqParameter<List<ByteArray?>?, List<ByteArray>> =
    this.parameter(this.dataTypes.pgByteAArray, value)


@JvmName("parameter__byteArrayList__notNull")
fun SqPgParameterNs.parameter(value: List<ByteArray?>): SqParameter<List<ByteArray?>, List<ByteArray>> =
    this.parameter(this.dataTypes.jByteArrayList, value)
@JvmName("parameter__byteArrayList__nullable")
fun SqPgParameterNs.parameter(value: List<ByteArray?>?): SqParameter<List<ByteArray?>?, List<ByteArray>> =
    this.parameter(this.dataTypes.jByteArrayList, value)
// endregion


// region Parameters / number
@JvmName("pgBigInt__notNull")
fun SqPgParameterNs.pgBigInt(value: Long): SqParameter<Long, Number> =
    this.parameter(this.dataTypes.pgBigInt, value)
@JvmName("pgBigInt__nullable")
fun SqPgParameterNs.pgBigInt(value: Long?): SqParameter<Long?, Number> =
    this.parameter(this.dataTypes.pgBigInt, value)

@JvmName("pgBigIntArray__notNull")
fun SqPgParameterNs.pgBigIntArray(value: List<Long?>): SqParameter<List<Long?>, List<Number>> =
    this.parameter(this.dataTypes.pgBigIntArray, value)
@JvmName("pgBigIntArray__nullable")
fun SqPgParameterNs.pgBigIntArray(value: List<Long?>?): SqParameter<List<Long?>?, List<Number>> =
    this.parameter(this.dataTypes.pgBigIntArray, value)

@JvmName("pgDoublePrecision__notNull")
fun SqPgParameterNs.pgDoublePrecision(value: Double): SqParameter<Double, Number> =
    this.parameter(this.dataTypes.pgDoublePrecision, value)
@JvmName("pgDoublePrecision__nullable")
fun SqPgParameterNs.pgDoublePrecision(value: Double?): SqParameter<Double?, Number> =
    this.parameter(this.dataTypes.pgDoublePrecision, value)

@JvmName("pgDoublePrecisionArray__notNull")
fun SqPgParameterNs.pgDoublePrecisionArray(value: List<Double?>): SqParameter<List<Double?>, List<Number>> =
    this.parameter(this.dataTypes.pgDoublePrecisionArray, value)
@JvmName("pgDoublePrecisionArray__nullable")
fun SqPgParameterNs.pgDoublePrecisionArray(value: List<Double?>?): SqParameter<List<Double?>?, List<Number>> =
    this.parameter(this.dataTypes.pgDoublePrecisionArray, value)

@JvmName("pgInteger__notNull")
fun SqPgParameterNs.pgInteger(value: Int): SqParameter<Int, Number> =
    this.parameter(this.dataTypes.pgInteger, value)
@JvmName("pgInteger__nullable")
fun SqPgParameterNs.pgInteger(value: Int?): SqParameter<Int?, Number> =
    this.parameter(this.dataTypes.pgInteger, value)

@JvmName("pgIntegerArray__notNull")
fun SqPgParameterNs.pgIntegerArray(value: List<Int?>): SqParameter<List<Int?>, List<Number>> =
    this.parameter(this.dataTypes.pgIntegerArray, value)
@JvmName("pgIntegerArray__nullable")
fun SqPgParameterNs.pgIntegerArray(value: List<Int?>?): SqParameter<List<Int?>?, List<Number>> =
    this.parameter(this.dataTypes.pgIntegerArray, value)

@JvmName("pgNumeric__notNull")
fun SqPgParameterNs.pgNumeric(value: BigDecimal): SqParameter<BigDecimal, Number> =
    this.parameter(this.dataTypes.pgNumeric, value)
@JvmName("pgNumeric__nullable")
fun SqPgParameterNs.pgNumeric(value: BigDecimal?): SqParameter<BigDecimal?, Number> =
    this.parameter(this.dataTypes.pgNumeric, value)

@JvmName("pgNumericArray__notNull")
fun SqPgParameterNs.pgNumericArray(value: List<BigDecimal?>): SqParameter<List<BigDecimal?>, List<Number>> =
    this.parameter(this.dataTypes.pgNumericArray, value)
@JvmName("pgNumericArray__nullable")
fun SqPgParameterNs.pgNumericArray(value: List<BigDecimal?>?): SqParameter<List<BigDecimal?>?, List<Number>> =
    this.parameter(this.dataTypes.pgNumericArray, value)

@JvmName("pgReal__notNull")
fun SqPgParameterNs.pgReal(value: Float): SqParameter<Float, Number> =
    this.parameter(this.dataTypes.pgReal, value)
@JvmName("pgReal__nullable")
fun SqPgParameterNs.pgReal(value: Float?): SqParameter<Float?, Number> =
    this.parameter(this.dataTypes.pgReal, value)

@JvmName("pgRealArray__notNull")
fun SqPgParameterNs.pgRealArray(value: List<Float?>): SqParameter<List<Float?>, List<Number>> =
    this.parameter(this.dataTypes.pgRealArray, value)
@JvmName("pgRealArray__nullable")
fun SqPgParameterNs.pgRealArray(value: List<Float?>?): SqParameter<List<Float?>?, List<Number>> =
    this.parameter(this.dataTypes.pgRealArray, value)

@JvmName("pgSmallInt__notNull")
fun SqPgParameterNs.pgSmallInt(value: Short): SqParameter<Short, Number> =
    this.parameter(this.dataTypes.pgSmallInt, value)
@JvmName("pgSmallInt__nullable")
fun SqPgParameterNs.pgSmallInt(value: Short?): SqParameter<Short?, Number> =
    this.parameter(this.dataTypes.pgSmallInt, value)

@JvmName("pgSmallIntArray__notNull")
fun SqPgParameterNs.pgSmallIntArray(value: List<Short?>): SqParameter<List<Short?>, List<Number>> =
    this.parameter(this.dataTypes.pgSmallIntArray, value)
@JvmName("pgSmallIntArray__nullable")
fun SqPgParameterNs.pgSmallIntArray(value: List<Short?>?): SqParameter<List<Short?>?, List<Number>> =
    this.parameter(this.dataTypes.pgSmallIntArray, value)


@JvmName("parameter__bigDecimalList__notNull")
fun SqPgParameterNs.parameter(value: List<BigDecimal?>): SqParameter<List<BigDecimal?>, List<Number>> =
    this.parameter(this.dataTypes.jBigDecimalList, value)
@JvmName("parameter__bigDecimalList__nullable")
fun SqPgParameterNs.parameter(value: List<BigDecimal?>?): SqParameter<List<BigDecimal?>?, List<Number>> =
    this.parameter(this.dataTypes.jBigDecimalList, value)

@JvmName("parameter__doubleList__notNull")
fun SqPgParameterNs.parameter(value: List<Double?>): SqParameter<List<Double?>, List<Number>> =
    this.parameter(this.dataTypes.jDoubleList, value)
@JvmName("parameter__doubleList__nullable")
fun SqPgParameterNs.parameter(value: List<Double?>?): SqParameter<List<Double?>?, List<Number>> =
    this.parameter(this.dataTypes.jDoubleList, value)

@JvmName("parameter__floatList__notNull")
fun SqPgParameterNs.parameter(value: List<Float?>): SqParameter<List<Float?>, List<Number>> =
    this.parameter(this.dataTypes.jFloatList, value)
@JvmName("parameter__floatList__nullable")
fun SqPgParameterNs.parameter(value: List<Float?>?): SqParameter<List<Float?>?, List<Number>> =
    this.parameter(this.dataTypes.jFloatList, value)

@JvmName("parameter__intList__notNull")
fun SqPgParameterNs.parameter(value: List<Int?>): SqParameter<List<Int?>, List<Number>> =
    this.parameter(this.dataTypes.jIntList, value)
@JvmName("parameter__intList__nullable")
fun SqPgParameterNs.parameter(value: List<Int?>?): SqParameter<List<Int?>?, List<Number>> =
    this.parameter(this.dataTypes.jIntList, value)

@JvmName("parameter__longList__notNull")
fun SqPgParameterNs.parameter(value: List<Long?>): SqParameter<List<Long?>, List<Number>> =
    this.parameter(this.dataTypes.jLongList, value)
@JvmName("parameter__longList__nullable")
fun SqPgParameterNs.parameter(value: List<Long?>?): SqParameter<List<Long?>?, List<Number>> =
    this.parameter(this.dataTypes.jLongList, value)

@JvmName("parameter__shortList__notNull")
fun SqPgParameterNs.parameter(value: List<Short?>): SqParameter<List<Short?>, List<Number>> =
    this.parameter(this.dataTypes.jShortList, value)
@JvmName("parameter__shortList__nullable")
fun SqPgParameterNs.parameter(value: List<Short?>?): SqParameter<List<Short?>?, List<Number>> =
    this.parameter(this.dataTypes.jShortList, value)
// endregion


// region Parameters / string
@JvmName("pgChar__notNull")
fun SqPgParameterNs.pgChar(value: Char): SqParameter<Char, String> =
    this.parameter(this.dataTypes.pgChar, value)
@JvmName("pgChar__nullable")
fun SqPgParameterNs.pgChar(value: Char?): SqParameter<Char?, String> =
    this.parameter(this.dataTypes.pgChar, value)

@JvmName("pgCharArray__notNull")
fun SqPgParameterNs.pgCharArray(value: List<Char?>): SqParameter<List<Char?>, List<String>> =
    this.parameter(this.dataTypes.pgCharArray, value)
@JvmName("pgCharArray__nullable")
fun SqPgParameterNs.pgCharArray(value: List<Char?>?): SqParameter<List<Char?>?, List<String>> =
    this.parameter(this.dataTypes.pgCharArray, value)

@JvmName("pgCharacter__notNull")
fun SqPgParameterNs.pgCharacter(value: String): SqParameter<String, String> =
    this.parameter(this.dataTypes.pgCharacter, value)
@JvmName("pgCharacter__nullable")
fun SqPgParameterNs.pgCharacter(value: String?): SqParameter<String?, String> =
    this.parameter(this.dataTypes.pgCharacter, value)

@JvmName("pgCharacterArray__notNull")
fun SqPgParameterNs.pgCharacterArray(value: List<String?>): SqParameter<List<String?>, List<String>> =
    this.parameter(this.dataTypes.pgCharacterArray, value)
@JvmName("pgCharacterArray__nullable")
fun SqPgParameterNs.pgCharacterArray(value: List<String?>?): SqParameter<List<String?>?, List<String>> =
    this.parameter(this.dataTypes.pgCharacterArray, value)

@JvmName("pgCharacterVarying__notNull")
fun SqPgParameterNs.pgCharacterVarying(value: String): SqParameter<String, String> =
    this.parameter(this.dataTypes.pgCharacterVarying, value)
@JvmName("pgCharacterVarying__nullable")
fun SqPgParameterNs.pgCharacterVarying(value: String?): SqParameter<String?, String> =
    this.parameter(this.dataTypes.pgCharacterVarying, value)

@JvmName("pgCharacterVaryingArray__notNull")
fun SqPgParameterNs.pgCharacterVaryingArray(value: List<String?>): SqParameter<List<String?>, List<String>> =
    this.parameter(this.dataTypes.pgCharacterVaryingArray, value)
@JvmName("pgCharacterVaryingArray__nullable")
fun SqPgParameterNs.pgCharacterVaryingArray(value: List<String?>?): SqParameter<List<String?>?, List<String>> =
    this.parameter(this.dataTypes.pgCharacterVaryingArray, value)

@JvmName("pgText__notNull")
fun SqPgParameterNs.pgText(value: String): SqParameter<String, String> =
    this.parameter(this.dataTypes.pgText, value)
@JvmName("pgText__nullable")
fun SqPgParameterNs.pgText(value: String?): SqParameter<String?, String> =
    this.parameter(this.dataTypes.pgText, value)

@JvmName("pgTextArray__notNull")
fun SqPgParameterNs.pgTextArray(value: List<String?>): SqParameter<List<String?>, List<String>> =
    this.parameter(this.dataTypes.pgTextArray, value)
@JvmName("pgTextArray__nullable")
fun SqPgParameterNs.pgTextArray(value: List<String?>?): SqParameter<List<String?>?, List<String>> =
    this.parameter(this.dataTypes.pgTextArray, value)


@JvmName("parameter__notNull")
fun SqPgParameterNs.parameter(value: Char): SqParameter<Char, String> =
    this.parameter(this.dataTypes.jChar, value)
@JvmName("parameter__nullable")
fun SqPgParameterNs.parameter(value: Char?): SqParameter<Char?, String> =
    this.parameter(this.dataTypes.jChar, value)

@JvmName("parameter__charList__notNull")
fun SqPgParameterNs.parameter(value: List<Char?>): SqParameter<List<Char?>, List<String>> =
    this.parameter(this.dataTypes.jCharList, value)
@JvmName("parameter__charList__nullable")
fun SqPgParameterNs.parameter(value: List<Char?>?): SqParameter<List<Char?>?, List<String>> =
    this.parameter(this.dataTypes.jCharList, value)

@JvmName("parameter__stringList__notNull")
fun SqPgParameterNs.parameter(value: List<String?>): SqParameter<List<String?>, List<String>> =
    this.parameter(this.dataTypes.jStringList, value)
@JvmName("parameter__stringList__nullable")
fun SqPgParameterNs.parameter(value: List<String?>?): SqParameter<List<String?>?, List<String>> =
    this.parameter(this.dataTypes.jStringList, value)
// endregion


// region Parameters / temporal
@JvmName("pgDate__notNull")
fun SqPgParameterNs.pgDate(value: LocalDate): SqParameter<LocalDate, Timestamp> =
    this.parameter(this.dataTypes.pgDate, value)
@JvmName("pgDate__nullable")
fun SqPgParameterNs.pgDate(value: LocalDate?): SqParameter<LocalDate?, Timestamp> =
    this.parameter(this.dataTypes.pgDate, value)

@JvmName("pgDateJdbc__notNull")
fun SqPgParameterNs.pgDateJdbc(value: Date): SqParameter<Date, Timestamp> =
    this.parameter(this.dataTypes.pgDateJdbc, value)
@JvmName("pgDateJdbc__nullable")
fun SqPgParameterNs.pgDateJdbc(value: Date?): SqParameter<Date?, Timestamp> =
    this.parameter(this.dataTypes.pgDateJdbc, value)

@JvmName("pgDateArray__notNull")
fun SqPgParameterNs.pgDateArray(value: List<LocalDate?>): SqParameter<List<LocalDate?>, List<Timestamp>> =
    this.parameter(this.dataTypes.pgDateArray, value)
@JvmName("pgDateArray__nullable")
fun SqPgParameterNs.pgDateArray(value: List<LocalDate?>?): SqParameter<List<LocalDate?>?, List<Timestamp>> =
    this.parameter(this.dataTypes.pgDateArray, value)

@JvmName("pgDateArrayJdbc__notNull")
fun SqPgParameterNs.pgDateArrayJdbc(value: List<Date?>): SqParameter<List<Date?>, List<Timestamp>> =
    this.parameter(this.dataTypes.pgDateArrayJdbc, value)
@JvmName("pgDateArrayJdbc__nullable")
fun SqPgParameterNs.pgDateArrayJdbc(value: List<Date?>?): SqParameter<List<Date?>?, List<Timestamp>> =
    this.parameter(this.dataTypes.pgDateArrayJdbc, value)

@JvmName("pgTimestamp__notNull")
fun SqPgParameterNs.pgTimestamp(value: LocalDateTime): SqParameter<LocalDateTime, Timestamp> =
    this.parameter(this.dataTypes.pgTimestamp, value)
@JvmName("pgTimestamp__nullable")
fun SqPgParameterNs.pgTimestamp(value: LocalDateTime?): SqParameter<LocalDateTime?, Timestamp> =
    this.parameter(this.dataTypes.pgTimestamp, value)

@JvmName("pgTimestampJdbc__notNull")
fun SqPgParameterNs.pgTimestampJdbc(value: Timestamp): SqParameter<Timestamp, Timestamp> =
    this.parameter(this.dataTypes.pgTimestampJdbc, value)
@JvmName("pgTimestampJdbc__nullable")
fun SqPgParameterNs.pgTimestampJdbc(value: Timestamp?): SqParameter<Timestamp?, Timestamp> =
    this.parameter(this.dataTypes.pgTimestampJdbc, value)

@JvmName("pgTimestampArray__notNull")
fun SqPgParameterNs.pgTimestampArray(value: List<LocalDateTime?>): SqParameter<List<LocalDateTime?>, List<Timestamp>> =
    this.parameter(this.dataTypes.pgTimestampArray, value)
@JvmName("pgTimestampArray__nullable")
fun SqPgParameterNs.pgTimestampArray(value: List<LocalDateTime?>?): SqParameter<List<LocalDateTime?>?, List<Timestamp>> =
    this.parameter(this.dataTypes.pgTimestampArray, value)

@JvmName("pgTimestampArrayJdbc__notNull")
fun SqPgParameterNs.pgTimestampArrayJdbc(value: List<Timestamp?>): SqParameter<List<Timestamp?>, List<Timestamp>> =
    this.parameter(this.dataTypes.pgTimestampArrayJdbc, value)
@JvmName("pgTimestampArrayJdbc__nullable")
fun SqPgParameterNs.pgTimestampArrayJdbc(value: List<Timestamp?>?): SqParameter<List<Timestamp?>?, List<Timestamp>> =
    this.parameter(this.dataTypes.pgTimestampArrayJdbc, value)

@JvmName("pgTimestampTz__notNull")
fun SqPgParameterNs.pgTimestampTz(value: OffsetDateTime): SqParameter<OffsetDateTime, Timestamp> =
    this.parameter(this.dataTypes.pgTimestampTz, value)
@JvmName("pgTimestampTz__nullable")
fun SqPgParameterNs.pgTimestampTz(value: OffsetDateTime?): SqParameter<OffsetDateTime?, Timestamp> =
    this.parameter(this.dataTypes.pgTimestampTz, value)

@JvmName("pgTimestampTzJdbc__notNull")
fun SqPgParameterNs.pgTimestampTzJdbc(value: Timestamp): SqParameter<Timestamp, Timestamp> =
    this.parameter(this.dataTypes.pgTimestampTzJdbc, value)
@JvmName("pgTimestampTzJdbc__nullable")
fun SqPgParameterNs.pgTimestampTzJdbc(value: Timestamp?): SqParameter<Timestamp?, Timestamp> =
    this.parameter(this.dataTypes.pgTimestampTzJdbc, value)

@JvmName("pgTimestampTzArray__notNull")
fun SqPgParameterNs.pgTimestampTzArray(value: List<OffsetDateTime?>): SqParameter<List<OffsetDateTime?>, List<Timestamp>> =
    this.parameter(this.dataTypes.pgTimestampTzArray, value)
@JvmName("pgTimestampTzArray__nullable")
fun SqPgParameterNs.pgTimestampTzArray(value: List<OffsetDateTime?>?): SqParameter<List<OffsetDateTime?>?, List<Timestamp>> =
    this.parameter(this.dataTypes.pgTimestampTzArray, value)

@JvmName("pgTimestampTzArrayJdbc__notNull")
fun SqPgParameterNs.pgTimestampTzArrayJdbc(value: List<Timestamp?>): SqParameter<List<Timestamp?>, List<Timestamp>> =
    this.parameter(this.dataTypes.pgTimestampTzArrayJdbc, value)
@JvmName("pgTimestampTzArrayJdbc__nullable")
fun SqPgParameterNs.pgTimestampTzArrayJdbc(value: List<Timestamp?>?): SqParameter<List<Timestamp?>?, List<Timestamp>> =
    this.parameter(this.dataTypes.pgTimestampTzArrayJdbc, value)

@JvmName("pgTime__notNull")
fun SqPgParameterNs.pgTime(value: LocalTime): SqParameter<LocalTime, Time> =
    this.parameter(this.dataTypes.pgTime, value)
@JvmName("pgTime__nullable")
fun SqPgParameterNs.pgTime(value: LocalTime?): SqParameter<LocalTime?, Time> =
    this.parameter(this.dataTypes.pgTime, value)

@JvmName("pgTimeJdbc__notNull")
fun SqPgParameterNs.pgTimeJdbc(value: Time): SqParameter<Time, Time> =
    this.parameter(this.dataTypes.pgTimeJdbc, value)
@JvmName("pgTimeJdbc__nullable")
fun SqPgParameterNs.pgTimeJdbc(value: Time?): SqParameter<Time?, Time> =
    this.parameter(this.dataTypes.pgTimeJdbc, value)

@JvmName("pgTimeArray__notNull")
fun SqPgParameterNs.pgTimeArray(value: List<LocalTime?>): SqParameter<List<LocalTime?>, List<Time>> =
    this.parameter(this.dataTypes.pgTimeArray, value)
@JvmName("pgTimeArray__nullable")
fun SqPgParameterNs.pgTimeArray(value: List<LocalTime?>?): SqParameter<List<LocalTime?>?, List<Time>> =
    this.parameter(this.dataTypes.pgTimeArray, value)

@JvmName("pgTimeArrayJdbc__notNull")
fun SqPgParameterNs.pgTimeArrayJdbc(value: List<Time?>): SqParameter<List<Time?>, List<Time>> =
    this.parameter(this.dataTypes.pgTimeArrayJdbc, value)
@JvmName("pgTimeArrayJdbc__nullable")
fun SqPgParameterNs.pgTimeArrayJdbc(value: List<Time?>?): SqParameter<List<Time?>?, List<Time>> =
    this.parameter(this.dataTypes.pgTimeArrayJdbc, value)

@JvmName("pgTimeTz__notNull")
fun SqPgParameterNs.pgTimeTz(value: OffsetTime): SqParameter<OffsetTime, Time> =
    this.parameter(this.dataTypes.pgTimeTz, value)
@JvmName("pgTimeTz__nullable")
fun SqPgParameterNs.pgTimeTz(value: OffsetTime?): SqParameter<OffsetTime?, Time> =
    this.parameter(this.dataTypes.pgTimeTz, value)

@JvmName("pgTimeTzJdbc__notNull")
fun SqPgParameterNs.pgTimeTzJdbc(value: Time): SqParameter<Time, Time> =
    this.parameter(this.dataTypes.pgTimeTzJdbc, value)
@JvmName("pgTimeTzJdbc__nullable")
fun SqPgParameterNs.pgTimeTzJdbc(value: Time?): SqParameter<Time?, Time> =
    this.parameter(this.dataTypes.pgTimeTzJdbc, value)

@JvmName("pgTimeTzArray__notNull")
fun SqPgParameterNs.pgTimeTzArray(value: List<OffsetTime?>): SqParameter<List<OffsetTime?>, List<Time>> =
    this.parameter(this.dataTypes.pgTimeTzArray, value)
@JvmName("pgTimeTzArray__nullable")
fun SqPgParameterNs.pgTimeTzArray(value: List<OffsetTime?>?): SqParameter<List<OffsetTime?>?, List<Time>> =
    this.parameter(this.dataTypes.pgTimeTzArray, value)

@JvmName("pgTimeTzArrayJdbc__notNull")
fun SqPgParameterNs.pgTimeTzArrayJdbc(value: List<Time?>): SqParameter<List<Time?>, List<Time>> =
    this.parameter(this.dataTypes.pgTimeTzArrayJdbc, value)
@JvmName("pgTimeTzArrayJdbc__nullable")
fun SqPgParameterNs.pgTimeTzArrayJdbc(value: List<Time?>?): SqParameter<List<Time?>?, List<Time>> =
    this.parameter(this.dataTypes.pgTimeTzArrayJdbc, value)


@JvmName("parameter__dateList__notNull")
fun SqPgParameterNs.parameter(value: List<Date?>): SqParameter<List<Date?>, List<Timestamp>> =
    this.parameter(this.dataTypes.jDateList, value)
@JvmName("parameter__dateList__nullable")
fun SqPgParameterNs.parameter(value: List<Date?>?): SqParameter<List<Date?>?, List<Timestamp>> =
    this.parameter(this.dataTypes.jDateList, value)

@JvmName("parameter__localDateList__notNull")
fun SqPgParameterNs.parameter(value: List<LocalDate?>): SqParameter<List<LocalDate?>, List<Timestamp>> =
    this.parameter(this.dataTypes.jLocalDateList, value)
@JvmName("parameter__localDateList__nullable")
fun SqPgParameterNs.parameter(value: List<LocalDate?>?): SqParameter<List<LocalDate?>?, List<Timestamp>> =
    this.parameter(this.dataTypes.jLocalDateList, value)

@JvmName("parameter__localDateTimeList__notNull")
fun SqPgParameterNs.parameter(value: List<LocalDateTime?>): SqParameter<List<LocalDateTime?>, List<Timestamp>> =
    this.parameter(this.dataTypes.jLocalDateTimeList, value)
@JvmName("parameter__localDateTimeList__nullable")
fun SqPgParameterNs.parameter(value: List<LocalDateTime?>?): SqParameter<List<LocalDateTime?>?, List<Timestamp>> =
    this.parameter(this.dataTypes.jLocalDateTimeList, value)

@JvmName("parameter__localTimeList__notNull")
fun SqPgParameterNs.parameter(value: List<LocalTime?>): SqParameter<List<LocalTime?>, List<Time>> =
    this.parameter(this.dataTypes.jLocalTimeList, value)
@JvmName("parameter__localTimeList__nullable")
fun SqPgParameterNs.parameter(value: List<LocalTime?>?): SqParameter<List<LocalTime?>?, List<Time>> =
    this.parameter(this.dataTypes.jLocalTimeList, value)

@JvmName("parameter__offsetDateTimeList__notNull")
fun SqPgParameterNs.parameter(value: List<OffsetDateTime?>): SqParameter<List<OffsetDateTime?>, List<Timestamp>> =
    this.parameter(this.dataTypes.jOffsetDateTimeList, value)
@JvmName("parameter__offsetDateTimeList__nullable")
fun SqPgParameterNs.parameter(value: List<OffsetDateTime?>?): SqParameter<List<OffsetDateTime?>?, List<Timestamp>> =
    this.parameter(this.dataTypes.jOffsetDateTimeList, value)

@JvmName("parameter__offsetTimeList__notNull")
fun SqPgParameterNs.parameter(value: List<OffsetTime?>): SqParameter<List<OffsetTime?>, List<Time>> =
    this.parameter(this.dataTypes.jOffsetTimeList, value)
@JvmName("parameter__offsetTimeList__nullable")
fun SqPgParameterNs.parameter(value: List<OffsetTime?>?): SqParameter<List<OffsetTime?>?, List<Time>> =
    this.parameter(this.dataTypes.jOffsetTimeList, value)

@JvmName("parameter__timeList__notNull")
fun SqPgParameterNs.parameter(value: List<Time?>): SqParameter<List<Time?>, List<Time>> =
    this.parameter(this.dataTypes.jTimeList, value)
@JvmName("parameter__timeList__nullable")
fun SqPgParameterNs.parameter(value: List<Time?>?): SqParameter<List<Time?>?, List<Time>> =
    this.parameter(this.dataTypes.jTimeList, value)

@JvmName("parameter__timestampList__notNull")
fun SqPgParameterNs.parameter(value: List<Timestamp?>): SqParameter<List<Timestamp?>, List<Timestamp>> =
    this.parameter(this.dataTypes.jTimestampList, value)
@JvmName("parameter__timestampList__nullable")
fun SqPgParameterNs.parameter(value: List<Timestamp?>?): SqParameter<List<Timestamp?>?, List<Timestamp>> =
    this.parameter(this.dataTypes.jTimestampList, value)
// endregion


// region Parameters / other
@JvmName("pgJson__notNull")
fun SqPgParameterNs.pgJson(value: String): SqParameter<String, String> =
    this.parameter(this.dataTypes.pgJson, value)
@JvmName("pgJson__nullable")
fun SqPgParameterNs.pgJson(value: String?): SqParameter<String?, String> =
    this.parameter(this.dataTypes.pgJson, value)

@JvmName("pgJsonArray__notNull")
fun SqPgParameterNs.pgJsonArray(value: List<String?>): SqParameter<List<String?>, List<String>> =
    this.parameter(this.dataTypes.pgJsonArray, value)
@JvmName("pgJsonArray__nullable")
fun SqPgParameterNs.pgJsonArray(value: List<String?>?): SqParameter<List<String?>?, List<String>> =
    this.parameter(this.dataTypes.pgJsonArray, value)

@JvmName("pgJsonB__notNull")
fun SqPgParameterNs.pgJsonB(value: String): SqParameter<String, String> =
    this.parameter(this.dataTypes.pgJsonB, value)
@JvmName("pgJsonB__nullable")
fun SqPgParameterNs.pgJsonB(value: String?): SqParameter<String?, String> =
    this.parameter(this.dataTypes.pgJsonB, value)

@JvmName("pgJsonBArray__notNull")
fun SqPgParameterNs.pgJsonBArray(value: List<String?>): SqParameter<List<String?>, List<String>> =
    this.parameter(this.dataTypes.pgJsonBArray, value)
@JvmName("pgJsonBArray__nullable")
fun SqPgParameterNs.pgJsonBArray(value: List<String?>?): SqParameter<List<String?>?, List<String>> =
    this.parameter(this.dataTypes.pgJsonBArray, value)

@JvmName("pgXml__notNull")
fun SqPgParameterNs.pgXml(value: SQLXML): SqParameter<SQLXML, String> =
    this.parameter(this.dataTypes.pgXml, value)
@JvmName("pgXml__nullable")
fun SqPgParameterNs.pgXml(value: SQLXML?): SqParameter<SQLXML?, String> =
    this.parameter(this.dataTypes.pgXml, value)

@JvmName("pgXmlArray__notNull")
fun SqPgParameterNs.pgXmlArray(value: List<SQLXML?>): SqParameter<List<SQLXML?>, List<String>> =
    this.parameter(this.dataTypes.pgXmlArray, value)
@JvmName("pgXmlArray__nullable")
fun SqPgParameterNs.pgXmlArray(value: List<SQLXML?>?): SqParameter<List<SQLXML?>?, List<String>> =
    this.parameter(this.dataTypes.pgXmlArray, value)


@JvmName("parameter__sqlXmlList__notNull")
fun SqPgParameterNs.parameter(value: List<SQLXML?>): SqParameter<List<SQLXML?>, List<String>> =
    this.parameter(this.dataTypes.jSqlXmlList, value)
@JvmName("parameter__sqlXmlList__nullable")
fun SqPgParameterNs.parameter(value: List<SQLXML?>?): SqParameter<List<SQLXML?>?, List<String>> =
    this.parameter(this.dataTypes.jSqlXmlList, value)
// endregion


// region Thread parameters / boolean
@JvmName("pgBit__notNull")
fun SqPgThreadParameterNs.pgBit(nullFlag: Any): SqThreadParameter<SqPgBit, SqPgBit> =
    this.parameter(this.dataTypes.pgBit, nullFlag)
@JvmName("pgBit__nullable")
fun SqPgThreadParameterNs.pgBit(nullFlag: Any?): SqThreadParameter<SqPgBit?, SqPgBit> =
    this.parameter(this.dataTypes.pgBit, nullFlag)

@JvmName("pgBitArray__notNull")
fun SqPgThreadParameterNs.pgBitArray(nullFlag: Any): SqThreadParameter<List<SqPgBit?>, List<SqPgBit>> =
    this.parameter(this.dataTypes.pgBitArray, nullFlag)
@JvmName("pgBitArray__nullable")
fun SqPgThreadParameterNs.pgBitArray(nullFlag: Any?): SqThreadParameter<List<SqPgBit?>?, List<SqPgBit>> =
    this.parameter(this.dataTypes.pgBitArray, nullFlag)

@JvmName("pgBitVarying__notNull")
fun SqPgThreadParameterNs.pgBitVarying(nullFlag: Any): SqThreadParameter<SqPgBit, SqPgBit> =
    this.parameter(this.dataTypes.pgBitVarying, nullFlag)
@JvmName("pgBitVarying__nullable")
fun SqPgThreadParameterNs.pgBitVarying(nullFlag: Any?): SqThreadParameter<SqPgBit?, SqPgBit> =
    this.parameter(this.dataTypes.pgBitVarying, nullFlag)

@JvmName("pgBitVaryingArray__notNull")
fun SqPgThreadParameterNs.pgBitVaryingArray(nullFlag: Any): SqThreadParameter<List<SqPgBit?>, List<SqPgBit>> =
    this.parameter(this.dataTypes.pgBitVaryingArray, nullFlag)
@JvmName("pgBitVaryingArray__nullable")
fun SqPgThreadParameterNs.pgBitVaryingArray(nullFlag: Any?): SqThreadParameter<List<SqPgBit?>?, List<SqPgBit>> =
    this.parameter(this.dataTypes.pgBitVaryingArray, nullFlag)

@JvmName("pgBoolean__notNull")
fun SqPgThreadParameterNs.pgBoolean(nullFlag: Any): SqThreadParameter<Boolean, Boolean> =
    this.parameter(this.dataTypes.pgBoolean, nullFlag)
@JvmName("pgBoolean__nullable")
fun SqPgThreadParameterNs.pgBoolean(nullFlag: Any?): SqThreadParameter<Boolean?, Boolean> =
    this.parameter(this.dataTypes.pgBoolean, nullFlag)

@JvmName("pgBooleanArray__notNull")
fun SqPgThreadParameterNs.pgBooleanArray(nullFlag: Any): SqThreadParameter<List<Boolean?>, List<Boolean>> =
    this.parameter(this.dataTypes.pgBooleanArray, nullFlag)
@JvmName("pgBooleanArray__nullable")
fun SqPgThreadParameterNs.pgBooleanArray(nullFlag: Any?): SqThreadParameter<List<Boolean?>?, List<Boolean>> =
    this.parameter(this.dataTypes.pgBooleanArray, nullFlag)


@JvmName("jBooleanList__notNull")
fun SqPgThreadParameterNs.jBooleanList(nullFlag: Any): SqThreadParameter<List<Boolean?>, List<Boolean>> =
    this.parameter(this.dataTypes.jBooleanList, nullFlag)
@JvmName("jBooleanList__nullable")
fun SqPgThreadParameterNs.jBooleanList(nullFlag: Any?): SqThreadParameter<List<Boolean?>?, List<Boolean>> =
    this.parameter(this.dataTypes.jBooleanList, nullFlag)

@JvmName("jPgBit__notNull")
fun SqPgThreadParameterNs.jPgBit(nullFlag: Any): SqThreadParameter<SqPgBit, SqPgBit> =
    this.parameter(this.dataTypes.jPgBit, nullFlag)
@JvmName("jPgBit__nullable")
fun SqPgThreadParameterNs.jPgBit(nullFlag: Any?): SqThreadParameter<SqPgBit?, SqPgBit> =
    this.parameter(this.dataTypes.jPgBit, nullFlag)

@JvmName("jPgBitList__notNull")
fun SqPgThreadParameterNs.jPgBitList(nullFlag: Any): SqThreadParameter<List<SqPgBit?>, List<SqPgBit>> =
    this.parameter(this.dataTypes.jPgBitList, nullFlag)
@JvmName("jPgBitList__nullable")
fun SqPgThreadParameterNs.jPgBitList(nullFlag: Any?): SqThreadParameter<List<SqPgBit?>?, List<SqPgBit>> =
    this.parameter(this.dataTypes.jPgBitList, nullFlag)
// endregion


// region Thread parameters / byte array
@JvmName("pgByteA__notNull")
fun SqPgThreadParameterNs.pgByteA(nullFlag: Any): SqThreadParameter<ByteArray, ByteArray> =
    this.parameter(this.dataTypes.pgByteA, nullFlag)
@JvmName("pgByteA__nullable")
fun SqPgThreadParameterNs.pgByteA(nullFlag: Any?): SqThreadParameter<ByteArray?, ByteArray> =
    this.parameter(this.dataTypes.pgByteA, nullFlag)

@JvmName("pgByteAArray__notNull")
fun SqPgThreadParameterNs.pgByteAArray(nullFlag: Any): SqThreadParameter<List<ByteArray?>, List<ByteArray>> =
    this.parameter(this.dataTypes.pgByteAArray, nullFlag)
@JvmName("pgByteAArray__nullable")
fun SqPgThreadParameterNs.pgByteAArray(nullFlag: Any?): SqThreadParameter<List<ByteArray?>?, List<ByteArray>> =
    this.parameter(this.dataTypes.pgByteAArray, nullFlag)


@JvmName("jByteArrayList__notNull")
fun SqPgThreadParameterNs.jByteArrayList(nullFlag: Any): SqThreadParameter<List<ByteArray?>, List<ByteArray>> =
    this.parameter(this.dataTypes.jByteArrayList, nullFlag)
@JvmName("jByteArrayList__nullable")
fun SqPgThreadParameterNs.jByteArrayList(nullFlag: Any?): SqThreadParameter<List<ByteArray?>?, List<ByteArray>> =
    this.parameter(this.dataTypes.jByteArrayList, nullFlag)
// endregion


// region Thread parameters / number
@JvmName("pgBigInt__notNull")
fun SqPgThreadParameterNs.pgBigInt(nullFlag: Any): SqThreadParameter<Long, Number> =
    this.parameter(this.dataTypes.pgBigInt, nullFlag)
@JvmName("pgBigInt__nullable")
fun SqPgThreadParameterNs.pgBigInt(nullFlag: Any?): SqThreadParameter<Long?, Number> =
    this.parameter(this.dataTypes.pgBigInt, nullFlag)

@JvmName("pgBigIntArray__notNull")
fun SqPgThreadParameterNs.pgBigIntArray(nullFlag: Any): SqThreadParameter<List<Long?>, List<Number>> =
    this.parameter(this.dataTypes.pgBigIntArray, nullFlag)
@JvmName("pgBigIntArray__nullable")
fun SqPgThreadParameterNs.pgBigIntArray(nullFlag: Any?): SqThreadParameter<List<Long?>?, List<Number>> =
    this.parameter(this.dataTypes.pgBigIntArray, nullFlag)

@JvmName("pgDoublePrecision__notNull")
fun SqPgThreadParameterNs.pgDoublePrecision(nullFlag: Any): SqThreadParameter<Double, Number> =
    this.parameter(this.dataTypes.pgDoublePrecision, nullFlag)
@JvmName("pgDoublePrecision__nullable")
fun SqPgThreadParameterNs.pgDoublePrecision(nullFlag: Any?): SqThreadParameter<Double?, Number> =
    this.parameter(this.dataTypes.pgDoublePrecision, nullFlag)

@JvmName("pgDoublePrecisionArray__notNull")
fun SqPgThreadParameterNs.pgDoublePrecisionArray(nullFlag: Any): SqThreadParameter<List<Double?>, List<Number>> =
    this.parameter(this.dataTypes.pgDoublePrecisionArray, nullFlag)
@JvmName("pgDoublePrecisionArray__nullable")
fun SqPgThreadParameterNs.pgDoublePrecisionArray(nullFlag: Any?): SqThreadParameter<List<Double?>?, List<Number>> =
    this.parameter(this.dataTypes.pgDoublePrecisionArray, nullFlag)

@JvmName("pgInteger__notNull")
fun SqPgThreadParameterNs.pgInteger(nullFlag: Any): SqThreadParameter<Int, Number> =
    this.parameter(this.dataTypes.pgInteger, nullFlag)
@JvmName("pgInteger__nullable")
fun SqPgThreadParameterNs.pgInteger(nullFlag: Any?): SqThreadParameter<Int?, Number> =
    this.parameter(this.dataTypes.pgInteger, nullFlag)

@JvmName("pgIntegerArray__notNull")
fun SqPgThreadParameterNs.pgIntegerArray(nullFlag: Any): SqThreadParameter<List<Int?>, List<Number>> =
    this.parameter(this.dataTypes.pgIntegerArray, nullFlag)
@JvmName("pgIntegerArray__nullable")
fun SqPgThreadParameterNs.pgIntegerArray(nullFlag: Any?): SqThreadParameter<List<Int?>?, List<Number>> =
    this.parameter(this.dataTypes.pgIntegerArray, nullFlag)

@JvmName("pgNumeric__notNull")
fun SqPgThreadParameterNs.pgNumeric(nullFlag: Any): SqThreadParameter<BigDecimal, Number> =
    this.parameter(this.dataTypes.pgNumeric, nullFlag)
@JvmName("pgNumeric__nullable")
fun SqPgThreadParameterNs.pgNumeric(nullFlag: Any?): SqThreadParameter<BigDecimal?, Number> =
    this.parameter(this.dataTypes.pgNumeric, nullFlag)

@JvmName("pgNumericArray__notNull")
fun SqPgThreadParameterNs.pgNumericArray(nullFlag: Any): SqThreadParameter<List<BigDecimal?>, List<Number>> =
    this.parameter(this.dataTypes.pgNumericArray, nullFlag)
@JvmName("pgNumericArray__nullable")
fun SqPgThreadParameterNs.pgNumericArray(nullFlag: Any?): SqThreadParameter<List<BigDecimal?>?, List<Number>> =
    this.parameter(this.dataTypes.pgNumericArray, nullFlag)

@JvmName("pgReal__notNull")
fun SqPgThreadParameterNs.pgReal(nullFlag: Any): SqThreadParameter<Float, Number> =
    this.parameter(this.dataTypes.pgReal, nullFlag)
@JvmName("pgReal__nullable")
fun SqPgThreadParameterNs.pgReal(nullFlag: Any?): SqThreadParameter<Float?, Number> =
    this.parameter(this.dataTypes.pgReal, nullFlag)

@JvmName("pgRealArray__notNull")
fun SqPgThreadParameterNs.pgRealArray(nullFlag: Any): SqThreadParameter<List<Float?>, List<Number>> =
    this.parameter(this.dataTypes.pgRealArray, nullFlag)
@JvmName("pgRealArray__nullable")
fun SqPgThreadParameterNs.pgRealArray(nullFlag: Any?): SqThreadParameter<List<Float?>?, List<Number>> =
    this.parameter(this.dataTypes.pgRealArray, nullFlag)

@JvmName("pgSmallInt__notNull")
fun SqPgThreadParameterNs.pgSmallInt(nullFlag: Any): SqThreadParameter<Short, Number> =
    this.parameter(this.dataTypes.pgSmallInt, nullFlag)
@JvmName("pgSmallInt__nullable")
fun SqPgThreadParameterNs.pgSmallInt(nullFlag: Any?): SqThreadParameter<Short?, Number> =
    this.parameter(this.dataTypes.pgSmallInt, nullFlag)

@JvmName("pgSmallIntArray__notNull")
fun SqPgThreadParameterNs.pgSmallIntArray(nullFlag: Any): SqThreadParameter<List<Short?>, List<Number>> =
    this.parameter(this.dataTypes.pgSmallIntArray, nullFlag)
@JvmName("pgSmallIntArray__nullable")
fun SqPgThreadParameterNs.pgSmallIntArray(nullFlag: Any?): SqThreadParameter<List<Short?>?, List<Number>> =
    this.parameter(this.dataTypes.pgSmallIntArray, nullFlag)


@JvmName("jBigDecimalList__notNull")
fun SqPgThreadParameterNs.jBigDecimalList(nullFlag: Any): SqThreadParameter<List<BigDecimal?>, List<Number>> =
    this.parameter(this.dataTypes.jBigDecimalList, nullFlag)
@JvmName("jBigDecimalList__nullable")
fun SqPgThreadParameterNs.jBigDecimalList(nullFlag: Any?): SqThreadParameter<List<BigDecimal?>?, List<Number>> =
    this.parameter(this.dataTypes.jBigDecimalList, nullFlag)

@JvmName("jDoubleList__notNull")
fun SqPgThreadParameterNs.jDoubleList(nullFlag: Any): SqThreadParameter<List<Double?>, List<Number>> =
    this.parameter(this.dataTypes.jDoubleList, nullFlag)
@JvmName("jDoubleList__nullable")
fun SqPgThreadParameterNs.jDoubleList(nullFlag: Any?): SqThreadParameter<List<Double?>?, List<Number>> =
    this.parameter(this.dataTypes.jDoubleList, nullFlag)

@JvmName("jFloatList__notNull")
fun SqPgThreadParameterNs.jFloatList(nullFlag: Any): SqThreadParameter<List<Float?>, List<Number>> =
    this.parameter(this.dataTypes.jFloatList, nullFlag)
@JvmName("jFloatList__nullable")
fun SqPgThreadParameterNs.jFloatList(nullFlag: Any?): SqThreadParameter<List<Float?>?, List<Number>> =
    this.parameter(this.dataTypes.jFloatList, nullFlag)

@JvmName("jIntList__notNull")
fun SqPgThreadParameterNs.jIntList(nullFlag: Any): SqThreadParameter<List<Int?>, List<Number>> =
    this.parameter(this.dataTypes.jIntList, nullFlag)
@JvmName("jIntList__nullable")
fun SqPgThreadParameterNs.jIntList(nullFlag: Any?): SqThreadParameter<List<Int?>?, List<Number>> =
    this.parameter(this.dataTypes.jIntList, nullFlag)

@JvmName("jLongList__notNull")
fun SqPgThreadParameterNs.jLongList(nullFlag: Any): SqThreadParameter<List<Long?>, List<Number>> =
    this.parameter(this.dataTypes.jLongList, nullFlag)
@JvmName("jLongList__nullable")
fun SqPgThreadParameterNs.jLongList(nullFlag: Any?): SqThreadParameter<List<Long?>?, List<Number>> =
    this.parameter(this.dataTypes.jLongList, nullFlag)

@JvmName("jShortList__notNull")
fun SqPgThreadParameterNs.jShortList(nullFlag: Any): SqThreadParameter<List<Short?>, List<Number>> =
    this.parameter(this.dataTypes.jShortList, nullFlag)
@JvmName("jShortList__nullable")
fun SqPgThreadParameterNs.jShortList(nullFlag: Any?): SqThreadParameter<List<Short?>?, List<Number>> =
    this.parameter(this.dataTypes.jShortList, nullFlag)
// endregion


// region Thread parameters / string
@JvmName("pgChar__notNull")
fun SqPgThreadParameterNs.pgChar(nullFlag: Any): SqThreadParameter<Char, String> =
    this.parameter(this.dataTypes.pgChar, nullFlag)
@JvmName("pgChar__nullable")
fun SqPgThreadParameterNs.pgChar(nullFlag: Any?): SqThreadParameter<Char?, String> =
    this.parameter(this.dataTypes.pgChar, nullFlag)

@JvmName("pgCharArray__notNull")
fun SqPgThreadParameterNs.pgCharArray(nullFlag: Any): SqThreadParameter<List<Char?>, List<String>> =
    this.parameter(this.dataTypes.pgCharArray, nullFlag)
@JvmName("pgCharArray__nullable")
fun SqPgThreadParameterNs.pgCharArray(nullFlag: Any?): SqThreadParameter<List<Char?>?, List<String>> =
    this.parameter(this.dataTypes.pgCharArray, nullFlag)

@JvmName("pgCharacter__notNull")
fun SqPgThreadParameterNs.pgCharacter(nullFlag: Any): SqThreadParameter<String, String> =
    this.parameter(this.dataTypes.pgCharacter, nullFlag)
@JvmName("pgCharacter__nullable")
fun SqPgThreadParameterNs.pgCharacter(nullFlag: Any?): SqThreadParameter<String?, String> =
    this.parameter(this.dataTypes.pgCharacter, nullFlag)

@JvmName("pgCharacterArray__notNull")
fun SqPgThreadParameterNs.pgCharacterArray(nullFlag: Any): SqThreadParameter<List<String?>, List<String>> =
    this.parameter(this.dataTypes.pgCharacterArray, nullFlag)
@JvmName("pgCharacterArray__nullable")
fun SqPgThreadParameterNs.pgCharacterArray(nullFlag: Any?): SqThreadParameter<List<String?>?, List<String>> =
    this.parameter(this.dataTypes.pgCharacterArray, nullFlag)

@JvmName("pgCharacterVarying__notNull")
fun SqPgThreadParameterNs.pgCharacterVarying(nullFlag: Any): SqThreadParameter<String, String> =
    this.parameter(this.dataTypes.pgCharacterVarying, nullFlag)
@JvmName("pgCharacterVarying__nullable")
fun SqPgThreadParameterNs.pgCharacterVarying(nullFlag: Any?): SqThreadParameter<String?, String> =
    this.parameter(this.dataTypes.pgCharacterVarying, nullFlag)

@JvmName("pgCharacterVaryingArray__notNull")
fun SqPgThreadParameterNs.pgCharacterVaryingArray(nullFlag: Any): SqThreadParameter<List<String?>, List<String>> =
    this.parameter(this.dataTypes.pgCharacterVaryingArray, nullFlag)
@JvmName("pgCharacterVaryingArray__nullable")
fun SqPgThreadParameterNs.pgCharacterVaryingArray(nullFlag: Any?): SqThreadParameter<List<String?>?, List<String>> =
    this.parameter(this.dataTypes.pgCharacterVaryingArray, nullFlag)

@JvmName("pgText__notNull")
fun SqPgThreadParameterNs.pgText(nullFlag: Any): SqThreadParameter<String, String> =
    this.parameter(this.dataTypes.pgText, nullFlag)
@JvmName("pgText__nullable")
fun SqPgThreadParameterNs.pgText(nullFlag: Any?): SqThreadParameter<String?, String> =
    this.parameter(this.dataTypes.pgText, nullFlag)

@JvmName("pgTextArray__notNull")
fun SqPgThreadParameterNs.pgTextArray(nullFlag: Any): SqThreadParameter<List<String?>, List<String>> =
    this.parameter(this.dataTypes.pgTextArray, nullFlag)
@JvmName("pgTextArray__nullable")
fun SqPgThreadParameterNs.pgTextArray(nullFlag: Any?): SqThreadParameter<List<String?>?, List<String>> =
    this.parameter(this.dataTypes.pgTextArray, nullFlag)


@JvmName("jChar__notNull")
fun SqPgThreadParameterNs.jChar(nullFlag: Any): SqThreadParameter<Char, String> =
    this.parameter(this.dataTypes.jChar, nullFlag)
@JvmName("jChar__nullable")
fun SqPgThreadParameterNs.jChar(nullFlag: Any?): SqThreadParameter<Char?, String> =
    this.parameter(this.dataTypes.jChar, nullFlag)

@JvmName("jCharList__notNull")
fun SqPgThreadParameterNs.jCharList(nullFlag: Any): SqThreadParameter<List<Char?>, List<String>> =
    this.parameter(this.dataTypes.jCharList, nullFlag)
@JvmName("jCharList__nullable")
fun SqPgThreadParameterNs.jCharList(nullFlag: Any?): SqThreadParameter<List<Char?>?, List<String>> =
    this.parameter(this.dataTypes.jCharList, nullFlag)

@JvmName("jStringList__notNull")
fun SqPgThreadParameterNs.jStringList(nullFlag: Any): SqThreadParameter<List<String?>, List<String>> =
    this.parameter(this.dataTypes.jStringList, nullFlag)
@JvmName("jStringList__nullable")
fun SqPgThreadParameterNs.jStringList(nullFlag: Any?): SqThreadParameter<List<String?>?, List<String>> =
    this.parameter(this.dataTypes.jStringList, nullFlag)
// endregion


// region Thread parameters / temporal
@JvmName("pgDate__notNull")
fun SqPgThreadParameterNs.pgDate(nullFlag: Any): SqThreadParameter<LocalDate, Timestamp> =
    this.parameter(this.dataTypes.pgDate, nullFlag)
@JvmName("pgDate__nullable")
fun SqPgThreadParameterNs.pgDate(nullFlag: Any?): SqThreadParameter<LocalDate?, Timestamp> =
    this.parameter(this.dataTypes.pgDate, nullFlag)

@JvmName("pgDateJdbc__notNull")
fun SqPgThreadParameterNs.pgDateJdbc(nullFlag: Any): SqThreadParameter<Date, Timestamp> =
    this.parameter(this.dataTypes.pgDateJdbc, nullFlag)
@JvmName("pgDateJdbc__nullable")
fun SqPgThreadParameterNs.pgDateJdbc(nullFlag: Any?): SqThreadParameter<Date?, Timestamp> =
    this.parameter(this.dataTypes.pgDateJdbc, nullFlag)

@JvmName("pgDateArray__notNull")
fun SqPgThreadParameterNs.pgDateArray(nullFlag: Any): SqThreadParameter<List<LocalDate?>, List<Timestamp>> =
    this.parameter(this.dataTypes.pgDateArray, nullFlag)
@JvmName("pgDateArray__nullable")
fun SqPgThreadParameterNs.pgDateArray(nullFlag: Any?): SqThreadParameter<List<LocalDate?>?, List<Timestamp>> =
    this.parameter(this.dataTypes.pgDateArray, nullFlag)

@JvmName("pgDateArrayJdbc__notNull")
fun SqPgThreadParameterNs.pgDateArrayJdbc(nullFlag: Any): SqThreadParameter<List<Date?>, List<Timestamp>> =
    this.parameter(this.dataTypes.pgDateArrayJdbc, nullFlag)
@JvmName("pgDateArrayJdbc__nullable")
fun SqPgThreadParameterNs.pgDateArrayJdbc(nullFlag: Any?): SqThreadParameter<List<Date?>?, List<Timestamp>> =
    this.parameter(this.dataTypes.pgDateArrayJdbc, nullFlag)

@JvmName("pgTimestamp__notNull")
fun SqPgThreadParameterNs.pgTimestamp(nullFlag: Any): SqThreadParameter<LocalDateTime, Timestamp> =
    this.parameter(this.dataTypes.pgTimestamp, nullFlag)
@JvmName("pgTimestamp__nullable")
fun SqPgThreadParameterNs.pgTimestamp(nullFlag: Any?): SqThreadParameter<LocalDateTime?, Timestamp> =
    this.parameter(this.dataTypes.pgTimestamp, nullFlag)

@JvmName("pgTimestampJdbc__notNull")
fun SqPgThreadParameterNs.pgTimestampJdbc(nullFlag: Any): SqThreadParameter<Timestamp, Timestamp> =
    this.parameter(this.dataTypes.pgTimestampJdbc, nullFlag)
@JvmName("pgTimestampJdbc__nullable")
fun SqPgThreadParameterNs.pgTimestampJdbc(nullFlag: Any?): SqThreadParameter<Timestamp?, Timestamp> =
    this.parameter(this.dataTypes.pgTimestampJdbc, nullFlag)

@JvmName("pgTimestampArray__notNull")
fun SqPgThreadParameterNs.pgTimestampArray(nullFlag: Any): SqThreadParameter<List<LocalDateTime?>, List<Timestamp>> =
    this.parameter(this.dataTypes.pgTimestampArray, nullFlag)
@JvmName("pgTimestampArray__nullable")
fun SqPgThreadParameterNs.pgTimestampArray(nullFlag: Any?): SqThreadParameter<List<LocalDateTime?>?, List<Timestamp>> =
    this.parameter(this.dataTypes.pgTimestampArray, nullFlag)

@JvmName("pgTimestampArrayJdbc__notNull")
fun SqPgThreadParameterNs.pgTimestampArrayJdbc(nullFlag: Any): SqThreadParameter<List<Timestamp?>, List<Timestamp>> =
    this.parameter(this.dataTypes.pgTimestampArrayJdbc, nullFlag)
@JvmName("pgTimestampArrayJdbc__nullable")
fun SqPgThreadParameterNs.pgTimestampArrayJdbc(nullFlag: Any?): SqThreadParameter<List<Timestamp?>?, List<Timestamp>> =
    this.parameter(this.dataTypes.pgTimestampArrayJdbc, nullFlag)

@JvmName("pgTimestampTz__notNull")
fun SqPgThreadParameterNs.pgTimestampTz(nullFlag: Any): SqThreadParameter<OffsetDateTime, Timestamp> =
    this.parameter(this.dataTypes.pgTimestampTz, nullFlag)
@JvmName("pgTimestampTz__nullable")
fun SqPgThreadParameterNs.pgTimestampTz(nullFlag: Any?): SqThreadParameter<OffsetDateTime?, Timestamp> =
    this.parameter(this.dataTypes.pgTimestampTz, nullFlag)

@JvmName("pgTimestampTzJdbc__notNull")
fun SqPgThreadParameterNs.pgTimestampTzJdbc(nullFlag: Any): SqThreadParameter<Timestamp, Timestamp> =
    this.parameter(this.dataTypes.pgTimestampTzJdbc, nullFlag)
@JvmName("pgTimestampTzJdbc__nullable")
fun SqPgThreadParameterNs.pgTimestampTzJdbc(nullFlag: Any?): SqThreadParameter<Timestamp?, Timestamp> =
    this.parameter(this.dataTypes.pgTimestampTzJdbc, nullFlag)

@JvmName("pgTimestampTzArray__notNull")
fun SqPgThreadParameterNs.pgTimestampTzArray(nullFlag: Any): SqThreadParameter<List<OffsetDateTime?>, List<Timestamp>> =
    this.parameter(this.dataTypes.pgTimestampTzArray, nullFlag)
@JvmName("pgTimestampTzArray__nullable")
fun SqPgThreadParameterNs.pgTimestampTzArray(nullFlag: Any?): SqThreadParameter<List<OffsetDateTime?>?, List<Timestamp>> =
    this.parameter(this.dataTypes.pgTimestampTzArray, nullFlag)

@JvmName("pgTimestampTzArrayJdbc__notNull")
fun SqPgThreadParameterNs.pgTimestampTzArrayJdbc(nullFlag: Any): SqThreadParameter<List<Timestamp?>, List<Timestamp>> =
    this.parameter(this.dataTypes.pgTimestampTzArrayJdbc, nullFlag)
@JvmName("pgTimestampTzArrayJdbc__nullable")
fun SqPgThreadParameterNs.pgTimestampTzArrayJdbc(nullFlag: Any?): SqThreadParameter<List<Timestamp?>?, List<Timestamp>> =
    this.parameter(this.dataTypes.pgTimestampTzArrayJdbc, nullFlag)

@JvmName("pgTime__notNull")
fun SqPgThreadParameterNs.pgTime(nullFlag: Any): SqThreadParameter<LocalTime, Time> =
    this.parameter(this.dataTypes.pgTime, nullFlag)
@JvmName("pgTime__nullable")
fun SqPgThreadParameterNs.pgTime(nullFlag: Any?): SqThreadParameter<LocalTime?, Time> =
    this.parameter(this.dataTypes.pgTime, nullFlag)

@JvmName("pgTimeJdbc__notNull")
fun SqPgThreadParameterNs.pgTimeJdbc(nullFlag: Any): SqThreadParameter<Time, Time> =
    this.parameter(this.dataTypes.pgTimeJdbc, nullFlag)
@JvmName("pgTimeJdbc__nullable")
fun SqPgThreadParameterNs.pgTimeJdbc(nullFlag: Any?): SqThreadParameter<Time?, Time> =
    this.parameter(this.dataTypes.pgTimeJdbc, nullFlag)

@JvmName("pgTimeArray__notNull")
fun SqPgThreadParameterNs.pgTimeArray(nullFlag: Any): SqThreadParameter<List<LocalTime?>, List<Time>> =
    this.parameter(this.dataTypes.pgTimeArray, nullFlag)
@JvmName("pgTimeArray__nullable")
fun SqPgThreadParameterNs.pgTimeArray(nullFlag: Any?): SqThreadParameter<List<LocalTime?>?, List<Time>> =
    this.parameter(this.dataTypes.pgTimeArray, nullFlag)

@JvmName("pgTimeArrayJdbc__notNull")
fun SqPgThreadParameterNs.pgTimeArrayJdbc(nullFlag: Any): SqThreadParameter<List<Time?>, List<Time>> =
    this.parameter(this.dataTypes.pgTimeArrayJdbc, nullFlag)
@JvmName("pgTimeArrayJdbc__nullable")
fun SqPgThreadParameterNs.pgTimeArrayJdbc(nullFlag: Any?): SqThreadParameter<List<Time?>?, List<Time>> =
    this.parameter(this.dataTypes.pgTimeArrayJdbc, nullFlag)

@JvmName("pgTimeTz__notNull")
fun SqPgThreadParameterNs.pgTimeTz(nullFlag: Any): SqThreadParameter<OffsetTime, Time> =
    this.parameter(this.dataTypes.pgTimeTz, nullFlag)
@JvmName("pgTimeTz__nullable")
fun SqPgThreadParameterNs.pgTimeTz(nullFlag: Any?): SqThreadParameter<OffsetTime?, Time> =
    this.parameter(this.dataTypes.pgTimeTz, nullFlag)

@JvmName("pgTimeTzJdbc__notNull")
fun SqPgThreadParameterNs.pgTimeTzJdbc(nullFlag: Any): SqThreadParameter<Time, Time> =
    this.parameter(this.dataTypes.pgTimeTzJdbc, nullFlag)
@JvmName("pgTimeTzJdbc__nullable")
fun SqPgThreadParameterNs.pgTimeTzJdbc(nullFlag: Any?): SqThreadParameter<Time?, Time> =
    this.parameter(this.dataTypes.pgTimeTzJdbc, nullFlag)

@JvmName("pgTimeTzArray__notNull")
fun SqPgThreadParameterNs.pgTimeTzArray(nullFlag: Any): SqThreadParameter<List<OffsetTime?>, List<Time>> =
    this.parameter(this.dataTypes.pgTimeTzArray, nullFlag)
@JvmName("pgTimeTzArray__nullable")
fun SqPgThreadParameterNs.pgTimeTzArray(nullFlag: Any?): SqThreadParameter<List<OffsetTime?>?, List<Time>> =
    this.parameter(this.dataTypes.pgTimeTzArray, nullFlag)

@JvmName("pgTimeTzArrayJdbc__notNull")
fun SqPgThreadParameterNs.pgTimeTzArrayJdbc(nullFlag: Any): SqThreadParameter<List<Time?>, List<Time>> =
    this.parameter(this.dataTypes.pgTimeTzArrayJdbc, nullFlag)
@JvmName("pgTimeTzArrayJdbc__nullable")
fun SqPgThreadParameterNs.pgTimeTzArrayJdbc(nullFlag: Any?): SqThreadParameter<List<Time?>?, List<Time>> =
    this.parameter(this.dataTypes.pgTimeTzArrayJdbc, nullFlag)


@JvmName("jDateList__notNull")
fun SqPgThreadParameterNs.jDateList(nullFlag: Any): SqThreadParameter<List<Date?>, List<Timestamp>> =
    this.parameter(this.dataTypes.jDateList, nullFlag)
@JvmName("jDateList__nullable")
fun SqPgThreadParameterNs.jDateList(nullFlag: Any?): SqThreadParameter<List<Date?>?, List<Timestamp>> =
    this.parameter(this.dataTypes.jDateList, nullFlag)

@JvmName("jLocalDateList__notNull")
fun SqPgThreadParameterNs.jLocalDateList(nullFlag: Any): SqThreadParameter<List<LocalDate?>, List<Timestamp>> =
    this.parameter(this.dataTypes.jLocalDateList, nullFlag)
@JvmName("jLocalDateList__nullable")
fun SqPgThreadParameterNs.jLocalDateList(nullFlag: Any?): SqThreadParameter<List<LocalDate?>?, List<Timestamp>> =
    this.parameter(this.dataTypes.jLocalDateList, nullFlag)

@JvmName("jLocalDateTimeList__notNull")
fun SqPgThreadParameterNs.jLocalDateTimeList(nullFlag: Any): SqThreadParameter<List<LocalDateTime?>, List<Timestamp>> =
    this.parameter(this.dataTypes.jLocalDateTimeList, nullFlag)
@JvmName("jLocalDateTimeList__nullable")
fun SqPgThreadParameterNs.jLocalDateTimeList(nullFlag: Any?): SqThreadParameter<List<LocalDateTime?>?, List<Timestamp>> =
    this.parameter(this.dataTypes.jLocalDateTimeList, nullFlag)

@JvmName("jLocalTimeList__notNull")
fun SqPgThreadParameterNs.jLocalTimeList(nullFlag: Any): SqThreadParameter<List<LocalTime?>, List<Time>> =
    this.parameter(this.dataTypes.jLocalTimeList, nullFlag)
@JvmName("jLocalTimeList__nullable")
fun SqPgThreadParameterNs.jLocalTimeList(nullFlag: Any?): SqThreadParameter<List<LocalTime?>?, List<Time>> =
    this.parameter(this.dataTypes.jLocalTimeList, nullFlag)

@JvmName("jOffsetDateTimeList__notNull")
fun SqPgThreadParameterNs.jOffsetDateTimeList(nullFlag: Any): SqThreadParameter<List<OffsetDateTime?>, List<Timestamp>> =
    this.parameter(this.dataTypes.jOffsetDateTimeList, nullFlag)
@JvmName("jOffsetDateTimeList__nullable")
fun SqPgThreadParameterNs.jOffsetDateTimeList(nullFlag: Any?): SqThreadParameter<List<OffsetDateTime?>?, List<Timestamp>> =
    this.parameter(this.dataTypes.jOffsetDateTimeList, nullFlag)

@JvmName("jOffsetTimeList__notNull")
fun SqPgThreadParameterNs.jOffsetTimeList(nullFlag: Any): SqThreadParameter<List<OffsetTime?>, List<Time>> =
    this.parameter(this.dataTypes.jOffsetTimeList, nullFlag)
@JvmName("jOffsetTimeList__nullable")
fun SqPgThreadParameterNs.jOffsetTimeList(nullFlag: Any?): SqThreadParameter<List<OffsetTime?>?, List<Time>> =
    this.parameter(this.dataTypes.jOffsetTimeList, nullFlag)

@JvmName("jTimeList__notNull")
fun SqPgThreadParameterNs.jTimeList(nullFlag: Any): SqThreadParameter<List<Time?>, List<Time>> =
    this.parameter(this.dataTypes.jTimeList, nullFlag)
@JvmName("jTimeList__nullable")
fun SqPgThreadParameterNs.jTimeList(nullFlag: Any?): SqThreadParameter<List<Time?>?, List<Time>> =
    this.parameter(this.dataTypes.jTimeList, nullFlag)

@JvmName("jTimestampList__notNull")
fun SqPgThreadParameterNs.jTimestampList(nullFlag: Any): SqThreadParameter<List<Timestamp?>, List<Timestamp>> =
    this.parameter(this.dataTypes.jTimestampList, nullFlag)
@JvmName("jTimestampList__nullable")
fun SqPgThreadParameterNs.jTimestampList(nullFlag: Any?): SqThreadParameter<List<Timestamp?>?, List<Timestamp>> =
    this.parameter(this.dataTypes.jTimestampList, nullFlag)
// endregion


// region Thread parameters / other
@JvmName("pgJson__notNull")
fun SqPgThreadParameterNs.pgJson(nullFlag: Any): SqThreadParameter<String, String> =
    this.parameter(this.dataTypes.pgJson, nullFlag)
@JvmName("pgJson__nullable")
fun SqPgThreadParameterNs.pgJson(nullFlag: Any?): SqThreadParameter<String?, String> =
    this.parameter(this.dataTypes.pgJson, nullFlag)

@JvmName("pgJsonArray__notNull")
fun SqPgThreadParameterNs.pgJsonArray(nullFlag: Any): SqThreadParameter<List<String?>, List<String>> =
    this.parameter(this.dataTypes.pgJsonArray, nullFlag)
@JvmName("pgJsonArray__nullable")
fun SqPgThreadParameterNs.pgJsonArray(nullFlag: Any?): SqThreadParameter<List<String?>?, List<String>> =
    this.parameter(this.dataTypes.pgJsonArray, nullFlag)

@JvmName("pgJsonB__notNull")
fun SqPgThreadParameterNs.pgJsonB(nullFlag: Any): SqThreadParameter<String, String> =
    this.parameter(this.dataTypes.pgJsonB, nullFlag)
@JvmName("pgJsonB__nullable")
fun SqPgThreadParameterNs.pgJsonB(nullFlag: Any?): SqThreadParameter<String?, String> =
    this.parameter(this.dataTypes.pgJsonB, nullFlag)

@JvmName("pgJsonBArray__notNull")
fun SqPgThreadParameterNs.pgJsonBArray(nullFlag: Any): SqThreadParameter<List<String?>, List<String>> =
    this.parameter(this.dataTypes.pgJsonBArray, nullFlag)
@JvmName("pgJsonBArray__nullable")
fun SqPgThreadParameterNs.pgJsonBArray(nullFlag: Any?): SqThreadParameter<List<String?>?, List<String>> =
    this.parameter(this.dataTypes.pgJsonBArray, nullFlag)

@JvmName("pgXml__notNull")
fun SqPgThreadParameterNs.pgXml(nullFlag: Any): SqThreadParameter<SQLXML, String> =
    this.parameter(this.dataTypes.pgXml, nullFlag)
@JvmName("pgXml__nullable")
fun SqPgThreadParameterNs.pgXml(nullFlag: Any?): SqThreadParameter<SQLXML?, String> =
    this.parameter(this.dataTypes.pgXml, nullFlag)

@JvmName("pgXmlArray__notNull")
fun SqPgThreadParameterNs.pgXmlArray(nullFlag: Any): SqThreadParameter<List<SQLXML?>, List<String>> =
    this.parameter(this.dataTypes.pgXmlArray, nullFlag)
@JvmName("pgXmlArray__nullable")
fun SqPgThreadParameterNs.pgXmlArray(nullFlag: Any?): SqThreadParameter<List<SQLXML?>?, List<String>> =
    this.parameter(this.dataTypes.pgXmlArray, nullFlag)


@JvmName("jSqlXmlList__notNull")
fun SqPgThreadParameterNs.jSqlXmlList(nullFlag: Any): SqThreadParameter<List<SQLXML?>, List<String>> =
    this.parameter(this.dataTypes.jSqlXmlList, nullFlag)
@JvmName("jSqlXmlList__nullable")
fun SqPgThreadParameterNs.jSqlXmlList(nullFlag: Any?): SqThreadParameter<List<SQLXML?>?, List<String>> =
    this.parameter(this.dataTypes.jSqlXmlList, nullFlag)
// endregion


// region Table columns / boolean
@JvmName("pgBit__notNull")
fun SqPgTableColumnHolder.pgBit(name: String, nullFlag: Any): SqTableColumn<SqPgBit, SqPgBit> =
    this.add(this.types.pgBit, name, nullFlag)
@JvmName("pgBit__nullable")
fun SqPgTableColumnHolder.pgBit(name: String, nullFlag: Any?): SqTableColumn<SqPgBit?, SqPgBit> =
    this.add(this.types.pgBit, name, nullFlag)

@JvmName("pgBitArray__notNull")
fun SqPgTableColumnHolder.pgBitArray(name: String, nullFlag: Any): SqTableColumn<List<SqPgBit?>, List<SqPgBit>> =
    this.add(this.types.pgBitArray, name, nullFlag)
@JvmName("pgBitArray__nullable")
fun SqPgTableColumnHolder.pgBitArray(name: String, nullFlag: Any?): SqTableColumn<List<SqPgBit?>?, List<SqPgBit>> =
    this.add(this.types.pgBitArray, name, nullFlag)

@JvmName("pgBitVarying__notNull")
fun SqPgTableColumnHolder.pgBitVarying(name: String, nullFlag: Any): SqTableColumn<SqPgBit, SqPgBit> =
    this.add(this.types.pgBitVarying, name, nullFlag)
@JvmName("pgBitVarying__nullable")
fun SqPgTableColumnHolder.pgBitVarying(name: String, nullFlag: Any?): SqTableColumn<SqPgBit?, SqPgBit> =
    this.add(this.types.pgBitVarying, name, nullFlag)

@JvmName("pgBitVaryingArray__notNull")
fun SqPgTableColumnHolder.pgBitVaryingArray(name: String, nullFlag: Any): SqTableColumn<List<SqPgBit?>, List<SqPgBit>> =
    this.add(this.types.pgBitVaryingArray, name, nullFlag)
@JvmName("pgBitVaryingArray__nullable")
fun SqPgTableColumnHolder.pgBitVaryingArray(name: String, nullFlag: Any?): SqTableColumn<List<SqPgBit?>?, List<SqPgBit>> =
    this.add(this.types.pgBitVaryingArray, name, nullFlag)

@JvmName("pgBoolean__notNull")
fun SqPgTableColumnHolder.pgBoolean(name: String, nullFlag: Any): SqTableColumn<Boolean, Boolean> =
    this.add(this.types.pgBoolean, name, nullFlag)
@JvmName("pgBoolean__nullable")
fun SqPgTableColumnHolder.pgBoolean(name: String, nullFlag: Any?): SqTableColumn<Boolean?, Boolean> =
    this.add(this.types.pgBoolean, name, nullFlag)

@JvmName("pgBooleanArray__notNull")
fun SqPgTableColumnHolder.pgBooleanArray(name: String, nullFlag: Any): SqTableColumn<List<Boolean?>, List<Boolean>> =
    this.add(this.types.pgBooleanArray, name, nullFlag)
@JvmName("pgBooleanArray__nullable")
fun SqPgTableColumnHolder.pgBooleanArray(name: String, nullFlag: Any?): SqTableColumn<List<Boolean?>?, List<Boolean>> =
    this.add(this.types.pgBooleanArray, name, nullFlag)


@JvmName("jBooleanList__notNull")
fun SqPgTableColumnHolder.jBooleanList(name: String, nullFlag: Any): SqTableColumn<List<Boolean?>, List<Boolean>> =
    this.add(this.types.jBooleanList, name, nullFlag)
@JvmName("jBooleanList__nullable")
fun SqPgTableColumnHolder.jBooleanList(name: String, nullFlag: Any?): SqTableColumn<List<Boolean?>?, List<Boolean>> =
    this.add(this.types.jBooleanList, name, nullFlag)

@JvmName("jPgBit__notNull")
fun SqPgTableColumnHolder.jPgBit(name: String, nullFlag: Any): SqTableColumn<SqPgBit, SqPgBit> =
    this.add(this.types.jPgBit, name, nullFlag)
@JvmName("jPgBit__nullable")
fun SqPgTableColumnHolder.jPgBit(name: String, nullFlag: Any?): SqTableColumn<SqPgBit?, SqPgBit> =
    this.add(this.types.jPgBit, name, nullFlag)

@JvmName("jPgBitList__notNull")
fun SqPgTableColumnHolder.jPgBitList(name: String, nullFlag: Any): SqTableColumn<List<SqPgBit?>, List<SqPgBit>> =
    this.add(this.types.jPgBitList, name, nullFlag)
@JvmName("jPgBitList__nullable")
fun SqPgTableColumnHolder.jPgBitList(name: String, nullFlag: Any?): SqTableColumn<List<SqPgBit?>?, List<SqPgBit>> =
    this.add(this.types.jPgBitList, name, nullFlag)
// endregion


// region Table columns / byte array
@JvmName("pgByteA__notNull")
fun SqPgTableColumnHolder.pgByteA(name: String, nullFlag: Any): SqTableColumn<ByteArray, ByteArray> =
    this.add(this.types.pgByteA, name, nullFlag)
@JvmName("pgByteA__nullable")
fun SqPgTableColumnHolder.pgByteA(name: String, nullFlag: Any?): SqTableColumn<ByteArray?, ByteArray> =
    this.add(this.types.pgByteA, name, nullFlag)

@JvmName("pgByteAArray__notNull")
fun SqPgTableColumnHolder.pgByteAArray(name: String, nullFlag: Any): SqTableColumn<List<ByteArray?>, List<ByteArray>> =
    this.add(this.types.pgByteAArray, name, nullFlag)
@JvmName("pgByteAArray__nullable")
fun SqPgTableColumnHolder.pgByteAArray(name: String, nullFlag: Any?): SqTableColumn<List<ByteArray?>?, List<ByteArray>> =
    this.add(this.types.pgByteAArray, name, nullFlag)


@JvmName("jByteArrayList__notNull")
fun SqPgTableColumnHolder.jByteArrayList(name: String, nullFlag: Any): SqTableColumn<List<ByteArray?>, List<ByteArray>> =
    this.add(this.types.jByteArrayList, name, nullFlag)
@JvmName("jByteArrayList__nullable")
fun SqPgTableColumnHolder.jByteArrayList(name: String, nullFlag: Any?): SqTableColumn<List<ByteArray?>?, List<ByteArray>> =
    this.add(this.types.jByteArrayList, name, nullFlag)
// endregion


// region Table columns / number
@JvmName("pgBigInt__notNull")
fun SqPgTableColumnHolder.pgBigInt(name: String, nullFlag: Any): SqTableColumn<Long, Number> =
    this.add(this.types.pgBigInt, name, nullFlag)
@JvmName("pgBigInt__nullable")
fun SqPgTableColumnHolder.pgBigInt(name: String, nullFlag: Any?): SqTableColumn<Long?, Number> =
    this.add(this.types.pgBigInt, name, nullFlag)

@JvmName("pgBigIntArray__notNull")
fun SqPgTableColumnHolder.pgBigIntArray(name: String, nullFlag: Any): SqTableColumn<List<Long?>, List<Number>> =
    this.add(this.types.pgBigIntArray, name, nullFlag)
@JvmName("pgBigIntArray__nullable")
fun SqPgTableColumnHolder.pgBigIntArray(name: String, nullFlag: Any?): SqTableColumn<List<Long?>?, List<Number>> =
    this.add(this.types.pgBigIntArray, name, nullFlag)

@JvmName("pgDoublePrecision__notNull")
fun SqPgTableColumnHolder.pgDoublePrecision(name: String, nullFlag: Any): SqTableColumn<Double, Number> =
    this.add(this.types.pgDoublePrecision, name, nullFlag)
@JvmName("pgDoublePrecision__nullable")
fun SqPgTableColumnHolder.pgDoublePrecision(name: String, nullFlag: Any?): SqTableColumn<Double?, Number> =
    this.add(this.types.pgDoublePrecision, name, nullFlag)

@JvmName("pgDoublePrecisionArray__notNull")
fun SqPgTableColumnHolder.pgDoublePrecisionArray(name: String, nullFlag: Any): SqTableColumn<List<Double?>, List<Number>> =
    this.add(this.types.pgDoublePrecisionArray, name, nullFlag)
@JvmName("pgDoublePrecisionArray__nullable")
fun SqPgTableColumnHolder.pgDoublePrecisionArray(name: String, nullFlag: Any?): SqTableColumn<List<Double?>?, List<Number>> =
    this.add(this.types.pgDoublePrecisionArray, name, nullFlag)

@JvmName("pgInteger__notNull")
fun SqPgTableColumnHolder.pgInteger(name: String, nullFlag: Any): SqTableColumn<Int, Number> =
    this.add(this.types.pgInteger, name, nullFlag)
@JvmName("pgInteger__nullable")
fun SqPgTableColumnHolder.pgInteger(name: String, nullFlag: Any?): SqTableColumn<Int?, Number> =
    this.add(this.types.pgInteger, name, nullFlag)

@JvmName("pgIntegerArray__notNull")
fun SqPgTableColumnHolder.pgIntegerArray(name: String, nullFlag: Any): SqTableColumn<List<Int?>, List<Number>> =
    this.add(this.types.pgIntegerArray, name, nullFlag)
@JvmName("pgIntegerArray__nullable")
fun SqPgTableColumnHolder.pgIntegerArray(name: String, nullFlag: Any?): SqTableColumn<List<Int?>?, List<Number>> =
    this.add(this.types.pgIntegerArray, name, nullFlag)

@JvmName("pgNumeric__notNull")
fun SqPgTableColumnHolder.pgNumeric(name: String, nullFlag: Any): SqTableColumn<BigDecimal, Number> =
    this.add(this.types.pgNumeric, name, nullFlag)
@JvmName("pgNumeric__nullable")
fun SqPgTableColumnHolder.pgNumeric(name: String, nullFlag: Any?): SqTableColumn<BigDecimal?, Number> =
    this.add(this.types.pgNumeric, name, nullFlag)

@JvmName("pgNumericArray__notNull")
fun SqPgTableColumnHolder.pgNumericArray(name: String, nullFlag: Any): SqTableColumn<List<BigDecimal?>, List<Number>> =
    this.add(this.types.pgNumericArray, name, nullFlag)
@JvmName("pgNumericArray__nullable")
fun SqPgTableColumnHolder.pgNumericArray(name: String, nullFlag: Any?): SqTableColumn<List<BigDecimal?>?, List<Number>> =
    this.add(this.types.pgNumericArray, name, nullFlag)

@JvmName("pgReal__notNull")
fun SqPgTableColumnHolder.pgReal(name: String, nullFlag: Any): SqTableColumn<Float, Number> =
    this.add(this.types.pgReal, name, nullFlag)
@JvmName("pgReal__nullable")
fun SqPgTableColumnHolder.pgReal(name: String, nullFlag: Any?): SqTableColumn<Float?, Number> =
    this.add(this.types.pgReal, name, nullFlag)

@JvmName("pgRealArray__notNull")
fun SqPgTableColumnHolder.pgRealArray(name: String, nullFlag: Any): SqTableColumn<List<Float?>, List<Number>> =
    this.add(this.types.pgRealArray, name, nullFlag)
@JvmName("pgRealArray__nullable")
fun SqPgTableColumnHolder.pgRealArray(name: String, nullFlag: Any?): SqTableColumn<List<Float?>?, List<Number>> =
    this.add(this.types.pgRealArray, name, nullFlag)

@JvmName("pgSmallInt__notNull")
fun SqPgTableColumnHolder.pgSmallInt(name: String, nullFlag: Any): SqTableColumn<Short, Number> =
    this.add(this.types.pgSmallInt, name, nullFlag)
@JvmName("pgSmallInt__nullable")
fun SqPgTableColumnHolder.pgSmallInt(name: String, nullFlag: Any?): SqTableColumn<Short?, Number> =
    this.add(this.types.pgSmallInt, name, nullFlag)

@JvmName("pgSmallIntArray__notNull")
fun SqPgTableColumnHolder.pgSmallIntArray(name: String, nullFlag: Any): SqTableColumn<List<Short?>, List<Number>> =
    this.add(this.types.pgSmallIntArray, name, nullFlag)
@JvmName("pgSmallIntArray__nullable")
fun SqPgTableColumnHolder.pgSmallIntArray(name: String, nullFlag: Any?): SqTableColumn<List<Short?>?, List<Number>> =
    this.add(this.types.pgSmallIntArray, name, nullFlag)


@JvmName("jBigDecimalList__notNull")
fun SqPgTableColumnHolder.jBigDecimalList(name: String, nullFlag: Any): SqTableColumn<List<BigDecimal?>, List<Number>> =
    this.add(this.types.jBigDecimalList, name, nullFlag)
@JvmName("jBigDecimalList__nullable")
fun SqPgTableColumnHolder.jBigDecimalList(name: String, nullFlag: Any?): SqTableColumn<List<BigDecimal?>?, List<Number>> =
    this.add(this.types.jBigDecimalList, name, nullFlag)

@JvmName("jDoubleList__notNull")
fun SqPgTableColumnHolder.jDoubleList(name: String, nullFlag: Any): SqTableColumn<List<Double?>, List<Number>> =
    this.add(this.types.jDoubleList, name, nullFlag)
@JvmName("jDoubleList__nullable")
fun SqPgTableColumnHolder.jDoubleList(name: String, nullFlag: Any?): SqTableColumn<List<Double?>?, List<Number>> =
    this.add(this.types.jDoubleList, name, nullFlag)

@JvmName("jFloatList__notNull")
fun SqPgTableColumnHolder.jFloatList(name: String, nullFlag: Any): SqTableColumn<List<Float?>, List<Number>> =
    this.add(this.types.jFloatList, name, nullFlag)
@JvmName("jFloatList__nullable")
fun SqPgTableColumnHolder.jFloatList(name: String, nullFlag: Any?): SqTableColumn<List<Float?>?, List<Number>> =
    this.add(this.types.jFloatList, name, nullFlag)

@JvmName("jLongList__notNull")
fun SqPgTableColumnHolder.jLongList(name: String, nullFlag: Any): SqTableColumn<List<Long?>, List<Number>> =
    this.add(this.types.jLongList, name, nullFlag)
@JvmName("jLongList__nullable")
fun SqPgTableColumnHolder.jLongList(name: String, nullFlag: Any?): SqTableColumn<List<Long?>?, List<Number>> =
    this.add(this.types.jLongList, name, nullFlag)

@JvmName("jShortList__notNull")
fun SqPgTableColumnHolder.jShortList(name: String, nullFlag: Any): SqTableColumn<List<Short?>, List<Number>> =
    this.add(this.types.jShortList, name, nullFlag)
@JvmName("jShortList__nullable")
fun SqPgTableColumnHolder.jShortList(name: String, nullFlag: Any?): SqTableColumn<List<Short?>?, List<Number>> =
    this.add(this.types.jShortList, name, nullFlag)
// endregion


// region Table columns / string
@JvmName("pgChar__notNull")
fun SqPgTableColumnHolder.pgChar(name: String, nullFlag: Any): SqTableColumn<Char, String> =
    this.add(this.types.pgChar, name, nullFlag)
@JvmName("pgChar__nullable")
fun SqPgTableColumnHolder.pgChar(name: String, nullFlag: Any?): SqTableColumn<Char?, String> =
    this.add(this.types.pgChar, name, nullFlag)

@JvmName("pgCharArray__notNull")
fun SqPgTableColumnHolder.pgCharArray(name: String, nullFlag: Any): SqTableColumn<List<Char?>, List<String>> =
    this.add(this.types.pgCharArray, name, nullFlag)
@JvmName("pgCharArray__nullable")
fun SqPgTableColumnHolder.pgCharArray(name: String, nullFlag: Any?): SqTableColumn<List<Char?>?, List<String>> =
    this.add(this.types.pgCharArray, name, nullFlag)

@JvmName("pgCharacter__notNull")
fun SqPgTableColumnHolder.pgCharacter(name: String, nullFlag: Any): SqTableColumn<String, String> =
    this.add(this.types.pgCharacter, name, nullFlag)
@JvmName("pgCharacter__nullable")
fun SqPgTableColumnHolder.pgCharacter(name: String, nullFlag: Any?): SqTableColumn<String?, String> =
    this.add(this.types.pgCharacter, name, nullFlag)

@JvmName("pgCharacterArray__notNull")
fun SqPgTableColumnHolder.pgCharacterArray(name: String, nullFlag: Any): SqTableColumn<List<String?>, List<String>> =
    this.add(this.types.pgCharacterArray, name, nullFlag)
@JvmName("pgCharacterArray__nullable")
fun SqPgTableColumnHolder.pgCharacterArray(name: String, nullFlag: Any?): SqTableColumn<List<String?>?, List<String>> =
    this.add(this.types.pgCharacterArray, name, nullFlag)

@JvmName("pgCharacterVarying__notNull")
fun SqPgTableColumnHolder.pgCharacterVarying(name: String, nullFlag: Any): SqTableColumn<String, String> =
    this.add(this.types.pgCharacterVarying, name, nullFlag)
@JvmName("pgCharacterVarying__nullable")
fun SqPgTableColumnHolder.pgCharacterVarying(name: String, nullFlag: Any?): SqTableColumn<String?, String> =
    this.add(this.types.pgCharacterVarying, name, nullFlag)

@JvmName("pgCharacterVaryingArray__notNull")
fun SqPgTableColumnHolder.pgCharacterVaryingArray(name: String, nullFlag: Any): SqTableColumn<List<String?>, List<String>> =
    this.add(this.types.pgCharacterVaryingArray, name, nullFlag)
@JvmName("pgCharacterVaryingArray__nullable")
fun SqPgTableColumnHolder.pgCharacterVaryingArray(name: String, nullFlag: Any?): SqTableColumn<List<String?>?, List<String>> =
    this.add(this.types.pgCharacterVaryingArray, name, nullFlag)

@JvmName("pgText__notNull")
fun SqPgTableColumnHolder.pgText(name: String, nullFlag: Any): SqTableColumn<String, String> =
    this.add(this.types.pgText, name, nullFlag)
@JvmName("pgText__nullable")
fun SqPgTableColumnHolder.pgText(name: String, nullFlag: Any?): SqTableColumn<String?, String> =
    this.add(this.types.pgText, name, nullFlag)

@JvmName("pgTextArray__notNull")
fun SqPgTableColumnHolder.pgTextArray(name: String, nullFlag: Any): SqTableColumn<List<String?>, List<String>> =
    this.add(this.types.pgTextArray, name, nullFlag)
@JvmName("pgTextArray__nullable")
fun SqPgTableColumnHolder.pgTextArray(name: String, nullFlag: Any?): SqTableColumn<List<String?>?, List<String>> =
    this.add(this.types.pgTextArray, name, nullFlag)


@JvmName("jChar__notNull")
fun SqPgTableColumnHolder.jChar(name: String, nullFlag: Any): SqTableColumn<Char, String> =
    this.add(this.types.jChar, name, nullFlag)
@JvmName("jChar__nullable")
fun SqPgTableColumnHolder.jChar(name: String, nullFlag: Any?): SqTableColumn<Char?, String> =
    this.add(this.types.jChar, name, nullFlag)

@JvmName("jCharList__notNull")
fun SqPgTableColumnHolder.jCharList(name: String, nullFlag: Any): SqTableColumn<List<Char?>, List<String>> =
    this.add(this.types.jCharList, name, nullFlag)
@JvmName("jCharList__nullable")
fun SqPgTableColumnHolder.jCharList(name: String, nullFlag: Any?): SqTableColumn<List<Char?>?, List<String>> =
    this.add(this.types.jCharList, name, nullFlag)

@JvmName("jStringList__notNull")
fun SqPgTableColumnHolder.jStringList(name: String, nullFlag: Any): SqTableColumn<List<String?>, List<String>> =
    this.add(this.types.jStringList, name, nullFlag)
@JvmName("jStringList__nullable")
fun SqPgTableColumnHolder.jStringList(name: String, nullFlag: Any?): SqTableColumn<List<String?>?, List<String>> =
    this.add(this.types.jStringList, name, nullFlag)
// endregion


// region Table columns / temporal
@JvmName("pgDate__notNull")
fun SqPgTableColumnHolder.pgDate(name: String, nullFlag: Any): SqTableColumn<LocalDate, Timestamp> =
    this.add(this.types.pgDate, name, nullFlag)
@JvmName("pgDate__nullable")
fun SqPgTableColumnHolder.pgDate(name: String, nullFlag: Any?): SqTableColumn<LocalDate?, Timestamp> =
    this.add(this.types.pgDate, name, nullFlag)

@JvmName("pgDateJdbc__notNull")
fun SqPgTableColumnHolder.pgDateJdbc(name: String, nullFlag: Any): SqTableColumn<Date, Timestamp> =
    this.add(this.types.pgDateJdbc, name, nullFlag)
@JvmName("pgDateJdbc__nullable")
fun SqPgTableColumnHolder.pgDateJdbc(name: String, nullFlag: Any?): SqTableColumn<Date?, Timestamp> =
    this.add(this.types.pgDateJdbc, name, nullFlag)

@JvmName("pgDateArray__notNull")
fun SqPgTableColumnHolder.pgDateArray(name: String, nullFlag: Any): SqTableColumn<List<LocalDate?>, List<Timestamp>> =
    this.add(this.types.pgDateArray, name, nullFlag)
@JvmName("pgDateArray__nullable")
fun SqPgTableColumnHolder.pgDateArray(name: String, nullFlag: Any?): SqTableColumn<List<LocalDate?>?, List<Timestamp>> =
    this.add(this.types.pgDateArray, name, nullFlag)

@JvmName("pgDateArrayJdbc__notNull")
fun SqPgTableColumnHolder.pgDateArrayJdbc(name: String, nullFlag: Any): SqTableColumn<List<Date?>, List<Timestamp>> =
    this.add(this.types.pgDateArrayJdbc, name, nullFlag)
@JvmName("pgDateArrayJdbc__nullable")
fun SqPgTableColumnHolder.pgDateArrayJdbc(name: String, nullFlag: Any?): SqTableColumn<List<Date?>?, List<Timestamp>> =
    this.add(this.types.pgDateArrayJdbc, name, nullFlag)

@JvmName("pgTimestamp__notNull")
fun SqPgTableColumnHolder.pgTimestamp(name: String, nullFlag: Any): SqTableColumn<LocalDateTime, Timestamp> =
    this.add(this.types.pgTimestamp, name, nullFlag)
@JvmName("pgTimestamp__nullable")
fun SqPgTableColumnHolder.pgTimestamp(name: String, nullFlag: Any?): SqTableColumn<LocalDateTime?, Timestamp> =
    this.add(this.types.pgTimestamp, name, nullFlag)

@JvmName("pgTimestampJdbc__notNull")
fun SqPgTableColumnHolder.pgTimestampJdbc(name: String, nullFlag: Any): SqTableColumn<Timestamp, Timestamp> =
    this.add(this.types.pgTimestampJdbc, name, nullFlag)
@JvmName("pgTimestampJdbc__nullable")
fun SqPgTableColumnHolder.pgTimestampJdbc(name: String, nullFlag: Any?): SqTableColumn<Timestamp?, Timestamp> =
    this.add(this.types.pgTimestampJdbc, name, nullFlag)

@JvmName("pgTimestampArray__notNull")
fun SqPgTableColumnHolder.pgTimestampArray(name: String, nullFlag: Any): SqTableColumn<List<LocalDateTime?>, List<Timestamp>> =
    this.add(this.types.pgTimestampArray, name, nullFlag)
@JvmName("pgTimestampArray__nullable")
fun SqPgTableColumnHolder.pgTimestampArray(name: String, nullFlag: Any?): SqTableColumn<List<LocalDateTime?>?, List<Timestamp>> =
    this.add(this.types.pgTimestampArray, name, nullFlag)

@JvmName("pgTimestampArrayJdbc__notNull")
fun SqPgTableColumnHolder.pgTimestampArrayJdbc(name: String, nullFlag: Any): SqTableColumn<List<Timestamp?>, List<Timestamp>> =
    this.add(this.types.pgTimestampArrayJdbc, name, nullFlag)
@JvmName("pgTimestampArrayJdbc__nullable")
fun SqPgTableColumnHolder.pgTimestampArrayJdbc(name: String, nullFlag: Any?): SqTableColumn<List<Timestamp?>?, List<Timestamp>> =
    this.add(this.types.pgTimestampArrayJdbc, name, nullFlag)

@JvmName("pgTimestampTz__notNull")
fun SqPgTableColumnHolder.pgTimestampTz(name: String, nullFlag: Any): SqTableColumn<OffsetDateTime, Timestamp> =
    this.add(this.types.pgTimestampTz, name, nullFlag)
@JvmName("pgTimestampTz__nullable")
fun SqPgTableColumnHolder.pgTimestampTz(name: String, nullFlag: Any?): SqTableColumn<OffsetDateTime?, Timestamp> =
    this.add(this.types.pgTimestampTz, name, nullFlag)

@JvmName("pgTimestampTzJdbc__notNull")
fun SqPgTableColumnHolder.pgTimestampTzJdbc(name: String, nullFlag: Any): SqTableColumn<Timestamp, Timestamp> =
    this.add(this.types.pgTimestampTzJdbc, name, nullFlag)
@JvmName("pgTimestampTzJdbc__nullable")
fun SqPgTableColumnHolder.pgTimestampTzJdbc(name: String, nullFlag: Any?): SqTableColumn<Timestamp?, Timestamp> =
    this.add(this.types.pgTimestampTzJdbc, name, nullFlag)

@JvmName("pgTimestampTzArray__notNull")
fun SqPgTableColumnHolder.pgTimestampTzArray(name: String, nullFlag: Any): SqTableColumn<List<OffsetDateTime?>, List<Timestamp>> =
    this.add(this.types.pgTimestampTzArray, name, nullFlag)
@JvmName("pgTimestampTzArray__nullable")
fun SqPgTableColumnHolder.pgTimestampTzArray(name: String, nullFlag: Any?): SqTableColumn<List<OffsetDateTime?>?, List<Timestamp>> =
    this.add(this.types.pgTimestampTzArray, name, nullFlag)

@JvmName("pgTimestampTzArrayJdbc__notNull")
fun SqPgTableColumnHolder.pgTimestampTzArrayJdbc(name: String, nullFlag: Any): SqTableColumn<List<Timestamp?>, List<Timestamp>> =
    this.add(this.types.pgTimestampTzArrayJdbc, name, nullFlag)
@JvmName("pgTimestampTzArrayJdbc__nullable")
fun SqPgTableColumnHolder.pgTimestampTzArrayJdbc(name: String, nullFlag: Any?): SqTableColumn<List<Timestamp?>?, List<Timestamp>> =
    this.add(this.types.pgTimestampTzArrayJdbc, name, nullFlag)

@JvmName("pgTime__notNull")
fun SqPgTableColumnHolder.pgTime(name: String, nullFlag: Any): SqTableColumn<LocalTime, Time> =
    this.add(this.types.pgTime, name, nullFlag)
@JvmName("pgTime__nullable")
fun SqPgTableColumnHolder.pgTime(name: String, nullFlag: Any?): SqTableColumn<LocalTime?, Time> =
    this.add(this.types.pgTime, name, nullFlag)

@JvmName("pgTimeJdbc__notNull")
fun SqPgTableColumnHolder.pgTimeJdbc(name: String, nullFlag: Any): SqTableColumn<Time, Time> =
    this.add(this.types.pgTimeJdbc, name, nullFlag)
@JvmName("pgTimeJdbc__nullable")
fun SqPgTableColumnHolder.pgTimeJdbc(name: String, nullFlag: Any?): SqTableColumn<Time?, Time> =
    this.add(this.types.pgTimeJdbc, name, nullFlag)

@JvmName("pgTimeArray__notNull")
fun SqPgTableColumnHolder.pgTimeArray(name: String, nullFlag: Any): SqTableColumn<List<LocalTime?>, List<Time>> =
    this.add(this.types.pgTimeArray, name, nullFlag)
@JvmName("pgTimeArray__nullable")
fun SqPgTableColumnHolder.pgTimeArray(name: String, nullFlag: Any?): SqTableColumn<List<LocalTime?>?, List<Time>> =
    this.add(this.types.pgTimeArray, name, nullFlag)

@JvmName("pgTimeArrayJdbc__notNull")
fun SqPgTableColumnHolder.pgTimeArrayJdbc(name: String, nullFlag: Any): SqTableColumn<List<Time?>, List<Time>> =
    this.add(this.types.pgTimeArrayJdbc, name, nullFlag)
@JvmName("pgTimeArrayJdbc__nullable")
fun SqPgTableColumnHolder.pgTimeArrayJdbc(name: String, nullFlag: Any?): SqTableColumn<List<Time?>?, List<Time>> =
    this.add(this.types.pgTimeArrayJdbc, name, nullFlag)

@JvmName("pgTimeTz__notNull")
fun SqPgTableColumnHolder.pgTimeTz(name: String, nullFlag: Any): SqTableColumn<OffsetTime, Time> =
    this.add(this.types.pgTimeTz, name, nullFlag)
@JvmName("pgTimeTz__nullable")
fun SqPgTableColumnHolder.pgTimeTz(name: String, nullFlag: Any?): SqTableColumn<OffsetTime?, Time> =
    this.add(this.types.pgTimeTz, name, nullFlag)

@JvmName("pgTimeTzJdbc__notNull")
fun SqPgTableColumnHolder.pgTimeTzJdbc(name: String, nullFlag: Any): SqTableColumn<Time, Time> =
    this.add(this.types.pgTimeTzJdbc, name, nullFlag)
@JvmName("pgTimeTzJdbc__nullable")
fun SqPgTableColumnHolder.pgTimeTzJdbc(name: String, nullFlag: Any?): SqTableColumn<Time?, Time> =
    this.add(this.types.pgTimeTzJdbc, name, nullFlag)

@JvmName("pgTimeTzArray__notNull")
fun SqPgTableColumnHolder.pgTimeTzArray(name: String, nullFlag: Any): SqTableColumn<List<OffsetTime?>, List<Time>> =
    this.add(this.types.pgTimeTzArray, name, nullFlag)
@JvmName("pgTimeTzArray__nullable")
fun SqPgTableColumnHolder.pgTimeTzArray(name: String, nullFlag: Any?): SqTableColumn<List<OffsetTime?>?, List<Time>> =
    this.add(this.types.pgTimeTzArray, name, nullFlag)

@JvmName("pgTimeTzArrayJdbc__notNull")
fun SqPgTableColumnHolder.pgTimeTzArrayJdbc(name: String, nullFlag: Any): SqTableColumn<List<Time?>, List<Time>> =
    this.add(this.types.pgTimeTzArrayJdbc, name, nullFlag)
@JvmName("pgTimeTzArrayJdbc__nullable")
fun SqPgTableColumnHolder.pgTimeTzArrayJdbc(name: String, nullFlag: Any?): SqTableColumn<List<Time?>?, List<Time>> =
    this.add(this.types.pgTimeTzArrayJdbc, name, nullFlag)


@JvmName("jDateList__notNull")
fun SqPgTableColumnHolder.jDateList(name: String, nullFlag: Any): SqTableColumn<List<Date?>, List<Timestamp>> =
    this.add(this.types.jDateList, name, nullFlag)
@JvmName("jDateList__nullable")
fun SqPgTableColumnHolder.jDateList(name: String, nullFlag: Any?): SqTableColumn<List<Date?>?, List<Timestamp>> =
    this.add(this.types.jDateList, name, nullFlag)

@JvmName("jLocalDateList__notNull")
fun SqPgTableColumnHolder.jLocalDateList(name: String, nullFlag: Any): SqTableColumn<List<LocalDate?>, List<Timestamp>> =
    this.add(this.types.jLocalDateList, name, nullFlag)
@JvmName("jLocalDateList__nullable")
fun SqPgTableColumnHolder.jLocalDateList(name: String, nullFlag: Any?): SqTableColumn<List<LocalDate?>?, List<Timestamp>> =
    this.add(this.types.jLocalDateList, name, nullFlag)

@JvmName("jLocalDateTimeList__notNull")
fun SqPgTableColumnHolder.jLocalDateTimeList(name: String, nullFlag: Any): SqTableColumn<List<LocalDateTime?>, List<Timestamp>> =
    this.add(this.types.jLocalDateTimeList, name, nullFlag)
@JvmName("jLocalDateTimeList__nullable")
fun SqPgTableColumnHolder.jLocalDateTimeList(name: String, nullFlag: Any?): SqTableColumn<List<LocalDateTime?>?, List<Timestamp>> =
    this.add(this.types.jLocalDateTimeList, name, nullFlag)

@JvmName("jLocalTimeList__notNull")
fun SqPgTableColumnHolder.jLocalTimeList(name: String, nullFlag: Any): SqTableColumn<List<LocalTime?>, List<Time>> =
    this.add(this.types.jLocalTimeList, name, nullFlag)
@JvmName("jLocalTimeList__nullable")
fun SqPgTableColumnHolder.jLocalTimeList(name: String, nullFlag: Any?): SqTableColumn<List<LocalTime?>?, List<Time>> =
    this.add(this.types.jLocalTimeList, name, nullFlag)

@JvmName("jOffsetDateTimeList__notNull")
fun SqPgTableColumnHolder.jOffsetDateTimeList(name: String, nullFlag: Any): SqTableColumn<List<OffsetDateTime?>, List<Timestamp>> =
    this.add(this.types.jOffsetDateTimeList, name, nullFlag)
@JvmName("jOffsetDateTimeList__nullable")
fun SqPgTableColumnHolder.jOffsetDateTimeList(name: String, nullFlag: Any?): SqTableColumn<List<OffsetDateTime?>?, List<Timestamp>> =
    this.add(this.types.jOffsetDateTimeList, name, nullFlag)

@JvmName("jOffsetTimeList__notNull")
fun SqPgTableColumnHolder.jOffsetTimeList(name: String, nullFlag: Any): SqTableColumn<List<OffsetTime?>, List<Time>> =
    this.add(this.types.jOffsetTimeList, name, nullFlag)
@JvmName("jOffsetTimeList__nullable")
fun SqPgTableColumnHolder.jOffsetTimeList(name: String, nullFlag: Any?): SqTableColumn<List<OffsetTime?>?, List<Time>> =
    this.add(this.types.jOffsetTimeList, name, nullFlag)

@JvmName("jTimeList__notNull")
fun SqPgTableColumnHolder.jTimeList(name: String, nullFlag: Any): SqTableColumn<List<Time?>, List<Time>> =
    this.add(this.types.jTimeList, name, nullFlag)
@JvmName("jTimeList__nullable")
fun SqPgTableColumnHolder.jTimeList(name: String, nullFlag: Any?): SqTableColumn<List<Time?>?, List<Time>> =
    this.add(this.types.jTimeList, name, nullFlag)

@JvmName("jTimestampList__notNull")
fun SqPgTableColumnHolder.jTimestampList(name: String, nullFlag: Any): SqTableColumn<List<Timestamp?>, List<Timestamp>> =
    this.add(this.types.jTimestampList, name, nullFlag)
@JvmName("jTimestampList__nullable")
fun SqPgTableColumnHolder.jTimestampList(name: String, nullFlag: Any?): SqTableColumn<List<Timestamp?>?, List<Timestamp>> =
    this.add(this.types.jTimestampList, name, nullFlag)
// endregion


// region Table columns / other
@JvmName("pgJson__notNull")
fun SqPgTableColumnHolder.pgJson(name: String, nullFlag: Any): SqTableColumn<String, String> =
    this.add(this.types.pgJson, name, nullFlag)
@JvmName("pgJson__nullable")
fun SqPgTableColumnHolder.pgJson(name: String, nullFlag: Any?): SqTableColumn<String?, String> =
    this.add(this.types.pgJson, name, nullFlag)

@JvmName("pgJsonArray__notNull")
fun SqPgTableColumnHolder.pgJsonArray(name: String, nullFlag: Any): SqTableColumn<List<String?>, List<String>> =
    this.add(this.types.pgJsonArray, name, nullFlag)
@JvmName("pgJsonArray__nullable")
fun SqPgTableColumnHolder.pgJsonArray(name: String, nullFlag: Any?): SqTableColumn<List<String?>?, List<String>> =
    this.add(this.types.pgJsonArray, name, nullFlag)

@JvmName("pgJsonB__notNull")
fun SqPgTableColumnHolder.pgJsonB(name: String, nullFlag: Any): SqTableColumn<String, String> =
    this.add(this.types.pgJsonB, name, nullFlag)
@JvmName("pgJsonB__nullable")
fun SqPgTableColumnHolder.pgJsonB(name: String, nullFlag: Any?): SqTableColumn<String?, String> =
    this.add(this.types.pgJsonB, name, nullFlag)

@JvmName("pgJsonBArray__notNull")
fun SqPgTableColumnHolder.pgJsonBArray(name: String, nullFlag: Any): SqTableColumn<List<String?>, List<String>> =
    this.add(this.types.pgJsonBArray, name, nullFlag)
@JvmName("pgJsonBArray__nullable")
fun SqPgTableColumnHolder.pgJsonBArray(name: String, nullFlag: Any?): SqTableColumn<List<String?>?, List<String>> =
    this.add(this.types.pgJsonBArray, name, nullFlag)

@JvmName("pgXml__notNull")
fun SqPgTableColumnHolder.pgXml(name: String, nullFlag: Any): SqTableColumn<SQLXML, String> =
    this.add(this.types.pgXml, name, nullFlag)
@JvmName("pgXml__nullable")
fun SqPgTableColumnHolder.pgXml(name: String, nullFlag: Any?): SqTableColumn<SQLXML?, String> =
    this.add(this.types.pgXml, name, nullFlag)

@JvmName("pgXmlArray__notNull")
fun SqPgTableColumnHolder.pgXmlArray(name: String, nullFlag: Any): SqTableColumn<List<SQLXML?>, List<String>> =
    this.add(this.types.pgXmlArray, name, nullFlag)
@JvmName("pgXmlArray__nullable")
fun SqPgTableColumnHolder.pgXmlArray(name: String, nullFlag: Any?): SqTableColumn<List<SQLXML?>?, List<String>> =
    this.add(this.types.pgXmlArray, name, nullFlag)


@JvmName("jSqlXmlList__notNull")
fun SqPgTableColumnHolder.jSqlXmlList(name: String, nullFlag: Any): SqTableColumn<List<SQLXML?>, List<String>> =
    this.add(this.types.jSqlXmlList, name, nullFlag)
@JvmName("jSqlXmlList__nullable")
fun SqPgTableColumnHolder.jSqlXmlList(name: String, nullFlag: Any?): SqTableColumn<List<SQLXML?>?, List<String>> =
    this.add(this.types.jSqlXmlList, name, nullFlag)
// endregion
