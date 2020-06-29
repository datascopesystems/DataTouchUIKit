package datatouch.uikit.components.floatingrevealmenu.model

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import java.util.*

class FABMenuItem {
    var id = 0
    var title: String
    var iconDrawable: Drawable? = null
    var iconBitmap: Bitmap? = null
        private set
    var isEnabled = true

    constructor(title: String, iconDrawable: Drawable?) {
        this.title = title
        this.iconDrawable = iconDrawable
    }

    constructor(id: Int, title: String, iconDrawable: Drawable?) {
        this.id = id
        this.title = title
        this.iconDrawable = iconDrawable
    }

    constructor(title: String, iconBitmap: Bitmap?) {
        this.title = title
        this.iconBitmap = iconBitmap
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as FABMenuItem
        return id == that.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}