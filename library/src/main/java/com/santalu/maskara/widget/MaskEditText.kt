package com.santalu.maskara.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import com.santalu.maskara.Action
import com.santalu.maskara.Mask
import com.santalu.maskara.MaskChangedListener
import com.santalu.maskara.MaskResult
import com.santalu.maskara.MaskStyle
import com.santalu.maskara.Maskara
import com.santalu.maskara.R
import com.santalu.maskara.mostOccurred

/**
 * Created by fatih.santalu on 7/7/2020.
 */

@SuppressLint("Recycle")
class MaskEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr) {

    private var maskChangedListener: MaskChangedListener? = null

    val masked: String
        get() = maskChangedListener?.masked.orEmpty()

    val unMasked: String
        get() = maskChangedListener?.unMasked.orEmpty()

    val isDone: Boolean
        get() = maskChangedListener?.isDone ?: false
    var mask : Mask? = null

    init {
        context.obtainStyledAttributes(attrs, R.styleable.MaskEditText).apply {
            val style = getInteger(R.styleable.MaskEditText_maskStyle, 0)
            val value = getString(R.styleable.MaskEditText_mask).orEmpty()
            val character = getString(R.styleable.MaskEditText_maskCharacter).orEmpty()

            if (value.isNotEmpty()) {
                val maskChar = if (character.isEmpty()) value.mostOccurred() else character.single()
                mask = Mask(value, maskChar, MaskStyle.valueOf(style))
                maskChangedListener = MaskChangedListener(mask!!)
            }

            recycle()
        }
    }

    /**
     * Let only one [maskChangedListener] allowed at a time
     */
    override fun addTextChangedListener(watcher: TextWatcher?) {
        if (watcher is MaskChangedListener) {
            removeTextChangedListener(maskChangedListener)
            maskChangedListener = watcher
        }
        super.addTextChangedListener(watcher)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addTextChangedListener(maskChangedListener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeTextChangedListener(maskChangedListener)
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        val maskara = Maskara(mask!!)
        val result = maskara.apply(this.text.toString(), Action.INSERT)
        this.setText(result.masked)
    }
}
