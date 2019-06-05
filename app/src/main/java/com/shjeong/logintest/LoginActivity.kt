package com.shjeong.logintest

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeResponseCallback
import com.kakao.usermgmt.response.model.UserProfile
import com.kakao.util.exception.KakaoException
import com.kakao.util.helper.log.Logger
import kotlinx.android.synthetic.main.activity_login.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private var callback: SessionCallback? = null
    private var mFacebookCallbackManager: CallbackManager? = null

    private var mGoogleApiClient : GoogleApiClient? = null


    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val intent = intent

//        Log.d("login!@# action : " , intent.action)
//        Log.d("login!@#  data : " , intent.dataString)



        getHashKey()

        // kakao loin
        kakao_init()

        // facebook login
        facebook_init()

        // google login
        google_init()

    }

    fun google_init() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, R.string.google_auth, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

//        googleSignInClient = GoogleSignIn.getClient(this, gso)
//
//        // Initialize Firebase Auth
//        auth = FirebaseAuth.getInstance()

        Google_Login.setOnClickListener {
            signIn()
        }
    }

    fun facebook_init() {
        AppEventsLogger.activateApp(this)
        facebook_login.setReadPermissions("email")
        facebookLogin()
    }

    fun kakao_init() {
        callback = SessionCallback()
        Session.getCurrentSession().addCallback(callback)
        requestMe()
    }

    private fun getHashKey() {
        try {                                                        // 패키지이름을 입력해줍니다.
            val info = packageManager.getPackageInfo("com.shjeong.logintest", PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("login!@#", "key_hash=" + Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

    }


    private fun requestMe() {
        //유저의 정보를 받아오는 함수
        UserManagement.requestMe(object : MeResponseCallback() {
            override fun onFailure(errorResult: ErrorResult?) {
                Log.d("login!@#", "error message=" + errorResult!!)
                //                super.onFailure(errorResult);
            }

            override fun onSessionClosed(errorResult: ErrorResult) {
                Log.d("login!@#", "onSessionClosed1 =$errorResult")
            }

            override fun onNotSignedUp() {
                //카카오톡 회원이 아닐시
                Log.d("login!@#", "onNotSignedUp ")
            }

            override fun onSuccess(result: UserProfile) {
                Log.d("login!@# user", result.toString())
                Log.d("login!@# user", result.nickname.toString() + "")

                val id = result.nickname

                Toast.makeText(this@LoginActivity, "카카오톡 로그인에 성공했습니다.", Toast.LENGTH_SHORT).show()

                val i = Intent(this@LoginActivity, MainActivity::class.java)
                i.putExtra("id", id)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
                finish()
            }
        })
    }

    inner class SessionCallback : ISessionCallback {
        override fun onSessionOpened() {
            UserManagement.requestMe(object : MeResponseCallback() {
                override fun onSuccess(result: UserProfile?) {
                    Log.d("login!@# onSession", result!!.toString())
                    Log.d("login!@# onSession", result.nickname.toString() + "")

                    val id = result.nickname

                    Toast.makeText(this@LoginActivity, "카카오톡 로그인에 성공했습니다.", Toast.LENGTH_SHORT).show()

                    val i = Intent(this@LoginActivity, MainActivity::class.java)
                    i.putExtra("id", id)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
                    finish()
                }

                override fun onSessionClosed(errorResult: ErrorResult?) {
                    Log.d("login!@# onSession", "onSessionClosed")
                }

                override fun onNotSignedUp() {
                    Log.d("login!@# onSession", "onNotSignedUp")
                }

            })
        }

        override fun onSessionOpenFailed(exception: KakaoException?) {
            if (exception != null) {
                Logger.e(exception)
            }
        }

    }


    private fun facebookLogin() {

        mFacebookCallbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(mFacebookCallbackManager!!,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {

                    // App code
                    Log.d("login!@#", "onSucces LoginResult=$loginResult")

                    val id = Profile.getCurrentProfile().id

                    Log.d("login!@#", "id : $id")

                    Toast.makeText(this@LoginActivity, "페이스북 로그인에 성공했습니다.", Toast.LENGTH_SHORT).show()

                    requestUserProfile(loginResult)
                }

                override fun onCancel() {
                    // App code
                    Log.d("login!@#", "onCancel")
                }

                override fun onError(exception: FacebookException) {
                    // App code
                    Log.d("login!@#", "onError")
                }
            })


    }

    fun requestUserProfile(loginResult: LoginResult) {

        val request: GraphRequest
        request = GraphRequest.newMeRequest(loginResult.accessToken) { user, _ ->
            try {
                loginResult.accessToken.userId
                val email = user.getString("email")
                val name = user.getString("name")

                Log.d("login!@# requestUser", loginResult.accessToken.userId + ", " + email)

                val i = Intent(this@LoginActivity, MainActivity::class.java)
                i.putExtra("id", email)
                startActivity(i)
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            setResult(RESULT_OK)
        }

        val parameters = Bundle()
        parameters.putString("fields", "id,name,email,gender,birthday")
        request.parameters = parameters
        request.executeAsync()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("login!@#", "onActivityResult, requestCode : $requestCode")

        mFacebookCallbackManager!!.onActivityResult(requestCode, resultCode, data)
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            Log.d("login!@#", "handleActivityResult 가 작동됨")
            return
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.d("login!@#", "requestCode == RC_SIGN_IN")
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            Log.d("login!@#", "result.isSuccess : ${result.isSuccess}")
            try {
                if (result.isSuccess) {
                    val account = result.signInAccount
                    firebaseAuthWithGoogle(account!!)
                } else {
                    Log.d("login!@#", "result.status : ${result.status}" )
                    Toast.makeText(this@LoginActivity, "구글 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.d("login!@#", "Google sign in failed", e)
                // ...
            }
        }

    }

    /*********************************************************
     * 구글 로그인
     *********************************************************/

    public override fun onStart() {
        super.onStart()
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        Toast.makeText(this@LoginActivity, "구글 로그인에 성공했습니다", Toast.LENGTH_SHORT).show()

        val i = Intent(this@LoginActivity, MainActivity::class.java)
        i.putExtra("id", acct.email)
        startActivity(i)

        finish()

//        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//
//                    val user = auth.currentUser
//                    Log.d("login!@#", "googlelogin user : $user")
//
//                    Toast.makeText(this@LoginActivity, "구글 로그인에 성공했습니다", Toast.LENGTH_SHORT).show()
//
//                    val i = Intent(this@LoginActivity, MainActivity::class.java)
//                    i.putExtra("id", acct.email)
//                    startActivity(i)
//
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.d("login!@#", "signInWithCredential:failure", task.exception)
//                    Toast.makeText(this@LoginActivity, "인증 실패", Toast.LENGTH_SHORT).show()
//                }
//
//            }
    }


    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut() {
        // Firebase sign out
        auth.signOut()

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
        }
    }



    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}



