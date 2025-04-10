package io.github.ore.sq.impl

import io.github.ore.sq.SqContext
import io.github.ore.sq.SqFragment
import io.github.ore.sq.SqInsertStartFragment
import io.github.ore.sq.SqItem
import io.github.ore.sq.SqPgInsert
import io.github.ore.sq.SqPgInsertFactory


// region Request
open class SqPgInsertImpl(
    commentAtStart: String? = null,
    commentAtEnd: String? = null,
    mutableFragments: MutableList<SqFragment> = ArrayList(),
    fragments: List<SqFragment> = mutableFragments,
): SqInsertImpl(commentAtStart, commentAtEnd, mutableFragments, fragments), SqPgInsert {
    open class Factory: SqPgInsertFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, table: SqItem): SqPgInsertImpl {
            val result = SqPgInsertImpl()
            SqInsertStartFragment.addToRequest(context, table, result)
            return result
        }
    }
}
// endregion
