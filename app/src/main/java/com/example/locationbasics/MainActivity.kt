package com.example.locationbasics

import android.content.Context
import android.os.Bundle
import android.Manifest
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.locationbasics.ui.theme.LocationBasicsTheme
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: LocationViewModel = viewModel()
            LocationBasicsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyApp(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}


@Composable
fun MyApp(modifier: Modifier = Modifier, viewModel: LocationViewModel)
{
    val context = LocalContext.current
    val locationUtils = LocationUtils(context)

    LocationDisplay(viewModel, locationUtils, context)
}



@Composable
fun LocationDisplay(
    viewModel: LocationViewModel,
    locationUtils: LocationUtils,
    context: Context
){
    val location = viewModel.location.value
    val address = location?.let {
        locationUtils.reverceGeocoderLocation(location)}

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult =
        { permissions ->
            if(permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                && permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
            {
                //Location permission granted
                locationUtils.requestLocationUpdates(viewModel = viewModel)
            }else{
                //ask Location permission
                val rationalRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

                if (rationalRequired){
                    Toast.makeText(context, "Location Permission is required for this feature to work",
                        Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(context, "Go to settings and enable location permission",
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    )



    Column(modifier = Modifier.fillMaxSize().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center)
    {

        if(location != null){
            Text("Coordinates : Latitude : ${location.latitude} " +
                    "Longitude : ${location.longitude} \n Address : $address")

        }else{
        Text("Location not available")}
        Spacer(modifier = Modifier.padding(8.dp))
        Button(onClick =
        {
            if(locationUtils.hasPermissionGranted(context)){
                // Location permission granted
                locationUtils.requestLocationUpdates(viewModel = viewModel)
                Toast.makeText(context, "Location permission granted",
                    Toast.LENGTH_LONG).show()
            }else{
                // Location permission not granted
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )

            }
        })
        {
            Text("Get Location")
        }
    }

}