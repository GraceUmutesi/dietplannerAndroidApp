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
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_new_recipe.*
import kotlinx.android.synthetic.main.default_toolbar.*
import kotlinx.android.synthetic.main.dialog_choose_food.view.*
import kotlinx.android.synthetic.main.progress_layout.*
import kotlinx.android.synthetic.main.progress_layout.view.*
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import rw.dietplanner.dietplanner.R
import rw.dietplanner.dietplanner.adapters.GenericAdapter
import rw.dietplanner.dietplanner.utils.ChosenFood
import rw.dietplanner.dietplanner.utils.Constants
import rw.dietplanner.dietplanner.utils.Constants.API_ROOT
import rw.dietplanner.dietplanner.utils.Constants.INGREDIENTS
import rw.dietplanner.dietplanner.utils.Constants.PREFERENCES
import rw.dietplanner.dietplanner.utils.Constants.PROFILE
import rw.dietplanner.dietplanner.utils.Constants.TOKEN
import java.util.concurrent.TimeUnit

class NewRecipeActivity : AppCompatActivity() {
    lateinit var context: Context
    private lateinit var preferences: SharedPreferences
    private lateinit var token: String

    private var recipeCategoriesList = ArrayList<JSONObject>()
    private var recipeCategoriesNames = ArrayList<String>()
    private var recipeCategoryId = ""

    private lateinit var selectFoodDialog: AlertDialog
    private lateinit var selectFoodDialogView: View
    private lateinit var selectFoodDialogBuilder: AlertDialog.Builder

    private lateinit var genericAdapter: GenericAdapter

    private var chosenFoods = ArrayList<ChosenFood>()
    private var chosenFoodsJSONArray = JSONArray()
    private var nutrValuesJSONArray = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_recipe)

        context = this

        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        val profileJson = JSONObject(preferences.getString(PROFILE, "")!!)
        token = profileJson.getString(TOKEN)

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

        selectFoodDialogBuilder = AlertDialog.Builder(context)
        selectFoodDialogView = View.inflate(context, R.layout.dialog_choose_food, null)
        selectFoodDialogBuilder.setView(selectFoodDialogView)

        selectFoodDialogView.foods_recycler_view.setHasFixedSize(true)
        selectFoodDialogView.foods_recycler_view.layoutManager = LinearLayoutManager(context)

        getRecipeCategories()
        getFoods()
        showFoods()

        food_view.setOnClickListener {
            openFoodsDialog()
        }

        selectFoodDialogView.close_dialog_btn.setOnClickListener {
            closeSelectFoodDialog()
        }

        approve_food_btn.setOnClickListener {
            approveChosenFood()
        }

        approve_nutr_btn.setOnClickListener {
            approveNutrValue()
        }

        selectFoodDialogView.search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                getFoods(true, query!!)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        create_recipe_btn.setOnClickListener {
            checkInputs()
        }

    }

    private fun checkInputs(){
        val recipeName = recipe_name_view.text.toString()
        val directions = directions_view.text.toString()
        val description = description_view.text.toString()

        when {
            TextUtils.isEmpty(recipeName.trim()) -> recipe_name_view.error = getString(R.string.field_required)
            TextUtils.isEmpty(directions.trim()) -> directions_view.error = getString(R.string.field_required)
            chosenFoodsJSONArray.length() < 1 -> Toast.makeText(context, "No ingredients", Toast.LENGTH_SHORT).show()
            recipeCategoryId.isEmpty() -> Toast.makeText(context, "No recipe type chosen", Toast.LENGTH_SHORT).show()
            else -> {
                disableViews()

                val params = JSONObject()

                params.put("name", recipeName)
                params.put("recipe_type", recipeCategoryId)
                params.put("ingredients", chosenFoodsJSONArray)
                params.put("directions", directions)
                params.put("nutritional_value", nutrValuesJSONArray)
                params.put("description", description)

                create_recipe_btn.visibility = View.GONE
                progress_bar.visibility = View.VISIBLE

                createRecipe(params)

            }
        }

    }

    private fun createRecipe(params: JSONObject) {
        AndroidNetworking.post(API_ROOT + "recipes")
            .setPriority(Priority.MEDIUM)
            .addHeaders("Content-Type", "application/json")
            .addHeaders("Authorization", "Token $token")
            .addJSONObjectBody(params)
            .build()
            .getAsJSONObject(object: JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    create_recipe_btn.visibility = View.VISIBLE
                    progress_bar.visibility = View.GONE

                    Toast.makeText(context, getString(R.string.recipe_created), Toast.LENGTH_SHORT).show()

                    startActivity(Intent(context, MainActivity::class.java))
                    finish()
                }

                override fun onError(anError: ANError?) {
                    enableViews()
                    create_recipe_btn.visibility = View.VISIBLE
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

    private fun openFoodsDialog() {
        food_view.setText("")
        food_quantity.setText("0")
        selectFoodDialog = selectFoodDialogBuilder.create()
        selectFoodDialog.show()
        selectFoodDialog.setCancelable(false)
        selectFoodDialog.setCanceledOnTouchOutside(false)
    }

    private fun closeSelectFoodDialog() {
        selectFoodDialog.dismiss()
        (selectFoodDialogView.parent as ViewGroup).removeView(selectFoodDialogView)
        getFoods()
    }

    private fun getRecipeCategories() {
        recipeCategoriesList.clear()
        recipeCategoriesNames.clear()
        recipeCategoriesNames.add("Recipe Types")

        AndroidNetworking.get(API_ROOT + "recipe-types?ordering=name")
            .setPriority(Priority.MEDIUM)
            .addHeaders("Authorization", "Token $token")
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray?) {
                    if (response!!.length() > 0) {
                        parseCategories(response)
                    } else {
                        val snack = Snackbar.make(root_view, getString(R.string.no_recipe_categories_found), Snackbar.LENGTH_INDEFINITE)
                        snack.setAction(getString(R.string.retry)) {
                            snack.dismiss()
                            getRecipeCategories()
                        }
                        snack.show()
                    }
                }

                override fun onError(anError: ANError?) {
                    val errorMessage = getString(R.string.cant_reach_server)
                    val snack = Snackbar.make(root_view, errorMessage, Snackbar.LENGTH_INDEFINITE)
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

        val recipeCatsAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, recipeCategoriesNames)
        recipeCatsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        recipe_type_spinner.adapter = recipeCatsAdapter

        recipe_type_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (position > 0) {
                    recipeCategoryId = recipeCategoriesList[position - 1].getString("id")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    private fun getFoods(searching: Boolean = false, queryString: String = "") {
        var url = API_ROOT + "foods?ordering=name"
        if (searching) {
            url = API_ROOT + "foods?search=${queryString}&&ordering=name"
        }
        selectFoodDialogView.progress_bar.visibility = View.VISIBLE
        selectFoodDialogView.foods_recycler_view.visibility = View.GONE
        AndroidNetworking.get(url)
            .setPriority(Priority.MEDIUM)
            .addHeaders("Authorization", "Token $token")
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray?) {
                    selectFoodDialogView.progress_bar.visibility = View.GONE
                    selectFoodDialogView.foods_recycler_view.visibility = View.VISIBLE

                    if (response!!.length() > 0) {
                        genericAdapter = GenericAdapter(context, response, INGREDIENTS, forChoosingFood = true)
                        selectFoodDialogView.foods_recycler_view.adapter = genericAdapter
                    } else {
                        val snack = Snackbar.make(root_view, getString(R.string.no_foods_found), Snackbar.LENGTH_INDEFINITE)
                        snack.setAction(getString(R.string.retry)) {
                            snack.dismiss()
                            getFoods()
                        }
                        snack.show()
                    }
                }

                override fun onError(anError: ANError?) {
                    selectFoodDialogView.progress_bar.visibility = View.GONE
                    selectFoodDialogView.foods_recycler_view.visibility = View.VISIBLE

                    val errorMessage = getString(R.string.cant_reach_server)
                    val snack = Snackbar.make(root_view, errorMessage, Snackbar.LENGTH_INDEFINITE)
                    snack.setAction(getString(R.string.retry)) {
                        snack.dismiss()
                        getFoods()
                    }
                    snack.show()
                }

            })
    }

    @SuppressLint("SetTextI18n")
    private fun showFoods() {

        if (chosenFoodsJSONArray.length() > 0) {
            ingredients_view.removeAllViews()

            for (i in 0 until  chosenFoodsJSONArray.length()) {
                val linearLayout = LinearLayout(context)

                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(0, 5, 0, 5)
                linearLayout.orientation = LinearLayout.HORIZONTAL
                linearLayout.layoutParams = layoutParams

                val foodNameView = TextView(context)
                val foodQuantityView = TextView(context)
                val foodUnitView = TextView(context)
                val dotsView = TextView(context)

                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                params.setMargins(0, 0, 0, 5)

                foodNameView.layoutParams = params
                foodQuantityView.layoutParams = params
                foodUnitView.layoutParams = params
                dotsView.layoutParams = params

                val cf = chosenFoodsJSONArray.getJSONObject(i)
                foodNameView.text = cf.getString("food_name")
                dotsView.text = " : "
                foodQuantityView.text = cf.getString("quantity")
                foodUnitView.text = "(${cf.getString("unit")})"

                linearLayout.addView(foodNameView)
                linearLayout.addView(dotsView)
                linearLayout.addView(foodQuantityView)
                linearLayout.addView(foodUnitView)

                ingredients_view.addView(linearLayout)
            }
        } else {
            ingredients_view.removeAllViews()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showNutrs() {

        if (nutrValuesJSONArray.length() > 0) {
            nutritional_value_view.removeAllViews()

            for (i in 0 until  nutrValuesJSONArray.length()) {
                val linearLayout = LinearLayout(context)

                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(0, 5, 0, 5)
                linearLayout.orientation = LinearLayout.HORIZONTAL
                linearLayout.layoutParams = layoutParams

                val nutrNameView = TextView(context)
                val nutrQuantityView = TextView(context)
                val nutrUnitView = TextView(context)
                val dotsView = TextView(context)

                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                params.setMargins(0, 0, 0, 5)

                nutrNameView.layoutParams = params
                nutrQuantityView.layoutParams = params
                nutrUnitView.layoutParams = params
                dotsView.layoutParams = params

                val nutr = nutrValuesJSONArray.getJSONObject(i)
                nutrNameView.text = nutr.getString("name")
                dotsView.text = " : "
                nutrQuantityView.text = nutr.getString("value")
                nutrUnitView.text = "(${nutr.getString("unit")})"

                linearLayout.addView(nutrNameView)
                linearLayout.addView(dotsView)
                linearLayout.addView(nutrQuantityView)
                linearLayout.addView(nutrUnitView)

                nutritional_value_view.addView(linearLayout)
            }
        } else {
            nutritional_value_view.removeAllViews()
        }
    }

    @SuppressLint("SetTextI18n")
    fun selectedFood(food: JSONObject) {
//        Log.d("Testing: ", food.toString())
        food_view.setText("${food.getString("name")} (${food.getString("quantitation")})")
        chosenFoods.add(ChosenFood(food))
        closeSelectFoodDialog()
    }

    private fun approveChosenFood() {
        if (food_view.text.toString().isEmpty()) {
            Toast.makeText(context, "Please choose food", Toast.LENGTH_SHORT).show()
        }
        val foodQuantity = food_quantity.text.toString()
        if (foodQuantity.isNotEmpty() && foodQuantity.toFloat() > 0) {
            val chosenFood = chosenFoods.last()
            val fObj = JSONObject()
            fObj.put("food_id", chosenFood.id)
            fObj.put("food_name", chosenFood.name)
            fObj.put("unit", chosenFood.unit)
            fObj.put("quantity", foodQuantity)

            chosenFoodsJSONArray.put(fObj)
            food_quantity.setText("")
            food_view.setText("")
            showFoods()

        } else {
            Toast.makeText(context, "Please fill the quantity", Toast.LENGTH_SHORT).show()
        }
    }

    private fun approveNutrValue() {
        val nutrName = nutr_name_view.text.toString()
        val nutrUnit = nutr_unit.text.toString()
        val nutrQuantity = nutr_quantity.text.toString()

        if (nutrName.isNotEmpty() && nutrUnit.isNotEmpty() && nutrQuantity.isNotEmpty() && nutrQuantity.toFloat() > 0) {
            val nObj = JSONObject()
            nObj.put("name", nutrName)
            nObj.put("unit", nutrUnit)
            nObj.put("value", nutrQuantity)
            nutrValuesJSONArray.put(nObj)

            nutr_name_view.setText("")
            nutr_unit.setText("")
            nutr_quantity.setText("")
            showNutrs()
        } else {
            nutr_name_view.error = "Field Required"
            nutr_unit.error = "Field Required"
            nutr_quantity.error = "Field Required"
        }
    }

    private fun disableViews(){
        recipe_name_view.isEnabled = false
        recipe_type_spinner.isEnabled = false
        food_view.isEnabled = false
        food_quantity.isEnabled = false
        approve_food_btn.isEnabled = false
        directions_view.isEnabled = false
    }

    private fun enableViews(){
        recipe_name_view.isEnabled = true
        recipe_type_spinner.isEnabled = true
        food_view.isEnabled = true
        food_quantity.isEnabled = true
        approve_food_btn.isEnabled = true
        directions_view.isEnabled = true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}