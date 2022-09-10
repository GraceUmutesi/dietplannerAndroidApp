package rw.dietplanner.dietplanner.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.UploadProgressListener
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_new_meal_plan.*
import kotlinx.android.synthetic.main.activity_recipe_details.*
import kotlinx.android.synthetic.main.activity_recipe_details.root_view
import kotlinx.android.synthetic.main.progress_layout.*
import okhttp3.OkHttpClient
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.json.JSONException
import org.json.JSONObject
import rw.dietplanner.dietplanner.R
import rw.dietplanner.dietplanner.adapters.GenericAdapter
import rw.dietplanner.dietplanner.utils.Constants
import rw.dietplanner.dietplanner.utils.Constants.INGREDIENTS
import rw.dietplanner.dietplanner.utils.Constants.NUTRIENTS
import rw.dietplanner.dietplanner.utils.Constants.RECIPE
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit


class RecipeDetailsActivity : AppCompatActivity() {
    lateinit var context: Context
    private lateinit var preferences: SharedPreferences
    private lateinit var token: String

    lateinit var genericAdapter: GenericAdapter

    private lateinit var recipe: JSONObject

    val REQUEST_CODE = 214
    val PERMISSION_REQUEST_CODE = 313
    private var permissionsGranted = false
    private val PICK_IMAGE = 100
    private var imageUri: Uri? = null

    private val UPDATE_IMAGE = 453

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        context = this

        recipe = JSONObject(intent.getStringExtra(RECIPE)!!)

        preferences = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE)
        val profileJson = JSONObject(preferences.getString(Constants.PROFILE, "")!!)
        token = profileJson.getString(Constants.TOKEN)

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

        ingredients_recyclerview.setHasFixedSize(true)
        ingredients_recyclerview.layoutManager = LinearLayoutManager(context)

        nutrients_recyclerview.setHasFixedSize(true)
        nutrients_recyclerview.layoutManager = LinearLayoutManager(context)

        loadRecipeDetails()

        take_recipe_image.setOnClickListener {
            takeRecipeImage()
        }

        like_recipe_btn.setOnClickListener {
            likeRecipe(recipe)
        }

        send_image_to_server.setOnClickListener {
            sendImageToServer()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadRecipeDetails() {
        recipe_name.text = recipe.getString("name")
        recipe_estimated_price.text = "${recipe.getString("estimated_price")} RWF"
        recipe_directions.text = recipe.getString("directions")

        val recipeType = recipe.getJSONObject("recipe_type")
        recipe_type.text = recipeType.getString("name")

        val recipeDesc = recipe.getString("description")
        if (recipeDesc.isNotEmpty()) {
            description_view.visibility = View.VISIBLE
            recipe_description.text = recipeDesc
        }

        try {
            val recipeImage = recipe.getString("image")
            Picasso.get().load(recipeImage).into(recipe_image)
            if (recipe.getBoolean("sys_recipe")) {
                take_recipe_image.visibility = View.GONE
            } else {
                take_recipe_image.visibility = View.VISIBLE
            }
        } catch (e: JSONException) {}

        val ingredients = recipe.getJSONArray("ingredients")

        if (ingredients.length() > 0) {
            ingredients_recyclerview.adapter = GenericAdapter(context, ingredients, INGREDIENTS)
        }

        val nutritionalValue = recipe.getJSONArray("nutritional_value")

        if (nutritionalValue.length() > 0) {
            nutrients_recyclerview.adapter = GenericAdapter(context, nutritionalValue, NUTRIENTS)
        }

    }

    private fun takeRecipeImage() {
        getPermissions()
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        getImageResult.launch(gallery)
    }

    private val getImageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK){
                imageUri= it.data?.data!!
                recipe_image.setImageURI(imageUri)
                send_image_to_server.visibility = View.VISIBLE
            }
        }

    // REQUESTING PERMISSIONS
    private fun getPermissions() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            permissionsGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    // PERMISSION RESULTS
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsGranted = false
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    var i = 0
                    while (i < grantResults.size) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            permissionsGranted = false
                            return
                        }
                        i++
                    }
                    permissionsGranted = true
                }
            }
        }
    }

    fun likeRecipe(rcp: JSONObject) {
        like_recipe_btn.visibility = View.GONE
        progress_bar.visibility = View.VISIBLE

        val params = JSONObject()
        params.put("recipe", rcp.getString("id"))

        AndroidNetworking.post(Constants.API_ROOT + "favorite-recipes")
            .setPriority(Priority.MEDIUM)
            .addHeaders("Content-Type", "application/json")
            .addHeaders("Authorization", "Token $token")
            .addJSONObjectBody(params)
            .build()
            .getAsJSONObject(object: JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    like_recipe_btn.visibility = View.VISIBLE
                    progress_bar.visibility = View.GONE

                    Toast.makeText(context, "Grocery list created Successfully", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(context, FavoritesActivity::class.java))
                }

                override fun onError(anError: ANError?) {
                    like_recipe_btn.visibility = View.VISIBLE
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

    @Throws(IOException::class)
    private fun toFile(uri: Uri, filename: String): File? {
        val displayName = ""
        val file = File.createTempFile(filename, "." + FilenameUtils.getExtension(displayName)
        )
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        FileUtils.copyInputStreamToFile(inputStream, file)
        return file
    }

    private fun sendImageToServer() {
        if (imageUri == null) {
            Toast.makeText(context, "Please choose an image first!", Toast.LENGTH_SHORT).show()
        } else {
            val imageFile = toFile(imageUri!!, recipe.getString("name"))

            AndroidNetworking.upload(Constants.API_ROOT + "save-recipe-image")
                .setPriority(Priority.HIGH)
                .addHeaders("Authorization", "Token $token")
                .addMultipartFile("recipe_image", imageFile)
                .addMultipartParameter("recipe_id", recipe.getString("id"))
                .build()
                .setUploadProgressListener(object : UploadProgressListener {
                    override fun onProgress(bytesUploaded: Long, totalBytes: Long) {
                        upload_progress.visibility = View.VISIBLE
                        val ub = ((bytesUploaded / totalBytes) * 100).toInt()

                        upload_progress.progress = ub
                    }

                })
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        upload_progress.visibility = View.GONE
                        send_image_to_server.visibility = View.GONE
                        Toast.makeText(context, "Image added successfully", Toast.LENGTH_SHORT).show()
                        recipe = response!!

                        loadRecipeDetails()
                        runOnUiThread {
                            // change UI elements here
                            val recipeImage = recipe.getString("image")
                            Picasso.get().load(recipeImage).into(recipe_image)
                        }
                    }

                    override fun onError(anError: ANError?) {
                        Log.d("UPLOAD_IMAGE", anError.toString())
                        upload_progress.visibility = View.GONE
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
    }

//    val handler: Handler = object : Handler(Looper.myLooper()!!) {
//        override fun handleMessage(msg: Message) {
//            if (msg.what === UPDATE_IMAGE) {
//                images.get(msg.arg1).setImageBitmap(msg.obj as Bitmap)
//            }
//            super.handleMessage(msg)
//        }
//    }
}