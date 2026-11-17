package app.mousike.android.updater.models

import app.mousike.android.BuildConfig

/**
 * GitHub endpoints of the mousike repository: the releases API, the APK
 * download URLs, the changelog raw files (in-app updater) and the public
 * repo links (About screen).
 *
 * Migrated from the previous update endpoint repository,
 * which has been removed.
 */
object UpdateEndpoints {

    const val GITHUB = "https://github.com"

    private const val GITHUB_API = "https://api.github.com"
    private const val GITHUB_RAW = "https://raw.githubusercontent.com"
    private const val OWNER = "Fernandomanoel"

    const val REAL_OWNER = "NEVARLeVrai"

    const val REPO = "$OWNER/mousik-"

    const val REPO_URL = "$GITHUB/$REPO"

    const val RELEASES_API_URL = "$GITHUB_API/repos/$REPO/releases"

    const val RELEASE_DOWNLOAD_URL = "$REPO_URL/releases/download/v"

    const val CHANGELOGS_PATH = "Updater/changelogs"

    const val CHANGELOGS_URL = "$GITHUB_RAW/$REPO/main/$CHANGELOGS_PATH"
}
