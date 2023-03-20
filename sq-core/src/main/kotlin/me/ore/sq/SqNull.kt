package me.ore.sq


class SqNull<JAVA: Any, DB: Any>(
    override val type: SqType<JAVA>,
    context: SqContext? = null,
): SqExpression<JAVA?, DB> {
    @Suppress("MemberVisibilityCanBePrivate", "PropertyName")
    protected var _context: SqContext? = context
    override val context: SqContext
        get() = this._context ?: SqContext.CONTEXT


    override val nullable: Boolean
        get() = true

    override fun nullable(): SqNull<JAVA, DB> = this

    override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
        target.add("NULL", spaced = spaceAllowed)
    }

    override fun parameters(): List<SqParameter<*, *>>? = null
}
