package rw.dietplanner.dietplanner.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsIntent
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import org.json.JSONObject
import rw.dietplanner.dietplanner.R
import rw.dietplanner.dietplanner.activities.FavoritesActivity
import rw.dietplanner.dietplanner.activities.LoginActivity
import rw.dietplanner.dietplanner.activities.MyMealPlansActivity
import rw.dietplanner.dietplanner.utils.Constants.EMAIL
import rw.dietplanner.dietplanner.utils.Constants.FIRST_NAME
import rw.dietplanner.dietplanner.utils.Constants.LAST_NAME
import rw.dietplanner.dietplanner.utils.Constants.PREFERENCES
import rw.dietplanner.dietplanner.utils.Constants.PROFILE

object MaterialDrawer {
    internal var fullName = "John Doe"
    internal var email = "johndoe@email.com"
    private var preferences: SharedPreferences? = null

    private var result: Drawer? = null

    fun getDrawer(activity: Activity, toolbar: Toolbar) {
        preferences = activity.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

        val profilePreferences = preferences!!.getString(Constants.PROFILE, "")
        val profileJson = JSONObject(profilePreferences!!)
//
        fullName = profileJson.getString(FIRST_NAME) + " " + profileJson.getString(LAST_NAME)
        email = profileJson.getString(EMAIL)

        val headerResult = AccountHeaderBuilder()
            .withActivity(activity)
            .withHeaderBackground(R.color.colorPrimary)
            .addProfiles(
                ProfileDrawerItem().withName(fullName).withEmail(email)
            )
            .withProfileImagesVisible(false)
            .withProfileImagesClickable(false)
            .withSelectionListEnabledForSingleProfile(false)

            .build()

//        val profile = PrimaryDrawerItem().withIdentifier(1)
//            .withName(activity.getString(R.string.profile))
//            .withIcon(R.drawable.ic_person)
//            .withTextColorRes(R.color.grey)
//            .withSelectedTextColorRes(R.color.colorPrimary)

        val favorites = PrimaryDrawerItem().withIdentifier(1)
            .withName(activity.getString(R.string.favorite_recipes))
            .withIcon(R.drawable.ic_favorite)
            .withTextColorRes(R.color.grey)
            .withSelectedTextColorRes(R.color.colorPrimary)

        val myMealPlans = PrimaryDrawerItem().withIdentifier(2)
            .withName(activity.getString(R.string.my_meal_plans))
            .withIcon(R.drawable.ic_assignment)
            .withTextColorRes(R.color.grey)
            .withSelectedTextColorRes(R.color.colorPrimary)

        val articles = PrimaryDrawerItem().withIdentifier(3)
            .withName(activity.getString(R.string.articles))
            .withIcon(R.drawable.ic_menu)
            .withTextColorRes(R.color.grey)
            .withSelectedTextColorRes(R.color.colorPrimary)

        val logout = PrimaryDrawerItem().withIdentifier(4)
            .withName(activity.getString(R.string.logout))
            .withIcon(R.drawable.ic_exit)
            .withTextColorRes(R.color.grey)
            .withSelectedTextColorRes(R.color.colorPrimary)

        result = DrawerBuilder()
            .withActivity(activity)
            .withAccountHeader(headerResult)
            .withToolbar(toolbar)
            .withActionBarDrawerToggle(true)
            .withActionBarDrawerToggleAnimated(true)
            .withCloseOnClick(true)
            .withSelectedItem(-1)
            .withDrawerWidthDp(280)
            .addDrawerItems(
                favorites,
                myMealPlans,
                articles,
                logout
            )
            .withOnDrawerItemClickListener { view, position, drawerItem ->
                when (drawerItem.identifier) {

//                    1.toLong() -> {
//                        result!!.closeDrawer()
//                        result!!.deselect(1)
////                        activity.startActivity(Intent(activity, SettingsActivity::class.java))
//                    }

                    1.toLong() -> {
                        result!!.closeDrawer()
                        result!!.deselect(2)
                        activity.startActivity(Intent(activity, FavoritesActivity::class.java))
                    }

                    2.toLong() -> {
                        result!!.closeDrawer()
                        result!!.deselect(3)
                        activity.startActivity(Intent(activity, MyMealPlansActivity::class.java))
                    }

                    3.toLong() -> {
                        val builder = CustomTabsIntent.Builder()
                        val customTabsIntent = builder.addDefaultShareMenuItem()
                            .setToolbarColor(Color.parseColor("#2ecc70"))
                            .setShowTitle(true)
                            .build()
                        customTabsIntent.launchUrl(activity, Uri.parse("https://diet-planner-web.netlify.app/articles"))

                    }

                    4.toLong() -> {
//                        preferences!!.getString(PROFILE, "")
                        preferences!!.edit().putString(PROFILE, "").apply()
                        activity.startActivity(Intent(activity, LoginActivity::class.java))
                    }

                }

                true
            }
            .build()
    }
}