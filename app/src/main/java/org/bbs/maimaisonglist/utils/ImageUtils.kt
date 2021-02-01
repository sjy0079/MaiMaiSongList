package org.bbs.maimaisonglist.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.NonNull
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import io.reactivex.Observable

/**
 * @author BBS
 * @since  2019-11-14
 */
object ImageUtils {
    fun loadWebImage(@NonNull context: Context, url: String): Observable<Bitmap> {
        return Observable.create { emitter ->
            if (url.isEmpty()) {
                emitter.onError(Exception("Image url cannot be empty"))
                return@create
            }
            Glide.with(context).load(url).asBitmap().dontAnimate().into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {
                    if (!emitter.isDisposed) {
                        emitter.onNext(resource)
                        emitter.onComplete()
                    }
                }

                override fun onLoadFailed(e: Exception, errorDrawable: Drawable?) {
                    super.onLoadFailed(e, errorDrawable)
                    if (!emitter.isDisposed) {
                        emitter.onError(e)
                    }
                }
            })
        }
    }
}