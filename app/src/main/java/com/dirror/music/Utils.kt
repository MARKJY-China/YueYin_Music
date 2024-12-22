package com.dirror.music

// Utils.kt

import android.app.Activity
import android.content.Context
import android.os.Environment
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

fun <T : View> Activity.id(@IdRes id: Int): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) { findViewById(id) }
}

fun <T : View> Fragment.id(@IdRes id: Int): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) { requireView().findViewById(id) }
}

val downloadDir get() = App.context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)

fun Context.suspendLaunch(block: suspend () -> Unit) =
    (this as? AppCompatActivity)?.lifecycleScope?.launch {
        block()
    }
