package datatouch.uikit.core.extensions

import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2


object ViewPagerExtensions {

    fun ViewPager2.nextPage() {
        currentItem += 1
    }

    fun ViewPager2.prevPage() {
        currentItem -= 1
    }

    fun ViewPager.nextPage() {
        currentItem += 1
    }

    fun ViewPager.prevPage() {
        currentItem -= 1
    }

}