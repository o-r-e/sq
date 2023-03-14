package me.ore.sq.generic

import me.ore.sq.*
import java.math.BigDecimal
import java.net.URL
import java.sql.*


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
    override val jBigDecimalType: SqType<BigDecimal>
        get() = SqGenericTypes.NUMERIC
    override val jBlobType: SqType<Blob>
        get() = SqGenericTypes.BLOB
    override val jBooleanType: SqType<Boolean>
        get() = SqGenericTypes.BOOLEAN
    override val jByteType: SqType<Byte>
        get() = SqGenericTypes.TINY_INT
    override val jByteArrayType: SqType<SqByteArray>
        get() = SqGenericTypes.VAR_BINARY
    override val jClobType: SqType<Clob>
        get() = SqGenericTypes.CLOB
    override val jDateType: SqType<Date>
        get() = SqGenericTypes.DATE
    override val jDoubleType: SqType<Double>
        get() = SqGenericTypes.DOUBLE
    override val jFloatType: SqType<Float>
        get() = SqGenericTypes.REAL
    override val jIntType: SqType<Int>
        get() = SqGenericTypes.INTEGER
    override val jLongType: SqType<Long>
        get() = SqGenericTypes.BIG_INT
    override val jNClobType: SqType<NClob>
        get() = SqGenericTypes.N_CLOB
    override val jNumberType: SqType<Number>
        get() = SqGenericTypes.J_NUMBER
    override val jRefType: SqType<Ref>
        get() = SqGenericTypes.REF
    override val jRowIdType: SqType<RowId>
        get() = SqGenericTypes.ROW_ID
    override val jSqlXmlType: SqType<SQLXML>
        get() = SqGenericTypes.SQL_XML
    override val jShortType: SqType<Short>
        get() = SqGenericTypes.SMALL_INT
    override val jStringType: SqType<String>
        get() = SqGenericTypes.VAR_CHAR
    override val jTimeType: SqType<Time>
        get() = SqGenericTypes.TIME
    override val jTimestampType: SqType<Timestamp>
        get() = SqGenericTypes.TIMESTAMP
    override val jUrlType: SqType<URL>
        get() = SqGenericTypes.DATA_LINK

    override val dbBigIntType: SqType<Long>
        get() = SqGenericTypes.BIG_INT
    override val dbBinaryType: SqType<SqByteArray>
        get() = SqGenericTypes.BINARY
    override val dbBitType: SqType<Boolean>
        get() = SqGenericTypes.BIT
    override val dbBlobType: SqType<Blob>
        get() = SqGenericTypes.BLOB
    override val dbBooleanType: SqType<Boolean>
        get() = SqGenericTypes.BOOLEAN
    override val dbCharType: SqType<String>
        get() = SqGenericTypes.CHAR
    override val dbClobType: SqType<Clob>
        get() = SqGenericTypes.CLOB
    override val dbDataLinkType: SqType<URL>
        get() = SqGenericTypes.DATA_LINK
    override val dbDateType: SqType<Date>
        get() = SqGenericTypes.DATE
    override val dbDecimalType: SqType<BigDecimal>
        get() = SqGenericTypes.DECIMAL
    override val dbDoubleType: SqType<Double>
        get() = SqGenericTypes.DOUBLE
    override val dbFloatType: SqType<Double>
        get() = SqGenericTypes.FLOAT
    override val dbIntegerType: SqType<Int>
        get() = SqGenericTypes.INTEGER
    override val dbLongNVarCharType: SqType<String>
        get() = SqGenericTypes.LONG_N_VAR_CHAR
    override val dbLongVarBinaryType: SqType<SqByteArray>
        get() = SqGenericTypes.LONG_VAR_BINARY
    override val dbLongVarCharType: SqType<String>
        get() = SqGenericTypes.LONG_VAR_CHAR
    override val dbNCharType: SqType<String>
        get() = SqGenericTypes.N_CHAR
    override val dbNClobType: SqType<NClob>
        get() = SqGenericTypes.N_CLOB
    override val dbNVarCharType: SqType<String>
        get() = SqGenericTypes.N_VAR_CHAR
    override val dbNumericType: SqType<BigDecimal>
        get() = SqGenericTypes.NUMERIC
    override val dbRealType: SqType<Float>
        get() = SqGenericTypes.REAL
    override val dbRefType: SqType<Ref>
        get() = SqGenericTypes.REF
    override val dbRowIdType: SqType<RowId>
        get() = SqGenericTypes.ROW_ID
    override val dbSmallIntType: SqType<Short>
        get() = SqGenericTypes.SMALL_INT
    override val dbSqlXmlType: SqType<SQLXML>
        get() = SqGenericTypes.SQL_XML
    override val dbTimeType: SqType<Time>
        get() = SqGenericTypes.TIME
    override val dbTimestampType: SqType<Timestamp>
        get() = SqGenericTypes.TIMESTAMP
    override val dbTinyIntType: SqType<Byte>
        get() = SqGenericTypes.TINY_INT
    override val dbVarBinaryType: SqType<SqByteArray>
        get() = SqGenericTypes.VAR_BINARY
    override val dbVarCharType: SqType<String>
        get() = SqGenericTypes.VAR_CHAR
    // endregion
}


interface SqGenericConnectedContext: SqConnectedContext, SqGenericContext
