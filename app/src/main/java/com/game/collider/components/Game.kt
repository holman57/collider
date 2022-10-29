package com.game.collider.components

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import com.game.collider.R
import kotlin.math.*

class Game(context: Context, vsAI: Boolean = true, var bounds: Rect) : GameLoop {
    enum class STATE { END, PAUSED, STARTED }

    override var updateRate: Int = 60
    override var timeToUpdate: Long = 0L
    var state = STATE.PAUSED
    var ball_sprite = R.drawable.ball
    var ball: Ball = Ball(context, ball_sprite)
    var orbital_bmp = BitmapFactory.decodeResource(context.resources, ball_sprite)
    var orientation = 0.0
//    var players: Array<Player> = arrayOf(
//        Player(context, R.drawable.button),
//        if (vsAI) BotPlayer(context, R.drawable.button, this) else
//            Player(context, R.drawable.button)
//    )

    init {
//        players[0].location.offsetTo(
//            bounds.exactCenterX() - players[0].location.width() / 2,
//            bounds.bottom - players[0].location.height()
//        )
//        ball.location.offsetTo(
//            players[1].location.centerX() - ball.location.centerX(),
//            players[1].location.bottom
//        )
        val min = 30f
        val randomWidth: Double = min + Math.random() * (bounds.width() - min)
        val randomHeight: Double = min + Math.random() * (bounds.height() - min)
        ball.location.offset(randomWidth.toFloat(), randomHeight.toFloat())
        println(ball.location)
    }

    override fun render(canvas: Canvas?) {
        orent()
        ball.render(canvas)
//        for (p in players) p.render(canvas)
    }

    override fun update() {
        ball.update()
//        for (p in players) p.update()
        state = if (collide(bounds)) STATE.END else state
//        for (p in players) collide(p)
    }

    fun orent() {
        val rotationAmount = 0.025
        val deltaY = ball.centerY - bounds.exactCenterY()
        val deltaX = ball.centerX - bounds.exactCenterX()
        ball.deltaCenter.bottom = bounds.exactCenterY()
        ball.deltaCenter.right = bounds.exactCenterX()
        var theta = atan2(deltaY, deltaX).toDouble()

        if (deltaX < 0 && deltaY < 0) {
            theta += 3 * Math.PI / 2
        } else {
            theta -= Math.PI / 2
        }

        val thetaDegrees = Math.toDegrees(theta)
        var heading = ""
        var centerDirection = ""

        if (orientation > Math.PI) {
            orientation = -Math.PI + (orientation - Math.PI)
        }
        if (orientation < -Math.PI) {
            orientation = Math.PI + (orientation + Math.PI)
        }
        if (deltaX < 0 && deltaY < 0) {
            if (orientation >= Math.PI / 2) {
                if (orientation > theta) {
                    orientation -= rotationAmount
                } else {
                    orientation += rotationAmount
                }
                heading = "Quadrant IV"
            } else if (orientation <= Math.PI / 2 && orientation >= 0) {
                orientation += rotationAmount
                heading = "Quadrant I"
            } else if (orientation <= 0 && orientation >= -Math.PI / 2) {
                if (-1 * (Math.PI - theta) > -Math.PI / 4) {
                    orientation += rotationAmount
                } else {
                    orientation -= rotationAmount
                }
                heading = "Quadrant II"
            } else if (orientation <= -Math.PI / 2) {
                orientation -= rotationAmount
                heading = "Quadrant III"
            }
            centerDirection = "Quadrant II"
        } else if (deltaX > 0 && deltaY < 0) {
            if (orientation >= Math.PI / 2) {
                orientation += rotationAmount
                heading = "Quadrant IV"
            } else if (orientation <= Math.PI / 2 && orientation >= 0) {
                if (Math.PI - (-theta) > Math.PI / 4) {
                    orientation -= rotationAmount
                } else {
                    orientation += rotationAmount
                }
                heading = "Quadrant I"
            } else if (orientation <= 0 && orientation >= -Math.PI / 2) {
                orientation -= rotationAmount
                heading = "Quadrant II"
            } else if (orientation <= -Math.PI / 2) {
                if (orientation < theta) {
                    orientation += rotationAmount
                } else {
                    orientation -= rotationAmount
                }
                heading = "Quadrant III"
            }
            centerDirection = "Quadrant I"
        } else if (deltaX > 0 && deltaY > 0) {
            if (orientation >= Math.PI / 2) {
                if (-1 * (Math.PI - theta) > -Math.PI / 4) {
                    orientation += rotationAmount
                } else {
                    orientation -= rotationAmount
                }
                heading = "Quadrant IV"
            } else if (orientation <= Math.PI / 2 && orientation >= 0) {
                orientation -= rotationAmount
                heading = "Quadrant I"
            } else if (orientation <= 0 && orientation >= -Math.PI / 2) {
                if (orientation > theta) {
                    orientation -= rotationAmount
                } else {
                    orientation += rotationAmount
                }
                heading = "Quadrant II"
            } else if (orientation <= -Math.PI / 2) {
                orientation += rotationAmount
                heading = "Quadrant III"
            }
            centerDirection = "Quadrant IV"
        } else if (deltaX < 0 && deltaY > 0) {
            if (orientation >= Math.PI / 2) {
                orientation -= rotationAmount
                heading = "Quadrant IV"
            } else if (orientation <= Math.PI / 2 && orientation >= 0) {
                if (orientation > theta) {
                    orientation -= rotationAmount
                } else {
                    orientation += rotationAmount
                }
                heading = "Quadrant I"
            } else if (orientation <= 0 && orientation >= -Math.PI / 2) {
                orientation += rotationAmount
                heading = "Quadrant II"
            } else if (orientation <= -Math.PI / 2) {
                if (Math.PI - (-theta) > Math.PI / 4) {
                    orientation += rotationAmount
                } else {
                    orientation -= rotationAmount
                }
                heading = "Quadrant III"
            }
            centerDirection = "Quadrant III"
        }

        val velocityScalar = 1
        val horzComp = cos(orientation) * velocityScalar
        val vertComp = sin(orientation) * velocityScalar
        val ts = 1.5f
        when (heading) {
            "Quadrant I" -> {
                ball.movVec.set(ts, -ts)
            }
            "Quadrant II" -> {
                ball.movVec.set(-ts, -ts)
            }
            "Quadrant III" -> {
                ball.movVec.set(-ts, ts)
            }
            "Quadrant IV" -> {
                ball.movVec.set(ts, ts)
            }
        }

        val orientationDegrees = Math.toDegrees(orientation)
        val matrix = Matrix()
        matrix.postRotate(orientationDegrees.toFloat())
        val rotatedBitmap = Bitmap.createBitmap(
            orbital_bmp,
            0,
            0,
            orbital_bmp.width,
            orbital_bmp.height,
            matrix,
            true
        )

        ball.shape = rotatedBitmap
    }


    fun processInput(o: Any?) {
        if (o is MotionEvent) {
//            if (o.y > bounds.exactCenterY()) {
//                players[0].location.offsetTo(o.x, players[0].location.top)
//            } else if (players[1] !is BotPlayer) {
//                players[1].location.offsetTo(o.x, 0f)
//            }
        }
    }

    /**
     * Ball to any other object
     * @param o or Player
     * @return true = end game
     */
    fun collide(o: Any?): Boolean {
        if (o is Rect) {
            if (ball.location.left <= 0 || ball.location.right >= o.right) {
                ball.movVec.x = -ball.movVec.x
                if (ball.location.left <= 0) {
                    ball.location.offset(1f, 0f)
                }
                if (ball.location.right >= o.right) {
                    ball.location.offset(-1f, 0f)
                }
            }
            if (ball.location.top <= 0 || ball.location.bottom >= o.bottom) {
                ball.movVec.y = -ball.movVec.y
                if (ball.location.top <= 0) {
                    ball.location.offset(0f, 1f)
                }
                if (ball.location.bottom >= o.bottom) {
                    ball.location.offset(0f, -1f)
                }
//                if (ball.location.top <= 0) {
//                    players[0].score++
//                } else {
//                    players[1].score++
//                }
//                return true
            }
        } else if (o is Player) {
            if (o.location.contains(ball.location.centerX(), ball.location.bottom)) {
                ball.movVec.y = -ball.movVec.y
                ball.location.offset(0f, o.location.top - ball.location.bottom)
            } else if (o.location.contains(ball.location.centerX(), ball.location.top)
            ) {
                ball.movVec.y = -ball.movVec.y
                ball.location.offset(0f, o.location.bottom - ball.location.top)
            }
        }
        return false
    }
}
