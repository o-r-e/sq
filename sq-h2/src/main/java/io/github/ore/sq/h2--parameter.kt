@file:Suppress("unused")

package io.github.ore.sq


// region Simple parameter
open class SqH2ParameterNs(
    override val context: SqH2Context,
): SqParameterNs(context) {
    override val dataTypes: SqH2DataTypes
        get() = this.settings.h2DataTypes
}

val SqH2Context.parameters: SqH2ParameterNs
    get() {
        return this.getValue(SqH2ParameterNs::class.java) ?: run {
            val result = SqH2ParameterNs(this)
            this.setValue(SqH2ParameterNs::class.java, result)
            result
        }
    }
// endregion


// region Thread parameter
open class SqH2ThreadParameterNs(
    override val context: SqH2Context,
): SqThreadParameterNs(context) {
    override val dataTypes: SqH2DataTypes
        get() = this.settings.h2DataTypes
}

val SqH2Context.threadParameters: SqH2ThreadParameterNs
    get() {
        return this.getValue(SqH2ThreadParameterNs::class.java) ?: run {
            val result = SqH2ThreadParameterNs(this)
            this.setValue(SqH2ThreadParameterNs::class.java, result)
            result
        }
    }
// endregion
