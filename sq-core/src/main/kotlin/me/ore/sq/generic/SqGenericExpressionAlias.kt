package me.ore.sq.generic

import me.ore.sq.SqContext
import me.ore.sq.SqExpression
import me.ore.sq.SqExpressionAlias
import me.ore.sq.SqExpressionAliasConstructor
import me.ore.sq.util.SqUtil


open class SqGenericExpressionAlias<JAVA: Any?, DB: Any, ORIG: SqExpression<JAVA, DB>>(
    override val context: SqContext,
    override val original: ORIG,
    override val alias: String,
    override val safeAlias: String = SqUtil.makeIdentifierSafeIfNeeded(alias),
): SqExpressionAlias<JAVA, DB, ORIG> {
    companion object {
        val CONSTRUCTOR: SqExpressionAliasConstructor = object : SqExpressionAliasConstructor {
            override fun <JAVA, DB : Any, ORIG : SqExpression<JAVA, DB>> createExpressionAlias(context: SqContext, original: ORIG, alias: String): SqExpressionAlias<JAVA, DB, ORIG> {
                return SqGenericExpressionAlias(context, original, alias)
            }
        }
    }
}
