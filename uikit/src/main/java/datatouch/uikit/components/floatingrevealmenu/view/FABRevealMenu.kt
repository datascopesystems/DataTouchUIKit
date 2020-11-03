package datatouch.uikit.components.floatingrevealmenu.view

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.annotation.MenuRes
import androidx.appcompat.view.menu.MenuBuilder
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import datatouch.uikit.R
import datatouch.uikit.components.floatingrevealmenu.enums.Direction
import datatouch.uikit.components.floatingrevealmenu.helper.AnimationHelper
import datatouch.uikit.components.floatingrevealmenu.helper.ViewHelper
import datatouch.uikit.components.floatingrevealmenu.listeners.AnimationListener
import datatouch.uikit.components.floatingrevealmenu.listeners.OnFABMenuSelectedListener
import datatouch.uikit.components.floatingrevealmenu.listeners.OnMenuStateChangedListener
import datatouch.uikit.components.floatingrevealmenu.model.FABMenuItem
import datatouch.uikit.core.utils.Conditions
import java.util.*

class FABRevealMenu : FrameLayout {
    //Common constants
    private val FAB_STATE_COLLAPSED = 0
    private val FAB_STATE_EXPANDED = 1
    private val FAB_MENU_SIZE_NORMAL = 0
    private val FAB_MENU_SIZE_SMALL = 1

    @JvmField
    var menuSelectedListener: OnFABMenuSelectedListener? = null
    private var mContext: Context? = null
    var customView: View? = null
        private set
    private var mFab: View? = null

    //attributes
    @MenuRes
    private var mMenuRes = 0
    private var mMenuBackground = 0
    private var mOverlayBackground = 0
    private var mShowOverlay = false
    private var mMenuSize = 0
    private var mDirection: Direction? = null
    private var mShowTitle = false
    private var mShowIcon = false
    private var mTitleTextColor = 0
    private var mTitleDisabledTextColor = 0
    private var animateItems = false
    private var FAB_CURRENT_STATE = FAB_STATE_COLLAPSED
    private var mMenuTitleTypeface: Typeface? = null

    //Views in the menu
    private var mOverlayLayout: FrameLayout? = null
    private var mRevealView: LinearLayout? = null
    private var mMenuView: RecyclerView? = null
    private var mEnableNestedScrolling = true
    private var mBaseView: CardView? = null
    private var menuAdapter: FABMenuAdapter? = null

    //Menu specific fields
    var menuList: ArrayList<FABMenuItem>? = null
        private set

    //Helper class
    private var viewHelper: ViewHelper? = null
    private var animationHelper: AnimationHelper? = null
    private var menuStateChangedListener: OnMenuStateChangedListener? = null
    private var isRevealViewChangeDetected = true
    private var isOnLayoutComplete = false

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    private fun initView(
        context: Context,
        attrs: AttributeSet?
    ) {
        mContext = context

        //helper initialization
        viewHelper = ViewHelper(context)
        animationHelper = AnimationHelper(viewHelper!!)
        if (attrs != null) {
            val a =
                context.obtainStyledAttributes(attrs, R.styleable.FABRevealMenu, 0, 0)

            //background
            mMenuBackground = a.getColor(
                R.styleable.FABRevealMenu_menuBackgroundColor,
                getColor(R.color.colorWhite)
            )
            mOverlayBackground = a.getColor(
                R.styleable.FABRevealMenu_overlayBackground,
                getColor(R.color.colorOverlayDark)
            )

            //menu
            mMenuRes = a.getResourceId(R.styleable.FABRevealMenu_menuRes, -1)

            //custom view
            val customView = a.getResourceId(R.styleable.FABRevealMenu_menuCustomView, -1)
            if (customView != -1) this.customView =
                LayoutInflater.from(context).inflate(customView, null)

            //direction
            mDirection = Direction.fromId(
                a.getInt(
                    R.styleable.FABRevealMenu_menuDirection,
                    0
                )
            )

            //title
            mTitleTextColor = a.getColor(
                R.styleable.FABRevealMenu_menuTitleTextColor,
                getColor(android.R.color.white)
            )
            mTitleDisabledTextColor = a.getColor(
                R.styleable.FABRevealMenu_menuTitleDisabledTextColor,
                getColor(android.R.color.darker_gray)
            )
            mShowTitle = a.getBoolean(R.styleable.FABRevealMenu_showTitle, true)
            mShowIcon = a.getBoolean(R.styleable.FABRevealMenu_showIcon, true)
            mShowOverlay = a.getBoolean(R.styleable.FABRevealMenu_showOverlay, true)

            //size
            mMenuSize = a.getInt(R.styleable.FABRevealMenu_menuSize, FAB_MENU_SIZE_NORMAL)

            //animationFAB
            animateItems = a.getBoolean(R.styleable.FABRevealMenu_animateItems, true)

            //Font
            if (a.hasValue(R.styleable.FABRevealMenu_menuTitleFontFamily)) {
                val fontId = a.getResourceId(R.styleable.FABRevealMenu_menuTitleFontFamily, -1)
                if (fontId != -1) mMenuTitleTypeface = ResourcesCompat.getFont(context, fontId)
            }
            a.recycle()

            //initialization
            if (mMenuRes != -1) {
                setMenu(mMenuRes)
            } else if (this.customView != null) {
                setCustomView(this.customView!!)
            }
        }
    }

    /**
     * Set custom view as menu
     *
     * @param view custom view
     */
    fun setCustomView(view: View) {
        mMenuRes = -1
        removeAllViews()
        customView = view
        customView?.isClickable = true
        viewHelper?.setLayoutParams(customView!!)
        setUpView(customView!!, false)
    }

    fun setOnMenuStateChangedListener(menuStateChangedListener: OnMenuStateChangedListener?) {
        this.menuStateChangedListener = menuStateChangedListener
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        mEnableNestedScrolling = enabled
    }

    /**
     * Set menu from menu xml
     *
     * @param menuRes menu xml resource
     */
    fun setMenu(@MenuRes menuRes: Int) {
        customView = null
        mMenuRes = menuRes
        removeAllViews()
        @SuppressLint("RestrictedApi") val menu: Menu = MenuBuilder(context)
        inflateMenu(menuRes, menu)
        setUpMenu(menu)
    }

    protected fun inflateMenu(@MenuRes menuRes: Int, menu: Menu?) {
        MenuInflater(context).inflate(menuRes, menu)
    }

    fun updateMenu() {
        customView = null
        removeAllViews()
        if (menuList!!.size > 0) {
            setUpMenuView()
        } else setMenu(mMenuRes)
    }

    /**
     * Set menu from list of items
     *
     * @param menuList list of items
     */
    @Throws(NullPointerException::class)
    fun setMenuItems(menuList: ArrayList<FABMenuItem>?) {
        this.menuList = menuList
        mMenuRes = -1
        customView = null
        if (menuList == null) throw NullPointerException("Null items are not allowed.")
        removeAllViews()
        if (menuList.size > 0) {
            for (i in menuList.indices) {
                val item = menuList[i]
                item.id = item.id
                if (item.iconDrawable == null && item.iconBitmap != null) {
                    item.iconDrawable = BitmapDrawable(resources, item.iconBitmap)
                }
            }
        }
        setUpMenuView()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Throws(IllegalStateException::class)
    private fun setUpMenu(menu: Menu) {
        menuList = ArrayList()
        if (menu.size() > 0) {
            for (i in 0 until menu.size()) {
                val item = menu.getItem(i)
                menuList!!.add(
                    FABMenuItem(
                        item.itemId,
                        item.title.toString(),
                        item.icon
                    )
                )
            }
            setUpMenuView()
        } else throw IllegalStateException("Menu resource not found.")
    }

    private fun setUpMenuView() {
        if (menuList != null && menuList!!.size > 0) {
            mMenuView = viewHelper!!.generateMenuView(mEnableNestedScrolling)
            var isCircularShape = false

            //set layout manager
            if (mDirection === Direction.LEFT || mDirection === Direction.RIGHT) {
                val minItemWidth = if (isMenuSmall) mContext!!.resources
                    .getDimension(R.dimen.column_size_small).toInt() else mContext!!.resources
                    .getDimension(R.dimen.column_size).toInt()
                val rowLayoutResId =
                    if (isMenuSmall) R.layout.row_horizontal_menu_item_small else R.layout.row_horizontal_menu_item
                mMenuView!!.layoutManager = DynamicGridLayoutManager(
                    mContext,
                    minItemWidth,
                    menuList!!.size
                )
                menuAdapter = FABMenuAdapter(
                    this,
                    menuList!!,
                    rowLayoutResId,
                    true,
                    mTitleTextColor,
                    mTitleDisabledTextColor,
                    mShowTitle,
                    mShowIcon,
                    mDirection!!,
                    animateItems
                )
                if (mMenuTitleTypeface != null) menuAdapter!!.setMenuTitleTypeface(
                    mMenuTitleTypeface
                )
            } else {
                isCircularShape = !mShowTitle
                val rowLayoutResId =
                    if (isMenuSmall) R.layout.row_vertical_menu_item_small else R.layout.row_vertical_menu_item
                mMenuView!!.layoutManager = DynamicGridLayoutManager(mContext, 0, 0)
                menuAdapter = FABMenuAdapter(
                    this,
                    menuList!!,
                    rowLayoutResId,
                    isCircularShape,
                    mTitleTextColor,
                    mTitleDisabledTextColor,
                    mShowTitle,
                    mShowIcon,
                    mDirection!!,
                    animateItems
                )
                if (mMenuTitleTypeface != null) menuAdapter!!.setMenuTitleTypeface(
                    mMenuTitleTypeface
                )
            }
            mMenuView?.adapter = menuAdapter
            setUpView(mMenuView!!, mShowTitle && !isCircularShape)
        }
    }

    private fun setUpView(mView: View, toSetMinWidth: Boolean) {
        mBaseView = viewHelper?.generateBaseView()
        mBaseView?.setCardBackgroundColor(mMenuBackground)
        setRevealViewChangeDetected(true)
        mRevealView = viewHelper?.generateRevealView()
        mOverlayLayout = null
        mOverlayLayout = viewHelper?.generateOverlayView()
        if (mShowOverlay) {
            mOverlayLayout?.setBackgroundColor(
                if (mShowOverlay) mOverlayBackground else getColor(
                    android.R.color.transparent
                )
            )
        }
        if (toSetMinWidth) mBaseView?.minimumWidth =
            resources.getDimensionPixelSize(if (isMenuSmall) R.dimen.menu_min_width_small else R.dimen.menu_min_width)
        //1.add menu view
        mBaseView?.addView(mView)
        //2.add base view
        mRevealView?.addView(mBaseView)
        //3.add overlay
        if (mOverlayLayout != null) {
            addView(mOverlayLayout)
        }
        //4.add reveal view
        addView(mRevealView)

        //set reveal center points after view is layed out
        mBaseView?.viewTreeObserver?.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mBaseView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                animationHelper?.calculateCenterPoints(mBaseView, mDirection)
            }
        })

//        mRevealView.post(() -> {
//            //set reveal center points after views are added
//            animationHelper.calculateCenterPoints(mBaseView, mDirection);
//        });
        if (mOverlayLayout != null) {
            mOverlayLayout?.setOnClickListener { v: View? -> closeMenu() }
        }
    }

    /**
     * Attach fab to menu
     *
     * @param fab fab view
     */
    fun bindAnchorView(fab: View) {
        mFab = fab

        // Request layout only when fab button bind happened
        // after setUpView() and onLayout() already called by android
        if (isOnLayoutComplete) {
            setRevealViewChangeDetected(true)
            mFab?.post { requestLayout() }
        }
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        super.onLayout(changed, left, top, right, bottom)
        isOnLayoutComplete = true
        applyRevealViewChanges()
    }

    private fun setRevealViewChangeDetected(changeDetected: Boolean) {
        isRevealViewChangeDetected = changeDetected
        enableButtonClickListener(!changeDetected)
    }

    private fun enableButtonClickListener(enable: Boolean) {
        if (Conditions.isNotNull(mFab)) {
            if (enable) {
                mFab?.setOnClickListener { v: View? -> showMenu() }
            } else {
                mFab?.setOnClickListener(null)
            }
        }
    }

    private fun applyRevealViewChanges() {
        if (isRevealViewChangeDetected) {
            if (Conditions.isNull(mRevealView) || Conditions.isNull(mRevealView?.parent)) {
                return
            }
            if (Conditions.isNull(mFab)) {
                return
            }
            if (mRevealView?.width!! > 0 || mRevealView?.height!! > 0) {
                setRevealViewChangeDetected(false)
                animationHelper?.calculateCenterPoints(mBaseView, mDirection)
                viewHelper?.alignMenuWithFab(mFab!!, mRevealView!!, mDirection!!)
            }
        }
    }

    // --- action methods --- //
    fun getItemByIndex(index: Int): FABMenuItem? {
        return if (menuAdapter != null) {
            menuAdapter?.getItemByIndex(index)
        } else null
    }

    fun getItemById(id: Int): FABMenuItem? {
        return if (menuAdapter != null) {
            menuAdapter?.getItemById(id)
        } else null
    }

    /**
     * Remove menu item by id
     */
    fun removeItem(id: Int): Boolean {
        menuList?.apply {
            for (i in this.indices) {
                if (this[i].id == id) {
                    this.removeAt(i)
                    (mMenuView?.layoutManager as DynamicGridLayoutManager?)?.updateTotalItems(
                        this.size
                    )
                    if (menuAdapter != null) {
                        menuAdapter?.notifyItemRemoved(i)
                        menuAdapter?.notifyItemRangeChanged(i, menuList?.size!!)
                    }
                    return true
                }
            }
        }

        return false
    }

    fun notifyItemChanged(id: Int) {
        if (menuAdapter != null) {
            menuAdapter?.notifyItemChangedById(id)
        }
    }

    fun setOnFABMenuSelectedListener(menuSelectedListener: OnFABMenuSelectedListener?) {
        this.menuSelectedListener = menuSelectedListener
    }

    val isShowing: Boolean
        get() = FAB_CURRENT_STATE == FAB_STATE_EXPANDED

    /**
     * Show the menu
     */
    fun showMenu() {
        checkNotNull(mFab) {
            "FloatingActionButton not bound." +
                    "Please, use bindAnchorView() to add your Fab button."
        }
        if (mRevealView?.visibility == View.VISIBLE) return
        animationHelper?.calculateCenterPoints(mBaseView, mDirection)
        viewHelper?.alignMenuWithFab(mFab!!, mRevealView!!, mDirection!!)
        if (FAB_CURRENT_STATE == FAB_STATE_COLLAPSED) {
            FAB_CURRENT_STATE = FAB_STATE_EXPANDED
            if (menuStateChangedListener != null) menuStateChangedListener?.onExpand()
            animationHelper?.moveFab(
                mFab!!,
                mRevealView!!,
                mDirection!!,
                false,
                object : AnimationListener() {
                    override fun onEnd() {
                        mFab?.visibility = View.INVISIBLE
                    }
                })

            // Show sheet after a delay
            postDelayed({
                val finalRadius =
                    Math.max(mBaseView?.width!!, mBaseView?.height!!)
                animationHelper?.revealMenu(
                    mBaseView!!,
                    mFab?.width!! / 2.toFloat(),
                    finalRadius.toFloat(),
                    false,
                    object : AnimationListener() {
                        override fun onStart() {
                            mRevealView?.visibility = View.VISIBLE
                            if (menuAdapter != null) {
                                menuAdapter?.resetAdapter(false)
                            }
                            if (mOverlayLayout != null) {
                                animationHelper?.showOverlay(mOverlayLayout!!)
                            }
                        }
                    })
            }, AnimationHelper.FAB_ANIM_DURATION - 50.toLong())
        }
    }

    @JvmOverloads
    @Throws(IllegalStateException::class)
    fun closeMenu(callback: OnMenuClosedCallback? = null) {
        checkNotNull(mFab) {
            "FloatingActionButton not bound." +
                    "Please, use bindAnchorView() to add your Fab button."
        }
        if (FAB_CURRENT_STATE == FAB_STATE_EXPANDED) {
            FAB_CURRENT_STATE = FAB_STATE_COLLAPSED
            if (menuStateChangedListener != null) menuStateChangedListener?.onCollapse()
            val initialRadius = Math.max(mBaseView?.width!!, mBaseView?.height!!)
            animationHelper?.revealMenu(
                mBaseView!!,
                initialRadius.toFloat(),
                mFab?.width!! / 2.toFloat(),
                true,
                object : AnimationListener() {
                    override fun onStart() {
                        if (menuAdapter != null) {
                            menuAdapter?.resetAdapter(true)
                        }
                        if (mOverlayLayout != null) {
                            animationHelper?.hideOverlay(mOverlayLayout!!)
                        }
                    }

                    override fun onEnd() {
                        mRevealView?.visibility = View.INVISIBLE
                        if (Conditions.isNotNull(callback)) callback?.onMenuClosed()
                    }
                })

            // Show FAB after a delay
            postDelayed({
                animationHelper?.moveFab(
                    mFab!!,
                    mRevealView!!,
                    mDirection!!,
                    true,
                    object : AnimationListener() {
                        override fun onStart() {
                            mFab?.visibility = View.VISIBLE
                        }
                    })
            }, AnimationHelper.REVEAL_DURATION - 50.toLong())
        }
    }

    interface OnMenuClosedCallback {
        fun onMenuClosed()
    }

    private fun recreateView() {
        if (mMenuRes != -1) updateMenu() else if (customView != null) setCustomView(customView!!) else if (menuList != null) setMenuItems(
            menuList
        )
    }

    private fun getColor(colorResId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) resources.getColor(
            colorResId,
            mContext?.theme
        ) else resources.getColor(colorResId)
        return colorResId
    }

    // ---- getter setter --- //
    @Throws(NullPointerException::class)
    fun setOverlayBackground(@ColorRes mOverlayBackground: Int) {
        this.mOverlayBackground = mOverlayBackground
        if (mOverlayLayout != null) {
            mOverlayLayout?.setBackgroundColor(getColor(mOverlayBackground))
        } else throw NullPointerException("Overlay view is not initialized/ set ShowOverlay to true")
    }

    fun setMenuBackground(@ColorRes menuBackgroundRes: Int) {
        mBaseView?.setCardBackgroundColor(getColor(menuBackgroundRes))
    }

    var isShowOverlay: Boolean
        get() = mShowOverlay
        set(mShowOverlay) {
            this.mShowOverlay = mShowOverlay
            closeMenu()
            post { recreateView() }
        }

    private val isMenuSmall: Boolean
        private get() = mMenuSize == FAB_MENU_SIZE_SMALL

    /**
     * Set normal size for menu item
     */
    fun enableItemAnimation(enabled: Boolean) {
        animateItems = enabled
        if (menuAdapter != null) {
            post {
                menuAdapter?.isAnimateItems = enabled
                menuAdapter?.notifyDataSetChanged()
            }
        }
    }

    /**
     * Set small size for menu item
     */
    fun setSmallerMenu() {
        mMenuSize = FAB_MENU_SIZE_SMALL
        post { recreateView() }
    }

    /**
     * Set normal size for menu item
     */
    fun setNormalMenu() {
        mMenuSize = FAB_MENU_SIZE_NORMAL
        post { recreateView() }
    }

    fun setTitleVisible(mShowTitle: Boolean) {
        this.mShowTitle = mShowTitle
        if (menuAdapter != null) {
            if (mShowTitle && (mDirection === Direction.UP || mDirection === Direction.DOWN)) mBaseView?.minimumWidth =
                resources.getDimensionPixelSize(R.dimen.menu_min_width) else mBaseView?.minimumWidth =
                LayoutParams.WRAP_CONTENT
            menuAdapter?.isShowTitle = mShowTitle
            closeMenu()
            post { recreateView() }
        }
    }

    fun setMenuTitleTextColor(@ColorRes mTitleTextColor: Int) {
        this.mTitleTextColor = mTitleTextColor
        if (menuAdapter != null) {
            menuAdapter?.titleTextColor = mTitleTextColor
            menuAdapter?.notifyDataSetChanged()
        }
    }

    fun setMenuTitleDisabledTextColor(@ColorRes mTitleDisabledTextColor: Int) {
        this.mTitleDisabledTextColor = mTitleDisabledTextColor
        if (menuAdapter != null) {
            menuAdapter?.setTitleDisabledTextColor(mTitleDisabledTextColor)
            menuAdapter?.notifyDataSetChanged()
        }
    }

    var menuDirection: Direction?
        get() = mDirection
        set(mDirection) {
            this.mDirection = mDirection
            if (menuAdapter != null) {
                menuAdapter?.direction = mDirection!!
                post { recreateView() }
            }
        }

    fun setMenuTitleTypeface(mMenuTitleTypeface: Typeface?) {
        if (mMenuTitleTypeface != null) {
            this.mMenuTitleTypeface = mMenuTitleTypeface
            post { recreateView() }
        }
    }
}