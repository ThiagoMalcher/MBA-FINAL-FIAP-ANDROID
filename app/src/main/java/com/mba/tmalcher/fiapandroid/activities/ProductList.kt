package com.mba.tmalcher.fiapandroid.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mba.tmalcher.fiapandroid.MainActivity
import com.mba.tmalcher.fiapandroid.R
import com.mba.tmalcher.fiapandroid.adapter.ProductAdapter
import com.mba.tmalcher.fiapandroid.firebase.Login
import com.mba.tmalcher.fiapandroid.firebase.Read
import com.mba.tmalcher.fiapandroid.model.Product
import com.mba.tmalcher.fiapandroid.utils.SwipeToDeleteUpdateCallback

class ProductList : AppCompatActivity() {

    private val products = mutableListOf<Product>()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        productAdapter = ProductAdapter(this, products)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = productAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val addButton: FloatingActionButton = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            val intent = Intent(this, RegisterProduct::class.java)
            startActivity(intent)
        }

        val swipeCallback = SwipeToDeleteUpdateCallback(productAdapter)
        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        initProducts()
    }

    override fun onBackPressed() {
        showLogoutConfirmationDialog()
    }

    private fun initProducts() {
        Read().retrieveAllProducts { productsRetrieved ->
            if (productsRetrieved.isNotEmpty()) {
                products.clear()

                for (product in productsRetrieved) {
                    products.add(product)
                    productAdapter.notifyItemInserted(products.size - 1)
                }
            } else {
                println("Nenhum produto encontrado para o usuário.")
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.logout))
        builder.setMessage(getString(R.string.logout_message))

        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }

        builder.setPositiveButton(getString(R.string.confirm)) { _, _ ->
            Login().logout()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        val dialog = builder.create()
        dialog.show()
    }
}