package io.github.ore.sq.impl

import io.github.ore.sq.*


// region Concatenation
open class SqConcatenationImpl<JAVA: String?>(
    override val reader: SqDataTypeReader<JAVA, String>,
    override val parameters: List<SqItem>,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqConcatenation<JAVA> {
    open class Factory: SqConcatenationFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun <JAVA : String?> create(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, String>,
            parameters: List<SqItem>
        ): SqConcatenationImpl<JAVA> {
            if (parameters.isEmpty()) {
                error("Parameter list is empty")
            }
            return SqConcatenationImpl(reader, parameters)
        }
    }
}
// endregion


// region SUBSTRING
open class SqSubstringImpl<JAVA: String?>(
    override val reader: SqDataTypeReader<JAVA, String>,
    override val source: SqItem,
    override val offset: SqItem?,
    override val length: SqItem?,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqSubstring<JAVA> {
    open class Factory: SqSubstringFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun <JAVA : String?> create(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, String>,
            source: SqItem,
            offset: SqItem?,
            length: SqItem?,
        ): SqSubstringImpl<JAVA> {
            if ((offset == null) && (length == null)) {
                error("Both \"offset\" and \"length\" are NULL")
            }
            return SqSubstringImpl(reader, source, offset, length)
        }
    }
}
// endregion


// region String convert function (UPPER, LOWER, TRIM, LTRIM, RTRIM)
open class SqStringConvertOperationImpl<JAVA: String?>(
    override val reader: SqDataTypeReader<JAVA, String>,
    override val operationKeyword: String,
    override val target: SqItem,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqStringConvertOperation<JAVA> {
    open class Factory: SqStringConvertOperationFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }


        override fun <JAVA : String?> createUpper(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, String>,
            target: SqItem,
        ): SqStringConvertOperationImpl<JAVA> {
            return SqStringConvertOperationImpl(reader, "upper", target)
        }

        override fun <JAVA : String?> createLower(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, String>,
            target: SqItem,
        ): SqStringConvertOperationImpl<JAVA> {
            return SqStringConvertOperationImpl(reader, "lower", target)
        }

        override fun <JAVA : String?> createTrim(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, String>,
            target: SqItem,
        ): SqStringConvertOperationImpl<JAVA> {
            return SqStringConvertOperationImpl(reader, "trim", target)
        }

        override fun <JAVA : String?> createLTrim(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, String>,
            target: SqItem,
        ): SqStringConvertOperationImpl<JAVA> {
            return SqStringConvertOperationImpl(reader, "ltrim", target)
        }

        override fun <JAVA : String?> createRTrim(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, String>,
            target: SqItem
        ): SqStringConvertOperationImpl<JAVA> {
            return SqStringConvertOperationImpl(reader, "rtrim", target)
        }
    }
}
// endregion


// region CHARACTER_LENGTH
open class SqCharacterLengthImpl<JAVA: Number?>(
    override val reader: SqDataTypeReader<JAVA, Number>,
    override val parameter: SqItem,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqCharacterLength<JAVA> {
    open class Factory: SqCharacterLengthFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun <JAVA : Number?> create(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Number>,
            parameter: SqItem,
        ): SqCharacterLengthImpl<JAVA> {
            return SqCharacterLengthImpl(reader, parameter)
        }
    }
}
// endregion


// region POSITION
open class SqTextPositionImpl<JAVA: Number?>(
    override val reader: SqDataTypeReader<JAVA, Number>,
    override val searchedText: SqItem,
    override val searchLocation: SqItem,
    override var commentAtStart: String? = null,
    override var commentAtEnd: String? = null,
): SqTextPosition<JAVA> {
    open class Factory: SqTextPositionFactory {
        companion object {
            val INSTANCE: Factory = Factory()
        }

        override fun <JAVA : Number?> create(
            context: SqContext,
            reader: SqDataTypeReader<JAVA, Number>,
            searchedText: SqItem,
            searchLocation: SqItem
        ): SqTextPositionImpl<JAVA> {
            return SqTextPositionImpl(reader, searchedText, searchLocation)
        }
    }
}
// endregion
