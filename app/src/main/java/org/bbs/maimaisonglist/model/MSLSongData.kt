package org.bbs.maimaisonglist.model

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.google.gson.annotations.SerializedName
import org.bbs.maimaisonglist.R

/**
 * @author shenjiayi@didiglobal.com
 * @since  2019-11-13
 */
data class MSLSongData(
    @SerializedName("data")
    var data: List<MSLSongListData>? = null
)

data class MSLSongListData(
    @SerializedName("level")
    var level: String? = null,
    @SerializedName("data")
    var data: List<MSLSongSingleData>
)

data class MSLSongSingleData(
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("image")
    var image: String? = null,
    @SerializedName("class")
    var className: String? = null,
    @SerializedName("category")
    var category: Int? = null,
    @SerializedName("bpm")
    var bpm: String? = null,
    @SerializedName("notes")
    var notes: String? = null,
    @SerializedName("level_accurate")
    var levelAccurate: String? = null
) {

    @DrawableRes
    fun getCategoryImage() =
        when (category) {
            1 -> R.drawable.msl_category_pops_anime
            2 -> R.drawable.msl_category_vocal_nico
            3 -> R.drawable.msl_category_touhou
            4 -> R.drawable.msl_category_variety
            else -> R.drawable.msl_category_original
        }

    @ColorInt
    fun getCategoryColor() =
        when (category) {
            1 -> 0xFFFFBD76.toInt()
            2 -> 0xFF61DCE4.toInt()
            3 -> 0xFFCD96F8.toInt()
            4 -> 0xFF86EA9F.toInt()
            else -> 0xFF7EB1FE.toInt()
        }

    @ColorInt
    fun getClassColor() =
        when (className) {
            "MASTER" -> 0xFF9D38E3.toInt()
            "EXPERT" -> 0xFFF36776.toInt()
            "ADVANCED" -> 0xFFFFBA04.toInt()
            else -> 0xFFFBF4FF.toInt()
        }
}
