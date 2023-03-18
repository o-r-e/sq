package me.ore.sq

import java.math.BigDecimal
import java.net.URL
import java.sql.*
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


inline fun <reified JAVA: BigDecimal?> SqContext.jBigDecimalParam(value: JAVA): SqParameter<JAVA, Number> = this.jBigDecimalParam((null is JAVA), value)
inline fun <reified JAVA: Blob?> SqContext.jBlobParam(value: JAVA): SqParameter<JAVA, Number> = this.jBlobParam((null is JAVA), value)
inline fun <reified JAVA: Boolean?> SqContext.jBooleanParam(value: JAVA): SqParameter<JAVA, Boolean> = this.jBooleanParam((null is JAVA), value)
inline fun <reified JAVA: Byte?> SqContext.jByteParam(value: JAVA): SqParameter<JAVA, Boolean> = this.jByteParam((null is JAVA), value)
inline fun <reified JAVA: SqByteArray?> SqContext.jByteArrayParam(value: JAVA): SqParameter<JAVA, SqByteArray> = this.jByteArrayParam((null is JAVA), value)
inline fun <reified JAVA: Clob?> SqContext.jClobParam(value: JAVA): SqParameter<JAVA, Clob> = this.jClobParam((null is JAVA), value)
inline fun <reified JAVA: Date?> SqContext.jDateParam(value: JAVA): SqParameter<JAVA, Date> = this.jDateParam((null is JAVA), value)
inline fun <reified JAVA: Double?> SqContext.jDoubleParam(value: JAVA): SqParameter<JAVA, Number> = this.jDoubleParam((null is JAVA), value)
inline fun <reified JAVA: Float?> SqContext.jFloatParam(value: JAVA): SqParameter<JAVA, Number> = this.jFloatParam((null is JAVA), value)
inline fun <reified JAVA: Int?> SqContext.jIntParam(value: JAVA): SqParameter<JAVA, Number> = this.jIntParam((null is JAVA), value)
inline fun <reified JAVA: Long?> SqContext.jLongParam(value: JAVA): SqParameter<JAVA, Number> = this.jLongParam((null is JAVA), value)
inline fun <reified JAVA: NClob?> SqContext.jNClobParam(value: JAVA): SqParameter<JAVA, Clob> = this.jNClobParam((null is JAVA), value)
inline fun <reified JAVA: Ref?> SqContext.jRefParam(value: JAVA): SqParameter<JAVA, Ref> = this.jRefParam((null is JAVA), value)
inline fun <reified JAVA: RowId?> SqContext.jRowIdParam(value: JAVA): SqParameter<JAVA, RowId> = this.jRowIdParam((null is JAVA), value)
inline fun <reified JAVA: SQLXML?> SqContext.jSqlXmlParam(value: JAVA): SqParameter<JAVA, SQLXML> = this.jSqlXmlParam((null is JAVA), value)
inline fun <reified JAVA: Short?> SqContext.jShortParam(value: JAVA): SqParameter<JAVA, Number> = this.jShortParam((null is JAVA), value)
inline fun <reified JAVA: String?> SqContext.jStringParam(value: JAVA): SqParameter<JAVA, String> = this.jStringParam((null is JAVA), value)
inline fun <reified JAVA: Time?> SqContext.jTimeParam(value: JAVA): SqParameter<JAVA, Time> = this.jTimeParam((null is JAVA), value)
inline fun <reified JAVA: Timestamp?> SqContext.jTimestampParam(value: JAVA): SqParameter<JAVA, Date> = this.jTimestampParam((null is JAVA), value)
inline fun <reified JAVA: URL?> SqContext.jUrlParam(value: JAVA): SqParameter<JAVA, String> = this.jUrlParam((null is JAVA), value)


inline fun <reified JAVA: Long?> SqContext.dbBigIntParam(value: JAVA): SqParameter<JAVA, Number> = this.dbBigIntParam((null is JAVA), value)
inline fun <reified JAVA: SqByteArray?> SqContext.dbBinaryParam(value: JAVA): SqParameter<JAVA, SqByteArray> = this.dbBinaryParam((null is JAVA), value)
inline fun <reified JAVA: Boolean?> SqContext.dbBitParam(value: JAVA): SqParameter<JAVA, Boolean> = this.dbBitParam((null is JAVA), value)
inline fun <reified JAVA: Blob?> SqContext.dbBlobParam(value: JAVA): SqParameter<JAVA, Blob> = this.dbBlobParam((null is JAVA), value)
inline fun <reified JAVA: Boolean?> SqContext.dbBooleanParam(value: JAVA): SqParameter<JAVA, Boolean> = this.dbBooleanParam((null is JAVA), value)
inline fun <reified JAVA: String?> SqContext.dbCharParam(value: JAVA): SqParameter<JAVA, String> = this.dbCharParam((null is JAVA), value)
inline fun <reified JAVA: Clob?> SqContext.dbClobParam(value: JAVA): SqParameter<JAVA, Boolean> = this.dbClobParam((null is JAVA), value)
inline fun <reified JAVA: URL?> SqContext.dbDataLinkParam(value: JAVA): SqParameter<JAVA, String> = this.dbDataLinkParam((null is JAVA), value)
inline fun <reified JAVA: Date?> SqContext.dbDateParam(value: JAVA): SqParameter<JAVA, Date> = this.dbDateParam((null is JAVA), value)
inline fun <reified JAVA: BigDecimal?> SqContext.dbDecimalParam(value: JAVA): SqParameter<JAVA, Number> = this.dbDecimalParam((null is JAVA), value)
inline fun <reified JAVA: Double?> SqContext.dbDoubleParam(value: JAVA): SqParameter<JAVA, Number> = this.dbDoubleParam((null is JAVA), value)
inline fun <reified JAVA: Double?> SqContext.dbFloatParam(value: JAVA): SqParameter<JAVA, Number> = this.dbFloatParam((null is JAVA), value)
inline fun <reified JAVA: Int?> SqContext.dbIntegerParam(value: JAVA): SqParameter<JAVA, Number> = this.dbIntegerParam((null is JAVA), value)
inline fun <reified JAVA: String?> SqContext.dbLongNVarCharParam(value: JAVA): SqParameter<JAVA, String> = this.dbLongNVarCharParam((null is JAVA), value)
inline fun <reified JAVA: SqByteArray?> SqContext.dbLongVarBinaryParam(value: JAVA): SqParameter<JAVA, SqByteArray> = this.dbLongVarBinaryParam((null is JAVA), value)
inline fun <reified JAVA: String?> SqContext.dbLongVarCharParam(value: JAVA): SqParameter<JAVA, String> = this.dbLongVarCharParam((null is JAVA), value)
inline fun <reified JAVA: String?> SqContext.dbNCharParam(value: JAVA): SqParameter<JAVA, String> = this.dbNCharParam((null is JAVA), value)
inline fun <reified JAVA: NClob?> SqContext.dbNClobParam(value: JAVA): SqParameter<JAVA, Clob> = this.dbNClobParam((null is JAVA), value)
inline fun <reified JAVA: String?> SqContext.dbNVarCharParam(value: JAVA): SqParameter<JAVA, String> = this.dbNVarCharParam((null is JAVA), value)
inline fun <reified JAVA: BigDecimal?> SqContext.dbNumericParam(value: JAVA): SqParameter<JAVA, Number> = this.dbNumericParam((null is JAVA), value)
inline fun <reified JAVA: Float?> SqContext.dbRealParam(value: JAVA): SqParameter<JAVA, Number> = this.dbRealParam((null is JAVA), value)
inline fun <reified JAVA: Ref?> SqContext.dbRefParam(value: JAVA): SqParameter<JAVA, Ref> = this.dbRefParam((null is JAVA), value)
inline fun <reified JAVA: RowId?> SqContext.dbRowIdParam(value: JAVA): SqParameter<JAVA, RowId> = this.dbRowIdParam((null is JAVA), value)
inline fun <reified JAVA: Short?> SqContext.dbSmallIntParam(value: JAVA): SqParameter<JAVA, Number> = this.dbSmallIntParam((null is JAVA), value)
inline fun <reified JAVA: SQLXML?> SqContext.dbSqlXmlParam(value: JAVA): SqParameter<JAVA, SQLXML> = this.dbSqlXmlParam((null is JAVA), value)
inline fun <reified JAVA: Time?> SqContext.dbTimeParam(value: JAVA): SqParameter<JAVA, Time> = this.dbTimeParam((null is JAVA), value)
inline fun <reified JAVA: Timestamp?> SqContext.dbTimestampParam(value: JAVA): SqParameter<JAVA, Date> = this.dbTimestampParam((null is JAVA), value)
inline fun <reified JAVA: Byte?> SqContext.dbTinyIntParam(value: JAVA): SqParameter<JAVA, Number> = this.dbTinyIntParam((null is JAVA), value)
inline fun <reified JAVA: SqByteArray?> SqContext.dbVarBinaryParam(value: JAVA): SqParameter<JAVA, SqByteArray> = this.dbVarBinaryParam((null is JAVA), value)
inline fun <reified JAVA: String?> SqContext.dbVarCharParam(value: JAVA): SqParameter<JAVA, String> = this.dbVarCharParam((null is JAVA), value)


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
