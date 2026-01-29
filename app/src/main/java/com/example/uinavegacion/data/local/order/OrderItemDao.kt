package com.example.uinavegacion.data.local.order

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OrderItemDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: OrderItemEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<OrderItemEntity>)
    
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getByOrderId(orderId: Long): List<OrderItemEntity>
}
