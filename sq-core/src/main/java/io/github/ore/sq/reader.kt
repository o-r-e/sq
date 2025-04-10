@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.SqMappedReader.Mapper
import io.github.ore.sq.impl.SqColumnReaderImpl
import io.github.ore.sq.impl.SqMappedReaderImpl
import java.sql.ResultSet
import java.sql.Statement


// region Base reader interface
interface SqReader: AutoCloseable {
    val resultSet: ResultSet

    fun next(): Boolean =
        this.resultSet.next()

    fun readAllAsLists(): List<List<Any?>> {
        val self = this
        val columnCount = this.resultSet.metaData.columnCount
        return buildList rowList@ {
            while (self.next()) {
                val row = buildList<Any?> row@ {
                    for (index in 1 .. columnCount) {
                        this.add(self.resultSet.getObject(index))
                    }
                }
                this.add(row)
            }
        }
    }
}
// endregion


// region Column reader
interface SqColumnReader: SqReader {
    val columns: List<SqColumn<*, *>>

    fun optionalIndexOf(column: SqColumn<*, *>): Int?

    fun indexOf(column: SqColumn<*, *>): Int {
        return this.optionalIndexOf(column)
            ?: error("SQ reader <$this> has no column <$column>")
    }

    operator fun <JAVA> get(column: SqColumn<JAVA, *>): JAVA {
        val index = this.indexOf(column)
        return column.read(this.resultSet, index + 1)
    }


    fun readAllAsMaps(): List<Map<SqColumn<*, *>, Any?>> {
        val self = this
        val columns = this.columns
        return buildList rowList@ {
            while (self.next()) {
                val row = buildMap<SqColumn<*, *>, Any?> {
                    columns.forEachIndexed { index, column ->
                        this[column] = column.read(self.resultSet, index + 1)
                    }
                }
                this.add(row)
            }
        }
    }
}

fun interface SqColumnReaderFactory {
    operator fun invoke(
        context: SqContext,
        columns: List<SqColumn<*, *>>,
        resultSet: ResultSet,
        closeResultSetOnClose: Boolean,
        statement: Statement?,
        closeStatementOnClose: Boolean,
    ): SqColumnReader
}

fun <T: SqSettingsBuilder> T.columnReaderFactory(value: SqColumnReaderFactory?): T =
    this.setValue(SqColumnReaderFactory::class.java, value)
val SqSettings.columnReaderFactory: SqColumnReaderFactory
    get() = this.getValue(SqColumnReaderFactory::class.java) ?: SqColumnReaderImpl.Factory.INSTANCE
// endregion


// region Mapped reader
interface SqMappedReader<T>: SqColumnReader {
    val mapper: Mapper<T>

    fun data(): T =
        this.mapper.invoke(this)

    fun row(): Row<T> =
        Row(this.data())

    fun nextRow(): Row<T>? {
        return if (this.next()) {
            this.row()
        } else {
            null
        }
    }


    fun readAllAsObjects(): List<T> {
        val self = this
        val mapper = this.mapper
        return buildList<T> {
            while (self.next()) {
                val row = mapper.invoke(self)
                this.add(row)
            }
        }
    }


    fun interface Mapper<T> {
        operator fun invoke(reader: SqMappedReader<T>): T
    }

    data class Row<T>(val data: T)
}

interface SqMappedReaderFactory {
    fun <T> create(
        context: SqContext,
        mapper: Mapper<T>,
        columns: List<SqColumn<*, *>>,
        resultSet: ResultSet,
        closeResultSetOnClose: Boolean,
        statement: Statement?,
        closeStatementOnClose: Boolean,
    ): SqMappedReader<T>
}

fun <T: SqSettingsBuilder> T.mappedReaderFactory(value: SqMappedReaderFactory?): T =
    this.setValue(SqMappedReaderFactory::class.java, value)
val SqSettings.mappedReaderFactory: SqMappedReaderFactory
    get() = this.getValue(SqMappedReaderFactory::class.java) ?: SqMappedReaderImpl.Factory.INSTANCE
// endregion
