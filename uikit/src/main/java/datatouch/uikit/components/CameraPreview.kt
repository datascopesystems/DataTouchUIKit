package datatouch.uikit.components

import android.content.Context
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.IOException

class CameraPreview(
    context: Context?,
    private var mCamera: Camera?
) : SurfaceView(context), SurfaceHolder.Callback {
    private val mHolder: SurfaceHolder
    var isPreviewRunning = false
        private set

    override fun surfaceCreated(holder: SurfaceHolder) {
        try {
            // create the surface and start camera preview
            if (mCamera != null) {
                mCamera!!.setPreviewDisplay(holder)
                mCamera!!.startPreview()
                isPreviewRunning = true
            }
        } catch (e: IOException) {
        }
    }

    fun refreshCamera(camera: Camera?) {
        if (mHolder.surface == null) {
            // preview surface does not exist
            return
        }
        // stop preview before making changes
        try {
            mCamera!!.stopPreview()
            isPreviewRunning = false
        } catch (e: Exception) {
            // ignore: tried to stop a non-existent preview
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        setCamera(camera)
        try {
            mCamera!!.setPreviewDisplay(mHolder)
            mCamera!!.startPreview()
            isPreviewRunning = true
        } catch (e: Exception) {
        }
    }

    override fun surfaceChanged(
        holder: SurfaceHolder,
        format: Int,
        w: Int,
        h: Int
    ) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        refreshCamera(mCamera)
    }

    fun setCamera(camera: Camera?) {
        //method to set a camera instance
        mCamera = camera
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mCamera!!.release()
        isPreviewRunning = false
    }

    init {
        mHolder = holder
        mHolder.addCallback(this)
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }
}