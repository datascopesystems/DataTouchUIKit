package datatouch.uikit.components.floatingrevealmenu.helper

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import datatouch.uikit.R
import datatouch.uikit.components.floatingrevealmenu.enums.Direction

class ViewHelper(private val mContext: Context) {
    private val SHEET_REVEAL_OFFSET_Y = 5
    private var const_val = 1

    //common layout parameter
    private var matchParams: ViewGroup.LayoutParams? = null
    private var wrapParams: ViewGroup.LayoutParams? = null
    fun generateBaseView(): CardView {
        //Base view
        val mBaseView = CardView(mContext)
        mBaseView.layoutParams = matchParams
        mBaseView.cardElevation = dpToPx(mContext, 5).toFloat()
        mBaseView.radius = mContext.resources.getDimension(R.dimen.card_radius)
        return mBaseView
    }

    fun generateMenuView(enableNestedScrolling: Boolean): RecyclerView {
        //Create menu view
        val mMenuView = RecyclerView(mContext)
        mMenuView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        mMenuView.setBackgroundColor(Color.TRANSPARENT)
        mMenuView.layoutParams = matchParams
        val padding = dpToPx(mContext, 10)
        mMenuView.setPadding(padding, padding, padding, padding)
        mMenuView.isNestedScrollingEnabled = enableNestedScrolling
        return mMenuView
    }

    fun generateRevealView(): LinearLayout {
        //Reveal view
        val mRevealView = LinearLayout(mContext)
        mRevealView.setBackgroundColor(Color.TRANSPARENT)
        mRevealView.layoutParams = wrapParams
        mRevealView.visibility = View.INVISIBLE
        return mRevealView
    }

    fun generateOverlayView(): FrameLayout {
        //Overlay view
        val mOverlayLayout = FrameLayout(mContext)
        mOverlayLayout.layoutParams = matchParams
        mOverlayLayout.animate().alpha(0f)
        mOverlayLayout.visibility = View.GONE
        mOverlayLayout.isClickable = true
        return mOverlayLayout
    }

    fun setLayoutParams(mView: View) {
        mView.layoutParams = matchParams
    }

    /**
     * Aligns the sheet's position with the FAB.
     */
    fun alignMenuWithFab(
        mFab: View,
        mRevealView: View,
        mDirection: Direction
    ) {
        val sheetLayoutParams =
            mRevealView.layoutParams as MarginLayoutParams
        val fabLayoutParams = mFab.layoutParams as MarginLayoutParams

        //adjust sheet margin
        //TODO : Warning, this will only work for direction UP!!!
        sheetLayoutParams.setMargins(
            fabLayoutParams.leftMargin + mFab.width - 50,
            fabLayoutParams.topMargin,
            fabLayoutParams.rightMargin,
            fabLayoutParams.bottomMargin + mFab.height
        )

        // Get FAB's coordinates
        val fabCoords = IntArray(2)
        mFab.getLocationOnScreen(fabCoords)


        // Get sheet's coordinates
        val sheetCoords = IntArray(2)
        mRevealView.getLocationOnScreen(sheetCoords)
        val leftDiff =
            Math.max(sheetCoords[0] - fabCoords[0], fabLayoutParams.leftMargin)
        val rightDiff = Math.min(
            sheetCoords[0] + mRevealView.width - (fabCoords[0] + mFab.width),
            -fabLayoutParams.rightMargin
        )
        val topDiff =
            Math.max(sheetCoords[1] - fabCoords[1], fabLayoutParams.topMargin)
        val bottomDiff = Math.min(
            sheetCoords[1] + mRevealView.height - (fabCoords[1] + mFab.height),
            -fabLayoutParams.bottomMargin
        )
        val sheetX = mRevealView.x
        val sheetY = mRevealView.y
        if (mDirection == Direction.LEFT || mDirection == Direction.UP) {
            // Align the right side of the sheet with the right side of the FAB
            mRevealView.x = sheetX - rightDiff - sheetLayoutParams.rightMargin
            // Align the bottom of the sheet with the bottom of the FAB
            mRevealView.y = sheetY - bottomDiff - sheetLayoutParams.bottomMargin
        } else if (mDirection == Direction.RIGHT) {
            // align the left side of the sheet with the left side of the FAB
            mRevealView.x = sheetX - leftDiff + sheetLayoutParams.leftMargin
            // Align the bottom of the sheet with the bottom of the FAB
            mRevealView.y = sheetY - bottomDiff - sheetLayoutParams.bottomMargin
        } else if (mDirection == Direction.DOWN) {
            // align the top of the sheet with the top of the FAB
            mRevealView.y = sheetY - topDiff + sheetLayoutParams.topMargin
            // Align the right side of the sheet with the right side of the FAB
            mRevealView.x = sheetX - rightDiff - sheetLayoutParams.rightMargin
        }
    }

    fun getSheetRevealCenterX(
        view: View,
        mDirection: Direction
    ): Int {
        return if (mDirection == Direction.LEFT) (view.x + view.width / 2 + view.width * const_val).toInt() else if (mDirection == Direction.RIGHT) (view.x + view.width / 2 - view.width * const_val).toInt() else (view.x + view.width / 2).toInt()
    }

    fun getSheetRevealCenterY(
        view: View,
        mDirection: Direction
    ): Int {
        return if (mDirection == Direction.UP) (view.y + view.height / 2 + view.height * const_val).toInt() else if (mDirection == Direction.DOWN) (view.y + view.height / 2 - view.height * const_val).toInt() else (view.y + view.height / 2).toInt()
    }

    fun updateFabAnchor(mFabView: View): Point {
        // Update the anchor with the current translation
        return setFabAnchor(mFabView, mFabView.translationX, mFabView.translationY)
    }

    private fun setFabAnchor(
        mFabView: View,
        translationX: Float,
        translationY: Float
    ): Point {
        val anchorX = Math
            .round(mFabView.x + mFabView.width / 2 + (translationX - mFabView.translationX))
        val anchorY = Math
            .round(mFabView.y + mFabView.height / 2 + (translationY - mFabView.translationY))
        return Point(anchorX, anchorY)
    }

    private fun dpToPx(mContext: Context, dp: Int): Int {
        val displayMetrics = mContext.resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    fun pxToDp(mContext: Context, px: Int): Int {
        val displayMetrics = mContext.resources.displayMetrics
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    init {
        matchParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        wrapParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        const_val = ((SHEET_REVEAL_OFFSET_Y - 1.5) / SHEET_REVEAL_OFFSET_Y).toInt()
    }
}