package com.mba.tmalcher.fiapandroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mba.tmalcher.fiapandroid.R
import com.mba.tmalcher.fiapandroid.model.Product

class ProductAdapter(private val context: Context, private val products: MutableList<Product>)
    : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
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
    }

    override fun getItemCount() = products.size

    fun getProductIdBy(index: Int): String {
        return products[index].imageName
    }

    fun getProductBy(index: Int): Product {
        return products[index]
    }

    fun removeProductBy(index: Int) {
        if (index != -1) {
            products.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun getContext(): Context {
        return context
    }
}