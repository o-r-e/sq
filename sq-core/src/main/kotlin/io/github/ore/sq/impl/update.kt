package io.github.ore.sq.impl

import io.github.ore.sq.SqColumn
import io.github.ore.sq.SqContext
import io.github.ore.sq.SqFragment
import io.github.ore.sq.SqItem
import io.github.ore.sq.SqUpdate
import io.github.ore.sq.SqUpdateColumnValueMapper
import io.github.ore.sq.SqUpdateColumnValueMapperFactory
import io.github.ore.sq.SqUpdateFactory
import io.github.ore.sq.SqUpdateSetFragment
import io.github.ore.sq.SqUpdateSetFragmentFactory
import io.github.ore.sq.SqUpdateStartFragment
import io.github.ore.sq.SqUpdateStartFragmentFactory
import io.github.ore.sq.SqUpdateWhereFragment
import io.github.ore.sq.SqUpdateWhereFragmentFactory
import io.github.ore.sq.updateStartFragmentFactory


// region Request
open class SqUpdateImpl(
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
    mutableFragments: MutableList<SqFragment> = ArrayList(),
    fragments: List<SqFragment> = mutableFragments,
): SqFragmentedImpl(mutableFragments, fragments), SqUpdate {
    open class Factory: SqUpdateFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, table: SqItem): SqUpdateImpl {
            val result = SqUpdateImpl()
            SqUpdateStartFragment.addToRequest(context, table, result)
            return result
        }
    }
}
// endregion


// region Start fragment
open class SqUpdateStartFragmentImpl(override val table: SqItem): SqUpdateStartFragment {
    open class Factory: SqUpdateStartFragmentFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, table: SqItem): SqUpdateStartFragmentImpl =
            SqUpdateStartFragmentImpl(table)
    }
}
// endregion


// region Fragment "SET"
open class SqUpdateSetFragmentImpl(override val columnValueMap: Map<SqColumn<*, *>, SqItem>): SqUpdateSetFragment {
    open class Factory: SqUpdateSetFragmentFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, columnValueMap: Map<SqColumn<*, *>, SqItem>): SqUpdateSetFragmentImpl {
            if (columnValueMap.isEmpty()) {
                error("\"Column-value\" map is empty")
            }
            return SqUpdateSetFragmentImpl(HashMap(columnValueMap))
        }
    }
}
// endregion


// region Column-value mapper
open class SqUpdateColumnValueMapperImpl(
    protected open val mutableColumnValueMap: MutableMap<SqColumn<*, *>, SqItem> = LinkedHashMap(),
    override val columnValueMap: Map<SqColumn<*, *>, SqItem> = mutableColumnValueMap,
): SqUpdateColumnValueMapper {
    override fun setValue(column: SqColumn<*, *>, value: SqItem) {
        this.mutableColumnValueMap[column] = value
    }

    override fun clear() {
        this.mutableColumnValueMap.clear()
    }


    open class Factory: SqUpdateColumnValueMapperFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext): SqUpdateColumnValueMapperImpl =
            SqUpdateColumnValueMapperImpl()
    }
}
// endregion


// region Fragment "WHERE"
open class SqUpdateWhereFragmentImpl(override val condition: SqItem): SqUpdateWhereFragment {
    open class Factory: SqUpdateWhereFragmentFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, condition: SqItem): SqUpdateWhereFragmentImpl =
            SqUpdateWhereFragmentImpl(condition)
    }
}
// endregion
