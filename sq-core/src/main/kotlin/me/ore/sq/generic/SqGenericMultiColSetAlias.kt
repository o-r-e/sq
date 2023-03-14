package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericMultiColSetAlias<ORIG: SqMultiColSet>(
    override val context: SqContext,
    original: ORIG,
    override val alias: String,
): SqMultiColSetAlias<ORIG> {
    @Suppress("CanBePrimaryConstructorProperty")
    override val original: ORIG = original

    override fun createColumnNotFoundException(column: SqColumn<*, *>): Exception {
        return IllegalArgumentException("Cannot find column $column in \"multi column set alias\" $this")
    }


    protected open fun createColumns(): List<SqColSetAliasColumn<*, *>> {
        return this.context.let { context ->
            this.original.columns.map { originalColumn ->
                context.colSetAliasColumn(this, originalColumn)
            }
        }
    }

    override val columns: List<SqColSetAliasColumn<*, *>> by lazy(LazyThreadSafetyMode.NONE) { this.createColumns() }
}
