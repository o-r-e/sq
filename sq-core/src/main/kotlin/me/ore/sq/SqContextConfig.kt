package me.ore.sq


/**
 * Configuration for [SqContext]
 *
 * @param printParameterValues if set to `true`, then parameter values will be printed (possibly truncated)
 * as comments in the request text
 * @param objectHolder holder of objects with binding to their classes
 */
data class SqContextConfig(
    val printParameterValues: Boolean,
    val objectHolder: SqObjectHolder,
) {
    companion object {
        /**
         * Empty configuration
         *
         * [printParameterValues] equals `false`, [objectHolder] equals [SqObjectHolder.EMPTY]
         */
        val EMPTY: SqContextConfig = run {
            SqContextConfig(
                printParameterValues = false,
                objectHolder = SqObjectHolder.EMPTY,
            )
        }


        /**
         * Default configuration used when creating contexts
         *
         * _Thread-safe (`@Volatile`)_
         */
        @Volatile
        var defaultConfig: SqContextConfig = EMPTY
    }
}
