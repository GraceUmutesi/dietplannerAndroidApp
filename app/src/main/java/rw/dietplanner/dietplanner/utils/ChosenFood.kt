package rw.dietplanner.dietplanner.utils

import org.json.JSONException
import org.json.JSONObject

class ChosenFood (food: JSONObject) {
    var id = ""
    var name = ""
    var unit = ""
    var quantity = 0.0

    init {
        try {
            this.id = food.getString("id")
            this.name = food.getString("name")
            this.unit = food.getString("quantitation")
        } catch (e: JSONException) { }
    }
}