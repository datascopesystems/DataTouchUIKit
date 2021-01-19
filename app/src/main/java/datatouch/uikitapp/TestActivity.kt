package datatouch.uikitapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import datatouch.uikit.components.camera.utils.CameraUtils
import datatouch.uikit.components.toast.ToastNotification
import kotlinx.android.synthetic.main.activity_main.*


class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)

        btnTest?.setOnClickListener {
            CameraUtils.openPhotoCamera(
                this, null
            ) {
                ToastNotification.showError(this, "Photo captured final callback!")
            }
        }

    }

}