package rw.dietplanner.dietplanner.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.progress_layout.*
import okhttp3.OkHttpClient
import org.json.JSONObject
import rw.dietplanner.dietplanner.R
import rw.dietplanner.dietplanner.utils.Constants
import rw.dietplanner.dietplanner.utils.Constants.API_ROOT
import rw.dietplanner.dietplanner.utils.Constants.PASSWORD
import rw.dietplanner.dietplanner.utils.Constants.PROFILE
import rw.dietplanner.dietplanner.utils.Constants.USERNAME
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    lateinit var context: Context
    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        context = this
        preferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()

        AndroidNetworking.initialize(context, okHttpClient)

        login_btn.setOnClickListener {
            checkInputs()
        }

        register_btn.setOnClickListener {
            startActivity(Intent(context, SignUp::class.java))
        }
    }

    private fun checkInputs(){
        val username: String = username_view.text.toString().trim()
        val password: String = password_view.text.toString().trim()

        when {
            TextUtils.isEmpty(username) -> username_view.error = getString(R.string.field_required)
            !isEmailValid(username) -> username_view.error = "Invalid Email"
            TextUtils.isEmpty(password) -> password_view.error = getString(R.string.field_required)
            else -> {
                disableViews()

                val params = JSONObject()
                params.put(USERNAME, username)
                params.put(PASSWORD, password)

                performLogin(params)
            }
        }

    }

    private fun isEmailValid(email: CharSequence?) : Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email!!).matches()
    }

    private fun performLogin(params: JSONObject) {
        login_btn.visibility = View.GONE
        progress_bar.visibility = View.VISIBLE

        AndroidNetworking.post(API_ROOT + "authentication/login")
            .setPriority(Priority.MEDIUM)
            .addHeaders("Content-Type", "application/json")
            .addJSONObjectBody(params)
            .build()
            .getAsJSONObject(object: JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    login_btn.visibility = View.VISIBLE
                    progress_bar.visibility = View.GONE

                    preferences.edit()
                        .putString(PROFILE, response!!.toString())
                        .apply()

                    startActivity(Intent(context, MainActivity::class.java))
                    finish()

                }

                override fun onError(anError: ANError?) {
                    enableViews()
                    login_btn.visibility = View.VISIBLE
                    progress_bar.visibility = View.GONE

                    var errorMessage = getString(R.string.cant_reach_server)

                    if (anError!!.errorCode == 400){
                        errorMessage = getString(R.string.invalid_user_text)
                    }

                    val snack = Snackbar.make(container,errorMessage, Snackbar.LENGTH_LONG)
                    snack.setAction(getString(R.string.dismiss)) {
                        snack.dismiss()
                    }
                    snack.show()

                }

            })

    }


    private fun disableViews(){
        username_view.isEnabled = false
        password_view.isEnabled = false
        register_btn.isEnabled = false
        login_btn.isEnabled = false
    }

    private fun enableViews(){
        username_view.isEnabled = true
        password_view.isEnabled = true
        register_btn.isEnabled = true
        login_btn.isEnabled = true
    }

}