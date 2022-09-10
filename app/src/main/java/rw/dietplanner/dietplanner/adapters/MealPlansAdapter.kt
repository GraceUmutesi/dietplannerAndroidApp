package rw.dietplanner.dietplanner.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_meal_plans.view.*
import org.json.JSONArray
import rw.dietplanner.dietplanner.R
import rw.dietplanner.dietplanner.activities.MealPlanDetailsActivity
import rw.dietplanner.dietplanner.utils.Constants.MEAL_PLAN

class MealPlansAdapter(private val context: Context, private val mealPlans: JSONArray) :
    RecyclerView.Adapter<MealPlansAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_meal_plans, parent, false))
    }

    override fun getItemCount(): Int {
        return mealPlans.length()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mealPlan = mealPlans.getJSONObject(position)

        holder.itemView.meal_plan_name.text = mealPlan.getString("name")
        holder.itemView.meal_plan_created_time.text = parseTime(mealPlan.getString("created_at"))

        holder.itemView.setOnClickListener {
            val intent = Intent(context, MealPlanDetailsActivity::class.java)
            intent.putExtra(MEAL_PLAN, mealPlan.toString())
            context.startActivity(intent)
        }
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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}