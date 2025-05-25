package com.shubhanya.fingenienxt.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shubhanya.fingenienxt.expense.ExpenseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Or ActivityRetainedComponent::class if you prefer
object RepositoryModule {

    @Provides
    @Singleton // Or @ActivityRetainedScoped
    fun provideExpenseRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): ExpenseRepository {
        return ExpenseRepository(firestore, auth)
    }
}
