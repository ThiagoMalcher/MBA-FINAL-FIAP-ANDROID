package com.mba.tmalcher.fiapandroid.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mba.tmalcher.fiapandroid.MainActivity
import com.mba.tmalcher.fiapandroid.R
import com.mba.tmalcher.fiapandroid.adapter.ProductAdapter
import com.mba.tmalcher.fiapandroid.model.Product
import com.mba.tmalcher.fiapandroid.utils.SwipeToDeleteUpdateCallback
class ProductList : AppCompatActivity() {

    private val products = mutableListOf<Product>()
    private lateinit var productAdapter: ProductAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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

    private fun initProducts() {
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
}