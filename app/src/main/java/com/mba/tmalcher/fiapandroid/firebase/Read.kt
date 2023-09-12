package com.mba.tmalcher.fiapandroid.firebase

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
                var product: Product? = null

                for (document in querySnapshot) {
                    val id = document.id
                    val name = document.getString("name")
                    val imageUrl = document.getString("imageUrl")
                    val imageName = document.getString("imageName")

                    println("$id - $name - $imageUrl - $imageName")
                    if (name != null && imageUrl != null && imageName != null) {
                        product = Product(id, name, imageUrl, imageName)
                        break
                    }
                }

                onProductRetrieved(product)
            }
            .addOnFailureListener { _ ->
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
                println("Erro ao recuperar produtos: $exception")
                onProductsRetrieved(emptyList()) // Tratar o erro conforme necessário
            }
    }
}