package me.ore.sq

import me.ore.sq.util.SqUtil
import java.sql.PreparedStatement
import java.sql.ResultSet


class SqType<JAVA: Any?, DB: Any> private constructor(
    val nullable: Boolean,
    val valueClass: Class<JAVA & Any>,
    val dbType: Class<DB>,
    val reader: SqValueReader<JAVA & Any>,
    val writer: SqValueWriter<JAVA & Any>,
    val valueClassText: String = SqUtil.readableClassName(valueClass),
    val dbTypeText: String = SqUtil.readableClassName(dbType),
) {
    companion object {
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


    private lateinit var oppositeType: SqType<JAVA?, DB>

    fun nullable(): SqType<JAVA?, DB> {
        return if (this.nullable) {
            @Suppress("UNCHECKED_CAST")
            this as SqType<JAVA?, DB>
        } else {
            this.oppositeType
        }
    }

    fun notNull(): SqType<JAVA & Any, DB> {
        val result = if (this.nullable) {
            this.oppositeType
        } else {
            this
        }

        @Suppress("UNCHECKED_CAST")
        return (result as SqType<JAVA & Any, DB>)
    }


    fun read(source: ResultSet, columnIndex: Int): JAVA {
        val result = if (this.nullable) {
            this.reader.readNullable(source, columnIndex)
        } else {
            this.reader.readNotNull(source, columnIndex)
        }
        @Suppress("UNCHECKED_CAST")
        return (result as JAVA)
    }

    fun write(target: PreparedStatement, parameterIndex: Int, value: JAVA) { this.writer.write(target, parameterIndex, value) }

    fun valueToComment(value: JAVA?): String = this.writer.valueToComment(value).replace("*/", "* /")

}
