package me.ore.sq

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement


// region Utils
/** Holder of objects with binding to their classes */
interface SqObjectHolder {
    companion object {
        /** Empty holder with no objects inside; any call of [get] will return `null` */
        val EMPTY: SqObjectHolder = object : SqObjectHolder {
            override fun <T : Any> get(requiredClass: Class<T>): T? = null
        }
    }

    /**
     * @param requiredClass the class to get the object for
     *
     * @return an instance of class [requiredClass] if current holder has such object; `null` otherwise
     */
    operator fun <T: Any> get(requiredClass: Class<T>): T?
}


/**
 * Reader of data from columns in [ResultSet]
 *
 * @param JAVA type of data returned after reading
 */
interface SqValueReader<JAVA: Any> {
    /**
     * Reading data
     *
     * @param source query result, source of data
     * @param columnIndex the index of the column whose data is to be read; index of first column is 1
     *
     * @return data read and converted to [JAVA] type; if the column contains "NULL", then `null` will be returned
     */
    fun readNullable(source: ResultSet, columnIndex: Int): JAVA?

    /**
     * Reading data
     *
     * @param source query result, source of data
     * @param columnIndex the index of the column whose data is to be read; index of first column is 1
     *
     * @return data read and converted to [JAVA] type
     *
     * @throws IllegalStateException if column contains "NULL"
     */
    fun readNotNull(source: ResultSet, columnIndex: Int): JAVA {
        return this.readNullable(source, columnIndex)
            ?: error("Column with index $columnIndex has NULL value")
    }
}

/**
 * Writer which fills in the parameters in the [PreparedStatement]
 *
 * @param JAVA data type accepted for writing
 */
interface SqValueWriter<JAVA: Any> {
    /**
     * Converting [value] and storing it as a parameter in [target]
     *
     * @param target statement in which the parameter will be stored
     * @param parameterIndex index of the saved parameter; the index of the first parameter is 1
     * @param value the value to be stored as a parameter
     */
    fun write(target: PreparedStatement, parameterIndex: Int, value: JAVA?)

    /**
     * Convert value to comment content to be added to SQL text
     * (usually called if [SqContextConfig.printParameterValues] is `true`)
     *
     * @param value value to be converted
     *
     * @return [value] as comment content
     */
    fun valueToComment(value: JAVA?): String
}


/**
 * A text buffer used to store and append the SQL query text
 *
 * To get the text of the request, call its method [toString()][SqTextBuffer.toString]
 */
interface SqTextBuffer {
    /** Adds line separator at the end of this buffer */
    fun addLineSeparator()

    /**
     * Adds text at the end of this buffer
     *
     * @param text text to be added
     * @param spaced if equal to `true` and there is no whitespace at the end of this buffer,
     * then a whitespace will be added before [text] (usually a space character)
     */
    fun addText(text: String, spaced: Boolean)

    /** Removes any data in this buffer */
    fun clearData()
}

/** Constructor for [SqTextBuffer] objects */
interface SqTextBufferConstructor {
    /**
     * @param context context, which can be stored in created text buffer
     *
     * @return new text buffer
     */
    fun createTextBuffer(context: SqContext): SqTextBuffer
}


/**
 * Mapping, which contains "table column + value for this column" pairs
 *
 * Used when creating queries for changing table data ([SqInsert], [SqUpdate])
 *
 * @param T the table whose data will be inserted/updated
 */
interface SqColumnValueMapping<T: SqTable> {
    /** Statement, which will insert/update data */
    val statement: SqTableDataWriteStatement<T>

    /** Context, associated with current object */
    val context: SqContext
        get() = this.statement.context

    /** Table whose data will be inserted/updated */
    val table: T
        get() = this.statement.table

    /** "column + value" pairs */
    val map: MutableMap<SqTableColumn<*, *>, SqExpression<*, *>>


    /**
     * Saving "column + value" pair
     *
     * @param column column
     * @param value value
     *
     * @return current mapping object
     */
    operator fun <DB: Any> set(column: SqTableColumn<*, DB>, value: SqExpression<*, DB>): SqColumnValueMapping<T> = this.apply {
        this.map[column] = value
    }

    /**
     * Saving "column + value" pair
     *
     * [value] will be converted to [SqParameter] using [SqContext.param] and type of [column]
     *
     * @param column column
     * @param value value
     *
     * @return current mapping object
     */
    operator fun <JAVA: Any?, DB: Any> set(column: SqTableColumn<JAVA, DB>, value: JAVA): SqColumnValueMapping<T> = this.apply {
        val param = this.context.param(column.type, value)
        this[column] = param
    }

    /** Removes all data in current mapping */
    fun clearData() { this.map.clear() }
}
// endregion


// region Base items
/** Part of SQL */
interface SqItem {
    /** Context, associated with current object */
    val context: SqContext

    /**
     * Part of SQL, which holds definition of current object
     *
     * For example, if current object is alias "t", it can have definition like "my_table as t".
     * This will be used in "from" part of "select" -
     * definition item (result - "from my_table as t") will be used instead of current alias (result - "from t")
     */
    val definitionItem: SqItem

    /**
     * Appends text of current item to text buffer
     *
     * @param target target text buffer
     * @param asPart if `true`, when current item must be added as part of another expression;
     * for example, "(" and ")" must be added at start and end of added text
     * @param spaced if `true`, when space must be added before rest text
     */
    fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean)

    /**
     * Adds all parameters from this item to parameter list (collecting parameters)
     *
     * @param target target list to add parameters to
     */
    fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>)

    /**
     * Builds SQL of current item
     *
     * _Note: temporary [SqTextBuffer] will be created_
     *
     * @return SQL of current item
     */
    fun sql(): String {
        return this.context.writer()
            .let { writer ->
                try {
                    this.appendSqlTo(writer, asPart = false, spaced = false)
                    writer.toString()
                } finally {
                    writer.clear()
                }
            }
    }


    /**
     * Builds parameter list of current item
     *
     * _Note: new [List] will be created_
     *
     * @return list of parameters which stored in current item
     */
    fun parameters(): List<SqParameter<*, *>> = buildList { this@SqItem.appendParametersTo(this) }

    /**
     * @param target statement, which will store the parameters of the current element
     * @return passed [target]
     */
    fun <T: PreparedStatement> setParametersTo(target: T): T = this.parameters().setTo(target)

    /**
     * Creates statement using [sql] and saves self [parameters] in it
     *
     * @param connection connection for which [PreparedStatement] will be created
     *
     * @return new [PreparedStatement]
     */
    fun prepareStatement(connection: Connection): PreparedStatement {
        val result = connection.prepareStatement(this.sql())
        this.setParametersTo(result)
        return result
    }
}

/** [SqItem] associated with [SqContext.ConnContext] */
interface SqConnItem: SqItem {
    /** Context, associated with current object */
    override val context: SqContext.ConnContext

    /** JDBC connection; usually - connection of [context] */
    val connection: Connection
        get() = this.context.connection

    /**
     * For own [connection] creates statement using [sql] and saves self [parameters] in it
     *
     * @return new [PreparedStatement]
     */
    fun prepareStatement(): PreparedStatement = this.prepareStatement(this.connection)
}


/**
 * An expression (column, value, function) that provides data
 *
 * @param JAVA "data type in kotlin"; parameter values and data read from the database will have this type
 * @param DB "data type in DB"; used when creating comparison operations -
 * so that DB then compares values of suitable types
 */
interface SqExpression<JAVA: Any?, DB: Any>: SqItem {
    /**
     * The data type of the current expression
     *
     * Used when saving query parameter values (usually in [Statement]) and when reading data from query result
     */
    val type: SqType<JAVA, DB>

    /**
     * Reading a value from a query result
     *
     * @param source query result
     * @param columnIndex the index of the column to get the value from; first column index is 1
     *
     * @return read value
     *
     * @throws Exception various errors, for example:
     * [source] is closed,
     * [source] has no columns for [columnIndex],
     * error reading data from column
     */
    fun read(source: ResultSet, columnIndex: Int): JAVA = this.type.read(source, columnIndex)
}


/**
 * SQL NULL item
 *
 * **Some implementations may not contain [type],
 * which will cause an error to be thrown when trying to read data from the query results**
 *
 * @param JAVA "data type in kotlin"; parameter values and data read from the database will have this type
 * @param DB "data type in DB"; used when creating comparison operations -
 * so that DB then compares values of suitable types
 */
interface SqNull<JAVA: Any, DB: Any>: SqExpression<JAVA?, DB> {
    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) { target.add("NULL", spaced = spaced) }
    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {}
}

/** Constructor which creates "safe" [SqNull] objects with [type][SqNull.type] */
interface SqNullConstructor {
    /**
     * @param context the context with which the created [SqNull] will be associated
     * @param type data type
     *
     * @return new [SqNull] ("safe")
     */
    fun <JAVA: Any, DB: Any> createNull(context: SqContext, type: SqType<JAVA?, DB>): SqNull<JAVA, DB>
}

/** Constructor which creates "unsafe" [SqNull] objects - without [type][SqNull.type] */
interface SqUnsafeNullConstructor {
    /**
     * @param context the context with which the created [SqNull] will be associated
     *
     * @return new [SqNull] ("unsafe")
     */
    fun <JAVA: Any, DB: Any> createUnsafeNull(context: SqContext): SqNull<JAVA, DB>
}


/**
 * Query parameter, usually printed as "?"
 *
 * If the [context] contains a [data][SqContext.config] whose [SqContextConfig.printParameterValues] property is `true`,
 * then after the "?" the value of the parameter will be printed in the comment;
 * some restrictions may apply, e.g. strings may be truncated, and for some other types only the size of the data may be printed
 *
 * @param JAVA "data type in kotlin"; parameter values and data read from the database will have this type
 * @param DB "data type in DB"; used when creating comparison operations -
 * so that DB then compares values of suitable types
 */
interface SqParameter<JAVA: Any?, DB: Any>: SqExpression<JAVA, DB> {
    /** Value of current parameter */
    val value: JAVA

    /**
     * Saving the value of the current parameter to [target]
     *
     * @param target statement in which the value of the parameter will be stored
     * @param parameterIndex parameter index; the index of the first parameter is 1
     */
    fun write(target: PreparedStatement, parameterIndex: Int) { this.type.write(target, parameterIndex, this.value) }

    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) {
        target.add("?", spaced = spaced)

        if (this.context.config.printParameterValues) {
            target.add(" /* ", spaced = false)
            target.add(this.type.valueToComment(this.value), spaced = false)
            target.add(" */", spaced = false)
        }
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) { target.add(this) }
}

/** Constructor which creates [SqParameter] objects */
interface SqParameterConstructor {
    /**
     * @param context the context with which the created [SqParameter] will be associated
     * @param type data type
     * @param value parameter value
     *
     * @return new [SqParameter]
     */
    fun <JAVA: Any?, DB: Any> createParameter(context: SqContext, type: SqType<JAVA, DB>, value: JAVA): SqParameter<JAVA, DB>
}


/**
 * Column
 *
 * It may be table column, expression with alias, etc.
 */
interface SqColumn<JAVA: Any?, DB: Any>: SqExpression<JAVA, DB> {
    /** Original column name */
    val columnName: String

    /**
     * "Safe" column name, based on [columnName]
     *
     * May be wrapped in double quotes if it contains any characters other than letters, numbers, and the "_" character
     */
    val safeColumnName: String
}

/** Table column */
interface SqTableColumn<JAVA: Any?, DB: Any>: SqColumn<JAVA, DB> {
    /** The table to which the current column belongs */
    val table: SqTable

    override val context: SqContext
        get() = this.table.context

    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) { target.add(this.safeColumnName, spaced = spaced) }
    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) { }
}


/**
 * Set (source) of columns
 *
 * It may be table, "select" request, etc.
 */
interface SqColSet: SqItem {
    /** Column list */
    val columns: List<SqColumn<*, *>>

    /**
     * Getting the index of a column in the current column set
     *
     * @param column the column whose index is needed
     *
     * @return index of [column], if it belongs to the current column set; otherwise - `null`
     */
    fun getColumnIndex(column: SqColumn<*, *>): Int? = this.context.getColumnIndex(this, column)

    /**
     * Getting the index of a column in the current column set
     *
     * @param column the column whose index is needed
     *
     * @return index of [column] within current column set
     *
     * @throws Exception if [column] does not belong to the current column set
     */
    fun requireColumnIndex(column: SqColumn<*, *>): Int {
        return this.getColumnIndex(column)
            ?: throw this.createColumnNotFoundException(column)
    }

    /**
     * Creating an error object like "column not found in current column set".
     * Used if when calling [requireColumnIndex] the passed column was not found in the current column set
     *
     * @param column column for which to create an error object
     *
     * @return error object
     */
    fun createColumnNotFoundException(column: SqColumn<*, *>): Exception
}

/**
 * Set (source) of columns, which contains multiple columns
 *
 * It may be table, "select" request, etc.
 */
interface SqMultiColSet: SqColSet

/**
 * Set (source) of columns, which contains single column
 *
 * It may be "select" or "union" request, etc.
 */
interface SqSingleColSet<JAVA: Any?, DB: Any>: SqColSet, SqExpression<JAVA, DB> {
    /** The only column that belongs to the current column set */
    val column: SqColumn<JAVA, DB>

    override val type: SqType<JAVA, DB>
        get() = this.column.type

    override fun getColumnIndex(column: SqColumn<*, *>): Int? {
        return if (column == this.column) {
            0
        } else {
            null
        }
    }
}


/**
 * Definition item for SQL alias
 *
 * This could be the element "my_table as t" for the alias "t"
 */
interface SqAliasDefinition: SqItem {
    /** SQL alias, to which the current definition item belongs */
    val alias: SqAlias<*>

    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) {
        this.definitionItem.appendSqlTo(target, asPart = true, spaced = spaced)
        target.add(" AS ")
        this.alias.appendSqlTo(target, asPart = true, spaced = false)
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.definitionItem.appendParametersTo(target)
        this.alias.appendParametersTo(target)
    }
}

/** SQL alias */
interface SqAlias<ORIG: SqItem>: SqItem {
    /** The original item for which the current alias was created */
    val original: ORIG

    override val definitionItem: SqAliasDefinition

    /** Alias text; for example, SQL part "my_table as t" has alias text "t" */
    val alias: String

    /**
     * "Safe" alias text, based on [alias]
     *
     * May be wrapped in double quotes if it contains any characters other than letters, numbers, and the "_" character
     */
    val safeAlias: String

    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) { target.add(this.alias, spaced = spaced) }
    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {}
}

/** Expression alias */
interface SqExpressionAlias<JAVA: Any?, DB: Any, ORIG: SqExpression<JAVA, DB>>: SqColumn<JAVA, DB>, SqAlias<ORIG> {
    override val type: SqType<JAVA, DB>
        get() = this.original.type
    override val columnName: String
        get() = this.alias
    override val safeColumnName: String
        get() = this.safeAlias
}

/** Constructor which creates [SqExpressionAlias] objects */
interface SqExpressionAliasConstructor {
    /**
     * Creates alias for expression
     *
     * @param context the context with which the created alias will be associated
     * @param original expression to create an alias item for
     * @param alias alias text
     *
     * @return new expression alias
     */
    fun <JAVA: Any?, DB: Any, ORIG: SqExpression<JAVA, DB>> createExpressionAlias(context: SqContext, original: ORIG, alias: String): SqExpressionAlias<JAVA, DB, ORIG>
}

/** Column set alias */
interface SqColSetAlias<ORIG: SqColSet>: SqAlias<ORIG>, SqColSet {
    override fun createColumnNotFoundException(column: SqColumn<*, *>): Exception {
        return IllegalArgumentException("Cannot find column $column in \"column set alias\" $this")
    }
}

/** Alias for column set, which contains multiple columns */
interface SqMultiColSetAlias<ORIG: SqMultiColSet>: SqColSetAlias<ORIG>, SqMultiColSet {
    override val columns: List<SqColSetAliasColumn<*, *>>

    /**
     * Getting the current alias column for the original column owned by [original]
     *
     * @param originalColumn the original column to get the current alias column for
     *
     * @return found current alias column
     *
     * @throws Exception if for [originalColumn] it was not possible to find the column of the current alias
     */
    fun <JAVA: Any?, DB: Any> getColumn(originalColumn: SqColumn<JAVA, DB>): SqColSetAliasColumn<JAVA, DB> {
        val result = this.columns
            .firstOrNull { it.column == originalColumn }
            ?: throw IllegalStateException("Cannot find alias column for original column $originalColumn")

        @Suppress("UNCHECKED_CAST")
        return (result as SqColSetAliasColumn<JAVA, DB>)
    }
}

/** Constructor which creates [SqMultiColSetAliasConstructor] objects */
interface SqMultiColSetAliasConstructor {
    /**
     * Creates alias for column set with multiple columns
     *
     * @param context the context with which the created alias will be associated
     * @param original column set to create an alias item for
     * @param alias alias text
     *
     * @return new alias for column set
     */
    fun <ORIG: SqMultiColSet> createMultiColSetAlias(context: SqContext, original: ORIG, alias: String): SqMultiColSetAlias<ORIG>
}

/** Alias for column set, which contains single column */
interface SqSingleColSetAlias<JAVA: Any?, DB: Any, ORIG: SqSingleColSet<JAVA, DB>>: SqColSetAlias<ORIG>, SqSingleColSet<JAVA, DB> {
    override val column: SqColSetAliasColumn<JAVA, DB>
}

/** Constructor which creates [SqSingleColSetAlias] objects */
interface SqSingleColSetAliasConstructor {
    /**
     * Creates alias for column set with single column
     *
     * @param context the context with which the created alias will be associated
     * @param original column set to create an alias item for
     * @param alias alias text
     *
     * @return new alias for column set
     */
    fun <JAVA: Any?, DB: Any, ORIG: SqSingleColSet<JAVA, DB>> createSingleColSetAlias(context: SqContext, original: ORIG, alias: String): SqSingleColSetAlias<JAVA, DB, ORIG>
}


/** Column for column set alias */
interface SqColSetAliasColumn<JAVA: Any?, DB: Any>: SqColumn<JAVA, DB> {
    /** Alias for the column set that the current column belongs to */
    val alias: SqColSetAlias<*>

    /** The original column from [alias].[original][SqColSetAlias.original] referenced by the current column */
    val column: SqColumn<JAVA, DB>

    override val columnName: String
        get() = this.column.columnName
    override val safeColumnName: String
        get() = this.column.safeColumnName

    override val type: SqType<JAVA, DB>
        get() = this.column.type

    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) {
        this.alias.appendSqlTo(target, asPart = true, spaced)
        target.add(".").add(this.safeColumnName, spaced = false)
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {}
}

/** Constructor, which creates [SqColSetAliasColumn] objects */
interface SqColSetAliasColumnConstructor {
    /**
     * Creates a column for a column set alias
     *
     * @param context the context with which the created column will be associated
     * @param alias alias of the column set to which the created column will belong
     * @param column original column from [alias].[original][SqColSetAlias.original]
     *
     * @return new column for a column set alias
     */
    fun <JAVA: Any?, DB: Any> createColSetAliasColumn(context: SqContext, alias: SqColSetAlias<*>, column: SqColumn<JAVA, DB>): SqColSetAliasColumn<JAVA, DB>
}
// endregion


// region Boolean groups, "single value" tests, comparisons
/** Type of group of boolean values */
enum class SqBooleanGroupType {
    AND,
    OR,
    ;
}

/** Group of boolean values, which will be separated by "AND" or "OR" depending on [groupType] */
interface SqBooleanGroup<JAVA: Boolean?>: SqExpression<JAVA, Boolean> {
    /** Type of current group */
    val groupType: SqBooleanGroupType

    /** Elements of current group */
    val items: List<SqExpression<*, Boolean>>

    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) {
        val firstItemSpaceAllowed = if (asPart) {
            target.add("(", spaced = spaced)
            false
        } else {
            spaced
        }

        val itemSeparator = when (this.groupType) {
            SqBooleanGroupType.AND -> " AND "
            SqBooleanGroupType.OR -> " OR "
        }

        this.items.forEachIndexed { index, item ->
            val itemSpaceAllowed = if (index == 0) {
                firstItemSpaceAllowed
            } else {
                target.add(itemSeparator)
                false
            }
            item.appendSqlTo(target, asPart = true, itemSpaceAllowed)
        }

        if (asPart) target.add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.items.forEach { it.appendParametersTo(target) }
    }
}

/** Constructor, which creates [SqBooleanGroup] objects */
interface SqBooleanGroupConstructor {
    /**
     * Creates a boolean group
     *
     * @param context the context with which the created group will be associated
     * @param type the value type of the created group
     * @param groupType group type
     * @param items group elements
     *
     * @return new boolean group
     */
    fun <JAVA: Boolean?> createBooleanGroup(
        context: SqContext,
        type: SqType<JAVA, Boolean>,
        groupType: SqBooleanGroupType,
        items: List<SqExpression<*, Boolean>>,
    ): SqBooleanGroup<JAVA>
}


/** Negation, SQL "NOT" item */
interface SqNot<JAVA: Boolean?>: SqExpression<JAVA, Boolean> {
    /** Expression, which will be negated */
    val expression: SqExpression<*, Boolean>

    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) {
        val internalSpaceAllowed = if (asPart) {
            target.add("(", spaced = spaced)
            false
        } else {
            spaced
        }

        target.add("NOT ", spaced = internalSpaceAllowed)
        this.expression.appendSqlTo(target, asPart = true, spaced = false)

        if (asPart) target.add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.expression.appendParametersTo(target)
    }
}

/** Constructor, which creates [SqNot] objects */
interface SqNotConstructor {
    /**
     * Creates SQL "NOT" item
     *
     * @param context the context with which the created item will be associated
     * @param type value type of created item
     * @param expression expression, which will be negated
     *
     * @return new SQL "NOT" item
     */
    fun <JAVA: Boolean?> createNot(
        context: SqContext,
        type: SqType<JAVA, Boolean>,
        expression: SqExpression<*, Boolean>,
    ): SqNot<JAVA>
}


/** "IS NULL"/"IS NOT NULL" test */
interface SqNullTest: SqExpression<Boolean, Boolean> {
    /** Negation; if `true`, when current test will be "IS NOT NULL" */
    val negative: Boolean

    /** Tested expression */
    val expression: SqExpression<*, *>

    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) {
        val expressionSpaceAllowed = if (asPart) {
            target.add("(", spaced = spaced)
            false
        } else {
            spaced
        }

        this.expression.appendSqlTo(target, asPart = true, spaced = expressionSpaceAllowed)
        if (this.negative) {
            target.add(" IS NOT NULL")
        } else {
            target.add(" IS NULL")
        }

        if (asPart) target.add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.expression.appendParametersTo(target)
    }
}

/** Constructor, which creates [SqNull] objects */
interface SqNullTestConstructor {
    /**
     * Creates "IS NULL"/"IS NOT NULL" test
     *
     * @param context the context with which the created item will be associated
     * @param type value type of created test
     * @param negative negation; if `true`, when current test will be "IS NOT NULL"
     * @param expression tested expression
     *
     * @return new "IS NULL"/"IS NOT NULL" test
     */
    fun createNullTest(
        context: SqContext,
        type: SqType<Boolean, Boolean>,
        negative: Boolean,
        expression: SqExpression<*, *>,
    ): SqNullTest
}


/** Two values comparison */
interface SqComparison<JAVA: Boolean?>: SqExpression<JAVA, Boolean> {
    /** First (left) value */
    val firstOperand: SqExpression<*, *>

    /** Second (right) value */
    val secondOperand: SqExpression<*, *>

    /** Comparison operation ("=", ">", etc.) */
    val operation: String

    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) {
        val firstOperandSpaceAllowed = if (asPart) {
            target.add("(", spaced = spaced)
            false
        } else {
            spaced
        }

        this.firstOperand.appendSqlTo(target, asPart = true, firstOperandSpaceAllowed)
        target.add(" ").add(this.operation).add(" ")
        this.secondOperand.appendSqlTo(target, asPart = true, spaced = false)

        if (asPart) target.add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.firstOperand.appendParametersTo(target)
        this.secondOperand.appendParametersTo(target)
    }
}

/** Constructor, which creates [SqComparison] objects */
interface SqComparisonConstructor {
    /**
     * Creates comparison of two values
     *
     * @param context the context with which the created item will be associated
     * @param type value type of created comparison
     * @param firstOperand first (left) value
     * @param secondOperand second (right) value
     * @param operation comparison operation ("=", ">", etc.)
     *
     * @return new comparison
     */
    fun <JAVA: Boolean?> createComparison(
        context: SqContext,
        type: SqType<JAVA, Boolean>,
        firstOperand: SqExpression<*, *>,
        secondOperand: SqExpression<*, *>,
        operation: String,
    ): SqComparison<JAVA>
}


/** SQL "BETWEEN" test */
interface SqBetweenTest<JAVA: Boolean?>: SqExpression<JAVA, Boolean> {
    /** Negation; if `true`, then current test will be "NOT BETWEEN" */
    val negative: Boolean

    /** Tested expression, before "BETWEEN" */
    val testedValue: SqExpression<*, *>

    /** First range expression, on the left of "AND" */
    val firstRangeValue: SqExpression<*, *>

    /** Second range expression, on the right of "AND" */
    val secondRangeValue: SqExpression<*, *>


    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) {
        val testedValueSpaceAllowed = if (asPart) {
            target.add("(", spaced = spaced)
            false
        } else {
            spaced
        }

        this.testedValue.appendSqlTo(target, asPart = true, spaced = testedValueSpaceAllowed)
        if (this.negative) {
            target.add(" NOT BETWEEN ")
        } else {
            target.add(" BETWEEN ")
        }
        this.firstRangeValue.appendSqlTo(target, asPart = true, spaced = false)
        target.add(" AND ")
        this.secondRangeValue.appendSqlTo(target, asPart = true, spaced = false)

        if (asPart) target.add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.testedValue.appendParametersTo(target)
        this.firstRangeValue.appendParametersTo(target)
        this.secondRangeValue.appendParametersTo(target)
    }
}

/** Constructor, which creates [SqBetweenTest] objects */
interface SqBetweenTestConstructor {
    /**
     * Creates "BETWEEN" test
     *
     * @param context the context with which the created item will be associated
     * @param type value type of created test
     * @param negative negation; if `true`, then created test will be "NOT BETWEEN"
     * @param testedValue tested expression, before "BETWEEN"
     * @param firstRangeValue first range expression, on the left of "AND"
     * @param secondRangeValue second range expression, on the right of "AND"
     *
     * @return new "BETWEEN" test
     */
    fun <JAVA: Boolean?> createBetweenTest(
        context: SqContext,
        type: SqType<JAVA, Boolean>,
        negative: Boolean,
        testedValue: SqExpression<*, *>,
        firstRangeValue: SqExpression<*, *>,
        secondRangeValue: SqExpression<*, *>,
    ): SqBetweenTest<JAVA>
}

/** Part on "BETWEEN" test with tested value and first (left) range value */
interface SqBetweenTestStart<JAVA: Any?, DB: Any> {
    /** Context, associated with current object */
    val context: SqContext

    /** Value type */
    val type: SqType<JAVA, DB>

    /** Negation; if `true` then this object is start of "NOT BETWEEN" test */
    val negative: Boolean

    /** Tested expression, before "BETWEEN" */
    val testedValue: SqExpression<*, DB>

    /** First range expression, on the left of "AND" */
    val firstRangeValue: SqExpression<*, DB>
}

/** Constructor, which creates [SqBetweenTestStart] objects */
interface SqBetweenTestStartConstructor {
    /**
     * Creates part of "BETWEEN" test
     *
     * @param context the context with which the created item will be associated
     * @param type value type of created part
     * @param negative negation; if `true`, then created part will be "NOT BETWEEN"
     * @param testedValue tested expression, before "BETWEEN"
     * @param firstRangeValue first range expression, on the left of "AND"
     *
     * @return new part
     */
    fun <JAVA: Any?, DB: Any> createBetweenTestStart(
        context: SqContext,
        type: SqType<JAVA, DB>,
        negative: Boolean,
        testedValue: SqExpression<*, DB>,
        firstRangeValue: SqExpression<*, DB>,
    ): SqBetweenTestStart<JAVA, DB>
}


/** SQL "IN" test */
interface SqInListTest<JAVA: Boolean?>: SqExpression<JAVA, Boolean> {
    /** Negation; if `true` then current test is "NOT IN" */
    val negative: Boolean

    /** Tested expression */
    val testedValue: SqExpression<*, *>

    /** List of allowed expressions */
    val listValues: List<SqExpression<*, *>>

    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) {
        val testedValueSpaceAllowed = if (asPart) {
            target.add("(", spaced = spaced)
            false
        } else {
            spaced
        }

        this.testedValue.appendSqlTo(target, asPart = true, spaced = testedValueSpaceAllowed)
        if (this.negative) {
            target.add(" NOT IN (")
        } else {
            target.add(" IN (")
        }
        this.listValues.forEachIndexed { index, listValue ->
            if (index > 0) target.add(", ")
            listValue.appendSqlTo(target, asPart = true, spaced = false)
        }

        target.add(")")
        if (asPart) target.add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.testedValue.appendParametersTo(target)
        this.listValues.forEach { it.appendParametersTo(target) }
    }
}

/** Constructor, which creates [SqInListTest] objects */
interface SqInListTestConstructor {
    /**
     * Creates "IN" test
     *
     * @param context the context with which the created item will be associated
     * @param type value type of created test
     * @param negative negation; if `true` then current test is "NOT IN"
     * @param testedValue tested expression
     * @param listValues list of allowed expressions
     *
     * @return new "IN" test
     */
    fun <JAVA: Boolean?> createInListTest(
        context: SqContext,
        type: SqType<JAVA, Boolean>,
        negative: Boolean,
        testedValue: SqExpression<*, *>,
        listValues: List<SqExpression<*, *>>,
    ): SqInListTest<JAVA>
}
// endregion


// region Named functions
/** Named SQL function */
interface SqNamedFunction<JAVA: Any?, DB: Any>: SqExpression<JAVA, DB> {
    /** Function name */
    val name: String

    /** If `true`, then function name must be separated from brackets */
    val nameSpaced: Boolean

    /** Parameters, which will be passed during function call */
    val params: List<SqItem>

    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) {
        target
            .add(this.name, spaced = spaced)
            .add("(", spaced = this.nameSpaced)

        this.params.forEachIndexed { index, value ->
            if (index > 0) target.add(", ")
            value.appendSqlTo(target, asPart = true, spaced = false)
        }

        target.add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.params.forEach { it.appendParametersTo(target) }
    }
}

/** Constructor, which creates [SqNamedFunction] objects */
interface SqNamedFunctionConstructor {
    /**
     * Creates named function
     *
     * @param context the context with which the created item will be associated
     * @param type value type of created named function
     * @param name function name
     * @param nameSpaced if `true`, then function name must be separated from brackets
     * @param params parameters, which will be passed during function call
     *
     * @return new named function
     */
    fun <JAVA: Any?, DB: Any> createNamedFunction(
        context: SqContext,
        type: SqType<JAVA, DB>,
        name: String,
        nameSpaced: Boolean?,
        params: List<SqItem>,
    ): SqNamedFunction<JAVA, DB>
}
// endregion


// region Mathematical operations
/** Mathematical operation */
interface SqMathOperation<JAVA: Number?>: SqExpression<JAVA, Number>

/** Mathematical operation with two operands */
interface SqTwoOperandMathOperation<JAVA: Number?>: SqMathOperation<JAVA> {
    /** First operand, before (on the left of) [operation] */
    val firstOperand: SqExpression<*, Number>

    /** Operation ("+", "*", etc.) */
    val operation: String

    /** Second operand, after (on the right of) [operation] */
    val secondOperand: SqExpression<*, Number>

    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) {
        val firstOperandSpaceAllowed = if (asPart) {
            target.add("(", spaced = spaced)
            false
        } else {
            spaced
        }

        this.firstOperand.appendSqlTo(target, asPart = true, spaced = firstOperandSpaceAllowed)
        target.add(" ").add(this.operation, spaced = false).add(" ")
        this.secondOperand.appendSqlTo(target, asPart = true, spaced = false)

        if (asPart) target.add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.firstOperand.appendParametersTo(target)
        this.secondOperand.appendParametersTo(target)
    }
}

/** Constructor, which creates [SqTwoOperandMathOperation] objects */
interface SqTwoOperandMathOperationConstructor {
    /**
     * Creates math. operation with two operands
     *
     * @param context the context with which the created item will be associated
     * @param type value type of created math. operation
     * @param firstOperand first operand, before (on the left of) [operation]
     * @param operation operation ("+", "*", etc.)
     * @param secondOperand second operand, after (on the right of) [operation]
     *
     * @return new math. operation
     */
    fun <JAVA: Number?> createTwoOperandMathOperation(
        context: SqContext,
        type: SqType<JAVA, Number>,
        firstOperand: SqExpression<*, Number>,
        operation: String,
        secondOperand: SqExpression<*, Number>,
    ): SqTwoOperandMathOperation<JAVA>
}
// endregion


// region Case
/** "WHEN ... THEN ..." item, part of SQL "CASE" */
interface SqCaseItem<JAVA: Any?, DB: Any>: SqItem {
    /** Expression between "WHEN" and "THEN" (condition) */
    val whenItem: SqExpression<*, Boolean>

    /** Expression after "THEN" (result) */
    val thenItem: SqExpression<JAVA, DB>

    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) {
        target.add("WHEN ", spaced = spaced)
        this.whenItem.appendSqlTo(target, asPart = true, spaced = false)
        target.add(" THEN ", spaced = false)
        this.thenItem.appendSqlTo(target, asPart = true, spaced = false)
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.whenItem.appendParametersTo(target)
        this.thenItem.appendParametersTo(target)
    }
}

/** Constructor, which creates [SqCaseItem] objects */
interface SqCaseItemConstructor {
    /**
     * Creates "WHEN ... THEN ..." item
     *
     * @param context the context with which the created item will be associated
     * @param whenItem expression between "WHEN" and "THEN" (condition)
     * @param thenItem expression after "THEN" (result)
     *
     * @return new "WHEN ... THEN ..." item
     */
    fun <JAVA: Any?, DB: Any> createCaseItem(
        context: SqContext,
        whenItem: SqExpression<*, Boolean>,
        thenItem: SqExpression<JAVA, DB>,
    ): SqCaseItem<JAVA, DB>
}

/** SQL "CASE" expression */
interface SqCase<JAVA: Any?, DB: Any>: SqExpression<JAVA, DB> {
    /** "WHEN ... THEN ..." items */
    val items: List<SqCaseItem<out JAVA, DB>>

    /** "ELSE ..." item */
    val elseItem: SqExpression<out JAVA, DB>?

    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) {
        val internalSpaceAllowed = if (asPart) {
            target.add("(", spaced = spaced)
            false
        } else {
            spaced
        }

        target.add("CASE", spaced = internalSpaceAllowed)
        this.items.forEach { item ->
            target.ls()
            item.appendSqlTo(target, asPart = true, spaced = false)
        }
        this.elseItem?.let { elseItem ->
            target.ls().add("ELSE ")
            elseItem.appendSqlTo(target, asPart = true, spaced = false)
        }
        target.ls().add("END")

        if (asPart) target.add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.items.forEach { it.appendParametersTo(target) }
        this.elseItem?.appendParametersTo(target)
    }
}

/** Constructor, which creates [SqCase] objects */
interface SqCaseConstructor {
    /**
     * Creates SQL "CASE" expression
     *
     * @param context the context with which the created item will be associated
     * @param type value type of created expression
     * @param items "WHEN ... THEN ..." items
     * @param elseItem "ELSE ..." item
     *
     * @return new SQL "CASE" expression
     */
    fun <JAVA: Any?, DB: Any> createCase(
        context: SqContext,
        type: SqType<JAVA, DB>,
        items: List<SqCaseItem<out JAVA, DB>>,
        elseItem: SqExpression<out JAVA, DB>?,
    ): SqCase<JAVA, DB>
}


/** Builder part for SQL "CASE", start item of "CASE" expression without type */
interface SqCaseBuildStartUntyped {
    /** Context, associated with current object */
    val context: SqContext

    /**
     * Starts "WHEN ... THEN ..." item
     *
     * @param condition expression between "WHEN" and "THEN"
     *
     * @return start item of "WHEN ... THEN ..." without type
     */
    infix fun startWhen(condition: SqExpression<*, Boolean>): SqCaseBuildItemStartUntyped

    /**
     * Starts "ELSE ..." item
     *
     * @param value result of "ELSE ..." item
     *
     * @return end item of "CASE" expression
     */
    infix fun <JAVA: Any?, DB: Any> startElse(value: SqExpression<JAVA, DB>): SqCaseBuildEnd<JAVA, DB>

    /**
     * Ends building of "CASE"
     *
     * @return created "CASE" expression
     */
    infix fun <JAVA: Any?, DB: Any> end(type: SqType<JAVA, DB>): SqCase<JAVA, DB>
}

/** Constructor, which creates [SqCaseBuildStartUntyped] objects */
interface SqCaseBuildStartUntypedConstructor {
    /**
     * Creates start item of "CASE" expression without type
     *
     * @param context the context with which the created item will be associated
     *
     * @return new start item
     */
    fun createCaseBuildStartUntyped(context: SqContext): SqCaseBuildStartUntyped
}

/** Builder part of SQL "CASE", start item of "WHEN ... THEN ..." item for untyped "CASE" builder */
interface SqCaseBuildItemStartUntyped {
    /** Context, associated with current object */
    val context: SqContext

    /** Condition, expression between "WHEN" and "THEN" */
    val whenItem: SqExpression<*, Boolean>

    /**
     * Creates middle builder part of SQL "CASE"
     *
     * @param value value, result of "WHEN ... THEN ..." item
     *
     * @return new middle part of "CASE" expression
     */
    infix fun <JAVA: Any?, DB: Any> addThen(value: SqExpression<JAVA, DB>): SqCaseBuildMiddle<JAVA, DB>
}

/** Builder part of SQL "CASE", start item of "WHEN ... THEN ..." item for typed "CASE" builder */
interface SqCaseBuildItemStartTyped<JAVA: Any?, DB: Any> {
    /** Context, associated with current object */
    val context: SqContext

    /** Value type of "CASE" expression */
    val type: SqType<JAVA, DB>

    /** Condition, expression between "WHEN" and "THEN" */
    val whenItem: SqExpression<*, Boolean>

    /**
     * Appends result to current "WHEN ... THEN ..." start item and returns execution to its "CASE" owner
     *
     * @param value result for current "WHEN ... THEN ..." start item
     *
     * @return "CASE" owner of current "WHEN ... THEN ..." start item
     */
    infix fun addThenNotNull(value: SqExpression<out JAVA, DB>): SqCaseBuildMiddle<JAVA, DB>

    /**
     * Appends result to current "WHEN ... THEN ..." start item and returns execution to its "CASE" owner
     *
     * @param value result for current "WHEN ... THEN ..." start item
     *
     * @return "CASE" owner of current "WHEN ... THEN ..." start item
     */
    infix fun addThenNullable(value: SqExpression<out JAVA?, DB>): SqCaseBuildMiddle<JAVA?, DB>

    /**
     * Appends result to current "WHEN ... THEN ..." start item and returns execution to its "CASE" owner
     *
     * @param value result for current "WHEN ... THEN ..." start item
     *
     * @return "CASE" owner of current "WHEN ... THEN ..." start item
     */
    infix fun addThenNotNull(value: JAVA): SqCaseBuildMiddle<JAVA, DB> =
        this.addThenNotNull(this.context.param(this.type, value))

    /**
     * Appends result to current "WHEN ... THEN ..." start item and returns execution to its "CASE" owner
     *
     * @param value result for current "WHEN ... THEN ..." start item
     *
     * @return "CASE" owner of current "WHEN ... THEN ..." start item
     */
    infix fun addThenNullable(value: JAVA?): SqCaseBuildMiddle<JAVA?, DB> =
        this.addThenNullable(this.context.param(this.type.nullable(), value))
}

/** Builder part for SQL "CASE", middle item of "CASE" expression */
interface SqCaseBuildMiddle<JAVA: Any?, DB: Any> {
    /** Context, associated with current object */
    val context: SqContext

    /** Value type of current item */
    val type: SqType<JAVA, DB>

    /**
     * Starts "WHEN ... THEN ..." item
     *
     * @param condition condition, expression between "WHEN" and "THEN"
     *
     * @return "WHEN ... THEN ..." start item
     */
    infix fun startWhen(condition: SqExpression<*, Boolean>): SqCaseBuildItemStartTyped<JAVA, DB>

    /**
     * Ends SQL "CASE" with "ELSE ..." item
     *
     * @param value result of "ELSE ..." item
     *
     * @return end item of SQL "CASE"
     */
    infix fun startElseNotNull(value: SqExpression<out JAVA, DB>): SqCaseBuildEnd<JAVA, DB>

    /**
     * Ends SQL "CASE" with "ELSE ..." item
     *
     * @param value result of "ELSE ..." item
     *
     * @return end item of SQL "CASE"
     */
    infix fun startElseNullable(value: SqExpression<out JAVA?, DB>): SqCaseBuildEnd<JAVA?, DB>

    /**
     * Ends building of "CASE"
     *
     * @return created "CASE" expression
     */
    fun end(): SqCase<JAVA?, DB>
}

/** Constructor, which creates [SqCaseBuildMiddle] objects */
interface SqCaseBuildMiddleConstructor {
    /**
     * Creates middle item of "CASE" expression
     *
     * @param context the context with which the created item will be associated
     * @param type value type of created item
     *
     * @return new middle item of "CASE" expression
     */
    fun <JAVA: Any?, DB: Any> createCaseBuildMiddle(
        context: SqContext,
        type: SqType<JAVA, DB>,
    ): SqCaseBuildMiddle<JAVA, DB>
}

/** Builder part for SQL "CASE", end item of "CASE" expression */
interface SqCaseBuildEnd<JAVA: Any?, DB: Any> {
    /** Context, associated with current object */
    val context: SqContext

    /** Value type of current item */
    val type: SqType<JAVA, DB>

    /**
     * Ends building of "CASE"
     *
     * @return created "CASE" expression
     */
    fun end(): SqCase<JAVA, DB>
}
// endregion


// region Statements - base
/** Statement ("select", "insert", etc.) */
interface SqStatement: SqItem

/** Statement ("select", "insert", etc.), associated with [SqContext.ConnContext]e */
interface SqConnStatement: SqStatement, SqConnItem


/** Reading statement ("select", "union") */
interface SqReadStatement: SqStatement, SqColSet {
    /** Index of first row, which will be returned by query (indices start from 0) */
    var firstResultIndexParam: SqParameter<Long, Number>?

    /**
     * Sets [firstResultIndexParam]
     *
     * @param firstResultIndex value for [firstResultIndexParam]
     */
    fun setFirstResultIndexValue(firstResultIndex: Long?)

    /** Value of [firstResultIndexParam] */
    var firstResultIndex: Long?
        get() = this.firstResultIndexParam?.value
        set(value) { this.setFirstResultIndexValue(value) }


    /** Count of rows, returned by query */
    var resultCountParam: SqParameter<Long, Number>?

    /**
     * Sets [resultCountParam]
     *
     * @param resultCount value for [resultCountParam]
     */
    fun setResultCountValue(resultCount: Long?)

    /** Value of [resultCountParam] */
    var resultCount: Long?
        get() = this.resultCountParam?.value
        set(value) { this.setResultCountValue(value) }
}

/** Reading statement ("select", "union"), associated with [SqContext.ConnContext] */
interface SqConnReadStatement: SqReadStatement, SqConnStatement

/** Reading statement ("select", "union"), which queries multiple columns */
interface SqMultiColReadStatement: SqReadStatement, SqMultiColSet

/** Reading statement ("select", "union"), which queries multiple columns; associated with [SqContext.ConnContext] */
interface SqConnMultiColReadStatement: SqMultiColReadStatement, SqConnReadStatement

/** Reading statement ("select", "union"), which queries single column */
interface SqSingleColReadStatement<JAVA: Any?, DB: Any>: SqReadStatement, SqSingleColSet<JAVA, DB>

/** Reading statement ("select", "union"), which queries single column; associated with [SqContext.ConnContext]; */
interface SqConnSingleColReadStatement<JAVA: Any?, DB: Any>: SqSingleColReadStatement<JAVA, DB>, SqConnReadStatement


/** Statement, which edits table data ("insert", "update", "delete") */
interface SqTableDataEditStatement<T: SqTable>: SqStatement {
    /** The table whose data will be changed */
    val table: T
}

/** Statement, which edits table data ("insert", "update", "delete"); associated with [SqContext.ConnContext] */
interface SqConnTableDataEditStatement<T: SqTable>: SqTableDataEditStatement<T>, SqConnStatement


/** Statement, which writes table data ("insert", "update") */
interface SqTableDataWriteStatement<T: SqTable>: SqTableDataEditStatement<T> {
    /**
     * @return mapping, which contains "table column + value for this column" pairs
     */
    fun createValueMapping(): SqColumnValueMapping<T>

    /**
     * Removes old "column + value" mappings and copies such data from [mapping]
     *
     * @param mapping mapping, which contains "table column + value for this column" pairs
     */
    fun applyValueMapping(mapping: SqColumnValueMapping<T>)
}

/** Statement, which writes table data ("insert", "update"); associated with [SqContext.ConnContext] */
interface SqConnTableDataWriteStatement<T: SqTable>: SqTableDataWriteStatement<T>, SqConnTableDataEditStatement<T>
// endregion


// region Statements - join, order by
/** Type of SQL JOIN (inner, left, etc.) */
enum class SqJoinType {
    INNER,
    LEFT,
    RIGHT,
    FULL,
    ;
}

/** SQL JOIN */
interface SqJoin: SqMultiColSet {
    /** Type of current SQL JOIN */
    val joinType: SqJoinType

    /** "Main" column set, before "JOIN" */
    val mainColSet: SqColSet

    /** "Joined" column set, after "JOIN" */
    val joinedColSet: SqColSet

    /** "ON" condition for current SQL JOIN */
    val on: SqExpression<*, Boolean>?

    /**
     * Sets [SqJoin.on] in current SQL JOIN
     *
     * @param on new value for [SqJoin.on]
     */
    fun setOn(on: SqExpression<*, Boolean>?)


    override fun createColumnNotFoundException(column: SqColumn<*, *>): Exception {
        return IllegalStateException("Cannot find column $column in \"join column set\" $this")
    }


    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) {
        this.mainColSet.definitionItem.appendSqlTo(target, asPart = true, spaced)
        target.add(this.joinType.name, spaced = true).add("JOIN", spaced = true)
        this.joinedColSet.definitionItem.appendSqlTo(target, asPart = true, spaced = true)

        this.on?.let { on ->
            target.add("ON", spaced = true)
            on.appendSqlTo(target, asPart = true, spaced = true)
        }
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.mainColSet.definitionItem.appendParametersTo(target)
        this.joinedColSet.definitionItem.appendParametersTo(target)
    }
}

/** Constructor, which creates [SqJoin] objects */
interface SqJoinConstructor {
    /**
     * Creates SQL JOIN
     *
     * @param context the context with which the created item will be associated
     * @param joinType type of created SQL JOIN
     * @param mainColSet "main" column set, before "JOIN"
     * @param joinedColSet "joined" column set, after "JOIN"
     *
     * @return new SQL JOIN
     */
    fun createJoin(context: SqContext, joinType: SqJoinType, mainColSet: SqColSet, joinedColSet: SqColSet): SqJoin
}


/** Sort order for "ORDER BY" */
enum class SqSortOrder {
    ASC,
    DESC,
    ;
}

/** SQL "ORDER BY" */
interface SqOrderBy: SqItem {
    /** Sorted column */
    val column: SqColumn<*, *>

    /** Sort order */
    val order: SqSortOrder

    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) {
        this.column.appendSqlTo(target, asPart = true, spaced)
        target.add(this.order.name, spaced = true)
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) { this.column.appendParametersTo(target) }
}

/** Constructor, which creates [SqOrderBy] objects */
interface SqOrderByConstructor {
    /**
     * Creates SQL "ORDER BY" item
     *
     * @param context the context with which the created item will be associated
     * @param column sorted column
     * @param order sort order
     *
     * @return new SQL "ORDER BY" item
     */
    fun createOrderBy(context: SqContext, column: SqColumn<*, *>, order: SqSortOrder): SqOrderBy
}
// endregion


// region Statements - select, union
/** SQL "SELECT" */
interface SqSelect: SqReadStatement {
    /** If `true`, then "DISTINCT" will be added after "SELECT" */
    var distinct: Boolean

    /** List of column sets in "FROM" part */
    var from: List<SqColSet>?

    /** "WHERE" condition */
    var where: SqExpression<*, Boolean>?

    /** Column list in "GROUP BY" part */
    var groupBy: List<SqColumn<*, *>>?

    /** "HAVING" condition */
    var having: SqExpression<*, Boolean>?

    /** List of column sorts in "ORDER BY" part */
    var orderBy: List<SqOrderBy>?

    override fun createColumnNotFoundException(column: SqColumn<*, *>): Exception {
        return IllegalArgumentException("Cannot find column $column in \"select\" statement $this")
    }
}

/** SQL "SELECT", which queries multiple columns */
interface SqMultiColSelect: SqSelect, SqMultiColReadStatement {
    override var columns: List<SqColumn<*, *>>
}

/** Constructor, which creates [SqMultiColSelect] objects */
interface SqMultiColSelectConstructor {
    /**
     * Creates multi-column SQL SELECT
     *
     * @param context the context with which the created item will be associated
     * @param distinct if `true`, then "DISTINCT" will be added after "SELECT"
     * @param columns column list
     * @param from list of column sets in "FROM" part
     * @param where "WHERE" condition
     * @param groupBy column list in "GROUP BY" part
     * @param having "HAVING" condition
     * @param orderBy list of column sorts in "ORDER BY" part
     *
     * @return new multi-column SQL SELECT
     */
    fun createMultiColSelect(
        context: SqContext,
        distinct: Boolean = false,
        columns: List<SqColumn<*, *>> = emptyList(),
        from: List<SqColSet>? = null,
        where: SqExpression<*, Boolean>? = null,
        groupBy: List<SqColumn<*, *>>? = null,
        having: SqExpression<*, Boolean>? = null,
        orderBy: List<SqOrderBy>? = null,
    ): SqMultiColSelect
}

/** SQL "SELECT", which queries single column */
interface SqSingleColSelect<JAVA: Any?, DB: Any>: SqSelect, SqSingleColReadStatement<JAVA, DB> {
    override var column: SqColumn<JAVA, DB>
}

/** Constructor, which creates [SqSingleColSelect] objects */
interface SqSingleColSelectConstructor {
    /**
     * Creates single-column SQL SELECT
     *
     * @param context the context with which the created item will be associated
     * @param distinct if `true`, then "DISTINCT" will be added after "SELECT"
     * @param column column
     * @param from list of column sets in "FROM" part
     * @param where "WHERE" condition
     * @param groupBy column list in "GROUP BY" part
     * @param having "HAVING" condition
     * @param orderBy list of column sorts in "ORDER BY" part
     *
     * @return new single-column SQL SELECT
     */
    fun <JAVA: Any?, DB: Any> createSingleColSelect(
        context: SqContext,
        distinct: Boolean,
        column: SqColumn<JAVA, DB>,
        from: List<SqColSet>? = null,
        where: SqExpression<*, Boolean>? = null,
        groupBy: List<SqColumn<*, *>>? = null,
        having: SqExpression<*, Boolean>? = null,
        orderBy: List<SqOrderBy>? = null,
    ): SqSingleColSelect<JAVA, DB>
}

/** SQL "SELECT"; associated with [SqContext.ConnContext] */
interface SqConnSelect: SqSelect, SqConnReadStatement

/** SQL "SELECT", which queries multiple columns; associated with [SqContext.ConnContext] */
interface SqConnMultiColSelect: SqMultiColSelect, SqConnSelect, SqConnMultiColReadStatement

/** Constructor, which creates [SqConnMultiColSelect] objects */
interface SqConnMultiColSelectConstructor {
    /**
     * Creates multi-column SQL SELECT
     *
     * @param context the context with which the created item will be associated
     * @param distinct if `true`, then "DISTINCT" will be added after "SELECT"
     * @param columns column list
     * @param from list of column sets in "FROM" part
     * @param where "WHERE" condition
     * @param groupBy column list in "GROUP BY" part
     * @param having "HAVING" condition
     * @param orderBy list of column sorts in "ORDER BY" part
     *
     * @return new multi-column SQL SELECT
     */
    fun createConnMultiColSelect(
        context: SqContext.ConnContext,
        distinct: Boolean,
        columns: List<SqColumn<*, *>>,
        from: List<SqColSet>? = null,
        where: SqExpression<*, Boolean>? = null,
        groupBy: List<SqColumn<*, *>>? = null,
        having: SqExpression<*, Boolean>? = null,
        orderBy: List<SqOrderBy>? = null,
    ): SqConnMultiColSelect
}

/** SQL "SELECT", which queries single column; associated with [SqContext.ConnContext] */
interface SqConnSingleColSelect<JAVA: Any?, DB: Any>: SqSingleColSelect<JAVA, DB>, SqConnSelect, SqConnSingleColReadStatement<JAVA, DB>

/** Constructor, which creates [SqConnSingleColSelect] objects */
interface SqConnSingleColSelectConstructor {
    /**
     * Creates single-column SQL SELECT
     *
     * @param context the context with which the created item will be associated
     * @param distinct if `true`, then "DISTINCT" will be added after "SELECT"
     * @param column column
     * @param from list of column sets in "FROM" part
     * @param where "WHERE" condition
     * @param groupBy column list in "GROUP BY" part
     * @param having "HAVING" condition
     * @param orderBy list of column sorts in "ORDER BY" part
     *
     * @return new single-column SQL SELECT
     */
    fun <JAVA: Any?, DB: Any> createConnSingleColSelect(
        context: SqContext.ConnContext,
        distinct: Boolean,
        column: SqColumn<JAVA, DB>,
        from: List<SqColSet>? = null,
        where: SqExpression<*, Boolean>? = null,
        groupBy: List<SqColumn<*, *>>? = null,
        having: SqExpression<*, Boolean>? = null,
        orderBy: List<SqOrderBy>? = null,
    ): SqConnSingleColSelect<JAVA, DB>
}


/** SQL UNION */
interface SqUnion: SqReadStatement {
    /** If `true`, then "UNION ALL" will be used instead of "UNION" */
    var unionAll: Boolean

    /** List of SQL SELECT */
    val selects: List<SqSelect>

    /** First of [selects]; throws error, if current SQL UNION has empty [selects] list */
    val firstSelect: SqSelect
        get() {
            return this.selects.firstOrNull()
                ?: error("\"Union\" request $this has no one select request")
        }

    override val columns: List<SqColumn<*, *>>
        get() = this.firstSelect.columns

    override fun createColumnNotFoundException(column: SqColumn<*, *>): Exception {
        return IllegalArgumentException("Cannot find column $column in \"union\" statement $this")
    }
}

/** SQL UNION, which queries multiple columns */
interface SqMultiColUnion: SqUnion, SqMultiColReadStatement {
    override var selects: List<SqSelect>
}

/** Constructor, which creates [SqMultiColUnion] objects */
interface SqMultiColUnionConstructor {
    /**
     * Creates multi-column SQL UNION
     *
     * @param context the context with which the created item will be associated
     * @param unionAll if `true`, then "UNION ALL" will be used instead of "UNION"
     * @param selects list of SQL SELECT
     *
     * @return new multi-column SQL UNION
     */
    fun createMultiColUnion(context: SqContext, unionAll: Boolean, selects: List<SqSelect>): SqMultiColUnion
}

/** SQL UNION, which queries single column */
interface SqSingleColUnion<JAVA: Any?, DB: Any>: SqUnion, SqSingleColReadStatement<JAVA, DB> {
    override var selects: List<SqSingleColSelect<JAVA, DB>>

    override val firstSelect: SqSingleColSelect<JAVA, DB>
        get() {
            return this.selects.firstOrNull()
                ?: throw IllegalArgumentException("\"Union\" request $this has no one select request")
        }

    override val column: SqColumn<JAVA, DB>
        get() = this.firstSelect.column
}

/** Constructor, which creates [SqSingleColUnion] objects */
interface SqSingleColUnionConstructor {
    /**
     * Creates single-column SQL UNION
     *
     * @param context the context with which the created item will be associated
     * @param unionAll if `true`, then "UNION ALL" will be used instead of "UNION"
     * @param selects list of SQL SELECT
     *
     * @return new single-column SQL UNION
     */
    fun <JAVA: Any?, DB: Any> createSingleColUnion(
        context: SqContext,
        unionAll: Boolean,
        selects: List<SqSingleColSelect<JAVA, DB>>,
    ): SqSingleColUnion<JAVA, DB>
}

/** SQL UNION; associated with [SqContext.ConnContext] */
interface SqConnUnion: SqUnion, SqConnReadStatement

/** SQL UNION, which queries multiple columns; associated with [SqContext.ConnContext] */
interface SqConnMultiColUnion: SqMultiColUnion, SqConnMultiColReadStatement

/** Constructor, which creates [SqConnMultiColUnion] objects */
interface SqConnMultiColUnionConstructor {
    /**
     * Creates multi-column SQL UNION
     *
     * @param context the context with which the created item will be associated
     * @param unionAll if `true`, then "UNION ALL" will be used instead of "UNION"
     * @param selects list of SQL SELECT
     *
     * @return new multi-column SQL UNION
     */
    fun createConnMultiColUnion(
        context: SqContext.ConnContext,
        unionAll: Boolean,
        selects: List<SqSelect>,
    ): SqConnMultiColUnion
}

/** SQL UNION, which queries single column; associated with [SqContext.ConnContext] */
interface SqConnSingleColUnion<JAVA: Any?, DB: Any>: SqSingleColUnion<JAVA, DB>, SqConnUnion, SqConnSingleColReadStatement<JAVA, DB>

/** Constructor, which creates [SqConnSingleColUnion] objects */
interface SqConnSingleColUnionConstructor {
    /**
     * Creates single-column SQL UNION
     *
     * @param context the context with which the created item will be associated
     * @param unionAll if `true`, then "UNION ALL" will be used instead of "UNION"
     * @param selects list of SQL SELECT
     *
     * @return new single-column SQL UNION
     */
    fun <JAVA: Any?, DB: Any> createConnSingleColUnion(
        context: SqContext.ConnContext,
        unionAll: Boolean,
        selects: List<SqSingleColSelect<JAVA, DB>>,
    ): SqConnSingleColUnion<JAVA, DB>
}
// endregion


// region Statements - insert, update, delete
/** SQL INSERT */
interface SqInsert<T: SqTable>: SqTableDataWriteStatement<T> {
    /** Column list */
    var columns: List<SqTableColumn<*, *>>?

    /** Value list; if it is not `null`, then [select] must be `null` */
    var values: List<SqExpression<*, *>>?

    /** SQL SELECT, which produces values; if it is not `null`, then [values] must be `null` */
    var select: SqReadStatement?

    override fun applyValueMapping(mapping: SqColumnValueMapping<T>) {
        this.columns(mapping.map.keys.toList()).values(mapping.map.values.toList())
    }

    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) {
        val insertSpaceAllowed = if (asPart) {
            target.add("(", spaced)
            false
        } else {
            spaced
        }

        target.add("INSERT INTO ", spaced = insertSpaceAllowed)
        this.table.appendSqlTo(target, asPart = true, spaced = false)

        this.columns?.takeUnless { it.isEmpty() }?.let { columns ->
            target.add(" (")
            columns.forEachIndexed { index, column ->
                if (index > 0) target.add(", ")
                column.appendSqlTo(target, asPart = true, spaced = false)
            }
            target.add(")")
        }

        this.values?.takeUnless { it.isEmpty() }?.let { values ->
            target.add(" VALUES (")
            values.forEachIndexed { index, value ->
                if (index > 0) target.add(", ")
                value.appendSqlTo(target, asPart = true, spaced = false)
            }
            target.add(")")
        }

        this.select?.let { select ->
            target.add(" ")
            select.appendSqlTo(target, asPart = true, spaced = false)
        }

        if (asPart) target.add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.columns?.forEach { it.appendParametersTo(target) }
        this.values?.forEach { it.appendParametersTo(target) }
        this.select?.appendParametersTo(target)
    }

    /**
     * Prepares [PreparedStatement] based on [sql] and [parameters][setParametersTo] of current SQL INSERT
     *
     * Optionally prepared statement can produce generated keys
     *
     * @param connection connection used for creating [PreparedStatement]
     * @param returnGeneratedKeys if `true`, when [Statement.RETURN_GENERATED_KEYS] setting will be applied to
     * prepared statement
     *
     * @return prepared statement
     */
    fun prepareStatement(connection: Connection, returnGeneratedKeys: Boolean): PreparedStatement {
        val autoGeneratedKeys = if (returnGeneratedKeys) {
            Statement.RETURN_GENERATED_KEYS
        } else {
            Statement.NO_GENERATED_KEYS
        }
        val result = connection.prepareStatement(this.sql(), autoGeneratedKeys)
        this.setParametersTo(result)
        return result
    }
}

/** Constructor, which creates [SqInsert] objects */
interface SqInsertConstructor {
    /**
     * Creates SQL INSERT
     *
     * @param context the context with which the created item will be associated
     * @param table table whose data will be changed
     * @param columns column list
     * @param values value list; if it is not `null`, then [select] must be `null`
     * @param select SQL SELECT, which produces values; if it is not `null`, then [values] must be `null`
     *
     * @return new SQL INSERT
     */
    fun <T: SqTable> createInsert(
        context: SqContext,
        table: T,
        columns: List<SqTableColumn<*, *>>? = null,
        values: List<SqExpression<*, *>>? = null,
        select: SqReadStatement? = null,
    ): SqInsert<T>
}

/** SQL INSERT; associated with [SqContext.ConnContext] */
interface SqConnInsert<T: SqTable>: SqInsert<T>, SqConnTableDataWriteStatement<T> {
    /**
     * Prepares [PreparedStatement] based on [sql] and [parameters][setParametersTo] of current SQL INSERT
     *
     * Uses own [connection] to [create prepared statement][Connection.prepareStatement]
     *
     * Optionally prepared statement can produce generated keys
     *
     * @param returnGeneratedKeys if `true`, when [Statement.RETURN_GENERATED_KEYS] setting will be applied to
     * prepared statement
     *
     * @return prepared statement
     */
    fun prepareStatement(returnGeneratedKeys: Boolean): PreparedStatement =
        this.prepareStatement(this.connection, returnGeneratedKeys)
}

/** Constructor, which creates [SqConnInsert] objects */
interface SqConnInsertConstructor {
    /**
     * Creates SQL INSERT
     *
     * @param context the context with which the created item will be associated
     * @param table table whose data will be changed
     * @param columns column list
     * @param values value list; if it is not `null`, then [select] must be `null`
     * @param select SQL SELECT, which produces values; if it is not `null`, then [values] must be `null`
     *
     * @return new SQL INSERT
     */
    fun <T: SqTable> createConnInsert(
        context: SqContext.ConnContext,
        table: T,
        columns: List<SqTableColumn<*, *>>? = null,
        values: List<SqExpression<*, *>>? = null,
        select: SqReadStatement? = null,
    ): SqConnInsert<T>
}


/** SQL UPDATE */
interface SqUpdate<T: SqTable>: SqTableDataWriteStatement<T> {
    /** "SET" part, map of "table column + value for this column" pairs */
    var set: Map<SqTableColumn<*, *>, SqExpression<*, *>>?

    /** "WHERE" condition */
    var where: SqExpression<*, Boolean>?

    override fun applyValueMapping(mapping: SqColumnValueMapping<T>) { this.set = LinkedHashMap(mapping.map) }

    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) {
        val updateSpaceAllowed = if (asPart) {
            target.add("(", spaced = spaced)
            false
        } else {
            spaced
        }

        target.add("UPDATE ", spaced = updateSpaceAllowed)
        this.table.appendSqlTo(target, asPart = true, spaced = false)

        this.set?.takeUnless { it.isEmpty() }?.let { set ->
            target.add(" SET ").ls()
            var first = true
            set.forEach { (column, value) ->
                if (first) {
                    first = false
                } else {
                    target.add(",").ls()
                }
                target.add("  ")
                column.appendSqlTo(target, asPart = true, spaced = false)
                target.add(" = ")
                value.appendSqlTo(target, asPart = true, spaced = false)
            }
        }

        this.where?.let { where ->
            target.ls().add("WHERE ")
            where.appendSqlTo(target, asPart = true, spaced = false)
        }

        if (asPart) target.add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.table.appendParametersTo(target)
        this.set?.forEach { (column, value) ->
            column.appendParametersTo(target)
            value.appendParametersTo(target)
        }
        this.where?.appendParametersTo(target)
    }
}

/** Constructor, which creates [SqUpdate] objects */
interface SqUpdateConstructor {
    /**
     * Creates SQL UPDATE
     *
     * @param context the context with which the created item will be associated
     * @param table table whose data will be changed
     * @param set "SET" part, map of "table column + value for this column" pairs
     * @param where "WHERE" condition
     *
     * @return new SQL UPDATE
     */
    fun <T: SqTable> createUpdate(
        context: SqContext,
        table: T,
        set: Map<SqTableColumn<*, *>, SqExpression<*, *>>? = null,
        where: SqExpression<*, Boolean>? = null,
    ): SqUpdate<T>
}

/** SQL UPDATE; associated with [SqContext.ConnContext] */
interface SqConnUpdate<T: SqTable>: SqUpdate<T>, SqConnTableDataWriteStatement<T>

/** Constructor, which creates [SqConnUpdate] objects */
interface SqConnUpdateConstructor {
    /**
     * Creates SQL UPDATE
     *
     * @param context the context with which the created item will be associated
     * @param table table whose data will be changed
     * @param set "SET" part, map of "table column + value for this column" pairs
     * @param where "WHERE" condition
     *
     * @return new SQL UPDATE
     */
    fun <T: SqTable> createConnUpdate(
        context: SqContext.ConnContext,
        table: T,
        set: Map<SqTableColumn<*, *>, SqExpression<*, *>>? = null,
        where: SqExpression<*, Boolean>? = null,
    ): SqConnUpdate<T>
}


/** SQL DELETE */
interface SqDelete<T: SqTable>: SqTableDataEditStatement<T> {
    /** "WHERE" condition */
    var where: SqExpression<*, Boolean>?

    override fun appendSqlTo(target: SqTextBuffer, asPart: Boolean, spaced: Boolean) {
        val deleteSpaceAllowed = if (asPart) {
            target.add("(", spaced = spaced)
            false
        } else {
            spaced
        }

        target.add("DELETE FROM ", spaced = deleteSpaceAllowed)
        this.table.appendSqlTo(target, asPart = true, spaced = false)

        this.where?.let { where ->
            target.add(" WHERE ")
            where.appendSqlTo(target, asPart = true, spaced = false)
        }

        if (asPart) target.add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.table.appendParametersTo(target)
        this.where?.appendParametersTo(target)
    }
}

/** Constructor, which creates [SqDelete] objects */
interface SqDeleteConstructor {
    /**
     * Creates SQL DELETE
     *
     * @param context the context with which the created item will be associated
     * @param table table whose data will be changed
     * @param where "WHERE" condition
     *
     * @return new SQL DELETE
     */
    fun <T: SqTable> createDelete(
        context: SqContext,
        table: T,
        where: SqExpression<*, Boolean>? = null,
    ): SqDelete<T>
}

/** SQL DELETE; associated with [SqContext.ConnContext] */
interface SqConnDelete<T: SqTable>: SqDelete<T>, SqConnTableDataEditStatement<T>

/** Constructor, which creates [SqConnDelete] objects */
interface SqConnDeleteConstructor {
    /**
     * Creates SQL DELETE
     *
     * @param context the context with which the created item will be associated
     * @param table table whose data will be changed
     * @param where "WHERE" condition
     *
     * @return new SQL DELETE
     */
    fun <T: SqTable> createConnDelete(
        context: SqContext.ConnContext,
        table: T,
        where: SqExpression<*, Boolean>? = null,
    ): SqConnDelete<T>
}
// endregion
