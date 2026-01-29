package com.example.uinavegacion.data.local.order

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.uinavegacion.data.local.product.ProductEntity

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    
    val orderId: Long,                   // ID del pedido
    val productId: Long,                 // ID del producto
    val productName: String,            // Nombre del producto (snapshot)
    val productPrice: Double,           // Precio del producto (snapshot)
    val quantity: Int,                  // Cantidad
    val subtotal: Double                // Subtotal (precio * cantidad)
)
