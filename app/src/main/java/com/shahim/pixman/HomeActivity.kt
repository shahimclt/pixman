package com.shahim.pixman

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import com.shahim.pixman.fragment.EditorFragment
import com.shahim.pixman.fragment.IntroFragment
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity(), IntroFragment.OnImagePickedListener, EditorFragment.OnImageFinishedListener {

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
        supportFragmentManager.beginTransaction().replace(R.id.container, EditorFragment.newInstance(image)).addToBackStack(null).commit()
    }

    override fun onImageFinished(image: Uri) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        showIntro()
        Snackbar.make(container,R.string.editor_save_success,Snackbar.LENGTH_LONG)
            .setAction(R.string.editor_save_view_prompt) {
                val openFile = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(image, "image/*")
                try {
                    startActivity(openFile)
                } catch (e: ActivityNotFoundException) {
                    Log.i("View", "Cannot open file.")
                }
            }
            .show()
    }

    override fun onAttachFragment(fragment: Fragment) {
        if(fragment is IntroFragment) {
            fragment.onImagePickedListener = this
        }
        else if(fragment is EditorFragment) {
            fragment.onImageFinishedListener = this
        }
    }
}

