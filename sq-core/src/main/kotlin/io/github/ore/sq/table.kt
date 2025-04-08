@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqDataTypesImpl
import io.github.ore.sq.impl.SqTableColumnImpl
import io.github.ore.sq.util.SqItemPartConfig
import java.util.*


open class SqTable(
    protected open val name: String,
    protected open val scheme: String? = null,
    open val types: SqDataTypes = SqDataTypesImpl.INSTANCE,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
) : SqColumnSource {
    override val isMultiline: Boolean
        get() = false


    // region Column holder
    private var mutableColumnHolder: SqTableColumnHolder? = null

    protected open fun createColumnHolder(): SqTableColumnHolder =
        SqTableColumnHolder(this)

    protected open val columnHolder: SqTableColumnHolder
        get() {
            return this.mutableColumnHolder ?: run {
                val result = this.createColumnHolder()
                this.mutableColumnHolder = result
                result
            }
        }

    override val columns: List<SqColumn<*, *>>
        get() = this.columnHolder.columns
    // endregion


    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?,
    ) {
        this.scheme?.let { scheme ->
            target.identifier(scheme).dot()
        }
        target.identifier(this.name)
    }
}


open class SqTableColumnHolder(
    protected open val owner: SqTable,
    protected open val mutableColumns: MutableList<SqColumn<*, *>> = ArrayList(),
    open val columns: List<SqColumn<*, *>> = Collections.unmodifiableList(mutableColumns),
) {
    open val types: SqDataTypes
        get() = this.owner.types


    protected open fun findByName(name: String): SqColumn<*, *>? =
        this.columns.find { it.name == name }

    protected open fun ensureNameNotBusy(name: String) {
        this.findByName(name)?.let {
            error("Column with name \"$name\" already exists - <$it>")
        }
    }

    fun <JAVA, DB: Any, T: SqColumn<JAVA, DB>> add(column: T): T {
        this.ensureNameNotBusy(column.name)
        this.mutableColumns.add(column)
        return column
    }

    fun <JAVA, DB: Any> add(reader: SqDataTypeReader<JAVA, DB>, writer: SqDataTypeWriter<JAVA & Any, DB>, name: String): SqTableColumn<JAVA, DB> {
        this.ensureNameNotBusy(name)
        val result = SqTableColumnImpl(this.owner, name, reader, writer)
        this.mutableColumns.add(result)
        return result
    }

    @JvmName("add__notNull")
    fun <JAVA: Any, DB: Any> add(typePack: SqDataTypePack<JAVA, DB>, name: String, nullFlag: Any): SqTableColumn<JAVA, DB> =
        this.add(typePack.notNullReader, typePack.writer, name)

    @JvmName("add__nullable")
    fun <JAVA: Any, DB: Any> add(typePack: SqDataTypePack<JAVA, DB>, name: String, nullFlag: Any?): SqTableColumn<JAVA?, DB> =
        this.add(typePack.nullableReader, typePack.writer, name)
}


interface SqTableColumn<JAVA, DB: Any>: SqOwnedColumn<JAVA, DB> {
    val writer: SqDataTypeWriter<JAVA & Any, DB>
}
