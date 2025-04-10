package io.github.ore.sq.impl

import io.github.ore.sq.SqColumn
import io.github.ore.sq.SqContext
import io.github.ore.sq.SqDataTypeReader
import io.github.ore.sq.SqFragment
import io.github.ore.sq.SqItem
import io.github.ore.sq.SqPgExpressionSelect
import io.github.ore.sq.SqPgExpressionSelectFactory
import io.github.ore.sq.SqPgSelect
import io.github.ore.sq.SqPgSelectFactory
import io.github.ore.sq.SqPgSelectFragmentLimit
import io.github.ore.sq.SqPgSelectFragmentLimitFactory
import io.github.ore.sq.SqPgSelectFragmentOffset
import io.github.ore.sq.SqPgSelectFragmentOffsetFactory
import io.github.ore.sq.SqSelectStartFragment


// region Request, factory
open class SqPgSelectImpl(
    columns: List<SqColumn<*, *>>,
    commentAtStart: String? = null,
    commentAtEnd: String? = null,
    mutableFragments: MutableList<SqFragment> = ArrayList(),
    fragments: List<SqFragment> = mutableFragments,
): SqSelectImpl(columns, commentAtStart, commentAtEnd, mutableFragments, fragments), SqPgSelect {
    open class Factory: SqPgSelectFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, distinct: Boolean, columns: List<SqColumn<*, *>>): SqPgSelectImpl {
            val result = SqPgSelectImpl(columns)
            SqSelectStartFragment.addToRequest(context, distinct, columns, result)
            return result
        }
    }
}

open class SqPgExpressionSelectImpl<JAVA, DB: Any>(
    override val reader: SqDataTypeReader<JAVA, DB>,
    columns: List<SqColumn<*, *>>,
    commentAtStart: String? = null,
    commentAtEnd: String? = null,
    mutableFragments: MutableList<SqFragment> = ArrayList(),
    fragments: List<SqFragment> = mutableFragments,
): SqPgSelectImpl(columns, commentAtStart, commentAtEnd, mutableFragments, fragments), SqPgExpressionSelect<JAVA, DB> {
    open class Factory: SqPgExpressionSelectFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun <JAVA, DB : Any> create(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, DB>,
            distinct: Boolean,
            columns: List<SqColumn<*, *>>
        ): SqPgExpressionSelectImpl<JAVA, DB> {
            val result = SqPgExpressionSelectImpl(reader, columns)
            SqSelectStartFragment.addToRequest(context, distinct, columns, result)
            return result
        }
    }
}
// endregion


// region Fragment "OFFSET"
open class SqPgSelectFragmentOffsetImpl(override val value: SqItem): SqPgSelectFragmentOffset {
    open class Factory: SqPgSelectFragmentOffsetFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, value: SqItem): SqPgSelectFragmentOffsetImpl =
            SqPgSelectFragmentOffsetImpl(value)
    }
}
// endregion


// region Fragment "LIMIT"
open class SqPgSelectFragmentLimitImpl(override val value: SqItem) : SqPgSelectFragmentLimit {
    open class Factory: SqPgSelectFragmentLimitFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, value: SqItem): SqPgSelectFragmentLimitImpl =
            SqPgSelectFragmentLimitImpl(value)
    }
}
// endregion
