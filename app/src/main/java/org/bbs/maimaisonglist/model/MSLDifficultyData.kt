package org.bbs.maimaisonglist.model

import androidx.annotation.DrawableRes
import org.bbs.maimaisonglist.R

/**
 * @author BBS
 * @since  2019-11-13
 */
data class MSLDifficultyData(
    val level: String,
    @DrawableRes val src: Int
) {
    companion object {
        fun getDefaultDataList(): List<MSLDifficultyData> {
            return listOf(
                MSLDifficultyData("LEVEL 10", R.drawable.msl_lv_10),
                MSLDifficultyData("LEVEL 10+", R.drawable.msl_lv_10p),
                MSLDifficultyData("LEVEL 11", R.drawable.msl_lv_11),
                MSLDifficultyData("LEVEL 11+", R.drawable.msl_lv_11p),
                MSLDifficultyData("LEVEL 12", R.drawable.msl_lv_12),
                MSLDifficultyData("LEVEL 12+", R.drawable.msl_lv_12p),
                MSLDifficultyData("LEVEL 13", R.drawable.msl_lv_13),
                MSLDifficultyData("LEVEL 13+", R.drawable.msl_lv_13p),
                MSLDifficultyData("LEVEL 14", R.drawable.msl_lv_14)
            )
        }
    }
}