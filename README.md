# SQ

SQL builder for Kotlin.

May be if future it will help to write something like this:

```kotlin
import me.ore.sq.*
import me.ore.sq.generic.dbBigInt
import me.ore.sq.generic.dbVarChar


object TBigintTable: SqTable(schemeName = "tst", tableName = "t_bigint") {
    val fld = this.dbBigInt("fld")
    val txt = this.dbVarChar("txt")
}

fun main() {
    connect { connection: java.sql.Connection ->
        sq(connection) sq@ {
            // this: SqConnectedContext
            this.printParameterValuesByThread = true

            val col1 = TBigintTable.fld
            val col2Expression: SqTwoOperandMathOperation<Number?> =
                TBigintTable.fld mult 2.5 // tst.t_bigint.fld * 2.5
            val col2: SqExpressionAlias<Number?, Number, SqTwoOperandMathOperation<Number?>> =
                col2Expression alias "TST"
            val col3: SqExpressionAlias<Number?, Number, SqTwoOperandMathOperation<Number?>> =
                jLongParam(123) add 321 alias "My column with sum"

            this
                .select(col1, col2, col3)
                .from(TBigintTable)
                .where(
                    or( // Conditions just for example
                        TBigintTable.fld.isNotNull(),
                        TBigintTable.fld eq 123,
                        TBigintTable.fld.between(10, 10_000),
                    )
                )
                .also {
                    println("SQL: ${it.sql()}")
                }
                .scanAll {
                    // this: SqConnMultiColSelect
                    //     Conn - Connected
                    //     MultiCol - Multiple columns; "single column" select can act as SQL expression with type of its data
                    // it: SqResultSet

                    val col1Value = it[col1] // col1Value: Long
                    println("Col 1: $col1Value")

                    it[col2].let { number -> // number: Number?
                        if (number == null) {
                            println("Col 2: value is null")
                        } else {
                            println("Col 2: value: [${number.javaClass.name}] $number")
                        }
                    }

                    val col3Value = it[col3] // col3Value: Number?
                    println("Col 3: $col3Value")
                }
        }
    }
}
```

which will produce something like this:

```
SQL: SELECT fld, (fld * ? /* 2.5 */) AS "TST", (? /* 123 */ + ? /* 321 */) AS "My column with sum"
FROM tst.t_bigint
WHERE ((fld IS NOT NULL) OR (fld = ? /* 123 */) OR (fld BETWEEN ? /* 10 */ AND ? /* 10000 */))
LIMIT ? /* 10 */ OFFSET ? /* 0 */
Col 1: 307.5
Col 2: value: [java.lang.Double] 307.5
Col 3: 444
```

###### Snapshots

Snapshots can be found here -
https://s01.oss.sonatype.org/content/repositories/snapshots/io/github/o-r-e/ .

Use snapshots in `build.gradle.kts`:

```kotlin
repositories {
    //...
    maven {
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
        mavenContent {
            snapshotsOnly()
        }
    }
    //...
}

dependencies {
    //...
    val sqVersion = "0.1-SNAPSHOT"
    implementation("io.github.o-r-e:sq-core:$sqVersion")
    implementation("io.github.o-r-e:sq-postgresql:$sqVersion")
    //...
}
```
