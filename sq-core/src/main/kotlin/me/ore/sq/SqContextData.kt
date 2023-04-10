package me.ore.sq


data class SqContextData(
    val printParameterValues: Boolean,
    val objectMap: SqObjectMap,
) {
    companion object {
        val EMPTY: SqContextData = run {
            SqContextData(
                printParameterValues = false,
                objectMap = SqObjectMap.EMPTY,
            )
        }
    }
}
