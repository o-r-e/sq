package io.github.ore.sq.impl

import io.github.ore.sq.SqColumn
import io.github.ore.sq.SqContext
import io.github.ore.sq.SqItem
import io.github.ore.sq.SqJoin
import io.github.ore.sq.SqJoinFactory


open class SqJoinImpl(
    override val leftItem: SqItem,
    override val operationKeyword: String,
    override val rightItem: SqItem,
    override val columns: List<SqColumn<*, *>>,
    override var condition: SqItem? = null,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqJoin {
    open class Factory: SqJoinFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }


        override fun createInner(
            context: SqContext,
            leftItem: SqItem,
            rightItem: SqItem,
            columns: List<SqColumn<*, *>>
        ): SqJoinImpl {
            return SqJoinImpl(leftItem, "inner join", rightItem, columns)
        }

        override fun createLeft(
            context: SqContext,
            leftItem: SqItem,
            rightItem: SqItem,
            columns: List<SqColumn<*, *>>
        ): SqJoinImpl {
            return SqJoinImpl(leftItem, "left join", rightItem, columns)
        }

        override fun createRight(
            context: SqContext,
            leftItem: SqItem,
            rightItem: SqItem,
            columns: List<SqColumn<*, *>>
        ): SqJoinImpl {
            return SqJoinImpl(leftItem, "right join", rightItem, columns)
        }

        override fun createFull(
            context: SqContext,
            leftItem: SqItem,
            rightItem: SqItem,
            columns: List<SqColumn<*, *>>
        ): SqJoinImpl {
            return SqJoinImpl(leftItem, "full join", rightItem, columns)
        }
    }
}
