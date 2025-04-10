package io.github.ore.sq.test

import io.github.ore.sq.*
import io.github.ore.sq.impl.SqDataTypesImpl
import io.github.ore.sq.impl.jdbcRequestDataBuilderFactory
import io.github.ore.sq.util.SqItemPartConfig
import org.postgresql.util.PGobject
import java.sql.*
import java.sql.Array
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


private fun connect(): Connection =
    DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5433/usr", "usr", "usr")!!

private inline fun <T> logTime(message: String, action: () -> T): T {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }

    var time = System.currentTimeMillis()
    val result = action()
    time = System.currentTimeMillis() - time
    println("[${time.toString().padStart(4, ' ')}] $message")
    return result
}

private fun getSqlTypeName(type: Int): String {
    return when (type) {
        Types.BIT -> "BIT"
        Types.TINYINT -> "TINYINT"
        Types.SMALLINT -> "SMALLINT"
        Types.INTEGER -> "INTEGER"
        Types.BIGINT -> "BIGINT"
        Types.FLOAT -> "FLOAT"
        Types.REAL -> "REAL"
        Types.DOUBLE -> "DOUBLE"
        Types.NUMERIC -> "NUMERIC"
        Types.DECIMAL -> "DECIMAL"
        Types.CHAR -> "CHAR"
        Types.VARCHAR -> "VARCHAR"
        Types.LONGVARCHAR -> "LONGVARCHAR"
        Types.DATE -> "DATE"
        Types.TIME -> "TIME"
        Types.TIMESTAMP -> "TIMESTAMP"
        Types.BINARY -> "BINARY"
        Types.VARBINARY -> "VARBINARY"
        Types.LONGVARBINARY -> "LONGVARBINARY"
        Types.NULL -> "NULL"
        Types.OTHER -> "OTHER"
        Types.JAVA_OBJECT -> "JAVA_OBJECT"
        Types.DISTINCT -> "DISTINCT"
        Types.STRUCT -> "STRUCT"
        Types.ARRAY -> "ARRAY"
        Types.BLOB -> "BLOB"
        Types.CLOB -> "CLOB"
        Types.REF -> "REF"
        Types.DATALINK -> "DATALINK"
        Types.BOOLEAN -> "BOOLEAN"
        Types.ROWID -> "ROWID"
        Types.NCHAR -> "NCHAR"
        Types.NVARCHAR -> "NVARCHAR"
        Types.LONGNVARCHAR -> "LONGNVARCHAR"
        Types.NCLOB -> "NCLOB"
        Types.SQLXML -> "SQLXML"
        Types.REF_CURSOR -> "REF_CURSOR"
        Types.TIME_WITH_TIMEZONE -> "TIME_WITH_TIMEZONE"
        Types.TIMESTAMP_WITH_TIMEZONE -> "TIMESTAMP_WITH_TIMEZONE"
        else -> "<Unknown - #${type}>"
    }
}

private fun scan(resultSet: ResultSet) {
    val metaData = resultSet.metaData

    val columnCount = metaData.columnCount
    println("Columns: $columnCount")

    for (columnIndex in 1 .. columnCount) {
        println("  Column #$columnIndex")
        println("    label ...... : ${metaData.getColumnLabel(columnIndex)}")
        println("    type name .. : ${metaData.getColumnTypeName(columnIndex)}")
        println("    type ....... : ${getSqlTypeName(metaData.getColumnType(columnIndex))}")
        println("    class name . : ${metaData.getColumnClassName(columnIndex)}")
    }


    var rowIndex = 0
    while (resultSet.next()) {
        rowIndex++
        println("Row #$rowIndex")

        for (columnIndex in 1 .. columnCount) {
            when (val value = resultSet.getObject(columnIndex)) {
                null -> println("  #$columnIndex : NULL")
                is CharSequence -> println("  #$columnIndex : [${value.javaClass.name}] \"$value\"")
                is Array -> {
                    try {
                        println("  #$columnIndex : Array")
                        println("    base type name . : ${value.baseTypeName}")
                        println("    base type ...... : ${getSqlTypeName(value.baseType)}")
                        value.resultSet.use { arrayResultSet ->
                            var arrayValueIndex = 0
                            while (arrayResultSet.next()) {
                                arrayValueIndex++
                                when (val arrayValue = arrayResultSet.getObject(2)) {
                                    null -> println("    #$arrayValueIndex : NULL")
                                    is CharSequence -> println("    #$arrayValueIndex : [${value.javaClass.name}] \"$arrayValue\"")
                                    else -> println("    #$arrayValueIndex : [${value.javaClass.name}] $arrayValue")
                                }
                            }
                        }
                    } finally {
                        value.free()
                    }
                }
                is ByteArray -> println("  #$columnIndex : [Array<byte>] ${value.toList()}")
                is ShortArray -> println("  #$columnIndex : [Array<short>] ${value.toList()}")
                is IntArray -> println("  #$columnIndex : [Array<int>] ${value.toList()}")
                is LongArray -> println("  #$columnIndex : [Array<long>] ${value.toList()}")
                is FloatArray -> println("  #$columnIndex : [Array<float>] ${value.toList()}")
                is DoubleArray -> println("  #$columnIndex : [Array<double>] ${value.toList()}")
                is BooleanArray -> println("  #$columnIndex : [Array<boolean>] ${value.toList()}")
                is CharArray -> println("  #$columnIndex : [Array<char>] ${value.toList()}")
                is kotlin.Array<*> -> println("  #$columnIndex : [Array<*>] ${value.toList()}")
                is PGobject -> println("  #$columnIndex : [PGobject::${value.type}] ${value.value?.let { "\"$it\"" }}")
                else -> println("  #$columnIndex : [${value.javaClass.name}] $value")
            }
        }
    }
}


object TstTable: SqPgTable("tst") {
    val ID = this.columnHolder.pgBigInt("id", 0)
    val F = this.columnHolder.pgCharacterVarying("f", null)
}

open class TstRecord(): SqRecord() {
    companion object: SqRecordClass<TstRecord>()

    open var id by TstTable.ID.primaryKeyField()
    open var f: String? by TstTable.F.commonField()

    constructor(f: String?): this() {
        this.f = f
    }

    public override fun dropPrimaryKeys() {
        super.dropPrimaryKeys()
    }

    public override fun hasAnyPrimaryKeySet(): Boolean {
        return super.hasAnyPrimaryKeySet()
    }
}


private fun runTmp() {}

fun SqContext.myFunc(): SqExpression<String, String> {
    return object : SqExpression<String, String> {
        override val reader: SqDataTypeReader<String, String>
            get() = SqDataTypesImpl.INSTANCE.varChar.notNullReader
        override val isMultiline: Boolean
            get() = false
        override var commentAtStart: String? = null
        override var commentAtEnd: String? = null

        override fun addToBuilderWithoutComments(
            context: SqContext,
            target: SqJdbcRequestDataBuilder,
            partConfig: SqItemPartConfig?
        ) {
            target.keyword("my_func").brackets {
                target.text("':)'")
            }
        }
    }
}

private fun runMain() {
    SqPg.defaultSettings {
        this.jdbcRequestDataBuilderFactory(
            pretty = true,
            allowComments = true,
        )
    }

    sqPg {
        connect().use { connection ->
            if (true) {
                val record = TstRecord("my text")
                println("Record created: $record")
            }

            // Insert
            if (false) {
                val record = TstRecord("my text")
                println("Record created: $record")

                insertInto(TstTable)
                    .useAndReloadRecords(record)
                    .execute(connection)
                println("Record: $record")
            }

            // Select
            if (false) {
                val limit = 2
                val page = 1

                // 652 - 656
                val offset = limit * page
                select(TstTable.ID, TstTable.F, TstTable.ID)
                    .from(TstTable)
                    .orderBy(TstTable.ID.asc())
                    .limit(parameters.parameter(limit))
                    .offset(parameters.parameter(offset))
                    .also {
                        println(it.createJdbcRequestData().sql)
                        println()
                    }
                    .execute(connection, TstRecord.mapper()) { it.readAllAsObjects() }
                    .forEach { println(it) }
            }
        }
    }
}


fun main() {
    //runTmp()
    runMain()
}
