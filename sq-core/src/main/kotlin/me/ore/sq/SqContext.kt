package me.ore.sq

import me.ore.sq.util.SqContextHolder
import java.sql.Connection


sealed class SqContext(data: SqContextData): AutoCloseable {
    companion object: SqContextHolder<SqContext>()


    // region Utils
    protected fun throwClosedError(): Nothing = error("Context is closed already")
    // endregion


    // region Data, lifecycle
    private var _data: SqContextData? = data
    val data: SqContextData
        get() = this._data ?: this.throwClosedError()
    operator fun <JAVA: Any> get(objectClass: Class<JAVA>): JAVA? = this.data.objectMap[objectClass]
    operator fun <JAVA: Any> get(objectClass: Class<JAVA>, defaultValue: JAVA): JAVA = this[objectClass] ?: defaultValue

    private var _columnIndexCache: MutableMap<SqColSet, MutableMap<SqColumn<*, *>, Int?>>? = null

    fun getColumnIndex(colSet: SqColSet, column: SqColumn<*, *>): Int? {
        val columnIndexCache = this._columnIndexCache ?: run {
            val columnIndexCache = HashMap<SqColSet, MutableMap<SqColumn<*, *>, Int?>>()
            this._columnIndexCache = columnIndexCache
            columnIndexCache
        }

        return columnIndexCache
            .computeIfAbsent(colSet) { HashMap() }
            .computeIfAbsent(column) {
                colSet.columns
                    .indexOf(column)
                    .takeUnless { it < 0 }
            }
    }

    open fun start(): SqContext = this.apply {
        SqContext.add(this)
    }

    override fun close() {
        SqContext.remove(this)
        this._data = null
        this._columnIndexCache?.let { columnIndexCache ->
            this._columnIndexCache = null
            columnIndexCache.values.forEach { it.clear() }
            columnIndexCache.clear()
        }
    }
    // endregion


    // region Implementation classes
    class Context(data: SqContextData): SqContext(data) {
        override fun start(): Context = this.apply { super.start() }
    }

    class ConnContext(data: SqContextData, connection: Connection): SqContext(data) {
        companion object: SqContextHolder<ConnContext>()


        private var _connection: Connection? = connection
        val connection: Connection
            get() = this._connection ?: this.throwClosedError()


        override fun start(): ConnContext = this.apply {
            super.start()
            ConnContext.add(this)
        }

        override fun close() {
            ConnContext.remove(this)
            super.close()
            this._connection = null
        }
    }
    // endregion
}
