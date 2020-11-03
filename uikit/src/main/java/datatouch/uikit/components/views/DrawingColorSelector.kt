package datatouch.uikit.components.views

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import datatouch.uikit.R
import datatouch.uikit.core.utils.Conditions.isNotNull
import datatouch.uikit.core.utils.animation.AnimationUtils
import kotlinx.android.synthetic.main.drawing_color_selector.view.*
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener

class DrawingColorSelector : RelativeLayout {

    private var selectedColor = 0
    private var callback: DrawingColorSelectorCallback =
        object : DrawingColorSelectorCallback {
            override fun onDrawingColorSelected(color: Int) {

            }
        }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        inflateView()
        afterView()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        inflateView()
        afterView()
    }

    fun afterView() {
        setupColors()
        btnPickColor.setOnClickListener { btnPickColor() }
        ivColour0.setOnClickListener { onColorClicked(it) }
        ivColour1.setOnClickListener { onColorClicked(it) }
        ivColour2.setOnClickListener { onColorClicked(it) }
        ivColour3.setOnClickListener { onColorClicked(it) }
        ivColour4.setOnClickListener { onColorClicked(it) }
        ivColour5.setOnClickListener { onColorClicked(it) }
        ivColour6.setOnClickListener { onColorClicked(it) }
        ivColour7.setOnClickListener { onColorClicked(it) }
        ivColour8.setOnClickListener { onColorClicked(it) }
    }

    private fun setupColors() {
        for (i in 0 until llColorsContainer!!.childCount) {
            val v = llColorsContainer!!.getChildAt(i)
            val color = Color.parseColor(v.tag.toString())
            v.background.setColorFilter(color, PorterDuff.Mode.SRC)
        }
    }


    private fun btnPickColor() {
        val dialog =
            AmbilWarnaDialog(context, selectedColor, object : OnAmbilWarnaListener {
                override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                    setDrawingColor(color)
                    callback.onDrawingColorSelected(color)
                }

                override fun onCancel(dialog: AmbilWarnaDialog) {}
            })
        dialog.show()
    }

    fun onColorClicked(view: View) {
        val imageView = view as ImageView
        selectedColor = Color.parseColor(imageView.tag.toString())
        resetSelectionOfColors()
        setSelectedStateForColor(imageView)
        animatePress(imageView)
        callback.onDrawingColorSelected(selectedColor)
    }

    private fun resetSelectionOfColors() {
        for (i in 0 until llColorsContainer!!.childCount) {
            val currentView =
                llColorsContainer!!.getChildAt(i) as ImageView
            currentView.setImageDrawable(null)
        }
    }

    private fun setSelectedStateForColor(imageView: ImageView?) {
        imageView?.setImageResource(R.drawable.ic_done_white)
    }

    private fun animatePress(view: View) {
        AnimationUtils.animate(
            view,
            AnimationUtils.AnimationTechniques.ZOOM_IN,
            BUTTON_PRESS_ANIMATION_DURATION
        )
    }

    fun setDrawingColor(color: Int) {
        selectedColor = color
        val colorImageView = findColorImageViewByColor(color)
        if (isNotNull(colorImageView)) setSelectedStateForColor(colorImageView) else resetSelectionOfColors()
    }

    private fun findColorImageViewByColor(colour: Int): ImageView? {
        val childCount = llColorsContainer!!.childCount
        for (i in 0 until childCount) {
            val currentView =
                llColorsContainer!!.getChildAt(i) as ImageView
            if (Color.parseColor(currentView.tag as String) == colour) {
                return currentView
            }
        }
        return null
    }

    protected fun inflateView() {
        View.inflate(context, R.layout.drawing_color_selector, this)
    }

    fun setCallback(callback: DrawingColorSelectorCallback) {
        if (isNotNull(callback)) this.callback = callback
    }

    interface DrawingColorSelectorCallback {
        fun onDrawingColorSelected(color: Int)
    }

    companion object {
        private const val BUTTON_PRESS_ANIMATION_DURATION = 100
    }
}