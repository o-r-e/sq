package me.ore.sq.pg

import me.ore.sq.SqColumn
import me.ore.sq.SqMultiColUnion
import me.ore.sq.SqSelect
import me.ore.sq.SqSingleColSelect
import me.ore.sq.generic.SqGenericConnectedContext
import me.ore.sq.generic.SqGenericContextBase
import java.sql.Connection


open class SqPgContextImpl: SqGenericContextBase(), SqPgContext {
    // region Utils
    override fun createConnectedContext(connection: Connection): SqPgConnectedContext = SqPgConnectedContextImpl(connection)

    override fun start() {
        super<SqGenericContextBase>.start()
        super<SqPgContext>.start()
    }

    override fun finish() {
        super<SqPgContext>.finish()
        super<SqGenericContextBase>.finish()
    }
    // endregion


    // region Statements - join, order by, select, union
    override fun select(distinct: Boolean, columns: Iterable<SqColumn<*, *>>): SqPgMultiColSelect =
        SqPgMultiColSelectImpl(this, distinct, columns)
    override fun <JAVA: Any?, DB: Any> select(distinct: Boolean, column: SqColumn<JAVA, DB>): SqPgSingleColSelect<JAVA, DB> =
        SqPgSingleColSelectImpl(this, distinct, column)

    override fun union(unionAll: Boolean, selects: Iterable<SqSelect>): SqPgMultiColUnion = SqPgMultiColUnionImpl(this, unionAll, selects.toList())

    override fun <JAVA: Any?, DB: Any> union(unionAll: Boolean, selects: Iterable<SqSingleColSelect<JAVA, DB>>): SqPgSingleColUnion<JAVA, DB> =
        SqPgSingleColUnionImpl(this, unionAll, selects.toList())
    // endregion
}
