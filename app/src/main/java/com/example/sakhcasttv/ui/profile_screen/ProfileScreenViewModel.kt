package com.example.sakhcasttv.ui.profile_screen

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

                val connection = URL(baseUrl).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 30000
                connection.readTimeout = 60000
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw IOException("HTTP error code: $responseCode")
                }

                val contentLength = connection.contentLength

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

                speedMbps
            } catch (e: Exception) {
                -1.0
            }
        }
    }
}