package me.ore.sq

import java.sql.PreparedStatement


class SqParameter<JAVA: Any?, DB: Any>(
    override val type: SqType<JAVA & Any>,
    val value: JAVA,
    context: SqContext? = null,
): SqExpression<JAVA?, DB> {
    @Suppress("MemberVisibilityCanBePrivate", "PropertyName")
    protected var _context: SqContext? = context
    override val context: SqContext
        get() = this._context ?: SqContext.CONTEXT


    override val nullable: Boolean
        get() = (this.value == null)

    override fun nullable(): SqParameter<JAVA?, DB> = SqUtil.uncheckedCast(this)


    override fun parameters(): List<SqParameter<*, *>> = listOf(this)


    protected open fun prepareValueForComment(): String = this.type.prepareValueForComment(this.value)

    override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
        target.add("?", spaced = spaceAllowed)

        if (this.context.printParameterValues) {
            target.add("/*", spaced = true)
            target.add(this.prepareValueForComment(), spaced = true)
            target.add("*/", spaced = true)
        }
    }

    fun write(target: PreparedStatement, index: Int) { this.type.write(target, index, this.value) }
}
