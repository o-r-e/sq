package me.ore.sq.pg

import me.ore.sq.SqColumn
import me.ore.sq.SqConnectedContext
import me.ore.sq.SqContext
import me.ore.sq.SqContextHolder
import me.ore.sq.generic.SqGenericConnectedContext
import java.sql.Connection


interface SqPgContext: SqContext {
    companion object: SqContextHolder<SqPgContext>() {
        override fun createDefaultContext(): SqPgContext = SqPgContextImpl()
    }


    // region Utils
    override fun createConnectedContext(connection: Connection): SqPgConnectedContext

    override fun start() { SqPgContext.start(this) }
    override fun finish() { SqPgContext.finish(this) }
    // endregion


    // region Statements - join, order by, select, union
    override fun select(distinct: Boolean, columns: Iterable<SqColumn<*, *>>): SqPgMultiColSelect
    override fun select(distinct: Boolean, first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqPgMultiColSelect =
        this.select(distinct, listOf(first, second, *more))
    override fun select(columns: Iterable<SqColumn<*, *>>): SqPgMultiColSelect = this.select(distinct = false, columns)
    override fun select(first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqPgMultiColSelect =
        this.select(distinct = false, first, second, *more)
    override fun selectDistinct(columns: Iterable<SqColumn<*, *>>): SqPgMultiColSelect = this.select(distinct = true, columns)
    override fun selectDistinct(first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqPgMultiColSelect =
        this.select(distinct = true, first, second, *more)

    override fun <JAVA: Any?, DB: Any> select(distinct: Boolean, column: SqColumn<JAVA, DB>): SqPgSingleColSelect<JAVA, DB>
    override fun <JAVA: Any?, DB: Any> select(column: SqColumn<JAVA, DB>): SqPgSingleColSelect<JAVA, DB> = this.select(distinct = false, column)
    override fun <JAVA: Any?, DB: Any> selectDistinct(column: SqColumn<JAVA, DB>): SqPgSingleColSelect<JAVA, DB> = this.select(distinct = true, column)
    // endregion
}


interface SqPgConnectedContext: SqGenericConnectedContext, SqPgContext {
    override fun start()
    override fun finish()


    // region Statements - join, order by, select, union
    override fun select(distinct: Boolean, columns: Iterable<SqColumn<*, *>>): SqPgConnMultiColSelect
    override fun select(distinct: Boolean, first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqPgConnMultiColSelect =
        this.select(distinct, listOf(first, second, *more))
    override fun select(columns: Iterable<SqColumn<*, *>>): SqPgConnMultiColSelect = this.select(distinct = false, columns)
    override fun select(first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqPgConnMultiColSelect =
        this.select(distinct = false, first, second, *more)
    override fun selectDistinct(columns: Iterable<SqColumn<*, *>>): SqPgConnMultiColSelect = this.select(distinct = true, columns)
    override fun selectDistinct(first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqPgConnMultiColSelect =
        this.select(distinct = true, first, second, *more)

    override fun <JAVA: Any?, DB: Any> select(distinct: Boolean, column: SqColumn<JAVA, DB>): SqPgConnSingleColSelect<JAVA, DB>
    override fun <JAVA: Any?, DB: Any> select(column: SqColumn<JAVA, DB>): SqPgConnSingleColSelect<JAVA, DB> = this.select(distinct = false, column)
    override fun <JAVA: Any?, DB: Any> selectDistinct(column: SqColumn<JAVA, DB>): SqPgConnSingleColSelect<JAVA, DB> = this.select(distinct = true, column)
    // endregion
}
