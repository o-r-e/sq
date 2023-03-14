package me.ore.sq.generic

import me.ore.sq.SqContext
import me.ore.sq.SqParameter
import me.ore.sq.SqType


open class SqGenericParameter<JAVA: Any?, DB: Any>(
    override val context: SqContext,
    override val type: SqType<JAVA & Any>,
    override val nullable: Boolean,
    override val value: JAVA,
): SqParameter<JAVA, DB>
