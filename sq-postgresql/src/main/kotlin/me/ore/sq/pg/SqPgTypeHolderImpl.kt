package me.ore.sq.pg

import me.ore.sq.SqDbTypeBit
import me.ore.sq.SqType
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.Date
import java.sql.SQLXML
import java.sql.Time
import java.sql.Timestamp
import java.time.*


object SqPgTypeHolderImpl: SqPgTypeHolder {
    // region Shared readers / writers
    val javaStringReader: SqPgJavaStringReader = SqPgJavaStringReader()
    val javaStringListReader: SqPgJavaStringListReader = SqPgJavaStringListReader()

    val multiBitReader: SqPgMultiBitReader = SqPgMultiBitReader()
    // endregion


    // region Boolean types
    override val pgSingleBit: SqType<Boolean, SqDbTypeBit> = run {
        SqType.notNull(
            Boolean::class.java,
            SqDbTypeBit::class.java,
            SqPgSingleBitReader(),
            SqPgSingleBitWriter(),
        )
    }

    override val pgSingleBitArray: SqType<List<Boolean?>, Array<SqDbTypeBit>> = run {
        SqType.notNull(
            emptyList<Boolean?>().javaClass,
            emptyArray<SqDbTypeBit>().javaClass,
            SqPgSingleBitArrayReader(),
            SqPgSingleBitArrayWriter(),
            valueClassText = "List<Boolean?>",
            dbTypeText = "Array<${SqDbTypeBit::class.java.name}>",
        )
    }

    override val pgBoolean: SqType<Boolean, Boolean> = run {
        SqType.notNull(
            Boolean::class.java,
            Boolean::class.java,
            SqPgBooleanReader(),
            SqPgBooleanWriter(),
        )
    }

    override val pgBooleanArray: SqType<List<Boolean?>, Array<Boolean>> = run {
        SqType.notNull(
            emptyList<Boolean?>().javaClass,
            emptyArray<Boolean>().javaClass,
            SqPgBooleanArrayReader(),
            SqPgBooleanArrayWriter(),
            valueClassText = "List<Boolean?>",
            dbTypeText = "Array<Boolean>",
        )
    }
    // endregion


    // region Byte array types
    override val pgBytea: SqType<ByteArray, ByteArray> = run {
        SqType.notNull(
            ByteArray::class.java,
            ByteArray::class.java,
            SqPgByteaReader(),
            SqPgByteaWriter(),
        )
    }

    override val pgByteaArray: SqType<List<ByteArray?>, Array<ByteArray>> = run {
        SqType.notNull(
            emptyList<ByteArray?>().javaClass,
            emptyArray<ByteArray>().javaClass,
            SqPgByteaArrayReader(),
            SqPgByteaArrayWriter(),
            valueClassText = "List<ByteArray?>",
            dbTypeText = "Array<ByteArray>",
        )
    }
    // endregion


    // region Date/time types
    override val pgDate: SqType<LocalDate, Timestamp> = run {
        SqType.notNull(
            LocalDate::class.java,
            Timestamp::class.java,
            SqPgDateReader(),
            SqPgDateWriter(),
        )
    }

    override val pgDateArray: SqType<List<LocalDate?>, Array<Timestamp>> = run {
        SqType.notNull(
            emptyList<LocalDate?>().javaClass,
            emptyArray<Timestamp>().javaClass,
            SqPgDateArrayReader(),
            SqPgDateArrayWriter(),
            valueClassText = "List<${LocalDate::class.java.name}?>",
            dbTypeText = "Array<${Timestamp::class.java.name}>",
        )
    }

    override val pgDateAsDate: SqType<Date, Timestamp> = run {
        SqType.notNull(
            Date::class.java,
            Timestamp::class.java,
            SqPgSqlDateReader(),
            SqPgSqlDateWriter(),
        )
    }

    override val pgTime: SqType<LocalTime, Time> = run {
        SqType.notNull(
            LocalTime::class.java,
            Time::class.java,
            SqPgTimeReader(),
            SqPgTimeWriter(),
        )
    }

    override val pgTimeArray: SqType<List<LocalTime?>, Array<Time>> = run {
        SqType.notNull(
            emptyList<LocalTime?>().javaClass,
            emptyArray<Time>().javaClass,
            SqPgTimeArrayReader(),
            SqPgTimeArrayWriter(),
            valueClassText = "List<${LocalTime::class.java.name}?>",
            dbTypeText = "Array<${Time::class.java.name}>",
        )
    }

    override val pgTimeAsTime: SqType<Time, Time> = run {
        SqType.notNull(
            Time::class.java,
            Time::class.java,
            SqPgSqlTimeReader(),
            SqPgSqlTimeWriter(),
        )
    }

    override val pgTimeTZ: SqType<OffsetTime, Time> = run {
        SqType.notNull(
            OffsetTime::class.java,
            Time::class.java,
            SqPgTimeTZReader(),
            SqPgTimeTZWriter(),
        )
    }

    override val pgTimeTZArray: SqType<List<OffsetTime?>, Array<Time>> = run {
        SqType.notNull(
            emptyList<OffsetTime?>().javaClass,
            emptyArray<Time>().javaClass,
            SqPgTimeTZArrayReader(),
            SqPgTimeTZArrayWriter(),
            valueClassText = "List<${OffsetTime::class.java.name}?>",
            dbTypeText = "Array<${Time::class.java.name}>",
        )
    }

    override val pgTimestamp: SqType<LocalDateTime, Timestamp> = run {
        SqType.notNull(
            LocalDateTime::class.java,
            Timestamp::class.java,
            SqPgTimestampReader(),
            SqPgTimestampWriter(),
        )
    }

    override val pgTimestampArray: SqType<List<LocalDateTime?>, Array<Timestamp>> = run {
        SqType.notNull(
            emptyList<LocalDateTime?>().javaClass,
            emptyArray<Timestamp>().javaClass,
            SqPgTimestampArrayReader(),
            SqPgTimestampArrayWriter(),
            valueClassText = "List<${LocalDateTime::class.java.name}?>",
            dbTypeText = "Array<${Timestamp::class.java.name}>",
        )
    }

    override val pgTimestampAsTimestamp: SqType<Timestamp, Timestamp> = run {
        SqType.notNull(
            Timestamp::class.java,
            Timestamp::class.java,
            SqPgSqlTimestampReader(),
            SqPgSqlTimestampWriter(),
        )
    }

    override val pgTimestampTZ: SqType<OffsetDateTime, Timestamp> = run {
        SqType.notNull(
            OffsetDateTime::class.java,
            Timestamp::class.java,
            SqPgTimestampTZReader(),
            SqPgTimestampTZWriter(),
        )
    }

    override val pgTimestampTZArray: SqType<List<OffsetDateTime?>, Array<Timestamp>> = run {
        SqType.notNull(
            emptyList<OffsetDateTime?>().javaClass,
            emptyArray<Timestamp>().javaClass,
            SqPgTimestampTZArrayReader(),
            SqPgTimestampTZArrayWriter(),
            valueClassText = "List<${OffsetDateTime::class.java.name}?>",
            dbTypeText = "Array<${Timestamp::class.java.name}>",
        )
    }
    // endregion


    // region Number types
    override val pgBigInt: SqType<Long, Number> = run {
        SqType.notNull(
            Long::class.java,
            Number::class.java,
            SqPgBigIntReader(),
            SqPgBigIntWriter(),
        )
    }

    override val pgBigIntArray: SqType<List<Long?>, Array<Number>> = run {
        SqType.notNull(
            emptyList<Long?>().javaClass,
            emptyArray<Number>().javaClass,
            SqPgBigIntArrayReader(),
            SqPgBigIntArrayWriter(),
            valueClassText = "List<Long?>",
            dbTypeText = "Array<Number>",
        )
    }

    override val pgBigIntAsBigInteger: SqType<BigInteger, Number> = run {
        SqType.notNull(
            BigInteger::class.java,
            Number::class.java,
            SqPgMathBigIntReader(),
            SqPgMathBigIntWriter(),
        )
    }

    override val pgDouble: SqType<Double, Number> = run {
        SqType.notNull(
            Double::class.java,
            Number::class.java,
            SqPgDoubleReader(),
            SqPgDoubleWriter(),
        )
    }

    override val pgDoubleArray: SqType<List<Double?>, Array<Number>> = run {
        SqType.notNull(
            emptyList<Double?>().javaClass,
            emptyArray<Number>().javaClass,
            SqPgDoubleArrayReader(),
            SqPgDoubleArrayWriter(),
            valueClassText = "List<Double?>",
            dbTypeText = "Array<Number>",
        )
    }

    override val pgInteger: SqType<Int, Number> = run {
        SqType.notNull(
            Int::class.java,
            Number::class.java,
            SqPgIntegerReader(),
            SqPgIntegerWriter(),
        )
    }

    override val pgIntegerArray: SqType<List<Int?>, Array<Number>> = run {
        SqType.notNull(
            emptyList<Int?>().javaClass,
            emptyArray<Number>().javaClass,
            SqPgIntegerArrayReader(),
            SqPgIntegerArrayWriter(),
            valueClassText = "List<Int?>",
            dbTypeText = "Array<Number>",
        )
    }

    override val pgNumeric: SqType<BigDecimal, Number> = run {
        SqType.notNull(
            BigDecimal::class.java,
            Number::class.java,
            SqPgNumericReader(),
            SqPgNumericWriter(),
        )
    }

    override val pgNumericArray: SqType<List<BigDecimal?>, Array<Number>> = run {
        SqType.notNull(
            emptyList<BigDecimal?>().javaClass,
            emptyArray<Number>().javaClass,
            SqPgNumericArrayReader(),
            SqPgNumericArrayWriter(),
            valueClassText = "List<${BigDecimal::class.java.name}?>",
            dbTypeText = "Array<Number>",
        )
    }

    override val pgReal: SqType<Float, Number> = run {
        SqType.notNull(
            Float::class.java,
            Number::class.java,
            SqPgRealReader(),
            SqPgRealWriter(),
        )
    }

    override val pgRealArray: SqType<List<Float?>, Array<Number>> = run {
        SqType.notNull(
            emptyList<Float?>().javaClass,
            emptyArray<Number>().javaClass,
            SqPgRealArrayReader(),
            SqPgRealArrayWriter(),
            valueClassText = "List<Float?>",
            dbTypeText = "Array<Number>",
        )
    }

    override val pgSmallInt: SqType<Short, Number> = run {
        SqType.notNull(
            Short::class.java,
            Number::class.java,
            SqPgSmallIntReader(),
            SqPgSmallIntWriter(),
        )
    }

    override val pgSmallIntArray: SqType<List<Short?>, Array<Number>> = run {
        SqType.notNull(
            emptyList<Short?>().javaClass,
            emptyArray<Number>().javaClass,
            SqPgSmallIntArrayReader(),
            SqPgSmallIntArrayWriter(),
            valueClassText = "List<Short?>",
            dbTypeText = "Array<Number>",
        )
    }
    // endregion


    // region Text types
    override val pgChar: SqType<String, String> = run {
        SqType.notNull(
            String::class.java,
            String::class.java,
            this.javaStringReader,
            SqPgJavaStringWriter(SqPgTypes.CHAR, SqPgTypes.CHAR__TYPE_NAME),
        )
    }

    override val pgCharArray: SqType<List<String?>, Array<String>> = run {
        SqType.notNull(
            emptyList<String>().javaClass,
            emptyArray<String>().javaClass,
            this.javaStringListReader,
            SqPgJavaStringListWriter(
                SqPgTypes.CHAR_ARRAY__TYPE_NAME,
                SqPgTypes.CHAR__TYPE_NAME,
            ),
            valueClassText = "List<String?>",
            dbTypeText = "Array<String>",
        )
    }

    override val pgCharacter: SqType<String, String> = run {
        SqType.notNull(
            String::class.java,
            String::class.java,
            this.javaStringReader,
            SqPgJavaStringWriter(SqPgTypes.CHARACTER, SqPgTypes.CHARACTER__TYPE_NAME),
        )
    }

    override val pgCharacterArray: SqType<List<String?>, Array<String>> = run {
        SqType.notNull(
            emptyList<String?>().javaClass,
            emptyArray<String>().javaClass,
            this.javaStringListReader,
            SqPgJavaStringListWriter(
                SqPgTypes.CHARACTER_ARRAY__TYPE_NAME,
                SqPgTypes.CHARACTER__TYPE_NAME,
            ),
            valueClassText = "List<String?>",
            dbTypeText = "Array<String>",
        )
    }

    override val pgText: SqType<String, String> = run {
        SqType.notNull(
            String::class.java,
            String::class.java,
            this.javaStringReader,
            SqPgJavaStringWriter(SqPgTypes.TEXT, SqPgTypes.TEXT__TYPE_NAME),
        )
    }

    override val pgTextArray: SqType<List<String?>, Array<String>> = run {
        SqType.notNull(
            emptyList<String?>().javaClass,
            emptyArray<String>().javaClass,
            this.javaStringListReader,
            SqPgJavaStringListWriter(
                SqPgTypes.TEXT_ARRAY__TYPE_NAME,
                SqPgTypes.TEXT__TYPE_NAME,
            ),
            valueClassText = "",
            dbTypeText = "",
        )
    }

    override val pgVarChar: SqType<String, String> = run {
        SqType.notNull(
            String::class.java,
            String::class.java,
            this.javaStringReader,
            SqPgJavaStringWriter(SqPgTypes.VAR_CHAR, SqPgTypes.VAR_CHAR__TYPE_NAME),
        )
    }

    override val pgVarCharArray: SqType<List<String?>, Array<String>> = run {
        SqType.notNull(
            emptyList<String?>().javaClass,
            emptyArray<String>().javaClass,
            this.javaStringListReader,
            SqPgJavaStringListWriter(
                SqPgTypes.VAR_CHAR_ARRAY__TYPE_NAME,
                SqPgTypes.VAR_CHAR__TYPE_NAME,
            ),
            valueClassText = "List<String?>",
            dbTypeText = "Array<String>",
        )
    }
    // endregion


    // region Other JDBC types
    override val pgXml: SqType<SQLXML, String> = run {
        SqType.notNull(
            SQLXML::class.java,
            String::class.java,
            SqPgXmlReader(),
            SqPgXmlWriter(),
        )
    }

    override val pgXmlArray: SqType<List<SQLXML?>, Array<String>> = run {
        SqType.notNull(
            emptyList<SQLXML?>().javaClass,
            emptyArray<String>().javaClass,
            SqPgXmlArrayReader(),
            SqPgXmlArrayWriter(),
            valueClassText = "List<${SQLXML::class.java.name}?>",
            dbTypeText = "Array<String>",
        )
    }
    // endregion


    // region Other Postgresql types
    override val pgJson: SqType<String, String> = run {
        SqType.notNull(
            String::class.java,
            String::class.java,
            this.javaStringReader,
            SqPgPGObjectWriter(SqPgTypes.JSON, SqPgTypes.JSON__TYPE_NAME),
        )
    }

    override val pgJsonArray: SqType<List<String?>, Array<String>> = run {
        SqType.notNull(
            emptyList<String?>().javaClass,
            emptyArray<String>().javaClass,
            this.javaStringListReader,
            SqPgPGObjectArrayWriter(
                SqPgTypes.JSON_ARRAY__TYPE_NAME,
                SqPgTypes.JSON__TYPE_NAME,
            ),
            valueClassText = "List<String?>",
            dbTypeText = "Array<String>",
        )
    }

    override val pgJsonB: SqType<String, String> = run {
        SqType.notNull(
            String::class.java,
            String::class.java,
            this.javaStringReader,
            SqPgPGObjectWriter(SqPgTypes.JSON_B, SqPgTypes.JSON_B__TYPE_NAME),
        )
    }

    override val pgJsonBArray: SqType<List<String?>, Array<String>> = run {
        SqType.notNull(
            emptyList<String?>().javaClass,
            emptyArray<String>().javaClass,
            this.javaStringListReader,
            SqPgPGObjectArrayWriter(
                SqPgTypes.JSON_B_ARRAY__TYPE_NAME,
                SqPgTypes.JSON_B__TYPE_NAME,
            ),
            valueClassText = "",
            dbTypeText = "",
        )
    }

    override val pgMultiBit: SqType<BooleanArray, SqDbTypeBit> = run {
        SqType.notNull(
            BooleanArray::class.java,
            SqDbTypeBit::class.java,
            this.multiBitReader,
            SqPgMultiBitWriter(SqPgTypes.BIT, SqPgTypes.BIT__TYPE_NAME),
        )
    }

    override val pgMultiBitArray: SqType<List<BooleanArray?>, Array<SqDbTypeBit>> = run {
        SqType.notNull(
            emptyList<BooleanArray?>().javaClass,
            emptyArray<SqDbTypeBit>().javaClass,
            SqPgMultiBitArrayReader(),
            SqPgMultiBitArrayWriter(
                SqPgTypes.BIT_ARRAY__TYPE_NAME,
                SqPgTypes.BIT__TYPE_NAME,
            ),
            valueClassText = "List<BooleanArray?>",
            dbTypeText = "Array<${SqDbTypeBit::class.java.name}>",
        )
    }

    override val pgVarBit: SqType<BooleanArray, SqDbTypeBit> = run {
        SqType.notNull(
            BooleanArray::class.java,
            SqDbTypeBit::class.java,
            this.multiBitReader,
            SqPgMultiBitWriter(SqPgTypes.VAR_BIT, SqPgTypes.VAR_BIT__TYPE_NAME),
        )
    }

    override val pgVarBitArray: SqType<List<BooleanArray?>, Array<SqDbTypeBit>> = run {
        SqType.notNull(
            emptyList<BooleanArray?>().javaClass,
            emptyArray<SqDbTypeBit>().javaClass,
            SqPgMultiBitArrayReader(),
            SqPgMultiBitArrayWriter(
                SqPgTypes.VAR_BIT_ARRAY__TYPE_NAME,
                SqPgTypes.VAR_BIT__TYPE_NAME,
            ),
            valueClassText = "List<BooleanArray?>",
            dbTypeText = "Array<${SqDbTypeBit::class.java.name}>",
        )
    }
    // endregion
}
