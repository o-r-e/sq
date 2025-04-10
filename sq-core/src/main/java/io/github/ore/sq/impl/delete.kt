package io.github.ore.sq.impl

import io.github.ore.sq.SqContext
import io.github.ore.sq.SqDelete
import io.github.ore.sq.SqDeleteFactory
import io.github.ore.sq.SqDeleteStartFragment
import io.github.ore.sq.SqDeleteStartFragmentFactory
import io.github.ore.sq.SqDeleteWhereFragment
import io.github.ore.sq.SqDeleteWhereFragmentFactory
import io.github.ore.sq.SqFragment
import io.github.ore.sq.SqItem
import io.github.ore.sq.deleteStartFragmentFactory


// region Request
open class SqDeleteImpl(
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
    mutableFragments: MutableList<SqFragment> = ArrayList(),
    fragments: List<SqFragment> = mutableFragments,
): SqFragmentedImpl(mutableFragments, fragments), SqDelete {
    open class Factory: SqDeleteFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, table: SqItem): SqDeleteImpl {
            val result = SqDeleteImpl()
            SqDeleteStartFragment.addToRequest(context, table, result)
            return result
        }
    }
}
// endregion


// region Start fragment
open class SqDeleteStartFragmentImpl(override val table: SqItem): SqDeleteStartFragment {
    open class Factory: SqDeleteStartFragmentFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, table: SqItem): SqDeleteStartFragmentImpl =
            SqDeleteStartFragmentImpl(table)
    }
}
// endregion


// region Fragment "WHERE"
open class SqDeleteWhereFragmentImpl(override val condition: SqItem): SqDeleteWhereFragment {
    open class Factory: SqDeleteWhereFragmentFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, condition: SqItem): SqDeleteWhereFragmentImpl =
            SqDeleteWhereFragmentImpl(condition)
    }
}
// endregion
