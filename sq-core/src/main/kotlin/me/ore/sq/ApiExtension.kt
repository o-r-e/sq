package me.ore.sq

import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import java.sql.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.util.Calendar
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


// region Context
inline fun <C: SqContext, T> C.use(block: C.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return try {
        this.start()
        block.invoke(this)
    } finally {
        this.finish()
    }
}

inline fun <C: SqContext, T> sq(context: C, block: C.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return context.use(block)
}

inline fun <T> sq(block: SqContext.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return SqContext.CONTEXT.use(block)
}

inline fun <T> sq(connection: Connection, block: SqConnectedContext.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return SqContext.CONTEXT.createConnectedContext(connection).use(block)
}
// endregion


// region Base items
inline fun <T> SqItem.workWithPossibleAlias(processAlias: (alias: SqAlias<*>) -> T, processItem: (item: SqItem) -> T): T {
    contract {
        callsInPlace(processAlias, InvocationKind.AT_MOST_ONCE)
        callsInPlace(processItem, InvocationKind.AT_MOST_ONCE)
    }

    return if (this is SqAlias<*>) {
        processAlias(this)
    } else {
        processItem(this)
    }
}

fun <T: PreparedStatement> Iterable<SqParameter<*, *>>?.setTo(target: T): T {
    this?.forEachIndexed { index, parameter ->
        parameter.write(target, index + 1)
    }
    return target
}


inline fun <reified JAVA: String?> SqContext.charParam(value: JAVA): SqParameter<JAVA, String> = this.charParam((null is JAVA), value)
inline fun <reified JAVA: String?> SqContext.varCharParam(value: JAVA): SqParameter<JAVA, String> = this.varCharParam((null is JAVA), value)
inline fun <reified JAVA: String?> SqContext.longVarCharParam(value: JAVA): SqParameter<JAVA, String> = this.longVarCharParam((null is JAVA), value)
inline fun <reified JAVA: String?> SqContext.nCharParam(value: JAVA): SqParameter<JAVA, String> = this.nCharParam((null is JAVA), value)
inline fun <reified JAVA: String?> SqContext.nVarCharParam(value: JAVA): SqParameter<JAVA, String> = this.nVarCharParam((null is JAVA), value)
inline fun <reified JAVA: String?> SqContext.longNVarCharParam(value: JAVA): SqParameter<JAVA, String> = this.longNVarCharParam((null is JAVA), value)
inline fun <reified JAVA: String?> SqContext.jStringParam(value: JAVA): SqParameter<JAVA, String> = this.jStringParam((null is JAVA), value)

inline fun <reified JAVA: BigDecimal?> SqContext.numericParam(value: JAVA): SqParameter<JAVA, Number> = this.numericParam((null is JAVA), value)
inline fun <reified JAVA: BigDecimal?> SqContext.decimalParam(value: JAVA): SqParameter<JAVA, Number> = this.decimalParam((null is JAVA), value)
inline fun <reified JAVA: Byte?> SqContext.tinyIntParam(value: JAVA): SqParameter<JAVA, Number> = this.tinyIntParam((null is JAVA), value)
inline fun <reified JAVA: Short?> SqContext.smallIntParam(value: JAVA): SqParameter<JAVA, Number> = this.smallIntParam((null is JAVA), value)
inline fun <reified JAVA: Int?> SqContext.integerParam(value: JAVA): SqParameter<JAVA, Number> = this.integerParam((null is JAVA), value)
inline fun <reified JAVA: Long?> SqContext.bigIntParam(value: JAVA): SqParameter<JAVA, Number> = this.bigIntParam((null is JAVA), value)
inline fun <reified JAVA: BigInteger?> SqContext.bigIntAsBigIntegerParam(value: JAVA): SqParameter<JAVA, Number> = this.bigIntAsBigIntegerParam((null is JAVA), value)
inline fun <reified JAVA: Float?> SqContext.realParam(value: JAVA): SqParameter<JAVA, Number> = this.realParam((null is JAVA), value)
inline fun <reified JAVA: Double?> SqContext.floatParam(value: JAVA): SqParameter<JAVA, Number> = this.floatParam((null is JAVA), value)
inline fun <reified JAVA: Double?> SqContext.doubleParam(value: JAVA): SqParameter<JAVA, Number> = this.doubleParam((null is JAVA), value)
inline fun <reified JAVA: BigDecimal?> SqContext.jBigDecimalParam(value: JAVA): SqParameter<JAVA, Number> = this.jBigDecimalParam((null is JAVA), value)
inline fun <reified JAVA: Byte?> SqContext.jByteParam(value: JAVA): SqParameter<JAVA, Number> = this.jByteParam((null is JAVA), value)
inline fun <reified JAVA: Short?> SqContext.jShortParam(value: JAVA): SqParameter<JAVA, Number> = this.jShortParam((null is JAVA), value)
inline fun <reified JAVA: Int?> SqContext.jIntParam(value: JAVA): SqParameter<JAVA, Number> = this.jIntParam((null is JAVA), value)
inline fun <reified JAVA: Long?> SqContext.jLongParam(value: JAVA): SqParameter<JAVA, Number> = this.jLongParam((null is JAVA), value)
inline fun <reified JAVA: BigInteger?> SqContext.jBigIntegerParam(value: JAVA): SqParameter<JAVA, Number> = this.jBigIntegerParam((null is JAVA), value)
inline fun <reified JAVA: Float?> SqContext.jFloatParam(value: JAVA): SqParameter<JAVA, Number> = this.jFloatParam((null is JAVA), value)
inline fun <reified JAVA: Double?> SqContext.jDoubleParam(value: JAVA): SqParameter<JAVA, Number> = this.jDoubleParam((null is JAVA), value)

inline fun <reified JAVA: Boolean?> SqContext.bitParam(value: JAVA): SqParameter<JAVA, Boolean> = this.bitParam((null is JAVA), value)
inline fun <reified JAVA: Boolean?> SqContext.booleanParam(value: JAVA): SqParameter<JAVA, Boolean> = this.booleanParam((null is JAVA), value)
inline fun <reified JAVA: Boolean?> SqContext.jBooleanParam(value: JAVA): SqParameter<JAVA, Boolean> = this.jBooleanParam((null is JAVA), value)

inline fun <reified JAVA: SqByteArray?> SqContext.binaryParam(value: JAVA): SqParameter<JAVA, ByteArray> = this.binaryParam((null is JAVA), value)
inline fun <reified JAVA: SqByteArray?> SqContext.varBinaryParam(value: JAVA): SqParameter<JAVA, ByteArray> = this.varBinaryParam((null is JAVA), value)
inline fun <reified JAVA: SqByteArray?> SqContext.longVarBinaryParam(value: JAVA): SqParameter<JAVA, ByteArray> = this.longVarBinaryParam((null is JAVA), value)
inline fun <reified JAVA: SqByteArray?> SqContext.jByteArrayParam(value: JAVA): SqParameter<JAVA, ByteArray> = this.jByteArrayParam((null is JAVA), value)

inline fun <reified JAVA: Clob?> SqContext.clobParam(value: JAVA): SqParameter<JAVA, Clob> = this.clobParam((null is JAVA), value)
inline fun <reified JAVA: Blob?> SqContext.blobParam(value: JAVA): SqParameter<JAVA, Blob> = this.blobParam((null is JAVA), value)
inline fun <reified JAVA: Ref?> SqContext.refParam(value: JAVA): SqParameter<JAVA, Ref> = this.refParam((null is JAVA), value)
inline fun <reified JAVA: URL?> SqContext.dataLinkParam(value: JAVA): SqParameter<JAVA, String> = this.dataLinkParam((null is JAVA), value)
inline fun <reified JAVA: RowId?> SqContext.rowIdParam(value: JAVA): SqParameter<JAVA, RowId> = this.rowIdParam((null is JAVA), value)
inline fun <reified JAVA: NClob?> SqContext.nClobParam(value: JAVA): SqParameter<JAVA, Clob> = this.nClobParam((null is JAVA), value)
inline fun <reified JAVA: SQLXML?> SqContext.sqlXmlParam(value: JAVA): SqParameter<JAVA, String> = this.sqlXmlParam((null is JAVA), value)
inline fun <reified JAVA: Clob?> SqContext.jClobParam(value: JAVA): SqParameter<JAVA, Clob> = this.jClobParam((null is JAVA), value)
inline fun <reified JAVA: Blob?> SqContext.jBlobParam(value: JAVA): SqParameter<JAVA, Blob> = this.jBlobParam((null is JAVA), value)
inline fun <reified JAVA: Ref?> SqContext.jRefParam(value: JAVA): SqParameter<JAVA, Ref> = this.jRefParam((null is JAVA), value)
inline fun <reified JAVA: URL?> SqContext.jUrlParam(value: JAVA): SqParameter<JAVA, String> = this.jUrlParam((null is JAVA), value)
inline fun <reified JAVA: RowId?> SqContext.jRowIdParam(value: JAVA): SqParameter<JAVA, RowId> = this.jRowIdParam((null is JAVA), value)
inline fun <reified JAVA: NClob?> SqContext.jNClobParam(value: JAVA): SqParameter<JAVA, Clob> = this.jNClobParam((null is JAVA), value)
inline fun <reified JAVA: SQLXML?> SqContext.jSqlXmlParam(value: JAVA): SqParameter<JAVA, String> = this.jSqlXmlParam((null is JAVA), value)

inline fun <reified JAVA: Date?> SqContext.jSqlDateParam(value: JAVA): SqParameter<JAVA, Timestamp> = this.jSqlDateParam((null is JAVA), value)
inline fun <reified JAVA: LocalDate?> SqContext.jLocalDateParam(value: JAVA): SqParameter<JAVA, Timestamp> = this.jLocalDateParam((null is JAVA), value)
inline fun <reified JAVA: Time?> SqContext.jSqlTimeParam(value: JAVA): SqParameter<JAVA, Time> = this.jSqlTimeParam((null is JAVA), value)
inline fun <reified JAVA: LocalTime?> SqContext.jLocalTimeParam(value: JAVA): SqParameter<JAVA, Time> = this.jLocalTimeParam((null is JAVA), value)
inline fun <reified JAVA: Timestamp?> SqContext.jSqlTimestampParam(value: JAVA): SqParameter<JAVA, Timestamp> = this.jSqlTimestampParam((null is JAVA), value)
inline fun <reified JAVA: Calendar?> SqContext.jCalendarParam(value: JAVA): SqParameter<JAVA, Timestamp> = this.jCalendarParam((null is JAVA), value)
inline fun <reified JAVA: java.util.Date?> SqContext.jDateParam(value: JAVA): SqParameter<JAVA, Timestamp> = this.jDateParam((null is JAVA), value)
inline fun <reified JAVA: LocalDateTime?> SqContext.jLocalDateTimeParam(value: JAVA): SqParameter<JAVA, Timestamp> = this.jLocalDateTimeParam((null is JAVA), value)
inline fun <reified JAVA: OffsetTime?> SqContext.jOffsetTimeParam(value: JAVA): SqParameter<JAVA, Time> = this.jOffsetTimeParam((null is JAVA), value)
inline fun <reified JAVA: OffsetDateTime?> SqContext.jOffsetDateTimeParam(value: JAVA): SqParameter<JAVA, Timestamp> = this.jOffsetDateTimeParam((null is JAVA), value)
inline fun <reified JAVA: Date?> SqContext.dateAsSqlDateParam(value: JAVA): SqParameter<JAVA, Timestamp> = this.dateAsSqlDateParam((null is JAVA), value)
inline fun <reified JAVA: LocalDate?> SqContext.dateParam(value: JAVA): SqParameter<JAVA, Timestamp> = this.dateParam((null is JAVA), value)
inline fun <reified JAVA: Time?> SqContext.timeAsSqlTimeParam(value: JAVA): SqParameter<JAVA, Time> = this.timeAsSqlTimeParam((null is JAVA), value)
inline fun <reified JAVA: LocalTime?> SqContext.timeParam(value: JAVA): SqParameter<JAVA, Time> = this.timeParam((null is JAVA), value)
inline fun <reified JAVA: Timestamp?> SqContext.timestampAsSqlTimestampParam(value: JAVA): SqParameter<JAVA, Timestamp> = this.timestampAsSqlTimestampParam((null is JAVA), value)
inline fun <reified JAVA: Calendar?> SqContext.timestampAsCalendarParam(value: JAVA): SqParameter<JAVA, Timestamp> = this.timestampAsCalendarParam((null is JAVA), value)
inline fun <reified JAVA: java.util.Date?> SqContext.timestampAsDateParam(value: JAVA): SqParameter<JAVA, Timestamp> = this.timestampAsDateParam((null is JAVA), value)
inline fun <reified JAVA: LocalDateTime?> SqContext.timestampParam(value: JAVA): SqParameter<JAVA, Timestamp> = this.timestampParam((null is JAVA), value)
inline fun <reified JAVA: OffsetTime?> SqContext.timeWithTimeZoneParam(value: JAVA): SqParameter<JAVA, Time> = this.timeWithTimeZoneParam((null is JAVA), value)
inline fun <reified JAVA: OffsetDateTime?> SqContext.timestampWithTimeZoneParam(value: JAVA): SqParameter<JAVA, Timestamp> = this.timestampWithTimeZoneParam((null is JAVA), value)


inline fun <S: SqConnReadStatement, R: Any?> S.read(resultListCapacity: Int = 1, block: S.(resultSet: SqResultSet) -> SqReadResult<R>): List<R> {
    contract { callsInPlace(block, InvocationKind.UNKNOWN) }

    val result = ArrayList<R>(resultListCapacity)

    this.prepareStatement().use { statement ->
        statement.executeQuery().use { jdbcResultSet ->
            val resultSet = SqResultSet(this, jdbcResultSet)
            while (resultSet.next()) {
                when (val blockResult = block.invoke(this, resultSet)) {
                    is SqReadResult.CancelReading -> {
                        break
                    }
                    is SqReadResult.Result -> {
                        result.add(blockResult.value)
                    }
                }
            }
        }
    }

    return result
}

inline fun <S: SqConnReadStatement, R: Any?> S.readAll(resultListCapacity: Int = 1, block: S.(resultSet: SqResultSet) -> R): List<R> {
    contract { callsInPlace(block, InvocationKind.UNKNOWN) }

    val result = ArrayList<R>(resultListCapacity)

    this.prepareStatement().use { statement ->
        statement.executeQuery().use { jdbcResultSet ->
            val resultSet = SqResultSet(this, jdbcResultSet)
            while (resultSet.next()) {
                val blockResult = block.invoke(this, resultSet)
                result.add(blockResult)
            }
        }
    }

    return result
}

inline fun <S: SqConnReadStatement> S.scan(block: S.(resultSet: SqResultSet) -> Boolean): Int {
    contract { callsInPlace(block, InvocationKind.UNKNOWN) }

    var result = 0

    this.prepareStatement().use { statement ->
        statement.executeQuery().use { jdbcResultSet ->
            val resultSet = SqResultSet(this, jdbcResultSet)
            while (resultSet.next()) {
                val blockResult = block.invoke(this, resultSet)
                result++
                if (!blockResult) {
                    break
                }
            }
        }
    }

    return result
}

inline fun <S: SqConnReadStatement> S.scanAll(block: S.(resultSet: SqResultSet) -> Unit): Int {
    contract { callsInPlace(block, InvocationKind.UNKNOWN) }

    var result = 0

    this.prepareStatement().use { statement ->
        statement.executeQuery().use { jdbcResultSet ->
            val resultSet = SqResultSet(this, jdbcResultSet)
            while (resultSet.next()) {
                block.invoke(this, resultSet)
                result++
            }
        }
    }

    return result
}
// endregion


// region Statements - modification
inline fun <T: SqTable, S: SqTableWriteStatement<T>> S.set(block: SqColumnValueMapping<T>.(table: T) -> Unit): S {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

    val mapping = this.createValueMapping()
    block.invoke(mapping, this.table)
    this.applyValueMapping(mapping)
    mapping.map.clear()
    return this
}
// endregion
