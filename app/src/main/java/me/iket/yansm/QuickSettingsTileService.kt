package me.iket.yansm

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

//import android.util.Log


class QuickSettingsTileService : TileService() {

    private lateinit var sharedPreferences: SharedPreferences
    private var state: State = State.INACTIVE
    private val preferencesKey = QuickSettingsTileService::class.qualifiedName ?: "tileService"
    private val preferencesTileStateKey = "tileState"

    private val isAndroid10OrNewer = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    private val updateInterval: Long = 1000
    private var handler: Handler? = null
    private lateinit var networkUsage: Network

    override fun onCreate() {
        super.onCreate()
//        Log.d("call", "onCreate")
        networkUsage = Network(this)
        sharedPreferences = getSharedPreferences(preferencesKey, Context.MODE_PRIVATE)
        restoreState()
    }


    override fun onStartListening() {
        super.onStartListening()
//        Log.d("call", "onStartListening")
        addHandler()
    }

    override fun onStopListening() {
        super.onStopListening()
//        Log.d("call", "onStopListening")
        removeHandler()
    }

    override fun onClick() {
        super.onClick()
//        Log.d("call", "onClick")
        val newState = if (state == State.ACTIVE) State.INACTIVE else State.ACTIVE
        val isActive = newState == State.ACTIVE

//        Log.d("state", "isActive: $isActive, newState: $newState, oldState: $state")

        state = newState
        saveState(isActive)

        if (isActive) {
            addHandler()
        } else {
            updateTileToInactive()
            removeHandler()
        }
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
//            Log.d("handler", "going to run, internet: ${networkUsage.hasInternet()}, state: $state")
            if (!networkUsage.hasInternet()) {
                updateTileToUnavailable()
                return
            } else if (state == State.INACTIVE) {
                updateTileToInactive()
                return
            } else {

                updateTile()
            }

            handler?.postDelayed(this, updateInterval)
        }
    }

    private fun updateTile() {
//        Log.d("call", "updateTile")
        val now = networkUsage.getUsageSinceLastTime()
        val speed = NetworkSpeed.getSpeedFromUsage(now)
        val formattedUploadSpeed = "${speed.upload.value}${speed.upload.unit.value}"
        val formattedDownloadSpeed = "${speed.download.value}${speed.download.unit.value}"
        val formattedTotalSpeed = "${speed.total.value}${speed.total.unit.value}"
        val formattedLabel = "$formattedUploadSpeed ↑  $formattedDownloadSpeed ↓"

        updateTileToActive(formattedTotalSpeed, formattedLabel)
    }

    private fun updateTileToUnavailable() {
//        Log.d("call", "updateTileToUnavailable")
        val tile = qsTile ?: return
        tile.label = "No internet"
        if (isAndroid10OrNewer) tile.subtitle = ""
        tile.state = Tile.STATE_UNAVAILABLE
        tile.icon = Icon.createWithResource(this, R.drawable.speed_meter_disabled)
        tile.updateTile()
    }

    private fun updateTileToInactive() {
//        Log.d("call", "updateTileToInactive")
        val tile = qsTile ?: return
        tile.label = "Click to enable"
        if (isAndroid10OrNewer) tile.subtitle = ""
        tile.state = Tile.STATE_INACTIVE
        tile.icon = Icon.createWithResource(this, R.drawable.speed_meter_disabled)
        tile.updateTile()
    }

    private fun updateTileToActive(label: String, subtitle: String) {
//        Log.d("call", "updateTileToActive")
        val tile = qsTile ?: return
        if (isAndroid10OrNewer) {
            tile.label = label
            tile.subtitle = subtitle
        } else {
            tile.label = subtitle
        }
        tile.state = Tile.STATE_ACTIVE
        tile.icon = Icon.createWithResource(this, R.drawable.speed_meter_enabled)
        tile.updateTile()
    }

    private fun saveState(isActive: Boolean) {
//        Log.d("call", "saveState (to $isActive)")
        sharedPreferences.edit().putBoolean(preferencesTileStateKey, isActive).apply()
    }

    private fun restoreState() {
        val isActive = sharedPreferences.getBoolean(preferencesTileStateKey, true)
//        Log.d("call", "restoreState (to: $isActive)")
        state = if (isActive) State.ACTIVE else State.INACTIVE
    }

    private fun removeHandler() {
//        Log.d("call", "removeHandler")
        handler?.removeCallbacks(updateRunnable)
        handler = null
    }

    private fun addHandler() {
//        Log.d("call", "addHandler (available: ${handler != null})")
        if (handler != null) {
            handler?.post(updateRunnable)
            return
        }
        handler = Handler(Looper.getMainLooper())
        handler?.post(updateRunnable)
    }

}

enum class State {
    ACTIVE,
    INACTIVE
}