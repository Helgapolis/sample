package com.kastapp.sample.ui.common.ext

import android.content.res.Resources

fun Int.dp(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
fun Float.dp(): Float = (this * Resources.getSystem().displayMetrics.density)
fun Int.sp(): Int = (this * Resources.getSystem().displayMetrics.scaledDensity).toInt()
fun Float.sp(): Float = (this * Resources.getSystem().displayMetrics.scaledDensity)
fun Int.px2dip(): Float = this.toFloat() / Resources.getSystem().displayMetrics.density
fun Int.px2sp(): Float = this.toFloat() / Resources.getSystem().displayMetrics.scaledDensity
