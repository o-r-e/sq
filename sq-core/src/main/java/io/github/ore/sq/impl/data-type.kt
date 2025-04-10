@file:Suppress("unused")

package io.github.ore.sq.impl

import io.github.ore.sq.SqDataTypePack
import io.github.ore.sq.SqDataTypeReadAction
import io.github.ore.sq.SqDataTypeReader
import io.github.ore.sq.SqDataTypeWriteAction
import io.github.ore.sq.SqDataTypeWriter
import io.github.ore.sq.SqDataTypeWriterConvertAction
import io.github.ore.sq.SqDataTypes
import io.github.ore.sq.util.SqBlobInputStream
import io.github.ore.sq.util.SqClobReader
import io.github.ore.sq.util.SqUtil
import java.io.InputStream
import java.io.Reader
import java.math.BigDecimal
import java.net.URL
import java.sql.Blob
import java.sql.Clob
import java.sql.Date
import java.sql.NClob
import java.sql.PreparedStatement
import java.sql.Ref
import java.sql.ResultSet
import java.sql.RowId
import java.sql.SQLXML
import java.sql.Time
import java.sql.Timestamp
import java.sql.Types
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime


// region Util classes
open class SqDataTypeReaderNotNullImpl<JAVA: Any, DB: Any>(
    open val action: SqDataTypeReadAction<JAVA>,
): SqDataTypeReader<JAVA, DB> {
    override fun get(source: ResultSet, index: Int): JAVA {
        val result = this.action[source, index]
        if (result == null) {
            error("Column with index $index expected to be NOT NULL, but it contains NULL value")
        }
        return result
    }

    override val isNullable: Boolean
        get() = false
    override lateinit var nullableReader: SqDataTypeReader<JAVA?, DB>
    override val notNullReader: SqDataTypeReader<JAVA, DB>
        get() = this
}

open class SqDataTypeReaderNullableImpl<JAVA: Any, DB: Any>(
    open val action: SqDataTypeReadAction<JAVA>,
): SqDataTypeReader<JAVA?, DB> {
    override fun get(source: ResultSet, index: Int): JAVA? =
        this.action[source, index]

    override val isNullable: Boolean
        get() = true
    override lateinit var notNullReader: SqDataTypeReader<JAVA, DB>
    override val nullableReader: SqDataTypeReader<JAVA?, DB>
        get() = this
}

open class SqDataTypeWriterImpl<JAVA: Any, DB: Any>(
    open val jdbcType: Int,
    open val dbTypeName: String? = null,
    open val action: SqDataTypeWriteAction<JAVA>,
): SqDataTypeWriter<JAVA, DB> {
    override fun set(target: PreparedStatement, index: Int, value: JAVA?) {
        if (value == null) {
            val dbTypeName = this.dbTypeName
            if (dbTypeName == null) {
                target.setNull(index, this.jdbcType)
            } else {
                target.setNull(index, this.jdbcType, dbTypeName)
            }
        } else {
            this.action[target, index] = value
        }
    }
}

open class SqDataTypePackImpl<JAVA: Any, DB: Any>(
    override val notNullReader: SqDataTypeReader<JAVA, DB>,
    override val nullableReader: SqDataTypeReader<JAVA?, DB>,
    override val writer: SqDataTypeWriter<JAVA, DB>,
): SqDataTypePack<JAVA, DB>

fun <JAVA, DB: Any> sqDataTypeReaderPair(
    read: SqDataTypeReadAction<JAVA & Any>,
): Pair<SqDataTypeReaderNotNullImpl<JAVA & Any, DB>, SqDataTypeReaderNullableImpl<JAVA & Any, DB>> {
    val notNullReader = SqDataTypeReaderNotNullImpl<JAVA & Any, DB>(read)
    val nullableReader = SqDataTypeReaderNullableImpl<JAVA & Any, DB>(read)
    notNullReader.nullableReader = nullableReader
    nullableReader.notNullReader = notNullReader
    return Pair(notNullReader, nullableReader)
}

fun <JAVA: Any, DB: Any> sqDataTypePack(
    jdbcType: Int,
    dbTypeName: String?,
    read: SqDataTypeReadAction<JAVA>,
    write: SqDataTypeWriteAction<JAVA>,
): SqDataTypePack<JAVA, DB> {
    val (notNullReader, nullableReader) = sqDataTypeReaderPair<JAVA, DB>(read)
    val writer = SqDataTypeWriterImpl<JAVA, DB>(jdbcType, dbTypeName, write)
    return SqDataTypePackImpl(notNullReader, nullableReader, writer)
}

fun <JAVA: Any, DB: Any> sqDataTypePack(
    jdbcType: Int,
    read: SqDataTypeReadAction<JAVA>,
    write: SqDataTypeWriteAction<JAVA>,
): SqDataTypePack<JAVA, DB> {
    return sqDataTypePack(jdbcType, null, read, write)
}


abstract class SqDataTypeArrayReadActionBase<E>: SqDataTypeReadAction<List<E>> {
    companion object {
        protected const val VALUE_COLUMN_INDEX: Int = 2
    }


    protected abstract fun read(elementSource: ResultSet, @Suppress("SameParameterValue") columnIndex: Int, elementIndex: Int): E

    override fun get(source: ResultSet, index: Int): List<E>? {
        return source.getArray(index)?.let { sqlArray ->
            try {
                sqlArray.resultSet?.use { resultSet ->
                    val self = this
                    buildList<E> {
                        while (resultSet.next()) {
                            val value = self.read(resultSet, VALUE_COLUMN_INDEX, this.size)
                            this.add(value)
                        }
                    }
                }
            } finally {
                sqlArray.free()
            }
        }
    }


    open class NotNullImpl<E>(
        protected open val elementReadAction: SqDataTypeReadAction<E & Any>,
    ): SqDataTypeArrayReadActionBase<E>() {
        override fun read(elementSource: ResultSet, columnIndex: Int, elementIndex: Int): E {
            val result = this.elementReadAction[elementSource, columnIndex]
            if (result == null) {
                error("Array element with index $elementIndex expected to be NOT NULL, but it is NULL")
            }
            return result
        }
    }

    open class NullableImpl<E>(
        protected open val elementReadAction: SqDataTypeReadAction<E & Any>,
    ): SqDataTypeArrayReadActionBase<E?>() {
        override fun read(elementSource: ResultSet, columnIndex: Int, elementIndex: Int): E? =
            this.elementReadAction[elementSource, columnIndex]
    }
}

open class SqDataTypeArrayWriteActionImpl<JAVA, JDBC>(
    protected open val elementDbTypeName: String,
    protected open val elementClass: Class<JDBC & Any>,
    protected open val valueConvertAction: SqDataTypeWriterConvertAction<JAVA, JDBC>,
): SqDataTypeWriteAction<List<JAVA>> {
    override fun set(target: PreparedStatement, index: Int, value: List<JAVA>) {
        @Suppress("UNCHECKED_CAST")
        val javaArray = (java.lang.reflect.Array.newInstance(this.elementClass, value.size) as Array<JDBC?>)
        val valueConvertAction = this.valueConvertAction
        value.forEachIndexed { index, originalElement ->
            val preparedElement = valueConvertAction.invoke(target, originalElement)
            javaArray[index] = preparedElement
        }
        val sqlArray = target.connection.createArrayOf(this.elementDbTypeName, javaArray)
        target.setArray(index, sqlArray)
    }


    object PassThroughConvertAction: SqDataTypeWriterConvertAction<Any?, Any?> {
        override fun invoke(target: PreparedStatement, value: Any?): Any? =
            value

        fun <T: Any> castNotNull(): SqDataTypeWriterConvertAction<T, T> {
            @Suppress("UNCHECKED_CAST")
            return this as SqDataTypeWriterConvertAction<T, T>
        }

        fun <T> castNullable(): SqDataTypeWriterConvertAction<T, T> {
            @Suppress("UNCHECKED_CAST")
            return this as SqDataTypeWriterConvertAction<T, T>
        }
    }
}

@JvmName("sqDataTypeArrayReaderPair__notNullElements")
fun <E, DB: Any> sqDataTypeArrayReaderPair(
    read: SqDataTypeReadAction<E & Any>,
    nullFlag: Any,
): Pair<SqDataTypeReaderNotNullImpl<List<E>, DB>, SqDataTypeReaderNullableImpl<List<E>, DB>> {
    val arrayRead = SqDataTypeArrayReadActionBase.NotNullImpl<E>(read)
    return sqDataTypeReaderPair<List<E>, DB>(arrayRead)
}

@JvmName("sqDataTypeArrayReaderPair__nullableElements")
fun <E, DB: Any> sqDataTypeArrayReaderPair(
    read: SqDataTypeReadAction<E & Any>,
    nullFlag: Any?,
): Pair<SqDataTypeReaderNotNullImpl<List<E?>, DB>, SqDataTypeReaderNullableImpl<List<E?>, DB>> {
    val arrayRead = SqDataTypeArrayReadActionBase.NullableImpl<E>(read)
    return sqDataTypeReaderPair(arrayRead)
}

@JvmName("sqDataTypeArrayPack__notNullElements")
fun <JAVA: Any, JDBC: Any, DB: Any> sqDataTypeArrayPack(
    nullFlag: Any,
    jdbcType: Int,
    dbTypeName: String?,
    elementDbTypeName: String,
    elementClass: Class<JDBC>,
    read: SqDataTypeReadAction<JAVA>,
    write: SqDataTypeWriterConvertAction<JAVA, JDBC>,
): SqDataTypePack<List<JAVA>, DB> {
    val (notNullReader, nullableReader) = sqDataTypeArrayReaderPair<JAVA, DB>(read, nullFlag)
    val writeAction = SqDataTypeArrayWriteActionImpl<JAVA, JDBC>(elementDbTypeName, elementClass, write)
    val writer = SqDataTypeWriterImpl<List<JAVA>, DB>(jdbcType, dbTypeName, writeAction)
    return SqDataTypePackImpl(notNullReader, nullableReader, writer)
}

@JvmName("sqDataTypeArrayPack__nullableElements")
fun <JAVA, JDBC, DB: Any> sqDataTypeArrayPack(
    nullFlag: Any?,
    jdbcType: Int,
    dbTypeName: String?,
    elementDbTypeName: String,
    elementClass: Class<JDBC & Any>,
    read: SqDataTypeReadAction<JAVA & Any>,
    write: SqDataTypeWriterConvertAction<JAVA?, JDBC?>,
): SqDataTypePack<List<JAVA?>, DB> {
    val (notNullReader, nullableReader) = sqDataTypeArrayReaderPair<JAVA, DB>(read, nullFlag)
    val writeAction = SqDataTypeArrayWriteActionImpl<JAVA?, JDBC?>(elementDbTypeName, elementClass, write)
    val writer = SqDataTypeWriterImpl<List<JAVA?>, DB>(jdbcType, dbTypeName, writeAction)
    return SqDataTypePackImpl(notNullReader, nullableReader, writer)
}
// endregion


// region Type collection
open class SqDataTypesImpl: SqDataTypes {
    companion object {
        val INSTANCE: SqDataTypesImpl = SqDataTypesImpl()


        fun bigDecimalTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<BigDecimal, Number> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = BigDecimalReadAction,
                write = { target, index, value -> target.setBigDecimal(index, value) },
            )
        }

        @JvmName("bigDecimalListTypePack__notNullElements")
        fun bigDecimalListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<BigDecimal>, List<Number>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = BigDecimal::class.java,
                read = BigDecimalReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNotNull(),
            )
        }

        @JvmName("bigDecimalListTypePack__nullableElements")
        fun bigDecimalListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<BigDecimal?>, List<Number>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = BigDecimal::class.java,
                read = BigDecimalReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun blobTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<Blob, Blob> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = BlobReadAction,
                write = { target, index, value -> target.setBlob(index, value) },
            )
        }

        @JvmName("blobListTypePack__notNullElements")
        fun blobListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Blob>, List<Blob>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Blob::class.java,
                read = BlobReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNotNull(),
            )
        }

        @JvmName("blobListTypePack__nullableElements")
        fun blobListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Blob?>, List<Blob>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Blob::class.java,
                read = BlobReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun blobStreamTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<InputStream, Blob> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = BlobStreamReadAction,
                write = { target, index, value -> target.setBlob(index, value) },
            )
        }

        fun booleanTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<Boolean, Boolean> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = BooleanReadAction,
                write = { target, index, value -> target.setBoolean(index, value) },
            )
        }

        @JvmName("booleanListTypePack__notNullElements")
        fun booleanListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Boolean>, List<Boolean>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Boolean::class.javaObjectType,
                read = BooleanReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNotNull(),
            )
        }

        @JvmName("booleanListTypePack__nullableElements")
        fun booleanListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Boolean?>, List<Boolean>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Boolean::class.javaObjectType,
                read = BooleanReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun byteArrayTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<ByteArray, ByteArray> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = ByteArrayReadAction,
                write = { target, index, value -> target.setBytes(index, value) },
            )
        }

        @JvmName("byteArrayListTypePack__notNullElements")
        fun byteArrayListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<ByteArray>, List<ByteArray>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = ByteArray::class.java,
                read = ByteArrayReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNotNull(),
            )
        }

        @JvmName("byteArrayListTypePack__nullableElements")
        fun byteArrayListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<ByteArray?>, List<ByteArray>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = ByteArray::class.java,
                read = ByteArrayReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun byteTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<Byte, Number> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = ByteReadAction,
                write = { target, index, value -> target.setByte(index, value) },
            )
        }

        @JvmName("byteListTypePack__notNullElements")
        fun byteListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Byte>, List<Number>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Byte::class.javaObjectType,
                read = ByteReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNotNull(),
            )
        }

        @JvmName("byteListTypePack__nullableElements")
        fun byteListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Byte?>, List<Number>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Byte::class.javaObjectType,
                read = ByteReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun charTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<Char, String> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = CharReadAction,
                write = { target, index, value ->
                    target.setString(index, value.toString())
                }
            )
        }

        @JvmName("charListTypePack__notNullElements")
        fun charListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Char>, List<String>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = String::class.java,
                read = CharReadAction,
                write = { _, value: Char -> value.toString() },
            )
        }

        @JvmName("charListTypePack__nullableElements")
        fun charListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Char?>, List<String>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = String::class.java,
                read = CharReadAction,
                write = { _, value: Char? -> value?.toString() },
            )
        }

        fun clobTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<Clob, Clob> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = ClobReadAction,
                write = { target, index, value -> target.setClob(index, value) },
            )
        }

        @JvmName("clobListTypePack__notNullElements")
        fun clobListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Clob>, List<Clob>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Clob::class.java,
                read = ClobReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNotNull(),
            )
        }

        @JvmName("clobListTypePack__nullableElements")
        fun clobListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Clob?>, List<Clob>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Clob::class.java,
                read = ClobReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun clobReaderTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<Reader, Clob> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = ClobReaderReadAction,
                write = { target, index, value -> target.setClob(index, value) },
            )
        }

        fun dateJdbcTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<Date, Timestamp> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = DateJdbcReadAction,
                write = { target, index, value -> target.setDate(index, value) },
            )
        }

        @JvmName("dateListJdbcTypePack__notNullElements")
        fun dateListJdbcTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Date>, List<Timestamp>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Date::class.java,
                read = DateJdbcReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNotNull(),
            )
        }

        @JvmName("dateListJdbcTypePack__nullableElements")
        fun dateListJdbcTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Date?>, List<Timestamp>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Date::class.java,
                read = DateJdbcReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun doubleTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<Double, Number> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = DoubleReadAction,
                write = { target, index, value -> target.setDouble(index, value) },
            )
        }

        @JvmName("doubleListTypePack__notNullElements")
        fun doubleListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Double>, List<Number>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Double::class.javaObjectType,
                read = DoubleReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNotNull(),
            )
        }

        @JvmName("doubleListTypePack__nullableElements")
        fun doubleListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Double?>, List<Number>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Double::class.javaObjectType,
                read = DoubleReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun floatTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<Float, Number> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = FloatReadAction,
                write = { target, index, value -> target.setFloat(index, value) },
            )
        }

        @JvmName("floatListTypePack__notNullElements")
        fun floatListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Float>, List<Number>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Float::class.javaObjectType,
                read = FloatReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNotNull(),
            )
        }

        @JvmName("floatListTypePack__nullableElements")
        fun floatListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Float?>, List<Number>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Float::class.javaObjectType,
                read = FloatReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun intTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<Int, Number> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = IntReadAction,
                write = { target, index, value -> target.setInt(index, value) },
            )
        }

        @JvmName("intListTypePack__notNullElements")
        fun intListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Int>, List<Number>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Int::class.javaObjectType,
                read = IntReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNotNull(),
            )
        }

        @JvmName("intListTypePack__nullableElements")
        fun intListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Int?>, List<Number>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Int::class.javaObjectType,
                read = IntReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun localDateTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<LocalDate, Timestamp> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = LocalDateReadAction,
                write = { target, index, value -> target.setDate(index, Date.valueOf(value)) },
            )
        }

        @JvmName("localDateListTypePack__notNullElements")
        fun localDateListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<LocalDate>, List<Timestamp>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Date::class.java,
                read = LocalDateReadAction,
                write = { target, value: LocalDate ->
                    Date.valueOf(value)
                },
            )
        }

        @JvmName("localDateListTypePack__nullableElements")
        fun localDateListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<LocalDate?>, List<Timestamp>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Date::class.java,
                read = LocalDateReadAction,
                write = { target, value ->
                    value?.let {
                        Date.valueOf(value)
                    }
                },
            )
        }

        fun localDateTimeTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<LocalDateTime, Timestamp> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = LocalDateTimeReadAction,
                write = { target, index, value -> target.setTimestamp(index, Timestamp.valueOf(value)) },
            )
        }

        @JvmName("localDateTimeListTypePack__notNullElements")
        fun localDateTimeListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<LocalDateTime>, List<Timestamp>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Timestamp::class.java,
                read = LocalDateTimeReadAction,
                write = { target, value: LocalDateTime ->
                    Timestamp.valueOf(value)
                },
            )
        }

        @JvmName("localDateTimeListTypePack__nullableElements")
        fun localDateTimeListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<LocalDateTime?>, List<Timestamp>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Timestamp::class.java,
                read = LocalDateTimeReadAction,
                write = { target, value ->
                    value?.let {
                        Timestamp.valueOf(value)
                    }
                },
            )
        }

        fun localTimeTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<LocalTime, Time> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = LocalTimeReadAction,
                write = { target, index, value ->
                    target.setTime(index, SqUtil.localTimeToJdbcTime(value))
                },
            )
        }

        @JvmName("localTimeListTypePack__notNullElements")
        fun localTimeListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<LocalTime>, List<Time>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Time::class.java,
                read = LocalTimeReadAction,
                write = { target, value: LocalTime ->
                    SqUtil.localTimeToJdbcTime(value)
                },
            )
        }

        @JvmName("localTimeListTypePack__nullableElements")
        fun localTimeListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<LocalTime?>, List<Time>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Time::class.java,
                read = LocalTimeReadAction,
                write = { target, value ->
                    value?.let {
                        SqUtil.localTimeToJdbcTime(value)
                    }
                },
            )
        }

        fun longTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<Long, Number> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = LongReadAction,
                write = { target, index, value -> target.setLong(index, value) },
            )
        }

        @JvmName("longListTypePack__notNullElements")
        fun longListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Long>, List<Number>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Long::class.javaObjectType,
                read = LongReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNotNull(),
            )
        }

        @JvmName("longListTypePack__nullableElements")
        fun longListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Long?>, List<Number>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Long::class.javaObjectType,
                read = LongReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun nClobTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<NClob, Clob> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = NClobReadAction,
                write = { target, index, value -> target.setNClob(index, value) },
            )
        }

        @JvmName("nClobListTypePack__notNullElements")
        fun nClobListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<NClob>, List<Clob>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = NClob::class.java,
                read = NClobReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNotNull(),
            )
        }

        @JvmName("nClobListTypePack__nullableElements")
        fun nClobListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<NClob?>, List<Clob>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = NClob::class.java,
                read = NClobReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun nClobReaderTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<Reader, Clob> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = NClobReaderReadAction,
                write = { target, index, value -> target.setNClob(index, value) },
            )
        }

        fun offsetDateTimeTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<OffsetDateTime, Timestamp> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = OffsetDateTimeReadAction,
                write = { target, index, value ->
                    target.setTimestamp(index, SqUtil.offsetDateTimeToJdbcTimestamp(value))
                },
            )
        }

        @JvmName("offsetDateTimeListTypePack__notNullElements")
        fun offsetDateTimeListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<OffsetDateTime>, List<Timestamp>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Timestamp::class.java,
                read = OffsetDateTimeReadAction,
                write = { target, value: OffsetDateTime ->
                    SqUtil.offsetDateTimeToJdbcTimestamp(value)
                },
            )
        }

        @JvmName("offsetDateTimeListTypePack__nullableElements")
        fun offsetDateTimeListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<OffsetDateTime?>, List<Timestamp>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Timestamp::class.java,
                read = OffsetDateTimeReadAction,
                write = { target, value ->
                    value?.let {
                        SqUtil.offsetDateTimeToJdbcTimestamp(value)
                    }
                },
            )
        }

        fun offsetTimeTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<OffsetTime, Time> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = OffsetTimeReadAction,
                write = { target, index, value ->
                    target.setTime(index, SqUtil.offsetTimeToJdbcTime(value))
                },
            )
        }

        @JvmName("offsetTimeListTypePack__notNullElements")
        fun offsetTimeListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<OffsetTime>, List<Time>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Time::class.java,
                read = OffsetTimeReadAction,
                write = { target, value: OffsetTime ->
                    SqUtil.offsetTimeToJdbcTime(value)
                },
            )
        }

        @JvmName("offsetTimeListTypePack__nullableElements")
        fun offsetTimeListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<OffsetTime?>, List<Time>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Time::class.java,
                read = OffsetTimeReadAction,
                write = { target, value ->
                    value?.let {
                        SqUtil.offsetTimeToJdbcTime(value)
                    }
                },
            )
        }

        fun refTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<Ref, Ref> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = RefReadAction,
                write = { target, index, value -> target.setRef(index,value) },
            )
        }

        @JvmName("refListTypePack__notNullElements")
        fun refListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Ref>, List<Ref>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Ref::class.java,
                read = RefReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNotNull(),
            )
        }

        @JvmName("refListTypePack__nullableElements")
        fun refListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Ref?>, List<Ref>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Ref::class.java,
                read = RefReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun rowIdTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<RowId, RowId> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = RowIdReadAction,
                write = { target, index, value -> target.setRowId(index,value) },
            )
        }

        @JvmName("rowIdListTypePack__notNullElements")
        fun rowIdListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<RowId>, List<Ref>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = RowId::class.java,
                read = RowIdReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNotNull(),
            )
        }

        @JvmName("rowIdListTypePack__nullableElements")
        fun rowIdListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<RowId?>, List<Ref>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = RowId::class.java,
                read = RowIdReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun shortTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<Short, Number> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = ShortReadAction,
                write = { target, index, value -> target.setShort(index, value) },
            )
        }

        @JvmName("shortListTypePack__notNullElements")
        fun shortListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Short>, List<Number>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Short::class.javaObjectType,
                read = ShortReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNotNull(),
            )
        }

        @JvmName("shortListTypePack__nullableElements")
        fun shortListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Short?>, List<Number>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Short::class.javaObjectType,
                read = ShortReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun sqlXmlTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<SQLXML, String> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = SqlXmlReadAction,
                write = { target, index, value -> target.setSQLXML(index, value) },
            )
        }

        @JvmName("sqlXmlListTypePack__notNullElements")
        fun sqlXmlListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<SQLXML>, List<String>> {
            return sqDataTypeArrayPack(
                nullFlag = 0,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = SQLXML::class.java,
                read = SqlXmlReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNotNull(),
            )
        }

        @JvmName("sqlXmlListTypePack__nullableElements")
        fun sqlXmlListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<SQLXML?>, List<String>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = SQLXML::class.java,
                read = SqlXmlReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun stringTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<String, String> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = StringReadAction,
                write = { target, index, value -> target.setString(index, value) },
            )
        }

        @JvmName("stringListTypePack__notNullElements")
        fun stringListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<String>, List<String>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = String::class.java,
                read = StringReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNotNull(),
            )
        }

        @JvmName("stringListTypePack__nullableElements")
        fun stringListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<String?>, List<String>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = String::class.java,
                read = StringReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun timeJdbcTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<Time, Time> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = TimeJdbcReadAction,
                write = { target, index, value -> target.setTime(index, value) },
            )
        }

        @JvmName("timeJdbcListTypePack__notNullElements")
        fun timeJdbcListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Time>, List<Time>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Time::class.java,
                read = TimeJdbcReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNotNull(),
            )
        }

        @JvmName("timeJdbcListTypePack__nullableElements")
        fun timeJdbcListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Time?>, List<Time>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Time::class.java,
                read = TimeJdbcReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun timestampJdbcTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<Timestamp, Timestamp> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = TimestampJdbcReadAction,
                write = { target, index, value -> target.setTimestamp(index, value) },
            )
        }

        @JvmName("timestampJdbcListTypePack__notNullElements")
        fun timestampJdbcListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Timestamp>, List<Timestamp>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Timestamp::class.java,
                read = TimestampJdbcReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNotNull(),
            )
        }

        @JvmName("timestampJdbcListTypePack__nullableElements")
        fun timestampJdbcListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<Timestamp?>, List<Timestamp>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = Timestamp::class.java,
                read = TimestampJdbcReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }

        fun urlTypePack(jdbcType: Int, dbTypeName: String? = null): SqDataTypePack<URL, String> {
            return sqDataTypePack(
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                read = UrlReadAction,
                write = { target, index, value -> target.setURL(index, value) },
            )
        }

        @JvmName("urlListTypePack__notNullElements")
        fun urlListTypePack(nullFlag: Any, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<URL>, List<String>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = URL::class.java,
                read = UrlReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNotNull(),
            )
        }

        @JvmName("urlListTypePack__nullableElements")
        fun urlListTypePack(nullFlag: Any?, jdbcType: Int, dbTypeName: String?, elementDbTypeName: String): SqDataTypePack<List<URL?>, List<String>> {
            return sqDataTypeArrayPack(
                nullFlag = nullFlag,
                jdbcType = jdbcType,
                dbTypeName = dbTypeName,
                elementDbTypeName = elementDbTypeName,
                elementClass = URL::class.java,
                read = UrlReadAction,
                write = SqDataTypeArrayWriteActionImpl.PassThroughConvertAction.castNullable(),
            )
        }
    }


    // region Blob, clob
    object BlobReadAction: SqDataTypeReadAction<Blob> {
        override fun get(source: ResultSet, index: Int): Blob? =
            source.getBlob(index)
    }

    object BlobStreamReadAction: SqDataTypeReadAction<InputStream> {
        override fun get(source: ResultSet, index: Int): InputStream? {
            return source.getBlob(index)?.let { blob ->
                val blobStream = blob.binaryStream
                    ?: error("No binary stream in BLOB object $blob")
                SqBlobInputStream(blob, blobStream)
            }
        }
    }

    object ClobReadAction: SqDataTypeReadAction<Clob> {
        override fun get(source: ResultSet, index: Int): Clob? =
            source.getClob(index)
    }

    object ClobReaderReadAction: SqDataTypeReadAction<Reader> {
        override fun get(source: ResultSet, index: Int): Reader? {
            return source.getClob(index)?.let {
                SqClobReader(it)
            }
        }
    }

    object NClobReadAction: SqDataTypeReadAction<NClob> {
        override fun get(source: ResultSet, index: Int): NClob? =
            source.getNClob(index)
    }

    object NClobReaderReadAction: SqDataTypeReadAction<Reader> {
        override fun get(source: ResultSet, index: Int): Reader? {
            return source.getNClob(index)?.let {
                SqClobReader(it)
            }
        }
    }


    override val blob: SqDataTypePack<Blob, Blob> = blobTypePack(Types.BLOB)
    override val blobStream: SqDataTypePack<InputStream, Blob> = blobStreamTypePack(Types.BLOB)
    override val clob: SqDataTypePack<Clob, Clob> = clobTypePack(Types.CLOB)
    override val clobReader: SqDataTypePack<Reader, Clob> = clobReaderTypePack(Types.CLOB)
    override val nClob: SqDataTypePack<NClob, Clob> = nClobTypePack(Types.NCLOB)
    override val nClobReader: SqDataTypePack<Reader, Clob> = nClobReaderTypePack(Types.NCLOB)
    // endregion


    // region Boolean
    object BooleanReadAction: SqDataTypeReadAction<Boolean> {
        override fun get(source: ResultSet, index: Int): Boolean? =
            source.getBoolean(index).takeIf { !source.wasNull() }
    }


    override val boolean: SqDataTypePack<Boolean, Boolean> = booleanTypePack(Types.BOOLEAN)
    // endregion


    // region Byte array
    object ByteArrayReadAction: SqDataTypeReadAction<ByteArray> {
        override fun get(source: ResultSet, index: Int): ByteArray? =
            source.getBytes(index)
    }


    override val binary: SqDataTypePack<ByteArray, ByteArray> = byteArrayTypePack(Types.BINARY)
    override val longVarBinary: SqDataTypePack<ByteArray, ByteArray> = byteArrayTypePack(Types.LONGVARBINARY)
    override val varBinary: SqDataTypePack<ByteArray, ByteArray> = byteArrayTypePack(Types.VARBINARY)
    // endregion


    // region Number
    object BigDecimalReadAction: SqDataTypeReadAction<BigDecimal> {
        override fun get(source: ResultSet, index: Int): BigDecimal? =
            source.getBigDecimal(index)
    }

    object ByteReadAction: SqDataTypeReadAction<Byte> {
        override fun get(source: ResultSet, index: Int): Byte? =
            source.getByte(index).takeIf { !source.wasNull() }
    }

    object DoubleReadAction: SqDataTypeReadAction<Double> {
        override fun get(source: ResultSet, index: Int): Double? =
            source.getDouble(index).takeIf { !source.wasNull() }
    }

    object FloatReadAction: SqDataTypeReadAction<Float> {
        override fun get(source: ResultSet, index: Int): Float? =
            source.getFloat(index).takeIf { !source.wasNull() }
    }

    object IntReadAction: SqDataTypeReadAction<Int> {
        override fun get(source: ResultSet, index: Int): Int? =
            source.getInt(index).takeIf { !source.wasNull() }
    }

    object LongReadAction: SqDataTypeReadAction<Long> {
        override fun get(source: ResultSet, index: Int): Long? =
            source.getLong(index).takeIf { !source.wasNull() }
    }

    object ShortReadAction: SqDataTypeReadAction<Short> {
        override fun get(source: ResultSet, index: Int): Short? =
            source.getShort(index).takeIf { !source.wasNull() }
    }


    override val bigInt: SqDataTypePack<Long, Number> = longTypePack(Types.BIGINT)
    override val decimal: SqDataTypePack<BigDecimal, Number> = bigDecimalTypePack(Types.DECIMAL)
    override val double: SqDataTypePack<Double, Number> = doubleTypePack(Types.DOUBLE)
    override val float: SqDataTypePack<Double, Number> = doubleTypePack(Types.FLOAT)
    override val integer: SqDataTypePack<Int, Number> = intTypePack(Types.INTEGER)
    override val numeric: SqDataTypePack<BigDecimal, Number> = bigDecimalTypePack(Types.NUMERIC)
    override val real: SqDataTypePack<Float, Number> = floatTypePack(Types.REAL)
    override val smallInt: SqDataTypePack<Short, Number> = shortTypePack(Types.SMALLINT)
    override val tinyInt: SqDataTypePack<Byte, Number> = byteTypePack(Types.TINYINT)
    // endregion


    // region Object
    override val jObjectReaderPair: Pair<SqDataTypeReader<Any, Any>, SqDataTypeReader<Any?, Any>> =
        sqDataTypeReaderPair<Any, Any> { source, index -> source.getObject(index) }
    // endregion


    // region String
    object CharReadAction: SqDataTypeReadAction<Char> {
        override fun get(source: ResultSet, index: Int): Char? =
            source.getString(index)?.takeIf { it.isNotEmpty() }?.get(0)
    }

    object StringReadAction: SqDataTypeReadAction<String> {
        override fun get(source: ResultSet, index: Int): String? =
            source.getString(index)
    }


    override val char: SqDataTypePack<String, String> = stringTypePack(Types.CHAR)
    override val longNVarChar: SqDataTypePack<String, String> = stringTypePack(Types.LONGNVARCHAR)
    override val longVarChar: SqDataTypePack<String, String> = stringTypePack(Types.LONGVARCHAR)
    override val nChar: SqDataTypePack<String, String> = stringTypePack(Types.NCHAR)
    override val nVarChar: SqDataTypePack<String, String> = stringTypePack(Types.NVARCHAR)
    override val varChar: SqDataTypePack<String, String> = stringTypePack(Types.VARCHAR)
    // endregion


    // region Temporal
    object DateJdbcReadAction: SqDataTypeReadAction<Date> {
        override fun get(source: ResultSet, index: Int): Date? =
            source.getDate(index)
    }

    object LocalDateReadAction: SqDataTypeReadAction<LocalDate> {
        override fun get(source: ResultSet, index: Int): LocalDate? =
            source.getDate(index)?.toLocalDate()
    }

    object LocalDateTimeReadAction: SqDataTypeReadAction<LocalDateTime> {
        override fun get(source: ResultSet, index: Int): LocalDateTime? =
            source.getTimestamp(index)?.toLocalDateTime()
    }

    object LocalTimeReadAction: SqDataTypeReadAction<LocalTime> {
        override fun get(source: ResultSet, index: Int): LocalTime? {
            return source.getTime(index)?.let {
                SqUtil.jdbcTimeToLocalTime(it)
            }
        }
    }

    object OffsetDateTimeReadAction: SqDataTypeReadAction<OffsetDateTime> {
        override fun get(source: ResultSet, index: Int): OffsetDateTime? {
            return source.getTimestamp(index)?.let {
                SqUtil.jdbcTimestampToOffsetDateTime(it)
            }
        }
    }

    object OffsetTimeReadAction: SqDataTypeReadAction<OffsetTime> {
        override fun get(source: ResultSet, index: Int): OffsetTime? {
            return source.getTime(index)?.let {
                SqUtil.jdbcTimeToOffsetTime(it)
            }
        }
    }

    object TimeJdbcReadAction: SqDataTypeReadAction<Time> {
        override fun get(source: ResultSet, index: Int): Time? =
            source.getTime(index)
    }

    object TimestampJdbcReadAction: SqDataTypeReadAction<Timestamp> {
        override fun get(source: ResultSet, index: Int): Timestamp? =
            source.getTimestamp(index)
    }


    override val date: SqDataTypePack<LocalDate, Timestamp> = localDateTypePack(Types.DATE)
    override val dateJdbc: SqDataTypePack<Date, Timestamp> = dateJdbcTypePack(Types.DATE)
    override val time: SqDataTypePack<LocalTime, Time> = localTimeTypePack(Types.TIME)
    override val timeJdbc: SqDataTypePack<Time, Time> = timeJdbcTypePack(Types.TIME)
    override val timeTz: SqDataTypePack<OffsetTime, Time> = offsetTimeTypePack(Types.TIME_WITH_TIMEZONE)
    override val timeTzJdbc: SqDataTypePack<Time, Time> = timeJdbcTypePack(Types.TIME_WITH_TIMEZONE)
    override val timestamp: SqDataTypePack<LocalDateTime, Timestamp> = localDateTimeTypePack(Types.TIMESTAMP)
    override val timestampJdbc: SqDataTypePack<Timestamp, Timestamp> = timestampJdbcTypePack(Types.TIMESTAMP)
    override val timestampTz: SqDataTypePack<OffsetDateTime, Timestamp> = offsetDateTimeTypePack(Types.TIMESTAMP_WITH_TIMEZONE)
    override val timestampTzJdbc: SqDataTypePack<Timestamp, Timestamp> = timestampJdbcTypePack(Types.TIMESTAMP_WITH_TIMEZONE)
    // endregion


    // region Other
    object RefReadAction: SqDataTypeReadAction<Ref> {
        override fun get(source: ResultSet, index: Int): Ref? =
            source.getRef(index)
    }

    object RowIdReadAction: SqDataTypeReadAction<RowId> {
        override fun get(source: ResultSet, index: Int): RowId? =
            source.getRowId(index)
    }

    object SqlXmlReadAction: SqDataTypeReadAction<SQLXML> {
        override fun get(source: ResultSet, index: Int): SQLXML? =
            source.getSQLXML(index)
    }

    object UrlReadAction: SqDataTypeReadAction<URL> {
        override fun get(source: ResultSet, index: Int): URL? =
            source.getURL(index)
    }


    override val dataLink: SqDataTypePack<URL, String> = urlTypePack(Types.DATALINK)
    override val ref: SqDataTypePack<Ref, Ref> = refTypePack(Types.REF)
    override val rowId: SqDataTypePack<RowId, RowId> = rowIdTypePack(Types.ROWID)
    override val sqlXml: SqDataTypePack<SQLXML, String> = sqlXmlTypePack(Types.SQLXML)
    // endregion
}
// endregion
