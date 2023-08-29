package com.mba.tmalcher.fiapandroid.firebase

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mba.tmalcher.fiapandroid.model.Product

class Upload {
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun productWithImage(name: String, id: Int, imageUri: Uri, onSuccess: () -> Unit, onFailure: (String) -> Unit) {

        val storageRef = storage.reference

        val imageName = name.replace(" ", "_")
        var spaceRef = storageRef.child("images/$imageName" + ".jpg")
        val uploadTask = spaceRef.putFile(imageUri)

        uploadTask.addOnSuccessListener {
            spaceRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                val product = Product(
                    id = id,
                    name = name,
                    imageUrl = imageUrl
                )

                db.collection("products")
                    .add(product)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onFailure("Erro ao salvar o produto")
                    }

            }
        }.addOnFailureListener {
            onFailure("Erro ao fazer upload da imagem")
        }
    }
}