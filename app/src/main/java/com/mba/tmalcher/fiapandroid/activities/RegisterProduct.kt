package com.mba.tmalcher.fiapandroid.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.mba.tmalcher.fiapandroid.R
import com.mba.tmalcher.fiapandroid.firebase.Upload
import com.mba.tmalcher.fiapandroid.utils.InputHelper
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RegisterProduct : AppCompatActivity() {

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
    private var isDefaultChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_product)

        mProductName = findViewById(R.id.inputProductName)
        mSaveProduct = findViewById(R.id.buttonSave)
        mSelectPhoto = findViewById(R.id.buttonChoosePhoto)
        mImageView = findViewById(R.id.imageView)
        mTakePhoto = findViewById(R.id.buttonTakePhoto)

        mImageView.setImageResource(R.drawable.product)

        InputHelper(this).closeKeyboardOnDone(mProductName)

        mSelectPhoto.setOnClickListener {
            openGallery()
        }

        mTakePhoto.setOnClickListener {
            takePhoto()
        }

        mSaveProduct.setOnClickListener {
            save()
        }
    }

    private fun save() {
        if (mProductName.text.toString().isEmpty() || !isDefaultChanged) {
            Toast.makeText(applicationContext, getString(R.string.msg_fields), LENGTH_SHORT).show()
            return
        }

        showProgressDialog()

        Upload().productWithImage(mProductName.text.toString(), imageUri,
            onSuccess = {
                progressDialog.dismiss()
                Toast.makeText(
                    applicationContext,
                    getString(R.string.app_product_update),
                    LENGTH_SHORT
                ).show()
                goToProductList()
            },
            onFailure = {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.app_product_update_error),
                    LENGTH_SHORT
                ).show()
                progressDialog.dismiss()
            }
        )
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
        val storageDir: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val imageFileName = "JPEG_${timeStamp}_"
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        currentPhotoPath = image.absolutePath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data!!
            val contentResolver: ContentResolver = this.contentResolver
            try {
                val inputStream = contentResolver.openInputStream(imageUri)
                mImageView.setImageBitmap(BitmapFactory.decodeStream(inputStream))
                isDefaultChanged = true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            try {
                val inputStream = contentResolver.openInputStream(imageUri)
                mImageView.setImageBitmap(BitmapFactory.decodeStream(inputStream))
                isDefaultChanged = true
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    private fun goToProductList() {
        val intent = Intent(this, ProductList::class.java)
        startActivity(intent)
        finish()
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