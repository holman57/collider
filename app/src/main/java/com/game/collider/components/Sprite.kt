package com.game.collider.components

import android.content.Context
import android.graphics.*

abstract class Sprite(context: Context, shapeId: Int?) : GameLoop {
    @Volatile
    var movVec = PointF()
    var paint = Paint()
    var context: Context? = context
    var location = RectF(0f, 0f, 0f, 0f)
    var centerX = (location.left + location.right) / 2
    var centerY = (location.bottom + location.top) / 2
    var deltaCenter = RectF(centerX, centerY, 0f, 0f)
    var shape: Bitmap? = null
    override fun render(canvas: Canvas?) {
        if (shape != null) {
            canvas?.drawBitmap(shape!!, null, location, paint)
        } else {
            canvas?.drawRect(location, paint)
        }
        centerX = (location.left + location.right) / 2
        centerY = (location.bottom + location.top) / 2
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        linePaint.color = Color.RED
        linePaint.strokeWidth = 2F
        deltaCenter.left = centerX
        deltaCenter.top = centerY
        canvas?.drawLine(
            deltaCenter.left,
            deltaCenter.top,
            deltaCenter.right,
            deltaCenter.bottom,
            linePaint
        )
    }

    init {
        this.shape = shapeId?.let { BitmapFactory.decodeResource(context.resources, it) }
        if (shape != null) {
            location.right = shape!!.width.toFloat()
            location.bottom = shape!!.height.toFloat()
        }
    }
}
