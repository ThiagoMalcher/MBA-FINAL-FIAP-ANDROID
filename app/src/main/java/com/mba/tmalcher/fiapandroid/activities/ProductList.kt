package com.mba.tmalcher.fiapandroid.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mba.tmalcher.fiapandroid.MainActivity
import com.mba.tmalcher.fiapandroid.R
import com.mba.tmalcher.fiapandroid.adapter.Products
import com.mba.tmalcher.fiapandroid.model.Product
import java.io.Serializable

class ProductList : AppCompatActivity(), Products.ProductListener {

    companion object {
        private const val EDIT_PRODUCT_REQUEST = 1
    }

    private val products = mutableListOf<Product>()
    private lateinit var productAdapter: Products
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var position: Serializable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        productAdapter = Products(products, this)


        recyclerView.adapter = productAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        getProductsAndSetList()

        val addButton: FloatingActionButton = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            val intent = Intent(this, RegisterProduct::class.java)
            startActivity(intent)
        }

    }

    override fun onRemoveProductClick(product: Product) {
        val position = products.indexOf(product)
        if (position != -1) {
            products.removeAt(position)
            productAdapter.notifyItemRemoved(position)
        }
    }

    override fun onEditProductClick(name: String, index: Int) {
        val intent = Intent(this, EditProduct::class.java)
        intent.putExtra("productName", name)
        startActivityForResult(intent, EDIT_PRODUCT_REQUEST)
    }

    private fun getProductsAndSetList() {
        val userId = auth.currentUser?.uid ?: return

        val handler = Handler()

        // Adicione um atraso de 5 segundos (5000 milissegundos)
        val delayMillis = 5000L // 5000 milissegundos = 5 segundos
        handler.postDelayed({
            db.collection("users")
                .document(userId)
                .collection("products")
                .get()
                .addOnSuccessListener { documents ->
                    products.clear()
                    for (document in documents) {
                        val itemData = document.data
                        val newProduct = Product(null, itemData["name"].toString(), itemData["imageUrl"].toString(), itemData["imageName"].toString())
                        products.add(newProduct)
                    }
                    productAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    println("Erro ao recuperar itens: $exception")
                }
        }, delayMillis)
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.logout))
        builder.setMessage(getString(R.string.lougout_message))

        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }

        builder.setPositiveButton(getString(R.string.confirm)) { _, _ ->
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onBackPressed() {
        showLogoutConfirmationDialog()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == EDIT_PRODUCT_REQUEST && resultCode == RESULT_OK) {
            products.clear()
            productAdapter.notifyDataSetChanged()
            getProductsAndSetList()
        }
    }


}