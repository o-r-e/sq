package me.ore.sq.generic

import me.ore.sq.SqColumn
import me.ore.sq.SqContext
import me.ore.sq.SqOrderBy
import me.ore.sq.SqSortOrder


open class SqGenericOrderBy(
    override val context: SqContext,
    override val column: SqColumn<*, *>,
    override val order: SqSortOrder,
): SqOrderBy
