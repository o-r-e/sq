package me.ore.sq.pg

import me.ore.sq.*
import me.ore.sq.generic.*
import java.sql.Connection


open class SqPgConnectedContextImpl(override val connection: Connection): SqPgContextImpl(), SqPgConnectedContext {
    // region Statements - select, union
    override fun select(distinct: Boolean, columns: Iterable<SqColumn<*, *>>): SqPgConnMultiColSelect = SqPgConnMultiColSelectImpl(this, distinct, columns)
    override fun <JAVA: Any?, DB: Any> select(distinct: Boolean, column: SqColumn<JAVA, DB>): SqPgConnSingleColSelect<JAVA, DB> =
        SqPgConnSingleColSelectImpl(this, distinct, column)

    override fun union(unionAll: Boolean, selects: Iterable<SqSelect>): SqConnMultiColUnion = SqGenericConnMultiColUnion(this, unionAll, selects)
    override fun <JAVA: Any?, DB: Any> union(unionAll: Boolean, selects: Iterable<SqSingleColSelect<JAVA, DB>>): SqConnSingleColUnion<JAVA, DB> =
        SqGenericConnSingleColUnion(this, unionAll, selects)
    // endregion


    // region Statements - other
    override fun <T : SqTable> insertInto(table: T): SqConnInsert<T> = SqGenericConnInsert(this, table)
    override fun <T : SqTable> update(table: T): SqConnUpdate<T> = SqGenericConnUpdate(this, table)
    override fun <T : SqTable> deleteFrom(table: T): SqConnDelete<T> = SqGenericConnDelete(this, table)
    // endregion
}
