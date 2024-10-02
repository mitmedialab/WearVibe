/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.wearvibe.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.example.wearvibe.R
import com.example.wearvibe.presentation.theme.WearVibeTheme
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.wear.compose.material.Button
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.wear.ambient.AmbientModeSupport

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)
        //setContentView(R.layout.activity_main)

        setContent {
            WearApp("Android")
        }
    }
}

@Composable
fun WearApp(greetingName: String) {
    WearVibeTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            VibeButton()
            //Greeting(greetingName = greetingName)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VibeButton() {
    val context = LocalContext.current
    val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator
    var isVibrating by remember { mutableStateOf(false) }
    val sharedPref = context.getSharedPreferences("wearVibePrefs", Context.MODE_PRIVATE)
    var frequency by remember {
        mutableStateOf(sharedPref.getFloat("saved_frequency", 40f)) // Retrieve saved frequency or default to 40f
    }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "WearVibe",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.primary
        )

        Text(text = "${frequency.toInt()} Hz",
            fontSize = 20.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = frequency,
            onValueChange = { newFrequency ->
                frequency = newFrequency

                with(sharedPref.edit()) {
                    putFloat("saved_frequency", frequency)
                    apply() // Save the value
                }

                if (isVibrating) {
                    val pattern = longArrayOf(0, 100, (1000 / frequency).toLong())  // Update vibration pattern
                    vibrator.vibrate(
                        VibrationEffect.createWaveform(pattern, 0)  // Loop indefinitely
                    )
                } },
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF2196F3),
                activeTrackColor = Color(0xFF2196F3),
                inactiveTrackColor = Color(0xFFBBDEFB),
                inactiveTickColor = Color(0xFF687CA5)
            ),
            valueRange = 1f..70f,
            steps = 69,
            modifier = Modifier.height(24.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))

        Button(onClick = {
        isVibrating = !isVibrating
            if (isVibrating) {
                coroutineScope.launch {
                    val pattern = longArrayOf(0, 100, (1000 / frequency).toLong())  // Vibration pattern
                    vibrator.vibrate(
                        VibrationEffect.createWaveform(pattern, 0)  // Loop indefinitely
                    )
                }
            } else {
                vibrator.cancel()  // Stop vibration
            }
        },
        modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 2.dp)
        )
    {
        Text(text = if (isVibrating) "Stop" else "Vibe")
        }
    }
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}



@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Sam")
}