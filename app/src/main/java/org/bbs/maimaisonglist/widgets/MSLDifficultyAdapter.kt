package org.bbs.maimaisonglist.widgets

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import org.bbs.maimaisonglist.model.MSLDifficultyData

/**
 * @author shenjiayi@didiglobal.com
 * @since  2019-11-13
 */
class MSLDifficultyAdapter : RecyclerView.Adapter<MSLDifficultyVH>() {
    val items = MSLDifficultyData.getDefaultDataList()
    var currentPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MSLDifficultyVH {
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                MATCH_PARENT, MATCH_PARENT
            )
        }
        return MSLDifficultyVH(imageView)
    }

    override fun getItemCount() = items.count()

    override fun onBindViewHolder(holder: MSLDifficultyVH, position: Int) {
        holder.image.setImageResource(items[position].src)
    }
}

class MSLDifficultyVH(val image: ImageView) : RecyclerView.ViewHolder(image)
