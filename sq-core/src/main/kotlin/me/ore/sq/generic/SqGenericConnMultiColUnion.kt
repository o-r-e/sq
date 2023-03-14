package me.ore.sq.generic

import me.ore.sq.SqConnMultiColUnion
import me.ore.sq.SqConnectedContext
import me.ore.sq.SqSelect


open class SqGenericConnMultiColUnion(
    override val context: SqConnectedContext,
    unionAll: Boolean,
    selects: Iterable<SqSelect>,
): SqGenericMultiColUnion(context, unionAll, selects), SqConnMultiColUnion
