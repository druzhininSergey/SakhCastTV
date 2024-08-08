package com.example.sakhcasttv.ui.profile_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakhcasttv.data.repository.SakhCastRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sakhCastRepository: SakhCastRepository
) : ViewModel() {

    private val _downloadSpeed = MutableStateFlow<Double?>(null)
    val downloadSpeed: StateFlow<Double?> = _downloadSpeed.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun measureDownloadSpeed() {
        viewModelScope.launch {
            _isLoading.value = true
            val speedTestInfo = sakhCastRepository.getSpeedtestUrl()
            Log.d("SpeedTest", "Received base URL: ${speedTestInfo?.url}")
            speedTestInfo?.let {
                val speed = performDownloadTest(it.url)
                _downloadSpeed.value = speed
            }
            _isLoading.value = false
        }
    }

    private suspend fun performDownloadTest(baseUrl: String): Double {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("SpeedTest", "Attempting to download from: $baseUrl")

                val connection = URL(baseUrl).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 30000
                connection.readTimeout = 60000
                connection.connect()

                val responseCode = connection.responseCode
                Log.d("SpeedTest", "HTTP Response Code: $responseCode")

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw IOException("HTTP error code: $responseCode")
                }

                val contentLength = connection.contentLength
                Log.d("SpeedTest", "Content Length: $contentLength")

                val inputStream = connection.inputStream
                val buffer = ByteArray(8192)
                var bytesRead: Int
                var totalBytesRead = 0L
                val startTime = System.currentTimeMillis()

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    totalBytesRead += bytesRead
                    if (contentLength in 1..totalBytesRead) break
                }

                val endTime = System.currentTimeMillis()
                val duration = (endTime - startTime) / 1000.0
                val speedMbps = (totalBytesRead * 8 / 1000000.0) / duration

                Log.d("SpeedTest", "Download completed. Speed: $speedMbps Mbps")
                speedMbps
            } catch (e: Exception) {
                Log.e("SpeedTest", "Error during speed test", e)
                -1.0
            }
        }
    }
}