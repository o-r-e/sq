package me.ore.sq


open class SqTableColumn<JAVA: Any?, DB: Any>(
    val table: SqTable,
    override val columnName: String,
    override val type: SqType<JAVA & Any>,
    nullable: Boolean,
): SqColumn<JAVA, DB> {
    override val context: SqContext
        get() = this.table.context

    // region Nullable
    private var _nullable: Boolean = nullable

    override val nullable: Boolean
        get() = this._nullable

    override fun nullable(): SqTableColumn<JAVA?, DB> {
        this._nullable = true
        return SqUtil.uncheckedCast(this)
    }
    // endregion

    override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
        val columnName = SqUtil.makeIdentifierSafeIfNeeded(this.columnName)
        target.add(columnName, spaced = spaceAllowed)
    }

    override fun parameters(): List<SqParameter<*, *>>? = null
}

fun <JAVA: Any, DB: Any> SqTable.addColumn(columnName: String, type: SqType<JAVA>): SqTableColumn<JAVA, DB> =
    this.addColumn(SqTableColumn(this, columnName, type, nullable = false))
