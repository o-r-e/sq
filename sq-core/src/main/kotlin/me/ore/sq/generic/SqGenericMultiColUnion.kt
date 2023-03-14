package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericMultiColUnion(
    override val context: SqContext,
    override val unionAll: Boolean,
    selects: Iterable<SqSelect>,
): SqMultiColUnion {
    override val selects: List<SqSelect> = selects.toList()
}
