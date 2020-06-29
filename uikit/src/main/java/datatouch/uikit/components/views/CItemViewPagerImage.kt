package datatouch.uikit.components.views

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import datatouch.uikit.R
import kotlinx.android.synthetic.main.view_pager_image_item.view.*

class CItemViewPagerImage : RelativeLayout {

    var bitmapImage: Bitmap? = null

    constructor(context: Context?) : super(context) {
        inflateView()
        afterViews()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet
    ) : super(context, attrs) {
        inflateView()
        afterViews()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        inflateView()
        afterViews()
    }

    fun onLoadingFinished(success: Boolean) {
        pbLoading?.visibility = View.GONE
        ibZoom.setVisibility(if (success) View.VISIBLE else View.GONE)
    }

    fun onLoadingStart() {
        tvInfoText?.setText(R.string.loading)
        pbLoading?.visibility = View.VISIBLE
    }

    fun afterViews() {
        setBackgroundColor(context.resources.getColor(R.color.default_screen_bg))
    }

    protected fun inflateView() {
        View.inflate(context, R.layout.view_pager_image_item, this)
    }


}