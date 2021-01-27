package datatouch.uikit.components.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class ViewHolder<TView: View>(val typedView: TView) : RecyclerView.ViewHolder(typedView)