package com.mba.tmalcher.fiapandroid.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.mba.tmalcher.fiapandroid.R
import com.mba.tmalcher.fiapandroid.firebase.Upload
import com.mba.tmalcher.fiapandroid.model.Product
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class RegisterProduct : AppCompatActivity(){

    private lateinit var mProductName: TextView
    private lateinit var mSaveProduct: Button
    private lateinit var mTakePhoto: Button
    private lateinit var mSelectPhoto: Button
    private lateinit var mImageView: ImageView
    private lateinit var imageUri: Uri
    private val REQUEST_IMAGE_GALLERY = 0
    private val REQUEST_IMAGE = 1
    private lateinit var currentPhotoPath: String
    private val products = mutableListOf<Product>()
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

        mProductName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(mProductName.windowToken, 0)
                true
            } else {
                false
            }
        }

        mSelectPhoto.setOnClickListener(View.OnClickListener {
            openGallery()
        })

        mTakePhoto.setOnClickListener(View.OnClickListener {
            takePhoto()
        })

        mSaveProduct.setOnClickListener(View.OnClickListener {
            if(mProductName.text.toString().isNotEmpty() && isDefaultChanged) {
                showProgressDialog()
                mProductName.text.toString()
                val newProductId = products.size + 1
                regProd(mProductName.text.toString(), newProductId, imageUri)
            } else {
                Toast.makeText(applicationContext, getString(R.string.msg_fields),
                    Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
    }

    fun takePhoto() {
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

    fun regProd(productName:String, prodId:Int, imageUri:Uri) {
        Upload().productWithImage(productName, prodId, imageUri!!,
            onSuccess = {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, getString(R.string.app_product_update),
                    Toast.LENGTH_SHORT).show()
                goToProductList()
            },
            onFailure = { _ ->
                Toast.makeText(applicationContext, getString(R.string.app_product_update_error),
                    Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
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
                isDefaultChanged = true;
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            try {
                val inputStream = contentResolver.openInputStream(imageUri)
                mImageView.setImageBitmap(BitmapFactory.decodeStream(inputStream))
                isDefaultChanged = true;
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
    fun goToProductList() {
        val intent = Intent(this, ProductList::class.java)
        startActivity(intent)
        finish()
    }
    private fun showProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Salvando...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}