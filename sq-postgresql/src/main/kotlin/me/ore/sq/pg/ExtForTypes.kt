package me.ore.sq.pg

import me.ore.sq.*
import me.ore.sq.generic.SqGenericTypeHolder
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import java.sql.*
import java.time.*


// region DB types-placeholders
interface SqPgDbTypeBit
// endregion


interface SqPgTypeHolder: SqTypeHolder {
    // region Boolean types
    val pgBoolean: SqType<Boolean, Boolean>
    override val boolean: SqType<Boolean, Boolean>
        get() = this.pgBoolean

    val pgBooleanArray: SqType<List<Boolean?>, Array<Boolean>>
    // endregion


    // region Byte array types
    val pgBytea: SqType<ByteArray, ByteArray>
    override val binary: SqType<ByteArray, ByteArray>
        get() = this.pgBytea

    val pgByteaArray: SqType<List<ByteArray?>, Array<ByteArray>>

    override val longVarBinary: SqType<ByteArray, ByteArray>
        get() = SqGenericTypeHolder.longVarBinary

    override val varBinary: SqType<ByteArray, ByteArray>
        get() = SqGenericTypeHolder.varBinary
    // endregion


    // region Date/time types
    val pgDate: SqType<LocalDate, Timestamp>
    override val date: SqType<LocalDate, Timestamp>
        get() = this.pgDate

    val pgDateArray: SqType<List<LocalDate?>, Array<Timestamp>>

    val pgDateAsDate: SqType<Date, Timestamp>
    override val dateAsDate: SqType<Date, Timestamp>
        get() = this.pgDateAsDate

    val pgTime: SqType<LocalTime, Time>
    override val time: SqType<LocalTime, Time>
        get() = this.pgTime

    val pgTimeArray: SqType<List<LocalTime?>, Array<Time>>

    val pgTimeAsTime: SqType<Time, Time>
    override val timeAsTime: SqType<Time, Time>
        get() = this.pgTimeAsTime

    val pgTimeTZ: SqType<OffsetTime, Time>
    override val timeWithTimeZone: SqType<OffsetTime, Time>
        get() = this.pgTimeTZ

    val pgTimeTZArray: SqType<List<OffsetTime?>, Array<Time>>

    val pgTimestamp: SqType<LocalDateTime, Timestamp>
    override val timestamp: SqType<LocalDateTime, Timestamp>
        get() = this.pgTimestamp

    val pgTimestampArray: SqType<List<LocalDateTime?>, Array<Timestamp>>

    val pgTimestampAsTimestamp: SqType<Timestamp, Timestamp>
    override val timestampAsTimestamp: SqType<Timestamp, Timestamp>
        get() = this.pgTimestampAsTimestamp

    val pgTimestampTZ: SqType<OffsetDateTime, Timestamp>
    override val timestampWithTimeZone: SqType<OffsetDateTime, Timestamp>
        get() = this.pgTimestampTZ

    val pgTimestampTZArray: SqType<List<OffsetDateTime?>, Array<Timestamp>>
    // endregion


    // region Number types
    val pgBigInt: SqType<Long, Number>
    override val bigInt: SqType<Long, Number>
        get() = this.pgBigInt

    val pgBigIntArray: SqType<List<Long?>, Array<Number>>

    val pgBigIntAsBigInteger: SqType<BigInteger, Number>
    override val bigIntAsBigInteger: SqType<BigInteger, Number>
        get() = this.pgBigIntAsBigInteger

    val pgDouble: SqType<Double, Number>
    override val double: SqType<Double, Number>
        get() = this.pgDouble
    override val float: SqType<Double, Number>
        get() = this.pgDouble

    val pgDoubleArray: SqType<List<Double?>, Array<Number>>

    val pgInteger: SqType<Int, Number>
    override val integer: SqType<Int, Number>
        get() = this.pgInteger

    val pgIntegerArray: SqType<List<Int?>, Array<Number>>

    val pgNumeric: SqType<BigDecimal, Number>
    override val numeric: SqType<BigDecimal, Number>
        get() = this.pgNumeric
    override val decimal: SqType<BigDecimal, Number>
        get() = this.pgNumeric

    val pgNumericArray: SqType<List<BigDecimal?>, Array<Number>>

    val pgReal: SqType<Float, Number>
    override val real: SqType<Float, Number>
        get() = this.pgReal

    val pgRealArray: SqType<List<Float?>, Array<Number>>

    val pgSmallInt: SqType<Short, Number>
    override val smallInt: SqType<Short, Number>
        get() = this.pgSmallInt

    val pgSmallIntArray: SqType<List<Short?>, Array<Number>>

    override val tinyInt: SqType<Byte, Number>
        get() = SqGenericTypeHolder.tinyInt

    override val javaNumber: SqType<Number, Number>
        get() = SqGenericTypeHolder.javaNumber
    // endregion


    // region Text types
    val pgChar: SqType<String, String>
    override val char: SqType<String, String>
        get() = this.pgChar

    val pgCharArray: SqType<List<String?>, Array<String>>

    val pgCharacter: SqType<String, String>

    val pgCharacterArray: SqType<List<String?>, Array<String>>

    val pgText: SqType<String, String>

    val pgTextArray: SqType<List<String?>, Array<String>>

    val pgVarChar: SqType<String, String>
    override val varChar: SqType<String, String>
        get() = this.pgVarChar

    val pgVarCharArray: SqType<List<String?>, Array<String>>

    override val longVarChar: SqType<String, String>
        get() = SqGenericTypeHolder.longVarChar
    override val nChar: SqType<String, String>
        get() = SqGenericTypeHolder.nChar
    override val nVarChar: SqType<String, String>
        get() = SqGenericTypeHolder.nVarChar
    override val longNVarChar: SqType<String, String>
        get() = SqGenericTypeHolder.longNVarChar
    // endregion


    // region Blob/Clob types
    override val blob: SqType<Blob, Blob>
        get() = SqGenericTypeHolder.blob

    override val clob: SqType<Clob, Clob>
        get() = SqGenericTypeHolder.clob

    override val nClob: SqType<NClob, Clob>
        get() = SqGenericTypeHolder.nClob
    // endregion


    // region Other JDBC types
    val pgXml: SqType<SQLXML, String>
    override val sqlXml: SqType<SQLXML, String>
        get() = this.pgXml

    val pgXmlArray: SqType<List<SQLXML?>, Array<String>>

    override val dataLink: SqType<URL, String>
        get() = SqGenericTypeHolder.dataLink
    override val ref: SqType<Ref, Ref>
        get() = SqGenericTypeHolder.ref
    override val rowId: SqType<RowId, RowId>
        get() = SqGenericTypeHolder.rowId
    // endregion


    // region Other Postgresql types
    val pgJson: SqType<String, String>

    val pgJsonArray: SqType<List<String?>, Array<String>>

    val pgJsonB: SqType<String, String>

    val pgJsonBArray: SqType<List<String?>, Array<String>>

    /** bit(x), where x unlimited */
    val pgMultiBit: SqType<BooleanArray, SqPgDbTypeBit>

    /** bit(x), where x unlimited */
    val pgMultiBitArray: SqType<List<BooleanArray?>, Array<SqPgDbTypeBit>>

    /** bit(x), where x = 1 */
    val pgSingleBit: SqType<Boolean, SqPgDbTypeBit>

    /** bit(x), where x = 1 */
    val pgSingleBitArray: SqType<List<Boolean?>, Array<SqPgDbTypeBit>>

    /** bit varying */
    val pgVarBit: SqType<BooleanArray, SqPgDbTypeBit>

    /** bit varying */
    val pgVarBitArray: SqType<List<BooleanArray?>, Array<SqPgDbTypeBit>>
    // endregion
}


fun SqContext.pgTypeHolder(): SqPgTypeHolder =
    this[SqPgTypeHolder::class.java, SqPgTypeHolderImpl]


// region Boolean types
fun SqContext.pgBooleanType(): SqType<Boolean, Boolean> =
    this.pgTypeHolder().pgBoolean
@JvmName("pgBooleanParam__not_null")
fun SqContext.pgBooleanParam(value: Boolean): SqParameter<Boolean, Boolean> =
    this.param(this.pgBooleanType(), value)
@JvmName("pgBooleanParam__nullable")
fun SqContext.pgBooleanParam(value: Boolean?): SqParameter<Boolean?, Boolean> =
    this.param(this.pgBooleanType().nullable(), value)
fun SqContext.pgBooleanNull(): SqNull<Boolean, Boolean> =
    this.nullItem(this.pgBooleanType().nullable())
fun SqTable.pgBooleanNotNull(columnName: String): SqTableColumn<Boolean, Boolean> =
    this.column(SqPgTypeHolderImpl.pgBoolean, columnName)
fun SqTable.pgBooleanNullable(columnName: String): SqTableColumn<Boolean?, Boolean> =
    this.column(SqPgTypeHolderImpl.pgBoolean.nullable(), columnName)

fun SqContext.pgBooleanArrayType(): SqType<List<Boolean?>, Array<Boolean>> =
    this.pgTypeHolder().pgBooleanArray
@JvmName("pgBooleanArrayParam__not_null")
fun SqContext.pgBooleanArrayParam(value: List<Boolean?>): SqParameter<List<Boolean?>, Array<Boolean>> =
    this.param(this.pgBooleanArrayType(), value)
@JvmName("pgBooleanArrayParam__nullable")
fun SqContext.pgBooleanArrayParam(value: List<Boolean?>?): SqParameter<List<Boolean?>?, Array<Boolean>> =
    this.param(this.pgBooleanArrayType().nullable(), value)
fun SqContext.pgBooleanArrayNull(): SqNull<List<Boolean?>, Array<Boolean>> =
    this.nullItem(this.pgBooleanArrayType().nullable())
fun SqTable.pgBooleanArrayNotNull(columnName: String): SqTableColumn<List<Boolean?>, Array<Boolean>> =
    this.column(SqPgTypeHolderImpl.pgBooleanArray, columnName)
fun SqTable.pgBooleanArrayNullable(columnName: String): SqTableColumn<List<Boolean?>?, Array<Boolean>> =
    this.column(SqPgTypeHolderImpl.pgBooleanArray.nullable(), columnName)
// endregion


// region Byte array types
fun SqContext.pgByteaType(): SqType<ByteArray, ByteArray> =
    this.pgTypeHolder().pgBytea
@JvmName("pgByteaParam__not_null")
fun SqContext.pgByteaParam(value: ByteArray): SqParameter<ByteArray, ByteArray> =
    this.param(this.pgByteaType(), value)
@JvmName("pgByteaParam__nullable")
fun SqContext.pgByteaParam(value: ByteArray?): SqParameter<ByteArray?, ByteArray> =
    this.param(this.pgByteaType().nullable(), value)
fun SqContext.pgByteaNull(): SqNull<ByteArray, ByteArray> =
    this.nullItem(this.pgByteaType().nullable())
fun SqTable.pgByteaNotNull(columnName: String): SqTableColumn<ByteArray, ByteArray> =
    this.column(SqPgTypeHolderImpl.pgBytea, columnName)
fun SqTable.pgByteaNullable(columnName: String): SqTableColumn<ByteArray?, ByteArray> =
    this.column(SqPgTypeHolderImpl.pgBytea.nullable(), columnName)

fun SqContext.pgByteaArrayType(): SqType<List<ByteArray?>, Array<ByteArray>> =
    this.pgTypeHolder().pgByteaArray
@JvmName("pgByteaArrayParam__not_null")
fun SqContext.pgByteaArrayParam(value: List<ByteArray?>): SqParameter<List<ByteArray?>, Array<ByteArray>> =
    this.param(this.pgByteaArrayType(), value)
@JvmName("pgByteaArrayParam__nullable")
fun SqContext.pgByteaArrayParam(value: List<ByteArray?>?): SqParameter<List<ByteArray?>?, Array<ByteArray>> =
    this.param(this.pgByteaArrayType().nullable(), value)
fun SqContext.pgByteaArrayNull(): SqNull<List<ByteArray?>, Array<ByteArray>> =
    this.nullItem(this.pgByteaArrayType().nullable())
fun SqTable.pgByteaArrayNotNull(columnName: String): SqTableColumn<List<ByteArray?>, Array<ByteArray>> =
    this.column(SqPgTypeHolderImpl.pgByteaArray, columnName)
fun SqTable.pgByteaArrayNullable(columnName: String): SqTableColumn<List<ByteArray?>?, Array<ByteArray>> =
    this.column(SqPgTypeHolderImpl.pgByteaArray.nullable(), columnName)
// endregion


// region Date/time types
fun SqContext.pgDateType(): SqType<LocalDate, Timestamp> =
    this.pgTypeHolder().pgDate
@JvmName("pgDateParam__not_null")
fun SqContext.pgDateParam(value: LocalDate): SqParameter<LocalDate, Timestamp> =
    this.param(this.pgDateType(), value)
@JvmName("pgDateParam__nullable")
fun SqContext.pgDateParam(value: LocalDate?): SqParameter<LocalDate?, Timestamp> =
    this.param(this.pgDateType().nullable(), value)
fun SqContext.pgDateNull(): SqNull<LocalDate, Timestamp> =
    this.nullItem(this.pgDateType().nullable())
fun SqTable.pgDateNotNull(columnName: String): SqTableColumn<LocalDate, Timestamp> =
    this.column(SqPgTypeHolderImpl.pgDate, columnName)
fun SqTable.pgDateNullable(columnName: String): SqTableColumn<LocalDate?, Timestamp> =
    this.column(SqPgTypeHolderImpl.pgDate.nullable(), columnName)

fun SqContext.pgDateArrayType(): SqType<List<LocalDate?>, Array<Timestamp>> =
    this.pgTypeHolder().pgDateArray
@JvmName("pgDateArrayParam__not_null")
fun SqContext.pgDateArrayParam(value: List<LocalDate?>): SqParameter<List<LocalDate?>, Array<Timestamp>> =
    this.param(this.pgDateArrayType(), value)
@JvmName("pgDateArrayParam__nullable")
fun SqContext.pgDateArrayParam(value: List<LocalDate?>?): SqParameter<List<LocalDate?>?, Array<Timestamp>> =
    this.param(this.pgDateArrayType().nullable(), value)
fun SqContext.pgDateArrayNull(): SqNull<List<LocalDate?>, Array<Timestamp>> =
    this.nullItem(this.pgDateArrayType().nullable())
fun SqTable.pgDateArrayNotNull(columnName: String): SqTableColumn<List<LocalDate?>, Array<Timestamp>> =
    this.column(SqPgTypeHolderImpl.pgDateArray, columnName)
fun SqTable.pgDateArrayNullable(columnName: String): SqTableColumn<List<LocalDate?>?, Array<Timestamp>> =
    this.column(SqPgTypeHolderImpl.pgDateArray.nullable(), columnName)

fun SqContext.pgDateAsDateType(): SqType<Date, Timestamp> =
    this.pgTypeHolder().pgDateAsDate
@JvmName("pgDateAsDateParam__not_null")
fun SqContext.pgDateAsDateParam(value: Date): SqParameter<Date, Timestamp> =
    this.param(this.pgDateAsDateType(), value)
@JvmName("pgDateAsDateParam__nullable")
fun SqContext.pgDateAsDateParam(value: Date?): SqParameter<Date?, Timestamp> =
    this.param(this.pgDateAsDateType().nullable(), value)
fun SqContext.pgDateAsDateNull(): SqNull<Date, Timestamp> =
    this.nullItem(this.pgDateAsDateType().nullable())
fun SqTable.pgDateAsDateNotNull(columnName: String): SqTableColumn<Date, Timestamp> =
    this.column(SqPgTypeHolderImpl.pgDateAsDate, columnName)
fun SqTable.pgDateAsDateNullable(columnName: String): SqTableColumn<Date?, Timestamp> =
    this.column(SqPgTypeHolderImpl.pgDateAsDate.nullable(), columnName)

fun SqContext.pgTimeType(): SqType<LocalTime, Time> =
    this.pgTypeHolder().pgTime
@JvmName("pgTimeParam__not_null")
fun SqContext.pgTimeParam(value: LocalTime): SqParameter<LocalTime, Time> =
    this.param(this.pgTimeType(), value)
@JvmName("pgTimeParam__nullable")
fun SqContext.pgTimeParam(value: LocalTime?): SqParameter<LocalTime?, Time> =
    this.param(this.pgTimeType().nullable(), value)
fun SqContext.pgTimeNull(): SqNull<LocalTime, Time> =
    this.nullItem(this.pgTimeType().nullable())
fun SqTable.pgTimeNotNull(columnName: String): SqTableColumn<LocalTime, Time> =
    this.column(SqPgTypeHolderImpl.pgTime, columnName)
fun SqTable.pgTimeNullable(columnName: String): SqTableColumn<LocalTime?, Time> =
    this.column(SqPgTypeHolderImpl.pgTime.nullable(), columnName)

fun SqContext.pgTimeArrayType(): SqType<List<LocalTime?>, Array<Time>> =
    this.pgTypeHolder().pgTimeArray
@JvmName("pgTimeArrayParam__not_null")
fun SqContext.pgTimeArrayParam(value: List<LocalTime?>): SqParameter<List<LocalTime?>, Array<Time>> =
    this.param(this.pgTimeArrayType(), value)
@JvmName("pgTimeArrayParam__nullable")
fun SqContext.pgTimeArrayParam(value: List<LocalTime?>?): SqParameter<List<LocalTime?>?, Array<Time>> =
    this.param(this.pgTimeArrayType().nullable(), value)
fun SqContext.pgTimeArrayNull(): SqNull<List<LocalTime?>, Array<Time>> =
    this.nullItem(this.pgTimeArrayType().nullable())
fun SqTable.pgTimeArrayNotNull(columnName: String): SqTableColumn<List<LocalTime?>, Array<Time>> =
    this.column(SqPgTypeHolderImpl.pgTimeArray, columnName)
fun SqTable.pgTimeArrayNullable(columnName: String): SqTableColumn<List<LocalTime?>?, Array<Time>> =
    this.column(SqPgTypeHolderImpl.pgTimeArray.nullable(), columnName)

fun SqContext.pgTimeAsTimeType(): SqType<Time, Time> =
    this.pgTypeHolder().pgTimeAsTime
@JvmName("pgTimeAsTimeParam__not_null")
fun SqContext.pgTimeAsTimeParam(value: Time): SqParameter<Time, Time> =
    this.param(this.pgTimeAsTimeType(), value)
@JvmName("pgTimeAsTimeParam__nullable")
fun SqContext.pgTimeAsTimeParam(value: Time?): SqParameter<Time?, Time> =
    this.param(this.pgTimeAsTimeType().nullable(), value)
fun SqContext.pgTimeAsTimeNull(): SqNull<Time, Time> =
    this.nullItem(this.pgTimeAsTimeType().nullable())
fun SqTable.pgTimeAsTimeNotNull(columnName: String): SqTableColumn<Time, Time> =
    this.column(SqPgTypeHolderImpl.pgTimeAsTime, columnName)
fun SqTable.pgTimeAsTimeNullable(columnName: String): SqTableColumn<Time?, Time> =
    this.column(SqPgTypeHolderImpl.pgTimeAsTime.nullable(), columnName)

fun SqContext.pgTimeTZType(): SqType<OffsetTime, Time> =
    this.pgTypeHolder().pgTimeTZ
@JvmName("pgTimeTZParam__not_null")
fun SqContext.pgTimeTZParam(value: OffsetTime): SqParameter<OffsetTime, Time> =
    this.param(this.pgTimeTZType(), value)
@JvmName("pgTimeTZParam__nullable")
fun SqContext.pgTimeTZParam(value: OffsetTime?): SqParameter<OffsetTime?, Time> =
    this.param(this.pgTimeTZType().nullable(), value)
fun SqContext.pgTimeTZNull(): SqNull<OffsetTime, Time> =
    this.nullItem(this.pgTimeTZType().nullable())
fun SqTable.pgTimeTZNotNull(columnName: String): SqTableColumn<OffsetTime, Time> =
    this.column(SqPgTypeHolderImpl.pgTimeTZ, columnName)
fun SqTable.pgTimeTZNullable(columnName: String): SqTableColumn<OffsetTime?, Time> =
    this.column(SqPgTypeHolderImpl.pgTimeTZ.nullable(), columnName)

fun SqContext.pgTimeTZArrayType(): SqType<List<OffsetTime?>, Array<Time>> =
    this.pgTypeHolder().pgTimeTZArray
@JvmName("pgTimeTZArrayParam__not_null")
fun SqContext.pgTimeTZArrayParam(value: List<OffsetTime?>): SqParameter<List<OffsetTime?>, Array<Time>> =
    this.param(this.pgTimeTZArrayType(), value)
@JvmName("pgTimeTZArrayParam__nullable")
fun SqContext.pgTimeTZArrayParam(value: List<OffsetTime?>?): SqParameter<List<OffsetTime?>?, Array<Time>> =
    this.param(this.pgTimeTZArrayType().nullable(), value)
fun SqContext.pgTimeTZArrayNull(): SqNull<List<OffsetTime?>, Array<Time>> =
    this.nullItem(this.pgTimeTZArrayType().nullable())
fun SqTable.pgTimeTZArrayNotNull(columnName: String): SqTableColumn<List<OffsetTime?>, Array<Time>> =
    this.column(SqPgTypeHolderImpl.pgTimeTZArray, columnName)
fun SqTable.pgTimeTZArrayNullable(columnName: String): SqTableColumn<List<OffsetTime?>?, Array<Time>> =
    this.column(SqPgTypeHolderImpl.pgTimeTZArray.nullable(), columnName)

fun SqContext.pgTimestampType(): SqType<LocalDateTime, Timestamp> =
    this.pgTypeHolder().pgTimestamp
@JvmName("pgTimestampParam__not_null")
fun SqContext.pgTimestampParam(value: LocalDateTime): SqParameter<LocalDateTime, Timestamp> =
    this.param(this.pgTimestampType(), value)
@JvmName("pgTimestampParam__nullable")
fun SqContext.pgTimestampParam(value: LocalDateTime?): SqParameter<LocalDateTime?, Timestamp> =
    this.param(this.pgTimestampType().nullable(), value)
fun SqContext.pgTimestampNull(): SqNull<LocalDateTime, Timestamp> =
    this.nullItem(this.pgTimestampType().nullable())
fun SqTable.pgTimestampNotNull(columnName: String): SqTableColumn<LocalDateTime, Timestamp> =
    this.column(SqPgTypeHolderImpl.pgTimestamp, columnName)
fun SqTable.pgTimestampNullable(columnName: String): SqTableColumn<LocalDateTime?, Timestamp> =
    this.column(SqPgTypeHolderImpl.pgTimestamp.nullable(), columnName)

fun SqContext.pgTimestampArrayType(): SqType<List<LocalDateTime?>, Array<Timestamp>> =
    this.pgTypeHolder().pgTimestampArray
@JvmName("pgTimestampArrayParam__not_null")
fun SqContext.pgTimestampArrayParam(value: List<LocalDateTime?>): SqParameter<List<LocalDateTime?>, Array<Timestamp>> =
    this.param(this.pgTimestampArrayType(), value)
@JvmName("pgTimestampArrayParam__nullable")
fun SqContext.pgTimestampArrayParam(value: List<LocalDateTime?>?): SqParameter<List<LocalDateTime?>?, Array<Timestamp>> =
    this.param(this.pgTimestampArrayType().nullable(), value)
fun SqContext.pgTimestampArrayNull(): SqNull<List<LocalDateTime?>, Array<Timestamp>> =
    this.nullItem(this.pgTimestampArrayType().nullable())
fun SqTable.pgTimestampArrayNotNull(columnName: String): SqTableColumn<List<LocalDateTime?>, Array<Timestamp>> =
    this.column(SqPgTypeHolderImpl.pgTimestampArray, columnName)
fun SqTable.pgTimestampArrayNullable(columnName: String): SqTableColumn<List<LocalDateTime?>?, Array<Timestamp>> =
    this.column(SqPgTypeHolderImpl.pgTimestampArray.nullable(), columnName)

fun SqContext.pgTimestampAsTimestampType(): SqType<Timestamp, Timestamp> =
    this.pgTypeHolder().pgTimestampAsTimestamp
@JvmName("pgTimestampAsTimestampParam__not_null")
fun SqContext.pgTimestampAsTimestampParam(value: Timestamp): SqParameter<Timestamp, Timestamp> =
    this.param(this.pgTimestampAsTimestampType(), value)
@JvmName("pgTimestampAsTimestampParam__nullable")
fun SqContext.pgTimestampAsTimestampParam(value: Timestamp?): SqParameter<Timestamp?, Timestamp> =
    this.param(this.pgTimestampAsTimestampType().nullable(), value)
fun SqContext.pgTimestampAsTimestampNull(): SqNull<Timestamp, Timestamp> =
    this.nullItem(this.pgTimestampAsTimestampType().nullable())
fun SqTable.pgTimestampAsTimestampNotNull(columnName: String): SqTableColumn<Timestamp, Timestamp> =
    this.column(SqPgTypeHolderImpl.pgTimestampAsTimestamp, columnName)
fun SqTable.pgTimestampAsTimestampNullable(columnName: String): SqTableColumn<Timestamp?, Timestamp> =
    this.column(SqPgTypeHolderImpl.pgTimestampAsTimestamp.nullable(), columnName)

fun SqContext.pgTimestampTZType(): SqType<OffsetDateTime, Timestamp> =
    this.pgTypeHolder().pgTimestampTZ
@JvmName("pgTimestampTZParam__not_null")
fun SqContext.pgTimestampTZParam(value: OffsetDateTime): SqParameter<OffsetDateTime, Timestamp> =
    this.param(this.pgTimestampTZType(), value)
@JvmName("pgTimestampTZParam__nullable")
fun SqContext.pgTimestampTZParam(value: OffsetDateTime?): SqParameter<OffsetDateTime?, Timestamp> =
    this.param(this.pgTimestampTZType().nullable(), value)
fun SqContext.pgTimestampTZNull(): SqNull<OffsetDateTime, Timestamp> =
    this.nullItem(this.pgTimestampTZType().nullable())
fun SqTable.pgTimestampTZNotNull(columnName: String): SqTableColumn<OffsetDateTime, Timestamp> =
    this.column(SqPgTypeHolderImpl.pgTimestampTZ, columnName)
fun SqTable.pgTimestampTZNullable(columnName: String): SqTableColumn<OffsetDateTime?, Timestamp> =
    this.column(SqPgTypeHolderImpl.pgTimestampTZ.nullable(), columnName)

fun SqContext.pgTimestampTZArrayType(): SqType<List<OffsetDateTime?>, Array<Timestamp>> =
    this.pgTypeHolder().pgTimestampTZArray
@JvmName("pgTimestampTZArrayParam__not_null")
fun SqContext.pgTimestampTZArrayParam(value: List<OffsetDateTime?>): SqParameter<List<OffsetDateTime?>, Array<Timestamp>> =
    this.param(this.pgTimestampTZArrayType(), value)
@JvmName("pgTimestampTZArrayParam__nullable")
fun SqContext.pgTimestampTZArrayParam(value: List<OffsetDateTime?>?): SqParameter<List<OffsetDateTime?>?, Array<Timestamp>> =
    this.param(this.pgTimestampTZArrayType().nullable(), value)
fun SqContext.pgTimestampTZArrayNull(): SqNull<List<OffsetDateTime?>, Array<Timestamp>> =
    this.nullItem(this.pgTimestampTZArrayType().nullable())
fun SqTable.pgTimestampTZArrayNotNull(columnName: String): SqTableColumn<List<OffsetDateTime?>, Array<Timestamp>> =
    this.column(SqPgTypeHolderImpl.pgTimestampTZArray, columnName)
fun SqTable.pgTimestampTZArrayNullable(columnName: String): SqTableColumn<List<OffsetDateTime?>?, Array<Timestamp>> =
    this.column(SqPgTypeHolderImpl.pgTimestampTZArray.nullable(), columnName)
// endregion


// region Number types
fun SqContext.pgBigIntType(): SqType<Long, Number> =
    this.pgTypeHolder().pgBigInt
@JvmName("pgBigIntParam__not_null")
fun SqContext.pgBigIntParam(value: Long): SqParameter<Long, Number> =
    this.param(this.pgBigIntType(), value)
@JvmName("pgBigIntParam__nullable")
fun SqContext.pgBigIntParam(value: Long?): SqParameter<Long?, Number> =
    this.param(this.pgBigIntType().nullable(), value)
fun SqContext.pgBigIntNull(): SqNull<Long, Number> =
    this.nullItem(this.pgBigIntType().nullable())
fun SqTable.pgBigIntNotNull(columnName: String): SqTableColumn<Long, Number> =
    this.column(SqPgTypeHolderImpl.pgBigInt, columnName)
fun SqTable.pgBigIntNullable(columnName: String): SqTableColumn<Long?, Number> =
    this.column(SqPgTypeHolderImpl.pgBigInt.nullable(), columnName)

fun SqContext.pgBigIntArrayType(): SqType<List<Long?>, Array<Number>> =
    this.pgTypeHolder().pgBigIntArray
@JvmName("pgBigIntArrayParam__not_null")
fun SqContext.pgBigIntArrayParam(value: List<Long?>): SqParameter<List<Long?>, Array<Number>> =
    this.param(this.pgBigIntArrayType(), value)
@JvmName("pgBigIntArrayParam__nullable")
fun SqContext.pgBigIntArrayParam(value: List<Long?>?): SqParameter<List<Long?>?, Array<Number>> =
    this.param(this.pgBigIntArrayType().nullable(), value)
fun SqContext.pgBigIntArrayNull(): SqNull<List<Long?>, Array<Number>> =
    this.nullItem(this.pgBigIntArrayType().nullable())
fun SqTable.pgBigIntArrayNotNull(columnName: String): SqTableColumn<List<Long?>, Array<Number>> =
    this.column(SqPgTypeHolderImpl.pgBigIntArray, columnName)
fun SqTable.pgBigIntArrayNullable(columnName: String): SqTableColumn<List<Long?>?, Array<Number>> =
    this.column(SqPgTypeHolderImpl.pgBigIntArray.nullable(), columnName)

fun SqContext.pgBigIntAsBigIntegerType(): SqType<BigInteger, Number> =
    this.pgTypeHolder().pgBigIntAsBigInteger
@JvmName("pgBigIntAsBigIntegerParam__not_null")
fun SqContext.pgBigIntAsBigIntegerParam(value: BigInteger): SqParameter<BigInteger, Number> =
    this.param(this.pgBigIntAsBigIntegerType(), value)
@JvmName("pgBigIntAsBigIntegerParam__nullable")
fun SqContext.pgBigIntAsBigIntegerParam(value: BigInteger?): SqParameter<BigInteger?, Number> =
    this.param(this.pgBigIntAsBigIntegerType().nullable(), value)
fun SqContext.pgBigIntAsBigIntegerNull(): SqNull<BigInteger, Number> =
    this.nullItem(this.pgBigIntAsBigIntegerType().nullable())
fun SqTable.pgBigIntAsBigIntegerNotNull(columnName: String): SqTableColumn<BigInteger, Number> =
    this.column(SqPgTypeHolderImpl.pgBigIntAsBigInteger, columnName)
fun SqTable.pgBigIntAsBigIntegerNullable(columnName: String): SqTableColumn<BigInteger?, Number> =
    this.column(SqPgTypeHolderImpl.pgBigIntAsBigInteger.nullable(), columnName)

fun SqContext.pgDoubleType(): SqType<Double, Number> =
    this.pgTypeHolder().pgDouble
@JvmName("pgDoubleParam__not_null")
fun SqContext.pgDoubleParam(value: Double): SqParameter<Double, Number> =
    this.param(this.pgDoubleType(), value)
@JvmName("pgDoubleParam__nullable")
fun SqContext.pgDoubleParam(value: Double?): SqParameter<Double?, Number> =
    this.param(this.pgDoubleType().nullable(), value)
fun SqContext.pgDoubleNull(): SqNull<Double, Number> =
    this.nullItem(this.pgDoubleType().nullable())
fun SqTable.pgDoubleNotNull(columnName: String): SqTableColumn<Double, Number> =
    this.column(SqPgTypeHolderImpl.pgDouble, columnName)
fun SqTable.pgDoubleNullable(columnName: String): SqTableColumn<Double?, Number> =
    this.column(SqPgTypeHolderImpl.pgDouble.nullable(), columnName)

fun SqContext.pgDoubleArrayType(): SqType<List<Double?>, Array<Number>> =
    this.pgTypeHolder().pgDoubleArray
@JvmName("pgDoubleArrayParam__not_null")
fun SqContext.pgDoubleArrayParam(value: List<Double?>): SqParameter<List<Double?>, Array<Number>> =
    this.param(this.pgDoubleArrayType(), value)
@JvmName("pgDoubleArrayParam__nullable")
fun SqContext.pgDoubleArrayParam(value: List<Double?>?): SqParameter<List<Double?>?, Array<Number>> =
    this.param(this.pgDoubleArrayType().nullable(), value)
fun SqContext.pgDoubleArrayNull(): SqNull<List<Double?>, Array<Number>> =
    this.nullItem(this.pgDoubleArrayType().nullable())
fun SqTable.pgDoubleArrayNotNull(columnName: String): SqTableColumn<List<Double?>, Array<Number>> =
    this.column(SqPgTypeHolderImpl.pgDoubleArray, columnName)
fun SqTable.pgDoubleArrayNullable(columnName: String): SqTableColumn<List<Double?>?, Array<Number>> =
    this.column(SqPgTypeHolderImpl.pgDoubleArray.nullable(), columnName)

fun SqContext.pgIntegerType(): SqType<Int, Number> =
    this.pgTypeHolder().pgInteger
@JvmName("pgIntegerParam__not_null")
fun SqContext.pgIntegerParam(value: Int): SqParameter<Int, Number> =
    this.param(this.pgIntegerType(), value)
@JvmName("pgIntegerParam__nullable")
fun SqContext.pgIntegerParam(value: Int?): SqParameter<Int?, Number> =
    this.param(this.pgIntegerType().nullable(), value)
fun SqContext.pgIntegerNull(): SqNull<Int, Number> =
    this.nullItem(this.pgIntegerType().nullable())
fun SqTable.pgIntegerNotNull(columnName: String): SqTableColumn<Int, Number> =
    this.column(SqPgTypeHolderImpl.pgInteger, columnName)
fun SqTable.pgIntegerNullable(columnName: String): SqTableColumn<Int?, Number> =
    this.column(SqPgTypeHolderImpl.pgInteger.nullable(), columnName)

fun SqContext.pgIntegerArrayType(): SqType<List<Int?>, Array<Number>> =
    this.pgTypeHolder().pgIntegerArray
@JvmName("pgIntegerArrayParam__not_null")
fun SqContext.pgIntegerArrayParam(value: List<Int?>): SqParameter<List<Int?>, Array<Number>> =
    this.param(this.pgIntegerArrayType(), value)
@JvmName("pgIntegerArrayParam__nullable")
fun SqContext.pgIntegerArrayParam(value: List<Int?>?): SqParameter<List<Int?>?, Array<Number>> =
    this.param(this.pgIntegerArrayType().nullable(), value)
fun SqContext.pgIntegerArrayNull(): SqNull<List<Int?>, Array<Number>> =
    this.nullItem(this.pgIntegerArrayType().nullable())
fun SqTable.pgIntegerArrayNotNull(columnName: String): SqTableColumn<List<Int?>, Array<Number>> =
    this.column(SqPgTypeHolderImpl.pgIntegerArray, columnName)
fun SqTable.pgIntegerArrayNullable(columnName: String): SqTableColumn<List<Int?>?, Array<Number>> =
    this.column(SqPgTypeHolderImpl.pgIntegerArray.nullable(), columnName)

fun SqContext.pgNumericType(): SqType<BigDecimal, Number> =
    this.pgTypeHolder().pgNumeric
@JvmName("pgNumericParam__not_null")
fun SqContext.pgNumericParam(value: BigDecimal): SqParameter<BigDecimal, Number> =
    this.param(this.pgNumericType(), value)
@JvmName("pgNumericParam__nullable")
fun SqContext.pgNumericParam(value: BigDecimal?): SqParameter<BigDecimal?, Number> =
    this.param(this.pgNumericType().nullable(), value)
fun SqContext.pgNumericNull(): SqNull<BigDecimal, Number> =
    this.nullItem(this.pgNumericType().nullable())
fun SqTable.pgNumericNotNull(columnName: String): SqTableColumn<BigDecimal, Number> =
    this.column(SqPgTypeHolderImpl.pgNumeric, columnName)
fun SqTable.pgNumericNullable(columnName: String): SqTableColumn<BigDecimal?, Number> =
    this.column(SqPgTypeHolderImpl.pgNumeric.nullable(), columnName)

fun SqContext.pgNumericArrayType(): SqType<List<BigDecimal?>, Array<Number>> =
    this.pgTypeHolder().pgNumericArray
@JvmName("pgNumericArrayParam__not_null")
fun SqContext.pgNumericArrayParam(value: List<BigDecimal?>): SqParameter<List<BigDecimal?>, Array<Number>> =
    this.param(this.pgNumericArrayType(), value)
@JvmName("pgNumericArrayParam__nullable")
fun SqContext.pgNumericArrayParam(value: List<BigDecimal?>?): SqParameter<List<BigDecimal?>?, Array<Number>> =
    this.param(this.pgNumericArrayType().nullable(), value)
fun SqContext.pgNumericArrayNull(): SqNull<List<BigDecimal?>, Array<Number>> =
    this.nullItem(this.pgNumericArrayType().nullable())
fun SqTable.pgNumericArrayNotNull(columnName: String): SqTableColumn<List<BigDecimal?>, Array<Number>> =
    this.column(SqPgTypeHolderImpl.pgNumericArray, columnName)
fun SqTable.pgNumericArrayNullable(columnName: String): SqTableColumn<List<BigDecimal?>?, Array<Number>> =
    this.column(SqPgTypeHolderImpl.pgNumericArray.nullable(), columnName)

fun SqContext.pgRealType(): SqType<Float, Number> =
    this.pgTypeHolder().pgReal
@JvmName("pgRealParam__not_null")
fun SqContext.pgRealParam(value: Float): SqParameter<Float, Number> =
    this.param(this.pgRealType(), value)
@JvmName("pgRealParam__nullable")
fun SqContext.pgRealParam(value: Float?): SqParameter<Float?, Number> =
    this.param(this.pgRealType().nullable(), value)
fun SqContext.pgRealNull(): SqNull<Float, Number> =
    this.nullItem(this.pgRealType().nullable())
fun SqTable.pgRealNotNull(columnName: String): SqTableColumn<Float, Number> =
    this.column(SqPgTypeHolderImpl.pgReal, columnName)
fun SqTable.pgRealNullable(columnName: String): SqTableColumn<Float?, Number> =
    this.column(SqPgTypeHolderImpl.pgReal.nullable(), columnName)

fun SqContext.pgRealArrayType(): SqType<List<Float?>, Array<Number>> =
    this.pgTypeHolder().pgRealArray
@JvmName("pgRealArrayParam__not_null")
fun SqContext.pgRealArrayParam(value: List<Float?>): SqParameter<List<Float?>, Array<Number>> =
    this.param(this.pgRealArrayType(), value)
@JvmName("pgRealArrayParam__nullable")
fun SqContext.pgRealArrayParam(value: List<Float?>?): SqParameter<List<Float?>?, Array<Number>> =
    this.param(this.pgRealArrayType().nullable(), value)
fun SqContext.pgRealArrayNull(): SqNull<List<Float?>, Array<Number>> =
    this.nullItem(this.pgRealArrayType().nullable())
fun SqTable.pgRealArrayNotNull(columnName: String): SqTableColumn<List<Float?>, Array<Number>> =
    this.column(SqPgTypeHolderImpl.pgRealArray, columnName)
fun SqTable.pgRealArrayNullable(columnName: String): SqTableColumn<List<Float?>?, Array<Number>> =
    this.column(SqPgTypeHolderImpl.pgRealArray.nullable(), columnName)

fun SqContext.pgSmallIntType(): SqType<Short, Number> =
    this.pgTypeHolder().pgSmallInt
@JvmName("pgSmallIntParam__not_null")
fun SqContext.pgSmallIntParam(value: Short): SqParameter<Short, Number> =
    this.param(this.pgSmallIntType(), value)
@JvmName("pgSmallIntParam__nullable")
fun SqContext.pgSmallIntParam(value: Short?): SqParameter<Short?, Number> =
    this.param(this.pgSmallIntType().nullable(), value)
fun SqContext.pgSmallIntNull(): SqNull<Short, Number> =
    this.nullItem(this.pgSmallIntType().nullable())
fun SqTable.pgSmallIntNotNull(columnName: String): SqTableColumn<Short, Number> =
    this.column(SqPgTypeHolderImpl.pgSmallInt, columnName)
fun SqTable.pgSmallIntNullable(columnName: String): SqTableColumn<Short?, Number> =
    this.column(SqPgTypeHolderImpl.pgSmallInt.nullable(), columnName)

fun SqContext.pgSmallIntArrayType(): SqType<List<Short?>, Array<Number>> =
    this.pgTypeHolder().pgSmallIntArray
@JvmName("pgSmallIntArrayParam__not_null")
fun SqContext.pgSmallIntArrayParam(value: List<Short?>): SqParameter<List<Short?>, Array<Number>> =
    this.param(this.pgSmallIntArrayType(), value)
@JvmName("pgSmallIntArrayParam__nullable")
fun SqContext.pgSmallIntArrayParam(value: List<Short?>?): SqParameter<List<Short?>?, Array<Number>> =
    this.param(this.pgSmallIntArrayType().nullable(), value)
fun SqContext.pgSmallIntArrayNull(): SqNull<List<Short?>, Array<Number>> =
    this.nullItem(this.pgSmallIntArrayType().nullable())
fun SqTable.pgSmallIntArrayNotNull(columnName: String): SqTableColumn<List<Short?>, Array<Number>> =
    this.column(SqPgTypeHolderImpl.pgSmallIntArray, columnName)
fun SqTable.pgSmallIntArrayNullable(columnName: String): SqTableColumn<List<Short?>?, Array<Number>> =
    this.column(SqPgTypeHolderImpl.pgSmallIntArray.nullable(), columnName)
// endregion


// region Text types
fun SqContext.pgCharType(): SqType<String, String> =
    this.pgTypeHolder().pgChar
@JvmName("pgCharParam__not_null")
fun SqContext.pgCharParam(value: String): SqParameter<String, String> =
    this.param(this.pgCharType(), value)
@JvmName("pgCharParam__nullable")
fun SqContext.pgCharParam(value: String?): SqParameter<String?, String> =
    this.param(this.pgCharType().nullable(), value)
fun SqContext.pgCharNull(): SqNull<String, String> =
    this.nullItem(this.pgCharType().nullable())
fun SqTable.pgCharNotNull(columnName: String): SqTableColumn<String, String> =
    this.column(SqPgTypeHolderImpl.pgChar, columnName)
fun SqTable.pgCharNullable(columnName: String): SqTableColumn<String?, String> =
    this.column(SqPgTypeHolderImpl.pgChar.nullable(), columnName)

fun SqContext.pgCharArrayType(): SqType<List<String?>, Array<String>> =
    this.pgTypeHolder().pgCharArray
@JvmName("pgCharArrayParam__not_null")
fun SqContext.pgCharArrayParam(value: List<String?>): SqParameter<List<String?>, Array<String>> =
    this.param(this.pgCharArrayType(), value)
@JvmName("pgCharArrayParam__nullable")
fun SqContext.pgCharArrayParam(value: List<String?>?): SqParameter<List<String?>?, Array<String>> =
    this.param(this.pgCharArrayType().nullable(), value)
fun SqContext.pgCharArrayNull(): SqNull<List<String?>, Array<String>> =
    this.nullItem(this.pgCharArrayType().nullable())
fun SqTable.pgCharArrayNotNull(columnName: String): SqTableColumn<List<String?>, Array<String>> =
    this.column(SqPgTypeHolderImpl.pgCharArray, columnName)
fun SqTable.pgCharArrayNullable(columnName: String): SqTableColumn<List<String?>?, Array<String>> =
    this.column(SqPgTypeHolderImpl.pgCharArray.nullable(), columnName)

fun SqContext.pgCharacterType(): SqType<String, String> =
    this.pgTypeHolder().pgCharacter
@JvmName("pgCharacterParam__not_null")
fun SqContext.pgCharacterParam(value: String): SqParameter<String, String> =
    this.param(this.pgCharacterType(), value)
@JvmName("pgCharacterParam__nullable")
fun SqContext.pgCharacterParam(value: String?): SqParameter<String?, String> =
    this.param(this.pgCharacterType().nullable(), value)
fun SqContext.pgCharacterNull(): SqNull<String, String> =
    this.nullItem(this.pgCharacterType().nullable())
fun SqTable.pgCharacterNotNull(columnName: String): SqTableColumn<String, String> =
    this.column(SqPgTypeHolderImpl.pgCharacter, columnName)
fun SqTable.pgCharacterNullable(columnName: String): SqTableColumn<String?, String> =
    this.column(SqPgTypeHolderImpl.pgCharacter.nullable(), columnName)

fun SqContext.pgCharacterArrayType(): SqType<List<String?>, Array<String>> =
    this.pgTypeHolder().pgCharacterArray
@JvmName("pgCharacterArrayParam__not_null")
fun SqContext.pgCharacterArrayParam(value: List<String?>): SqParameter<List<String?>, Array<String>> =
    this.param(this.pgCharacterArrayType(), value)
@JvmName("pgCharacterArrayParam__nullable")
fun SqContext.pgCharacterArrayParam(value: List<String?>?): SqParameter<List<String?>?, Array<String>> =
    this.param(this.pgCharacterArrayType().nullable(), value)
fun SqContext.pgCharacterArrayNull(): SqNull<List<String?>, Array<String>> =
    this.nullItem(this.pgCharacterArrayType().nullable())
fun SqTable.pgCharacterArrayNotNull(columnName: String): SqTableColumn<List<String?>, Array<String>> =
    this.column(SqPgTypeHolderImpl.pgCharacterArray, columnName)
fun SqTable.pgCharacterArrayNullable(columnName: String): SqTableColumn<List<String?>?, Array<String>> =
    this.column(SqPgTypeHolderImpl.pgCharacterArray.nullable(), columnName)

fun SqContext.pgTextType(): SqType<String, String> =
    this.pgTypeHolder().pgText
@JvmName("pgTextParam__not_null")
fun SqContext.pgTextParam(value: String): SqParameter<String, String> =
    this.param(this.pgTextType(), value)
@JvmName("pgTextParam__nullable")
fun SqContext.pgTextParam(value: String?): SqParameter<String?, String> =
    this.param(this.pgTextType().nullable(), value)
fun SqContext.pgTextNull(): SqNull<String, String> =
    this.nullItem(this.pgTextType().nullable())
fun SqTable.pgTextNotNull(columnName: String): SqTableColumn<String, String> =
    this.column(SqPgTypeHolderImpl.pgText, columnName)
fun SqTable.pgTextNullable(columnName: String): SqTableColumn<String?, String> =
    this.column(SqPgTypeHolderImpl.pgText.nullable(), columnName)

fun SqContext.pgTextArrayType(): SqType<List<String?>, Array<String>> =
    this.pgTypeHolder().pgTextArray
@JvmName("pgTextArrayParam__not_null")
fun SqContext.pgTextArrayParam(value: List<String?>): SqParameter<List<String?>, Array<String>> =
    this.param(this.pgTextArrayType(), value)
@JvmName("pgTextArrayParam__nullable")
fun SqContext.pgTextArrayParam(value: List<String?>?): SqParameter<List<String?>?, Array<String>> =
    this.param(this.pgTextArrayType().nullable(), value)
fun SqContext.pgTextArrayNull(): SqNull<List<String?>, Array<String>> =
    this.nullItem(this.pgTextArrayType().nullable())
fun SqTable.pgTextArrayNotNull(columnName: String): SqTableColumn<List<String?>, Array<String>> =
    this.column(SqPgTypeHolderImpl.pgTextArray, columnName)
fun SqTable.pgTextArrayNullable(columnName: String): SqTableColumn<List<String?>?, Array<String>> =
    this.column(SqPgTypeHolderImpl.pgTextArray.nullable(), columnName)

fun SqContext.pgVarCharType(): SqType<String, String> =
    this.pgTypeHolder().pgVarChar
@JvmName("pgVarCharParam__not_null")
fun SqContext.pgVarCharParam(value: String): SqParameter<String, String> =
    this.param(this.pgVarCharType(), value)
@JvmName("pgVarCharParam__nullable")
fun SqContext.pgVarCharParam(value: String?): SqParameter<String?, String> =
    this.param(this.pgVarCharType().nullable(), value)
fun SqContext.pgVarCharNull(): SqNull<String, String> =
    this.nullItem(this.pgVarCharType().nullable())
fun SqTable.pgVarCharNotNull(columnName: String): SqTableColumn<String, String> =
    this.column(SqPgTypeHolderImpl.pgVarChar, columnName)
fun SqTable.pgVarCharNullable(columnName: String): SqTableColumn<String?, String> =
    this.column(SqPgTypeHolderImpl.pgVarChar.nullable(), columnName)

fun SqContext.pgVarCharArrayType(): SqType<List<String?>, Array<String>> =
    this.pgTypeHolder().pgVarCharArray
@JvmName("pgVarCharArrayParam__not_null")
fun SqContext.pgVarCharArrayParam(value: List<String?>): SqParameter<List<String?>, Array<String>> =
    this.param(this.pgVarCharArrayType(), value)
@JvmName("pgVarCharArrayParam__nullable")
fun SqContext.pgVarCharArrayParam(value: List<String?>?): SqParameter<List<String?>?, Array<String>> =
    this.param(this.pgVarCharArrayType().nullable(), value)
fun SqContext.pgVarCharArrayNull(): SqNull<List<String?>, Array<String>> =
    this.nullItem(this.pgVarCharArrayType().nullable())
fun SqTable.pgVarCharArrayNotNull(columnName: String): SqTableColumn<List<String?>, Array<String>> =
    this.column(SqPgTypeHolderImpl.pgVarCharArray, columnName)
fun SqTable.pgVarCharArrayNullable(columnName: String): SqTableColumn<List<String?>?, Array<String>> =
    this.column(SqPgTypeHolderImpl.pgVarCharArray.nullable(), columnName)
// endregion


// region Other JDBC types
fun SqContext.pgXmlType(): SqType<SQLXML, String> =
    this.pgTypeHolder().pgXml
@JvmName("pgXmlParam__not_null")
fun SqContext.pgXmlParam(value: SQLXML): SqParameter<SQLXML, String> =
    this.param(this.pgXmlType(), value)
@JvmName("pgXmlParam__nullable")
fun SqContext.pgXmlParam(value: SQLXML?): SqParameter<SQLXML?, String> =
    this.param(this.pgXmlType().nullable(), value)
fun SqContext.pgXmlNull(): SqNull<SQLXML, String> =
    this.nullItem(this.pgXmlType().nullable())
fun SqTable.pgXmlNotNull(columnName: String): SqTableColumn<SQLXML, String> =
    this.column(SqPgTypeHolderImpl.pgXml, columnName)
fun SqTable.pgXmlNullable(columnName: String): SqTableColumn<SQLXML?, String> =
    this.column(SqPgTypeHolderImpl.pgXml.nullable(), columnName)

fun SqContext.pgXmlArrayType(): SqType<List<SQLXML?>, Array<String>> =
    this.pgTypeHolder().pgXmlArray
@JvmName("pgXmlArrayParam__not_null")
fun SqContext.pgXmlArrayParam(value: List<SQLXML?>): SqParameter<List<SQLXML?>, Array<String>> =
    this.param(this.pgXmlArrayType(), value)
@JvmName("pgXmlArrayParam__nullable")
fun SqContext.pgXmlArrayParam(value: List<SQLXML?>?): SqParameter<List<SQLXML?>?, Array<String>> =
    this.param(this.pgXmlArrayType().nullable(), value)
fun SqContext.pgXmlArrayNull(): SqNull<List<SQLXML?>, Array<String>> =
    this.nullItem(this.pgXmlArrayType().nullable())
fun SqTable.pgXmlArrayNotNull(columnName: String): SqTableColumn<List<SQLXML?>, Array<String>> =
    this.column(SqPgTypeHolderImpl.pgXmlArray, columnName)
fun SqTable.pgXmlArrayNullable(columnName: String): SqTableColumn<List<SQLXML?>?, Array<String>> =
    this.column(SqPgTypeHolderImpl.pgXmlArray.nullable(), columnName)
// endregion


// region Other Postgresql types
fun SqContext.pgJsonType(): SqType<String, String> =
    this.pgTypeHolder().pgJson
@JvmName("pgJsonParam__not_null")
fun SqContext.pgJsonParam(value: String): SqParameter<String, String> =
    this.param(this.pgJsonType(), value)
@JvmName("pgJsonParam__nullable")
fun SqContext.pgJsonParam(value: String?): SqParameter<String?, String> =
    this.param(this.pgJsonType().nullable(), value)
fun SqContext.pgJsonNull(): SqNull<String, String> =
    this.nullItem(this.pgJsonType().nullable())
fun SqTable.pgJsonNotNull(columnName: String): SqTableColumn<String, String> =
    this.column(SqPgTypeHolderImpl.pgJson, columnName)
fun SqTable.pgJsonNullable(columnName: String): SqTableColumn<String?, String> =
    this.column(SqPgTypeHolderImpl.pgJson.nullable(), columnName)

fun SqContext.pgJsonArrayType(): SqType<List<String?>, Array<String>> =
    this.pgTypeHolder().pgJsonArray
@JvmName("pgJsonArrayParam__not_null")
fun SqContext.pgJsonArrayParam(value: List<String?>): SqParameter<List<String?>, Array<String>> =
    this.param(this.pgJsonArrayType(), value)
@JvmName("pgJsonArrayParam__nullable")
fun SqContext.pgJsonArrayParam(value: List<String?>?): SqParameter<List<String?>?, Array<String>> =
    this.param(this.pgJsonArrayType().nullable(), value)
fun SqContext.pgJsonArrayNull(): SqNull<List<String?>, Array<String>> =
    this.nullItem(this.pgJsonArrayType().nullable())
fun SqTable.pgJsonArrayNotNull(columnName: String): SqTableColumn<List<String?>, Array<String>> =
    this.column(SqPgTypeHolderImpl.pgJsonArray, columnName)
fun SqTable.pgJsonArrayNullable(columnName: String): SqTableColumn<List<String?>?, Array<String>> =
    this.column(SqPgTypeHolderImpl.pgJsonArray.nullable(), columnName)

fun SqContext.pgJsonBType(): SqType<String, String> =
    this.pgTypeHolder().pgJsonB
@JvmName("pgJsonBParam__not_null")
fun SqContext.pgJsonBParam(value: String): SqParameter<String, String> =
    this.param(this.pgJsonBType(), value)
@JvmName("pgJsonBParam__nullable")
fun SqContext.pgJsonBParam(value: String?): SqParameter<String?, String> =
    this.param(this.pgJsonBType().nullable(), value)
fun SqContext.pgJsonBNull(): SqNull<String, String> =
    this.nullItem(this.pgJsonBType().nullable())
fun SqTable.pgJsonBNotNull(columnName: String): SqTableColumn<String, String> =
    this.column(SqPgTypeHolderImpl.pgJsonB, columnName)
fun SqTable.pgJsonBNullable(columnName: String): SqTableColumn<String?, String> =
    this.column(SqPgTypeHolderImpl.pgJsonB.nullable(), columnName)

fun SqContext.pgJsonBArrayType(): SqType<List<String?>, Array<String>> =
    this.pgTypeHolder().pgJsonBArray
@JvmName("pgJsonBArrayParam__not_null")
fun SqContext.pgJsonBArrayParam(value: List<String?>): SqParameter<List<String?>, Array<String>> =
    this.param(this.pgJsonBArrayType(), value)
@JvmName("pgJsonBArrayParam__nullable")
fun SqContext.pgJsonBArrayParam(value: List<String?>?): SqParameter<List<String?>?, Array<String>> =
    this.param(this.pgJsonBArrayType().nullable(), value)
fun SqContext.pgJsonBArrayNull(): SqNull<List<String?>, Array<String>> =
    this.nullItem(this.pgJsonBArrayType().nullable())
fun SqTable.pgJsonBArrayNotNull(columnName: String): SqTableColumn<List<String?>, Array<String>> =
    this.column(SqPgTypeHolderImpl.pgJsonBArray, columnName)
fun SqTable.pgJsonBArrayNullable(columnName: String): SqTableColumn<List<String?>?, Array<String>> =
    this.column(SqPgTypeHolderImpl.pgJsonBArray.nullable(), columnName)

fun SqContext.pgMultiBitType(): SqType<BooleanArray, SqPgDbTypeBit> =
    this.pgTypeHolder().pgMultiBit
@JvmName("pgMultiBitParam__not_null")
fun SqContext.pgMultiBitParam(value: BooleanArray): SqParameter<BooleanArray, SqPgDbTypeBit> =
    this.param(this.pgMultiBitType(), value)
@JvmName("pgMultiBitParam__nullable")
fun SqContext.pgMultiBitParam(value: BooleanArray?): SqParameter<BooleanArray?, SqPgDbTypeBit> =
    this.param(this.pgMultiBitType().nullable(), value)
fun SqContext.pgMultiBitNull(): SqNull<BooleanArray, SqPgDbTypeBit> =
    this.nullItem(this.pgMultiBitType().nullable())
fun SqTable.pgMultiBitNotNull(columnName: String): SqTableColumn<BooleanArray, SqPgDbTypeBit> =
    this.column(SqPgTypeHolderImpl.pgMultiBit, columnName)
fun SqTable.pgMultiBitNullable(columnName: String): SqTableColumn<BooleanArray?, SqPgDbTypeBit> =
    this.column(SqPgTypeHolderImpl.pgMultiBit.nullable(), columnName)

fun SqContext.pgMultiBitArrayType(): SqType<List<BooleanArray?>, Array<SqPgDbTypeBit>> =
    this.pgTypeHolder().pgMultiBitArray
@JvmName("pgMultiBitArrayParam__not_null")
fun SqContext.pgMultiBitArrayParam(value: List<BooleanArray?>): SqParameter<List<BooleanArray?>, Array<SqPgDbTypeBit>> =
    this.param(this.pgMultiBitArrayType(), value)
@JvmName("pgMultiBitArrayParam__nullable")
fun SqContext.pgMultiBitArrayParam(value: List<BooleanArray?>?): SqParameter<List<BooleanArray?>?, Array<SqPgDbTypeBit>> =
    this.param(this.pgMultiBitArrayType().nullable(), value)
fun SqContext.pgMultiBitArrayNull(): SqNull<List<BooleanArray?>, Array<SqPgDbTypeBit>> =
    this.nullItem(this.pgMultiBitArrayType().nullable())
fun SqTable.pgMultiBitArrayNotNull(columnName: String): SqTableColumn<List<BooleanArray?>, Array<SqPgDbTypeBit>> =
    this.column(SqPgTypeHolderImpl.pgMultiBitArray, columnName)
fun SqTable.pgMultiBitArrayNullable(columnName: String): SqTableColumn<List<BooleanArray?>?, Array<SqPgDbTypeBit>> =
    this.column(SqPgTypeHolderImpl.pgMultiBitArray.nullable(), columnName)

fun SqContext.pgSingleBitType(): SqType<Boolean, SqPgDbTypeBit> =
    this.pgTypeHolder().pgSingleBit
@JvmName("pgSingleBitParam__not_null")
fun SqContext.pgSingleBitParam(value: Boolean): SqParameter<Boolean, SqPgDbTypeBit> =
    this.param(this.pgSingleBitType(), value)
@JvmName("pgSingleBitParam__nullable")
fun SqContext.pgSingleBitParam(value: Boolean?): SqParameter<Boolean?, SqPgDbTypeBit> =
    this.param(this.pgSingleBitType().nullable(), value)
fun SqContext.pgSingleBitNull(): SqNull<Boolean, SqPgDbTypeBit> =
    this.nullItem(this.pgSingleBitType().nullable())
fun SqTable.pgSingleBitNotNull(columnName: String): SqTableColumn<Boolean, SqPgDbTypeBit> =
    this.column(SqPgTypeHolderImpl.pgSingleBit, columnName)
fun SqTable.pgSingleBitNullable(columnName: String): SqTableColumn<Boolean?, SqPgDbTypeBit> =
    this.column(SqPgTypeHolderImpl.pgSingleBit.nullable(), columnName)

fun SqContext.pgSingleBitArrayType(): SqType<List<Boolean?>, Array<SqPgDbTypeBit>> =
    this.pgTypeHolder().pgSingleBitArray
@JvmName("pgSingleBitArrayParam__not_null")
fun SqContext.pgSingleBitArrayParam(value: List<Boolean?>): SqParameter<List<Boolean?>, Array<SqPgDbTypeBit>> =
    this.param(this.pgSingleBitArrayType(), value)
@JvmName("pgSingleBitArrayParam__nullable")
fun SqContext.pgSingleBitArrayParam(value: List<Boolean?>?): SqParameter<List<Boolean?>?, Array<SqPgDbTypeBit>> =
    this.param(this.pgSingleBitArrayType().nullable(), value)
fun SqContext.pgSingleBitArrayNull(): SqNull<List<Boolean?>, Array<SqPgDbTypeBit>> =
    this.nullItem(this.pgSingleBitArrayType().nullable())
fun SqTable.pgSingleBitArrayNotNull(columnName: String): SqTableColumn<List<Boolean?>, Array<SqPgDbTypeBit>> =
    this.column(SqPgTypeHolderImpl.pgSingleBitArray, columnName)
fun SqTable.pgSingleBitArrayNullable(columnName: String): SqTableColumn<List<Boolean?>?, Array<SqPgDbTypeBit>> =
    this.column(SqPgTypeHolderImpl.pgSingleBitArray.nullable(), columnName)

fun SqContext.pgVarBitType(): SqType<BooleanArray, SqPgDbTypeBit> =
    this.pgTypeHolder().pgVarBit
@JvmName("pgVarBitParam__ not_null")
fun SqContext.pgVarBitParam(value: BooleanArray): SqParameter<BooleanArray, SqPgDbTypeBit> =
    this.param(this.pgVarBitType(), value)
@JvmName("pgVarBitParam__nullable")
fun SqContext.pgVarBitParam(value: BooleanArray?): SqParameter<BooleanArray?, SqPgDbTypeBit> =
    this.param(this.pgVarBitType().nullable(), value)
fun SqContext.pgVarBitNull(): SqNull<BooleanArray, SqPgDbTypeBit> =
    this.nullItem(this.pgVarBitType().nullable())
fun SqTable.pgVarBitNotNull(columnName: String): SqTableColumn<BooleanArray, SqPgDbTypeBit> =
    this.column(SqPgTypeHolderImpl.pgVarBit, columnName)
fun SqTable.pgVarBitNullable(columnName: String): SqTableColumn<BooleanArray?, SqPgDbTypeBit> =
    this.column(SqPgTypeHolderImpl.pgVarBit.nullable(), columnName)

fun SqContext.pgVarBitArrayType(): SqType<List<BooleanArray?>, Array<SqPgDbTypeBit>> =
    this.pgTypeHolder().pgVarBitArray
@JvmName("pgVarBitArrayParam__not_null")
fun SqContext.pgVarBitArrayParam(value: List<BooleanArray?>): SqParameter<List<BooleanArray?>, Array<SqPgDbTypeBit>> =
    this.param(this.pgVarBitArrayType(), value)
@JvmName("pgVarBitArrayParam__nullable")
fun SqContext.pgVarBitArrayParam(value: List<BooleanArray?>?): SqParameter<List<BooleanArray?>?, Array<SqPgDbTypeBit>> =
    this.param(this.pgVarBitArrayType().nullable(), value)
fun SqContext.pgVarBitArrayNull(): SqNull<List<BooleanArray?>, Array<SqPgDbTypeBit>> =
    this.nullItem(this.pgVarBitArrayType().nullable())
fun SqTable.pgVarBitArrayNotNull(columnName: String): SqTableColumn<List<BooleanArray?>, Array<SqPgDbTypeBit>> =
    this.column(SqPgTypeHolderImpl.pgVarBitArray, columnName)
fun SqTable.pgVarBitArrayNullable(columnName: String): SqTableColumn<List<BooleanArray?>?, Array<SqPgDbTypeBit>> =
    this.column(SqPgTypeHolderImpl.pgVarBitArray.nullable(), columnName)
// endregion
