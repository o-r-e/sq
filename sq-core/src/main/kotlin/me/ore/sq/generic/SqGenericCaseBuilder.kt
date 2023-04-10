package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericCaseBuilder<JAVA: Any?, DB: Any>(
    val context: SqContext,
    val type: SqType<JAVA, DB>,
    caseItems: List<SqCaseItem<out JAVA, DB>>?,
    open var elseItem: SqExpression<out JAVA, DB>?,
) {
    companion object {
        val CONSTRUCTOR__START_UNTYPED: SqCaseBuildStartUntypedConstructor = object : SqCaseBuildStartUntypedConstructor {
            override fun createCaseBuildStartUntyped(context: SqContext): SqCaseBuildStartUntyped {
                return StartUntyped(context)
            }
        }

        val CONSTRUCTOR__MIDDLE: SqCaseBuildMiddleConstructor = object : SqCaseBuildMiddleConstructor {
            override fun <JAVA, DB : Any> createCaseBuildMiddle(
                context: SqContext,
                type: SqType<JAVA, DB>
            ): SqCaseBuildMiddle<JAVA, DB> {
                return SqGenericCaseBuilder(context, type, caseItems = null, elseItem = null).middle
            }
        }
    }


    // region "Untyped" classes
    open class StartUntyped(
        override val context: SqContext,
    ): SqCaseBuildStartUntyped {
        override fun startWhen(condition: SqExpression<*, Boolean>): SqCaseBuildItemStartUntyped =
            ItemStartUntyped(context, condition)

        override fun <JAVA, DB : Any> startElse(value: SqExpression<JAVA, DB>): SqCaseBuildEnd<JAVA, DB> =
            SqGenericCaseBuilder(this.context, value.type, caseItems = null, elseItem = value).end

        override fun <JAVA, DB : Any> end(type: SqType<JAVA, DB>): SqCase<JAVA, DB> =
            SqGenericCaseBuilder(this.context, type, caseItems = null, elseItem = null).build()
    }

    open class ItemStartUntyped(
        override val context: SqContext,
        override val whenItem: SqExpression<*, Boolean>,
    ): SqCaseBuildItemStartUntyped {
        override fun <JAVA, DB : Any> addThen(value: SqExpression<JAVA, DB>): SqCaseBuildMiddle<JAVA, DB> {
            val caseItem = this.context.caseItem(this.whenItem, value)
            val build = SqGenericCaseBuilder(this.context, value.type, caseItems = listOf(caseItem), elseItem = null)
            return build.middle
        }
    }
    // endregion


    // region "Typed" classes
    open class ItemStartTyped<JAVA: Any?, DB: Any>(
        open val owner: SqGenericCaseBuilder<JAVA, DB>,
        override val whenItem: SqExpression<*, Boolean>,
    ): SqCaseBuildItemStartTyped<JAVA, DB> {
        override val context: SqContext
            get() = this.owner.context
        override val type: SqType<JAVA, DB>
            get() = this.owner.type

        override fun addThenNotNull(value: SqExpression<out JAVA, DB>): SqCaseBuildMiddle<JAVA, DB> {
            val caseItem = this.context.caseItem(this.whenItem, value)
            this.owner.caseItems.add(caseItem)
            return this.owner.middle
        }

        override fun addThenNullable(value: SqExpression<out JAVA?, DB>): SqCaseBuildMiddle<JAVA?, DB> {
            val caseItem = this.context.caseItem(this.whenItem, value)
            val nullableOwner = this.owner.nullable()
            nullableOwner.caseItems.add(caseItem)
            return nullableOwner.middle
        }
    }

    open class Middle<JAVA: Any?, DB: Any>(
        open val owner: SqGenericCaseBuilder<JAVA, DB>,
    ): SqCaseBuildMiddle<JAVA, DB> {
        override val context: SqContext
            get() = this.owner.context
        override val types: SqType<JAVA, DB>
            get() = this.owner.type

        override fun startWhen(condition: SqExpression<*, Boolean>): SqCaseBuildItemStartTyped<JAVA, DB> =
            ItemStartTyped(this.owner, condition)

        override fun startElseNotNull(value: SqExpression<out JAVA, DB>): SqCaseBuildEnd<JAVA, DB> =
            this.owner.end

        override fun startElseNullable(value: SqExpression<out JAVA?, DB>): SqCaseBuildEnd<JAVA?, DB> =
            this.owner.nullable().end

        override fun end(): SqCase<JAVA?, DB> = this.owner.nullable().build()
    }

    open class End<JAVA: Any?, DB: Any>(
        open val owner: SqGenericCaseBuilder<JAVA, DB>,
    ): SqCaseBuildEnd<JAVA, DB> {
        override val context: SqContext
            get() = this.owner.context
        override val type: SqType<JAVA, DB>
            get() = this.owner.type

        override fun end(): SqCase<JAVA, DB> = this.owner.build()
    }
    // endregion


    // region Data
    open val caseItems: MutableList<SqCaseItem<out JAVA, DB>> = run {
        if (caseItems == null) {
            ArrayList()
        } else {
            ArrayList(caseItems)
        }
    }

    @Suppress("PropertyName")
    protected open var _end: End<JAVA, DB>? = null
    open val end: End<JAVA, DB>
        get() {
            return this._end ?: run {
                val result = End(this)
                this._end = result
                result
            }
        }

    @Suppress("PropertyName")
    protected open var _middle: Middle<JAVA, DB>? = null
    open val middle: Middle<JAVA, DB>
        get() {
            return this._middle ?: run {
                val result = Middle(this)
                this._middle = result
                result
            }
        }

    fun nullable(): SqGenericCaseBuilder<JAVA?, DB> {
        if (this.type.nullable) {
            @Suppress("UNCHECKED_CAST")
            return (this as SqGenericCaseBuilder<JAVA?, DB>)
        }

        return SqGenericCaseBuilder(this.context, this.type.nullable(), this.caseItems, this.elseItem)
    }

    fun build(): SqCase<JAVA, DB> = this.context.case(this.type, this.caseItems, this.elseItem)
    // endregion
}
