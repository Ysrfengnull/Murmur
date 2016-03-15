package cn.nekocode.murmur.presentation.main

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import butterknife.bindView
import cn.nekocode.kotgo.component.presentation.BaseFragment

import cn.nekocode.murmur.R
import cn.nekocode.murmur.data.dto.DoubanSong
import cn.nekocode.murmur.data.dto.Murmur
import cn.nekocode.murmur.util.CircleTransform
import cn.nekocode.murmur.util.ImageUtil
import cn.nekocode.murmur.view.ShaderRenderer
import com.pnikosis.materialishprogress.ProgressWheel
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlin.properties.Delegates

class MainFragment: BaseFragment(), MainPresenter.ViewInterface, View.OnTouchListener {
    override val layoutId: Int = R.layout.fragment_main
    val presenter = MainPresenter(this)

    val surfaceView: GLSurfaceView by bindView(R.id.surfaceView)
    var renderer: ShaderRenderer by Delegates.notNull<ShaderRenderer>()

    val coverImageView: ImageSwitcher by bindView(R.id.coverImageView)
    val progressWheel: ProgressWheel by bindView(R.id.progressWheel)
    val titleTextView: TextView by bindView(R.id.titleTextView)
    val performerTextView: TextView by bindView(R.id.performerTextView)
    val murmursTextView: TextView by bindView(R.id.murmursTextView)
    val timeTextView: TextView by bindView(R.id.timeTextView)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupGLSufaceview()

        coverImageView.setFactory {
            val imageView = ImageView(activity)
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            imageView.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
            imageView
        }

        coverImageView.inAnimation = AnimationUtils.loadAnimation(activity, android.R.anim.fade_in)
        coverImageView.outAnimation = AnimationUtils.loadAnimation(activity, android.R.anim.fade_out)
        coverImageView.inAnimation.duration = 500
        coverImageView.outAnimation.duration = 500
        coverImageView.setImageResource(R.drawable.transparent)

        presenter.init()
    }

    override fun murmursChange(murmurs: List<Murmur>) {
        var text = ""
        val last = murmurs.lastOrNull()
        murmurs.forEach {
            text += it.name
            if(it != last) text += ", "
        }
        murmursTextView.text = text
    }

    override fun songChange(song: DoubanSong) {
        titleTextView.text = song.title
        performerTextView.text = song.artist
        timeTextView.text = song.length.toString()

        Picasso.with(activity).load(song.picture).transform(CircleTransform()).into(object: Target {
            override fun onPrepareLoad(drawable: Drawable?) {
                coverImageView.setImageResource(R.drawable.transparent)
            }

            override fun onBitmapFailed(drawable: Drawable?) {
                coverImageView.setImageResource(R.drawable.transparent)
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, p1: Picasso.LoadedFrom?) {
                coverImageView.setImageDrawable(ImageUtil.bitmap2Drawable(bitmap))
            }

        })
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private fun setupGLSufaceview() {
        surfaceView.setEGLContextClientVersion(2)
        surfaceView.setOnTouchListener(this)

        val shader = resources.openRawResource(R.raw.shader).reader().readText()

        renderer = ShaderRenderer(activity, shader)
        renderer.setBackColor(resources.getColor(R.color.color_primary_dark))
        surfaceView.setRenderer(renderer)
    }

    val gestureDetector by lazy {
        GestureDetector(activity, object: GestureDetector.OnGestureListener {
            override fun onSingleTapUp(p0: MotionEvent?): Boolean {
                throw UnsupportedOperationException()
            }

            override fun onDown(p0: MotionEvent?): Boolean {
                throw UnsupportedOperationException()
            }

            override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
                throw UnsupportedOperationException()
            }

            override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
                throw UnsupportedOperationException()
            }

            override fun onShowPress(p0: MotionEvent?) {
                throw UnsupportedOperationException()
            }

            override fun onLongPress(p0: MotionEvent?) {
                throw UnsupportedOperationException()
            }

        })
    }
}
