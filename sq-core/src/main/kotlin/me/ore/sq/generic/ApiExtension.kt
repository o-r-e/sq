package me.ore.sq.generic

import me.ore.sq.use
import java.sql.Connection
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


inline fun <C: SqGenericContext, T> sqGeneric(context: C, block: C.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return context.use(block)
}

inline fun <T> sqGeneric(block: SqGenericContext.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return SqGenericContext.CONTEXT.use(block)
}

inline fun <T> sqGeneric(connection: Connection, block: SqGenericConnectedContext.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return SqGenericContext.CONTEXT.createConnectedContext(connection).use(block)
}
