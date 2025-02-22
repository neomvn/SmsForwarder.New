package com.idormy.sms.forwarder.utils

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.idormy.sms.forwarder.App

data class AppInfo(
    val name: String,
    val icon: Drawable,
    val packageName: String,
    val packagePath: String,
    val versionName: String,
    val versionCode: Int,
    val isSystem: Boolean,
    val uid: Int
)

@SuppressLint("StaticFieldLeak")
@Suppress("DEPRECATION")
object AppUtils {

    fun getAppsInfo(): List<AppInfo> {
        val packageManager = App.context.packageManager ?: return emptyList()
        val appsInfo = mutableListOf<AppInfo>()

        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        for (app in apps) {
            try {
                val packageInfo = packageManager.getPackageInfo(app.packageName, 0)
                val appInfo = AppInfo(
                    app.loadLabel(packageManager).toString(),
                    app.loadIcon(packageManager),
                    app.packageName,
                    app.sourceDir,
                    packageInfo?.versionName ?: "Unknown",
                    packageInfo?.versionCode ?: 0,
                    (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
                    app.uid
                )
                appsInfo.add(appInfo)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }

        return appsInfo
    }

    fun getAppVersionName(): String? {
        return getAppVersionName(App.context.packageName)
    }

    fun getAppVersionName(packageName: String): String? {
        if (packageName.isBlank()) {
            return null
        }
        return try {
            val pm: PackageManager = App.context.packageManager
            val pi: PackageInfo = pm.getPackageInfo(packageName, 0)
            pi.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    fun openApp(packageName: String) {
        val packageManager = App.context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            App.context.startActivity(intent)
        }
    }

}