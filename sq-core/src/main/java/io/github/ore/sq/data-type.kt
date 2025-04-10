@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqDataTypesImpl
import io.github.ore.sq.util.SqUtil
import java.io.InputStream
import java.io.Reader
import java.math.BigDecimal
import java.net.URL
import java.sql.*
import java.time.*


// region Util classes
/**
 * ```kotlin
 * fun get(source: ResultSet, index: Int): JAVA?
 * ```
 */
fun interface SqDataTypeReadAction<JAVA: Any> {
    operator fun get(source: ResultSet, index: Int): JAVA?
}

/**
 * ```kotlin
 * fun set(target: PreparedStatement, index: Int, value: JAVA)
 * ```
 */
fun interface SqDataTypeWriteAction<JAVA: Any> {
    operator fun set(target: PreparedStatement, index: Int, value: JAVA)
}

/**
 * ```kotlin
 * fun invoke(target: PreparedStatement, value: FROM): TO
 * ```
 */
fun interface SqDataTypeWriterConvertAction<FROM, TO> {
    operator fun invoke(target: PreparedStatement, value: FROM): TO
}

/**
 * ```kotlin
 * fun get(source: PreparedStatement, index: Int): JAVA
 * ```
 */
interface SqDataTypeReader<JAVA, DB: Any> {
    operator fun get(source: ResultSet, index: Int): JAVA

    val isNullable: Boolean
    val nullableReader: SqDataTypeReader<JAVA?, DB>
    val notNullReader: SqDataTypeReader<JAVA & Any, DB>
}

/**
 * ```kotlin
 * fun set(target: PreparedStatement, index: Int, value: JAVA?)
 * ```
 */
fun interface SqDataTypeWriter<JAVA: Any, DB: Any> {
    operator fun set(target: PreparedStatement, index: Int, value: JAVA?)
}

interface SqDataTypePack<JAVA: Any, DB: Any> {
    val notNullReader: SqDataTypeReader<JAVA, DB>
    val nullableReader: SqDataTypeReader<JAVA?, DB>
    val writer: SqDataTypeWriter<JAVA, DB>
}
// endregion


// region Type collection
interface SqDataTypes {
    // region Blob, clob
    val blob: SqDataTypePack<Blob, Blob>
    val blobStream: SqDataTypePack<InputStream, Blob>
    val clob: SqDataTypePack<Clob, Clob>
    val clobReader: SqDataTypePack<Reader, Clob>
    val nClob: SqDataTypePack<NClob, Clob>
    val nClobReader: SqDataTypePack<Reader, Clob>

    val jBlob: SqDataTypePack<Blob, Blob>
        get() = this.blob
    val jClob: SqDataTypePack<Clob, Clob>
        get() = this.clob
    val jInputStream: SqDataTypePack<InputStream, Blob>
        get() = this.blobStream
    val jNClob: SqDataTypePack<NClob, Clob>
        get() = this.nClob
    val jReader: SqDataTypePack<Reader, Clob>
        get() = this.clobReader
    // endregion


    // region Boolean
    val boolean: SqDataTypePack<Boolean, Boolean>

    val jBoolean: SqDataTypePack<Boolean, Boolean>
        get() = this.boolean
    // endregion


    // region Byte array
    val binary: SqDataTypePack<ByteArray, ByteArray>
    val longVarBinary: SqDataTypePack<ByteArray, ByteArray>
    val varBinary: SqDataTypePack<ByteArray, ByteArray>

    val jByteArray: SqDataTypePack<ByteArray, ByteArray>
        get() = this.varBinary
    // endregion


    // region Number
    val bigInt: SqDataTypePack<Long, Number>
    val decimal: SqDataTypePack<BigDecimal, Number>
    val double: SqDataTypePack<Double, Number>
    val float: SqDataTypePack<Double, Number>
    val integer: SqDataTypePack<Int, Number>
    val numeric: SqDataTypePack<BigDecimal, Number>
    val real: SqDataTypePack<Float, Number>
    val smallInt: SqDataTypePack<Short, Number>
    val tinyInt: SqDataTypePack<Byte, Number>

    val jBigDecimal: SqDataTypePack<BigDecimal, Number>
        get() = this.numeric
    val jByte: SqDataTypePack<Byte, Number>
        get() = this.tinyInt
    val jDouble: SqDataTypePack<Double, Number>
        get() = this.double
    val jFloat: SqDataTypePack<Float, Number>
        get() = this.real
    val jInt: SqDataTypePack<Int, Number>
        get() = this.integer
    val jLong: SqDataTypePack<Long, Number>
        get() = this.bigInt
    val jShort: SqDataTypePack<Short, Number>
        get() = this.smallInt
    // endregion


    // region Object
    val jObjectReaderPair: Pair<SqDataTypeReader<Any, Any>, SqDataTypeReader<Any?, Any>>
    // endregion


    // region String
    val char: SqDataTypePack<String, String>
    val longNVarChar: SqDataTypePack<String, String>
    val longVarChar: SqDataTypePack<String, String>
    val nChar: SqDataTypePack<String, String>
    val nVarChar: SqDataTypePack<String, String>
    val varChar: SqDataTypePack<String, String>

    val jString: SqDataTypePack<String, String>
        get() = this.varChar
    // endregion


    // region Temporal
    val date: SqDataTypePack<LocalDate, Timestamp>
    val dateJdbc: SqDataTypePack<Date, Timestamp>
    val time: SqDataTypePack<LocalTime, Time>
    val timeJdbc: SqDataTypePack<Time, Time>
    val timeTz: SqDataTypePack<OffsetTime, Time>
    val timeTzJdbc: SqDataTypePack<Time, Time>
    val timestamp: SqDataTypePack<LocalDateTime, Timestamp>
    val timestampJdbc: SqDataTypePack<Timestamp, Timestamp>
    val timestampTz: SqDataTypePack<OffsetDateTime, Timestamp>
    val timestampTzJdbc: SqDataTypePack<Timestamp, Timestamp>

    val jDate: SqDataTypePack<Date, Timestamp>
        get() = this.dateJdbc
    val jLocalDate: SqDataTypePack<LocalDate, Timestamp>
        get() = this.date
    val jLocalDateTime: SqDataTypePack<LocalDateTime, Timestamp>
        get() = this.timestamp
    val jLocalTime: SqDataTypePack<LocalTime, Time>
        get() = this.time
    val jOffsetDateTime: SqDataTypePack<OffsetDateTime, Timestamp>
        get() = this.timestampTz
    val jOffsetTime: SqDataTypePack<OffsetTime, Time>
        get() = this.timeTz
    val jTime: SqDataTypePack<Time, Time>
        get() = this.timeJdbc
    val jTimestamp: SqDataTypePack<Timestamp, Timestamp>
        get() = this.timestampJdbc
    // endregion


    // region Other
    val dataLink: SqDataTypePack<URL, String>
    val ref: SqDataTypePack<Ref, Ref>
    val rowId: SqDataTypePack<RowId, RowId>
    val sqlXml: SqDataTypePack<SQLXML, String>

    val jRef: SqDataTypePack<Ref, Ref>
        get() = this.ref
    val jRowId: SqDataTypePack<RowId, RowId>
        get() = this.rowId
    val jSqlXml: SqDataTypePack<SQLXML, String>
        get() = this.sqlXml
    val jUrl: SqDataTypePack<URL, String>
        get() = this.dataLink
    // endregion
}

fun <T : SqSettingsBuilder> T.dataTypes(value: SqDataTypes?): T =
    this.setValue(SqDataTypes::class.java, value)
val SqSettings.dataTypes: SqDataTypes
    get() = this.getValue(SqDataTypes::class.java) ?: SqDataTypesImpl.INSTANCE
val SqContext.dataTypes: SqDataTypes
    get() = this.settings.dataTypes
// endregion


// region NULLs / blob, clob
fun SqNullNs.blob(): SqNull<Blob, Blob> =
    this.nullItem(this.dataTypes.blob)
fun SqNullNs.blobStream(): SqNull<InputStream, Blob> =
    this.nullItem(this.dataTypes.blobStream)
fun SqNullNs.clob(): SqNull<Clob, Clob> =
    this.nullItem(this.dataTypes.clob)
fun SqNullNs.clobReader(): SqNull<Reader, Clob> =
    this.nullItem(this.dataTypes.clobReader)
fun SqNullNs.nClob(): SqNull<NClob, Clob> =
    this.nullItem(this.dataTypes.nClob)
fun SqNullNs.nClobReader(): SqNull<Reader, Clob> =
    this.nullItem(this.dataTypes.nClobReader)

fun SqNullNs.jBlob(): SqNull<Blob, Blob> =
    this.nullItem(this.dataTypes.jBlob)
fun SqNullNs.jClob(): SqNull<Clob, Clob> =
    this.nullItem(this.dataTypes.jClob)
fun SqNullNs.jInputStream(): SqNull<InputStream, Blob> =
    this.nullItem(this.dataTypes.jInputStream)
fun SqNullNs.jNClob(): SqNull<NClob, Clob> =
    this.nullItem(this.dataTypes.jNClob)
fun SqNullNs.jReader(): SqNull<Reader, Clob> =
    this.nullItem(this.dataTypes.jReader)
// endregion


// region NULLs / boolean
fun SqNullNs.boolean(): SqNull<Boolean, Boolean> =
    this.nullItem(this.dataTypes.boolean)

fun SqNullNs.jBoolean(): SqNull<Boolean, Boolean> =
    this.nullItem(this.dataTypes.jBoolean)
// endregion


// region NULLs / byte array
fun SqNullNs.binary(): SqNull<ByteArray, ByteArray> =
    this.nullItem(this.dataTypes.binary)
fun SqNullNs.longVarBinary(): SqNull<ByteArray, ByteArray> =
    this.nullItem(this.dataTypes.longVarBinary)
fun SqNullNs.varBinary(): SqNull<ByteArray, ByteArray> =
    this.nullItem(this.dataTypes.varBinary)

fun SqNullNs.jByteArray(): SqNull<ByteArray, ByteArray> =
    this.nullItem(this.dataTypes.jByteArray)
// endregion


// region NULLs / number
fun SqNullNs.bigInt(): SqNull<Long, Number> =
    this.nullItem(this.dataTypes.bigInt)
fun SqNullNs.decimal(): SqNull<BigDecimal, Number> =
    this.nullItem(this.dataTypes.decimal)
fun SqNullNs.double(): SqNull<Double, Number> =
    this.nullItem(this.dataTypes.double)
fun SqNullNs.float(): SqNull<Double, Number> =
    this.nullItem(this.dataTypes.float)
fun SqNullNs.integer(): SqNull<Int, Number> =
    this.nullItem(this.dataTypes.integer)
fun SqNullNs.numeric(): SqNull<BigDecimal, Number> =
    this.nullItem(this.dataTypes.numeric)
fun SqNullNs.real(): SqNull<Float, Number> =
    this.nullItem(this.dataTypes.real)
fun SqNullNs.smallInt(): SqNull<Short, Number> =
    this.nullItem(this.dataTypes.smallInt)
fun SqNullNs.tinyInt(): SqNull<Byte, Number> =
    this.nullItem(this.dataTypes.tinyInt)

fun SqNullNs.jBigDecimal(): SqNull<BigDecimal, Number> =
    this.nullItem(this.dataTypes.jBigDecimal)
fun SqNullNs.jByte(): SqNull<Byte, Number> =
    this.nullItem(this.dataTypes.jByte)
fun SqNullNs.jDouble(): SqNull<Double, Number> =
    this.nullItem(this.dataTypes.jDouble)
fun SqNullNs.jFloat(): SqNull<Float, Number> =
    this.nullItem(this.dataTypes.jFloat)
fun SqNullNs.jInt(): SqNull<Int, Number> =
    this.nullItem(this.dataTypes.jInt)
fun SqNullNs.jLong(): SqNull<Long, Number> =
    this.nullItem(this.dataTypes.jLong)
fun SqNullNs.jShort(): SqNull<Short, Number> =
    this.nullItem(this.dataTypes.jShort)
// endregion


// region NULLs / object
fun SqNullNs.jObject(): SqNull<Any, Any> =
    this.nullItem(this.dataTypes.jObjectReaderPair.second)
// endregion


// region NULLs / string
fun SqNullNs.char(): SqNull<String, String> =
    this.nullItem(this.dataTypes.char)
fun SqNullNs.longNVarChar(): SqNull<String, String> =
    this.nullItem(this.dataTypes.longNVarChar)
fun SqNullNs.longVarChar(): SqNull<String, String> =
    this.nullItem(this.dataTypes.longVarChar)
fun SqNullNs.nChar(): SqNull<String, String> =
    this.nullItem(this.dataTypes.nChar)
fun SqNullNs.nVarChar(): SqNull<String, String> =
    this.nullItem(this.dataTypes.nVarChar)
fun SqNullNs.varChar(): SqNull<String, String> =
    this.nullItem(this.dataTypes.varChar)

fun SqNullNs.jString(): SqNull<String, String> =
    this.nullItem(this.dataTypes.jString)
// endregion


// region NULLs / temporal
fun SqNullNs.date(): SqNull<LocalDate, Timestamp> =
    this.nullItem(this.dataTypes.date)
fun SqNullNs.dateJdbc(): SqNull<Date, Timestamp> =
    this.nullItem(this.dataTypes.dateJdbc)
fun SqNullNs.time(): SqNull<LocalTime, Time> =
    this.nullItem(this.dataTypes.time)
fun SqNullNs.timeJdbc(): SqNull<Time, Time> =
    this.nullItem(this.dataTypes.timeJdbc)
fun SqNullNs.timeTz(): SqNull<OffsetTime, Time> =
    this.nullItem(this.dataTypes.timeTz)
fun SqNullNs.timeTzJdbc(): SqNull<Time, Time> =
    this.nullItem(this.dataTypes.timeTzJdbc)
fun SqNullNs.timestamp(): SqNull<LocalDateTime, Timestamp> =
    this.nullItem(this.dataTypes.timestamp)
fun SqNullNs.timestampJdbc(): SqNull<Timestamp, Timestamp> =
    this.nullItem(this.dataTypes.timestampJdbc)
fun SqNullNs.timestampTz(): SqNull<OffsetDateTime, Timestamp> =
    this.nullItem(this.dataTypes.timestampTz)
fun SqNullNs.timestampTzJdbc(): SqNull<Timestamp, Timestamp> =
    this.nullItem(this.dataTypes.timestampTzJdbc)

fun SqNullNs.jDate(): SqNull<Date, Timestamp> =
    this.nullItem(this.dataTypes.jDate)
fun SqNullNs.jLocalDate(): SqNull<LocalDate, Timestamp> =
    this.nullItem(this.dataTypes.jLocalDate)
fun SqNullNs.jLocalDateTime(): SqNull<LocalDateTime, Timestamp> =
    this.nullItem(this.dataTypes.jLocalDateTime)
fun SqNullNs.jLocalTime(): SqNull<LocalTime, Time> =
    this.nullItem(this.dataTypes.jLocalTime)
fun SqNullNs.jOffsetDateTime(): SqNull<OffsetDateTime, Timestamp> =
    this.nullItem(this.dataTypes.jOffsetDateTime)
fun SqNullNs.jOffsetTime(): SqNull<OffsetTime, Time> =
    this.nullItem(this.dataTypes.jOffsetTime)
fun SqNullNs.jTime(): SqNull<Time, Time> =
    this.nullItem(this.dataTypes.jTime)
fun SqNullNs.jTimestamp(): SqNull<Timestamp, Timestamp> =
    this.nullItem(this.dataTypes.jTimestamp)
// endregion


// region NULLs / other
fun SqNullNs.dataLink(): SqNull<URL, String> =
    this.nullItem(this.dataTypes.dataLink)
fun SqNullNs.ref(): SqNull<Ref, Ref> =
    this.nullItem(this.dataTypes.ref)
fun SqNullNs.rowId(): SqNull<RowId, RowId> =
    this.nullItem(this.dataTypes.rowId)
fun SqNullNs.sqlXml(): SqNull<SQLXML, String> =
    this.nullItem(this.dataTypes.sqlXml)

fun SqNullNs.jRef(): SqNull<Ref, Ref> =
    this.nullItem(this.dataTypes.jRef)
fun SqNullNs.jRowId(): SqNull<RowId, RowId> =
    this.nullItem(this.dataTypes.jRowId)
fun SqNullNs.jSqlXml(): SqNull<SQLXML, String> =
    this.nullItem(this.dataTypes.jSqlXml)
fun SqNullNs.jUrl(): SqNull<URL, String> =
    this.nullItem(this.dataTypes.jUrl)
// endregion


// region Parameters / blob, clob
@JvmName("blob__notNull")
fun SqParameterNs.blob(value: Blob): SqParameter<Blob, Blob> =
    this.parameter(this.dataTypes.blob, value)
@JvmName("blob__nullable")
fun SqParameterNs.blob(value: Blob?): SqParameter<Blob?, Blob> =
    this.parameter(this.dataTypes.blob, value)

@JvmName("blobStream__notNull")
fun SqParameterNs.blobStream(value: InputStream): SqParameter<InputStream, Blob> =
    this.parameter(this.dataTypes.blobStream, value)
@JvmName("blobStream__nullable")
fun SqParameterNs.blobStream(value: InputStream?): SqParameter<InputStream?, Blob> =
    this.parameter(this.dataTypes.blobStream, value)

@JvmName("clob__notNull")
fun SqParameterNs.clob(value: Clob): SqParameter<Clob, Clob> =
    this.parameter(this.dataTypes.clob, value)
@JvmName("clob__nullable")
fun SqParameterNs.clob(value: Clob?): SqParameter<Clob?, Clob> =
    this.parameter(this.dataTypes.clob, value)

@JvmName("clobReader__notNull")
fun SqParameterNs.clobReader(value: Reader): SqParameter<Reader, Clob> =
    this.parameter(this.dataTypes.clobReader, value)
@JvmName("clobReader__nullable")
fun SqParameterNs.clobReader(value: Reader?): SqParameter<Reader?, Clob> =
    this.parameter(this.dataTypes.clobReader, value)

@JvmName("nClob__notNull")
fun SqParameterNs.nClob(value: NClob): SqParameter<NClob, Clob> =
    this.parameter(this.dataTypes.nClob, value)
@JvmName("nClob__nullable")
fun SqParameterNs.nClob(value: NClob?): SqParameter<NClob?, Clob> =
    this.parameter(this.dataTypes.nClob, value)

@JvmName("nClobReader__notNull")
fun SqParameterNs.nClobReader(value: Reader): SqParameter<Reader, Clob> =
    this.parameter(this.dataTypes.nClobReader, value)
@JvmName("nClobReader__nullable")
fun SqParameterNs.nClobReader(value: Reader?): SqParameter<Reader?, Clob> =
    this.parameter(this.dataTypes.nClobReader, value)


@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: Blob): SqParameter<Blob, Blob> =
    this.parameter(this.dataTypes.jBlob, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: Blob?): SqParameter<Blob?, Blob> =
    this.parameter(this.dataTypes.jBlob, value)

@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: Clob): SqParameter<Clob, Clob> =
    this.parameter(this.dataTypes.jClob, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: Clob?): SqParameter<Clob?, Clob> =
    this.parameter(this.dataTypes.jClob, value)

@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: InputStream): SqParameter<InputStream, Blob> =
    this.parameter(this.dataTypes.jInputStream, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: InputStream?): SqParameter<InputStream?, Blob> =
    this.parameter(this.dataTypes.jInputStream, value)

@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: NClob): SqParameter<NClob, Clob> =
    this.parameter(this.dataTypes.jNClob, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: NClob?): SqParameter<NClob?, Clob> =
    this.parameter(this.dataTypes.jNClob, value)

@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: Reader): SqParameter<Reader, Clob> =
    this.parameter(this.dataTypes.jReader, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: Reader?): SqParameter<Reader?, Clob> =
    this.parameter(this.dataTypes.jReader, value)
// endregion


// region Parameters / boolean
@JvmName("boolean__notNull")
fun SqParameterNs.boolean(value: Boolean): SqParameter<Boolean, Boolean> =
    this.parameter(this.dataTypes.boolean, value)
@JvmName("boolean__nullable")
fun SqParameterNs.boolean(value: Boolean?): SqParameter<Boolean?, Boolean> =
    this.parameter(this.dataTypes.boolean, value)


@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: Boolean): SqParameter<Boolean, Boolean> =
    this.parameter(this.dataTypes.jBoolean, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: Boolean?): SqParameter<Boolean?, Boolean> =
    this.parameter(this.dataTypes.jBoolean, value)
// endregion


// region Parameters / byte array
@JvmName("binary__notNull")
fun SqParameterNs.binary(value: ByteArray): SqParameter<ByteArray, ByteArray> =
    this.parameter(this.dataTypes.binary, value)
@JvmName("binary__nullable")
fun SqParameterNs.binary(value: ByteArray?): SqParameter<ByteArray?, ByteArray> =
    this.parameter(this.dataTypes.binary, value)

@JvmName("longVarBinary__notNull")
fun SqParameterNs.longVarBinary(value: ByteArray): SqParameter<ByteArray, ByteArray> =
    this.parameter(this.dataTypes.longVarBinary, value)
@JvmName("longVarBinary__nullable")
fun SqParameterNs.longVarBinary(value: ByteArray?): SqParameter<ByteArray?, ByteArray> =
    this.parameter(this.dataTypes.longVarBinary, value)

@JvmName("varBinary__notNull")
fun SqParameterNs.varBinary(value: ByteArray): SqParameter<ByteArray, ByteArray> =
    this.parameter(this.dataTypes.varBinary, value)
@JvmName("varBinary__nullable")
fun SqParameterNs.varBinary(value: ByteArray?): SqParameter<ByteArray?, ByteArray> =
    this.parameter(this.dataTypes.varBinary, value)


@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: ByteArray): SqParameter<ByteArray, ByteArray> =
    this.parameter(this.dataTypes.jByteArray, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: ByteArray?): SqParameter<ByteArray?, ByteArray> =
    this.parameter(this.dataTypes.jByteArray, value)
// endregion


// region Parameters / number
@JvmName("bigInt__notNull")
fun SqParameterNs.bigInt(value: Long): SqParameter<Long, Number> =
    this.parameter(this.dataTypes.bigInt, value)
@JvmName("bigInt__nullable")
fun SqParameterNs.bigInt(value: Long?): SqParameter<Long?, Number> =
    this.parameter(this.dataTypes.bigInt, value)

@JvmName("decimal__notNull")
fun SqParameterNs.decimal(value: BigDecimal): SqParameter<BigDecimal, Number> =
    this.parameter(this.dataTypes.decimal, value)
@JvmName("decimal__nullable")
fun SqParameterNs.decimal(value: BigDecimal?): SqParameter<BigDecimal?, Number> =
    this.parameter(this.dataTypes.decimal, value)

@JvmName("double__notNull")
fun SqParameterNs.double(value: Double): SqParameter<Double, Number> =
    this.parameter(this.dataTypes.double, value)
@JvmName("double__nullable")
fun SqParameterNs.double(value: Double?): SqParameter<Double?, Number> =
    this.parameter(this.dataTypes.double, value)

@JvmName("float__notNull")
fun SqParameterNs.float(value: Double): SqParameter<Double, Number> =
    this.parameter(this.dataTypes.float, value)
@JvmName("float__nullable")
fun SqParameterNs.float(value: Double?): SqParameter<Double?, Number> =
    this.parameter(this.dataTypes.float, value)

@JvmName("integer__notNull")
fun SqParameterNs.integer(value: Int): SqParameter<Int, Number> =
    this.parameter(this.dataTypes.integer, value)
@JvmName("integer__nullable")
fun SqParameterNs.integer(value: Int?): SqParameter<Int?, Number> =
    this.parameter(this.dataTypes.integer, value)

@JvmName("numeric__notNull")
fun SqParameterNs.numeric(value: BigDecimal): SqParameter<BigDecimal, Number> =
    this.parameter(this.dataTypes.numeric, value)
@JvmName("numeric__nullable")
fun SqParameterNs.numeric(value: BigDecimal?): SqParameter<BigDecimal?, Number> =
    this.parameter(this.dataTypes.numeric, value)

@JvmName("real__notNull")
fun SqParameterNs.real(value: Float): SqParameter<Float, Number> =
    this.parameter(this.dataTypes.real, value)
@JvmName("real__nullable")
fun SqParameterNs.real(value: Float?): SqParameter<Float?, Number> =
    this.parameter(this.dataTypes.real, value)

@JvmName("smallInt__notNull")
fun SqParameterNs.smallInt(value: Short): SqParameter<Short, Number> =
    this.parameter(this.dataTypes.smallInt, value)
@JvmName("smallInt__nullable")
fun SqParameterNs.smallInt(value: Short?): SqParameter<Short?, Number> =
    this.parameter(this.dataTypes.smallInt, value)

@JvmName("tinyInt__notNull")
fun SqParameterNs.tinyInt(value: Byte): SqParameter<Byte, Number> =
    this.parameter(this.dataTypes.tinyInt, value)
@JvmName("tinyInt__nullable")
fun SqParameterNs.tinyInt(value: Byte?): SqParameter<Byte?, Number> =
    this.parameter(this.dataTypes.tinyInt, value)


@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: BigDecimal): SqParameter<BigDecimal, Number> =
    this.parameter(this.dataTypes.jBigDecimal, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: BigDecimal?): SqParameter<BigDecimal?, Number> =
    this.parameter(this.dataTypes.jBigDecimal, value)

@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: Byte): SqParameter<Byte, Number> =
    this.parameter(this.dataTypes.jByte, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: Byte?): SqParameter<Byte?, Number> =
    this.parameter(this.dataTypes.jByte, value)

@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: Double): SqParameter<Double, Number> =
    this.parameter(this.dataTypes.jDouble, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: Double?): SqParameter<Double?, Number> =
    this.parameter(this.dataTypes.jDouble, value)

@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: Float): SqParameter<Float, Number> =
    this.parameter(this.dataTypes.jFloat, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: Float?): SqParameter<Float?, Number> =
    this.parameter(this.dataTypes.jFloat, value)

@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: Int): SqParameter<Int, Number> =
    this.parameter(this.dataTypes.jInt, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: Int?): SqParameter<Int?, Number> =
    this.parameter(this.dataTypes.jInt, value)

@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: Long): SqParameter<Long, Number> =
    this.parameter(this.dataTypes.jLong, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: Long?): SqParameter<Long?, Number> =
    this.parameter(this.dataTypes.jLong, value)

@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: Short): SqParameter<Short, Number> =
    this.parameter(this.dataTypes.jShort, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: Short?): SqParameter<Short?, Number> =
    this.parameter(this.dataTypes.jShort, value)
// endregion


// region Parameters / string
@JvmName("char__notNull")
fun SqParameterNs.char(value: String): SqParameter<String, String> =
    this.parameter(this.dataTypes.char, value)
@JvmName("char__nullable")
fun SqParameterNs.char(value: String?): SqParameter<String?, String> =
    this.parameter(this.dataTypes.char, value)

@JvmName("longNVarChar__notNull")
fun SqParameterNs.longNVarChar(value: String): SqParameter<String, String> =
    this.parameter(this.dataTypes.longNVarChar, value)
@JvmName("longNVarChar__nullable")
fun SqParameterNs.longNVarChar(value: String?): SqParameter<String?, String> =
    this.parameter(this.dataTypes.longNVarChar, value)

@JvmName("longVarChar__notNull")
fun SqParameterNs.longVarChar(value: String): SqParameter<String, String> =
    this.parameter(this.dataTypes.longVarChar, value)
@JvmName("longVarChar__nullable")
fun SqParameterNs.longVarChar(value: String?): SqParameter<String?, String> =
    this.parameter(this.dataTypes.longVarChar, value)

@JvmName("nChar__notNull")
fun SqParameterNs.nChar(value: String): SqParameter<String, String> =
    this.parameter(this.dataTypes.nChar, value)
@JvmName("nChar__nullable")
fun SqParameterNs.nChar(value: String?): SqParameter<String?, String> =
    this.parameter(this.dataTypes.nChar, value)

@JvmName("nVarChar__notNull")
fun SqParameterNs.nVarChar(value: String): SqParameter<String, String> =
    this.parameter(this.dataTypes.nVarChar, value)
@JvmName("nVarChar__nullable")
fun SqParameterNs.nVarChar(value: String?): SqParameter<String?, String> =
    this.parameter(this.dataTypes.nVarChar, value)

@JvmName("varChar__notNull")
fun SqParameterNs.varChar(value: String): SqParameter<String, String> =
    this.parameter(this.dataTypes.varChar, value)
@JvmName("varChar__nullable")
fun SqParameterNs.varChar(value: String?): SqParameter<String?, String> =
    this.parameter(this.dataTypes.varChar, value)


@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: String): SqParameter<String, String> =
    this.parameter(this.dataTypes.jString, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: String?): SqParameter<String?, String> =
    this.parameter(this.dataTypes.jString, value)
// endregion


// region Parameters / temporal
@JvmName("date__notNull")
fun SqParameterNs.date(value: LocalDate): SqParameter<LocalDate, Timestamp> =
    this.parameter(this.dataTypes.date, value)
@JvmName("date__nullable")
fun SqParameterNs.date(value: LocalDate?): SqParameter<LocalDate?, Timestamp> =
    this.parameter(this.dataTypes.date, value)

@JvmName("dateJdbc__notNull")
fun SqParameterNs.dateJdbc(value: Date): SqParameter<Date, Timestamp> =
    this.parameter(this.dataTypes.dateJdbc, value)
@JvmName("dateJdbc__nullable")
fun SqParameterNs.dateJdbc(value: Date?): SqParameter<Date?, Timestamp> =
    this.parameter(this.dataTypes.dateJdbc, value)

@JvmName("time__notNull")
fun SqParameterNs.time(value: LocalTime): SqParameter<LocalTime, Time> =
    this.parameter(this.dataTypes.time, value)
@JvmName("time__nullable")
fun SqParameterNs.time(value: LocalTime?): SqParameter<LocalTime?, Time> =
    this.parameter(this.dataTypes.time, value)

@JvmName("timeJdbc__notNull")
fun SqParameterNs.timeJdbc(value: Time): SqParameter<Time, Time> =
    this.parameter(this.dataTypes.timeJdbc, value)
@JvmName("timeJdbc__nullable")
fun SqParameterNs.timeJdbc(value: Time?): SqParameter<Time?, Time> =
    this.parameter(this.dataTypes.timeJdbc, value)

@JvmName("timeTz__notNull")
fun SqParameterNs.timeTz(value: OffsetTime): SqParameter<OffsetTime, Time> =
    this.parameter(this.dataTypes.timeTz, value)
@JvmName("timeTz__nullable")
fun SqParameterNs.timeTz(value: OffsetTime?): SqParameter<OffsetTime?, Time> =
    this.parameter(this.dataTypes.timeTz, value)

@JvmName("timeTzJdbc__notNull")
fun SqParameterNs.timeTzJdbc(value: Time): SqParameter<Time, Time> =
    this.parameter(this.dataTypes.timeTzJdbc, value)
@JvmName("timeTzJdbc__nullable")
fun SqParameterNs.timeTzJdbc(value: Time?): SqParameter<Time?, Time> =
    this.parameter(this.dataTypes.timeTzJdbc, value)

@JvmName("timestamp__notNull")
fun SqParameterNs.timestamp(value: LocalDateTime): SqParameter<LocalDateTime, Timestamp> =
    this.parameter(this.dataTypes.timestamp, value)
@JvmName("timestamp__nullable")
fun SqParameterNs.timestamp(value: LocalDateTime?): SqParameter<LocalDateTime?, Timestamp> =
    this.parameter(this.dataTypes.timestamp, value)

@JvmName("timestampJdbc__notNull")
fun SqParameterNs.timestampJdbc(value: Timestamp): SqParameter<Timestamp, Timestamp> =
    this.parameter(this.dataTypes.timestampJdbc, value)
@JvmName("timestampJdbc__nullable")
fun SqParameterNs.timestampJdbc(value: Timestamp?): SqParameter<Timestamp?, Timestamp> =
    this.parameter(this.dataTypes.timestampJdbc, value)

@JvmName("timestampTz__notNull")
fun SqParameterNs.timestampTz(value: OffsetDateTime): SqParameter<OffsetDateTime, Timestamp> =
    this.parameter(this.dataTypes.timestampTz, value)
@JvmName("timestampTz__nullable")
fun SqParameterNs.timestampTz(value: OffsetDateTime?): SqParameter<OffsetDateTime?, Timestamp> =
    this.parameter(this.dataTypes.timestampTz, value)

@JvmName("timestampTzJdbc__notNull")
fun SqParameterNs.timestampTzJdbc(value: Timestamp): SqParameter<Timestamp, Timestamp> =
    this.parameter(this.dataTypes.timestampTzJdbc, value)
@JvmName("timestampTzJdbc__nullable")
fun SqParameterNs.timestampTzJdbc(value: Timestamp?): SqParameter<Timestamp?, Timestamp> =
    this.parameter(this.dataTypes.timestampTzJdbc, value)


@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: Date): SqParameter<Date, Timestamp> =
    this.parameter(this.dataTypes.jDate, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: Date?): SqParameter<Date?, Timestamp> =
    this.parameter(this.dataTypes.jDate, value)

@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: LocalDate): SqParameter<LocalDate, Timestamp> =
    this.parameter(this.dataTypes.jLocalDate, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: LocalDate?): SqParameter<LocalDate?, Timestamp> =
    this.parameter(this.dataTypes.jLocalDate, value)

@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: LocalDateTime): SqParameter<LocalDateTime, Timestamp> =
    this.parameter(this.dataTypes.jLocalDateTime, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: LocalDateTime?): SqParameter<LocalDateTime?, Timestamp> =
    this.parameter(this.dataTypes.jLocalDateTime, value)

@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: LocalTime): SqParameter<LocalTime, Time> =
    this.parameter(this.dataTypes.jLocalTime, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: LocalTime?): SqParameter<LocalTime?, Time> =
    this.parameter(this.dataTypes.jLocalTime, value)

@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: OffsetDateTime): SqParameter<OffsetDateTime, Timestamp> =
    this.parameter(this.dataTypes.jOffsetDateTime, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: OffsetDateTime?): SqParameter<OffsetDateTime?, Timestamp> =
    this.parameter(this.dataTypes.jOffsetDateTime, value)

@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: OffsetTime): SqParameter<OffsetTime, Time> =
    this.parameter(this.dataTypes.jOffsetTime, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: OffsetTime?): SqParameter<OffsetTime?, Time> =
    this.parameter(this.dataTypes.jOffsetTime, value)

@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: Time): SqParameter<Time, Time> =
    this.parameter(this.dataTypes.jTime, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: Time?): SqParameter<Time?, Time> =
    this.parameter(this.dataTypes.jTime, value)

@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: Timestamp): SqParameter<Timestamp, Timestamp> =
    this.parameter(this.dataTypes.jTimestamp, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: Timestamp?): SqParameter<Timestamp?, Timestamp> =
    this.parameter(this.dataTypes.jTimestamp, value)
// endregion


// region Parameters / other
@JvmName("dataLink__notNull")
fun SqParameterNs.dataLink(value: URL): SqParameter<URL, String> =
    this.parameter(this.dataTypes.dataLink, value)
@JvmName("dataLink__nullable")
fun SqParameterNs.dataLink(value: URL?): SqParameter<URL?, String> =
    this.parameter(this.dataTypes.dataLink, value)

@JvmName("ref__notNull")
fun SqParameterNs.ref(value: Ref): SqParameter<Ref, Ref> =
    this.parameter(this.dataTypes.ref, value)
@JvmName("ref__nullable")
fun SqParameterNs.ref(value: Ref?): SqParameter<Ref?, Ref> =
    this.parameter(this.dataTypes.ref, value)

@JvmName("rowId__notNull")
fun SqParameterNs.rowId(value: RowId): SqParameter<RowId, RowId> =
    this.parameter(this.dataTypes.rowId, value)
@JvmName("rowId__nullable")
fun SqParameterNs.rowId(value: RowId?): SqParameter<RowId?, RowId> =
    this.parameter(this.dataTypes.rowId, value)

@JvmName("sqlXml__notNull")
fun SqParameterNs.sqlXml(value: SQLXML): SqParameter<SQLXML, String> =
    this.parameter(this.dataTypes.sqlXml, value)
@JvmName("sqlXml__nullable")
fun SqParameterNs.sqlXml(value: SQLXML?): SqParameter<SQLXML?, String> =
    this.parameter(this.dataTypes.sqlXml, value)


@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: Ref): SqParameter<Ref, Ref> =
    this.parameter(this.dataTypes.jRef, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: Ref?): SqParameter<Ref?, Ref> =
    this.parameter(this.dataTypes.jRef, value)

@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: RowId): SqParameter<RowId, RowId> =
    this.parameter(this.dataTypes.jRowId, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: RowId?): SqParameter<RowId?, RowId> =
    this.parameter(this.dataTypes.jRowId, value)

@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: SQLXML): SqParameter<SQLXML, String> =
    this.parameter(this.dataTypes.jSqlXml, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: SQLXML?): SqParameter<SQLXML?, String> =
    this.parameter(this.dataTypes.jSqlXml, value)

@JvmName("parameter__notNull")
fun SqParameterNs.parameter(value: URL): SqParameter<URL, String> =
    this.parameter(this.dataTypes.jUrl, value)
@JvmName("parameter__nullable")
fun SqParameterNs.parameter(value: URL?): SqParameter<URL?, String> =
    this.parameter(this.dataTypes.jUrl, value)
// endregion


// region Thread parameters / blob, clob
@JvmName("blob__notNull")
fun SqThreadParameterNs.blob(nullFlag: Any): SqThreadParameter<Blob, Blob> =
    this.parameter(this.dataTypes.blob, nullFlag)
@JvmName("blob__nullable")
fun SqThreadParameterNs.blob(nullFlag: Any?): SqThreadParameter<Blob?, Blob> =
    this.parameter(this.dataTypes.blob, nullFlag)

@JvmName("blobStream__notNull")
fun SqThreadParameterNs.blobStream(nullFlag: Any): SqThreadParameter<InputStream, Blob> =
    this.parameter(this.dataTypes.blobStream, nullFlag)
@JvmName("blobStream__nullable")
fun SqThreadParameterNs.blobStream(nullFlag: Any?): SqThreadParameter<InputStream?, Blob> =
    this.parameter(this.dataTypes.blobStream, nullFlag)

@JvmName("clob__notNull")
fun SqThreadParameterNs.clob(nullFlag: Any): SqThreadParameter<Clob, Clob> =
    this.parameter(this.dataTypes.clob, nullFlag)
@JvmName("clob__nullable")
fun SqThreadParameterNs.clob(nullFlag: Any?): SqThreadParameter<Clob?, Clob> =
    this.parameter(this.dataTypes.clob, nullFlag)

@JvmName("clobReader__notNull")
fun SqThreadParameterNs.clobReader(nullFlag: Any): SqThreadParameter<Reader, Clob> =
    this.parameter(this.dataTypes.clobReader, nullFlag)
@JvmName("clobReader__nullable")
fun SqThreadParameterNs.clobReader(nullFlag: Any?): SqThreadParameter<Reader?, Clob> =
    this.parameter(this.dataTypes.clobReader, nullFlag)

@JvmName("nClob__notNull")
fun SqThreadParameterNs.nClob(nullFlag: Any): SqThreadParameter<NClob, Clob> =
    this.parameter(this.dataTypes.nClob, nullFlag)
@JvmName("nClob__nullable")
fun SqThreadParameterNs.nClob(nullFlag: Any?): SqThreadParameter<NClob?, Clob> =
    this.parameter(this.dataTypes.nClob, nullFlag)

@JvmName("nClobReader__notNull")
fun SqThreadParameterNs.nClobReader(nullFlag: Any): SqThreadParameter<Reader, Clob> =
    this.parameter(this.dataTypes.nClobReader, nullFlag)
@JvmName("nClobReader__nullable")
fun SqThreadParameterNs.nClobReader(nullFlag: Any?): SqThreadParameter<Reader?, Clob> =
    this.parameter(this.dataTypes.nClobReader, nullFlag)


@JvmName("jBlob__notNull")
fun SqThreadParameterNs.jBlob(nullFlag: Any): SqThreadParameter<Blob, Blob> =
    this.parameter(this.dataTypes.jBlob, nullFlag)
@JvmName("jBlob__nullable")
fun SqThreadParameterNs.jBlob(nullFlag: Any?): SqThreadParameter<Blob?, Blob> =
    this.parameter(this.dataTypes.jBlob, nullFlag)

@JvmName("jInputStream__notNull")
fun SqThreadParameterNs.jInputStream(nullFlag: Any): SqThreadParameter<InputStream, Blob> =
    this.parameter(this.dataTypes.jInputStream, nullFlag)
@JvmName("jInputStream__nullable")
fun SqThreadParameterNs.jInputStream(nullFlag: Any?): SqThreadParameter<InputStream?, Blob> =
    this.parameter(this.dataTypes.jInputStream, nullFlag)

@JvmName("jClob__notNull")
fun SqThreadParameterNs.jClob(nullFlag: Any): SqThreadParameter<Clob, Clob> =
    this.parameter(this.dataTypes.jClob, nullFlag)
@JvmName("jClob__nullable")
fun SqThreadParameterNs.jClob(nullFlag: Any?): SqThreadParameter<Clob?, Clob> =
    this.parameter(this.dataTypes.jClob, nullFlag)

@JvmName("jNClob__notNull")
fun SqThreadParameterNs.jNClob(nullFlag: Any): SqThreadParameter<NClob, Clob> =
    this.parameter(this.dataTypes.jNClob, nullFlag)
@JvmName("jNClob__nullable")
fun SqThreadParameterNs.jNClob(nullFlag: Any?): SqThreadParameter<NClob?, Clob> =
    this.parameter(this.dataTypes.jNClob, nullFlag)

@JvmName("jReader__notNull")
fun SqThreadParameterNs.jReader(nullFlag: Any): SqThreadParameter<Reader, Clob> =
    this.parameter(this.dataTypes.jReader, nullFlag)
@JvmName("jReader__nullable")
fun SqThreadParameterNs.jReader(nullFlag: Any?): SqThreadParameter<Reader?, Clob> =
    this.parameter(this.dataTypes.jReader, nullFlag)
// endregion


// region Thread parameters / boolean
@JvmName("boolean__notNull")
fun SqThreadParameterNs.boolean(nullFlag: Any): SqThreadParameter<Boolean, Boolean> =
    this.parameter(this.dataTypes.boolean, nullFlag)
@JvmName("boolean__nullable")
fun SqThreadParameterNs.boolean(nullFlag: Any?): SqThreadParameter<Boolean?, Boolean> =
    this.parameter(this.dataTypes.boolean, nullFlag)


@JvmName("jBoolean__notNull")
fun SqThreadParameterNs.jBoolean(nullFlag: Any): SqThreadParameter<Boolean, Boolean> =
    this.parameter(this.dataTypes.jBoolean, nullFlag)
@JvmName("jBoolean__nullable")
fun SqThreadParameterNs.jBoolean(nullFlag: Any?): SqThreadParameter<Boolean?, Boolean> =
    this.parameter(this.dataTypes.jBoolean, nullFlag)
// endregion


// region Thread parameters / byte array
@JvmName("binary__notNull")
fun SqThreadParameterNs.binary(nullFlag: Any): SqThreadParameter<ByteArray, ByteArray> =
    this.parameter(this.dataTypes.binary, nullFlag)
@JvmName("binary__nullable")
fun SqThreadParameterNs.binary(nullFlag: Any?): SqThreadParameter<ByteArray?, ByteArray> =
    this.parameter(this.dataTypes.binary, nullFlag)

@JvmName("longVarBinary__notNull")
fun SqThreadParameterNs.longVarBinary(nullFlag: Any): SqThreadParameter<ByteArray, ByteArray> =
    this.parameter(this.dataTypes.longVarBinary, nullFlag)
@JvmName("longVarBinary__nullable")
fun SqThreadParameterNs.longVarBinary(nullFlag: Any?): SqThreadParameter<ByteArray?, ByteArray> =
    this.parameter(this.dataTypes.longVarBinary, nullFlag)

@JvmName("varBinary__notNull")
fun SqThreadParameterNs.varBinary(nullFlag: Any): SqThreadParameter<ByteArray, ByteArray> =
    this.parameter(this.dataTypes.varBinary, nullFlag)
@JvmName("varBinary__nullable")
fun SqThreadParameterNs.varBinary(nullFlag: Any?): SqThreadParameter<ByteArray?, ByteArray> =
    this.parameter(this.dataTypes.varBinary, nullFlag)


@JvmName("jByteArray__notNull")
fun SqThreadParameterNs.jByteArray(nullFlag: Any): SqThreadParameter<ByteArray, ByteArray> =
    this.parameter(this.dataTypes.jByteArray, nullFlag)
@JvmName("jByteArray__nullable")
fun SqThreadParameterNs.jByteArray(nullFlag: Any?): SqThreadParameter<ByteArray?, ByteArray> =
    this.parameter(this.dataTypes.jByteArray, nullFlag)
// endregion


// region Thread parameters / number
@JvmName("bigInt__notNull")
fun SqThreadParameterNs.bigInt(nullFlag: Any): SqThreadParameter<Long, Number> =
    this.parameter(this.dataTypes.bigInt, nullFlag)
@JvmName("bigInt__nullable")
fun SqThreadParameterNs.bigInt(nullFlag: Any?): SqThreadParameter<Long?, Number> =
    this.parameter(this.dataTypes.bigInt, nullFlag)

@JvmName("decimal__notNull")
fun SqThreadParameterNs.decimal(nullFlag: Any): SqThreadParameter<BigDecimal, Number> =
    this.parameter(this.dataTypes.decimal, nullFlag)
@JvmName("decimal__nullable")
fun SqThreadParameterNs.decimal(nullFlag: Any?): SqThreadParameter<BigDecimal?, Number> =
    this.parameter(this.dataTypes.decimal, nullFlag)

@JvmName("double__notNull")
fun SqThreadParameterNs.double(nullFlag: Any): SqThreadParameter<Double, Number> =
    this.parameter(this.dataTypes.double, nullFlag)
@JvmName("double__nullable")
fun SqThreadParameterNs.double(nullFlag: Any?): SqThreadParameter<Double?, Number> =
    this.parameter(this.dataTypes.double, nullFlag)

@JvmName("float__notNull")
fun SqThreadParameterNs.float(nullFlag: Any): SqThreadParameter<Double, Number> =
    this.parameter(this.dataTypes.float, nullFlag)
@JvmName("float__nullable")
fun SqThreadParameterNs.float(nullFlag: Any?): SqThreadParameter<Double?, Number> =
    this.parameter(this.dataTypes.float, nullFlag)

@JvmName("integer__notNull")
fun SqThreadParameterNs.integer(nullFlag: Any): SqThreadParameter<Int, Number> =
    this.parameter(this.dataTypes.integer, nullFlag)
@JvmName("integer__nullable")
fun SqThreadParameterNs.integer(nullFlag: Any?): SqThreadParameter<Int?, Number> =
    this.parameter(this.dataTypes.integer, nullFlag)

@JvmName("numeric__notNull")
fun SqThreadParameterNs.numeric(nullFlag: Any): SqThreadParameter<BigDecimal, Number> =
    this.parameter(this.dataTypes.numeric, nullFlag)
@JvmName("numeric__nullable")
fun SqThreadParameterNs.numeric(nullFlag: Any?): SqThreadParameter<BigDecimal?, Number> =
    this.parameter(this.dataTypes.numeric, nullFlag)

@JvmName("real__notNull")
fun SqThreadParameterNs.real(nullFlag: Any): SqThreadParameter<Float, Number> =
    this.parameter(this.dataTypes.real, nullFlag)
@JvmName("real__nullable")
fun SqThreadParameterNs.real(nullFlag: Any?): SqThreadParameter<Float?, Number> =
    this.parameter(this.dataTypes.real, nullFlag)

@JvmName("smallInt__notNull")
fun SqThreadParameterNs.smallInt(nullFlag: Any): SqThreadParameter<Short, Number> =
    this.parameter(this.dataTypes.smallInt, nullFlag)
@JvmName("smallInt__nullable")
fun SqThreadParameterNs.smallInt(nullFlag: Any?): SqThreadParameter<Short?, Number> =
    this.parameter(this.dataTypes.smallInt, nullFlag)

@JvmName("tinyInt__notNull")
fun SqThreadParameterNs.tinyInt(nullFlag: Any): SqThreadParameter<Byte, Number> =
    this.parameter(this.dataTypes.tinyInt, nullFlag)
@JvmName("tinyInt__nullable")
fun SqThreadParameterNs.tinyInt(nullFlag: Any?): SqThreadParameter<Byte?, Number> =
    this.parameter(this.dataTypes.tinyInt, nullFlag)


@JvmName("jBigDecimal__notNull")
fun SqThreadParameterNs.jBigDecimal(nullFlag: Any): SqThreadParameter<BigDecimal, Number> =
    this.parameter(this.dataTypes.jBigDecimal, nullFlag)
@JvmName("jBigDecimal__nullable")
fun SqThreadParameterNs.jBigDecimal(nullFlag: Any?): SqThreadParameter<BigDecimal?, Number> =
    this.parameter(this.dataTypes.jBigDecimal, nullFlag)

@JvmName("jByte__notNull")
fun SqThreadParameterNs.jByte(nullFlag: Any): SqThreadParameter<Byte, Number> =
    this.parameter(this.dataTypes.jByte, nullFlag)
@JvmName("jByte__nullable")
fun SqThreadParameterNs.jByte(nullFlag: Any?): SqThreadParameter<Byte?, Number> =
    this.parameter(this.dataTypes.jByte, nullFlag)

@JvmName("jDouble__notNull")
fun SqThreadParameterNs.jDouble(nullFlag: Any): SqThreadParameter<Double, Number> =
    this.parameter(this.dataTypes.jDouble, nullFlag)
@JvmName("jDouble__nullable")
fun SqThreadParameterNs.jDouble(nullFlag: Any?): SqThreadParameter<Double?, Number> =
    this.parameter(this.dataTypes.jDouble, nullFlag)

@JvmName("jFloat__notNull")
fun SqThreadParameterNs.jFloat(nullFlag: Any): SqThreadParameter<Float, Number> =
    this.parameter(this.dataTypes.jFloat, nullFlag)
@JvmName("jFloat__nullable")
fun SqThreadParameterNs.jFloat(nullFlag: Any?): SqThreadParameter<Float?, Number> =
    this.parameter(this.dataTypes.jFloat, nullFlag)

@JvmName("jInt__notNull")
fun SqThreadParameterNs.jInt(nullFlag: Any): SqThreadParameter<Int, Number> =
    this.parameter(this.dataTypes.jInt, nullFlag)
@JvmName("jInt__nullable")
fun SqThreadParameterNs.jInt(nullFlag: Any?): SqThreadParameter<Int?, Number> =
    this.parameter(this.dataTypes.jInt, nullFlag)

@JvmName("jLong__notNull")
fun SqThreadParameterNs.jLong(nullFlag: Any): SqThreadParameter<Long, Number> =
    this.parameter(this.dataTypes.jLong, nullFlag)
@JvmName("jLong__nullable")
fun SqThreadParameterNs.jLong(nullFlag: Any?): SqThreadParameter<Long?, Number> =
    this.parameter(this.dataTypes.jLong, nullFlag)

@JvmName("jShort__notNull")
fun SqThreadParameterNs.jShort(nullFlag: Any): SqThreadParameter<Short, Number> =
    this.parameter(this.dataTypes.jShort, nullFlag)
@JvmName("jShort__nullable")
fun SqThreadParameterNs.jShort(nullFlag: Any?): SqThreadParameter<Short?, Number> =
    this.parameter(this.dataTypes.jShort, nullFlag)
// endregion


// region Thread parameters / string
@JvmName("char__notNull")
fun SqThreadParameterNs.char(nullFlag: Any): SqThreadParameter<String, String> =
    this.parameter(this.dataTypes.char, nullFlag)
@JvmName("char__nullable")
fun SqThreadParameterNs.char(nullFlag: Any?): SqThreadParameter<String?, String> =
    this.parameter(this.dataTypes.char, nullFlag)

@JvmName("longNVarChar__notNull")
fun SqThreadParameterNs.longNVarChar(nullFlag: Any): SqThreadParameter<String, String> =
    this.parameter(this.dataTypes.longNVarChar, nullFlag)
@JvmName("longNVarChar__nullable")
fun SqThreadParameterNs.longNVarChar(nullFlag: Any?): SqThreadParameter<String?, String> =
    this.parameter(this.dataTypes.longNVarChar, nullFlag)

@JvmName("longVarChar__notNull")
fun SqThreadParameterNs.longVarChar(nullFlag: Any): SqThreadParameter<String, String> =
    this.parameter(this.dataTypes.longVarChar, nullFlag)
@JvmName("longVarChar__nullable")
fun SqThreadParameterNs.longVarChar(nullFlag: Any?): SqThreadParameter<String?, String> =
    this.parameter(this.dataTypes.longVarChar, nullFlag)

@JvmName("nChar__notNull")
fun SqThreadParameterNs.nChar(nullFlag: Any): SqThreadParameter<String, String> =
    this.parameter(this.dataTypes.nChar, nullFlag)
@JvmName("nChar__nullable")
fun SqThreadParameterNs.nChar(nullFlag: Any?): SqThreadParameter<String?, String> =
    this.parameter(this.dataTypes.nChar, nullFlag)

@JvmName("nVarChar__notNull")
fun SqThreadParameterNs.nVarChar(nullFlag: Any): SqThreadParameter<String, String> =
    this.parameter(this.dataTypes.nVarChar, nullFlag)
@JvmName("nVarChar__nullable")
fun SqThreadParameterNs.nVarChar(nullFlag: Any?): SqThreadParameter<String?, String> =
    this.parameter(this.dataTypes.nVarChar, nullFlag)

@JvmName("varChar__notNull")
fun SqThreadParameterNs.varChar(nullFlag: Any): SqThreadParameter<String, String> =
    this.parameter(this.dataTypes.varChar, nullFlag)
@JvmName("varChar__nullable")
fun SqThreadParameterNs.varChar(nullFlag: Any?): SqThreadParameter<String?, String> =
    this.parameter(this.dataTypes.varChar, nullFlag)


@JvmName("jString__notNull")
fun SqThreadParameterNs.jString(nullFlag: Any): SqThreadParameter<String, String> =
    this.parameter(this.dataTypes.jString, nullFlag)
@JvmName("jString__nullable")
fun SqThreadParameterNs.jString(nullFlag: Any?): SqThreadParameter<String?, String> =
    this.parameter(this.dataTypes.jString, nullFlag)
// endregion


// region Thread parameters / temporal
@JvmName("date__notNull")
fun SqThreadParameterNs.date(nullFlag: Any): SqThreadParameter<LocalDate, Timestamp> =
    this.parameter(this.dataTypes.date, nullFlag)
@JvmName("date__nullable")
fun SqThreadParameterNs.date(nullFlag: Any?): SqThreadParameter<LocalDate?, Timestamp> =
    this.parameter(this.dataTypes.date, nullFlag)

@JvmName("dateJdbc__notNull")
fun SqThreadParameterNs.dateJdbc(nullFlag: Any): SqThreadParameter<Date, Timestamp> =
    this.parameter(this.dataTypes.dateJdbc, nullFlag)
@JvmName("dateJdbc__nullable")
fun SqThreadParameterNs.dateJdbc(nullFlag: Any?): SqThreadParameter<Date?, Timestamp> =
    this.parameter(this.dataTypes.dateJdbc, nullFlag)

@JvmName("time__notNull")
fun SqThreadParameterNs.time(nullFlag: Any): SqThreadParameter<LocalTime, Time> =
    this.parameter(this.dataTypes.time, nullFlag)
@JvmName("time__nullable")
fun SqThreadParameterNs.time(nullFlag: Any?): SqThreadParameter<LocalTime?, Time> =
    this.parameter(this.dataTypes.time, nullFlag)

@JvmName("timeJdbc__notNull")
fun SqThreadParameterNs.timeJdbc(nullFlag: Any): SqThreadParameter<Time, Time> =
    this.parameter(this.dataTypes.timeJdbc, nullFlag)
@JvmName("timeJdbc__nullable")
fun SqThreadParameterNs.timeJdbc(nullFlag: Any?): SqThreadParameter<Time?, Time> =
    this.parameter(this.dataTypes.timeJdbc, nullFlag)

@JvmName("timeTz__notNull")
fun SqThreadParameterNs.timeTz(nullFlag: Any): SqThreadParameter<OffsetTime, Time> =
    this.parameter(this.dataTypes.timeTz, nullFlag)
@JvmName("timeTz__nullable")
fun SqThreadParameterNs.timeTz(nullFlag: Any?): SqThreadParameter<OffsetTime?, Time> =
    this.parameter(this.dataTypes.timeTz, nullFlag)

@JvmName("timeTzJdbc__notNull")
fun SqThreadParameterNs.timeTzJdbc(nullFlag: Any): SqThreadParameter<Time, Time> =
    this.parameter(this.dataTypes.timeTzJdbc, nullFlag)
@JvmName("timeTzJdbc__nullable")
fun SqThreadParameterNs.timeTzJdbc(nullFlag: Any?): SqThreadParameter<Time?, Time> =
    this.parameter(this.dataTypes.timeTzJdbc, nullFlag)

@JvmName("timestamp__notNull")
fun SqThreadParameterNs.timestamp(nullFlag: Any): SqThreadParameter<LocalDateTime, Timestamp> =
    this.parameter(this.dataTypes.timestamp, nullFlag)
@JvmName("timestamp__nullable")
fun SqThreadParameterNs.timestamp(nullFlag: Any?): SqThreadParameter<LocalDateTime?, Timestamp> =
    this.parameter(this.dataTypes.timestamp, nullFlag)

@JvmName("timestampJdbc__notNull")
fun SqThreadParameterNs.timestampJdbc(nullFlag: Any): SqThreadParameter<Timestamp, Timestamp> =
    this.parameter(this.dataTypes.timestampJdbc, nullFlag)
@JvmName("timestampJdbc__nullable")
fun SqThreadParameterNs.timestampJdbc(nullFlag: Any?): SqThreadParameter<Timestamp?, Timestamp> =
    this.parameter(this.dataTypes.timestampJdbc, nullFlag)

@JvmName("timestampTz__notNull")
fun SqThreadParameterNs.timestampTz(nullFlag: Any): SqThreadParameter<OffsetDateTime, Timestamp> =
    this.parameter(this.dataTypes.timestampTz, nullFlag)
@JvmName("timestampTz__nullable")
fun SqThreadParameterNs.timestampTz(nullFlag: Any?): SqThreadParameter<OffsetDateTime?, Timestamp> =
    this.parameter(this.dataTypes.timestampTz, nullFlag)

@JvmName("timestampTzJdbc__notNull")
fun SqThreadParameterNs.timestampTzJdbc(nullFlag: Any): SqThreadParameter<Timestamp, Timestamp> =
    this.parameter(this.dataTypes.timestampTzJdbc, nullFlag)
@JvmName("timestampTzJdbc__nullable")
fun SqThreadParameterNs.timestampTzJdbc(nullFlag: Any?): SqThreadParameter<Timestamp?, Timestamp> =
    this.parameter(this.dataTypes.timestampTzJdbc, nullFlag)


@JvmName("jDate__notNull")
fun SqThreadParameterNs.jDate(nullFlag: Any): SqThreadParameter<Date, Timestamp> =
    this.parameter(this.dataTypes.jDate, nullFlag)
@JvmName("jDate__nullable")
fun SqThreadParameterNs.jDate(nullFlag: Any?): SqThreadParameter<Date?, Timestamp> =
    this.parameter(this.dataTypes.jDate, nullFlag)

@JvmName("jLocalDate__notNull")
fun SqThreadParameterNs.jLocalDate(nullFlag: Any): SqThreadParameter<LocalDate, Timestamp> =
    this.parameter(this.dataTypes.jLocalDate, nullFlag)
@JvmName("jLocalDate__nullable")
fun SqThreadParameterNs.jLocalDate(nullFlag: Any?): SqThreadParameter<LocalDate?, Timestamp> =
    this.parameter(this.dataTypes.jLocalDate, nullFlag)

@JvmName("jLocalDateTime__notNull")
fun SqThreadParameterNs.jLocalDateTime(nullFlag: Any): SqThreadParameter<LocalDateTime, Timestamp> =
    this.parameter(this.dataTypes.jLocalDateTime, nullFlag)
@JvmName("jLocalDateTime__nullable")
fun SqThreadParameterNs.jLocalDateTime(nullFlag: Any?): SqThreadParameter<LocalDateTime?, Timestamp> =
    this.parameter(this.dataTypes.jLocalDateTime, nullFlag)

@JvmName("jLocalTime__notNull")
fun SqThreadParameterNs.jLocalTime(nullFlag: Any): SqThreadParameter<LocalTime, Time> =
    this.parameter(this.dataTypes.jLocalTime, nullFlag)
@JvmName("jLocalTime__nullable")
fun SqThreadParameterNs.jLocalTime(nullFlag: Any?): SqThreadParameter<LocalTime?, Time> =
    this.parameter(this.dataTypes.jLocalTime, nullFlag)

@JvmName("jOffsetDateTime__notNull")
fun SqThreadParameterNs.jOffsetDateTime(nullFlag: Any): SqThreadParameter<OffsetDateTime, Timestamp> =
    this.parameter(this.dataTypes.jOffsetDateTime, nullFlag)
@JvmName("jOffsetDateTime__nullable")
fun SqThreadParameterNs.jOffsetDateTime(nullFlag: Any?): SqThreadParameter<OffsetDateTime?, Timestamp> =
    this.parameter(this.dataTypes.jOffsetDateTime, nullFlag)

@JvmName("jOffsetTime__notNull")
fun SqThreadParameterNs.jOffsetTime(nullFlag: Any): SqThreadParameter<OffsetTime, Time> =
    this.parameter(this.dataTypes.jOffsetTime, nullFlag)
@JvmName("jOffsetTime__nullable")
fun SqThreadParameterNs.jOffsetTime(nullFlag: Any?): SqThreadParameter<OffsetTime?, Time> =
    this.parameter(this.dataTypes.jOffsetTime, nullFlag)

@JvmName("jTime__notNull")
fun SqThreadParameterNs.jTime(nullFlag: Any): SqThreadParameter<Time, Time> =
    this.parameter(this.dataTypes.jTime, nullFlag)
@JvmName("jTime__nullable")
fun SqThreadParameterNs.jTime(nullFlag: Any?): SqThreadParameter<Time?, Time> =
    this.parameter(this.dataTypes.jTime, nullFlag)

@JvmName("jTimestamp__notNull")
fun SqThreadParameterNs.jTimestamp(nullFlag: Any): SqThreadParameter<Timestamp, Timestamp> =
    this.parameter(this.dataTypes.jTimestamp, nullFlag)
@JvmName("jTimestamp__nullable")
fun SqThreadParameterNs.jTimestamp(nullFlag: Any?): SqThreadParameter<Timestamp?, Timestamp> =
    this.parameter(this.dataTypes.jTimestamp, nullFlag)
// endregion


// region Thread parameters / other
@JvmName("dataLink__notNull")
fun SqThreadParameterNs.dataLink(nullFlag: Any): SqThreadParameter<URL, String> =
    this.parameter(this.dataTypes.dataLink, nullFlag)
@JvmName("dataLink__nullable")
fun SqThreadParameterNs.dataLink(nullFlag: Any?): SqThreadParameter<URL?, String> =
    this.parameter(this.dataTypes.dataLink, nullFlag)

@JvmName("ref__notNull")
fun SqThreadParameterNs.ref(nullFlag: Any): SqThreadParameter<Ref, Ref> =
    this.parameter(this.dataTypes.ref, nullFlag)
@JvmName("ref__nullable")
fun SqThreadParameterNs.ref(nullFlag: Any?): SqThreadParameter<Ref?, Ref> =
    this.parameter(this.dataTypes.ref, nullFlag)

@JvmName("rowId__notNull")
fun SqThreadParameterNs.rowId(nullFlag: Any): SqThreadParameter<RowId, RowId> =
    this.parameter(this.dataTypes.rowId, nullFlag)
@JvmName("rowId__nullable")
fun SqThreadParameterNs.rowId(nullFlag: Any?): SqThreadParameter<RowId?, RowId> =
    this.parameter(this.dataTypes.rowId, nullFlag)

@JvmName("sqlXml__notNull")
fun SqThreadParameterNs.sqlXml(nullFlag: Any): SqThreadParameter<SQLXML, String> =
    this.parameter(this.dataTypes.sqlXml, nullFlag)
@JvmName("sqlXml__nullable")
fun SqThreadParameterNs.sqlXml(nullFlag: Any?): SqThreadParameter<SQLXML?, String> =
    this.parameter(this.dataTypes.sqlXml, nullFlag)


@JvmName("jRef__notNull")
fun SqThreadParameterNs.jRef(nullFlag: Any): SqThreadParameter<Ref, Ref> =
    this.parameter(this.dataTypes.jRef, nullFlag)
@JvmName("jRef__nullable")
fun SqThreadParameterNs.jRef(nullFlag: Any?): SqThreadParameter<Ref?, Ref> =
    this.parameter(this.dataTypes.jRef, nullFlag)

@JvmName("jRowId__notNull")
fun SqThreadParameterNs.jRowId(nullFlag: Any): SqThreadParameter<RowId, RowId> =
    this.parameter(this.dataTypes.jRowId, nullFlag)
@JvmName("jRowId__nullable")
fun SqThreadParameterNs.jRowId(nullFlag: Any?): SqThreadParameter<RowId?, RowId> =
    this.parameter(this.dataTypes.jRowId, nullFlag)

@JvmName("jSqlXml__notNull")
fun SqThreadParameterNs.jSqlXml(nullFlag: Any): SqThreadParameter<SQLXML, String> =
    this.parameter(this.dataTypes.jSqlXml, nullFlag)
@JvmName("jSqlXml__nullable")
fun SqThreadParameterNs.jSqlXml(nullFlag: Any?): SqThreadParameter<SQLXML?, String> =
    this.parameter(this.dataTypes.jSqlXml, nullFlag)

@JvmName("jUrl__notNull")
fun SqThreadParameterNs.jUrl(nullFlag: Any): SqThreadParameter<URL, String> =
    this.parameter(this.dataTypes.jUrl, nullFlag)
@JvmName("jUrl__nullable")
fun SqThreadParameterNs.jUrl(nullFlag: Any?): SqThreadParameter<URL?, String> =
    this.parameter(this.dataTypes.jUrl, nullFlag)
// endregion


// region Table columns / blob, clob
@JvmName("blob__notNull")
fun SqTableColumnHolder.blob(name: String, nullFlag: Any): SqTableColumn<Blob, Blob> =
    this.add(this.types.blob, name, nullFlag)
@JvmName("blob__nullable")
fun SqTableColumnHolder.blob(name: String, nullFlag: Any?): SqTableColumn<Blob?, Blob> =
    this.add(this.types.blob, name, nullFlag)

@JvmName("blobStream__notNull")
fun SqTableColumnHolder.blobStream(name: String, nullFlag: Any): SqTableColumn<InputStream, Blob> =
    this.add(this.types.blobStream, name, nullFlag)
@JvmName("blobStream__nullable")
fun SqTableColumnHolder.blobStream(name: String, nullFlag: Any?): SqTableColumn<InputStream?, Blob> =
    this.add(this.types.blobStream, name, nullFlag)

@JvmName("clob__notNull")
fun SqTableColumnHolder.clob(name: String, nullFlag: Any): SqTableColumn<Clob, Clob> =
    this.add(this.types.clob, name, nullFlag)
@JvmName("clob__nullable")
fun SqTableColumnHolder.clob(name: String, nullFlag: Any?): SqTableColumn<Clob?, Clob> =
    this.add(this.types.clob, name, nullFlag)

@JvmName("clobReader__notNull")
fun SqTableColumnHolder.clobReader(name: String, nullFlag: Any): SqTableColumn<Reader, Clob> =
    this.add(this.types.clobReader, name, nullFlag)
@JvmName("clobReader__nullable")
fun SqTableColumnHolder.clobReader(name: String, nullFlag: Any?): SqTableColumn<Reader?, Clob> =
    this.add(this.types.clobReader, name, nullFlag)

@JvmName("nClob__notNull")
fun SqTableColumnHolder.nClob(name: String, nullFlag: Any): SqTableColumn<NClob, Clob> =
    this.add(this.types.nClob, name, nullFlag)
@JvmName("nClob__nullable")
fun SqTableColumnHolder.nClob(name: String, nullFlag: Any?): SqTableColumn<NClob?, Clob> =
    this.add(this.types.nClob, name, nullFlag)

@JvmName("nClobReader__notNull")
fun SqTableColumnHolder.nClobReader(name: String, nullFlag: Any): SqTableColumn<Reader, Clob> =
    this.add(this.types.nClobReader, name, nullFlag)
@JvmName("nClobReader__nullable")
fun SqTableColumnHolder.nClobReader(name: String, nullFlag: Any?): SqTableColumn<Reader?, Clob> =
    this.add(this.types.nClobReader, name, nullFlag)


@JvmName("jBlob__notNull")
fun SqTableColumnHolder.jBlob(name: String, nullFlag: Any): SqTableColumn<Blob, Blob> =
    this.add(this.types.jBlob, name, nullFlag)
@JvmName("jBlob__nullable")
fun SqTableColumnHolder.jBlob(name: String, nullFlag: Any?): SqTableColumn<Blob?, Blob> =
    this.add(this.types.jBlob, name, nullFlag)

@JvmName("jClob__notNull")
fun SqTableColumnHolder.jClob(name: String, nullFlag: Any): SqTableColumn<Clob, Clob> =
    this.add(this.types.jClob, name, nullFlag)
@JvmName("jClob__nullable")
fun SqTableColumnHolder.jClob(name: String, nullFlag: Any?): SqTableColumn<Clob?, Clob> =
    this.add(this.types.jClob, name, nullFlag)

@JvmName("jInputStream__notNull")
fun SqTableColumnHolder.jInputStream(name: String, nullFlag: Any): SqTableColumn<InputStream, Blob> =
    this.add(this.types.jInputStream, name, nullFlag)
@JvmName("jInputStream__nullable")
fun SqTableColumnHolder.jInputStream(name: String, nullFlag: Any?): SqTableColumn<InputStream?, Blob> =
    this.add(this.types.jInputStream, name, nullFlag)

@JvmName("jNClob__notNull")
fun SqTableColumnHolder.jNClob(name: String, nullFlag: Any): SqTableColumn<NClob, Clob> =
    this.add(this.types.jNClob, name, nullFlag)
@JvmName("jNClob__nullable")
fun SqTableColumnHolder.jNClob(name: String, nullFlag: Any?): SqTableColumn<NClob?, Clob> =
    this.add(this.types.jNClob, name, nullFlag)

@JvmName("jReader__notNull")
fun SqTableColumnHolder.jReader(name: String, nullFlag: Any): SqTableColumn<Reader, Clob> =
    this.add(this.types.jReader, name, nullFlag)
@JvmName("jReader__nullable")
fun SqTableColumnHolder.jReader(name: String, nullFlag: Any?): SqTableColumn<Reader?, Clob> =
    this.add(this.types.jReader, name, nullFlag)
// endregion


// region Table columns / boolean
@JvmName("boolean__notNull")
fun SqTableColumnHolder.boolean(name: String, nullFlag: Any): SqTableColumn<Boolean, Boolean> =
    this.add(this.types.boolean, name, nullFlag)
@JvmName("boolean__nullable")
fun SqTableColumnHolder.boolean(name: String, nullFlag: Any?): SqTableColumn<Boolean?, Boolean> =
    this.add(this.types.boolean, name, nullFlag)


@JvmName("jBoolean__notNull")
fun SqTableColumnHolder.jBoolean(name: String, nullFlag: Any): SqTableColumn<Boolean, Boolean> =
    this.add(this.types.jBoolean, name, nullFlag)
@JvmName("jBoolean__nullable")
fun SqTableColumnHolder.jBoolean(name: String, nullFlag: Any?): SqTableColumn<Boolean?, Boolean> =
    this.add(this.types.jBoolean, name, nullFlag)
// endregion


// region Table columns / byte array
@JvmName("binary__notNull")
fun SqTableColumnHolder.binary(name: String, nullFlag: Any): SqTableColumn<ByteArray, ByteArray> =
    this.add(this.types.binary, name, nullFlag)
@JvmName("binary__nullable")
fun SqTableColumnHolder.binary(name: String, nullFlag: Any?): SqTableColumn<ByteArray?, ByteArray> =
    this.add(this.types.binary, name, nullFlag)

@JvmName("longVarBinary__notNull")
fun SqTableColumnHolder.longVarBinary(name: String, nullFlag: Any): SqTableColumn<ByteArray, ByteArray> =
    this.add(this.types.longVarBinary, name, nullFlag)
@JvmName("longVarBinary__nullable")
fun SqTableColumnHolder.longVarBinary(name: String, nullFlag: Any?): SqTableColumn<ByteArray?, ByteArray> =
    this.add(this.types.longVarBinary, name, nullFlag)

@JvmName("varBinary__notNull")
fun SqTableColumnHolder.varBinary(name: String, nullFlag: Any): SqTableColumn<ByteArray, ByteArray> =
    this.add(this.types.varBinary, name, nullFlag)
@JvmName("varBinary__nullable")
fun SqTableColumnHolder.varBinary(name: String, nullFlag: Any?): SqTableColumn<ByteArray?, ByteArray> =
    this.add(this.types.varBinary, name, nullFlag)


@JvmName("jByteArray__notNull")
fun SqTableColumnHolder.jByteArray(name: String, nullFlag: Any): SqTableColumn<ByteArray, ByteArray> =
    this.add(this.types.jByteArray, name, nullFlag)
@JvmName("jByteArray__nullable")
fun SqTableColumnHolder.jByteArray(name: String, nullFlag: Any?): SqTableColumn<ByteArray?, ByteArray> =
    this.add(this.types.jByteArray, name, nullFlag)
// endregion


// region Table columns / number
@JvmName("bigInt__notNull")
fun SqTableColumnHolder.bigInt(name: String, nullFlag: Any): SqTableColumn<Long, Number> =
    this.add(this.types.bigInt, name, nullFlag)
@JvmName("bigInt__nullable")
fun SqTableColumnHolder.bigInt(name: String, nullFlag: Any?): SqTableColumn<Long?, Number> =
    this.add(this.types.bigInt, name, nullFlag)

@JvmName("decimal__notNull")
fun SqTableColumnHolder.decimal(name: String, nullFlag: Any): SqTableColumn<BigDecimal, Number> =
    this.add(this.types.decimal, name, nullFlag)
@JvmName("decimal__nullable")
fun SqTableColumnHolder.decimal(name: String, nullFlag: Any?): SqTableColumn<BigDecimal?, Number> =
    this.add(this.types.decimal, name, nullFlag)

@JvmName("double__notNull")
fun SqTableColumnHolder.double(name: String, nullFlag: Any): SqTableColumn<Double, Number> =
    this.add(this.types.double, name, nullFlag)
@JvmName("double__nullable")
fun SqTableColumnHolder.double(name: String, nullFlag: Any?): SqTableColumn<Double?, Number> =
    this.add(this.types.double, name, nullFlag)

@JvmName("float__notNull")
fun SqTableColumnHolder.float(name: String, nullFlag: Any): SqTableColumn<Double, Number> =
    this.add(this.types.float, name, nullFlag)
@JvmName("float__nullable")
fun SqTableColumnHolder.float(name: String, nullFlag: Any?): SqTableColumn<Double?, Number> =
    this.add(this.types.float, name, nullFlag)

@JvmName("integer__notNull")
fun SqTableColumnHolder.integer(name: String, nullFlag: Any): SqTableColumn<Int, Number> =
    this.add(this.types.integer, name, nullFlag)
@JvmName("integer__nullable")
fun SqTableColumnHolder.integer(name: String, nullFlag: Any?): SqTableColumn<Int?, Number> =
    this.add(this.types.integer, name, nullFlag)

@JvmName("numeric__notNull")
fun SqTableColumnHolder.numeric(name: String, nullFlag: Any): SqTableColumn<BigDecimal, Number> =
    this.add(this.types.numeric, name, nullFlag)
@JvmName("numeric__nullable")
fun SqTableColumnHolder.numeric(name: String, nullFlag: Any?): SqTableColumn<BigDecimal?, Number> =
    this.add(this.types.numeric, name, nullFlag)

@JvmName("real__notNull")
fun SqTableColumnHolder.real(name: String, nullFlag: Any): SqTableColumn<Float, Number> =
    this.add(this.types.real, name, nullFlag)
@JvmName("real__nullable")
fun SqTableColumnHolder.real(name: String, nullFlag: Any?): SqTableColumn<Float?, Number> =
    this.add(this.types.real, name, nullFlag)

@JvmName("smallInt__notNull")
fun SqTableColumnHolder.smallInt(name: String, nullFlag: Any): SqTableColumn<Short, Number> =
    this.add(this.types.smallInt, name, nullFlag)
@JvmName("smallInt__nullable")
fun SqTableColumnHolder.smallInt(name: String, nullFlag: Any?): SqTableColumn<Short?, Number> =
    this.add(this.types.smallInt, name, nullFlag)

@JvmName("tinyInt__notNull")
fun SqTableColumnHolder.tinyInt(name: String, nullFlag: Any): SqTableColumn<Byte, Number> =
    this.add(this.types.tinyInt, name, nullFlag)
@JvmName("tinyInt__nullable")
fun SqTableColumnHolder.tinyInt(name: String, nullFlag: Any?): SqTableColumn<Byte?, Number> =
    this.add(this.types.tinyInt, name, nullFlag)


@JvmName("jBigDecimal__notNull")
fun SqTableColumnHolder.jBigDecimal(name: String, nullFlag: Any): SqTableColumn<BigDecimal, Number> =
    this.add(this.types.jBigDecimal, name, nullFlag)
@JvmName("jBigDecimal__nullable")
fun SqTableColumnHolder.jBigDecimal(name: String, nullFlag: Any?): SqTableColumn<BigDecimal?, Number> =
    this.add(this.types.jBigDecimal, name, nullFlag)

@JvmName("jByte__notNull")
fun SqTableColumnHolder.jByte(name: String, nullFlag: Any): SqTableColumn<Byte, Number> =
    this.add(this.types.jByte, name, nullFlag)
@JvmName("jByte__nullable")
fun SqTableColumnHolder.jByte(name: String, nullFlag: Any?): SqTableColumn<Byte?, Number> =
    this.add(this.types.jByte, name, nullFlag)

@JvmName("jDouble__notNull")
fun SqTableColumnHolder.jDouble(name: String, nullFlag: Any): SqTableColumn<Double, Number> =
    this.add(this.types.jDouble, name, nullFlag)
@JvmName("jDouble__nullable")
fun SqTableColumnHolder.jDouble(name: String, nullFlag: Any?): SqTableColumn<Double?, Number> =
    this.add(this.types.jDouble, name, nullFlag)

@JvmName("jFloat__notNull")
fun SqTableColumnHolder.jFloat(name: String, nullFlag: Any): SqTableColumn<Float, Number> =
    this.add(this.types.jFloat, name, nullFlag)
@JvmName("jFloat__nullable")
fun SqTableColumnHolder.jFloat(name: String, nullFlag: Any?): SqTableColumn<Float?, Number> =
    this.add(this.types.jFloat, name, nullFlag)

@JvmName("jInt__notNull")
fun SqTableColumnHolder.jInt(name: String, nullFlag: Any): SqTableColumn<Int, Number> =
    this.add(this.types.jInt, name, nullFlag)
@JvmName("jInt__nullable")
fun SqTableColumnHolder.jInt(name: String, nullFlag: Any?): SqTableColumn<Int?, Number> =
    this.add(this.types.jInt, name, nullFlag)

@JvmName("jLong__notNull")
fun SqTableColumnHolder.jLong(name: String, nullFlag: Any): SqTableColumn<Long, Number> =
    this.add(this.types.jLong, name, nullFlag)
@JvmName("jLong__nullable")
fun SqTableColumnHolder.jLong(name: String, nullFlag: Any?): SqTableColumn<Long?, Number> =
    this.add(this.types.jLong, name, nullFlag)

@JvmName("jShort__notNull")
fun SqTableColumnHolder.jShort(name: String, nullFlag: Any): SqTableColumn<Short, Number> =
    this.add(this.types.jShort, name, nullFlag)
@JvmName("jShort__nullable")
fun SqTableColumnHolder.jShort(name: String, nullFlag: Any?): SqTableColumn<Short?, Number> =
    this.add(this.types.jShort, name, nullFlag)
// endregion


// region Table columns / string
@JvmName("char__notNull")
fun SqTableColumnHolder.char(name: String, nullFlag: Any): SqTableColumn<String, String> =
    this.add(this.types.char, name, nullFlag)
@JvmName("char__nullable")
fun SqTableColumnHolder.char(name: String, nullFlag: Any?): SqTableColumn<String?, String> =
    this.add(this.types.char, name, nullFlag)

@JvmName("longNVarChar__notNull")
fun SqTableColumnHolder.longNVarChar(name: String, nullFlag: Any): SqTableColumn<String, String> =
    this.add(this.types.longNVarChar, name, nullFlag)
@JvmName("longNVarChar__nullable")
fun SqTableColumnHolder.longNVarChar(name: String, nullFlag: Any?): SqTableColumn<String?, String> =
    this.add(this.types.longNVarChar, name, nullFlag)

@JvmName("longVarChar__notNull")
fun SqTableColumnHolder.longVarChar(name: String, nullFlag: Any): SqTableColumn<String, String> =
    this.add(this.types.longVarChar, name, nullFlag)
@JvmName("longVarChar__nullable")
fun SqTableColumnHolder.longVarChar(name: String, nullFlag: Any?): SqTableColumn<String?, String> =
    this.add(this.types.longVarChar, name, nullFlag)

@JvmName("nChar__notNull")
fun SqTableColumnHolder.nChar(name: String, nullFlag: Any): SqTableColumn<String, String> =
    this.add(this.types.nChar, name, nullFlag)
@JvmName("nChar__nullable")
fun SqTableColumnHolder.nChar(name: String, nullFlag: Any?): SqTableColumn<String?, String> =
    this.add(this.types.nChar, name, nullFlag)

@JvmName("nVarChar__notNull")
fun SqTableColumnHolder.nVarChar(name: String, nullFlag: Any): SqTableColumn<String, String> =
    this.add(this.types.nVarChar, name, nullFlag)
@JvmName("nVarChar__nullable")
fun SqTableColumnHolder.nVarChar(name: String, nullFlag: Any?): SqTableColumn<String?, String> =
    this.add(this.types.nVarChar, name, nullFlag)

@JvmName("varChar__notNull")
fun SqTableColumnHolder.varChar(name: String, nullFlag: Any): SqTableColumn<String, String> =
    this.add(this.types.varChar, name, nullFlag)
@JvmName("varChar__nullable")
fun SqTableColumnHolder.varChar(name: String, nullFlag: Any?): SqTableColumn<String?, String> =
    this.add(this.types.varChar, name, nullFlag)


@JvmName("jString__notNull")
fun SqTableColumnHolder.jString(name: String, nullFlag: Any): SqTableColumn<String, String> =
    this.add(this.types.jString, name, nullFlag)
@JvmName("jString__nullable")
fun SqTableColumnHolder.jString(name: String, nullFlag: Any?): SqTableColumn<String?, String> =
    this.add(this.types.jString, name, nullFlag)
// endregion


// region Table columns / temporal
@JvmName("date__notNull")
fun SqTableColumnHolder.date(name: String, nullFlag: Any): SqTableColumn<LocalDate, Timestamp> =
    this.add(this.types.date, name, nullFlag)
@JvmName("date__nullable")
fun SqTableColumnHolder.date(name: String, nullFlag: Any?): SqTableColumn<LocalDate?, Timestamp> =
    this.add(this.types.date, name, nullFlag)

@JvmName("dateJdbc__notNull")
fun SqTableColumnHolder.dateJdbc(name: String, nullFlag: Any): SqTableColumn<Date, Timestamp> =
    this.add(this.types.dateJdbc, name, nullFlag)
@JvmName("dateJdbc__nullable")
fun SqTableColumnHolder.dateJdbc(name: String, nullFlag: Any?): SqTableColumn<Date?, Timestamp> =
    this.add(this.types.dateJdbc, name, nullFlag)

@JvmName("time__notNull")
fun SqTableColumnHolder.time(name: String, nullFlag: Any): SqTableColumn<LocalTime, Time> =
    this.add(this.types.time, name, nullFlag)
@JvmName("time__nullable")
fun SqTableColumnHolder.time(name: String, nullFlag: Any?): SqTableColumn<LocalTime?, Time> =
    this.add(this.types.time, name, nullFlag)

@JvmName("timeJdbc__notNull")
fun SqTableColumnHolder.timeJdbc(name: String, nullFlag: Any): SqTableColumn<Time, Time> =
    this.add(this.types.timeJdbc, name, nullFlag)
@JvmName("timeJdbc__nullable")
fun SqTableColumnHolder.timeJdbc(name: String, nullFlag: Any?): SqTableColumn<Time?, Time> =
    this.add(this.types.timeJdbc, name, nullFlag)

@JvmName("timeTz__notNull")
fun SqTableColumnHolder.timeTz(name: String, nullFlag: Any): SqTableColumn<OffsetTime, Time> =
    this.add(this.types.timeTz, name, nullFlag)
@JvmName("timeTz__nullable")
fun SqTableColumnHolder.timeTz(name: String, nullFlag: Any?): SqTableColumn<OffsetTime?, Time> =
    this.add(this.types.timeTz, name, nullFlag)

@JvmName("timeTzJdbc__notNull")
fun SqTableColumnHolder.timeTzJdbc(name: String, nullFlag: Any): SqTableColumn<Time, Time> =
    this.add(this.types.timeTzJdbc, name, nullFlag)
@JvmName("timeTzJdbc__nullable")
fun SqTableColumnHolder.timeTzJdbc(name: String, nullFlag: Any?): SqTableColumn<Time?, Time> =
    this.add(this.types.timeTzJdbc, name, nullFlag)

@JvmName("timestamp__notNull")
fun SqTableColumnHolder.timestamp(name: String, nullFlag: Any): SqTableColumn<LocalDateTime, Timestamp> =
    this.add(this.types.timestamp, name, nullFlag)
@JvmName("timestamp__nullable")
fun SqTableColumnHolder.timestamp(name: String, nullFlag: Any?): SqTableColumn<LocalDateTime?, Timestamp> =
    this.add(this.types.timestamp, name, nullFlag)

@JvmName("timestampJdbc__notNull")
fun SqTableColumnHolder.timestampJdbc(name: String, nullFlag: Any): SqTableColumn<Timestamp, Timestamp> =
    this.add(this.types.timestampJdbc, name, nullFlag)
@JvmName("timestampJdbc__nullable")
fun SqTableColumnHolder.timestampJdbc(name: String, nullFlag: Any?): SqTableColumn<Timestamp?, Timestamp> =
    this.add(this.types.timestampJdbc, name, nullFlag)

@JvmName("timestampTz__notNull")
fun SqTableColumnHolder.timestampTz(name: String, nullFlag: Any): SqTableColumn<OffsetDateTime, Timestamp> =
    this.add(this.types.timestampTz, name, nullFlag)
@JvmName("timestampTz__nullable")
fun SqTableColumnHolder.timestampTz(name: String, nullFlag: Any?): SqTableColumn<OffsetDateTime?, Timestamp> =
    this.add(this.types.timestampTz, name, nullFlag)

@JvmName("timestampTzJdbc__notNull")
fun SqTableColumnHolder.timestampTzJdbc(name: String, nullFlag: Any): SqTableColumn<Timestamp, Timestamp> =
    this.add(this.types.timestampTzJdbc, name, nullFlag)
@JvmName("timestampTzJdbc__nullable")
fun SqTableColumnHolder.timestampTzJdbc(name: String, nullFlag: Any?): SqTableColumn<Timestamp?, Timestamp> =
    this.add(this.types.timestampTzJdbc, name, nullFlag)


@JvmName("jDate__notNull")
fun SqTableColumnHolder.jDate(name: String, nullFlag: Any): SqTableColumn<Date, Timestamp> =
    this.add(this.types.jDate, name, nullFlag)
@JvmName("jDate__nullable")
fun SqTableColumnHolder.jDate(name: String, nullFlag: Any?): SqTableColumn<Date?, Timestamp> =
    this.add(this.types.jDate, name, nullFlag)

@JvmName("jLocalDate__notNull")
fun SqTableColumnHolder.jLocalDate(name: String, nullFlag: Any): SqTableColumn<LocalDate, Timestamp> =
    this.add(this.types.jLocalDate, name, nullFlag)
@JvmName("jLocalDate__nullable")
fun SqTableColumnHolder.jLocalDate(name: String, nullFlag: Any?): SqTableColumn<LocalDate?, Timestamp> =
    this.add(this.types.jLocalDate, name, nullFlag)

@JvmName("jLocalDateTime__notNull")
fun SqTableColumnHolder.jLocalDateTime(name: String, nullFlag: Any): SqTableColumn<LocalDateTime, Timestamp> =
    this.add(this.types.jLocalDateTime, name, nullFlag)
@JvmName("jLocalDateTime__nullable")
fun SqTableColumnHolder.jLocalDateTime(name: String, nullFlag: Any?): SqTableColumn<LocalDateTime?, Timestamp> =
    this.add(this.types.jLocalDateTime, name, nullFlag)

@JvmName("jLocalTime__notNull")
fun SqTableColumnHolder.jLocalTime(name: String, nullFlag: Any): SqTableColumn<LocalTime, Time> =
    this.add(this.types.jLocalTime, name, nullFlag)
@JvmName("jLocalTime__nullable")
fun SqTableColumnHolder.jLocalTime(name: String, nullFlag: Any?): SqTableColumn<LocalTime?, Time> =
    this.add(this.types.jLocalTime, name, nullFlag)

@JvmName("jOffsetDateTime__notNull")
fun SqTableColumnHolder.jOffsetDateTime(name: String, nullFlag: Any): SqTableColumn<OffsetDateTime, Timestamp> =
    this.add(this.types.jOffsetDateTime, name, nullFlag)
@JvmName("jOffsetDateTime__nullable")
fun SqTableColumnHolder.jOffsetDateTime(name: String, nullFlag: Any?): SqTableColumn<OffsetDateTime?, Timestamp> =
    this.add(this.types.jOffsetDateTime, name, nullFlag)

@JvmName("jOffsetTime__notNull")
fun SqTableColumnHolder.jOffsetTime(name: String, nullFlag: Any): SqTableColumn<OffsetTime, Time> =
    this.add(this.types.jOffsetTime, name, nullFlag)
@JvmName("jOffsetTime__nullable")
fun SqTableColumnHolder.jOffsetTime(name: String, nullFlag: Any?): SqTableColumn<OffsetTime?, Time> =
    this.add(this.types.jOffsetTime, name, nullFlag)

@JvmName("jTime__notNull")
fun SqTableColumnHolder.jTime(name: String, nullFlag: Any): SqTableColumn<Time, Time> =
    this.add(this.types.jTime, name, nullFlag)
@JvmName("jTime__nullable")
fun SqTableColumnHolder.jTime(name: String, nullFlag: Any?): SqTableColumn<Time?, Time> =
    this.add(this.types.jTime, name, nullFlag)

@JvmName("jTimestamp__notNull")
fun SqTableColumnHolder.jTimestamp(name: String, nullFlag: Any): SqTableColumn<Timestamp, Timestamp> =
    this.add(this.types.jTimestamp, name, nullFlag)
@JvmName("jTimestamp__nullable")
fun SqTableColumnHolder.jTimestamp(name: String, nullFlag: Any?): SqTableColumn<Timestamp?, Timestamp> =
    this.add(this.types.jTimestamp, name, nullFlag)
// endregion


// region Table columns / other
@JvmName("dataLink__notNull")
fun SqTableColumnHolder.dataLink(name: String, nullFlag: Any): SqTableColumn<URL, String> =
    this.add(this.types.dataLink, name, nullFlag)
@JvmName("dataLink__nullable")
fun SqTableColumnHolder.dataLink(name: String, nullFlag: Any?): SqTableColumn<URL?, String> =
    this.add(this.types.dataLink, name, nullFlag)

@JvmName("ref__notNull")
fun SqTableColumnHolder.ref(name: String, nullFlag: Any): SqTableColumn<Ref, Ref> =
    this.add(this.types.ref, name, nullFlag)
@JvmName("ref__nullable")
fun SqTableColumnHolder.ref(name: String, nullFlag: Any?): SqTableColumn<Ref?, Ref> =
    this.add(this.types.ref, name, nullFlag)

@JvmName("rowId__notNull")
fun SqTableColumnHolder.rowId(name: String, nullFlag: Any): SqTableColumn<RowId, RowId> =
    this.add(this.types.rowId, name, nullFlag)
@JvmName("rowId__nullable")
fun SqTableColumnHolder.rowId(name: String, nullFlag: Any?): SqTableColumn<RowId?, RowId> =
    this.add(this.types.rowId, name, nullFlag)

@JvmName("sqlXml__notNull")
fun SqTableColumnHolder.sqlXml(name: String, nullFlag: Any): SqTableColumn<SQLXML, String> =
    this.add(this.types.sqlXml, name, nullFlag)
@JvmName("sqlXml__nullable")
fun SqTableColumnHolder.sqlXml(name: String, nullFlag: Any?): SqTableColumn<SQLXML?, String> =
    this.add(this.types.sqlXml, name, nullFlag)


@JvmName("jRef__notNull")
fun SqTableColumnHolder.jRef(name: String, nullFlag: Any): SqTableColumn<Ref, Ref> =
    this.add(this.types.jRef, name, nullFlag)
@JvmName("jRef__nullable")
fun SqTableColumnHolder.jRef(name: String, nullFlag: Any?): SqTableColumn<Ref?, Ref> =
    this.add(this.types.jRef, name, nullFlag)

@JvmName("jRowId__notNull")
fun SqTableColumnHolder.jRowId(name: String, nullFlag: Any): SqTableColumn<RowId, RowId> =
    this.add(this.types.jRowId, name, nullFlag)
@JvmName("jRowId__nullable")
fun SqTableColumnHolder.jRowId(name: String, nullFlag: Any?): SqTableColumn<RowId?, RowId> =
    this.add(this.types.jRowId, name, nullFlag)

@JvmName("jSqlXml__notNull")
fun SqTableColumnHolder.jSqlXml(name: String, nullFlag: Any): SqTableColumn<SQLXML, String> =
    this.add(this.types.jSqlXml, name, nullFlag)
@JvmName("jSqlXml__nullable")
fun SqTableColumnHolder.jSqlXml(name: String, nullFlag: Any?): SqTableColumn<SQLXML?, String> =
    this.add(this.types.jSqlXml, name, nullFlag)

@JvmName("jUrl__notNull")
fun SqTableColumnHolder.jUrl(name: String, nullFlag: Any): SqTableColumn<URL, String> =
    this.add(this.types.jUrl, name, nullFlag)
@JvmName("jUrl__nullable")
fun SqTableColumnHolder.jUrl(name: String, nullFlag: Any?): SqTableColumn<URL?, String> =
    this.add(this.types.jUrl, name, nullFlag)
// endregion
