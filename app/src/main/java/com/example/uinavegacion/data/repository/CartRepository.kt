package com.example.uinavegacion.data.repository

import com.example.uinavegacion.data.local.cart.CartDao
import com.example.uinavegacion.data.local.cart.CartItemEntity
import com.example.uinavegacion.data.local.product.ProductDao

class CartRepository(
    private val cartDao: CartDao,
    private val productDao: ProductDao
) {
    suspend fun getCartItems(userId: Long) = cartDao.getByUserId(userId)
    
    suspend fun addToCart(userId: Long, productId: Long, quantity: Int = 1): Result<Long> {
        return try {
            val existing = cartDao.getByUserAndProduct(userId, productId)
            if (existing != null) {
                // Si ya existe, actualizamos la cantidad
                val updated = existing.copy(quantity = existing.quantity + quantity)
                cartDao.update(updated)
                Result.success(existing.id)
            } else {
                // Si no existe, creamos nuevo item
                val newItem = CartItemEntity(
                    userId = userId,
                    productId = productId,
                    quantity = quantity
                )
                val id = cartDao.insert(newItem)
                Result.success(id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateQuantity(userId: Long, productId: Long, quantity: Int): Result<Unit> {
        return try {
            val existing = cartDao.getByUserAndProduct(userId, productId)
            if (existing != null) {
                if (quantity <= 0) {
                    cartDao.delete(existing.id)
                } else {
                    val updated = existing.copy(quantity = quantity)
                    cartDao.update(updated)
                }
                Result.success(Unit)
            } else {
                Result.failure(IllegalStateException("Item no encontrado en el carrito"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun removeFromCart(itemId: Long) {
        cartDao.delete(itemId)
    }
    
    suspend fun clearCart(userId: Long) {
        cartDao.clearCart(userId)
    }
    
    suspend fun getItemCount(userId: Long) = cartDao.getItemCount(userId)
    
    suspend fun getTotalQuantity(userId: Long) = cartDao.getTotalQuantity(userId) ?: 0
    
    suspend fun getCartTotal(userId: Long): Double {
        val items = cartDao.getByUserId(userId)
        var total = 0.0
        items.forEach { item ->
            val product = productDao.getById(item.productId)
            product?.let {
                total += it.price * item.quantity
            }
        }
        return total
    }
}
