package app.mousike.android.utils

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import okhttp3.OkHttpClient
import app.mousike.android.core.network.client.NetworkClientFactory
import app.mousike.innertube.Innertube

val Context.okHttpDataSourceFactory
    @OptIn(UnstableApi::class)
    get() = DefaultDataSource.Factory(
        this,
        OkHttpDataSource.Factory { request -> okHttpClient().newCall(request) }
            .apply {
                val headers = mutableMapOf<String, String>()
                Innertube.cookie?.let {
                    headers["Cookie"] = it
                }
                setDefaultRequestProperties(headers)
            }
    )

private fun okHttpClient(): OkHttpClient {
    return NetworkClientFactory.getCachelessClient()
}
