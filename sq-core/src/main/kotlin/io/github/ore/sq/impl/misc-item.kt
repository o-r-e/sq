package io.github.ore.sq.impl

import io.github.ore.sq.*


// region ANY, ALL
open class SqExpressionRequestAggregationImpl<JAVA, DB: Any>(
    override val reader: SqDataTypeReader<JAVA, DB>,
    override val operationKeyword: String,
    override val request: SqItem,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqExpressionRequestAggregation<JAVA, DB> {
    open class Factory: SqExpressionRequestAggregationFactory {
        companion object {
            val INSTANCE: Factory = Factory()
            const val OPERATION_KEYWORD__ANY = "any"
            const val OPERATION_KEYWORD__ALL = "all"
        }


        override fun <JAVA, DB : Any> createAny(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, DB>,
            request: SqItem,
        ): SqExpressionRequestAggregationImpl<JAVA, DB> {
            return SqExpressionRequestAggregationImpl(reader, OPERATION_KEYWORD__ANY, request)
        }

        override fun <JAVA, DB : Any> createAll(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, DB>,
            request: SqItem,
        ): SqExpressionRequestAggregationImpl<JAVA, DB> {
            return SqExpressionRequestAggregationImpl(reader, OPERATION_KEYWORD__ALL, request)
        }
    }
}
// endregion


// region COALESCE
open class SqCoalesceImpl<JAVA, DB: Any>(
    override val reader: SqDataTypeReader<JAVA, DB>,
    override val parameters: List<SqItem>,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqCoalesce<JAVA, DB> {
    open class Factory: SqCoalesceFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun <JAVA, DB : Any> create(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, DB>,
            parameters: List<SqItem>
        ): SqCoalesceImpl<JAVA, DB> {
            if (parameters.isEmpty()) {
                error("Parameter list is empty")
            }
            return SqCoalesceImpl(reader, ArrayList(parameters))
        }
    }
}


open class SqCoalesceBuilderImpl<JAVA, DB: Any>(
    protected open var mutableReader: SqDataTypeReader<JAVA, DB>? = null,
    protected open val parameters: MutableList<SqItem> = ArrayList(),
): SqCoalesceEmptyBuilder, SqCoalesceBuilder<JAVA, DB> {
    override val reader: SqDataTypeReader<JAVA, DB>
        get() {
            return this.mutableReader
                ?: error("Reader is not set yet")
        }

    override fun <JAVA_NEW, DB_NEW: Any> add(
        reader: SqDataTypeReader<JAVA_NEW, DB_NEW>,
        parameter: SqItem
    ): SqCoalesceBuilderImpl<JAVA_NEW, DB_NEW> {
        @Suppress("UNCHECKED_CAST")
        this.mutableReader = reader as SqDataTypeReader<JAVA, DB>
        this.parameters.add(parameter)

        @Suppress("UNCHECKED_CAST")
        return this as SqCoalesceBuilderImpl<JAVA_NEW, DB_NEW>
    }

    override fun end(context: SqContext): SqCoalesce<JAVA, DB> =
        context.settings.coalesceFactory.create(context, this.reader, ArrayList(this.parameters))


    open class Factory: SqCoalesceBuilderFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext): SqCoalesceBuilderImpl<Any, Any> =
            SqCoalesceBuilderImpl()
    }
}
// endregion


// region CAST
open class SqCastImpl<JAVA, DB: Any>(
    override val reader: SqDataTypeReader<JAVA, DB>,
    override val expression: SqItem,
    override val dbTypeText: String,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqCast<JAVA, DB> {
    open class Factory: SqCastFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun <JAVA, DB : Any> create(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, DB>,
            expression: SqItem,
            dbTypeText: String,
        ): SqCastImpl<JAVA, DB> {
            return SqCastImpl(reader, expression, dbTypeText)
        }
    }
}
// endregion


// region Fragment "ORDER BY"
open class SqOrderFragmentImpl(override val columns: List<SqOrderFragmentColumn>): SqOrderFragment {
    open class Factory: SqOrderFragmentFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, columns: List<SqOrderFragmentColumn>): SqOrderFragmentImpl {
            if (columns.isEmpty()) {
                error("Column list is empty")
            }
            return SqOrderFragmentImpl(ArrayList(columns))
        }
    }
}


open class SqOrderFragmentColumnImpl(
    override val column: SqItem,
    override val directionKeyword: String?,
): SqOrderFragmentColumn {
    open class Factory: SqOrderFragmentColumnFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }


        override fun create(context: SqContext, column: SqItem): SqOrderFragmentColumnImpl =
            SqOrderFragmentColumnImpl(column, null)

        override fun createAsc(context: SqContext, column: SqItem): SqOrderFragmentColumnImpl =
            SqOrderFragmentColumnImpl(column, "asc")

        override fun createDesc(context: SqContext, column: SqItem): SqOrderFragmentColumnImpl =
            SqOrderFragmentColumnImpl(column, "desc")
    }
}
// endregion
