package io.github.ore.sq.impl

import io.github.ore.sq.*
import java.util.*


// region Request, factory
open class SqSelectImpl(
    override val columns: List<SqColumn<*, *>>,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
    mutableFragments: MutableList<SqFragment> = ArrayList(),
    fragments: List<SqFragment> = mutableFragments,
): SqFragmentedImpl(mutableFragments, fragments), SqSelect {
    open class Factory: SqSelectFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(
            context: SqContext,
            distinct: Boolean,
            columns: List<SqColumn<*, *>>,
        ): SqSelect {
            if (columns.isEmpty()) {
                error("Column list is empty")
            }
            val result = SqSelectImpl(ArrayList(columns))
            SqSelectStartFragment.addToRequest(context, distinct, columns, result)
            return result
        }
    }
}


open class SqExpressionSelectImpl<JAVA, DB: Any>(
    override val reader: SqDataTypeReader<JAVA, DB>,
    columns: List<SqColumn<*, *>>,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
    mutableFragments: MutableList<SqFragment> = ArrayList(),
    fragments: List<SqFragment> = Collections.unmodifiableList(mutableFragments),
): SqSelectImpl(columns, commentAtStart, commentAtEnd, mutableFragments, fragments), SqExpressionSelect<JAVA, DB> {
    open class Factory: SqExpressionSelectFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun <JAVA, DB : Any> create(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, DB>,
            distinct: Boolean,
            columns: List<SqColumn<*, *>>,
        ): SqExpressionSelectImpl<JAVA, DB> {
            if (columns.isEmpty()) {
                error("Column list is empty")
            }
            val result = SqExpressionSelectImpl<JAVA, DB>(reader, ArrayList(columns))
            SqSelectStartFragment.addToRequest(context, distinct, columns, result)
            return result
        }
    }
}
// endregion


// region Start fragment
open class SqSelectStartFragmentImpl(
    override val distinct: Boolean,
    override val columns: List<SqItem>,
): SqSelectStartFragment {
    open class Factory: SqSelectStartFragmentFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, distinct: Boolean, columns: List<SqItem>): SqSelectStartFragmentImpl {
            if (columns.isEmpty()) {
                error("Column list is empty")
            }
            return SqSelectStartFragmentImpl(distinct, ArrayList(columns))
        }
    }
}

fun <T: SqSelect> SqContext.addStartFragment(target: T, distinct: Boolean, columns: List<SqColumn<*, *>>): T {
    val fragment = this.settings.selectStartFragmentFactory.invoke(this, distinct, columns)
    target.addFragment(fragment)
    return target
}
// endregion


// region Fragment "FROM"
open class SqSelectFromFragmentImpl(override val sources: List<SqItem>): SqSelectFromFragment {
    open class Factory: SqSelectFromFragmentFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, sources: List<SqItem>): SqSelectFromFragmentImpl {
            if (sources.isEmpty()) {
                error("Source list is empty")
            }
            return SqSelectFromFragmentImpl(ArrayList(sources))
        }
    }
}
// endregion


// region Fragment "WHERE"
open class SqSelectWhereFragmentImpl(override val condition: SqItem): SqSelectWhereFragment {
    open class Factory: SqSelectWhereFragmentFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, condition: SqItem): SqSelectWhereFragmentImpl =
            SqSelectWhereFragmentImpl(condition)
    }
}
// endregion


// region Fragment "GROUP BY"
open class SqSelectGroupFragmentImpl(override val columns: List<SqItem>): SqSelectGroupFragment {
    open class Factory: SqSelectGroupFragmentFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, columns: List<SqItem>): SqSelectGroupFragmentImpl {
            if (columns.isEmpty()) {
                error("Column list is empty")
            }
            return SqSelectGroupFragmentImpl(ArrayList(columns))
        }
    }
}
// endregion


// region Fragment "HAVING"
open class SqSelectHavingFragmentImpl(override val condition: SqItem): SqSelectHavingFragment {
    open class Factory: SqSelectHavingFragmentFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, condition: SqItem): SqSelectHavingFragmentImpl =
            SqSelectHavingFragmentImpl(condition)
    }
}
// endregion
