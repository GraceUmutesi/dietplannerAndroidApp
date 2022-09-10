package rw.dietplanner.dietplanner.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_my_meal_plans.*
import kotlinx.android.synthetic.main.default_toolbar.*
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import rw.dietplanner.dietplanner.R
import rw.dietplanner.dietplanner.adapters.MealPlansAdapter
import rw.dietplanner.dietplanner.utils.Constants
import rw.dietplanner.dietplanner.utils.Constants.API_ROOT
import java.util.concurrent.TimeUnit

class MyMealPlansActivity : AppCompatActivity() {
    lateinit var context: Context
    private lateinit var preferences: SharedPreferences
    private lateinit var token: String
    private lateinit var userId: String

    lateinit var mealPlansAdapter: MealPlansAdapter
    private lateinit var mealPlans: JSONArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_meal_plans)

        context = this
        preferences = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE)
        val profileJson = JSONObject(preferences.getString(Constants.PROFILE, "")!!)
        token = profileJson.getString(Constants.TOKEN)
        userId = profileJson.getString(Constants.ID)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = "My Meal plans"

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()

        AndroidNetworking.initialize(context, okHttpClient)

        meal_plans_recycler_view.setHasFixedSize(true)
        meal_plans_recycler_view.layoutManager = LinearLayoutManager(context)

        swipe_refresh_layout.setColorSchemeResources(R.color.colorPrimary)
        swipe_refresh_layout.setOnRefreshListener {
            swipe_refresh_layout.isRefreshing = false
            getMealPlans()
        }

        getMealPlans()

        add_meal_plan_btn.setOnClickListener {
            startActivity(Intent(context, NewMealPlanActivity::class.java))
        }

    }

    private fun getMealPlans() {
        swipe_refresh_layout.isRefreshing = true
        AndroidNetworking.get(API_ROOT + "meal-plans?created_by=${userId}")
            .setPriority(Priority.MEDIUM)
            .addHeaders("Authorization", "Token $token")
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    swipe_refresh_layout.isRefreshing = false
                    mealPlans = response!!.getJSONArray("results")
                    mealPlansAdapter = MealPlansAdapter(context, mealPlans)
                    meal_plans_recycler_view.adapter = mealPlansAdapter
                }

                override fun onError(anError: ANError?) {
                    swipe_refresh_layout.isRefreshing = false

                    val errorMessage = getString(R.string.cant_reach_server)
                    val snack = Snackbar.make(swipe_refresh_layout, errorMessage, Snackbar.LENGTH_INDEFINITE)
                    snack.setAction(getString(R.string.retry)) {
                        snack.dismiss()
                        getMealPlans()
                    }
                    snack.show()
                }

            })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}