package io.github.ore.sq.util

import java.io.InputStream
import java.sql.Blob


open class SqBlobInputStream(
    protected open val blob: Blob,
    protected open val wrappedStream: InputStream = blob.binaryStream,
    protected open val closeWrappedStreamOnClose: Boolean = true,
    protected open val freeBlobOnClose: Boolean = true,
): InputStream() {
    override fun available(): Int =
        this.wrappedStream.available()

    override fun read(): Int =
        this.wrappedStream.read()

    override fun read(b: ByteArray): Int =
        this.wrappedStream.read(b)

    override fun read(b: ByteArray, off: Int, len: Int): Int =
        this.wrappedStream.read(b, off, len)

    override fun skip(n: Long): Long =
        this.wrappedStream.skip(n)

    override fun markSupported(): Boolean =
        this.wrappedStream.markSupported()

    override fun mark(readlimit: Int) {
        this.wrappedStream.mark(readlimit)
    }

    override fun reset() {
        this.wrappedStream.reset()
    }

    override fun close() {
        val exceptions = ArrayList<Exception>()

        // Close input stream
        if (this.closeWrappedStreamOnClose) {
            try {
                this.wrappedStream.close()
            } catch (e: Exception) {
                exceptions.add(e)
            }
        }

        // Free blob
        if (this.freeBlobOnClose) {
            try {
                this.blob.free()
            } catch (e: Exception) {
                exceptions.add(e)
            }
        }

        SqUtil.toSingleException(exceptions) { Exception("Multiple exceptions while closing BLOB wrapper stream") }
    }
}
