package io.github.ore.sq.impl

import io.github.ore.sq.*


// region Read request
open class SqReadRequestTemplateImpl(
    override val context: SqContext,
    override val data: SqJdbcRequestData,
    override val columns: List<SqColumn<*, *>>,
): SqReadRequestTemplate {
    open class Factory: SqReadRequestTemplateFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(
            context: SqContext,
            data: SqJdbcRequestData,
            columns: List<SqColumn<*, *>>,
        ): SqReadRequestTemplateImpl {
            if (columns.isEmpty()) {
                error("Column list is empty")
            }
            return SqReadRequestTemplateImpl(context, data, columns)
        }
    }
}
// endregion


// region Data modification request
open class SqDataModificationRequestTemplateImpl(
    override val context: SqContext,
    override val data: SqJdbcRequestData,
): SqDataModificationRequestTemplate {
    open class Factory: SqDataModificationRequestTemplateFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, data: SqJdbcRequestData): SqDataModificationRequestTemplateImpl =
            SqDataModificationRequestTemplateImpl(context, data)
    }
}
// endregion


// region Returning request
open class SqReturningRequestImpl(
    override val wrappedRequest: SqItem,
    override val columns: List<SqColumn<*, *>>,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqReturningRequest {
    open class Factory: SqReturningRequestFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, wrappedRequest: SqItem, columns: List<SqColumn<*, *>>): SqReturningRequestImpl {
            if (columns.isEmpty()) {
                error("Column list is empty")
            }
            return SqReturningRequestImpl(wrappedRequest, columns)
        }
    }
}


open class SqExpressionReturningRequestImpl<JAVA, DB: Any>(
    override val reader: SqDataTypeReader<JAVA, DB>,
    wrappedRequest: SqItem,
    columns: List<SqColumn<*, *>>,
    commentAtStart: String? = null,
    commentAtEnd: String? = null,
): SqReturningRequestImpl(wrappedRequest, columns, commentAtStart, commentAtEnd), SqExpressionReturningRequest<JAVA, DB> {
    open class Factory: SqExpressionReturningRequestFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun <JAVA, DB : Any> create(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, DB>,
            wrappedRequest: SqItem,
            columns: List<SqColumn<*, *>>
        ): SqExpressionReturningRequestImpl<JAVA, DB> {
            if (columns.isEmpty()) {
                error("Column list is empty")
            }
            return SqExpressionReturningRequestImpl(reader, wrappedRequest, columns)
        }
    }
}
// endregion


// region Record reload request
open class SqRecordReloadRequestImpl<T: SqRecord>(
    override val wrappedRequest: SqItem,
    override val config: SqRecordReloadRequest.ReloadableConfig<T>,
): SqRecordReloadRequest<T> {
    open class Factory: SqRecordReloadRequestFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun <T : SqRecord> create(
            context: SqContext,
            wrappedRequest: SqItem,
            config: SqRecordReloadRequest.ReloadableConfig<T>
        ): SqRecordReloadRequestImpl<T> {
            return SqRecordReloadRequestImpl(wrappedRequest, config)
        }
    }
}
// endregion
