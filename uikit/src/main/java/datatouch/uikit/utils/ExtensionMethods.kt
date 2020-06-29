package datatouch.uikit.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


fun ViewGroup.getInflater(): LayoutInflater {
    return this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
}

fun View.getInflater(): LayoutInflater {
    return this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
}