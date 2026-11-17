package app.mousike.android.components.navigation.header

import app.mousike.android.utils.FaceAvatar
import app.mousike.android.utils.faceInitials
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HeaderProfileFaceTest {
    @Test
    fun `header begins with profile initials while resolved avatar loads`() {
        val face = initialHeaderProfileFace("mousike Fan")
        assertEquals("mousike Fan", face.name)
        assertEquals(FaceAvatar.Initials("mousike Fan"), face.avatar)
        assertEquals("NF", faceInitials((face.avatar as FaceAvatar.Initials).name))
    }
}
