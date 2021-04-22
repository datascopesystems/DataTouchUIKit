package datatouch.uikitapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import datatouch.uikit.components.toast.ToastNotification
import datatouch.uikit.core.utils.imaging.bitmap.MemorySafeBitmapUtils
import kotlinx.android.synthetic.main.activity_main.*


class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        btn.setOnClickListener {
            ToastNotification.showError(this, "||Test")

            for (i in 0..100) {
                val s =
                    MemorySafeBitmapUtils.compressImageToBase64("/storage/sdcard0/DCIM/macos-high-sierra-5120x2880-stock-landscape-5k-hd-7872.jpg")
                s.trim()
            }

        }

    }


}