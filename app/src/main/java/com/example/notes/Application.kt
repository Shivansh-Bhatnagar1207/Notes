package com.example.notes

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.concurrent.thread

class Application  : Application(),Application.ActivityLifecycleCallbacks{

    private var activityReferences = 0
    private var isActivityChangingConfigurations = false
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityStarted(activity : Activity){
        if(activityReferences == 0 && !isActivityChangingConfigurations){
            Log.d("AppLifecycle","App is in foreground")
        }
        activityReferences++
    }

    override fun onActivityStopped(activity: Activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations
        activityReferences--
        if(activityReferences == 0 && !isActivityChangingConfigurations){
            Log.d("AppLifecycle","App is in background")
        }
    }

    override fun onActivityDestroyed(activity: Activity) {}

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    }

