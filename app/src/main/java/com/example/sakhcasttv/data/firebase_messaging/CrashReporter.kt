package com.example.sakhcasttv.data.firebase_messaging

interface CrashReporter {
    fun logError(message: String)
    fun setCustomKey(key: String, value: String)
    fun recordException(throwable: Throwable)
}