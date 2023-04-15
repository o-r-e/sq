package me.ore.sq

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement


// region Utils
interface SqObjectMap {
    companion object {
        val EMPTY: SqObjectMap = object : SqObjectMap {
            override fun <T : Any> get(key: Class<T>): T? = null
        }
    }

    operator fun <T: Any> get(key: Class<T>): T?
}


interface SqValueReader<JAVA: Any> {
    fun readNullable(source: ResultSet, columnIndex: Int): JAVA?
    fun readNotNull(source: ResultSet, columnIndex: Int): JAVA {
        return this.readNullable(source, columnIndex)
            ?: error("Column with index $columnIndex has NULL value")
    }
}

interface SqValueWriter<JAVA: Any> {
    fun write(target: PreparedStatement, parameterIndex: Int, value: JAVA?)

    fun valueToComment(value: JAVA?): String
}


interface SqWriter {
    fun addLineSeparator()
    fun addText(text: String, spaced: Boolean)
    fun clearData()
}
interface SqWriterConstructor {
    fun createWriter(context: SqContext): SqWriter
}


interface SqColumnValueMapping<T: SqTable> {
    val statement: SqTableWriteStatement<T>
    val context: SqContext
        get() = this.statement.context
    val table: T
        get() = this.statement.table
    val map: MutableMap<SqTableColumn<*, *>, SqExpression<*, *>>

    operator fun <DB: Any> set(column: SqTableColumn<*, DB>, value: SqExpression<*, DB>): SqColumnValueMapping<T> = this.apply {
        this.map[column] = value
    }

    operator fun <JAVA: Any?, DB: Any> set(column: SqTableColumn<JAVA, DB>, value: JAVA): SqColumnValueMapping<T> = this.apply {
        val param = this.context.param<JAVA, DB>(column.type, value)
        this[column] = param
    }

    fun clearData() { this.map.clear() }
}
// endregion


// region Base items
interface SqItem {
    val context: SqContext
    val definitionItem: SqItem

    fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean)
    fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>)

    fun sql(): String {
        return this.context.writer()
            .let { writer ->
                try {
                    this.appendSqlTo(writer, asPart = false, spaceAllowed = false)
                    writer.toString()
                } finally {
                    writer.clear()
                }
            }
    }

    fun parameters(): List<SqParameter<*, *>> = buildList { this@SqItem.appendParametersTo(this) }

    fun <T: PreparedStatement> setParametersTo(target: T): T = this.parameters().setTo(target)

    fun prepareStatement(connection: Connection): PreparedStatement {
        val result = connection.prepareStatement(this.sql())
        this.setParametersTo(result)
        return result
    }
}

interface SqConnItem: SqItem {
    override val context: SqContext.ConnContext
    val connection: Connection
        get() = this.context.connection

    fun prepareStatement(): PreparedStatement = this.prepareStatement(this.connection)
}


interface SqExpression<JAVA: Any?, DB: Any>: SqItem {
    val type: SqType<JAVA, DB>
    fun read(source: ResultSet, columnIndex: Int): JAVA = this.type.read(source, columnIndex)
}

interface SqNull<JAVA: Any, DB: Any>: SqExpression<JAVA?, DB> {
    override fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean) { target.add("NULL", spaced = spaceAllowed) }
    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {}
}
interface SqNullConstructor {
    fun <JAVA: Any, DB: Any> createNull(context: SqContext, type: SqType<JAVA?, DB>): SqNull<JAVA, DB>
}
interface SqUntypedNullConstructor {
    fun <JAVA: Any, DB: Any> createUntypedNull(context: SqContext): SqNull<JAVA, DB>
}

interface SqParameter<JAVA: Any?, DB: Any>: SqExpression<JAVA, DB> {
    val value: JAVA
    fun write(target: PreparedStatement, parameterIndex: Int) { this.type.write(target, parameterIndex, this.value) }

    override fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean) {
        target.add("?", spaced = spaceAllowed)

        if (this.context.data.printParameterValues) {
            target.add("/*", spaced = true)
            target.add(this.type.valueToComment(this.value), spaced = true)
            target.add("*/", spaced = true)
        }
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) { target.add(this) }
}
interface SqParameterConstructor {
    fun <JAVA: Any?, DB: Any> createParameter(context: SqContext, type: SqType<JAVA, DB>, value: JAVA): SqParameter<JAVA, DB>
}

interface SqColumn<JAVA: Any?, DB: Any>: SqExpression<JAVA, DB> {
    val columnName: String
    val safeColumnName: String
}

interface SqTableColumn<JAVA: Any?, DB: Any>: SqColumn<JAVA, DB> {
    val table: SqTable
    override val context: SqContext
        get() = this.table.context

    override fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean) { target.add(this.safeColumnName, spaced = spaceAllowed) }
    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) { }
}


interface SqColSet: SqItem {
    val columns: List<SqColumn<*, *>>

    fun getColumnIndex(column: SqColumn<*, *>): Int? = this.context.getColumnIndex(this, column)

    fun requireColumnIndex(column: SqColumn<*, *>): Int {
        return this.getColumnIndex(column)
            ?: throw this.createColumnNotFoundException(column)
    }

    fun createColumnNotFoundException(column: SqColumn<*, *>): Exception
}

interface SqMultiColSet: SqColSet

interface SqSingleColSet<JAVA: Any?, DB: Any>: SqColSet, SqExpression<JAVA, DB> {
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


interface SqAlias<ORIG: SqItem>: SqItem {
    val original: ORIG
    override val definitionItem: SqItem
        get() = this.original

    val alias: String
    val safeAlias: String

    override fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean) { target.add(this.alias, spaced = spaceAllowed) }
    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {}
}

interface SqExpressionAlias<JAVA: Any?, DB: Any, ORIG: SqExpression<JAVA, DB>>: SqColumn<JAVA, DB>, SqAlias<ORIG> {
    override val type: SqType<JAVA, DB>
        get() = this.original.type
    override val columnName: String
        get() = this.alias
    override val safeColumnName: String
        get() = this.safeAlias
}
interface SqExpressionAliasConstructor {
    fun <JAVA: Any?, DB: Any, ORIG: SqExpression<JAVA, DB>> createExpressionAlias(context: SqContext, original: ORIG, alias: String): SqExpressionAlias<JAVA, DB, ORIG>
}

interface SqColSetAlias<ORIG: SqColSet>: SqAlias<ORIG>, SqColSet {
    override fun createColumnNotFoundException(column: SqColumn<*, *>): Exception {
        return IllegalArgumentException("Cannot find column $column in \"column set alias\" $this")
    }
}

interface SqMultiColSetAlias<ORIG: SqMultiColSet>: SqColSetAlias<ORIG>, SqMultiColSet {
    override val columns: List<SqColSetAliasColumn<*, *>>

    fun <JAVA: Any?, DB: Any> getColumn(originalColumn: SqColumn<JAVA, DB>): SqColSetAliasColumn<JAVA, DB> {
        val result = this.columns
            .firstOrNull { it.column == originalColumn }
            ?: throw IllegalStateException("Cannot find alias column for original column $originalColumn")

        @Suppress("UNCHECKED_CAST")
        return (result as SqColSetAliasColumn<JAVA, DB>)
    }
}
interface SqMultiColSetAliasConstructor {
    fun <ORIG: SqMultiColSet> createMultiColSetAlias(context: SqContext, original: ORIG, alias: String): SqMultiColSetAlias<ORIG>
}

interface SqSingleColSetAlias<JAVA: Any?, DB: Any, ORIG: SqSingleColSet<JAVA, DB>>: SqColSetAlias<ORIG>, SqSingleColSet<JAVA, DB> {
    override val column: SqColSetAliasColumn<JAVA, DB>
}
interface SqSingleColSetAliasConstructor {
    fun <JAVA: Any?, DB: Any, ORIG: SqSingleColSet<JAVA, DB>> createSingleColSetAlias(context: SqContext, original: ORIG, alias: String): SqSingleColSetAlias<JAVA, DB, ORIG>
}


interface SqColSetAliasColumn<JAVA: Any?, DB: Any>: SqColumn<JAVA, DB> {
    val alias: SqColSetAlias<*>
    val column: SqColumn<JAVA, DB>

    override val columnName: String
        get() = this.column.columnName
    override val safeColumnName: String
        get() = this.column.safeColumnName

    override val type: SqType<JAVA, DB>
        get() = this.column.type

    override fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean) {
        this.alias.appendSqlTo(target, asPart = true, spaceAllowed)
        target.add(".").add(this.safeColumnName, spaced = false)
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {}
}
interface SqColSetAliasColumnConstructor {
    fun <JAVA: Any?, DB: Any> createColSetAliasColumn(context: SqContext, alias: SqColSetAlias<*>, column: SqColumn<JAVA, DB>): SqColSetAliasColumn<JAVA, DB>
}
// endregion


// region Boolean groups, "single value" tests, comparisons, named functions, mathematical operations
interface SqBooleanGroup<JAVA: Boolean?>: SqExpression<JAVA, Boolean> {
    val groupType: SqBooleanGroupType
    val items: List<SqExpression<*, Boolean>>

    override fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean) {
        val firstItemSpaceAllowed = if (asPart) {
            target.add("(", spaced = spaceAllowed)
            false
        } else {
            spaceAllowed
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
interface SqBooleanGroupConstructor {
    fun <JAVA: Boolean?> createBooleanGroup(context: SqContext, type: SqType<JAVA, Boolean>, groupType: SqBooleanGroupType, items: List<SqExpression<*, Boolean>>): SqBooleanGroup<JAVA>
}

interface SqNot<JAVA: Boolean?>: SqExpression<JAVA, Boolean> {
    val expression: SqExpression<*, Boolean>

    override fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean) {
        val internalSpaceAllowed = if (asPart) {
            target.add("(", spaced = spaceAllowed)
            false
        } else {
            spaceAllowed
        }

        target.add("NOT ", spaced = internalSpaceAllowed)
        this.expression.appendSqlTo(target, asPart = true, spaceAllowed = false)

        if (asPart) target.add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.expression.appendParametersTo(target)
    }
}
interface SqNotConstructor {
    fun <JAVA: Boolean?> createNot(context: SqContext, type: SqType<JAVA, Boolean>, expression: SqExpression<*, Boolean>): SqNot<JAVA>
}


interface SqNullTest: SqExpression<Boolean, Boolean> {
    val negation: Boolean
    val expression: SqExpression<*, *>

    override fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean) {
        val expressionSpaceAllowed = if (asPart) {
            target.add("(", spaced = spaceAllowed)
            false
        } else {
            spaceAllowed
        }

        this.expression.appendSqlTo(target, asPart = true, spaceAllowed = expressionSpaceAllowed)
        if (this.negation) {
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
interface SqNullTestConstructor {
    fun createNullTest(context: SqContext, type: SqType<Boolean, Boolean>, negation: Boolean, expression: SqExpression<*, *>): SqNullTest
}


interface SqComparison<JAVA: Boolean?>: SqExpression<JAVA, Boolean> {
    val firstOperand: SqExpression<*, *>
    val secondOperand: SqExpression<*, *>
    val operation: String

    override fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean) {
        val firstOperandSpaceAllowed = if (asPart) {
            target.add("(", spaced = spaceAllowed)
            false
        } else {
            spaceAllowed
        }

        this.firstOperand.appendSqlTo(target, asPart = true, firstOperandSpaceAllowed)
        target.add(" ").add(this.operation).add(" ")
        this.secondOperand.appendSqlTo(target, asPart = true, spaceAllowed = false)

        if (asPart) target.add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.firstOperand.appendParametersTo(target)
        this.secondOperand.appendParametersTo(target)
    }
}
interface SqComparisonConstructor {
    fun <JAVA: Boolean?> createComparison(
        context: SqContext,
        type: SqType<JAVA, Boolean>,
        firstOperand: SqExpression<*, *>,
        secondOperand: SqExpression<*, *>,
        operation: String,
    ): SqComparison<JAVA>
}


interface SqBetweenTest<JAVA: Boolean?>: SqExpression<JAVA, Boolean> {
    val negation: Boolean
    val testedValue: SqExpression<*, *>
    val firstRangeValue: SqExpression<*, *>
    val secondRangeValue: SqExpression<*, *>


    override fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean) {
        val testedValueSpaceAllowed = if (asPart) {
            target.add("(", spaced = spaceAllowed)
            false
        } else {
            spaceAllowed
        }

        this.testedValue.appendSqlTo(target, asPart = true, spaceAllowed = testedValueSpaceAllowed)
        if (this.negation) {
            target.add(" NOT BETWEEN ")
        } else {
            target.add(" BETWEEN ")
        }
        this.firstRangeValue.appendSqlTo(target, asPart = true, spaceAllowed = false)
        target.add(" AND ")
        this.secondRangeValue.appendSqlTo(target, asPart = true, spaceAllowed = false)

        if (asPart) target.add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.testedValue.appendParametersTo(target)
        this.firstRangeValue.appendParametersTo(target)
        this.secondRangeValue.appendParametersTo(target)
    }
}
interface SqBetweenTestConstructor {
    fun <JAVA: Boolean?> createBetweenTest(
        context: SqContext,
        type: SqType<JAVA, Boolean>,
        negation: Boolean,
        testedValue: SqExpression<*, *>,
        firstRangeValue: SqExpression<*, *>,
        secondRangeValue: SqExpression<*, *>,
    ): SqBetweenTest<JAVA>
}

interface SqBetweenTestStart<JAVA: Any?, DB: Any> {
    val context: SqContext
    val type: SqType<JAVA, DB>
    val negation: Boolean
    val testedValue: SqExpression<*, DB>
    val firstRangeValue: SqExpression<*, DB>
}
interface SqBetweenTestStartConstructor {
    fun <JAVA: Any?, DB: Any> createBetweenTestStart(
        context: SqContext,
        type: SqType<JAVA, DB>,
        negation: Boolean,
        testedValue: SqExpression<*, DB>,
        firstRangeValue: SqExpression<*, DB>,
    ): SqBetweenTestStart<JAVA, DB>
}


interface SqInListTest<JAVA: Boolean?>: SqExpression<JAVA, Boolean> {
    val negation: Boolean
    val testedValue: SqExpression<*, *>
    val listValues: List<SqExpression<*, *>>

    override fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean) {
        val testedValueSpaceAllowed = if (asPart) {
            target.add("(", spaced = spaceAllowed)
            false
        } else {
            spaceAllowed
        }

        this.testedValue.appendSqlTo(target, asPart = true, spaceAllowed = testedValueSpaceAllowed)
        if (this.negation) {
            target.add(" NOT IN (")
        } else {
            target.add(" IN (")
        }
        this.listValues.forEachIndexed { index, listValue ->
            if (index > 0) target.add(", ")
            listValue.appendSqlTo(target, asPart = true, spaceAllowed = false)
        }

        target.add(")")
        if (asPart) target.add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.testedValue.appendParametersTo(target)
        this.listValues.forEach { it.appendParametersTo(target) }
    }
}
interface SqInListTestConstructor {
    fun <JAVA: Boolean?> createInListTest(
        context: SqContext,
        type: SqType<JAVA, Boolean>,
        negation: Boolean,
        testedValue: SqExpression<*, *>,
        listValues: List<SqExpression<*, *>>,
    ): SqInListTest<JAVA>
}


interface SqNamedFunction<JAVA: Any?, DB: Any>: SqExpression<JAVA, DB> {
    val name: String
    val nameSpaced: Boolean
    val values: List<SqItem>

    override fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean) {
        target
            .add(this.name, spaced = spaceAllowed)
            .add("(", spaced = this.nameSpaced)

        this.values.forEachIndexed { index, value ->
            if (index > 0) target.add(", ")
            value.appendSqlTo(target, asPart = true, spaceAllowed = false)
        }

        target.add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.values.forEach { it.appendParametersTo(target) }
    }
}
interface SqNamedFunctionConstructor {
    fun <JAVA: Any?, DB: Any> createNamedFunction(
        context: SqContext,
        type: SqType<JAVA, DB>,
        name: String,
        nameSpaced: Boolean?,
        values: List<SqItem>,
    ): SqNamedFunction<JAVA, DB>
}


interface SqMathOperation<JAVA: Number?>: SqExpression<JAVA, Number>

interface SqTwoOperandMathOperation<JAVA: Number?>: SqMathOperation<JAVA> {
    val firstOperand: SqExpression<*, Number>
    val operation: String
    val secondOperand: SqExpression<*, Number>

    override fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean) {
        val firstOperandSpaceAllowed = if (asPart) {
            target.add("(", spaced = spaceAllowed)
            false
        } else {
            spaceAllowed
        }

        this.firstOperand.appendSqlTo(target, asPart = true, spaceAllowed = firstOperandSpaceAllowed)
        target.add(" ").add(this.operation, spaced = false).add(" ")
        this.secondOperand.appendSqlTo(target, asPart = true, spaceAllowed = false)

        if (asPart) target.add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.firstOperand.appendParametersTo(target)
        this.secondOperand.appendParametersTo(target)
    }
}
interface SqTwoOperandMathOperationConstructor {
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
interface SqCaseItem<JAVA: Any?, DB: Any>: SqItem {
    val whenItem: SqExpression<*, Boolean>
    val thenItem: SqExpression<JAVA, DB>

    override fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean) {
        target.add("WHEN ", spaced = spaceAllowed)
        this.whenItem.appendSqlTo(target, asPart = true, spaceAllowed = false)
        target.add(" THEN ", spaced = false)
        this.thenItem.appendSqlTo(target, asPart = true, spaceAllowed = false)
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.whenItem.appendParametersTo(target)
        this.thenItem.appendParametersTo(target)
    }
}
interface SqCaseItemConstructor {
    fun <JAVA: Any?, DB: Any> createCaseItem(context: SqContext, whenItem: SqExpression<*, Boolean>, thenItem: SqExpression<JAVA, DB>): SqCaseItem<JAVA, DB>
}

interface SqCase<JAVA: Any?, DB: Any>: SqExpression<JAVA, DB> {
    val items: List<SqCaseItem<out JAVA, DB>>
    val elseItem: SqExpression<out JAVA, DB>?

    override fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean) {
        val internalSpaceAllowed = if (asPart) {
            target.add("(", spaced = spaceAllowed)
            false
        } else {
            spaceAllowed
        }

        target.add("CASE", spaced = internalSpaceAllowed)
        this.items.forEach { item ->
            target.ls()
            item.appendSqlTo(target, asPart = true, spaceAllowed = false)
        }
        this.elseItem?.let { elseItem ->
            target.ls().add("ELSE ")
            elseItem.appendSqlTo(target, asPart = true, spaceAllowed = false)
        }
        target.ls().add("END")

        if (asPart) target.add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.items.forEach { it.appendParametersTo(target) }
        this.elseItem?.appendParametersTo(target)
    }
}
interface SqCaseConstructor {
    fun <JAVA: Any?, DB: Any> createCase(
        context: SqContext,
        type: SqType<JAVA, DB>,
        items: List<SqCaseItem<out JAVA, DB>>,
        elseItem: SqExpression<out JAVA, DB>?,
    ): SqCase<JAVA, DB>
}



interface SqCaseBuildStartUntyped {
    val context: SqContext

    infix fun startWhen(condition: SqExpression<*, Boolean>): SqCaseBuildItemStartUntyped
    infix fun <JAVA: Any?, DB: Any> startElse(value: SqExpression<JAVA, DB>): SqCaseBuildEnd<JAVA, DB>
    infix fun <JAVA: Any?, DB: Any> end(type: SqType<JAVA, DB>): SqCase<JAVA, DB>
}
interface SqCaseBuildStartUntypedConstructor {
    fun createCaseBuildStartUntyped(context: SqContext): SqCaseBuildStartUntyped
}

interface SqCaseBuildItemStartUntyped {
    val context: SqContext
    val whenItem: SqExpression<*, Boolean>

    infix fun <JAVA: Any?, DB: Any> addThen(value: SqExpression<JAVA, DB>): SqCaseBuildMiddle<JAVA, DB>
}

interface SqCaseBuildItemStartTyped<JAVA: Any?, DB: Any> {
    val context: SqContext
    val type: SqType<JAVA, DB>
    val whenItem: SqExpression<*, Boolean>

    infix fun addThenNotNull(value: SqExpression<out JAVA, DB>): SqCaseBuildMiddle<JAVA, DB>
    infix fun addThenNullable(value: SqExpression<out JAVA?, DB>): SqCaseBuildMiddle<JAVA?, DB>

    infix fun addThenNotNull(value: JAVA): SqCaseBuildMiddle<JAVA, DB> =
        this.addThenNotNull(this.context.param(this.type, value))
    infix fun addThenNullable(value: JAVA?): SqCaseBuildMiddle<JAVA?, DB> =
        this.addThenNullable(this.context.param(this.type.nullable(), value))
}

interface SqCaseBuildMiddle<JAVA: Any?, DB: Any> {
    val context: SqContext
    val types: SqType<JAVA, DB>

    infix fun startWhen(condition: SqExpression<*, Boolean>): SqCaseBuildItemStartTyped<JAVA, DB>

    infix fun startElseNotNull(value: SqExpression<out JAVA, DB>): SqCaseBuildEnd<JAVA, DB>
    infix fun startElseNullable(value: SqExpression<out JAVA?, DB>): SqCaseBuildEnd<JAVA?, DB>

    fun end(): SqCase<JAVA?, DB>
}
interface SqCaseBuildMiddleConstructor {
    fun <JAVA: Any?, DB: Any> createCaseBuildMiddle(context: SqContext, type: SqType<JAVA, DB>): SqCaseBuildMiddle<JAVA, DB>
}

interface SqCaseBuildEnd<JAVA: Any?, DB: Any> {
    val context: SqContext
    val type: SqType<JAVA, DB>

    fun end(): SqCase<JAVA, DB>
}
// endregion


// region Statements - base
interface SqStatement: SqItem

interface SqConnStatement: SqStatement, SqConnItem


interface SqReadStatement: SqStatement, SqColSet {
    var firstResultIndexParam: SqParameter<Long, Number>?
    fun setFirstResultIndexValue(firstResultIndex: Long?)
    var firstResultIndex: Long?
        get() = this.firstResultIndexParam?.value
        set(value) { this.setFirstResultIndexValue(value) }


    var resultCountParam: SqParameter<Long, Number>?
    fun setResultCountValue(value: Long?)
    var resultCount: Long?
        get() = this.resultCountParam?.value
        set(value) { this.setResultCountValue(value) }
}
interface SqConnReadStatement: SqReadStatement, SqConnStatement

interface SqMultiColReadStatement: SqReadStatement, SqMultiColSet
interface SqConnMultiColReadStatement: SqMultiColReadStatement, SqConnReadStatement

interface SqSingleColReadStatement<JAVA: Any?, DB: Any>: SqReadStatement, SqSingleColSet<JAVA, DB>
interface SqConnSingleColReadStatement<JAVA: Any?, DB: Any>: SqSingleColReadStatement<JAVA, DB>, SqConnReadStatement


interface SqTableEditStatement<T: SqTable>: SqStatement {
    val table: T
}
interface SqConnTableEditStatement<T: SqTable>: SqTableEditStatement<T>, SqConnStatement


interface SqTableWriteStatement<T: SqTable>: SqTableEditStatement<T> {
    fun createValueMapping(): SqColumnValueMapping<T>
    fun applyValueMapping(mapping: SqColumnValueMapping<T>)
}
interface SqConnTableWriteStatement<T: SqTable>: SqTableWriteStatement<T>, SqConnTableEditStatement<T>
// endregion


// region Statements - join, order by, select, union
interface SqJoin: SqMultiColSet {
    val joinType: SqJoinType
    val mainColSet: SqColSet
    val joinedColSet: SqColSet

    val on: SqExpression<*, Boolean>?
    fun setOn(on: SqExpression<*, Boolean>?)


    override fun createColumnNotFoundException(column: SqColumn<*, *>): Exception {
        return IllegalStateException("Cannot find column $column in \"join column set\" $this")
    }


    override fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean) {
        this.mainColSet.definitionItem.appendSqlTo(target, asPart = true, spaceAllowed)
        target.add(this.joinType.name, spaced = true).add("JOIN", spaced = true)
        this.joinedColSet.definitionItem.appendSqlTo(target, asPart = true, spaceAllowed = true)

        this.on?.let { on ->
            target.add("ON", spaced = true)
            on.appendSqlTo(target, asPart = true, spaceAllowed = true)
        }
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.mainColSet.definitionItem.appendParametersTo(target)
        this.joinedColSet.definitionItem.appendParametersTo(target)
    }
}
interface SqJoinConstructor {
    fun createJoin(context: SqContext, joinType: SqJoinType, mainColSet: SqColSet, joinedColSet: SqColSet): SqJoin
}

interface SqOrderBy: SqItem {
    val column: SqColumn<*, *>
    val order: SqSortOrder

    override fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean) {
        this.column.appendSqlTo(target, asPart = true, spaceAllowed)
        target.add(this.order.name, spaced = true)
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) { this.column.appendParametersTo(target) }
}
interface SqOrderByConstructor {
    fun createOrderBy(context: SqContext, column: SqColumn<*, *>, order: SqSortOrder): SqOrderBy
}


interface SqSelect: SqReadStatement {
    var distinct: Boolean
    var from: List<SqColSet>?
    var where: SqExpression<*, Boolean>?
    var groupBy: List<SqColumn<*, *>>?
    var having: SqExpression<*, Boolean>?
    var orderBy: List<SqOrderBy>?

    override fun createColumnNotFoundException(column: SqColumn<*, *>): Exception {
        return IllegalArgumentException("Cannot find column $column in \"select\" statement $this")
    }
}

interface SqMultiColSelect: SqSelect, SqMultiColReadStatement {
    override var columns: List<SqColumn<*, *>>
}
interface SqMultiColSelectConstructor {
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

interface SqSingleColSelect<JAVA: Any?, DB: Any>: SqSelect, SqSingleColReadStatement<JAVA, DB> {
    override var column: SqColumn<JAVA, DB>
}
interface SqSingleColSelectConstructor {
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

interface SqConnSelect: SqSelect, SqConnReadStatement

interface SqConnMultiColSelect: SqMultiColSelect, SqConnSelect, SqConnMultiColReadStatement
interface SqConnMultiColSelectConstructor {
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

interface SqConnSingleColSelect<JAVA: Any?, DB: Any>: SqSingleColSelect<JAVA, DB>, SqConnSelect, SqConnSingleColReadStatement<JAVA, DB>
interface SqConnSingleColSelectConstructor {
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


interface SqUnion: SqReadStatement {
    var unionAll: Boolean
    val selects: List<SqSelect>

    val firstSelect: SqSelect
        get() {
            return this.selects.firstOrNull()
                ?: throw IllegalArgumentException("\"Union\" request $this has no one select request")
        }
    override val columns: List<SqColumn<*, *>>
        get() = this.firstSelect.columns

    override fun createColumnNotFoundException(column: SqColumn<*, *>): Exception {
        return IllegalArgumentException("Cannot find column $column in \"union\" statement $this")
    }
}

interface SqMultiColUnion: SqUnion, SqMultiColReadStatement {
    override var selects: List<SqSelect>
}
interface SqMultiColUnionConstructor {
    fun createMultiColUnion(context: SqContext, unionAll: Boolean, selects: List<SqSelect>): SqMultiColUnion
}

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
interface SqSingleColUnionConstructor {
    fun <JAVA: Any?, DB: Any> createSingleColUnion(context: SqContext, unionAll: Boolean, selects: List<SqSingleColSelect<JAVA, DB>>): SqSingleColUnion<JAVA, DB>
}

interface SqConnUnion: SqUnion, SqConnReadStatement

interface SqConnMultiColUnion: SqMultiColUnion, SqConnMultiColReadStatement
interface SqConnMultiColUnionConstructor {
    fun createConnMultiColUnion(context: SqContext.ConnContext, unionAll: Boolean, selects: List<SqSelect>,): SqConnMultiColUnion
}

interface SqConnSingleColUnion<JAVA: Any?, DB: Any>: SqSingleColUnion<JAVA, DB>, SqConnUnion, SqConnSingleColReadStatement<JAVA, DB>
interface SqConnSingleColUnionConstructor {
    fun <JAVA: Any?, DB: Any> createConnSingleColUnion(
        context: SqContext.ConnContext,
        unionAll: Boolean,
        selects: List<SqSingleColSelect<JAVA, DB>>,
    ): SqConnSingleColUnion<JAVA, DB>
}
// endregion


// region Statements - modification
interface SqInsert<T: SqTable>: SqTableWriteStatement<T> {
    var columns: List<SqTableColumn<*, *>>?
    var values: List<SqExpression<*, *>>?
    var select: SqReadStatement?

    override fun applyValueMapping(mapping: SqColumnValueMapping<T>) {
        this.columns(mapping.map.keys.toList()).values(mapping.map.values.toList())
    }

    override fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean) {
        val insertSpaceAllowed = if (asPart) {
            target.add("(", spaceAllowed)
            false
        } else {
            spaceAllowed
        }

        target.add("INSERT INTO ", spaced = insertSpaceAllowed)
        this.table.appendSqlTo(target, asPart = true, spaceAllowed = false)

        this.columns?.takeUnless { it.isEmpty() }?.let { columns ->
            target.add(" (")
            columns.forEachIndexed { index, column ->
                if (index > 0) target.add(", ")
                column.appendSqlTo(target, asPart = true, spaceAllowed = false)
            }
            target.add(")")
        }

        this.values?.takeUnless { it.isEmpty() }?.let { values ->
            target.add(" VALUES (")
            values.forEachIndexed { index, value ->
                if (index > 0) target.add(", ")
                value.appendSqlTo(target, asPart = true, spaceAllowed = false)
            }
            target.add(")")
        }

        this.select?.let { select ->
            target.add(" ")
            select.appendSqlTo(target, asPart = true, spaceAllowed = false)
        }

        if (asPart) target.add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.columns?.forEach { it.appendParametersTo(target) }
        this.values?.forEach { it.appendParametersTo(target) }
        this.select?.appendParametersTo(target)
    }

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
interface SqInsertConstructor {
    fun <T: SqTable> createInsert(
        context: SqContext,
        table: T,
        columns: List<SqTableColumn<*, *>>? = null,
        values: List<SqExpression<*, *>>? = null,
        select: SqReadStatement? = null,
    ): SqInsert<T>
}

interface SqConnInsert<T: SqTable>: SqInsert<T>, SqConnTableWriteStatement<T> {
    fun prepareStatement(returnGeneratedKeys: Boolean): PreparedStatement = this.prepareStatement(this.connection, returnGeneratedKeys)
}
interface SqConnInsertConstructor {
    fun <T: SqTable> createConnInsert(
        context: SqContext.ConnContext,
        table: T,
        columns: List<SqTableColumn<*, *>>? = null,
        values: List<SqExpression<*, *>>? = null,
        select: SqReadStatement? = null,
    ): SqConnInsert<T>
}


interface SqUpdate<T: SqTable>: SqTableWriteStatement<T> {
    var set: Map<SqTableColumn<*, *>, SqExpression<*, *>>?
    var where: SqExpression<*, Boolean>?

    override fun applyValueMapping(mapping: SqColumnValueMapping<T>) { this.set = LinkedHashMap(mapping.map) }

    override fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean) {
        val updateSpaceAllowed = if (asPart) {
            target.add("(", spaced = spaceAllowed)
            false
        } else {
            spaceAllowed
        }

        target.add("UPDATE ", spaced = updateSpaceAllowed)
        this.table.appendSqlTo(target, asPart = true, spaceAllowed = false)

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
                column.appendSqlTo(target, asPart = true, spaceAllowed = false)
                target.add(" = ")
                value.appendSqlTo(target, asPart = true, spaceAllowed = false)
            }
        }

        this.where?.let { where ->
            target.ls().add("WHERE ")
            where.appendSqlTo(target, asPart = true, spaceAllowed = false)
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
interface SqUpdateConstructor {
    fun <T: SqTable> createUpdate(
        context: SqContext,
        table: T,
        set: Map<SqTableColumn<*, *>, SqExpression<*, *>>? = null,
        where: SqExpression<*, Boolean>? = null,
    ): SqUpdate<T>
}

interface SqConnUpdate<T: SqTable>: SqUpdate<T>, SqConnTableWriteStatement<T>
interface SqConnUpdateConstructor {
    fun <T: SqTable> createConnUpdate(
        context: SqContext.ConnContext,
        table: T,
        set: Map<SqTableColumn<*, *>, SqExpression<*, *>>? = null,
        where: SqExpression<*, Boolean>? = null,
    ): SqConnUpdate<T>
}


interface SqDelete<T: SqTable>: SqTableEditStatement<T> {
    var where: SqExpression<*, Boolean>?

    override fun appendSqlTo(target: SqWriter, asPart: Boolean, spaceAllowed: Boolean) {
        val deleteSpaceAllowed = if (asPart) {
            target.add("(", spaced = spaceAllowed)
            false
        } else {
            spaceAllowed
        }

        target.add("DELETE FROM ", spaced = deleteSpaceAllowed)
        this.table.appendSqlTo(target, asPart = true, spaceAllowed = false)

        this.where?.let { where ->
            target.add(" WHERE ")
            where.appendSqlTo(target, asPart = true, spaceAllowed = false)
        }

        if (asPart) target.add(")")
    }

    override fun appendParametersTo(target: MutableCollection<SqParameter<*, *>>) {
        this.table.appendParametersTo(target)
        this.where?.appendParametersTo(target)
    }
}
interface SqDeleteConstructor {
    fun <T: SqTable> createDelete(
        context: SqContext,
        table: T,
        where: SqExpression<*, Boolean>? = null,
    ): SqDelete<T>
}

interface SqConnDelete<T: SqTable>: SqDelete<T>, SqConnTableEditStatement<T>
interface SqConnDeleteConstructor {
    fun <T: SqTable> createConnDelete(
        context: SqContext.ConnContext,
        table: T,
        where: SqExpression<*, Boolean>? = null,
    ): SqConnDelete<T>
}
// endregion
