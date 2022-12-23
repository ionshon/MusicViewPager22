package com.inu.musicviewpager2.util

import android.content.Context
import android.graphics.Canvas
import android.text.method.ScrollingMovementMethod
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Scroller
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.inu.musicviewpager2.fragments.FragmentList
import com.inu.musicviewpager2.fragments.FragmentPlay
import com.inu.musicviewpager2.fragments.FragmentPlay.Companion.isPlaying
import java.util.*
interface EventListener {
    fun onEvent(event: Boolean)
}

class VerticalScrollingTextViewKt(context: Context, attrs: AttributeSet?)
    :  AppCompatTextView(context, attrs){
    val attr = attrs
    private val DEFAULT_SPEED = 65.0f
    var speed = DEFAULT_SPEED
//    var sc = Scroller(context, LinearInterpolator())
//    val myGraphView = this.rootView

    companion object {
        lateinit var mView : VerticalScrollingTextViewKt
        val attr : AttributeSet? = null
        var isScrolling = true
        var continuousScrolling = true
        val sc = Scroller(MyApplication.applicationContext(), LinearInterpolator())
        var offset = -50
        var duration= 0
//        var paddingBottom=0
//        var paddingTop=0

//        var height2 = 0
        var viewHeight = 0
        var visibleHeight =0
        var lineHeight2 = 0
        var distance = 0
    }

    fun generate() {
//        listener.onEvent(isPlaying)
        if (!sc.isFinished ) {
            scrollStop()
            isScrolling = false
            Log.d("scroller-gen", "Stop, offset->${offset}, ${sc.currY}")
        } else if (sc.isFinished /*&& !isPlaying*/){
            startSroll()
//            computeScroll()
            isScrolling = true
            Log.d("scroller-gen", "start, offset->${offset}, ${sc.currY}")
        }

        Log.d("ListenerKKK:GEN", "isScrolling=> $isScrolling,  isPlaying=> $isPlaying, sc.isFinished=> ${sc.isFinished}")
    }
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
//        listenStart()
        mView = this
        if (isScrolling) {
            setScroller(sc)
            overScrollMode

            Log.d("scroller(scroll(1))", sc.isFinished.toString())
            val viewHeight = height
            val visibleHeight = viewHeight - paddingBottom - paddingTop
            val lineHeight = lineHeight
            val offset = -1 * visibleHeight
            val distance = visibleHeight + lineCount * lineHeight
            val duration = (distance * speed).toInt()
            sc.startScroll(0, offset, 0, distance, duration) // 여기서 false로 변경
        }

        this.setOnClickListener {
            if (!sc.isFinished) {
                scrollStop()
                isScrolling = false
                Log.d("scroller", "Stop, ${-1*height}, ${sc.currY}")
            } else if (sc.isFinished){
                startSroll()
                isScrolling = true
                Log.d("scroller", "start, offset =?${offset}, ${sc.currY}")
            }
            Log.d("ListenerKKK:SETON", "isScrolling=> $isScrolling, isPlaying=> $isPlaying, sc.isFinished=> ${sc.isFinished}")
        }

//        Log.d("scroller(onLayout1)", sc.isFinished.toString()) // true
        if (sc.isFinished) {
//            scroll()
//            Log.d("scroller(onLayout2)", sc.isFinished.toString())
        }
    }

    fun startSroll() {
//        Log.d("scroller(scroll(1))",  sc.isFinished.toString())
//        val viewHeight = height
//        val visibleHeight = viewHeight - paddingBottom - paddingTop
//        val lineHeight = lineHeight
//        val offset = -1 * visibleHeight
//        val distance = visibleHeight + lineCount * lineHeight
//        val duration = (distance * speed).toInt()
        if (this.computeVerticalScrollOffset() != 0) {
//            height2 = height
            viewHeight = height
            visibleHeight = viewHeight - paddingBottom - paddingTop
            lineHeight2 = lineHeight
            distance = visibleHeight + lineCount * lineHeight2
            duration = (distance * speed).toInt()

            offset = mView.computeVerticalScrollOffset()
        }
        sc.startScroll(0, offset, 0, distance, duration) // 여기서 false로 변경
        computeScroll()

        Log.d("scroller(scroll(2))",  "offset="+ offset + ",  distance=$distance, duration= $duration, height=$viewHeight, paddingBottom=$paddingBottom, paddingTop=$paddingTop,  lineHeight=$lineHeight, speed=$speed")

    } //scroll()

    fun scrollStop() {
        continuousScrolling = false
        sc.abortAnimation()
        this.movementMethod = ScrollingMovementMethod()
    }
/*
    fun onEvent(event: Boolean) {
        Log.d("EventListener(vetical)", "$isPlaying")
        if (event) {

        } else {
            sc.abortAnimation()
//                    .movementMethod = ScrollingMovementMethod()
            VerticalScrollingTextViewKt.isScrolling = false}
    }*/


    // 여기서 스크롤 끝나고 다시시작 명령 scroll()
   /* override fun computeScroll() {
        super.computeScroll()
        if (sc.isFinished && continuousScrolling) {
            Log.d("computeScroller33333", sc.isFinished.toString())
            startSroll()
//            scrollToTop()
        }
    }*/
    /*override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (null == sc) return
        if (sc.isFinished && continuousScrolling) {
            startSroll()

            Log.d("Scroller(onDraw)", sc.isFinished.toString())
        }
    }*/
}

/*
    fun VerticalScrollingTextView(context: Context?) {
//        super(context)
        init(null)
        scrollerInstance(context)
        Log.d("computeScroller0", scroller.isFinished.toString())
    }

    fun VerticalScrollingTextView(context: Context?, attrs: AttributeSet?) {
//        super(context, attrs)
        init(attrs)
        scrollerInstance(context)
        Log.d("computeScroller0", scroller.isFinished.toString())
    }*/

/*
    private fun init(attrs: AttributeSet?) {
        val attrArray = context.obtainStyledAttributes(attrs, R.styleable.VerticalScrollingTextView,0, 0)
        Log.d("computeScroller0", scroller.isFinished.toString())
        initAttributes(attrArray)
    }
    private fun initAttributes(attrArray: TypedArray) {
        val textStyle = attrArray.getString(R.styleable.VerticalScrollingTextView_myTextStyle)
        if (textStyle == "") {
        } else {
            val tf = Typeface.createFromAsset(context.assets, textStyle)
            typeface = tf
            Log.d("computeScroller", scroller.isFinished.toString())
        }
    }*/
/* open fun scrollerInstance(context: Context?) {
     val sc = Scroller(context, LinearInterpolator())
     Log.d("scroller1", sc.isFinished().toString())
     setScroller(sc)
 }*/

