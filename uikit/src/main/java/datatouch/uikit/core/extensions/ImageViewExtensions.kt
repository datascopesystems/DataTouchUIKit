package datatouch.uikit.core.extensions

import android.widget.ImageView
import datatouch.uikit.R
import datatouch.uikit.core.utils.imaging.ImageUtils
import java.io.File

object ImageViewExtensions {

    fun ImageView.showImageResource(resId: Int) =
        ImageUtils.setImageResource(resId, this)

    fun ImageView.showLocalImage(imagePath: String?, errorResId: Int = R.drawable.image_not_found) =
        showLocalImage(File(imagePath.orEmpty()), errorResId)

    fun ImageView.showLocalImage(file: File, errorResId: Int = R.drawable.image_not_found) =
        ImageUtils.loadImageFile(context, file, this, errorResId)

    fun ImageView.showLocalImageRoundedCorners(file: File, errorResId: Int = R.drawable.image_not_found) =
        ImageUtils.loadImageFileRoundedCorners(context, file, this, errorResId)

    fun ImageView.showRemoteImage(imageUrl: String?, errorResId: Int = R.drawable.image_not_found) =
        ImageUtils.loadImageFile(context, imageUrl, this, errorResId)

}