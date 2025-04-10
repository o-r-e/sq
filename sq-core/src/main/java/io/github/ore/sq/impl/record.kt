@file:Suppress("unused")

package io.github.ore.sq.impl

import io.github.ore.sq.SqRecordFieldDelegate
import io.github.ore.sq.SqTableColumn
import io.github.ore.sq.util.SqValueWrap


// region Record field delegate
open class SqRecordFieldDelegateImpl<T>(
    override val column: SqTableColumn<T, *>,
    override val isPrimaryKey: Boolean,
): SqRecordFieldDelegate<T> {
    protected open var valueWrap: SqValueWrap<T>? = null

    override val hasValue: Boolean
        get() = (this.valueWrap != null)

    override fun get(): T {
        val valueWrap = this.valueWrap
            ?: error("Record field delegate for column ${this.column} has not been initialized yet")
        return valueWrap.value
    }

    override fun set(value: T): T? {
        val result = this.valueWrap?.value
        this.valueWrap = SqValueWrap(value)
        return result
    }

    override fun dropValue() {
        this.valueWrap = null
    }
}
// endregion
