package rw.dietplanner.dietplanner.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_meal_plan_details.*
import kotlinx.android.synthetic.main.activity_meal_plan_details.root_view
import kotlinx.android.synthetic.main.activity_new_meal_plan.*
import kotlinx.android.synthetic.main.default_toolbar.*
import kotlinx.android.synthetic.main.progress_layout.*
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import rw.dietplanner.dietplanner.R
import rw.dietplanner.dietplanner.adapters.DailyMealsAdapter
import rw.dietplanner.dietplanner.utils.Constants
import rw.dietplanner.dietplanner.utils.Constants.API_ROOT
import rw.dietplanner.dietplanner.utils.Constants.MEAL_PLAN
import java.util.concurrent.TimeUnit

class MealPlanDetailsActivity : AppCompatActivity() {
    lateinit var context: Context
    private lateinit var preferences: SharedPreferences
    private lateinit var token: String
    private lateinit var userId: String

    private lateinit var mealPlan: JSONObject
    lateinit var dailyMealsAdapter: DailyMealsAdapter
    private lateinit var dailyMeals: JSONArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_plan_details)

        context = this
        preferences = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE)
        val profileJson = JSONObject(preferences.getString(Constants.PROFILE, "")!!)
        token = profileJson.getString(Constants.TOKEN)
        userId = profileJson.getString(Constants.ID)

        mealPlan = JSONObject(intent.getStringExtra(MEAL_PLAN)!!)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = ""

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()

        AndroidNetworking.initialize(context, okHttpClient)

        daily_meals_recycler_view.setHasFixedSize(true)
        daily_meals_recycler_view.layoutManager = LinearLayoutManager(context)

        view_grocery_btn.setOnClickListener {
            val intent = Intent(context, GroceryListActivity::class.java)
            intent.putExtra(MEAL_PLAN, mealPlan.toString())
            startActivity(intent)
        }

        delete_meal_plan_btn.setOnClickListener {
            deleteMealPlan()
        }

        loadMealPlanDetails()
    }

    private fun loadMealPlanDetails() {
        meal_plan_name.text = mealPlan.getString("name")
        meal_plan_created_time.text = parseTime(mealPlan.getString("created_at"))

        dailyMeals = mealPlan.getJSONArray("meals")
//        Log.d("MEALS", dailyMeals.toString())
        dailyMealsAdapter = DailyMealsAdapter(context, organizeDailyMeals(dailyMeals))
//        Log.d("ORDERED_MEALS", organizeDailyMeals(dailyMeals).toString())
        daily_meals_recycler_view.adapter = dailyMealsAdapter
    }

    private fun organizeDailyMeals(dailyMeals: JSONArray) : JSONObject {
        val mJSONObject = JSONObject()
        val moObj = JSONArray()
        val tuObj = JSONArray()
        val weObj = JSONArray()
        val thObj = JSONArray()
        val frObj = JSONArray()
        val saObj = JSONArray()
        val suObj = JSONArray()
        val days = JSONArray()
        days.put("Monday")
        days.put("Tuesday")
        days.put("Wednesday")
        days.put("Thursday")
        days.put("Friday")
        days.put("Saturday")
        days.put("Sunday")

        for (i in 0 until dailyMeals.length()) {
            val obj = dailyMeals.getJSONObject(i)

            if (obj.getString("day") == "Monday") {
                moObj.put(obj)
            } else if (obj.getString("day") == "Tuesday") {
                tuObj.put(obj)
            } else if (obj.getString("day") == "Wednesday") {
                weObj.put(obj)
            } else if (obj.getString("day") == "Thursday") {
                thObj.put(obj)
            } else if (obj.getString("day") == "Friday") {
                frObj.put(obj)
            } else if (obj.getString("day") == "Saturday") {
                saObj.put(obj)
            } else if (obj.getString("day") == "Sunday") {
                suObj.put(obj)
            }
        }

        mJSONObject.put("Monday", moObj)
        mJSONObject.put("Tuesday", tuObj)
        mJSONObject.put("Wednesday", weObj)
        mJSONObject.put("Thursday", thObj)
        mJSONObject.put("Friday", frObj)
        mJSONObject.put("Saturday", saObj)
        mJSONObject.put("Sunday", suObj)
        mJSONObject.put("days", days)
        return mJSONObject
    }

    private fun parseTime(time: String) : String {
        val requestTime = time.split("+")
        val readableTime = requestTime[0]
        val td = readableTime.split("T")

        val date = td[0]

        val timeTime: String = try {
            val t = td[1].split(".")
            t[0]
        } catch (e: Exception) {
            td[1]
        }

        return "$date $timeTime"
    }

    private fun deleteMealPlan() {
        delete_meal_plan_btn.visibility = View.GONE
        progress_bar.visibility = View.VISIBLE

        AndroidNetworking.delete(API_ROOT + "meal-plans/${mealPlan.getString("id")}")
            .setPriority(Priority.MEDIUM)
            .addHeaders("Authorization", "Token $token")
            .build()
            .getAsJSONObject(object: JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    delete_meal_plan_btn.visibility = View.VISIBLE
                    progress_bar.visibility = View.GONE

                    Toast.makeText(context, "Meal Plan deleted Successfully", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(context, MyMealPlansActivity::class.java))
                    finish()
                }

                override fun onError(anError: ANError?) {
                    delete_meal_plan_btn.visibility = View.VISIBLE
                    progress_bar.visibility = View.GONE

                    var errorMessage = getString(R.string.cant_reach_server)

                    when(anError!!.errorCode) {
                        400 -> errorMessage = getString(R.string.bad_request)
                        403 -> errorMessage = "You are not allowed to perform this activity"
                    }

                    val snack = Snackbar.make(root_view, errorMessage, Snackbar.LENGTH_LONG)
                    snack.setAction(getString(R.string.dismiss)) {
                        snack.dismiss()
                    }
                    snack.show()

                }

            })
    }

    fun deleteMPItem(meal: JSONObject) {
        val mpms = JSONArray(dailyMeals.toString())

        for (i in 0 until mpms.length()) {
            val m = mpms.getJSONObject(i)

            if (m.getString("recipe_id") == meal.getString("recipe_id") &&
                m.getString("day") == meal.getString("day") &&
                m.getString("time") == meal.getString("time")) {

                mpms.remove(i)
                break
            }
        }

        val params = JSONObject()
        params.put("meals", mpms)

        progress_bar.visibility = View.VISIBLE
        performDelMPItem(params)

//        Log.d("MEALS", dailyMeals.toString())
//        Log.d("MEALS COUNT", dailyMeals.length().toString())
//
//        Log.d("UPDATED MEALS", mpms.toString())
//        Log.d("UPDATED MEALS COUNT", mpms.length().toString())
    }

    private fun performDelMPItem(params: JSONObject) {
        AndroidNetworking.patch(API_ROOT + "meal-plans/${mealPlan.getString("id")}")
            .setPriority(Priority.MEDIUM)
            .addHeaders("Content-Type", "application/json")
            .addHeaders("Authorization", "Token $token")
            .addJSONObjectBody(params)
            .build()
            .getAsJSONObject(object: JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    try {
                        if (!mealPlan.getJSONObject("grocery_list").getString("id").isNullOrEmpty()) {
                            deleteGroceryList()
                        } else {
                            progress_bar.visibility = View.GONE
                            Toast.makeText(context, "Item has been removed from meal plan", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(context, MyMealPlansActivity::class.java))
                            finish()
                        }
                    } catch (e: JSONException) {
                        progress_bar.visibility = View.GONE
                        Toast.makeText(context, "Item has been removed from meal plan", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(context, MyMealPlansActivity::class.java))
                        finish()
                    }
                }

                override fun onError(anError: ANError?) {

                    var errorMessage = getString(R.string.cant_reach_server)

                    when(anError!!.errorCode) {
                        400 -> errorMessage = getString(R.string.bad_request)
                    }

                    Log.d("ERRORRRRR", anError.toString())

                    val snack = Snackbar.make(root_view, errorMessage, Snackbar.LENGTH_LONG)
                    snack.setAction(getString(R.string.dismiss)) {
                        snack.dismiss()
                    }
                    snack.show()

                }

            })
    }

    private fun deleteGroceryList() {
        AndroidNetworking.delete(API_ROOT + "grocery-lists/${mealPlan.getJSONObject("grocery_list").getString("id")}")
            .setPriority(Priority.MEDIUM)
            .addHeaders("Content-Type", "application/json")
            .addHeaders("Authorization", "Token $token")
            .build()
            .getAsJSONObject(object: JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    progress_bar.visibility = View.GONE
                    Toast.makeText(context, "Item has been removed from meal plan", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(context, MyMealPlansActivity::class.java))
                    finish()
                }

                override fun onError(anError: ANError?) {

                    var errorMessage = getString(R.string.cant_reach_server)

                    when(anError!!.errorCode) {
                        400 -> errorMessage = getString(R.string.bad_request)
                    }

                    val snack = Snackbar.make(root_view, errorMessage, Snackbar.LENGTH_LONG)
                    snack.setAction(getString(R.string.dismiss)) {
                        snack.dismiss()
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