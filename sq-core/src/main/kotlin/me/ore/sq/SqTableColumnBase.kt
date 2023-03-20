package me.ore.sq


abstract class SqTableColumnBase<JAVA: Any?, DB: Any>(
    override val table: SqTable,
    override val columnName: String,
    nullable: Boolean,
): SqTableColumn<JAVA, DB> {
    // region Nullable
    private var _nullable: Boolean = nullable

    override val nullable: Boolean
        get() = this._nullable

    override fun nullable(): SqTableColumnBase<JAVA?, DB> {
        this._nullable = true
        return SqUtil.uncheckedCast(this)
    }
    // endregion
}
