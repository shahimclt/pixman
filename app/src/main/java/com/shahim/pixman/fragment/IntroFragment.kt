package com.shahim.pixman.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.CompositePermissionListener
import com.karumi.dexter.listener.single.PermissionListener
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener
import com.shahim.pixman.R
import kotlinx.android.synthetic.main.fragment_intro.*


const val PICK_IMAGE = 2

class IntroFragment : Fragment() {

    lateinit var onImagePickedListener: OnImagePickedListener

    interface OnImagePickedListener {
        fun onImagePicked(image: Uri)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_intro, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        image_picker_prompt.setOnClickListener { openFile() }
    }

    private fun openFile() {
        checkFilePermission {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
                val mimeTypes = arrayOf("image/jpeg", "image/png")
                putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
            startActivityForResult(intent, PICK_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) when (requestCode) {
            PICK_IMAGE -> {
                //data.getData returns the content URI for the selected Image
                val selectedImage: Uri? = data?.data
                onImagePickedListener.let { selectedImage?.let { it1 -> onImagePickedListener.onImagePicked(it1) } }
            }
        }
    }

    private fun checkFilePermission(onGrant : () -> Unit) {

        val baseLis: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted(response: PermissionGrantedResponse) {
                onGrant()
            }
            override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest,token: PermissionToken) {
                context?.let {
                    AlertDialog.Builder(it)
                        .setTitle(R.string.permission_rat_storage_title)
                        .setMessage(R.string.permission_rat_storage_message_import)
                        .setNegativeButton(R.string.dialog_button_cancel) { dialog, _ ->
                            dialog.dismiss()
                            token.cancelPermissionRequest()
                        }
                        .setPositiveButton(R.string.dialog_button_ok){ dialog, _ ->
                            dialog.dismiss()
                            token.continuePermissionRequest()
                        }
                        .setOnDismissListener { token.cancelPermissionRequest() }
                        .show()
                }
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
            }
        }

        val snackBarLis: PermissionListener = SnackbarOnDeniedPermissionListener.Builder.with(
            activity?.findViewById(R.id.container) as ViewGroup,
            R.string.permission_rat_storage_denied_import)
            .withDuration(5000)
            .build()

        Dexter.withActivity(activity)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(CompositePermissionListener(baseLis,snackBarLis)).check()
    }
}


