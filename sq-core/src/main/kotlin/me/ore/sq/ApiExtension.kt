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
