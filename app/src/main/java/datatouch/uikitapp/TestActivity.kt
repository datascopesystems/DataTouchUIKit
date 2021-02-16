package datatouch.uikitapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import datatouch.uikit.core.extensions.ImageViewExtensions.showImageBase64
import kotlinx.android.synthetic.main.activity_main.*


class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        iv.showImageBase64("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")

    }

}