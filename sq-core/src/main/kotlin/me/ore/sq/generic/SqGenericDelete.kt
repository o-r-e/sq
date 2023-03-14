package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericDelete<T: SqTable>(
    override val context: SqContext,
    override val table: T,
): SqDelete<T> {
    // region Where
    @Suppress("MemberVisibilityCanBePrivate", "PropertyName")
    protected var _where: SqExpression<*, Boolean>? = null

    override val where: SqExpression<*, Boolean>?
        get() = this._where

    override fun where(condition: SqExpression<*, Boolean>?): SqGenericDelete<T> = this.apply {
        this._where = condition
    }
    // endregion


    override fun appendTo(target: SqWriter, asTextPart: Boolean, spaceAllowed: Boolean) {
        val internalSpaceAllowed = if (asTextPart) {
            target.add("(", spaced = false)
            false
        } else {
            spaceAllowed
        }

        target.add("DELETE FROM", spaced = internalSpaceAllowed)
        this.table.appendTo(target, asTextPart = true, spaceAllowed = true)

        this.where?.let { where ->
            target.ls().add("WHERE ", spaced = false)
            where.appendTo(target, asTextPart = true, spaceAllowed = false)
        }

        if (asTextPart) {
            target.add(")", spaced = false)
        }
    }
}
