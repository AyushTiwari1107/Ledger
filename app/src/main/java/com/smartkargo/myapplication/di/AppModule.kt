package com.smartkargo.myapplication.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.smartkargo.myapplication.data.local.SmartExpenseDatabase
import com.smartkargo.myapplication.data.local.dao.ExpenseAlertDao
import com.smartkargo.myapplication.data.local.dao.TransactionDao
import com.smartkargo.myapplication.data.repository.ExpenseAlertRepositoryImpl
import com.smartkargo.myapplication.data.repository.SettingsRepositoryImpl
import com.smartkargo.myapplication.data.repository.TransactionRepositoryImpl
import com.smartkargo.myapplication.domain.repository.ExpenseAlertRepository
import com.smartkargo.myapplication.domain.repository.SettingsRepository
import com.smartkargo.myapplication.domain.repository.TransactionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SmartExpenseDatabase =
        Room.databaseBuilder(
            context,
            SmartExpenseDatabase::class.java,
            "smart_expense_db"
        ).fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideTransactionDao(db: SmartExpenseDatabase): TransactionDao = db.transactionDao()

    @Provides
    @Singleton
    fun provideExpenseAlertDao(db: SmartExpenseDatabase): ExpenseAlertDao = db.expenseAlertDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore

    @Provides
    @Singleton
    fun provideTransactionRepository(dao: TransactionDao): TransactionRepository =
        TransactionRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideSettingsRepository(dataStore: DataStore<Preferences>): SettingsRepository =
        SettingsRepositoryImpl(dataStore)

    @Provides
    @Singleton
    fun provideExpenseAlertRepository(dao: ExpenseAlertDao): ExpenseAlertRepository =
        ExpenseAlertRepositoryImpl(dao)
}
