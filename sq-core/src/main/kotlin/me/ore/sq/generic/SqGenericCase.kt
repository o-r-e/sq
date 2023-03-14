package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericCase<JAVA: Any?, DB: Any>(
    override val context: SqContext,
    override val type: SqType<JAVA & Any>,
    nullable: Boolean,
    items: Iterable<SqCaseItem<JAVA, DB>>,
    override val elseItem: SqExpression<JAVA, DB>?,
): SqCase<JAVA, DB> {
    // region Nullable
    @Suppress("PropertyName")
    protected open var _nullable = nullable

    override val nullable: Boolean
        get() = this._nullable

    override fun nullable(): SqExpression<JAVA?, DB> {
        this._nullable = true
        return SqUtil.uncheckedCast(this)
    }
    // endregion

    override val items: List<SqCaseItem<JAVA, DB>> = items.toList()

    override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
        val startSpaceAllowed = if (asTextPart) {
            target.add("(", spaced = spaceAllowed)
            false
        } else {
            spaceAllowed
        }

        target.add("CASE", spaced = startSpaceAllowed)
        this.items.forEach { it.appendTo(target, asTextPart = true, spaceAllowed = true) }
        this.elseItem?.let { elseItem ->
            target.add("ELSE", spaced = true)
            elseItem.appendTo(target, asTextPart = true, spaceAllowed = true)
        }
        target.add("END", spaced = true)

        if (asTextPart) target.add(")", spaced = false)
    }

    override fun parameters(): List<SqParameter<*, *>>? = SqUtil.collectParameters(buildList {
        this.addAll(this@SqGenericCase.items)
        this@SqGenericCase.elseItem?.let {
            this.add(it)
        }
    })
}
