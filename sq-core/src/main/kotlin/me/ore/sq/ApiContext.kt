package me.ore.sq

import me.ore.sq.generic.SqGenericContextImpl
import java.sql.Connection
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


    // region Base items
    fun <JAVA: Any?, DB: Any> SqType<JAVA & Any>.param(value: JAVA): SqParameter<JAVA, DB> = this.createParam(value, this@SqContext)
    fun <JAVA: Any?, DB: Any> param(type: SqType<JAVA & Any>, value: JAVA, methodDiff: Boolean = true): SqParameter<JAVA, DB> = type.createParam(value, this)


    fun <JAVA: Any, DB: Any> SqType<JAVA>.nullItem(methodDiff: Boolean = true): SqNull<JAVA, DB> = this.createNull(this@SqContext)
    fun <JAVA: Any, DB: Any> nullItem(type: SqType<JAVA>): SqNull<JAVA, DB> = type.createNull(this)


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
    fun and(values: Iterable<SqExpression<*, Boolean>>, type: SqType<Boolean>? = null): SqMultiValueTest<Boolean>
    fun and(first: SqExpression<*, Boolean>, vararg more: SqExpression<*, Boolean>, type: SqType<Boolean>? = null): SqMultiValueTest<Boolean> = this.and(listOf(first, *more), type)

    fun or(values: Iterable<SqExpression<*, Boolean>>, type: SqType<Boolean>? = null): SqMultiValueTest<Boolean>
    fun or(first: SqExpression<*, Boolean>, vararg more: SqExpression<*, Boolean>, type: SqType<Boolean>? = null): SqMultiValueTest<Boolean> = this.or(listOf(first, *more), type)


    fun not(value: SqExpression<*, Boolean>, type: SqType<Boolean>? = null): SqSingleValueTest<Boolean>


    fun isNull(value: SqExpression<*, *>, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqSingleValueTest<Boolean>
    fun SqExpression<*, *>.isNull(type: SqType<Boolean>? = null): SqSingleValueTest<Boolean> = this@SqContext.isNull(this, type)

    fun isNotNull(value: SqExpression<*, *>, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqSingleValueTest<Boolean>
    fun SqExpression<*, *>.isNotNull(type: SqType<Boolean>? = null): SqSingleValueTest<Boolean> = this@SqContext.isNotNull(this, type)
    // endregion


    // region Comparisons - "two value" tests
    fun <DB: Any> eq(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqTwoValueTest<Boolean>
    fun <DB: Any> SqExpression<*, DB>.eq(other: SqExpression<*, DB>, type: SqType<Boolean>? = null): SqTwoValueTest<Boolean> = this@SqContext.eq(this, other, type)
    infix fun <DB: Any> SqExpression<*, DB>.eq(other: SqExpression<*, DB>): SqTwoValueTest<Boolean> = this.eq(other, null)
    fun <JAVA: Any?, DB: Any> eq(expression: SqExpression<JAVA, DB>, value: JAVA?, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, DB>(expression.type.sqCast(), value)
        return this.eq(expression, param, type)
    }
    fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.eq(value: JAVA?, type: SqType<Boolean>? = null): SqTwoValueTest<Boolean> = this@SqContext.eq(this, value, type)
    infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.eq(value: JAVA?): SqTwoValueTest<Boolean> = this.eq(value, null)

    fun <DB: Any> neq(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqTwoValueTest<Boolean>
    fun <DB: Any> SqExpression<*, DB>.neq(other: SqExpression<*, DB>, type: SqType<Boolean>? = null): SqTwoValueTest<Boolean> = this@SqContext.neq(this, other, type)
    infix fun <DB: Any> SqExpression<*, DB>.neq(other: SqExpression<*, DB>): SqTwoValueTest<Boolean> = this.neq(other, null)
    fun <JAVA: Any?, DB: Any> neq(expression: SqExpression<JAVA, DB>, value: JAVA?, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, DB>(expression.type.sqCast(), value)
        return this.neq(expression, param, type)
    }
    fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.neq(value: JAVA?, type: SqType<Boolean>? = null): SqTwoValueTest<Boolean> = this@SqContext.neq(this, value, type)
    infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.neq(value: JAVA?): SqTwoValueTest<Boolean> = this.neq(value, null)

    fun <DB: Any> gt(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqTwoValueTest<Boolean>
    fun <DB: Any> SqExpression<*, DB>.gt(other: SqExpression<*, DB>, type: SqType<Boolean>? = null): SqTwoValueTest<Boolean> = this@SqContext.gt(this, other, type)
    infix fun <DB: Any> SqExpression<*, DB>.gt(other: SqExpression<*, DB>): SqTwoValueTest<Boolean> = this.gt(other, null)
    fun <JAVA: Any?, DB: Any> gt(expression: SqExpression<JAVA, DB>, value: JAVA?, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, DB>(expression.type.sqCast(), value)
        return this.gt(expression, param, type)
    }
    fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.gt(value: JAVA?, type: SqType<Boolean>? = null): SqTwoValueTest<Boolean> = this@SqContext.gt(this, value, type)
    infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.gt(value: JAVA?): SqTwoValueTest<Boolean> = this.gt(value, null)

    fun <DB: Any> gte(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqTwoValueTest<Boolean>
    fun <DB: Any> SqExpression<*, DB>.gte(other: SqExpression<*, DB>, type: SqType<Boolean>? = null): SqTwoValueTest<Boolean> = this@SqContext.gte(this, other, type)
    infix fun <DB: Any> SqExpression<*, DB>.gte(other: SqExpression<*, DB>): SqTwoValueTest<Boolean> = this.gte(other, null)
    fun <JAVA: Any?, DB: Any> gte(expression: SqExpression<JAVA, DB>, value: JAVA?, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, DB>(expression.type.sqCast(), value)
        return this.gte(expression, param, type)
    }
    fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.gte(value: JAVA?, type: SqType<Boolean>? = null): SqTwoValueTest<Boolean> = this@SqContext.gte(this, value, type)
    infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.gte(value: JAVA?): SqTwoValueTest<Boolean> = this.gte(value, null)

    fun <DB: Any> lt(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqTwoValueTest<Boolean>
    fun <DB: Any> SqExpression<*, DB>.lt(other: SqExpression<*, DB>, type: SqType<Boolean>? = null): SqTwoValueTest<Boolean> = this@SqContext.lt(this, other, type)
    infix fun <DB: Any> SqExpression<*, DB>.lt(other: SqExpression<*, DB>): SqTwoValueTest<Boolean> = this.lt(other, null)
    fun <JAVA: Any?, DB: Any> lt(expression: SqExpression<JAVA, DB>, value: JAVA?, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, DB>(expression.type.sqCast(), value)
        return this.lt(expression, param, type)
    }
    fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.lt(value: JAVA?, type: SqType<Boolean>? = null): SqTwoValueTest<Boolean> = this@SqContext.lt(this, value, type)
    infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.lt(value: JAVA?): SqTwoValueTest<Boolean> = this.lt(value, null)

    fun <DB: Any> lte(firstValue: SqExpression<*, DB>, secondValue: SqExpression<*, DB>, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqTwoValueTest<Boolean>
    fun <DB: Any> SqExpression<*, DB>.lte(other: SqExpression<*, DB>, type: SqType<Boolean>? = null): SqTwoValueTest<Boolean> = this@SqContext.lte(this, other, type)
    infix fun <DB: Any> SqExpression<*, DB>.lte(other: SqExpression<*, DB>): SqTwoValueTest<Boolean> = this.lte(other, null)
    fun <JAVA: Any?, DB: Any> lte(expression: SqExpression<JAVA, DB>, value: JAVA?, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, DB>(expression.type.sqCast(), value)
        return this.lte(expression, param, type)
    }
    fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.lte(value: JAVA?, type: SqType<Boolean>? = null): SqTwoValueTest<Boolean> = this@SqContext.lte(this, value, type)
    infix fun <JAVA: Any?, DB: Any> SqExpression<JAVA, DB>.lte(value: JAVA?): SqTwoValueTest<Boolean> = this.lte(value, null)

    fun like(firstValue: SqExpression<*, String>, secondValue: SqExpression<*, String>, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqTwoValueTest<Boolean>
    fun SqExpression<*, String>.like(other: SqExpression<*, String>, type: SqType<Boolean>? = null): SqTwoValueTest<Boolean> = this@SqContext.like(this, other, type)
    infix fun SqExpression<*, String>.like(other: SqExpression<*, String>): SqTwoValueTest<Boolean> = this.like(other, null)
    fun <JAVA: Any?> like(expression: SqExpression<JAVA, String>, value: JAVA?, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, String>(expression.type.sqCast(), value)
        return this.like(expression, param, type)
    }
    fun <JAVA: Any?> SqExpression<JAVA, String>.like(value: JAVA?, type: SqType<Boolean>? = null): SqTwoValueTest<Boolean> = this@SqContext.like(this, value, type)
    infix fun <JAVA: Any?> SqExpression<JAVA, String>.like(value: JAVA?): SqTwoValueTest<Boolean> = this.like(value, null)

    fun notLike(firstValue: SqExpression<*, String>, secondValue: SqExpression<*, String>, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqTwoValueTest<Boolean>
    fun SqExpression<*, String>.notLike(other: SqExpression<*, String>, type: SqType<Boolean>? = null): SqTwoValueTest<Boolean> = this@SqContext.notLike(this, other, type)
    infix fun SqExpression<*, String>.notLike(other: SqExpression<*, String>): SqTwoValueTest<Boolean> = this.notLike(other, null)
    fun <JAVA: Any?> notLike(expression: SqExpression<JAVA, String>, value: JAVA?, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqTwoValueTest<Boolean> {
        val param = this.param<JAVA?, String>(expression.type.sqCast(), value)
        return this.notLike(expression, param, type)
    }
    fun <JAVA: Any?> SqExpression<JAVA, String>.notLike(value: JAVA?, type: SqType<Boolean>? = null): SqTwoValueTest<Boolean> = this@SqContext.notLike(this, value, type)
    infix fun <JAVA: Any?> SqExpression<JAVA, String>.notLike(value: JAVA?): SqTwoValueTest<Boolean> = this.notLike(value, null)
    // endregion


    // region Comparisons - between, not between, in [list], not in [list]
    fun <DB: Any> between(
        mainValue: SqExpression<*, DB>,
        firstBoundsValue: SqExpression<*, DB>,
        secondBoundsValue: SqExpression<*, DB>,
        type: SqType<Boolean>? = null,
        methodDiff: Boolean = true,
    ): SqBetweenTest<Boolean>
    fun <DB: Any> SqExpression<*, DB>.between(firstBoundsValue: SqExpression<*, DB>, secondBoundsValue: SqExpression<*, DB>, type: SqType<Boolean>? = null): SqBetweenTest<Boolean> =
        this@SqContext.between(this, firstBoundsValue, secondBoundsValue, type)
    fun <JAVA: Any?, DB: Any> between(expression: SqExpression<JAVA, DB>, first: JAVA?, second: JAVA?, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqBetweenTest<Boolean> {
        val firstParam = this.param<JAVA?, DB>(expression.type.sqCast(), first)
        val secondParam = this.param<JAVA?, DB>(expression.type.sqCast(), second)
        return this.between(expression, firstParam, secondParam, type)
    }
    fun <JAVA: Any?> SqExpression<JAVA, *>.between(first: JAVA?, second: JAVA?, type: SqType<Boolean>? = null): SqBetweenTest<Boolean> =
        this@SqContext.between(this, first, second, type)

    fun <DB: Any> notBetween(
        mainValue: SqExpression<*, DB>,
        firstBoundsValue: SqExpression<*, DB>,
        secondBoundsValue: SqExpression<*, DB>,
        type: SqType<Boolean>? = null,
        methodDiff: Boolean = true,
    ): SqBetweenTest<Boolean>
    fun <DB: Any> SqExpression<*, DB>.notBetween(firstBoundsValue: SqExpression<*, DB>, secondBoundsValue: SqExpression<*, DB>, type: SqType<Boolean>? = null): SqBetweenTest<Boolean> =
        this@SqContext.notBetween(this, firstBoundsValue, secondBoundsValue, type)
    fun <JAVA: Any?, DB: Any> notBetween(expression: SqExpression<JAVA, DB>, first: JAVA?, second: JAVA?, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqBetweenTest<Boolean> {
        @Suppress("DuplicatedCode")
        val firstParam = this.param<JAVA?, DB>(expression.type.sqCast(), first)
        val secondParam = this.param<JAVA?, DB>(expression.type.sqCast(), second)
        return this.notBetween(expression, firstParam, secondParam, type)
    }
    fun <JAVA: Any?> SqExpression<JAVA, *>.notBetween(first: JAVA?, second: JAVA?, type: SqType<Boolean>? = null): SqBetweenTest<Boolean> =
        this@SqContext.notBetween(this, first, second, type)


    fun <DB: Any> inList(mainValue: SqExpression<*, DB>, listValues: Array<out SqExpression<*, DB>>, type: SqType<Boolean>? = null): SqInListTest<Boolean>
    fun <DB: Any> inList(mainValue: SqExpression<*, DB>, firstValue: SqExpression<*, DB>, vararg moreValues: SqExpression<*, DB>, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqInListTest<Boolean> =
        this.inList(mainValue, arrayOf(firstValue, *moreValues), type)
    infix fun <DB: Any> SqExpression<*, DB>.inList(values: Array<out SqExpression<*, DB>>): SqInListTest<Boolean> =
        this@SqContext.inList(this, values)
    fun <DB: Any> SqExpression<*, DB>.inList(firstValue: SqExpression<*, DB>, vararg moreValues: SqExpression<*, DB>, type: SqType<Boolean>? = null): SqInListTest<Boolean> =
        this@SqContext.inList(this, firstValue, *moreValues, type = type)
    fun <JAVA: Any?, DB: Any> inList(expression: SqExpression<JAVA, DB>, listValues: Array<out JAVA?>, type: SqType<Boolean>? = null): SqInListTest<Boolean> {
        val expressionType = expression.type.sqCast<JAVA & Any>()
        val listParams = listValues.map { value ->
            this.param<JAVA?, DB>(expressionType, value)
        }
        return this.inList(expression, listParams.toTypedArray(), type)
    }
    fun <JAVA: Any?, DB: Any> inList(expression: SqExpression<JAVA, DB>, first: JAVA?, vararg more: JAVA?, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqInListTest<Boolean> {
        @Suppress("DuplicatedCode")
        val expressionType = expression.type.sqCast<JAVA & Any>()
        val listParams = listOf(first, *more).map { value ->
            this.param<JAVA?, DB>(expressionType, value)
        }
        return this.inList(expression, listParams.toTypedArray(), type)
    }
    infix fun <JAVA: Any?> SqExpression<JAVA, *>.inList(listValues: Array<out JAVA?>): SqInListTest<Boolean> = this@SqContext.inList(this, listValues)
    fun <JAVA: Any?> SqExpression<JAVA, *>.inList(first: JAVA?, vararg more: JAVA?, type: SqType<Boolean>? = null): SqInListTest<Boolean> =
        this@SqContext.inList(this, first, *more, type = type)

    fun <DB: Any> notInList(mainValue: SqExpression<*, DB>, listValues: Array<out SqExpression<*, DB>>, type: SqType<Boolean>? = null): SqInListTest<Boolean>
    fun <DB: Any> notInList(mainValue: SqExpression<*, DB>, firstValue: SqExpression<*, DB>, vararg moreValues: SqExpression<*, DB>, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqInListTest<Boolean> =
        this.notInList(mainValue, arrayOf(firstValue, *moreValues), type)
    infix fun <DB: Any> SqExpression<*, DB>.notInList(values: Array<out SqExpression<*, DB>>): SqInListTest<Boolean> =
        this@SqContext.notInList(this, values)
    fun <DB: Any> SqExpression<*, DB>.notInList(firstValue: SqExpression<*, DB>, vararg moreValues: SqExpression<*, DB>, type: SqType<Boolean>? = null): SqInListTest<Boolean> =
        this@SqContext.notInList(this, firstValue, *moreValues, type = type)
    fun <JAVA: Any?, DB: Any> notInList(expression: SqExpression<JAVA, DB>, listValues: Array<out JAVA?>, type: SqType<Boolean>? = null): SqInListTest<Boolean> {
        @Suppress("DuplicatedCode")
        val expressionType = expression.type.sqCast<JAVA & Any>()
        val listParams = listValues.map { value ->
            this.param<JAVA?, DB>(expressionType, value)
        }
        return this.notInList(expression, listParams.toTypedArray(), type)
    }
    fun <JAVA: Any?, DB: Any> notInList(expression: SqExpression<JAVA, DB>, first: JAVA?, vararg more: JAVA?, type: SqType<Boolean>? = null, methodDiff: Boolean = true): SqInListTest<Boolean> {
        @Suppress("DuplicatedCode")
        val expressionType = expression.type.sqCast<JAVA & Any>()
        val listParams = listOf(first, *more).map { value ->
            this.param<JAVA?, DB>(expressionType, value)
        }
        return this.notInList(expression, listParams.toTypedArray(), type)
    }
    infix fun <JAVA: Any?> SqExpression<JAVA, *>.notInList(listValues: Array<out JAVA?>): SqInListTest<Boolean> = this@SqContext.notInList(this, listValues)
    fun <JAVA: Any?> SqExpression<JAVA, *>.notInList(first: JAVA?, vararg more: JAVA?, type: SqType<Boolean>? = null): SqInListTest<Boolean> =
        this@SqContext.notInList(this, first, *more, type = type)
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
    fun count(value: SqExpression<*, *>, type: SqType<Long>? = null): SqNamedFunction<Long, Number>
    fun exists(select: SqSelect, type: SqType<Boolean>? = null): SqNamedFunction<Boolean, Boolean>
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
    fun <JAVA: Number?> add(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperand: SqExpression<*, Number>,
        type: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA>
    fun add(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.add(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.add(nullable: Boolean, other: SqExpression<*, Number>, type: SqType<JAVA & Any>? = null): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.add(nullable, this, other, type)
    infix fun SqExpression<*, Number>.add(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.add(this, other)

    fun <JAVA: Number?, PARAM: Number?> add(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA>
    fun <JAVA: Number?, PARAM: Number?> add(
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
        methodDiff: Boolean = true,
    ): SqTwoOperandMathOperation<JAVA> =
        this.add(nullable = true, firstOperand, secondOperandValue, secondOperandType, resultType)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.add(
        nullable: Boolean,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.add(nullable, this, secondOperandValue, secondOperandType, resultType)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.add(
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.add(this, secondOperandValue, secondOperandType, resultType)
    infix fun <JAVA: Number?> SqExpression<*, Number>.add(secondOperandValue: Number?): SqTwoOperandMathOperation<JAVA> =
        this.add(secondOperandValue, null, null)


    fun <JAVA: Number?> sub(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperand: SqExpression<*, Number>,
        type: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA>
    fun sub(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.sub(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.sub(nullable: Boolean, other: SqExpression<*, Number>, type: SqType<JAVA & Any>? = null): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.sub(nullable, this, other, type)
    infix fun SqExpression<*, Number>.sub(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.sub(this, other)

    fun <JAVA: Number?, PARAM: Number?> sub(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA>
    fun <JAVA: Number?, PARAM: Number?> sub(
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
        methodDiff: Boolean = true,
    ): SqTwoOperandMathOperation<JAVA> =
        this.sub(nullable = true, firstOperand, secondOperandValue, secondOperandType, resultType)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.sub(
        nullable: Boolean,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.sub(nullable, this, secondOperandValue, secondOperandType, resultType)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.sub(
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.sub(this, secondOperandValue, secondOperandType, resultType)
    infix fun <JAVA: Number?> SqExpression<*, Number>.sub(secondOperandValue: Number?): SqTwoOperandMathOperation<JAVA> =
        this.sub(secondOperandValue, null, null)


    fun <JAVA: Number?> mult(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperand: SqExpression<*, Number>,
        type: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA>
    fun mult(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.mult(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.mult(nullable: Boolean, other: SqExpression<*, Number>, type: SqType<JAVA & Any>? = null): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.mult(nullable, this, other, type)
    infix fun SqExpression<*, Number>.mult(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.mult(this, other)

    fun <JAVA: Number?, PARAM: Number?> mult(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA>
    fun <JAVA: Number?, PARAM: Number?> mult(
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
        methodDiff: Boolean = true,
    ): SqTwoOperandMathOperation<JAVA> =
        this.mult(nullable = true, firstOperand, secondOperandValue, secondOperandType, resultType)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.mult(
        nullable: Boolean,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.mult(nullable, this, secondOperandValue, secondOperandType, resultType)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.mult(
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.mult(this, secondOperandValue, secondOperandType, resultType)
    infix fun <JAVA: Number?> SqExpression<*, Number>.mult(secondOperandValue: Number?): SqTwoOperandMathOperation<JAVA> =
        this.mult(secondOperandValue, null, null)


    fun <JAVA: Number?> div(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperand: SqExpression<*, Number>,
        type: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA>
    fun div(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.div(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.div(nullable: Boolean, other: SqExpression<*, Number>, type: SqType<JAVA & Any>? = null): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.div(nullable, this, other, type)
    infix fun SqExpression<*, Number>.div(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.div(this, other)

    fun <JAVA: Number?, PARAM: Number?> div(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA>
    fun <JAVA: Number?, PARAM: Number?> div(
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
        methodDiff: Boolean = true,
    ): SqTwoOperandMathOperation<JAVA> =
        this.div(nullable = true, firstOperand, secondOperandValue, secondOperandType, resultType)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.div(
        nullable: Boolean,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.div(nullable, this, secondOperandValue, secondOperandType, resultType)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.div(
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.div(this, secondOperandValue, secondOperandType, resultType)
    infix fun <JAVA: Number?> SqExpression<*, Number>.div(secondOperandValue: Number?): SqTwoOperandMathOperation<JAVA> =
        this.div(secondOperandValue, null, null)


    fun <JAVA: Number?> mod(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperand: SqExpression<*, Number>,
        type: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA>
    fun mod(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.mod(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.mod(nullable: Boolean, other: SqExpression<*, Number>, type: SqType<JAVA & Any>? = null): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.mod(nullable, this, other, type)
    infix fun SqExpression<*, Number>.mod(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.mod(this, other)

    fun <JAVA: Number?, PARAM: Number?> mod(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA>
    fun <JAVA: Number?, PARAM: Number?> mod(
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
        methodDiff: Boolean = true,
    ): SqTwoOperandMathOperation<JAVA> =
        this.mod(nullable = true, firstOperand, secondOperandValue, secondOperandType, resultType)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.mod(
        nullable: Boolean,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.mod(nullable, this, secondOperandValue, secondOperandType, resultType)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.mod(
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.mod(this, secondOperandValue, secondOperandType, resultType)
    infix fun <JAVA: Number?> SqExpression<*, Number>.mod(secondOperandValue: Number?): SqTwoOperandMathOperation<JAVA> =
        this.mod(secondOperandValue, null, null)


    fun <JAVA: Number?> bitwiseAnd(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperand: SqExpression<*, Number>,
        type: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA>
    fun bitwiseAnd(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.bitwiseAnd(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.bitwiseAnd(nullable: Boolean, other: SqExpression<*, Number>, type: SqType<JAVA & Any>? = null): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseAnd(nullable, this, other, type)
    infix fun SqExpression<*, Number>.bitwiseAnd(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.bitwiseAnd(this, other)

    fun <JAVA: Number?, PARAM: Number?> bitwiseAnd(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA>
    fun <JAVA: Number?, PARAM: Number?> bitwiseAnd(
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
        methodDiff: Boolean = true,
    ): SqTwoOperandMathOperation<JAVA> =
        this.bitwiseAnd(nullable = true, firstOperand, secondOperandValue, secondOperandType, resultType)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.bitwiseAnd(
        nullable: Boolean,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseAnd(nullable, this, secondOperandValue, secondOperandType, resultType)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.bitwiseAnd(
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseAnd(this, secondOperandValue, secondOperandType, resultType)
    infix fun <JAVA: Number?> SqExpression<*, Number>.bitwiseAnd(secondOperandValue: Number?): SqTwoOperandMathOperation<JAVA> =
        this.bitwiseAnd(secondOperandValue, null, null)


    fun <JAVA: Number?> bitwiseOr(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperand: SqExpression<*, Number>,
        type: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA>
    fun bitwiseOr(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.bitwiseOr(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.bitwiseOr(nullable: Boolean, other: SqExpression<*, Number>, type: SqType<JAVA & Any>? = null): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseOr(nullable, this, other, type)
    infix fun SqExpression<*, Number>.bitwiseOr(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.bitwiseOr(this, other)

    fun <JAVA: Number?, PARAM: Number?> bitwiseOr(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA>
    fun <JAVA: Number?, PARAM: Number?> bitwiseOr(
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
        methodDiff: Boolean = true,
    ): SqTwoOperandMathOperation<JAVA> =
        this.bitwiseOr(nullable = true, firstOperand, secondOperandValue, secondOperandType, resultType)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.bitwiseOr(
        nullable: Boolean,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseOr(nullable, this, secondOperandValue, secondOperandType, resultType)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.bitwiseOr(
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseOr(this, secondOperandValue, secondOperandType, resultType)
    infix fun <JAVA: Number?> SqExpression<*, Number>.bitwiseOr(secondOperandValue: Number?): SqTwoOperandMathOperation<JAVA> =
        this.bitwiseOr(secondOperandValue, null, null)


    fun <JAVA: Number?> bitwiseXor(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperand: SqExpression<*, Number>,
        type: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA>
    fun bitwiseXor(firstOperand: SqExpression<*, Number>, secondOperand: SqExpression<*, Number>, methodDiff: Boolean = true): SqTwoOperandMathOperation<Number?> =
        this.bitwiseXor(nullable = true, firstOperand, secondOperand)
    fun <JAVA: Number?> SqExpression<*, Number>.bitwiseXor(nullable: Boolean, other: SqExpression<*, Number>, type: SqType<JAVA & Any>? = null): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseXor(nullable, this, other, type)
    infix fun SqExpression<*, Number>.bitwiseXor(other: SqExpression<*, Number>): SqTwoOperandMathOperation<Number?> =
        this@SqContext.bitwiseXor(this, other)

    fun <JAVA: Number?, PARAM: Number?> bitwiseXor(
        nullable: Boolean,
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA>
    fun <JAVA: Number?, PARAM: Number?> bitwiseXor(
        firstOperand: SqExpression<*, Number>,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
        methodDiff: Boolean = true,
    ): SqTwoOperandMathOperation<JAVA> =
        this.bitwiseXor(nullable = true, firstOperand, secondOperandValue, secondOperandType, resultType)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.bitwiseXor(
        nullable: Boolean,
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseXor(nullable, this, secondOperandValue, secondOperandType, resultType)
    fun <JAVA: Number?, PARAM: Number?> SqExpression<*, Number>.bitwiseXor(
        secondOperandValue: PARAM,
        secondOperandType: SqType<PARAM & Any>? = null,
        resultType: SqType<JAVA & Any>? = null,
    ): SqTwoOperandMathOperation<JAVA> =
        this@SqContext.bitwiseXor(this, secondOperandValue, secondOperandType, resultType)
    infix fun <JAVA: Number?> SqExpression<*, Number>.bitwiseXor(secondOperandValue: Number?): SqTwoOperandMathOperation<JAVA> =
        this.bitwiseXor(secondOperandValue, null, null)
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
