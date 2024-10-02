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
import androidx.wear.compose.material.Button
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)
        //setContentView(R.layout.activity_main)

        // Get the Vibrator service
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        // Find the button in the layout
        //val vibrateButton: Button = findViewById(R.id.vibrate_button)
        // Set an OnClickListener on the button
        //vibrateButton.setOnClickListener { // Check if the device has a vibrator
        //    if (vibrator.hasVibrator()) {
        //        // Vibrate for 500 milliseconds
        //        val vibrationEffect =
        //            VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
         //       vibrator.vibrate(vibrationEffect)
          //  }
       // }

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

@Composable
fun VibeButton() {
    val context = LocalContext.current
    val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator
    var isVibrating by remember { mutableStateOf(false) }
    var frequency by remember { mutableStateOf(40f) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
    Button(onClick = {
        isVibrating = !isVibrating
        if (isVibrating) {
            coroutineScope.launch {
                while (isVibrating) {
                    vibrator.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE))
                    delay((1000 / frequency).toLong())
                }
            }
        } else {
            vibrator.cancel()
        }
    }) {
        Text(text = if (isVibrating) "Stop" else "Vibe")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Frequency: ${frequency.toInt()} Hz")

        Slider(
            value = frequency,
            onValueChange = { frequency = it },
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