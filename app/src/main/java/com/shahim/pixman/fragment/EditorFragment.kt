package com.shahim.pixman.fragment

import android.R.attr.button
import android.content.Context
import android.content.res.ColorStateList
import android.database.Cursor
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
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
    lateinit var workImage : Bitmap

    var undoStack : ArrayList<Bitmap> = ArrayList()
    var redoStack : ArrayList<Bitmap> = ArrayList()

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
        loadOriginalImage()
        patient.setImageBitmap(ogImage)

        tool_flip_h.setOnClickListener { flipH() }
        tool_flip_v.setOnClickListener { flipV() }

        tool_text_add.setOnClickListener { addTextLogo() }

        tool_opacity_50.setOnClickListener { reduceAlpha() }

        history_undo.setOnClickListener { undo() }
        history_redo.setOnClickListener { redo() }

        action_compare.setOnTouchListener(OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    toggleOG(true)
                    return@OnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    toggleOG(false)
                    return@OnTouchListener true
                }
            }
            false
        })
    }

    private fun toggleOG(og:Boolean) {
        if(og) patient.setImageBitmap(ogImage) else updateWorkImage()
    }

    private fun flipH() {
        addToUndoStack()
        val matrix = Matrix()
        matrix.postScale(-1f, 1f, workImage.width/2f, workImage.height/2f)
        workImage = Bitmap.createBitmap(workImage,0,0,workImage.width,workImage.height,matrix,true)
        updateWorkImage()
    }

    private fun flipV() {
        addToUndoStack()
        val matrix = Matrix()
        matrix.postScale(1f, -1f, workImage.width/2f, workImage.height/2f)
        workImage = Bitmap.createBitmap(workImage,0,0,workImage.width,workImage.height,matrix,true)
        updateWorkImage()
    }

    private fun reduceAlpha() {
        addToUndoStack()

        val mutableBitmap: Bitmap = if (workImage.isMutable) workImage else workImage.copy(
            Bitmap.Config.ARGB_8888,
            true
        )
        val canvas = Canvas(mutableBitmap)
        val colour = 80 and 0xFF shl 24
        canvas.drawColor(colour, PorterDuff.Mode.DST_IN)
        workImage = mutableBitmap
        updateWorkImage()
    }

    private fun addTextLogo() {
        addToUndoStack()
        val padding = workImage.width/20

        //Add Image
        var logo = BitmapFactory.decodeResource(resources, R.drawable.im_gg_logo)

        val logoW = workImage.width.toFloat() / 20 * 5
        val logoH = logo.height.toFloat() / logo.width * logoW

        logo = Bitmap.createScaledBitmap(logo,logoW.toInt(),logoH.toInt(),false)

        val canvas = Canvas(workImage)
        val x = workImage.width - padding - logo.width
        val y = workImage.height - padding - logo.height
        canvas.drawBitmap(logo, x.toFloat(), y.toFloat(), Paint())

        //Add Text
        val paint = Paint()
        paint.style = Paint.Style.FILL
        context?.let { paint.color =  ContextCompat.getColor(it,R.color.black) }

        val textSize = workImage.width / 20
        paint.textSize = textSize.toFloat()
        val xt = padding
        val yt = workImage.height - padding - ((paint.descent())/2)
        canvas.drawText("GreedyGame", xt.toFloat(), yt, paint)

        updateWorkImage()
    }

    private fun loadOriginalImage() {
        val ims = activity?.contentResolver?.openInputStream(imageUri)
        ogImage = BitmapFactory.decodeStream(ims)
        context?.let {
            val rtImage = rotateViaMatrix(ogImage,getOrientation(it,imageUri))
            rtImage?.let { ogImage = rtImage }
        }
        ims?.close()
        workImage = ogImage.copy(ogImage.config,true)
    }

    private fun updateWorkImage() {
        patient.setImageBitmap(workImage)
    }

    private fun addToUndoStack(clearRedo: Boolean = true) {
        undoStack.add(workImage.copy(workImage.config,true))
        if(undoStack.size>3) undoStack.removeAt(0)
        if(clearRedo) redoStack.clear()
        refreshHistoryButton(history_undo,undoStack)
        refreshHistoryButton(history_redo,redoStack)
    }

    private fun undo() {
        if(undoStack.size>0) {
            addToRedoStack()
            workImage = undoStack.removeAt(undoStack.size-1)
        }
        refreshHistoryButton(history_undo,undoStack)
        updateWorkImage()
    }

    private fun refreshHistoryButton(btn: ImageButton, stack: ArrayList<Bitmap>) {
        context?.let {
            if(stack.size==0) {
                btn.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(it,R.color.textColorDisabled))
                btn.isClickable = false
            }
            else {
                btn.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(it,R.color.textColorPrimary))
                btn.isClickable = true
            }
        }
    }

    private fun addToRedoStack() {
        redoStack.add(workImage.copy(workImage.config,true))
        if(redoStack.size>3) redoStack.removeAt(0)
        refreshHistoryButton(history_redo,redoStack)
    }

    private fun redo() {
        if(redoStack.size>0) {
            addToUndoStack(false)
            workImage = redoStack.removeAt(redoStack.size-1)
        }
        refreshHistoryButton(history_undo,undoStack)
        refreshHistoryButton(history_redo,redoStack)
        updateWorkImage()
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


