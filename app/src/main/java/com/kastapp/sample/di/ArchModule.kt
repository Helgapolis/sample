package com.kastapp.sample.di

import com.kastapp.sample.ui.feed.FeedListViewModel
import com.kastapp.sample.ui.user.UserInfoViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ArchModule {
    val module = module {
        viewModel { FeedListViewModel(get()) }
        viewModel { UserInfoViewModel(get()) }
    }
}
