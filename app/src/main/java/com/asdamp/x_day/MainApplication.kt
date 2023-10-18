package com.asdamp.x_day

import android.annotation.TargetApi
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import com.asdamp.widget.XdayWidgetProvider
import com.google.android.gms.ads.MobileAds
import com.google.android.material.color.DynamicColors
import com.jakewharton.threetenabp.AndroidThreeTen
import com.pixplicity.easyprefs.library.Prefs
import timber.log.Timber

class MainApplication : Application() {
    override fun onTerminate() {
        Costanti.chiudiDB()
        super.onTerminate()
    }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Prefs.Builder()
            .setContext(this)
            .setMode(MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()
        AndroidThreeTen.init(this)
        MobileAds.initialize(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                "dates",
                getString(R.string.notification_channel_name_dates),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(notificationChannel)
        }
        initSingletons()

    }

    protected fun initSingletons() {
        Costanti.inizializza(this)
        Costanti.getDB().apri()
    }

    protected fun isPackageInstalled(packageName: String?): Boolean {
        try {
            packageManager.getPackageInfo(packageName!!, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
        return true
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun aggiornaWidget() {
        if (Costanti.getOsVersion() >= 11) {
            val appwidgetmanager = AppWidgetManager
                .getInstance(this)
            appwidgetmanager.notifyAppWidgetViewDataChanged(
                appwidgetmanager
                    .getAppWidgetIds(
                        ComponentName(
                            this,
                            XdayWidgetProvider::class.java
                        )
                    ), R.id.list_view_widget
            )
        }
    }

    companion object {
        val isMoreThenICS: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
        val isMoreThenGB: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
    }
}