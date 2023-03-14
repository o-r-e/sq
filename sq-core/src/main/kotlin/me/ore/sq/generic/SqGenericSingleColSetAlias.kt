package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericSingleColSetAlias<JAVA: Any?, DB: Any, ORIG: SqSingleColSet<JAVA?, DB>>(
    override val context: SqContext,
    original: ORIG,
    override val alias: String,
): SqSingleColSetAlias<JAVA, DB, ORIG> {
    // region Nullable
    override fun nullable(): SqGenericSingleColSetAlias<JAVA?, DB, ORIG> {
        this.column.nullable()
        return SqUtil.uncheckedCast(this)
    }

    override val nullable: Boolean
        get() = this.column.nullable
    // endregion


    @Suppress("CanBePrimaryConstructorProperty")
    override val original: ORIG = original

    override fun createColumnNotFoundException(column: SqColumn<*, *>): Exception {
        return IllegalArgumentException("Cannot find column $column in \"single col set alias\" $this")
    }


    override val column: SqColSetAliasColumn<JAVA, DB> by lazy(LazyThreadSafetyMode.NONE) {
        SqUtil.uncheckedCast(this.context.colSetAliasColumn(this, original.column))
    }

    override val columns: List<SqColumn<*, *>> by lazy(LazyThreadSafetyMode.NONE) {
        listOf(this.column)
    }
}
