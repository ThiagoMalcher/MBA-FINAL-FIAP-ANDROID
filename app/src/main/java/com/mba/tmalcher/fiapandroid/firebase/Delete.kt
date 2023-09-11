package com.mba.tmalcher.fiapandroid.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class Delete {
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun product(productName: String) {
        val userId = auth.currentUser?.uid ?: return
        val storageRef = storage.reference
        val imageRef = storageRef.child("users/$userId/images/$productName.jpg")

        imageRef.delete()
            .addOnSuccessListener {
                val query =  db.collection("users")
                    .document(userId)
                    .collection("products")
                    .whereEqualTo("name", productName)

                query.get()
                    .addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot) {
                            db.collection("users")
                                .document(userId)
                                .collection("products")
                                .document(document.id)
                                .delete()
                                .addOnSuccessListener { }
                                .addOnFailureListener { _ -> }
                        }
                    }
                    .addOnFailureListener { _ -> }
            }
            .addOnFailureListener { _ -> }
    }
}
