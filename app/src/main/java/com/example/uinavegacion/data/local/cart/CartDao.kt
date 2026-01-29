package com.example.uinavegacion.data.local.cart

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update

@Dao
interface CartDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartItemEntity): Long
    
    @Update
    suspend fun update(item: CartItemEntity)
    
    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    suspend fun getByUserId(userId: Long): List<CartItemEntity>
    
    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId LIMIT 1")
    suspend fun getByUserAndProduct(userId: Long, productId: Long): CartItemEntity?
    
    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: Long)
    
    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun delete(id: Long)
    
    @Query("SELECT COUNT(*) FROM cart_items WHERE userId = :userId")
    suspend fun getItemCount(userId: Long): Int
    
    @Query("SELECT SUM(quantity) FROM cart_items WHERE userId = :userId")
    suspend fun getTotalQuantity(userId: Long): Int?
}
