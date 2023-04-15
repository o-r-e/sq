package me.ore.sq.pg

import me.ore.sq.*


data class SqPgObjectMap(
    val multiColSelectConstructor: SqMultiColSelectConstructor = SqPgMultiColSelect.CONSTRUCTOR,
    val singleColSelectConstructor: SqSingleColSelectConstructor = SqPgSingleColSelect.CONSTRUCTOR,
    val connMultiColSelectConstructor: SqConnMultiColSelectConstructor = SqPgConnMultiColSelect.CONSTRUCTOR,
    val connSingleColSelectConstructor: SqConnSingleColSelectConstructor = SqPgConnSingleColSelect.CONSTRUCTOR,

    val multiColUnionConstructor: SqMultiColUnionConstructor = SqPgMultiColUnion.CONSTRUCTOR,
    val singleColUnionConstructor: SqSingleColUnionConstructor = SqPgSingleColUnion.CONSTRUCTOR,
    val connMultiColUnionConstructor: SqConnMultiColUnionConstructor = SqPgConnMultiColUnion.CONSTRUCTOR,
    val connSingleColUnionConstructor: SqConnSingleColUnionConstructor = SqPgConnSingleColUnion.CONSTRUCTOR
): SqObjectMap {
    companion object {
        val INSTANCE: SqPgObjectMap = SqPgObjectMap()
    }


    override fun <T : Any> get(key: Class<T>): T? {
        val result: Any? = when (key) {
            SqTypeHolder::class.java -> SqPgTypeHolderImpl
            SqPgTypeHolder::class.java -> SqPgTypeHolderImpl

            SqMultiColSelectConstructor::class.java -> this.multiColSelectConstructor
            SqSingleColSelectConstructor::class.java -> this.singleColSelectConstructor
            SqConnMultiColSelectConstructor::class.java -> this.connMultiColSelectConstructor
            SqConnSingleColSelectConstructor::class.java -> this.connSingleColSelectConstructor

            SqMultiColUnionConstructor::class.java -> this.multiColUnionConstructor
            SqSingleColUnionConstructor::class.java -> this.singleColUnionConstructor
            SqConnMultiColUnionConstructor::class.java -> this.connMultiColUnionConstructor
            SqConnSingleColUnionConstructor::class.java -> this.connSingleColUnionConstructor

            else -> null
        }

        @Suppress("UNCHECKED_CAST")
        return (result as T?)
    }
}
