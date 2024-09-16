package com.example.calllogapp

// Importing necessary Android components for permissions, logs, and call history.
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.provider.CallLog
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    // Late initialization for CallLogObserver (custom class, assumed to handle call log events).
    private lateinit var callLogObserver: CallLogObserver

    // Request code to handle call log permission requests.
    private val REQUEST_CODE_READ_CALL_LOG = 1

    // onCreate is the starting point of the activity lifecycle.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // Link the UI layout (ensure this layout exists).

        // Checking if the app has permission to read call logs.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, request it from the user.
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALL_LOG), REQUEST_CODE_READ_CALL_LOG)
        } else {
            // Permission is already granted, so we initialize the observer.
            initObserver()
        }
    }

    // This method sets up the CallLogObserver to monitor call logs.
    private fun initObserver() {
        val handler = Handler()  // Creates a handler to process messages or runnables.
        callLogObserver = CallLogObserver(this, handler)  // Initializes the call log observer.

        // Registers the observer to listen for changes in the call logs.
        contentResolver.registerContentObserver(CallLog.Calls.CONTENT_URI, true, callLogObserver)
    }

    // This method gets called when the user responds to the permission request.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_CALL_LOG) {
            // If permission is granted, initialize the observer.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initObserver()
            } else {
                // Log error if permission is denied.
                Log.e("MainActivity", "Permission denied to read call logs")
            }
        }
    }

    // onDestroy is called when the activity is about to be destroyed (clean up resources here).
    override fun onDestroy() {
        super.onDestroy()
        // Unregister the observer to stop monitoring call logs when the app is destroyed.
        contentResolver.unregisterContentObserver(callLogObserver)
    }
}
