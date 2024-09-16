package com.example.calllogapp

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.CallLog
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.Manifest

// Custom ContentObserver to monitor changes in the call log
class CallLogObserver(context: Context, handler: Handler) : ContentObserver(handler) {
    // Save the context for use in fetching call logs
    private val context: Context = context

    // Called when the call log changes
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        Log.d("CallLogObserver", "Call log changed") // Log message indicating a change in the call log

        // Launch a coroutine to fetch and print new call logs
        GlobalScope.launch {
            val callLogs = getCallDetails()  // Fetch the call logs
            printCallLogs(callLogs)  // Print the fetched call logs
        }
    }

    // Fetches call details from the call log
    private fun getCallDetails(): List<Map<String, String>> {
        val callLogsList = mutableListOf<Map<String, String>>()  // List to store call log details

        // Query the call log content provider
        val cursor = context.contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, null)

        // Use the cursor to read data
        cursor?.use {
            val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)  // Index for the call number
            val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)  // Index for the call type
            val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)  // Index for the call date
            val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)  // Index for the call duration

            // Iterate through the cursor to extract call log details
            while (it.moveToNext()) {
                val callLog = mapOf(
                    "number" to it.getString(numberIndex),  // Call number
                    "type" to it.getString(typeIndex),  // Call type (incoming, outgoing, missed)
                    "date" to it.getString(dateIndex),  // Call date
                    "duration" to it.getString(durationIndex)  // Call duration
                )
                callLogsList.add(callLog)  // Add the call log to the list
            }
        }

        return callLogsList  // Return the list of call logs
    }

    // Prints the call logs to Logcat
    private fun printCallLogs(callLogs: List<Map<String, String>>) {
        // Log each call log entry
        callLogs.forEach { callLog ->
            Log.d("CallLogObserver", "Call Log: $callLog")  // Print each call log detail
        }
    }
}
