package me.ore.sq.generic

import me.ore.sq.SqWriter


open class SqGenericWriter(
    open val lineSeparator: String = DEFAULT_LINE_SEPARATOR,
    open val space: String = DEFAULT_SPACE,
): SqWriter {
    companion object {
        const val DEFAULT_LINE_SEPARATOR = "\r\n"
        const val DEFAULT_SPACE = " "
    }


    protected open val stringBuilder: StringBuilder = StringBuilder()

    protected open fun lastCharIsWhiteSpace(): Boolean {
        if (this.stringBuilder.isEmpty()) {
            return false
        }

        val char = this.stringBuilder.last()
        return char.isWhitespace()
    }

    protected open fun addSpace() {
        this.stringBuilder.append(this.space)
    }

    protected open fun addSpaceIfMissed() {
        if (!this.lastCharIsWhiteSpace()) {
            this.addSpace()
        }
    }


    override fun toString(): String = this.stringBuilder.toString()

    override fun ls(): SqGenericWriter = this.apply {
        this.stringBuilder.append(this.lineSeparator)
    }

    override fun add(text: String, spaced: Boolean): SqGenericWriter = this.apply {
        if (spaced) {
            this.addSpaceIfMissed()
        }
        this.stringBuilder.append(text)
    }

    override fun clear(): SqGenericWriter = this.apply {
        this.stringBuilder.clear()
    }
}
