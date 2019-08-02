package com.haroldadmin.moonshot

import android.app.Application
import androidx.work.Configuration
import com.airbnb.epoxy.Carousel
import com.haroldadmin.moonshot.notifications.LaunchNotificationsManager
import com.haroldadmin.moonshot.notifications.workers.MoonShotWorkerFactory
import com.haroldadmin.moonshot.sync.SyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin
import kotlin.coroutines.CoroutineContext

class MoonShot : Application(), Configuration.Provider, CoroutineScope {

    companion object {
        const val MOONSHOT_SHARED_PREFS = "moonshot-shared-prefs"
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Main

    override fun getWorkManagerConfiguration(): Configuration {
        val workerFactory = get<MoonShotWorkerFactory>()
        return Configuration.Builder().setWorkerFactory(workerFactory).build()
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            logger(AndroidLogger())
            androidContext(this@MoonShot.applicationContext)
            modules(appModule)
        }

        Carousel.setDefaultGlobalSnapHelperFactory(null)

        launch {
            get<SyncManager>().enableSync()
            get<LaunchNotificationsManager>().enable()
        }
    }
}