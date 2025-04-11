@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqParameterImpl
import io.github.ore.sq.impl.SqThreadParameterImpl
import io.github.ore.sq.util.SqItemPartConfig
import io.github.ore.sq.util.SqValueWrap
import java.sql.PreparedStatement
import java.util.WeakHashMap
import kotlin.concurrent.getOrSet
import kotlin.math.max


// region Simple parameter
interface SqParameter<JAVA, DB: Any>: SqExpression<JAVA, DB> {
    val writer: SqDataTypeWriter<JAVA & Any, DB>
    val value: JAVA

    override val isMultiline: Boolean
        get() = false

    override fun addToBuilderWithoutComments(
        context: SqContext,
        target: SqJdbcRequestDataBuilder,
        partConfig: SqItemPartConfig?
    ) {
        target.parameter(this)
    }
}

interface SqParameterFactory {
    fun <JAVA, DB: Any> create(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, DB>,
        writer: SqDataTypeWriter<JAVA & Any, DB>,
        value: JAVA,
    ): SqParameter<JAVA, DB>
}

fun <T: SqSettingsBuilder> T.parameterFactory(value: SqParameterFactory?): T =
    this.setValue(SqParameterFactory::class.java, value)
val SqSettings.parameterFactory: SqParameterFactory
    get() = this.getValue(SqParameterFactory::class.java) ?: SqParameterImpl.Factory.INSTANCE


fun <T: SqParameter<*, *>> T.write(target: PreparedStatement, index: Int): T = this.apply {
    (this.writer as SqDataTypeWriter<Any, Any>)[target, index] = this.value
}

private fun prepareComment(value: Any?, requestedMaxLength: Int): String {
    val actualMaxLength = max(requestedMaxLength, 4)
    val fullComment = (value ?: "NULL").toString()
    return if (fullComment.length > actualMaxLength) {
        "${fullComment.take(actualMaxLength - 3)}..."
    } else {
        fullComment
    }
}

fun <T: SqParameter<*, *>> T.commentValueAtStart(maxLength: Int = 40): T =
    this.commentAtStart(prepareComment(this.value, maxLength))

fun <T: SqParameter<*, *>> T.commentValueAtEnd(maxLength: Int = 40): T =
    this.commentAtEnd(prepareComment(this.value, maxLength))


open class SqParameterNs(open val context: SqContext) {
    open val settings: SqSettings
        get() = this.context.settings

    open val dataTypes: SqDataTypes
        get() = this.settings.dataTypes

    open fun <JAVA, DB: Any> parameter(reader: SqDataTypeReader<JAVA, DB>, writer: SqDataTypeWriter<JAVA & Any, DB>, value: JAVA): SqParameter<JAVA, DB> =
        this.settings.parameterFactory.create(this.context, reader, writer, value)

    @JvmName("parameter__notNull")
    fun <JAVA: Any, DB: Any> parameter(typePack: SqDataTypePack<JAVA, DB>, value: JAVA): SqParameter<JAVA, DB> =
        this.parameter(typePack.notNullReader, typePack.writer, value)

    @JvmName("parameter__nullable")
    fun <JAVA: Any, DB: Any> parameter(typePack: SqDataTypePack<JAVA, DB>, value: JAVA?): SqParameter<JAVA?, DB> =
        this.parameter(typePack.nullableReader, typePack.writer, value)
}

val SqContext.parameters: SqParameterNs
    get() {
        return this.getValue(SqParameterNs::class.java) ?: run {
            val result = SqParameterNs(this)
            this.setValue(SqParameterNs::class.java, result)
            result
        }
    }
// endregion


// region Thread parameter
interface SqThreadParameter<JAVA, DB: Any>: SqParameter<JAVA, DB> {
    companion object {
        private val THREAD_VALUE_MAP_HOLDER = ThreadLocal<WeakHashMap<SqThreadParameter<*, *>, SqValueWrap<*>>>()

        fun <JAVA> getThreadValue(parameter: SqThreadParameter<JAVA, *>): SqValueWrap<JAVA>? {
            val map = THREAD_VALUE_MAP_HOLDER.get()
                ?: return null
            val result = map[parameter]
                ?: return null
            @Suppress("UNCHECKED_CAST")
            return result as SqValueWrap<JAVA>
        }

        fun <JAVA, DB: Any> setThreadValue(parameter: SqThreadParameter<JAVA, DB>, value: SqValueWrap<JAVA>?) {
            if (value == null) {
                val map = THREAD_VALUE_MAP_HOLDER.get()
                    ?: return
                map.remove(parameter)
                if (map.isEmpty()) {
                    THREAD_VALUE_MAP_HOLDER.remove()
                }
            } else {
                THREAD_VALUE_MAP_HOLDER.getOrSet { WeakHashMap() }[parameter] = value
            }
        }
    }


    var threadValue: SqValueWrap<JAVA>?
        get() = getThreadValue(this)
        set(value) = setThreadValue(this, value)

    override val value: JAVA
        get() {
            val threadValue = this.threadValue
            if (threadValue == null) {
                error("Thread parameter <$this> has no value bound to current thread")
            }
            return threadValue.value
        }
}

interface SqThreadParameterFactory {
    fun <JAVA, DB: Any> create(
        context: SqContext,
        reader: SqDataTypeReader<JAVA, DB>,
        writer: SqDataTypeWriter<JAVA & Any, DB>,
    ): SqThreadParameter<JAVA, DB>
}

fun <T: SqSettingsBuilder> T.threadParameterFactory(value: SqThreadParameterFactory?): T =
    this.setValue(SqThreadParameterFactory::class.java, value)
val SqSettings.threadParameterFactory: SqThreadParameterFactory
    get() = this.getValue(SqThreadParameterFactory::class.java) ?: SqThreadParameterImpl.Factory.INSTANCE


fun <JAVA, DB: Any, T: SqThreadParameter<JAVA, DB>> T.threadValue(value: SqValueWrap<JAVA>?): T = this.apply {
    this.threadValue = value
}

fun <JAVA, DB: Any, T: SqThreadParameter<JAVA, DB>> T.threadValue(value: JAVA): T = this.apply {
    this.threadValue = SqValueWrap(value)
}

fun <JAVA, DB: Any, T: SqThreadParameter<JAVA, DB>> T.removeThreadValue(): T = this.apply {
    this.threadValue = null
}


open class SqThreadParameterNs(open val context: SqContext) {
    open val settings: SqSettings
        get() = this.context.settings

    open val dataTypes: SqDataTypes
        get() = this.settings.dataTypes

    open fun <JAVA, DB: Any> parameter(reader: SqDataTypeReader<JAVA, DB>, writer: SqDataTypeWriter<JAVA & Any, DB>): SqThreadParameter<JAVA, DB> =
        this.settings.threadParameterFactory.create(this.context, reader, writer)

    @JvmName("parameter__notNull")
    fun <JAVA: Any, DB: Any> parameter(typePack: SqDataTypePack<JAVA, DB>, nullFlag: Any): SqThreadParameter<JAVA, DB> =
        this.parameter(typePack.notNullReader, typePack.writer)

    @JvmName("parameter__nullable")
    fun <JAVA: Any, DB: Any> parameter(typePack: SqDataTypePack<JAVA, DB>, nullFlag: Any?): SqThreadParameter<JAVA?, DB> =
        this.parameter(typePack.nullableReader, typePack.writer)
}

val SqContext.threadParameters: SqThreadParameterNs
    get() {
        return this.getValue(SqThreadParameterNs::class.java) ?: run {
            val result = SqThreadParameterNs(this)
            this.setValue(SqThreadParameterNs::class.java, result)
            result
        }
    }
// endregion
