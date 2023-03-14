package me.ore.sq.generic

import me.ore.sq.*


class SqGenericNull<JAVA: Any, DB: Any>(
    override val context: SqContext,
    override val type: SqType<JAVA>,
): SqNull<JAVA, DB> {
    override fun nullable(): SqGenericNull<JAVA, DB> = this
}
