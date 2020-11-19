package datatouch.uikit.components.camera.activities.params

import datatouch.uikit.core.files.Files
import java.io.File
import java.io.Serializable

data class CameraPhotoActivityParams(private val saveDir: File) : Serializable {

    fun newPhotoJpgFile(): File {
        saveDir.mkdirs()
        return File(saveDir, Files.randomFileName + ".jpg")
    }

}