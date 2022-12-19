package dlna

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.CharacterStyle
import android.text.style.ImageSpan
import androidx.annotation.DrawableRes

class SpannableBuilder(context: Context?) {

    private var mBuilder: SpannableStringBuilder? = null
    private var mContext: Context? = null
/*
    fun SpannableBuilder(context: Context?) {
        mBuilder = SpannableStringBuilder()
        mContext = context
    }*/

    fun append(seq: CharSequence?) {
        mBuilder!!.append(seq)
    }

    fun append(seq: CharSequence, vararg whats: CharacterStyle?) {
        append(seq, 0, *whats)
    }

    fun append(seq: CharSequence, flags: Int, vararg whats: CharacterStyle?) {
        val start = mBuilder!!.length
        val end = start + seq.length
        mBuilder!!.append(seq)
        for (what in whats) {
            mBuilder!!.setSpan(what, start, end, flags)
        }
    }

    fun append(@DrawableRes drawableResId: Int) {
        val d = mContext!!.resources.getDrawable(drawableResId)
        d.setBounds(0, 0, d.intrinsicWidth, d.intrinsicHeight)
        append(" ", ImageSpan(d, ImageSpan.ALIGN_BASELINE))
    }

    fun build(): SpannableStringBuilder? {
        return mBuilder
    }

    fun length(): Int {
        return mBuilder!!.length
    }
}
