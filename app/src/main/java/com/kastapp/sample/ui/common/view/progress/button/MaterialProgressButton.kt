package com.kastapp.sample.ui.common.view.progress.button

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.button.MaterialButton
import com.kastapp.sample.R
import com.kastapp.sample.ui.common.ext.dp
import com.kastapp.sample.ui.common.ext.lockUi
import com.kastapp.sample.ui.common.ext.unlockUi

class MaterialProgressButton : MaterialButton {

    constructor(context: Context) : super(context) {
        initAttr()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttr(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttr(attrs)
    }

    private lateinit var progressDrawable: CircularProgressDrawable
    private val progressDrawableCallback: Drawable.Callback = object : Drawable.Callback {
        override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        }

        override fun invalidateDrawable(who: Drawable) {
            invalidate()
        }

        override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
        }
    }
    var isProgressVisible: Boolean = false
        private set
    private var remText: CharSequence? = null
    private var needDisplayTextOnProgress: Boolean = false
    private var needLockUi: Boolean = false
    private var progressRadius: Float = 0f

    private fun initAttr(attrs: AttributeSet? = null) {
        attrs?.let {
            val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.MaterialProgressButton,
                0,
                0
            )
            progressRadius = a.getDimension(
                R.styleable.MaterialProgressButton_progress_radius,
                10f.dp()
            )
            needDisplayTextOnProgress = a.getBoolean(
                R.styleable.MaterialProgressButton_progress_text_visibility,
                true
            )
            needLockUi = a.getBoolean(
                R.styleable.MaterialProgressButton_progress_lock_ui,
                true
            )
            a.recycle()
        }
        progressDrawable = CircularProgressDrawable(context).apply {
            setStyle(CircularProgressDrawable.LARGE)
            setColorSchemeColors(currentTextColor)
            centerRadius = progressRadius
            strokeWidth = 3f.dp()
            val size = (centerRadius + strokeWidth).toInt() * 2
            setBounds(0, 0, size, size)
            callback = progressDrawableCallback
        }
    }

    override fun onDetachedFromWindow() {
        hideProgress()
        super.onDetachedFromWindow()
    }

    fun showProgress() {
        remText = text
        isProgressVisible = true
        text = SpannableString(if (needDisplayTextOnProgress) "$text  " else " ").apply {
            setSpan(
                ProgressSpanDrawable(progressDrawable),
                length - 1,
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        progressDrawable.start()
        if (needLockUi) context.lockUi() else isEnabled = false
    }

    fun hideProgress() {
        isProgressVisible = false
        progressDrawable.stop()
        remText?.let {
            text = it
            remText = null
        }
        if (needLockUi) context.unlockUi() else isEnabled = true
    }
}
