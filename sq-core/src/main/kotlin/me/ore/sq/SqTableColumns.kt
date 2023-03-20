package me.ore.sq


private class SqTypeGetterTableColumn<JAVA: Any?, DB: Any>(
    table: SqTable,
    columnName: String,
    nullable: Boolean,
    protected val getType: (context: SqContext) -> SqType<JAVA & Any>,
): SqTableColumnBase<JAVA, DB>(table, columnName, nullable) {
    override val type: SqType<JAVA & Any>
        get() = this.getType(this.context)
}

private fun <JAVA: Any, DB: Any> SqTable.addColumn(columnName: String, getType: (context: SqContext) -> SqType<JAVA>): SqTableColumn<JAVA, DB> =
    this.addColumn(SqTypeGetterTableColumn(this, columnName, nullable = false, getType))
