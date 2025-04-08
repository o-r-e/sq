@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqPgContextImpl
import io.github.ore.sq.impl.SqPgDeleteImpl
import io.github.ore.sq.impl.SqPgExpressionSelectImpl
import io.github.ore.sq.impl.SqPgInsertImpl
import io.github.ore.sq.impl.SqPgSelectImpl
import io.github.ore.sq.impl.SqPgUpdateImpl
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


object SqPg {
    fun <T: SqSettingsBuilder> init(target: T): T {
        target
            .contextFactory(SqPgContextImpl.Factory.INSTANCE)
            .deleteFactory(SqPgDeleteImpl.Factory.INSTANCE)
            .insertFactory(SqPgInsertImpl.Factory.INSTANCE)
            .selectFactory(SqPgSelectImpl.Factory.INSTANCE).expressionSelectFactory(SqPgExpressionSelectImpl.Factory.INSTANCE)
            .updateFactory(SqPgUpdateImpl.Factory.INSTANCE)
        return target
    }

    @Volatile
    var defaultSettings: SqSettings = sqSettings {
        @Suppress("RemoveRedundantQualifierName")
        SqPg.init(this)
    }

    inline fun <T: SqSettingsBuilder> defaultSettings(builder: T, block: T.() -> Unit): SqSettings {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

        block.invoke(builder)

        val result = builder.create()
        @Suppress("RemoveRedundantQualifierName")
        SqPg.defaultSettings = result
        return result
    }

    inline fun defaultSettings(block: SqSettingsBuilder.() -> Unit): SqSettings {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
        return defaultSettings(SqSettingsBuilder(), block)
    }
}
