package app.mousike.android.components.ui.screens.bridge

import app.mousike.android.privacy.JournalPermission
import app.mousike.android.privacy.PermissionEventKind
import app.mousike.android.privacy.PermissionFeature
import app.mousike.android.privacy.PermissionUsageJournal
import app.mousike.android.ui.components.applyInterfaceLayoutDirection
import com.journeyapps.barcodescanner.CameraPreview
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class PairingCaptureActivity : CaptureActivity() {
    internal val cameraStateListener = object : CameraPreview.StateListener {
        override fun previewStarted() {
            PermissionUsageJournal.record(
                this@PairingCaptureActivity,
                PermissionFeature.PC_PAIRING,
                JournalPermission.CAMERA,
                PermissionEventKind.USED,
            )
        }

        override fun previewSized() = Unit
        override fun previewStopped() = Unit
        override fun cameraError(error: Exception) = Unit
        override fun cameraClosed() = Unit
    }

    override fun initializeContent(): DecoratedBarcodeView = super.initializeContent().also {
        window.decorView.applyInterfaceLayoutDirection()
        it.barcodeView.addStateListener(cameraStateListener)
    }
}
