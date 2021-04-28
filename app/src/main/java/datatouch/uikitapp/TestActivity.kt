package datatouch.uikitapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import datatouch.uikit.core.utils.imaging.bitmap.MemorySafeBitmapUtils
import kotlinx.android.synthetic.main.activity_main.*


class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        btn.setOnClickListener {
            MemorySafeBitmapUtils.compressImageToBase64("loh.jpg")
        }

    }


}