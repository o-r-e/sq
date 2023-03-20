package me.ore.sq.generic

import me.ore.sq.*
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import java.sql.*
import java.sql.Date
import java.time.*
import java.util.*


interface SqGenericContext: SqContext {
    companion object: SqContextHolder<SqGenericContext>() {
        override fun createDefaultContext(): SqGenericContext = SqGenericContextImpl()
    }


    // region Utils
    override fun createConnectedContext(connection: Connection): SqGenericConnectedContext

    override fun start() { SqGenericContext.start(this) }
    override fun finish() { SqGenericContext.finish(this) }
    // endregion
}


interface SqGenericConnectedContext: SqConnectedContext, SqGenericContext
