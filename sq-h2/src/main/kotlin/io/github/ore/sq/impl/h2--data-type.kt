package io.github.ore.sq.impl

import io.github.ore.sq.SqDataTypePack
import io.github.ore.sq.SqH2DataTypes
import java.io.InputStream
import java.io.Reader
import java.sql.Blob
import java.sql.Clob
import java.sql.Types


open class SqH2DataTypesImpl: SqDataTypesImpl(), SqH2DataTypes {
    companion object {
        val INSTANCE: SqH2DataTypesImpl = SqH2DataTypesImpl()
    }


    // region Blob, clob
    override val h2BinaryLargeObject: SqDataTypePack<Blob, Blob> =
        blobTypePack(Types.BLOB, SqH2DataTypes.DB_TYPE_NAME__BINARY_LARGE_OBJECT)
    override val h2BinaryLargeObjectStream: SqDataTypePack<InputStream, Blob> =
        blobStreamTypePack(Types.BLOB, SqH2DataTypes.DB_TYPE_NAME__BINARY_LARGE_OBJECT)
    override val h2CharacterLargeObject: SqDataTypePack<Clob, Clob> =
        clobTypePack(Types.CLOB, SqH2DataTypes.DB_TYPE_NAME__CHARACTER_LARGE_OBJECT)
    override val h2CharacterLargeObjectReader: SqDataTypePack<Reader, Clob> =
        clobReaderTypePack(Types.CLOB, SqH2DataTypes.DB_TYPE_NAME__CHARACTER_LARGE_OBJECT)

    override val blob: SqDataTypePack<Blob, Blob>
        get() = this.h2BinaryLargeObject
    override val blobStream: SqDataTypePack<InputStream, Blob>
        get() = this.h2BinaryLargeObjectStream
    override val clob: SqDataTypePack<Clob, Clob>
        get() = this.h2CharacterLargeObject
    override val clobReader: SqDataTypePack<Reader, Clob>
        get() = this.h2CharacterLargeObjectReader
    /*
    val nClob: SqDataTypePack<NClob, Clob>
    val nClobReader: SqDataTypePack<Reader, Clob>
     */
    // endregion


    // region Byte array
    override val h2Binary: SqDataTypePack<ByteArray, ByteArray> =
        byteArrayTypePack(Types.BINARY, SqH2DataTypes.DB_TYPE_NAME__BINARY)
    override val h2BinaryVarying: SqDataTypePack<ByteArray, ByteArray> =
        byteArrayTypePack(Types.VARBINARY, SqH2DataTypes.DB_TYPE_NAME__BINARY_VARYING)

    override val binary: SqDataTypePack<ByteArray, ByteArray>
        get() = this.h2Binary
    override val longVarBinary: SqDataTypePack<ByteArray, ByteArray>
        get() = this.h2BinaryVarying
    override val varBinary: SqDataTypePack<ByteArray, ByteArray>
        get() = this.h2BinaryVarying
    // endregion


    // region String
    override val h2Character: SqDataTypePack<String, String> =
        stringTypePack(Types.CHAR, SqH2DataTypes.DB_TYPE_NAME__CHARACTER)
    override val h2CharacterVarying: SqDataTypePack<String, String> =
        stringTypePack(Types.VARCHAR, SqH2DataTypes.DB_TYPE_NAME__CHARACTER_VARYING)
    override val h2VarCharIgnoreCase: SqDataTypePack<String, String> =
        stringTypePack(Types.VARCHAR, SqH2DataTypes.DB_TYPE_NAME__VAR_CHAR_IGNORE_CASE)

    override val char: SqDataTypePack<String, String>
        get() = this.h2Character
    override val longNVarChar: SqDataTypePack<String, String>
        get() = this.h2CharacterVarying
    override val longVarChar: SqDataTypePack<String, String>
        get() = this.h2CharacterVarying
    override val nChar: SqDataTypePack<String, String>
        get() = this.h2Character
    override val nVarChar: SqDataTypePack<String, String>
        get() = this.h2CharacterVarying
    override val varChar: SqDataTypePack<String, String>
        get() = this.h2CharacterVarying
    // endregion
}
