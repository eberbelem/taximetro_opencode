package com.taximetro

import android.app.Application
import com.taximetro.config.AppConfig
import com.taximetro.persistence.AppDatabase

class TaximetroApp : Application() {

    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getInstance(this)
        AppConfig.init(this)
    }

    companion object {
        lateinit var instance: TaximetroApp
            private set
    }
}
