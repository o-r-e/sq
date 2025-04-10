@file:Suppress("unused")

package io.github.ore.sq


open class SqPgNullNs(
    override val context: SqPgContext,
): SqNullNs(context) {
    override val dataTypes: SqPgDataTypes
        get() = this.settings.pgDataTypes
}

val SqPgContext.nulls: SqPgNullNs
    get() {
        return this.getValue(SqPgNullNs::class.java) ?: run {
            val result = SqPgNullNs(this)
            this.setValue(SqPgNullNs::class.java, result)
            result
        }
    }
