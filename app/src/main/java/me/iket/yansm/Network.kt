package me.iket.yansm

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.TrafficStats
import java.util.Locale


class Network(context: Context) {
    private var latestReceivedBytes = TrafficStats.getTotalRxBytes()
    private var latestSentBytes = TrafficStats.getTotalTxBytes()
    private var latestTime = System.currentTimeMillis()
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

    fun hasInternet(): Boolean {
        val currentNetwork = connectivityManager?.activeNetwork
        val caps = connectivityManager?.getNetworkCapabilities(currentNetwork)
        return caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    fun getUsageSinceLastTime(): CategorisedUsage {
        val currentReceivedBytes = TrafficStats.getTotalRxBytes()
        val currentSentBytes = TrafficStats.getTotalTxBytes()
        val currentTime = System.currentTimeMillis()

        val relativeReceivedBytes = currentReceivedBytes - latestReceivedBytes
        val relativeSentBytes = currentSentBytes - latestSentBytes
        val relativeTime = currentTime - latestTime

        latestReceivedBytes = relativeReceivedBytes
        latestSentBytes = relativeSentBytes
        latestTime = relativeTime

        return CategorisedUsage(
            relativeSentBytes,
            relativeReceivedBytes,
            relativeSentBytes + relativeReceivedBytes,
            relativeTime
        )

    }
}

data class CategorisedUsage(
    val upload: Long,
    val download: Long,
    val total: Long,
    val time: Long,
)

enum class SpeedUnit(val value: String) {
    //    BYTE_PER_SECOND("B/s"),
    KILOBYTE_PER_SECOND("KB/s"),
    MEGABYTE_PER_SECOND("MB/s"),
    GIGABYTE_PER_SECOND("GB/s"),
}

data class SpeedWithUnit(val value: String, val unit: SpeedUnit)

data class CategorisedSpeedWithUnit(
    val upload: SpeedWithUnit,
    val download: SpeedWithUnit,
    val total: SpeedWithUnit,
)

object NetworkSpeed {
    private const val THRESHOLD = 10
    private const val KILOBYTE = 1000
    private const val MEGABYTE = 1000 * KILOBYTE
    private const val GIGABYTE = 1000 * MEGABYTE
    private const val ONE_SEC_IN_MILLIS = 1000

    fun getSpeedFromUsage(usage: CategorisedUsage): CategorisedSpeedWithUnit {
        val timeInSecs = usage.time / ONE_SEC_IN_MILLIS
        val total = if (timeInSecs > 0) usage.total / timeInSecs else 0
        val download = if (timeInSecs > 0) usage.download / timeInSecs else 0
        val upload = if (timeInSecs > 0) usage.upload / timeInSecs else 0

        return CategorisedSpeedWithUnit(
            getSpeedWithUnit(upload),
            getSpeedWithUnit(download),
            getSpeedWithUnit(total)
        )
    }

    private fun getSpeedWithUnit(speed: Long): SpeedWithUnit {
        val (value, unit) = when {
            speed < THRESHOLD * KILOBYTE -> "0" to SpeedUnit.KILOBYTE_PER_SECOND
            speed < MEGABYTE -> (speed / KILOBYTE).toString() to SpeedUnit.KILOBYTE_PER_SECOND
            // most common speed range on mobile, so showing up to 1 decimal places
            speed < 20 * MEGABYTE -> String.format(
                Locale.ENGLISH,
                "%.1f",
                speed / MEGABYTE.toDouble()
            ) to SpeedUnit.MEGABYTE_PER_SECOND

            speed < GIGABYTE -> (speed / MEGABYTE).toString() to SpeedUnit.MEGABYTE_PER_SECOND
            else -> String.format(
                Locale.ENGLISH,
                "%.1f",
                speed / GIGABYTE
            ) to SpeedUnit.GIGABYTE_PER_SECOND
        }

        return SpeedWithUnit(value, unit)
    }
}