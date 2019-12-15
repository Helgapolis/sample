package com.kastapp.sample.di

import android.content.Context
import androidx.core.net.toUri
import com.google.gson.Gson
import com.kastapp.sample.util.fromJson
import com.kastapp.sample.BuildConfig
import com.kastapp.sample.data.remote.ConnectionInterceptor
import com.kastapp.sample.data.remote.Endpoints
import com.kastapp.sample.data.remote.response.ApiResponse
import com.kastapp.sample.data.remote.response.FeedListResponse
import io.appflate.restmock.MockAnswer
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.RESTMockServerStarter
import io.appflate.restmock.android.AndroidAssetsFileParser
import io.appflate.restmock.android.AndroidLogger
import io.appflate.restmock.utils.RequestMatchers.pathContains
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    val module = module {
        single { createConnectionInterceptor(androidContext()) }
        single { createOkHttpClient(get()) }
        single { createRetrofitStub(get(), get()) }
        single { createEndpoints(get()) }
    }

    private fun createConnectionInterceptor(context: Context): ConnectionInterceptor {
        return ConnectionInterceptor(context)
    }

    private fun createOkHttpClient(connectionInterceptor: ConnectionInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(connectionInterceptor)

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }

        return builder.build()
    }

    private fun createRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }

    private fun createRetrofitStub(okHttpClient: OkHttpClient, context: Context): Retrofit {
        val fileParser = AndroidAssetsFileParser(context)
        RESTMockServerStarter.startSync(fileParser, AndroidLogger())

        RESTMockServer.whenGET(pathContains("feed/list/init"))
            .thenReturnFile(200, "api/get_feed_list_error.json")
            .thenReturnFile(200, "api/get_feed_list_init_ok.json")
            .delayBody(TimeUnit.SECONDS, 1, 3)

        RESTMockServer.whenGET(pathContains("feed/list/after"))
            .thenReturnFile(200, "api/get_feed_list_error.json")
            .thenAnswer(object : MockAnswer {
                override fun answer(request: RecordedRequest): MockResponse {
                    val gson = Gson()
                    val json = fileParser.readJsonFile("api/get_feed_list_after_ok.json")
                    val response = gson.fromJson<ApiResponse<FeedListResponse>>(json)
                    val uri = request.path!!.toUri()
                    val key = uri.getQueryParameter("key")!!.toLong()
                    val size = uri.getQueryParameter("size")!!.toInt()
                    val data = response.data.feeds.filter {
                        it.id < key
                    }.take(size)
                    return MockResponse()
                        .setBody(gson.toJson(ApiResponse(FeedListResponse(data))))
                        .setResponseCode(200)
                }
            })
            .delayBody(TimeUnit.SECONDS, 1, 5)

        RESTMockServer.whenGET(pathContains("feed/list/new"))
            .thenReturnFile(200, "api/get_feed_list_error.json")
            .thenReturnFile(200, "api/get_feed_list_new_ok.json")
            .delayBody(TimeUnit.SECONDS, 2, 2)

        RESTMockServer.whenGET(pathContains("user/info"))
            .thenReturnFile(200, "api/get_user_info_error.json")
            .thenReturnFile(200, "api/get_user_info_ok.json")
            .delayBody(TimeUnit.SECONDS, 1, 1)

        RESTMockServer.whenPOST(pathContains("user/info"))
            .thenReturnFile(200, "api/set_user_info_error.json")
            .thenReturnFile(200, "api/set_user_info_ok.json")
            .delayBody(TimeUnit.SECONDS, 5, 5)

        RESTMockServer.whenPOST(pathContains("feed/delete"))
            .thenReturnFile(200, "api/delete_feeds_error.json")
            .thenReturnFile(200, "api/delete_feeds_ok.json")
            .delayBody(TimeUnit.SECONDS, 5, 5)

        return Retrofit.Builder()
            .baseUrl(RESTMockServer.getUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }

    private fun createEndpoints(retrofit: Retrofit): Endpoints {
        return retrofit.create(Endpoints::class.java)
    }
}
