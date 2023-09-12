package com.mba.tmalcher.fiapandroid.activities

import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.JPEG
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Handler
import android.provider.MediaStore
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy.ALL
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mba.tmalcher.fiapandroid.R
import com.mba.tmalcher.fiapandroid.firebase.Delete
import com.mba.tmalcher.fiapandroid.firebase.Read
import com.mba.tmalcher.fiapandroid.firebase.Upload
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditProduct : AppCompatActivity() {

    private lateinit var mProductName: TextView
    private lateinit var mSaveProduct: Button
    private lateinit var mTakePhoto: Button
    private lateinit var mSelectPhoto: Button
    private lateinit var mImageView: ImageView
    private lateinit var imageUri: Uri
    private val REQUEST_IMAGE_GALLERY = 0
    private val REQUEST_IMAGE = 1
    private lateinit var currentPhotoPath: String
    private lateinit var progressDialog: ProgressDialog
    private var productName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_product)

        productName = intent.getStringExtra("productName")

        mProductName = findViewById(R.id.inputProductName)
        mSaveProduct = findViewById(R.id.buttonSave)
        mSelectPhoto = findViewById(R.id.buttonChoosePhoto)
        mImageView = findViewById(R.id.imageView)
        mTakePhoto = findViewById(R.id.buttonTakePhoto)

        mImageView.setImageResource(R.drawable.product)

        mProductName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(mProductName.windowToken, 0)
                true
            } else {
                false
            }
        }

        mSelectPhoto.setOnClickListener {
            openGallery()
        }

        mTakePhoto.setOnClickListener {
            takePhoto()
        }

        mSaveProduct.setOnClickListener {
            if (mProductName.text.toString().isNotEmpty()) {
                showProgressDialog()
                Upload().productWithImage(mProductName.text.toString(), imageUri,
                    onSuccess = {
                        Toast.makeText(applicationContext, getString(R.string.app_product_update), LENGTH_SHORT).show()
                        Delete().product(productName.toString())
                        goToProductList()
                    },
                    onFailure = {
                        Toast.makeText(applicationContext, getString(R.string.app_product_update_error), LENGTH_SHORT).show()
                        progressDialog.dismiss()
                    })
            } else {
                Toast.makeText(
                    applicationContext, getString(R.string.msg_fields),
                    LENGTH_SHORT
                ).show()
            }
        }

        Read().retrieveProductBy(productName.toString()) { product ->
            if (product != null) {
                mProductName.text = product.name

                Glide.with(this)
                    .load(product.imageUrl)
                    .diskCacheStrategy(ALL)
                    .into(mImageView)

                Glide.with(this)
                    .asBitmap()
                    .load(product.imageUrl)
                    .into(object: CustomTarget<Bitmap>() {
                        override fun onLoadCleared(placeholder: Drawable?) { }

                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            val fileName = "${product.name}.jpg"

                            val file = File(getExternalFilesDir(DIRECTORY_PICTURES), fileName)

                            try {
                                val outputStream = FileOutputStream(file)
                                resource.compress(JPEG, 100, outputStream)
                                outputStream.flush()
                                outputStream.close()

                                imageUri = Uri.fromFile(file)

                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    })
            } else {
                Toast.makeText(
                    applicationContext, getString(R.string.app_product_load_error),
                    LENGTH_SHORT
                ).show()
                goToProductList()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
    }

    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }
            // Continue only if the File was successfully created
            photoFile?.also {
                imageUri = FileProvider.getUriForFile(
                    this,
                    "com.mba.tmalcher.fiapandroid.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE)
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)
        val imageFileName = "JPEG_${timeStamp}_"
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        currentPhotoPath = image.absolutePath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            imageUri = data.data!!
            val contentResolver: ContentResolver = this.contentResolver
            try {
                val inputStream = contentResolver.openInputStream(imageUri)
                mImageView.setImageBitmap(BitmapFactory.decodeStream(inputStream))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            try {
                val inputStream = contentResolver.openInputStream(imageUri)
                mImageView.setImageBitmap(BitmapFactory.decodeStream(inputStream))
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
    private fun goToProductList() {
        val handler = Handler()
        val delay = 5000L // 5000 milissegundos = 5 segundos

        handler.postDelayed({
            progressDialog.dismiss()
            val intent = Intent(this, ProductList::class.java)
            startActivity(intent)
            finish()
        }, delay)
    }
    private fun showProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.app_product_saving))
        progressDialog.setCancelable(false)
        progressDialog.show()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}