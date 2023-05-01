package me.ore.sq

import me.ore.sq.util.SqContextHolder
import java.sql.Connection


/**
 * SQ context
 *
 * Used to create SQL elements and store various cached data (for example, column indexes in column sets)
 *
 * @param config context configuration
 */
sealed class SqContext(config: SqContextConfig): AutoCloseable {
    companion object: SqContextHolder<SqContext>()


    // region Utils
    /**
     * @throws IllegalStateException always throws error with text like "Context is closed already"
     */
    protected fun throwClosedError(): Nothing = error("Context is closed already")
    // endregion


    // region Data, lifecycle
    /**
     * Configuration, mutable backing field for [config]
     *
     * Set to `null` when [close] is executed
     */
    private var _config: SqContextConfig? = config

    /**
     * Configuration of current context
     *
     * @throws IllegalStateException if current context is closed already
     */
    val config: SqContextConfig
        get() = this._config ?: this.throwClosedError()

    /**
     * Returns an object of the requested class
     *
     * Uses [config].[objectHolder][SqObjectHolder].[get][SqObjectHolder.get]
     *
     * @param objectClass the class by which the object is to be found;
     * the returned object must be an instance of this class or a class that inherits from this class
     *
     * @return an object of class [objectClass] if it exists; otherwise - `null`
     */
    operator fun <JAVA: Any> get(objectClass: Class<JAVA>): JAVA? = this.config.objectHolder[objectClass]

    /**
     * Returns an object of the requested class or default value
     *
     * Uses [config].[objectHolder][SqObjectHolder].[get][SqObjectHolder.get]
     *
     * @param objectClass the class by which the object is to be found;
     * the returned object must be an instance of this class or a class that inherits from this class
     * @param defaultValue the value to be returned if the required object is not found
     *
     * @return an object of class [objectClass] if it exists; otherwise - [defaultValue]
     */
    operator fun <JAVA: Any> get(objectClass: Class<JAVA>, defaultValue: JAVA): JAVA = this[objectClass] ?: defaultValue


    /** Map (cache) of column indices in column sets */
    private var _columnIndexCache: MutableMap<SqColSet, MutableMap<SqColumn<*, *>, Int?>>? = null

    /**
     * Getting the index of a column in a column set; numbering starts from `0`
     *
     * @param colSet set of columns to search for [column]
     * @param column column to get index
     *
     * @return the index of [column] in the set [colSet], if the column belongs to this set; otherwise - `null`
     */
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

    /**
     * "Starts" current context
     *
     * When called, adds the current context to the current thread's "queue" of contexts
     */
    open fun start(): SqContext = this.apply {
        SqContext.add(this)
    }

    /**
     * Closes current context.
     *
     * Removes miscellaneous data from the current context,
     * removes the current context from the current thread's "queue" of contexts.
     *
     * Using a closed context will result in errors.
     */
    override fun close() {
        SqContext.remove(this)
        this._config = null
        this._columnIndexCache?.let { columnIndexCache ->
            this._columnIndexCache = null
            columnIndexCache.values.forEach { it.clear() }
            columnIndexCache.clear()
        }
    }
    // endregion


    // region Implementation classes
    /**
     * Simple implementation of the context class
     *
     * @param config context configuration
     *
     * @see ConnContext
     */
    class Context(config: SqContextConfig): SqContext(config) {
        override fun start(): Context = this.apply { super.start() }
    }

    /**
     * Context class implementation with access to [Connection]
     *
     * @param config context configuration
     * @param connection database connection
     *
     * @see Context
     */
    class ConnContext(config: SqContextConfig, connection: Connection): SqContext(config) {
        companion object: SqContextHolder<ConnContext>()


        /**
         * Database connection, mutable backing field for [connection]
         *
         * Set to `null` when [close] is executed
         */
        private var _connection: Connection? = connection

        /**
         * Database connection of current context
         *
         * @throws IllegalStateException if current context is closed already
         */
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
