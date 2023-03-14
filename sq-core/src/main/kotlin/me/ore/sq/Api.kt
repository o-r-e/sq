package me.ore.sq

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement


// region Utils
interface SqWriter {
    fun ls(): SqWriter
    fun add(text: String, spaced: Boolean): SqWriter
    fun clear(): SqWriter

    fun comma(): SqWriter = this.add(",", spaced = false)
    fun dot(): SqWriter = this.add(".", spaced = false)
}

interface SqValueMapping<T: SqTable> {
    val statement: SqTableWriteStatement<T>
    val context: SqContext
        get() = this.statement.context
    val table: T
        get() = this.statement.table
    val map: MutableMap<SqColumn<*, *>, SqExpression<*, *>>

    operator fun <DB: Any> set(column: SqColumn<*, DB>, value: SqExpression<*, DB>): SqValueMapping<T> = this.apply {
        this.map[column] = value
    }

    operator fun <JAVA: Any?, DB: Any> set(column: SqColumn<JAVA, DB>, value: JAVA): SqValueMapping<T> = this.apply {
        val param = this.context.param<JAVA?, DB>(column.type.sqCast(), (value == null), value)
        this[column] = param
    }
}
// endregion


// region Base items
interface SqItem {
    val context: SqContext

    fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean)
    fun sql(): String {
        return this.context.createWriter()
            .let { writer ->
                try {
                    this.appendTo(writer, asTextPart = false, spaceAllowed = false)
                    writer.toString()
                } finally {
                    writer.clear()
                }
            }
    }
    fun parameters(): List<SqParameter<*, *>>?

    fun appendPossibleDefinitionTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
        return this.workWithPossibleAlias(
            { it.appendDefinitionTo(target, asTextPart, spaceAllowed) },
            { it.appendTo(target, asTextPart, spaceAllowed) },
        )
    }

    fun possibleDefinitionParameters(): List<SqParameter<*, *>>? {
        return this.workWithPossibleAlias(
            { it.definitionParameters() },
            { it.parameters() },
        )
    }

    fun <T: PreparedStatement> setParametersTo(target: T): T = this.parameters().setTo(target)

    fun prepareStatement(connection: Connection): PreparedStatement {
        val result = connection.prepareStatement(this.sql())
        this.setParametersTo(result)
        return result
    }
}


interface SqExpression<JAVA: Any?, DB: Any>: SqItem {
    val type: SqType<JAVA & Any>

    val nullable: Boolean
    fun nullable(): SqExpression<JAVA?, DB>

    fun read(resultSet: ResultSet, index: Int): JAVA {
        val result = if (this.nullable) {
            this.type.readNullable(resultSet, index)
        } else {
            this.type.readNotNull(resultSet, index)
        }
        return SqUtil.uncheckedCast(result)
    }
}

interface SqNull<JAVA: Any, DB: Any>: SqExpression<JAVA?, DB> {
    override val nullable: Boolean
        get() = true

    override fun nullable(): SqNull<JAVA, DB> = this

    override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
        target.add("NULL", spaced = spaceAllowed)
    }

    override fun parameters(): List<SqParameter<*, *>>? = null
}


interface SqColumn<JAVA: Any?, DB: Any>: SqExpression<JAVA, DB> {
    val columnName: String
    val safeColumnName: String
        get() = SqUtil.makeIdentifierSafeIfNeeded(this.columnName)
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

    override fun getColumnIndex(column: SqColumn<*, *>): Int? {
        return if (column == this.column) {
            0
        } else {
            null
        }
    }

    override val type: SqType<JAVA & Any>
        get() = this.column.type

    override fun nullable(): SqSingleColSet<JAVA?, DB>
}


interface SqParameter<JAVA: Any?, DB: Any>: SqExpression<JAVA?, DB> {
    val value: JAVA
    override val nullable: Boolean
        get() = (this.value == null)

    fun prepareValueForComment(): String = this.type.prepareValueForComment(this.value)

    override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
        target.add("?", spaced = spaceAllowed)

        if (this.context.printParameterValues) {
            target.add("/*", spaced = true)
            target.add(this.prepareValueForComment(), spaced = true)
            target.add("*/", spaced = true)
        }
    }

    override fun parameters(): List<SqParameter<*, *>> = listOf(this)

    override fun nullable(): SqParameter<JAVA?, DB> = SqUtil.uncheckedCast(this)

    fun write(target: PreparedStatement, index: Int) { this.type.write(target, index, this.value) }
}


interface SqStatement: SqItem

interface SqReadStatement: SqStatement, SqColSet {
    fun <T: Any?> cancelReading(): SqReadResult.CancelReading<T> = SqReadResult.CancelReading()
    fun <T: Any?> result(value: T): SqReadResult.Result<T> = SqReadResult.Result(value)
}

interface SqMultiColReadStatement: SqReadStatement, SqMultiColSet

interface SqSingleColReadStatement<JAVA: Any?, DB: Any>: SqReadStatement, SqSingleColSet<JAVA, DB> {
    override fun nullable(): SqSingleColReadStatement<JAVA?, DB>
}

interface SqTableModificationStatement<T: SqTable>: SqStatement {
    val table: T
}

interface SqTableWriteStatement<T: SqTable>: SqTableModificationStatement<T> {
    fun createValueMapping(): SqValueMapping<T>
    fun applyValueMapping(mapping: SqValueMapping<T>): SqTableWriteStatement<T>
}

interface SqConnStatement: SqStatement {
    override val context: SqConnectedContext
    val connection: Connection
        get() = this.context.connection

    fun prepareStatement(): PreparedStatement = this.prepareStatement(this.connection)
}

interface SqConnReadStatement: SqReadStatement, SqConnStatement

interface SqConnMultiColReadStatement: SqMultiColReadStatement, SqConnReadStatement

interface SqConnSingleColReadStatement<JAVA: Any?, DB: Any>: SqSingleColReadStatement<JAVA, DB>, SqConnReadStatement

interface SqConnTableModificationStatement<T: SqTable>: SqTableModificationStatement<T>, SqConnStatement

interface SqConnTableWriteStatement<T: SqTable>: SqTableWriteStatement<T>, SqConnTableModificationStatement<T>


interface SqAlias<ORIG: SqItem>: SqItem {
    val original: ORIG
    val alias: String
    val safeAlias: String
        get() = SqUtil.makeIdentifierSafeIfNeeded(this.alias)


    override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
        target.add(this.safeAlias, spaced = spaceAllowed)
    }

    fun appendDefinitionTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
        this.original.appendTo(target, asTextPart = true, spaceAllowed)
        target.add("AS", spaced = true).add(this.safeAlias, spaced = true)
    }


    override fun parameters(): List<SqParameter<*, *>>? = null

    fun definitionParameters(): List<SqParameter<*, *>>? = this.original.parameters()
}

interface SqExpressionAlias<JAVA: Any?, DB: Any, ORIG: SqExpression<JAVA, DB>>: SqColumn<JAVA, DB>, SqAlias<ORIG> {
    override val type: SqType<JAVA & Any>
        get() = this.original.type

    override val columnName: String
        get() = this.alias
}

interface SqColSetAlias<ORIG: SqColSet>: SqAlias<ORIG>, SqColSet

interface SqColSetAliasColumn<JAVA: Any?, DB: Any>: SqColumn<JAVA, DB> {
    val alias: SqColSetAlias<*>
    val column: SqColumn<JAVA, DB>

    override val columnName: String
        get() = this.column.columnName

    override val type: SqType<JAVA & Any>
        get() = this.column.type

    override fun nullable(): SqColSetAliasColumn<JAVA?, DB>

    override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
        this.alias.appendTo(target, asTextPart = true, spaceAllowed)
        target.dot().add(this.safeColumnName, spaced = false)
    }

    override fun parameters(): List<SqParameter<*, *>>? = null
}

interface SqMultiColSetAlias<ORIG: SqMultiColSet>: SqColSetAlias<ORIG>, SqMultiColSet {
    override val columns: List<SqColSetAliasColumn<*, *>>

    fun <JAVA: Any?, DB: Any> getColumn(originalColumn: SqColumn<JAVA, DB>): SqColSetAliasColumn<JAVA, DB> {
        val result = this.columns
            .firstOrNull { it.column == originalColumn }
            ?: throw IllegalStateException("Cannot find alias column for original column $originalColumn")

        return SqUtil.uncheckedCast(result)
    }
}

interface SqSingleColSetAlias<JAVA: Any?, DB: Any, ORIG: SqSingleColSet<JAVA?, DB>>: SqColSetAlias<ORIG>, SqSingleColSet<JAVA, DB> {
    override fun nullable(): SqSingleColSetAlias<JAVA?, DB, ORIG>
}
// endregion


// region Comparisons (tests)
interface SqSingleValueTest<JAVA: Boolean?>: SqExpression<JAVA, Boolean> {
    val value: SqItem

    override fun parameters(): List<SqParameter<*, *>>? = this.value.parameters()

    override fun nullable(): SqSingleValueTest<JAVA?>
}

interface SqTwoValueTest<JAVA: Boolean?>: SqExpression<JAVA, Boolean> {
    val firstValue: SqItem
    val secondValue: SqItem

    override fun parameters(): List<SqParameter<*, *>>? = SqUtil.collectParameters(this.firstValue, this.secondValue)

    override fun nullable(): SqTwoValueTest<JAVA?>
}

interface SqMultiValueTest<JAVA: Boolean?>: SqExpression<JAVA, Boolean> {
    val values: List<SqItem>

    override fun parameters(): List<SqParameter<*, *>>? = SqUtil.collectParameters(this.values)

    override fun nullable(): SqMultiValueTest<JAVA?>
}

interface SqBetweenTest<JAVA: Boolean?>: SqExpression<JAVA, Boolean> {
    val mainValue: SqItem
    val firstBoundsValue: SqItem
    val secondBoundsValue: SqItem

    override fun parameters(): List<SqParameter<*, *>>? = SqUtil.collectParameters(this.mainValue, this.firstBoundsValue, this.secondBoundsValue)

    override fun nullable(): SqBetweenTest<JAVA?>
}

interface SqInListTest<JAVA: Boolean?>: SqExpression<JAVA, Boolean> {
    val mainValue: SqItem
    val listValues: List<SqItem>

    override fun parameters(): List<SqParameter<*, *>>? = SqUtil.collectParameters(buildList {
        val self = this@SqInListTest
        this.add(self.mainValue)
        this.addAll(self.listValues)
    })

    override fun nullable(): SqInListTest<JAVA?>
}
// endregion


// region Case
interface SqCaseItemStart {
    val context: SqContext
    val whenItem: SqExpression<*, Boolean>
    infix fun <JAVA: Any?, DB: Any> then(thenItem: SqExpression<JAVA, DB>): SqCaseItem<JAVA, DB> {
        return this.context.caseItem(this.whenItem, thenItem)
    }
}

interface SqCaseItem<JAVA: Any?, DB: Any>: SqItem {
    val whenItem: SqExpression<*, Boolean>
    val thenItem: SqExpression<JAVA, DB>
}

interface SqCase<JAVA: Any?, DB: Any>: SqExpression<JAVA, DB> {
    val items: List<SqCaseItem<JAVA, DB>>
    val elseItem: SqExpression<JAVA, DB>?
}
// endregion


// region Functions
interface SqNamedFunction<JAVA: Any?, DB: Any>: SqExpression<JAVA, DB> {
    val name: String
    val nameSeparated: Boolean
    val values: List<SqItem>

    override fun parameters(): List<SqParameter<*, *>>? = SqUtil.collectParameters(this.values)

    override fun nullable(): SqNamedFunction<JAVA?, DB>

    override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
        target
            .add(this.name, spaced = spaceAllowed)
            .add("(", spaced = this.nameSeparated)

        this.values.forEachIndexed { index, value ->
            val valueSpaceAllowed = if (index > 0) {
                target.comma()
                true
            } else {
                false
            }
            value.appendTo(target, asTextPart = true, spaceAllowed = valueSpaceAllowed)
        }

        target.add(")", spaced = false)
    }
}
// endregion


// region Mathematical operations
interface SqMathOperation<JAVA: Number?>: SqExpression<JAVA, Number> {
    val operands: List<SqExpression<*, Number>>

    override fun nullable(): SqMathOperation<JAVA?>

    override fun parameters(): List<SqParameter<*, *>>? = SqUtil.collectParameters(this.operands)
}

interface SqTwoOperandMathOperation<JAVA: Number?>: SqMathOperation<JAVA> {
    val firstOperand: SqExpression<*, Number>
    val operation: String
    val secondOperand: SqExpression<*, Number>
    override val operands: List<SqExpression<*, Number>>
        get() = listOf(this.firstOperand, this.secondOperand)

    override fun nullable(): SqTwoOperandMathOperation<JAVA?>
}
// endregion


// region Statements - join, order by, select, union
interface SqJoin: SqMultiColSet {
    val type: SqJoinType
    val mainColSet: SqColSet
    val joinedColSet: SqColSet

    val on: SqExpression<*, Boolean>?
    fun on(on: SqExpression<*, Boolean>?): SqJoin


    override val columns: List<SqColumn<*, *>>
        get() = this.mainColSet.columns

    override fun createColumnNotFoundException(column: SqColumn<*, *>): Exception {
        return IllegalStateException("Cannot find column $column in \"join column set\" $this")
    }


    override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
        this.mainColSet.appendPossibleDefinitionTo(target, asTextPart = true, spaceAllowed)
        target.add(this.type.name, spaced = true).add("JOIN", spaced = true)
        this.joinedColSet.appendPossibleDefinitionTo(target, asTextPart = true, spaceAllowed = true)

        this.on?.let { on ->
            target.add("ON", spaced = true)
            on.appendTo(target, asTextPart = true, spaceAllowed = true)
        }
    }

    override fun parameters(): List<SqParameter<*, *>>? {
        return SqUtil.collectParametersFromLists(
            this.mainColSet.possibleDefinitionParameters(),
            this.joinedColSet.possibleDefinitionParameters(),
            this.on?.parameters(),
        )
    }
}

interface SqOrderBy: SqItem {
    val column: SqColumn<*, *>
    val order: SqSortOrder

    override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
        this.column.appendTo(target, asTextPart = true, spaceAllowed)
        target.add(this.order.name, spaced = true)
    }

    override fun parameters(): List<SqParameter<*, *>>? = this.column.parameters()
}


interface SqSelect: SqReadStatement {
    val distinct: Boolean

    val from: List<SqColSet>?
    fun from(from: Iterable<SqColSet>): SqSelect
    fun from(first: SqColSet, vararg more: SqColSet): SqSelect = this.from(listOf(first, *more))

    val where: SqExpression<*, Boolean>?
    fun where(condition: SqExpression<*, Boolean>?): SqSelect

    val groupBy: List<SqColumn<*, *>>?
    fun groupBy(items: Iterable<SqColumn<*, *>>): SqSelect
    fun groupBy(first: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqSelect = this.groupBy(listOf(first, *more))

    val having: SqExpression<*, Boolean>?
    fun having(condition: SqExpression<*, Boolean>?): SqSelect

    val orderBy: List<SqOrderBy>?
    fun orderBy(items: Iterable<SqOrderBy>): SqSelect
    fun orderBy(first: SqOrderBy, vararg more: SqOrderBy): SqSelect = this.orderBy(listOf(first, *more))


    val firstResultIndex: SqParameter<Int, Number>?
    fun firstResultIndex(firstResultIndex: SqParameter<Int, Number>?): SqSelect
    fun firstResultIndex(firstResultIndex: Int?): SqSelect =
        this.firstResultIndex(firstResultIndex?.let { this.context.dbIntegerParam(nullable = false, firstResultIndex) })

    val resultCount: SqParameter<Int, Number>?
    fun resultCount(resultCount: SqParameter<Int, Number>?): SqSelect
    fun resultCount(resultCount: Int?): SqSelect =
        this.resultCount(resultCount?.let { this.context.dbIntegerParam(nullable = false, resultCount) })

    fun limit(resultCount: SqParameter<Int, Number>, firstResultIndex: SqParameter<Int, Number>? = null): SqSelect =
        this.resultCount(resultCount).firstResultIndex(firstResultIndex)
    fun limit(resultCount: Int, firstResultIndex: Int? = null): SqSelect =
        this.resultCount(resultCount).firstResultIndex(firstResultIndex)


    override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) { SqUtil.appendSelect(this, target, asTextPart, spaceAllowed) }
}

interface SqMultiColSelect: SqSelect, SqMultiColReadStatement {
    override fun createColumnNotFoundException(column: SqColumn<*, *>): Exception {
        return IllegalArgumentException("Cannot find column $column in \"select\" statement $this")
    }

    override fun from(from: Iterable<SqColSet>): SqMultiColSelect
    override fun from(first: SqColSet, vararg more: SqColSet): SqMultiColSelect = this.from(listOf(first, *more))

    override fun where(condition: SqExpression<*, Boolean>?): SqMultiColSelect

    override fun groupBy(items: Iterable<SqColumn<*, *>>): SqMultiColSelect
    override fun groupBy(first: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqMultiColSelect = this.groupBy(listOf(first, *more))

    override fun having(condition: SqExpression<*, Boolean>?): SqMultiColSelect

    override fun orderBy(items: Iterable<SqOrderBy>): SqMultiColSelect
    override fun orderBy(first: SqOrderBy, vararg more: SqOrderBy): SqMultiColSelect = this.orderBy(listOf(first, *more))


    override fun firstResultIndex(firstResultIndex: SqParameter<Int, Number>?): SqMultiColSelect
    override fun firstResultIndex(firstResultIndex: Int?): SqMultiColSelect = this.apply { super.firstResultIndex(firstResultIndex) }
    override fun resultCount(resultCount: SqParameter<Int, Number>?): SqMultiColSelect
    override fun resultCount(resultCount: Int?): SqMultiColSelect = this.apply { super.resultCount(resultCount) }

    override fun limit(resultCount: SqParameter<Int, Number>, firstResultIndex: SqParameter<Int, Number>?): SqMultiColSelect =
        this.resultCount(resultCount).firstResultIndex(firstResultIndex)
    override fun limit(resultCount: Int, firstResultIndex: Int?): SqMultiColSelect =
        this.resultCount(resultCount).firstResultIndex(firstResultIndex)
}

interface SqSingleColSelect<JAVA: Any?, DB: Any>: SqSelect, SqSingleColReadStatement<JAVA, DB> {
    override fun nullable(): SqSingleColSelect<JAVA?, DB>

    override fun createColumnNotFoundException(column: SqColumn<*, *>): Exception {
        return IllegalArgumentException("Cannot find column $column in \"select\" statement $this")
    }

    override fun from(from: Iterable<SqColSet>): SqSingleColSelect<JAVA, DB>
    override fun from(first: SqColSet, vararg more: SqColSet): SqSingleColSelect<JAVA, DB> = this.from(listOf(first, *more))

    override fun where(condition: SqExpression<*, Boolean>?): SqSingleColSelect<JAVA, DB>

    override fun groupBy(items: Iterable<SqColumn<*, *>>): SqSingleColSelect<JAVA, DB>
    override fun groupBy(first: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqSingleColSelect<JAVA, DB> = this.groupBy(listOf(first, *more))

    override fun having(condition: SqExpression<*, Boolean>?): SqSingleColSelect<JAVA, DB>

    override fun orderBy(items: Iterable<SqOrderBy>): SqSingleColSelect<JAVA, DB>
    override fun orderBy(first: SqOrderBy, vararg more: SqOrderBy): SqSingleColSelect<JAVA, DB> = this.orderBy(listOf(first, *more))


    override fun firstResultIndex(firstResultIndex: SqParameter<Int, Number>?): SqSingleColSelect<JAVA, DB>
    override fun firstResultIndex(firstResultIndex: Int?): SqSingleColSelect<JAVA, DB> = this.apply { super.firstResultIndex(firstResultIndex) }
    override fun resultCount(resultCount: SqParameter<Int, Number>?): SqSingleColSelect<JAVA, DB>
    override fun resultCount(resultCount: Int?): SqSingleColSelect<JAVA, DB> = this.apply { super.resultCount(resultCount) }

    override fun limit(resultCount: SqParameter<Int, Number>, firstResultIndex: SqParameter<Int, Number>?): SqSingleColSelect<JAVA, DB> =
        this.resultCount(resultCount).firstResultIndex(firstResultIndex)
    override fun limit(resultCount: Int, firstResultIndex: Int?): SqSingleColSelect<JAVA, DB> =
        this.resultCount(resultCount).firstResultIndex(firstResultIndex)
}

interface SqConnSelect: SqSelect, SqConnReadStatement {
    override fun from(from: Iterable<SqColSet>): SqConnSelect
    override fun from(first: SqColSet, vararg more: SqColSet): SqConnSelect = this.from(listOf(first, *more))

    override fun where(condition: SqExpression<*, Boolean>?): SqConnSelect

    override fun groupBy(items: Iterable<SqColumn<*, *>>): SqConnSelect
    override fun groupBy(first: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqConnSelect = this.groupBy(listOf(first, *more))

    override fun having(condition: SqExpression<*, Boolean>?): SqConnSelect

    override fun orderBy(items: Iterable<SqOrderBy>): SqConnSelect
    override fun orderBy(first: SqOrderBy, vararg more: SqOrderBy): SqConnSelect = this.orderBy(listOf(first, *more))


    override fun firstResultIndex(firstResultIndex: SqParameter<Int, Number>?): SqConnSelect
    override fun firstResultIndex(firstResultIndex: Int?): SqConnSelect = this.apply { super.firstResultIndex(firstResultIndex) }
    override fun resultCount(resultCount: SqParameter<Int, Number>?): SqConnSelect
    override fun resultCount(resultCount: Int?): SqConnSelect = this.apply { super.resultCount(resultCount) }

    override fun limit(resultCount: SqParameter<Int, Number>, firstResultIndex: SqParameter<Int, Number>?): SqConnSelect =
        this.resultCount(resultCount).firstResultIndex(firstResultIndex)
    override fun limit(resultCount: Int, firstResultIndex: Int?): SqConnSelect =
        this.resultCount(resultCount).firstResultIndex(firstResultIndex)
}

interface SqConnMultiColSelect: SqMultiColSelect, SqConnSelect, SqConnMultiColReadStatement {
    override fun from(from: Iterable<SqColSet>): SqConnMultiColSelect
    override fun from(first: SqColSet, vararg more: SqColSet): SqConnMultiColSelect = this.from(listOf(first, *more))

    override fun where(condition: SqExpression<*, Boolean>?): SqConnMultiColSelect

    override fun groupBy(items: Iterable<SqColumn<*, *>>): SqConnMultiColSelect
    override fun groupBy(first: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqConnMultiColSelect = this.groupBy(listOf(first, *more))

    override fun having(condition: SqExpression<*, Boolean>?): SqConnMultiColSelect

    override fun orderBy(items: Iterable<SqOrderBy>): SqConnMultiColSelect
    override fun orderBy(first: SqOrderBy, vararg more: SqOrderBy): SqConnMultiColSelect = this.orderBy(listOf(first, *more))


    override fun firstResultIndex(firstResultIndex: SqParameter<Int, Number>?): SqConnMultiColSelect
    override fun firstResultIndex(firstResultIndex: Int?): SqConnMultiColSelect = this.apply { super<SqConnSelect>.firstResultIndex(firstResultIndex) }
    override fun resultCount(resultCount: SqParameter<Int, Number>?): SqConnMultiColSelect
    override fun resultCount(resultCount: Int?): SqConnMultiColSelect = this.apply { super<SqConnSelect>.resultCount(resultCount) }

    override fun limit(resultCount: SqParameter<Int, Number>, firstResultIndex: SqParameter<Int, Number>?): SqConnMultiColSelect =
        this.resultCount(resultCount).firstResultIndex(firstResultIndex)
    override fun limit(resultCount: Int, firstResultIndex: Int?): SqConnMultiColSelect =
        this.resultCount(resultCount).firstResultIndex(firstResultIndex)
}

interface SqConnSingleColSelect<JAVA: Any?, DB: Any>: SqSingleColSelect<JAVA, DB>, SqConnSelect, SqConnSingleColReadStatement<JAVA, DB> {
    override fun from(from: Iterable<SqColSet>): SqConnSingleColSelect<JAVA, DB>
    override fun from(first: SqColSet, vararg more: SqColSet): SqConnSingleColSelect<JAVA, DB> = this.from(listOf(first, *more))

    override fun where(condition: SqExpression<*, Boolean>?): SqConnSingleColSelect<JAVA, DB>

    override fun groupBy(items: Iterable<SqColumn<*, *>>): SqConnSingleColSelect<JAVA, DB>
    override fun groupBy(first: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqConnSingleColSelect<JAVA, DB> = this.groupBy(listOf(first, *more))

    override fun having(condition: SqExpression<*, Boolean>?): SqConnSingleColSelect<JAVA, DB>

    override fun orderBy(items: Iterable<SqOrderBy>): SqConnSingleColSelect<JAVA, DB>
    override fun orderBy(first: SqOrderBy, vararg more: SqOrderBy): SqConnSingleColSelect<JAVA, DB> = this.orderBy(listOf(first, *more))


    override fun firstResultIndex(firstResultIndex: SqParameter<Int, Number>?): SqConnSingleColSelect<JAVA, DB>
    override fun firstResultIndex(firstResultIndex: Int?): SqConnSingleColSelect<JAVA, DB> = this.apply { super<SqConnSelect>.firstResultIndex(firstResultIndex) }
    override fun resultCount(resultCount: SqParameter<Int, Number>?): SqConnSingleColSelect<JAVA, DB>
    override fun resultCount(resultCount: Int?): SqConnSingleColSelect<JAVA, DB> = this.apply { super<SqConnSelect>.resultCount(resultCount) }

    override fun limit(resultCount: SqParameter<Int, Number>, firstResultIndex: SqParameter<Int, Number>?): SqConnSingleColSelect<JAVA, DB> =
        this.resultCount(resultCount).firstResultIndex(firstResultIndex)
    override fun limit(resultCount: Int, firstResultIndex: Int?): SqConnSingleColSelect<JAVA, DB> =
        this.resultCount(resultCount).firstResultIndex(firstResultIndex)
}


interface SqUnion: SqReadStatement {
    val unionAll: Boolean
    val selects: List<SqSelect>

    val firstSelect: SqSelect
        get() {
            return this.selects.firstOrNull()
                ?: throw IllegalArgumentException("\"Union\" request $this has no one select request")
        }

    override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
        val firstSelectSpaceAllowed = if (asTextPart) {
            target.add("(", spaced = spaceAllowed).ls()
            false
        } else {
            spaceAllowed
        }

        val separator = if (this.unionAll) {
            "UNION ALL"
        } else {
            "UNION"
        }

        this.selects.forEachIndexed { index, select ->
            val selectSpaceAllowed = if (index == 0) {
                firstSelectSpaceAllowed
            } else {
                target.ls().add(separator, spaced = false).ls()
                false
            }

            select.appendTo(target, asTextPart = true, spaceAllowed = selectSpaceAllowed)
        }

        if (asTextPart) {
            target.ls().add(")", spaced = false)
        }
    }

    override fun parameters(): List<SqParameter<*, *>>? = SqUtil.collectParameters(this.selects)

    override val columns: List<SqColumn<*, *>>
        get() = this.firstSelect.columns
}

interface SqMultiColUnion: SqUnion, SqMultiColReadStatement {
    override fun createColumnNotFoundException(column: SqColumn<*, *>): Exception {
        return IllegalArgumentException("Cannot find column $column in \"union\" statement $this")
    }
}

interface SqSingleColUnion<JAVA: Any?, DB: Any>: SqUnion, SqSingleColReadStatement<JAVA, DB> {
    override val selects: List<SqSingleColSelect<JAVA, DB>>

    override val firstSelect: SqSingleColSelect<JAVA, DB>
        get() {
            return this.selects.firstOrNull()
                ?: throw IllegalArgumentException("\"Union\" request $this has no one select request")
        }

    override val column: SqColumn<JAVA, DB>
        get() = this.firstSelect.column

    override fun createColumnNotFoundException(column: SqColumn<*, *>): Exception {
        return IllegalArgumentException("Cannot find column $column in \"union\" statement $this")
    }

    override fun nullable(): SqSingleColUnion<JAVA?, DB>
}

interface SqConnUnion: SqUnion, SqConnReadStatement

interface SqConnMultiColUnion: SqMultiColUnion, SqConnMultiColReadStatement

interface SqConnSingleColUnion<JAVA: Any?, DB: Any>: SqSingleColUnion<JAVA, DB>, SqConnUnion, SqConnSingleColReadStatement<JAVA, DB>
// endregion


// region Statements - modification
interface SqInsert<T: SqTable>: SqTableWriteStatement<T> {
    val columns: List<SqColumn<*, *>>
    fun columns(columns: Iterable<SqColumn<*, *>>?): SqInsert<T>
    fun columns(first: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqInsert<T> = this.columns(listOf(first, *more))

    val values: List<SqExpression<*, *>>?
    fun values(values: Iterable<SqExpression<*, *>>?): SqInsert<T>
    fun values(first: SqExpression<*, *>, vararg more: SqExpression<*, *>): SqInsert<T> = this.values(listOf(first, *more))

    val select: SqReadStatement?
    fun select(select: SqReadStatement?): SqInsert<T>


    override fun applyValueMapping(mapping: SqValueMapping<T>): SqInsert<T> {
        return this
            .columns(mapping.map.keys)
            .values(mapping.map.values)
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

interface SqConnInsert<T: SqTable>: SqInsert<T>, SqConnTableWriteStatement<T> {
    override fun columns(columns: Iterable<SqColumn<*, *>>?): SqConnInsert<T>
    override fun columns(first: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqConnInsert<T> = this.columns(listOf(first, *more))

    override fun values(values: Iterable<SqExpression<*, *>>?): SqConnInsert<T>
    override fun values(first: SqExpression<*, *>, vararg more: SqExpression<*, *>): SqConnInsert<T> = this.values(listOf(first, *more))

    override fun select(select: SqReadStatement?): SqConnInsert<T>


    override fun applyValueMapping(mapping: SqValueMapping<T>): SqConnInsert<T> = this.apply { super.applyValueMapping(mapping) }


    fun prepareStatement(returnGeneratedKeys: Boolean): PreparedStatement = this.prepareStatement(this.connection, returnGeneratedKeys)
}


interface SqUpdate<T: SqTable>: SqTableWriteStatement<T> {
    val set: Map<SqColumn<*, *>, SqExpression<*, *>>
    fun set(columnValueMap: Map<SqColumn<*, *>, SqExpression<*, *>>): SqUpdate<T>
    fun set(vararg columnValuePairs: Pair<SqColumn<*, *>, SqExpression<*, *>>): SqUpdate<T> = this.set(mapOf(*columnValuePairs))

    val where: SqExpression<*, Boolean>?
    fun where(condition: SqExpression<*, Boolean>?): SqUpdate<T>


    override fun applyValueMapping(mapping: SqValueMapping<T>): SqUpdate<T> = this.set(mapping.map)
}

interface SqConnUpdate<T: SqTable>: SqUpdate<T>, SqConnTableWriteStatement<T> {
    override fun set(columnValueMap: Map<SqColumn<*, *>, SqExpression<*, *>>): SqConnUpdate<T>
    override fun set(vararg columnValuePairs: Pair<SqColumn<*, *>, SqExpression<*, *>>): SqConnUpdate<T> = this.set(mapOf(*columnValuePairs))

    override fun where(condition: SqExpression<*, Boolean>?): SqConnUpdate<T>


    override fun applyValueMapping(mapping: SqValueMapping<T>): SqConnUpdate<T> = this.set(mapping.map)
}


interface SqDelete<T: SqTable>: SqTableModificationStatement<T> {
    val where: SqExpression<*, Boolean>?
    fun where(condition: SqExpression<*, Boolean>?): SqDelete<T>

    override fun parameters(): List<SqParameter<*, *>>? = this.where?.parameters()
}

interface SqConnDelete<T: SqTable>: SqDelete<T>, SqConnTableModificationStatement<T> {
    override fun where(condition: SqExpression<*, Boolean>?): SqConnDelete<T>
}
// endregion
