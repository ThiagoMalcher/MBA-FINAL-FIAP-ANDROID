package com.mba.tmalcher.fiapandroid.activities

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mba.tmalcher.fiapandroid.R
import com.mba.tmalcher.fiapandroid.adapter.Products
import com.mba.tmalcher.fiapandroid.model.Product

class ProductList : AppCompatActivity(), Products.ProductListener {

    private val products = mutableListOf<Product>()
    private lateinit var productAdapter: Products

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        productAdapter = Products(products, this)
        recyclerView.adapter = productAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val addButton: Button = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            val newProductId = products.size + 1
            val newProduct = Product(newProductId, "Product $newProductId", "URL_DA_IMAGEM")
            products.add(newProduct)
            productAdapter.notifyItemInserted(products.size - 1)
        }
    }

    override fun onRemoveProductClick(product: Product) {
        val position = products.indexOf(product)
        if (position != -1) {
            products.removeAt(position)
            productAdapter.notifyItemRemoved(position)
        }
    }
}