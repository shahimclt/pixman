package com.shahim.pixman.fragment

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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

    lateinit var ogImage : Bitmap

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
//        patient.setImageURI(imageUri)

        val ims = activity?.contentResolver?.openInputStream(imageUri)
        ogImage = BitmapFactory.decodeStream(ims)
        context?.let {
            val rtImage = rotateViaMatrix(ogImage,getOrientation(it,imageUri))
            rtImage?.let { ogImage = rtImage }
        }
        ims?.close()

        patient.setImageBitmap(ogImage)

        tool_flip_h.setOnClickListener { flipH() }

        tool_text_add.setOnClickListener { addTextLogo() }
    }

    private fun flipH() {

    }

    private fun addTextLogo() {

    }

    fun getOrientation(context: Context, photoUri: Uri): Int {
        val cursor: Cursor? = context.contentResolver.query(photoUri,
            arrayOf(MediaStore.Images.ImageColumns.ORIENTATION),
            null,
            null,
            null
        )
        if (cursor?.count != 1) {
            return -1
        }
        cursor.moveToFirst()
        val or = cursor.getInt(0)
        cursor.close()
        return or
    }

    private fun rotateViaMatrix(original: Bitmap, orientation: Int): Bitmap? {
        val matrix = Matrix()
        matrix.setRotate(orientation.toFloat())
        return Bitmap.createBitmap(
            original, 0, 0, original.width,
            original.height, matrix, true
        )
    }
}


