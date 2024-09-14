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

class CallLogObserver(context: Context, handler: Handler) : ContentObserver(handler) {
    private val context: Context = context

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        Log.d("CallLogObserver", "Call log changed")
        // Fetch new call logs and print them
        GlobalScope.launch {
            val callLogs = getCallDetails()
            printCallLogs(callLogs)
        }
    }

    private fun getCallDetails(): List<Map<String, String>> {
        val callLogsList = mutableListOf<Map<String, String>>()
        val cursor = context.contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, null)

        cursor?.use {
            val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
            val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
            val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
            val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)

            while (it.moveToNext()) {
                val callLog = mapOf(
                    "number" to it.getString(numberIndex),
                    "type" to it.getString(typeIndex),
                    "date" to it.getString(dateIndex),
                    "duration" to it.getString(durationIndex)
                )
                callLogsList.add(callLog)
            }
        }

        return callLogsList
    }

    private fun printCallLogs(callLogs: List<Map<String, String>>) {
        // Print each call log to Logcat
        callLogs.forEach { callLog ->
            Log.d("CallLogObserver", "Call Log: $callLog")
        }
    }
}
