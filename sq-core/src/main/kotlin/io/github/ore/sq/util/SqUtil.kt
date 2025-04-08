package io.github.ore.sq.util

import io.github.ore.sq.*
import java.sql.PreparedStatement
import java.sql.Time
import java.sql.Timestamp
import java.time.Clock
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZoneOffset
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


object SqUtil {
    fun handle(exception: Throwable) {
        val thread = Thread.currentThread()

        // Use handler from current thread
        try {
            val handler = thread.uncaughtExceptionHandler
            if (handler != null) {
                handler.uncaughtException(thread, exception)
                return
            }
        } catch (e: Exception) {
            exception.addSuppressed(e)
        }

        // Use default handler
        try {
            val handler = Thread.getDefaultUncaughtExceptionHandler()
            if (handler != null) {
                handler.uncaughtException(thread, exception)
                return
            }
        } catch (e: Exception) {
            exception.addSuppressed(e)
        }

        // Print to console
        exception.printStackTrace(System.err)
    }

    fun <T: Throwable> toSingleException(exceptions: List<T>, createGroupException: () -> T) {
        when (exceptions.size) {
            0 -> return
            1 -> throw exceptions[0]
            else -> {
                val groupException = createGroupException()
                exceptions.forEach { exception ->
                    groupException.addSuppressed(exception)
                }
                throw groupException
            }
        }
    }

    fun executeUpdate(statement: PreparedStatement): Long {
        return try {
            statement.executeLargeUpdate()
        } catch (largeUpdateException: UnsupportedOperationException) {
            try {
                statement.executeUpdate().toLong()
            } catch (updateException: Exception) {
                updateException.addSuppressed(largeUpdateException)
                throw updateException
            }
        }
    }

    inline fun <R: SqRecord, T> processRecordFieldColumnParameterPair(
        parameters: SqParameterNs,
        record: R,
        recordClassInfo: SqRecordClassInfo<out R>,
        propertyName: String,
        delegate: SqRecordFieldDelegate<out Any?>,
        block: (column: SqTableColumn<Any?, Any>, parameter: SqParameter<Any?, Any>) -> T,
    ): T {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

        val valueGetter = recordClassInfo.valueGetterMap[propertyName]
            ?: error("$recordClassInfo has no value getter for property \"$propertyName\"")

        @Suppress("UNCHECKED_CAST")
        val value = (valueGetter as SqRecordClassInfo.ValueGetter<R, Any?>)[record]

        @Suppress("UNCHECKED_CAST")
        val column = delegate.column as SqTableColumn<Any?, Any>

        val parameter = parameters.parameter(column.reader, column.writer, value)

        return block.invoke(column, parameter)
    }

    /**
     * 10^6
     */
    const val MILLI_TO_NANO_MULTIPLIER = 1_000_000


    const val MILLISECONDS_IN_DAY: Long = 1000 * 60 * 60 * 24

    fun defaultTimeOffset(): ZoneOffset {
        val defaultClock = Clock.systemDefaultZone()
        val now = defaultClock.instant()
        return defaultClock.zone.rules.getOffset(now)
    }

    fun localTimeToJdbcTime(value: LocalTime, timeOffset: ZoneOffset = defaultTimeOffset()): Time {
        val milliseconds = (value.toNanoOfDay() / MILLI_TO_NANO_MULTIPLIER) - (timeOffset.totalSeconds * 1_000)
        return Time(milliseconds)
    }

    fun jdbcTimeToLocalTime(value: Time, timeOffset: ZoneOffset = defaultTimeOffset()): LocalTime {
        var milliseconds = (value.time + (timeOffset.totalSeconds * 1_000))
        while (milliseconds < 0) {
            milliseconds += MILLISECONDS_IN_DAY
        }
        while (milliseconds >= MILLISECONDS_IN_DAY) {
            milliseconds -= MILLISECONDS_IN_DAY
        }

        return LocalTime.ofNanoOfDay(milliseconds * MILLI_TO_NANO_MULTIPLIER)
    }

    fun offsetDateTimeToJdbcTimestamp(value: OffsetDateTime, timeOffset: ZoneOffset = defaultTimeOffset()): Timestamp {
        val localDateTime = value.withOffsetSameInstant(timeOffset).toLocalDateTime()
        return Timestamp.valueOf(localDateTime)
    }

    fun jdbcTimestampToOffsetDateTime(value: Timestamp, timeOffset: ZoneOffset = defaultTimeOffset()): OffsetDateTime {
        val localDateTime = value.toLocalDateTime()
        return OffsetDateTime.of(localDateTime, timeOffset)
    }

    fun offsetTimeToJdbcTime(value: OffsetTime): Time {
        val localTime = value.withOffsetSameInstant(ZoneOffset.UTC).toLocalTime()
        return localTimeToJdbcTime(localTime, ZoneOffset.UTC)
    }

    fun jdbcTimeToOffsetTime(value: Time, timeOffset: ZoneOffset = defaultTimeOffset()): OffsetTime {
        val localTime = jdbcTimeToLocalTime(value, timeOffset)
        return OffsetTime.of(localTime, timeOffset)
    }
}
