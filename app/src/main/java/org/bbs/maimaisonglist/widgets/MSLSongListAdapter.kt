package org.bbs.maimaisonglist.widgets

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.Disposable
import org.bbs.maimaisonglist.R
import org.bbs.maimaisonglist.drawable.MSLSongPicDrawable
import org.bbs.maimaisonglist.model.MSLSongSingleData
import org.bbs.maimaisonglist.utils.ImageUtils
import org.bbs.maimaisonglist.utils.SpUtils

/**
 * @author shenjiayi@didiglobal.com
 * @since  2019-11-14
 */
class MSLSongListAdapter : RecyclerView.Adapter<MSLSongListVH>() {
    private var songList = listOf<MSLSongSingleData>()
        set(value) {
            originalList = value
            field = value
                .filter {
                    if (filterRank.isEmpty()) {
                        return@filter true
                    }
                    return@filter SpUtils.getRank(it.name ?: String(), it.className ?: String()) == filterRank
                }
                .filter {
                    if (searchingKeyword.isEmpty() || it.name == null) {
                        return@filter true
                    }
                    return@filter it.name!!.indexOf(searchingKeyword, 0, true) > -1
                }
            if (isSortByLevel) {
                field = field.sortedBy { it.levelAccurate }
            }
        }
    private var originalList = listOf<MSLSongSingleData>()
    private val disposeMap = hashMapOf<View, Disposable>()
    private var filterRank = String()
    private var isSortByLevel = false
    private var searchingKeyword = String()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MSLSongListVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.msl_song_info, parent, false)
        return MSLSongListVH(view)
    }

    override fun getItemCount() = songList.size

    override fun onBindViewHolder(holder: MSLSongListVH, position: Int) {
        val songData = songList[position]
        val layerDrawable = (holder.itemView.background as LayerDrawable).getDrawable(0) as LayerDrawable
        val layerBg = layerDrawable.getDrawable(0) as GradientDrawable
        layerBg.setColor(songData.getCategoryColor())
        holder.category.setImageResource(songData.getCategoryImage())
        bindImage(holder.image, songData)
        holder.title.text = songData.name
        holder.desc.text = holder.itemView.context.getString(
            R.string.msl_song_desc,
            songData.levelAccurate, songData.bpm, songData.notes
        )
        holder.accurate.apply {
            typeface = Typeface.createFromAsset(context.assets, "fonts/main_theme_font.otf")
        }
        holder.rank.setImageResource(SpUtils.getRankImg(songData.name ?: String(), songData.className ?: String()))
        holder.itemView.setOnLongClickListener {
            showLongPressDialog(it.context, songData)
            true
        }
    }

    fun setData(data: List<MSLSongSingleData>) {
        songList = data
        notifyDataSetChanged()
    }

    @DrawableRes
    fun changeFilterRank(): Int {
        filterRank = when (filterRank) {
            SpUtils.RANK_S -> SpUtils.RANK_SS
            SpUtils.RANK_SS -> SpUtils.RANK_SSS
            SpUtils.RANK_SSS -> SpUtils.RANK_AP
            SpUtils.RANK_AP -> String()
            else -> SpUtils.RANK_S
        }
        songList = originalList
        notifyDataSetChanged()
        return when (filterRank) {
            SpUtils.RANK_S -> R.drawable.msl_filter_rank_s_bg
            SpUtils.RANK_SS -> R.drawable.msl_filter_rank_ss_bg
            SpUtils.RANK_SSS -> R.drawable.msl_filter_rank_sss_bg
            SpUtils.RANK_AP -> R.drawable.msl_filter_rank_ap_bg
            else -> R.drawable.msl_filter_rank_all_bg
        }
    }

    // todo: more sort ways
    @DrawableRes
    fun changeSortBy(): Int {
        isSortByLevel = !isSortByLevel
        songList = originalList
        notifyDataSetChanged()
        return if (isSortByLevel) {
            R.drawable.msl_filter_sort_bg_level
        } else {
            R.drawable.msl_filter_sort_bg_default
        }
    }

    fun search(keyword: String) {
        searchingKeyword = keyword
        songList = originalList
        notifyDataSetChanged()
    }

    private fun bindImage(imageView: ImageView, songData: MSLSongSingleData) {
        imageView.setImageResource(R.mipmap.ic_launcher)
        disposeMap[imageView]?.dispose()
        disposeMap[imageView] = ImageUtils.loadWebImage(imageView.context, songData.image ?: String())
            .subscribe(
                {
                    imageView.setImageDrawable(
                        MSLSongPicDrawable(it, songData.getClassColor())
                    )
                    disposeMap.remove(imageView)
                },
                {
                    it.printStackTrace()
                    disposeMap.remove(imageView)
                }
            )
    }

    private fun showLongPressDialog(context: Context, songData: MSLSongSingleData) {
        MSLLongPressDialog(context).apply {
            callback = { rank ->
                SpUtils.setRank(songData.name ?: String(), songData.className ?: String(), rank)
                notifyDataSetChanged()
                when (rank) {
                    SpUtils.RANK_AP -> Toast.makeText(
                        context,
                        R.string.msl_congratulations_ap,
                        Toast.LENGTH_SHORT
                    ).show()
                    else -> {
                        if (!rank.isEmpty()) {
                            Toast.makeText(context, R.string.msl_congratulations, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
        }.show()
    }
}

class MSLSongListVH(view: View) : RecyclerView.ViewHolder(view) {
    val category: ImageView = view.findViewById(R.id.category)
    val image: ImageView = view.findViewById(R.id.image)
    val title: TextView = view.findViewById(R.id.title)
    val desc: TextView = view.findViewById(R.id.desc)
    val accurate: TextView = view.findViewById(R.id.accurate)
    val rank: ImageView = view.findViewById(R.id.rank)
}