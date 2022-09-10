package rw.dietplanner.dietplanner.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_recipes.view.*
import kotlinx.android.synthetic.main.fragment_recipes.view.search_view
import kotlinx.android.synthetic.main.progress_layout.view.*
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import rw.dietplanner.dietplanner.R
import rw.dietplanner.dietplanner.activities.NewRecipeActivity
import rw.dietplanner.dietplanner.adapters.GenericAdapter
import rw.dietplanner.dietplanner.adapters.RecipesAdapter
import rw.dietplanner.dietplanner.utils.Constants
import rw.dietplanner.dietplanner.utils.Constants.API_ROOT
import rw.dietplanner.dietplanner.utils.Constants.ID
import java.util.concurrent.TimeUnit


class RecipesFragment : Fragment() {

    private lateinit var preferences: SharedPreferences
    private lateinit var token: String
    private lateinit var userId: String
    lateinit var recipesAdapter: RecipesAdapter
    private lateinit var recipes: JSONArray
    private lateinit var recipesRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var noRecipeView: TextView
    private lateinit var addMealBtn: FloatingActionButton
    private lateinit var searchView: SearchView
    private lateinit var filterCatSpinner: Spinner
    private var myRecipes: Boolean = false

    private var recipeCategoriesList = ArrayList<JSONObject>()
    private var recipeCategoriesNames = ArrayList<String>()
    private var recipeCategoryId = ""
    private var searchQueryString = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recipes, container, false)

        preferences = requireActivity().getSharedPreferences(Constants.PREFERENCES, AppCompatActivity.MODE_PRIVATE)
        val profileJson = JSONObject(preferences.getString(Constants.PROFILE, "")!!)
        token = profileJson.getString(Constants.TOKEN)
        userId = profileJson.getString(ID)

        myRecipes = requireArguments().getBoolean(Constants.MY_RECIPES, false)

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()

        AndroidNetworking.initialize(requireContext(), okHttpClient)

        recipesRecyclerView = view.recipes_recycler_view
        swipeRefreshLayout = view.swipe_refresh_layout
        noRecipeView = view.no_recipes_view
        addMealBtn = view.add_meal_btn
        searchView = view.search_view
        filterCatSpinner = view.filter_cat_spinner

        if (myRecipes) {
            addMealBtn.visibility = View.VISIBLE
        } else {
            addMealBtn.visibility = View.GONE
        }

        addMealBtn.setOnClickListener {
            requireActivity().startActivity(Intent(requireContext(), NewRecipeActivity::class.java))
        }

        recipesRecyclerView.setHasFixedSize(true)
        recipesRecyclerView.layoutManager = LinearLayoutManager(activity)

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        swipeRefreshLayout.setOnRefreshListener {
            recipeCategoryId = ""
            searchQueryString = ""

            getRecipes(myRecipes = myRecipes, queryString = searchQueryString, recipesCatId = recipeCategoryId)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchQueryString = query!!
                getRecipes(myRecipes = myRecipes, queryString = searchQueryString, recipesCatId = recipeCategoryId)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        getRecipeCategories()
        getRecipes(myRecipes = myRecipes, queryString = searchQueryString, recipesCatId = recipeCategoryId)

        return view
    }

    private fun getRecipes(myRecipes: Boolean = false, queryString: String = "", recipesCatId: String = "") {
        var url = ""
        url = if (myRecipes) {
            API_ROOT + "recipes?created_by=${userId}&&recipe_type=${recipesCatId}&&search=${queryString}"
        } else {
            API_ROOT + "recipes?created_by__is_staff=True&&recipe_type=${recipesCatId}&&search=${queryString}"
        }
        swipeRefreshLayout.isRefreshing = true
        AndroidNetworking.get(url)
            .setPriority(Priority.MEDIUM)
            .addHeaders("Authorization", "Token $token")
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    swipeRefreshLayout.isRefreshing = false
                    recipes = response!!.getJSONArray("results")
                    recipesAdapter = RecipesAdapter(requireActivity(), recipes)
                    recipesRecyclerView.adapter = recipesAdapter
                }

                override fun onError(anError: ANError?) {
                    swipeRefreshLayout.isRefreshing = false

                    val errorMessage = getString(R.string.cant_reach_server)
                    val snack = Snackbar.make(swipeRefreshLayout, errorMessage, Snackbar.LENGTH_INDEFINITE)
                    snack.setAction(getString(R.string.retry)) {
                        snack.dismiss()
                        getRecipes(myRecipes, queryString, recipesCatId)
                    }
                    snack.show()
                }

            })
    }

    private fun getRecipeCategories() {
        recipeCategoriesList.clear()
        recipeCategoriesNames.clear()
        recipeCategoriesNames.add("All Recipes")

        AndroidNetworking.get(API_ROOT + "recipe-types?ordering=name")
            .setPriority(Priority.MEDIUM)
            .addHeaders("Authorization", "Token $token")
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray?) {
                    if (response!!.length() > 0) {
                        parseCategories(response)
                    } else {
                        val snack = Snackbar.make(swipeRefreshLayout, getString(R.string.no_recipe_categories_found), Snackbar.LENGTH_INDEFINITE)
                        snack.setAction(getString(R.string.retry)) {
                            snack.dismiss()
                            getRecipeCategories()
                        }
                        snack.show()
                    }
                }

                override fun onError(anError: ANError?) {
                    val errorMessage = getString(R.string.cant_reach_server)
                    val snack = Snackbar.make(swipeRefreshLayout, errorMessage, Snackbar.LENGTH_INDEFINITE)
                    snack.setAction(getString(R.string.retry)) {
                        snack.dismiss()
                        getRecipeCategories()
                    }
                    snack.show()
                }

            })
    }

    private fun parseCategories(categories: JSONArray) {
        for (i in 0 until categories.length()) {
            recipeCategoriesList.add(categories.getJSONObject(i))
            recipeCategoriesNames.add(recipeCategoriesList[i].getString("name"))
        }

        val recipeCatsAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, recipeCategoriesNames)
        recipeCatsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterCatSpinner.adapter = recipeCatsAdapter

        filterCatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (position > 0) {
                    recipeCategoryId = recipeCategoriesList[position-1].getString("id")
                    getRecipes(myRecipes = myRecipes, queryString = "", recipesCatId = recipeCategoryId)
                } else {
                    recipeCategoryId = ""
                    getRecipes(myRecipes = myRecipes, queryString = "", recipesCatId = recipeCategoryId)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

}