package com.cme.digicellogin.helper

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.applicaster.app.CustomApplication
import com.applicaster.util.OSUtil
import com.applicaster.util.StringUtil
import com.applicaster.util.TextUtil
import com.applicaster.util.ui.CustomButton
import com.applicaster.util.ui.CustomEditText
import java.util.HashMap
import android.util.Log
import android.util.TypedValue
import java.util.logging.Logger


class CustomizationHelper {
    companion object {
        @JvmStatic
        fun updateImageView(imageView: ImageView, key: String) {
            val iconDrawableRedId = OSUtil.getDrawableResourceIdentifier(key)
            if (imageView != null && iconDrawableRedId != 0) {
                imageView.setImageResource(iconDrawableRedId)
            }
        }

        @JvmStatic
        fun updateImageView(activity: Activity, id: Int, key: String) {
            val imageView = activity.findViewById(id) as ImageView? ?: return
            val iconDrawableRedId = OSUtil.getDrawableResourceIdentifier(key)
            if (iconDrawableRedId != 0) {
                imageView.setImageResource(iconDrawableRedId)
            }
        }


        @JvmStatic
        fun updateTextView(activity: Activity, textView: TextView?, hashMapStyles: Map<String, Any>?, hashMapText: Map<String, Any>?, textKey: String, textSizeKey: String, fontKey: String, textColorKey: String) {

            if (textView != null && hashMapStyles != null) {

                var textValue = getHashMapStringValue(hashMapText, textKey)
                var textSizeValue = getHashMapStringValue(hashMapStyles, textSizeKey)
                var fontValue = getHashMapStringValue(hashMapStyles, fontKey)
                var textColorValue = getHashMapStringValue(hashMapStyles, textColorKey)

                if (StringUtil.isNotEmpty(textValue)) {
                    textView.text = textValue
                }
                setTextViewFont(textView, fontValue)
                setTextViewColor(textView, textColorValue)
                setTextViewSize(textView, textSizeValue)

            }
        }

        @JvmStatic
        fun updateTextView(activity: Activity, textView: TextView?, hashMapStyles: Map<String, Any>?, text: String?, textSizeKey: String, fontKey: String, textColorKey: String) {

            if (textView != null && hashMapStyles != null) {

                var textSizeValue = getHashMapStringValue(hashMapStyles, textSizeKey)
                var fontValue = getHashMapStringValue(hashMapStyles, fontKey)
                var textColorValue = getHashMapStringValue(hashMapStyles, textColorKey)

                if (StringUtil.isNotEmpty(text)) {
                    textView.text = text
                }
                setTextViewFont(textView, fontValue)
                setTextViewColor(textView, textColorValue)
                setTextViewSize(textView, textSizeValue)

            }
        }


        @JvmStatic
        fun setTextViewSize(textView: TextView?, textSizeValue: String?) {
            if (textView != null && StringUtil.isNotEmpty(textSizeValue)) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeValue?.toFloat()!!)
            }
        }


        @JvmStatic
        fun setTextViewFont(textView: TextView?, fontName: String?) {
            if (textView != null && StringUtil.isNotEmpty(fontName)) {
                val typeface = getFontFromAssets(fontName)
                if (typeface != null) {
                    textView.typeface = typeface
                }
            }
        }


        @JvmStatic
        fun setTextViewColor(textView: TextView?, textColorValue: String?) {
            if (textView != null && StringUtil.isNotEmpty(textColorValue)) {
                textView.setTextColor(Color.parseColor(textColorValue))
            }
        }

        @JvmStatic
        fun setImageViewColor(image: ImageView?, colorValue: String?) {
            image?.let {
                it.setBackgroundColor(Color.parseColor(colorValue))
            }
        }


        @JvmStatic
        fun getFontFromAssets(fontName: String?): Typeface? {

            if (StringUtil.isNotEmpty(fontName)) {

                try {
                    val firstSuffix = ".otf"
                    val secondSuffix = ".ttf"
                    var customFontTypeface = Typeface.createFromAsset(CustomApplication.getAppContext().assets, "fonts/$fontName$firstSuffix")

                    if (customFontTypeface == null) {
                        customFontTypeface = Typeface.createFromAsset(CustomApplication.getAppContext().assets, "fonts/$fontName$secondSuffix")
                    }
                    return customFontTypeface
                } catch (e: Exception) {
                    Log.d("", "")
                }

            }

            return null
        }


        @JvmStatic
        fun getHashMapStringValue(hashMap: Map<String, Any>?, key: String): String? {
            if (hashMap != null && hashMap[key] != null && hashMap[key] is String) {
                return hashMap[key] as String
            }
            return null
        }


        @JvmStatic
        fun updateTextView(activity: Activity, textView: TextView, key: String, style: String?) {

            if (textView != null) {
                var textValue = PluginConfigurationHelper.getConfigurationValue(key)
                textValue = if (StringUtil.isNotEmpty(textValue)) textValue else key
                textView.text = textValue

                if (style != null)
                    updateTextStyle(activity, textView, style)
            }
        }


        @JvmStatic
        fun updateTextView(activity: Activity, id: Int, key: String, style: String?) {
            val textView = activity.findViewById(id) as TextView? ?: return
            var textValue = PluginConfigurationHelper.getConfigurationValue(key)
            textValue = if (StringUtil.isNotEmpty(textValue)) textValue else key
            textView.text = textValue

            if (style != null)
                updateTextStyle(activity, textView, style)

        }

        @JvmStatic
        fun updateEditTextView(activity: Activity, id: Int, key: String, isHint: Boolean) {
            val editText = activity.findViewById(id) as CustomEditText? ?: return
            var textValue = PluginConfigurationHelper.getConfigurationValue(key)
            textValue = if (StringUtil.isNotEmpty(textValue)) textValue else key

            updateTextStyle(activity, editText, "CleengLoginDefaultText")
            if (!isHint) {
                editText.setText(textValue)
            } else {
                editText.hint = textValue
                editText.setHintTextColor(activity.resources.getColor(OSUtil.getColorResourceIdentifier("cleeng_login_default_text")))
            }
        }

        @JvmStatic
        fun updateEditTextView(activity: Activity, id: Int, key: String, isHint: Boolean, style: String) {
            val editText = activity.findViewById(id) as CustomEditText? ?: return
            var textValue = PluginConfigurationHelper.getConfigurationValue(key)
            textValue = if (StringUtil.isNotEmpty(textValue)) textValue else key
            if (!isHint) {
                editText.setText(textValue)
            } else {
                editText.hint = textValue
            }

            if (StringUtil.isNotEmpty(style))
                updateTextStyle(activity, editText, style)
        }

        @JvmStatic
        fun updateButtonViewText(activity: Activity, id: Int, key: String, style: String?) {
            val button = activity.findViewById(id) as CustomButton? ?: return
            var textValue = PluginConfigurationHelper.getConfigurationValue(key)
            textValue = if (StringUtil.isNotEmpty(textValue)) textValue else key
            button.text = textValue

            if (style != null)
                updateTextStyle(activity, button, style)
        }

        @JvmStatic
        fun updateBgResource(activity: Activity, id: Int, key: String) {
            val view = activity.findViewById(id) as View? ?: return
            val bgDrawableRedId = OSUtil.getDrawableResourceIdentifier(key)
            if (bgDrawableRedId != 0) {
                view.setBackgroundResource(bgDrawableRedId)
            }
        }

        @JvmStatic
        fun updateBgColorFromRemote(activity: Activity, hashMapStyles: Map<String, Any>?, id: Int, key: String) {
            val view = activity.findViewById(id) as View? ?: return

            var bgColorId = getHashMapStringValue(hashMapStyles, "cleeng_settings_screen_background_color")


            bgColorId?.let {
                setImageViewColor(view as ImageView, bgColorId)
            }

        }

        @JvmStatic
        fun updateBgColor(activity: Activity, id: Int, key: String) {
            val view = activity.findViewById(id) as View? ?: return
            val bgColorId = OSUtil.getColorResourceIdentifier(key)
            if (bgColorId != 0) {
                view.setBackgroundColor(bgColorId)
            }
        }

        @JvmStatic
        fun updateButtonStyle(activity: Activity, id: Int, key: String) {
            val view = activity.findViewById(id) as Button? ?: return
            val bgDrawableRedId = OSUtil.getStylableResourceIdentifier(key)
            if (bgDrawableRedId != 0) {
                view.setTextAppearance(activity, bgDrawableRedId)
            }
        }

        @JvmStatic
        fun updateTextStyle(context: Context, view: TextView, key: String) {
            val styleRedId = OSUtil.getStyleResourceIdentifier(key)
            if (styleRedId != 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.setTextAppearance(styleRedId)
                } else {
                    view.setTextAppearance(context, styleRedId)
                }
                TextUtil.setTextFont(view, getFontName(key))
            }
        }

        private fun getFontName(key: String): String {
            val attributes: IntArray = intArrayOf(OSUtil.getAttributeResourceIdentifier("customtypeface"))
            val styles = CustomApplication.getAppContext().obtainStyledAttributes(OSUtil.getStyleResourceIdentifier(key), attributes)

            val font = styles.getString(0)
            styles.recycle()
            return font
        }

    }
}
