package io.lamart.lux.sample.android

import android.app.Application
import android.content.Context
import androidx.activity.ComponentActivity
import io.lamart.lux.sample.AppActions
import io.lamart.lux.sample.AppMachine
import io.lamart.lux.sample.AppState
import kotlinx.coroutines.flow.MutableStateFlow

class App : Application() {
    val machine: AppMachine by lazy { AppMachine() }
}

val ComponentActivity.app: App
    get() = application.let { it as App }

val Context.app: App
    get() = applicationContext.let { it as App }
