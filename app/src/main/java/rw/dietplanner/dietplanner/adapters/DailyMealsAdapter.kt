package rw.dietplanner.dietplanner.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_recipe_details.*
import kotlinx.android.synthetic.main.item_daily_meals.view.*
import org.json.JSONArray
import org.json.JSONObject
import rw.dietplanner.dietplanner.R
import rw.dietplanner.dietplanner.activities.MealPlanDetailsActivity

class DailyMealsAdapter(private val context: Context, private val meals: JSONObject) :
    RecyclerView.Adapter<DailyMealsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_daily_meals, parent, false))
    }

    override fun getItemCount(): Int {
        return meals.getJSONArray("days").length()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (position) {
            0 -> {
                holder.itemView.day_view.text = "MONDAY"
                val jArr = meals.getJSONArray("Monday")
                if (jArr.length() > 0) {
                    for (i in 0 until jArr.length()) {
                        val obj = jArr.getJSONObject(i)
                        if (obj.getString("time") == "Breakfast") {
                            holder.itemView.bf_meal_img.visibility = View.VISIBLE
                            holder.itemView.bf_meal_name.visibility = View.VISIBLE
                            holder.itemView.bf_delete_btn.visibility = View.VISIBLE
                            Picasso.get().load(obj.getString("recipe_image")).into(holder.itemView.bf_meal_img)
                            holder.itemView.bf_meal_name.text = obj.getString("recipe_name")
                        } else if (obj.getString("time") == "Lunch") {
                            holder.itemView.l_meal_img.visibility = View.VISIBLE
                            holder.itemView.l_meal_name.visibility = View.VISIBLE
                            holder.itemView.l_delete_btn.visibility = View.VISIBLE
                            Picasso.get().load(obj.getString("recipe_image")).into(holder.itemView.l_meal_img)
                            holder.itemView.l_meal_name.text = obj.getString("recipe_name")
                        } else if (obj.getString("time") == "Dinner") {
                            holder.itemView.d_meal_img.visibility = View.VISIBLE
                            holder.itemView.d_meal_name.visibility = View.VISIBLE
                            holder.itemView.d_delete_btn.visibility = View.VISIBLE
                            Picasso.get().load(obj.getString("recipe_image")).into(holder.itemView.d_meal_img)
                            holder.itemView.d_meal_name.text = obj.getString("recipe_name")
                        }
                    }
                }
            }
            1 -> {
                holder.itemView.day_view.text = "TUESDAY"
                val jArr = meals.getJSONArray("Tuesday")
                if (jArr.length() > 0) {
                    for (i in 0 until jArr.length()) {
                        val obj = jArr.getJSONObject(i)
                        if (obj.getString("time") == "Breakfast") {
                            holder.itemView.bf_meal_img.visibility = View.VISIBLE
                            holder.itemView.bf_meal_name.visibility = View.VISIBLE
                            holder.itemView.bf_delete_btn.visibility = View.VISIBLE
                            Picasso.get().load(obj.getString("recipe_image")).into(holder.itemView.bf_meal_img)
                            holder.itemView.bf_meal_name.text = obj.getString("recipe_name")
                        } else if (obj.getString("time") == "Lunch") {
                            holder.itemView.l_meal_img.visibility = View.VISIBLE
                            holder.itemView.l_meal_name.visibility = View.VISIBLE
                            holder.itemView.l_delete_btn.visibility = View.VISIBLE
                            Picasso.get().load(obj.getString("recipe_image")).into(holder.itemView.l_meal_img)
                            holder.itemView.l_meal_name.text = obj.getString("recipe_name")
                        } else if (obj.getString("time") == "Dinner") {
                            holder.itemView.d_meal_img.visibility = View.VISIBLE
                            holder.itemView.d_meal_name.visibility = View.VISIBLE
                            holder.itemView.d_delete_btn.visibility = View.VISIBLE
                            Picasso.get().load(obj.getString("recipe_image")).into(holder.itemView.d_meal_img)
                            holder.itemView.d_meal_name.text = obj.getString("recipe_name")
                        }
                    }
                }
            }
            2 -> {
                holder.itemView.day_view.text = "WEDNESDAY"
                val jArr = meals.getJSONArray("Wednesday")
                if (jArr.length() > 0) {
                    for (i in 0 until jArr.length()) {
                        val obj = jArr.getJSONObject(i)
                        if (obj.getString("time") == "Breakfast") {
                            holder.itemView.bf_meal_img.visibility = View.VISIBLE
                            holder.itemView.bf_meal_name.visibility = View.VISIBLE
                            holder.itemView.bf_delete_btn.visibility = View.VISIBLE
                            Picasso.get().load(obj.getString("recipe_image")).into(holder.itemView.bf_meal_img)
                            holder.itemView.bf_meal_name.text = obj.getString("recipe_name")
                        } else if (obj.getString("time") == "Lunch") {
                            holder.itemView.l_meal_img.visibility = View.VISIBLE
                            holder.itemView.l_meal_name.visibility = View.VISIBLE
                            holder.itemView.l_delete_btn.visibility = View.VISIBLE
                            Picasso.get().load(obj.getString("recipe_image")).into(holder.itemView.l_meal_img)
                            holder.itemView.l_meal_name.text = obj.getString("recipe_name")
                        } else if (obj.getString("time") == "Dinner") {
                            holder.itemView.d_meal_img.visibility = View.VISIBLE
                            holder.itemView.d_meal_name.visibility = View.VISIBLE
                            holder.itemView.d_delete_btn.visibility = View.VISIBLE
                            Picasso.get().load(obj.getString("recipe_image")).into(holder.itemView.d_meal_img)
                            holder.itemView.d_meal_name.text = obj.getString("recipe_name")
                        }
                    }
                }
            }
            3 -> {
                holder.itemView.day_view.text = "THURSDAY"
                val jArr = meals.getJSONArray("Thursday")
                if (jArr.length() > 0) {
                    for (i in 0 until jArr.length()) {
                        val obj = jArr.getJSONObject(i)
                        if (obj.getString("time") == "Breakfast") {
                            holder.itemView.bf_meal_img.visibility = View.VISIBLE
                            holder.itemView.bf_meal_name.visibility = View.VISIBLE
                            holder.itemView.bf_delete_btn.visibility = View.VISIBLE
                            Picasso.get().load(obj.getString("recipe_image")).into(holder.itemView.bf_meal_img)
                            holder.itemView.bf_meal_name.text = obj.getString("recipe_name")
                        } else if (obj.getString("time") == "Lunch") {
                            holder.itemView.l_meal_img.visibility = View.VISIBLE
                            holder.itemView.l_meal_name.visibility = View.VISIBLE
                            holder.itemView.l_delete_btn.visibility = View.VISIBLE
                            Picasso.get().load(obj.getString("recipe_image")).into(holder.itemView.l_meal_img)
                            holder.itemView.l_meal_name.text = obj.getString("recipe_name")
                        } else if (obj.getString("time") == "Dinner") {
                            holder.itemView.d_meal_img.visibility = View.VISIBLE
                            holder.itemView.d_meal_name.visibility = View.VISIBLE
                            holder.itemView.d_delete_btn.visibility = View.VISIBLE
                            Picasso.get().load(obj.getString("recipe_image")).into(holder.itemView.d_meal_img)
                            holder.itemView.d_meal_name.text = obj.getString("recipe_name")
                        }
                    }
                }
            }
            4 -> {
                holder.itemView.day_view.text = "FRIDAY"
                val jArr = meals.getJSONArray("Friday")
                if (jArr.length() > 0) {
                    for (i in 0 until jArr.length()) {
                        val obj = jArr.getJSONObject(i)
                        if (obj.getString("time") == "Breakfast") {
                            holder.itemView.bf_meal_img.visibility = View.VISIBLE
                            holder.itemView.bf_meal_name.visibility = View.VISIBLE
                            holder.itemView.bf_delete_btn.visibility = View.VISIBLE
                            Picasso.get().load(obj.getString("recipe_image")).into(holder.itemView.bf_meal_img)
                            holder.itemView.bf_meal_name.text = obj.getString("recipe_name")
                        } else if (obj.getString("time") == "Lunch") {
                            holder.itemView.l_meal_img.visibility = View.VISIBLE
                            holder.itemView.l_meal_name.visibility = View.VISIBLE
                            holder.itemView.l_delete_btn.visibility = View.VISIBLE
                            Picasso.get().load(obj.getString("recipe_image")).into(holder.itemView.l_meal_img)
                            holder.itemView.l_meal_name.text = obj.getString("recipe_name")
                        } else if (obj.getString("time") == "Dinner") {
                            holder.itemView.d_meal_img.visibility = View.VISIBLE
                            holder.itemView.d_meal_name.visibility = View.VISIBLE
                            holder.itemView.d_delete_btn.visibility = View.VISIBLE
                            Picasso.get().load(obj.getString("recipe_image")).into(holder.itemView.d_meal_img)
                            holder.itemView.d_meal_name.text = obj.getString("recipe_name")
                        }
                    }
                }
            }
            5 -> {
                holder.itemView.day_view.text = "SATURDAY"
                val jArr = meals.getJSONArray("Saturday")
                if (jArr.length() > 0) {
                    for (i in 0 until jArr.length()) {
                        val obj = jArr.getJSONObject(i)
                        if (obj.getString("time") == "Breakfast") {
                            holder.itemView.bf_meal_img.visibility = View.VISIBLE
                            holder.itemView.bf_meal_name.visibility = View.VISIBLE
                            holder.itemView.bf_delete_btn.visibility = View.VISIBLE
                            Picasso.get().load(obj.getString("recipe_image")).into(holder.itemView.bf_meal_img)
                            holder.itemView.bf_meal_name.text = obj.getString("recipe_name")
                        } else if (obj.getString("time") == "Lunch") {
                            holder.itemView.l_meal_img.visibility = View.VISIBLE
                            holder.itemView.l_meal_name.visibility = View.VISIBLE
                            holder.itemView.l_delete_btn.visibility = View.VISIBLE
                            Picasso.get().load(obj.getString("recipe_image")).into(holder.itemView.l_meal_img)
                            holder.itemView.l_meal_name.text = obj.getString("recipe_name")
                        } else if (obj.getString("time") == "Dinner") {
                            holder.itemView.d_meal_img.visibility = View.VISIBLE
                            holder.itemView.d_meal_name.visibility = View.VISIBLE
                            holder.itemView.d_delete_btn.visibility = View.VISIBLE
                            Picasso.get().load(obj.getString("recipe_image")).into(holder.itemView.d_meal_img)
                            holder.itemView.d_meal_name.text = obj.getString("recipe_name")
                        }
                    }
                }
            }
            6 -> {
                holder.itemView.day_view.text = "SUNDAY"
                val jArr = meals.getJSONArray("Sunday")
                if (jArr.length() > 0) {
                    for (i in 0 until jArr.length()) {
                        val obj = jArr.getJSONObject(i)
                        if (obj.getString("time") == "Breakfast") {
                            holder.itemView.bf_meal_img.visibility = View.VISIBLE
                            holder.itemView.bf_meal_name.visibility = View.VISIBLE
                            holder.itemView.bf_delete_btn.visibility = View.VISIBLE
                            Picasso.get().load(obj.getString("recipe_image")).into(holder.itemView.bf_meal_img)
                            holder.itemView.bf_meal_name.text = obj.getString("recipe_name")
                        } else if (obj.getString("time") == "Lunch") {
                            holder.itemView.l_meal_img.visibility = View.VISIBLE
                            holder.itemView.l_meal_name.visibility = View.VISIBLE
                            holder.itemView.l_delete_btn.visibility = View.VISIBLE
                            Picasso.get().load(obj.getString("recipe_image")).into(holder.itemView.l_meal_img)
                            holder.itemView.l_meal_name.text = obj.getString("recipe_name")
                        } else if (obj.getString("time") == "Dinner") {
                            holder.itemView.d_meal_img.visibility = View.VISIBLE
                            holder.itemView.d_meal_name.visibility = View.VISIBLE
                            holder.itemView.d_delete_btn.visibility = View.VISIBLE
                            Picasso.get().load(obj.getString("recipe_image")).into(holder.itemView.d_meal_img)
                            holder.itemView.d_meal_name.text = obj.getString("recipe_name")
                        }
                    }
                }
            }
        }

        holder.itemView.bf_delete_btn.setOnClickListener {
            when (position) {
                0 -> {
                    val jArr = meals.getJSONArray("Monday")
                    val obj = getObjectFromKey(jArr, "time", "Breakfast")
                    (context as MealPlanDetailsActivity).deleteMPItem(obj)
                }
                1 -> {
                    val jArr = meals.getJSONArray("Tuesday")
                    val obj = getObjectFromKey(jArr, "time", "Breakfast")
                    (context as MealPlanDetailsActivity).deleteMPItem(obj)
                }
                2 -> {
                    val jArr = meals.getJSONArray("Wednesday")
                    val obj = getObjectFromKey(jArr, "time", "Breakfast")
                    (context as MealPlanDetailsActivity).deleteMPItem(obj)
                }
                3 -> {
                    val jArr = meals.getJSONArray("Thursday")
                    val obj = getObjectFromKey(jArr, "time", "Breakfast")
                    (context as MealPlanDetailsActivity).deleteMPItem(obj)
                }
                4 -> {
                    val jArr = meals.getJSONArray("Friday")
                    val obj = getObjectFromKey(jArr, "time", "Breakfast")
                    (context as MealPlanDetailsActivity).deleteMPItem(obj)
                }
                5 -> {
                    val jArr = meals.getJSONArray("Saturday")
                    val obj = getObjectFromKey(jArr, "time", "Breakfast")
                    (context as MealPlanDetailsActivity).deleteMPItem(obj)
                }
                6 -> {
                    val jArr = meals.getJSONArray("Sunday")
                    val obj = getObjectFromKey(jArr, "time", "Breakfast")
                    (context as MealPlanDetailsActivity).deleteMPItem(obj)
                }
            }
        }

        holder.itemView.l_delete_btn.setOnClickListener {
            when (position) {
                0 -> {
                    val jArr = meals.getJSONArray("Monday")
                    val obj = getObjectFromKey(jArr, "time", "Lunch")
                    (context as MealPlanDetailsActivity).deleteMPItem(obj)
                }
                1 -> {
                    val jArr = meals.getJSONArray("Tuesday")
                    val obj = getObjectFromKey(jArr, "time", "Lunch")
                    (context as MealPlanDetailsActivity).deleteMPItem(obj)
                }
                2 -> {
                    val jArr = meals.getJSONArray("Wednesday")
                    val obj = getObjectFromKey(jArr, "time", "Lunch")
                    (context as MealPlanDetailsActivity).deleteMPItem(obj)
                }
                3 -> {
                    val jArr = meals.getJSONArray("Thursday")
                    val obj = getObjectFromKey(jArr, "time", "Lunch")
                    (context as MealPlanDetailsActivity).deleteMPItem(obj)
                }
                4 -> {
                    val jArr = meals.getJSONArray("Friday")
                    val obj = getObjectFromKey(jArr, "time", "Lunch")
                    (context as MealPlanDetailsActivity).deleteMPItem(obj)
                }
                5 -> {
                    val jArr = meals.getJSONArray("Saturday")
                    val obj = getObjectFromKey(jArr, "time", "Lunch")
                    (context as MealPlanDetailsActivity).deleteMPItem(obj)
                }
                6 -> {
                    val jArr = meals.getJSONArray("Sunday")
                    val obj = getObjectFromKey(jArr, "time", "Lunch")
                    (context as MealPlanDetailsActivity).deleteMPItem(obj)
                }
            }
        }

        holder.itemView.d_delete_btn.setOnClickListener {
            when (position) {
                0 -> {
                    val jArr = meals.getJSONArray("Monday")
                    val obj = getObjectFromKey(jArr, "time", "Dinner")
                    (context as MealPlanDetailsActivity).deleteMPItem(obj)
                }
                1 -> {
                    val jArr = meals.getJSONArray("Tuesday")
                    val obj = getObjectFromKey(jArr, "time", "Dinner")
                    (context as MealPlanDetailsActivity).deleteMPItem(obj)
                }
                2 -> {
                    val jArr = meals.getJSONArray("Wednesday")
                    val obj = getObjectFromKey(jArr, "time", "Dinner")
                    (context as MealPlanDetailsActivity).deleteMPItem(obj)
                }
                3 -> {
                    val jArr = meals.getJSONArray("Thursday")
                    val obj = getObjectFromKey(jArr, "time", "Dinner")
                    (context as MealPlanDetailsActivity).deleteMPItem(obj)
                }
                4 -> {
                    val jArr = meals.getJSONArray("Friday")
                    val obj = getObjectFromKey(jArr, "time", "Dinner")
                    (context as MealPlanDetailsActivity).deleteMPItem(obj)
                }
                5 -> {
                    val jArr = meals.getJSONArray("Saturday")
                    val obj = getObjectFromKey(jArr, "time", "Dinner")
                    (context as MealPlanDetailsActivity).deleteMPItem(obj)
                }
                6 -> {
                    val jArr = meals.getJSONArray("Sunday")
                    val obj = getObjectFromKey(jArr, "time", "Dinner")
                    (context as MealPlanDetailsActivity).deleteMPItem(obj)
                }
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private fun getObjectFromKey(array: JSONArray, key: String, value: String): JSONObject {
        var obj: JSONObject? = null
        for (i in 0 until array.length()) {
            val item = array.getJSONObject(i)
            if (item.has(key)) {
                if (item[key] == value) {
                    obj = item
                    break
                }
//                value = item[key] as String
//                break
            }
        }
        return obj!!
    }
}