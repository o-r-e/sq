package io.github.ore.sq.impl

import io.github.ore.sq.*


// region Reader pair
open class SqBooleanOperationReaderPairImpl(
    override val notNullReader: SqDataTypeReader<Boolean, Boolean>,
    override val nullableReader: SqDataTypeReader<Boolean?, Boolean>,
): SqBooleanOperationReaderPair {
    companion object {
        val INSTANCE: SqBooleanOperationReaderPairImpl = SqBooleanOperationReaderPairImpl(
            notNullReader = SqDataTypesImpl.INSTANCE.boolean.notNullReader,
            nullableReader = SqDataTypesImpl.INSTANCE.boolean.nullableReader,
        )
    }
}
// endregion


// region Boolean group (AND, OR)
open class SqBooleanGroupImpl<JAVA: Boolean?>(
    override val items: List<SqItem>,
    override val operationKeyword: String,
    override val reader: SqDataTypeReader<JAVA, Boolean>,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqBooleanGroup<JAVA> {
    open class Factory: SqBooleanGroupFactory {
        companion object {
            val INSTANCE: Factory = Factory()
            protected const val OPERATION_KEYWORD__AND: String = "and"
            protected const val OPERATION_KEYWORD__OR: String = "or"
        }


        protected open fun <JAVA : Boolean?> create(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Boolean>,
            items: List<SqItem>,
            operationKeyword: String,
        ): SqBooleanGroupImpl<JAVA> {
            if (items.isEmpty()) {
                error("Item list is empty")
            }
            return SqBooleanGroupImpl(items, operationKeyword, reader)
        }

        override fun <JAVA : Boolean?> createAnd(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Boolean>,
            items: List<SqItem>,
        ): SqBooleanGroupImpl<JAVA> {
            return this.create(context, reader, items, OPERATION_KEYWORD__AND)
        }

        override fun <JAVA : Boolean?> createOr(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Boolean>,
            items: List<SqItem>,
        ): SqBooleanGroup<JAVA> {
            return this.create(context, reader, items, OPERATION_KEYWORD__OR)
        }
    }
}
// endregion


// region IS NULL, IS NOT NULL
open class SqIsNullTestImpl(
    override val testedItem: SqItem,
    override val negative: Boolean,
    override val reader: SqDataTypeReader<Boolean, Boolean> = SqDataTypesImpl.INSTANCE.boolean.notNullReader,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqIsNullTest {
    open class Factory: SqIsNullTestFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(
            context: SqContext,
            testedItem: SqItem,
            negative: Boolean
        ): SqIsNullTestImpl {
            return SqIsNullTestImpl(testedItem, negative, context.settings.booleanOperationReaderPair.notNullReader)
        }
    }
}
// endregion


// region NOT
open class SqNotImpl<JAVA: Boolean?>(
    override val reader: SqDataTypeReader<JAVA, Boolean>,
    override val testedItem: SqItem,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqNot<JAVA> {
    open class Factory: SqNotFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun <JAVA : Boolean?> create(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Boolean>,
            testedItem: SqItem
        ): SqNotImpl<JAVA> {
            return SqNotImpl(reader, testedItem)
        }
    }
}
// endregion


// region Comparison
open class SqTwoValuesComparisonImpl<JAVA: Boolean?>(
    override val reader: SqDataTypeReader<JAVA, Boolean>,
    override val leftItem: SqItem,
    override val operationKeyword: String,
    override val rightItem: SqItem,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqTwoValuesComparison<JAVA> {
    open class Factory: SqTwoValuesComparisonFactory {
        companion object {
            val INSTANCE: Factory = Factory()
            protected const val OPERATION_KEYWORD__EQ: String = "="
            protected const val OPERATION_KEYWORD__NEQ: String = "<>"
            protected const val OPERATION_KEYWORD__GT: String = ">"
            protected const val OPERATION_KEYWORD__GTE: String = ">="
            protected const val OPERATION_KEYWORD__LT: String = "<"
            protected const val OPERATION_KEYWORD__LTE: String = "<="
            protected const val OPERATION_KEYWORD__LIKE: String = "like"
            protected const val OPERATION_KEYWORD__NOT_LIKE: String = "not like"
        }


        override fun <JAVA : Boolean?> createEq(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Boolean>,
            leftItem: SqItem,
            rightItem: SqItem,
        ): SqTwoValuesComparisonImpl<JAVA> {
            return SqTwoValuesComparisonImpl(reader, leftItem, OPERATION_KEYWORD__EQ, rightItem)
        }

        override fun <JAVA : Boolean?> createNeq(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Boolean>,
            leftItem: SqItem,
            rightItem: SqItem,
        ): SqTwoValuesComparisonImpl<JAVA> {
            return SqTwoValuesComparisonImpl(reader, leftItem, OPERATION_KEYWORD__NEQ, rightItem)
        }

        override fun <JAVA : Boolean?> createGt(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Boolean>,
            leftItem: SqItem,
            rightItem: SqItem,
        ): SqTwoValuesComparisonImpl<JAVA> {
            return SqTwoValuesComparisonImpl(reader, leftItem, OPERATION_KEYWORD__GT, rightItem)
        }

        override fun <JAVA : Boolean?> createGte(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Boolean>,
            leftItem: SqItem,
            rightItem: SqItem,
        ): SqTwoValuesComparisonImpl<JAVA> {
            return SqTwoValuesComparisonImpl(reader, leftItem, OPERATION_KEYWORD__GTE, rightItem)
        }

        override fun <JAVA : Boolean?> createLt(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Boolean>,
            leftItem: SqItem,
            rightItem: SqItem,
        ): SqTwoValuesComparisonImpl<JAVA> {
            return SqTwoValuesComparisonImpl(reader, leftItem, OPERATION_KEYWORD__LT, rightItem)
        }

        override fun <JAVA : Boolean?> createLte(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Boolean>,
            leftItem: SqItem,
            rightItem: SqItem,
        ): SqTwoValuesComparisonImpl<JAVA> {
            return SqTwoValuesComparisonImpl(reader, leftItem, OPERATION_KEYWORD__LTE, rightItem)
        }

        override fun <JAVA : Boolean?> createLike(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Boolean>,
            leftItem: SqItem,
            rightItem: SqItem,
        ): SqTwoValuesComparisonImpl<JAVA> {
            return SqTwoValuesComparisonImpl(reader, leftItem, OPERATION_KEYWORD__LIKE, rightItem)
        }

        override fun <JAVA : Boolean?> createNotLike(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Boolean>,
            leftItem: SqItem,
            rightItem: SqItem,
        ): SqTwoValuesComparisonImpl<JAVA> {
            return SqTwoValuesComparisonImpl(reader, leftItem, OPERATION_KEYWORD__NOT_LIKE, rightItem)
        }
    }
}
// endregion


// region BETWEEN, NOT BETWEEN
open class SqBetweenImpl<JAVA: Boolean?>(
    override val reader: SqDataTypeReader<JAVA, Boolean>,
    override val testedItem: SqItem,
    override val negative: Boolean,
    override val rangeStart: SqItem,
    override val rangeEnd: SqItem,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqBetween<JAVA> {
    open class Factory: SqBetweenFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }


        override fun <JAVA : Boolean?> create(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Boolean>,
            testedItem: SqItem,
            negative: Boolean,
            rangeStart: SqItem,
            rangeEnd: SqItem
        ): SqBetweenImpl<JAVA> {
            return SqBetweenImpl(reader, testedItem, negative, rangeStart, rangeEnd)
        }
    }
}
// endregion


// region IN, NOT IN
open class SqInImpl<JAVA: Boolean?>(
    override val reader: SqDataTypeReader<JAVA, Boolean>,
    override val testedItem: SqItem,
    override val negative: Boolean,
    override val values: List<SqItem>,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqIn<JAVA> {
    open class Factory: SqInFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }


        override fun <JAVA : Boolean?> create(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Boolean>,
            testedItem: SqItem,
            negative: Boolean,
            values: List<SqItem>
        ): SqInImpl<JAVA> {
            if (values.isEmpty()) {
                error("Value list is empty")
            }
            return SqInImpl(reader, testedItem, negative, values)
        }
    }
}
// endregion


// region EXISTS
open class SqExistsImpl(
    override val reader: SqDataTypeReader<Boolean, Boolean>,
    override val request: SqItem,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqExists {
    open class Factory: SqExistsFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun invoke(context: SqContext, request: SqItem): SqExistsImpl =
            SqExistsImpl(context.settings.booleanOperationReaderPair.notNullReader, request)
    }
}
// endregion
