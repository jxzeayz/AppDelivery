package com.example.uinavegacion.data.local.order

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface OrderDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: OrderEntity): Long
    
    @Update
    suspend fun update(order: OrderEntity)
    
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getByUserId(userId: Long): List<OrderEntity>
    
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    suspend fun getAll(): List<OrderEntity>
    
    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): OrderEntity?
    
    @Query("SELECT * FROM orders WHERE status = :status ORDER BY createdAt DESC")
    suspend fun getByStatus(status: String): List<OrderEntity>
}
