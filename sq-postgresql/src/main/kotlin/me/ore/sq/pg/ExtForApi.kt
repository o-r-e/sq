package me.ore.sq.pg

import me.ore.sq.SqContext
import me.ore.sq.SqContextData
import java.sql.Connection
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


// region Context / start
private val DATA: SqContextData = SqContextData(printParameterValues = false, SqPgObjectMap.INSTANCE)
val SqContextData.Companion.PG_DATA: SqContextData
    get() = DATA


inline fun <T> sqPg(block: SqContext.Context.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return block.invoke(SqContext.Context(SqContextData.PG_DATA))
}

inline fun <T> sqPg(connection: Connection, block: SqContext.ConnContext.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return block.invoke(SqContext.ConnContext(SqContextData.PG_DATA, connection))
}
// endregion
