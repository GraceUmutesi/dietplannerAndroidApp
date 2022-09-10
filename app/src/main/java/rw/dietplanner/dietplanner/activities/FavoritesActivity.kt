package rw.dietplanner.dietplanner.activities

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_favorites.*
import kotlinx.android.synthetic.main.default_toolbar.*
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import rw.dietplanner.dietplanner.R
import rw.dietplanner.dietplanner.adapters.RecipesAdapter
import rw.dietplanner.dietplanner.utils.Constants
import rw.dietplanner.dietplanner.utils.Constants.API_ROOT
import java.util.concurrent.TimeUnit

class FavoritesActivity : AppCompatActivity() {
    lateinit var context: Context
    private lateinit var preferences: SharedPreferences
    private lateinit var token: String
    private lateinit var userId: String

    lateinit var recipesAdapter: RecipesAdapter
    private lateinit var recipes: JSONArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        context = this
        preferences = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE)
        val profileJson = JSONObject(preferences.getString(Constants.PROFILE, "")!!)
        token = profileJson.getString(Constants.TOKEN)
        userId = profileJson.getString(Constants.ID)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = "Favorite recipes"

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()

        AndroidNetworking.initialize(context, okHttpClient)

        recipes_recycler_view.setHasFixedSize(true)
        recipes_recycler_view.layoutManager = LinearLayoutManager(context)

        swipe_refresh_layout.setColorSchemeResources(R.color.colorPrimary)
        swipe_refresh_layout.setOnRefreshListener {
            getRecipes()
        }

        getRecipes()

    }

    private fun getRecipes() {
        swipe_refresh_layout.isRefreshing = true
        AndroidNetworking.get(API_ROOT + "favorite-recipes?created_by=${userId}")
            .setPriority(Priority.MEDIUM)
            .addHeaders("Authorization", "Token $token")
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    swipe_refresh_layout.isRefreshing = false
                    val res = response!!.getJSONArray("results")
                    recipes = JSONArray()
                    try {
                        recipes = res.getJSONObject(0).getJSONArray("recipes")
                    } catch (e: JSONException) {}


                    if (recipes.length() > 0) {
                        no_recipes_view.visibility = View.GONE
                        recipes_recycler_view.visibility = View.VISIBLE
                        recipesAdapter = RecipesAdapter(context, recipes)
                        recipes_recycler_view.adapter = recipesAdapter
                    } else {
                        no_recipes_view.visibility = View.VISIBLE
                        recipes_recycler_view.visibility = View.GONE
                    }

                }

                override fun onError(anError: ANError?) {
                    swipe_refresh_layout.isRefreshing = false

                    val errorMessage = getString(R.string.cant_reach_server)
                    val snack = Snackbar.make(swipe_refresh_layout, errorMessage, Snackbar.LENGTH_INDEFINITE)
                    snack.setAction(getString(R.string.retry)) {
                        snack.dismiss()
                        getRecipes()
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