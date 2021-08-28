package com.thazin.moviestore.application

import android.app.Application
import androidx.work.*
import com.thazin.moviestore.utils.ResponseHandler
import com.thazin.moviestore.worker.RefreshMoviesWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MovieStoreApp : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: MovieStoreApp? = null
        fun getInstance(): MovieStoreApp {
            synchronized(MovieStoreApp::class.java) {
                if (instance == null)
                    instance =
                        MovieStoreApp()
            }
            return instance!!
        }
    }

    private var responseHandler: ResponseHandler? = null
    fun getResponseHandler(): ResponseHandler {
        synchronized(MovieStoreApp::class.java) {
            if (responseHandler == null)
                responseHandler = ResponseHandler()
        }
        return responseHandler!!
    }

    override fun onCreate() {
        super.onCreate()
        setupWorker()
    }

    private fun setupWorker() {
        CoroutineScope(Dispatchers.IO).launch {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(false)
                .setRequiresCharging(false)
                .build()
            val periodicWorkRequest =
                PeriodicWorkRequestBuilder<RefreshMoviesWorker>(repeatInterval = 1, repeatIntervalTimeUnit = TimeUnit.DAYS)
                    .setConstraints(constraints)
                    .build()
            WorkManager.getInstance().enqueueUniquePeriodicWork(
                RefreshMoviesWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
            )
        }
    }
}