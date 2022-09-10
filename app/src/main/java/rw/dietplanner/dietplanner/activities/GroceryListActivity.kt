package rw.dietplanner.dietplanner.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_grocery_list.*
import kotlinx.android.synthetic.main.activity_grocery_list.root_view
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.progress_layout.*
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import rw.dietplanner.dietplanner.R
import rw.dietplanner.dietplanner.adapters.GenericAdapter
import rw.dietplanner.dietplanner.utils.Constants
import rw.dietplanner.dietplanner.utils.Constants.GROCERIES
import java.util.concurrent.TimeUnit

class GroceryListActivity : AppCompatActivity() {

    lateinit var context: Context
    lateinit var genericAdapter: GenericAdapter

    private lateinit var preferences: SharedPreferences
    private lateinit var token: String
    private lateinit var userId: String

    private lateinit var mealPlan: JSONObject

    private var groceryListTotal: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grocery_list)

        context = this
        preferences = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE)
        val profileJson = JSONObject(preferences.getString(Constants.PROFILE, "")!!)
        token = profileJson.getString(Constants.TOKEN)
        userId = profileJson.getString(Constants.ID)

        mealPlan = JSONObject(intent.getStringExtra(Constants.MEAL_PLAN)!!)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = "Grocery List"

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()

        AndroidNetworking.initialize(context, okHttpClient)

        grocery_list_recycler_view.setHasFixedSize(true)
        grocery_list_recycler_view.layoutManager = LinearLayoutManager(context)

        meal_plan_name.text = mealPlan.getString("name")

        try {
            val gl = mealPlan.getJSONObject("grocery_list")
            if (!gl.getString("id").isNullOrEmpty()) {
                loadGroceryList(gl)
            } else {
                createGroceryList()
            }
        } catch (e: JSONException) {
            createGroceryList()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun loadGroceryList(groceryList: JSONObject) {
        val gl = groceryList.getJSONArray("foods")
        grocery_list_recycler_view.adapter = GenericAdapter(context, gl, GROCERIES)

        for (i in 0 until gl.length()) {
            val item = gl.getJSONObject(i)
            val price = item.getString("estimated_price").toDouble()
            groceryListTotal += price
        }

        gf_price.text = "$groceryListTotal RWF"
    }

    private fun createGroceryList() {
        grocery_list_recycler_view.visibility = View.GONE
        progress_bar.visibility = View.VISIBLE

        val params = JSONObject()
        params.put("meal_plan", mealPlan.getString("id"))

        AndroidNetworking.post(Constants.API_ROOT + "grocery-lists")
            .setPriority(Priority.MEDIUM)
            .addHeaders("Content-Type", "application/json")
            .addHeaders("Authorization", "Token $token")
            .addJSONObjectBody(params)
            .build()
            .getAsJSONObject(object: JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    grocery_list_recycler_view.visibility = View.VISIBLE
                    progress_bar.visibility = View.GONE

                    Toast.makeText(context, "Grocery list created Successfully", Toast.LENGTH_SHORT).show()

                    loadGroceryList(response!!)
                }

                override fun onError(anError: ANError?) {
                    grocery_list_recycler_view.visibility = View.VISIBLE
                    progress_bar.visibility = View.GONE

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