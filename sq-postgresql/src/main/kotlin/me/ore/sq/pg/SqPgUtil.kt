package me.ore.sq.pg


object SqPgUtil {
    fun bitsToString(bits: BooleanArray): String {
        return bits
            .map {
                if (it) {
                    '1'
                } else {
                    '0'
                }
            }
            .joinToString("")
    }
}
