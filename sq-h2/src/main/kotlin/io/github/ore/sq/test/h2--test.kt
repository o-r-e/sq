package io.github.ore.sq.test

import io.github.ore.sq.SqH2Context
import io.github.ore.sq.SqH2Table
import io.github.ore.sq.SqMappedReader
import io.github.ore.sq.SqRecord
import io.github.ore.sq.SqRecordClass
import io.github.ore.sq.asc
import io.github.ore.sq.bigInt
import io.github.ore.sq.deleteFrom
import io.github.ore.sq.execute
import io.github.ore.sq.from
import io.github.ore.sq.h2Binary
import io.github.ore.sq.h2BinaryLargeObject
import io.github.ore.sq.h2BinaryLargeObjectStream
import io.github.ore.sq.h2BinaryVarying
import io.github.ore.sq.h2Character
import io.github.ore.sq.h2CharacterLargeObject
import io.github.ore.sq.h2CharacterLargeObjectReader
import io.github.ore.sq.h2CharacterVarying
import io.github.ore.sq.h2VarCharIgnoreCase
import io.github.ore.sq.insertInto
import io.github.ore.sq.orderBy
import io.github.ore.sq.select
import io.github.ore.sq.sqH2
import io.github.ore.sq.update
import io.github.ore.sq.useAndReloadRecord
import io.github.ore.sq.useAndReloadRecords
import io.github.ore.sq.useRecords
import io.github.ore.sq.util.SqClobReader
import io.github.ore.sq.varChar
import java.io.ByteArrayInputStream
import java.io.StringReader
import java.io.StringWriter
import java.sql.Array
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Types
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.use


private fun connect(): Connection =
    DriverManager.getConnection("jdbc:h2:/mnt/data/dev-stuff/h2/playground;AUTO_RECONNECT=TRUE;AUTO_SERVER=TRUE;AUTO_SERVER_PORT=20002", "user", "")!!

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
                else -> println("  #$columnIndex : [${value.javaClass.name}] $value")
            }
        }
    }
}


object TstTable: SqH2Table("tst") {
    val ID = this.columnHolder.bigInt("id", 0)
    val F = this.columnHolder.h2BinaryLargeObjectStream("f", null)
}

open class TstRecord: SqRecord() {
    companion object: SqRecordClass<TstRecord>()

    val id by TstTable.ID.primaryKeyField()
    var f by TstTable.F.commonField()
}


fun main() {
    org.h2.Driver.load()
    connect().use { connection ->
        if (true) {
            connection.prepareStatement("select * from tst").use { statement ->
                statement.executeQuery().use { resultSet ->
                    scan(resultSet)
                }
            }
            return
        }

        sqH2 {
            fun print(index: Int, record: TstRecord) {
                println("  #$index - $record")
                //println("    ${record.f?.let { it.getBytes(1, it.length().toInt()).toList() }}")
            }

            fun print(records: List<TstRecord>) {
                println("Records:")
                records.forEachIndexed { index, record ->
                    print(index, record)
                }
            }

            fun selectAndPrint() {
                val records = select(TstTable.columns)
                    .from(TstTable)
                    .orderBy(TstTable.ID.asc())
                    .execute(connection, this, TstRecord.mapper()) { it.readAllAsObjects() }
                print(records)
            }


            val v1 = ByteArrayInputStream(byteArrayOf(10, 11, 12))
            val v2 = ByteArrayInputStream(byteArrayOf(25, 35))
            val r1 = TstRecord().apply {
                this.f = v1
            }
            val r2 = TstRecord().apply {
                this.f = v2
            }

            logTime("Insert") {
                insertInto(TstTable).useAndReloadRecords(r1, r2).execute(connection)
            }
            selectAndPrint()

            println()

            r1.f = ByteArrayInputStream(byteArrayOf(25, 35))
            r2.f = ByteArrayInputStream(byteArrayOf(10, 11, 12))
            logTime("Update") {
                update(TstTable).useAndReloadRecord(r1).execute(connection)
                update(TstTable).useAndReloadRecord(r2).execute(connection)
            }
            selectAndPrint()

            println()

            logTime("Delete") {
                // Use-and-Reload not working
                deleteFrom(TstTable).useRecords(r1).execute(connection)
            }
            selectAndPrint()
        }
    }
}
