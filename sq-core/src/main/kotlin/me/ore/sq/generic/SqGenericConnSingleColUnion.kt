package me.ore.sq.generic

import me.ore.sq.SqConnSingleColUnion
import me.ore.sq.SqConnectedContext
import me.ore.sq.SqSingleColSelect


open class SqGenericConnSingleColUnion<JAVA: Any?, DB: Any>(
    override val context: SqConnectedContext,
    unionAll: Boolean,
    selects: Iterable<SqSingleColSelect<JAVA, DB>>,
): SqGenericSingleColUnion<JAVA, DB>(context, unionAll, selects), SqConnSingleColUnion<JAVA, DB>
