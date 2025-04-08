package io.github.ore.sq.impl

import io.github.ore.sq.SqContext
import io.github.ore.sq.SqDeleteStartFragment
import io.github.ore.sq.SqFragment
import io.github.ore.sq.SqItem
import io.github.ore.sq.SqPgDelete
import io.github.ore.sq.SqPgDeleteFactory


// region Request
open class SqPgDeleteImpl(
    commentAtStart: String? = null,
    commentAtEnd: String? = null,
    mutableFragments: MutableList<SqFragment> = ArrayList(),
    fragments: List<SqFragment> = mutableFragments,
): SqDeleteImpl(commentAtStart, commentAtEnd, mutableFragments, fragments), SqPgDelete {
    open class Factory: SqPgDeleteFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, table: SqItem): SqPgDeleteImpl {
            val result = SqPgDeleteImpl()
            SqDeleteStartFragment.addToRequest(context, table, result)
            return result
        }
    }
}
// endregion
