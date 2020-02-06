package com.shahim.pixman.fragment

import android.R.attr.name
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.shahim.pixman.R
import kotlinx.android.synthetic.main.fragment_editor.*


class EditorFragment : Fragment() {

    private lateinit var callback: OnImageFinishedListener

    fun OnImageFinishedListener(callback: OnImageFinishedListener) {
        this.callback = callback
    }
    interface OnImageFinishedListener {
        fun onImageFinished(image: Uri)
    }

    companion object {
        fun newInstance(image: Uri): EditorFragment {
            val bundle = Bundle()
            bundle.putString("image_uri", image.toString())
            val fragment = EditorFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private fun readBundle(bundle: Bundle?) {
        if (bundle != null) {
            val im = bundle.getString("image_uri")
            imageUri = Uri.parse(im)
        }
    }

    lateinit var imageUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        readBundle(arguments)
        return inflater.inflate(R.layout.fragment_editor, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        patient.setImageURI(imageUri)
    }
}


