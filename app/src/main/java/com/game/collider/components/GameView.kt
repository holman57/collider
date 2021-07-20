package com.game.collider.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

// https://stackoverflow.com/questions/24561596/smoothing-out-android-game-loop
class GameView(context: Context?) : SurfaceView(context), SurfaceHolder.Callback, Runnable,
    GameLoop {
    override var updateRate: Int = 120
    override var timeToUpdate: Long = System.currentTimeMillis()
    private var mThread: Thread? = null
    lateinit var mCanvas: Canvas
    var game: Game? = null

    var mHolder: SurfaceHolder? = holder
    private var bounds: Rect = Rect()
        set(value) {
            field = value
            setup()
        }

    init {
        if (mHolder != null) {
            mHolder?.addCallback(this)
        }
    }

    private fun setup(vsAI: Boolean = true) {
        game = Game(this.context, vsAI, bounds)
    }

    private fun start() {
        game?.state = Game.STATE.STARTED
        mThread = Thread(this)
        timeToUpdate = System.currentTimeMillis()
        mThread?.start()
    }

    private fun stop() {
        game?.state = Game.STATE.END
        try {
            // Stop the thread == rejoin the main thread.
            mThread?.join()
        } catch (e: InterruptedException) {
        }
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        Log.d(this.toString(), "surface created")
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
        Log.d(this.toString(), "surface changed")
        bounds = Rect(0, 0, p2, p3)
        start()
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        stop()
    }

    override fun run() {
        while (game?.state == Game.STATE.STARTED) {
            while (shouldUpdate) {
                update()
            }
            render()
        }
    }

    override fun update() {
        timeToUpdate = System.currentTimeMillis() + 1000L / updateRate
        game?.update()
    }

    override fun render(canvas: Canvas?) {
        if (mHolder!!.surface?.isValid == true) {
            mCanvas = mHolder!!.lockCanvas()
            mCanvas.drawColor(Color.WHITE)
            game?.render(mCanvas)
            mHolder!!.unlockCanvasAndPost(mCanvas)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        game?.processInput(event)
        return true
    }
}
