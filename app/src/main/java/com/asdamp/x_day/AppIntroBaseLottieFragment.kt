package com.asdamp.x_day

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder
import com.github.paolorotolo.appintro.ISlideSelectionListener
import com.github.paolorotolo.appintro.util.LogHelper

internal const val ARG_TITLE = "title"
internal const val ARG_TITLE_TYPEFACE = "title_typeface"
internal const val ARG_TITLE_TYPEFACE_RES = "title_typeface_res"
internal const val ARG_DESC = "desc"
internal const val ARG_DESC_TYPEFACE = "desc_typeface"
internal const val ARG_DESC_TYPEFACE_RES = "desc_typeface_res"
internal const val ARG_DRAWABLE = "drawable"
internal const val ARG_BG_COLOR = "bg_color"
internal const val ARG_TITLE_COLOR = "title_color"
internal const val ARG_DESC_COLOR = "desc_color"
internal const val ARG_BG_ANIM = "bg_drawable"

class AppIntroBaseLottieFragment : Fragment(), ISlideSelectionListener, ISlideBackgroundColorHolder {
    override fun getDefaultBackgroundColor(): Int {
        return backgroundColor
    }

    private val TAG = LogHelper.makeLogTag(AppIntroBaseLottieFragment::class.java)


    private var animInt: Int = 0

    private var titleColor: Int = 0
    private var descColor: Int = 0
    private var backgroundColor: Int = 0
    private var title: String? = null
    private var description: String? = null
    private var mainLayout: ConstraintLayout? = null
    private var slideAnim: LottieAnimationView? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        val args = arguments
        if (args != null && args.size() != 0) {
            title = args.getString(ARG_TITLE)
            description = args.getString(ARG_DESC)
            animInt = args.getInt(ARG_BG_ANIM)
            backgroundColor = args.getInt(ARG_BG_COLOR)
            titleColor = args.getInt(ARG_TITLE_COLOR, 0)
            descColor = args.getInt(ARG_DESC_COLOR, 0)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (savedInstanceState != null) {
            title = savedInstanceState.getString(ARG_TITLE)
            description = savedInstanceState.getString(ARG_DESC)


            backgroundColor = savedInstanceState.getInt(ARG_BG_COLOR)
            animInt = savedInstanceState.getInt(ARG_BG_ANIM)
            titleColor = savedInstanceState.getInt(ARG_TITLE_COLOR)
            descColor = savedInstanceState.getInt(ARG_DESC_COLOR)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.app_intro_lottie_fragment, container, false)
        val titleText = view.findViewById<TextView>(R.id.title)
        val descriptionText = view.findViewById<TextView>(R.id.description)
        slideAnim = view.findViewById<LottieAnimationView>(R.id.animation_view)
        mainLayout = view.findViewById(R.id.main)

        titleText.text = title
        descriptionText.text = description
        if (titleColor != 0) {
            titleText.setTextColor(titleColor)
        }
        if (descColor != 0) {
            descriptionText.setTextColor(descColor)
        }

        slideAnim?.setAnimation(animInt)

        mainLayout?.setBackgroundColor(backgroundColor)


        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(ARG_BG_ANIM, animInt)
        outState.putString(ARG_TITLE, title)
        outState.putString(ARG_DESC, description)
        outState.putInt(ARG_BG_COLOR, backgroundColor)
        outState.putInt(ARG_TITLE_COLOR, titleColor)
        outState.putInt(ARG_DESC_COLOR, descColor)

        super.onSaveInstanceState(outState)
    }

    override fun onSlideDeselected() {
        LogHelper.d(TAG, "Slide $title has been deselected.")
    }

    override fun onSlideSelected() {
        LogHelper.d(TAG, "Slide $title has been selected.")
        slideAnim?.cancelAnimation()
        slideAnim?.playAnimation()
    }

    override fun setBackgroundColor(@ColorInt backgroundColor: Int) {
        mainLayout?.setBackgroundColor(backgroundColor)
    }

    companion object Factory {
        @JvmStatic
        fun newInstance(animInt: Int=0,title: String="",description: String="", backgroundColor: Int=0x2196f3, titleColor: Int=0xffffff,descColor: Int = 0xffffff): AppIntroBaseLottieFragment {
            val outState=Bundle()
            outState.putInt(ARG_BG_ANIM, animInt)
            outState.putString(ARG_TITLE, title)
            outState.putString(ARG_DESC, description)
            outState.putInt(ARG_BG_COLOR, backgroundColor)
            outState.putInt(ARG_TITLE_COLOR, titleColor)
            outState.putInt(ARG_DESC_COLOR, descColor)
            val mFrag = AppIntroBaseLottieFragment()

            mFrag.arguments = outState
            return mFrag
        }
    }
}