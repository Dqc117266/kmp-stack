package com.dqc.kit.network.di

import com.dqc.kit.datastore.domain.repository.PreferencesRepository
import com.dqc.kit.network.auth.AuthManager
import com.dqc.kit.network.auth.PreferencesTokenProvider
import com.dqc.kit.network.auth.TokenProvider
import com.dqc.kit.network.core.createHttpClient
import com.dqc.kit.network.core.getPlatformEngine
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Core network module for Koin DI with authentication support
 *
 * @param baseUrl API base URL
 * @param isDebug Enable debug logging
 * @param enableAuth Enable authentication (default: true)
 */
fun coreNetworkModule(
    baseUrl: String,
    isDebug: Boolean = false,
    enableAuth: Boolean = true
): Module = module {

    // Platform engine
    single { getPlatformEngine() }

    // Token provider using DataStore for persistence (if auth enabled)
    if (enableAuth) {
        single<TokenProvider> {
            PreferencesTokenProvider(
                preferences = get<PreferencesRepository>()
            )
        }

        // Auth manager for handling login/logout operations
        single { AuthManager(tokenProvider = get()) }
    }

    // HttpClient with auth support
    single {
        createHttpClient(
            engine = get(),
            baseUrl = baseUrl,
            isDebug = isDebug,
            tokenProvider = if (enableAuth) get() else null
        )
    }
}