package me.iket.yansm

import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import me.iket.yansm.ui.theme.YansmTheme
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YansmTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen()
                }
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val helperTextDialog = remember {
        mutableStateOf<String?>(null)
    }

    fun requestToAddTile() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            helperTextDialog.value = helperTextForBelowAndroid13
            return
        }
        val statusBarService = context.getSystemService(StatusBarManager::class.java)

        val resultExecutor = Executors.newSingleThreadExecutor()
        val resultCallback = java.util.function.Consumer<Int> { result ->
            when (result) {
                StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED -> {
                    Handler(Looper.getMainLooper()).post {
                        val toast = Toast.makeText(context, "Tile added", Toast.LENGTH_SHORT)
                        toast.show()
                    }
                }

                StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED -> {
                    Handler(Looper.getMainLooper()).post {
                        val toast =
                            Toast.makeText(context, "Tile already added", Toast.LENGTH_SHORT)
                        toast.show()
                    }
                }

                else -> helperTextDialog.value = helperTextForAndroid13AndAbove
            }
        }
        statusBarService.requestAddTileService(
            ComponentName(context, QuickSettingsTileService::class.java.name),
            context.getString(R.string.app_name),
            Icon.createWithResource(context, R.drawable.speed_meter_enabled),
            resultExecutor,
            resultCallback
        )
    }


    if (helperTextDialog.value != null) AlertDialog(
        title = {
            Text(text = "How to add tile to quick settings?")
        },
        text = {
            Text(text = helperTextDialog.value!!)
        },


        dismissButton = {
            TextButton(onClick = { helperTextDialog.value = null }) {
                Text("Nah, I'm good")
            }
        },
        onDismissRequest = { helperTextDialog.value = null },
        confirmButton = {
            TextButton(onClick = {
                val browseIntent = Intent(Intent.ACTION_VIEW, Uri.parse(linkToDemo))
                ContextCompat.startActivity(context, browseIntent, null)
            }) {
                Text("Watch demo")
            }
        },

        )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "YANSM: Yet Another Network Speed Monitor",
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = modifier.padding(20.dp))
        Button(onClick = { requestToAddTile() }) {
            Text(text = "Add tile to quick settings")
        }
        Spacer(modifier = Modifier.padding(20.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            IconButton(onClick = {
                val browseIntent = Intent(Intent.ACTION_VIEW, Uri.parse(linkToReadme))
                ContextCompat.startActivity(context, browseIntent, null)
            }) {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = "About",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    YansmTheme {
        HomeScreen()
    }
}