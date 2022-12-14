package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAuthenticationBinding

    companion object{
        const val SING_IN_RESULT_CODE = 1001
        const val TAG = "LoginFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_authentication)

        if (FirebaseAuth.getInstance().currentUser == null){
            binding.loginBtn.setOnClickListener{launchSignInFlow()}
        }else{
            startActivity(Intent(this,RemindersActivity::class.java))
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SING_IN_RESULT_CODE){
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK){
                Log.i(TAG,"Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!")
                Toast.makeText(this,"SignIn Successfully & Hello ${FirebaseAuth.getInstance().currentUser?.displayName}",Toast.LENGTH_LONG).show()
                startActivity(Intent(this,RemindersActivity::class.java))
                finish()
            }else{
                Log.i(TAG,"signIn Failed ${response?.error?.errorCode}!")
                Toast.makeText(this,"SignIn Failed Please Try Again",Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun launchSignInFlow() {
        val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build(),AuthUI.IdpConfig.GoogleBuilder().build())

        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),
            SING_IN_RESULT_CODE)
    }
}
