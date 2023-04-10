package me.ore.sq.generic

import me.ore.sq.SqItem
import me.ore.sq.SqTable
import me.ore.sq.SqTableColumn
import me.ore.sq.SqType
import me.ore.sq.util.SqUtil


open class SqGenericTableColumn<JAVA: Any?, DB: Any>(
    override val table: SqTable,
    override val type: SqType<JAVA, DB>,
    override val columnName: String,
    override val safeColumnName: String = SqUtil.makeIdentifierSafeIfNeeded(columnName),
): SqTableColumn<JAVA, DB> {
    override val definitionItem: SqItem
        get() = this
}
