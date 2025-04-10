@file:Suppress("unused")

package io.github.ore.sq.impl

import io.github.ore.sq.*
import java.sql.Connection
import java.util.*


// region Request
open class SqInsertImpl(
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
    mutableFragments: MutableList<SqFragment> = ArrayList(),
    fragments: List<SqFragment> = mutableFragments,
): SqFragmentedImpl(mutableFragments, fragments), SqInsert {
    open class Factory: SqInsertFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, table: SqItem): SqInsertImpl {
            val result = SqInsertImpl()
            SqInsertStartFragment.addToRequest(context, table, result)
            return result
        }
    }
}
// endregion


// region Start fragment
open class SqInsertStartFragmentImpl(override val table: SqItem): SqInsertStartFragment {
    open class Factory: SqInsertStartFragmentFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, table: SqItem): SqInsertStartFragmentImpl =
            SqInsertStartFragmentImpl(table)
    }
}
// endregion


// region Fragment with columns
open class SqInsertColumnsFragmentImpl(override val columns: List<SqColumn<*, *>>): SqInsertColumnsFragment {
    open class Factory: SqInsertColumnsFragmentFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, columns: List<SqColumn<*, *>>): SqInsertColumnsFragmentImpl {
            if (columns.isEmpty()) {
                error("Column list is empty")
            }
            return SqInsertColumnsFragmentImpl(ArrayList(columns))
        }
    }
}
// endregion


// region Fragment "VALUES"
open class SqInsertValuesFragmentImpl(override val valueRows: List<List<SqItem>>): SqInsertValuesFragment {
    open class Factory: SqInsertValuesFragmentFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, valueRows: List<List<SqItem>>): SqInsertValuesFragmentImpl {
            if (valueRows.isEmpty()) {
                error("Value row list is empty")
            }
            val actualValueRows = valueRows.mapIndexed { index, row ->
                if (row.isEmpty()) {
                    error("Value row with index #$index is empty")
                }
                ArrayList(row)
            }
            return SqInsertValuesFragmentImpl(actualValueRows)
        }
    }
}
// endregion


// region Column-value mapper
open class SqInsertColumnValueMapperImpl(
    protected open val mutableValueRows: MutableList<MutableMap<SqItem, SqItem>> = ArrayList(),
    protected open val mutableColumns: MutableSet<SqColumn<*, *>> = LinkedHashSet(),
    override val valueRows: Collection<Map<SqItem, SqItem>> = mutableValueRows,
    override val columns: Collection<SqColumn<*, *>> = mutableColumns,
): SqInsertColumnValueMapper {
    override fun setValue(rowIndex: Int, column: SqColumn<*, *>, value: SqItem) {
        val mutableValueRows = this.mutableValueRows
        while (mutableValueRows.size <= rowIndex) {
            mutableValueRows.add(HashMap())
        }

        val valueRow = mutableValueRows[rowIndex]
        valueRow[column] = value

        this.mutableColumns.add(column)
    }

    override fun clear() {
        this.mutableValueRows.forEach { it.clear() }
        this.mutableValueRows.clear()
        this.mutableColumns.clear()
    }


    open class Factory: SqInsertColumnValueMapperFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext): SqInsertColumnValueMapperImpl =
            SqInsertColumnValueMapperImpl()
    }
}
// endregion


// region Fragment "SELECT"
open class SqInsertSelectFragmentImpl(override val select: SqItem): SqInsertSelectFragment {
    open class Factory: SqInsertSelectFragmentFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, select: SqItem): SqInsertSelectFragmentImpl =
            SqInsertSelectFragmentImpl(select)
    }
}
// endregion
