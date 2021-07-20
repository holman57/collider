package com.game.collider.components

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import com.game.collider.R
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

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
        val min = 0f
        val randomWidth: Double = min + Math.random() * (bounds.width() - min)
        val randomHeight: Double = min + Math.random() * (bounds.height() - min)
        ball.location.offset(ball.location.left, randomWidth.toFloat())
        ball.location.offset(ball.location.top, randomHeight.toFloat())
        ball.location.offset(ball.location.right, 0f)
        ball.location.offset(ball.location.bottom, 0f)
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
        val rotationAmount = 0.01
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
                heading = "┘"
            } else if (orientation <= Math.PI / 2 && orientation >= 0) {
                orientation += rotationAmount
                heading = "┐"
            } else if (orientation <= 0 && orientation >= -Math.PI / 2) {
                if (-1 * (Math.PI - theta) > -Math.PI / 4) {
                    orientation += rotationAmount
                } else {
                    orientation -= rotationAmount
                }
                heading = "┌"
            } else if (orientation <= -Math.PI / 2) {
                orientation -= rotationAmount
                heading = "└"
            }
            centerDirection = "┌"
        } else if (deltaX > 0 && deltaY < 0) {
            if (orientation >= Math.PI / 2) {
                orientation += rotationAmount
                heading = "┘"
            } else if (orientation <= Math.PI / 2 && orientation >= 0) {
                if (Math.PI - (-theta) > Math.PI / 4) {
                    orientation -= rotationAmount
                } else {
                    orientation += rotationAmount
                }
                heading = "┐"
            } else if (orientation <= 0 && orientation >= -Math.PI / 2) {
                orientation -= rotationAmount
                heading = "┌"
            } else if (orientation <= -Math.PI / 2) {
                if (orientation < theta) {
                    orientation += rotationAmount
                } else {
                    orientation -= rotationAmount
                }
                heading = "└"
            }
            centerDirection = "┐"
        } else if (deltaX > 0 && deltaY > 0) {
            if (orientation >= Math.PI / 2) {
                if (-1 * (Math.PI - theta) > -Math.PI / 4) {
                    orientation += rotationAmount
                } else {
                    orientation -= rotationAmount
                }
                heading = "┘"
            } else if (orientation <= Math.PI / 2 && orientation >= 0) {
                orientation -= rotationAmount
                heading = "┐"
            } else if (orientation <= 0 && orientation >= -Math.PI / 2) {
                if (orientation > theta) {
                    orientation -= rotationAmount
                } else {
                    orientation += rotationAmount
                }
                heading = "┌"
            } else if (orientation <= -Math.PI / 2) {
                orientation += rotationAmount
                heading = "└"
            }
            centerDirection = "┘"
        } else if (deltaX < 0 && deltaY > 0) {
            if (orientation >= Math.PI / 2) {
                orientation -= rotationAmount
                heading = "┘"
            } else if (orientation <= Math.PI / 2 && orientation >= 0) {
                if (orientation > theta) {
                    orientation -= rotationAmount
                } else {
                    orientation += rotationAmount
                }
                heading = "┐"
            } else if (orientation <= 0 && orientation >= -Math.PI / 2) {
                orientation += rotationAmount
                heading = "┌"
            } else if (orientation <= -Math.PI / 2) {
                if (Math.PI - (-theta) > Math.PI / 4) {
                    orientation += rotationAmount
                } else {
                    orientation -= rotationAmount
                }
                heading = "└"
            }
            centerDirection = "└"
        }

        val velocityScalar = 0.01
        val horzComp = cos(orientation) * velocityScalar
        val vertComp = sin(orientation) * velocityScalar
        when (heading) {
            "└" -> {
                ball.movVec.x = horzComp.toFloat()
                ball.movVec.y = vertComp.toFloat()
            }
            "┌" -> {
                ball.movVec.x = -horzComp.toFloat()
                ball.movVec.y = vertComp.toFloat()
            }
            "┘" -> {
                ball.movVec.x = horzComp.toFloat()
                ball.movVec.y = vertComp.toFloat()
            }
            "┐" -> {
                ball.movVec.x = horzComp.toFloat()
                ball.movVec.y = vertComp.toFloat()
            }
        }

        println(" $centerDirection $heading  theta: $theta, orient: $orientation")
        println("horzComp: $horzComp   vertComp: $vertComp")


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
                    ball.location.offset(-ball.location.left, 0f)
                } else {
                    ball.location.offset(o.right - ball.location.right, 0f)
                }
            }
            if (ball.location.top <= 0 || ball.location.bottom >= o.bottom) {
                ball.movVec.y = -ball.movVec.y
                if (ball.location.top <= 0) {
                    ball.location.offset(-ball.location.top, 0f)
                } else {
                    ball.location.offset(o.bottom - ball.location.bottom, 0f)
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
