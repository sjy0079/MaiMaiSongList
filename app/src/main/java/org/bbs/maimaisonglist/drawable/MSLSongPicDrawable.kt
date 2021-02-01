package org.bbs.maimaisonglist.drawable

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import org.bbs.maimaisonglist.utils.WindowUtils


/**
 * @author BBS
 * @since  2019-11-14
 */
class MSLSongPicDrawable(private val image: Bitmap, @ColorInt borderColor: Int) : Drawable() {
    companion object {
        const val RADIUS_DP = 6F
        const val STROKE_WIDTH_DP = 3F
    }

    private val bitmapShader = BitmapShader(image, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    private val paint = Paint().apply {
        isAntiAlias = true
        shader = bitmapShader
    }
    private val borderPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = WindowUtils.dip2px(STROKE_WIDTH_DP)
        color = borderColor
    }
    private var boundsRect: Rect = Rect()

    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(
            RectF(0f, 0f, boundsRect.width().toFloat(), boundsRect.height().toFloat()),
            WindowUtils.dip2px(RADIUS_DP + STROKE_WIDTH_DP),
            WindowUtils.dip2px(RADIUS_DP + STROKE_WIDTH_DP),
            paint
        )
        canvas.drawRoundRect(
            RectF(
                borderPaint.strokeWidth / 2,
                borderPaint.strokeWidth / 2,
                boundsRect.width().toFloat() - borderPaint.strokeWidth / 2,
                boundsRect.height().toFloat() - borderPaint.strokeWidth / 2
            ),
            WindowUtils.dip2px(RADIUS_DP),
            WindowUtils.dip2px(RADIUS_DP),
            borderPaint
        )
    }

    private fun updateShaderMatrix() {
        val scale: Float
        var dx = 0f
        var dy = 0f
        val shaderMatrix = Matrix()
        if (image.width * boundsRect.height() > boundsRect.width() * image.height) {
            scale = boundsRect.height() / image.height.toFloat()
            dx = (boundsRect.width() - image.width * scale) * 0.5f
        } else {
            scale = boundsRect.width() / image.width.toFloat()
            dy = (boundsRect.height() - image.height * scale) * 0.5f
        }
        shaderMatrix.setScale(scale, scale)
        shaderMatrix.postTranslate(dx + 0.5f, dy + 0.5f)
        bitmapShader.setLocalMatrix(shaderMatrix)
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        boundsRect = bounds ?: Rect()
        updateShaderMatrix()
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun getOpacity() = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
}