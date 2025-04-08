package io.github.ore.sq

import io.github.ore.sq.impl.SqH2DataTypesImpl
import java.util.Collections


open class SqH2Table(
    name: String,
    scheme: String? = null,
    override val types: SqH2DataTypes = SqH2DataTypesImpl.INSTANCE,
    commentAtStart: String? = null,
    commentAtEnd: String? = null,
): SqTable(name, scheme, types, commentAtStart, commentAtEnd) {
    private var mutableColumnHolder: SqH2TableColumnHolder? = null

    override fun createColumnHolder(): SqH2TableColumnHolder =
        SqH2TableColumnHolder(this)

    override val columnHolder: SqH2TableColumnHolder
        get() {
            return this.mutableColumnHolder ?: run {
                val result = this.createColumnHolder()
                this.mutableColumnHolder = result
                result
            }
        }
}


open class SqH2TableColumnHolder(
    override val owner: SqH2Table,
    mutableColumns: MutableList<SqColumn<*, *>> = ArrayList(),
    columns: List<SqColumn<*, *>> = Collections.unmodifiableList(mutableColumns),
): SqTableColumnHolder(owner, mutableColumns, columns) {
    override val types: SqH2DataTypes
        get() = this.owner.types
}
