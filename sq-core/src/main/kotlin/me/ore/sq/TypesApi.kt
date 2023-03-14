package me.ore.sq

import java.sql.Connection
import java.sql.JDBCType
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLType
import kotlin.math.max


abstract class SqType<JAVA: Any> {
    abstract val valueClass: Class<JAVA>
    abstract val sqlType: SQLType
    open val sqlVendorType: Int
        get() = this.sqlType.vendorTypeNumber ?: throw IllegalStateException("\"vendorTypeNumber\" is null in SQL type ${this.sqlType}")
    open val dbTypeName: String?
        get() = this.sqlType.name


    open fun getTypeInfo(): String {
        val self = this
        return buildString {
            this
                .append(self.javaClass.name)
                .append("[")
                .append(self.valueClass.name)
                .append(" :: ")
                .append(self.sqlType)
                .append("]")
        }
    }


    open fun readNotNull(source: ResultSet, columnIndex: Int): JAVA {
        return this.readNullable(source, columnIndex)
            ?: throw IllegalStateException("Got NULL value for NOT-NULL column with index #$columnIndex")
    }

    open fun readNullable(source: ResultSet, columnIndex: Int): JAVA? {
        val result = this.readImpl(source, columnIndex)
        return if (source.wasNull()) {
            null
        } else {
            result
        }
    }

    protected abstract fun readImpl(source: ResultSet, columnIndex: Int): JAVA?


    open fun write(target: PreparedStatement, parameterIndex: Int, value: JAVA?) {
        if (value == null) {
            this.writeNull(target, parameterIndex)
        } else {
            this.writeNotNull(target, parameterIndex, value)
        }
    }

    protected abstract fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: JAVA)

    protected open fun writeNull(target: PreparedStatement, parameterIndex: Int) {
        this.dbTypeName.let { dbTypeName ->
            if (dbTypeName == null) {
                target.setNull(parameterIndex, this.sqlVendorType)
            } else {
                target.setNull(parameterIndex, this.sqlVendorType, dbTypeName)
            }
        }
    }


    open fun prepareValueForComment(value: JAVA?): String {
        return if (value == null) {
            "<NULL>"
        } else {
            this.prepareNotNullValueForComment(value)
        }
    }

    protected abstract fun prepareNotNullValueForComment(value: JAVA): String


    @Suppress("NOTHING_TO_INLINE")
    inline fun <OPT: JAVA?> sqCast(): SqType<OPT & Any> = SqUtil.uncheckedCast(this)
}

abstract class SqArrayType<JAVA: Any>: SqType<Array<JAVA?>>() {
    companion object {
        const val COLUMN_INDEX__ITEM_INDEX = 1
        const val COLUMN_INDEX__VALUE = 2
    }


    abstract val arrayItemClass: Class<JAVA>
    abstract val exampleArray: Array<JAVA?>

    override val valueClass: Class<Array<JAVA?>>
        get() = this.exampleArray.javaClass

    override val sqlType: SQLType
        get() = JDBCType.ARRAY


    override fun readImpl(source: ResultSet, columnIndex: Int): Array<JAVA?>? {
        return source.getArray(columnIndex)?.let { jdbcArray ->
            val result = ArrayList<JAVA?>()

            try {
                jdbcArray.resultSet.use { arrayResultSet ->
                    while (arrayResultSet.next()) {
                        val item = this.readArrayItem(arrayResultSet, COLUMN_INDEX__ITEM_INDEX, COLUMN_INDEX__VALUE)
                        result.add(item)
                    }
                }
            } finally {
                jdbcArray.free()
            }

            result.toArray(this.exampleArray)
        }
    }

    @Suppress("SameParameterValue")
    protected open fun readArrayItem(arrayResultSet: ResultSet, itemIndexColumnIndex: Int, valueColumnIndex: Int): JAVA? {
        val result = this.readArrayItemImpl(arrayResultSet, itemIndexColumnIndex, valueColumnIndex)
        return if (arrayResultSet.wasNull()) {
            null
        } else {
            result
        }
    }

    protected abstract fun readArrayItemImpl(arrayResultSet: ResultSet, itemIndexColumnIndex: Int, valueColumnIndex: Int): JAVA?


    override fun writeNotNull(target: PreparedStatement, parameterIndex: Int, value: Array<JAVA?>) {
        val array = this.createJdbcArray(target, value)
        target.setArray(parameterIndex, array)
    }

    protected open fun createJdbcArray(target: PreparedStatement, items: Array<JAVA?>): java.sql.Array = this.createJdbcArray(target.connection, items)
    protected open fun createJdbcArray(connection: Connection, items: Array<JAVA?>): java.sql.Array = connection.createArrayOf(this.dbTypeName, items)


    override fun prepareNotNullValueForComment(value: Array<JAVA?>): String {
        val hiddenCount: Int
        val lastIndex: Int

        value.size.let { size ->
            hiddenCount = max(size - 5, 0)
            lastIndex = if (hiddenCount > 0) {
                4
            } else {
                size - 1
            }
        }

        val self = this
        return buildString {
            this.append("[")
            for (i in 0 .. lastIndex) {
                if (i > 0) this.append(", ")
                val item = value[i]
                val itemString = self.prepareItemForComment(item)
                this.append(itemString)
            }
            if (hiddenCount > 0) {
                this.append(", ... (").append(hiddenCount).append(" more)")
            }
            this.append("]")
        }
    }

    protected open fun prepareItemForComment(item: JAVA?): String {
        return if (item == null) {
            "<NULL>"
        } else {
            this.prepareNotNullItemForComment(item)
        }
    }

    protected abstract fun prepareNotNullItemForComment(item: JAVA): String
}
