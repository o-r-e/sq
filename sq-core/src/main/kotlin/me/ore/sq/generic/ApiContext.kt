package me.ore.sq.generic

import me.ore.sq.*
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import java.sql.*
import java.sql.Date
import java.time.*
import java.util.*


interface SqGenericContext: SqContext {
    companion object: SqContextHolder<SqGenericContext>() {
        override fun createDefaultContext(): SqGenericContext = SqGenericContextImpl()
    }


    // region Utils
    override fun createConnectedContext(connection: Connection): SqGenericConnectedContext

    override fun start() { SqGenericContext.start(this) }
    override fun finish() { SqGenericContext.finish(this) }
    // endregion


    // region Types
    override val charType: SqType<String>
        get() = SqGenericTypes.CHAR
    override val varCharType: SqType<String>
        get() = SqGenericTypes.VAR_CHAR
    override val longVarCharType: SqType<String>
        get() = SqGenericTypes.LONG_VAR_CHAR
    override val nCharType: SqType<String>
        get() = SqGenericTypes.N_CHAR
    override val nVarCharType: SqType<String>
        get() = SqGenericTypes.N_VAR_CHAR
    override val longNVarCharType: SqType<String>
        get() = SqGenericTypes.LONG_N_VAR_CHAR

    override val numericType: SqType<BigDecimal>
        get() = SqGenericTypes.NUMERIC
    override val decimalType: SqType<BigDecimal>
        get() = SqGenericTypes.DECIMAL
    override val tinyIntType: SqType<Byte>
        get() = SqGenericTypes.TINY_INT
    override val smallIntType: SqType<Short>
        get() = SqGenericTypes.SMALL_INT
    override val integerType: SqType<Int>
        get() = SqGenericTypes.INTEGER
    override val bigIntType: SqType<Long>
        get() = SqGenericTypes.BIG_INT
    override val bigIntAsBigIntegerType: SqType<BigInteger>
        get() = SqGenericTypes.BIG_INT__AS__BIG_INTEGER
    override val realType: SqType<Float>
        get() = SqGenericTypes.REAL
    override val floatType: SqType<Double>
        get() = SqGenericTypes.FLOAT
    override val doubleType: SqType<Double>
        get() = SqGenericTypes.DOUBLE
    override val jInexactNumberType: SqType<Number>
        get() = SqGenericTypes.J_INEXACT_NUMBER_TYPE

    override val bitType: SqType<Boolean>
        get() = SqGenericTypes.BIT
    override val booleanType: SqType<Boolean>
        get() = SqGenericTypes.BOOLEAN

    override val binaryType: SqType<SqByteArray>
        get() = SqGenericTypes.BINARY
    override val varBinaryType: SqType<SqByteArray>
        get() = SqGenericTypes.VAR_BINARY
    override val longVarBinaryType: SqType<SqByteArray>
        get() = SqGenericTypes.LONG_VAR_BINARY

    override val clobType: SqType<Clob>
        get() = SqGenericTypes.CLOB
    override val blobType: SqType<Blob>
        get() = SqGenericTypes.BLOB
    override val refType: SqType<Ref>
        get() = SqGenericTypes.REF
    override val dataLinkType: SqType<URL>
        get() = SqGenericTypes.DATA_LINK
    override val rowIdType: SqType<RowId>
        get() = SqGenericTypes.ROW_ID
    override val nClobType: SqType<NClob>
        get() = SqGenericTypes.N_CLOB
    override val sqlXmlType: SqType<SQLXML>
        get() = SqGenericTypes.SQL_XML

    override val jSqlDateType: SqType<Date>
        get() = SqGenericTypes.DATE__AS__SQL_DATE
    override val jLocalDateType: SqType<LocalDate>
        get() = SqGenericTypes.DATE
    override val jSqlTimeType: SqType<Time>
        get() = SqGenericTypes.TIME__AS__SQL_TIME
    override val jLocalTimeType: SqType<LocalTime>
        get() = SqGenericTypes.TIME
    override val jSqlTimestampType: SqType<Timestamp>
        get() = SqGenericTypes.TIMESTAMP__AS__SQL_TIMESTAMP
    override val jCalendarType: SqType<Calendar>
        get() = SqGenericTypes.TIMESTAMP__AS__CALENDAR
    override val jDateType: SqType<java.util.Date>
        get() = SqGenericTypes.TIMESTAMP__AS__UTIL_DATE
    override val jLocalDateTimeType: SqType<LocalDateTime>
        get() = SqGenericTypes.TIMESTAMP
    override val jOffsetTimeType: SqType<OffsetTime>
        get() = SqGenericTypes.TIME_WITH_TIME_ZONE
    override val jOffsetDateTimeType: SqType<OffsetDateTime>
        get() = SqGenericTypes.TIMESTAMP_WITH_TIME_ZONE
    // endregion
}


interface SqGenericConnectedContext: SqConnectedContext, SqGenericContext
