package com.inu.musicviewpager2.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.inu.musicviewpager2.R;

public class VerticalScrollingTextView extends androidx.appcompat.widget.AppCompatTextView {

        private static final float DEFAULT_SPEED = 65.0f;
        public Scroller scroller;
        public float speed = DEFAULT_SPEED;
        public boolean continuousScrolling = true;
        public VerticalScrollingTextView(Context context) {
                super(context);
                init(null);
                scrollerInstance(context);
        }

        public VerticalScrollingTextView(Context context, AttributeSet attrs) {
                super(context, attrs);
                init(attrs);
                scrollerInstance(context);
        }

          /*  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public VerticalScrollingTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
                                             int defStyleRes) {
                super(context, attrs, defStyleAttr, defStyleRes);
                init(attrs, defStyleAttr);
                scrollerInstance(context);
            }*/


        private void init(AttributeSet attrs) {
                TypedArray attrArray = getContext().obtainStyledAttributes(attrs, R.styleable.VerticalScrollingTextView,
                        0, 0);
                initAttributes(attrArray);
        }

        protected void initAttributes(TypedArray attrArray) {
                String textStyle = attrArray.getString(R.styleable.VerticalScrollingTextView_myTextStyle);
                if (textStyle == null || textStyle.equals("")) {

                } else {
                        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), textStyle);
                        setTypeface(tf);
                }

        }

        public void scrollerInstance(Context context) {
                scroller = new Scroller(context, new LinearInterpolator());
                setScroller(scroller);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (scroller.isFinished()) {
                        scroll();
                }
        }

        public void scroll() {
                int viewHeight = getHeight();
                int visibleHeight = viewHeight - getPaddingBottom() - getPaddingTop();
                int lineHeight = getLineHeight();
                int offset = -1 * visibleHeight;
                int distance = visibleHeight + getLineCount() * lineHeight;
                int duration = (int) (distance * speed);
                scroller.startScroll(0, offset, 0, distance, duration);
                Log.d("scroller", String.valueOf(scroller.isFinished()));
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
                Log.d("scroller", String.valueOf(scroller.isFinished()));
                return super.onTouchEvent(event);

        }

        @Override
        public void computeScroll() {
                super.computeScroll();
               /* if (null == scroller)
                return;*/
                if (scroller.isFinished() && continuousScrolling) {
                        Log.d("computeScroller", String.valueOf(scroller.isFinished()));
                        scroll();
                }
        }
        @Override protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (null == scroller)
                        return;
                if (scroller.isFinished() && continuousScrolling) {
                        scroll();
                }
        }

        public void setSpeed(float speed) {
                this.speed = speed;
                }

        public float getSpeed() {
                return speed;
                }

        public void setContinuousScrolling(boolean continuousScrolling) {
                this.continuousScrolling = continuousScrolling;
                }

        public boolean isContinuousScrolling() {
                return continuousScrolling;
                }
}
