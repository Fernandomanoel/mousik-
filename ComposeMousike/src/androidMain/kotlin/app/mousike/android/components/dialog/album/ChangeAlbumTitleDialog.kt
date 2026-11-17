package app.mousike.android.components.dialog.album

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import app.mousike.android.MODIFIED_PREFIX
import app.mousike.android.core.ondevice.isDeviceAlbumId
import app.mousike.android.core.ondevice.rememberDeviceGroupOriginalName
import app.mousike.android.cleanPrefix
import app.mousike.android.models.Album
import app.mousike.android.core.utils.Toaster
import app.mousike.android.R
import app.mousike.android.components.dialog.song.RenameDialog
import app.mousike.android.core.database.Database

class ChangeAlbumTitleDialog private constructor(
    activeState: MutableState<Boolean>,
    valueState: MutableState<TextFieldValue>,
    private val getAlbum: () -> Album?
) : RenameDialog(activeState, valueState) {

    companion object {
        @Composable
        operator fun invoke(getAlbum: () -> Album?): ChangeAlbumTitleDialog {
            val current = rememberUpdatedState(getAlbum)
            return remember(getAlbum()?.id) {
                ChangeAlbumTitleDialog(mutableStateOf(false), mutableStateOf(TextFieldValue())) { current.value() }
            }
        }
    }

    override val keyboardOption: KeyboardOptions = KeyboardOptions.Default
    override val iconId: Int = R.drawable.title_edit
    override val messageId: Int = R.string.metadata_action_change_title
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )
    override val dialogTitle: String
        @Composable
        get() = menuIconTitle

    override fun showDialog() {
        value = TextFieldValue(cleanPrefix(getAlbum()?.title ?: ""))
        errorMessage = ""
        super.showDialog()
    }

    override fun onSet( newValue: String ) {
        super.onSet( newValue )
        if( errorMessage.isNotEmpty() ) return

        val album = getAlbum() ?: return
        Database.asyncTransaction {
            if (isDeviceAlbumId(album.id)) rememberDeviceGroupOriginalName(album.id, album.title)
            albumTable.insertIgnore( album )
            albumTable.updateTitle( album.id, "$MODIFIED_PREFIX$newValue" )
            Toaster.done()
        }

        hideDialog()
    }
}
