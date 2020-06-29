package datatouch.uikit.components

import android.content.Context
import android.util.AttributeSet
import java.util.*

class WebViewWithCustomHeaders(
    context: Context?,
    attrs: AttributeSet?
) : InterceptableWebView(context, attrs) {
    private var httpHeaders =
        HashMap<String, String>()

    override fun loadUrl(url: String) {
        super.loadUrl(url, httpHeaders)
    }

    override fun loadUrl(
        url: String,
        additionalHttpHeaders: MutableMap<String, String>
    ) {
        if (additionalHttpHeaders != null) {
            additionalHttpHeaders.putAll(httpHeaders)
            super.loadUrl(url, additionalHttpHeaders)
        } else {
            super.loadUrl(url, httpHeaders)
        }
    }

    fun setHttpHeaders(httpHeaders: HashMap<String, String>) {
        this.httpHeaders = httpHeaders
    }
}