package com.mba.tmalcher.fiapandroid.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.mba.tmalcher.fiapandroid.R
import com.mba.tmalcher.fiapandroid.adapter.Products
import com.mba.tmalcher.fiapandroid.firebase.Upload
import com.mba.tmalcher.fiapandroid.model.Product

class ProductList : AppCompatActivity(), Products.ProductListener {

    private val products = mutableListOf<Product>()
    private lateinit var productAdapter: Products
    val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        productAdapter = Products(products, this)
        recyclerView.adapter = productAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        getProductsAndSetList()
        val addButton: Button = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            val intent = Intent(this, RegisterProduct::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onRemoveProductClick(product: Product) {
        val position = products.indexOf(product)
        if (position != -1) {
            products.removeAt(position)
            productAdapter.notifyItemRemoved(position)
        }
    }

    fun getProductsAndSetList() {
        db.collection("products")
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


}