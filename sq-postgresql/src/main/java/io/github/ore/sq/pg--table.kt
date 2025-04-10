package io.github.ore.sq

import io.github.ore.sq.impl.SqPgDataTypesImpl
import java.util.Collections


open class SqPgTable(
    name: String,
    scheme: String? = null,
    override val types: SqPgDataTypes = SqPgDataTypesImpl.INSTANCE,
    commentAtStart: String? = null,
    commentAtEnd: String? = null,
): SqTable(name, scheme, types, commentAtStart, commentAtEnd) {
    private var mutableColumnHolder: SqPgTableColumnHolder? = null

    override fun createColumnHolder(): SqPgTableColumnHolder =
        SqPgTableColumnHolder(this)

    override val columnHolder: SqPgTableColumnHolder
        get() {
            return this.mutableColumnHolder ?: run {
                val result = this.createColumnHolder()
                this.mutableColumnHolder = result
                result
            }
        }
}


open class SqPgTableColumnHolder(
    override val owner: SqPgTable,
    mutableColumns: MutableList<SqColumn<*, *>> = ArrayList(),
    columns: List<SqColumn<*, *>> = Collections.unmodifiableList(mutableColumns),
): SqTableColumnHolder(owner, mutableColumns, columns) {
    override val types: SqPgDataTypes
        get() = this.owner.types
}
