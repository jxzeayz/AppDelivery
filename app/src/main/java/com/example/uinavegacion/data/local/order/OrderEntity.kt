package com.example.uinavegacion.data.local.order

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    
    val userId: Long,                    // ID del usuario que hizo el pedido
    val total: Double,                   // Total del pedido
    val status: String,                  // Estado: "Pendiente", "En preparación", "En camino", "Entregado", "Cancelado"
    val deliveryAddress: String,        // Dirección de entrega
    val phone: String,                   // Teléfono de contacto
    val notes: String? = null,           // Notas adicionales
    val createdAt: Long = System.currentTimeMillis(), // Fecha de creación
    val deliveredAt: Long? = null        // Fecha de entrega (null si no entregado)
)
