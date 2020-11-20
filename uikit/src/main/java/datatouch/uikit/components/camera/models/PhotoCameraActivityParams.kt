package datatouch.uikit.components.camera.models

import datatouch.uikit.components.camera.activities.PhotoCameraActivity
import datatouch.uikit.core.files.Files
import java.io.File


class PhotoCameraActivityParams(saveDir: File) : CameraActivityParams(saveDir) {

    fun newPhotoJpgFile(): File {
        saveDir.mkdirs()
        return File(saveDir, Files.randomFileName + ".jpg")
    }

    override val key get() = Key

    override val cameraActivityClass get() = PhotoCameraActivity::class.java

    companion object {
        const val Key = "CameraPhotoActivityParamsKey"
    }

}