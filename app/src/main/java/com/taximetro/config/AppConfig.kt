package com.taximetro.config

import android.content.Context
import java.util.Locale

object AppConfig {

    lateinit var context: Context
        private set

    val locale: Locale get() = Locale("pt", "BR")

    fun init(context: Context) {
        this.context = context.applicationContext
    }
}
