package com.mba.tmalcher.fiapandroid.utils

import android.content.Intent
import android.graphics.Canvas
import android.view.View.*
import android.widget.ImageView
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.mba.tmalcher.fiapandroid.R
import com.mba.tmalcher.fiapandroid.activities.EditProduct
import com.mba.tmalcher.fiapandroid.adapter.ProductActionListener
import com.mba.tmalcher.fiapandroid.adapter.ProductAdapter
import com.mba.tmalcher.fiapandroid.firebase.Delete

class SwipeToDeleteUpdateCallback(private val adapter: ProductAdapter) :
    SimpleCallback(0, LEFT or RIGHT),
    ProductActionListener {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val index = viewHolder.bindingAdapterPosition

        when(direction) {
            LEFT  -> this.onRemoveProductClickBy(index)
            RIGHT -> this.onEditProductClickBy(index)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView
        val deleteIcon = itemView.findViewById<ImageView>(R.id.deleteIcon)
        val updateIcon = itemView.findViewById<ImageView>(R.id.updateIcon)

        if (dX < 0) { // Swipe LEFT
            deleteIcon.visibility = VISIBLE
            updateIcon.visibility = GONE
        } else if (dX > 0) { // Swipe RIGHT
            deleteIcon.visibility = GONE
            updateIcon.visibility = VISIBLE
        } else { // No action
            deleteIcon.visibility = GONE
            updateIcon.visibility = GONE
        }
    }

    override fun onEditProductClickBy(index: Int) {
        val productName = adapter.getProductIdBy(index)
        val intent = Intent(adapter.getContext(), EditProduct::class.java)
        intent.putExtra("productName", productName)
        adapter.getContext().startActivity(intent)
    }

    override fun onRemoveProductClickBy(index: Int) {
        val product = adapter.getProductBy(index)
        Delete().product(product.imageName)
        adapter.removeProductBy(index)
    }
}
