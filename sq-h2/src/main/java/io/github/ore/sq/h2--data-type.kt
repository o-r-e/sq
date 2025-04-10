@file:Suppress("unused")

package io.github.ore.sq

import io.github.ore.sq.impl.SqH2DataTypesImpl
import java.io.InputStream
import java.io.Reader
import java.sql.Blob
import java.sql.Clob


// region Type collection
interface SqH2DataTypes: SqDataTypes {
    companion object {
        const val DB_TYPE_NAME__BINARY: String = "BINARY"
        const val DB_TYPE_NAME__BINARY_LARGE_OBJECT: String = "BINARY LARGE OBJECT"
        const val DB_TYPE_NAME__BINARY_VARYING: String = "BINARY VARYING"
        const val DB_TYPE_NAME__CHARACTER: String = "CHARACTER"
        const val DB_TYPE_NAME__CHARACTER_VARYING: String = "CHARACTER VARYING"
        const val DB_TYPE_NAME__CHARACTER_LARGE_OBJECT: String = "CHARACTER LARGE OBJECT"
        const val DB_TYPE_NAME__VAR_CHAR_IGNORE_CASE: String = "VARCHAR_IGNORECASE"
    }


    // region Blob, clob
    val h2BinaryLargeObject: SqDataTypePack<Blob, Blob>
    val h2BinaryLargeObjectStream: SqDataTypePack<InputStream, Blob>
    val h2CharacterLargeObject: SqDataTypePack<Clob, Clob>
    val h2CharacterLargeObjectReader: SqDataTypePack<Reader, Clob>
    // endregion


    // region Byte array
    val h2Binary: SqDataTypePack<ByteArray, ByteArray>
    val h2BinaryVarying: SqDataTypePack<ByteArray, ByteArray>
    // endregion


    // region String
    val h2Character: SqDataTypePack<String, String>
    val h2CharacterVarying: SqDataTypePack<String, String>
    val h2VarCharIgnoreCase: SqDataTypePack<String, String>
    // endregion
}

fun <T: SqSettingsBuilder> T.h2DataTypes(value: SqH2DataTypes?): T =
    this.setValue(SqH2DataTypes::class.java, value)
val SqSettings.h2DataTypes: SqH2DataTypes
    get() = this.getValue(SqH2DataTypes::class.java) ?: SqH2DataTypesImpl.INSTANCE
val SqH2Context.dataTypes: SqH2DataTypes
    get() = this.settings.h2DataTypes
// endregion


// region NULLs / blob, clob
fun SqH2NullNs.h2BinaryLargeObject(): SqNull<Blob, Blob> =
    this.nullItem(this.dataTypes.h2BinaryLargeObject)
fun SqH2NullNs.h2BinaryLargeObjectStream(): SqNull<InputStream, Blob> =
    this.nullItem(this.dataTypes.h2BinaryLargeObjectStream)
fun SqH2NullNs.h2CharacterLargeObject(): SqNull<Clob, Clob> =
    this.nullItem(this.dataTypes.h2CharacterLargeObject)
fun SqH2NullNs.h2CharacterLargeObjectReader(): SqNull<Reader, Clob> =
    this.nullItem(this.dataTypes.h2CharacterLargeObjectReader)
// endregion


// region NULLs / byte array
fun SqH2NullNs.h2Binary(): SqNull<ByteArray, ByteArray> =
    this.nullItem(this.dataTypes.h2Binary)
fun SqH2NullNs.h2BinaryVarying(): SqNull<ByteArray, ByteArray> =
    this.nullItem(this.dataTypes.h2BinaryVarying)
// endregion


// region NULLs / string
fun SqH2NullNs.h2Character(): SqNull<String, String> =
    this.nullItem(this.dataTypes.h2Character)
fun SqH2NullNs.h2CharacterVarying(): SqNull<String, String> =
    this.nullItem(this.dataTypes.h2CharacterVarying)
fun SqH2NullNs.h2VarCharIgnoreCase(): SqNull<String, String> =
    this.nullItem(this.dataTypes.h2VarCharIgnoreCase)
// endregion


// region Parameters / blob, clob
@JvmName("h2BinaryLargeObject__notNull")
fun SqH2ParameterNs.h2BinaryLargeObject(value: Blob): SqParameter<Blob, Blob> =
    this.parameter(this.dataTypes.h2BinaryLargeObject, value)
@JvmName("h2BinaryLargeObject__nullable")
fun SqH2ParameterNs.h2BinaryLargeObject(value: Blob?): SqParameter<Blob?, Blob> =
    this.parameter(this.dataTypes.h2BinaryLargeObject, value)

@JvmName("h2BinaryLargeObjectStream__notNull")
fun SqH2ParameterNs.h2BinaryLargeObjectStream(value: InputStream): SqParameter<InputStream, Blob> =
    this.parameter(this.dataTypes.h2BinaryLargeObjectStream, value)
@JvmName("h2BinaryLargeObjectStream__nullable")
fun SqH2ParameterNs.h2BinaryLargeObjectStream(value: InputStream?): SqParameter<InputStream?, Blob> =
    this.parameter(this.dataTypes.h2BinaryLargeObjectStream, value)

@JvmName("h2CharacterLargeObject__notNull")
fun SqH2ParameterNs.h2CharacterLargeObject(value: Clob): SqParameter<Clob, Clob> =
    this.parameter(this.dataTypes.h2CharacterLargeObject, value)
@JvmName("h2CharacterLargeObject__nullable")
fun SqH2ParameterNs.h2CharacterLargeObject(value: Clob?): SqParameter<Clob?, Clob> =
    this.parameter(this.dataTypes.h2CharacterLargeObject, value)

@JvmName("h2CharacterLargeObjectReader__notNull")
fun SqH2ParameterNs.h2CharacterLargeObjectReader(value: Reader): SqParameter<Reader, Clob> =
    this.parameter(this.dataTypes.h2CharacterLargeObjectReader, value)
@JvmName("h2CharacterLargeObjectReader__nullable")
fun SqH2ParameterNs.h2CharacterLargeObjectReader(value: Reader?): SqParameter<Reader?, Clob> =
    this.parameter(this.dataTypes.h2CharacterLargeObjectReader, value)
// endregion


// region Parameters / byte array
@JvmName("h2Binary__notNull")
fun SqH2ParameterNs.h2Binary(value: ByteArray): SqParameter<ByteArray, ByteArray> =
    this.parameter(this.dataTypes.h2Binary, value)
@JvmName("h2Binary__nullable")
fun SqH2ParameterNs.h2Binary(value: ByteArray?): SqParameter<ByteArray?, ByteArray> =
    this.parameter(this.dataTypes.h2Binary, value)

@JvmName("h2BinaryVarying__notNull")
fun SqH2ParameterNs.h2BinaryVarying(value: ByteArray): SqParameter<ByteArray, ByteArray> =
    this.parameter(this.dataTypes.h2BinaryVarying, value)
@JvmName("h2BinaryVarying__nullable")
fun SqH2ParameterNs.h2BinaryVarying(value: ByteArray?): SqParameter<ByteArray?, ByteArray> =
    this.parameter(this.dataTypes.h2BinaryVarying, value)
// endregion


// region Parameters / string
@JvmName("h2Character__notNull")
fun SqH2ParameterNs.h2Character(value: String): SqParameter<String, String> =
    this.parameter(this.dataTypes.h2Character, value)
@JvmName("h2Character__nullable")
fun SqH2ParameterNs.h2Character(value: String?): SqParameter<String?, String> =
    this.parameter(this.dataTypes.h2Character, value)

@JvmName("h2CharacterVarying__notNull")
fun SqH2ParameterNs.h2CharacterVarying(value: String): SqParameter<String, String> =
    this.parameter(this.dataTypes.h2CharacterVarying, value)
@JvmName("h2CharacterVarying__nullable")
fun SqH2ParameterNs.h2CharacterVarying(value: String?): SqParameter<String?, String> =
    this.parameter(this.dataTypes.h2CharacterVarying, value)

@JvmName("h2VarCharIgnoreCase__notNull")
fun SqH2ParameterNs.h2VarCharIgnoreCase(value: String): SqParameter<String, String> =
    this.parameter(this.dataTypes.h2VarCharIgnoreCase, value)
@JvmName("h2VarCharIgnoreCase__nullable")
fun SqH2ParameterNs.h2VarCharIgnoreCase(value: String?): SqParameter<String?, String> =
    this.parameter(this.dataTypes.h2VarCharIgnoreCase, value)
// endregion


// region Thread parameters / blob, clob
@JvmName("h2BinaryLargeObject__notNull")
fun SqH2ThreadParameterNs.h2BinaryLargeObject(nullFlag: Any): SqThreadParameter<Blob, Blob> =
    this.parameter(this.dataTypes.h2BinaryLargeObject, nullFlag)
@JvmName("h2BinaryLargeObject__nullable")
fun SqH2ThreadParameterNs.h2BinaryLargeObject(nullFlag: Any?): SqThreadParameter<Blob?, Blob> =
    this.parameter(this.dataTypes.h2BinaryLargeObject, nullFlag)

@JvmName("h2BinaryLargeObjectStream__notNull")
fun SqH2ThreadParameterNs.h2BinaryLargeObjectStream(nullFlag: Any): SqThreadParameter<InputStream, Blob> =
    this.parameter(this.dataTypes.h2BinaryLargeObjectStream, nullFlag)
@JvmName("h2BinaryLargeObjectStream__nullable")
fun SqH2ThreadParameterNs.h2BinaryLargeObjectStream(nullFlag: Any?): SqThreadParameter<InputStream?, Blob> =
    this.parameter(this.dataTypes.h2BinaryLargeObjectStream, nullFlag)

@JvmName("h2CharacterLargeObject__notNull")
fun SqH2ThreadParameterNs.h2CharacterLargeObject(nullFlag: Any): SqThreadParameter<Clob, Clob> =
    this.parameter(this.dataTypes.h2CharacterLargeObject, nullFlag)
@JvmName("h2CharacterLargeObject__nullable")
fun SqH2ThreadParameterNs.h2CharacterLargeObject(nullFlag: Any?): SqThreadParameter<Clob?, Clob> =
    this.parameter(this.dataTypes.h2CharacterLargeObject, nullFlag)

@JvmName("h2CharacterLargeObjectReader__notNull")
fun SqH2ThreadParameterNs.h2CharacterLargeObjectReader(nullFlag: Any): SqThreadParameter<Reader, Clob> =
    this.parameter(this.dataTypes.h2CharacterLargeObjectReader, nullFlag)
@JvmName("h2CharacterLargeObjectReader__nullable")
fun SqH2ThreadParameterNs.h2CharacterLargeObjectReader(nullFlag: Any?): SqThreadParameter<Reader?, Clob> =
    this.parameter(this.dataTypes.h2CharacterLargeObjectReader, nullFlag)
// endregion


// region Thread parameters / byte array
@JvmName("h2Binary__notNull")
fun SqH2ThreadParameterNs.h2Binary(nullFlag: Any): SqThreadParameter<ByteArray, ByteArray> =
    this.parameter(this.dataTypes.h2Binary, nullFlag)
@JvmName("h2Binary__nullable")
fun SqH2ThreadParameterNs.h2Binary(nullFlag: Any?): SqThreadParameter<ByteArray?, ByteArray> =
    this.parameter(this.dataTypes.h2Binary, nullFlag)

@JvmName("h2BinaryVarying__notNull")
fun SqH2ThreadParameterNs.h2BinaryVarying(nullFlag: Any): SqThreadParameter<ByteArray, ByteArray> =
    this.parameter(this.dataTypes.h2BinaryVarying, nullFlag)
@JvmName("h2BinaryVarying__nullable")
fun SqH2ThreadParameterNs.h2BinaryVarying(nullFlag: Any?): SqThreadParameter<ByteArray?, ByteArray> =
    this.parameter(this.dataTypes.h2BinaryVarying, nullFlag)
// endregion


// region Thread parameters / string
@JvmName("h2Character__notNull")
fun SqH2ThreadParameterNs.h2Character(nullFlag: Any): SqThreadParameter<String, String> =
    this.parameter(this.dataTypes.h2Character, nullFlag)
@JvmName("h2Character__nullable")
fun SqH2ThreadParameterNs.h2Character(nullFlag: Any?): SqThreadParameter<String?, String> =
    this.parameter(this.dataTypes.h2Character, nullFlag)

@JvmName("h2CharacterVarying__notNull")
fun SqH2ThreadParameterNs.h2CharacterVarying(nullFlag: Any): SqThreadParameter<String, String> =
    this.parameter(this.dataTypes.h2CharacterVarying, nullFlag)
@JvmName("h2CharacterVarying__nullable")
fun SqH2ThreadParameterNs.h2CharacterVarying(nullFlag: Any?): SqThreadParameter<String?, String> =
    this.parameter(this.dataTypes.h2CharacterVarying, nullFlag)

@JvmName("h2VarCharIgnoreCase__notNull")
fun SqH2ThreadParameterNs.h2VarCharIgnoreCase(nullFlag: Any): SqThreadParameter<String, String> =
    this.parameter(this.dataTypes.h2VarCharIgnoreCase, nullFlag)
@JvmName("h2VarCharIgnoreCase__nullable")
fun SqH2ThreadParameterNs.h2VarCharIgnoreCase(nullFlag: Any?): SqThreadParameter<String?, String> =
    this.parameter(this.dataTypes.h2VarCharIgnoreCase, nullFlag)
// endregion


// region Table columns / blob, clob
@JvmName("h2BinaryLargeObject__notNull")
fun SqH2TableColumnHolder.h2BinaryLargeObject(name: String, nullFlag: Any): SqTableColumn<Blob, Blob> =
    this.add(this.types.h2BinaryLargeObject, name, nullFlag)
@JvmName("h2BinaryLargeObject__nullable")
fun SqH2TableColumnHolder.h2BinaryLargeObject(name: String, nullFlag: Any?): SqTableColumn<Blob?, Blob> =
    this.add(this.types.h2BinaryLargeObject, name, nullFlag)

@JvmName("h2BinaryLargeObjectStream__notNull")
fun SqH2TableColumnHolder.h2BinaryLargeObjectStream(name: String, nullFlag: Any): SqTableColumn<InputStream, Blob> =
    this.add(this.types.h2BinaryLargeObjectStream, name, nullFlag)
@JvmName("h2BinaryLargeObjectStream__nullable")
fun SqH2TableColumnHolder.h2BinaryLargeObjectStream(name: String, nullFlag: Any?): SqTableColumn<InputStream?, Blob> =
    this.add(this.types.h2BinaryLargeObjectStream, name, nullFlag)

@JvmName("h2CharacterLargeObject__notNull")
fun SqH2TableColumnHolder.h2CharacterLargeObject(name: String, nullFlag: Any): SqTableColumn<Clob, Clob> =
    this.add(this.types.h2CharacterLargeObject, name, nullFlag)
@JvmName("h2CharacterLargeObject__nullable")
fun SqH2TableColumnHolder.h2CharacterLargeObject(name: String, nullFlag: Any?): SqTableColumn<Clob?, Clob> =
    this.add(this.types.h2CharacterLargeObject, name, nullFlag)

@JvmName("h2CharacterLargeObjectReader__notNull")
fun SqH2TableColumnHolder.h2CharacterLargeObjectReader(name: String, nullFlag: Any): SqTableColumn<Reader, Clob> =
    this.add(this.types.h2CharacterLargeObjectReader, name, nullFlag)
@JvmName("h2CharacterLargeObjectReader__nullable")
fun SqH2TableColumnHolder.h2CharacterLargeObjectReader(name: String, nullFlag: Any?): SqTableColumn<Reader?, Clob> =
    this.add(this.types.h2CharacterLargeObjectReader, name, nullFlag)
// endregion


// region Table columns / byte array
@JvmName("h2Binary__notNull")
fun SqH2TableColumnHolder.h2Binary(name: String, nullFlag: Any): SqTableColumn<ByteArray, ByteArray> =
    this.add(this.types.h2Binary, name, nullFlag)
@JvmName("h2Binary__nullable")
fun SqH2TableColumnHolder.h2Binary(name: String, nullFlag: Any?): SqTableColumn<ByteArray?, ByteArray> =
    this.add(this.types.h2Binary, name, nullFlag)

@JvmName("h2BinaryVarying__notNull")
fun SqH2TableColumnHolder.h2BinaryVarying(name: String, nullFlag: Any): SqTableColumn<ByteArray, ByteArray> =
    this.add(this.types.h2BinaryVarying, name, nullFlag)
@JvmName("h2BinaryVarying__nullable")
fun SqH2TableColumnHolder.h2BinaryVarying(name: String, nullFlag: Any?): SqTableColumn<ByteArray?, ByteArray> =
    this.add(this.types.h2BinaryVarying, name, nullFlag)
// endregion


// region Table columns / string
@JvmName("h2Character__notNull")
fun SqH2TableColumnHolder.h2Character(name: String, nullFlag: Any): SqTableColumn<String, String> =
    this.add(this.types.h2Character, name, nullFlag)
@JvmName("h2Character__nullable")
fun SqH2TableColumnHolder.h2Character(name: String, nullFlag: Any?): SqTableColumn<String?, String> =
    this.add(this.types.h2Character, name, nullFlag)

@JvmName("h2CharacterVarying__notNull")
fun SqH2TableColumnHolder.h2CharacterVarying(name: String, nullFlag: Any): SqTableColumn<String, String> =
    this.add(this.types.h2CharacterVarying, name, nullFlag)
@JvmName("h2CharacterVarying__nullable")
fun SqH2TableColumnHolder.h2CharacterVarying(name: String, nullFlag: Any?): SqTableColumn<String?, String> =
    this.add(this.types.h2CharacterVarying, name, nullFlag)

@JvmName("h2VarCharIgnoreCase__notNull")
fun SqH2TableColumnHolder.h2VarCharIgnoreCase(name: String, nullFlag: Any): SqTableColumn<String, String> =
    this.add(this.types.h2VarCharIgnoreCase, name, nullFlag)
@JvmName("h2VarCharIgnoreCase__nullable")
fun SqH2TableColumnHolder.h2VarCharIgnoreCase(name: String, nullFlag: Any?): SqTableColumn<String?, String> =
    this.add(this.types.h2VarCharIgnoreCase, name, nullFlag)
// endregion
