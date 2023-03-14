package me.ore.sq


sealed class SqReadResult<T: Any?> {
    companion object {
        fun <T: Any?> cancelReading(): CancelReading<T> = CancelReading()
        fun <T: Any?> wrap(value: T): Result<T> = Result(value)
    }


    class CancelReading<T>: SqReadResult<T>()

    class Result<T>(val value: T): SqReadResult<T>()
}
