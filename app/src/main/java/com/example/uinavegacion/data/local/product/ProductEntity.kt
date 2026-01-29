package com.example.uinavegacion.data.local.product

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    
    val name: String,                    // Nombre del producto
    val description: String,              // Descripción
    val price: Double,                   // Precio
    val imageUri: String? = null,        // URI de la imagen (puede ser null)
    val category: String,                // Categoría (ej: "Comida", "Bebida", "Postre")
    val available: Boolean = true,       // Disponibilidad
    val createdAt: Long = System.currentTimeMillis() // Timestamp de creación
)
