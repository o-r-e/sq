package me.ore.sq.util


object SqUtil {
    // region SQL - text utils
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
    // endregion


    // region SQL - comparisons, function names, mathematical operations
    const val COMPARISON__EQUAL = "="
    const val COMPARISON__NOT_EQUAL = "<>"
    const val COMPARISON__GREATER = ">"
    const val COMPARISON__GREATER_OR_EQUAL = ">="
    const val COMPARISON__LESS = "<"
    const val COMPARISON__LESS_OR_EQUAL = "<="
    const val COMPARISON__LIKE = "LIKE"
    const val COMPARISON__NOT_LIKE = "NOT LIKE"

    const val FUNCTION_NAME__ALL = "ALL"
    const val FUNCTION_NAME__ANY = "ANY"
    const val FUNCTION_NAME__AVG = "AVG"
    const val FUNCTION_NAME__COALESCE = "COALESCE"
    const val FUNCTION_NAME__COUNT = "COUNT"
    const val FUNCTION_NAME__EXISTS = "EXISTS"
    const val FUNCTION_NAME__MIN = "MIN"
    const val FUNCTION_NAME__MAX = "MAX"
    const val FUNCTION_NAME__SUM = "SUM"

    const val MATH_OPERATION__ADD = "+"
    const val MATH_OPERATION__SUBTRACT = "-"
    const val MATH_OPERATION__MULTIPLY = "*"
    const val MATH_OPERATION__DIVIDE = "/"
    const val MATH_OPERATION__MODULO = "%"
    const val MATH_OPERATION__BITWISE_AND = "&"
    const val MATH_OPERATION__BITWISE_OR = "|"
    const val MATH_OPERATION__BITWISE_XOR = "^"
    // endregion


    // region Misc
    fun readableClassName(clazz: Class<*>): String = buildString {
        var braceCount = 0
        var current = clazz

        while (true) {
            if (current.isArray) {
                this.append("Array<")
                braceCount++
                current = current.componentType!!
            } else {
                this.append(current.name)
                break
            }
        }

        for (i in 1 .. braceCount) this.append('>')
    }

    fun throwUnexpectedClassException(columnIndex: Int, value: Any, expectedClass: Class<*>, vararg moreExpectedClasses: Class<*>): Nothing {
        val message = buildString {
            this
                .append("Column with index $columnIndex has unexpected value; value class is ")
                .append(value.javaClass.name)
                .append(", but required class ")

            if (moreExpectedClasses.isEmpty()) {
                this.append("is ")
            } else {
                this.append("are ")
            }

            this.append(expectedClass.name)

            moreExpectedClasses.forEach { additionalExpectedClass ->
                this
                    .append(", ")
                    .append(additionalExpectedClass.name)
            }
        }

        val exception = IllegalStateException(message)
        exception.stackTrace = exception.stackTrace.let { stackTrace ->
            val size = stackTrace.size
            if (size < 2) {
                emptyArray()
            } else {
                stackTrace.takeLast(size - 1).toTypedArray()
            }
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

    fun prepareCollectionValueForComment(value: Collection<Any?>): String {
        return if (value.size > 10) {
            buildString {
                this
                    .append('[')
                    .append(value.take(10).joinToString())
                    .append(", ... (")
                    .append(value.size - 10)
                    .append(" item(s) more)")
            }
        } else {
            value.toString()
        }
    }

    fun escapeCommentContent(content: String): String {
        if (content.length < 2) return content

        return buildString(content.length) {
            for (i in 0 until content.lastIndex) {
                when (val char = content[i]) {
                    '/' -> {
                        if (content[i + 1] == '*') {
                            this.append("/ ")
                        } else {
                            this.append('/')
                        }
                    }
                    '*' -> {
                        if (content[i + 1] == '/') {
                            this.append("* ")
                        } else {
                            this.append('*')
                        }
                    }
                    else -> this.append(char)
                }
            }
            this.append(content.last())
        }
    }
    // endregion
}
