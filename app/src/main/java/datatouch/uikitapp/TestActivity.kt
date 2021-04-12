package datatouch.uikitapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import datatouch.uikit.components.camera.utils.CameraUtils
import kotlinx.android.synthetic.main.activity_main.*


class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        tv.setText("Testik Testik Testik")
        blSample.setOnClickListener {
            blSample.showLoadingState()
        }
        btnDeleteComment?.setOnClickListener { CameraUtils.openPhotoCamera(this) }

    }

}