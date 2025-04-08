package io.github.ore.sq.impl

import io.github.ore.sq.*
import io.github.ore.sq.util.SqItemPartConfig


// region Alias definition
open class SqAliasDefinitionImpl(
    override val original: SqItem,
    override val name: String,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqAliasDefinition {
    open class Factory: SqAliasDefinitionFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(
            context: SqContext,
            original: SqItem,
            name: String
        ): SqAliasDefinitionImpl {
            return SqAliasDefinitionImpl(original, name)
        }
    }
}
// endregion


// region Expression alias
open class SqExpressionAliasImpl<JAVA, DB: Any>(
    override val reader: SqDataTypeReader<JAVA, DB>,
    override val name: String,
    override val definition: SqItem,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqExpressionAlias<JAVA, DB> {
    open class Factory: SqExpressionAliasFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun <JAVA, DB : Any> create(
            context: SqContext,
            original: SqItem,
            reader: SqDataTypeReader<JAVA, DB>,
            name: String
        ): SqExpressionAliasImpl<JAVA, DB> {
            val definition = context.settings.aliasDefinitionFactory.invoke(context, original, name)
            return SqExpressionAliasImpl(reader, name, definition)
        }
    }
}
// endregion


// region Column source alias
open class SqColumnSourceAliasImpl(
    override val name: String,
    override val definition: SqItem,
    override val columns: List<SqColumn<*, *>>,
    protected open val columnMapByOriginal: Map<SqItem, SqColumn<*, *>>,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqColumnSourceAlias {
    override fun <JAVA, DB : Any> get(original: SqColumn<JAVA, DB>): SqColumn<JAVA, DB> {
        val result = this.columnMapByOriginal[original]
            ?: error("Column source alias <$this> has not column associated with original column <$original>")
        @Suppress("UNCHECKED_CAST")
        return result as SqColumn<JAVA, DB>
    }


    open class Column<JAVA, DB: Any>(
        protected open val ownerAlias: String,
        override val name: String,
        override val reader: SqDataTypeReader<JAVA, DB>,
        override var commentAtStart: String? = null,
        override var commentAtEnd: String? = null,
    ): SqColumn<JAVA, DB> {
        override val isMultiline: Boolean
            get() = false

        override fun addToBuilderWithoutComments(
            context: SqContext,
            target: SqJdbcRequestDataBuilder,
            partConfig: SqItemPartConfig?
        ) {
            target.identifier(this.ownerAlias).dot().identifier(this.name)
        }
    }

    open class FactoryBase {
        protected inline fun <T> create(
            context: SqContext,
            name: String,
            original: SqItem,
            columns: List<SqColumn<*, *>>,
            block: (definition: SqAliasDefinition, localColumns: MutableList<SqColumn<*, *>>, columnMapByOriginal: MutableMap<SqItem, SqColumn<*, *>>) -> T,
        ): T {
            if (columns.isEmpty()) {
                error("Column list is empty")
            }

            val definition = context.settings.aliasDefinitionFactory.invoke(context, original, name)

            val localColumns = ArrayList<SqColumn<*, *>>()
            val columnMapByOriginal = HashMap<SqItem, SqColumn<*, *>>()
            columns.forEach { originalColumn ->
                val aliasColumn = Column(
                    ownerAlias = name,
                    name = originalColumn.name,
                    reader = originalColumn.reader,
                )
                localColumns.add(aliasColumn)
                columnMapByOriginal[originalColumn] = aliasColumn
            }

            return block(definition, localColumns, columnMapByOriginal)
        }
    }

    open class Factory: FactoryBase(), SqColumnSourceAliasFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(
            context: SqContext,
            name: String,
            original: SqItem,
            columns: List<SqColumn<*, *>>,
        ): SqColumnSourceAliasImpl {
            return this.create(context, name, original, columns) { definition, localColumns, columnMapByOriginal ->
                SqColumnSourceAliasImpl(
                    name = name,
                    definition = definition,
                    columns = localColumns,
                    columnMapByOriginal = columnMapByOriginal,
                )
            }
        }
    }
}


open class SqExpressionColumnSourceAliasImpl<JAVA, DB: Any>(
    override val reader: SqDataTypeReader<JAVA, DB>,
    name: String,
    definition: SqItem,
    columns: List<SqColumn<*, *>>,
    columnMapByOriginal: Map<SqItem, SqColumn<*, *>>,
    commentAtStart: String? = null,
    commentAtEnd: String? = null,
): SqColumnSourceAliasImpl(name, definition, columns, columnMapByOriginal, commentAtStart, commentAtEnd), SqExpressionColumnSourceAlias<JAVA, DB> {
    open class Factory: FactoryBase(), SqExpressionColumnSourceAliasFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun <JAVA, DB : Any> create(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, DB>,
            name: String,
            original: SqItem,
            columns: List<SqColumn<*, *>>
        ): SqExpressionColumnSourceAliasImpl<JAVA, DB> {
            return this.create(context, name, original, columns) { definition, localColumns, columnMapByOriginal ->
                SqExpressionColumnSourceAliasImpl(
                    reader = reader,
                    name = name,
                    definition = definition,
                    columns = localColumns,
                    columnMapByOriginal = columnMapByOriginal,
                )
            }
        }
    }
}
// endregion
