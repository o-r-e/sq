package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericColumnValueMapping<T: SqTable>(
    override val statement: SqTableDataWriteStatement<T>,
    override val map: MutableMap<SqTableColumn<*, *>, SqExpression<*, *>> = LinkedHashMap(),
): SqColumnValueMapping<T>
