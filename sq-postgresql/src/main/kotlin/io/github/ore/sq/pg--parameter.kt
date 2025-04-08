@file:Suppress("unused")

package io.github.ore.sq


// region Simple parameter
open class SqPgParameterNs(
    override val context: SqPgContext,
): SqParameterNs(context) {
    override val dataTypes: SqPgDataTypes
        get() = this.settings.pgDataTypes
}

val SqPgContext.parameters: SqPgParameterNs
    get() {
        return this.getValue(SqPgParameterNs::class.java) ?: run {
            val result = SqPgParameterNs(this)
            this.setValue(SqPgParameterNs::class.java, result)
            result
        }
    }
// endregion


// region Thread parameter
open class SqPgThreadParameterNs(
    override val context: SqPgContext,
): SqThreadParameterNs(context) {
    override val dataTypes: SqPgDataTypes
        get() = this.settings.pgDataTypes
}

val SqPgContext.threadParameters: SqPgThreadParameterNs
    get() {
        return this.getValue(SqPgThreadParameterNs::class.java) ?: run {
            val result = SqPgThreadParameterNs(this)
            this.setValue(SqPgThreadParameterNs::class.java, result)
            result
        }
    }
// endregion
