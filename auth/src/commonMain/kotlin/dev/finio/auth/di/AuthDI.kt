package dev.finio.auth.di

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

fun initAuth(baseUrl: String){
    stopKoin()
    startKoin {
        modules(authModule(baseUrl))
    }
}