package me.ore.sq.pg

import me.ore.sq.*
import java.sql.Connection
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


// region Context data / context start
private val DATA: SqContextConfig = SqContextConfig(printParameterValues = false, SqPgObjectHolder.INSTANCE)
val SqContextConfig.Companion.POSTGRESQL: SqContextConfig
    get() = DATA


inline fun <T> sqPg(block: SqContext.Context.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return block.invoke(SqContext.Context(SqContextConfig.POSTGRESQL))
}

inline fun <T> sqPg(connection: Connection, block: SqContext.ConnContext.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return block.invoke(SqContext.ConnContext(SqContextConfig.POSTGRESQL, connection))
}
// endregion
