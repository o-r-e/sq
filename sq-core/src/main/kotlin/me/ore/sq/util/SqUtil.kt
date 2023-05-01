package me.ore.sq.util


/** Miscellaneous library utilities */
object SqUtil {
    // region SQL - text utils
    /**
     * Regular expression for checks "is an identifier safe for SQL"
     *
     * Value - "`^[a-z0-9_]*$`"
     *
     * @see isIdentifierSafe
     * @see makeIdentifierSafe
     * @see makeIdentifierSafeIfNeeded
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val SAVE_IDENTIFIER_REGEX: Regex = Regex("^[a-z0-9_]*$")

    /**
     * Checks with [SAVE_IDENTIFIER_REGEX] whether an identifier is SQL safe
     *
     * @param identifier identifier to check
     *
     * @return `true` if [identifier] can be used "as is", without any changes and/or escaping
     *
     * @see SAVE_IDENTIFIER_REGEX
     * @see makeIdentifierSafe
     * @see makeIdentifierSafeIfNeeded
     */
    fun isIdentifierSafe(identifier: String): Boolean = SAVE_IDENTIFIER_REGEX.matches(identifier)

    /**
     * Makes the identifier safe for SQL:
     * * double quotes are added at the beginning and end of the identifier;
     * * if there are double quotes in the identifier itself, they are escaped with the `"\"` character.
     *
     * @param identifier identifier to be made safe for SQL
     *
     * @return SQL safe identifier
     *
     * @see SAVE_IDENTIFIER_REGEX
     * @see isIdentifierSafe
     * @see makeIdentifierSafeIfNeeded
     */
    fun makeIdentifierSafe(identifier: String): String = "\"${identifier.replace("\"", "\\\"")}\""

    /**
     * Makes the identifier safe for SQL if needed. If the identifier is not SQL safe:
     * * double quotes are added at the beginning and end of the identifier;
     * * if there are double quotes in the identifier itself, they are escaped with the `"\"` character.
     *
     * @param identifier an identifier to check and make safe for SQL if it is not
     *
     * @return SQL safe identifier
     *
     * @see SAVE_IDENTIFIER_REGEX
     * @see isIdentifierSafe
     * @see makeIdentifierSafe
     */
    fun makeIdentifierSafeIfNeeded(identifier: String): String {
        return if (this.isIdentifierSafe(identifier)) {
            identifier
        } else {
            this.makeIdentifierSafe(identifier)
        }
    }
    // endregion


    // region SQL - comparisons, function names, mathematical operations
    /** SQL comparison operation `"="` */
    const val COMPARISON__EQUAL = "="

    /** SQL comparison operation `"<>"` */
    const val COMPARISON__NOT_EQUAL = "<>"

    /** SQL comparison operation `">"` */
    const val COMPARISON__GREATER = ">"

    /** SQL comparison operation `">="` */
    const val COMPARISON__GREATER_OR_EQUAL = ">="

    /** SQL comparison operation `"<"` */
    const val COMPARISON__LESS = "<"

    /** SQL comparison operation `"<="` */
    const val COMPARISON__LESS_OR_EQUAL = "<="

    /** SQL comparison operation `"LIKE"` */
    const val COMPARISON__LIKE = "LIKE"

    /** SQL comparison operation `"NOT LIKE"` */
    const val COMPARISON__NOT_LIKE = "NOT LIKE"


    /** SQL function name `"ALL"` */
    const val FUNCTION_NAME__ALL = "ALL"

    /** SQL function name `"ANY"` */
    const val FUNCTION_NAME__ANY = "ANY"

    /** SQL function name `"AVG"` */
    const val FUNCTION_NAME__AVG = "AVG"

    /** SQL function name `"COALESCE"` */
    const val FUNCTION_NAME__COALESCE = "COALESCE"

    /** SQL function name `"COUNT"` */
    const val FUNCTION_NAME__COUNT = "COUNT"

    /** SQL function name `"EXISTS"` */
    const val FUNCTION_NAME__EXISTS = "EXISTS"

    /** SQL function name `"MIN"` */
    const val FUNCTION_NAME__MIN = "MIN"

    /** SQL function name `"MAX"` */
    const val FUNCTION_NAME__MAX = "MAX"

    /** SQL function name `"SUM"` */
    const val FUNCTION_NAME__SUM = "SUM"


    /** SQL mathematical operation `"+"` */
    const val MATH_OPERATION__ADD = "+"

    /** SQL mathematical operation `"-"` */
    const val MATH_OPERATION__SUBTRACT = "-"

    /** SQL mathematical operation `"*"` */
    const val MATH_OPERATION__MULTIPLY = "*"

    /** SQL mathematical operation `"/"` */
    const val MATH_OPERATION__DIVIDE = "/"

    /** SQL mathematical operation `"%"` */
    const val MATH_OPERATION__MODULO = "%"

    /** SQL mathematical operation `"&"` */
    const val MATH_OPERATION__BITWISE_AND = "&"

    /** SQL mathematical operation `"|"` */
    const val MATH_OPERATION__BITWISE_OR = "|"

    /** SQL mathematical operation `"^"` */
    const val MATH_OPERATION__BITWISE_XOR = "^"
    // endregion


    // region Misc
    /**
     * Creates a "readable" class name
     *
     * @param clazz the class to get the "readable" name for
     *
     * @return "readable" class name;
     * if [clazz] is array type, returns `"Array<...>"` with
     * result of [clazz].[getComponentType()][Class.getComponentType] instead of `"..."`
     * (may be converted recursively)
     */
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

    /**
     * Creates and throws an [IllegalStateException] with text like
     * "The column with index ... contains an unexpected value type, the required classes are ..."
     *
     * @param columnIndex
     * @param value
     * @param expectedClass
     * @param moreExpectedClasses
     */
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

    /**
     * Prepares a string value for printing in an SQL comment (truncates to 25 characters if longer)
     *
     * @param value value to be prepared
     *
     * @return prepared [value]
     */
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

    /**
     * Prepares a collection value for printing in an SQL comment (truncates to 10 items if longer)
     *
     * @param value value to be prepared
     *
     * @return prepared [value]
     */
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

    /**
     * Escapes content of SQL comment
     *
     * @param content content to be escaped
     *
     * @return escaped content
     */
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
