package org.bbs.maimaisonglist.utils

import android.view.View

/**
 * @author BBS
 * @since  2019-11-13
 */
fun View.avoidDoubleClick(time: Long) {
    this.isEnabled = false
    this.postDelayed({
        this.isEnabled = true
    }, time)
}

fun View.setOnClickListenerAvoidDoubleClick(listener: (view: View) -> Long) {
    this.setOnClickListener {
        val time = listener.invoke(it)
        avoidDoubleClick(time)
    }
}