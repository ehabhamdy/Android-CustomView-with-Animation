package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var loadingButtonBgColor = 0
    private var text = ""
    private var textColor = 0

    private var loadingRectWidth = 0F
    private var circleLocation = 0F

    private val valueAnimator = ValueAnimator()


    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            loadingButtonBgColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            text = getString(R.styleable.LoadingButton_text).toString()
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = loadingButtonBgColor
        canvas?.drawRoundRect(0F, 0F, widthSize.toFloat(), heightSize.toFloat(), 10F, 10F, paint);

        paint.color = Color.GRAY
        canvas?.drawRoundRect(0F, 0F, loadingRectWidth, heightSize.toFloat(), 10F, 10F, paint);

        paint.color = Color.RED
        val circleRadius = (heightSize / 2).toFloat()
        canvas?.drawCircle(circleLocation, circleRadius, circleRadius, paint);

        paint.color = textColor
        canvas?.drawText(text, (widthSize / 2).toFloat(), (heightSize / 2 + 15).toFloat(), paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun performClick(): Boolean {
        super.performClick()

        valueAnimator.setFloatValues(0f, widthSize.toFloat())

        valueAnimator.duration = 2000
        valueAnimator.reverse()
        valueAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator?) {
                val animatedValue = animation?.animatedValue as Float
                loadingRectWidth = animatedValue
                circleLocation = animatedValue
                invalidate()
            }

        })
        valueAnimator.start()

        invalidate()
        return true
    }

}