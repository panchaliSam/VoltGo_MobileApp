/**
 * ------------------------------------------------------------
 * File: NetworkModule.kt
 * Authors: Ishini Aposo & Panchali Samarasinghe
 * Date: 2025-10-10
 *
 * Description:
 * This Dagger Hilt module provides all the network-related dependencies for the VoltGo app.
 * It includes configuration for Retrofit, OkHttpClient, authentication interceptors,
 * and HTTP logging. These components ensure secure and efficient API communication
 * between the app and backend services.
 * ------------------------------------------------------------
 */

package lk.voltgo.voltgo.di

import kotlinx.coroutines.runBlocking
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import lk.voltgo.voltgo.auth.TokenManager
import lk.voltgo.voltgo.data.remote.api.AuthApiService
import lk.voltgo.voltgo.data.remote.api.StationApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://10.0.2.2:5005"

    // Provides the authentication interceptor that attaches the Bearer token to API requests.
    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val token = runBlocking { tokenManager.getToken() }

            val newRequest = if (token != null) {
                originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            } else {
                originalRequest
            }

            val response = chain.proceed(newRequest)

            // Handle 401 Unauthorized - token refresh logic would go here
            if (response.code == 401) {
                // In a real implementation, you would refresh the token here
                // and retry the request
            }

            response
        }
    }

    // Provides the OkHttpClient configured with authentication and logging interceptors.
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    // Provides the Retrofit instance used for API communication with the backend.
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Provides the HTTP logging interceptor for monitoring API request and response logs.
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    // Provides the AuthApiService for authentication-related network calls.
    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    // Provides the StationApiService for station-related network calls.
    @Provides
    @Singleton
    fun provideStationApiService(retrofit: Retrofit): StationApiService {
        return retrofit.create(StationApiService::class.java)
    }

}