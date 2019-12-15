package com.kastapp.sample.di

import com.kastapp.sample.data.repository.FeedRepository
import com.kastapp.sample.data.repository.UserRepository
import org.koin.dsl.module

object RepositoryModule {
    val module = module {
        single { UserRepository(get(), get()) }
        single { FeedRepository(get(), get(), get()) }
    }
}
