package datatouch.uikit.components.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSeekBar
import datatouch.uikit.R

class SeekBarHint : AppCompatSeekBar, OnSeekBarChangeListener {
    private var mPopupWidth = 0
    private var mPopupStyle = 0
    private var mPopup: PopupWindow? = null
    private var mPopupTextView: TextView? = null
    private var mYLocationOffset = 0
    private var mInternalListener: OnSeekBarChangeListener? = null
    private var mExternalListener: OnSeekBarChangeListener? = null
    private var mProgressChangeListener: OnSeekBarHintProgressChangeListener? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(
        context: Context,
        attrs: AttributeSet?
    ) {
        setOnSeekBarChangeListener(this)
        val a = context.obtainStyledAttributes(attrs, R.styleable.SeekBarHint)
        mPopupWidth = a.getDimension(
            R.styleable.SeekBarHint_popupWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT.toFloat()
        ).toInt()
        mYLocationOffset = a.getDimension(R.styleable.SeekBarHint_yOffset, 0f).toInt()
        mPopupStyle =
            a.getInt(R.styleable.SeekBarHint_popupStyle, POPUP_FOLLOW)
        a.recycle()
        initHintPopup()
    }

    private fun initHintPopup() {
        var popupText: String? = null
        if (mProgressChangeListener != null) {
            popupText = mProgressChangeListener!!.onHintTextChanged(this, progress)
        }
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        @SuppressLint("InflateParams") val undoView =
            inflater.inflate(R.layout.popup, null)
        mPopupTextView = undoView.findViewById<View>(R.id.text) as TextView
        mPopupTextView!!.text = popupText ?: progress.toString()
        mPopup = PopupWindow(undoView, mPopupWidth, ViewGroup.LayoutParams.WRAP_CONTENT, false)
    }

    private fun showPopup() {
        if (mPopupStyle == POPUP_FOLLOW) {
            mPopup!!.showAtLocation(
                this,
                Gravity.START or Gravity.BOTTOM,
                (this.x + getXPosition(this).toInt()).toInt(),
                (this.y + mYLocationOffset + this.height).toInt()
            )
        }
        if (mPopupStyle == POPUP_FIXED) {
            mPopup!!.showAtLocation(
                this,
                Gravity.CENTER or Gravity.BOTTOM,
                0,
                (this.y + mYLocationOffset + this.height).toInt()
            )
        }
    }

    private fun hidePopup() {
        if (mPopup!!.isShowing) {
            mPopup!!.dismiss()
        }
    }

    override fun setOnSeekBarChangeListener(l: OnSeekBarChangeListener) {
        if (mInternalListener == null) {
            mInternalListener = l
            super.setOnSeekBarChangeListener(l)
        } else {
            mExternalListener = l
        }
    }

    fun setOnProgressChangeListener(l: OnSeekBarHintProgressChangeListener?) {
        mProgressChangeListener = l
    }

    override fun onProgressChanged(
        seekBar: SeekBar,
        progress: Int,
        b: Boolean
    ) {
        var popupText: String? = null
        if (mProgressChangeListener != null) {
            popupText = mProgressChangeListener!!.onHintTextChanged(this, getProgress())
        }
        if (mExternalListener != null) {
            mExternalListener!!.onProgressChanged(seekBar, progress, b)
        }
        mPopupTextView!!.text = popupText ?: progress.toString()
        if (mPopupStyle == POPUP_FOLLOW) {
            mPopup!!.update(
                (this.x + getXPosition(seekBar).toInt()).toInt(),
                (this.y + mYLocationOffset + this.height).toInt(),
                -1,
                -1
            )
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        if (mExternalListener != null) {
            mExternalListener!!.onStartTrackingTouch(seekBar)
        }
        showPopup()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        if (mExternalListener != null) {
            mExternalListener!!.onStopTrackingTouch(seekBar)
        }
        hidePopup()
    }

    private fun getXPosition(seekBar: SeekBar): Float {
        val `val` =
            seekBar.progress
                .toFloat() * (seekBar.width - 2 * seekBar.thumbOffset).toFloat() / seekBar.max
        return `val` + seekBar.thumbOffset - mPopupWidth / 2.0f
    }

    interface OnSeekBarHintProgressChangeListener {
        fun onHintTextChanged(seekBarHint: SeekBarHint?, progress: Int): String?
    }

    companion object {
        const val POPUP_FIXED = 1
        const val POPUP_FOLLOW = 0
    }
}