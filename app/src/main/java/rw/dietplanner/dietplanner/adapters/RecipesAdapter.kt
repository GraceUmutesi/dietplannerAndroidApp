package rw.dietplanner.dietplanner.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_recipes.view.*
import org.json.JSONArray
import org.json.JSONException
import rw.dietplanner.dietplanner.R
import rw.dietplanner.dietplanner.activities.NewMealPlanActivity
import rw.dietplanner.dietplanner.activities.RecipeDetailsActivity
import rw.dietplanner.dietplanner.utils.Constants.API_BASE_ROOT
import rw.dietplanner.dietplanner.utils.Constants.RECIPE

class RecipesAdapter(private val context: Context,
                     private val recipes: JSONArray,
                     private val forChoosingMeal: Boolean = false) :
    RecyclerView.Adapter<RecipesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recipes, parent, false))
    }

    override fun getItemCount(): Int {
        return recipes.length()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = recipes.getJSONObject(position)

        holder.itemView.recipe_name.text = item.getString("name")

        val recipeType = item.getJSONObject("recipe_type")
        holder.itemView.recipe_type.text = recipeType.getString("name")

        try {
            val recipeImage = item.getString("image")
            Picasso.get().load(recipeImage).into(holder.itemView.recipe_img)
        } catch (e: JSONException) {}



        holder.itemView.setOnClickListener {
            if (forChoosingMeal) {
                (context as NewMealPlanActivity).selectMeal(item)
            } else {
                val intent = Intent(context, RecipeDetailsActivity::class.java)
                intent.putExtra(RECIPE, item.toString())
                context.startActivity(intent)
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}