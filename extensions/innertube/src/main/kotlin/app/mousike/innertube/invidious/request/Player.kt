package app.mousike.innertube.invidious.request

import io.ktor.http.HttpMethod
import app.mousike.innertube.utils.runCatchingNonCancellable
import app.mousike.innertube.common.HttpFetcher
import app.mousike.innertube.invidious.Invidious
import app.mousike.innertube.invidious.response.PlayerResponse

suspend fun Invidious.player( videoId: String ): Result<PlayerResponse?>? =
    runCatchingNonCancellable {
        HttpFetcher.asyncMultiRequestGetFirstValid<PlayerResponse>(
            HttpMethod.Get,
            reachableInstances,
            "/api/v1/videos/$videoId"
        ) { _, hostUrl ->
            /**
             * This is a failsafe + performance boost.
             * If a website returns unusable responses, it will
             * be blacklisted to prevent from future use.
             *
             * This in turn makes the time it takes to fetch
             * data in the future shorter because there's less
             * link to fetch, also less error to check.
             */
            blacklistUrl( hostUrl )
            null
        }
    }