package com.cme.digicellogincme.helper

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue
import android.widget.TextView
import com.applicaster.app.CustomApplication
import com.applicaster.util.StringUtil

class UiHelper {
    companion object {
        private val TAG = "UiHelper"

        @JvmStatic
        fun updateTextViewText(textView: TextView?, key: String) {
            if (textView != null) {
                var textValue = PluginConfigurationHelper.getConfigurationValue(key)
                textValue = if (StringUtil.isNotEmpty(textValue)) textValue else key
                textView.text = textValue
            }
        }

        @JvmStatic
        fun updateTextViewFont(textView: TextView?, key: String) {
            var fontName = PluginConfigurationHelper.getConfigurationValue(key)
            if (textView != null && StringUtil.isNotEmpty(fontName)) {
                var typeface = getFontFromAssets(fontName,".otf")
                typeface?.let {
                    textView.typeface = typeface
                    return
                }
                typeface = getFontFromAssets(fontName,".ttf")
                typeface?.let {
                    textView.typeface = typeface
                    return
                }
            }
        }

        @JvmStatic
        fun updateTextViewSize(textView: TextView?, key: String) {
            var size: Float = 15F
            try {
                var sizeInPixel = PluginConfigurationHelper.getConfigurationValue(key)
                sizeInPixel?.let {
                    size = sizeInPixel?.toFloat();
                }
            }catch (err:Exception){
                Log.d(TAG, "key: ${key} couldn't be paresed to float.")
            }

            if (textView != null) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
            }
        }

        @JvmStatic
        fun updateTextViewColor(textView: TextView?, key: String) {
            if (textView != null) {
                var colorValue = PluginConfigurationHelper.getConfigurationValue(key)
                colorValue = if (StringUtil.isNotEmpty(colorValue)) colorValue else "#000000"
                textView.setTextColor(Color.parseColor(colorValue))
            }
        }



        @JvmStatic
        fun getFontFromAssets(fontName: String?, suffix: String?): Typeface? {

            if (StringUtil.isNotEmpty(fontName)) {

                try {
                    var customFontTypeface = Typeface.createFromAsset(CustomApplication.getAppContext().assets, "fonts/$fontName$suffix")
                    return customFontTypeface
                } catch (e: Exception) {
                    Log.d("", "")
                }

            }

            return null
        }

    }
}