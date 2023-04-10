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
