package me.ore.sq

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
import java.util.Calendar


private class SqTypeGetterTableColumn<JAVA: Any?, DB: Any>(
    table: SqTable,
    columnName: String,
    nullable: Boolean,
    protected val getType: (context: SqContext) -> SqType<JAVA & Any>,
): SqTableColumnBase<JAVA, DB>(table, columnName, nullable) {
    override val type: SqType<JAVA & Any>
        get() = this.getType(this.context)
}

private fun <JAVA: Any, DB: Any> SqTable.addColumn(columnName: String, getType: (context: SqContext) -> SqType<JAVA>): SqTableColumn<JAVA, DB> =
    this.addColumn(SqTypeGetterTableColumn(this, columnName, nullable = false, getType))


fun SqTable.char(columnName: String): SqTableColumn<String, String> = this.addColumn(columnName, SqContext::charType)
fun SqTable.varChar(columnName: String): SqTableColumn<String, String> = this.addColumn(columnName, SqContext::varCharType)
fun SqTable.longVarChar(columnName: String): SqTableColumn<String, String> = this.addColumn(columnName, SqContext::longVarCharType)
fun SqTable.nChar(columnName: String): SqTableColumn<String, String> = this.addColumn(columnName, SqContext::nCharType)
fun SqTable.nVarChar(columnName: String): SqTableColumn<String, String> = this.addColumn(columnName, SqContext::nVarCharType)
fun SqTable.longNVarChar(columnName: String): SqTableColumn<String, String> = this.addColumn(columnName, SqContext::longNVarCharType)
fun SqTable.jString(columnName: String): SqTableColumn<String, String> = this.addColumn(columnName, SqContext::jStringType)

fun SqTable.numeric(columnName: String): SqTableColumn<BigDecimal, Number> = this.addColumn(columnName, SqContext::numericType)
fun SqTable.decimal(columnName: String): SqTableColumn<BigDecimal, Number> = this.addColumn(columnName, SqContext::decimalType)
fun SqTable.tinyInt(columnName: String): SqTableColumn<Byte, Number> = this.addColumn(columnName, SqContext::tinyIntType)
fun SqTable.smallInt(columnName: String): SqTableColumn<Short, Number> = this.addColumn(columnName, SqContext::smallIntType)
fun SqTable.integer(columnName: String): SqTableColumn<Int, Number> = this.addColumn(columnName, SqContext::integerType)
fun SqTable.bigInt(columnName: String): SqTableColumn<Long, Number> = this.addColumn(columnName, SqContext::bigIntType)
fun SqTable.bigIntAsBigInteger(columnName: String): SqTableColumn<BigInteger, Number> = this.addColumn(columnName, SqContext::bigIntAsBigIntegerType)
fun SqTable.real(columnName: String): SqTableColumn<Float, Number> = this.addColumn(columnName, SqContext::realType)
fun SqTable.float(columnName: String): SqTableColumn<Double, Number> = this.addColumn(columnName, SqContext::floatType)
fun SqTable.double(columnName: String): SqTableColumn<Double, Number> = this.addColumn(columnName, SqContext::doubleType)
fun SqTable.jBigDecimal(columnName: String): SqTableColumn<BigDecimal, Number> = this.addColumn(columnName, SqContext::jBigDecimalType)
fun SqTable.jByte(columnName: String): SqTableColumn<Byte, Number> = this.addColumn(columnName, SqContext::jByteType)
fun SqTable.jShort(columnName: String): SqTableColumn<Short, Number> = this.addColumn(columnName, SqContext::jShortType)
fun SqTable.jInt(columnName: String): SqTableColumn<Int, Number> = this.addColumn(columnName, SqContext::jIntType)
fun SqTable.jLong(columnName: String): SqTableColumn<Long, Number> = this.addColumn(columnName, SqContext::jLongType)
fun SqTable.jBigInteger(columnName: String): SqTableColumn<BigInteger, Number> = this.addColumn(columnName, SqContext::jBigIntegerType)
fun SqTable.jFloat(columnName: String): SqTableColumn<Float, Number> = this.addColumn(columnName, SqContext::jFloatType)
fun SqTable.jDouble(columnName: String): SqTableColumn<Double, Number> = this.addColumn(columnName, SqContext::jDoubleType)

fun SqTable.bit(columnName: String): SqTableColumn<Boolean, Boolean> = this.addColumn(columnName, SqContext::bitType)
fun SqTable.boolean(columnName: String): SqTableColumn<Boolean, Boolean> = this.addColumn(columnName, SqContext::booleanType)
fun SqTable.jBoolean(columnName: String): SqTableColumn<Boolean, Boolean> = this.addColumn(columnName, SqContext::jBooleanType)

fun SqTable.binary(columnName: String): SqTableColumn<SqByteArray, ByteArray> = this.addColumn(columnName, SqContext::binaryType)
fun SqTable.varBinary(columnName: String): SqTableColumn<SqByteArray, ByteArray> = this.addColumn(columnName, SqContext::varBinaryType)
fun SqTable.longVarBinary(columnName: String): SqTableColumn<SqByteArray, ByteArray> = this.addColumn(columnName, SqContext::longVarBinaryType)
fun SqTable.jByteArray(columnName: String): SqTableColumn<SqByteArray, ByteArray> = this.addColumn(columnName, SqContext::jByteArrayType)

fun SqTable.clob(columnName: String): SqTableColumn<Clob, Clob> = this.addColumn(columnName, SqContext::clobType)
fun SqTable.blob(columnName: String): SqTableColumn<Blob, Blob> = this.addColumn(columnName, SqContext::blobType)
fun SqTable.ref(columnName: String): SqTableColumn<Ref, Ref> = this.addColumn(columnName, SqContext::refType)
fun SqTable.dataLink(columnName: String): SqTableColumn<URL, String> = this.addColumn(columnName, SqContext::dataLinkType)
fun SqTable.rowId(columnName: String): SqTableColumn<RowId, RowId> = this.addColumn(columnName, SqContext::rowIdType)
fun SqTable.nClob(columnName: String): SqTableColumn<NClob, Clob> = this.addColumn(columnName, SqContext::nClobType)
fun SqTable.sqlXml(columnName: String): SqTableColumn<SQLXML, String> = this.addColumn(columnName, SqContext::sqlXmlType)
fun SqTable.jClob(columnName: String): SqTableColumn<Clob, Clob> = this.addColumn(columnName, SqContext::jClobType)
fun SqTable.jBlob(columnName: String): SqTableColumn<Blob, Blob> = this.addColumn(columnName, SqContext::jBlobType)
fun SqTable.jRef(columnName: String): SqTableColumn<Ref, Ref> = this.addColumn(columnName, SqContext::jRefType)
fun SqTable.jUrl(columnName: String): SqTableColumn<URL, String> = this.addColumn(columnName, SqContext::jUrlType)
fun SqTable.jRowId(columnName: String): SqTableColumn<RowId, RowId> = this.addColumn(columnName, SqContext::jRowIdType)
fun SqTable.jNClob(columnName: String): SqTableColumn<NClob, Clob> = this.addColumn(columnName, SqContext::jNClobType)
fun SqTable.jSqlXml(columnName: String): SqTableColumn<SQLXML, String> = this.addColumn(columnName, SqContext::jSqlXmlType)

fun SqTable.jSqlDate(columnName: String): SqTableColumn<Date, Timestamp> = this.addColumn(columnName, SqContext::jSqlDateType)
fun SqTable.jLocalDate(columnName: String): SqTableColumn<LocalDate, Timestamp> = this.addColumn(columnName, SqContext::jLocalDateType)
fun SqTable.jSqlTime(columnName: String): SqTableColumn<Time, Time> = this.addColumn(columnName, SqContext::jSqlTimeType)
fun SqTable.jLocalTime(columnName: String): SqTableColumn<LocalTime, Time> = this.addColumn(columnName, SqContext::jLocalTimeType)
fun SqTable.jSqlTimestamp(columnName: String): SqTableColumn<Timestamp, Timestamp> = this.addColumn(columnName, SqContext::jSqlTimestampType)
fun SqTable.jCalendar(columnName: String): SqTableColumn<Calendar, Timestamp> = this.addColumn(columnName, SqContext::jCalendarType)
fun SqTable.jDate(columnName: String): SqTableColumn<java.util.Date, Timestamp> = this.addColumn(columnName, SqContext::jDateType)
fun SqTable.jLocalDateTime(columnName: String): SqTableColumn<LocalDateTime, Timestamp> = this.addColumn(columnName, SqContext::jLocalDateTimeType)
fun SqTable.jOffsetTime(columnName: String): SqTableColumn<OffsetTime, Time> = this.addColumn(columnName, SqContext::jOffsetTimeType)
fun SqTable.jOffsetDateTime(columnName: String): SqTableColumn<OffsetDateTime, Timestamp> = this.addColumn(columnName, SqContext::jOffsetDateTimeType)
fun SqTable.dateAsSqlDate(columnName: String): SqTableColumn<Date, Timestamp> = this.addColumn(columnName, SqContext::dateAsSqlDateType)
fun SqTable.date(columnName: String): SqTableColumn<LocalDate, Timestamp> = this.addColumn(columnName, SqContext::dateType)
fun SqTable.timeAsSqlTime(columnName: String): SqTableColumn<Time, Time> = this.addColumn(columnName, SqContext::timeAsSqlTimeType)
fun SqTable.time(columnName: String): SqTableColumn<LocalTime, Time> = this.addColumn(columnName, SqContext::timeType)
fun SqTable.timestampAsSqlTimestamp(columnName: String): SqTableColumn<Timestamp, Timestamp> = this.addColumn(columnName, SqContext::timestampAsSqlTimestampType)
fun SqTable.timestampAsCalendar(columnName: String): SqTableColumn<Calendar, Timestamp> = this.addColumn(columnName, SqContext::timestampAsCalendarType)
fun SqTable.timestampAsDate(columnName: String): SqTableColumn<java.util.Date, Timestamp> = this.addColumn(columnName, SqContext::timestampAsDateType)
fun SqTable.timestamp(columnName: String): SqTableColumn<LocalDateTime, Timestamp> = this.addColumn(columnName, SqContext::timestampType)
fun SqTable.timeWithTimeZone(columnName: String): SqTableColumn<OffsetTime, Time> = this.addColumn(columnName, SqContext::timeWithTimeZoneType)
fun SqTable.timestampWithTimeZone(columnName: String): SqTableColumn<OffsetDateTime, Timestamp> = this.addColumn(columnName, SqContext::timestampWithTimeZoneType)
