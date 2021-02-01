package org.bbs.maimaisonglist.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.DrawableRes
import org.bbs.maimaisonglist.MainApplication
import org.bbs.maimaisonglist.R

/**
 * @author BBS
 * @since  2019-11-14
 */
object SpUtils {
    const val RANK_S = "S"
    const val RANK_SS = "SS"
    const val RANK_SSS = "SSS"
    const val RANK_AP = "AP"

    private const val SP_NAME = "msl_song_record"

    private var pref: SharedPreferences
    private var editor: SharedPreferences.Editor

    init {
        val context = MainApplication.instance!!
        pref = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        @SuppressLint("CommitPrefEdits")
        editor = pref.edit()
    }

    fun setRank(name: String, clazz: String, rank: String) {
        editor.putString(name + clazz, rank)
        editor.apply()
    }

    @DrawableRes
    fun getRankImg(name: String, clazz: String): Int {
        val rank = pref.getString(name + clazz, "")
        return when (rank) {
            RANK_S -> R.drawable.msl_rank_s
            RANK_SS -> R.drawable.msl_rank_ss
            RANK_SSS -> R.drawable.msl_rank_sss
            RANK_AP -> R.drawable.msl_rank_ap
            else -> 0
        }
    }

    fun getRank(name: String, clazz: String): String {
        return pref.getString(name + clazz, "") ?: String()
    }
}