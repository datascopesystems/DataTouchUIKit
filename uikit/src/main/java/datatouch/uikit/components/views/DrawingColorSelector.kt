package datatouch.uikit.components.views

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import datatouch.uikit.R
import datatouch.uikit.core.utils.Conditions.isNotNull
import datatouch.uikit.core.utils.animation.AnimationUtils
import datatouch.uikit.databinding.DrawingColorSelectorBinding
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener

class DrawingColorSelector : RelativeLayout {

    private val ui = DrawingColorSelectorBinding
        .inflate(LayoutInflater.from(context), this, true)

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
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupColors()
        ui.btnPickColor.setOnClickListener { btnPickColor() }
        ui.ivColour0.setOnClickListener { onColorClicked(it) }
        ui.ivColour1.setOnClickListener { onColorClicked(it) }
        ui.ivColour2.setOnClickListener { onColorClicked(it) }
        ui.ivColour3.setOnClickListener { onColorClicked(it) }
        ui.ivColour4.setOnClickListener { onColorClicked(it) }
        ui.ivColour5.setOnClickListener { onColorClicked(it) }
        ui.ivColour6.setOnClickListener { onColorClicked(it) }
        ui.ivColour7.setOnClickListener { onColorClicked(it) }
        ui.ivColour8.setOnClickListener { onColorClicked(it) }
    }

    private fun setupColors() {
        for (i in 0 until ui.llColorsContainer.childCount) {
            val v = ui.llColorsContainer.getChildAt(i)
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
        for (i in 0 until ui.llColorsContainer.childCount) {
            val currentView =
                ui.llColorsContainer.getChildAt(i) as ImageView
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
        val childCount = ui.llColorsContainer.childCount
        for (i in 0 until childCount) {
            val currentView =
                ui.llColorsContainer.getChildAt(i) as ImageView
            if (Color.parseColor(currentView.tag as String) == colour) {
                return currentView
            }
        }
        return null
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