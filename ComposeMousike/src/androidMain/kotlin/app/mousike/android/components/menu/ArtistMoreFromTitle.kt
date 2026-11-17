package app.mousike.android.components.menu

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.mousike.android.cleanPrefix
import app.mousike.android.R

@Composable
internal fun artistMoreFromTitle(artistName: String?): String =
    stringResource(R.string.artist_more_from_format, cleanPrefix(artistName.orEmpty()))
