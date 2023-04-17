package me.ore.sq.pg

import me.ore.sq.SqValueReader
import me.ore.sq.SqValueWriterBase
import me.ore.sq.util.SqUtil
import org.postgresql.util.PGobject
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.*
import java.time.*


object SqPgTypes {
    const val ARRAY: Int = Types.ARRAY

    const val BIG_INT: Int = Types.BIGINT
    const val BIG_INT__TYPE_NAME: String = "int8"
    const val BIG_INT_ARRAY__TYPE_NAME: String = "_int8"

    const val BIT: Int = Types.BIT
    const val BIT__TYPE_NAME: String = "bit"
    const val BIT_ARRAY__TYPE_NAME: String = "_bit"

    const val BOOLEAN: Int = Types.BOOLEAN
    const val BOOLEAN__TYPE_NAME: String = "bool"
    const val BOOLEAN_ARRAY__TYPE_NAME: String = "_bool"

    const val BYTEA: Int = Types.BINARY
    const val BYTEA__TYPE_NAME: String = "bytea"
    const val BYTEA_ARRAY__TYPE_NAME: String = "_bytea"

    const val CHAR: Int = Types.CHAR
    const val CHAR__TYPE_NAME: String = "char"
    const val CHAR_ARRAY__TYPE_NAME: String = "_char"

    const val CHARACTER: Int = Types.CHAR
    const val CHARACTER__TYPE_NAME: String = "bpchar"
    const val CHARACTER_ARRAY__TYPE_NAME: String = "_bpchar"

    const val DATE: Int = Types.DATE
    const val DATE__TYPE_NAME: String = "date"
    const val DATE_ARRAY__TYPE_NAME: String = "_date"

    const val DOUBLE: Int = Types.DOUBLE
    const val DOUBLE__TYPE_NAME: String = "float8"
    const val DOUBLE_ARRAY__TYPE_NAME: String = "_float8"

    const val INTEGER: Int = Types.INTEGER
    const val INTEGER__TYPE_NAME: String = "int4"
    const val INTEGER_ARRAY__TYPE_NAME: String = "_int4"

    const val JSON: Int = Types.OTHER
    const val JSON__TYPE_NAME: String = "json"
    const val JSON_ARRAY__TYPE_NAME: String = "_json"

    const val JSON_B: Int = Types.OTHER
    const val JSON_B__TYPE_NAME: String = "jsonb"
    const val JSON_B_ARRAY__TYPE_NAME: String = "_jsonb"

    const val NUMERIC: Int = Types.NUMERIC
    const val NUMERIC__TYPE_NAME: String = "numeric"
    const val NUMERIC_ARRAY__TYPE_NAME: String = "_numeric"

    const val REAL: Int = Types.REAL
    const val REAL__TYPE_NAME: String = "float4"
    const val REAL_ARRAY__TYPE_NAME: String = "_float4"

    const val SMALL_INT: Int = Types.SMALLINT
    const val SMALL_INT__TYPE_NAME: String = "int2"
    const val SMALL_INT_ARRAY__TYPE_NAME: String = "_int2"

    const val TEXT: Int = Types.VARCHAR
    const val TEXT__TYPE_NAME: String = "text"
    const val TEXT_ARRAY__TYPE_NAME: String = "_text"

    const val TIME: Int = Types.TIME
    const val TIME__TYPE_NAME: String = "time"
    const val TIME_ARRAY__TYPE_NAME: String = "_time"

    const val TIME_TZ: Int = Types.TIME
    const val TIME_TZ__TYPE_NAME: String = "timetz"
    const val TIME_TZ_ARRAY__TYPE_NAME: String = "_timetz"

    const val TIMESTAMP: Int = Types.TIMESTAMP
    const val TIMESTAMP__TYPE_NAME: String = "timestamp"
    const val TIMESTAMP_ARRAY__TYPE_NAME: String = "_timestamp"

    const val TIMESTAMP_TZ: Int = Types.TIMESTAMP
    const val TIMESTAMP_TZ__TYPE_NAME: String = "timestamptz"
    const val TIMESTAMP_TZ_ARRAY__TYPE_NAME: String = "_timestamptz"

    const val VAR_BIT: Int = Types.OTHER
    const val VAR_BIT__TYPE_NAME: String = "varbit"
    const val VAR_BIT_ARRAY__TYPE_NAME: String = "_varbit"

    const val VAR_CHAR: Int = Types.VARCHAR
    const val VAR_CHAR__TYPE_NAME: String = "varchar"
    const val VAR_CHAR_ARRAY__TYPE_NAME: String = "_varchar"

    const val XML: Int = Types.SQLXML
    const val XML__TYPE_NAME: String = "xml"
    const val XML_ARRAY__TYPE_NAME: String = "xml"
}


// region Base types
abstract class SqPgArrayReaderBase<E: Any>: SqValueReader<List<E?>> {
    companion object {
        const val ARRAY_SOURCE_ITEM_COLUMN: Int = 2
    }


    protected abstract fun readItem(
        itemSource: ResultSet,
        @Suppress("SameParameterValue")
        columnIndex: Int,
    ): E?

    override fun readNullable(source: ResultSet, columnIndex: Int): List<E?>? {
        return source.getArray(columnIndex)?.let { array ->
            buildList {
                try {
                    array.resultSet.use { itemSource ->
                        while (itemSource.next()) {
                            val item = this@SqPgArrayReaderBase.readItem(itemSource, ARRAY_SOURCE_ITEM_COLUMN)
                            this.add(item)
                        }
                    }
                } finally {
                    array.free()
                }
            }
        }
    }
}

abstract class SqPgArrayWriterBase<E: Any>: SqValueWriterBase<List<E?>>() {
    override val sqlType: Int
        get() = SqPgTypes.ARRAY

    protected abstract val arrayItemClass: Class<out Any>
    protected abstract val arrayItemTypeName: String

    protected abstract fun convertItem(item: E?): Any?

    protected open fun convertValue(value: List<E?>): Array<Any?> {
        val result: Array<Any?> = run {
            @Suppress("UNCHECKED_CAST")
            java.lang.reflect.Array.newInstance(this.arrayItemClass, value.size) as Array<Any?>
        }
        value.forEachIndexed { index, valueItem ->
            val jdbcArrayItem = this.convertItem(valueItem)
            result[index] = jdbcArrayItem
        }
        return result
    }

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: List<E?>) {
        val jdbcArrayItems = this.convertValue(value)
        val jdbcArray = target.connection.createArrayOf(this.arrayItemTypeName, jdbcArrayItems)
        target.setArray(parameterIndex, jdbcArray)
    }
}
// endregion


// region Boolean types
open class SqPgBooleanReader: SqValueReader<Boolean> {
    override fun readNullable(source: ResultSet, columnIndex: Int): Boolean? {
        return source.getBoolean(columnIndex).takeUnless { source.wasNull() }
    }
}

open class SqPgBooleanWriter: SqValueWriterBase<Boolean>() {
    override val sqlType: Int
        get() = SqPgTypes.BOOLEAN
    override val typeName: String?
        get() = SqPgTypes.BOOLEAN__TYPE_NAME

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Boolean) {
        target.setBoolean(parameterIndex, value)
    }

    override fun notNullValueToComment(value: Boolean): String = value.toString()
}

open class SqPgBooleanArrayReader: SqPgArrayReaderBase<Boolean>() {
    override fun readItem(itemSource: ResultSet, columnIndex: Int): Boolean? {
        return itemSource.getBoolean(columnIndex).takeUnless { itemSource.wasNull() }
    }
}

open class SqPgBooleanArrayWriter: SqPgArrayWriterBase<Boolean>() {
    override val typeName: String?
        get() = SqPgTypes.BOOLEAN_ARRAY__TYPE_NAME

    override fun notNullValueToComment(value: List<Boolean?>): String {
        return SqUtil.prepareCollectionValueForComment(value)
    }

    override val arrayItemClass: Class<out Any>
        get() = Boolean::class.java
    override val arrayItemTypeName: String
        get() = SqPgTypes.BOOLEAN__TYPE_NAME

    override fun convertItem(item: Boolean?): Any? = item
}

open class SqPgSingleBitReader: SqValueReader<Boolean> {
    override fun readNullable(source: ResultSet, columnIndex: Int): Boolean? {
        return source.getBoolean(columnIndex).takeUnless { source.wasNull() }
    }
}

open class SqPgSingleBitWriter: SqValueWriterBase<Boolean>() {
    override val sqlType: Int
        get() = SqPgTypes.BIT
    override val typeName: String?
        get() = SqPgTypes.BIT__TYPE_NAME

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Boolean) {
        target.setObject(
            parameterIndex,
            PGobject().apply {
                this.type = this@SqPgSingleBitWriter.typeName
                this.value = if (value) "1" else "0"
            }
        )
    }

    override fun notNullValueToComment(value: Boolean): String = value.toString()
}

open class SqPgSingleBitArrayReader: SqPgArrayReaderBase<Boolean>() {
    override fun readItem(itemSource: ResultSet, columnIndex: Int): Boolean? {
        return itemSource.getBoolean(columnIndex).takeUnless { itemSource.wasNull() }
    }
}

open class SqPgSingleBitArrayWriter: SqPgArrayWriterBase<Boolean>() {
    override val typeName: String?
        get() = SqPgTypes.BIT_ARRAY__TYPE_NAME

    override fun notNullValueToComment(value: List<Boolean?>): String {
        return SqUtil.prepareCollectionValueForComment(value.map {
            when (it) {
                true -> "1"
                false -> "0"
                null -> "NULL"
            }
        })
    }

    override val arrayItemClass: Class<out Any>
        get() = Boolean::class.java
    override val arrayItemTypeName: String
        get() = SqPgTypes.BIT__TYPE_NAME

    override fun convertItem(item: Boolean?): Any? = item
}
// endregion


// region Byte array types
open class SqPgByteaReader: SqValueReader<ByteArray> {
    override fun readNullable(source: ResultSet, columnIndex: Int): ByteArray? {
        return source.getBytes(columnIndex)
    }
}

open class SqPgByteaWriter: SqValueWriterBase<ByteArray>() {
    override val sqlType: Int
        get() = SqPgTypes.BYTEA
    override val typeName: String?
        get() = SqPgTypes.BYTEA__TYPE_NAME

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: ByteArray) {
        target.setBytes(parameterIndex, value)
    }

    override fun notNullValueToComment(value: ByteArray): String = "Byte array (${value.size} byte(s))"
}

open class SqPgByteaArrayReader: SqPgArrayReaderBase<ByteArray>() {
    override fun readItem(itemSource: ResultSet, columnIndex: Int): ByteArray? {
        return itemSource.getBytes(columnIndex)
    }
}

open class SqPgByteaArrayWriter: SqPgArrayWriterBase<ByteArray>() {
    override val typeName: String?
        get() = SqPgTypes.BYTEA_ARRAY__TYPE_NAME

    override fun notNullValueToComment(value: List<ByteArray?>): String {
        return SqUtil.prepareCollectionValueForComment(value.map {
            if (it == null) {
                "NULL"
            } else {
                "${it.size} byte(s)"
            }
        })
    }

    override val arrayItemClass: Class<out Any>
        get() = ByteArray::class.java
    override val arrayItemTypeName: String
        get() = SqPgTypes.BYTEA__TYPE_NAME

    override fun convertItem(item: ByteArray?): Any? = item
}
// endregion


// region Date/time types
open class SqPgDateReader: SqValueReader<LocalDate> {
    override fun readNullable(source: ResultSet, columnIndex: Int): LocalDate? {
        return source.getObject(1, LocalDate::class.java)
    }
}

open class SqPgDateWriter: SqValueWriterBase<LocalDate>() {
    override val sqlType: Int
        get() = SqPgTypes.DATE
    override val typeName: String?
        get() = SqPgTypes.DATE__TYPE_NAME

    override fun notNullValueToComment(value: LocalDate): String = value.toString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: LocalDate) {
        target.setObject(parameterIndex, value)
    }
}

open class SqPgSqlDateReader: SqValueReader<Date> {
    override fun readNullable(source: ResultSet, columnIndex: Int): Date? {
        return source.getDate(columnIndex)
    }
}

open class SqPgSqlDateWriter: SqValueWriterBase<Date>() {
    override val sqlType: Int
        get() = SqPgTypes.DATE
    override val typeName: String?
        get() = SqPgTypes.DATE__TYPE_NAME

    override fun notNullValueToComment(value: Date): String = value.toString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Date) {
        target.setDate(parameterIndex, value)
    }
}

open class SqPgDateArrayReader: SqPgArrayReaderBase<LocalDate>() {
    override fun readItem(itemSource: ResultSet, columnIndex: Int): LocalDate? {
        return itemSource.getObject(columnIndex, LocalDate::class.java)
    }
}

open class SqPgDateArrayWriter: SqPgArrayWriterBase<LocalDate>() {
    override val typeName: String?
        get() = SqPgTypes.DATE_ARRAY__TYPE_NAME

    override fun notNullValueToComment(value: List<LocalDate?>): String {
        return SqUtil.prepareCollectionValueForComment(value)
    }

    override val arrayItemClass: Class<out Any>
        get() = LocalDate::class.java
    override val arrayItemTypeName: String
        get() = SqPgTypes.DATE__TYPE_NAME

    override fun convertItem(item: LocalDate?): Any? = item
}


open class SqPgTimeReader: SqValueReader<LocalTime> {
    override fun readNullable(source: ResultSet, columnIndex: Int): LocalTime? {
        return source.getObject(columnIndex, LocalTime::class.java)
    }
}

open class SqPgTimeWriter: SqValueWriterBase<LocalTime>() {
    override val sqlType: Int
        get() = SqPgTypes.TIME
    override val typeName: String?
        get() = SqPgTypes.TIME__TYPE_NAME

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: LocalTime) {
        target.setObject(parameterIndex, value)
    }

    override fun notNullValueToComment(value: LocalTime): String = value.toString()
}

open class SqPgSqlTimeReader: SqValueReader<Time> {
    override fun readNullable(source: ResultSet, columnIndex: Int): Time? {
        return source.getTime(columnIndex)
    }
}

open class SqPgSqlTimeWriter: SqValueWriterBase<Time>() {
    override val sqlType: Int
        get() = SqPgTypes.TIME
    override val typeName: String?
        get() = SqPgTypes.TIME__TYPE_NAME

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Time) {
        target.setTime(parameterIndex, value)
    }

    override fun notNullValueToComment(value: Time): String = value.toString()
}

open class SqPgTimeArrayReader: SqPgArrayReaderBase<LocalTime>() {
    override fun readItem(itemSource: ResultSet, columnIndex: Int): LocalTime? {
        return itemSource.getObject(columnIndex, LocalTime::class.java)
    }
}

open class SqPgTimeArrayWriter: SqPgArrayWriterBase<LocalTime>() {
    override val typeName: String?
        get() = SqPgTypes.TIME_ARRAY__TYPE_NAME
    override val arrayItemTypeName: String
        get() = SqPgTypes.TIME__TYPE_NAME

    override fun notNullValueToComment(value: List<LocalTime?>): String {
        return SqUtil.prepareCollectionValueForComment(value)
    }

    override val arrayItemClass: Class<out Any>
        get() = LocalTime::class.java

    override fun convertItem(item: LocalTime?): Any? = item
}


open class SqPgTimeTZReader: SqValueReader<OffsetTime> {
    override fun readNullable(source: ResultSet, columnIndex: Int): OffsetTime? {
        return source.getObject(columnIndex, OffsetTime::class.java)
    }
}

open class SqPgTimeTZWriter: SqValueWriterBase<OffsetTime>() {
    override val sqlType: Int
        get() = SqPgTypes.TIME_TZ
    override val typeName: String?
        get() = SqPgTypes.TIME_TZ__TYPE_NAME

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: OffsetTime) {
        target.setObject(parameterIndex, value)
    }

    override fun notNullValueToComment(value: OffsetTime): String = value.toString()
}

open class SqPgTimeTZArrayReader: SqPgArrayReaderBase<OffsetTime>() {
    override fun readItem(itemSource: ResultSet, columnIndex: Int): OffsetTime? {
        return itemSource.getObject(columnIndex, OffsetTime::class.java)
    }
}

open class SqPgTimeTZArrayWriter: SqPgArrayWriterBase<OffsetTime>() {
    override val typeName: String?
        get() = SqPgTypes.TIME_TZ_ARRAY__TYPE_NAME
    override val arrayItemTypeName: String
        get() = SqPgTypes.TIME_TZ__TYPE_NAME

    override fun notNullValueToComment(value: List<OffsetTime?>): String {
        return SqUtil.prepareCollectionValueForComment(value)
    }

    override val arrayItemClass: Class<out Any>
        get() = OffsetTime::class.java

    override fun convertItem(item: OffsetTime?): Any? = item
}


open class SqPgTimestampReader: SqValueReader<LocalDateTime> {
    override fun readNullable(source: ResultSet, columnIndex: Int): LocalDateTime? {
        return source.getObject(columnIndex, LocalDateTime::class.java)
    }
}

open class SqPgTimestampWriter: SqValueWriterBase<LocalDateTime>() {
    override val sqlType: Int
        get() = SqPgTypes.TIMESTAMP
    override val typeName: String?
        get() = SqPgTypes.TIMESTAMP__TYPE_NAME

    override fun notNullValueToComment(value: LocalDateTime): String = value.toString()

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: LocalDateTime) {
        target.setObject(parameterIndex, value)
    }
}

open class SqPgSqlTimestampReader: SqValueReader<Timestamp> {
    override fun readNullable(source: ResultSet, columnIndex: Int): Timestamp? {
        return source.getTimestamp(columnIndex)
    }
}

open class SqPgSqlTimestampWriter: SqValueWriterBase<Timestamp>() {
    override val sqlType: Int
        get() = SqPgTypes.TIMESTAMP
    override val typeName: String?
        get() = SqPgTypes.TIMESTAMP__TYPE_NAME

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Timestamp) {
        target.setTimestamp(parameterIndex, value)
    }

    override fun notNullValueToComment(value: Timestamp): String = value.toString()
}

open class SqPgTimestampArrayReader: SqPgArrayReaderBase<LocalDateTime>() {
    override fun readItem(itemSource: ResultSet, columnIndex: Int): LocalDateTime? {
        return itemSource.getObject(columnIndex, LocalDateTime::class.java)
    }
}

open class SqPgTimestampArrayWriter: SqPgArrayWriterBase<LocalDateTime>() {
    override val typeName: String?
        get() = SqPgTypes.TIMESTAMP_ARRAY__TYPE_NAME

    override fun notNullValueToComment(value: List<LocalDateTime?>): String {
        return SqUtil.prepareCollectionValueForComment(value)
    }

    override val arrayItemClass: Class<out Any>
        get() = LocalDateTime::class.java
    override val arrayItemTypeName: String
        get() = SqPgTypes.TIMESTAMP__TYPE_NAME

    override fun convertItem(item: LocalDateTime?): Any? = item
}


open class SqPgTimestampTZReader: SqValueReader<OffsetDateTime> {
    override fun readNullable(source: ResultSet, columnIndex: Int): OffsetDateTime? {
        return source.getObject(columnIndex, OffsetDateTime::class.java)
    }
}

open class SqPgTimestampTZWriter: SqValueWriterBase<OffsetDateTime>() {
    override val sqlType: Int
        get() = SqPgTypes.TIMESTAMP_TZ
    override val typeName: String?
        get() = SqPgTypes.TIMESTAMP_TZ__TYPE_NAME

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: OffsetDateTime) {
        target.setObject(parameterIndex, value)
    }

    override fun notNullValueToComment(value: OffsetDateTime): String = value.toString()
}

open class SqPgTimestampTZArrayReader: SqPgArrayReaderBase<OffsetDateTime>() {
    override fun readItem(itemSource: ResultSet, columnIndex: Int): OffsetDateTime? {
        return itemSource.getObject(columnIndex, OffsetDateTime::class.java)
    }
}

open class SqPgTimestampTZArrayWriter: SqPgArrayWriterBase<OffsetDateTime>() {
    override val typeName: String?
        get() = SqPgTypes.TIMESTAMP_TZ_ARRAY__TYPE_NAME
    override val arrayItemTypeName: String
        get() = SqPgTypes.TIMESTAMP_TZ__TYPE_NAME

    override fun notNullValueToComment(value: List<OffsetDateTime?>): String {
        return SqUtil.prepareCollectionValueForComment(value)
    }

    override val arrayItemClass: Class<out Any>
        get() = OffsetDateTime::class.java

    override fun convertItem(item: OffsetDateTime?): Any? = item
}
// endregion


// region Number types
open class SqPgBigIntArrayReader: SqPgArrayReaderBase<Long>() {
    override fun readItem(itemSource: ResultSet, columnIndex: Int): Long? {
        return itemSource.getLong(columnIndex).takeUnless { itemSource.wasNull() }
    }
}

open class SqPgBigIntArrayWriter: SqPgArrayWriterBase<Long>() {
    override val typeName: String?
        get() = SqPgTypes.BIG_INT_ARRAY__TYPE_NAME

    override fun notNullValueToComment(value: List<Long?>): String = SqUtil.prepareCollectionValueForComment(value)

    override val arrayItemClass: Class<out Any>
        get() = Long::class.java
    override val arrayItemTypeName: String
        get() = SqPgTypes.BIG_INT__TYPE_NAME

    override fun convertItem(item: Long?): Any? = item
}

open class SqPgBigIntReader: SqValueReader<Long> {
    override fun readNullable(source: ResultSet, columnIndex: Int): Long? {
        return source.getLong(columnIndex).takeUnless { source.wasNull() }
    }
}

open class SqPgBigIntWriter: SqValueWriterBase<Long>() {
    override val sqlType: Int
        get() = SqPgTypes.BIG_INT
    override val typeName: String?
        get() = SqPgTypes.BIG_INT__TYPE_NAME

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Long) {
        target.setLong(parameterIndex, value)
    }

    override fun notNullValueToComment(value: Long): String = value.toString()
}

open class SqPgDoubleArrayReader: SqPgArrayReaderBase<Double>() {
    override fun readItem(itemSource: ResultSet, columnIndex: Int): Double? {
        return itemSource.getDouble(columnIndex).takeUnless { itemSource.wasNull() }
    }
}

open class SqPgDoubleArrayWriter: SqPgArrayWriterBase<Double>() {
    override val typeName: String?
        get() = SqPgTypes.DOUBLE_ARRAY__TYPE_NAME
    override val arrayItemTypeName: String
        get() = SqPgTypes.DOUBLE__TYPE_NAME

    override fun notNullValueToComment(value: List<Double?>): String {
        return SqUtil.prepareCollectionValueForComment(value)
    }

    override val arrayItemClass: Class<out Any>
        get() = Double::class.java

    override fun convertItem(item: Double?): Any? = item
}

open class SqPgDoubleReader: SqValueReader<Double> {
    override fun readNullable(source: ResultSet, columnIndex: Int): Double? {
        return source.getDouble(columnIndex).takeUnless { source.wasNull() }
    }
}

open class SqPgDoubleWriter: SqValueWriterBase<Double>() {
    override val sqlType: Int
        get() = SqPgTypes.DOUBLE
    override val typeName: String?
        get() = SqPgTypes.DOUBLE__TYPE_NAME

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Double) {
        target.setDouble(parameterIndex, value)
    }

    override fun notNullValueToComment(value: Double): String = value.toString()
}

open class SqPgIntegerArrayReader: SqPgArrayReaderBase<Int>() {
    override fun readItem(itemSource: ResultSet, columnIndex: Int): Int? {
        return itemSource.getInt(columnIndex).takeUnless { itemSource.wasNull() }
    }
}

open class SqPgIntegerArrayWriter: SqPgArrayWriterBase<Int>() {
    override val typeName: String?
        get() = SqPgTypes.INTEGER_ARRAY__TYPE_NAME
    override val arrayItemTypeName: String
        get() = SqPgTypes.INTEGER__TYPE_NAME

    override fun notNullValueToComment(value: List<Int?>): String {
        return SqUtil.prepareCollectionValueForComment(value)
    }

    override val arrayItemClass: Class<out Any>
        get() = Int::class.java

    override fun convertItem(item: Int?): Any? = item
}

open class SqPgIntegerReader: SqValueReader<Int> {
    override fun readNullable(source: ResultSet, columnIndex: Int): Int? {
        return source.getInt(columnIndex).takeUnless { source.wasNull() }
    }
}

open class SqPgIntegerWriter: SqValueWriterBase<Int>() {
    override val sqlType: Int
        get() = SqPgTypes.INTEGER
    override val typeName: String?
        get() = SqPgTypes.INTEGER__TYPE_NAME

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Int) {
        target.setInt(parameterIndex, value)
    }

    override fun notNullValueToComment(value: Int): String = value.toString()
}

open class SqPgMathBigIntReader: SqValueReader<BigInteger> {
    override fun readNullable(source: ResultSet, columnIndex: Int): BigInteger? {
        return source.getObject(columnIndex, BigInteger::class.java)
    }
}

open class SqPgMathBigIntWriter: SqValueWriterBase<BigInteger>() {
    override val sqlType: Int
        get() = SqPgTypes.BIG_INT
    override val typeName: String?
        get() = SqPgTypes.BIG_INT__TYPE_NAME

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: BigInteger) {
        target.setObject(parameterIndex, value)
    }

    override fun notNullValueToComment(value: BigInteger): String = value.toString()
}

open class SqPgNumericArrayReader: SqPgArrayReaderBase<BigDecimal>() {
    override fun readItem(itemSource: ResultSet, columnIndex: Int): BigDecimal? {
        return itemSource.getBigDecimal(columnIndex)
    }
}

open class SqPgNumericArrayWriter: SqPgArrayWriterBase<BigDecimal>() {
    override val typeName: String?
        get() = SqPgTypes.NUMERIC_ARRAY__TYPE_NAME
    override val arrayItemTypeName: String
        get() = SqPgTypes.NUMERIC__TYPE_NAME

    override fun notNullValueToComment(value: List<BigDecimal?>): String {
        return SqUtil.prepareCollectionValueForComment(value)
    }

    override val arrayItemClass: Class<out Any>
        get() = BigDecimal::class.java

    override fun convertItem(item: BigDecimal?): Any? = item
}

open class SqPgNumericReader: SqValueReader<BigDecimal> {
    override fun readNullable(source: ResultSet, columnIndex: Int): BigDecimal? {
        return source.getBigDecimal(columnIndex)
    }
}

open class SqPgNumericWriter: SqValueWriterBase<BigDecimal>() {
    override val sqlType: Int
        get() = SqPgTypes.NUMERIC
    override val typeName: String?
        get() = SqPgTypes.NUMERIC__TYPE_NAME

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: BigDecimal) {
        target.setBigDecimal(parameterIndex, value)
    }

    override fun notNullValueToComment(value: BigDecimal): String = value.toPlainString()
}

open class SqPgRealArrayReader: SqPgArrayReaderBase<Float>() {
    override fun readItem(itemSource: ResultSet, columnIndex: Int): Float? {
        return itemSource.getFloat(columnIndex).takeUnless { itemSource.wasNull() }
    }
}

open class SqPgRealArrayWriter: SqPgArrayWriterBase<Float>() {
    override val typeName: String?
        get() = SqPgTypes.REAL_ARRAY__TYPE_NAME
    override val arrayItemTypeName: String
        get() = SqPgTypes.REAL__TYPE_NAME

    override fun notNullValueToComment(value: List<Float?>): String {
        return SqUtil.prepareCollectionValueForComment(value)
    }

    override val arrayItemClass: Class<out Any>
        get() = Float::class.java

    override fun convertItem(item: Float?): Any? = item
}

open class SqPgRealReader: SqValueReader<Float> {
    override fun readNullable(source: ResultSet, columnIndex: Int): Float? {
        return source.getFloat(columnIndex).takeUnless { source.wasNull() }
    }
}

open class SqPgRealWriter: SqValueWriterBase<Float>() {
    override val sqlType: Int
        get() = SqPgTypes.REAL
    override val typeName: String?
        get() = SqPgTypes.REAL__TYPE_NAME

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Float) {
        target.setFloat(parameterIndex,value)
    }

    override fun notNullValueToComment(value: Float): String = value.toString()
}

open class SqPgSmallIntArrayReader: SqPgArrayReaderBase<Short>() {
    override fun readItem(itemSource: ResultSet, columnIndex: Int): Short? {
        return itemSource.getShort(columnIndex).takeUnless { itemSource.wasNull() }
    }
}

open class SqPgSmallIntArrayWriter: SqPgArrayWriterBase<Short>() {
    override val typeName: String?
        get() = SqPgTypes.SMALL_INT_ARRAY__TYPE_NAME
    override val arrayItemTypeName: String
        get() = SqPgTypes.SMALL_INT__TYPE_NAME

    override fun notNullValueToComment(value: List<Short?>): String {
        return SqUtil.prepareCollectionValueForComment(value)
    }

    override val arrayItemClass: Class<out Any>
        get() = Short::class.java

    override fun convertItem(item: Short?): Any? = item
}

open class SqPgSmallIntReader: SqValueReader<Short> {
    override fun readNullable(source: ResultSet, columnIndex: Int): Short? {
        return source.getShort(columnIndex).takeUnless { source.wasNull() }
    }
}

open class SqPgSmallIntWriter: SqValueWriterBase<Short>() {
    override val sqlType: Int
        get() = SqPgTypes.SMALL_INT
    override val typeName: String?
        get() = SqPgTypes.SMALL_INT__TYPE_NAME

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Short) {
        target.setShort(parameterIndex, value)
    }

    override fun notNullValueToComment(value: Short): String = value.toString()
}
// endregion


// region Text types
open class SqPgJavaStringReader: SqValueReader<String> {
    override fun readNullable(source: ResultSet, columnIndex: Int): String? {
        return source.getString(columnIndex)
    }
}

open class SqPgJavaStringWriter(
    override val sqlType: Int,
    override val typeName: String,
): SqValueWriterBase<String>() {
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: String) {
        target.setString(parameterIndex, value)
    }

    override fun notNullValueToComment(value: String): String = SqUtil.prepareStringValueForComment(value)
}

open class SqPgJavaStringListReader: SqPgArrayReaderBase<String>() {
    override fun readItem(itemSource: ResultSet, columnIndex: Int): String? {
        return itemSource.getString(columnIndex)
    }
}

open class SqPgJavaStringListWriter(
    override val typeName: String?,
    override val arrayItemTypeName: String,
): SqPgArrayWriterBase<String>() {
    override fun notNullValueToComment(value: List<String?>): String {
        return SqUtil.prepareCollectionValueForComment(value.map { string ->
            when {
                (string == null) -> null
                (string.length > 10) -> "\"${string.take(7)}...\""
                else -> "\"$string\""
            }
        })
    }

    override val arrayItemClass: Class<out Any>
        get() = String::class.java

    override fun convertItem(item: String?): Any? = item
}
// endregion


// region Other JDBC types
open class SqPgXmlArrayReader: SqPgArrayReaderBase<SQLXML>() {
    override fun readItem(itemSource: ResultSet, columnIndex: Int): SQLXML? {
        return itemSource.getSQLXML(columnIndex).takeUnless { itemSource.wasNull() }
    }
}

open class SqPgXmlArrayWriter: SqPgArrayWriterBase<SQLXML>() {
    override val typeName: String?
        get() = SqPgTypes.XML_ARRAY__TYPE_NAME
    override val arrayItemTypeName: String
        get() = SqPgTypes.XML__TYPE_NAME

    override fun notNullValueToComment(value: List<SQLXML?>): String {
        return SqUtil.prepareStringValueForComment(value.toString())
    }

    override val arrayItemClass: Class<out Any>
        get() = String::class.java

    override fun convertItem(item: SQLXML?): Any? {
        return item?.string
    }
}

open class SqPgXmlReader: SqValueReader<SQLXML> {
    override fun readNullable(source: ResultSet, columnIndex: Int): SQLXML? {
        return source.getSQLXML(columnIndex).takeUnless { source.wasNull() }
    }
}

open class SqPgXmlWriter: SqValueWriterBase<SQLXML>() {
    override val sqlType: Int
        get() = SqPgTypes.XML
    override val typeName: String?
        get() = SqPgTypes.XML__TYPE_NAME

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: SQLXML) {
        target.setSQLXML(parameterIndex, value)
    }

    override fun notNullValueToComment(value: SQLXML): String = "... SQL XML content ..."
}
// endregion


// region Misc Postgresql types
open class SqPgMultiBitReader: SqValueReader<BooleanArray> {
    override fun readNullable(source: ResultSet, columnIndex: Int): BooleanArray? {
        return source.getString(columnIndex)?.let { stringValue ->
            BooleanArray(stringValue.length) { index ->
                when (val char = stringValue[index]) {
                    '0' -> false
                    '1' -> true
                    else -> error(buildString {
                        this
                            .append("BIT column with index ")
                            .append(columnIndex)
                            .append(" has invalid value \"")
                            .append(stringValue)
                            .append("\" - found invalid character \"")
                            .append(char)
                            .append("\" (only \"0\" and \"1\" are allowed)")
                    })
                }
            }
        }
    }
}

open class SqPgMultiBitWriter(
    override val sqlType: Int,
    override val typeName: String,
): SqValueWriterBase<BooleanArray>() {
    override fun notNullValueToComment(value: BooleanArray): String {
        val trimmedValue: BooleanArray
        val trimmed: Boolean
        if (value.size > 15) {
            trimmedValue = value.copyOf(12)
            trimmed = true
        } else {
            trimmedValue = value
            trimmed = false
        }

        val string = SqPgUtil.bitsToString(trimmedValue)
        return if (trimmed) {
            "$string... (${value.size - string.length} bit(s) more)"
        } else {
            string
        }
    }

    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: BooleanArray) {
        val pgObject = PGobject().apply {
            this.type = this@SqPgMultiBitWriter.typeName
            this.value = SqPgUtil.bitsToString(value)
        }
        target.setObject(parameterIndex, pgObject)
    }
}

open class SqPgMultiBitArrayReader: SqPgArrayReaderBase<BooleanArray>() {
    override fun readItem(itemSource: ResultSet, columnIndex: Int): BooleanArray? {
        return itemSource.getString(columnIndex)?.let { stringValue ->
            BooleanArray(stringValue.length) { index ->
                when (val char = stringValue[index]) {
                    '0' -> false
                    '1' -> true
                    else -> error(buildString {
                        this
                            .append("BIT array has invalid value \"")
                            .append(stringValue)
                            .append("\" - found invalid character \"")
                            .append(char)
                            .append("\" (only \"0\" and \"1\" are allowed)")
                    })
                }
            }
        }
    }
}

open class SqPgMultiBitArrayWriter(
    override val typeName: String?,
    override val arrayItemTypeName: String,
): SqPgArrayWriterBase<BooleanArray>() {
    override fun notNullValueToComment(value: List<BooleanArray?>): String {
        return SqUtil.prepareCollectionValueForComment(value.map {
            it?.let {
                SqPgUtil.bitsToString(it)
            }
        })
    }

    override val arrayItemClass: Class<out Any>
        get() = PGobject::class.java

    override fun convertItem(item: BooleanArray?): Any? {
        return item?.let {
            PGobject().apply {
                this.type = this@SqPgMultiBitArrayWriter.arrayItemTypeName
                this.value = SqPgUtil.bitsToString(item)
            }
        }
    }
}

open class SqPgPGObjectWriter(
    override val sqlType: Int,
    override val typeName: String?,
): SqValueWriterBase<String>() {
    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: String) {
        val valueObject = PGobject().apply {
            this.type = this@SqPgPGObjectWriter.typeName
            this.value = value
        }
        target.setObject(parameterIndex, valueObject)
    }

    override fun notNullValueToComment(value: String): String = SqUtil.prepareStringValueForComment(value)
}

open class SqPgPGObjectArrayWriter(
    override val typeName: String?,
    override val arrayItemTypeName: String,
): SqPgArrayWriterBase<String>() {
    override fun notNullValueToComment(value: List<String?>): String {
        return SqUtil.prepareStringValueForComment(value.toString())
    }

    override val arrayItemClass: Class<out Any>
        get() = PGobject::class.java

    override fun convertItem(item: String?): Any? {
        return item?.let {
            PGobject().also {
                it.type = this.arrayItemTypeName
                it.value = item
            }
        }
    }
}
// endregion
