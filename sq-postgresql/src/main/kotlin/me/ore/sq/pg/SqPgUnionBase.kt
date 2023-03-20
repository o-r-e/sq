package me.ore.sq.pg

import me.ore.sq.SqParameter
import me.ore.sq.SqWriter


abstract class SqPgUnionBase: SqPgUnion {
    @Suppress("PropertyName")
    protected open var _firstResultIndex: SqParameter<Long, Number>? = null
    override val firstResultIndex: SqParameter<Long, Number>?
        get() = this._firstResultIndex

    override fun firstResultIndex(firstResultIndex: SqParameter<Long, Number>?): SqPgUnionBase = this.apply {
        this._firstResultIndex = firstResultIndex
    }

    override fun firstResultIndex(firstResultIndex: Long?): SqPgUnionBase {
        val param = if (firstResultIndex == null) {
            null
        } else {
            SqParameter<Long, Number>(SqPgTypes.BIG_INT.sqCast(), firstResultIndex, this.context)
        }
        return this.firstResultIndex(param)
    }


    @Suppress("PropertyName")
    protected open var _resultCount: SqParameter<Long, Number>? = null
    override val resultCount: SqParameter<Long, Number>?
        get() = this._resultCount

    override fun resultCount(resultCount: SqParameter<Long, Number>?): SqPgUnionBase = this.apply {
        this._resultCount = resultCount
    }

    override fun resultCount(resultCount: Long?): SqPgUnionBase {
        val param = if (resultCount == null) {
            null
        } else {
            SqParameter<Long, Number>(SqPgTypes.BIG_INT.sqCast(), resultCount, this.context)
        }
        return this.resultCount(param)
    }


    override fun limit(resultCount: SqParameter<Long, Number>, firstResultIndex: SqParameter<Long, Number>?): SqPgUnionBase =
        this.resultCount(resultCount).firstResultIndex(firstResultIndex)
    override fun limit(resultCount: Long, firstResultIndex: Long?): SqPgUnionBase =
        this.resultCount(resultCount).firstResultIndex(firstResultIndex)


    override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
        val internalSpaceAllowed = if (asTextPart) {
            target.add("(", spaced = spaceAllowed)
            false
        } else {
            spaceAllowed
        }

        super.appendTo(target, asTextPart = false, spaceAllowed = internalSpaceAllowed)

        this.firstResultIndex.let { firstResultIndex ->
            this.resultCount.let { resultCount ->
                if ((firstResultIndex != null) || (resultCount != null)) {
                    target.ls()

                    var resultCountAdded = false
                    resultCount?.let {
                        target.add("OFFSET ", spaced = false)
                        resultCount.appendTo(target, asTextPart = true, spaceAllowed = false)
                        resultCountAdded = true
                    }

                    firstResultIndex?.let {
                        target.add("LIMIT ", spaced = resultCountAdded)
                        firstResultIndex.appendTo(target, asTextPart = true, spaceAllowed = false)
                        true
                    }
                }
            }
        }

        if (asTextPart) target.add(")", spaced = false)
    }


    @Suppress("DuplicatedCode")
    override fun parameters(): List<SqParameter<*, *>>? {
        val resultCount = this.resultCount
        val firstResultIndex = this.firstResultIndex
        val parameters = super.parameters()

        return if ((resultCount == null) && (firstResultIndex == null)) {
            parameters
        } else {
            val resultCapacity = (parameters?.size ?: 0) + 2
            buildList(resultCapacity) {
                if (!parameters.isNullOrEmpty()) {
                    this.addAll(parameters)
                }
                if (resultCount != null) {
                    this.add(resultCount)
                }
                if (firstResultIndex != null) {
                    this.add(firstResultIndex)
                }
            }
        }
    }
}
