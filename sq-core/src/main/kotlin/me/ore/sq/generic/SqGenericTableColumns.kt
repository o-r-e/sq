package me.ore.sq.generic

import me.ore.sq.*
import java.math.BigDecimal
import java.net.URL
import java.sql.Ref
import java.sql.RowId
import java.sql.SQLXML


// region Boolean - BIT, BOOLEAN
fun SqTable.dbBit(columnName: String): SqTableColumn<Boolean, Boolean> = this.addColumn(columnName, SqGenericTypes.BIT)
fun SqTable.dbBoolean(columnName: String): SqTableColumn<Boolean, Boolean> = this.addColumn(columnName, SqGenericTypes.BOOLEAN)

fun SqTable.boolean(columnName: String): SqTableColumn<Boolean, Boolean> = this.dbBoolean(columnName)
// endregion

// region ByteArray - BINARY, VARBINARY, LONGVARBINARY
fun SqTable.dbBinary(columnName: String): SqTableColumn<SqByteArray, SqByteArray> = this.addColumn(columnName, SqGenericTypes.BINARY)
fun SqTable.dbVarBinary(columnName: String): SqTableColumn<SqByteArray, SqByteArray> = this.addColumn(columnName, SqGenericTypes.VAR_BINARY)
fun SqTable.dbLongVarBinary(columnName: String): SqTableColumn<SqByteArray, SqByteArray> = this.addColumn(columnName, SqGenericTypes.LONG_VAR_BINARY)

fun SqTable.jByteArray(columnName: String): SqTableColumn<SqByteArray, SqByteArray> = this.dbBinary(columnName)
// endregion

// region Number - DOUBLE, FLOAT, REAL, BIGINT, INTEGER, DECIMAL, NUMERIC
fun SqTable.dbDouble(columnName: String): SqTableColumn<Double, Number> = this.addColumn(columnName, SqGenericTypes.DOUBLE)
fun SqTable.dbFloat(columnName: String): SqTableColumn<Double, Number> = this.addColumn(columnName, SqGenericTypes.FLOAT)
fun SqTable.dbReal(columnName: String): SqTableColumn<Float, Number> = this.addColumn(columnName, SqGenericTypes.REAL)
fun SqTable.dbBigInt(columnName: String): SqTableColumn<Long, Number> = this.addColumn(columnName, SqGenericTypes.BIG_INT)
fun SqTable.dbInteger(columnName: String): SqTableColumn<Int, Number> = this.addColumn(columnName, SqGenericTypes.INTEGER)
fun SqTable.dbSmallInt(columnName: String): SqTableColumn<Short, Number> = this.addColumn(columnName, SqGenericTypes.SMALL_INT)
fun SqTable.dbTinyInt(columnName: String): SqTableColumn<Byte, Number> = this.addColumn(columnName, SqGenericTypes.TINY_INT)
fun SqTable.dbDecimal(columnName: String): SqTableColumn<BigDecimal, Number> = this.addColumn(columnName, SqGenericTypes.DECIMAL)
fun SqTable.dbNumeric(columnName: String): SqTableColumn<BigDecimal, Number> = this.addColumn(columnName, SqGenericTypes.NUMERIC)

fun SqTable.jBigDecimal(columnName: String): SqTableColumn<BigDecimal, Number> = this.dbDecimal(columnName)
fun SqTable.jByte(columnName: String): SqTableColumn<Byte, Number> = this.dbTinyInt(columnName)
fun SqTable.jDouble(columnName: String): SqTableColumn<Double, Number> = this.dbDouble(columnName)
fun SqTable.jFloat(columnName: String): SqTableColumn<Float, Number> = this.dbReal(columnName)
fun SqTable.jInt(columnName: String): SqTableColumn<Int, Number> = this.dbInteger(columnName)
fun SqTable.jLong(columnName: String): SqTableColumn<Long, Number> = this.dbBigInt(columnName)
fun SqTable.jShort(columnName: String): SqTableColumn<Short, Number> = this.dbSmallInt(columnName)
// endregion

// region String - CHAR, VARCHAR, LONGVARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR
fun SqTable.dbChar(columnName: String): SqTableColumn<String, String> = this.addColumn(columnName, SqGenericTypes.CHAR)
fun SqTable.dbVarChar(columnName: String): SqTableColumn<String, String> = this.addColumn(columnName, SqGenericTypes.VAR_CHAR)
fun SqTable.dbLongVarChar(columnName: String): SqTableColumn<String, String> = this.addColumn(columnName, SqGenericTypes.LONG_VAR_CHAR)
fun SqTable.dbNChar(columnName: String): SqTableColumn<String, String> = this.addColumn(columnName, SqGenericTypes.N_CHAR)
fun SqTable.dbNVarChar(columnName: String): SqTableColumn<String, String> = this.addColumn(columnName, SqGenericTypes.N_VAR_CHAR)
fun SqTable.dbLongNVarChar(columnName: String): SqTableColumn<String, String> = this.addColumn(columnName, SqGenericTypes.LONG_N_VAR_CHAR)

fun SqTable.jString(columnName: String): SqTableColumn<String, String> = this.dbVarChar(columnName)
// endregion

// region Temporal - TIME, DATE, TIMESTAMP
fun SqTable.dbTime(columnName: String): SqTableColumn<java.sql.Time, java.sql.Time> = this.addColumn(columnName, SqGenericTypes.TIME)
fun SqTable.dbDate(columnName: String): SqTableColumn<java.sql.Date, java.sql.Date> = this.addColumn(columnName, SqGenericTypes.DATE)
fun SqTable.dbTimestamp(columnName: String): SqTableColumn<java.sql.Timestamp, java.sql.Date> = this.addColumn(columnName, SqGenericTypes.TIMESTAMP)

fun SqTable.jTime(columnName: String): SqTableColumn<java.sql.Time, java.sql.Time> = this.dbTime(columnName)
fun SqTable.jDate(columnName: String): SqTableColumn<java.sql.Date, java.sql.Date> = this.dbDate(columnName)
fun SqTable.jTimestamp(columnName: String): SqTableColumn<java.sql.Timestamp, java.sql.Date> = this.dbTimestamp(columnName)
// endregion

// region Large objects - BLOB, CLOB, NCLOB
fun SqTable.dbBlob(columnName: String): SqTableColumn<java.sql.Blob, java.sql.Blob> = this.addColumn(columnName, SqGenericTypes.BLOB)
fun SqTable.dbClob(columnName: String): SqTableColumn<java.sql.Clob, java.sql.Clob> = this.addColumn(columnName, SqGenericTypes.CLOB)
fun SqTable.dbNClob(columnName: String): SqTableColumn<java.sql.NClob, java.sql.Clob> = this.addColumn(columnName, SqGenericTypes.N_CLOB)

fun SqTable.jBlob(columnName: String): SqTableColumn<java.sql.Blob, java.sql.Blob> = this.dbBlob(columnName)
fun SqTable.jClob(columnName: String): SqTableColumn<java.sql.Clob, java.sql.Clob> = this.dbClob(columnName)
fun SqTable.jNClob(columnName: String): SqTableColumn<java.sql.NClob, java.sql.Clob> = this.dbNClob(columnName)
// endregion

// region Various - DATALINK, REF, ROWID, SQLXML
fun SqTable.dbDataLink(columnName: String): SqTableColumn<URL, String> = this.addColumn(columnName, SqGenericTypes.DATA_LINK)
fun SqTable.dbRef(columnName: String): SqTableColumn<Ref, Ref> = this.addColumn(columnName, SqGenericTypes.REF)
fun SqTable.dbRowId(columnName: String): SqTableColumn<RowId, RowId> = this.addColumn(columnName, SqGenericTypes.ROW_ID)
fun SqTable.dbSqlXml(columnName: String): SqTableColumn<SQLXML, SQLXML> = this.addColumn(columnName, SqGenericTypes.SQL_XML)

fun SqTable.jDataLink(columnName: String): SqTableColumn<URL, String> = this.dbDataLink(columnName)
fun SqTable.jRef(columnName: String): SqTableColumn<Ref, Ref> = this.dbRef(columnName)
fun SqTable.jRowId(columnName: String): SqTableColumn<RowId, RowId> = this.dbRowId(columnName)
fun SqTable.jSqlXml(columnName: String): SqTableColumn<SQLXML, SQLXML> = this.dbSqlXml(columnName)
// endregion
