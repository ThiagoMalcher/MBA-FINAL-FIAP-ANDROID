package com.mba.tmalcher.fiapandroid.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mba.tmalcher.fiapandroid.model.Product

class Read {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun retrieveProductBy(productName: String, onProductRetrieved: (Product?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val query =  db.collection("users")
            .document(userId)
            .collection("products")
            .whereEqualTo("imageName", productName)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                val product: Product? = querySnapshot
                    .firstOrNull()
                    ?.let { document ->
                        val itemData = document.data
                        val id = document.id
                        val name = itemData["name"] as String
                        val imageUrl = itemData["imageUrl"] as String
                        val imageName = itemData["imageName"] as String

                        Product(id, name, imageUrl, imageName)
                    }

                onProductRetrieved(product)
            }
            .addOnFailureListener { exception ->
                Log.e("retrieveProductBy","Erro ao recuperar produto: ${exception.message}")
                onProductRetrieved(null)
            }
    }

    fun retrieveAllProducts(onProductsRetrieved: (List<Product>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        val products = mutableListOf<Product>()

        val query = db.collection("users")
            .document(userId)
            .collection("products")

        query.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val itemData = document.data
                    val id = document.id
                    val name = itemData["name"] as String
                    val imageUrl = itemData["imageUrl"] as String
                    val imageName = itemData["imageName"] as String

                    val product = Product(id, name, imageUrl, imageName)
                    products.add(product)
                }

                onProductsRetrieved(products)
            }
            .addOnFailureListener { exception ->
                Log.e("retrieveAllProducts","Erro ao recuperar produtos: ${exception.message}")
                onProductsRetrieved(emptyList())
            }
    }
}