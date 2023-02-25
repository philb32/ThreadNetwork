package com.example.threadnetwork

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.threadnetwork.ThreadBorderAgent
import com.google.android.gms.threadnetwork.ThreadNetwork
import com.google.android.gms.threadnetwork.ThreadNetworkCredentials


fun String.dsToByteArray(): ByteArray {
    return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}

class MainActivity : ComponentActivity() {

    private lateinit var preferredCredentialsLauncher: ActivityResultLauncher<IntentSenderRequest>
    private val activeDataset = "0e080000000000010000000300000f35060004001fffe0020811111111222222220708fdff74d969e7147a051000112233445566778899aabbccddeeff030e4f70656e54687265616444656d6f010212340410445f2b5ca6f2a93a55ce570a70efeecb0c0402a0f7f8".dsToByteArray()

    // Boreder Router ID - not sure what this is - use extaddr padded with zeroes to see if that works ...
    private val extaddr = "00000000000000009ad4bb0af635a879".dsToByteArray()

    // Creates the IntentSender result launcher for the getPreferredCredentials API
    private val getPreferredCredentialsLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            val context = getApplicationContext()
            if (result.getResultCode() == RESULT_OK) {
                val preferredCredentials : ThreadNetworkCredentials =
                    ThreadNetworkCredentials.fromIntentSenderResultData(
                        checkNotNull(result.getData())
                    );
                Toast.makeText(context, "Network name : ${preferredCredentials.getNetworkName()}", Toast.LENGTH_SHORT).show()
                Log.d("info", "Credentials returned: Network name : ${preferredCredentials.getNetworkName()}")
            } else {
                Toast.makeText(context, "User refused to share thread network credentials", Toast.LENGTH_SHORT).show()
                Log.d("info", "User refused to share thread network credentials")
            }
        }

    public fun GetPreferredNetworkCredentials() {

        // Invokes the getPreferredCredentials API and starts the dialog activity with the returned
        // IntentSender
        val threadNetworkClient = ThreadNetwork.getClient(getApplicationContext())
        threadNetworkClient
            .getPreferredCredentials()
            .addOnSuccessListener() { intentSenderResult ->
                val intentSender = intentSenderResult.getIntentSender()
                if (intentSender != null) {
                    Log.d("info", "Launching getPreferredCredentialsLauncher")
                    getPreferredCredentialsLauncher.launch(
                        IntentSenderRequest.Builder(intentSender).build()
                    )
                } else {
                    Log.d("info", "No preferred credentials found")
                }
            } // Handles the failure
            .addOnFailureListener() { e -> {}}
    }

    public fun AddThreadBorderAgent() {
        val threadBorderAgent = ThreadBorderAgent.newBuilder(extaddr).build()
        val threadCredentials = ThreadNetworkCredentials.fromActiveOperationalDataset(activeDataset)

        val threadNetworkClient = ThreadNetwork.getClient(getApplicationContext())
        threadNetworkClient.addCredentials(threadBorderAgent, threadCredentials)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                getPreferredNetwork(this@MainActivity)
                addBorderAgent(this@MainActivity)
            }
        }
    }
}

@Composable
fun getPreferredNetwork(mainActivity : MainActivity) {
    val context = LocalContext.current
    Button(onClick = {
        Toast.makeText(context, "Button clicked", Toast.LENGTH_SHORT).show()
        mainActivity.GetPreferredNetworkCredentials()
    }) {
        Text("Get Preferred Network")
    }
}

@Composable
fun addBorderAgent(mainActivity : MainActivity) {
    val context = LocalContext.current
    Button(onClick = {
        mainActivity.AddThreadBorderAgent()
    }) {
        Text("Add Border Router")
    }
}