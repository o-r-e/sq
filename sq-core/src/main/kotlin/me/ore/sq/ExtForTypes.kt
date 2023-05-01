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


/**
 * Holder which contains [SqType] objects
 */
interface SqTypeHolder {
    // region Boolean types
    /**
     * Type for JDBC "BOOLEAN"
     */
    val boolean: SqType<Boolean, Boolean>


    /**
     * Type for [Boolean]; usually - for JDBC "BOOLEAN"
     */
    val javaBoolean: SqType<Boolean, Boolean>
        get() = this.boolean
    // endregion


    // region Byte array types
    /**
     * Type for JDBC "BINARY"
     */
    val binary: SqType<ByteArray, ByteArray>

    /**
     * Type for JDBC "LONGVARBINARY"
     */
    val longVarBinary: SqType<ByteArray, ByteArray>

    /**
     * Type for JDBC "VARBINARY"
     */
    val varBinary: SqType<ByteArray, ByteArray>


    /**
     * Type for [ByteArray];
     * usually - for one of JDBC "BINARY", "LONGVARBINARY" or "VARBINARY"
     */
    val javaByteArray: SqType<ByteArray, ByteArray>
        get() = this.varBinary
    // endregion


    // region Date/time types
    /**
     * Type for JDBC "DATE"
     *
     * @see dateAsDate
     */
    val date: SqType<LocalDate, Timestamp>

    /**
     * Type for JDBC "DATE"
     *
     * @see date
     */
    val dateAsDate: SqType<Date, Timestamp>

    /**
     * Type for JDBC "TIME"
     *
     * @see timeAsTime
     */
    val time: SqType<LocalTime, Time>

    /**
     * Type for JDBC "TIME"
     *
     * @see time
     */
    val timeAsTime: SqType<Time, Time>

    /**
     * Type for JDBC "TIME_WITH_TIMEZONE"
     */
    val timeWithTimeZone: SqType<OffsetTime, Time>

    /**
     * Type for JDBC "TIMESTAMP"
     *
     * @see timestampAsTimestamp
     */
    val timestamp: SqType<LocalDateTime, Timestamp>

    /**
     * Type for JDBC "TIMESTAMP"
     *
     * @see timestamp
     */
    val timestampAsTimestamp: SqType<Timestamp, Timestamp>

    /**
     * Type for JDBC "TIMESTAMP_WITH_TIMEZONE"
     */
    val timestampWithTimeZone: SqType<OffsetDateTime, Timestamp>


    /**
     * Type for [java.sql.Date]; usually - for JDBC "DATE"
     */
    val javaDate: SqType<Date, Timestamp>
        get() = this.dateAsDate

    /**
     * Type for [java.time.LocalDate]; usually - for JDBC "DATE"
     */
    val javaLocalDate: SqType<LocalDate, Timestamp>
        get() = this.date

    /**
     * Type for [java.time.LocalDateTime];
     * usually - for JDBC "TIMESTAMP"
     */
    val javaLocalDateTime: SqType<LocalDateTime, Timestamp>
        get() = this.timestamp

    /**
     * Type for [java.time.LocalTime];
     * usually - for JDBC "TIME"
     */
    val javaLocalTime: SqType<LocalTime, Time>
        get() = this.time

    /**
     * Type for [java.time.OffsetDateTime];
     * usually - for JDBC "TIMESTAMP_WITH_TIMEZONE"
     */
    val javaOffsetDateTime: SqType<OffsetDateTime, Timestamp>
        get() = this.timestampWithTimeZone

    /**
     * Type for [java.time.OffsetTime];
     * usually - for JDBC "TIME_WITH_TIMEZONE"
     */
    val javaOffsetTime: SqType<OffsetTime, Time>
        get() = this.timeWithTimeZone

    /**
     * Type for [java.sql.Time];
     * usually - for JDBC "TIME"
     */
    val javaTime: SqType<Time, Time>
        get() = this.timeAsTime

    /**
     * Type for [java.sql.Timestamp];
     * usually - for JDBC "TIMESTAMP"
     */
    val javaTimestamp: SqType<Timestamp, Timestamp>
        get() = this.timestampAsTimestamp
    // endregion


    // region Number types
    /**
     * Type for JDBC "BIGINT"
     *
     * @see bigIntAsBigInteger
     */
    val bigInt: SqType<Long, Number>

    /**
     * Type for JDBC "BIGINT"
     *
     * @see bigInt
     */
    val bigIntAsBigInteger: SqType<BigInteger, Number>

    /**
     * Type for JDBC "DECIMAL"
     */
    val decimal: SqType<BigDecimal, Number>

    /**
     * Type for JDBC "DOUBLE"
     */
    val double: SqType<Double, Number>

    /**
     * Type for JDBC "FLOAT"
     */
    val float: SqType<Double, Number>

    /**
     * Type for JDBC "INTEGER"
     */
    val integer: SqType<Int, Number>

    /**
     * Type for JDBC "NUMERIC"
     */
    val numeric: SqType<BigDecimal, Number>

    /**
     * Type for JDBC "REAL"
     */
    val real: SqType<Float, Number>

    /**
     * Type for JDBC "SMALLINT"
     */
    val smallInt: SqType<Short, Number>

    /**
     * Type for JDBC "TINYINT"
     */
    val tinyInt: SqType<Byte, Number>


    /**
     * Type for [java.math.BigDecimal];
     * usually - for JDBC "NUMERIC"
     */
    val javaBigDecimal: SqType<BigDecimal, Number>
        get() = this.numeric

    /**
     * Type for [java.math.BigInteger];
     * usually - for JDBC "BIGINT"
     */
    val javaBigInteger: SqType<BigInteger, Number>
        get() = this.bigIntAsBigInteger

    /**
     * Type for [Byte];
     * usually - for JDBC "TINYINT"
     */
    val javaByte: SqType<Byte, Number>
        get() = this.tinyInt

    /**
     * Type for [Double];
     * usually - for JDBC "DOUBLE"
     */
    val javaDouble: SqType<Double, Number>
        get() = this.double

    /**
     * Type for [Float];
     * usually - for JDBC "REAL"
     */
    val javaFloat: SqType<Float, Number>
        get() = this.real

    /**
     * Type for [Int];
     * usually - for JDBC "INTEGER"
     */
    val javaInt: SqType<Int, Number>
        get() = this.integer

    /**
     * Type for [Long];
     * usually - for JDBC "BIGINT"
     */
    val javaLong: SqType<Long, Number>
        get() = this.bigInt

    /**
     * Type for [Short];
     * usually - for JDBC "SMALLINT"
     */
    val javaShort: SqType<Short, Number>
        get() = this.smallInt


    /**
     * Type for [Number].
     *
     * Usually used only for reading data of JDBC types
     * "BIGINT", "DOUBLE", "INTEGER", "NUMERIC", "REAL", "SMALLINT", "TINYINT".
     *
     * Writing (setting parameters into [java.sql.PreparedStatement]) may cause errors.
     */
    val javaNumber: SqType<Number, Number>
    // endregion


    // region Text types
    /**
     * Type for JDBC "CHAR"
     */
    val char: SqType<String, String>

    /**
     * Type for JDBC "LONGVARCHAR"
     */
    val longVarChar: SqType<String, String>

    /**
     * Type for JDBC "VARCHAR"
     */
    val varChar: SqType<String, String>

    /**
     * Type for JDBC "NCHAR"
     */
    val nChar: SqType<String, String>

    /**
     * Type for JDBC "NVARCHAR"
     */
    val nVarChar: SqType<String, String>

    /**
     * Type for JDBC "LONGNVARCHAR"
     */
    val longNVarChar: SqType<String, String>


    /**
     * Type for [String];
     * usually - for one of JDBC types "CHAR", "LONGVARCHAR", "VARCHAR", "NCHAR", "NVARCHAR", "LONGNVARCHAR"
     */
    val javaString: SqType<String, String>
        get() = this.varChar
    // endregion


    // region Blob/Clob types
    /**
     * Type for JDBC "BLOB"
     */
    val blob: SqType<Blob, Blob>

    /**
     * Type for JDBC "CLOB"
     */
    val clob: SqType<Clob, Clob>

    /**
     * Type for JDBC "NCLOB"
     */
    val nClob: SqType<NClob, Clob>


    /**
     * Type for [java.sql.Blob];
     * usually - for JDBC "BLOB"
     */
    val javaBlob: SqType<Blob, Blob>
        get() = this.blob

    /**
     * Type for [java.sql.Clob];
     * usually - for JDBC "CLOB"
     */
    val javaClob: SqType<Clob, Clob>
        get() = this.clob

    /**
     * Type for [java.sql.NClob];
     * usually - for JDBC "NCLOB"
     */
    val javaNClob: SqType<NClob, Clob>
        get() = this.nClob
    // endregion


    // region Other JDBC types
    /**
     * Type for JDBC "DATALINK"
     */
    val dataLink: SqType<URL, String>

    /**
     * Type for JDBC "REF"
     */
    val ref: SqType<Ref, Ref>

    /**
     * Type for JDBC "ROWID"
     */
    val rowId: SqType<RowId, RowId>

    /**
     * Type for JDBC "SQLXML"
     */
    val sqlXml: SqType<SQLXML, String>


    /**
     * Type for [java.sql.Ref];
     * usually - for JDBC "REF"
     */
    val javaRef: SqType<Ref, Ref>
        get() = this.ref

    /**
     * Type for [java.sql.RowId];
     * usually - for JDBC "ROWID"
     */
    val javaRowId: SqType<RowId, RowId>
        get() = this.rowId

    /**
     * Type for [java.sql.SQLXML];
     * usually - for JDBC "SQLXML"
     */
    val javaSqlXml: SqType<SQLXML, String>
        get() = this.sqlXml

    /**
     * Type for [java.net.URL];
     * usually - for JDBC "DATALINK"
     */
    val javaUrl: SqType<URL, String>
        get() = this.dataLink
    // endregion


    // region API item types
    /**
     * Type used for [SqBooleanGroup];
     * usually - JDBC "BOOLEAN"
     */
    val booleanGroup: SqType<Boolean, Boolean>
        get() = this.boolean

    /**
     * Type used for [SqNot];
     * usually - JDBC "BOOLEAN"
     */
    val not: SqType<Boolean, Boolean>
        get() = this.boolean

    /**
     * Type used for [SqNullTest];
     * usually - JDBC "BOOLEAN"
     */
    val nullTest: SqType<Boolean, Boolean>
        get() = this.boolean

    /**
     * Type used for [SqComparison];
     * usually - JDBC "BOOLEAN"
     */
    val comparison: SqType<Boolean, Boolean>
        get() = this.boolean

    /**
     * Type used for [SqMathOperation];
     * usually - [javaNumber]
     */
    val mathOperation: SqType<Number, Number>
        get() = this.javaNumber
    // endregion
}


/**
 * @return instance of [SqTypeHolder] for current context
 */
fun SqContext.typeHolder(): SqTypeHolder =
    this[SqTypeHolder::class.java, SqGenericTypeHolder]


// region Boolean types
/**
 * @return type for JDBC "BOOLEAN"
 */
fun SqContext.booleanType(): SqType<Boolean, Boolean> =
    this.typeHolder().boolean

/**
 * Creates parameter with JDBC type "BOOLEAN"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("booleanParam__not_null")
fun SqContext.booleanParam(value: Boolean): SqParameter<Boolean, Boolean> =
    this.param(this.booleanType(), value)

/**
 * Creates parameter with JDBC type "BOOLEAN"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("booleanParam__nullable")
fun SqContext.booleanParam(value: Boolean?): SqParameter<Boolean?, Boolean> =
    this.param(this.booleanType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "BOOLEAN"
 */
fun SqContext.booleanNull(): SqNull<Boolean, Boolean> =
    this.nullItem(this.booleanType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "BOOLEAN"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.booleanNotNull(columnName: String): SqTableColumn<Boolean, Boolean> =
    this.column(SqGenericTypeHolder.boolean, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "BOOLEAN"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.booleanNullable(columnName: String): SqTableColumn<Boolean?, Boolean> =
    this.column(SqGenericTypeHolder.boolean.nullable(), columnName)


/**
 * @return type for [Boolean]; usually - for JDBC "BOOLEAN"
 */
fun SqContext.javaBooleanType(): SqType<Boolean, Boolean> =
    this.typeHolder().javaBoolean

/**
 * Creates parameter with type for [Boolean] (usually - for JDBC "BOOLEAN")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaBooleanParam__not_null")
fun SqContext.javaBooleanParam(value: Boolean): SqParameter<Boolean, Boolean> =
    this.param(this.javaBooleanType(), value)

/**
 * Creates parameter with type for [Boolean] (usually - for JDBC "BOOLEAN")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaBooleanParam__nullable")
fun SqContext.javaBooleanParam(value: Boolean?): SqParameter<Boolean?, Boolean> =
    this.param(this.javaBooleanType().nullable(), value)

/**
 * @return SQL NULL with type for [Boolean] (usually - for JDBC "BOOLEAN")
 */
fun SqContext.javaBooleanNull(): SqNull<Boolean, Boolean> =
    this.nullItem(this.javaBooleanType().nullable())
// endregion


// region Byte array types
/**
 * @return type for JDBC "BINARY"
 */
fun SqContext.binaryType(): SqType<ByteArray, ByteArray> =
    this.typeHolder().binary

/**
 * Creates parameter with JDBC type "BINARY"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("binaryParam__not_null")
fun SqContext.binaryParam(value: ByteArray): SqParameter<ByteArray, ByteArray> =
    this.param(this.binaryType(), value)

/**
 * Creates parameter with JDBC type "BINARY"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("binaryParam__nullable")
fun SqContext.binaryParam(value: ByteArray?): SqParameter<ByteArray?, ByteArray> =
    this.param(this.binaryType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "BINARY"
 */
fun SqContext.binaryNull(): SqNull<ByteArray, ByteArray> =
    this.nullItem(this.binaryType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "BINARY"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.binaryNotNull(columnName: String): SqTableColumn<ByteArray, ByteArray> =
    this.column(SqGenericTypeHolder.binary, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "BINARY"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.binaryNullable(columnName: String): SqTableColumn<ByteArray?, ByteArray> =
    this.column(SqGenericTypeHolder.binary.nullable(), columnName)


/**
 * @return type for JDBC "LONGVARBINARY"
 */
fun SqContext.longVarBinaryType(): SqType<ByteArray, ByteArray> =
    this.typeHolder().longVarBinary

/**
 * Creates parameter with JDBC type "LONGVARBINARY"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("longVarBinaryParam__not_null")
fun SqContext.longVarBinaryParam(value: ByteArray): SqParameter<ByteArray, ByteArray> =
    this.param(this.longVarBinaryType(), value)

/**
 * Creates parameter with JDBC type "LONGVARBINARY"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("longVarBinaryParam__nullable")
fun SqContext.longVarBinaryParam(value: ByteArray?): SqParameter<ByteArray?, ByteArray> =
    this.param(this.longVarBinaryType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "LONGVARBINARY"
 */
fun SqContext.longVarBinaryNull(): SqNull<ByteArray, ByteArray> =
    this.nullItem(this.longVarBinaryType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "LONGVARBINARY"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.longVarBinaryNotNull(columnName: String): SqTableColumn<ByteArray, ByteArray> =
    this.column(SqGenericTypeHolder.longVarBinary, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "LONGVARBINARY"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.longVarBinaryNullable(columnName: String): SqTableColumn<ByteArray?, ByteArray> =
    this.column(SqGenericTypeHolder.longVarBinary.nullable(), columnName)


/**
 * @return type for JDBC "VARBINARY"
 */
fun SqContext.varBinaryType(): SqType<ByteArray, ByteArray> =
    this.typeHolder().varBinary

/**
 * Creates parameter with JDBC type "VARBINARY"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("varBinaryParam__not_null")
fun SqContext.varBinaryParam(value: ByteArray): SqParameter<ByteArray, ByteArray> =
    this.param(this.varBinaryType(), value)

/**
 * Creates parameter with JDBC type "VARBINARY"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("varBinaryParam__nullable")
fun SqContext.varBinaryParam(value: ByteArray?): SqParameter<ByteArray?, ByteArray> =
    this.param(this.varBinaryType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "VARBINARY"
 */
fun SqContext.varBinaryNull(): SqNull<ByteArray, ByteArray> =
    this.nullItem(this.varBinaryType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "VARBINARY"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.varBinaryNotNull(columnName: String): SqTableColumn<ByteArray, ByteArray> =
    this.column(SqGenericTypeHolder.varBinary, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "VARBINARY"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.varBinaryNullable(columnName: String): SqTableColumn<ByteArray?, ByteArray> =
    this.column(SqGenericTypeHolder.varBinary.nullable(), columnName)


/**
 * @return type for [ByteArray]; usually - for one of JDBC "BINARY", "LONGVARBINARY" or "VARBINARY"
 */
fun SqContext.javaByteArrayType(): SqType<ByteArray, ByteArray> =
    this.typeHolder().javaByteArray

/**
 * Creates parameter with type for [ByteArray] (usually - for one of JDBC "BINARY", "LONGVARBINARY" or "VARBINARY")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaByteArrayParam__not_null")
fun SqContext.javaByteArrayParam(value: ByteArray): SqParameter<ByteArray, ByteArray> =
    this.param(this.javaByteArrayType(), value)

/**
 * Creates parameter with type for [ByteArray] (usually - for one of JDBC "BINARY", "LONGVARBINARY" or "VARBINARY")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaByteArrayParam__nullable")
fun SqContext.javaByteArrayParam(value: ByteArray?): SqParameter<ByteArray?, ByteArray> =
    this.param(this.javaByteArrayType().nullable(), value)

/**
 * @return SQL NULL with type for [ByteArray] (usually - for one of JDBC "BINARY", "LONGVARBINARY" or "VARBINARY")
 */
fun SqContext.javaByteArrayNull(): SqNull<ByteArray, ByteArray> =
    this.nullItem(this.javaByteArrayType().nullable())
// endregion


// region Date/time types
/**
 * @return type for JDBC "DATE"
 */
fun SqContext.dateType(): SqType<LocalDate, Timestamp> =
    this.typeHolder().date

/**
 * Creates parameter with JDBC type "DATE"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("dateParam__not_null")
fun SqContext.dateParam(value: LocalDate): SqParameter<LocalDate, Timestamp> =
    this.param(this.dateType(), value)

/**
 * Creates parameter with JDBC type "DATE"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("dateParam__nullable")
fun SqContext.dateParam(value: LocalDate?): SqParameter<LocalDate?, Timestamp> =
    this.param(this.dateType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "DATE"
 */
fun SqContext.dateNull(): SqNull<LocalDate, Timestamp> =
    this.nullItem(this.dateType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "DATE"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.dateNotNull(columnName: String): SqTableColumn<LocalDate, Timestamp> =
    this.column(SqGenericTypeHolder.date, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "DATE"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.dateNullable(columnName: String): SqTableColumn<LocalDate?, Timestamp> =
    this.column(SqGenericTypeHolder.date.nullable(), columnName)


/**
 * @return type for JDBC "DATE"
 */
fun SqContext.dateAsDateType(): SqType<Date, Timestamp> =
    this.typeHolder().dateAsDate

/**
 * Creates parameter with JDBC type "DATE"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("dateAsDateParam__not_null")
fun SqContext.dateAsDateParam(value: Date): SqParameter<Date, Timestamp> =
    this.param(this.dateAsDateType(), value)

/**
 * Creates parameter with JDBC type "DATE"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("dateAsDateParam__nullable")
fun SqContext.dateAsDateParam(value: Date?): SqParameter<Date?, Timestamp> =
    this.param(this.dateAsDateType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "DATE"
 */
fun SqContext.dateAsDateNull(): SqNull<Date, Timestamp> =
    this.nullItem(this.dateAsDateType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "DATE"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.dateAsDateNotNull(columnName: String): SqTableColumn<Date, Timestamp> =
    this.column(SqGenericTypeHolder.dateAsDate, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "DATE"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.dateAsDateNullable(columnName: String): SqTableColumn<Date?, Timestamp> =
    this.column(SqGenericTypeHolder.dateAsDate.nullable(), columnName)


/**
 * @return type for JDBC "TIME"
 */
fun SqContext.timeType(): SqType<LocalTime, Time> =
    this.typeHolder().time

/**
 * Creates parameter with JDBC type "TIME"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("timeParam__not_null")
fun SqContext.timeParam(value: LocalTime): SqParameter<LocalTime, Time> =
    this.param(this.timeType(), value)

/**
 * Creates parameter with JDBC type "TIME"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("timeParam__nullable")
fun SqContext.timeParam(value: LocalTime?): SqParameter<LocalTime?, Time> =
    this.param(this.timeType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "TIME"
 */
fun SqContext.timeNull(): SqNull<LocalTime, Time> =
    this.nullItem(this.timeType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "TIME"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.timeNotNull(columnName: String): SqTableColumn<LocalTime, Time> =
    this.column(SqGenericTypeHolder.time, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "TIME"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.timeNullable(columnName: String): SqTableColumn<LocalTime?, Time> =
    this.column(SqGenericTypeHolder.time.nullable(), columnName)


/**
 * @return type for JDBC "TIME"
 */
fun SqContext.timeAsTimeType(): SqType<Time, Time> =
    this.typeHolder().timeAsTime

/**
 * Creates parameter with JDBC type "TIME"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("timeAsTimeParam__not_null")
fun SqContext.timeAsTimeParam(value: Time): SqParameter<Time, Time> =
    this.param(this.timeAsTimeType(), value)

/**
 * Creates parameter with JDBC type "TIME"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("timeAsTimeParam__nullable")
fun SqContext.timeAsTimeParam(value: Time?): SqParameter<Time?, Time> =
    this.param(this.timeAsTimeType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "TIME"
 */
fun SqContext.timeAsTimeNull(): SqNull<Time, Time> =
    this.nullItem(this.timeAsTimeType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "TIME"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.timeAsTimeNotNull(columnName: String): SqTableColumn<Time, Time> =
    this.column(SqGenericTypeHolder.timeAsTime, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "TIME"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.timeAsTimeNullable(columnName: String): SqTableColumn<Time?, Time> =
    this.column(SqGenericTypeHolder.timeAsTime.nullable(), columnName)


/**
 * @return type for JDBC "TIME_WITH_TIMEZONE"
 */
fun SqContext.timeTZType(): SqType<OffsetTime, Time> =
    this.typeHolder().timeWithTimeZone

/**
 * Creates parameter with JDBC type "TIME_WITH_TIMEZONE"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("timeTZParam__not_null")
fun SqContext.timeTZParam(value: OffsetTime): SqParameter<OffsetTime, Time> =
    this.param(this.timeTZType(), value)

/**
 * Creates parameter with JDBC type "TIME_WITH_TIMEZONE"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("timeTZParam__nullable")
fun SqContext.timeTZParam(value: OffsetTime?): SqParameter<OffsetTime?, Time> =
    this.param(this.timeTZType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "TIME_WITH_TIMEZONE"
 */
fun SqContext.timeTZNull(): SqNull<OffsetTime, Time> =
    this.nullItem(this.timeTZType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "TIME_WITH_TIMEZONE"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.timeTZNotNull(columnName: String): SqTableColumn<OffsetTime, Time> =
    this.column(SqGenericTypeHolder.timeWithTimeZone, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "TIME_WITH_TIMEZONE"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.timeTZNullable(columnName: String): SqTableColumn<OffsetTime?, Time> =
    this.column(SqGenericTypeHolder.timeWithTimeZone.nullable(), columnName)


/**
 * @return type for JDBC "TIMESTAMP"
 */
fun SqContext.timestampType(): SqType<LocalDateTime, Timestamp> =
    this.typeHolder().timestamp

/**
 * Creates parameter with JDBC type "TIMESTAMP"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("timestampParam__not_null")
fun SqContext.timestampParam(value: LocalDateTime): SqParameter<LocalDateTime, Timestamp> =
    this.param(this.timestampType(), value)

/**
 * Creates parameter with JDBC type "TIMESTAMP"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("timestampParam__nullable")
fun SqContext.timestampParam(value: LocalDateTime?): SqParameter<LocalDateTime?, Timestamp> =
    this.param(this.timestampType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "TIMESTAMP"
 */
fun SqContext.timestampNull(): SqNull<LocalDateTime, Timestamp> =
    this.nullItem(this.timestampType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "TIMESTAMP"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.timestampNotNull(columnName: String): SqTableColumn<LocalDateTime, Timestamp> =
    this.column(SqGenericTypeHolder.timestamp, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "TIMESTAMP"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.timestampNullable(columnName: String): SqTableColumn<LocalDateTime?, Timestamp> =
    this.column(SqGenericTypeHolder.timestamp.nullable(), columnName)


/**
 * @return type for JDBC "TIMESTAMP"
 */
fun SqContext.timestampAsTimestampType(): SqType<Timestamp, Timestamp> =
    this.typeHolder().timestampAsTimestamp

/**
 * Creates parameter with JDBC type "TIMESTAMP"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("timestampAsTimestampParam__not_null")
fun SqContext.timestampAsTimestampParam(value: Timestamp): SqParameter<Timestamp, Timestamp> =
    this.param(this.timestampAsTimestampType(), value)

/**
 * Creates parameter with JDBC type "TIMESTAMP"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("timestampAsTimestampParam__nullable")
fun SqContext.timestampAsTimestampParam(value: Timestamp?): SqParameter<Timestamp?, Timestamp> =
    this.param(this.timestampAsTimestampType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "TIMESTAMP"
 */
fun SqContext.timestampAsTimestampNull(): SqNull<Timestamp, Timestamp> =
    this.nullItem(this.timestampAsTimestampType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "TIMESTAMP"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.timestampAsTimestampNotNull(columnName: String): SqTableColumn<Timestamp, Timestamp> =
    this.column(SqGenericTypeHolder.timestampAsTimestamp, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "TIMESTAMP"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.timestampAsTimestampNullable(columnName: String): SqTableColumn<Timestamp?, Timestamp> =
    this.column(SqGenericTypeHolder.timestampAsTimestamp.nullable(), columnName)


/**
 * @return type for JDBC "TIMESTAMP_WITH_TIMEZONE"
 */
fun SqContext.timestampTZType(): SqType<OffsetDateTime, Timestamp> =
    this.typeHolder().timestampWithTimeZone

/**
 * Creates parameter with JDBC type "TIMESTAMP_WITH_TIMEZONE"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("timestampTZParam__not_null")
fun SqContext.timestampTZParam(value: OffsetDateTime): SqParameter<OffsetDateTime, Timestamp> =
    this.param(this.timestampTZType(), value)

/**
 * Creates parameter with JDBC type "TIMESTAMP_WITH_TIMEZONE"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("timestampTZParam__nullable")
fun SqContext.timestampTZParam(value: OffsetDateTime?): SqParameter<OffsetDateTime?, Timestamp> =
    this.param(this.timestampTZType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "TIMESTAMP_WITH_TIMEZONE"
 */
fun SqContext.timestampTZNull(): SqNull<OffsetDateTime, Timestamp> =
    this.nullItem(this.timestampTZType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "TIMESTAMP_WITH_TIMEZONE"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.timestampTZNotNull(columnName: String): SqTableColumn<OffsetDateTime, Timestamp> =
    this.column(SqGenericTypeHolder.timestampWithTimeZone, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "TIMESTAMP_WITH_TIMEZONE"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.timestampTZNullable(columnName: String): SqTableColumn<OffsetDateTime?, Timestamp> =
    this.column(SqGenericTypeHolder.timestampWithTimeZone.nullable(), columnName)


/**
 * @return type for [java.sql.Date]; usually - for JDBC "DATE"
 */
fun SqContext.javaDateType(): SqType<Date, Timestamp> =
    this.typeHolder().javaDate

/**
 * Creates parameter with type for [java.sql.Date] (usually - for JDBC "DATE")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaDateParam__not_null")
fun SqContext.javaDateParam(value: Date): SqParameter<Date, Timestamp> =
    this.param(this.javaDateType(), value)

/**
 * Creates parameter with type for [java.sql.Date] (usually - for JDBC "DATE")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaDateParam__nullable")
fun SqContext.javaDateParam(value: Date?): SqParameter<Date?, Timestamp> =
    this.param(this.javaDateType().nullable(), value)

/**
 * @return SQL NULL with type for [java.sql.Date] (usually - for JDBC "DATE")
 */
fun SqContext.javaDateNull(): SqNull<Date, Timestamp> =
    this.nullItem(this.javaDateType().nullable())


/**
 * @return type for [java.time.LocalDate]; usually - for JDBC "DATE"
 */
fun SqContext.javaLocalDateType(): SqType<LocalDate, Timestamp> =
    this.typeHolder().javaLocalDate

/**
 * Creates parameter with type for [java.time.LocalDate] (usually - for JDBC "DATE")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaLocalDateParam__not_null")
fun SqContext.javaLocalDateParam(value: LocalDate): SqParameter<LocalDate, Timestamp> =
    this.param(this.javaLocalDateType(), value)

/**
 * Creates parameter with type for [java.time.LocalDate] (usually - for JDBC "DATE")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaLocalDateParam__nullable")
fun SqContext.javaLocalDateParam(value: LocalDate?): SqParameter<LocalDate?, Timestamp> =
    this.param(this.javaLocalDateType().nullable(), value)

/**
 * @return SQL NULL with type for [java.time.LocalDate] (usually - for JDBC "DATE")
 */
fun SqContext.javaLocalDateNull(): SqNull<LocalDate, Timestamp> =
    this.nullItem(this.javaLocalDateType().nullable())


/**
 * @return type for [java.time.LocalDateTime]; usually - for JDBC "TIMESTAMP"
 */
fun SqContext.javaLocalDateTimeType(): SqType<LocalDateTime, Timestamp> =
    this.typeHolder().javaLocalDateTime

/**
 * Creates parameter with type for [java.time.LocalDateTime] (usually - for JDBC "TIMESTAMP")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaLocalDateTimeParam__not_null")
fun SqContext.javaLocalDateTimeParam(value: LocalDateTime): SqParameter<LocalDateTime, Timestamp> =
    this.param(this.javaLocalDateTimeType(), value)

/**
 * Creates parameter with type for [java.time.LocalDateTime] (usually - for JDBC "TIMESTAMP")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaLocalDateTimeParam__nullable")
fun SqContext.javaLocalDateTimeParam(value: LocalDateTime?): SqParameter<LocalDateTime?, Timestamp> =
    this.param(this.javaLocalDateTimeType().nullable(), value)

/**
 * @return SQL NULL with type for [java.time.LocalDateTime] (usually - for JDBC "TIMESTAMP")
 */
fun SqContext.javaLocalDateTimeNull(): SqNull<LocalDateTime, Timestamp> =
    this.nullItem(this.javaLocalDateTimeType().nullable())


/**
 * @return type for [java.time.LocalTime]; usually - for JDBC "TIME"
 */
fun SqContext.javaLocalTimeType(): SqType<LocalTime, Time> =
    this.typeHolder().javaLocalTime

/**
 * Creates parameter with type for [java.time.LocalTime] (usually - for JDBC "TIME")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaLocalTimeParam__not_null")
fun SqContext.javaLocalTimeParam(value: LocalTime): SqParameter<LocalTime, Time> =
    this.param(this.javaLocalTimeType(), value)

/**
 * Creates parameter with type for [java.time.LocalTime] (usually - for JDBC "TIME")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaLocalTimeParam__nullable")
fun SqContext.javaLocalTimeParam(value: LocalTime?): SqParameter<LocalTime?, Time> =
    this.param(this.javaLocalTimeType().nullable(), value)

/**
 * @return SQL NULL with type for [java.time.LocalTime] (usually - for JDBC "TIME")
 */
fun SqContext.javaLocalTimeNull(): SqNull<LocalTime, Time> =
    this.nullItem(this.javaLocalTimeType().nullable())


/**
 * @return type for [java.time.OffsetDateTime]; usually - for JDBC "TIMESTAMP_WITH_TIMEZONE"
 */
fun SqContext.javaOffsetDateTimeType(): SqType<OffsetDateTime, Timestamp> =
    this.typeHolder().javaOffsetDateTime

/**
 * Creates parameter with type for [java.time.OffsetDateTime] (usually - for JDBC "TIMESTAMP_WITH_TIMEZONE")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaOffsetDateTimeParam__not_null")
fun SqContext.javaOffsetDateTimeParam(value: OffsetDateTime): SqParameter<OffsetDateTime, Timestamp> =
    this.param(this.javaOffsetDateTimeType(), value)

/**
 * Creates parameter with type for [java.time.OffsetDateTime] (usually - for JDBC "TIMESTAMP_WITH_TIMEZONE")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaOffsetDateTimeParam__nullable")
fun SqContext.javaOffsetDateTimeParam(value: OffsetDateTime?): SqParameter<OffsetDateTime?, Timestamp> =
    this.param(this.javaOffsetDateTimeType().nullable(), value)

/**
 * @return SQL NULL with type for [java.time.OffsetDateTime] (usually - for JDBC "TIMESTAMP_WITH_TIMEZONE")
 */
fun SqContext.javaOffsetDateTimeNull(): SqNull<OffsetDateTime, Timestamp> =
    this.nullItem(this.javaOffsetDateTimeType().nullable())


/**
 * @return type for [java.time.OffsetTime]; usually - for JDBC "TIME_WITH_TIMEZONE"
 */
fun SqContext.javaOffsetTimeType(): SqType<OffsetTime, Time> =
    this.typeHolder().javaOffsetTime

/**
 * Creates parameter with type for [java.time.OffsetTime] (usually - for JDBC "TIME_WITH_TIMEZONE")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaOffsetTimeParam__not_null")
fun SqContext.javaOffsetTimeParam(value: OffsetTime): SqParameter<OffsetTime, Time> =
    this.param(this.javaOffsetTimeType(), value)

/**
 * Creates parameter with type for [java.time.OffsetTime] (usually - for JDBC "TIME_WITH_TIMEZONE")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaOffsetTimeParam__nullable")
fun SqContext.javaOffsetTimeParam(value: OffsetTime?): SqParameter<OffsetTime?, Time> =
    this.param(this.javaOffsetTimeType().nullable(), value)

/**
 * @return SQL NULL with type for [java.time.OffsetTime] (usually - for JDBC "TIME_WITH_TIMEZONE")
 */
fun SqContext.javaOffsetTimeNull(): SqNull<OffsetTime, Time> =
    this.nullItem(this.javaOffsetTimeType().nullable())


/**
 * @return type for [java.sql.Time]; usually - for JDBC "TIME"
 */
fun SqContext.javaTimeType(): SqType<Time, Time> =
    this.typeHolder().javaTime

/**
 * Creates parameter with type for [java.sql.Time] (usually - for JDBC "TIME")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaTimeParam__not_null")
fun SqContext.javaTimeParam(value: Time): SqParameter<Time, Time> =
    this.param(this.javaTimeType(), value)

/**
 * Creates parameter with type for [java.sql.Time] (usually - for JDBC "TIME")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaTimeParam__nullable")
fun SqContext.javaTimeParam(value: Time?): SqParameter<Time?, Time> =
    this.param(this.javaTimeType().nullable(), value)

/**
 * @return SQL NULL with type for [java.sql.Time] (usually - for JDBC "TIME")
 */
fun SqContext.javaTimeNull(): SqNull<Time, Time> =
    this.nullItem(this.javaTimeType().nullable())


/**
 * @return type for [java.sql.Timestamp]; usually - for JDBC "TIMESTAMP"
 */
fun SqContext.javaTimestampType(): SqType<Timestamp, Timestamp> =
    this.typeHolder().javaTimestamp

/**
 * Creates parameter with type for [java.sql.Timestamp] (usually - for JDBC "TIMESTAMP")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaTimestampParam__not_null")
fun SqContext.javaTimestampParam(value: Timestamp): SqParameter<Timestamp, Timestamp> =
    this.param(this.javaTimestampType(), value)

/**
 * Creates parameter with type for [java.sql.Timestamp] (usually - for JDBC "TIMESTAMP")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaTimestampParam__nullable")
fun SqContext.javaTimestampParam(value: Timestamp?): SqParameter<Timestamp?, Timestamp> =
    this.param(this.javaTimestampType().nullable(), value)

/**
 * @return SQL NULL with type for [java.sql.Timestamp] (usually - for JDBC "TIMESTAMP")
 */
fun SqContext.javaTimestampNull(): SqNull<Timestamp, Timestamp> =
    this.nullItem(this.javaTimestampType().nullable())
// endregion


// region Number types
/**
 * @return type for JDBC "BIGINT"
 */
fun SqContext.bigIntType(): SqType<Long, Number> =
    this.typeHolder().bigInt

/**
 * Creates parameter with JDBC type "BIGINT"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("bigIntParam__not_null")
fun SqContext.bigIntParam(value: Long): SqParameter<Long, Number> =
    this.param(this.bigIntType(), value)

/**
 * Creates parameter with JDBC type "BIGINT"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("bigIntParam__nullable")
fun SqContext.bigIntParam(value: Long?): SqParameter<Long?, Number> =
    this.param(this.bigIntType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "BIGINT"
 */
fun SqContext.bigIntNull(): SqNull<Long, Number> =
    this.nullItem(this.bigIntType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "BIGINT"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.bigIntNotNull(columnName: String): SqTableColumn<Long, Number> =
    this.column(SqGenericTypeHolder.bigInt, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "BIGINT"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.bigIntNullable(columnName: String): SqTableColumn<Long?, Number> =
    this.column(SqGenericTypeHolder.bigInt.nullable(), columnName)


/**
 * @return type for JDBC "BIGINT"
 */
fun SqContext.bigIntAsBigIntegerType(): SqType<BigInteger, Number> =
    this.typeHolder().bigIntAsBigInteger

/**
 * Creates parameter with JDBC type "BIGINT"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("bigIntAsBigIntegerParam__not_null")
fun SqContext.bigIntAsBigIntegerParam(value: BigInteger): SqParameter<BigInteger, Number> =
    this.param(this.bigIntAsBigIntegerType(), value)

/**
 * Creates parameter with JDBC type "BIGINT"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("bigIntAsBigIntegerParam__nullable")
fun SqContext.bigIntAsBigIntegerParam(value: BigInteger?): SqParameter<BigInteger?, Number> =
    this.param(this.bigIntAsBigIntegerType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "BIGINT"
 */
fun SqContext.bigIntAsBigIntegerNull(): SqNull<BigInteger, Number> =
    this.nullItem(this.bigIntAsBigIntegerType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "BIGINT"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.bigIntAsBigIntegerNotNull(columnName: String): SqTableColumn<BigInteger, Number> =
    this.column(SqGenericTypeHolder.bigIntAsBigInteger, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "BIGINT"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.bigIntAsBigIntegerNullable(columnName: String): SqTableColumn<BigInteger?, Number> =
    this.column(SqGenericTypeHolder.bigIntAsBigInteger.nullable(), columnName)


/**
 * @return type for JDBC "DECIMAL"
 */
fun SqContext.decimalType(): SqType<BigDecimal, Number> =
    this.typeHolder().decimal

/**
 * Creates parameter with JDBC type "DECIMAL"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("decimalParam__not_null")
fun SqContext.decimalParam(value: BigDecimal): SqParameter<BigDecimal, Number> =
    this.param(this.decimalType(), value)

/**
 * Creates parameter with JDBC type "DECIMAL"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("decimalParam__nullable")
fun SqContext.decimalParam(value: BigDecimal?): SqParameter<BigDecimal?, Number> =
    this.param(this.decimalType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "DECIMAL"
 */
fun SqContext.decimalNull(): SqNull<BigDecimal, Number> =
    this.nullItem(this.decimalType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "DECIMAL"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.decimalNotNull(columnName: String): SqTableColumn<BigDecimal, Number> =
    this.column(SqGenericTypeHolder.decimal, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "DECIMAL"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.decimalNullable(columnName: String): SqTableColumn<BigDecimal?, Number> =
    this.column(SqGenericTypeHolder.decimal.nullable(), columnName)


/**
 * @return type for JDBC "DOUBLE"
 */
fun SqContext.doubleType(): SqType<Double, Number> =
    this.typeHolder().double

/**
 * Creates parameter with JDBC type "DOUBLE"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("doubleParam__not_null")
fun SqContext.doubleParam(value: Double): SqParameter<Double, Number> =
    this.param(this.doubleType(), value)

/**
 * Creates parameter with JDBC type "DOUBLE"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("doubleParam__nullable")
fun SqContext.doubleParam(value: Double?): SqParameter<Double?, Number> =
    this.param(this.doubleType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "DOUBLE"
 */
fun SqContext.doubleNull(): SqNull<Double, Number> =
    this.nullItem(this.doubleType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "DOUBLE"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.doubleNotNull(columnName: String): SqTableColumn<Double, Number> =
    this.column(SqGenericTypeHolder.double, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "DOUBLE"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.doubleNullable(columnName: String): SqTableColumn<Double?, Number> =
    this.column(SqGenericTypeHolder.double.nullable(), columnName)


/**
 * @return type for JDBC "FLOAT"
 */
fun SqContext.floatType(): SqType<Double, Number> =
    this.typeHolder().float

/**
 * Creates parameter with JDBC type "FLOAT"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("floatParam__not_null")
fun SqContext.floatParam(value: Double): SqParameter<Double, Number> =
    this.param(this.floatType(), value)

/**
 * Creates parameter with JDBC type "FLOAT"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("floatParam__nullable")
fun SqContext.floatParam(value: Double?): SqParameter<Double?, Number> =
    this.param(this.floatType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "FLOAT"
 */
fun SqContext.floatNull(): SqNull<Double, Number> =
    this.nullItem(this.floatType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "FLOAT"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.floatNotNull(columnName: String): SqTableColumn<Double, Number> =
    this.column(SqGenericTypeHolder.float, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "FLOAT"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.floatNullable(columnName: String): SqTableColumn<Double?, Number> =
    this.column(SqGenericTypeHolder.float.nullable(), columnName)


/**
 * @return type for JDBC "INTEGER"
 */
fun SqContext.integerType(): SqType<Int, Number> =
    this.typeHolder().integer

/**
 * Creates parameter with JDBC type "INTEGER"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("integerParam__not_null")
fun SqContext.integerParam(value: Int): SqParameter<Int, Number> =
    this.param(this.integerType(), value)

/**
 * Creates parameter with JDBC type "INTEGER"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("integerParam__nullable")
fun SqContext.integerParam(value: Int?): SqParameter<Int?, Number> =
    this.param(this.integerType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "INTEGER"
 */
fun SqContext.integerNull(): SqNull<Int, Number> =
    this.nullItem(this.integerType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "INTEGER"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.integerNotNull(columnName: String): SqTableColumn<Int, Number> =
    this.column(SqGenericTypeHolder.integer, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "INTEGER"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.integerNullable(columnName: String): SqTableColumn<Int?, Number> =
    this.column(SqGenericTypeHolder.integer.nullable(), columnName)


/**
 * @return type for JDBC "NUMERIC"
 */
fun SqContext.numericType(): SqType<BigDecimal, Number> =
    this.typeHolder().numeric

/**
 * Creates parameter with JDBC type "NUMERIC"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("numericParam__not_null")
fun SqContext.numericParam(value: BigDecimal): SqParameter<BigDecimal, Number> =
    this.param(this.numericType(), value)

/**
 * Creates parameter with JDBC type "NUMERIC"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("numericParam__nullable")
fun SqContext.numericParam(value: BigDecimal?): SqParameter<BigDecimal?, Number> =
    this.param(this.numericType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "NUMERIC"
 */
fun SqContext.numericNull(): SqNull<BigDecimal, Number> =
    this.nullItem(this.numericType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "NUMERIC"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.numericNotNull(columnName: String): SqTableColumn<BigDecimal, Number> =
    this.column(SqGenericTypeHolder.numeric, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "NUMERIC"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.numericNullable(columnName: String): SqTableColumn<BigDecimal?, Number> =
    this.column(SqGenericTypeHolder.numeric.nullable(), columnName)


/**
 * @return type for JDBC "REAL"
 */
fun SqContext.realType(): SqType<Float, Number> =
    this.typeHolder().real

/**
 * Creates parameter with JDBC type "REAL"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("realParam__not_null")
fun SqContext.realParam(value: Float): SqParameter<Float, Number> =
    this.param(this.realType(), value)

/**
 * Creates parameter with JDBC type "REAL"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("realParam__nullable")
fun SqContext.realParam(value: Float?): SqParameter<Float?, Number> =
    this.param(this.realType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "REAL"
 */
fun SqContext.realNull(): SqNull<Float, Number> =
    this.nullItem(this.realType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "REAL"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.realNotNull(columnName: String): SqTableColumn<Float, Number> =
    this.column(SqGenericTypeHolder.real, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "REAL"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.realNullable(columnName: String): SqTableColumn<Float?, Number> =
    this.column(SqGenericTypeHolder.real.nullable(), columnName)


/**
 * @return type for JDBC "SMALLINT"
 */
fun SqContext.smallIntType(): SqType<Short, Number> =
    this.typeHolder().smallInt

/**
 * Creates parameter with JDBC type "SMALLINT"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("smallIntParam__not_null")
fun SqContext.smallIntParam(value: Short): SqParameter<Short, Number> =
    this.param(this.smallIntType(), value)

/**
 * Creates parameter with JDBC type "SMALLINT"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("smallIntParam__nullable")
fun SqContext.smallIntParam(value: Short?): SqParameter<Short?, Number> =
    this.param(this.smallIntType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "SMALLINT"
 */
fun SqContext.smallIntNull(): SqNull<Short, Number> =
    this.nullItem(this.smallIntType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "SMALLINT"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.smallIntNotNull(columnName: String): SqTableColumn<Short, Number> =
    this.column(SqGenericTypeHolder.smallInt, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "SMALLINT"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.smallIntNullable(columnName: String): SqTableColumn<Short?, Number> =
    this.column(SqGenericTypeHolder.smallInt.nullable(), columnName)


/**
 * @return type for JDBC "TINYINT"
 */
fun SqContext.tinyIntType(): SqType<Byte, Number> =
    this.typeHolder().tinyInt

/**
 * Creates parameter with JDBC type "TINYINT"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("tinyIntParam__not_null")
fun SqContext.tinyIntParam(value: Byte): SqParameter<Byte, Number> =
    this.param(this.tinyIntType(), value)

/**
 * Creates parameter with JDBC type "TINYINT"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("tinyIntParam__nullable")
fun SqContext.tinyIntParam(value: Byte?): SqParameter<Byte?, Number> =
    this.param(this.tinyIntType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "TINYINT"
 */
fun SqContext.tinyIntNull(): SqNull<Byte, Number> =
    this.nullItem(this.tinyIntType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "TINYINT"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.tinyIntNotNull(columnName: String): SqTableColumn<Byte, Number> =
    this.column(SqGenericTypeHolder.tinyInt, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "TINYINT"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.tinyIntNullable(columnName: String): SqTableColumn<Byte?, Number> =
    this.column(SqGenericTypeHolder.tinyInt.nullable(), columnName)



/**
 * @return type for [java.math.BigDecimal]; usually - for JDBC "NUMERIC"
 */
fun SqContext.javaBigDecimalType(): SqType<BigDecimal, Number> =
    this.typeHolder().javaBigDecimal

/**
 * Creates parameter with type for [java.math.BigDecimal] (usually - for JDBC "NUMERIC")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaBigDecimalParam__not_null")
fun SqContext.javaBigDecimalParam(value: BigDecimal): SqParameter<BigDecimal, Number> =
    this.param(this.javaBigDecimalType(), value)

/**
 * Creates parameter with type for [java.math.BigDecimal] (usually - for JDBC "NUMERIC")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaBigDecimalParam__nullable")
fun SqContext.javaBigDecimalParam(value: BigDecimal?): SqParameter<BigDecimal?, Number> =
    this.param(this.javaBigDecimalType().nullable(), value)

/**
 * @return SQL NULL with type for [java.math.BigDecimal] (usually - for JDBC "NUMERIC")
 */
fun SqContext.javaBigDecimalNull(): SqNull<BigDecimal, Number> =
    this.nullItem(this.javaBigDecimalType().nullable())


/**
 * @return type for [java.math.BigInteger]; usually - for JDBC "BIGINT"
 */
fun SqContext.javaBigIntegerType(): SqType<BigInteger, Number> =
    this.typeHolder().javaBigInteger

/**
 * Creates parameter with type for [java.math.BigInteger] (usually - for JDBC "BIGINT")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaBigIntegerParam__not_null")
fun SqContext.javaBigIntegerParam(value: BigInteger): SqParameter<BigInteger, Number> =
    this.param(this.javaBigIntegerType(), value)

/**
 * Creates parameter with type for [java.math.BigInteger] (usually - for JDBC "BIGINT")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaBigIntegerParam__nullable")
fun SqContext.javaBigIntegerParam(value: BigInteger?): SqParameter<BigInteger?, Number> =
    this.param(this.javaBigIntegerType().nullable(), value)

/**
 * @return SQL NULL with type for [java.math.BigInteger] (usually - for JDBC "BIGINT")
 */
fun SqContext.javaBigIntegerNull(): SqNull<BigInteger, Number> =
    this.nullItem(this.javaBigIntegerType().nullable())


/**
 * @return type for [Byte]; usually - for JDBC "TINYINT"
 */
fun SqContext.javaByteType(): SqType<Byte, Number> =
    this.typeHolder().javaByte

/**
 * Creates parameter with type for [Byte] (usually - for JDBC "TINYINT")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaByteParam__not_null")
fun SqContext.javaByteParam(value: Byte): SqParameter<Byte, Number> =
    this.param(this.javaByteType(), value)

/**
 * Creates parameter with type for [Byte] (usually - for JDBC "TINYINT")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaByteParam__nullable")
fun SqContext.javaByteParam(value: Byte?): SqParameter<Byte?, Number> =
    this.param(this.javaByteType().nullable(), value)

/**
 * @return SQL NULL with type for [Byte] (usually - for JDBC "TINYINT")
 */
fun SqContext.javaByteNull(): SqNull<Byte, Number> =
    this.nullItem(this.javaByteType().nullable())


/**
 * @return type for [Double]; usually - for JDBC "DOUBLE"
 */
fun SqContext.javaDoubleType(): SqType<Double, Number> =
    this.typeHolder().double

/**
 * Creates parameter with type for [Double] (usually - for JDBC "DOUBLE")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaDoubleParam__not_null")
fun SqContext.javaDoubleParam(value: Double): SqParameter<Double, Number> =
    this.param(this.javaDoubleType(), value)

/**
 * Creates parameter with type for [Double] (usually - for JDBC "DOUBLE")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaDoubleParam__nullable")
fun SqContext.javaDoubleParam(value: Double?): SqParameter<Double?, Number> =
    this.param(this.javaDoubleType().nullable(), value)

/**
 * @return SQL NULL with type for [Double] (usually - for JDBC "DOUBLE")
 */
fun SqContext.javaDoubleNull(): SqNull<Double, Number> =
    this.nullItem(this.javaDoubleType().nullable())


/**
 * @return type for [Float]; usually - for JDBC "REAL"
 */
fun SqContext.javaFloatType(): SqType<Float, Number> =
    this.typeHolder().javaFloat

/**
 * Creates parameter with type for [Float] (usually - for JDBC "REAL")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaFloatParam__not_null")
fun SqContext.javaFloatParam(value: Float): SqParameter<Float, Number> =
    this.param(this.javaFloatType(), value)

/**
 * Creates parameter with type for [Float] (usually - for JDBC "REAL")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaFloatParam__nullable")
fun SqContext.javaFloatParam(value: Float?): SqParameter<Float?, Number> =
    this.param(this.javaFloatType().nullable(), value)

/**
 * @return SQL NULL with type for [Float] (usually - for JDBC "REAL")
 */
fun SqContext.javaFloatNull(): SqNull<Float, Number> =
    this.nullItem(this.javaFloatType().nullable())


/**
 * @return type for [Int]; usually - for JDBC "INTEGER"
 */
fun SqContext.javaIntType(): SqType<Int, Number> =
    this.typeHolder().javaInt

/**
 * Creates parameter with type for [Int] (usually - for JDBC "INTEGER")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaIntParam__not_null")
fun SqContext.javaIntParam(value: Int): SqParameter<Int, Number> =
    this.param(this.javaIntType(), value)

/**
 * Creates parameter with type for [Int] (usually - for JDBC "INTEGER")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaIntParam__nullable")
fun SqContext.javaIntParam(value: Int?): SqParameter<Int?, Number> =
    this.param(this.javaIntType().nullable(), value)

/**
 * @return SQL NULL with type for [Int] (usually - for JDBC "INTEGER")
 */
fun SqContext.javaIntNull(): SqNull<Int, Number> =
    this.nullItem(this.javaIntType().nullable())


/**
 * @return type for [Long]; usually - for JDBC "BIGINT"
 */
fun SqContext.javaLongType(): SqType<Long, Number> =
    this.typeHolder().javaLong

/**
 * Creates parameter with type for [Long] (usually - for JDBC "BIGINT")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaLongParam__not_null")
fun SqContext.javaLongParam(value: Long): SqParameter<Long, Number> =
    this.param(this.javaLongType(), value)

/**
 * Creates parameter with type for [Long] (usually - for JDBC "BIGINT")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaLongParam__nullable")
fun SqContext.javaLongParam(value: Long?): SqParameter<Long?, Number> =
    this.param(this.javaLongType().nullable(), value)

/**
 * @return SQL NULL with type for [Long] (usually - for JDBC "BIGINT")
 */
fun SqContext.javaLongNull(): SqNull<Long, Number> =
    this.nullItem(this.javaLongType().nullable())


/**
 * @return type for [Short]; usually - for JDBC "SMALLINT"
 */
fun SqContext.javaShortType(): SqType<Short, Number> =
    this.typeHolder().javaShort

/**
 * Creates parameter with type for [Short] (usually - for JDBC "SMALLINT")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaShortParam__not_null")
fun SqContext.javaShortParam(value: Short): SqParameter<Short, Number> =
    this.param(this.javaShortType(), value)

/**
 * Creates parameter with type for [Short] (usually - for JDBC "SMALLINT")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaShortParam__nullable")
fun SqContext.javaShortParam(value: Short?): SqParameter<Short?, Number> =
    this.param(this.javaShortType().nullable(), value)

/**
 * @return SQL NULL with type for [Short] (usually - for JDBC "SMALLINT")
 */
fun SqContext.javaShortNull(): SqNull<Short, Number> =
    this.nullItem(this.javaShortType().nullable())


/**
 * @return type for [Number] (see [SqTypeHolder.javaNumber])
 */
fun SqContext.javaNumberType(): SqType<Number, Number> =
    this.typeHolder().javaNumber

/**
 * Creates parameter with type for [Number] (see [SqTypeHolder.javaNumber])
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaNumberParam__not_null")
fun SqContext.javaNumberParam(value: Number): SqParameter<Number, Number> =
    this.param(this.javaNumberType(), value)

/**
 * Creates parameter with type for [Number] (see [SqTypeHolder.javaNumber])
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaNumberParam__nullable")
fun SqContext.javaNumberParam(value: Number?): SqParameter<Number?, Number> =
    this.param(this.javaNumberType().nullable(), value)

/**
 * @return SQL NULL with type for [Number] (see [SqTypeHolder.javaNumber])
 */
fun SqContext.javaNumberNull(): SqNull<Number, Number> =
    this.nullItem(this.javaNumberType().nullable())
// endregion


// region Text types
/**
 * @return type for JDBC "CHAR"
 */
fun SqContext.charType(): SqType<String, String> =
    this.typeHolder().char

/**
 * Creates parameter with JDBC type "CHAR"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("charParam__not_null")
fun SqContext.charParam(value: String): SqParameter<String, String> =
    this.param(this.charType(), value)

/**
 * Creates parameter with JDBC type "CHAR"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("charParam__nullable")
fun SqContext.charParam(value: String?): SqParameter<String?, String> =
    this.param(this.charType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "CHAR"
 */
fun SqContext.charNull(): SqNull<String, String> =
    this.nullItem(this.charType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "CHAR"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.charNotNull(columnName: String): SqTableColumn<String, String> =
    this.column(SqGenericTypeHolder.char, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "CHAR"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.charNullable(columnName: String): SqTableColumn<String?, String> =
    this.column(SqGenericTypeHolder.char.nullable(), columnName)


/**
 * @return type for JDBC "LONGVARCHAR"
 */
fun SqContext.longVarCharType(): SqType<String, String> =
    this.typeHolder().longVarChar

/**
 * Creates parameter with JDBC type "LONGVARCHAR"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("longVarCharParam__not_null")
fun SqContext.longVarCharParam(value: String): SqParameter<String, String> =
    this.param(this.longVarCharType(), value)

/**
 * Creates parameter with JDBC type "LONGVARCHAR"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("longVarCharParam__nullable")
fun SqContext.longVarCharParam(value: String?): SqParameter<String?, String> =
    this.param(this.longVarCharType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "LONGVARCHAR"
 */
fun SqContext.longVarCharNull(): SqNull<String, String> =
    this.nullItem(this.longVarCharType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "LONGVARCHAR"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.longVarCharNotNull(columnName: String): SqTableColumn<String, String> =
    this.column(SqGenericTypeHolder.longVarChar, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "LONGVARCHAR"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.longVarCharNullable(columnName: String): SqTableColumn<String?, String> =
    this.column(SqGenericTypeHolder.longVarChar.nullable(), columnName)


/**
 * @return type for JDBC "VARCHAR"
 */
fun SqContext.varCharType(): SqType<String, String> =
    this.typeHolder().varChar

/**
 * Creates parameter with JDBC type "VARCHAR"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("varCharParam__not_null")
fun SqContext.varCharParam(value: String): SqParameter<String, String> =
    this.param(this.varCharType(), value)

/**
 * Creates parameter with JDBC type "VARCHAR"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("varCharParam__nullable")
fun SqContext.varCharParam(value: String?): SqParameter<String?, String> =
    this.param(this.varCharType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "VARCHAR"
 */
fun SqContext.varCharNull(): SqNull<String, String> =
    this.nullItem(this.varCharType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "VARCHAR"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.varCharNotNull(columnName: String): SqTableColumn<String, String> =
    this.column(SqGenericTypeHolder.varChar, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "VARCHAR"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.varCharNullable(columnName: String): SqTableColumn<String?, String> =
    this.column(SqGenericTypeHolder.varChar.nullable(), columnName)


/**
 * @return type for JDBC "NCHAR"
 */
fun SqContext.nCharType(): SqType<String, String> =
    this.typeHolder().nChar

/**
 * Creates parameter with JDBC type "NCHAR"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("nCharParam__not_null")
fun SqContext.nCharParam(value: String): SqParameter<String, String> =
    this.param(this.nCharType(), value)

/**
 * Creates parameter with JDBC type "NCHAR"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("nCharParam__nullable")
fun SqContext.nCharParam(value: String?): SqParameter<String?, String> =
    this.param(this.nCharType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "NCHAR"
 */
fun SqContext.nCharNull(): SqNull<String, String> =
    this.nullItem(this.nCharType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "NCHAR"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.nCharNotNull(columnName: String): SqTableColumn<String, String> =
    this.column(SqGenericTypeHolder.nChar, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "NCHAR"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.nCharNullable(columnName: String): SqTableColumn<String?, String> =
    this.column(SqGenericTypeHolder.nChar.nullable(), columnName)


/**
 * @return type for JDBC "NVARCHAR"
 */
fun SqContext.nVarCharType(): SqType<String, String> =
    this.typeHolder().nVarChar

/**
 * Creates parameter with JDBC type "NVARCHAR"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("nVarCharParam__not_null")
fun SqContext.nVarCharParam(value: String): SqParameter<String, String> =
    this.param(this.nVarCharType(), value)

/**
 * Creates parameter with JDBC type "NVARCHAR"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("nVarCharParam__nullable")
fun SqContext.nVarCharParam(value: String?): SqParameter<String?, String> =
    this.param(this.nVarCharType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "NVARCHAR"
 */
fun SqContext.nVarCharNull(): SqNull<String, String> =
    this.nullItem(this.nVarCharType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "NVARCHAR"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.nVarCharNotNull(columnName: String): SqTableColumn<String, String> =
    this.column(SqGenericTypeHolder.nVarChar, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "NVARCHAR"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.nVarCharNullable(columnName: String): SqTableColumn<String?, String> =
    this.column(SqGenericTypeHolder.nVarChar.nullable(), columnName)


/**
 * @return type for JDBC "LONGNVARCHAR"
 */
fun SqContext.longNVarCharType(): SqType<String, String> =
    this.typeHolder().longNVarChar

/**
 * Creates parameter with JDBC type "LONGNVARCHAR"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("longNVarCharParam__not_null")
fun SqContext.longNVarCharParam(value: String): SqParameter<String, String> =
    this.param(this.longNVarCharType(), value)

/**
 * Creates parameter with JDBC type "LONGNVARCHAR"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("longNVarCharParam__nullable")
fun SqContext.longNVarCharParam(value: String?): SqParameter<String?, String> =
    this.param(this.longNVarCharType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "LONGNVARCHAR"
 */
fun SqContext.longNVarCharNull(): SqNull<String, String> =
    this.nullItem(this.longNVarCharType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "LONGNVARCHAR"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.longNVarCharNotNull(columnName: String): SqTableColumn<String, String> =
    this.column(SqGenericTypeHolder.longNVarChar, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "LONGNVARCHAR"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.longNVarCharNullable(columnName: String): SqTableColumn<String?, String> =
    this.column(SqGenericTypeHolder.longNVarChar.nullable(), columnName)



/**
 * @return type for [String];
 * usually - for one of JDBC types "CHAR", "LONGVARCHAR", "VARCHAR", "NCHAR", "NVARCHAR", "LONGNVARCHAR"
 */
fun SqContext.javaStringType(): SqType<String, String> =
    this.typeHolder().javaString

/**
 * Creates parameter with type for [String]
 * (usually - for one of JDBC types "CHAR", "LONGVARCHAR", "VARCHAR", "NCHAR", "NVARCHAR", "LONGNVARCHAR")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaStringParam__not_null")
fun SqContext.javaStringParam(value: String): SqParameter<String, String> =
    this.param(this.javaStringType(), value)

/**
 * Creates parameter with type for [String]
 * (usually - for one of JDBC types "CHAR", "LONGVARCHAR", "VARCHAR", "NCHAR", "NVARCHAR", "LONGNVARCHAR")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaStringParam__nullable")
fun SqContext.javaStringParam(value: String?): SqParameter<String?, String> =
    this.param(this.javaStringType().nullable(), value)

/**
 * @return SQL NULL with type for [String]
 * (usually - for one of JDBC types "CHAR", "LONGVARCHAR", "VARCHAR", "NCHAR", "NVARCHAR", "LONGNVARCHAR")
 */
fun SqContext.javaStringNull(): SqNull<String, String> =
    this.nullItem(this.javaStringType().nullable())
// endregion


// region Blob/Clob types
/**
 * @return type for JDBC "BLOB"
 */
fun SqContext.blobType(): SqType<Blob, Blob> =
    this.typeHolder().blob

/**
 * Creates parameter with JDBC type "BLOB"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("blobParam__not_null")
fun SqContext.blobParam(value: Blob): SqParameter<Blob, Blob> =
    this.param(this.blobType(), value)

/**
 * Creates parameter with JDBC type "BLOB"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("blobParam__nullable")
fun SqContext.blobParam(value: Blob?): SqParameter<Blob?, Blob> =
    this.param(this.blobType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "BLOB"
 */
fun SqContext.blobNull(): SqNull<Blob, Blob> =
    this.nullItem(this.blobType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "BLOB"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.blobNotNull(columnName: String): SqTableColumn<Blob, Blob> =
    this.column(SqGenericTypeHolder.blob, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "BLOB"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.blobNullable(columnName: String): SqTableColumn<Blob?, Blob> =
    this.column(SqGenericTypeHolder.blob.nullable(), columnName)


/**
 * @return type for JDBC "CLOB"
 */
fun SqContext.clobType(): SqType<Clob, Clob> =
    this.typeHolder().clob

/**
 * Creates parameter with JDBC type "CLOB"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("clobParam__not_null")
fun SqContext.clobParam(value: Clob): SqParameter<Clob, Clob> =
    this.param(this.clobType(), value)

/**
 * Creates parameter with JDBC type "CLOB"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("clobParam__nullable")
fun SqContext.clobParam(value: Clob?): SqParameter<Clob?, Clob> =
    this.param(this.clobType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "CLOB"
 */
fun SqContext.clobNull(): SqNull<Clob, Clob> =
    this.nullItem(this.clobType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "CLOB"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.clobNotNull(columnName: String): SqTableColumn<Clob, Clob> =
    this.column(SqGenericTypeHolder.clob, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "CLOB"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.clobNullable(columnName: String): SqTableColumn<Clob?, Clob> =
    this.column(SqGenericTypeHolder.clob.nullable(), columnName)


/**
 * @return type for JDBC "NCLOB"
 */
fun SqContext.nClobType(): SqType<NClob, Clob> =
    this.typeHolder().nClob

/**
 * Creates parameter with JDBC type "NCLOB"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("nClobParam__not_null")
fun SqContext.nClobParam(value: NClob): SqParameter<NClob, Clob> =
    this.param(this.nClobType(), value)

/**
 * Creates parameter with JDBC type "NCLOB"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("nClobParam__nullable")
fun SqContext.nClobParam(value: NClob?): SqParameter<NClob?, Clob> =
    this.param(this.nClobType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "NCLOB"
 */
fun SqContext.nClobNull(): SqNull<NClob, Clob> =
    this.nullItem(this.nClobType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "NCLOB"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.nClobNotNull(columnName: String): SqTableColumn<NClob, Clob> =
    this.column(SqGenericTypeHolder.nClob, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "NCLOB"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.nClobNullable(columnName: String): SqTableColumn<NClob?, Clob> =
    this.column(SqGenericTypeHolder.nClob.nullable(), columnName)



/**
 * @return type for [java.sql.Blob]; usually - for JDBC "BLOB"
 */
fun SqContext.javaBlobType(): SqType<Blob, Blob> =
    this.typeHolder().javaBlob

/**
 * Creates parameter with type for [java.sql.Blob] (usually - for JDBC "BLOB")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaBlobParam__not_null")
fun SqContext.javaBlobParam(value: Blob): SqParameter<Blob, Blob> =
    this.param(this.javaBlobType(), value)

/**
 * Creates parameter with type for [java.sql.Blob] (usually - for JDBC "BLOB")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaBlobParam__nullable")
fun SqContext.javaBlobParam(value: Blob?): SqParameter<Blob?, Blob> =
    this.param(this.javaBlobType().nullable(), value)

/**
 * @return SQL NULL with type for [java.sql.Blob] (usually - for JDBC "BLOB")
 */
fun SqContext.javaBlobNull(): SqNull<Blob, Blob> =
    this.nullItem(this.javaBlobType().nullable())


/**
 * @return type for [java.sql.Clob]; usually - for JDBC "CLOB"
 */
fun SqContext.javaClobType(): SqType<Clob, Clob> =
    this.typeHolder().javaClob

/**
 * Creates parameter with type for [java.sql.Clob] (usually - for JDBC "CLOB")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaClobParam__not_null")
fun SqContext.javaClobParam(value: Clob): SqParameter<Clob, Clob> =
    this.param(this.javaClobType(), value)

/**
 * Creates parameter with type for [java.sql.Clob] (usually - for JDBC "CLOB")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaClobParam__nullable")
fun SqContext.javaClobParam(value: Clob?): SqParameter<Clob?, Clob> =
    this.param(this.javaClobType().nullable(), value)

/**
 * @return SQL NULL with type for [java.sql.Clob] (usually - for JDBC "CLOB")
 */
fun SqContext.javaClobNull(): SqNull<Clob, Clob> =
    this.nullItem(this.javaClobType().nullable())


/**
 * @return type for [java.sql.NClob]; usually - for JDBC "NCLOB"
 */
fun SqContext.javaNClobType(): SqType<NClob, Clob> =
    this.typeHolder().javaNClob

/**
 * Creates parameter with type for [java.sql.NClob] (usually - for JDBC "NCLOB")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaNClobParam__not_null")
fun SqContext.javaNClobParam(value: NClob): SqParameter<NClob, Clob> =
    this.param(this.javaNClobType(), value)

/**
 * Creates parameter with type for [java.sql.NClob] (usually - for JDBC "NCLOB")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaNClobParam__nullable")
fun SqContext.javaNClobParam(value: NClob?): SqParameter<NClob?, Clob> =
    this.param(this.javaNClobType().nullable(), value)

/**
 * @return SQL NULL with type for [java.sql.NClob] (usually - for JDBC "NCLOB")
 */
fun SqContext.javaNClobNull(): SqNull<NClob, Clob> =
    this.nullItem(this.javaNClobType().nullable())
// endregion


// region Other JDBC types
/**
 * @return type for JDBC "DATALINK"
 */
fun SqContext.dataLinkType(): SqType<URL, String> =
    this.typeHolder().dataLink

/**
 * Creates parameter with JDBC type "DATALINK"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("dataLinkParam__not_null")
fun SqContext.dataLinkParam(value: URL): SqParameter<URL, String> =
    this.param(this.dataLinkType(), value)

/**
 * Creates parameter with JDBC type "DATALINK"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("dataLinkParam__nullable")
fun SqContext.dataLinkParam(value: URL?): SqParameter<URL?, String> =
    this.param(this.dataLinkType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "DATALINK"
 */
fun SqContext.dataLinkNull(): SqNull<URL, String> =
    this.nullItem(this.dataLinkType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "DATALINK"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.dataLinkNotNull(columnName: String): SqTableColumn<URL, String> =
    this.column(SqGenericTypeHolder.dataLink, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "DATALINK"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.dataLinkNullable(columnName: String): SqTableColumn<URL?, String> =
    this.column(SqGenericTypeHolder.dataLink.nullable(), columnName)


/**
 * @return type for JDBC "REF"
 */
fun SqContext.refType(): SqType<Ref, Ref> =
    this.typeHolder().ref

/**
 * Creates parameter with JDBC type "REF"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("refParam__not_null")
fun SqContext.refParam(value: Ref): SqParameter<Ref, Ref> =
    this.param(this.refType(), value)

/**
 * Creates parameter with JDBC type "REF"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("refParam__nullable")
fun SqContext.refParam(value: Ref?): SqParameter<Ref?, Ref> =
    this.param(this.refType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "REF"
 */
fun SqContext.refNull(): SqNull<Ref, Ref> =
    this.nullItem(this.refType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "REF"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.refNotNull(columnName: String): SqTableColumn<Ref, Ref> =
    this.column(SqGenericTypeHolder.ref, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "REF"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.refNullable(columnName: String): SqTableColumn<Ref?, Ref> =
    this.column(SqGenericTypeHolder.ref.nullable(), columnName)


/**
 * @return type for JDBC "ROWID"
 */
fun SqContext.rowIdType(): SqType<RowId, RowId> =
    this.typeHolder().rowId

/**
 * Creates parameter with JDBC type "ROWID"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("rowIdParam__not_null")
fun SqContext.rowIdParam(value: RowId): SqParameter<RowId, RowId> =
    this.param(this.rowIdType(), value)

/**
 * Creates parameter with JDBC type "ROWID"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("rowIdParam__nullable")
fun SqContext.rowIdParam(value: RowId?): SqParameter<RowId?, RowId> =
    this.param(this.rowIdType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "ROWID"
 */
fun SqContext.rowIdNull(): SqNull<RowId, RowId> =
    this.nullItem(this.rowIdType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "ROWID"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.rowIdNotNull(columnName: String): SqTableColumn<RowId, RowId> =
    this.column(SqGenericTypeHolder.rowId, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "ROWID"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.rowIdNullable(columnName: String): SqTableColumn<RowId?, RowId> =
    this.column(SqGenericTypeHolder.rowId.nullable(), columnName)


/**
 * @return type for JDBC "SQLXML"
 */
fun SqContext.sqlXmlType(): SqType<SQLXML, String> =
    this.typeHolder().sqlXml

/**
 * Creates parameter with JDBC type "SQLXML"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("sqlXmlParam__not_null")
fun SqContext.sqlXmlParam(value: SQLXML): SqParameter<SQLXML, String> =
    this.param(this.sqlXmlType(), value)

/**
 * Creates parameter with JDBC type "SQLXML"
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("sqlXmlParam__nullable")
fun SqContext.sqlXmlParam(value: SQLXML?): SqParameter<SQLXML?, String> =
    this.param(this.sqlXmlType().nullable(), value)

/**
 * @return SQL NULL with JDBC type "SQLXML"
 */
fun SqContext.sqlXmlNull(): SqNull<SQLXML, String> =
    this.nullItem(this.sqlXmlType().nullable())

/**
 * Creates and adds "NOT NULL" column to current table; column will have JDBC type "SQLXML"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.sqlXmlNotNull(columnName: String): SqTableColumn<SQLXML, String> =
    this.column(SqGenericTypeHolder.sqlXml, columnName)

/**
 * Creates and adds "NULLABLE" column to current table; column will have JDBC type "SQLXML"
 *
 * @param columnName column name
 *
 * @return created column
 */
fun SqTable.sqlXmlNullable(columnName: String): SqTableColumn<SQLXML?, String> =
    this.column(SqGenericTypeHolder.sqlXml.nullable(), columnName)



/**
 * @return type for [java.sql.Ref]; usually - for JDBC "REF"
 */
fun SqContext.javaRefType(): SqType<Ref, Ref> =
    this.typeHolder().javaRef

/**
 * Creates parameter with type for [java.sql.Ref] (usually - for JDBC "REF")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaRefParam__not_null")
fun SqContext.javaRefParam(value: Ref): SqParameter<Ref, Ref> =
    this.param(this.javaRefType(), value)

/**
 * Creates parameter with type for [java.sql.Ref] (usually - for JDBC "REF")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaRefParam__nullable")
fun SqContext.javaRefParam(value: Ref?): SqParameter<Ref?, Ref> =
    this.param(this.javaRefType().nullable(), value)

/**
 * @return SQL NULL with type for [java.sql.Ref] (usually - for JDBC "REF")
 */
fun SqContext.javaRefNull(): SqNull<Ref, Ref> =
    this.nullItem(this.javaRefType().nullable())


/**
 * @return type for [java.sql.RowId]; usually - for JDBC "ROWID"
 */
fun SqContext.javaRowIdType(): SqType<RowId, RowId> =
    this.typeHolder().javaRowId

/**
 * Creates parameter with type for [java.sql.RowId] (usually - for JDBC "ROWID")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaRowIdParam__not_null")
fun SqContext.javaRowIdParam(value: RowId): SqParameter<RowId, RowId> =
    this.param(this.javaRowIdType(), value)

/**
 * Creates parameter with type for [java.sql.RowId] (usually - for JDBC "ROWID")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaRowIdParam__nullable")
fun SqContext.javaRowIdParam(value: RowId?): SqParameter<RowId?, RowId> =
    this.param(this.javaRowIdType().nullable(), value)

/**
 * @return SQL NULL with type for [java.sql.RowId] (usually - for JDBC "ROWID")
 */
fun SqContext.javaRowIdNull(): SqNull<RowId, RowId> =
    this.nullItem(this.javaRowIdType().nullable())


/**
 * @return type for [java.sql.SQLXML]; usually - for JDBC "SQLXML"
 */
fun SqContext.javaSqlXmlType(): SqType<SQLXML, String> =
    this.typeHolder().javaSqlXml

/**
 * Creates parameter with type for [java.sql.SQLXML] (usually - for JDBC "SQLXML")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaSqlXmlParam__not_null")
fun SqContext.javaSqlXmlParam(value: SQLXML): SqParameter<SQLXML, String> =
    this.param(this.javaSqlXmlType(), value)

/**
 * Creates parameter with type for [java.sql.SQLXML] (usually - for JDBC "SQLXML")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaSqlXmlParam__nullable")
fun SqContext.javaSqlXmlParam(value: SQLXML?): SqParameter<SQLXML?, String> =
    this.param(this.javaSqlXmlType().nullable(), value)

/**
 * @return SQL NULL with type for [java.sql.SQLXML] (usually - for JDBC "SQLXML")
 */
fun SqContext.javaSqlXmlNull(): SqNull<SQLXML, String> =
    this.nullItem(this.javaSqlXmlType().nullable())


/**
 * @return type for [java.net.URL]; usually - for JDBC "DATALINK"
 */
fun SqContext.javaUrlType(): SqType<URL, String> =
    this.typeHolder().javaUrl

/**
 * Creates parameter with type for [java.net.URL] (usually - for JDBC "DATALINK")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaUrlParam__not_null")
fun SqContext.javaUrlParam(value: URL): SqParameter<URL, String> =
    this.param(this.javaUrlType(), value)

/**
 * Creates parameter with type for [java.net.URL] (usually - for JDBC "DATALINK")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("javaUrlParam__nullable")
fun SqContext.javaUrlParam(value: URL?): SqParameter<URL?, String> =
    this.param(this.javaUrlType().nullable(), value)

/**
 * @return SQL NULL with type for [java.net.URL] (usually - for JDBC "DATALINK")
 */
fun SqContext.javaUrlNull(): SqNull<URL, String> =
    this.nullItem(this.javaUrlType().nullable())
// endregion


// region API item types
/**
 * @return type used for [SqBooleanGroup]; usually - JDBC "BOOLEAN"
 */
fun SqContext.sqBooleanGroupType(): SqType<Boolean, Boolean> = this.typeHolder().booleanGroup

/**
 * @return type used for [SqNot]; usually - JDBC "BOOLEAN"
 */
fun SqContext.sqNotType(): SqType<Boolean, Boolean> = this.typeHolder().not

/**
 * @return type used for [SqNullTest]; usually - JDBC "BOOLEAN"
 */
fun SqContext.sqNullTestType(): SqType<Boolean, Boolean> = this.typeHolder().nullTest

/**
 * @return type used for [SqComparison]; usually - JDBC "BOOLEAN"
 */
fun SqContext.sqComparisonType(): SqType<Boolean, Boolean> = this.typeHolder().comparison

/**
 * @return type used for [SqMathOperation]; usually - [SqTypeHolder.javaNumber]
 */
fun SqContext.sqMathOperationType(): SqType<Number, Number> = this.typeHolder().mathOperation
// endregion


// region Value-based parameters for Java types
/**
 * Creates parameter with type for [Boolean] (usually - for JDBC "BOOLEAN")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: Boolean): SqParameter<Boolean, Boolean> =
    this.javaBooleanParam(value)

/**
 * Creates parameter with type for [Boolean] (usually - for JDBC "BOOLEAN")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: Boolean?): SqParameter<Boolean?, Boolean> =
    this.javaBooleanParam(value)


/**
 * Creates parameter with type for [ByteArray] (usually - for one of JDBC "BINARY", "LONGVARBINARY" or "VARBINARY")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: ByteArray): SqParameter<ByteArray, ByteArray> =
    this.javaByteArrayParam(value)

/**
 * Creates parameter with type for [ByteArray] (usually - for one of JDBC "BINARY", "LONGVARBINARY" or "VARBINARY")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: ByteArray?): SqParameter<ByteArray?, ByteArray> =
    this.javaByteArrayParam(value)


/**
 * Creates parameter with type for [java.sql.Date] (usually - for JDBC "DATE")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: Date): SqParameter<Date, Timestamp> =
    this.javaDateParam(value)

/**
 * Creates parameter with type for [java.sql.Date] (usually - for JDBC "DATE")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: Date?): SqParameter<Date?, Timestamp> =
    this.javaDateParam(value)


/**
 * Creates parameter with type for [java.time.LocalDate] (usually - for JDBC "DATE")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: LocalDate): SqParameter<LocalDate, Timestamp> =
    this.javaLocalDateParam(value)

/**
 * Creates parameter with type for [java.time.LocalDate] (usually - for JDBC "DATE")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: LocalDate?): SqParameter<LocalDate?, Timestamp> =
    this.javaLocalDateParam(value)


/**
 * Creates parameter with type for [java.time.LocalDateTime] (usually - for JDBC "TIMESTAMP")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: LocalDateTime): SqParameter<LocalDateTime, Timestamp> =
    this.javaLocalDateTimeParam(value)

/**
 * Creates parameter with type for [java.time.LocalDateTime] (usually - for JDBC "TIMESTAMP")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: LocalDateTime?): SqParameter<LocalDateTime?, Timestamp> =
    this.javaLocalDateTimeParam(value)


/**
 * Creates parameter with type for [java.time.LocalTime] (usually - for JDBC "TIME")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: LocalTime): SqParameter<LocalTime, Time> =
    this.javaLocalTimeParam(value)

/**
 * Creates parameter with type for [java.time.LocalTime] (usually - for JDBC "TIME")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: LocalTime?): SqParameter<LocalTime?, Time> =
    this.javaLocalTimeParam(value)


/**
 * Creates parameter with type for [java.time.OffsetDateTime] (usually - for JDBC "TIMESTAMP_WITH_TIMEZONE")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: OffsetDateTime): SqParameter<OffsetDateTime, Timestamp> =
    this.javaOffsetDateTimeParam(value)

/**
 * Creates parameter with type for [java.time.OffsetDateTime] (usually - for JDBC "TIMESTAMP_WITH_TIMEZONE")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: OffsetDateTime?): SqParameter<OffsetDateTime?, Timestamp> =
    this.javaOffsetDateTimeParam(value)


/**
 * Creates parameter with type for [java.time.OffsetTime] (usually - for JDBC "TIME_WITH_TIMEZONE")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: OffsetTime): SqParameter<OffsetTime, Time> =
    this.javaOffsetTimeParam(value)

/**
 * Creates parameter with type for [java.time.OffsetTime] (usually - for JDBC "TIME_WITH_TIMEZONE")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: OffsetTime?): SqParameter<OffsetTime?, Time> =
    this.javaOffsetTimeParam(value)


/**
 * Creates parameter with type for [java.sql.Time] (usually - for JDBC "TIME")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: Time): SqParameter<Time, Time> =
    this.javaTimeParam(value)

/**
 * Creates parameter with type for [java.sql.Time] (usually - for JDBC "TIME")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: Time?): SqParameter<Time?, Time> =
    this.javaTimeParam(value)


/**
 * Creates parameter with type for [java.sql.Timestamp] (usually - for JDBC "TIMESTAMP")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: Timestamp): SqParameter<Timestamp, Timestamp> =
    this.javaTimestampParam(value)

/**
 * Creates parameter with type for [java.sql.Timestamp] (usually - for JDBC "TIMESTAMP")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: Timestamp?): SqParameter<Timestamp?, Timestamp> =
    this.javaTimestampParam(value)


/**
 * Creates parameter with type for [java.math.BigDecimal] (usually - for JDBC "NUMERIC")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: BigDecimal): SqParameter<BigDecimal, Number> =
    this.javaBigDecimalParam(value)

/**
 * Creates parameter with type for [java.math.BigDecimal] (usually - for JDBC "NUMERIC")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: BigDecimal?): SqParameter<BigDecimal?, Number> =
    this.javaBigDecimalParam(value)


/**
 * Creates parameter with type for [java.math.BigInteger] (usually - for JDBC "BIGINT")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: BigInteger): SqParameter<BigInteger, Number> =
    this.javaBigIntegerParam(value)

/**
 * Creates parameter with type for [java.math.BigInteger] (usually - for JDBC "BIGINT")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: BigInteger?): SqParameter<BigInteger?, Number> =
    this.javaBigIntegerParam(value)


/**
 * Creates parameter with type for [Byte] (usually - for JDBC "TINYINT")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: Byte): SqParameter<Byte, Number> =
    this.javaByteParam(value)

/**
 * Creates parameter with type for [Byte] (usually - for JDBC "TINYINT")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: Byte?): SqParameter<Byte?, Number> =
    this.javaByteParam(value)


/**
 * Creates parameter with type for [Double] (usually - for JDBC "DOUBLE")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: Double): SqParameter<Double, Number> =
    this.javaDoubleParam(value)

/**
 * Creates parameter with type for [Double] (usually - for JDBC "DOUBLE")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: Double?): SqParameter<Double?, Number> =
    this.javaDoubleParam(value)


/**
 * Creates parameter with type for [Float] (usually - for JDBC "REAL")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: Float): SqParameter<Float, Number> =
    this.javaFloatParam(value)

/**
 * Creates parameter with type for [Float] (usually - for JDBC "REAL")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: Float?): SqParameter<Float?, Number> =
    this.javaFloatParam(value)


/**
 * Creates parameter with type for [Int] (usually - for JDBC "INTEGER")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: Int): SqParameter<Int, Number> =
    this.javaIntParam(value)

/**
 * Creates parameter with type for [Int] (usually - for JDBC "INTEGER")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: Int?): SqParameter<Int?, Number> =
    this.javaIntParam(value)


/**
 * Creates parameter with type for [Long] (usually - for JDBC "BIGINT")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: Long): SqParameter<Long, Number> =
    this.javaLongParam(value)

/**
 * Creates parameter with type for [Long] (usually - for JDBC "BIGINT")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: Long?): SqParameter<Long?, Number> =
    this.javaLongParam(value)


/**
 * Creates parameter with type for [Short] (usually - for JDBC "SMALLINT")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: Short): SqParameter<Short, Number> =
    this.javaShortParam(value)

/**
 * Creates parameter with type for [Short] (usually - for JDBC "SMALLINT")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: Short?): SqParameter<Short?, Number> =
    this.javaShortParam(value)


/**
 * Creates parameter with type for [String]
 * (usually - for one of JDBC types "CHAR", "LONGVARCHAR", "VARCHAR", "NCHAR", "NVARCHAR", "LONGNVARCHAR")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: String): SqParameter<String, String> =
    this.javaStringParam(value)

/**
 * Creates parameter with type for [String]
 * (usually - for one of JDBC types "CHAR", "LONGVARCHAR", "VARCHAR", "NCHAR", "NVARCHAR", "LONGNVARCHAR")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: String?): SqParameter<String?, String> =
    this.javaStringParam(value)


/**
 * Creates parameter with type for [java.sql.Blob] (usually - for JDBC "BLOB")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: Blob): SqParameter<Blob, Blob> =
    this.javaBlobParam(value)

/**
 * Creates parameter with type for [java.sql.Blob] (usually - for JDBC "BLOB")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: Blob?): SqParameter<Blob?, Blob> =
    this.javaBlobParam(value)


/**
 * Creates parameter with type for [java.sql.Clob] (usually - for JDBC "CLOB")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: Clob): SqParameter<Clob, Clob> =
    this.javaClobParam(value)

/**
 * Creates parameter with type for [java.sql.Clob] (usually - for JDBC "CLOB")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: Clob?): SqParameter<Clob?, Clob> =
    this.javaClobParam(value)


/**
 * Creates parameter with type for [java.sql.NClob] (usually - for JDBC "NCLOB")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: NClob): SqParameter<NClob, Clob> =
    this.javaNClobParam(value)

/**
 * Creates parameter with type for [java.sql.NClob] (usually - for JDBC "NCLOB")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: NClob?): SqParameter<NClob?, Clob> =
    this.javaNClobParam(value)


/**
 * Creates parameter with type for [java.sql.Ref] (usually - for JDBC "REF")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: Ref): SqParameter<Ref, Ref> =
    this.javaRefParam(value)

/**
 * Creates parameter with type for [java.sql.Ref] (usually - for JDBC "REF")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: Ref?): SqParameter<Ref?, Ref> =
    this.javaRefParam(value)


/**
 * Creates parameter with type for [java.sql.RowId] (usually - for JDBC "ROWID")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: RowId): SqParameter<RowId, RowId> =
    this.javaRowIdParam(value)

/**
 * Creates parameter with type for [java.sql.RowId] (usually - for JDBC "ROWID")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: RowId?): SqParameter<RowId?, RowId> =
    this.javaRowIdParam(value)


/**
 * Creates parameter with type for [java.sql.SQLXML] (usually - for JDBC "SQLXML")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: SQLXML): SqParameter<SQLXML, String> =
    this.javaSqlXmlParam(value)

/**
 * Creates parameter with type for [java.sql.SQLXML] (usually - for JDBC "SQLXML")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: SQLXML?): SqParameter<SQLXML?, String> =
    this.javaSqlXmlParam(value)


/**
 * Creates parameter with type for [java.net.URL] (usually - for JDBC "DATALINK")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__not_null")
fun SqContext.param(value: URL): SqParameter<URL, String> =
    this.javaUrlParam(value)

/**
 * Creates parameter with type for [java.net.URL] (usually - for JDBC "DATALINK")
 *
 * @param value parameter value
 *
 * @return created parameter
 */
@JvmName("param__nullable")
fun SqContext.param(value: URL?): SqParameter<URL?, String> =
    this.javaUrlParam(value)
// endregion
