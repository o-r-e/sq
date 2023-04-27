package me.ore.sq.pg

import me.ore.sq.*
import java.sql.Connection
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


// region Context data / context start
private val DATA: SqContextData = SqContextData(printParameterValues = false, SqPgObjectHolder.INSTANCE)
val SqContextData.Companion.POSTGRESQL: SqContextData
    get() = DATA


inline fun <T> sqPg(block: SqContext.Context.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return block.invoke(SqContext.Context(SqContextData.POSTGRESQL))
}

inline fun <T> sqPg(connection: Connection, block: SqContext.ConnContext.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return block.invoke(SqContext.ConnContext(SqContextData.POSTGRESQL, connection))
}
// endregion
