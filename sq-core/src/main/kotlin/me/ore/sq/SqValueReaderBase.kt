package me.ore.sq

import java.sql.ResultSet


abstract class SqValueReaderBase<JAVA: Any>: SqValueReader<JAVA> {
    protected abstract fun simpleRead(source: ResultSet, columnIndex: Int): JAVA?
    protected abstract val expectedClass: Class<JAVA>?
    protected abstract fun complexRead(expectedClassException: Exception?, source: ResultSet, columnIndex: Int, value: Any): JAVA?

    override fun readNullable(source: ResultSet, columnIndex: Int): JAVA? {
        val simpleReadResult = this.simpleRead(source, columnIndex)
        if (simpleReadResult != null) return simpleReadResult

        var expectedClassException: Exception? = null
        val expectedClass = this.expectedClass
        if (expectedClass != null) {
            try {
                return source.getObject(columnIndex, expectedClass)
                    ?: error("Read by expected class - got NULL for column index $columnIndex and class ${expectedClass.name}")
            } catch (e: Exception) {
                expectedClassException = e
            }
        }

        return try {
            source.getObject(columnIndex)?.let { value ->
                this.complexRead(expectedClassException, source, columnIndex, value)
            }
        } catch (e: Exception) {
            expectedClassException?.let { e.addSuppressed(it) }
            throw e
        }
    }
}
