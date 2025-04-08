@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqExtendableImpl
import io.github.ore.sq.impl.SqMutableExtendableImpl
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


open class SqSettings(dataMap: Map<Any, Any>? = null): SqExtendableImpl(dataMap) {
    companion object {
        @Volatile
        var default: SqSettings = SqSettings()

        inline fun <T: SqSettingsBuilder> default(builder: T, block: T.() -> Unit): SqSettings {
            contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

            val result = sqSettings(builder, block)
            this.default = result
            return result
        }

        inline fun default(block: SqSettingsBuilder.() -> Unit): SqSettings {
            contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
            return SqSettingsBuilder().use {
                default(this, block)
            }
        }
    }
}

open class SqSettingsBuilder(dataMap: MutableMap<Any, Any> = HashMap()): SqMutableExtendableImpl(dataMap) {
    open fun create(): SqSettings {
        val dataMap = this.dataMap
        return if (dataMap.isEmpty()) {
            SqSettings(null)
        } else {
            SqSettings(HashMap(dataMap))
        }
    }
}


inline fun <T: SqSettingsBuilder> sqSettings(builder: T, block: T.() -> Unit): SqSettings {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    builder.block()
    return builder.create()
}

inline fun sqSettings(block: SqSettingsBuilder.() -> Unit): SqSettings {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return SqSettingsBuilder().use {
        sqSettings(this, block)
    }
}
