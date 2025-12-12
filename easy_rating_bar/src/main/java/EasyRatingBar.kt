package com.ext.easy_rating_bar

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import kotlin.math.cos
import kotlin.math.sin

class EasyRatingBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val TYPE_STAR = 0
        const val TYPE_HEART = 1
        const val TYPE_EMOJI = 2
    }

    // Properties
    private var ratingType = TYPE_STAR
    private var starCount = 5
    private var rating = 0f
    private var animatedRating = 0f
    private var iconSize = 100f
    private var iconSpacing = 20f

    /** DEFAULT COLORS */
    private val defaultFilledColor = 0xFFFFD700.toInt() // Gold
    private val defaultEmptyColor  = 0xFFE0E0E0.toInt() // Light Gray

    /** USER COLORS (if not provided → default applied) */
    private var filledColor = defaultFilledColor
    private var emptyColor  = defaultEmptyColor

    private var isIndicator = false
    private var enableAnimation = true
    private var enableSwipe = true
    private var useDrawables = true

    // Drawables
    private var starEmpty: Drawable? = null
    private var starFilled: Drawable? = null
    private var heartEmpty: Drawable? = null
    private var heartFilled: Drawable? = null
    private var emojiEmpty: Drawable? = null
    private var emojiFilled: Drawable? = null

    // Animation
    private var scaleAnimators = mutableMapOf<Int, ValueAnimator>()
    private var scales = FloatArray(starCount) { 1f }

    // Paints (fallback)
    private val filledPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val emptyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val emojiPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }

    // Listener
    private var onRatingChangeListener: ((Float) -> Unit)? = null

    private val emojis = arrayOf("😢","😕","😐","😊","😍")

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.EasyRatingBar, 0, 0).apply {
            try {
                ratingType = getInt(R.styleable.EasyRatingBar_ratingType, TYPE_STAR)
                starCount = getInt(R.styleable.EasyRatingBar_starCount, 5)
                rating = getFloat(R.styleable.EasyRatingBar_rating, 0f)

                iconSize = getDimension(R.styleable.EasyRatingBar_iconSize, 100f)
                iconSpacing = getDimension(R.styleable.EasyRatingBar_iconSpacing, 20f)

                /** APPLY USER COLORS IF PROVIDED ELSE DEFAULT */
                filledColor = getColor(R.styleable.EasyRatingBar_filledColor, defaultFilledColor)
                emptyColor  = getColor(R.styleable.EasyRatingBar_emptyColor, defaultEmptyColor)

                isIndicator = getBoolean(R.styleable.EasyRatingBar_isIndicator, false)
                enableAnimation = getBoolean(R.styleable.EasyRatingBar_enableAnimation, true)
                enableSwipe = getBoolean(R.styleable.EasyRatingBar_enableSwipe, true)
                useDrawables = getBoolean(R.styleable.EasyRatingBar_useDrawables, true)
            } finally {
                recycle()
            }
        }

        /** APPLY COLOR TO PAINTS */
        filledPaint.color = filledColor
        emptyPaint.color = emptyColor
        emojiPaint.textSize = iconSize * 0.7f

        scales = FloatArray(starCount) { 1f }
        animatedRating = rating

        loadDrawables()
    }

    /** Load drawables safely */
    private fun loadDrawables() {
        try {
            starEmpty = ContextCompat.getDrawable(context, R.drawable.ic_star_empty)
            starFilled = ContextCompat.getDrawable(context, R.drawable.ic_star_filled)
            heartEmpty = ContextCompat.getDrawable(context, R.drawable.ic_heart_empty)
            heartFilled = ContextCompat.getDrawable(context, R.drawable.ic_heart_filled)
            emojiEmpty = ContextCompat.getDrawable(context, R.drawable.ic_emoji_empty)
            emojiFilled = ContextCompat.getDrawable(context, R.drawable.ic_emoji_filled)
        } catch (e: Exception) {
            useDrawables = false
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = (iconSize * starCount + iconSpacing * (starCount - 1)).toInt()
        val desiredHeight = (iconSize * 1.2f).toInt()
        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val totalWidth = iconSize * starCount + iconSpacing * (starCount - 1)
        val startX = (width - totalWidth) / 2

        for (i in 0 until starCount) {

            val x = startX + i * (iconSize + iconSpacing)
            val y = (height - iconSize) / 2

            val iconRating = when {
                animatedRating >= i + 1 -> 1f
                animatedRating > i -> animatedRating - i
                else -> 0f
            }

            val scale = scales[i]
            canvas.save()
            canvas.translate(x + iconSize / 2, y + iconSize / 2)
            canvas.scale(scale, scale)
            canvas.translate(-iconSize / 2, -iconSize / 2)

            when (ratingType) {
                TYPE_STAR  -> drawStar(canvas, iconSize / 2, iconSize / 2, iconSize / 2, iconRating)
                TYPE_HEART -> drawHeart(canvas, iconSize / 2, iconSize / 2, iconSize / 2, iconRating)
                TYPE_EMOJI -> drawEmoji(canvas, iconSize / 2, iconSize / 2, i, iconRating)
            }

            canvas.restore()
        }
    }

    /**
     * ⭐ DRAW STAR — NOW FOLLOWS USER COLORS ALWAYS
     */
    private fun drawStar(canvas: Canvas, cx: Float, cy: Float, radius: Float, fill: Float) {

        if (useDrawables && starEmpty != null && starFilled != null) {
            // draw empty
            starEmpty!!.setTint(emptyColor)
            starEmpty!!.setBounds((cx-radius).toInt(), (cy-radius).toInt(), (cx+radius).toInt(), (cy+radius).toInt())
            starEmpty!!.draw(canvas)

            if (fill > 0f) {
                canvas.save()
                canvas.clipRect(cx-radius, cy-radius, cx-radius + radius*2*fill, cy+radius)
                starFilled!!.setTint(filledColor)
                starFilled!!.setBounds((cx-radius).toInt(), (cy-radius).toInt(), (cx+radius).toInt(), (cy+radius).toInt())
                starFilled!!.draw(canvas)
                canvas.restore()
            }
            return
        }

        // fallback drawing
        val path = Path()
        val innerRadius = radius * 0.4f
        val points = 5
        val angle = Math.PI / points

        for (i in 0 until points * 2) {
            val r = if (i % 2 == 0) radius else innerRadius
            val a = i * angle - Math.PI / 2
            val x = cx + (r * cos(a)).toFloat()
            val y = cy + (r * sin(a)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        path.close()
        canvas.drawPath(path, emptyPaint)

        if (fill > 0f) {
            canvas.save()
            canvas.clipRect(cx-radius, cy-radius, cx-radius + radius*2*fill, cy+radius)
            canvas.drawPath(path, filledPaint)
            canvas.restore()
        }
    }

    /**
     * ❤️ DRAW HEART — COLORS APPLIED CORRECTLY
     */
    private fun drawHeart(canvas: Canvas, cx: Float, cy: Float, size: Float, fill: Float) {

        if (useDrawables && heartEmpty != null && heartFilled != null) {

            heartEmpty!!.setTint(emptyColor)
            heartEmpty!!.setBounds((cx-size).toInt(),(cy-size).toInt(),(cx+size).toInt(),(cy+size).toInt())
            heartEmpty!!.draw(canvas)

            if (fill > 0f) {
                canvas.save()
                canvas.clipRect(cx-size, cy-size, cx-size + size*2*fill, cy+size)
                heartFilled!!.setTint(filledColor)
                heartFilled!!.setBounds((cx-size).toInt(),(cy-size).toInt(),(cx+size).toInt(),(cy+size).toInt())
                heartFilled!!.draw(canvas)
                canvas.restore()
            }
            return
        }

        // fallback
        val path = Path()
        val w = size * 2
        val h = size * 2

        path.moveTo(cx, cy + h * 0.3f)
        path.cubicTo(cx - w * 0.5f, cy - h * 0.1f, cx - w * 0.5f, cy - h * 0.5f, cx, cy - h * 0.2f)
        path.cubicTo(cx + w * 0.5f, cy - h * 0.5f, cx + w * 0.5f, cy - h * 0.1f, cx, cy + h * 0.3f)

        canvas.drawPath(path, emptyPaint)

        if (fill > 0f) {
            canvas.save()
            canvas.clipRect(cx-w/2, cy-h/2, cx-w/2 + w*fill, cy+h/2)
            canvas.drawPath(path, filledPaint)
            canvas.restore()
        }
    }

    /**
     * 😍 EMOJI DRAWING — COLORS ALSO APPLIED
     */
    private fun drawEmoji(canvas: Canvas, cx: Float, cy: Float, index: Int, fill: Float) {

        if (emojiEmpty != null && emojiFilled != null) {

            emojiEmpty!!.setTint(emptyColor)
            emojiEmpty!!.setBounds(
                (cx-iconSize/2).toInt(),
                (cy-iconSize/2).toInt(),
                (cx+iconSize/2).toInt(),
                (cy+iconSize/2).toInt()
            )
            emojiEmpty!!.draw(canvas)

            if (fill > 0f) {
                canvas.save()
                canvas.clipRect(
                    cx-iconSize/2,
                    cy-iconSize/2,
                    cx-iconSize/2 + iconSize * fill,
                    cy+iconSize/2
                )
                emojiFilled!!.setTint(filledColor)
                emojiFilled!!.setBounds(
                    (cx-iconSize/2).toInt(),
                    (cy-iconSize/2).toInt(),
                    (cx+iconSize/2).toInt(),
                    (cy+iconSize/2).toInt()
                )
                emojiFilled!!.draw(canvas)
                canvas.restore()
            }
            return
        }

        // fallback: show emoji character
        val emoji = emojis[index % emojis.size]
        emojiPaint.color = if (fill > 0.5f) filledColor else emptyColor
        canvas.drawText(emoji, cx, cy + (emojiPaint.textSize * 0.35f), emojiPaint)
    }


    // Touch handling
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isIndicator) return false
        when(event.action){
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> { handleTouch(event.x); return true }
            MotionEvent.ACTION_UP -> { resetScales(); return true }
        }
        return super.onTouchEvent(event)
    }

    private fun handleTouch(x: Float) {
        val totalWidth = iconSize*starCount + iconSpacing*(starCount-1)
        val startX = (width - totalWidth)/2
        val adjustedX = x - startX
        if(adjustedX>=0 && adjustedX<=totalWidth){
            var newRating = 0f
            var remain = adjustedX
            for(i in 0 until starCount){
                if(remain <= iconSize){
                    newRating = if(enableSwipe) i + (remain/iconSize).coerceIn(0f,1f) else (i+1).toFloat()
                    if(enableAnimation) animateScale(i)
                    break
                }
                remain -= (iconSize+iconSpacing)
            }
            if(newRating != rating){
                setRating(newRating)
                onRatingChangeListener?.invoke(rating)
            }
        }
    }

    private fun animateScale(index:Int){
        scaleAnimators[index]?.cancel()
        val anim = ValueAnimator.ofFloat(1f,1.3f,1f).apply{
            duration=300
            interpolator=OvershootInterpolator()
            addUpdateListener { scales[index] = it.animatedValue as Float; invalidate() }
        }
        scaleAnimators[index] = anim
        anim.start()
    }

    private fun resetScales(){ scaleAnimators.values.forEach { it.cancel() }; scales.fill(1f); invalidate() }

    private fun animateRating(from: Float, to: Float){
        if(!enableAnimation){ animatedRating = to; invalidate(); return }
        ValueAnimator.ofFloat(from,to).apply{
            duration=500
            interpolator=OvershootInterpolator()
            addUpdateListener { animatedRating = it.animatedValue as Float; invalidate() }
            start()
        }
    }

    // Public APIs
    fun setRating(r: Float){ val old = rating; rating = r.coerceIn(0f, starCount.toFloat()); animateRating(old,rating) }
    fun getRating(): Float = rating
    fun setRatingType(type:Int){ ratingType=type; invalidate() }
    fun setStarCount(count:Int){ starCount=count; scales=FloatArray(starCount){1f}; requestLayout() }
    fun setIconSize(size:Float){ iconSize=size; emojiPaint.textSize=size*0.7f; requestLayout() }
    fun setIconSpacing(spacing:Float){ iconSpacing=spacing; requestLayout() }
    fun setIsIndicator(ind:Boolean){ isIndicator=ind }
    fun setEnableAnimation(enable:Boolean){ enableAnimation=enable }
    fun setEnableSwipe(enable:Boolean){ enableSwipe=enable }
    fun setOnRatingChangeListener(listener:(Float)->Unit){ onRatingChangeListener=listener }

    /** NEW — Runtime color update */
    fun setFilledColor(color:Int){
        filledColor = color
        filledPaint.color = color
        invalidate()
    }

    fun setEmptyColor(color:Int){
        emptyColor = color
        emptyPaint.color = color
        invalidate()
    }
}
