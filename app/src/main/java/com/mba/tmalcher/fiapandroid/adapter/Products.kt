package com.mba.tmalcher.fiapandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mba.tmalcher.fiapandroid.R
import com.mba.tmalcher.fiapandroid.firebase.Delete
import com.mba.tmalcher.fiapandroid.model.Product

class Products(private val products: MutableList<Product>, private val listener: ProductListener) : RecyclerView.Adapter<Products.ProductViewHolder>() {

    interface ProductListener {
        fun onRemoveProductClick(product: Product)
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        val removeButton: ImageView = itemView.findViewById(R.id.removeButton)
        val imageView:ImageView = itemView.findViewById(R.id.productImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct = products[position]
        holder.productNameTextView.text = currentProduct.name
        Glide.with(holder.imageView)
            .load(currentProduct.imageUrl)
            .into(holder.imageView)


        holder.removeButton.setOnClickListener {
            //Delete product on firebase
            Delete().product(currentProduct.name)
            listener.onRemoveProductClick(currentProduct)
        }
    }

    override fun getItemCount() = products.size
}