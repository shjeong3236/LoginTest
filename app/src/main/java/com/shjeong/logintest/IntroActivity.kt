package com.shjeong.logintest

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_intro.*
import java.util.*

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        Glide
            .with(this)
            .load(R.drawable.intro)
            .crossFade().centerCrop().bitmapTransform(CropCircleTransformation(this@IntroActivity))
            .into(intro)

        introTimer()

    }

    private fun introTimer() {

        val mTimer = Timer()
        mTimer.schedule(

            object : TimerTask() {
                override fun run() {
                    val i = Intent(this@IntroActivity, LoginActivity::class.java)
                    startActivity(i)

                    finish()
                }
            }

            , 500

        )

    }

}
