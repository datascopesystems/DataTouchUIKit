package datatouch.uikit.components.camera.models

import java.io.File
import java.io.Serializable

abstract class CameraActivityParams(protected val saveDir: File) : Serializable {

    abstract val key: String

    abstract val cameraActivityClass: Class<*>

}