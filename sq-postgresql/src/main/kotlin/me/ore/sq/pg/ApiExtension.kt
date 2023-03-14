package me.ore.sq.pg

import me.ore.sq.use
import java.sql.Connection
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


inline fun <C: SqPgContext, T> sqPg(context: C, block: C.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return context.use(block)
}

inline fun <T> sqPg(block: SqPgContext.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return SqPgContext.CONTEXT.use(block)
}

inline fun <T> sqPg(connection: Connection, block: SqPgConnectedContext.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return SqPgContext.CONTEXT.createConnectedContext(connection).use(block)
}
