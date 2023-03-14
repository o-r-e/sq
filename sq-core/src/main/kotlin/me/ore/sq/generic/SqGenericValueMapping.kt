package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericValueMapping<T: SqTable>(
    override val statement: SqTableWriteStatement<T>,
    override val map: MutableMap<SqColumn<*, *>, SqExpression<*, *>> = LinkedHashMap(),
): SqValueMapping<T>
