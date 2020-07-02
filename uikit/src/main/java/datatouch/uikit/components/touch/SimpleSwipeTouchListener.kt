package datatouch.uikit.components.touch


import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import datatouch.uikit.interfaces.UiJustCallback
import datatouch.uikit.interfaces.UiJustValue
import kotlin.math.abs

open class SimpleSwipeTouchListener(c: Context?,
                                    private var touchCoordinatorProvider: UiJustValue<ViewTouchCoordinator?> = { SimpleTouchCoordinator() },
                                    private var onLongClickCallback: UiJustCallback? = null,
                                    private var onClickCallback: UiJustCallback? = null
) : OnTouchListener {
    private val gestureDetector: GestureDetector
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(motionEvent)
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            onClick()
            return super.onSingleTapUp(e)
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            onDoubleClick()
            return super.onDoubleTap(e)
        }

        override fun onLongPress(e: MotionEvent) {
            onLongClick()
            super.onLongPress(e)
        }

        // Determines the fling velocity and then fires the appropriate swipe event accordingly
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            val result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                    }
                } else {
                    if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeDown()
                        } else {
                            onSwipeUp()
                        }
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return result
        }


    }

    open fun onSwipeRight() {}
    open fun onSwipeLeft() {}
    open fun onSwipeUp() {}
    open fun onSwipeDown() {}
    open fun onClick() {
        onClickCallback?.invoke()
    }

    open fun onDoubleClick() {}
    open fun onLongClick() {
        if (touchCoordinatorProvider.invoke()?.isLongClickAllowed() == true)
            onLongClickCallback?.invoke()
    }

    init {
        gestureDetector = GestureDetector(c, GestureListener())
    }
}

private const val SWIPE_THRESHOLD = 100
private const val SWIPE_VELOCITY_THRESHOLD = 100