package com.mba.tmalcher.fiapandroid.firebase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mba.tmalcher.fiapandroid.model.Product

class Upload {
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun productWithImage(name: String, id: Int, imageUri: Uri, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val storageRef = storage.reference

        val imageName = name.replace(" ", "_")
        val spaceRef = storageRef.child("users/$userId/images/$imageName.jpg")
        val uploadTask = spaceRef.putFile(imageUri)

        uploadTask.addOnSuccessListener {
            spaceRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                val product = Product(
                    id = id,
                    name = name,
                    imageUrl = imageUrl
                )

                db.collection("users")
                    .document(userId)
                    .collection("products")
                    .add(product)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { _ ->
                        onFailure("Erro ao salvar o produto")
                    }

            }
        }.addOnFailureListener {
            onFailure("Erro ao fazer upload da imagem")
        }
    }
}