package dev.finio.auth.di

import dev.finio.auth.data.remote.AuthRemoteDataSource
import dev.finio.auth.data.remote.createHttpClient
import dev.finio.auth.domain.repository.AuthRepository
import dev.finio.auth.domain.repository.AuthRepositoryImpl
import dev.finio.auth.presentation.AuthViewModel
import dev.finio.auth.storage.createTokenStorage
import org.koin.core.module.Module
import org.koin.dsl.module

fun authModule(baseUrl: String): Module = module{
    single { createHttpClient() }
    single { createTokenStorage() }
    single { AuthRemoteDataSource(client = get(), baseUrl = baseUrl) }
    single<AuthRepository>{
        AuthRepositoryImpl(
            remoteDataSource = get(),
            tokenStorage = get()
        )
    }

    factory { AuthViewModel(repository = get()) }
}