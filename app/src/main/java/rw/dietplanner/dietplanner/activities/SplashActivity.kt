package rw.dietplanner.dietplanner.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import rw.dietplanner.dietplanner.R
import rw.dietplanner.dietplanner.utils.Constants.PREFERENCES
import rw.dietplanner.dietplanner.utils.Constants.PROFILE

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    lateinit var context: Context
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        context = this
        preferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

        checkIfUserIsAuthenticated()

//        val handler = Handler(Looper.myLooper()!!)
//        handler.postDelayed({
//            startActivity(Intent(context, WelcomeActivity::class.java))
//            finish()
//        }, 2000)

    }

    private fun checkIfUserIsAuthenticated() {
        val profile = preferences.getString(PROFILE, "")

        if (profile!!.isEmpty()) {
            startActivity(Intent(context, WelcomeActivity::class.java))
            finish()
        } else {
            startActivity(Intent(context, MainActivity::class.java))
            finish()
        }
    }

}