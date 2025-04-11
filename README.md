# SQ

SQL builder for Kotlin.

It can help to write something like this:

```kotlin
import io.github.ore.sq.*
import io.github.ore.sq.impl.jdbcRequestDataBuilderFactory
import org.postgresql.util.PGobject
import java.sql.*
import java.sql.Array
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


object TstTable: SqPgTable("tst") { /* io.github.ore.sq.SqPgTable */
    val ID = this.columnHolder.pgBigInt("id", 0)
    val F = this.columnHolder.pgCharacterVarying("f", null)
}

open class TstRecord(): SqRecord() { /* io.github.ore.sq.SqRecord */
    companion object: SqRecordClass<TstRecord>()

    open var id: Long by TstTable.ID.primaryKeyField()
    open var f: String? by TstTable.F.commonField()

    constructor(f: String?): this() {
        this.f = f
    }

    public override fun hasAnyPrimaryKeySet(): Boolean {
        return super.hasAnyPrimaryKeySet()
    }
}

fun main() {
    SqPg.defaultSettings { /* this: io.github.ore.sq.SqSettingsBuilder */
        this.jdbcRequestDataBuilderFactory(
            pretty = true,
            allowComments = true,
        )
    }

    sqPg { /* this: io.github.ore.sq.SqPgContext */
        connect().use { connection: java.sql.Connection ->
            // Insert
            run {
                val record = TstRecord("my text")
                println("Record before INSERT: $record")

                insertInto(TstTable)
                    .useAndReloadRecords(record)
                    .also { /* it: io.github.ore.sq.SqRecordReloadRequest<TstRecord> */
                        println("-- SQL: INSERT --")
                        println(it.wrappedRequest.createJdbcRequestData().sql)
                        println("-----------------")
                    }
                    .execute(connection)
                println("Record after INSERT: $record")
            }

            println()
            println("----")
            println()

            // Select
            run {
                select(TstTable.ID, TstTable.F, TstTable.ID)
                    .from(TstTable)
                    .where(TstTable.ID gt parameters.parameter(5))
                    .orderBy(TstTable.ID.asc())
                    .limit(parameters.parameter(10).commentValueAtEnd())
                    .offset(parameters.parameter(10).commentValueAtEnd())
                    .also { /* it: io.github.ore.sq.SqPgSelect */
                        println("-- SQL: SELECT --")
                        println(it.createJdbcRequestData().sql)
                        println("-----------------")
                    }
                    .execute(connection, TstRecord.mapper()) { /* it: List<TstRecord> */
                        it.readAllAsObjects()
                    }
                    .forEach { println(it) }
            }
        }
    }
}
```

which will produce console output like this:

```
Record before INSERT: TstRecord(id=<value not set>, f=my text)
-- SQL: INSERT --
INSERT INTO tst
  (f)
VALUES
  (?)
-----------------
Record after INSERT: TstRecord(id=664, f=my text)

----

-- SQL: SELECT --
SELECT
  tst.id,
  tst.f,
  tst.id
FROM
  tst
WHERE
  tst.id > ?
ORDER BY
  tst.id ASC
LIMIT ? /* 10 */
OFFSET ? /* 10 */
-----------------
TstRecord(id=662, f=my text)
TstRecord(id=663, f=my text)
TstRecord(id=664, f=my text)
```

###### Maven

Only snapshots are available now. Add following to `pom.xml` to use them:

```xml
<repositories>
    <repository>
        <name>Central Portal Snapshots</name>
        <id>central-portal-snapshots</id>
        <url>https://central.sonatype.com/repository/maven-snapshots/</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
        <releases>
            <enabled>false</enabled>
        </releases>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>io.github.o-r-e</groupId>
        <artifactId>sq-core</artifactId>
        <version>0.0-k1.9.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```
