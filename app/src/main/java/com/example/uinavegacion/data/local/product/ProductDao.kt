package com.example.uinavegacion.data.local.product

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity): Long
    
    @Update
    suspend fun update(product: ProductEntity)
    
    @Query("SELECT * FROM products WHERE available = 1 ORDER BY name ASC")
    suspend fun getAllAvailable(): List<ProductEntity>
    
    @Query("SELECT * FROM products ORDER BY name ASC")
    suspend fun getAll(): List<ProductEntity>
    
    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ProductEntity?
    
    @Query("SELECT * FROM products WHERE category = :category AND available = 1 ORDER BY name ASC")
    suspend fun getByCategory(category: String): List<ProductEntity>
    
    @Query("DELETE FROM products WHERE id = :id")
    suspend fun delete(id: Long)
    
    @Query("SELECT COUNT(*) FROM products")
    suspend fun count(): Int
}
