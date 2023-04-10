package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericColSetAliasColumn<JAVA: Any?, DB: Any>(
    override val context: SqContext,
    override val alias: SqColSetAlias<*>,
    override val column: SqColumn<JAVA, DB>,
): SqColSetAliasColumn<JAVA, DB> {
    companion object {
        val CONSTRUCTOR: SqColSetAliasColumnConstructor = object : SqColSetAliasColumnConstructor {
            override fun <JAVA, DB : Any> createColSetAliasColumn(context: SqContext, alias: SqColSetAlias<*>, column: SqColumn<JAVA, DB>): SqColSetAliasColumn<JAVA, DB> {
                return SqGenericColSetAliasColumn(context, alias, column)
            }
        }
    }


    override val definitionItem: SqItem
        get() = this
}
