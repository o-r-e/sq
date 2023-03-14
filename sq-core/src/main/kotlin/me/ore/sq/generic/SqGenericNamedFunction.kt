package me.ore.sq.generic

import me.ore.sq.*


open class SqGenericNamedFunction<JAVA: Any?, DB: Any>(
    override val context: SqContext,
    override val type: SqType<JAVA & Any>,
    nullable: Boolean,
    name: String,
    override val nameSeparated: Boolean,
    override val values: List<SqItem>,
): SqNamedFunction<JAVA, DB> {
    override val name: String = name.trim().uppercase()


    @Suppress("PropertyName")
    protected open var _nullable = nullable

    override fun nullable(): SqNamedFunction<JAVA?, DB> {
        this._nullable = true
        return SqUtil.uncheckedCast(this)
    }

    override val nullable: Boolean
        get() = this._nullable
}
