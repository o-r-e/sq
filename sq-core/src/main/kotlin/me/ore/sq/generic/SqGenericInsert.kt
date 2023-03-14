package me.ore.sq.generic

import me.ore.sq.*
import java.util.Collections


open class SqGenericInsert<T: SqTable>(
    override val context: SqContext,
    override val table: T,
): SqInsert<T> {
    // region Columns
    protected open fun createDefinedColumnList(): MutableList<SqColumn<*, *>> = ArrayList()

    protected open var definedColumns: MutableList<SqColumn<*, *>>? = null

    override val columns: List<SqColumn<*, *>>
        get() = this.definedColumns?.takeIf { it.isNotEmpty() } ?: this.table.columns

    override fun columns(columns: Iterable<SqColumn<*, *>>?): SqGenericInsert<T> = this.apply {
        val columnList = columns?.toList()

        if (columnList.isNullOrEmpty()) {
            this.definedColumns?.clear()
            return@apply
        }

        val definedColumns = this.definedColumns
            ?.also { it.clear() }
            ?: run {
                val tmpDefinedColumns = this.createDefinedColumnList()
                this.definedColumns = tmpDefinedColumns
                tmpDefinedColumns
            }
        definedColumns.addAll(columnList)
    }
    // endregion


    // region Values
    protected open fun createValueList(): MutableList<SqExpression<*, *>> = ArrayList()

    @Suppress("PropertyName")
    protected open var _values: MutableList<SqExpression<*, *>>? = null

    override val values: List<SqExpression<*, *>>?
        get() = this._values?.let { Collections.unmodifiableList(it) }

    override fun values(values: Iterable<SqExpression<*, *>>?): SqGenericInsert<T> = this.apply {
        val valueList = values?.toList()
        if (valueList.isNullOrEmpty()) {
            this._values?.clear()
            return@apply
        }

        this.select(null)

        val localValues = this._values
            ?.also { it.clear() }
            ?: run {
                val tmpValues = this.createValueList()
                this._values = tmpValues
                tmpValues
            }
        localValues.addAll(valueList)
    }
    // endregion


    // region Select
    @Suppress("PropertyName")
    protected open var _select: SqReadStatement? = null

    override val select: SqReadStatement?
        get() = this._select

    override fun select(select: SqReadStatement?): SqGenericInsert<T> = this.apply {
        if (select == null) {
            this._select = null
            return@apply
        }

        this.values(null)
        this._select = select
    }
    // endregion


    override fun createValueMapping(): SqValueMapping<T> = SqGenericValueMapping(this)


    override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
        val textSpaceAllowed = if (asTextPart) {
            target.add("(", spaced = spaceAllowed)
            false
        } else {
            spaceAllowed
        }

        target.add("INSERT INTO", spaced = textSpaceAllowed)
        this.table.appendTo(target, asTextPart = true, spaceAllowed = true)

        target.ls()
        target.add("(", spaced = false)
        this.columns.forEachIndexed { index, column ->
            val columnSpaceAllowed = if (index == 0) {
                false
            } else {
                target.comma()
                true
            }
            column.appendTo(target, asTextPart = true, spaceAllowed = columnSpaceAllowed)
        }
        target.add(")", spaced = false)

        this.values?.takeIf { it.isNotEmpty() }?.let { values ->
            target.ls()
            target.add("VALUES (", spaced = false)
            values.forEachIndexed { index, value ->
                val valueSpaceAllowed = if (index == 0) {
                    false
                } else {
                    target.comma()
                    true
                }
                value.appendTo(target, asTextPart = true, spaceAllowed = valueSpaceAllowed)
            }
            target.add(")", spaced = false)
        }

        this.select?.let { select ->
            target.ls()
            select.appendTo(target, asTextPart = false, spaceAllowed = true)
        }

        if (asTextPart) {
            target.add(")", spaced = true)
        }
    }

    override fun parameters(): List<SqParameter<*, *>>? {
        var result: MutableList<SqParameter<*, *>>? = null

        this.values?.takeIf { it.isNotEmpty() }?.let { values ->
            val tmpResult = ArrayList<SqParameter<*, *>>()
            result = tmpResult
            values.forEach { value ->
                value.parameters()?.let { parameters ->
                    tmpResult.addAll(parameters)
                }
            }
        }

        this.select?.let { select ->
            select.parameters()?.let { parameters ->
                val tmpResult = result ?: run {
                    val tmpResult = ArrayList<SqParameter<*, *>>()
                    result = tmpResult
                    tmpResult
                }
                tmpResult.addAll(parameters)
            }
        }

        return result?.takeIf { it.isNotEmpty() }
    }
}
