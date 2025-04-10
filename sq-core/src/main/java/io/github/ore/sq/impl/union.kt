package io.github.ore.sq.impl

import io.github.ore.sq.*
import java.util.*


// region Request
open class SqUnionImpl(
    override val columns: List<SqColumn<*, *>>,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
    mutableFragments: MutableList<SqFragment> = ArrayList(),
    fragments: List<SqFragment> = mutableFragments,
): SqFragmentedImpl(mutableFragments, fragments), SqUnion {
    override fun addFragment(fragment: SqFragment) {
        this.mutableFragments.add(fragment)
    }


    open class Factory: SqUnionFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }


        protected open fun create(
            context: SqContext,
            columns: List<SqColumn<*, *>>,
            mainFragment: SqUnionMainFragment,
        ): SqUnionImpl {
            if (columns.isEmpty()) {
                error("Column list is empty")
            }
            val result = SqUnionImpl(ArrayList(columns))
            result.addFragment(mainFragment)
            return result
        }

        override fun createUnion(
            context: SqContext,
            columns: List<SqColumn<*, *>>,
            requests: List<SqItem>
        ): SqUnionImpl {
            return this.create(context, columns, context.settings.unionMainFragmentFactory.createUnion(context, requests))
        }

        override fun createUnionAll(
            context: SqContext,
            columns: List<SqColumn<*, *>>,
            requests: List<SqItem>
        ): SqUnion {
            return return this.create(context, columns, context.settings.unionMainFragmentFactory.createUnionAll(context, requests))
        }
    }
}


open class SqExpressionUnionImpl<JAVA, DB: Any>(
    override val reader: SqDataTypeReader<JAVA, DB>,
    columns: List<SqColumn<*, *>>,
    commentAtStart: String? = null,
    commentAtEnd: String? = null,
    mutableFragments: MutableList<SqFragment> = ArrayList(),
    fragments: List<SqFragment> = mutableFragments,
): SqUnionImpl(columns, commentAtStart, commentAtEnd, mutableFragments, fragments), SqExpressionUnion<JAVA, DB> {
    open class Factory: SqExpressionUnionFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }


        protected open fun <JAVA, DB : Any> create(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, DB>,
            columns: List<SqColumn<*, *>>,
            mainFragment: SqUnionMainFragment,
        ): SqExpressionUnionImpl<JAVA, DB> {
            if (columns.isEmpty()) {
                error("Column list is empty")
            }
            val result = SqExpressionUnionImpl(reader, ArrayList(columns))
            result.addFragment(mainFragment)
            return result
        }

        override fun <JAVA, DB : Any> createUnion(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, DB>,
            columns: List<SqColumn<*, *>>,
            requests: List<SqItem>,
        ): SqExpressionUnionImpl<JAVA, DB> {
            return this.create(context, reader, columns, context.settings.unionMainFragmentFactory.createUnion(context, requests))
        }

        override fun <JAVA, DB : Any> createUnionAll(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, DB>,
            columns: List<SqColumn<*, *>>,
            requests: List<SqItem>,
        ): SqExpressionUnion<JAVA, DB> {
            return this.create(context, reader, columns, context.settings.unionMainFragmentFactory.createUnionAll(context, requests))
        }
    }
}
// endregion


// region Main fragment
open class SqUnionMainFragmentImpl(
    override val separatorKeyword: String,
    override val requests: List<SqItem>,
): SqUnionMainFragment {
    open class Factory: SqUnionMainFragmentFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }


        protected open fun create(
            context: SqContext,
            requests: List<SqItem>,
            separatorKeyword: String,
        ): SqUnionMainFragmentImpl {
            if (requests.isEmpty()) {
                error("Request list is empty")
            }
            return SqUnionMainFragmentImpl(separatorKeyword, ArrayList(requests))
        }

        override fun createUnion(
            context: SqContext,
            requests: List<SqItem>
        ): SqUnionMainFragmentImpl {
            return this.create(context, requests, "union")
        }

        override fun createUnionAll(
            context: SqContext,
            requests: List<SqItem>
        ): SqUnionMainFragmentImpl {
            return this.create(context, requests, "union all")
        }
    }
}
// endregion
