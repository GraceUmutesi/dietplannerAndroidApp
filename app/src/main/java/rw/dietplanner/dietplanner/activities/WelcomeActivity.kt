package rw.dietplanner.dietplanner.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_welcome.*
import rw.dietplanner.dietplanner.R
import rw.dietplanner.dietplanner.fragments.WelcomeMessagesFragment
import rw.dietplanner.dietplanner.utils.Constants.WELCOME_PAGE_CAPTION
import rw.dietplanner.dietplanner.utils.Constants.WELCOME_PAGE_IMAGE_ID
import rw.dietplanner.dietplanner.utils.Constants.WELCOME_PAGE_NUMBER

class WelcomeActivity : AppCompatActivity() {

    lateinit var context: Context
    lateinit var tabsPagerAdapter: TabsPagerAdapter
    private val pageCount: Int = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        context = this

        tabsPagerAdapter = TabsPagerAdapter(supportFragmentManager)
        welcome_view_pager.adapter = tabsPagerAdapter
        welcome_view_pager.offscreenPageLimit = tabsPagerAdapter.count
//        welcome_view_pager.

        forward_btn.setOnClickListener {
            startActivity(Intent(context, LoginActivity::class.java))
        }

    }

    fun setDotsLayout(position: Int) {
        dots_layout.removeAllViews()

        for (i in 0 until pageCount) {
            val view = View(context)
            lateinit var layoutParams: LinearLayout.LayoutParams

            if (i == position) {
                layoutParams = LinearLayout.LayoutParams(20, 20)
                view.setBackgroundResource(R.drawable.round_rect_primary_15)
            } else {
                layoutParams = LinearLayout.LayoutParams(19, 19)
                view.setBackgroundResource(R.drawable.round_rect_stroked_15)
            }

            layoutParams.setMargins(10, 0, 0, 0)
            view.layoutParams = layoutParams

            dots_layout.addView(view)
        }

        if (position == pageCount - 1) {
            forward_btn.visibility = View.VISIBLE
        } else {
            forward_btn.visibility = View.INVISIBLE
        }
    }

    inner class TabsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val tabTitles = arrayOf("1", "2", "3", "4", "5", "6")
//        private val pageCount = tabTitles.size

        override fun getItem(position: Int): Fragment {

            val bundle = Bundle()

            return when (position) {
                0 -> {
                    val frag = WelcomeMessagesFragment()
                    bundle.putInt(WELCOME_PAGE_NUMBER, position)
                    bundle.putString(WELCOME_PAGE_CAPTION, "Welcome to your meal planning app; your journey to eating healthy and balanced meals begins here.\nMove on to the next card to learn how to use the app. Enjoy!!")
                    bundle.putInt(WELCOME_PAGE_IMAGE_ID, R.mipmap.ic_app_logo_foreground)
                    frag.arguments = bundle
                    return frag
                }
                1 -> {
                    val frag = WelcomeMessagesFragment()
                    bundle.putInt(WELCOME_PAGE_NUMBER, position)
                    bundle.putString(WELCOME_PAGE_CAPTION, "Sign up and Log in to the app")
                    bundle.putInt(WELCOME_PAGE_IMAGE_ID, R.drawable.img_home_4)
                    frag.arguments = bundle
                    return frag
                }
                2 -> {
                    val frag = WelcomeMessagesFragment()
                    bundle.putInt(WELCOME_PAGE_NUMBER, position)
                    bundle.putString(WELCOME_PAGE_CAPTION, "View Recipes / mark your favorites")
                    bundle.putInt(WELCOME_PAGE_IMAGE_ID, R.drawable.img_home_1)
                    frag.arguments = bundle
                    return frag
                }
                3 -> {
                    val frag = WelcomeMessagesFragment()
                    bundle.putInt(WELCOME_PAGE_NUMBER, position)
                    bundle.putString(WELCOME_PAGE_CAPTION, "Create recipes (meals)")
                    bundle.putInt(WELCOME_PAGE_IMAGE_ID, R.drawable.img_home_1)
                    frag.arguments = bundle
                    return frag
                }
                4 -> {
                    val frag = WelcomeMessagesFragment()
                    bundle.putInt(WELCOME_PAGE_NUMBER, position)
                    bundle.putString(WELCOME_PAGE_CAPTION, "Plan your meal with our 'Meal Planner' feature")
                    bundle.putInt(WELCOME_PAGE_IMAGE_ID, R.drawable.img_home_2)
                    frag.arguments = bundle
                    return frag
                }
                5 -> {
                    val frag = WelcomeMessagesFragment()
                    bundle.putInt(WELCOME_PAGE_NUMBER, position)
                    bundle.putString(WELCOME_PAGE_CAPTION, "Generate a grocery list")
                    bundle.putInt(WELCOME_PAGE_IMAGE_ID, R.drawable.img_home_3)
                    frag.arguments = bundle
                    return frag
                }
                else -> Fragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence {
            return tabTitles[position]
        }

        override fun getCount(): Int {
            return pageCount
        }
    }
}