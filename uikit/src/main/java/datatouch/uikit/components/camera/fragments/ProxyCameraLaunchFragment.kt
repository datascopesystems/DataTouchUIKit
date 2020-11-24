package datatouch.uikit.components.camera.fragments

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import datatouch.uikit.components.camera.models.CameraActivityResult
import datatouch.uikit.components.camera.models.CameraSavedContent
import datatouch.uikit.components.camera.utils.CameraContentSavedCallback

class ProxyCameraLaunchFragment(private val fm : FragmentManager,
                                private val key : String,
                                private val callback: CameraContentSavedCallback?) : Fragment() {

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        fm.beginTransaction().remove(this).commit()

        if (resultCode == Activity.RESULT_OK) {
            val result = data?.getSerializableExtra(key) as? CameraActivityResult?
            result?.let { callback?.invoke(CameraSavedContent(result.savedFile)) }
        }
    }

}