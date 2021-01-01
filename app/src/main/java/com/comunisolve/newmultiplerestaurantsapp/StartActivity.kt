package com.comunisolve.newmultiplerestaurantsapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.comunisolve.newmultiplerestaurantsapp.Common.Common
import com.comunisolve.newmultiplerestaurantsapp.Model.UserModel
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.IMyRestaurantAPI
import com.comunisolve.newmultiplerestaurantsapp.Retrofit.RetrofitClient
import com.comunisolve.newmultiplerestaurantsapp.ui.RegisterUserFragmentDialog.AlertDialogFragment
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dmax.dialog.SpotsDialog
import io.paperdb.Paper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.*

class StartActivity : AppCompatActivity() {
    lateinit var fm: FragmentManager
    lateinit var alertDialogFragment: AlertDialogFragment
    var myRestaurantAPI: IMyRestaurantAPI? = null
    var compositeDisposable = CompositeDisposable()
    var dialog: AlertDialog? = null


    companion object {
        private val LOGIN_REQUEST_CODE = 7171
        private const val TAG = "StartActivity"
        private val SPLASH_TIME_OUT: Long = 3000 // 1 sec

    }

    lateinit var providers: List<AuthUI.IdpConfig>
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var listner: FirebaseAuth.AuthStateListener

    override fun onStart() {
        super.onStart()
        delaySplashScreen()
    }

    private fun delaySplashScreen() {
        Handler().postDelayed({

            firebaseAuth.addAuthStateListener(listner)

        }, SPLASH_TIME_OUT)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        init()

    }

    private fun init() {

        Paper.init(this)
        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI::class.java)

        fm = getSupportFragmentManager()
        alertDialogFragment =
                AlertDialogFragment.newInstance("Some Title")!!
        providers = Arrays.asList(
                AuthUI.IdpConfig.PhoneBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
        )

        firebaseAuth = FirebaseAuth.getInstance()
        listner = FirebaseAuth.AuthStateListener { myFirebaseAuth ->
            val user = myFirebaseAuth.currentUser
            if (user != null) {

                checkUserFromDatabase()
            } else {
                showLogInLayout()
            }
        }
    }

    private fun checkUserFromDatabase() {

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                        FirebaseInstanceId.getInstance().instanceId
                                .addOnFailureListener {
                                    Toast.makeText(this@StartActivity, "[Get TOKEN]" + it.message, Toast.LENGTH_SHORT).show()

                                    Log.d(TAG, "onPermissionGranted: "+it.message)
                                }.addOnCompleteListener {
                                    Log.d(TAG, "onPermissionGranted: "+it.result.token)


                                    val user: FirebaseUser?
                                    user = FirebaseAuth.getInstance().currentUser
                                    if (user != null) {
                                        //dialog!!.show()
                                        Paper.book().write(Common.REMEMBER_FBID,user.uid)
                                        showRegisterLayout(user, it)
                                    } else {
//                            startActivity(Intent(this@StartActivity, MainActivity::class.java))
//                            finish()
                                    }
                                }


                    }

                    override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {
                        Toast.makeText(this@StartActivity, "Permission Required to Use this App", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(permissionRequest: PermissionRequest, permissionToken: PermissionToken) {}
                }).check()


    }

    private fun showRegisterLayout(user: FirebaseUser, task: Task<InstanceIdResult>) {
        Paper.book().write(Common.REMEMBER_FBID, user.uid)

        compositeDisposable.add(myRestaurantAPI!!.updateTokenToServer(
                Common.API_KEY,
                user.uid, task.result.token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    if (!it.isSuccess)
                        Toast.makeText(this, "[Update Token Error]" + it.message, Toast.LENGTH_SHORT).show()

                    compositeDisposable.add(myRestaurantAPI!!.getUser(Common.API_KEY, user.uid)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ userModel: UserModel ->
                                if (userModel.isSuccess) {

                                    Log.d(TAG, "onPermissionGranted: "+task.result.token)


                                    Toast.makeText(this@StartActivity, "Success" + userModel.result[0].name, Toast.LENGTH_SHORT).show()
                                    Common.currentUser = userModel.result[0]
                                    val intent = Intent(this@StartActivity, HomeActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this@StartActivity, "Success: Not registered", Toast.LENGTH_SHORT).show()
                                    alertDialogFragment.show(fm, "fragment_edit_name")
                                    alertDialogFragment.isCancelable = false
                                }
                                dialog!!.dismiss()
                            }
                            ) { throwable: Throwable ->
                                dialog!!.dismiss()
                                Toast.makeText(this@StartActivity, "[GET USER API] " + throwable.message, Toast.LENGTH_LONG).show()
                                Log.d(TAG, "onPermissionGranted: " + throwable.message)
                            })
                }, {

                    Toast.makeText(this, "[Update Token]" + it.message, Toast.LENGTH_SHORT).show()

                }))


    }

    private fun showLogInLayout() {


        val authMethodPickerLayout =
                AuthMethodPickerLayout.Builder(R.layout.login_methods_firebase_ui)
                        .setPhoneButtonId(R.id.sign_with_phone)
                        .setGoogleButtonId(R.id.sign_with_google)
                        .build();
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAuthMethodPickerLayout(authMethodPickerLayout)
                        .setTheme(R.style.LogInTheme)
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .build()
                , LOGIN_REQUEST_CODE
        )

    }

    override fun onStop() {
        if (firebaseAuth != null && listner != null) firebaseAuth.removeAuthStateListener(listner)
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOGIN_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
            } else {
                Toast.makeText(this, "Error:\n" + response!!.error, Toast.LENGTH_SHORT).show()
            }
        }
    }
}