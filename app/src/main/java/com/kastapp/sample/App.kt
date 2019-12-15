package com.kastapp.sample

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import com.kastapp.sample.di.ArchModule
import com.kastapp.sample.di.LocalDataModule
import com.kastapp.sample.di.NetworkModule
import com.kastapp.sample.di.RepositoryModule
import io.reactivex.plugins.RxJavaPlugins
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber
import timber.log.Timber.DebugTree

class App : MultiDexApplication(), LifecycleObserver {

    companion object {
        var appInForeground = false
            private set
    }

    override fun onCreate() {
        super.onCreate()

        initLogs()
        initDI()

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    private fun initLogs() {
        Timber.plant(if (BuildConfig.DEBUG) DebugTree() else object : Timber.Tree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                if (priority >= Log.WARN) {
                    // тут можно вызвать крашлитику
                }
            }
        })
        RxJavaPlugins.setErrorHandler { throwable -> Timber.e(throwable) }
    }

    private fun initDI() {
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(
                listOf(
                    NetworkModule.module,
                    LocalDataModule.module,
                    RepositoryModule.module,
                    ArchModule.module
                )
            )
        }
    }

    /**
     * Вызывается только когда приложение раскрывают - возврат из другого приложения, из Overview, клик по иконке запуска
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onMoveToForeground() {
        appInForeground = true
    }

    /**
     * Вызывается только когда приложение скрывают - нажатие кнопки Home, Overview, открытие другого приложения
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onMoveToBackground() {
        appInForeground = false
    }
}
