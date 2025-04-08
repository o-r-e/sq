@file:Suppress("unused")

package io.github.ore.sq


open class SqH2NullNs(
    override val context: SqH2Context,
): SqNullNs(context) {
    override val dataTypes: SqH2DataTypes
        get() = this.settings.h2DataTypes
}

val SqH2Context.nulls: SqH2NullNs
    get() {
        return this.getValue(SqH2NullNs::class.java) ?: run {
            val result = SqH2NullNs(this)
            this.setValue(SqH2NullNs::class.java, result)
            result
        }
    }
