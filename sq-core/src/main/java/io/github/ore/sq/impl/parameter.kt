package io.github.ore.sq.impl

import io.github.ore.sq.SqContext
import io.github.ore.sq.SqDataTypeReader
import io.github.ore.sq.SqDataTypeWriter
import io.github.ore.sq.SqParameter
import io.github.ore.sq.SqParameterFactory
import io.github.ore.sq.SqThreadParameter
import io.github.ore.sq.SqThreadParameterFactory


// region Simple parameter
open class SqParameterImpl<JAVA, DB: Any>(
    override val reader: SqDataTypeReader<JAVA, DB>,
    override val writer: SqDataTypeWriter<JAVA & Any, DB>,
    override val value: JAVA,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqParameter<JAVA, DB> {
    open class Factory: SqParameterFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun <JAVA, DB : Any> create(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, DB>,
            writer: SqDataTypeWriter<JAVA & Any, DB>,
            value: JAVA
        ): SqParameterImpl<JAVA, DB> {
            return SqParameterImpl(reader, writer, value)
        }
    }
}
// endregion


// region Thread parameter
open class SqThreadParameterImpl<JAVA, DB: Any>(
    override val reader: SqDataTypeReader<JAVA, DB>,
    override val writer: SqDataTypeWriter<JAVA & Any, DB>,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
) : SqThreadParameter<JAVA, DB> {
    open class Factory : SqThreadParameterFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun <JAVA, DB : Any> create(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, DB>,
            writer: SqDataTypeWriter<JAVA & Any, DB>,
        ): SqThreadParameterImpl<JAVA, DB> {
            return SqThreadParameterImpl(reader, writer)
        }
    }
}
// endregion
