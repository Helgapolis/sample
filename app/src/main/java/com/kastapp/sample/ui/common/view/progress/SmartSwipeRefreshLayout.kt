package com.kastapp.sample.ui.common.view.progress

import android.content.Context
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * Проблема: если ответ от сервера прийдет слишком быстро, то тробер показывается слишком быстро.
 * Человеческий глаз не успевает понять что он что-то увидел или ему показалось.
 *
 * Если ответ от сервера приходит быстро (до 350 мс) - тробер не показываем.
 * Если троббер уже виден - отображаем его как минимум пол секунды и только потом скрываем
 */
class SmartSwipeRefreshLayout : SwipeRefreshLayout {

    companion object {
        private const val MIN_SHOW_TIME = 500 // ms
        private const val MIN_DELAY = 350 // ms
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var startTime: Long = -1
    private var postedHide = false
    private var postedShow = false
    private var dismissed = false

    private val delayedHide = Runnable {
        postedHide = false
        startTime = -1
        if (isRefreshing) isRefreshing = false
    }

    private val delayedShow = Runnable {
        postedShow = false
        if (!dismissed) {
            startTime = System.currentTimeMillis()
            if (!isRefreshing) isRefreshing = true
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        removeCallbacks()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks()
    }

    private fun removeCallbacks() {
        removeCallbacks(delayedHide)
        removeCallbacks(delayedShow)
    }

    @Synchronized
    fun hide() {
        dismissed = true
        removeCallbacks(delayedShow)
        postedShow = false
        val diff = System.currentTimeMillis() - startTime
        if (diff >= MIN_SHOW_TIME || startTime == -1L) {
            if (isRefreshing) isRefreshing = false
        } else {
            if (!postedHide) {
                postDelayed(delayedHide, MIN_SHOW_TIME - diff)
                postedHide = true
            }
        }
    }

    @Synchronized
    fun show() {
        startTime = -1
        dismissed = false
        removeCallbacks(delayedHide)
        postedHide = false
        if (!postedShow) {
            postDelayed(delayedShow, MIN_DELAY.toLong())
            postedShow = true
        }
    }
}
