package me.ore.sq.generic

import me.ore.sq.SqContext
import me.ore.sq.SqTextBuffer
import me.ore.sq.SqTextBufferConstructor


open class SqGenericTextBuffer(
    open val lineSeparator: String = DEFAULT_LINE_SEPARATOR,
    open val space: String = DEFAULT_SPACE,
): SqTextBuffer {
    companion object {
        const val DEFAULT_LINE_SEPARATOR = "\r\n"
        const val DEFAULT_SPACE = " "

        val CONSTRUCTOR: SqTextBufferConstructor = object : SqTextBufferConstructor {
            override fun createTextBuffer(context: SqContext): SqTextBuffer {
                return SqGenericTextBuffer()
            }
        }
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


    override fun addLineSeparator() { this.stringBuilder.append(this.lineSeparator) }

    override fun addText(text: String, spaced: Boolean) {
        if (spaced) this.addSpaceIfMissed()
        this.stringBuilder.append(text)
    }

    override fun clearData() { this.stringBuilder.clear() }
}
