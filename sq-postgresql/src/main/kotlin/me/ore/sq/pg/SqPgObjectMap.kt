package me.ore.sq.pg

import me.ore.sq.*


data class SqPgObjectMap(
    val multiColSelectConstructor: SqMultiColSelectConstructor = SqPgMultiColSelect.CONSTRUCTOR,
    val multiColUnionConstructor: SqMultiColUnionConstructor = SqPgMultiColUnion.CONSTRUCTOR,
    val singleColSelectConstructor: SqSingleColSelectConstructor = SqPgSingleColSelect.CONSTRUCTOR,
    val singleColUnionConstructor: SqSingleColUnionConstructor = SqPgSingleColUnion.CONSTRUCTOR,
    val connMultiColSelectConstructor: SqConnMultiColSelectConstructor = SqPgConnMultiColSelect.CONSTRUCTOR,
    val connMultiColUnionConstructor: SqConnMultiColUnionConstructor = SqPgConnMultiColUnion.CONSTRUCTOR,
    val connSingleColSelectConstructor: SqConnSingleColSelectConstructor = SqPgConnSingleColSelect.CONSTRUCTOR,
    val connSingleColUnionConstructor: SqConnSingleColUnionConstructor = SqPgConnSingleColUnion.CONSTRUCTOR
): SqObjectMap {
    companion object {
        val INSTANCE: SqPgObjectMap = SqPgObjectMap()
    }


    override fun <T : Any> get(key: Class<T>): T? {
        val result: Any? = when (key) {
            SqMultiColSelectConstructor::class.java -> this.multiColSelectConstructor
            SqMultiColUnionConstructor::class.java -> this.multiColUnionConstructor
            SqSingleColSelectConstructor::class.java -> this.singleColSelectConstructor
            SqSingleColUnionConstructor::class.java -> this.singleColUnionConstructor
            SqConnMultiColSelectConstructor::class.java -> this.connMultiColSelectConstructor
            SqConnMultiColUnionConstructor::class.java -> this.connMultiColUnionConstructor
            SqConnSingleColSelectConstructor::class.java -> this.connSingleColSelectConstructor
            SqConnSingleColUnionConstructor::class.java -> this.connSingleColUnionConstructor
            else -> null
        }

        @Suppress("UNCHECKED_CAST")
        return (result as T?)
    }
}
