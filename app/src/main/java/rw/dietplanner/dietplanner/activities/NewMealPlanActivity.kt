package rw.dietplanner.dietplanner.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_new_meal_plan.*
import kotlinx.android.synthetic.main.activity_new_meal_plan.root_view
import kotlinx.android.synthetic.main.default_toolbar.*
import kotlinx.android.synthetic.main.dialog_choose_meal.view.*
import kotlinx.android.synthetic.main.dialog_choose_meal.view.close_dialog_btn
import kotlinx.android.synthetic.main.progress_layout.*
import kotlinx.android.synthetic.main.progress_layout.view.*
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import rw.dietplanner.dietplanner.R
import rw.dietplanner.dietplanner.adapters.GenericAdapter
import rw.dietplanner.dietplanner.adapters.RecipesAdapter
import rw.dietplanner.dietplanner.utils.Constants
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class NewMealPlanActivity : AppCompatActivity() {

    lateinit var context: Context
    private lateinit var preferences: SharedPreferences
    private lateinit var token: String
    private lateinit var userId: String

    private lateinit var selectMealDialog: AlertDialog
    private lateinit var selectMealDialogView: View
    private lateinit var selectMealDialogBuilder: AlertDialog.Builder

    private var editTextId by Delegates.notNull<Int>()
    private lateinit var recipesAdapter: RecipesAdapter

    private var myRecipes: Boolean = false
    private var searchQueryString = ""

    private var selectedMealsJSONArray = JSONArray()
    private var selectedDayTimeJSONObject = JSONObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_meal_plan)

        context = this
        preferences = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE)
        val profileJson = JSONObject(preferences.getString(Constants.PROFILE, "")!!)
        token = profileJson.getString(Constants.TOKEN)
        userId = profileJson.getString(Constants.ID)

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

        selectMealDialogBuilder = AlertDialog.Builder(context)
        selectMealDialogView = View.inflate(context, R.layout.dialog_choose_meal, null)
        selectMealDialogBuilder.setView(selectMealDialogView)

        selectMealDialogView.meals_recycler_view.setHasFixedSize(true)
        selectMealDialogView.meals_recycler_view.layoutManager = LinearLayoutManager(context)

        selectedDayTimeJSONObject.put("day", "")
        selectedDayTimeJSONObject.put("time", "")

        val mealType = selectMealDialogView.meal_type_spinner.selectedItem.toString()

        if (mealType == "My Recipes") {
            myRecipes = true
            getRecipes(myRecipes = myRecipes)
        } else {
            myRecipes = false
            getRecipes(myRecipes = myRecipes)
        }

        selectMealDialogView.meal_type_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent!!.getItemAtPosition(position).toString() == "My Recipes") {
                    myRecipes = true
                    getRecipes(myRecipes = myRecipes)
                } else {
                    myRecipes = false
                    getRecipes(myRecipes = myRecipes)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        selectMealDialogView.search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchQueryString = query!!
                getRecipes(myRecipes = myRecipes, queryString = searchQueryString)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        selectMealDialogView.close_dialog_btn.setOnClickListener {
            closeSelectMealDialog()
        }

        mo_breakfast_view.setOnClickListener {
            editTextId = it.id
            selectedDayTimeJSONObject.put("day", "Monday")
            selectedDayTimeJSONObject.put("time", "Breakfast")
            openMealsDialog()
        }

        mo_lunch_view.setOnClickListener {
            editTextId = it.id
            selectedDayTimeJSONObject.put("day", "Monday")
            selectedDayTimeJSONObject.put("time", "Lunch")
            openMealsDialog()
        }

        mo_dinner_view.setOnClickListener {
            editTextId = it.id
            selectedDayTimeJSONObject.put("day", "Monday")
            selectedDayTimeJSONObject.put("time", "Dinner")
            openMealsDialog()
        }

        tu_breakfast_view.setOnClickListener {
            editTextId = it.id
            selectedDayTimeJSONObject.put("day", "Tuesday")
            selectedDayTimeJSONObject.put("time", "Breakfast")
            openMealsDialog()
        }

        tu_lunch_view.setOnClickListener {
            editTextId = it.id
            selectedDayTimeJSONObject.put("day", "Tuesday")
            selectedDayTimeJSONObject.put("time", "Lunch")
            openMealsDialog()
        }

        tu_dinner_view.setOnClickListener {
            editTextId = it.id
            selectedDayTimeJSONObject.put("day", "Tuesday")
            selectedDayTimeJSONObject.put("time", "Dinner")
            openMealsDialog()
        }

        we_breakfast_view.setOnClickListener {
            editTextId = it.id
            selectedDayTimeJSONObject.put("day", "Wednesday")
            selectedDayTimeJSONObject.put("time", "Breakfast")
            openMealsDialog()
        }

        we_lunch_view.setOnClickListener {
            editTextId = it.id
            selectedDayTimeJSONObject.put("day", "Wednesday")
            selectedDayTimeJSONObject.put("time", "Lunch")
            openMealsDialog()
        }

        we_dinner_view.setOnClickListener {
            editTextId = it.id
            selectedDayTimeJSONObject.put("day", "Wednesday")
            selectedDayTimeJSONObject.put("time", "Dinner")
            openMealsDialog()
        }

        th_breakfast_view.setOnClickListener {
            editTextId = it.id
            selectedDayTimeJSONObject.put("day", "Thursday")
            selectedDayTimeJSONObject.put("time", "Breakfast")
            openMealsDialog()
        }

        th_lunch_view.setOnClickListener {
            editTextId = it.id
            selectedDayTimeJSONObject.put("day", "Thursday")
            selectedDayTimeJSONObject.put("time", "Lunch")
            openMealsDialog()
        }

        th_dinner_view.setOnClickListener {
            editTextId = it.id
            selectedDayTimeJSONObject.put("day", "Thursday")
            selectedDayTimeJSONObject.put("time", "Dinner")
            openMealsDialog()
        }

        fr_breakfast_view.setOnClickListener {
            editTextId = it.id
            selectedDayTimeJSONObject.put("day", "Friday")
            selectedDayTimeJSONObject.put("time", "Breakfast")
            openMealsDialog()
        }

        fr_lunch_view.setOnClickListener {
            editTextId = it.id
            selectedDayTimeJSONObject.put("day", "Friday")
            selectedDayTimeJSONObject.put("time", "Lunch")
            openMealsDialog()
        }

        fr_dinner_view.setOnClickListener {
            editTextId = it.id
            selectedDayTimeJSONObject.put("day", "Friday")
            selectedDayTimeJSONObject.put("time", "Dinner")
            openMealsDialog()
        }

        sa_breakfast_view.setOnClickListener {
            editTextId = it.id
            selectedDayTimeJSONObject.put("day", "Saturday")
            selectedDayTimeJSONObject.put("time", "Breakfast")
            openMealsDialog()
        }

        sa_lunch_view.setOnClickListener {
            editTextId = it.id
            selectedDayTimeJSONObject.put("day", "Saturday")
            selectedDayTimeJSONObject.put("time", "Lunch")
            openMealsDialog()
        }

        sa_dinner_view.setOnClickListener {
            editTextId = it.id
            selectedDayTimeJSONObject.put("day", "Saturday")
            selectedDayTimeJSONObject.put("time", "Dinner")
            openMealsDialog()
        }

        su_breakfast_view.setOnClickListener {
            editTextId = it.id
            selectedDayTimeJSONObject.put("day", "Sunday")
            selectedDayTimeJSONObject.put("time", "Breakfast")
            openMealsDialog()
        }

        su_lunch_view.setOnClickListener {
            editTextId = it.id
            selectedDayTimeJSONObject.put("day", "Sunday")
            selectedDayTimeJSONObject.put("time", "Lunch")
            openMealsDialog()
        }

        su_dinner_view.setOnClickListener {
            editTextId = it.id
            selectedDayTimeJSONObject.put("day", "Sunday")
            selectedDayTimeJSONObject.put("time", "Dinner")
            openMealsDialog()
        }

        create_meal_plan_btn.setOnClickListener {
            checkInputs()
        }
    }

    private fun getRecipes(myRecipes: Boolean = false, queryString: String = "") {
        var url = ""
        url = if (myRecipes) {
            Constants.API_ROOT + "recipes?created_by=${userId}&&search=${queryString}"
        } else {
            Constants.API_ROOT + "recipes?created_by__is_staff=True&&search=${queryString}"
        }
        selectMealDialogView.progress_bar.visibility = View.VISIBLE
        selectMealDialogView.meals_recycler_view.visibility = View.GONE

        AndroidNetworking.get(url)
            .setPriority(Priority.MEDIUM)
            .addHeaders("Authorization", "Token $token")
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    selectMealDialogView.progress_bar.visibility = View.GONE
                    selectMealDialogView.meals_recycler_view.visibility = View.VISIBLE

                    val recipes = response!!.getJSONArray("results")

                    if (recipes.length() > 0) {
                        recipesAdapter = RecipesAdapter(context, recipes, forChoosingMeal = true)
                        selectMealDialogView.meals_recycler_view.adapter = recipesAdapter
                    } else {
                        val snack = Snackbar.make(root_view, getString(R.string.no_recipes_found), Snackbar.LENGTH_INDEFINITE)
                        snack.setAction(getString(R.string.retry)) {
                            snack.dismiss()
                            getRecipes(myRecipes, queryString)
                        }
                        snack.show()
                    }
                }

                override fun onError(anError: ANError?) {
                    selectMealDialogView.progress_bar.visibility = View.GONE
                    selectMealDialogView.meals_recycler_view.visibility = View.VISIBLE

                    val errorMessage = getString(R.string.cant_reach_server)
                    val snack = Snackbar.make(root_view, errorMessage, Snackbar.LENGTH_INDEFINITE)
                    snack.setAction(getString(R.string.retry)) {
                        snack.dismiss()
                        getRecipes(myRecipes, queryString)
                    }
                    snack.show()
                }

            })
    }

    private fun openMealsDialog() {
        selectMealDialog = selectMealDialogBuilder.create()
        selectMealDialog.show()
        selectMealDialog.setCancelable(false)
        selectMealDialog.setCanceledOnTouchOutside(false)
    }

    @SuppressLint("SetTextI18n")
    fun selectMeal(selectedMeal: JSONObject) {
        val editText: EditText = findViewById(editTextId)
        editText.setText(selectedMeal.getString("name"))

        val cmObj = JSONObject()
        cmObj.put("recipe_id", selectedMeal.getString("id"))
        cmObj.put("recipe_name", selectedMeal.getString("name"))
        cmObj.put("recipe_image", selectedMeal.getString("image"))
        cmObj.put("day", selectedDayTimeJSONObject.getString("day"))
        cmObj.put("time", selectedDayTimeJSONObject.getString("time"))

        var objIndex = -1

        if (selectedMealsJSONArray.length() < 1) {
            selectedMealsJSONArray.put(cmObj)
        } else {
            for (i in 0 until selectedMealsJSONArray.length()) {
                val obj = selectedMealsJSONArray.getJSONObject(i)
                if (obj.getString("day") == cmObj.getString("day") &&
                    obj.getString("time") == cmObj.getString("time")) {
                    objIndex = i
                    break
                }
            }

            selectedMealsJSONArray.remove(objIndex)
            selectedMealsJSONArray.put(cmObj)
        }

        closeSelectMealDialog()
    }

    private fun closeSelectMealDialog() {
        searchQueryString = ""
        selectMealDialog.dismiss()
        (selectMealDialogView.parent as ViewGroup).removeView(selectMealDialogView)
    }

    private fun disableViews() {
        meal_plan_name_view.isEnabled = false
        create_meal_plan_btn.visibility = View.GONE
        progress_bar.visibility = View.VISIBLE
    }

    private fun enableViews() {
        meal_plan_name_view.isEnabled = true
        create_meal_plan_btn.visibility = View.VISIBLE
        progress_bar.visibility = View.GONE
    }

    private fun checkInputs(){
        val mealPlanName = meal_plan_name_view.text.toString()

        when {
            TextUtils.isEmpty(mealPlanName.trim()) -> meal_plan_name_view.error = getString(R.string.field_required)
            selectedMealsJSONArray.length() < 1 -> Toast.makeText(context, "No Selected Meals", Toast.LENGTH_SHORT).show()
            else -> {
                val params = JSONObject()

                params.put("name", mealPlanName)
                params.put("meals", selectedMealsJSONArray)
                params.put("created_by", userId)

                disableViews()
                createMealPlan(params)

            }
        }

    }

    private fun createMealPlan(params: JSONObject) {
        AndroidNetworking.post(Constants.API_ROOT + "meal-plans")
            .setPriority(Priority.MEDIUM)
            .addHeaders("Content-Type", "application/json")
            .addHeaders("Authorization", "Token $token")
            .addJSONObjectBody(params)
            .build()
            .getAsJSONObject(object: JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    enableViews()

                    Toast.makeText(context, "Meal Plan created Successfully", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(context, MyMealPlansActivity::class.java))
                    finish()
                }

                override fun onError(anError: ANError?) {
                    enableViews()

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