package rw.dietplanner.dietplanner.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.container
import kotlinx.android.synthetic.main.activity_sign_up.password_view
import kotlinx.android.synthetic.main.progress_layout.*
import okhttp3.OkHttpClient
import org.json.JSONObject
import rw.dietplanner.dietplanner.R
import rw.dietplanner.dietplanner.utils.Constants.API_ROOT
import rw.dietplanner.dietplanner.utils.Constants.EMAIL
import rw.dietplanner.dietplanner.utils.Constants.FIRST_NAME
import rw.dietplanner.dietplanner.utils.Constants.LAST_NAME
import rw.dietplanner.dietplanner.utils.Constants.PASSWORD
import rw.dietplanner.dietplanner.utils.Constants.PREFERENCES
import rw.dietplanner.dietplanner.utils.Constants.PROFILE
import java.util.concurrent.TimeUnit

class SignUp : AppCompatActivity() {
    lateinit var context: Context
    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        context = this
        preferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()

        AndroidNetworking.initialize(context, okHttpClient)

        go_to_login_btn.setOnClickListener {
            startActivity(Intent(context, LoginActivity::class.java))
        }

        sign_up_btn.setOnClickListener {
            checkInputs()
        }
    }

    private fun checkInputs(){
        val firstName = first_name_view.text.toString()
        val lastName = last_name_view.text.toString()
        val email = email_view.text.toString().trim()
        val password = password_view.text.toString().trim()

        when {
            TextUtils.isEmpty(firstName.trim()) -> first_name_view.error = getString(R.string.field_required)
            TextUtils.isEmpty(lastName.trim()) -> last_name_view.error = getString(R.string.field_required)
            TextUtils.isEmpty(email) -> email_view.error = getString(R.string.field_required)
            !isEmailValid(email) -> email_view.error = "Invalid Email"
            TextUtils.isEmpty(password) -> password_view.error = getString(R.string.field_required)
            else -> {
                disableViews()

                val params = JSONObject()

                params.put(FIRST_NAME, firstName)
                params.put(LAST_NAME, lastName)
                params.put(EMAIL, email)
                params.put(PASSWORD, password)

                sign_up_btn.visibility = View.GONE
                progress_bar.visibility = View.VISIBLE

                performRegister(params)

            }
        }

    }

    private fun isEmailValid(email: CharSequence?) : Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email!!).matches()
    }

    private fun performRegister(params: JSONObject) {
        AndroidNetworking.post(API_ROOT + "authentication/create-account")
            .setPriority(Priority.MEDIUM)
            .addHeaders("Content-Type", "application/json")
            .addJSONObjectBody(params)
            .build()
            .getAsJSONObject(object: JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    sign_up_btn.visibility = View.VISIBLE
                    progress_bar.visibility = View.GONE

                    Toast.makeText(context, getString(R.string.created_account), Toast.LENGTH_SHORT).show()

                    preferences.edit()
                        .putString(PROFILE, response!!.toString())
                        .apply()

                    startActivity(Intent(context, MainActivity::class.java))
                    finish()
                }

                override fun onError(anError: ANError?) {
                    enableViews()
                    sign_up_btn.visibility = View.VISIBLE
                    progress_bar.visibility = View.GONE

                    var errorMessage = getString(R.string.cant_reach_server)

                    when(anError!!.errorCode) {
                        409 -> errorMessage = getString(R.string.user_exists)
                        400 -> errorMessage = getString(R.string.bad_request)
                    }

                    val snack = Snackbar.make(container, errorMessage, Snackbar.LENGTH_LONG)
                    snack.setAction(getString(R.string.dismiss)) {
                        snack.dismiss()
                    }
                    snack.show()

                }

            })
    }

    private fun disableViews(){
        first_name_view.isEnabled = false
        last_name_view.isEnabled = false
        email_view.isEnabled = false
        password_view.isEnabled = false
        go_to_login_btn.isEnabled = false
    }

    private fun enableViews(){
        first_name_view.isEnabled = true
        last_name_view.isEnabled = true
        email_view.isEnabled = true
        password_view.isEnabled = true
        go_to_login_btn.isEnabled = true
    }
}