@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqNullImpl
import io.github.ore.sq.util.SqItemPartConfig


interface SqNull<JAVA: Any, DB: Any>: SqExpression<JAVA?, DB> {
    override val isMultiline: Boolean
        get() = false

    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        target.keyword("null")
    }
}

interface SqNullFactory {
    fun <JAVA: Any, DB: Any> create(context: SqContext, reader: SqDataTypeReader<JAVA?, DB>): SqNull<JAVA, DB>
}

fun <T: SqSettingsBuilder> T.nullFactory(value: SqNullFactory?): T =
    this.setValue(SqThreadParameterFactory::class.java, value)
val SqSettings.nullFactory: SqNullFactory
    get() = this.getValue(SqNullFactory::class.java) ?: SqNullImpl.Factory.INSTANCE


open class SqNullNs(open val context: SqContext) {
    open val settings: SqSettings
        get() = this.context.settings

    open val dataTypes: SqDataTypes
        get() = this.settings.dataTypes

    open fun <JAVA: Any, DB: Any> nullItem(reader: SqDataTypeReader<JAVA?, DB>): SqNull<JAVA, DB> =
        this.settings.nullFactory.create(this.context, reader)

    open fun <JAVA: Any, DB: Any> nullItem(typePack: SqDataTypePack<JAVA, DB>): SqNull<JAVA, DB> =
        this.nullItem(typePack.nullableReader)
}

val SqContext.nulls: SqNullNs
    get() {
        return this.getValue(SqNullNs::class.java) ?: run {
            val result = SqNullNs(this)
            this.setValue(SqNullNs::class.java, result)
            result
        }
    }
