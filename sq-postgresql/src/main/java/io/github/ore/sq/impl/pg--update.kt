package io.github.ore.sq.impl

import io.github.ore.sq.SqContext
import io.github.ore.sq.SqFragment
import io.github.ore.sq.SqItem
import io.github.ore.sq.SqPgUpdate
import io.github.ore.sq.SqPgUpdateFactory
import io.github.ore.sq.SqUpdateStartFragment


// region Request
open class SqPgUpdateImpl(
    commentAtStart: String? = null,
    commentAtEnd: String? = null,
    mutableFragments: MutableList<SqFragment> = ArrayList(),
    fragments: List<SqFragment> = mutableFragments,
): SqUpdateImpl(commentAtStart, commentAtEnd, mutableFragments, fragments), SqPgUpdate {
    open class Factory: SqPgUpdateFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, table: SqItem): SqPgUpdateImpl {
            val result = SqPgUpdateImpl()
            SqUpdateStartFragment.addToRequest(context, table, result)
            return result
        }
    }
}
// endregion
