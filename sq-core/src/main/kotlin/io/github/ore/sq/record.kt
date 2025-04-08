@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqRecordFieldDelegateImpl
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KProperty


// region Record class
open class SqRecord {
    override fun toString(): String =
        SqRecordClassInfo.toString(this)


    protected open fun <JAVA, DB: Any> SqTableColumn<JAVA, DB>.primaryKeyField(): SqRecordFieldDelegate<JAVA> =
        SqRecordFieldDelegateImpl(this, isPrimaryKey = true)

    protected open fun <JAVA, DB: Any> SqTableColumn<JAVA, DB>.commonField(): SqRecordFieldDelegate<JAVA> =
        SqRecordFieldDelegateImpl(this, isPrimaryKey = false)

    protected open fun <JAVA, DB: Any> SqTableColumn<JAVA, DB>.commonField(initialValue: JAVA): SqRecordFieldDelegate<JAVA> =
        SqRecordFieldDelegateImpl(this, isPrimaryKey = false).apply { this.set(initialValue) }


    protected inline fun forEachDelegate(block: (propertyName: String, delegate: SqRecordFieldDelegate<out Any?>) -> Unit) {
        contract { callsInPlace(block, InvocationKind.UNKNOWN) }
        SqRecordClassInfo[this.javaClass].delegateGetterMap.forEach { (propertyName, delegateGetter) ->
            val delegate = delegateGetter[this]
            block(propertyName, delegate)
        }
    }

    protected inline fun anyDelegate(block: (propertyName: String, delegate: SqRecordFieldDelegate<out Any?>) -> Boolean): SqRecordFieldDelegate<out Any?>? {
        for (entry in SqRecordClassInfo[this.javaClass].delegateGetterMap) {
            val delegate = entry.value[this]
            if (block(entry.key, delegate)) {
                return delegate
            }
        }
        return null
    }

    protected open fun dropPrimaryKeys() {
        this.forEachDelegate { _, delegate ->
            if (delegate.isPrimaryKey) {
                delegate.dropValue()
            }
        }
    }

    protected open fun hasAnyPrimaryKeySet(): Boolean {
        val primaryKeyDelegateWithValue = this.anyDelegate { _, delegate ->
            delegate.isPrimaryKey && delegate.hasValue
        }
        return (primaryKeyDelegateWithValue != null)
    }

    protected open fun hasAllPrimaryKeysSet(): Boolean {
        var hasPrimaryKeyDelegate = false
        val primaryKeyDelegateWithoutValue = this.anyDelegate { _, delegate ->
            if (delegate.isPrimaryKey) {
                hasPrimaryKeyDelegate = true
                !delegate.hasValue
            } else {
                false
            }
        }

        return if (hasPrimaryKeyDelegate) {
            primaryKeyDelegateWithoutValue == null
        } else {
            false
        }
    }
}

fun <T: SqRecord> T.read(source: SqColumnReader, aliases: List<SqColumnSourceAlias>?): T = this.apply {
    SqRecordClassInfo[this.javaClass].read(source, this, aliases)
}

fun <T: SqRecord> T.read(source: SqColumnReader, vararg aliases: SqColumnSourceAlias): T {
    return if (aliases.isEmpty()) {
        this.read(source, null)
    } else {
        this.read(source, aliases.toList())
    }
}


open class SqRecordClass<T: SqRecord> {
    val recordClass: Class<T>


    fun read(source: SqColumnReader, aliases: List<SqColumnSourceAlias>): T =
        SqRecordClassInfo[this.recordClass].read(source, aliases)

    fun read(source: SqColumnReader, vararg aliases: SqColumnSourceAlias): T =
        SqRecordClassInfo[this.recordClass].read(source, *aliases)

    fun mapper(aliases: List<SqColumnSourceAlias>?): SqRecordClassInfo.Mapper<T> =
        SqRecordClassInfo.mapper(this.recordClass, aliases)

    fun mapper(vararg aliases: SqColumnSourceAlias): SqRecordClassInfo.Mapper<T> =
        SqRecordClassInfo.mapper(this.recordClass, *aliases)


    init {
        val enclosingRecordClass = this.javaClass.enclosingClass?.takeIf { SqRecord::class.java.isAssignableFrom(it) }
            ?: error("Class ${this.javaClass.name} must be defined directly inside class, which inherits class ${SqRecord::class.java.name}")
        @Suppress("UNCHECKED_CAST")
        this.recordClass = enclosingRecordClass as Class<T>
        SqRecordClassInfo[this.recordClass]
    }
}
// endregion


// region Record field delegate
interface SqRecordFieldDelegate<T> {
    val column: SqTableColumn<T, *>

    val hasValue: Boolean
    fun get(): T
    fun set(value: T): T?
    fun dropValue()

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
        this.get()
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) =
        this.set(value)

    val isPrimaryKey: Boolean
    val isDataForInsert: Boolean
        get() = (this.isPrimaryKey && this.hasValue) || (!this.isPrimaryKey)
    val isReloadedAfterInsert: Boolean
        get() = (this.isPrimaryKey && (!this.hasValue))
    val isDataForUpdate: Boolean
        get() = (!this.isPrimaryKey)
    val isSearchKeyForUpdate: Boolean
        get() = this.isPrimaryKey
    val isReloadedAfterUpdate: Boolean
        get() = false
    val isSearchKeyForDelete: Boolean
        get() = this.isPrimaryKey
    val isReloadedAfterDelete: Boolean
        get() = (!this.isPrimaryKey)
}
// endregion


// region Record class info
open class SqRecordClassInfo<T: SqRecord>(
    open val recordClass: Class<T>,
    open val propertyNames: List<String>,
    open val delegateFieldMap: Map<String, Field>,
    open val commonFieldMap: Map<String, Field>,
    open val getterMap: Map<String, Method>,
    open val setterMap: Map<String, List<Method>>,
    open val parameterlessConstructor: Constructor<T>?,
    open val delegateGetterMap: Map<String, DelegateGetter<T, *>>,
    open val valueSetterMap: Map<String, ValueSetter<T>>,
    open val valueGetterMap: Map<String, ValueGetter<T, *>>,
) {
    companion object {
        private const val COMMON_FIELD__REQUIRED_MODIFIERS: Int = Modifier.PUBLIC
        private const val COMMON_FIELD__FORBIDDEN_MODIFIERS: Int = Modifier.PROTECTED or Modifier.PRIVATE or Modifier.STATIC or Modifier.FINAL
        private const val METHOD__REQUIRED_MODIFIERS: Int = Modifier.PUBLIC
        private const val METHOD__FORBIDDEN_MODIFIERS: Int = Modifier.PROTECTED or Modifier.PRIVATE or Modifier.ABSTRACT or Modifier.STATIC

        private fun isCommonFieldAllowedByModifiers(field: Field): Boolean {
            val modifiers = field.modifiers
            return (
                ((modifiers and COMMON_FIELD__REQUIRED_MODIFIERS) != 0)
                &&
                ((modifiers and COMMON_FIELD__FORBIDDEN_MODIFIERS) == 0)
            )
        }

        private fun isMethodAllowedByModifiers(method: Method): Boolean {
            val modifiers = method.modifiers
            return (
                ((modifiers and METHOD__REQUIRED_MODIFIERS) != 0)
                &&
                ((modifiers and METHOD__FORBIDDEN_MODIFIERS) == 0)
            )
        }

        private fun isVoid(returnType: Class<*>): Boolean {
            return (
                (returnType == Any::class.java)
                ||
                (returnType == Nothing::class.java)
                ||
                (returnType == Void::class.java)
                ||
                (returnType == Void.TYPE)
            )
        }


        private val LOCK = Object()
        @Volatile
        private var classInfoMap = HashMap<Class<out SqRecord>, SqRecordClassInfo<*>>()

        operator fun <T: SqRecord> get(recordClass: Class<T>): SqRecordClassInfo<T> {
            return if (this.classInfoMap.containsKey(recordClass)) {
                @Suppress("UNCHECKED_CAST")
                this.classInfoMap[recordClass] as SqRecordClassInfo<T>
            } else {
                synchronized(LOCK) {
                    if (this.classInfoMap.containsKey(recordClass)) {
                        @Suppress("UNCHECKED_CAST")
                        this.classInfoMap[recordClass] as SqRecordClassInfo<T>
                    } else {
                        val result = this.createInfo(recordClass)

                        val newMap = HashMap(this.classInfoMap)
                        newMap[recordClass] = result
                        this.classInfoMap = newMap

                        result
                    }
                }
            }
        }

        operator fun <T: SqRecord> set(recordClass: Class<T>, info: SqRecordClassInfo<T>): SqRecordClassInfo<T>? {
            return synchronized(LOCK) {
                val previous = this.classInfoMap[recordClass]

                val newMap = HashMap(this.classInfoMap)
                newMap[recordClass] = info
                this.classInfoMap = newMap

                @Suppress("UNCHECKED_CAST")
                previous as SqRecordClassInfo<T>?
            }
        }

        private fun <T: SqRecord> createInfo(recordClass: Class<T>): SqRecordClassInfo<T> {
            // Get info of super class
            val superClassInfo = run {
                val superClass = recordClass.superclass
                if ((superClass == SqRecord::class.java) || (superClass == null) || (!SqRecord::class.java.isAssignableFrom(superClass))) {
                    null
                } else {
                    this[superClass as Class<out SqRecord>]
                }
            }


            val propertyNameSet: MutableSet<String>
            val delegateFieldMap: MutableMap<String, Field>
            val commonFieldMap: MutableMap<String, Field>
            val getterMap: MutableMap<String, Method>
            val setterMap: MutableMap<String, MutableList<Method>>

            // region Copy info of super class, if present
            if (superClassInfo == null) {
                propertyNameSet = LinkedHashSet()
                delegateFieldMap = HashMap()
                commonFieldMap = HashMap()
                getterMap = HashMap()
                setterMap = HashMap()
            } else {
                propertyNameSet = LinkedHashSet(superClassInfo.propertyNames)
                delegateFieldMap = HashMap(superClassInfo.delegateFieldMap)
                commonFieldMap = HashMap(superClassInfo.commonFieldMap)
                getterMap = HashMap(superClassInfo.getterMap)
                setterMap = superClassInfo.setterMap.let { originalMap ->
                    val setterMap = HashMap<String, MutableList<Method>>(originalMap.size)
                    originalMap.forEach { (propertyName, originalMethods) ->
                        setterMap[propertyName] = ArrayList(originalMethods)
                    }
                    setterMap
                }
            }
            // endregion


            // Scan declared fields
            recordClass.declaredFields.forEach { field ->
                val modifiers = field.modifiers
                if (!Modifier.isStatic(modifiers)) {
                    val name = field.name
                    val dollarPosition = name.lastIndexOf('$')
                    if (dollarPosition > 0) {
                        // Util field
                        if (name.endsWith("\$delegate")) {
                            val propertyName = name.substring(0, dollarPosition)
                            propertyNameSet.add(propertyName)
                            delegateFieldMap[propertyName] = field
                            field.isAccessible = true
                        }
                    } else {
                        // Common field
                        if (this.isCommonFieldAllowedByModifiers(field)) {
                            propertyNameSet.add(name)
                            commonFieldMap[name] = field
                        }
                    }
                }
            }

            // Scan declared methods
            recordClass.declaredMethods.forEach { method ->
                if (isMethodAllowedByModifiers(method)) {
                    val name = method.name
                    if (name.startsWith("get")) {
                        if ((name.length > 3) && (method.parameterCount == 0)) {
                            val returnType = method.returnType
                            if (!this.isVoid(returnType)) {
                                val firstChar = name[3]
                                if (firstChar.isLetter() && firstChar.isUpperCase()) {
                                    val propertyName = if (name.length > 4) {
                                        firstChar.lowercase() + name.substring(4)
                                    } else {
                                        firstChar.lowercase().toString()
                                    }
                                    propertyNameSet.add(propertyName)
                                    getterMap[propertyName] = method
                                }
                            }
                        }
                    } else if (name.startsWith("set")) {
                        if ((name.length > 3) && (method.parameterCount == 1)) {
                            val parameterType = method.parameterTypes[0]
                            if (!this.isVoid(parameterType)) {
                                val firstChar = name[3]
                                if (firstChar.isLetter() && firstChar.isUpperCase()) {
                                    val propertyName = if (name.length > 4) {
                                        firstChar.lowercase() + name.substring(4)
                                    } else {
                                        firstChar.lowercase().toString()
                                    }
                                    propertyNameSet.add(propertyName)
                                    setterMap.computeIfAbsent(propertyName) { ArrayList() }.add(0, method)
                                }
                            }
                        }
                    } else if (name.startsWith("is")) {
                        if ((name.length > 2) && (method.parameterCount == 0)) {
                            val returnType = method.returnType
                            if ((returnType == Boolean::class.java) || (returnType == Boolean::class.javaPrimitiveType)) {
                                val firstChar = name[2]
                                if (firstChar.isLetter() && firstChar.isUpperCase()) {
                                    val propertyName = if (name.length > 3) {
                                        firstChar.lowercase() + name.substring(3)
                                    } else {
                                        firstChar.lowercase().toString()
                                    }
                                    propertyNameSet.add(propertyName)
                                    getterMap[propertyName] = method
                                }
                            }
                        }
                    }
                }
            }


            val parameterlessConstructor = try {
                val parameterlessConstructor = recordClass.getDeclaredConstructor()
                parameterlessConstructor.isAccessible = true
                parameterlessConstructor
            } catch (_: NoSuchMethodException) {
                null
            }


            val columnGetterMap = HashMap<String, DelegateGetter<T, *>>(propertyNameSet.size)
            val valueGetterMap = HashMap<String, ValueGetter<T, *>>(propertyNameSet.size)
            val valueSetterMap = HashMap<String, ValueSetter<T>>(propertyNameSet.size)
            // Define parts using "reflection" fields and methods
            propertyNameSet.forEach { propertyName ->
                val delegateField = delegateFieldMap[propertyName]
                val commonField = commonFieldMap[propertyName]
                val getter = getterMap[propertyName]

                fun chooseAndStoreSetter() {
                    val setter = setterMap[propertyName]?.firstOrNull()
                    @Suppress("DuplicatedCode")
                    if (setter != null) {
                        valueSetterMap[propertyName] = ValueSetter.MethodBasedSetter<T>(setter)
                        return
                    }

                    if (commonField != null) {
                        valueSetterMap[propertyName] = ValueSetter.CommonFieldBasedSetter<T>(commonField)
                        return
                    }

                    if (delegateField != null) {
                        valueSetterMap[propertyName] = ValueSetter.DelegateFieldBasedSetter<T>(delegateField)
                        return
                    }
                }

                fun chooseAndStoreSetter(type: Class<*>) {
                    val setter = setterMap[propertyName]?.firstOrNull { it.parameterTypes[0] == type }
                    @Suppress("DuplicatedCode")
                    if (setter != null) {
                        valueSetterMap[propertyName] = ValueSetter.MethodBasedSetter<T>(setter)
                        return
                    }

                    if (commonField != null) {
                        valueSetterMap[propertyName] = ValueSetter.CommonFieldBasedSetter<T>(commonField)
                        return
                    }

                    if (delegateField != null) {
                        valueSetterMap[propertyName] = ValueSetter.DelegateFieldBasedSetter<T>(delegateField)
                        return
                    }
                }

                // Apply getter and common field
                if (getter == null) {
                    if (commonField == null) {
                        if (delegateField == null) {
                            // No action possible
                        } else {
                            columnGetterMap[propertyName] = DelegateGetter.GetterImpl<T, Any?>(delegateField)
                            valueGetterMap[propertyName] = ValueGetter.DelegateFieldBasedGetter<T, Any?>(delegateField)
                            chooseAndStoreSetter()
                        }
                    } else {
                        valueGetterMap[propertyName] = ValueGetter.CommonFieldBasedGetter<T, Any?>(commonField)
                        chooseAndStoreSetter(commonField.type)
                        if (delegateField != null) {
                            columnGetterMap[propertyName] = DelegateGetter.GetterImpl<T, Any?>(delegateField)
                        }
                    }
                } else {
                    valueGetterMap[propertyName] = ValueGetter.MethodBasedGetter<T, Any?>(getter)
                    chooseAndStoreSetter(getter.returnType)
                    if (delegateField != null) {
                        columnGetterMap[propertyName] = DelegateGetter.GetterImpl<T, Any?>(delegateField)
                    }
                }
            }


            val propertyNames = ArrayList(propertyNameSet)
            propertyNameSet.clear()

            return SqRecordClassInfo(
                recordClass = recordClass,
                propertyNames = propertyNames,
                delegateFieldMap = delegateFieldMap,
                commonFieldMap = commonFieldMap,
                getterMap = getterMap,
                setterMap = setterMap,
                parameterlessConstructor = parameterlessConstructor,
                delegateGetterMap = columnGetterMap,
                valueSetterMap = valueSetterMap,
                valueGetterMap = valueGetterMap,
            )
        }


        fun <T: SqRecord> read(recordClass: Class<T>, source: SqColumnReader, aliases: List<SqColumnSourceAlias>?): T =
            this[recordClass].read(source, aliases)

        fun <T: SqRecord> read(recordClass: Class<T>, source: SqColumnReader, vararg aliases: SqColumnSourceAlias): T =
            this[recordClass].read(source, *aliases)

        fun <T: SqRecord> mapper(recordClass: Class<T>, aliases: List<SqColumnSourceAlias>?): Mapper<T> =
            this[recordClass].mapper(aliases)

        fun <T: SqRecord> mapper(recordClass: Class<T>, vararg aliases: SqColumnSourceAlias): Mapper<T> =
            this[recordClass].mapper(*aliases)


        fun <T: SqRecord> toString(record: T): String {
            val recordClass = record.javaClass
            val recordClassInfo = this[recordClass]
            val delegateGetterMap = recordClassInfo.delegateGetterMap
            return buildString {
                this.append(recordClass.simpleName).append('(')

                var first = true
                recordClassInfo.propertyNames.forEach { propertyName ->
                    recordClassInfo.valueGetterMap[propertyName]?.let { valueGetter ->
                        if (first) {
                            first = false
                        } else {
                            this.append(", ")
                        }

                        val delegateGetter = delegateGetterMap[propertyName]
                        val hasValue = if (delegateGetter == null) {
                            true
                        } else {
                            delegateGetter[record].hasValue
                        }

                        this.append(propertyName).append("=")
                        if (hasValue) {
                            this.append(valueGetter.getForLog(record))
                        } else {
                            this.append("<value not set>")
                        }
                    }
                }

                this.append(')')
            }
        }
    }


    // region Properties and methods of record class info
    protected open val lock = Object()


    override fun toString(): String =
        "${this.javaClass.simpleName}<${this.recordClass.name}>"


    fun create(): T {
        val parameterlessConstructor = this.parameterlessConstructor
            ?: error("Class ${this.recordClass.name} has no constructor without parameters")
        return parameterlessConstructor.newInstance()
    }

    fun <R: T> read(source: SqColumnReader, target: R, aliases: List<SqColumnSourceAlias>?): R {
        this.propertyNames.forEach { propertyName ->
            val delegateGetter = this.delegateGetterMap[propertyName]
            if (delegateGetter != null) {
                val valueSetter = this.valueSetterMap[propertyName]
                if (valueSetter != null) {
                    var column: SqColumn<out Any?, *> = delegateGetter[target].column

                    if (aliases != null) {
                        for (alias in aliases) {
                            column = alias[column]
                        }
                    }

                    val value = source[column]
                    valueSetter[target] = value
                }
            }
        }

        return target
    }

    fun read(source: SqColumnReader, aliases: List<SqColumnSourceAlias>?): T =
        this.read(source, this.create(), aliases)


    protected open var directMapper: Mapper<T>? = null

    protected open fun directMapper(): Mapper<T> {
        return synchronized(this.lock) {
            this.directMapper ?: run {
                val result = Mapper(this, null)
                this.directMapper = result
                result
            }
        }
    }

    fun mapper(aliases: List<SqColumnSourceAlias>?): Mapper<T> {
        return if ((aliases == null) || aliases.isEmpty()) {
            this.directMapper()
        } else {
            Mapper(this, aliases)
        }
    }
    // endregion


    // region Util/helper classes
    abstract class DelegateGetter<T: SqRecord, JAVA> {
        abstract operator fun get(source: T): SqRecordFieldDelegate<JAVA>


        open class GetterImpl<T: SqRecord, JAVA>(
            open val delegateField: Field,
        ): DelegateGetter<T, JAVA>() {
            override fun get(source: T): SqRecordFieldDelegate<JAVA> {
                val delegateField = this.delegateField

                val delegateObject = delegateField.get(source)
                    ?: error("Got NULL delegate from record of class ${source.javaClass.name} using field <$delegateField>")

                @Suppress("UNCHECKED_CAST")
                return (delegateObject as? SqRecordFieldDelegate<JAVA>)
                    ?: error(buildString {
                        this
                            .append("Got invalid delegate of class ")
                            .append(delegateObject.javaClass.name)
                            .append(" from record of class ")
                            .append(source.javaClass.name)
                            .append(" using field <")
                            .append(delegateField)
                            .append(">; delegate must be instance of class ")
                            .append(SqRecordFieldDelegate::class.java.name)
                    })
            }
        }
    }

    abstract class ValueGetter<T: SqRecord, JAVA> {
        companion object {
            const val VALUE_LOG_TEXT_MAX_LENGTH: Int = 100
        }


        abstract operator fun get(source: T): JAVA

        open fun getForLog(source: T): String =
            this.convert(this[source])

        protected open fun convert(value: Any?): String {
            if (value == null) {
                return "<NULL>"
            }

            val stringValue = value.toString()
            return if (stringValue.length > VALUE_LOG_TEXT_MAX_LENGTH) {
                "${stringValue.substring(0, VALUE_LOG_TEXT_MAX_LENGTH - 3)}..."
            } else {
                stringValue
            }
        }


        open class MethodBasedGetter<T: SqRecord, JAVA>(open val method: Method): ValueGetter<T, JAVA>() {
            override fun get(source: T): JAVA {
                @Suppress("UNCHECKED_CAST")
                return (this.method.invoke(source) as JAVA)
            }
        }

        open class CommonFieldBasedGetter<T: SqRecord, JAVA>(open val field: Field): ValueGetter<T, JAVA>() {
            override fun get(source: T): JAVA {
                @Suppress("UNCHECKED_CAST")
                return (this.field.get(source) as JAVA)
            }
        }

        open class DelegateFieldBasedGetter<T: SqRecord, JAVA>(open val field: Field): ValueGetter<T, JAVA>() {
            override fun get(source: T): JAVA {
                @Suppress("UNCHECKED_CAST")
                val delegate = (field.get(source) as SqRecordFieldDelegate<JAVA>)
                return delegate.get()
            }
        }
    }

    abstract class ValueSetter<T: SqRecord> {
        abstract operator fun set(target: T, value: Any?)


        open class MethodBasedSetter<T: SqRecord>(open val method: Method): ValueSetter<T>() {
            override fun set(target: T, value: Any?) {
                this.method.invoke(target, value)
            }
        }

        open class CommonFieldBasedSetter<T: SqRecord>(open val field: Field): ValueSetter<T>() {
            override fun set(target: T, value: Any?) {
                this.field.set(target, value)
            }
        }

        open class DelegateFieldBasedSetter<T: SqRecord>(open val field: Field): ValueSetter<T>() {
            override fun set(target: T, value: Any?) {
                @Suppress("UNCHECKED_CAST")
                val delegate = (field.get(target) as SqRecordFieldDelegate<Any?>)
                delegate.set(value)
            }
        }
    }

    open class Mapper<T: SqRecord>(
        open val classInfo: SqRecordClassInfo<T>,
        open val aliases: List<SqColumnSourceAlias>?,
    ): SqMappedReader.Mapper<T> {
        override fun invoke(reader: SqMappedReader<T>): T =
            this.classInfo.read(reader, this.aliases)
    }
    // endregion
}

fun <T: SqRecord, R: T> SqRecordClassInfo<T>.read(source: SqColumnReader, target: R, vararg aliases: SqColumnSourceAlias): R {
    return if (aliases.isEmpty()) {
        this.read(source, target, null)
    } else {
        this.read(source, target, aliases.toList())
    }
}

fun <T: SqRecord> SqRecordClassInfo<T>.read(source: SqColumnReader, vararg aliases: SqColumnSourceAlias): T {
    return if (aliases.isEmpty()) {
        this.read(source, null)
    } else {
        this.read(source, aliases.toList())
    }
}

fun <T: SqRecord> SqRecordClassInfo<T>.mapper(vararg aliases: SqColumnSourceAlias): SqRecordClassInfo.Mapper<T> {
    return if (aliases.isEmpty()) {
        this.mapper(null)
    } else {
        this.mapper(aliases.toList())
    }
}
// endregion
