@file:Suppress("unused")

package me.ore.sq

import me.ore.sq.generic.SqGenericTypeHolder
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import java.sql.Blob
import java.sql.Clob
import java.sql.Date
import java.sql.NClob
import java.sql.Ref
import java.sql.RowId
import java.sql.SQLXML
import java.sql.Time
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime


interface SqTypeHolder {
    // region Boolean types
    val bit: SqType<Boolean, Boolean>
    val boolean: SqType<Boolean, Boolean>

    val javaBoolean: SqType<Boolean, Boolean>
        get() = this.boolean
    // endregion


    // region Byte array types
    val binary: SqType<ByteArray, ByteArray>
    val longVarBinary: SqType<ByteArray, ByteArray>
    val varBinary: SqType<ByteArray, ByteArray>

    val javaByteArray: SqType<ByteArray, ByteArray>
        get() = this.varBinary
    // endregion


    // region Date/time types
    val date: SqType<LocalDate, Timestamp>
    val dateAsDate: SqType<Date, Timestamp>
    val time: SqType<LocalTime, Time>
    val timeAsTime: SqType<Time, Time>
    val timeWithTimeZone: SqType<OffsetTime, Time>
    val timestamp: SqType<LocalDateTime, Timestamp>
    val timestampAsTimestamp: SqType<Timestamp, Timestamp>
    val timestampWithTimeZone: SqType<OffsetDateTime, Timestamp>

    val javaDate: SqType<Date, Timestamp>
        get() = this.dateAsDate
    val javaLocalDate: SqType<LocalDate, Timestamp>
        get() = this.date
    val javaLocalDateTime: SqType<LocalDateTime, Timestamp>
        get() = this.timestamp
    val javaLocalTime: SqType<LocalTime, Time>
        get() = this.time
    val javaOffsetDateTime: SqType<OffsetDateTime, Timestamp>
        get() = this.timestampWithTimeZone
    val javaOffsetTime: SqType<OffsetTime, Time>
        get() = this.timeWithTimeZone
    val javaTime: SqType<Time, Time>
        get() = this.timeAsTime
    val javaTimestamp: SqType<Timestamp, Timestamp>
        get() = this.timestampAsTimestamp
    // endregion


    // region Number types
    val bigInt: SqType<Long, Number>
    val bigIntAsBigInteger: SqType<BigInteger, Number>
    val decimal: SqType<BigDecimal, Number>
    val double: SqType<Double, Number>
    val float: SqType<Double, Number>
    val integer: SqType<Int, Number>
    val numeric: SqType<BigDecimal, Number>
    val real: SqType<Float, Number>
    val smallInt: SqType<Short, Number>
    val tinyInt: SqType<Byte, Number>

    val javaBigDecimal: SqType<BigDecimal, Number>
        get() = this.numeric
    val javaBigInteger: SqType<BigInteger, Number>
        get() = this.bigIntAsBigInteger
    val javaByte: SqType<Byte, Number>
        get() = this.tinyInt
    val javaDouble: SqType<Double, Number>
        get() = this.double
    val javaFloat: SqType<Float, Number>
        get() = this.real
    val javaInt: SqType<Int, Number>
        get() = this.integer
    val javaLong: SqType<Long, Number>
        get() = this.bigInt
    val javaNumber: SqType<Number, Number>
    val javaShort: SqType<Short, Number>
        get() = this.smallInt
    // endregion


    // region Text types
    val char: SqType<String, String>
    val longVarChar: SqType<String, String>
    val varChar: SqType<String, String>
    val nChar: SqType<String, String>
    val nVarChar: SqType<String, String>
    val longNVarChar: SqType<String, String>

    val javaString: SqType<String, String>
        get() = this.varChar
    // endregion


    // region Blob/Clob types
    val blob: SqType<Blob, Blob>
    val clob: SqType<Clob, Clob>
    val nClob: SqType<NClob, Clob>

    val javaBlob: SqType<Blob, Blob>
        get() = this.blob
    val javaClob: SqType<Clob, Clob>
        get() = this.clob
    val javaNClob: SqType<NClob, Clob>
        get() = this.nClob
    // endregion


    // region Other JDBC types
    val dataLink: SqType<URL, String>
    val ref: SqType<Ref, Ref>
    val rowId: SqType<RowId, RowId>
    val sqlXml: SqType<SQLXML, String>

    val javaRef: SqType<Ref, Ref>
        get() = this.ref
    val javaRowId: SqType<RowId, RowId>
        get() = this.rowId
    val javaSqlXml: SqType<SQLXML, String>
        get() = this.sqlXml
    val javaUrl: SqType<URL, String>
        get() = this.dataLink
    // endregion


    // region API item types
    val booleanGroup: SqType<Boolean, Boolean>
        get() = this.boolean
    val not: SqType<Boolean, Boolean>
        get() = this.boolean
    val nullTest: SqType<Boolean, Boolean>
        get() = this.boolean
    val comparison: SqType<Boolean, Boolean>
        get() = this.boolean
    val mathOperation: SqType<Number, Number>
        get() = this.javaNumber
    // endregion
}

fun SqContext.typeHolder(): SqTypeHolder =
    this[SqTypeHolder::class.java, SqGenericTypeHolder]


// region Boolean types
fun SqContext.booleanType(): SqType<Boolean, Boolean> =
    this.typeHolder().boolean
@JvmName("booleanParam__not_null")
fun SqContext.booleanParam(value: Boolean): SqParameter<Boolean, Boolean> =
    this.param(this.booleanType(), value)
@JvmName("booleanParam__nullable")
fun SqContext.booleanParam(value: Boolean?): SqParameter<Boolean?, Boolean> =
    this.param(this.booleanType().nullable(), value)
fun SqContext.booleanNull(): SqNull<Boolean, Boolean> =
    this.nullItem(this.booleanType().nullable())
fun SqTable.booleanNotNull(columnName: String): SqTableColumn<Boolean, Boolean> =
    this.column(SqGenericTypeHolder.boolean, columnName)
fun SqTable.booleanNullable(columnName: String): SqTableColumn<Boolean?, Boolean> =
    this.column(SqGenericTypeHolder.boolean.nullable(), columnName)

fun SqContext.bitType(): SqType<Boolean, Boolean> =
    this.typeHolder().bit
@JvmName("bitParam__not_null")
fun SqContext.bitParam(value: Boolean): SqParameter<Boolean, Boolean> =
    this.param(this.bitType(), value)
@JvmName("bitParam__nullable")
fun SqContext.bitParam(value: Boolean?): SqParameter<Boolean?, Boolean> =
    this.param(this.bitType().nullable(), value)
fun SqContext.bitNull(): SqNull<Boolean, Boolean> =
    this.nullItem(this.bitType().nullable())
fun SqTable.bitNotNull(columnName: String): SqTableColumn<Boolean, Boolean> =
    this.column(SqGenericTypeHolder.bit, columnName)
fun SqTable.bitNullable(columnName: String): SqTableColumn<Boolean?, Boolean> =
    this.column(SqGenericTypeHolder.bit.nullable(), columnName)


fun SqContext.javaBooleanType(): SqType<Boolean, Boolean> =
    this.typeHolder().javaBoolean
@JvmName("javaBooleanParam__not_null")
fun SqContext.javaBooleanParam(value: Boolean): SqParameter<Boolean, Boolean> =
    this.param(this.javaBooleanType(), value)
@JvmName("javaBooleanParam__nullable")
fun SqContext.javaBooleanParam(value: Boolean?): SqParameter<Boolean?, Boolean> =
    this.param(this.javaBooleanType().nullable(), value)
fun SqContext.javaBooleanNull(): SqNull<Boolean, Boolean> =
    this.nullItem(this.javaBooleanType().nullable())
// endregion


// region Byte array types
fun SqContext.binaryType(): SqType<ByteArray, ByteArray> =
    this.typeHolder().binary
@JvmName("binaryParam__not_null")
fun SqContext.binaryParam(value: ByteArray): SqParameter<ByteArray, ByteArray> =
    this.param(this.binaryType(), value)
@JvmName("binaryParam__nullable")
fun SqContext.binaryParam(value: ByteArray?): SqParameter<ByteArray?, ByteArray> =
    this.param(this.binaryType().nullable(), value)
fun SqContext.binaryNull(): SqNull<ByteArray, ByteArray> =
    this.nullItem(this.binaryType().nullable())
fun SqTable.binaryNotNull(columnName: String): SqTableColumn<ByteArray, ByteArray> =
    this.column(SqGenericTypeHolder.binary, columnName)
fun SqTable.binaryNullable(columnName: String): SqTableColumn<ByteArray?, ByteArray> =
    this.column(SqGenericTypeHolder.binary.nullable(), columnName)

fun SqContext.longVarBinaryType(): SqType<ByteArray, ByteArray> =
    this.typeHolder().longVarBinary
@JvmName("longVarBinaryParam__not_null")
fun SqContext.longVarBinaryParam(value: ByteArray): SqParameter<ByteArray, ByteArray> =
    this.param(this.longVarBinaryType(), value)
@JvmName("longVarBinaryParam__nullable")
fun SqContext.longVarBinaryParam(value: ByteArray?): SqParameter<ByteArray?, ByteArray> =
    this.param(this.longVarBinaryType().nullable(), value)
fun SqContext.longVarBinaryNull(): SqNull<ByteArray, ByteArray> =
    this.nullItem(this.longVarBinaryType().nullable())
fun SqTable.longVarBinaryNotNull(columnName: String): SqTableColumn<ByteArray, ByteArray> =
    this.column(SqGenericTypeHolder.longVarBinary, columnName)
fun SqTable.longVarBinaryNullable(columnName: String): SqTableColumn<ByteArray?, ByteArray> =
    this.column(SqGenericTypeHolder.longVarBinary.nullable(), columnName)

fun SqContext.varBinaryType(): SqType<ByteArray, ByteArray> =
    this.typeHolder().varBinary
@JvmName("varBinaryParam__not_null")
fun SqContext.varBinaryParam(value: ByteArray): SqParameter<ByteArray, ByteArray> =
    this.param(this.varBinaryType(), value)
@JvmName("varBinaryParam__nullable")
fun SqContext.varBinaryParam(value: ByteArray?): SqParameter<ByteArray?, ByteArray> =
    this.param(this.varBinaryType().nullable(), value)
fun SqContext.varBinaryNull(): SqNull<ByteArray, ByteArray> =
    this.nullItem(this.varBinaryType().nullable())
fun SqTable.varBinaryNotNull(columnName: String): SqTableColumn<ByteArray, ByteArray> =
    this.column(SqGenericTypeHolder.varBinary, columnName)
fun SqTable.varBinaryNullable(columnName: String): SqTableColumn<ByteArray?, ByteArray> =
    this.column(SqGenericTypeHolder.varBinary.nullable(), columnName)


fun SqContext.javaByteArrayType(): SqType<ByteArray, ByteArray> =
    this.typeHolder().javaByteArray
@JvmName("javaByteArrayParam__not_null")
fun SqContext.javaByteArrayParam(value: ByteArray): SqParameter<ByteArray, ByteArray> =
    this.param(this.javaByteArrayType(), value)
@JvmName("javaByteArrayParam__nullable")
fun SqContext.javaByteArrayParam(value: ByteArray?): SqParameter<ByteArray?, ByteArray> =
    this.param(this.javaByteArrayType().nullable(), value)
fun SqContext.javaByteArrayNull(): SqNull<ByteArray, ByteArray> =
    this.nullItem(this.javaByteArrayType().nullable())
// endregion


// region Date/time types
fun SqContext.dateType(): SqType<LocalDate, Timestamp> =
    this.typeHolder().date
@JvmName("dateParam__not_null")
fun SqContext.dateParam(value: LocalDate): SqParameter<LocalDate, Timestamp> =
    this.param(this.dateType(), value)
@JvmName("dateParam__nullable")
fun SqContext.dateParam(value: LocalDate?): SqParameter<LocalDate?, Timestamp> =
    this.param(this.dateType().nullable(), value)
fun SqContext.dateNull(): SqNull<LocalDate, Timestamp> =
    this.nullItem(this.dateType().nullable())
fun SqTable.dateNotNull(columnName: String): SqTableColumn<LocalDate, Timestamp> =
    this.column(SqGenericTypeHolder.date, columnName)
fun SqTable.dateNullable(columnName: String): SqTableColumn<LocalDate?, Timestamp> =
    this.column(SqGenericTypeHolder.date.nullable(), columnName)

fun SqContext.dateAsDateType(): SqType<Date, Timestamp> =
    this.typeHolder().dateAsDate
@JvmName("dateAsDateParam__not_null")
fun SqContext.dateAsDateParam(value: Date): SqParameter<Date, Timestamp> =
    this.param(this.dateAsDateType(), value)
@JvmName("dateAsDateParam__nullable")
fun SqContext.dateAsDateParam(value: Date?): SqParameter<Date?, Timestamp> =
    this.param(this.dateAsDateType().nullable(), value)
fun SqContext.dateAsDateNull(): SqNull<Date, Timestamp> =
    this.nullItem(this.dateAsDateType().nullable())
fun SqTable.dateAsDateNotNull(columnName: String): SqTableColumn<Date, Timestamp> =
    this.column(SqGenericTypeHolder.dateAsDate, columnName)
fun SqTable.dateAsDateNullable(columnName: String): SqTableColumn<Date?, Timestamp> =
    this.column(SqGenericTypeHolder.dateAsDate.nullable(), columnName)

fun SqContext.timeType(): SqType<LocalTime, Time> =
    this.typeHolder().time
@JvmName("timeParam__not_null")
fun SqContext.timeParam(value: LocalTime): SqParameter<LocalTime, Time> =
    this.param(this.timeType(), value)
@JvmName("timeParam__nullable")
fun SqContext.timeParam(value: LocalTime?): SqParameter<LocalTime?, Time> =
    this.param(this.timeType().nullable(), value)
fun SqContext.timeNull(): SqNull<LocalTime, Time> =
    this.nullItem(this.timeType().nullable())
fun SqTable.timeNotNull(columnName: String): SqTableColumn<LocalTime, Time> =
    this.column(SqGenericTypeHolder.time, columnName)
fun SqTable.timeNullable(columnName: String): SqTableColumn<LocalTime?, Time> =
    this.column(SqGenericTypeHolder.time.nullable(), columnName)

fun SqContext.timeAsTimeType(): SqType<Time, Time> =
    this.typeHolder().timeAsTime
@JvmName("timeAsTimeParam__not_null")
fun SqContext.timeAsTimeParam(value: Time): SqParameter<Time, Time> =
    this.param(this.timeAsTimeType(), value)
@JvmName("timeAsTimeParam__nullable")
fun SqContext.timeAsTimeParam(value: Time?): SqParameter<Time?, Time> =
    this.param(this.timeAsTimeType().nullable(), value)
fun SqContext.timeAsTimeNull(): SqNull<Time, Time> =
    this.nullItem(this.timeAsTimeType().nullable())
fun SqTable.timeAsTimeNotNull(columnName: String): SqTableColumn<Time, Time> =
    this.column(SqGenericTypeHolder.timeAsTime, columnName)
fun SqTable.timeAsTimeNullable(columnName: String): SqTableColumn<Time?, Time> =
    this.column(SqGenericTypeHolder.timeAsTime.nullable(), columnName)

fun SqContext.timeWithTimeZoneType(): SqType<OffsetTime, Time> =
    this.typeHolder().timeWithTimeZone
@JvmName("timeWithTimeZoneParam__not_null")
fun SqContext.timeWithTimeZoneParam(value: OffsetTime): SqParameter<OffsetTime, Time> =
    this.param(this.timeWithTimeZoneType(), value)
@JvmName("timeWithTimeZoneParam__nullable")
fun SqContext.timeWithTimeZoneParam(value: OffsetTime?): SqParameter<OffsetTime?, Time> =
    this.param(this.timeWithTimeZoneType().nullable(), value)
fun SqContext.timeWithTimeZoneNull(): SqNull<OffsetTime, Time> =
    this.nullItem(this.timeWithTimeZoneType().nullable())
fun SqTable.timeWithTimeZoneNotNull(columnName: String): SqTableColumn<OffsetTime, Time> =
    this.column(SqGenericTypeHolder.timeWithTimeZone, columnName)
fun SqTable.timeWithTimeZoneNullable(columnName: String): SqTableColumn<OffsetTime?, Time> =
    this.column(SqGenericTypeHolder.timeWithTimeZone.nullable(), columnName)

fun SqContext.timestampType(): SqType<LocalDateTime, Timestamp> =
    this.typeHolder().timestamp
@JvmName("timestampParam__not_null")
fun SqContext.timestampParam(value: LocalDateTime): SqParameter<LocalDateTime, Timestamp> =
    this.param(this.timestampType(), value)
@JvmName("timestampParam__nullable")
fun SqContext.timestampParam(value: LocalDateTime?): SqParameter<LocalDateTime?, Timestamp> =
    this.param(this.timestampType().nullable(), value)
fun SqContext.timestampNull(): SqNull<LocalDateTime, Timestamp> =
    this.nullItem(this.timestampType().nullable())
fun SqTable.timestampNotNull(columnName: String): SqTableColumn<LocalDateTime, Timestamp> =
    this.column(SqGenericTypeHolder.timestamp, columnName)
fun SqTable.timestampNullable(columnName: String): SqTableColumn<LocalDateTime?, Timestamp> =
    this.column(SqGenericTypeHolder.timestamp.nullable(), columnName)

fun SqContext.timestampAsTimestampType(): SqType<Timestamp, Timestamp> =
    this.typeHolder().timestampAsTimestamp
@JvmName("timestampAsTimestampParam__not_null")
fun SqContext.timestampAsTimestampParam(value: Timestamp): SqParameter<Timestamp, Timestamp> =
    this.param(this.timestampAsTimestampType(), value)
@JvmName("timestampAsTimestampParam__nullable")
fun SqContext.timestampAsTimestampParam(value: Timestamp?): SqParameter<Timestamp?, Timestamp> =
    this.param(this.timestampAsTimestampType().nullable(), value)
fun SqContext.timestampAsTimestampNull(): SqNull<Timestamp, Timestamp> =
    this.nullItem(this.timestampAsTimestampType().nullable())
fun SqTable.timestampAsTimestampNotNull(columnName: String): SqTableColumn<Timestamp, Timestamp> =
    this.column(SqGenericTypeHolder.timestampAsTimestamp, columnName)
fun SqTable.timestampAsTimestampNullable(columnName: String): SqTableColumn<Timestamp?, Timestamp> =
    this.column(SqGenericTypeHolder.timestampAsTimestamp.nullable(), columnName)

fun SqContext.timestampWithTimeZoneType(): SqType<OffsetDateTime, Timestamp> =
    this.typeHolder().timestampWithTimeZone
@JvmName("timestampWithTimeZoneParam__not_null")
fun SqContext.timestampWithTimeZoneParam(value: OffsetDateTime): SqParameter<OffsetDateTime, Timestamp> =
    this.param(this.timestampWithTimeZoneType(), value)
@JvmName("timestampWithTimeZoneParam__nullable")
fun SqContext.timestampWithTimeZoneParam(value: OffsetDateTime?): SqParameter<OffsetDateTime?, Timestamp> =
    this.param(this.timestampWithTimeZoneType().nullable(), value)
fun SqContext.timestampWithTimeZoneNull(): SqNull<OffsetDateTime, Timestamp> =
    this.nullItem(this.timestampWithTimeZoneType().nullable())
fun SqTable.timestampWithTimeZoneNotNull(columnName: String): SqTableColumn<OffsetDateTime, Timestamp> =
    this.column(SqGenericTypeHolder.timestampWithTimeZone, columnName)
fun SqTable.timestampWithTimeZoneNullable(columnName: String): SqTableColumn<OffsetDateTime?, Timestamp> =
    this.column(SqGenericTypeHolder.timestampWithTimeZone.nullable(), columnName)


fun SqContext.javaDateType(): SqType<Date, Timestamp> =
    this.typeHolder().javaDate
@JvmName("javaDateParam__not_null")
fun SqContext.javaDateParam(value: Date): SqParameter<Date, Timestamp> =
    this.param(this.javaDateType(), value)
@JvmName("javaDateParam__nullable")
fun SqContext.javaDateParam(value: Date?): SqParameter<Date?, Timestamp> =
    this.param(this.javaDateType().nullable(), value)
fun SqContext.javaDateNull(): SqNull<Date, Timestamp> =
    this.nullItem(this.javaDateType().nullable())

fun SqContext.javaLocalDateType(): SqType<LocalDate, Timestamp> =
    this.typeHolder().javaLocalDate
@JvmName("javaLocalDateParam__not_null")
fun SqContext.javaLocalDateParam(value: LocalDate): SqParameter<LocalDate, Timestamp> =
    this.param(this.javaLocalDateType(), value)
@JvmName("javaLocalDateParam__nullable")
fun SqContext.javaLocalDateParam(value: LocalDate?): SqParameter<LocalDate?, Timestamp> =
    this.param(this.javaLocalDateType().nullable(), value)
fun SqContext.javaLocalDateNull(): SqNull<LocalDate, Timestamp> =
    this.nullItem(this.javaLocalDateType().nullable())

fun SqContext.javaLocalDateTimeType(): SqType<LocalDateTime, Timestamp> =
    this.typeHolder().javaLocalDateTime
@JvmName("javaLocalDateTimeParam__not_null")
fun SqContext.javaLocalDateTimeParam(value: LocalDateTime): SqParameter<LocalDateTime, Timestamp> =
    this.param(this.javaLocalDateTimeType(), value)
@JvmName("javaLocalDateTimeParam__nullable")
fun SqContext.javaLocalDateTimeParam(value: LocalDateTime?): SqParameter<LocalDateTime?, Timestamp> =
    this.param(this.javaLocalDateTimeType().nullable(), value)
fun SqContext.javaLocalDateTimeNull(): SqNull<LocalDateTime, Timestamp> =
    this.nullItem(this.javaLocalDateTimeType().nullable())

fun SqContext.javaLocalTimeType(): SqType<LocalTime, Time> =
    this.typeHolder().javaLocalTime
@JvmName("javaLocalTimeParam__not_null")
fun SqContext.javaLocalTimeParam(value: LocalTime): SqParameter<LocalTime, Time> =
    this.param(this.javaLocalTimeType(), value)
@JvmName("javaLocalTimeParam__nullable")
fun SqContext.javaLocalTimeParam(value: LocalTime?): SqParameter<LocalTime?, Time> =
    this.param(this.javaLocalTimeType().nullable(), value)
fun SqContext.javaLocalTimeNull(): SqNull<LocalTime, Time> =
    this.nullItem(this.javaLocalTimeType().nullable())

fun SqContext.javaOffsetDateTimeType(): SqType<OffsetDateTime, Timestamp> =
    this.typeHolder().javaOffsetDateTime
@JvmName("javaOffsetDateTimeParam__not_null")
fun SqContext.javaOffsetDateTimeParam(value: OffsetDateTime): SqParameter<OffsetDateTime, Timestamp> =
    this.param(this.javaOffsetDateTimeType(), value)
@JvmName("javaOffsetDateTimeParam__nullable")
fun SqContext.javaOffsetDateTimeParam(value: OffsetDateTime?): SqParameter<OffsetDateTime?, Timestamp> =
    this.param(this.javaOffsetDateTimeType().nullable(), value)
fun SqContext.javaOffsetDateTimeNull(): SqNull<OffsetDateTime, Timestamp> =
    this.nullItem(this.javaOffsetDateTimeType().nullable())

fun SqContext.javaOffsetTimeType(): SqType<OffsetTime, Time> =
    this.typeHolder().javaOffsetTime
@JvmName("javaOffsetTimeParam__not_null")
fun SqContext.javaOffsetTimeParam(value: OffsetTime): SqParameter<OffsetTime, Time> =
    this.param(this.javaOffsetTimeType(), value)
@JvmName("javaOffsetTimeParam__nullable")
fun SqContext.javaOffsetTimeParam(value: OffsetTime?): SqParameter<OffsetTime?, Time> =
    this.param(this.javaOffsetTimeType().nullable(), value)
fun SqContext.javaOffsetTimeNull(): SqNull<OffsetTime, Time> =
    this.nullItem(this.javaOffsetTimeType().nullable())

fun SqContext.javaTimeType(): SqType<Time, Time> =
    this.typeHolder().javaTime
@JvmName("javaTimeParam__not_null")
fun SqContext.javaTimeParam(value: Time): SqParameter<Time, Time> =
    this.param(this.javaTimeType(), value)
@JvmName("javaTimeParam__nullable")
fun SqContext.javaTimeParam(value: Time?): SqParameter<Time?, Time> =
    this.param(this.javaTimeType().nullable(), value)
fun SqContext.javaTimeNull(): SqNull<Time, Time> =
    this.nullItem(this.javaTimeType().nullable())

fun SqContext.javaTimestampType(): SqType<Timestamp, Timestamp> =
    this.typeHolder().javaTimestamp
@JvmName("javaTimestampParam__not_null")
fun SqContext.javaTimestampParam(value: Timestamp): SqParameter<Timestamp, Timestamp> =
    this.param(this.javaTimestampType(), value)
@JvmName("javaTimestampParam__nullable")
fun SqContext.javaTimestampParam(value: Timestamp?): SqParameter<Timestamp?, Timestamp> =
    this.param(this.javaTimestampType().nullable(), value)
fun SqContext.javaTimestampNull(): SqNull<Timestamp, Timestamp> =
    this.nullItem(this.javaTimestampType().nullable())
// endregion


// region Number types
fun SqContext.bigIntType(): SqType<Long, Number> =
    this.typeHolder().bigInt
@JvmName("bigIntParam__not_null")
fun SqContext.bigIntParam(value: Long): SqParameter<Long, Number> =
    this.param(this.bigIntType(), value)
@JvmName("bigIntParam__nullable")
fun SqContext.bigIntParam(value: Long?): SqParameter<Long?, Number> =
    this.param(this.bigIntType().nullable(), value)
fun SqContext.bigIntNull(): SqNull<Long, Number> =
    this.nullItem(this.bigIntType().nullable())
fun SqTable.bigIntNotNull(columnName: String): SqTableColumn<Long, Number> =
    this.column(SqGenericTypeHolder.bigInt, columnName)
fun SqTable.bigIntNullable(columnName: String): SqTableColumn<Long?, Number> =
    this.column(SqGenericTypeHolder.bigInt.nullable(), columnName)

fun SqContext.bigIntAsBigIntegerType(): SqType<BigInteger, Number> =
    this.typeHolder().bigIntAsBigInteger
@JvmName("bigIntAsBigIntegerParam__not_null")
fun SqContext.bigIntAsBigIntegerParam(value: BigInteger): SqParameter<BigInteger, Number> =
    this.param(this.bigIntAsBigIntegerType(), value)
@JvmName("bigIntAsBigIntegerParam__nullable")
fun SqContext.bigIntAsBigIntegerParam(value: BigInteger?): SqParameter<BigInteger?, Number> =
    this.param(this.bigIntAsBigIntegerType().nullable(), value)
fun SqContext.bigIntAsBigIntegerNull(): SqNull<BigInteger, Number> =
    this.nullItem(this.bigIntAsBigIntegerType().nullable())
fun SqTable.bigIntAsBigIntegerNotNull(columnName: String): SqTableColumn<BigInteger, Number> =
    this.column(SqGenericTypeHolder.bigIntAsBigInteger, columnName)
fun SqTable.bigIntAsBigIntegerNullable(columnName: String): SqTableColumn<BigInteger?, Number> =
    this.column(SqGenericTypeHolder.bigIntAsBigInteger.nullable(), columnName)

fun SqContext.decimalType(): SqType<BigDecimal, Number> =
    this.typeHolder().decimal
@JvmName("decimalParam__not_null")
fun SqContext.decimalParam(value: BigDecimal): SqParameter<BigDecimal, Number> =
    this.param(this.decimalType(), value)
@JvmName("decimalParam__nullable")
fun SqContext.decimalParam(value: BigDecimal?): SqParameter<BigDecimal?, Number> =
    this.param(this.decimalType().nullable(), value)
fun SqContext.decimalNull(): SqNull<BigDecimal, Number> =
    this.nullItem(this.decimalType().nullable())
fun SqTable.decimalNotNull(columnName: String): SqTableColumn<BigDecimal, Number> =
    this.column(SqGenericTypeHolder.decimal, columnName)
fun SqTable.decimalNullable(columnName: String): SqTableColumn<BigDecimal?, Number> =
    this.column(SqGenericTypeHolder.decimal.nullable(), columnName)

fun SqContext.doubleType(): SqType<Double, Number> =
    this.typeHolder().double
@JvmName("doubleParam__not_null")
fun SqContext.doubleParam(value: Double): SqParameter<Double, Number> =
    this.param(this.doubleType(), value)
@JvmName("doubleParam__nullable")
fun SqContext.doubleParam(value: Double?): SqParameter<Double?, Number> =
    this.param(this.doubleType().nullable(), value)
fun SqContext.doubleNull(): SqNull<Double, Number> =
    this.nullItem(this.doubleType().nullable())
fun SqTable.doubleNotNull(columnName: String): SqTableColumn<Double, Number> =
    this.column(SqGenericTypeHolder.double, columnName)
fun SqTable.doubleNullable(columnName: String): SqTableColumn<Double?, Number> =
    this.column(SqGenericTypeHolder.double.nullable(), columnName)

fun SqContext.floatType(): SqType<Double, Number> =
    this.typeHolder().float
@JvmName("floatParam__not_null")
fun SqContext.floatParam(value: Double): SqParameter<Double, Number> =
    this.param(this.floatType(), value)
@JvmName("floatParam__nullable")
fun SqContext.floatParam(value: Double?): SqParameter<Double?, Number> =
    this.param(this.floatType().nullable(), value)
fun SqContext.floatNull(): SqNull<Double, Number> =
    this.nullItem(this.floatType().nullable())
fun SqTable.floatNotNull(columnName: String): SqTableColumn<Double, Number> =
    this.column(SqGenericTypeHolder.float, columnName)
fun SqTable.floatNullable(columnName: String): SqTableColumn<Double?, Number> =
    this.column(SqGenericTypeHolder.float.nullable(), columnName)

fun SqContext.integerType(): SqType<Int, Number> =
    this.typeHolder().integer
@JvmName("integerParam__not_null")
fun SqContext.integerParam(value: Int): SqParameter<Int, Number> =
    this.param(this.integerType(), value)
@JvmName("integerParam__nullable")
fun SqContext.integerParam(value: Int?): SqParameter<Int?, Number> =
    this.param(this.integerType().nullable(), value)
fun SqContext.integerNull(): SqNull<Int, Number> =
    this.nullItem(this.integerType().nullable())
fun SqTable.integerNotNull(columnName: String): SqTableColumn<Int, Number> =
    this.column(SqGenericTypeHolder.integer, columnName)
fun SqTable.integerNullable(columnName: String): SqTableColumn<Int?, Number> =
    this.column(SqGenericTypeHolder.integer.nullable(), columnName)


fun SqContext.numericType(): SqType<BigDecimal, Number> =
    this.typeHolder().numeric
@JvmName("numericParam__not_null")
fun SqContext.numericParam(value: BigDecimal): SqParameter<BigDecimal, Number> =
    this.param(this.numericType(), value)
@JvmName("numericParam__nullable")
fun SqContext.numericParam(value: BigDecimal?): SqParameter<BigDecimal?, Number> =
    this.param(this.numericType().nullable(), value)
fun SqContext.numericNull(): SqNull<BigDecimal, Number> =
    this.nullItem(this.numericType().nullable())
fun SqTable.numericNotNull(columnName: String): SqTableColumn<BigDecimal, Number> =
    this.column(SqGenericTypeHolder.numeric, columnName)
fun SqTable.numericNullable(columnName: String): SqTableColumn<BigDecimal?, Number> =
    this.column(SqGenericTypeHolder.numeric.nullable(), columnName)

fun SqContext.realType(): SqType<Float, Number> =
    this.typeHolder().real
@JvmName("realParam__not_null")
fun SqContext.realParam(value: Float): SqParameter<Float, Number> =
    this.param(this.realType(), value)
@JvmName("realParam__nullable")
fun SqContext.realParam(value: Float?): SqParameter<Float?, Number> =
    this.param(this.realType().nullable(), value)
fun SqContext.realNull(): SqNull<Float, Number> =
    this.nullItem(this.realType().nullable())
fun SqTable.realNotNull(columnName: String): SqTableColumn<Float, Number> =
    this.column(SqGenericTypeHolder.real, columnName)
fun SqTable.realNullable(columnName: String): SqTableColumn<Float?, Number> =
    this.column(SqGenericTypeHolder.real.nullable(), columnName)

fun SqContext.smallIntType(): SqType<Short, Number> =
    this.typeHolder().smallInt
@JvmName("smallIntParam__not_null")
fun SqContext.smallIntParam(value: Short): SqParameter<Short, Number> =
    this.param(this.smallIntType(), value)
@JvmName("smallIntParam__nullable")
fun SqContext.smallIntParam(value: Short?): SqParameter<Short?, Number> =
    this.param(this.smallIntType().nullable(), value)
fun SqContext.smallIntNull(): SqNull<Short, Number> =
    this.nullItem(this.smallIntType().nullable())
fun SqTable.smallIntNotNull(columnName: String): SqTableColumn<Short, Number> =
    this.column(SqGenericTypeHolder.smallInt, columnName)
fun SqTable.smallIntNullable(columnName: String): SqTableColumn<Short?, Number> =
    this.column(SqGenericTypeHolder.smallInt.nullable(), columnName)

fun SqContext.tinyIntType(): SqType<Byte, Number> =
    this.typeHolder().tinyInt
@JvmName("tinyIntParam__not_null")
fun SqContext.tinyIntParam(value: Byte): SqParameter<Byte, Number> =
    this.param(this.tinyIntType(), value)
@JvmName("tinyIntParam__nullable")
fun SqContext.tinyIntParam(value: Byte?): SqParameter<Byte?, Number> =
    this.param(this.tinyIntType().nullable(), value)
fun SqContext.tinyIntNull(): SqNull<Byte, Number> =
    this.nullItem(this.tinyIntType().nullable())
fun SqTable.tinyIntNotNull(columnName: String): SqTableColumn<Byte, Number> =
    this.column(SqGenericTypeHolder.tinyInt, columnName)
fun SqTable.tinyIntNullable(columnName: String): SqTableColumn<Byte?, Number> =
    this.column(SqGenericTypeHolder.tinyInt.nullable(), columnName)


fun SqContext.javaBigDecimalType(): SqType<BigDecimal, Number> =
    this.typeHolder().javaBigDecimal
@JvmName("javaBigDecimalParam__not_null")
fun SqContext.javaBigDecimalParam(value: BigDecimal): SqParameter<BigDecimal, Number> =
    this.param(this.javaBigDecimalType(), value)
@JvmName("javaBigDecimalParam__nullable")
fun SqContext.javaBigDecimalParam(value: BigDecimal?): SqParameter<BigDecimal?, Number> =
    this.param(this.javaBigDecimalType().nullable(), value)
fun SqContext.javaBigDecimalNull(): SqNull<BigDecimal, Number> =
    this.nullItem(this.javaBigDecimalType().nullable())

fun SqContext.javaBigIntegerType(): SqType<BigInteger, Number> =
    this.typeHolder().javaBigInteger
@JvmName("javaBigIntegerParam__not_null")
fun SqContext.javaBigIntegerParam(value: BigInteger): SqParameter<BigInteger, Number> =
    this.param(this.javaBigIntegerType(), value)
@JvmName("javaBigIntegerParam__nullable")
fun SqContext.javaBigIntegerParam(value: BigInteger?): SqParameter<BigInteger?, Number> =
    this.param(this.javaBigIntegerType().nullable(), value)
fun SqContext.javaBigIntegerNull(): SqNull<BigInteger, Number> =
    this.nullItem(this.javaBigIntegerType().nullable())

fun SqContext.javaByteType(): SqType<Byte, Number> =
    this.typeHolder().javaByte
@JvmName("javaByteParam__not_null")
fun SqContext.javaByteParam(value: Byte): SqParameter<Byte, Number> =
    this.param(this.javaByteType(), value)
@JvmName("javaByteParam__nullable")
fun SqContext.javaByteParam(value: Byte?): SqParameter<Byte?, Number> =
    this.param(this.javaByteType().nullable(), value)
fun SqContext.javaByteNull(): SqNull<Byte, Number> =
    this.nullItem(this.javaByteType().nullable())

fun SqContext.javaDoubleType(): SqType<Double, Number> =
    this.typeHolder().double
@JvmName("javaDoubleParam__not_null")
fun SqContext.javaDoubleParam(value: Double): SqParameter<Double, Number> =
    this.param(this.javaDoubleType(), value)
@JvmName("javaDoubleParam__nullable")
fun SqContext.javaDoubleParam(value: Double?): SqParameter<Double?, Number> =
    this.param(this.javaDoubleType().nullable(), value)
fun SqContext.javaDoubleNull(): SqNull<Double, Number> =
    this.nullItem(this.javaDoubleType().nullable())

fun SqContext.javaFloatType(): SqType<Float, Number> =
    this.typeHolder().javaFloat
@JvmName("javaFloatParam__not_null")
fun SqContext.javaFloatParam(value: Float): SqParameter<Float, Number> =
    this.param(this.javaFloatType(), value)
@JvmName("javaFloatParam__nullable")
fun SqContext.javaFloatParam(value: Float?): SqParameter<Float?, Number> =
    this.param(this.javaFloatType().nullable(), value)
fun SqContext.javaFloatNull(): SqNull<Float, Number> =
    this.nullItem(this.javaFloatType().nullable())

fun SqContext.javaIntType(): SqType<Int, Number> =
    this.typeHolder().javaInt
@JvmName("javaIntParam__not_null")
fun SqContext.javaIntParam(value: Int): SqParameter<Int, Number> =
    this.param(this.javaIntType(), value)
@JvmName("javaIntParam__nullable")
fun SqContext.javaIntParam(value: Int?): SqParameter<Int?, Number> =
    this.param(this.javaIntType().nullable(), value)
fun SqContext.javaIntNull(): SqNull<Int, Number> =
    this.nullItem(this.javaIntType().nullable())

fun SqContext.javaLongType(): SqType<Long, Number> =
    this.typeHolder().javaLong
@JvmName("javaLongParam__not_null")
fun SqContext.javaLongParam(value: Long): SqParameter<Long, Number> =
    this.param(this.javaLongType(), value)
@JvmName("javaLongParam__nullable")
fun SqContext.javaLongParam(value: Long?): SqParameter<Long?, Number> =
    this.param(this.javaLongType().nullable(), value)
fun SqContext.javaLongNull(): SqNull<Long, Number> =
    this.nullItem(this.javaLongType().nullable())

fun SqContext.javaNumberType(): SqType<Number, Number> =
    this.typeHolder().javaNumber
@JvmName("javaNumberParam__not_null")
fun SqContext.javaNumberParam(value: Number): SqParameter<Number, Number> =
    this.param(this.javaNumberType(), value)
@JvmName("javaNumberParam__nullable")
fun SqContext.javaNumberParam(value: Number?): SqParameter<Number?, Number> =
    this.param(this.javaNumberType().nullable(), value)
fun SqContext.javaNumberNull(): SqNull<Number, Number> =
    this.nullItem(this.javaNumberType().nullable())

fun SqContext.javaShortType(): SqType<Short, Number> =
    this.typeHolder().javaShort
@JvmName("javaShortParam__not_null")
fun SqContext.javaShortParam(value: Short): SqParameter<Short, Number> =
    this.param(this.javaShortType(), value)
@JvmName("javaShortParam__nullable")
fun SqContext.javaShortParam(value: Short?): SqParameter<Short?, Number> =
    this.param(this.javaShortType().nullable(), value)
fun SqContext.javaShortNull(): SqNull<Short, Number> =
    this.nullItem(this.javaShortType().nullable())
// endregion


// region Text types
fun SqContext.charType(): SqType<String, String> =
    this.typeHolder().char
@JvmName("charParam__not_null")
fun SqContext.charParam(value: String): SqParameter<String, String> =
    this.param(this.charType(), value)
@JvmName("charParam__nullable")
fun SqContext.charParam(value: String?): SqParameter<String?, String> =
    this.param(this.charType().nullable(), value)
fun SqContext.charNull(): SqNull<String, String> =
    this.nullItem(this.charType().nullable())
fun SqTable.charNotNull(columnName: String): SqTableColumn<String, String> =
    this.column(SqGenericTypeHolder.char, columnName)
fun SqTable.charNullable(columnName: String): SqTableColumn<String?, String> =
    this.column(SqGenericTypeHolder.char.nullable(), columnName)

fun SqContext.longVarCharType(): SqType<String, String> =
    this.typeHolder().longVarChar
@JvmName("longVarCharParam__not_null")
fun SqContext.longVarCharParam(value: String): SqParameter<String, String> =
    this.param(this.longVarCharType(), value)
@JvmName("longVarCharParam__nullable")
fun SqContext.longVarCharParam(value: String?): SqParameter<String?, String> =
    this.param(this.longVarCharType().nullable(), value)
fun SqContext.longVarCharNull(): SqNull<String, String> =
    this.nullItem(this.longVarCharType().nullable())
fun SqTable.longVarCharNotNull(columnName: String): SqTableColumn<String, String> =
    this.column(SqGenericTypeHolder.longVarChar, columnName)
fun SqTable.longVarCharNullable(columnName: String): SqTableColumn<String?, String> =
    this.column(SqGenericTypeHolder.longVarChar.nullable(), columnName)

fun SqContext.varCharType(): SqType<String, String> =
    this.typeHolder().varChar
@JvmName("varCharParam__not_null")
fun SqContext.varCharParam(value: String): SqParameter<String, String> =
    this.param(this.varCharType(), value)
@JvmName("varCharParam__nullable")
fun SqContext.varCharParam(value: String?): SqParameter<String?, String> =
    this.param(this.varCharType().nullable(), value)
fun SqContext.varCharNull(): SqNull<String, String> =
    this.nullItem(this.varCharType().nullable())
fun SqTable.varCharNotNull(columnName: String): SqTableColumn<String, String> =
    this.column(SqGenericTypeHolder.varChar, columnName)
fun SqTable.varCharNullable(columnName: String): SqTableColumn<String?, String> =
    this.column(SqGenericTypeHolder.varChar.nullable(), columnName)

fun SqContext.nCharType(): SqType<String, String> =
    this.typeHolder().nChar
@JvmName("nCharParam__not_null")
fun SqContext.nCharParam(value: String): SqParameter<String, String> =
    this.param(this.nCharType(), value)
@JvmName("nCharParam__nullable")
fun SqContext.nCharParam(value: String?): SqParameter<String?, String> =
    this.param(this.nCharType().nullable(), value)
fun SqContext.nCharNull(): SqNull<String, String> =
    this.nullItem(this.nCharType().nullable())
fun SqTable.nCharNotNull(columnName: String): SqTableColumn<String, String> =
    this.column(SqGenericTypeHolder.nChar, columnName)
fun SqTable.nCharNullable(columnName: String): SqTableColumn<String?, String> =
    this.column(SqGenericTypeHolder.nChar.nullable(), columnName)

fun SqContext.nVarCharType(): SqType<String, String> =
    this.typeHolder().nVarChar
@JvmName("nVarCharParam__not_null")
fun SqContext.nVarCharParam(value: String): SqParameter<String, String> =
    this.param(this.nVarCharType(), value)
@JvmName("nVarCharParam__nullable")
fun SqContext.nVarCharParam(value: String?): SqParameter<String?, String> =
    this.param(this.nVarCharType().nullable(), value)
fun SqContext.nVarCharNull(): SqNull<String, String> =
    this.nullItem(this.nVarCharType().nullable())
fun SqTable.nVarCharNotNull(columnName: String): SqTableColumn<String, String> =
    this.column(SqGenericTypeHolder.nVarChar, columnName)
fun SqTable.nVarCharNullable(columnName: String): SqTableColumn<String?, String> =
    this.column(SqGenericTypeHolder.nVarChar.nullable(), columnName)

fun SqContext.longNVarCharType(): SqType<String, String> =
    this.typeHolder().longNVarChar
@JvmName("longNVarCharParam__not_null")
fun SqContext.longNVarCharParam(value: String): SqParameter<String, String> =
    this.param(this.longNVarCharType(), value)
@JvmName("longNVarCharParam__nullable")
fun SqContext.longNVarCharParam(value: String?): SqParameter<String?, String> =
    this.param(this.longNVarCharType().nullable(), value)
fun SqContext.longNVarCharNull(): SqNull<String, String> =
    this.nullItem(this.longNVarCharType().nullable())
fun SqTable.longNVarCharNotNull(columnName: String): SqTableColumn<String, String> =
    this.column(SqGenericTypeHolder.longNVarChar, columnName)
fun SqTable.longNVarCharNullable(columnName: String): SqTableColumn<String?, String> =
    this.column(SqGenericTypeHolder.longNVarChar.nullable(), columnName)


fun SqContext.javaStringType(): SqType<String, String> =
    this.typeHolder().javaString
@JvmName("javaStringParam__not_null")
fun SqContext.javaStringParam(value: String): SqParameter<String, String> =
    this.param(this.javaStringType(), value)
@JvmName("javaStringParam__nullable")
fun SqContext.javaStringParam(value: String?): SqParameter<String?, String> =
    this.param(this.javaStringType().nullable(), value)
fun SqContext.javaStringNull(): SqNull<String, String> =
    this.nullItem(this.javaStringType().nullable())
// endregion


// region Blob/Clob types
fun SqContext.blobType(): SqType<Blob, Blob> =
    this.typeHolder().blob
@JvmName("blobParam__not_null")
fun SqContext.blobParam(value: Blob): SqParameter<Blob, Blob> =
    this.param(this.blobType(), value)
@JvmName("blobParam__nullable")
fun SqContext.blobParam(value: Blob?): SqParameter<Blob?, Blob> =
    this.param(this.blobType().nullable(), value)
fun SqContext.blobNull(): SqNull<Blob, Blob> =
    this.nullItem(this.blobType().nullable())
fun SqTable.blobNotNull(columnName: String): SqTableColumn<Blob, Blob> =
    this.column(SqGenericTypeHolder.blob, columnName)
fun SqTable.blobNullable(columnName: String): SqTableColumn<Blob?, Blob> =
    this.column(SqGenericTypeHolder.blob.nullable(), columnName)

fun SqContext.clobType(): SqType<Clob, Clob> =
    this.typeHolder().clob
@JvmName("clobParam__not_null")
fun SqContext.clobParam(value: Clob): SqParameter<Clob, Clob> =
    this.param(this.clobType(), value)
@JvmName("clobParam__nullable")
fun SqContext.clobParam(value: Clob?): SqParameter<Clob?, Clob> =
    this.param(this.clobType().nullable(), value)
fun SqContext.clobNull(): SqNull<Clob, Clob> =
    this.nullItem(this.clobType().nullable())
fun SqTable.clobNotNull(columnName: String): SqTableColumn<Clob, Clob> =
    this.column(SqGenericTypeHolder.clob, columnName)
fun SqTable.clobNullable(columnName: String): SqTableColumn<Clob?, Clob> =
    this.column(SqGenericTypeHolder.clob.nullable(), columnName)

fun SqContext.nClobType(): SqType<NClob, Clob> =
    this.typeHolder().nClob
@JvmName("nClobParam__not_null")
fun SqContext.nClobParam(value: NClob): SqParameter<NClob, Clob> =
    this.param(this.nClobType(), value)
@JvmName("nClobParam__nullable")
fun SqContext.nClobParam(value: NClob?): SqParameter<NClob?, Clob> =
    this.param(this.nClobType().nullable(), value)
fun SqContext.nClobNull(): SqNull<NClob, Clob> =
    this.nullItem(this.nClobType().nullable())
fun SqTable.nClobNotNull(columnName: String): SqTableColumn<NClob, Clob> =
    this.column(SqGenericTypeHolder.nClob, columnName)
fun SqTable.nClobNullable(columnName: String): SqTableColumn<NClob?, Clob> =
    this.column(SqGenericTypeHolder.nClob.nullable(), columnName)


fun SqContext.javaBlobType(): SqType<Blob, Blob> =
    this.typeHolder().javaBlob
@JvmName("javaBlobParam__not_null")
fun SqContext.javaBlobParam(value: Blob): SqParameter<Blob, Blob> =
    this.param(this.javaBlobType(), value)
@JvmName("javaBlobParam__nullable")
fun SqContext.javaBlobParam(value: Blob?): SqParameter<Blob?, Blob> =
    this.param(this.javaBlobType().nullable(), value)
fun SqContext.javaBlobNull(): SqNull<Blob, Blob> =
    this.nullItem(this.javaBlobType().nullable())

fun SqContext.javaClobType(): SqType<Clob, Clob> =
    this.typeHolder().javaClob
@JvmName("javaClobParam__not_null")
fun SqContext.javaClobParam(value: Clob): SqParameter<Clob, Clob> =
    this.param(this.javaClobType(), value)
@JvmName("javaClobParam__nullable")
fun SqContext.javaClobParam(value: Clob?): SqParameter<Clob?, Clob> =
    this.param(this.javaClobType().nullable(), value)
fun SqContext.javaClobNull(): SqNull<Clob, Clob> =
    this.nullItem(this.javaClobType().nullable())

fun SqContext.javaNClobType(): SqType<NClob, Clob> =
    this.typeHolder().javaNClob
@JvmName("javaNClobParam__not_null")
fun SqContext.javaNClobParam(value: NClob): SqParameter<NClob, Clob> =
    this.param(this.javaNClobType(), value)
@JvmName("javaNClobParam__nullable")
fun SqContext.javaNClobParam(value: NClob?): SqParameter<NClob?, Clob> =
    this.param(this.javaNClobType().nullable(), value)
fun SqContext.javaNClobNull(): SqNull<NClob, Clob> =
    this.nullItem(this.javaNClobType().nullable())
// endregion


// region Other JDBC types
fun SqContext.dataLinkType(): SqType<URL, String> =
    this.typeHolder().dataLink
@JvmName("dataLinkParam__not_null")
fun SqContext.dataLinkParam(value: URL): SqParameter<URL, String> =
    this.param(this.dataLinkType(), value)
@JvmName("dataLinkParam__nullable")
fun SqContext.dataLinkParam(value: URL?): SqParameter<URL?, String> =
    this.param(this.dataLinkType().nullable(), value)
fun SqContext.dataLinkNull(): SqNull<URL, String> =
    this.nullItem(this.dataLinkType().nullable())
fun SqTable.dataLinkNotNull(columnName: String): SqTableColumn<URL, String> =
    this.column(SqGenericTypeHolder.dataLink, columnName)
fun SqTable.dataLinkNullable(columnName: String): SqTableColumn<URL?, String> =
    this.column(SqGenericTypeHolder.dataLink.nullable(), columnName)

fun SqContext.refType(): SqType<Ref, Ref> =
    this.typeHolder().ref
@JvmName("refParam__not_null")
fun SqContext.refParam(value: Ref): SqParameter<Ref, Ref> =
    this.param(this.refType(), value)
@JvmName("refParam__nullable")
fun SqContext.refParam(value: Ref?): SqParameter<Ref?, Ref> =
    this.param(this.refType().nullable(), value)
fun SqContext.refNull(): SqNull<Ref, Ref> =
    this.nullItem(this.refType().nullable())
fun SqTable.refNotNull(columnName: String): SqTableColumn<Ref, Ref> =
    this.column(SqGenericTypeHolder.ref, columnName)
fun SqTable.refNullable(columnName: String): SqTableColumn<Ref?, Ref> =
    this.column(SqGenericTypeHolder.ref.nullable(), columnName)

fun SqContext.rowIdType(): SqType<RowId, RowId> =
    this.typeHolder().rowId
@JvmName("rowIdParam__not_null")
fun SqContext.rowIdParam(value: RowId): SqParameter<RowId, RowId> =
    this.param(this.rowIdType(), value)
@JvmName("rowIdParam__nullable")
fun SqContext.rowIdParam(value: RowId?): SqParameter<RowId?, RowId> =
    this.param(this.rowIdType().nullable(), value)
fun SqContext.rowIdNull(): SqNull<RowId, RowId> =
    this.nullItem(this.rowIdType().nullable())
fun SqTable.rowIdNotNull(columnName: String): SqTableColumn<RowId, RowId> =
    this.column(SqGenericTypeHolder.rowId, columnName)
fun SqTable.rowIdNullable(columnName: String): SqTableColumn<RowId?, RowId> =
    this.column(SqGenericTypeHolder.rowId.nullable(), columnName)

fun SqContext.sqlXmlType(): SqType<SQLXML, String> =
    this.typeHolder().sqlXml
@JvmName("sqlXmlParam__not_null")
fun SqContext.sqlXmlParam(value: SQLXML): SqParameter<SQLXML, String> =
    this.param(this.sqlXmlType(), value)
@JvmName("sqlXmlParam__nullable")
fun SqContext.sqlXmlParam(value: SQLXML?): SqParameter<SQLXML?, String> =
    this.param(this.sqlXmlType().nullable(), value)
fun SqContext.sqlXmlNull(): SqNull<SQLXML, String> =
    this.nullItem(this.sqlXmlType().nullable())
fun SqTable.sqlXmlNotNull(columnName: String): SqTableColumn<SQLXML, String> =
    this.column(SqGenericTypeHolder.sqlXml, columnName)
fun SqTable.sqlXmlNullable(columnName: String): SqTableColumn<SQLXML?, String> =
    this.column(SqGenericTypeHolder.sqlXml.nullable(), columnName)


fun SqContext.javaRefType(): SqType<Ref, Ref> =
    this.typeHolder().javaRef
@JvmName("javaRefParam__not_null")
fun SqContext.javaRefParam(value: Ref): SqParameter<Ref, Ref> =
    this.param(this.javaRefType(), value)
@JvmName("javaRefParam__nullable")
fun SqContext.javaRefParam(value: Ref?): SqParameter<Ref?, Ref> =
    this.param(this.javaRefType().nullable(), value)
fun SqContext.javaRefNull(): SqNull<Ref, Ref> =
    this.nullItem(this.javaRefType().nullable())

fun SqContext.javaRowIdType(): SqType<RowId, RowId> =
    this.typeHolder().javaRowId
@JvmName("javaRowIdParam__not_null")
fun SqContext.javaRowIdParam(value: RowId): SqParameter<RowId, RowId> =
    this.param(this.javaRowIdType(), value)
@JvmName("javaRowIdParam__nullable")
fun SqContext.javaRowIdParam(value: RowId?): SqParameter<RowId?, RowId> =
    this.param(this.javaRowIdType().nullable(), value)
fun SqContext.javaRowIdNull(): SqNull<RowId, RowId> =
    this.nullItem(this.javaRowIdType().nullable())

fun SqContext.javaSqlXmlType(): SqType<SQLXML, String> =
    this.typeHolder().javaSqlXml
@JvmName("javaSqlXmlParam__not_null")
fun SqContext.javaSqlXmlParam(value: SQLXML): SqParameter<SQLXML, String> =
    this.param(this.javaSqlXmlType(), value)
@JvmName("javaSqlXmlParam__nullable")
fun SqContext.javaSqlXmlParam(value: SQLXML?): SqParameter<SQLXML?, String> =
    this.param(this.javaSqlXmlType().nullable(), value)
fun SqContext.javaSqlXmlNull(): SqNull<SQLXML, String> =
    this.nullItem(this.javaSqlXmlType().nullable())

fun SqContext.javaUrlType(): SqType<URL, String> =
    this.typeHolder().javaUrl
@JvmName("javaUrlParam__not_null")
fun SqContext.javaUrlParam(value: URL): SqParameter<URL, String> =
    this.param(this.javaUrlType(), value)
@JvmName("javaUrlParam__nullable")
fun SqContext.javaUrlParam(value: URL?): SqParameter<URL?, String> =
    this.param(this.javaUrlType().nullable(), value)
fun SqContext.javaUrlNull(): SqNull<URL, String> =
    this.nullItem(this.javaUrlType().nullable())
// endregion


// region API item types
fun SqContext.booleanGroupType(): SqType<Boolean, Boolean> = this.typeHolder().booleanGroup
fun SqContext.notType(): SqType<Boolean, Boolean> = this.typeHolder().not
fun SqContext.nullTestType(): SqType<Boolean, Boolean> = this.typeHolder().nullTest
fun SqContext.comparisonType(): SqType<Boolean, Boolean> = this.typeHolder().comparison
fun SqContext.mathOperationType(): SqType<Number, Number> = this.typeHolder().mathOperation
// endregion
