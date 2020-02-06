package com.shahim.pixman

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.shahim.pixman.fragment.IntroFragment

import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), IntroFragment.OnImagePickedListener {

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
        showIntro()
    }

    private fun showIntro() {
        supportFragmentManager.beginTransaction().add(R.id.container, IntroFragment()).commit()
    }

    override fun onImagePicked(image: Uri) {
        showEditor()
    }

    private fun showEditor() {
        
    }

    override fun onAttachFragment(fragment: Fragment) {
        if(fragment is IntroFragment) {
            fragment.setOnImagePickedListener(this)
        }
    }
}

