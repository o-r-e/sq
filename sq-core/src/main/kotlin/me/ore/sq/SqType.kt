package me.ore.sq

import me.ore.sq.SqType.Companion.toString
import me.ore.sq.util.SqUtil
import java.sql.PreparedStatement
import java.sql.ResultSet


/** Type object used to specify a database data type, to read and write values of that type */
class SqType<JAVA: Any?, DB: Any> private constructor(
    /** If `true`, then the current type object allows working with "NULLABLE" values */
    val nullable: Boolean,

    /** "Data type in kotlin"; parameter values and data read from the database will have this type */
    val valueClass: Class<JAVA & Any>,

    /** "Data type in DB"; used when creating comparison operations - so that DB then compares values of suitable types */
    val dbType: Class<DB>,

    /** Reader of data from columns in [ResultSet] */
    val reader: SqValueReader<JAVA & Any>,

    /** Writer which fills in the parameters in the [PreparedStatement] */
    val writer: SqValueWriter<JAVA & Any>,

    /** Text for "data type in kotlin", only used in [toString] */
    val valueClassText: String = SqUtil.readableClassName(valueClass),

    /** Text for "data type in DB", only used in [toString] */
    val dbTypeText: String = SqUtil.readableClassName(dbType),
) {
    companion object {
        /**
         * Creating a pair of objects of type - "NULLABLE" and "NOT NULL"
         *
         * @param valueClass "data type in kotlin";
         * parameter values and data read from the database will have this type
         * @param dbType "data type in DB";
         * used when creating comparison operations - so that DB then compares values of suitable types
         * @param reader reader of data from columns in [ResultSet]
         * @param writer writer which fills in the parameters in the [PreparedStatement]
         * @param valueClassText text for "data type in kotlin"
         * @param dbTypeText text for "data type in DB"
         *
         * @return "NULLABLE" and "NOT NULL" type object pair
         */
        private fun <JAVA: Any, DB: Any> createTypePair(
            valueClass: Class<JAVA>,
            dbType: Class<DB>,
            reader: SqValueReader<JAVA>,
            writer: SqValueWriter<JAVA>,
            valueClassText: String = SqUtil.readableClassName(valueClass),
            dbTypeText: String = SqUtil.readableClassName(dbType),
        ): Pair<SqType<JAVA?, DB>, SqType<JAVA, DB>> {
            val nullable = SqType<JAVA?, DB>(nullable = true, valueClass, dbType, reader, writer, valueClassText, dbTypeText)
            val notNull = SqType(nullable = false, valueClass, dbType, reader, writer, valueClassText, dbTypeText)

            @Suppress("UNCHECKED_CAST")
            nullable.oppositeType = notNull as SqType<JAVA?, DB>
            notNull.oppositeType = nullable

            return nullable to notNull
        }

        /**
         * Creating a "NULLABLE" type object
         *
         * @param valueClass "data type in kotlin";
         * parameter values and data read from the database will have this type
         * @param dbType "data type in DB";
         * used when creating comparison operations - so that DB then compares values of suitable types
         * @param reader reader of data from columns in [ResultSet]
         * @param writer writer which fills in the parameters in the [PreparedStatement]
         * @param valueClassText text for "data type in kotlin"
         * @param dbTypeText text for "data type in DB"
         *
         * @return "NULLABLE" type object
         *
         * @see notNull
         */
        fun <JAVA: Any, DB: Any> nullable(
            valueClass: Class<JAVA>,
            dbType: Class<DB>,
            reader: SqValueReader<JAVA>,
            writer: SqValueWriter<JAVA>,
            valueClassText: String = SqUtil.readableClassName(valueClass),
            dbTypeText: String = SqUtil.readableClassName(dbType),
        ): SqType<JAVA?, DB> {
            return this.createTypePair(valueClass, dbType, reader, writer, valueClassText, dbTypeText).first
        }

        /**
         * Creating a "NOT NULL" type object
         *
         * @param valueClass "data type in kotlin";
         * parameter values and data read from the database will have this type
         * @param dbType "data type in DB";
         * used when creating comparison operations - so that DB then compares values of suitable types
         * @param reader reader of data from columns in [ResultSet]
         * @param writer writer which fills in the parameters in the [PreparedStatement]
         * @param valueClassText text for "data type in kotlin"
         * @param dbTypeText text for "data type in DB"
         *
         * @return "NOT NULL" type object
         *
         * @see nullable
         */
        fun <JAVA: Any, DB: Any> notNull(
            valueClass: Class<JAVA>,
            dbType: Class<DB>,
            reader: SqValueReader<JAVA>,
            writer: SqValueWriter<JAVA>,
            valueClassText: String = SqUtil.readableClassName(valueClass),
            dbTypeText: String = SqUtil.readableClassName(dbType),
        ): SqType<JAVA, DB> {
            return this.createTypePair(valueClass, dbType, reader, writer, valueClassText, dbTypeText).second
        }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SqType<*, *>

        if (nullable != other.nullable) return false
        if (valueClass != other.valueClass) return false
        if (dbType != other.dbType) return false
        if (reader != other.reader) return false
        return writer == other.writer
    }

    override fun hashCode(): Int {
        var result = nullable.hashCode()
        result = 31 * result + valueClass.hashCode()
        result = 31 * result + dbType.hashCode()
        result = 31 * result + reader.hashCode()
        result = 31 * result + writer.hashCode()
        return result
    }

    override fun toString(): String = buildString {
        val self = this@SqType

        this.append(self.javaClass.simpleName)
            .append('<')
            .append(self.valueClassText)
            .append(", ")
            .append(self.dbTypeText)
            .append(">(")

        if (self.nullable) this.append("nullable")
        else this.append("not null")

        this
            .append(", valueClass=").append(SqUtil.readableClassName(self.valueClass))
            .append(", dbType=").append(SqUtil.readableClassName(self.dbType))
            .append(", reader=").append(self.reader)
            .append(", writer=").append(self.writer)
            .append(')')
    }


    /**
     * "Opposite type"
     *
     * In "NOT NULL" type object this property contains "NULLABLE" type object and vice versa
     */
    private lateinit var oppositeType: SqType<JAVA?, DB>

    /**
     * Returns the "NULLABLE" variant of the current type object
     *
     * @return "NULLABLE" type object; will return the current object if [nullable] is `true`
     *
     * @see notNull
     */
    fun nullable(): SqType<JAVA?, DB> {
        return if (this.nullable) {
            @Suppress("UNCHECKED_CAST")
            this as SqType<JAVA?, DB>
        } else {
            this.oppositeType
        }
    }

    /**
     * Returns the "NOT NULL" variant of the current type object
     *
     * @return "NOT NULL" type object; will return the current object if [nullable] is `false`
     *
     * @see nullable
     */
    fun notNull(): SqType<JAVA & Any, DB> {
        val result = if (this.nullable) {
            this.oppositeType
        } else {
            this
        }

        @Suppress("UNCHECKED_CAST")
        return (result as SqType<JAVA & Any, DB>)
    }


    /**
     * Reading data
     *
     * @param source query result, source of data
     * @param columnIndex the index of the column whose data is to be read; index of first column is 1
     *
     * @return data read and converted to [JAVA] type
     */
    fun read(source: ResultSet, columnIndex: Int): JAVA {
        val result = if (this.nullable) {
            this.reader.readNullable(source, columnIndex)
        } else {
            this.reader.readNotNull(source, columnIndex)
        }
        @Suppress("UNCHECKED_CAST")
        return (result as JAVA)
    }

    /**
     * Converting [value] and storing it as a parameter in [target]
     *
     * @param target statement in which the parameter will be stored
     * @param parameterIndex index of the saved parameter; the index of the first parameter is 1
     * @param value the value to be stored as a parameter
     */
    fun write(target: PreparedStatement, parameterIndex: Int, value: JAVA) { this.writer.write(target, parameterIndex, value) }

    /**
     * Convert value to comment content to be added to SQL text
     * (usually called if [SqContextConfig.printParameterValues] is `true`)
     *
     * @param value value to be converted
     *
     * @return [value] as comment content
     */
    fun valueToComment(value: JAVA?): String {
        return SqUtil.escapeCommentContent(this.writer.valueToComment(value))
    }
}
