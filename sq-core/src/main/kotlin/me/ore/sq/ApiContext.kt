package me.ore.sq

import me.ore.sq.generic.SqGenericContextImpl
import java.lang.IllegalStateException
import java.math.BigDecimal
import java.net.URL
import java.sql.Blob
import java.sql.Clob
import java.sql.Connection
import java.sql.Date
import java.sql.NClob
import java.sql.Ref
import java.sql.RowId
import java.sql.SQLXML
import java.sql.Time
import java.sql.Timestamp
import kotlin.concurrent.getOrSet


interface SqContext {
    companion object: SqContextHolder<SqContext>() {
        override fun createDefaultContext(): SqContext = SqGenericContextImpl()


        // region Column index cache
        private val colIndexCacheHolder = ThreadLocal<MutableMap<SqColSet, MutableMap<SqColumn<*, *>, Int?>>>()

        private fun colIndexCache(): MutableMap<SqColSet, MutableMap<SqColumn<*, *>, Int?>> {
            return this.colIndexCacheHolder.getOrSet { HashMap() }
        }

        private fun colIndexCacheFor(colSet: SqColSet): MutableMap<SqColumn<*, *>, Int?> {
            return this.colIndexCache().computeIfAbsent(colSet) { HashMap() }
        }

        fun getColumnIndex(colSet: SqColSet, column: SqColumn<*, *>): Int? {
            return this.colIndexCacheFor(colSet).computeIfAbsent(column) {
                colSet.columns.indexOf(column).takeIf { it >= 0 }
            }
        }

        private fun clearColIndexCache() {
            this.colIndexCacheHolder.get()?.let { cache ->
                this.colIndexCacheHolder.remove()

                cache.values.forEach { colSetCache ->
                    colSetCache.clear()
                }
                cache.clear()
            }
        }
        // endregion


        override fun onLastContextFinished() {
            super.onLastContextFinished()
            this.clearColIndexCache()
        }
    }


    // region Utils
    fun createConnectedContext(connection: Connection): SqConnectedContext

    fun createWriter(): SqWriter

    fun start() { SqContext.start(this) }
    fun finish() { SqContext.finish(this) }

    fun getColumnIndex(colSet: SqColSet, column: SqColumn<*, *>): Int? = SqContext.getColumnIndex(colSet, column)

    var printParameterValuesByDefault: Boolean
    var printParameterValuesByThread: Boolean?
    val printParameterValues: Boolean
        get() = this.printParameterValuesByThread ?: this.printParameterValuesByDefault
    // endregion


    // region Types
    val jBigDecimalType: SqType<BigDecimal>
    val jBlobType: SqType<Blob>
    val jBooleanType: SqType<Boolean>
    val jByteType: SqType<Byte>
    val jByteArrayType: SqType<SqByteArray>
    val jClobType: SqType<Clob>
    val jDateType: SqType<Date>
    val jDoubleType: SqType<Double>
    val jFloatType: SqType<Float>
    val jIntType: SqType<Int>
    val jLongType: SqType<Long>
    val jNClobType: SqType<NClob>
    val jNumberType: SqType<Number>
    val jRefType: SqType<Ref>
    val jRowIdType: SqType<RowId>
    val jSqlXmlType: SqType<SQLXML>
    val jShortType: SqType<Short>
    val jStringType: SqType<String>
    val jTimeType: SqType<Time>
    val jTimestampType: SqType<Timestamp>
    val jUrlType: SqType<URL>

    val dbBigIntType: SqType<Long>
    val dbBinaryType: SqType<SqByteArray>
    val dbBitType: SqType<Boolean>
    val dbBlobType: SqType<Blob>
    val dbBooleanType: SqType<Boolean>
    val dbCharType: SqType<String>
    val dbClobType: SqType<Clob>
    val dbDataLinkType: SqType<URL>
    val dbDateType: SqType<Date>
    val dbDecimalType: SqType<BigDecimal>
    val dbDoubleType: SqType<Double>
    val dbFloatType: SqType<Double>
    val dbIntegerType: SqType<Int>
    val dbLongNVarCharType: SqType<String>
    val dbLongVarBinaryType: SqType<SqByteArray>
    val dbLongVarCharType: SqType<String>
    val dbNCharType: SqType<String>
    val dbNClobType: SqType<NClob>
    val dbNVarCharType: SqType<String>
    val dbNumericType: SqType<BigDecimal>
    val dbRealType: SqType<Float>
    val dbRefType: SqType<Ref>
    val dbRowIdType: SqType<RowId>
    val dbSmallIntType: SqType<Short>
    val dbSqlXmlType: SqType<SQLXML>
    val dbTimeType: SqType<Time>
    val dbTimestampType: SqType<Timestamp>
    val dbTinyIntType: SqType<Byte>
    val dbVarBinaryType: SqType<SqByteArray>
    val dbVarCharType: SqType<String>

    val operationBooleanType: SqType<Boolean>
        get() = this.jBooleanType

    val mathOpNumberType: SqType<Number>
        get() = this.jNumberType

    fun <T: Number?> getTypeForNumber(numberClass: Class<T>): SqType<T & Any>? {
        val result = when (numberClass) {
            BigDecimal::class.java -> this.jBigDecimalType
            Byte::class.java -> this.jByteType
            java.lang.Byte::class.java -> this.jByteType
            Double::class.java -> this.jDoubleType
            java.lang.Double::class.java -> this.jDoubleType
            Float::class.java -> this.jFloatType
            java.lang.Float::class.java -> this.jFloatType
            Int::class.java -> this.jIntType
            java.lang.Integer::class.java -> this.jIntType
            Long::class.java -> this.jLongType
            java.lang.Long::class.java -> this.jLongType
            Short::class.java -> this.jShortType
            java.lang.Short::class.java -> this.jShortType
            Number::class.java -> this.jNumberType
            java.lang.Number::class.java -> this.jNumberType
            else -> null
        }
        return SqUtil.uncheckedCast(result)
    }

    fun <T: Number?> requireTypeForNumber(numberClass: Class<T>): SqType<T & Any> {
        return this.getTypeForNumber(numberClass)
            ?: throw IllegalStateException("Cannot define SQ type for number class ${numberClass.name}")
    }
    // endregion


    // region Base items
    fun <JAVA: Any?, DB: Any> param(type: SqType<JAVA & Any>, nullable: Boolean, value: JAVA): SqParameter<JAVA, DB>

    fun <JAVA: BigDecimal?> jBigDecimalParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.jBigDecimalType.sqCast(), nullable, value)
    fun <JAVA: Blob?> jBlobParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.jBlobType.sqCast(), nullable, value)
    fun <JAVA: Boolean?> jBooleanParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Boolean> = this.param(this.jBooleanType.sqCast(), nullable, value)
    fun <JAVA: Byte?> jByteParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Boolean> = this.param(this.jByteType.sqCast(), nullable, value)
    fun <JAVA: SqByteArray?> jByteArrayParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, SqByteArray> = this.param(this.jByteArrayType.sqCast(), nullable, value)
    fun <JAVA: Clob?> jClobParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Clob> = this.param(this.jClobType.sqCast(), nullable, value)
    fun <JAVA: Date?> jDateParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Date> = this.param(this.jDateType.sqCast(), nullable, value)
    fun <JAVA: Double?> jDoubleParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.jDoubleType.sqCast(), nullable, value)
    fun <JAVA: Float?> jFloatParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.jFloatType.sqCast(), nullable, value)
    fun <JAVA: Int?> jIntParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.jIntType.sqCast(), nullable, value)
    fun <JAVA: Long?> jLongParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.jLongType.sqCast(), nullable, value)
    fun <JAVA: NClob?> jNClobParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Clob> = this.param(this.jNClobType.sqCast(), nullable, value)
    fun <JAVA: Ref?> jRefParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Ref> = this.param(this.jRefType.sqCast(), nullable, value)
    fun <JAVA: RowId?> jRowIdParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, RowId> = this.param(this.jRowIdType.sqCast(), nullable, value)
    fun <JAVA: SQLXML?> jSqlXmlParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, SQLXML> = this.param(this.jSqlXmlType.sqCast(), nullable, value)
    fun <JAVA: Short?> jShortParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.jShortType.sqCast(), nullable, value)
    fun <JAVA: String?> jStringParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, String> = this.param(this.jStringType.sqCast(), nullable, value)
    fun <JAVA: Time?> jTimeParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Time> = this.param(this.jTimeType.sqCast(), nullable, value)
    fun <JAVA: Timestamp?> jTimestampParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Date> = this.param(this.jTimestampType.sqCast(), nullable, value)
    fun <JAVA: URL?> jUrlParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, String> = this.param(this.jUrlType.sqCast(), nullable, value)

    fun <JAVA: Long?> dbBigIntParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.dbBigIntType.sqCast(), nullable, value)
    fun <JAVA: SqByteArray?> dbBinaryParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, SqByteArray> = this.param(this.dbBinaryType.sqCast(), nullable, value)
    fun <JAVA: Boolean?> dbBitParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Boolean> = this.param(this.dbBitType.sqCast(), nullable, value)
    fun <JAVA: Blob?> dbBlobParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Blob> = this.param(this.dbBlobType.sqCast(), nullable, value)
    fun <JAVA: Boolean?> dbBooleanParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Boolean> = this.param(this.dbBooleanType.sqCast(), nullable, value)
    fun <JAVA: String?> dbCharParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, String> = this.param(this.dbCharType.sqCast(), nullable, value)
    fun <JAVA: Clob?> dbClobParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Boolean> = this.param(this.dbClobType.sqCast(), nullable, value)
    fun <JAVA: URL?> dbDataLinkParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, String> = this.param(this.dbDataLinkType.sqCast(), nullable, value)
    fun <JAVA: Date?> dbDateParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Date> = this.param(this.dbDateType.sqCast(), nullable, value)
    fun <JAVA: BigDecimal?> dbDecimalParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.dbDecimalType.sqCast(), nullable, value)
    fun <JAVA: Double?> dbDoubleParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.dbDoubleType.sqCast(), nullable, value)
    fun <JAVA: Double?> dbFloatParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.dbFloatType.sqCast(), nullable, value)
    fun <JAVA: Int?> dbIntegerParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.dbIntegerType.sqCast(), nullable, value)
    fun <JAVA: String?> dbLongNVarCharParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, String> = this.param(this.dbLongNVarCharType.sqCast(), nullable, value)
    fun <JAVA: SqByteArray?> dbLongVarBinaryParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, SqByteArray> = this.param(this.dbLongVarBinaryType.sqCast(), nullable, value)
    fun <JAVA: String?> dbLongVarCharParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, String> = this.param(this.dbLongVarCharType.sqCast(), nullable, value)
    fun <JAVA: String?> dbNCharParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, String> = this.param(this.dbNCharType.sqCast(), nullable, value)
    fun <JAVA: NClob?> dbNClobParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Clob> = this.param(this.dbNClobType.sqCast(), nullable, value)
    fun <JAVA: String?> dbNVarCharParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, String> = this.param(this.dbNVarCharType.sqCast(), nullable, value)
    fun <JAVA: BigDecimal?> dbNumericParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.dbNumericType.sqCast(), nullable, value)
    fun <JAVA: Float?> dbRealParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.dbRealType.sqCast(), nullable, value)
    fun <JAVA: Ref?> dbRefParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Ref> = this.param(this.dbRefType.sqCast(), nullable, value)
    fun <JAVA: RowId?> dbRowIdParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, RowId> = this.param(this.dbRowIdType.sqCast(), nullable, value)
    fun <JAVA: Short?> dbSmallIntParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.dbSmallIntType.sqCast(), nullable, value)
    fun <JAVA: SQLXML?> dbSqlXmlParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, SQLXML> = this.param(this.dbSqlXmlType.sqCast(), nullable, value)
    fun <JAVA: Time?> dbTimeParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Time> = this.param(this.dbTimeType.sqCast(), nullable, value)
    fun <JAVA: Timestamp?> dbTimestampParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Date> = this.param(this.dbTimestampType.sqCast(), nullable, value)
    fun <JAVA: Byte?> dbTinyIntParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, Number> = this.param(this.dbTinyIntType.sqCast(), nullable, value)
    fun <JAVA: SqByteArray?> dbVarBinaryParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, SqByteArray> = this.param(this.dbVarBinaryType.sqCast(), nullable, value)
    fun <JAVA: String?> dbVarCharParam(nullable: Boolean, value: JAVA): SqParameter<JAVA, String> = this.param(this.dbVarCharType.sqCast(), nullable, value)


    fun <JAVA: Any, DB: Any> nullItem(type: SqType<JAVA>): SqNull<JAVA, DB>

    fun jBigDecimalNull(): SqNull<BigDecimal, Number> = this.nullItem(this.jBigDecimalType)
    fun jBlobNull(): SqNull<Blob, Blob> = this.nullItem(this.jBlobType)
    fun jBooleanNull(): SqNull<Boolean, Boolean> = this.nullItem(this.jBooleanType)
    fun jByteNull(): SqNull<Byte, Number> = this.nullItem(this.jByteType)
    fun jByteArrayNull(): SqNull<SqByteArray, SqByteArray> = this.nullItem(this.jByteArrayType)
    fun jClobNull(): SqNull<Clob, Clob> = this.nullItem(this.jClobType)
    fun jDateNull(): SqNull<Date, Date> = this.nullItem(this.jDateType)
    fun jDoubleNull(): SqNull<Double, Number> = this.nullItem(this.jDoubleType)
    fun jFloatNull(): SqNull<Float, Number> = this.nullItem(this.jFloatType)
    fun jIntNull(): SqNull<Int, Number> = this.nullItem(this.jIntType)
    fun jLongNull(): SqNull<Long, Number> = this.nullItem(this.jLongType)
    fun jNClobNull(): SqNull<NClob, Clob> = this.nullItem(this.jNClobType)
    fun jRefNull(): SqNull<Ref, Ref> = this.nullItem(this.jRefType)
    fun jRowIdNull(): SqNull<RowId, RowId> = this.nullItem(this.jRowIdType)
    fun jSqlXmlNull(): SqNull<SQLXML, SQLXML> = this.nullItem(this.jSqlXmlType)
    fun jShortNull(): SqNull<Short, Number> = this.nullItem(this.jShortType)
    fun jStringNull(): SqNull<String, String> = this.nullItem(this.jStringType)
    fun jTimeNull(): SqNull<Time, Time> = this.nullItem(this.jTimeType)
    fun jTimestampNull(): SqNull<Timestamp, Date> = this.nullItem(this.jTimestampType)
    fun jUrlNull(): SqNull<URL, String> = this.nullItem(this.jUrlType)

    fun dbBigIntNull(): SqNull<Long, Number> = this.nullItem(this.dbBigIntType)
    fun dbBinaryNull(): SqNull<SqByteArray, SqByteArray> = this.nullItem(this.dbBinaryType)
    fun dbBitNull(): SqNull<Boolean, Boolean> = this.nullItem(this.dbBitType)
    fun dbBlobNull(): SqNull<Blob, Blob> = this.nullItem(this.dbBlobType)
    fun dbBooleanNull(): SqNull<Boolean, Boolean> = this.nullItem(this.dbBooleanType)
    fun dbCharNull(): SqNull<String, String> = this.nullItem(this.dbCharType)
    fun dbClobNull(): SqNull<Clob, Clob> = this.nullItem(this.dbClobType)
    fun dbDatalinkNull(): SqNull<URL, String> = this.nullItem(this.dbDataLinkType)
    fun dbDateNull(): SqNull<Date, Date> = this.nullItem(this.dbDateType)
    fun dbDecimalNull(): SqNull<BigDecimal, Number> = this.nullItem(this.dbDecimalType)
    fun dbDoubleNull(): SqNull<Double, Number> = this.nullItem(this.dbDoubleType)
    fun dbFloatNull(): SqNull<Double, Number> = this.nullItem(this.dbFloatType)
    fun dbIntegerNull(): SqNull<Int, Number> = this.nullItem(this.dbIntegerType)
    fun dbLongNVarcharNull(): SqNull<String, String> = this.nullItem(this.dbLongNVarCharType)
    fun dbLongVarbinaryNull(): SqNull<SqByteArray, SqByteArray> = this.nullItem(this.dbLongVarBinaryType)
    fun dbLongVarcharNull(): SqNull<String, String> = this.nullItem(this.dbLongVarCharType)
    fun dbNCharNull(): SqNull<String, String> = this.nullItem(this.dbNCharType)
    fun dbNClobNull(): SqNull<NClob, Clob> = this.nullItem(this.dbNClobType)
    fun dbNVarcharNull(): SqNull<String, String> = this.nullItem(this.dbNVarCharType)
    fun dbNumericNull(): SqNull<BigDecimal, Number> = this.nullItem(this.dbNumericType)
    fun dbRealNull(): SqNull<Float, Number> = this.nullItem(this.dbRealType)
    fun dbRefNull(): SqNull<Ref, Ref> = this.nullItem(this.dbRefType)
    fun dbRowIdNull(): SqNull<RowId, RowId> = this.nullItem(this.dbRowIdType)
    fun dbSmallintNull(): SqNull<Short, Number> = this.nullItem(this.dbSmallIntType)
    fun dbSqlxmlNull(): SqNull<SQLXML, SQLXML> = this.nullItem(this.dbSqlXmlType)
    fun dbTimeNull(): SqNull<Time, Time> = this.nullItem(this.dbTimeType)
    fun dbTimestampNull(): SqNull<Timestamp, Timestamp> = this.nullItem(this.dbTimestampType)
    fun dbTinyintNull(): SqNull<Byte, Number> = this.nullItem(this.dbTinyIntType)
    fun dbVarbinaryNull(): SqNull<SqByteArray, SqByteArray> = this.nullItem(this.dbVarBinaryType)
    fun dbVarcharNull(): SqNull<String, String> = this.nullItem(this.dbVarCharType)


    fun <JAVA: Any?, DB: Any, ORIG: SqExpression<JAVA, DB>> expressionAlias(original: ORIG, alias: String): SqExpressionAlias<JAVA, DB, ORIG>
    infix fun <JAVA: Any?, DB: Any, ORIG: SqExpression<JAVA, DB>> ORIG.alias(alias: String): SqExpressionAlias<JAVA, DB, ORIG> {
        return this@SqContext.expressionAlias(this, alias)
    }


    fun <ORIG: SqMultiColSet> multiColSetAlias(original: ORIG, alias: String): SqMultiColSetAlias<ORIG>
    infix fun <ORIG: SqMultiColSet> ORIG.alias(alias: String): SqMultiColSetAlias<ORIG> = this@SqContext.multiColSetAlias(this, alias)

    operator fun <JAVA: Any?, DB: Any> SqMultiColSetAlias<*>.get(originalColumn: SqColumn<JAVA, DB>): SqColSetAliasColumn<JAVA, DB> = this.getColumn(originalColumn)


    fun <JAVA: Any?, DB: Any, ORIG: SqSingleColSet<JAVA?, DB>> singleColSetAlias(original: ORIG, alias: String): SqSingleColSetAlias<JAVA, DB, ORIG>
    infix fun <JAVA: Any?, DB: Any, ORIG: SqSingleColSelect<JAVA, DB>> ORIG.alias(alias: String): SqSingleColSetAlias<JAVA, DB, SqSingleColSelect<JAVA?, DB>> {
        return this@SqContext.singleColSetAlias(SqUtil.uncheckedCast(this), alias)
    }


    fun <JAVA: Any?, DB: Any> colSetAliasColumn(alias: SqColSetAlias<*>, column: SqColumn<JAVA, DB>): SqColSetAliasColumn<JAVA, DB>
    // endregion


    // region Comparisons - groups and "single value" tests
    fun and(type: SqType<Boolean>, values: Iterable<SqExpression<*, Boolean>>): SqMultiValueTest<Boolean>
    fun and(values: Iterable<SqExpression<*, Boolean>>, type: SqType<Boolean> = this.operationBooleanType): SqMultiValueTest<Boolean> = this.and(type, values)
    fun and(first: SqExpression<*, Boolean>, vararg more: SqExpression<*, Boolean>): SqMultiValueTest<Boolean> = this.and(listOf(first, *more))

    fun or(type: SqType<Boolean>, values: Iterable<SqExpression<*, Boolean>>): SqMultiValueTest<Boolean>
    fun or(values: Iterable<SqExpression<*, Boolean>>, type: SqType<Boolean> = this.operationBooleanType): SqMultiValueTest<Boolean> = this.or(type, values)
    fun or(first: SqExpression<*, Boolean>, vararg more: SqExpression<*, Boolean>): SqMultiValueTest<Boolean> = this.or(listOf(first, *more))


    fun not(type: SqType<Boolean>, value: SqExpression<*, Boolean>): SqSingleValueTest<Boolean>
    fun not(value: SqExpression<*, Boolean>): SqSingleValueTest<Boolean> = this.not(this.operationBooleanType, value)


    fun isNull(type: SqType<Boolean>, value: SqExpression<*, *>): SqSingleValueTest<Boolean>
    fun isNull(value: SqExpression<*, *>, type: SqType<Boolean> = this.operationBooleanType): SqSingleValueTest<Boolean> = this.isNull(type, value)
    fun SqExpression<*, *>.isNull(): SqSingleValueTest<Boolean> = this@SqContext.isNull(this)

    fun isNotNull(type: SqType<Boolean>, value: SqExpression<*, *>): SqSingleValueTest<Boolean>
    fun isNotNull(value: SqExpression<*, *>, type: SqType<Boolean> = this.operationBooleanType): SqSingleValueTest<Boolean> = this.isNotNull(type, value)
    fun SqExpression<*, *>.isNotNull(): SqSingleValueTest<Boolean> = this@SqContext.isNotNull(this)
    // endregion


    // region Comparisons - "two value" tests
    fun <DB: Any> eq(type: SqType<Boolean>, firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>): SqTwoValueTest<Boolean>
    fun <DB: Any> eq(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> =
        this.eq(type, firstValue, secondValue)
    infix fun <DB: Any> SqExpression<*, DB>.eq(other: SqExpression<*, DB>): SqTwoValueTest<Boolean> =
        this@SqContext.eq(this, other)
    fun <JAVA: Any?, DB: Any> eq(expression: SqExpression<JAVA, DB>, value: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, DB>(expression.type.sqCast(), (value == null), value)
        return this.eq(expression, param, type)
    }
    infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.eq(value: JAVA?): SqTwoValueTest<Boolean> = this@SqContext.eq(this, value)

    fun <DB: Any> neq(type: SqType<Boolean>, firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>): SqTwoValueTest<Boolean>
    fun <DB: Any> neq(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> =
        this.neq(type, firstValue, secondValue)
    infix fun <DB: Any> SqExpression<*, DB>.neq(other: SqExpression<*, DB>): SqTwoValueTest<Boolean> =
        this@SqContext.neq(this, other)
    fun <JAVA: Any?, DB: Any> neq(expression: SqExpression<JAVA, DB>, value: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, DB>(expression.type.sqCast(), (value == null), value)
        return this.neq(expression, param)
    }
    infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.neq(value: JAVA?): SqTwoValueTest<Boolean> = this@SqContext.neq(this, value)

    fun <DB: Any> gt(type: SqType<Boolean>, firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>): SqTwoValueTest<Boolean>
    fun <DB: Any> gt(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> =
        this.gt(type, firstValue, secondValue)
    infix fun <DB: Any> SqExpression<*, DB>.gt(other: SqExpression<*, DB>): SqTwoValueTest<Boolean> =
        this@SqContext.gt(this, other)
    fun <JAVA: Any?, DB: Any> gt(expression: SqExpression<JAVA, DB>, value: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, DB>(expression.type.sqCast(), (value == null), value)
        return this.gt(expression, param)
    }
    infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.gt(value: JAVA?): SqTwoValueTest<Boolean> = this@SqContext.gt(this, value)

    fun <DB: Any> gte(type: SqType<Boolean>, firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>): SqTwoValueTest<Boolean>
    fun <DB: Any> gte(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> =
        this.gte(type, firstValue, secondValue)
    infix fun <DB: Any> SqExpression<*, DB>.gte(other: SqExpression<*, DB>): SqTwoValueTest<Boolean> =
        this@SqContext.gte(this, other)
    fun <JAVA: Any?, DB: Any> gte(expression: SqExpression<JAVA, DB>, value: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, DB>(expression.type.sqCast(), (value == null), value)
        return this.gte(expression, param)
    }
    infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.gte(value: JAVA?): SqTwoValueTest<Boolean> = this@SqContext.gte(this, value)

    fun <DB: Any> lt(type: SqType<Boolean>, firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>): SqTwoValueTest<Boolean>
    fun <DB: Any> lt(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> =
        this.lt(type, firstValue, secondValue)
    infix fun <DB: Any> SqExpression<*, DB>.lt(other: SqExpression<*, DB>): SqTwoValueTest<Boolean> =
        this@SqContext.lt(this, other)
    fun <JAVA: Any?, DB: Any> lt(expression: SqExpression<JAVA, DB>, value: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, DB>(expression.type.sqCast(), (value == null), value)
        return this.lt(expression, param)
    }
    infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.lt(value: JAVA?): SqTwoValueTest<Boolean> = this@SqContext.lt(this, value)

    fun <DB: Any> lte(type: SqType<Boolean>, firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>): SqTwoValueTest<Boolean>
    fun <DB: Any> lte(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> =
        this.lte(type, firstValue, secondValue)
    infix fun <DB: Any> SqExpression<*, DB>.lte(other: SqExpression<*, DB>): SqTwoValueTest<Boolean> =
        this@SqContext.lte(this, other)
    fun <JAVA: Any?, DB: Any> lte(expression: SqExpression<JAVA, DB>, value: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, DB>(expression.type.sqCast(), (value == null), value)
        return this.lte(expression, param)
    }
    infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.lte(value: JAVA?): SqTwoValueTest<Boolean> = this@SqContext.lte(this, value)

    fun like(type: SqType<Boolean>, firstValue: SqExpression<*, String>, secondValue: SqExpression<*, String>): SqTwoValueTest<Boolean>
    fun like(firstValue: SqExpression<*, String>, secondValue: SqExpression<*, String>, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> =
        this.like(type, firstValue, secondValue)
    infix fun SqExpression<*, String>.like(other: SqExpression<*, String>): SqTwoValueTest<Boolean> =
        this@SqContext.like(this, other)
    fun <JAVA: Any?> like(expression: SqExpression<JAVA, String>, value: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, String>(expression.type.sqCast(), (value == null), value)
        return this.like(expression, param)
    }
    infix fun <JAVA: Any?> SqExpression<JAVA, String>.like(value: JAVA?): SqTwoValueTest<Boolean> = this@SqContext.like(this, value)

    fun notLike(type: SqType<Boolean>, firstValue: SqExpression<*, String>, secondValue: SqExpression<*, String>): SqTwoValueTest<Boolean>
    fun notLike(firstValue: SqExpression<*, String>, secondValue: SqExpression<*, String>, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> =
        this.notLike(type, firstValue, secondValue)
    infix fun SqExpression<*, String>.notLike(other: SqExpression<*, String>): SqTwoValueTest<Boolean> =
        this@SqContext.notLike(this, other)
    fun <JAVA: Any?> notLike(expression: SqExpression<JAVA, String>, value: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, String>(expression.type.sqCast(), (value == null), value)
        return this.notLike(expression, param)
    }
    infix fun <JAVA: Any?> SqExpression<JAVA, String>.notLike(value: JAVA?): SqTwoValueTest<Boolean> = this@SqContext.notLike(this, value)
    // endregion


    // region Comparisons - between, not between, in [list], not in [list]
    fun <DB: Any> between(
        type: SqType<Boolean>,
        mainValue: SqExpression<*, DB>,
        firstBoundsValue: SqExpression<*, DB>,
        secondBoundsValue: SqExpression<*, DB>,
    ): SqBetweenTest<Boolean>
    fun <DB: Any> between(
        mainValue: SqExpression<*, DB>,
        firstBoundsValue: SqExpression<*, DB>,
        secondBoundsValue: SqExpression<*, DB>,
        type: SqType<Boolean> = this.operationBooleanType,
    ): SqBetweenTest<Boolean> =
        this.between(type, mainValue, firstBoundsValue, secondBoundsValue)
    fun <DB: Any> SqExpression<*, DB>.between(firstBoundsValue: SqExpression<*, DB>, secondBoundsValue: SqExpression<*, DB>): SqBetweenTest<Boolean> =
        this@SqContext.between(this, firstBoundsValue, secondBoundsValue)
    fun <JAVA: Any?, DB: Any> between(expression: SqExpression<JAVA, DB>, first: JAVA?, second: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqBetweenTest<Boolean> {
        val firstParam = this.param<JAVA?, DB>(expression.type.sqCast(), (first == null), first)
        val secondParam = this.param<JAVA?, DB>(expression.type.sqCast(), (second == null), second)
        return this.between(expression, firstParam, secondParam, type)
    }
    fun <JAVA: Any?> SqExpression<JAVA, *>.between(first: JAVA?, second: JAVA?): SqBetweenTest<Boolean> = this@SqContext.between(this, first, second)

    fun <DB: Any> notBetween(
        type: SqType<Boolean>,
        mainValue: SqExpression<*, DB>,
        firstBoundsValue: SqExpression<*, DB>,
        secondBoundsValue: SqExpression<*, DB>,
    ): SqBetweenTest<Boolean>
    fun <DB: Any> notBetween(
        mainValue: SqExpression<*, DB>,
        firstBoundsValue: SqExpression<*, DB>,
        secondBoundsValue: SqExpression<*, DB>,
        type: SqType<Boolean> = this.operationBooleanType,
    ): SqBetweenTest<Boolean> =
        this.notBetween(type, mainValue, firstBoundsValue, secondBoundsValue)
    fun <DB: Any> SqExpression<*, DB>.notBetween(firstBoundsValue: SqExpression<*, DB>, secondBoundsValue: SqExpression<*, DB>): SqBetweenTest<Boolean> =
        this@SqContext.notBetween(this, firstBoundsValue, secondBoundsValue)
    fun <JAVA: Any?, DB: Any> notBetween(expression: SqExpression<JAVA, DB>, first: JAVA?, second: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqBetweenTest<Boolean> {
        @Suppress("DuplicatedCode")
        val firstParam = this.param<JAVA?, DB>(expression.type.sqCast(), (first == null), first)
        val secondParam = this.param<JAVA?, DB>(expression.type.sqCast(), (second == null), second)
        return this.notBetween(expression, firstParam, secondParam, type)
    }
    fun <JAVA: Any?> SqExpression<JAVA, *>.notBetween(first: JAVA?, second: JAVA?): SqBetweenTest<Boolean> = this@SqContext.notBetween(this, first, second)


    fun <DB: Any> inList(
        type: SqType<Boolean>,
        mainValue: SqExpression<*, DB>,
        listValues: Array<out SqExpression<*, DB>>,
    ): SqInListTest<Boolean>
    fun <DB: Any> inList(mainValue: SqExpression<*, DB>, listValues: Array<out SqExpression<*, DB>>, type: SqType<Boolean> = this.operationBooleanType): SqInListTest<Boolean> =
        this.inList(type, mainValue, listValues)
    fun <DB: Any> inList(mainValue: SqExpression<*, DB>, firstValue: SqExpression<*, DB>, vararg moreValues: SqExpression<*, DB>, type: SqType<Boolean> = this.operationBooleanType): SqInListTest<Boolean> =
        this.inList(mainValue, arrayOf(firstValue, *moreValues), type)
    infix fun <DB: Any> SqExpression<*, DB>.inList(values: Array<out SqExpression<*, DB>>): SqInListTest<Boolean> =
        this@SqContext.inList(this, values)
    fun <DB: Any> SqExpression<*, DB>.inList(firstValue: SqExpression<*, DB>, vararg moreValues: SqExpression<*, DB>): SqInListTest<Boolean> =
        this@SqContext.inList(this, firstValue, *moreValues)
    fun <JAVA: Any?, DB: Any> inList(expression: SqExpression<JAVA, DB>, listValues: Array<out JAVA?>, type: SqType<Boolean> = this.operationBooleanType): SqInListTest<Boolean> {
        val expressionType = expression.type.sqCast<JAVA & Any>()
        val listParams = listValues.map { value ->
            this.param<JAVA?, DB>(expressionType, (value == null), value)
        }
        return this.inList(expression, listParams.toTypedArray(), type)
    }
    fun <JAVA: Any?, DB: Any> inList(expression: SqExpression<JAVA, DB>, first: JAVA?, vararg more: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqInListTest<Boolean> {
        @Suppress("DuplicatedCode")
        val expressionType = expression.type.sqCast<JAVA & Any>()
        val listParams = listOf(first, *more).map { value ->
            this.param<JAVA?, DB>(expressionType, (value == null), value)
        }
        return this.inList(expression, listParams.toTypedArray(), type)
    }
    infix fun <JAVA: Any?> SqExpression<JAVA, *>.inList(listValues: Array<out JAVA?>): SqInListTest<Boolean> = this@SqContext.inList(this, listValues)
    fun <JAVA: Any?> SqExpression<JAVA, *>.inList(first: JAVA?, vararg more: JAVA?): SqInListTest<Boolean> = this@SqContext.inList(this, first, *more)

    fun <DB: Any> notInList(
        type: SqType<Boolean>,
        mainValue: SqExpression<*, DB>,
        listValues: Array<out SqExpression<*, DB>>,
    ): SqInListTest<Boolean>
    fun <DB: Any> notInList(mainValue: SqExpression<*, DB>, listValues: Array<out SqExpression<*, DB>>, type: SqType<Boolean> = this.operationBooleanType): SqInListTest<Boolean> =
        this.notInList(type, mainValue, listValues)
    fun <DB: Any> notInList(mainValue: SqExpression<*, DB>, firstValue: SqExpression<*, DB>, vararg moreValues: SqExpression<*, DB>, type: SqType<Boolean> = this.operationBooleanType): SqInListTest<Boolean> =
        this.notInList(type, mainValue, arrayOf(firstValue, *moreValues))
    infix fun <DB: Any> SqExpression<*, DB>.notInList(values: Array<out SqExpression<*, DB>>): SqInListTest<Boolean> =
        this@SqContext.notInList(this, values)
    fun <DB: Any> SqExpression<*, DB>.notInList(firstValue: SqExpression<*, DB>, vararg moreValues: SqExpression<*, DB>): SqInListTest<Boolean> =
        this@SqContext.notInList(this, firstValue, *moreValues)
    fun <JAVA: Any?, DB: Any> notInList(expression: SqExpression<JAVA, DB>, listValues: Array<out JAVA?>, type: SqType<Boolean> = this.operationBooleanType): SqInListTest<Boolean> {
        @Suppress("DuplicatedCode")
        val expressionType = expression.type.sqCast<JAVA & Any>()
        val listParams = listValues.map { value ->
            this.param<JAVA?, DB>(expressionType, (value == null), value)
        }
        return this.notInList(expression, listParams.toTypedArray(), type)
    }
    fun <JAVA: Any?, DB: Any> notInList(expression: SqExpression<JAVA, DB>, first: JAVA?, vararg more: JAVA?, type: SqType<Boolean> = this.operationBooleanType): SqInListTest<Boolean> {
        @Suppress("DuplicatedCode")
        val expressionType = expression.type.sqCast<JAVA & Any>()
        val listParams = listOf(first, *more).map { value ->
            this.param<JAVA?, DB>(expressionType, (value == null), value)
        }
        return this.notInList(expression, listParams.toTypedArray(), type)
    }
    infix fun <JAVA: Any?> SqExpression<JAVA, *>.notInList(listValues: Array<out JAVA?>): SqInListTest<Boolean> = this@SqContext.notInList(this, listValues)
    fun <JAVA: Any?> SqExpression<JAVA, *>.notInList(first: JAVA?, vararg more: JAVA?): SqInListTest<Boolean> = this@SqContext.notInList(this, first, *more)
    // endregion


    // region Functions
    fun <JAVA: Any?, DB: Any> function(
        type: SqType<JAVA & Any>, nullable: Boolean,
        name: String, nameSeparated: Boolean,
        values: Iterable<SqItem>,
    ): SqNamedFunction<JAVA, DB>

    fun <JAVA: Any?, DB: Any> function(
        type: SqType<JAVA & Any>, nullable: Boolean,
        name: String, nameSeparated: Boolean,
        vararg values: SqItem,
    ): SqNamedFunction<JAVA, DB> = this.function(type, nullable, name, nameSeparated, values.toList())

    fun <JAVA: Any?, DB: Any> all(select: SqSingleColReadStatement<JAVA, DB>): SqNamedFunction<JAVA, DB> =
        this.function(select.type, select.nullable, SqUtil.FUNCTION_NAME__ALL, nameSeparated = true, listOf(select))
    fun <JAVA: Any?, DB: Any> any(select: SqSingleColReadStatement<JAVA, DB>): SqNamedFunction<JAVA, DB> =
        this.function(select.type, select.nullable, SqUtil.FUNCTION_NAME__ANY, nameSeparated = true, listOf(select))
    fun <JAVA: Number?> avg(expression: SqExpression<JAVA, Number>): SqNamedFunction<JAVA, Number> =
        this.function(expression.type, expression.nullable, SqUtil.FUNCTION_NAME__AVG, nameSeparated = false, expression)
    fun <JAVA: Any?, DB: Any> coalesce(values: Iterable<SqExpression<out JAVA?, DB>>, last: SqExpression<JAVA, DB>): SqNamedFunction<JAVA, DB> =
        this.function(last.type, last.nullable, SqUtil.FUNCTION_NAME__COALESCE, nameSeparated = false, values.plus(last))
    fun <JAVA: Any?, DB: Any> coalesce(vararg values: SqExpression<out JAVA?, DB>, last: SqExpression<JAVA, DB>): SqNamedFunction<JAVA, DB> =
        this.function(last.type, last.nullable, SqUtil.FUNCTION_NAME__COALESCE, nameSeparated = false, listOf(*values, last))
    fun <JAVA: Number> count(type: SqType<JAVA>, value: SqExpression<*, *>): SqNamedFunction<JAVA, Number> =
        this.function(type.sqCast(), nullable = false, SqUtil.FUNCTION_NAME__COUNT, nameSeparated = false, value)
    fun count(value: SqExpression<*, *>, type: SqType<Long> = this.jLongType): SqNamedFunction<Long, Number> = this.count(type, value)
    fun exists(type: SqType<Boolean>, select: SqSelect): SqNamedFunction<Boolean, Boolean> =
        this.function(type, false, SqUtil.FUNCTION_NAME__EXISTS, nameSeparated = true, listOf(select))
    fun exists(select: SqSelect, type: SqType<Boolean> = this.operationBooleanType): SqNamedFunction<Boolean, Boolean> = this.exists(type, select)
    fun <JAVA: Any?, DB: Any> min(value: SqExpression<JAVA, DB>): SqNamedFunction<JAVA, DB> =
        this.function(value.type, value.nullable, SqUtil.FUNCTION_NAME__MIN, nameSeparated = false, listOf(value))
    fun <JAVA: Any?, DB: Any> max(value: SqExpression<JAVA, DB>): SqNamedFunction<JAVA, DB> =
        this.function(value.type, value.nullable, SqUtil.FUNCTION_NAME__MAX, nameSeparated = false, listOf(value))
    fun <JAVA: Number?> sum(expression: SqExpression<JAVA, Number>): SqNamedFunction<JAVA, Number> =
        this.function(expression.type, expression.nullable, SqUtil.FUNCTION_NAME__SUM, nameSeparated = false, expression)
    // endregion


    // region Case
    fun caseWhen(whenItem: SqExpression<*, Boolean>): SqCaseItemStart
    fun <JAVA: Any?, DB: Any> caseItem(whenItem: SqExpression<*, Boolean>, thenItem: SqExpression<JAVA, DB>): SqCaseItem<JAVA, DB>

    fun <JAVA: Any?, DB: Any> case(forceNullable: Boolean, items: Iterable<SqCaseItem<JAVA, DB>>): SqCase<JAVA?, DB>
    fun <JAVA: Any?, DB: Any> case(forceNullable: Boolean, items: Iterable<SqCaseItem<out JAVA, DB>>, elseItem: SqExpression<out JAVA, DB>): SqCase<out JAVA, DB>
    fun <JAVA: Any?, DB: Any> case(items: Iterable<SqCaseItem<JAVA, DB>>): SqCase<JAVA?, DB> = this.case(forceNullable = true, items)
    fun <JAVA: Any?, DB: Any> case(vararg items: SqCaseItem<JAVA, DB>): SqCase<JAVA?, DB> = this.case(items.toList())
    fun <JAVA: Any?, DB: Any> case(items: Iterable<SqCaseItem<out JAVA, DB>>, elseItem: SqExpression<out JAVA, DB>): SqCase<out JAVA, DB> =
        this.case(forceNullable = false, items, elseItem)
    fun <JAVA: Any?, DB: Any> case(vararg items: SqCaseItem<out JAVA, DB>, elseItem: SqExpression<out JAVA, DB>): SqCase<out JAVA, DB> =
        this.case(items.toList(), elseItem)
    // endregion


    // region Mathematical operations
    fun <JAVA: Number?> twoOperandMathOperation(
        type: SqType<JAVA & Any>, nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        operation: String,
        secondOperand: SqExpression<*, Number>,
    ): SqTwoOperandMathOperation<JAVA>
    fun <JAVA: Number?, PARAM: Number?> twoOperandMathOperation(
        type: SqType<JAVA & Any>, nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        operation: String,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> {
        val secondOperand = this.param<PARAM, Number>(secondOperandType.sqCast(), (secondOperandValue == null), secondOperandValue)
        return this.twoOperandMathOperation(type, nullable, firstOperand, operation, secondOperand)
    }


    fun <JAVA: Number?> add(type: SqType<JAVA & Any>, nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__ADD, secondOperand)
    fun <JAVA: Number?> add(nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.add(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperand)
    fun add(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.add(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.add(type: SqType<JAVA & Any>, nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.add(type, nullable, this, other)
    fun <JAVA: Number?> SqExpression<*, Number>.add(nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.add(nullable, this, other)
    infix fun SqExpression<*, Number>.add(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.add(this, other)

    fun <JAVA: Number?, PARAM: Number?> add(
        type: SqType<JAVA & Any>, nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__ADD, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> add(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.add(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> add(firstOperand: SqExpression<*, Number>, secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.add(nullable = true, firstOperand, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.add(
        type: SqType<JAVA & Any>, nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.add(type, nullable, this, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.add(
        nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.add(nullable, this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.add(secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this@SqContext.add(this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.add(secondOperandValue: PARAM, secondOperandClass: Class<Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.add(this, this@SqContext.requireTypeForNumber(secondOperandClass), secondOperandValue)
    infix fun <PARAM: Number?> SqExpression<*, Number>.add(secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this.add(secondOperandValue, (secondOperandValue as? Number)?.javaClass ?: Number::class.java)


    fun <JAVA: Number?> sub(type: SqType<JAVA & Any>, nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__SUBTRACT, secondOperand)
    fun <JAVA: Number?> sub(nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.sub(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperand)
    fun sub(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.sub(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.sub(type: SqType<JAVA & Any>, nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.sub(type, nullable, this, other)
    fun <JAVA: Number?> SqExpression<*, Number>.sub(nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.sub(nullable, this, other)
    infix fun SqExpression<*, Number>.sub(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.sub(this, other)

    fun <JAVA: Number?, PARAM: Number?> sub(
        type: SqType<JAVA & Any>, nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__SUBTRACT, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> sub(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.sub(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> sub(firstOperand: SqExpression<*, Number>, secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.sub(nullable = true, firstOperand, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.sub(
        type: SqType<JAVA & Any>, nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.sub(type, nullable, this, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.sub(
        nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.sub(nullable, this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.sub(secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this@SqContext.sub(this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.sub(secondOperandValue: PARAM, secondOperandClass: Class<Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.sub(this, this@SqContext.requireTypeForNumber(secondOperandClass), secondOperandValue)
    infix fun <PARAM: Number?> SqExpression<*, Number>.sub(secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this.sub(secondOperandValue, (secondOperandValue as? Number)?.javaClass ?: Number::class.java)

    fun <JAVA: Number?> mult(type: SqType<JAVA & Any>, nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__MULTIPLY, secondOperand)
    fun <JAVA: Number?> mult(nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.mult(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperand)
    fun mult(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.mult(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.mult(type: SqType<JAVA & Any>, nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.mult(type, nullable, this, other)
    fun <JAVA: Number?> SqExpression<*, Number>.mult(nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.mult(nullable, this, other)
    infix fun SqExpression<*, Number>.mult(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.mult(this, other)

    fun <JAVA: Number?, PARAM: Number?> mult(
        type: SqType<JAVA & Any>, nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__MULTIPLY, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> mult(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.mult(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> mult(firstOperand: SqExpression<*, Number>, secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.mult(nullable = true, firstOperand, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.mult(
        type: SqType<JAVA & Any>, nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.mult(type, nullable, this, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.mult(
        nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.mult(nullable, this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.mult(secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this@SqContext.mult(this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.mult(secondOperandValue: PARAM, secondOperandClass: Class<Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.mult(this, this@SqContext.requireTypeForNumber(secondOperandClass), secondOperandValue)
    infix fun <PARAM: Number?> SqExpression<*, Number>.mult(secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this.mult(secondOperandValue, (secondOperandValue as? Number)?.javaClass ?: Number::class.java)

    fun <JAVA: Number?> div(type: SqType<JAVA & Any>, nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__DIVIDE, secondOperand)
    fun <JAVA: Number?> div(nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.div(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperand)
    fun div(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.div(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.div(type: SqType<JAVA & Any>, nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.div(type, nullable, this, other)
    fun <JAVA: Number?> SqExpression<*, Number>.div(nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.div(nullable, this, other)
    infix fun SqExpression<*, Number>.div(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.div(this, other)

    fun <JAVA: Number?, PARAM: Number?> div(
        type: SqType<JAVA & Any>, nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__DIVIDE, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> div(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.div(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> div(firstOperand: SqExpression<*, Number>, secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.div(nullable = true, firstOperand, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.div(
        type: SqType<JAVA & Any>, nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.div(type, nullable, this, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.div(
        nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.div(nullable, this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.div(secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this@SqContext.div(this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.div(secondOperandValue: PARAM, secondOperandClass: Class<Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.div(this, this@SqContext.requireTypeForNumber(secondOperandClass), secondOperandValue)
    infix fun <PARAM: Number?> SqExpression<*, Number>.div(secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this.div(secondOperandValue, (secondOperandValue as? Number)?.javaClass ?: Number::class.java)

    fun <JAVA: Number?> mod(type: SqType<JAVA & Any>, nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__MODULO, secondOperand)
    fun <JAVA: Number?> mod(nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.mod(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperand)
    fun mod(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.mod(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.mod(type: SqType<JAVA & Any>, nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.mod(type, nullable, this, other)
    fun <JAVA: Number?> SqExpression<*, Number>.mod(nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.mod(nullable, this, other)
    infix fun SqExpression<*, Number>.mod(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.mod(this, other)

    fun <JAVA: Number?, PARAM: Number?> mod(
        type: SqType<JAVA & Any>, nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__MODULO, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> mod(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.mod(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> mod(firstOperand: SqExpression<*, Number>, secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.mod(nullable = true, firstOperand, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.mod(
        type: SqType<JAVA & Any>, nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.mod(type, nullable, this, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.mod(
        nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.mod(nullable, this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.mod(secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this@SqContext.mod(this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.mod(secondOperandValue: PARAM, secondOperandClass: Class<Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.mod(this, this@SqContext.requireTypeForNumber(secondOperandClass), secondOperandValue)
    infix fun <PARAM: Number?> SqExpression<*, Number>.mod(secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this.mod(secondOperandValue, (secondOperandValue as? Number)?.javaClass ?: Number::class.java)

    fun <JAVA: Number?> bitwiseAnd(type: SqType<JAVA & Any>, nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__BITWISE_AND, secondOperand)
    fun <JAVA: Number?> bitwiseAnd(nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.bitwiseAnd(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperand)
    fun bitwiseAnd(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.bitwiseAnd(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.bitwiseAnd(type: SqType<JAVA & Any>, nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseAnd(type, nullable, this, other)
    fun <JAVA: Number?> SqExpression<*, Number>.bitwiseAnd(nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseAnd(nullable, this, other)
    infix fun SqExpression<*, Number>.bitwiseAnd(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.bitwiseAnd(this, other)

    fun <JAVA: Number?, PARAM: Number?> bitwiseAnd(
        type: SqType<JAVA & Any>, nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__BITWISE_AND, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> bitwiseAnd(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.bitwiseAnd(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> bitwiseAnd(firstOperand: SqExpression<*, Number>, secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.bitwiseAnd(nullable = true, firstOperand, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.bitwiseAnd(
        type: SqType<JAVA & Any>, nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseAnd(type, nullable, this, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.bitwiseAnd(
        nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseAnd(nullable, this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.bitwiseAnd(secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this@SqContext.bitwiseAnd(this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.bitwiseAnd(secondOperandValue: PARAM, secondOperandClass: Class<Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.bitwiseAnd(this, this@SqContext.requireTypeForNumber(secondOperandClass), secondOperandValue)
    infix fun <PARAM: Number?> SqExpression<*, Number>.bitwiseAnd(secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this.bitwiseAnd(secondOperandValue, (secondOperandValue as? Number)?.javaClass ?: Number::class.java)

    fun <JAVA: Number?> bitwiseOr(type: SqType<JAVA & Any>, nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__BITWISE_OR, secondOperand)
    fun <JAVA: Number?> bitwiseOr(nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.bitwiseOr(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperand)
    fun bitwiseOr(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.bitwiseOr(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.bitwiseOr(type: SqType<JAVA & Any>, nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseOr(type, nullable, this, other)
    fun <JAVA: Number?> SqExpression<*, Number>.bitwiseOr(nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseOr(nullable, this, other)
    infix fun SqExpression<*, Number>.bitwiseOr(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.bitwiseOr(this, other)

    fun <JAVA: Number?, PARAM: Number?> bitwiseOr(
        type: SqType<JAVA & Any>, nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__BITWISE_OR, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> bitwiseOr(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.bitwiseOr(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> bitwiseOr(firstOperand: SqExpression<*, Number>, secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.bitwiseOr(nullable = true, firstOperand, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.bitwiseOr(
        type: SqType<JAVA & Any>, nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseOr(type, nullable, this, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.bitwiseOr(
        nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseOr(nullable, this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.bitwiseOr(secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this@SqContext.bitwiseOr(this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.bitwiseOr(secondOperandValue: PARAM, secondOperandClass: Class<Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.bitwiseOr(this, this@SqContext.requireTypeForNumber(secondOperandClass), secondOperandValue)
    infix fun <PARAM: Number?> SqExpression<*, Number>.bitwiseOr(secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this.bitwiseOr(secondOperandValue, (secondOperandValue as? Number)?.javaClass ?: Number::class.java)

    fun <JAVA: Number?> bitwiseXor(type: SqType<JAVA & Any>, nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__BITWISE_XOR, secondOperand)
    fun <JAVA: Number?> bitwiseXor(nullable: Boolean, firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this.bitwiseXor(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperand)
    fun bitwiseXor(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.bitwiseXor(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.bitwiseXor(type: SqType<JAVA & Any>, nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseXor(type, nullable, this, other)
    fun <JAVA: Number?> SqExpression<*, Number>.bitwiseXor(nullable: Boolean, other: SqExpression<*, Number>): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseXor(nullable, this, other)
    infix fun SqExpression<*, Number>.bitwiseXor(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.bitwiseXor(this, other)

    fun <JAVA: Number?, PARAM: Number?> bitwiseXor(
        type: SqType<JAVA & Any>, nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.twoOperandMathOperation(type, nullable, firstOperand, SqUtil.MATH_OPERATION__BITWISE_XOR, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> bitwiseXor(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this.bitwiseXor(this.mathOpNumberType.sqCast(), nullable, firstOperand, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> bitwiseXor(firstOperand: SqExpression<*, Number>, secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.bitwiseXor(nullable = true, firstOperand, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.bitwiseXor(
        type: SqType<JAVA & Any>, nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseXor(type, nullable, this, secondOperandType, secondOperandValue)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.bitwiseXor(
        nullable: Boolean,
        secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseXor(nullable, this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.bitwiseXor(secondOperandType: SqType<PARAM & Any>, secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this@SqContext.bitwiseXor(this, secondOperandType, secondOperandValue)
    fun <PARAM: Number?> SqExpression<*, Number>.bitwiseXor(secondOperandValue: PARAM, secondOperandClass: Class<Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.bitwiseXor(this, this@SqContext.requireTypeForNumber(secondOperandClass), secondOperandValue)
    infix fun <PARAM: Number?> SqExpression<*, Number>.bitwiseXor(secondOperandValue: PARAM): SqTwoOperandMathOperation<Number?> =
        this.bitwiseXor(secondOperandValue, (secondOperandValue as? Number)?.javaClass ?: Number::class.java)
    // endregion


    // region Statements - join, order by, select, union
    fun createJoin(type: SqJoinType, mainColSet: SqColSet, joinedColSet: SqColSet): SqJoin
    fun SqColSet.join(type: SqJoinType, joined: SqColSet): SqJoin = this@SqContext.createJoin(type, this, joined)
    fun createInnerJoin(mainColSet: SqColSet, joinedColSet: SqColSet): SqJoin = this.createJoin(SqJoinType.INNER, mainColSet, joinedColSet)
    infix fun SqColSet.innerJoin(joined: SqColSet): SqJoin = this@SqContext.createInnerJoin(this, joined)
    fun createLeftJoin(mainColSet: SqColSet, joinedColSet: SqColSet): SqJoin = this.createJoin(SqJoinType.LEFT, mainColSet, joinedColSet)
    infix fun SqColSet.leftJoin(joined: SqColSet): SqJoin = this@SqContext.createLeftJoin(this, joined)
    fun createRightJoin(mainColSet: SqColSet, joinedColSet: SqColSet): SqJoin = this.createJoin(SqJoinType.RIGHT, mainColSet, joinedColSet)
    infix fun SqColSet.rightJoin(joined: SqColSet): SqJoin = this@SqContext.createRightJoin(this, joined)
    fun createFullJoin(mainColSet: SqColSet, joinedColSet: SqColSet): SqJoin = this.createJoin(SqJoinType.FULL, mainColSet, joinedColSet)
    infix fun SqColSet.fullJoin(joined: SqColSet): SqJoin = this@SqContext.createFullJoin(this, joined)

    fun createOrderBy(column: SqColumn<*, *>, order: SqSortOrder): SqOrderBy
    infix fun SqColumn<*, *>.orderBy(order: SqSortOrder): SqOrderBy = this@SqContext.createOrderBy(this, order)
    fun SqColumn<*, *>.asc(): SqOrderBy = this.orderBy(SqSortOrder.ASC)
    fun SqColumn<*, *>.desc(): SqOrderBy = this.orderBy(SqSortOrder.DESC)


    fun select(distinct: Boolean, columns: Iterable<SqColumn<*, *>>): SqMultiColSelect
    fun select(distinct: Boolean, first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqMultiColSelect =
        this.select(distinct, listOf(first, second, *more))
    fun select(columns: Iterable<SqColumn<*, *>>): SqMultiColSelect = this.select(distinct = false, columns)
    fun select(first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqMultiColSelect =
        this.select(distinct = false, first, second, *more)
    fun selectDistinct(columns: Iterable<SqColumn<*, *>>): SqMultiColSelect = this.select(distinct = true, columns)
    fun selectDistinct(first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqMultiColSelect =
        this.select(distinct = true, first, second, *more)

    fun <JAVA: Any?, DB: Any> select(distinct: Boolean, column: SqColumn<JAVA, DB>): SqSingleColSelect<JAVA, DB>
    fun <JAVA: Any?, DB: Any> select(column: SqColumn<JAVA, DB>): SqSingleColSelect<JAVA, DB> = this.select(distinct = false, column)
    fun <JAVA: Any?, DB: Any> selectDistinct(column: SqColumn<JAVA, DB>): SqSingleColSelect<JAVA, DB> = this.select(distinct = true, column)


    fun union(unionAll: Boolean, selects: Iterable<SqSelect>): SqMultiColUnion
    fun union(unionAll: Boolean, first: SqSelect, second: SqSelect, vararg more: SqSelect): SqMultiColUnion = this.union(unionAll, listOf(first, second, *more))
    fun union(selects: Iterable<SqSelect>): SqMultiColUnion = this.union(unionAll = false, selects)
    fun union(first: SqSelect, second: SqSelect, vararg more: SqSelect): SqMultiColUnion = this.union(unionAll = false, first, second, *more)
    fun unionAll(selects: Iterable<SqSelect>): SqMultiColUnion = this.union(unionAll = true, selects)
    fun unionAll(first: SqSelect, second: SqSelect, vararg more: SqSelect): SqMultiColUnion = this.union(unionAll = true, first, second, *more)

    fun <JAVA: Any?, DB: Any> union(unionAll: Boolean, selects: Iterable<SqSingleColSelect<JAVA, DB>>): SqSingleColUnion<JAVA, DB>
    fun <JAVA: Any?, DB: Any> union(
        unionAll: Boolean,
        first: SqSingleColSelect<JAVA, DB>,
        second: SqSingleColSelect<JAVA, DB>,
        vararg more: SqSingleColSelect<JAVA, DB>,
    ): SqSingleColUnion<JAVA, DB> = this.union(unionAll, listOf(first, second, *more))
    fun <JAVA: Any?, DB: Any> union(selects: Iterable<SqSingleColSelect<JAVA, DB>>): SqSingleColUnion<JAVA, DB> =
        this.union(unionAll = false, selects)
    fun <JAVA: Any?, DB: Any> union(
        first: SqSingleColSelect<JAVA, DB>,
        second: SqSingleColSelect<JAVA, DB>,
        vararg more: SqSingleColSelect<JAVA, DB>,
    ): SqSingleColUnion<JAVA, DB> = this.union(unionAll = false, first, second, *more)
    fun <JAVA: Any?, DB: Any> unionAll(selects: Iterable<SqSingleColSelect<JAVA, DB>>): SqSingleColUnion<JAVA, DB> =
        this.union(unionAll = true, selects)
    fun <JAVA: Any?, DB: Any> unionAll(
        first: SqSingleColSelect<JAVA, DB>,
        second: SqSingleColSelect<JAVA, DB>,
        vararg more: SqSingleColSelect<JAVA, DB>,
    ): SqSingleColUnion<JAVA, DB> = this.union(unionAll = true, first, second, *more)
    // endregion


    // region Statements - other
    fun <T: SqTable> insertInto(table: T): SqInsert<T>
    fun <T: SqTable> update(table: T): SqUpdate<T>
    fun <T: SqTable> deleteFrom(table: T): SqDelete<T>
    // endregion
}


interface SqConnectedContext: SqContext {
    val connection: Connection


    // region Statements - select, union
    override fun select(distinct: Boolean, columns: Iterable<SqColumn<*, *>>): SqConnMultiColSelect
    override fun select(distinct: Boolean, first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqConnMultiColSelect =
        this.select(distinct, listOf(first, second, *more))
    override fun select(columns: Iterable<SqColumn<*, *>>): SqConnMultiColSelect = this.select(distinct = false, columns)
    override fun select(first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqConnMultiColSelect =
        this.select(distinct = false, first, second, *more)
    override fun selectDistinct(columns: Iterable<SqColumn<*, *>>): SqConnMultiColSelect = this.select(distinct = true, columns)
    override fun selectDistinct(first: SqColumn<*, *>, second: SqColumn<*, *>, vararg more: SqColumn<*, *>): SqConnMultiColSelect =
        this.select(distinct = true, first, second, *more)

    override fun <JAVA: Any?, DB: Any> select(distinct: Boolean, column: SqColumn<JAVA, DB>): SqConnSingleColSelect<JAVA, DB>
    override fun <JAVA: Any?, DB: Any> select(column: SqColumn<JAVA, DB>): SqConnSingleColSelect<JAVA, DB> = this.select(distinct = false, column)
    override fun <JAVA: Any?, DB: Any> selectDistinct(column: SqColumn<JAVA, DB>): SqConnSingleColSelect<JAVA, DB> = this.select(distinct = true, column)


    override fun union(unionAll: Boolean, selects: Iterable<SqSelect>): SqConnMultiColUnion
    override fun union(unionAll: Boolean, first: SqSelect, second: SqSelect, vararg more: SqSelect): SqConnMultiColUnion = this.union(unionAll, listOf(first, second, *more))
    override fun union(selects: Iterable<SqSelect>): SqConnMultiColUnion = this.union(unionAll = false, selects)
    override fun union(first: SqSelect, second: SqSelect, vararg more: SqSelect): SqConnMultiColUnion = this.union(unionAll = false, first, second, *more)
    override fun unionAll(selects: Iterable<SqSelect>): SqConnMultiColUnion = this.union(unionAll = true, selects)
    override fun unionAll(first: SqSelect, second: SqSelect, vararg more: SqSelect): SqConnMultiColUnion = this.union(unionAll = true, first, second, *more)

    override fun <JAVA: Any?, DB: Any> union(unionAll: Boolean, selects: Iterable<SqSingleColSelect<JAVA, DB>>): SqConnSingleColUnion<JAVA, DB>
    override fun <JAVA: Any?, DB: Any> union(
        unionAll: Boolean,
        first: SqSingleColSelect<JAVA, DB>,
        second: SqSingleColSelect<JAVA, DB>,
        vararg more: SqSingleColSelect<JAVA, DB>,
    ): SqConnSingleColUnion<JAVA, DB> = this.union(unionAll, listOf(first, second, *more))
    override fun <JAVA: Any?, DB: Any> union(selects: Iterable<SqSingleColSelect<JAVA, DB>>): SqConnSingleColUnion<JAVA, DB> =
        this.union(unionAll = false, selects)
    override fun <JAVA: Any?, DB: Any> union(
        first: SqSingleColSelect<JAVA, DB>,
        second: SqSingleColSelect<JAVA, DB>,
        vararg more: SqSingleColSelect<JAVA, DB>,
    ): SqConnSingleColUnion<JAVA, DB> = this.union(unionAll = false, first, second, *more)
    override fun <JAVA: Any?, DB: Any> unionAll(selects: Iterable<SqSingleColSelect<JAVA, DB>>): SqConnSingleColUnion<JAVA, DB> =
        this.union(unionAll = true, selects)
    override fun <JAVA: Any?, DB: Any> unionAll(
        first: SqSingleColSelect<JAVA, DB>,
        second: SqSingleColSelect<JAVA, DB>,
        vararg more: SqSingleColSelect<JAVA, DB>,
    ): SqConnSingleColUnion<JAVA, DB> = this.union(unionAll = true, first, second, *more)
    // endregion


    // region Statements - other
    override fun <T : SqTable> insertInto(table: T): SqConnInsert<T>
    override fun <T : SqTable> update(table: T): SqConnUpdate<T>
    override fun <T : SqTable> deleteFrom(table: T): SqConnDelete<T>
    // endregion
}
