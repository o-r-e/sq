package io.github.ore.sq.impl

import io.github.ore.sq.SqContext
import io.github.ore.sq.SqDataTypeReader
import io.github.ore.sq.SqNull
import io.github.ore.sq.SqNullFactory


open class SqNullImpl<JAVA: Any, DB: Any>(
    override val reader: SqDataTypeReader<JAVA?, DB>,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
) : SqNull<JAVA, DB> {
    open class Factory: SqNullFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun <JAVA : Any, DB : Any> create(context: SqContext, reader: SqDataTypeReader<JAVA?, DB>): SqNullImpl<JAVA, DB> =
            SqNullImpl(reader)
    }
}
