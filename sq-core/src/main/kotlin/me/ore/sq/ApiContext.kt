package me.ore.sq

import me.ore.sq.generic.SqGenericContextImpl
import java.lang.IllegalStateException
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import java.sql.Blob
import java.sql.Clob
import java.sql.Connection
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
import java.util.Calendar
import kotlin.concurrent.getOrSet


interface SqContext {
    companion object: SqContextHolder<SqContext>() {
        override fun createDefaultContext(): SqContext = SqGenericContextImpl()


        // region Column index cache
        private val colIndexCacheHolder = ThreadLocal<MutableMap<SqColSet, MutableMap<SqColumn<*, *>, Int?>>>()

        private fun colIndexCache(): MutableMap<SqColSet, MutableMap<SqColumn<*, *>, Int?>> {
            return this.colIndexCacheHolder.getOrSet { HashMap() }
        }

        private fun colIndexCacheFor(colSet: SqColSet): MutableMap<SqColumn<*, *>, Int?> {
            return this.colIndexCache().computeIfAbsent(colSet) { HashMap() }
        }

        fun getColumnIndex(colSet: SqColSet, column: SqColumn<*, *>): Int? {
            return this.colIndexCacheFor(colSet).computeIfAbsent(column) {
                colSet.columns.indexOf(column).takeIf { it >= 0 }
            }
        }

        private fun clearColIndexCache() {
            this.colIndexCacheHolder.get()?.let { cache ->
                this.colIndexCacheHolder.remove()

                cache.values.forEach { colSetCache ->
                    colSetCache.clear()
                }
                cache.clear()
            }
        }
        // endregion


        override fun onLastContextFinished() {
            super.onLastContextFinished()
            this.clearColIndexCache()
        }
    }


    // region Utils
    fun createConnectedContext(connection: Connection): SqConnectedContext

    fun createWriter(): SqWriter

    fun start() { SqContext.start(this) }
    fun finish() { SqContext.finish(this) }

    fun getColumnIndex(colSet: SqColSet, column: SqColumn<*, *>): Int? = SqContext.getColumnIndex(colSet, column)

    var printParameterValuesByDefault: Boolean
    var printParameterValuesByThread: Boolean?
    val printParameterValues: Boolean
        get() = this.printParameterValuesByThread ?: this.printParameterValuesByDefault
    // endregion


    // region Types
    val charType: SqType<String>
    val varCharType: SqType<String>
    val longVarCharType: SqType<String>
    val nCharType: SqType<String>
    val nVarCharType: SqType<String>
    val longNVarCharType: SqType<String>
    val jStringType: SqType<String>
        get() = this.varCharType

    val numericType: SqType<BigDecimal>
    val decimalType: SqType<BigDecimal>
    val tinyIntType: SqType<Byte>
    val smallIntType: SqType<Short>
    val integerType: SqType<Int>
    val bigIntType: SqType<Long>
    val bigIntAsBigIntegerType: SqType<BigInteger>
    val realType: SqType<Float>
    val floatType: SqType<Double>
    val doubleType: SqType<Double>
    val jBigDecimalType: SqType<BigDecimal>
        get() = this.numericType
    val jByteType: SqType<Byte>
        get() = this.tinyIntType
    val jShortType: SqType<Short>
        get() = this.smallIntType
    val jIntType: SqType<Int>
        get() = this.integerType
    val jLongType: SqType<Long>
        get() = this.bigIntType
    val jBigIntegerType: SqType<BigInteger>
        get() = this.bigIntAsBigIntegerType
    val jFloatType: SqType<Float>
        get() = this.realType
    val jDoubleType: SqType<Double>
        get() = this.doubleType
    val jInexactNumberType: SqType<Number>

    val bitType: SqType<Boolean>
    val booleanType: SqType<Boolean>
    val jBooleanType: SqType<Boolean>
        get() = this.booleanType

    val binaryType: SqType<SqByteArray>
    val varBinaryType: SqType<SqByteArray>
    val longVarBinaryType: SqType<SqByteArray>
    val jByteArrayType: SqType<SqByteArray>
        get() = this.varBinaryType

    val clobType: SqType<Clob>
    val blobType: SqType<Blob>
    val refType: SqType<Ref>
    val dataLinkType: SqType<URL>
    val rowIdType: SqType<RowId>
    val nClobType: SqType<NClob>
    val sqlXmlType: SqType<SQLXML>
    val jClobType: SqType<Clob>
        get() = this.clobType
    val jBlobType: SqType<Blob>
        get() = this.blobType
    val jRefType: SqType<Ref>
        get() = this.refType
    val jUrlType: SqType<URL>
        get() = this.dataLinkType
    val jRowIdType: SqType<RowId>
        get() = this.rowIdType
    val jNClobType: SqType<NClob>
        get() = this.nClobType
    val jSqlXmlType: SqType<SQLXML>
        get() = this.sqlXmlType

    val jSqlDateType: SqType<Date>
    val jLocalDateType: SqType<LocalDate>
    val jSqlTimeType: SqType<Time>
    val jLocalTimeType: SqType<LocalTime>
    val jSqlTimestampType: SqType<Timestamp>
    val jCalendarType: SqType<Calendar>
    val jDateType: SqType<java.util.Date>
    val jLocalDateTimeType: SqType<LocalDateTime>
    val jOffsetTimeType: SqType<OffsetTime>
    val jOffsetDateTimeType: SqType<OffsetDateTime>
    val dateAsSqlDateType: SqType<Date>
        get() = this.jSqlDateType
    val dateType: SqType<LocalDate>
        get() = this.jLocalDateType
    val timeAsSqlTimeType: SqType<Time>
        get() = this.jSqlTimeType
    val timeType: SqType<LocalTime>
        get() = this.jLocalTimeType
    val timestampAsSqlTimestampType: SqType<Timestamp>
        get() = this.jSqlTimestampType
    val timestampAsCalendarType: SqType<Calendar>
        get() = this.jCalendarType
    val timestampAsDateType: SqType<java.util.Date>
        get() = this.jDateType
    val timestampType: SqType<LocalDateTime>
        get() = this.jLocalDateTimeType
    val timeWithTimeZoneType: SqType<OffsetTime>
        get() = this.jOffsetTimeType
    val timestampWithTimeZoneType: SqType<OffsetDateTime>
        get() = this.jOffsetDateTimeType

    val operationBooleanType: SqType<Boolean>
        get() = this.jBooleanType

    val mathOpNumberType: SqType<Number>
        get() = this.jInexactNumberType

    fun <T: Number?> getTypeForNumber(numberClass: Class<T>): SqType<T & Any>? {
        val result = when (numberClass) {
            Double::class.java -> this.jDoubleType
            java.lang.Double::class.java -> this.jDoubleType
            Long::class.java -> this.jLongType
            java.lang.Long::class.java -> this.jLongType
            Int::class.java -> this.jIntType
            java.lang.Integer::class.java -> this.jIntType

            BigDecimal::class.java -> this.jBigDecimalType
            BigInteger::class.java -> this.jBigIntegerType

            Float::class.java -> this.jFloatType
            java.lang.Float::class.java -> this.jFloatType
            Short::class.java -> this.jShortType
            java.lang.Short::class.java -> this.jShortType
            Byte::class.java -> this.jByteType
            java.lang.Byte::class.java -> this.jByteType

            Number::class.java -> this.jInexactNumberType
            java.lang.Number::class.java -> this.jInexactNumberType

            else -> null
        }
        return SqUtil.uncheckedCast(result)
    }

    fun <T: Number?> requireTypeForNumber(numberClass: Class<T>): SqType<T & Any> {
        return this.getTypeForNumber(numberClass)
            ?: throw IllegalStateException("Cannot define SQ type for number class ${numberClass.name}")
    }
    // endregion


    // region Base items
    fun <JAVA: Any?, DB: Any> param(type: SqType<JAVA & Any>, nullable: Boolean, value: JAVA): SqParameter<JAVA, DB>

    fun <JAVA: String?> charParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, String> = this.param(this.charType.sqCast(), nullable, value)
    fun <JAVA: String?> varCharParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, String> = this.param(this.varCharType.sqCast(), nullable, value)
    fun <JAVA: String?> longVarCharParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, String> = this.param(this.longVarCharType.sqCast(), nullable, value)
    fun <JAVA: String?> nCharParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, String> = this.param(this.nCharType.sqCast(), nullable, value)
    fun <JAVA: String?> nVarCharParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, String> = this.param(this.nVarCharType.sqCast(), nullable, value)
    fun <JAVA: String?> longNVarCharParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, String> = this.param(this.longNVarCharType.sqCast(), nullable, value)
    fun <JAVA: String?> jStringParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, String> = this.param(this.jStringType.sqCast(), nullable, value)

    fun <JAVA: BigDecimal?> numericParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.numericType.sqCast(), nullable, value)
    fun <JAVA: BigDecimal?> decimalParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.decimalType.sqCast(), nullable, value)
    fun <JAVA: Byte?> tinyIntParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.tinyIntType.sqCast(), nullable, value)
    fun <JAVA: Short?> smallIntParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.smallIntType.sqCast(), nullable, value)
    fun <JAVA: Int?> integerParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.integerType.sqCast(), nullable, value)
    fun <JAVA: Long?> bigIntParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.bigIntType.sqCast(), nullable, value)
    fun <JAVA: BigInteger?> bigIntAsBigIntegerParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.bigIntAsBigIntegerType.sqCast(), nullable, value)
    fun <JAVA: Float?> realParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.realType.sqCast(), nullable, value)
    fun <JAVA: Double?> floatParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.floatType.sqCast(), nullable, value)
    fun <JAVA: Double?> doubleParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.doubleType.sqCast(), nullable, value)
    fun <JAVA: BigDecimal?> jBigDecimalParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.jBigDecimalType.sqCast(), nullable, value)
    fun <JAVA: Byte?> jByteParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.jByteType.sqCast(), nullable, value)
    fun <JAVA: Short?> jShortParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.jShortType.sqCast(), nullable, value)
    fun <JAVA: Int?> jIntParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.jIntType.sqCast(), nullable, value)
    fun <JAVA: Long?> jLongParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.jLongType.sqCast(), nullable, value)
    fun <JAVA: BigInteger?> jBigIntegerParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.jBigIntegerType.sqCast(), nullable, value)
    fun <JAVA: Float?> jFloatParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.jFloatType.sqCast(), nullable, value)
    fun <JAVA: Double?> jDoubleParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.jDoubleType.sqCast(), nullable, value)

    fun <JAVA: Boolean?> bitParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Boolean> = this.param(this.bitType.sqCast(), nullable, value)
    fun <JAVA: Boolean?> booleanParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Boolean> = this.param(this.booleanType.sqCast(), nullable, value)
    fun <JAVA: Boolean?> jBooleanParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Boolean> = this.param(this.jBooleanType.sqCast(), nullable, value)

    fun <JAVA: SqByteArray?> binaryParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, ByteArray> = this.param(this.binaryType.sqCast(), nullable, value)
    fun <JAVA: SqByteArray?> varBinaryParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, ByteArray> = this.param(this.varBinaryType.sqCast(), nullable, value)
    fun <JAVA: SqByteArray?> longVarBinaryParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, ByteArray> = this.param(this.longVarBinaryType.sqCast(), nullable, value)
    fun <JAVA: SqByteArray?> jByteArrayParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, ByteArray> = this.param(this.jByteArrayType.sqCast(), nullable, value)

    fun <JAVA: Clob?> clobParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Clob> = this.param(this.clobType.sqCast(), nullable, value)
    fun <JAVA: Blob?> blobParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Blob> = this.param(this.blobType.sqCast(), nullable, value)
    fun <JAVA: Ref?> refParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Ref> = this.param(this.refType.sqCast(), nullable, value)
    fun <JAVA: URL?> dataLinkParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, String> = this.param(this.dataLinkType.sqCast(), nullable, value)
    fun <JAVA: RowId?> rowIdParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, RowId> = this.param(this.rowIdType.sqCast(), nullable, value)
    fun <JAVA: NClob?> nClobParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Clob> = this.param(this.nClobType.sqCast(), nullable, value)
    fun <JAVA: SQLXML?> sqlXmlParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, String> = this.param(this.sqlXmlType.sqCast(), nullable, value)
    fun <JAVA: Clob?> jClobParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Clob> = this.param(this.jClobType.sqCast(), nullable, value)
    fun <JAVA: Blob?> jBlobParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Blob> = this.param(this.jBlobType.sqCast(), nullable, value)
    fun <JAVA: Ref?> jRefParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Ref> = this.param(this.jRefType.sqCast(), nullable, value)
    fun <JAVA: URL?> jUrlParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, String> = this.param(this.jUrlType.sqCast(), nullable, value)
    fun <JAVA: RowId?> jRowIdParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, RowId> = this.param(this.jRowIdType.sqCast(), nullable, value)
    fun <JAVA: NClob?> jNClobParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Clob> = this.param(this.jNClobType.sqCast(), nullable, value)
    fun <JAVA: SQLXML?> jSqlXmlParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, String> = this.param(this.jSqlXmlType.sqCast(), nullable, value)

    fun <JAVA: Date?> jSqlDateParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Timestamp> = this.param(this.jSqlDateType.sqCast(), nullable, value)
    fun <JAVA: LocalDate?> jLocalDateParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Timestamp> = this.param(this.jLocalDateType.sqCast(), nullable, value)
    fun <JAVA: Time?> jSqlTimeParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Time> = this.param(this.jSqlTimeType.sqCast(), nullable, value)
    fun <JAVA: LocalTime?> jLocalTimeParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Time> = this.param(this.jLocalTimeType.sqCast(), nullable, value)
    fun <JAVA: Timestamp?> jSqlTimestampParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Timestamp> = this.param(this.jSqlTimestampType.sqCast(), nullable, value)
    fun <JAVA: Calendar?> jCalendarParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Timestamp> = this.param(this.jCalendarType.sqCast(), nullable, value)
    fun <JAVA: java.util.Date?> jDateParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Timestamp> = this.param(this.jDateType.sqCast(), nullable, value)
    fun <JAVA: LocalDateTime?> jLocalDateTimeParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Timestamp> = this.param(this.jLocalDateTimeType.sqCast(), nullable, value)
    fun <JAVA: OffsetTime?> jOffsetTimeParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Time> = this.param(this.jOffsetTimeType.sqCast(), nullable, value)
    fun <JAVA: OffsetDateTime?> jOffsetDateTimeParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Timestamp> = this.param(this.jOffsetDateTimeType.sqCast(), nullable, value)
    fun <JAVA: Date?> dateAsSqlDateParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Timestamp> = this.param(this.dateAsSqlDateType.sqCast(), nullable, value)
    fun <JAVA: LocalDate?> dateParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Timestamp> = this.param(this.dateType.sqCast(), nullable, value)
    fun <JAVA: Time?> timeAsSqlTimeParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Time> = this.param(this.timeAsSqlTimeType.sqCast(), nullable, value)
    fun <JAVA: LocalTime?> timeParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Time> = this.param(this.timeType.sqCast(), nullable, value)
    fun <JAVA: Timestamp?> timestampAsSqlTimestampParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Timestamp> = this.param(this.timestampAsSqlTimestampType.sqCast(), nullable, value)
    fun <JAVA: Calendar?> timestampAsCalendarParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Timestamp> = this.param(this.timestampAsCalendarType.sqCast(), nullable, value)
    fun <JAVA: java.util.Date?> timestampAsDateParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Timestamp> = this.param(this.timestampAsDateType.sqCast(), nullable, value)
    fun <JAVA: LocalDateTime?> timestampParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Timestamp> = this.param(this.timestampType.sqCast(), nullable, value)
    fun <JAVA: OffsetTime?> timeWithTimeZoneParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Time> = this.param(this.timeWithTimeZoneType.sqCast(), nullable, value)
    fun <JAVA: OffsetDateTime?> timestampWithTimeZoneParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Timestamp> = this.param(this.timestampWithTimeZoneType.sqCast(), nullable, value)


    fun <JAVA: Any, DB: Any> nullItem(type: SqType<JAVA>): SqNull<JAVA, DB>

    fun charNull(): SqNull<String, String> = this.nullItem(this.charType)
    fun varCharNull(): SqNull<String, String> = this.nullItem(this.varCharType)
    fun longVarCharNull(): SqNull<String, String> = this.nullItem(this.longVarCharType)
    fun nCharNull(): SqNull<String, String> = this.nullItem(this.nCharType)
    fun nVarCharNull(): SqNull<String, String> = this.nullItem(this.nVarCharType)
    fun longNVarCharNull(): SqNull<String, String> = this.nullItem(this.longNVarCharType)
    fun jStringNull(): SqNull<String, String> = this.nullItem(this.jStringType)

    fun numericNull(): SqNull<BigDecimal, Number> = this.nullItem(this.numericType)
    fun decimalNull(): SqNull<BigDecimal, Number> = this.nullItem(this.decimalType)
    fun tinyIntNull(): SqNull<Byte, Number> = this.nullItem(this.tinyIntType)
    fun smallIntNull(): SqNull<Short, Number> = this.nullItem(this.smallIntType)
    fun integerNull(): SqNull<Int, Number> = this.nullItem(this.integerType)
    fun bigIntNull(): SqNull<Long, Number> = this.nullItem(this.bigIntType)
    fun bigIntAsBigIntegerNull(): SqNull<BigInteger, Number> = this.nullItem(this.bigIntAsBigIntegerType)
    fun realNull(): SqNull<Float, Number> = this.nullItem(this.realType)
    fun floatNull(): SqNull<Double, Number> = this.nullItem(this.floatType)
    fun doubleNull(): SqNull<Double, Number> = this.nullItem(this.doubleType)
    fun jBigDecimalNull(): SqNull<BigDecimal, Number> = this.nullItem(this.jBigDecimalType)
    fun jByteNull(): SqNull<Byte, Number> = this.nullItem(this.jByteType)
    fun jShortNull(): SqNull<Short, Number> = this.nullItem(this.jShortType)
    fun jIntNull(): SqNull<Int, Number> = this.nullItem(this.jIntType)
    fun jLongNull(): SqNull<Long, Number> = this.nullItem(this.jLongType)
    fun jBigIntegerNull(): SqNull<BigInteger, Number> = this.nullItem(this.jBigIntegerType)
    fun jFloatNull(): SqNull<Float, Number> = this.nullItem(this.jFloatType)
    fun jDoubleNull(): SqNull<Double, Number> = this.nullItem(this.jDoubleType)

    fun bitNull(): SqNull<Boolean, Boolean> = this.nullItem(this.bitType)
    fun booleanNull(): SqNull<Boolean, Boolean> = this.nullItem(this.booleanType)
    fun jBooleanNull(): SqNull<Boolean, Boolean> = this.nullItem(this.jBooleanType)

    fun binaryNull(): SqNull<SqByteArray, ByteArray> = this.nullItem(this.binaryType)
    fun varBinaryNull(): SqNull<SqByteArray, ByteArray> = this.nullItem(this.varBinaryType)
    fun longVarBinaryNull(): SqNull<SqByteArray, ByteArray> = this.nullItem(this.longVarBinaryType)
    fun jByteArrayNull(): SqNull<SqByteArray, ByteArray> = this.nullItem(this.jByteArrayType)

    fun clobNull(): SqNull<Clob, Clob> = this.nullItem(this.clobType)
    fun blobNull(): SqNull<Blob, Blob> = this.nullItem(this.blobType)
    fun refNull(): SqNull<Ref, Ref> = this.nullItem(this.refType)
    fun dataLinkNull(): SqNull<URL, String> = this.nullItem(this.dataLinkType)
    fun rowIdNull(): SqNull<RowId, RowId> = this.nullItem(this.rowIdType)
    fun nClobNull(): SqNull<NClob, Clob> = this.nullItem(this.nClobType)
    fun sqlXmlNull(): SqNull<SQLXML, String> = this.nullItem(this.sqlXmlType)
    fun jClobNull(): SqNull<Clob, Clob> = this.nullItem(this.jClobType)
    fun jBlobNull(): SqNull<Blob, Blob> = this.nullItem(this.jBlobType)
    fun jRefNull(): SqNull<Ref, Ref> = this.nullItem(this.jRefType)
    fun jUrlNull(): SqNull<URL, String> = this.nullItem(this.jUrlType)
    fun jRowIdNull(): SqNull<RowId, RowId> = this.nullItem(this.jRowIdType)
    fun jNClobNull(): SqNull<NClob, Clob> = this.nullItem(this.jNClobType)
    fun jSqlXmlNull(): SqNull<SQLXML, String> = this.nullItem(this.jSqlXmlType)

    fun jSqlDateNull(): SqNull<Date, Timestamp> = this.nullItem(this.jSqlDateType)
    fun jLocalDateNull(): SqNull<LocalDate, Timestamp> = this.nullItem(this.jLocalDateType)
    fun jSqlTimeNull(): SqNull<Time, Time> = this.nullItem(this.jSqlTimeType)
    fun jLocalTimeNull(): SqNull<LocalTime, Time> = this.nullItem(this.jLocalTimeType)
    fun jSqlTimestampNull(): SqNull<Timestamp, Timestamp> = this.nullItem(this.jSqlTimestampType)
    fun jCalendarNull(): SqNull<Calendar, Timestamp> = this.nullItem(this.jCalendarType)
    fun jDateNull(): SqNull<java.util.Date, Timestamp> = this.nullItem(this.jDateType)
    fun jLocalDateTimeNull(): SqNull<LocalDateTime, Timestamp> = this.nullItem(this.jLocalDateTimeType)
    fun jOffsetTimeNull(): SqNull<OffsetTime, Time> = this.nullItem(this.jOffsetTimeType)
    fun jOffsetDateTimeNull(): SqNull<OffsetDateTime, Timestamp> = this.nullItem(this.jOffsetDateTimeType)
    fun dateAsSqlDateNull(): SqNull<Date, Timestamp> = this.nullItem(this.dateAsSqlDateType)
    fun dateNull(): SqNull<LocalDate, Timestamp> = this.nullItem(this.dateType)
    fun timeAsSqlTimeNull(): SqNull<Time, Time> = this.nullItem(this.timeAsSqlTimeType)
    fun timeNull(): SqNull<LocalTime, Time> = this.nullItem(this.timeType)
    fun timestampAsSqlTimestampNull(): SqNull<Timestamp, Timestamp> = this.nullItem(this.timestampAsSqlTimestampType)
    fun timestampAsCalendarNull(): SqNull<Calendar, Timestamp> = this.nullItem(this.timestampAsCalendarType)
    fun timestampAsDateNull(): SqNull<java.util.Date, Timestamp> = this.nullItem(this.timestampAsDateType)
    fun timestampNull(): SqNull<LocalDateTime, Timestamp> = this.nullItem(this.timestampType)
    fun timeWithTimeZoneNull(): SqNull<OffsetTime, Time> = this.nullItem(this.timeWithTimeZoneType)
    fun timestampWithTimeZoneNull(): SqNull<OffsetDateTime, Timestamp> = this.nullItem(this.timestampWithTimeZoneType)


    fun <JAVA: Any?, DB: Any, ORIG: SqExpression<JAVA, DB>> expressionAlias(original: ORIG, alias: String): SqExpressionAlias<JAVA, DB, ORIG>
    infix fun <JAVA: Any?, DB: Any, ORIG: SqExpression<JAVA, DB>> ORIG.alias(alias: String): SqExpressionAlias<JAVA, DB, ORIG> {
        return this@SqContext.expressionAlias(this, alias)
    }


    fun <ORIG: SqMultiColSet> multiColSetAlias(original: ORIG, alias: String): SqMultiColSetAlias<ORIG>
    infix fun <ORIG: SqMultiColSet> ORIG.alias(alias: String): SqMultiColSetAlias<ORIG> = this@SqContext.multiColSetAlias(this, alias)

    operator fun <JAVA: Any?, DB: Any> SqMultiColSetAlias<*>.get(originalColumn: SqColumn<JAVA, DB>): SqColSetAliasColumn<JAVA, DB> = this.getColumn(originalColumn)


    fun <JAVA: Any?, DB: Any, ORIG: SqSingleColSet<JAVA?, DB>> singleColSetAlias(original: ORIG, alias: String): SqSingleColSetAlias<JAVA, DB, ORIG>
    infix fun <JAVA: Any?, DB: Any, ORIG: SqSingleColSelect<JAVA, DB>> ORIG.alias(alias: String): SqSingleColSetAlias<JAVA, DB, SqSingleColSelect<JAVA?, DB>> {
        return this@SqContext.singleColSetAlias(SqUtil.uncheckedCast(this), alias)
    }


    fun <JAVA: Any?, DB: Any> colSetAliasColumn(alias: SqColSetAlias<*>, column: SqColumn<JAVA, DB>): SqColSetAliasColumn<JAVA, DB>
    // endregion


    // region Comparisons - groups and "single value" tests
    fun and(type: SqType<Boolean>, values: Iterable<SqExpression<*, Boolean>>): SqMultiValueTest<Boolean>
    fun and(values: Iterable<SqExpression<*, Boolean>>, type: SqType<Boolean> = this.operationBooleanType): SqMultiValueTest<Boolean> = this.and(type, values)
    fun and(first: SqExpression<*, Boolean>, vararg more: SqExpression<*, Boolean>): SqMultiValueTest<Boolean> = this.and(listOf(first, *more))

    fun or(type: SqType<Boolean>, values: Iterable<SqExpression<*, Boolean>>): SqMultiValueTest<Boolean>
    fun or(values: Iterable<SqExpression<*, Boolean>>, type: SqType<Boolean> = this.operationBooleanType): SqMultiValueTest<Boolean> = this.or(type, values)
    fun or(first: SqExpression<*, Boolean>, vararg more: SqExpression<*, Boolean>): SqMultiValueTest<Boolean> = this.or(listOf(first, *more))


    fun not(type: SqType<Boolean>, value: SqExpression<*, Boolean>): SqSingleValueTest<Boolean>
    fun not(value: SqExpression<*, Boolean>): SqSingleValueTest<Boolean> = this.not(this.operationBooleanType, value)


    fun isNull(type: SqType<Boolean>, value: SqExpression<*, *>): SqSingleValueTest<Boolean>
    fun isNull(value: SqExpression<*, *>, type: SqType<Boolean> = this.operationBooleanType): SqSingleValueTest<Boolean> = this.isNull(type, value)
    fun SqExpression<*, *>.isNull(): SqSingleValueTest<Boolean> = this@SqContext.isNull(this)

    fun isNotNull(type: SqType<Boolean>, value: SqExpression<*, *>): SqSingleValueTest<Boolean>
    fun isNotNull(value: SqExpression<*, *>, type: SqType<Boolean> = this.operationBooleanType): SqSingleValueTest<Boolean> = this.isNotNull(type, value)
    fun SqExpression<*, *>.isNotNull(): SqSingleValueTest<Boolean> = this@SqContext.isNotNull(this)
    // endregion


    // region Comparisons - "two value" tests
    fun <DB: Any> eq(type: SqType<Boolean>, firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>): SqTwoValueTest<Boolean>
    fun <DB: Any> eq(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> =
        this.eq(type, firstValue, secondValue)
    infix fun <DB: Any> SqExpression<*, DB>.eq(other: SqExpression<*, DB>): SqTwoValueTest<Boolean> =
        this@SqContext.eq(this, other)
    fun <JAVA: Any?, DB: Any> eq(expression: SqExpression<JAVA, DB>, value: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, DB>(expression.type.sqCast(), (value == null), value)
        return this.eq(expression, param, type)
    }
    infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.eq(value: JAVA?): SqTwoValueTest<Boolean> = this@SqContext.eq(this, value)

    fun <DB: Any> neq(type: SqType<Boolean>, firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>): SqTwoValueTest<Boolean>
    fun <DB: Any> neq(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> =
        this.neq(type, firstValue, secondValue)
    infix fun <DB: Any> SqExpression<*, DB>.neq(other: SqExpression<*, DB>): SqTwoValueTest<Boolean> =
        this@SqContext.neq(this, other)
    fun <JAVA: Any?, DB: Any> neq(expression: SqExpression<JAVA, DB>, value: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, DB>(expression.type.sqCast(), (value == null), value)
        return this.neq(expression, param)
    }
    infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.neq(value: JAVA?): SqTwoValueTest<Boolean> = this@SqContext.neq(this, value)

    fun <DB: Any> gt(type: SqType<Boolean>, firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>): SqTwoValueTest<Boolean>
    fun <DB: Any> gt(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> =
        this.gt(type, firstValue, secondValue)
    infix fun <DB: Any> SqExpression<*, DB>.gt(other: SqExpression<*, DB>): SqTwoValueTest<Boolean> =
        this@SqContext.gt(this, other)
    fun <JAVA: Any?, DB: Any> gt(expression: SqExpression<JAVA, DB>, value: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, DB>(expression.type.sqCast(), (value == null), value)
        return this.gt(expression, param)
    }
    infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.gt(value: JAVA?): SqTwoValueTest<Boolean> = this@SqContext.gt(this, value)

    fun <DB: Any> gte(type: SqType<Boolean>, firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>): SqTwoValueTest<Boolean>
    fun <DB: Any> gte(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> =
        this.gte(type, firstValue, secondValue)
    infix fun <DB: Any> SqExpression<*, DB>.gte(other: SqExpression<*, DB>): SqTwoValueTest<Boolean> =
        this@SqContext.gte(this, other)
    fun <JAVA: Any?, DB: Any> gte(expression: SqExpression<JAVA, DB>, value: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, DB>(expression.type.sqCast(), (value == null), value)
        return this.gte(expression, param)
    }
    infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.gte(value: JAVA?): SqTwoValueTest<Boolean> = this@SqContext.gte(this, value)

    fun <DB: Any> lt(type: SqType<Boolean>, firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>): SqTwoValueTest<Boolean>
    fun <DB: Any> lt(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> =
        this.lt(type, firstValue, secondValue)
    infix fun <DB: Any> SqExpression<*, DB>.lt(other: SqExpression<*, DB>): SqTwoValueTest<Boolean> =
        this@SqContext.lt(this, other)
    fun <JAVA: Any?, DB: Any> lt(expression: SqExpression<JAVA, DB>, value: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, DB>(expression.type.sqCast(), (value == null), value)
        return this.lt(expression, param)
    }
    infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.lt(value: JAVA?): SqTwoValueTest<Boolean> = this@SqContext.lt(this, value)

    fun <DB: Any> lte(type: SqType<Boolean>, firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>): SqTwoValueTest<Boolean>
    fun <DB: Any> lte(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> =
        this.lte(type, firstValue, secondValue)
    infix fun <DB: Any> SqExpression<*, DB>.lte(other: SqExpression<*, DB>): SqTwoValueTest<Boolean> =
        this@SqContext.lte(this, other)
    fun <JAVA: Any?, DB: Any> lte(expression: SqExpression<JAVA, DB>, value: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, DB>(expression.type.sqCast(), (value == null), value)
        return this.lte(expression, param)
    }
    infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.lte(value: JAVA?): SqTwoValueTest<Boolean> = this@SqContext.lte(this, value)

    fun like(type: SqType<Boolean>, firstValue: SqExpression<*, String>, secondValue: SqExpression<*, String>): SqTwoValueTest<Boolean>
    fun like(firstValue: SqExpression<*, String>, secondValue: SqExpression<*, String>, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> =
        this.like(type, firstValue, secondValue)
    infix fun SqExpression<*, String>.like(other: SqExpression<*, String>): SqTwoValueTest<Boolean> =
        this@SqContext.like(this, other)
    fun <JAVA: Any?> like(expression: SqExpression<JAVA, String>, value: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, String>(expression.type.sqCast(), (value == null), value)
        return this.like(expression, param)
    }
    infix fun <JAVA: Any?> SqExpression<JAVA, String>.like(value: JAVA?): SqTwoValueTest<Boolean> = this@SqContext.like(this, value)

    fun notLike(type: SqType<Boolean>, firstValue: SqExpression<*, String>, secondValue: SqExpression<*, String>): SqTwoValueTest<Boolean>
    fun notLike(firstValue: SqExpression<*, String>, secondValue: SqExpression<*, String>, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> =
        this.notLike(type, firstValue, secondValue)
    infix fun SqExpression<*, String>.notLike(other: SqExpression<*, String>): SqTwoValueTest<Boolean> =
        this@SqContext.notLike(this, other)
    fun <JAVA: Any?> notLike(expression: SqExpression<JAVA, String>, value: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, String>(expression.type.sqCast(), (value == null), value)
        return this.notLike(expression, param)
    }
    infix fun <JAVA: Any?> SqExpression<JAVA, String>.notLike(value: JAVA?): SqTwoValueTest<Boolean> = this@SqContext.notLike(this, value)
    // endregion


    // region Comparisons - between, not between, in [list], not in [list]
    fun <DB: Any> between(
        type: SqType<Boolean>,
        mainValue: SqExpression<*, DB>,
        firstBoundsValue: SqExpression<*, DB>,
        secondBoundsValue: SqExpression<*, DB>,
    ): SqBetweenTest<Boolean>
    fun <DB: Any> between(
        mainValue: SqExpression<*, DB>,
        firstBoundsValue: SqExpression<*, DB>,
        secondBoundsValue: SqExpression<*, DB>,
        type: SqType<Boolean> = this.operationBooleanType,
    ): SqBetweenTest<Boolean> =
        this.between(type, mainValue, firstBoundsValue, secondBoundsValue)
    fun <DB: Any> SqExpression<*, DB>.between(firstBoundsValue: SqExpression<*, DB>, secondBoundsValue: SqExpression<*, DB>): SqBetweenTest<Boolean> =
        this@SqContext.between(this, firstBoundsValue, secondBoundsValue)
    fun <JAVA: Any?, DB: Any> between(expression: SqExpression<JAVA, DB>, first: JAVA?, second: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqBetweenTest<Boolean> {
        val firstParam = this.param<JAVA?, DB>(expression.type.sqCast(), (first == null), first)
        val secondParam = this.param<JAVA?, DB>(expression.type.sqCast(), (second == null), second)
        return this.between(expression, firstParam, secondParam, type)
    }
    fun <JAVA: Any?> SqExpression<JAVA, *>.between(first: JAVA?, second: JAVA?): SqBetweenTest<Boolean> = this@SqContext.between(this, first, second)

    fun <DB: Any> notBetween(
        type: SqType<Boolean>,
        mainValue: SqExpression<*, DB>,
        firstBoundsValue: SqExpression<*, DB>,
        secondBoundsValue: SqExpression<*, DB>,
    ): SqBetweenTest<Boolean>
    fun <DB: Any> notBetween(
        mainValue: SqExpression<*, DB>,
        firstBoundsValue: SqExpression<*, DB>,
        secondBoundsValue: SqExpression<*, DB>,
        type: SqType<Boolean> = this.operationBooleanType,
    ): SqBetweenTest<Boolean> =
        this.notBetween(type, mainValue, firstBoundsValue, secondBoundsValue)
    fun <DB: Any> SqExpression<*, DB>.notBetween(firstBoundsValue: SqExpression<*, DB>, secondBoundsValue: SqExpression<*, DB>): SqBetweenTest<Boolean> =
        this@SqContext.notBetween(this, firstBoundsValue, secondBoundsValue)
    fun <JAVA: Any?, DB: Any> notBetween(expression: SqExpression<JAVA, DB>, first: JAVA?, second: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqBetweenTest<Boolean> {
        @Suppress("DuplicatedCode")
        val firstParam = this.param<JAVA?, DB>(expression.type.sqCast(), (first == null), first)
        val secondParam = this.param<JAVA?, DB>(expression.type.sqCast(), (second == null), second)
        return this.notBetween(expression, firstParam, secondParam, type)
    }
    fun <JAVA: Any?> SqExpression<JAVA, *>.notBetween(first: JAVA?, second: JAVA?): SqBetweenTest<Boolean> = this@SqContext.notBetween(this, first, second)


    fun <DB: Any> inList(
        type: SqType<Boolean>,
        mainValue: SqExpression<*, DB>,
        listValues: Array<out SqExpression<*, DB>>,
    ): SqInListTest<Boolean>
    fun <DB: Any> inList(mainValue: SqExpression<*, DB>, listValues: Array<out SqExpression<*, DB>>, type: SqType<Boolean> = this.operationBooleanType): SqInListTest<Boolean> =
        this.inList(type, mainValue, listValues)
    fun <DB: Any> inList(mainValue: SqExpression<*, DB>, firstValue: SqExpression<*, DB>, vararg moreValues: SqExpression<*, DB>, type: SqType<Boolean> = this.operationBooleanType): SqInListTest<Boolean> =
        this.inList(mainValue, arrayOf(firstValue, *moreValues), type)
    infix fun <DB: Any> SqExpression<*, DB>.inList(values: Array<out SqExpression<*, DB>>): SqInListTest<Boolean> =
        this@SqContext.inList(this, values)
    fun <DB: Any> SqExpression<*, DB>.inList(firstValue: SqExpression<*, DB>, vararg moreValues: SqExpression<*, DB>): SqInListTest<Boolean> =
        this@SqContext.inList(this, firstValue, *moreValues)
    fun <JAVA: Any?, DB: Any> inList(expression: SqExpression<JAVA, DB>, listValues: Array<out JAVA?>, type: SqType<Boolean> = this.operationBooleanType): SqInListTest<Boolean> {
        val expressionType = expression.type.sqCast<JAVA & Any>()
        val listParams = listValues.map { value ->
            this.param<JAVA?, DB>(expressionType, (value == null), value)
        }
        return this.inList(expression, listParams.toTypedArray(), type)
    }
    fun <JAVA: Any?, DB: Any> inList(expression: SqExpression<JAVA, DB>, first: JAVA?, vararg more: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqInListTest<Boolean> {
        @Suppress("DuplicatedCode")
        val expressionType = expression.type.sqCast<JAVA & Any>()
        val listParams = listOf(first, *more).map { value ->
            this.param<JAVA?, DB>(expressionType, (value == null), value)
        }
        return this.inList(expression, listParams.toTypedArray(), type)
    }
    infix fun <JAVA: Any?> SqExpression<JAVA, *>.inList(listValues: Array<out JAVA?>): SqInListTest<Boolean> = this@SqContext.inList(this, listValues)
    fun <JAVA: Any?> SqExpression<JAVA, *>.inList(first: JAVA?, vararg more: JAVA?): SqInListTest<Boolean> = this@SqContext.inList(this, first, *more)

    fun <DB: Any> notInList(
        type: SqType<Boolean>,
        mainValue: SqExpression<*, DB>,
        listValues: Array<out SqExpression<*, DB>>,
    ): SqInListTest<Boolean>
    fun <DB: Any> notInList(mainValue: SqExpression<*, DB>, listValues: Array<out SqExpression<*, DB>>, type: SqType<Boolean> = this.operationBooleanType): SqInListTest<Boolean> =
        this.notInList(type, mainValue, listValues)
    fun <DB: Any> notInList(mainValue: SqExpression<*, DB>, firstValue: SqExpression<*, DB>, vararg moreValues: SqExpression<*, DB>, type: SqType<Boolean> = this.operationBooleanType): SqInListTest<Boolean> =
        this.notInList(type, mainValue, arrayOf(firstValue, *moreValues))
    infix fun <DB: Any> SqExpression<*, DB>.notInList(values: Array<out SqExpression<*, DB>>): SqInListTest<Boolean> =
        this@SqContext.notInList(this, values)
    fun <DB: Any> SqExpression<*, DB>.notInList(firstValue: SqExpression<*, DB>, vararg moreValues: SqExpression<*, DB>): SqInListTest<Boolean> =
        this@SqContext.notInList(this, firstValue, *moreValues)
    fun <JAVA: Any?, DB: Any> notInList(expression: SqExpression<JAVA, DB>, listValues: Array<out JAVA?>, type: SqType<Boolean> = this.operationBooleanType): SqInListTest<Boolean> {
        @Suppress("DuplicatedCode")
        val expressionType = expression.type.sqCast<JAVA & Any>()
        val listParams = listValues.map { value ->
            this.param<JAVA?, DB>(expressionType, (value == null), value)
        }
        return this.notInList(expression, listParams.toTypedArray(), type)
    }
    fun <JAVA: Any?, DB: Any> notInList(expression: SqExpression<JAVA, DB>, first: JAVA?, vararg more: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqInListTest<Boolean> {
        @Suppress("DuplicatedCode")
        val expressionType = expression.type.sqCast<JAVA & Any>()
        val listParams = listOf(first, *more).map { value ->
            this.param<JAVA?, DB>(expressionType, (value == null), value)
        }
        return this.notInList(expression, listParams.toTypedArray(), type)
    }
    infix fun <JAVA: Any?> SqExpression<JAVA, *>.notInList(listValues: Array<out JAVA?>): SqInListTest<Boolean> = this@SqContext.notInList(this, listValues)
    fun <JAVA: Any?> SqExpression<JAVA, *>.notInList(first: JAVA?, vararg more: JAVA?): SqInListTest<Boolean> = this@SqContext.notInList(this, first, *more)
    // endregion


    // region Functions
    fun <JAVA: Any?, DB: Any> function(
        type: SqType<JAVA & Any>, nullable: Boolean,
        name: String, nameSeparated: Boolean,
        values: Iterable<SqItem>,
    ): SqNamedFunction<JAVA, DB>

    fun <JAVA: Any?, DB: Any> function(
        type: SqType<JAVA & Any>, nullable: Boolean,
        name: String, nameSeparated: Boolean,
        vararg values: SqItem,
    ): SqNamedFunction<JAVA, DB> = this.function(type, nullable, name, nameSeparated, values.toList())

    fun <JAVA: Any?, DB: Any> all(select: SqSingleColReadStatement<JAVA, DB>): SqNamedFunction<JAVA, DB> =
        this.function(select.type, select.nullable, SqUtil.FUNCTION_NAME__ALL, nameSeparated = true, listOf(select))
    fun <JAVA: Any?, DB: Any> any(select: SqSingleColReadStatement<JAVA, DB>): SqNamedFunction<JAVA, DB> =
        this.function(select.type, select.nullable, SqUtil.FUNCTION_NAME__ANY, nameSeparated = true, listOf(select))
    fun <JAVA: Number?> avg(expression: SqExpression<JAVA, Number>): SqNamedFunction<JAVA, Number> =
        this.function(expression.type, expression.nullable, SqUtil.FUNCTION_NAME__AVG, nameSeparated = false, expression)
    fun <JAVA: Any?, DB: Any> coalesce(values: Iterable<SqExpression<out JAVA?, DB>>, last: SqExpression<JAVA, DB>): SqNamedFunction<JAVA, DB> =
        this.function(last.type, last.nullable, SqUtil.FUNCTION_NAME__COALESCE, nameSeparated = false, values.plus(last))
    fun <JAVA: Any?, DB: Any> coalesce(vararg values: SqExpression<out JAVA?, DB>, last: SqExpression<JAVA, DB>): SqNamedFunction<JAVA, DB> =
        this.function(last.type, last.nullable, SqUtil.FUNCTION_NAME__COALESCE, nameSeparated = false, listOf(*values, last))
    fun <JAVA: Number> count(type: SqType<JAVA>, value: SqExpression<*, *>): SqNamedFunction<JAVA, Number> =
        this.function(type.sqCast(), nullable = false, SqUtil.FUNCTION_NAME__COUNT, nameSeparated = false, value)
    fun count(value: SqExpression<*, *>, type: SqType<Long> = this.jLongType): SqNamedFunction<Long, Number> = this.count(type, value)
    fun exists(type: SqType<Boolean>, select: SqSelect): SqNamedFunction<Boolean, Boolean> =
        this.function(type, false, SqUtil.FUNCTION_NAME__EXISTS, nameSeparated = true, listOf(select))
    fun exists(select: SqSelect, type: SqType<Boolean> = this.operationBooleanType): SqNamedFunction<Boolean, Boolean> = this.exists(type, select)
    fun <JAVA: Any?, DB: Any> min(value: SqExpression<JAVA, DB>): SqNamedFunction<JAVA, DB> =
        this.function(value.type, value.nullable, SqUtil.FUNCTION_NAME__MIN, nameSeparated = false, listOf(value))
    fun <JAVA: Any?, DB: Any> max(value: SqExpression<JAVA, DB>): SqNamedFunction<JAVA, DB> =
        this.function(value.type, value.nullable, SqUtil.FUNCTION_NAME__MAX, nameSeparated = false, listOf(value))
    fun <JAVA: Number?> sum(expression: SqExpression<JAVA, Number>): SqNamedFunction<JAVA, Number> =
        this.function(expression.type, expression.nullable, SqUtil.FUNCTION_NAME__SUM, nameSeparated = false, expression)
    // endregion


    // region Case
    fun caseWhen(whenItem: SqExpression<*, Boolean>): SqCaseItemStart
    fun <JAVA: Any?, DB: Any> caseItem(whenItem: SqExpression<*, Boolean>, thenItem: SqExpression<JAVA, DB>): SqCaseItem<JAVA, DB>

    fun <JAVA: Any?, DB: Any> case(forceNullable: Boolean, items: Iterable<SqCaseItem<JAVA, DB>>): SqCase<JAVA?, DB>
    fun <JAVA: Any?, DB: Any> case(forceNullable: Boolean, items: Iterable<SqCaseItem<out JAVA, DB>>, elseItem: SqExpression<out JAVA, DB>): SqCase<out JAVA, DB>
    fun <JAVA: Any?, DB: Any> case(items: Iterable<SqCaseItem<JAVA, DB>>): SqCase<JAVA?, DB> = this.case(forceNullable = true, items)
    fun <JAVA: Any?, DB: Any> case(vararg items: SqCaseItem<JAVA, DB>): SqCase<JAVA?, DB> = this.case(items.toList())
    fun <JAVA: Any?, DB: Any> case(items: Iterable<SqCaseItem<out JAVA, DB>>, elseItem: SqExpression<out JAVA, DB>): SqCase<out JAVA, DB> =
        this.case(forceNullable = false, items, elseItem)
    fun <JAVA: Any?, DB: Any> case(vararg items: SqCaseItem<out JAVA, DB>, elseItem: SqExpression<out JAVA, DB>): SqCase<out JAVA, DB> =
        this.case(items.toList(), elseItem)
    // endregion


    // region Mathematical operations
    fun <JAVA: Number?> twoOperandMathOperation(
        type: SqType<JAVA & Any>, nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        operation: String,
        secondOperand: SqExpression<*, Number>,
    ): SqTwoOperandMathOperation<JAVA>
    fun <JAVA: Number?, PARAM: Number?> twoOperandMathOperation(
        type: SqType<JAVA & Any>, nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        operation: String,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> {
        val secondOperand = this.param<PARAM, Number>(secondOperandType.sqCast(), (secondOperandValue == null), secondOperandValue)
        return this.twoOperandMathOperation(type, nullable, firstOperand, operation, secondOperand)
    }


    fun <JAVA: Number?> add(type: SqType<JAVA & Any>, nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__ADD, secondOperand)
    fun <JAVA: Number?> add(nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.add(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperand)
    fun add(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.add(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.add(type: SqType<JAVA & Any>, nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.add(type, nullable, this, other)
    fun <JAVA: Number?> SqExpression<*, Number>.add(nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.add(nullable, this, other)
    infix fun SqExpression<*, Number>.add(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.add(this, other)

    fun <JAVA: Number?, PARAM: Number?> add(
        type: SqType<JAVA & Any>, nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__ADD, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> add(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.add(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> add(firstOperand: SqExpression<*, Number>, secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.add(nullable = true, firstOperand, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.add(
        type: SqType<JAVA & Any>, nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.add(type, nullable, this, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.add(
        nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.add(nullable, this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.add(secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this@SqContext.add(this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.add(secondOperandValue: PARAM, secondOperandClass: Class<Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.add(this, this@SqContext.requireTypeForNumber(secondOperandClass), secondOperandValue)
    infix fun <PARAM: Number?> SqExpression<*, Number>.add(secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this.add(secondOperandValue, (secondOperandValue as? Number)?.javaClass ?: Number::class.java)


    fun <JAVA: Number?> sub(type: SqType<JAVA & Any>, nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__SUBTRACT, secondOperand)
    fun <JAVA: Number?> sub(nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.sub(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperand)
    fun sub(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.sub(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.sub(type: SqType<JAVA & Any>, nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.sub(type, nullable, this, other)
    fun <JAVA: Number?> SqExpression<*, Number>.sub(nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.sub(nullable, this, other)
    infix fun SqExpression<*, Number>.sub(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.sub(this, other)

    fun <JAVA: Number?, PARAM: Number?> sub(
        type: SqType<JAVA & Any>, nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__SUBTRACT, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> sub(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.sub(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> sub(firstOperand: SqExpression<*, Number>, secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.sub(nullable = true, firstOperand, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.sub(
        type: SqType<JAVA & Any>, nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.sub(type, nullable, this, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.sub(
        nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.sub(nullable, this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.sub(secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this@SqContext.sub(this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.sub(secondOperandValue: PARAM, secondOperandClass: Class<Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.sub(this, this@SqContext.requireTypeForNumber(secondOperandClass), secondOperandValue)
    infix fun <PARAM: Number?> SqExpression<*, Number>.sub(secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this.sub(secondOperandValue, (secondOperandValue as? Number)?.javaClass ?: Number::class.java)

    fun <JAVA: Number?> mult(type: SqType<JAVA & Any>, nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__MULTIPLY, secondOperand)
    fun <JAVA: Number?> mult(nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.mult(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperand)
    fun mult(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.mult(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.mult(type: SqType<JAVA & Any>, nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.mult(type, nullable, this, other)
    fun <JAVA: Number?> SqExpression<*, Number>.mult(nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.mult(nullable, this, other)
    infix fun SqExpression<*, Number>.mult(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.mult(this, other)

    fun <JAVA: Number?, PARAM: Number?> mult(
        type: SqType<JAVA & Any>, nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__MULTIPLY, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> mult(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.mult(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> mult(firstOperand: SqExpression<*, Number>, secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.mult(nullable = true, firstOperand, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.mult(
        type: SqType<JAVA & Any>, nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.mult(type, nullable, this, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.mult(
        nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.mult(nullable, this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.mult(secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this@SqContext.mult(this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.mult(secondOperandValue: PARAM, secondOperandClass: Class<Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.mult(this, this@SqContext.requireTypeForNumber(secondOperandClass), secondOperandValue)
    infix fun <PARAM: Number?> SqExpression<*, Number>.mult(secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this.mult(secondOperandValue, (secondOperandValue as? Number)?.javaClass ?: Number::class.java)

    fun <JAVA: Number?> div(type: SqType<JAVA & Any>, nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__DIVIDE, secondOperand)
    fun <JAVA: Number?> div(nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.div(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperand)
    fun div(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.div(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.div(type: SqType<JAVA & Any>, nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.div(type, nullable, this, other)
    fun <JAVA: Number?> SqExpression<*, Number>.div(nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.div(nullable, this, other)
    infix fun SqExpression<*, Number>.div(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.div(this, other)

    fun <JAVA: Number?, PARAM: Number?> div(
        type: SqType<JAVA & Any>, nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__DIVIDE, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> div(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.div(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> div(firstOperand: SqExpression<*, Number>, secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.div(nullable = true, firstOperand, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.div(
        type: SqType<JAVA & Any>, nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.div(type, nullable, this, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.div(
        nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.div(nullable, this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.div(secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this@SqContext.div(this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.div(secondOperandValue: PARAM, secondOperandClass: Class<Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.div(this, this@SqContext.requireTypeForNumber(secondOperandClass), secondOperandValue)
    infix fun <PARAM: Number?> SqExpression<*, Number>.div(secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this.div(secondOperandValue, (secondOperandValue as? Number)?.javaClass ?: Number::class.java)

    fun <JAVA: Number?> mod(type: SqType<JAVA & Any>, nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__MODULO, secondOperand)
    fun <JAVA: Number?> mod(nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.mod(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperand)
    fun mod(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.mod(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.mod(type: SqType<JAVA & Any>, nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.mod(type, nullable, this, other)
    fun <JAVA: Number?> SqExpression<*, Number>.mod(nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.mod(nullable, this, other)
    infix fun SqExpression<*, Number>.mod(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.mod(this, other)

    fun <JAVA: Number?, PARAM: Number?> mod(
        type: SqType<JAVA & Any>, nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__MODULO, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> mod(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.mod(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> mod(firstOperand: SqExpression<*, Number>, secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.mod(nullable = true, firstOperand, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.mod(
        type: SqType<JAVA & Any>, nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.mod(type, nullable, this, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.mod(
        nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.mod(nullable, this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.mod(secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this@SqContext.mod(this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.mod(secondOperandValue: PARAM, secondOperandClass: Class<Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.mod(this, this@SqContext.requireTypeForNumber(secondOperandClass), secondOperandValue)
    infix fun <PARAM: Number?> SqExpression<*, Number>.mod(secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this.mod(secondOperandValue, (secondOperandValue as? Number)?.javaClass ?: Number::class.java)

    fun <JAVA: Number?> bitwiseAnd(type: SqType<JAVA & Any>, nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__BITWISE_AND, secondOperand)
    fun <JAVA: Number?> bitwiseAnd(nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.bitwiseAnd(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperand)
    fun bitwiseAnd(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.bitwiseAnd(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.bitwiseAnd(type: SqType<JAVA & Any>, nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseAnd(type, nullable, this, other)
    fun <JAVA: Number?> SqExpression<*, Number>.bitwiseAnd(nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseAnd(nullable, this, other)
    infix fun SqExpression<*, Number>.bitwiseAnd(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.bitwiseAnd(this, other)

    fun <JAVA: Number?, PARAM: Number?> bitwiseAnd(
        type: SqType<JAVA & Any>, nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__BITWISE_AND, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> bitwiseAnd(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.bitwiseAnd(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> bitwiseAnd(firstOperand: SqExpression<*, Number>, secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.bitwiseAnd(nullable = true, firstOperand, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.bitwiseAnd(
        type: SqType<JAVA & Any>, nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseAnd(type, nullable, this, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.bitwiseAnd(
        nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseAnd(nullable, this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.bitwiseAnd(secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this@SqContext.bitwiseAnd(this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.bitwiseAnd(secondOperandValue: PARAM, secondOperandClass: Class<Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.bitwiseAnd(this, this@SqContext.requireTypeForNumber(secondOperandClass), secondOperandValue)
    infix fun <PARAM: Number?> SqExpression<*, Number>.bitwiseAnd(secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this.bitwiseAnd(secondOperandValue, (secondOperandValue as? Number)?.javaClass ?: Number::class.java)

    fun <JAVA: Number?> bitwiseOr(type: SqType<JAVA & Any>, nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__BITWISE_OR, secondOperand)
    fun <JAVA: Number?> bitwiseOr(nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.bitwiseOr(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperand)
    fun bitwiseOr(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.bitwiseOr(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.bitwiseOr(type: SqType<JAVA & Any>, nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseOr(type, nullable, this, other)
    fun <JAVA: Number?> SqExpression<*, Number>.bitwiseOr(nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseOr(nullable, this, other)
    infix fun SqExpression<*, Number>.bitwiseOr(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.bitwiseOr(this, other)

    fun <JAVA: Number?, PARAM: Number?> bitwiseOr(
        type: SqType<JAVA & Any>, nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__BITWISE_OR, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> bitwiseOr(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.bitwiseOr(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> bitwiseOr(firstOperand: SqExpression<*, Number>, secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.bitwiseOr(nullable = true, firstOperand, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.bitwiseOr(
        type: SqType<JAVA & Any>, nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseOr(type, nullable, this, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.bitwiseOr(
        nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseOr(nullable, this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.bitwiseOr(secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this@SqContext.bitwiseOr(this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.bitwiseOr(secondOperandValue: PARAM, secondOperandClass: Class<Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.bitwiseOr(this, this@SqContext.requireTypeForNumber(secondOperandClass), secondOperandValue)
    infix fun <PARAM: Number?> SqExpression<*, Number>.bitwiseOr(secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this.bitwiseOr(secondOperandValue, (secondOperandValue as? Number)?.javaClass ?: Number::class.java)

    fun <JAVA: Number?> bitwiseXor(type: SqType<JAVA & Any>, nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__BITWISE_XOR, secondOperand)
    fun <JAVA: Number?> bitwiseXor(nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.bitwiseXor(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperand)
    fun bitwiseXor(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.bitwiseXor(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.bitwiseXor(type: SqType<JAVA & Any>, nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseXor(type, nullable, this, other)
    fun <JAVA: Number?> SqExpression<*, Number>.bitwiseXor(nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseXor(nullable, this, other)
    infix fun SqExpression<*, Number>.bitwiseXor(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.bitwiseXor(this, other)

    fun <JAVA: Number?, PARAM: Number?> bitwiseXor(
        type: SqType<JAVA & Any>, nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__BITWISE_XOR, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> bitwiseXor(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.bitwiseXor(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> bitwiseXor(firstOperand: SqExpression<*, Number>, secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.bitwiseXor(nullable = true, firstOperand, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.bitwiseXor(
        type: SqType<JAVA & Any>, nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseXor(type, nullable, this, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.bitwiseXor(
        nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseXor(nullable, this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.bitwiseXor(secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this@SqContext.bitwiseXor(this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.bitwiseXor(secondOperandValue: PARAM, secondOperandClass: Class<Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.bitwiseXor(this, this@SqContext.requireTypeForNumber(secondOperandClass), secondOperandValue)
    infix fun <PARAM: Number?> SqExpression<*, Number>.bitwiseXor(secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this.bitwiseXor(secondOperandValue, (secondOperandValue as? Number)?.javaClass ?: Number::class.java)
    // endregion


    // region Statements - join, order by, select, union
    fun createJoin(type: SqJoinType, mainColSet: SqColSet, joinedColSet: SqColSet): SqJoin
    fun SqColSet.join(type: SqJoinType, joined: SqColSet): SqJoin = this@SqContext.createJoin(type, this, joined)
    fun createInnerJoin(mainColSet: SqColSet, joinedColSet: SqColSet): SqJoin = this.createJoin(SqJoinType.INNER, mainColSet, joinedColSet)
    infix fun SqColSet.innerJoin(joined: SqColSet): SqJoin = this@SqContext.createInnerJoin(this, joined)
    fun createLeftJoin(mainColSet: SqColSet, joinedColSet: SqColSet): SqJoin = this.createJoin(SqJoinType.LEFT, mainColSet, joinedColSet)
    infix fun SqColSet.leftJoin(joined: SqColSet): SqJoin = this@SqContext.createLeftJoin(this, joined)
    fun createRightJoin(mainColSet: SqColSet, joinedColSet: SqColSet): SqJoin = this.createJoin(SqJoinType.RIGHT, mainColSet, joinedColSet)
    infix fun SqColSet.rightJoin(joined: SqColSet): SqJoin = this@SqContext.createRightJoin(this, joined)
    fun createFullJoin(mainColSet: SqColSet, joinedColSet: SqColSet): SqJoin = this.createJoin(SqJoinType.FULL, mainColSet, joinedColSet)
    infix fun SqColSet.fullJoin(joined: SqColSet): SqJoin = this@SqContext.createFullJoin(this, joined)

    fun createOrderBy(column: SqColumn<*, *>, order: SqSortOrder): SqOrderBy
    infix fun SqColumn<*, *>.orderBy(order: SqSortOrder): SqOrderBy = this@SqContext.createOrderBy(this, order)
    fun SqColumn<*, *>.asc(): SqOrderBy = this.orderBy(SqSortOrder.ASC)
    fun SqColumn<*, *>.desc(): SqOrderBy = this.orderBy(SqSortOrder.DESC)


    fun select(distinct: Boolean, columns: Iterable<SqColumn<*, *>>): SqMultiColSelect
    fun select(distinct: Boolean, first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqMultiColSelect =
        this.select(distinct, listOf(first, second, *more))
    fun select(columns: Iterable<SqColumn<*, *>>): SqMultiColSelect = this.select(distinct = false, columns)
    fun select(first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqMultiColSelect =
        this.select(distinct = false, first, second, *more)
    fun selectDistinct(columns: Iterable<SqColumn<*, *>>): SqMultiColSelect = this.select(distinct = true, columns)
    fun selectDistinct(first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqMultiColSelect =
        this.select(distinct = true, first, second, *more)

    fun <JAVA: Any?, DB: Any> select(distinct: Boolean, column: SqColumn<JAVA, DB>): SqSingleColSelect<JAVA, DB>
    fun <JAVA: Any?, DB: Any> select(column: SqColumn<JAVA, DB>): SqSingleColSelect<JAVA, DB> = this.select(distinct = false, column)
    fun <JAVA: Any?, DB: Any> selectDistinct(column: SqColumn<JAVA, DB>): SqSingleColSelect<JAVA, DB> = this.select(distinct = true, column)


    fun union(unionAll: Boolean, selects: Iterable<SqSelect>): SqMultiColUnion
    fun union(unionAll: Boolean, first: SqSelect, second: SqSelect, vararg more: SqSelect): SqMultiColUnion = this.union(unionAll, listOf(first, second, *more))
    fun union(selects: Iterable<SqSelect>): SqMultiColUnion = this.union(unionAll = false, selects)
    fun union(first: SqSelect, second: SqSelect, vararg more: SqSelect): SqMultiColUnion = this.union(unionAll = false, first, second, *more)
    fun unionAll(selects: Iterable<SqSelect>): SqMultiColUnion = this.union(unionAll = true, selects)
    fun unionAll(first: SqSelect, second: SqSelect, vararg more: SqSelect): SqMultiColUnion = this.union(unionAll = true, first, second, *more)

    fun <JAVA: Any?, DB: Any> union(unionAll: Boolean, selects: Iterable<SqSingleColSelect<JAVA, DB>>): SqSingleColUnion<JAVA, DB>
    fun <JAVA: Any?, DB: Any> union(
        unionAll: Boolean,
        first: SqSingleColSelect<JAVA, DB>,
        second: SqSingleColSelect<JAVA, DB>,
        vararg more: SqSingleColSelect<JAVA, DB>,
    ): SqSingleColUnion<JAVA, DB> = this.union(unionAll, listOf(first, second, *more))
    fun <JAVA: Any?, DB: Any> union(selects: Iterable<SqSingleColSelect<JAVA, DB>>): SqSingleColUnion<JAVA, DB> =
        this.union(unionAll = false, selects)
    fun <JAVA: Any?, DB: Any> union(
        first: SqSingleColSelect<JAVA, DB>,
        second: SqSingleColSelect<JAVA, DB>,
        vararg more: SqSingleColSelect<JAVA, DB>,
    ): SqSingleColUnion<JAVA, DB> = this.union(unionAll = false, first, second, *more)
    fun <JAVA: Any?, DB: Any> unionAll(selects: Iterable<SqSingleColSelect<JAVA, DB>>): SqSingleColUnion<JAVA, DB> =
        this.union(unionAll = true, selects)
    fun <JAVA: Any?, DB: Any> unionAll(
        first: SqSingleColSelect<JAVA, DB>,
        second: SqSingleColSelect<JAVA, DB>,
        vararg more: SqSingleColSelect<JAVA, DB>,
    ): SqSingleColUnion<JAVA, DB> = this.union(unionAll = true, first, second, *more)
    // endregion


    // region Statements - other
    fun <T: SqTable> insertInto(table: T): SqInsert<T>
    fun <T: SqTable> update(table: T): SqUpdate<T>
    fun <T: SqTable> deleteFrom(table: T): SqDelete<T>
    // endregion
}


interface SqConnectedContext: SqContext {
    val connection: Connection


    // region Statements - select, union
    override fun select(distinct: Boolean, columns: Iterable<SqColumn<*, *>>): SqConnMultiColSelect
    override fun select(distinct: Boolean, first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqConnMultiColSelect =
        this.select(distinct, listOf(first, second, *more))
    override fun select(columns: Iterable<SqColumn<*, *>>): SqConnMultiColSelect = this.select(distinct = false, columns)
    override fun select(first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqConnMultiColSelect =
        this.select(distinct = false, first, second, *more)
    override fun selectDistinct(columns: Iterable<SqColumn<*, *>>): SqConnMultiColSelect = this.select(distinct = true, columns)
    override fun selectDistinct(first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqConnMultiColSelect =
        this.select(distinct = true, first, second, *more)

    override fun <JAVA: Any?, DB: Any> select(distinct: Boolean, column: SqColumn<JAVA, DB>): SqConnSingleColSelect<JAVA, DB>
    override fun <JAVA: Any?, DB: Any> select(column: SqColumn<JAVA, DB>): SqConnSingleColSelect<JAVA, DB> = this.select(distinct = false, column)
    override fun <JAVA: Any?, DB: Any> selectDistinct(column: SqColumn<JAVA, DB>): SqConnSingleColSelect<JAVA, DB> = this.select(distinct = true, column)


    override fun union(unionAll: Boolean, selects: Iterable<SqSelect>): SqConnMultiColUnion
    override fun union(unionAll: Boolean, first: SqSelect, second: SqSelect, vararg more: SqSelect): SqConnMultiColUnion = this.union(unionAll, listOf(first, second, *more))
    override fun union(selects: Iterable<SqSelect>): SqConnMultiColUnion = this.union(unionAll = false, selects)
    override fun union(first: SqSelect, second: SqSelect, vararg more: SqSelect): SqConnMultiColUnion = this.union(unionAll = false, first, second, *more)
    override fun unionAll(selects: Iterable<SqSelect>): SqConnMultiColUnion = this.union(unionAll = true, selects)
    override fun unionAll(first: SqSelect, second: SqSelect, vararg more: SqSelect): SqConnMultiColUnion = this.union(unionAll = true, first, second, *more)

    override fun <JAVA: Any?, DB: Any> union(unionAll: Boolean, selects: Iterable<SqSingleColSelect<JAVA, DB>>): SqConnSingleColUnion<JAVA, DB>
    override fun <JAVA: Any?, DB: Any> union(
        unionAll: Boolean,
        first: SqSingleColSelect<JAVA, DB>,
        second: SqSingleColSelect<JAVA, DB>,
        vararg more: SqSingleColSelect<JAVA, DB>,
    ): SqConnSingleColUnion<JAVA, DB> = this.union(unionAll, listOf(first, second, *more))
    override fun <JAVA: Any?, DB: Any> union(selects: Iterable<SqSingleColSelect<JAVA, DB>>): SqConnSingleColUnion<JAVA, DB> =
        this.union(unionAll = false, selects)
    override fun <JAVA: Any?, DB: Any> union(
        first: SqSingleColSelect<JAVA, DB>,
        second: SqSingleColSelect<JAVA, DB>,
        vararg more: SqSingleColSelect<JAVA, DB>,
    ): SqConnSingleColUnion<JAVA, DB> = this.union(unionAll = false, first, second, *more)
    override fun <JAVA: Any?, DB: Any> unionAll(selects: Iterable<SqSingleColSelect<JAVA, DB>>): SqConnSingleColUnion<JAVA, DB> =
        this.union(unionAll = true, selects)
    override fun <JAVA: Any?, DB: Any> unionAll(
        first: SqSingleColSelect<JAVA, DB>,
        second: SqSingleColSelect<JAVA, DB>,
        vararg more: SqSingleColSelect<JAVA, DB>,
    ): SqConnSingleColUnion<JAVA, DB> = this.union(unionAll = true, first, second, *more)
    // endregion


    // region Statements - other
    override fun <T : SqTable> insertInto(table: T): SqConnInsert<T>
    override fun <T : SqTable> update(table: T): SqConnUpdate<T>
    override fun <T : SqTable> deleteFrom(table: T): SqConnDelete<T>
    // endregion
}
