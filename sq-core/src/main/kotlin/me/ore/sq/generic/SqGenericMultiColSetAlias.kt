package me.ore.sq.generic

import me.ore.sq.*
import me.ore.sq.SqMultiColSetAlias
import me.ore.sq.util.SqUtil


open class SqGenericMultiColSetAlias<ORIG: SqMultiColSet>(
    override val context: SqContext,
    override val original: ORIG,
    override val alias: String,
    override val safeAlias: String = SqUtil.makeIdentifierSafeIfNeeded(alias),
): SqMultiColSetAlias<ORIG> {
    companion object {
        val CONSTRUCTOR: SqMultiColSetAliasConstructor = object : SqMultiColSetAliasConstructor {
            override fun <ORIG : SqMultiColSet> createMultiColSetAlias(context: SqContext, original: ORIG, alias: String): SqMultiColSetAlias<ORIG> {
                return SqGenericMultiColSetAlias(context, original, alias)
            }
        }
    }


    override val columns: List<SqColSetAliasColumn<*, *>> by lazy(LazyThreadSafetyMode.NONE) {
        this.original.columns.map {
            this.context.colSetAliasColumn(this, it)
        }
    }
}
