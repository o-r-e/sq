package io.github.ore.sq.util

import io.github.ore.sq.SqPgDataTypes
import org.postgresql.util.PGobject


@Suppress("unused")
open class SqPgBit(
    open val value: List<Boolean>,
) {
    companion object {
        fun parse(stringValue: String): List<Boolean> {
            return stringValue.map {
                when {
                    (it == '0') -> false
                    (it == '1') -> true
                    else -> error("Postgresql BIT string has invalid character '$it', invalid string: \"$stringValue\"")
                }
            }
        }

        fun parse(objectValue: PGobject): List<Boolean> {
            val stringValue = objectValue.value
                ?: return emptyList()
            return parse(stringValue)
        }
    }


    constructor(value: String): this(parse(value))

    constructor(value: PGobject): this(parse(value))


    override fun toString(): String =
        "${this.javaClass.simpleName}(${this.toStringValue()})"

    open fun toStringValue(): String {
        val value = this.value
        return buildString(value.size) {
            value.forEach {
                if (it) {
                    this.append('1')
                } else {
                    this.append('0')
                }
            }
        }
    }

    open fun toPgObject(dbTypeName: String = SqPgDataTypes.DB_TYPE_NAME__BIT): PGobject {
        return PGobject().also { result ->
            result.type = dbTypeName
            result.value = this.toStringValue()
        }
    }
}
