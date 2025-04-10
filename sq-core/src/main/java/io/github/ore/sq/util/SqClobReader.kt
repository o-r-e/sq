package io.github.ore.sq.util

import java.io.Reader
import java.nio.CharBuffer
import java.sql.Clob


open class SqClobReader(
    protected open val clob: Clob,
    protected open val wrappedReader: Reader = clob.characterStream,
    protected open val closeWrappedReaderOnClose: Boolean = true,
    protected open val freeClobOnClose: Boolean = true,
): Reader() {
    override fun ready(): Boolean =
        this.wrappedReader.ready()

    override fun read(): Int =
        this.wrappedReader.read()

    override fun read(cbuf: CharArray): Int =
        this.wrappedReader.read(cbuf)

    override fun read(cbuf: CharArray, off: Int, len: Int): Int =
        this.wrappedReader.read(cbuf, off, len)

    override fun read(target: CharBuffer): Int =
        this.wrappedReader.read(target)

    override fun skip(n: Long): Long =
        this.wrappedReader.skip(n)

    override fun markSupported(): Boolean =
        this.wrappedReader.markSupported()

    override fun mark(readAheadLimit: Int) {
        this.wrappedReader.mark(readAheadLimit)
    }

    override fun reset() {
        this.wrappedReader.reset()
    }

    override fun close() {
        val exceptions = ArrayList<Exception>()

        // Close reader
        if (this.closeWrappedReaderOnClose) {
            try {
                this.wrappedReader.close()
            } catch (e: Exception) {
                exceptions.add(e)
            }
        }

        // Free clob
        if (this.freeClobOnClose) {
            try {
                this.clob.free()
            } catch (e: Exception) {
                exceptions.add(e)
            }
        }

        SqUtil.toSingleException(exceptions) { Exception("Multiple exceptions while closing CLOB wrapper reader") }
    }
}
