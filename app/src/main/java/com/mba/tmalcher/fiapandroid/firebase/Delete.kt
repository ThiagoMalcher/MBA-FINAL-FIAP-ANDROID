package com.mba.tmalcher.fiapandroid.firebase

import com.google.firebase.firestore.FirebaseFirestore

class Delete {

    private val db = FirebaseFirestore.getInstance()

    fun product(productName:String) {

        val query = db.collection("products")
            .whereEqualTo("name", productName)
        query.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    db.collection("products")
                        .document(document.id)
                        .delete()
                        .addOnSuccessListener {

                        }
                        .addOnFailureListener { exception ->

                        }
                }
            }
            .addOnFailureListener { exception ->

            }
    }
}
