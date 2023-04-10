package me.ore.sq

import me.ore.sq.util.SqUtil
import java.util.*


open class SqTable(
    @Suppress("MemberVisibilityCanBePrivate")
    val tableName: String,
    @Suppress("MemberVisibilityCanBePrivate")
    val schemeName: String? = null,
): SqMultiColSet {
    override fun toString(): String = buildString {
        val self = this@SqTable

        self.schemeName?.takeIf { it.isNotBlank() }?.let { schemeName ->
            this.append(schemeName).append('.')
        }
        this.append(self.tableName)
    }


    override val context: SqContext
        get() = SqContext.CONTEXT

    override val definitionItem: SqItem
        get() = this

    override fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean) {
        val schemeName = this.schemeName
        val tableName = this.tableName

        val tableNameSafe = SqUtil.isIdentifierSafe(tableName)
        val schemeNameSafe: Boolean
        val completeTableName: String
        (schemeName?.takeIf { it.isNotBlank() }).let { scheme ->
            if (scheme == null) {
                schemeNameSafe = true
                completeTableName = tableName
            } else {
                schemeNameSafe = SqUtil.isIdentifierSafe(scheme)
                completeTableName = "$scheme.$tableName"
            }
        }

        val preparedTableName = if (schemeNameSafe && tableNameSafe) {
            completeTableName
        } else {
            SqUtil.makeIdentifierSafe(completeTableName)
        }

        target.add(preparedTableName, spaced = spaceAllowed)
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {}


    override fun createColumnNotFoundException(column: SqColumn<*, *>): Exception {
        return IllegalArgumentException("Cannot find column $column in table $this")
    }


    protected open fun createColumnList(): MutableList<SqColumn<*, *>> = ArrayList()

    @Suppress("MemberVisibilityCanBePrivate", "PropertyName")
    protected val _columns: MutableList<SqColumn<*, *>> = this.createColumnList()

    override val columns: List<SqColumn<*, *>> = Collections.unmodifiableList(this._columns)

    fun <T: SqColumn<*, *>> addColumn(column: T): T {
        this._columns.add(column)
        return column
    }
}
