package com.loopeer.codereaderkt.ui.view

import android.content.Context
import android.support.v4.view.MotionEventCompat
import android.support.v4.view.NestedScrollingChild
import android.support.v4.view.NestedScrollingChildHelper
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebView

/*
* https://github.com/rhlff/NestedScrollWebView
* */
class NestedScrollWebView : WebView, NestedScrollingChild {

    interface ScrollChangeListener {
        fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int)
    }

    private var mLastMotionY: Int = 0

    private val mScrollOffset = IntArray(2)
    private val mScrollConsumed = IntArray(2)

    private var mNestedYOffset: Int = 0

    private var mChildHelper: NestedScrollingChildHelper? = null

    private var mScrollChangeListener: ScrollChangeListener? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        mChildHelper = NestedScrollingChildHelper(this)
        isNestedScrollingEnabled = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var result = false

        val trackedEvent = MotionEvent.obtain(event)

        val action = MotionEventCompat.getActionMasked(event)

        if (action == MotionEvent.ACTION_DOWN) {
            mNestedYOffset = 0
        }

        val y = event.y.toInt()

        event.offsetLocation(0f, mNestedYOffset.toFloat())

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mLastMotionY = y
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
                result = super.onTouchEvent(event)
            }
            MotionEvent.ACTION_MOVE -> {
                var deltaY = mLastMotionY - y

                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                    deltaY -= mScrollConsumed[1]
                    trackedEvent.offsetLocation(0f, mScrollOffset[1].toFloat())
                    mNestedYOffset += mScrollOffset[1]
                }

                val oldY = scrollY
                mLastMotionY = y - mScrollOffset[1]
                if (deltaY < 0) {
                    val newScrollY = Math.max(0, oldY + deltaY)
                    deltaY -= newScrollY - oldY
                    if (dispatchNestedScroll(0, newScrollY - deltaY, 0, deltaY, mScrollOffset)) {
                        mLastMotionY -= mScrollOffset[1]
                        trackedEvent.offsetLocation(0f, mScrollOffset[1].toFloat())
                        mNestedYOffset += mScrollOffset[1]
                    }
                }

                trackedEvent.recycle()
                result = super.onTouchEvent(trackedEvent)
            }
            MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                stopNestedScroll()
                result = super.onTouchEvent(event)
            }
        }
        return result
    }

    // NestedScrollingChild

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        mChildHelper!!.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return mChildHelper!!.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return mChildHelper!!.startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        mChildHelper!!.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        return mChildHelper!!.hasNestedScrollingParent()
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?): Boolean {
        return mChildHelper!!.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?): Boolean {
        return mChildHelper!!.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return mChildHelper!!.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return mChildHelper!!.dispatchNestedPreFling(velocityX, velocityY)
    }

    fun setScrollChangeListener(scrollChangeListener: ScrollChangeListener) {
        mScrollChangeListener = scrollChangeListener
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (mScrollChangeListener != null) {
            mScrollChangeListener!!.onScrollChanged(l, t, oldl, oldt)
        }
    }

    companion object {

        val TAG = NestedScrollWebView::class.java.simpleName
    }

}