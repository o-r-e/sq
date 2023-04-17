package me.ore.sq.generic

import me.ore.sq.SqAlias
import me.ore.sq.SqAliasDefinition
import me.ore.sq.SqContext
import me.ore.sq.SqItem


open class SqGenericAliasDefinition(
    override val context: SqContext,
    override val definitionItem: SqItem,
    override val alias: SqAlias<*>,
): SqAliasDefinition
