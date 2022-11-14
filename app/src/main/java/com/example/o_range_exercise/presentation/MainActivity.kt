/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.o_range_exercise.presentation

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.services.client.HealthServices
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.o_range_exercise.R
import com.example.o_range_exercise.presentation.theme.O_range_exerciseTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val healthClient = HealthServices.getClient(this)
        var startDestination = "greeting"
        var healthServicesManager = HealthServicesManager(healthClient,this)
        lifecycleScope.launch {
                val destination = if (healthServicesManager.hasExerciseCapability()) {
                    delay(5000)
                    Log.d(TAG,"Running ok, go for preparing")
                    startDestination = "preparing"
           //         R.id.prepareFragment
                } else {
                    Log.d(TAG, "No running")
                    startDestination = "greeting"
             //       R.id.notAvailableFragment
                }
            setContent {
                WearApp("Android",
                    startDestination = startDestination)
            }    //     findNavController().navigate(destination)
        }

        //    val healthClient = HealthServices.getClient(this)
    //    val exerciseClient = healthClient.exerciseClient
    //    lifecycleScope.launch {
    //        val capabilities = exerciseClient.getCapabilitiesAsync().await()
    //        if (ExerciseType.RUNNING in capabilities.supportedExerciseTypes) {
    //            var runningCapabilities =
    //                capabilities.getExerciseTypeCapabilities(ExerciseType.RUNNING)
    //            Log.d(TAG,"Running")
    //            val supportsHeartRate = DataType.HEART_RATE_BPM in runningCapabilities.supportedDataTypes
    //            Log.d(TAG,supportsHeartRate.toString())
    //        }
    //        else {
    //            Log.d(TAG, "No running")

    //        }
    //    }


    }
}

@Composable
fun WearApp(greetingName: String,
            navController: NavHostController = rememberNavController(),
            startDestination: String
) {
    O_range_exerciseTheme {
        /* If you have enough items in your list, use [ScalingLazyColumn] which is an optimized
         * version of LazyColumn for wear devices with some added features. For more information,
         * see d.android.com/wear/compose.
         */

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable("greeting") {
                    Greeting(greetingName = greetingName)
                }
                composable("preparing") {
                    Preparing(greetingName = greetingName)
                }
            }
        }
    }
}

@Composable
fun Preparing(greetingName: String
    ) {
    Log.d(TAG,"Preparing")
    val coroutineScope = rememberCoroutineScope()
    var serviceConnection = ExerciseServiceConnection()
    val permissionLauncher =rememberLauncherForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
    if (result.all { it.value }) {
        Log.d(TAG, "All required permissions granted")
        coroutineScope.launch {
            // Await binding of ExerciseService, since it takes a bit of time
            // to instantiate the service.
            serviceConnection.repeatWhenConnected {
                checkNotNull(serviceConnection.exerciseService) {
                    "Failed to achieve ExerciseService instance"
                }.prepareExercise()
            }
        }
    } else {
        Log.d(TAG, "Not all required permissions granted")
    }
}
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = "Preparing for exercise"
    )
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.secondary,
        text = "Prep"
    )
}
@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.secondary,
        text = "Atle"
    )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android",
    startDestination = "greeting")
}


