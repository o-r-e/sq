package me.ore.sq.generic

import me.ore.sq.*
import java.util.Collections


open class SqGenericUpdate<T: SqTable>(
    override val context: SqContext,
    override val table: T,
): SqUpdate<T> {
    // region Column value map
    protected open fun createColumnValueMap(): MutableMap<SqColumn<*, *>, SqExpression<*, *>> = LinkedHashMap()

    @Suppress("PropertyName")
    protected open val _columnValueMap: MutableMap<SqColumn<*, *>, SqExpression<*, *>> by lazy(LazyThreadSafetyMode.NONE) { this.createColumnValueMap() }

    override val set: Map<SqColumn<*, *>, SqExpression<*, *>> by lazy(LazyThreadSafetyMode.NONE) { Collections.unmodifiableMap(this._columnValueMap) }

    override fun set(columnValueMap: Map<SqColumn<*, *>, SqExpression<*, *>>): SqGenericUpdate<T> = this.apply {
        this._columnValueMap.clear()
        this._columnValueMap.putAll(columnValueMap)
    }
    // endregion


    // region Where
    @Suppress("MemberVisibilityCanBePrivate", "PropertyName")
    protected var _where: SqExpression<*, Boolean>? = null

    override val where: SqExpression<*, Boolean>?
        get() = this._where

    override fun where(condition: SqExpression<*, Boolean>?): SqGenericUpdate<T> = this.apply {
        this._where = condition
    }
    // endregion


    override fun createValueMapping(): SqValueMapping<T> = SqGenericValueMapping(this)

    override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
        val internalSpaceAllowed = if (asTextPart) {
            target.add("(", spaced = spaceAllowed)
            false
        } else {
            spaceAllowed
        }

        target.add("UPDATE ", spaced = internalSpaceAllowed)
        this.table.appendTo(target, asTextPart = true, spaceAllowed = false)
        target.add("SET", spaced = true)

        var first = true
        this.set.forEach { (column, value) ->
            if (first) {
                first = false
            } else {
                target.add(",", spaced = false)
            }
            target.ls()
            column.appendTo(target, asTextPart = true, spaceAllowed = false)
            target.add(" =", spaced = false)
            value.appendTo(target, asTextPart = true, spaceAllowed = true)
        }

        this.where?.let { where ->
            target.ls().add("WHERE ", spaced = false)
            where.appendTo(target, asTextPart = true, spaceAllowed = false)
        }

        if (asTextPart) {
            target.add(")", spaced = false)
        }
    }

    override fun parameters(): List<SqParameter<*, *>>? {
        var result: ArrayList<SqParameter<*, *>>? = null

        this.set.values.forEach { value ->
            value.parameters()?.let { parameters ->
                if (parameters.isNotEmpty()) {
                    val tmpResult = result ?: run {
                        val tmpResult = ArrayList<SqParameter<*, *>>()
                        result = tmpResult
                        tmpResult
                    }

                    tmpResult.addAll(parameters)
                }
            }
        }

        return result
    }
}
