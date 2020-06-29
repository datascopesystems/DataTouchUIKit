/*
 * Created by Ihor Karpachev, Copyright (c) 2015. .
 */
package datatouch.uikit.components.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import datatouch.uikit.R

class CMaterialMenuItem : RelativeLayout {
    constructor(context: Context?) : super(context) {
        inflateView()

    }

    constructor(
        context: Context?,
        attrs: AttributeSet
    ) : super(context, attrs) {
        inflateView()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        inflateView()
    }

    protected fun inflateView() {
        View.inflate(context, R.layout.listview_context_menu, this)
    }
}