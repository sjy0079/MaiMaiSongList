package org.bbs.maimaisonglist.widgets

import android.app.Dialog
import android.content.Context
import kotlinx.android.synthetic.main.msl_long_press_dialog.*
import org.bbs.maimaisonglist.R
import org.bbs.maimaisonglist.utils.SpUtils

/**
 * @author shenjiayi@didiglobal.com
 * @since  2019-11-15
 */
class MSLLongPressDialog(context: Context) : Dialog(context) {
    var callback: ((String) -> Unit)? = null

    init {
        setContentView(R.layout.msl_long_press_dialog)
        rankS.setOnClickListener {
            dismiss()
            callback?.invoke(SpUtils.RANK_S)
        }
        rankSS.setOnClickListener {
            dismiss()
            callback?.invoke(SpUtils.RANK_SS)
        }
        rankSSS.setOnClickListener {
            dismiss()
            callback?.invoke(SpUtils.RANK_SSS)
        }
        rankAP.setOnClickListener {
            dismiss()
            callback?.invoke(SpUtils.RANK_AP)
        }
        rankCancel.setOnClickListener {
            dismiss()
            callback?.invoke(String())
        }
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}