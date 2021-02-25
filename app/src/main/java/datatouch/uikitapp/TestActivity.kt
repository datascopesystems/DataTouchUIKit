package datatouch.uikitapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import datatouch.uikit.components.camera.utils.CameraUtils
import datatouch.uikit.components.toast.ToastNotification
import datatouch.uikit.core.extensions.ImageViewExtensions.showLocalImage
import kotlinx.android.synthetic.main.activity_main.*


class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        btn?.setOnClickListener {
            CameraUtils.openPhotoCamera(this, callback = {
                iv.showLocalImage(it.savedFile)
                ToastNotification.showSuccess(this, "Saved")
            })
        }

    }

}