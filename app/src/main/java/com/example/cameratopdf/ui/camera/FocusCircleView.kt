package com.example.cameratopdf.ui.camera

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class FocusCircleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = context.getColor(android.R.color.white)
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private var circleX: Float = 0f
    private var circleY: Float = 0f
    private var radius: Float = 100f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(circleX, circleY, radius, paint)
    }

    fun setCirclePosition(x: Float, y: Float) {
        circleX = x
        circleY = y
        invalidate()
    }
}