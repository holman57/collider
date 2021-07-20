package com.game.collider.components

import android.content.Context
import java.lang.System.currentTimeMillis

class Ball(context: Context, shapeId: Int?) : Sprite(context, shapeId) {
    var ratio: Float = 1f
    override var updateRate: Int = 100
    override var timeToUpdate: Long = currentTimeMillis()

    init {
        val min = 2f
        val max = 5f
        val randomX: Double = min + Math.random() * (max - min)
        val randomY: Double = min + Math.random() * (max - min)

        this.movVec.set(randomX.toFloat(), randomY.toFloat())
    }

    override fun update() {
        if (shouldUpdate) {
            val current = currentTimeMillis()
            val delta = current - timeToUpdate
            ratio = 1f + delta.toFloat() * updateRate / 1000f
            timeToUpdate = current + 1000L / updateRate
            location.offset(
                movVec.x * ratio,
                movVec.y * ratio
            )
        }
    }
}
