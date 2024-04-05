package com.apextech.bexatobotuz.di

import com.apextech.bexatobotuz.repository.CyrillLatinRepository
import com.apextech.bexatobotuz.repository.impl.CyrillLatinRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun getCyrillLatin(cyrillLatinRepositoryImpl: CyrillLatinRepositoryImpl): CyrillLatinRepository

}