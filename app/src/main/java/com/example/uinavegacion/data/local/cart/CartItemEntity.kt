package com.example.uinavegacion.data.local.cart

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.uinavegacion.data.local.product.ProductEntity

@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    
    val userId: Long,                    // ID del usuario que tiene el carrito
    val productId: Long,                 // ID del producto
    val quantity: Int,                   // Cantidad
    val addedAt: Long = System.currentTimeMillis() // Timestamp
)
