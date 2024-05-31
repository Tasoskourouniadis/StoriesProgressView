package com.kafkaai.storiesprogressview.storiesprogressview


import android.content.ContentValues.TAG
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.ScaleAnimation
import android.view.animation.Transformation
import android.widget.FrameLayout
import com.kafkaai.storiesprogressview.R


class PausableProgressBar : FrameLayout {

    private val DEFAULT_PROGRESS_DURATION = 2000

    private var frontProgressView: View
    private var maxProgressView: View

    private var animation: PausableScaleAnimation? = null
    private var duration = DEFAULT_PROGRESS_DURATION.toLong()
    private var callback: Callback? = null


    interface Callback {
        fun onStartProgress()
        fun onFinishProgress()
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        LayoutInflater.from(context).inflate(R.layout.pausable_progress, this, true)
        frontProgressView = findViewById(R.id.front_progress)
        maxProgressView = findViewById(R.id.max_progress)

    }

    fun setDuration(duration: Long) {
        this.duration = duration
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun setMax() {
        finishProgress(true)
    }

    fun setMin() {
        finishProgress(false)
    }

    fun setMinWithoutCallback() {
        maxProgressView.setBackgroundResource(R.color.progress_secondary)
        maxProgressView.visibility = VISIBLE
        if (animation != null) {
            animation!!.setAnimationListener(null)
            animation!!.cancel()
        }
    }

    fun setMaxWithoutCallback() {
        maxProgressView.setBackgroundResource(R.color.progress_max_active)
        maxProgressView.visibility = VISIBLE
        if (animation != null) {
            animation!!.setAnimationListener(null)
            animation!!.cancel()
        }
    }

    private fun finishProgress(isMax: Boolean) {
        if (isMax) maxProgressView.setBackgroundResource(R.color.progress_max_active)
        maxProgressView.visibility = if (isMax) VISIBLE else GONE
        if (animation != null) {
            animation!!.setAnimationListener(null)
            animation!!.cancel()
            if (callback != null) {
                callback!!.onFinishProgress()
            }
        }
    }

    fun startProgress() {
        maxProgressView.visibility = GONE
        animation = PausableScaleAnimation(0F, 1F, 1F, 1F, Animation.ABSOLUTE, 0F, Animation.RELATIVE_TO_SELF, 0F)
        animation!!.duration = duration
        animation!!.interpolator = LinearInterpolator()
        animation!!.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                frontProgressView.visibility = VISIBLE
                if (callback != null) callback!!.onStartProgress()
            }
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                if (callback != null) callback!!.onFinishProgress()
            }
        })
        animation!!.fillAfter = true
        frontProgressView.startAnimation(animation)
    }

    fun pauseProgress() {
        animation!!.pause()
    }

    fun resumeProgress() {
        animation!!.resume()
    }

    fun clear() {
        if (animation != null) {
            animation!!.setAnimationListener(null)
            animation!!.cancel()
            animation = null
        }
    }


    inner class PausableScaleAnimation(fromX: Float, toX: Float, fromY: Float, toY: Float, pivotXType: Int, pivotXValue: Float, pivotYType: Int, pivotYValue: Float) : ScaleAnimation(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType, pivotYValue) {

        private var mElapsedAtPause: Long = 0
        private var mPaused = false

        override fun getTransformation(currentTime: Long, outTransformation: Transformation?, scale: Float): Boolean {
            if (mPaused && mElapsedAtPause == 0L) {
                mElapsedAtPause = currentTime - startTime
            }
            if (mPaused) {
                startTime = currentTime - mElapsedAtPause
            }
            return super.getTransformation(currentTime, outTransformation, scale)
        }

        /***
         * pause animation
         */
        fun pause() {
            if (mPaused) return
            mElapsedAtPause = 0
            mPaused = true
        }

        /***
         * resume animation
         */
        fun resume() {
            mPaused = false
        }
    }
}