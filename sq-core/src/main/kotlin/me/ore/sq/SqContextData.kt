package me.ore.sq


data class SqContextData(
    val printParameterValues: Boolean,
    val objectHolder: SqObjectHolder,
) {
    companion object {
        val EMPTY: SqContextData = run {
            SqContextData(
                printParameterValues = false,
                objectHolder = SqObjectHolder.EMPTY,
            )
        }
    }
}
