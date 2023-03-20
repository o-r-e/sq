package me.ore.sq

import java.util.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


object SqUtil {
    // region Misc
    fun collectParametersFromLists(parameterLists: Iterable<Iterable<SqParameter<*, *>>?>): List<SqParameter<*, *>>? {
        var result: MutableList<SqParameter<*, *>>? = null

        parameterLists.forEach { parameterList ->
            parameterList?.let { parameters ->
                val tmpResult = result ?: run {
                    val tmpResult = ArrayList<SqParameter<*, *>>()
                    result = tmpResult
                    tmpResult
                }

                tmpResult.addAll(parameters)
            }
        }

        return result?.toList()
    }

    fun collectParametersFromLists(vararg parameterLists: Iterable<SqParameter<*, *>>?): List<SqParameter<*, *>>? = this.collectParametersFromLists(parameterLists.toList())

    fun collectParameters(items: Iterable<SqItem?>): List<SqParameter<*, *>>? {
        var result: MutableList<SqParameter<*, *>>? = null

        items.forEach { item ->
            item?.parameters()?.let { parameters ->
                val tmpResult = result ?: run {
                    val tmpResult = ArrayList<SqParameter<*, *>>()
                    result = tmpResult
                    tmpResult
                }

                tmpResult.addAll(parameters)
            }
        }

        return result?.toList()
    }

    fun collectParameters(vararg items: SqItem?): List<SqParameter<*, *>>? = this.collectParameters(items.toList())

    @Suppress("NOTHING_TO_INLINE")
    inline fun <R : Any?> uncheckedCast(value: Any?): R {
        @Suppress("UNCHECKED_CAST")
        return value as R
    }

    fun handleError(error: Throwable) {
        val thread = Thread.currentThread()
        var processed = false

        // region Use handler of current thread
        try {
            thread.uncaughtExceptionHandler?.let { handler ->
                handler.uncaughtException(thread, error)
                processed = true
            }
        } catch (e: Exception) {
            error.addSuppressed(e)
        }
        // endregion

        // region If not processed yet - use default handler
        if (!processed) {
            try {
                Thread.getDefaultUncaughtExceptionHandler()?.let { handler ->
                    handler.uncaughtException(thread, error)
                    processed = true
                }
            } catch (e: Exception) {
                error.addSuppressed(e)
            }
        }
        // endregion

        // region If not processed yet - print to console
        if (!processed) {
            error.printStackTrace(System.err)
        }
        // endregion
    }
    // endregion


    // region SQL text
    @Suppress("MemberVisibilityCanBePrivate")
    val SAVE_IDENTIFIER_REGEX: Regex = Regex("^[a-z0-9_]*$")

    fun isIdentifierSafe(identifier: String): Boolean = SAVE_IDENTIFIER_REGEX.matches(identifier)

    fun makeIdentifierSafe(identifier: String): String = "\"${identifier.replace("\"", "\\\"")}\""

    fun makeIdentifierSafeIfNeeded(identifier: String): String {
        return if (this.isIdentifierSafe(identifier)) {
            identifier
        } else {
            this.makeIdentifierSafe(identifier)
        }
    }


    inline fun appendSelect(select: SqSelect, target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean, atEnd: () -> Unit) {
        contract { callsInPlace(atEnd, InvocationKind.EXACTLY_ONCE) }

        val distinct = select.distinct
        val columns = select.columns
        val from = select.from?.takeIf { it.isNotEmpty() }
        val where = select.where
        val groupBy = select.groupBy?.takeIf { it.isNotEmpty() }
        val having = select.having
        val orderBy = select.orderBy?.takeIf { it.isNotEmpty() }

        val firstKeywordSpaceAllowed = if (asTextPart) {
            target.add("(", spaced = spaceAllowed)
            false
        } else {
            spaceAllowed
        }

        target.add("SELECT", spaced = firstKeywordSpaceAllowed)
        if (distinct) target.add("DISTINCT", spaced = true)

        columns.forEachIndexed { index, column ->
            if (index > 0) {
                target.comma()
            }
            column.appendPossibleDefinitionTo(target, asTextPart = true, spaceAllowed = true)
        }

        if (from != null) {
            target.ls().add("FROM", spaced = false)
            from.forEachIndexed { index, fromItem ->
                if (index > 0) {
                    target.comma()
                }
                fromItem.appendPossibleDefinitionTo(target, asTextPart = true, spaceAllowed = true)
            }
        }

        if (where != null) {
            target.ls().add("WHERE", spaced = false)
            where.appendTo(target, asTextPart = true, spaceAllowed = true)
        }

        if (groupBy != null) {
            target.ls().add("GROUP BY", spaced = false)
            groupBy.forEachIndexed { index, column ->
                if (index > 0) {
                    target.comma()
                }
                column.appendTo(target, asTextPart = true, spaceAllowed = true)
            }
        }

        if (having != null) {
            target.ls().add("HAVING", spaced = false)
            having.appendTo(target, asTextPart = true, spaceAllowed = true)
        }

        if (orderBy != null) {
            target.ls().add("ORDER BY", spaced = true)
            orderBy.forEachIndexed { index, orderByItem ->
                if (index > 0) {
                    target.comma()
                }
                orderByItem.appendTo(target, asTextPart = true, spaceAllowed = true)
            }
        }

        atEnd.invoke()

        if (asTextPart) target.add(")", spaced = false)
    }

    fun appendSelect(select: SqSelect, target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
        this.appendSelect(select, target, asTextPart, spaceAllowed) {}
    }
    // endregion


    // region Function names
    const val FUNCTION_NAME__ALL = "ALL"
    const val FUNCTION_NAME__ANY = "ANY"
    const val FUNCTION_NAME__AVG = "AVG"
    const val FUNCTION_NAME__COALESCE = "COALESCE"
    const val FUNCTION_NAME__COUNT = "COUNT"
    const val FUNCTION_NAME__EXISTS = "EXISTS"
    const val FUNCTION_NAME__MIN = "MIN"
    const val FUNCTION_NAME__MAX = "MAX"
    const val FUNCTION_NAME__SUM = "SUM"
    // endregion


    // region Mathematical operations
    const val MATH_OPERATION__ADD = "+"
    const val MATH_OPERATION__SUBTRACT = "-"
    const val MATH_OPERATION__MULTIPLY = "*"
    const val MATH_OPERATION__DIVIDE = "/"
    const val MATH_OPERATION__MODULO = "%"
    const val MATH_OPERATION__BITWISE_AND = "&"
    const val MATH_OPERATION__BITWISE_OR = "|"
    const val MATH_OPERATION__BITWISE_XOR = "^"
    // endregion


    // region Utils for types
    fun throwUnexpectedColumnValueClassException(columnIndex: Int, value: Any, requiredClass: Class<*>, vararg moreRequiredClasses: Class<*>): Nothing {
        val message = buildString {
            this
                .append("Column with index $columnIndex has unexpected value; value class is ")
                .append(value.javaClass.name)
                .append(", but required class ")

            if (moreRequiredClasses.isEmpty()) {
                this.append("is ")
            } else {
                this.append("are ")
            }

            this.append(requiredClass.name)

            moreRequiredClasses.forEach { additionalRequiredClass ->
                this
                    .append(", ")
                    .append(additionalRequiredClass.name)
            }
        }

        val exception = IllegalStateException(message)
        exception.stackTrace = exception.stackTrace.let { stackTrace ->
            val size = stackTrace.size
            stackTrace.takeLast(size - 1).toTypedArray()
        }
        throw exception
    }

    fun prepareStringValueForComment(value: String): String {
        val maxLength = 25
        return if (value.length > maxLength) {
            val printedLength = maxLength - 3
            val more = value.length - printedLength
            "\"${value.take(printedLength)}... ($more character(s) hidden)\""
        } else {
            "\"$value\""
        }
    }
    // endregion


    // region Result set reading
    @Suppress("MemberVisibilityCanBePrivate")
    val READING_CANCELLED = object {
        override fun equals(other: Any?): Boolean = (this === other)
    }

    @Suppress("UNCHECKED_CAST")
    fun <JAVA: Any?> cancelReading(): JAVA = READING_CANCELLED as JAVA
    // endregion
}
