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

    val pgTimeWithTimeZone: SqType<OffsetTime, Time>
    override val timeWithTimeZone: SqType<OffsetTime, Time>
        get() = this.pgTimeWithTimeZone

    val pgTimestamp: SqType<LocalDateTime, Timestamp>
    override val timestamp: SqType<LocalDateTime, Timestamp>
        get() = this.pgTimestamp

    val pgTimestampAsTimestamp: SqType<Timestamp, Timestamp>
    override val timestampAsTimestamp: SqType<Timestamp, Timestamp>
        get() = this.pgTimestampAsTimestamp

    val pgTimestampWithTimeZone: SqType<OffsetDateTime, Timestamp>
    override val timestampWithTimeZone: SqType<OffsetDateTime, Timestamp>
        get() = this.pgTimestampWithTimeZone
    // endregion


    // region Number types
    val pgBitInt: SqType<Long, Number>
    override val bigInt: SqType<Long, Number>
        get() = this.pgBitInt

    val pgBitIntAsBigInteger: SqType<BigInteger, Number>
    override val bigIntAsBigInteger: SqType<BigInteger, Number>
        get() = this.pgBitIntAsBigInteger

    val pgDoublePrecision: SqType<Double, Number>
    override val double: SqType<Double, Number>
        get() = this.pgDoublePrecision
    override val float: SqType<Double, Number>
        get() = this.pgDoublePrecision

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

    val pgCharacterVarying: SqType<String, String>
    override val varChar: SqType<String, String>
        get() = this.pgCharacterVarying

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
