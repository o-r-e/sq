package io.github.ore.sq.impl

import io.github.ore.sq.*


// region Main classes
open class SqCaseWhenThenImpl<JAVA, DB: Any>(
    override val reader: SqDataTypeReader<JAVA, DB>,
    override val condition: SqItem,
    override val result: SqItem,
): SqCaseWhenThen<JAVA, DB>


open class SqCaseElseImpl<JAVA, DB: Any>(
    override val reader: SqDataTypeReader<JAVA, DB>,
    override val result: SqItem,
): SqCaseElse<JAVA, DB>


open class SqCaseImpl<JAVA, DB: Any>(
    override val reader: SqDataTypeReader<JAVA, DB>,
    override val items: List<SqCaseItem<*, *>>,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqCase<JAVA, DB>


open class SqCaseFactoryImpl: SqCaseFactory {
    override fun <JAVA, DB : Any> createWhenThen(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, DB>,
        condition: SqItem,
        result: SqItem
    ): SqCaseWhenThenImpl<JAVA, DB> {
        return SqCaseWhenThenImpl(reader, condition, result)
    }

    override fun <JAVA, DB : Any> createElse(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, DB>,
        result: SqItem
    ): SqCaseElseImpl<JAVA, DB> {
        return SqCaseElseImpl(reader, result)
    }

    override fun <JAVA, DB : Any> createCase(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, DB>,
        items: List<SqCaseItem<*, *>>
    ): SqCaseImpl<JAVA, DB> {
        if (items.isEmpty()) {
            error("Item list is empty")
        }
        return SqCaseImpl(reader, items)
    }

    companion object {
        val INSTANCE: SqCaseFactoryImpl = SqCaseFactoryImpl()
    }
}
// endregion


// region Builder
open class SqCaseBuilderImpl<JAVA, DB: Any>(
    protected open val whenThenPairs: MutableCollection<Pair<SqItem, SqItem>> = ArrayList(),
    protected open var mutableReader: SqDataTypeReader<JAVA?, DB>? = null,
    protected open var elseItemResult: SqItem? = null,
    protected open var whenItem: WhenItem<JAVA, DB>? = null,
): SqCaseEmptyBuilder, SqCaseIncompleteBuilder<JAVA, DB>, SqCaseCompleteBuilder<JAVA?, DB> {
    override val reader: SqDataTypeReader<JAVA?, DB>
        get() {
            return this.mutableReader
                ?: error("Reader is not set yet")
        }

    protected open fun <JAVA_NEW, DB_NEW: Any> addWhenThen(
        reader: SqDataTypeReader<JAVA_NEW?, DB_NEW>,
        condition: SqItem,
        result: SqItem,
    ): SqCaseBuilderImpl<JAVA_NEW, DB_NEW> {
        @Suppress("UNCHECKED_CAST")
        this.mutableReader = reader as SqDataTypeReader<JAVA?, DB>

        this.whenThenPairs.add(Pair(condition, result))

        @Suppress("UNCHECKED_CAST")
        return this as SqCaseBuilderImpl<JAVA_NEW, DB_NEW>
    }

    override fun start(condition: SqItem): WhenItem<JAVA, DB> {
        val result = this.whenItem ?: run {
            val result = WhenItemImpl<JAVA, DB>(this)
            this.whenItem = result
            result
        }
        result.setCondition(condition)
        return result
    }

    override fun <JAVA_NEW, DB_NEW : Any> complete(
        reader: SqDataTypeReader<JAVA_NEW, DB_NEW>,
        elseItemResult: SqItem
    ): SqCaseCompleteBuilder<JAVA_NEW, DB_NEW> {
        @Suppress("UNCHECKED_CAST")
        this.mutableReader = reader as SqDataTypeReader<JAVA?, DB>

        this.elseItemResult = elseItemResult

        @Suppress("UNCHECKED_CAST")
        return this as SqCaseCompleteBuilder<JAVA_NEW, DB_NEW>
    }

    override fun end(context: SqContext): SqCase<JAVA?, DB> {
        val reader = this.reader
        val caseFactory = context.settings.caseFactory

        val items = this.elseItemResult.let { elseItemResult ->
            if (elseItemResult == null) {
                this.whenThenPairs.map { (condition, result) ->
                    caseFactory.createWhenThen(context, reader, condition, result)
                }
            } else {
                val whenThenPairs = this.whenThenPairs
                buildList<SqCaseItem<JAVA?, DB>>(whenThenPairs.size + 1) {
                    whenThenPairs.forEach { (condition, result) ->
                        this.add(caseFactory.createWhenThen(context, reader, condition, result))
                    }
                    this.add(caseFactory.createElse(context, reader, elseItemResult))
                }
            }
        }

        return context.settings.caseFactory.createCase(context, this.reader, items)
    }


    interface WhenItem<JAVA, DB: Any>: SqCaseEmptyBuilderWhenItem, SqCaseBuilderWhenItem<JAVA, DB> {
        fun setCondition(condition: SqItem)
    }

    protected open class WhenItemImpl<JAVA, DB: Any>(
        protected open val owner: SqCaseBuilderImpl<JAVA, DB>,
        protected open var mutableCondition: SqItem? = null,
    ): WhenItem<JAVA, DB> {
        override val reader: SqDataTypeReader<JAVA?, DB>
            get() = this.owner.reader

        protected open val condition: SqItem
            get() {
                return this.mutableCondition
                    ?: error("Condition is not set yet")
            }

        override fun setCondition(condition: SqItem) {
            this.mutableCondition = condition
        }

        override fun <JAVA, DB: Any> end(
            reader: SqDataTypeReader<JAVA?, DB>,
            result: SqItem
        ): SqCaseBuilderImpl<JAVA, DB> {
            return this.owner.addWhenThen(reader, this.condition, result)
        }
    }


    open class Factory: SqCaseBuilderFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext): SqCaseBuilderImpl<Any?, Any> =
            SqCaseBuilderImpl()
    }
}
// endregion
