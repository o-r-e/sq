package io.github.ore.sq.impl

import io.github.ore.sq.SqColumn
import io.github.ore.sq.SqColumnReader
import io.github.ore.sq.SqColumnReaderFactory
import io.github.ore.sq.SqContext
import io.github.ore.sq.SqMappedReader
import io.github.ore.sq.SqMappedReader.Mapper
import io.github.ore.sq.SqMappedReaderFactory
import io.github.ore.sq.util.SqUtil
import java.sql.ResultSet
import java.sql.Statement


// region Column reader
open class SqColumnReaderImpl(
    override val resultSet: ResultSet,
    override val columns: List<SqColumn<*, *>>,
    protected open val columnIndexMap: Map<SqColumn<*, *>, Int>,
    protected open val closeResultSetOnClose: Boolean = true,
    protected open val statement: Statement? = resultSet.statement,
    protected open val closeStatementOnClose: Boolean = true,
): SqColumnReader {
    override fun optionalIndexOf(column: SqColumn<*, *>): Int? =
        this.columnIndexMap[column]

    override fun close() {
        val exceptions = ArrayList<Throwable>()
        try {
            // Close result set
            try {
                if (this.closeResultSetOnClose) {
                    this.resultSet.close()
                }
            } catch (e: Exception) {
                exceptions.add(e)
            }

            // Close statement
            try {
                if (this.closeStatementOnClose) {
                    this.statement?.close()
                }
            } catch (e: Exception) {
                exceptions.add(e)
            }

            SqUtil.toSingleException(exceptions) { IllegalStateException("Multiple exceptions while closing SQ reader") }
        } finally {
            exceptions.clear()
        }
    }


    open class Factory: SqColumnReaderFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(
            context: SqContext,
            columns: List<SqColumn<*, *>>,
            resultSet: ResultSet,
            closeResultSetOnClose: Boolean,
            statement: Statement?,
            closeStatementOnClose: Boolean,
        ): SqColumnReaderImpl {
            if (columns.isEmpty()) {
                error("Column list is empty")
            }
            val columnIndexMap = buildMap(columns.size) {
                columns.forEachIndexed { index, column ->
                    this[column] = index
                }
            }
            return SqColumnReaderImpl(resultSet, columns, columnIndexMap, closeResultSetOnClose, statement, closeStatementOnClose)
        }
    }
}
// endregion


// region Mapped reader
open class SqMappedReaderImpl<T>(
    override val mapper: Mapper<T>,
    resultSet: ResultSet,
    columns: List<SqColumn<*, *>>,
    columnIndexMap: Map<SqColumn<*, *>, Int>,
    closeResultSetOnClose: Boolean = true,
    statement: Statement? = resultSet.statement,
    closeStatementOnClose: Boolean = true,
): SqColumnReaderImpl(resultSet, columns, columnIndexMap, closeResultSetOnClose, statement, closeStatementOnClose), SqMappedReader<T> {
    open class Factory: SqMappedReaderFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun <T> create(
            context: SqContext,
            mapper: Mapper<T>,
            columns: List<SqColumn<*, *>>,
            resultSet: ResultSet,
            closeResultSetOnClose: Boolean,
            statement: Statement?,
            closeStatementOnClose: Boolean,
        ): SqMappedReaderImpl<T> {
            if (columns.isEmpty()) {
                error("Column list is empty")
            }
            val columnIndexMap = buildMap(columns.size) {
                columns.forEachIndexed { index, column ->
                    this[column] = index
                }
            }
            return SqMappedReaderImpl(mapper, resultSet, columns, columnIndexMap, closeResultSetOnClose, statement, closeStatementOnClose)
        }
    }
}
// endregion
