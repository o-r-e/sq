package me.ore.sq.generic

import java.sql.Connection


open class SqGenericContextImpl: SqGenericContextBase() {
    // region Utils
    override fun createConnectedContext(connection: Connection): SqGenericConnectedContext = SqGenericConnectedContextImpl(connection)
    // endregion
}
