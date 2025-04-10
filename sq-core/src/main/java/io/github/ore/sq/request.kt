@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqDataModificationRequestTemplateImpl
import io.github.ore.sq.impl.SqExpressionReturningRequestImpl
import io.github.ore.sq.impl.SqReadRequestTemplateImpl
import io.github.ore.sq.impl.SqRecordReloadRequestImpl
import io.github.ore.sq.impl.SqReturningRequestImpl
import io.github.ore.sq.util.SqItemPartConfig
import io.github.ore.sq.util.bracketsAreOptional
import io.github.ore.sq.util.SqUtil
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


interface SqRequest: SqItem

interface SqRequestTemplate {
    val context: SqContext
    val data: SqJdbcRequestData
}


//region Read request
interface SqReadRequest: SqRequest, SqColumnSource {
    fun prepareStatement(connection: Connection, context: SqContext = SqContext.last): PreparedStatement =
        this.createJdbcRequestData(context).prepareStatement(connection)

    fun execute(connection: Connection, context: SqContext = SqContext.last): SqColumnReader {
        return execute(connection, context, this) { preparedStatement, resultSet ->
            context.settings.columnReaderFactory
                .invoke(context, this.columns, resultSet, closeResultSetOnClose = true, preparedStatement, closeStatementOnClose = true)
        }
    }

    fun <T> execute(connection: Connection, context: SqContext, mapper: SqMappedReader.Mapper<T>): SqMappedReader<T> {
        return execute(connection, context, this) { preparedStatement, resultSet ->
            context.settings.mappedReaderFactory
                .create(context, mapper, this.columns, resultSet, closeResultSetOnClose = true, preparedStatement, closeStatementOnClose = true)
        }
    }

    fun toTemplate(context: SqContext = SqContext.last): SqReadRequestTemplate {
        return context.settings.readRequestTemplateFactory
            .invoke(context, this.createJdbcRequestData(context, null), this.columns)
    }
}

interface SqExpressionReadRequest<JAVA, DB: Any>: SqReadRequest, SqExpressionColumnSource<JAVA, DB>

interface SqReadRequestTemplate: SqRequestTemplate {
    val columns: List<SqColumn<*, *>>

    fun prepareStatement(connection: Connection): PreparedStatement =
        this.data.prepareStatement(connection)
}

fun interface SqReadRequestTemplateFactory {
    fun invoke(context: SqContext, data: SqJdbcRequestData, columns: List<SqColumn<*, *>>): SqReadRequestTemplate
}

fun <T: SqSettingsBuilder> T.readRequestTemplateFactory(value: SqReadRequestTemplateFactory?): T =
    this.setValue(SqReadRequestTemplateFactory::class.java, value)

val SqSettings.readRequestTemplateFactory: SqReadRequestTemplateFactory
    get() = this.getValue(SqReadRequestTemplateFactory::class.java) ?: SqReadRequestTemplateImpl.Factory.INSTANCE


private inline fun <T> execute(
    connection: Connection,
    context: SqContext,
    readRequest: SqReadRequest,
    block: (preparedStatement: PreparedStatement, resultSet: ResultSet) -> T,
): T {
    val preparedStatement = readRequest.prepareStatement(connection, context)
    preparedStatement.closeOnCompletion()
    return try {
        val resultSet = preparedStatement.executeQuery()
        try {
            block(preparedStatement, resultSet)
        } catch (e: Exception) {
            // Close result set
            try {
                resultSet.close()
            } catch (closeException: Exception) {
                e.addSuppressed(closeException)
            }

            throw e
        }
    } catch (e: Exception) {
        // Close statement
        try {
            preparedStatement.close()
        } catch (closeException: Exception) {
            e.addSuppressed(closeException)
        }

        throw e
    }
}


inline fun <T> SqReadRequest.execute(connection: Connection, context: SqContext = SqContext.last, block: (reader: SqColumnReader) -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return this.execute(connection, context).use(block)
}

fun <T> SqReadRequest.execute(connection: Connection, mapper: SqMappedReader.Mapper<T>): SqMappedReader<T> =
    this.execute(connection, SqContext.last, mapper)

inline fun <T, R> SqReadRequest.execute(
    connection: Connection,
    context: SqContext,
    mapper: SqMappedReader.Mapper<T>,
    block: (reader: SqMappedReader<T>) -> R,
): R {
    return this.execute(connection, context, mapper).use(block)
}

inline fun <T, R> SqReadRequest.execute(
    connection: Connection,
    mapper: SqMappedReader.Mapper<T>,
    block: (reader: SqMappedReader<T>) -> R,
): R {
    return this.execute(connection, SqContext.last, mapper).use(block)
}


fun SqReadRequestTemplate.execute(
    connection: Connection,
    context: SqContext? = null,
    closeResultSetOnClose: Boolean = true,
    closeStatementOnClose: Boolean = true,
): SqColumnReader {
    val actualContext = context ?: this.context

    var optPreparedStatement: PreparedStatement? = null
    var optResultSet: ResultSet? = null
    return try {
        val preparedStatement = this.prepareStatement(connection)
        optPreparedStatement = preparedStatement

        val resultSet = preparedStatement.executeQuery()
        optResultSet = resultSet

        actualContext.settings.columnReaderFactory.invoke(
            context = actualContext,
            columns = this.columns,
            resultSet = resultSet,
            closeResultSetOnClose = closeResultSetOnClose,
            statement = preparedStatement,
            closeStatementOnClose = closeStatementOnClose,
        )
    } catch (e: Exception) {
        // Close result set
        optResultSet?.let {
            try {
                it.close()
            } catch (closeException: Exception) {
                e.addSuppressed(closeException)
            }
        }

        // Close prepared statement
        optPreparedStatement?.let {
            try {
                it.close()
            } catch (closeException: Exception) {
                e.addSuppressed(closeException)
            }
        }

        throw e
    }
}

inline fun <T> SqReadRequestTemplate.execute(
    connection: Connection,
    context: SqContext? = null,
    closeResultSetOnClose: Boolean = true,
    closeStatementOnClose: Boolean = true,
    block: (reader: SqColumnReader) -> T,
): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return this.execute(connection, context, closeResultSetOnClose, closeStatementOnClose).use(block)
}

fun <T> SqReadRequestTemplate.execute(
    connection: Connection,
    mapper: SqMappedReader.Mapper<T>,
    context: SqContext? = null,
    closeResultSetOnClose: Boolean = true,
    closeStatementOnClose: Boolean = true,
): SqMappedReader<T> {
    val actualContext = context ?: this.context

    var optPreparedStatement: PreparedStatement? = null
    var optResultSet: ResultSet? = null
    @Suppress("DuplicatedCode")
    return try {
        val preparedStatement = this.prepareStatement(connection)
        optPreparedStatement = preparedStatement

        val resultSet = preparedStatement.executeQuery()
        optResultSet = resultSet

        actualContext.settings.mappedReaderFactory.create(
            context = actualContext,
            mapper = mapper,
            columns = this.columns,
            resultSet = resultSet,
            closeResultSetOnClose = closeResultSetOnClose,
            statement = preparedStatement,
            closeStatementOnClose = closeStatementOnClose,
        )
    } catch (e: Exception) {
        // Close result set
        optResultSet?.let {
            try {
                it.close()
            } catch (closeException: Exception) {
                e.addSuppressed(closeException)
            }
        }

        // Close prepared statement
        optPreparedStatement?.let {
            try {
                it.close()
            } catch (closeException: Exception) {
                e.addSuppressed(closeException)
            }
        }

        throw e
    }
}

inline fun <R, T> SqReadRequestTemplate.execute(
    connection: Connection,
    mapper: SqMappedReader.Mapper<R>,
    context: SqContext? = null,
    closeResultSetOnClose: Boolean = true,
    closeStatementOnClose: Boolean = true,
    block: (reader: SqMappedReader<R>) -> T,
): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return this.execute(connection, mapper, context, closeResultSetOnClose, closeStatementOnClose).use(block)
}
// endregion


// region Data modification request
interface SqDataModificationRequest: SqRequest {
    fun prepareStatement(connection: Connection, context: SqContext = SqContext.last): PreparedStatement =
        this.createJdbcRequestData(context).prepareStatement(connection)

    fun execute(connection: Connection, context: SqContext = SqContext.last): Long {
        return this.prepareStatement(connection, context).use { preparedStatement ->
            SqUtil.executeUpdate(preparedStatement)
        }
    }

    fun toTemplate(context: SqContext = SqContext.last): SqDataModificationRequestTemplate {
        return context.settings.dataModificationRequestTemplateFactory
            .invoke(context, this.createJdbcRequestData(context, null))
    }
}

interface SqDataModificationRequestTemplate: SqRequestTemplate {
    fun prepareStatement(connection: Connection): PreparedStatement =
        this.data.prepareStatement(connection)

    fun execute(connection: Connection): Long {
        return this.prepareStatement(connection).use { preparedStatement ->
            try {
                preparedStatement.executeLargeUpdate()
            } catch (e: UnsupportedOperationException) {
                preparedStatement.executeUpdate().toLong()
            }
        }
    }
}

fun interface SqDataModificationRequestTemplateFactory {
    fun invoke(context: SqContext, data: SqJdbcRequestData): SqDataModificationRequestTemplate
}

fun <T: SqSettingsBuilder> T.dataModificationRequestTemplateFactory(value: SqDataModificationRequestTemplateFactory?): T =
    this.setValue(SqDataModificationRequestTemplateFactory::class.java, value)

val SqSettings.dataModificationRequestTemplateFactory: SqDataModificationRequestTemplateFactory
    get() = this.getValue(SqDataModificationRequestTemplateFactory::class.java) ?: SqDataModificationRequestTemplateImpl.Factory.INSTANCE
// endregion


// region Returning request
interface SqDataModificationReturnableRequest: SqDataModificationRequest


interface SqReturningRequest: SqReadRequest {
    val wrappedRequest: SqItem

    override val isMultiline: Boolean
        get() = true

    fun addToBuilder(context: SqContext, target: SqJdbcRequestDataBuilder) {
        this.wrappedRequest.addToBuilder(context, target, null)

        target.spaceOrNewLine().keyword("returning").indent {
            @Suppress("DuplicatedCode")
            this.columns.forEachIndexed { index, column ->
                if (index > 0) {
                    target.comma().space()
                }
                target.identifier(column.name)
            }
        }
    }

    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        if (partConfig.bracketsAreOptional()) {
            this.addToBuilder(context, target)
        } else {
            target.bracketsWithIndent {
                this.addToBuilder(context, target)
            }
        }
    }
}

fun interface SqReturningRequestFactory {
    operator fun invoke(context: SqContext, wrappedRequest: SqItem, columns: List<SqColumn<*, *>>): SqReturningRequest
}

fun <T: SqSettingsBuilder> T.returningRequestFactory(value: SqReturningRequestFactory?): T =
    this.setValue(SqReturningRequestFactory::class.java, value)
val SqSettings.returningRequestFactory: SqReturningRequestFactory
    get() = this.getValue(SqReturningRequestFactory::class.java) ?: SqReturningRequestImpl.Factory.INSTANCE


interface SqExpressionReturningRequest<JAVA, DB: Any>: SqReturningRequest, SqExpressionReadRequest<JAVA, DB>

interface SqExpressionReturningRequestFactory {
    fun <JAVA, DB: Any> create(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, DB>,
        wrappedRequest: SqItem,
        columns: List<SqColumn<*, *>>,
    ): SqExpressionReturningRequest<JAVA, DB>
}

fun <T: SqSettingsBuilder> T.expressionReturningRequestFactory(value: SqExpressionReturningRequestFactory): T =
    this.setValue(SqExpressionReturningRequestFactory::class.java, value)
val SqSettings.expressionReturningRequestFactory: SqExpressionReturningRequestFactory
    get() = this.getValue(SqExpressionReturningRequestFactory::class.java) ?: SqExpressionReturningRequestImpl.Factory.INSTANCE


fun SqDataModificationReturnableRequest.returning(
    columns: List<SqColumn<*, *>>,
    context: SqContext = SqContext.last,
): SqReturningRequest {
    return context.settings.returningRequestFactory.invoke(context, this, columns)
}

fun SqDataModificationReturnableRequest.returning(
    firstColumn: SqColumn<*, *>,
    secondColumn: SqColumn<*, *>,
    vararg moreColumns: SqColumn<*, *>,
    context: SqContext = SqContext.last,
): SqReturningRequest {
    return this.returning(listOf(firstColumn, secondColumn, *moreColumns), context)
}

fun <JAVA, DB: Any> SqDataModificationReturnableRequest.returning(
    column: SqColumn<JAVA, DB>,
    context: SqContext = SqContext.last,
): SqExpressionReturningRequest<JAVA, DB> {
    return context.settings.expressionReturningRequestFactory.create(context, column.reader, this, listOf(column))
}
// endregion


// region Record reload request
interface SqRecordReloadRequest<T: SqRecord> {
    val wrappedRequest: SqItem
    val config: ReloadableConfig<T>

    fun prepareStatement(connection: Connection, context: SqContext = SqContext.last): PreparedStatement {
        val columnNames = this.config.columnNames
        val jdbcRequestData = this.wrappedRequest.createJdbcRequestData(context)
        return if (columnNames.isEmpty()) {
            jdbcRequestData.prepareStatement(connection)
        } else {
            jdbcRequestData.prepareStatement(connection, columnNames)
        }
    }

    fun reloadRecords(
        connection: Connection,
        affectedRecordCount: Long,
        executedStatement: PreparedStatement,
        context: SqContext = SqContext.last,
    ): List<T> {
        val config = this.config
        if (config.columnNames.isNotEmpty()) {
            val generatedKeys = executedStatement.generatedKeys
                ?: error("SQL statement object has no generated keys after SQL data modification request, property \"generatedKeys\" contains NULL")

            generatedKeys.use {
                config.reloadRecords(generatedKeys)
            }
        }

        return config.records
    }

    fun execute(connection: Connection, context: SqContext = SqContext.last): List<T> {
        return this.prepareStatement(connection, context).use { preparedStatement ->
            val affectedRecordCount = SqUtil.executeUpdate(preparedStatement)
            this.reloadRecords(connection, affectedRecordCount, preparedStatement, context)
        }
    }


    open class ReloadableRecordField(
        open val columnNumber: Int,
        open val reader: SqDataTypeReader<out Any?, *>,
        open val setter: SqRecordClassInfo.ValueSetter<SqRecord>,
    ) {
        open fun read(source: ResultSet, target: SqRecord) {
            val value = this.reader[source, this.columnNumber]
            this.setter[target] = value
        }
    }

    open class ReloadableRecord<T: SqRecord>(
        open val record: T,
        open val fields: List<ReloadableRecordField>,
    ) {
        open fun read(source: ResultSet) {
            this.fields.forEach { it.read(source, this.record) }
        }
    }

    open class ReloadableConfig<T: SqRecord>(
        val columns: List<SqTableColumn<*, *>>,
        val reloadableRecords: List<ReloadableRecord<T>>,
    ) {
        companion object {
            protected val EMPTY: ReloadableConfig<SqRecord> = ReloadableConfig(emptyList(), emptyList())

            fun <T: SqRecord> emptyInstance(): ReloadableConfig<T> {
                @Suppress("UNCHECKED_CAST")
                return EMPTY as ReloadableConfig<T>
            }

            inline fun <T: SqRecord> create(
                records: Collection<T>,
                block: (record: T, classInfo: SqRecordClassInfo<out T>, recordIndex: Int, propertyName: String, delegate: SqRecordFieldDelegate<out Any?>) -> Boolean,
            ): ReloadableConfig<T> {
                contract { callsInPlace(block, InvocationKind.UNKNOWN) }

                val columnMap = LinkedHashMap<String, Pair<Int, SqTableColumn<*, *>>>()
                val reloadableRecords = ArrayList<ReloadableRecord<T>>(records.size)

                records.forEachIndexed { recordIndex, record ->
                    val recordClassInfo = SqRecordClassInfo[record.javaClass]
                    val delegateGetterMap = recordClassInfo.delegateGetterMap
                    val valueSetterMap = recordClassInfo.valueSetterMap
                    val reloadableFields = ArrayList<ReloadableRecordField>(delegateGetterMap.size)

                    val reloadableRecord = ReloadableRecord(record, reloadableFields)
                    reloadableRecords.add(reloadableRecord)

                    delegateGetterMap.forEach { (propertyName, delegateGetter) ->
                        val delegate = delegateGetter[record]

                        if (block(record, recordClassInfo, recordIndex, propertyName, delegate)) {
                            val valueSetter = valueSetterMap[propertyName]
                                ?: error("$recordClassInfo has no value setter for property \"$propertyName\"")
                            val column = delegate.column
                            val columnName = column.name

                            val columnNumber = if (columnMap.containsKey(columnName)) {
                                columnMap[columnName]!!.first
                            } else {
                                val columnNumber = columnMap.size + 1
                                columnMap[columnName] = Pair(columnNumber, column)
                                columnNumber
                            }

                            @Suppress("UNCHECKED_CAST")
                            val reloadableField = ReloadableRecordField(columnNumber, column.reader, valueSetter as SqRecordClassInfo.ValueSetter<SqRecord>)
                            reloadableFields.add(reloadableField)
                        }
                    }
                }

                return try {
                    ReloadableConfig(
                        columns = columnMap.map { it.value.second },
                        reloadableRecords = reloadableRecords,
                    )
                } finally {
                    columnMap.clear()
                }
            }
        }


        protected open var mutableColumnNames: Array<String>? = null

        val columnNames: Array<String>
            get() {
                return this.mutableColumnNames ?: run {
                    val result = this.columns.map { it.name }.toTypedArray()
                    this.mutableColumnNames = result
                    result
                }
            }

        protected open var mutableRecords: List<T>? = null

        val records: List<T>
            get() {
                return this.mutableRecords ?: run {
                    val result = this.reloadableRecords.map { it.record }
                    this.mutableRecords = result
                    result
                }
            }

        open fun reloadRecords(source: ResultSet) {
            this.reloadableRecords.forEachIndexed { recordIndex, record ->
                if (!source.next()) {
                    error("Result set <$source> has no row with index #$recordIndex")
                }
                record.read(source)
            }
        }
    }
}

interface SqRecordReloadRequestFactory {
    fun <T: SqRecord> create(
        context: SqContext,
        wrappedRequest: SqItem,
        config: SqRecordReloadRequest.ReloadableConfig<T>,
    ): SqRecordReloadRequest<T>
}

fun <T: SqSettingsBuilder> T.recordReloadRequestFactory(value: SqRecordReloadRequestFactory?): T =
    this.setValue(SqRecordReloadRequestFactory::class.java, value)
val SqSettings.recordReloadRequestFactory: SqRecordReloadRequestFactory
    get() = this.getValue(SqRecordReloadRequestFactory::class.java) ?: SqRecordReloadRequestImpl.Factory.INSTANCE
// endregion
