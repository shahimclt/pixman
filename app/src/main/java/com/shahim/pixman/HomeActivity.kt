package com.shahim.pixman

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shahim.pixman.fragment.IntroFragment

import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    companion object {
        fun craftIntent(c: Context): Intent {
            val list = Intent(c,HomeActivity::class.java)
            return list
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(app_bar)
        init()
    }

    private fun init() {
        supportFragmentManager.beginTransaction().add(R.id.container, IntroFragment()).commit()
    }
}

