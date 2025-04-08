@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqDataTypesImpl
import io.github.ore.sq.impl.SqH2ContextImpl
import io.github.ore.sq.impl.SqH2ExpressionSelectImpl
import io.github.ore.sq.impl.SqH2SelectImpl
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


object SqH2 {
    fun <T: SqSettingsBuilder> init(target: T): T {
        target
            .contextFactory(SqH2ContextImpl.Factory.INSTANCE)
            .dataTypes(SqDataTypesImpl.INSTANCE)
            .selectFactory(SqH2SelectImpl.Factory.INSTANCE).expressionSelectFactory(SqH2ExpressionSelectImpl.Factory.INSTANCE)
        return target
    }

    @Volatile
    var defaultSettings: SqSettings = sqSettings {
        @Suppress("RemoveRedundantQualifierName")
        SqH2.init(this)
    }

    inline fun <T: SqSettingsBuilder> defaultSettings(builder: T, block: T.() -> Unit): SqSettings {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

        block.invoke(builder)

        val result = builder.create()
        @Suppress("RemoveRedundantQualifierName")
        SqH2.defaultSettings = result
        return result
    }

    inline fun defaultSettings(block: SqSettingsBuilder.() -> Unit): SqSettings {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
        return defaultSettings(SqSettingsBuilder(), block)
    }
}
