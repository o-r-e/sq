package io.github.ore.sq.impl

import io.github.ore.sq.SqColumn
import io.github.ore.sq.SqContext
import io.github.ore.sq.SqDataTypeReader
import io.github.ore.sq.SqFragment
import io.github.ore.sq.SqH2ExpressionSelect
import io.github.ore.sq.SqH2ExpressionSelectFactory
import io.github.ore.sq.SqH2SelectFragmentOffset
import io.github.ore.sq.SqH2SelectFragmentOffsetFactory
import io.github.ore.sq.SqH2Select
import io.github.ore.sq.SqH2SelectFactory
import io.github.ore.sq.SqH2SelectFragmentFetch
import io.github.ore.sq.SqH2SelectFragmentFetchFactory
import io.github.ore.sq.SqItem
import io.github.ore.sq.SqSelectStartFragment


// region Request, factory
open class SqH2SelectImpl(
    columns: List<SqColumn<*, *>>,
    commentAtStart: String? = null,
    commentAtEnd: String? = null,
    mutableFragments: MutableList<SqFragment> = ArrayList(),
    fragments: List<SqFragment> = mutableFragments,
): SqSelectImpl(columns, commentAtStart, commentAtEnd, mutableFragments, fragments), SqH2Select {
    open class Factory: SqH2SelectFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, distinct: Boolean, columns: List<SqColumn<*, *>>): SqH2SelectImpl {
            val result = SqH2SelectImpl(columns)
            SqSelectStartFragment.addToRequest(context, distinct, columns, result)
            return result
        }
    }
}

open class SqH2ExpressionSelectImpl<JAVA, DB: Any>(
    override val reader: SqDataTypeReader<JAVA, DB>,
    columns: List<SqColumn<*, *>>,
    commentAtStart: String? = null,
    commentAtEnd: String? = null,
    mutableFragments: MutableList<SqFragment> = ArrayList(),
    fragments: List<SqFragment> = mutableFragments,
): SqH2SelectImpl(columns, commentAtStart, commentAtEnd, mutableFragments, fragments), SqH2ExpressionSelect<JAVA, DB> {
    open class Factory: SqH2ExpressionSelectFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun <JAVA, DB : Any> create(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, DB>,
            distinct: Boolean,
            columns: List<SqColumn<*, *>>,
        ): SqH2ExpressionSelectImpl<JAVA, DB> {
            val result = SqH2ExpressionSelectImpl(reader, columns)
            SqSelectStartFragment.addToRequest(context, distinct, columns, result)
            return result
        }
    }
}
// endregion


// region Fragment "OFFSET"
open class SqH2OffsetFragmentImpl(override val value: SqItem): SqH2SelectFragmentOffset {
    open class Factory: SqH2SelectFragmentOffsetFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, value: SqItem): SqH2OffsetFragmentImpl =
            SqH2OffsetFragmentImpl(value)
    }
}
// endregion


// region Fragment "FETCH"
open class SqH2SelectFragmentFetchImpl(
    override val value: SqItem,
    override val fetchFirst: Boolean,
    override val percent: Boolean,
    override val withTies: Boolean,
): SqH2SelectFragmentFetch {
    open class Factory: SqH2SelectFragmentFetchFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(
            context: SqContext,
            value: SqItem,
            fetchFirst: Boolean,
            percent: Boolean,
            withTies: Boolean,
        ): SqH2SelectFragmentFetchImpl {
            return SqH2SelectFragmentFetchImpl(value, fetchFirst, percent, withTies)
        }
    }
}
// endregion
