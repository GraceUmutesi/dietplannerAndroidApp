package rw.dietplanner.dietplanner.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_groceries.view.*
import kotlinx.android.synthetic.main.item_ingredients.view.*
import kotlinx.android.synthetic.main.item_nutrients.view.*
import org.json.JSONArray
import rw.dietplanner.dietplanner.R
import rw.dietplanner.dietplanner.activities.NewRecipeActivity
import rw.dietplanner.dietplanner.utils.Constants.GROCERIES
import rw.dietplanner.dietplanner.utils.Constants.INGREDIENTS
import rw.dietplanner.dietplanner.utils.Constants.NUTRIENTS
import java.lang.Exception

class GenericAdapter(private val context: Context, private val items: JSONArray,
                     private val adapterFor: String, private var forChoosingFood: Boolean = false) :
    RecyclerView.Adapter<GenericAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder: ViewHolder = when (adapterFor) {
            INGREDIENTS -> {
                ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_ingredients, parent, false))
            }
            NUTRIENTS -> {
                ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_nutrients, parent, false))
            }
            GROCERIES -> {
                ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_groceries, parent, false))
            }
            else -> {
                ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_nutrients, parent, false))
            }
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return items.length()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (adapterFor == INGREDIENTS) {
            val ingredient = items.getJSONObject(position)
            if (forChoosingFood) {
                holder.itemView.food_list_view.visibility = View.GONE
                holder.itemView.choose_food_view.visibility = View.VISIBLE

                val item = items.getJSONObject(position)

                holder.itemView.food_name.text = item.getString("name")
                holder.itemView.food_unit.text = "${item.getDouble("unit_price")}RWF / ${item.getString("quantitation")}"
                holder.itemView.food_type.text = item.getString("food_group")

            } else {
                holder.itemView.food_list_view.visibility = View.VISIBLE
                holder.itemView.choose_food_view.visibility = View.GONE

                try {
                    holder.itemView.name.text = ingredient.getString("food_name")
                    holder.itemView.quantity.text = "${ingredient.getString("quantity")} (${ingredient.getString("unit")})"
                } catch (e: Exception) {}
            }
        } else if (adapterFor == NUTRIENTS) {
            val nutrient = items.getJSONObject(position)

            try {
                holder.itemView.nut_name.text = "${nutrient.getString("name")} (${nutrient.getString("unit")})"
                holder.itemView.nut_quantity.text = nutrient.getString("value")
            } catch (e: Exception) {}
        } else if (adapterFor == GROCERIES) {
            val item = items.getJSONObject(position)

            holder.itemView.gf_name.text = item.getString("food_name")
            holder.itemView.gf_quantity.text = "${item.getString("quantity")} (${item.getString("unit")})"
            holder.itemView.gf_price.text = "${item.getString("estimated_price")} RWF"
        }

        holder.itemView.setOnClickListener {
            if (adapterFor == INGREDIENTS && forChoosingFood) {
//                Log.d("Testing: ", items.getJSONObject(position).toString())
                (context as NewRecipeActivity).selectedFood(items.getJSONObject(position))
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}