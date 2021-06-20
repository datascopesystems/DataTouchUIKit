package datatouch.uikitapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import datatouch.uikit.components.wizard.FTestWizard
import datatouch.uikitapp.signalingexample.ASignalTest


class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val btn = findViewById<View>(R.id.btn)

        btn?.setOnClickListener {
            FTestWizard().show(supportFragmentManager)
        }

        val btnShowSignalingExample = findViewById<View>(R.id.btnShowSignalingActivity)
        btnShowSignalingExample?.setOnClickListener {
            val intent = Intent(this, ASignalTest::class.java)
            ASignalTest.setArgs(intent, "ABCDEFG1234567")
            startActivity(intent)
        }
    }

}