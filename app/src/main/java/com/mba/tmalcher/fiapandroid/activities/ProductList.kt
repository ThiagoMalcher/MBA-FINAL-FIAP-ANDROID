package com.mba.tmalcher.fiapandroid.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
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

class ProductList : AppCompatActivity(), Products.ProductListener {

    private val products = mutableListOf<Product>()
    private lateinit var productAdapter: Products
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

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

    private fun getProductsAndSetList() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("products")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val itemData = document.data
                    val newProductId = products.size + 1
                    val newProduct = Product(newProductId, document.data.get("name").toString(), document.data.get("imageUrl").toString())
                    products.add(newProduct)
                    productAdapter.notifyItemInserted(products.size - 1)
                }
            }
            .addOnFailureListener { exception ->
                println("Erro ao recuperar itens: $exception")
            }
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Tem certeza de que deseja sair da aplicação?")

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.setPositiveButton("Confirmar") { _, _ ->
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

}