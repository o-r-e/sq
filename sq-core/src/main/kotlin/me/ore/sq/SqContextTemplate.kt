package me.ore.sq

import java.sql.Connection
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


class SqContextTemplate(
    @Volatile
    var data: SqContextData,
) {
    companion object {
        @Volatile
        var defaultTemplate: SqContextTemplate = SqContextTemplate(SqContextData.EMPTY)
    }


    fun create(): SqContext.Context = SqContext.Context(this.data)
    fun create(connection: Connection): SqContext.ConnContext = SqContext.ConnContext(this.data, connection)
}


inline fun <T, C: SqContext> sq(context: C, block: C.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return block.invoke(context)
}

inline fun <T> sq(data: SqContextData, block: SqContext.Context.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return SqContext.Context(data).start().use(block)
}

inline fun <T> sq(data: SqContextData, connection: Connection, block: SqContext.ConnContext.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return SqContext.ConnContext(data, connection).start().use(block)
}

inline fun <T> sq(template: SqContextTemplate, block: SqContext.Context.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return template.create().start().use(block)
}

inline fun <T> sq(template: SqContextTemplate, connection: Connection, block: SqContext.ConnContext.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return template.create(connection).start().use(block)
}

inline fun <T> sq(block: SqContext.Context.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return SqContextTemplate.defaultTemplate.create().start().use(block)
}

inline fun <T> sq(connection: Connection, block: SqContext.ConnContext.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return SqContextTemplate.defaultTemplate.create(connection).start().use(block)
}
