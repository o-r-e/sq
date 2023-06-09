package me.ore.sq.generic

import me.ore.sq.*
import me.ore.sq.util.SqUtil


open class SqGenericSingleColSetAlias<JAVA: Any?, DB: Any, ORIG: SqSingleColSet<JAVA, DB>>(
    override val context: SqContext,
    override val original: ORIG,
    override val alias: String,
    override val safeAlias: String = SqUtil.makeIdentifierSafeIfNeeded(alias),
): SqSingleColSetAlias<JAVA, DB, ORIG> {
    companion object {
        val CONSTRUCTOR: SqSingleColSetAliasConstructor = object : SqSingleColSetAliasConstructor {
            override fun <JAVA, DB : Any, ORIG : SqSingleColSet<JAVA, DB>> createSingleColSetAlias(context: SqContext, original: ORIG, alias: String): SqSingleColSetAlias<JAVA, DB, ORIG> {
                return SqGenericSingleColSetAlias(context, original, alias)
            }
        }
    }


    protected open var _definitionItem: SqAliasDefinition? = null

    protected open fun createAliasDefinition(): SqAliasDefinition =
        SqGenericAliasDefinition(this.context, this.original, this)

    override val definitionItem: SqAliasDefinition
        get() {
            return this._definitionItem ?: run {
                val result = this.createAliasDefinition()
                this._definitionItem = result
                result
            }
        }


    override val column: SqColSetAliasColumn<JAVA, DB> by lazy(LazyThreadSafetyMode.NONE) {
        this.context.colSetAliasColumn(this, this.original.column)
    }
    override val columns: List<SqColumn<*, *>> by lazy(LazyThreadSafetyMode.NONE) { listOf(this.column) }
}
