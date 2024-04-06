package com.apextech.bexatobotuz.di

import com.apextech.bexatobotuz.useCase.HistoryUseCase
import com.apextech.bexatobotuz.useCase.TranslateUseCase
import com.apextech.bexatobotuz.useCase.impl.HistoryUseCaseImpl
import com.apextech.bexatobotuz.useCase.impl.TranslateUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface UseCaseModule {

    @Binds
    fun getTranslate(useCaseImpl: TranslateUseCaseImpl): TranslateUseCase
    @Binds
    fun getHistory(useCaseImpl: HistoryUseCaseImpl): HistoryUseCase

}