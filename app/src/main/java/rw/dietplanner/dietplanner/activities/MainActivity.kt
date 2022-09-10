package rw.dietplanner.dietplanner.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import rw.dietplanner.dietplanner.R
import rw.dietplanner.dietplanner.fragments.MealPlannerFragment
import rw.dietplanner.dietplanner.fragments.RecipesFragment
import rw.dietplanner.dietplanner.utils.Constants.MY_RECIPES
import rw.dietplanner.dietplanner.utils.MaterialDrawer

class MainActivity : AppCompatActivity() {
    lateinit var context: Context
    lateinit var tabsPagerAdapter: TabsPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Diet Planner"

        MaterialDrawer.getDrawer(this, toolbar)

        tabsPagerAdapter = TabsPagerAdapter(supportFragmentManager)
        view_pager.adapter = tabsPagerAdapter
        tab_layout.setupWithViewPager(view_pager)
        view_pager.offscreenPageLimit = tabsPagerAdapter.count
        tab_layout.tabTextColors = ContextCompat.getColorStateList(this, R.color.white)
        tab_layout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.white))
    }

    inner class TabsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val tabTitles = arrayOf("SYSTEM RECIPES", "MY RECIPES")
        private val pageCount = tabTitles.size

        override fun getItem(position: Int): Fragment {

            val bundle = Bundle()

            return when (position) {
                0 -> {
                    bundle.putBoolean(MY_RECIPES, false)
                    val frag = RecipesFragment()
                    frag.arguments = bundle
                    return frag
                }
                1 -> {
                    bundle.putBoolean(MY_RECIPES, true)
                    val frag = RecipesFragment()
                    frag.arguments = bundle
                    return frag
                }
                else -> Fragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return tabTitles[position]
        }

        override fun getCount(): Int {
            return this.pageCount
        }
    }
}