package io.github.ore.sq.impl

import io.github.ore.sq.SqExtendable
import io.github.ore.sq.SqFragment
import io.github.ore.sq.SqFragmented
import io.github.ore.sq.SqMutableExtendable
import java.util.*


// region Extendable
open class SqExtendableImpl(
    protected open val dataMap: Map<Any, Any>? = null,
): SqExtendable {
    override fun get(key: Any): Any? =
        this.dataMap?.get(key)
}

open class SqMutableExtendableImpl(
    override val dataMap: MutableMap<Any, Any> = HashMap(),
): SqExtendableImpl(dataMap), SqMutableExtendable {
    override fun set(key: Any, value: Any?) {
        if (value == null) {
            this.dataMap.remove(key)
        } else {
            this.dataMap[key] = value
        }
    }

    override fun clear() {
        this.dataMap.clear()
    }
}
// endregion


// region Fragment, Fragmented
abstract class SqFragmentedImpl(
    protected open val mutableFragments: MutableList<SqFragment> = ArrayList(),
    override val fragments: List<SqFragment> = mutableFragments,
): SqFragmented {
    override fun addFragment(fragment: SqFragment) {
        this.mutableFragments.add(fragment)
    }
}
// endregion
