package com.example.uinavegacion.data.repository

import com.example.uinavegacion.data.local.product.ProductDao
import com.example.uinavegacion.data.local.product.ProductEntity

class ProductRepository(
    private val productDao: ProductDao
) {
    suspend fun getAllAvailable() = productDao.getAllAvailable()
    suspend fun getAll() = productDao.getAll()
    suspend fun getById(id: Long) = productDao.getById(id)
    suspend fun getByCategory(category: String) = productDao.getByCategory(category)
    suspend fun insert(product: ProductEntity) = productDao.insert(product)
    suspend fun update(product: ProductEntity) = productDao.update(product)
    suspend fun delete(id: Long) = productDao.delete(id)
}
