package me.ore.sq.pg

import me.ore.sq.*
import me.ore.sq.generic.SqGenericTypeHolder
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import java.sql.*
import java.time.*


interface SqPgTypeHolder: SqTypeHolder {
    // region Boolean types
    val pgSingleBit: SqType<Boolean, SqDbTypeBit>
    override val bit: SqType<Boolean, SqDbTypeBit>
        get() = this.pgSingleBit

    val pgBoolean: SqType<Boolean, Boolean>
    override val boolean: SqType<Boolean, Boolean>
        get() = this.pgBoolean
    // endregion


    // region Byte array types
    val pgBytea: SqType<ByteArray, ByteArray>
    override val binary: SqType<ByteArray, ByteArray>
        get() = this.pgBytea

    override val longVarBinary: SqType<ByteArray, ByteArray>
        get() = SqGenericTypeHolder.longVarBinary

    override val varBinary: SqType<ByteArray, ByteArray>
        get() = SqGenericTypeHolder.varBinary
    // endregion


    // region Date/time types
    val pgDate: SqType<LocalDate, Timestamp>
    override val date: SqType<LocalDate, Timestamp>
        get() = this.pgDate

    val pgDateAsDate: SqType<Date, Timestamp>
    override val dateAsDate: SqType<Date, Timestamp>
        get() = this.pgDateAsDate

    val pgTime: SqType<LocalTime, Time>
    override val time: SqType<LocalTime, Time>
        get() = this.pgTime

    val pgTimeAsTime: SqType<Time, Time>
    override val timeAsTime: SqType<Time, Time>
        get() = this.pgTimeAsTime

    val pgTimeTZ: SqType<OffsetTime, Time>
    override val timeWithTimeZone: SqType<OffsetTime, Time>
        get() = this.pgTimeTZ

    val pgTimestamp: SqType<LocalDateTime, Timestamp>
    override val timestamp: SqType<LocalDateTime, Timestamp>
        get() = this.pgTimestamp

    val pgTimestampAsTimestamp: SqType<Timestamp, Timestamp>
    override val timestampAsTimestamp: SqType<Timestamp, Timestamp>
        get() = this.pgTimestampAsTimestamp

    val pgTimestampTZ: SqType<OffsetDateTime, Timestamp>
    override val timestampWithTimeZone: SqType<OffsetDateTime, Timestamp>
        get() = this.pgTimestampTZ
    // endregion


    // region Number types
    val pgBigInt: SqType<Long, Number>
    override val bigInt: SqType<Long, Number>
        get() = this.pgBigInt

    val pgBigIntAsBigInteger: SqType<BigInteger, Number>
    override val bigIntAsBigInteger: SqType<BigInteger, Number>
        get() = this.pgBigIntAsBigInteger

    val pgDouble: SqType<Double, Number>
    override val double: SqType<Double, Number>
        get() = this.pgDouble
    override val float: SqType<Double, Number>
        get() = this.pgDouble

    val pgInteger: SqType<Int, Number>
    override val integer: SqType<Int, Number>
        get() = this.pgInteger

    val pgNumeric: SqType<BigDecimal, Number>
    override val numeric: SqType<BigDecimal, Number>
        get() = this.pgNumeric
    override val decimal: SqType<BigDecimal, Number>
        get() = this.pgNumeric

    val pgReal: SqType<Float, Number>
    override val real: SqType<Float, Number>
        get() = this.pgReal

    val pgSmallInt: SqType<Short, Number>
    override val smallInt: SqType<Short, Number>
        get() = this.pgSmallInt

    override val tinyInt: SqType<Byte, Number>
        get() = SqGenericTypeHolder.tinyInt

    override val javaNumber: SqType<Number, Number>
        get() = SqGenericTypeHolder.javaNumber
    // endregion


    // region Text types
    val pgChar: SqType<String, String>
    override val char: SqType<String, String>
        get() = this.pgChar

    val pgVarChar: SqType<String, String>
    override val varChar: SqType<String, String>
        get() = this.pgVarChar

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

    override val dataLink: SqType<URL, String>
        get() = SqGenericTypeHolder.dataLink
    override val ref: SqType<Ref, Ref>
        get() = SqGenericTypeHolder.ref
    override val rowId: SqType<RowId, RowId>
        get() = SqGenericTypeHolder.rowId
    // endregion
}


fun SqContext.pgTypeHolder(): SqPgTypeHolder =
    this[SqPgTypeHolder::class.java, SqPgTypeHolderImpl]


// region Boolean types
fun SqContext.pgSingleBitType(): SqType<Boolean, SqDbTypeBit> =
    this.pgTypeHolder().pgSingleBit
@JvmName("pgSingleBitParam__not_null")
fun SqContext.pgSingleBitParam(value: Boolean): SqParameter<Boolean, SqDbTypeBit> =
    this.param(this.pgSingleBitType(), value)
@JvmName("pgSingleBitParam__nullable")
fun SqContext.pgSingleBitParam(value: Boolean?): SqParameter<Boolean?, SqDbTypeBit> =
    this.param(this.pgSingleBitType().nullable(), value)
fun SqContext.pgSingleBitNull(): SqNull<Boolean, SqDbTypeBit> =
    this.nullItem(this.pgSingleBitType().nullable())
fun SqTable.pgSingleBitNotNull(columnName: String): SqTableColumn<Boolean, SqDbTypeBit> =
    this.column(SqPgTypeHolderImpl.pgSingleBit, columnName)
fun SqTable.pgSingleBitNullable(columnName: String): SqTableColumn<Boolean?, SqDbTypeBit> =
    this.column(SqPgTypeHolderImpl.pgSingleBit.nullable(), columnName)

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
// endregion
