package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericBooleanGroup<JAVA: Boolean?>(
    override val context: SqContext,
    override val type: SqType<JAVA, Boolean>,
    override val groupType: SqBooleanGroupType,
    override val items: List<SqExpression<*, Boolean>>,
): SqBooleanGroup<JAVA> {
    companion object {
        val CONSTRUCTOR: SqBooleanGroupConstructor = object : SqBooleanGroupConstructor {
            override fun <JAVA : Boolean?> createBooleanGroup(
                context: SqContext,
                type: SqType<JAVA, Boolean>,
                groupType: SqBooleanGroupType,
                items: List<SqExpression<*, Boolean>>
            ): SqBooleanGroup<JAVA> {
                return SqGenericBooleanGroup(context, type, groupType, items)
            }
        }
    }


    override val definitionItem: SqItem
        get() = this
}
