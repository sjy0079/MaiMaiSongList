package org.bbs.maimaisonglist.utils

import android.content.Context
import org.bbs.maimaisonglist.MainApplication

/**
 * @author BBS
 * @since  2019-11-13
 */
object WindowUtils {
    fun dip2px(dp: Float): Float {
        val scale = MainApplication.instance!!.resources.displayMetrics.density
        return dp * scale + 0.5f
    }

    fun getWindowWidth(context: Context): Float {
        val dm = context.resources.displayMetrics ?: return 0F
        return dm.widthPixels.toFloat()
    }
}