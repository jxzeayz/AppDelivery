package com.example.uinavegacion.data.repository

import com.example.uinavegacion.data.local.order.OrderDao
import com.example.uinavegacion.data.local.order.OrderEntity
import com.example.uinavegacion.data.local.order.OrderItemDao
import com.example.uinavegacion.data.local.order.OrderItemEntity
import com.example.uinavegacion.data.local.cart.CartDao
import com.example.uinavegacion.data.local.product.ProductDao

class OrderRepository(
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao,
    private val cartDao: CartDao,
    private val productDao: ProductDao
) {
    suspend fun createOrder(
        userId: Long,
        deliveryAddress: String,
        phone: String,
        notes: String? = null
    ): Result<Long> {
        return try {
            // Obtener items del carrito
            val cartItems = cartDao.getByUserId(userId)
            if (cartItems.isEmpty()) {
                return Result.failure(IllegalStateException("El carrito está vacío"))
            }
            
            // Calcular total
            var total = 0.0
            val orderItems = mutableListOf<OrderItemEntity>()
            
            cartItems.forEach { cartItem ->
                val product = productDao.getById(cartItem.productId)
                product?.let {
                    val subtotal = it.price * cartItem.quantity
                    total += subtotal
                    orderItems.add(
                        OrderItemEntity(
                            orderId = 0, // Se actualizará después
                            productId = it.id,
                            productName = it.name,
                            productPrice = it.price,
                            quantity = cartItem.quantity,
                            subtotal = subtotal
                        )
                    )
                }
            }
            
            // Crear pedido
            val order = OrderEntity(
                userId = userId,
                total = total,
                status = "Pendiente",
                deliveryAddress = deliveryAddress,
                phone = phone,
                notes = notes
            )
            
            val orderId = orderDao.insert(order)
            
            // Actualizar orderId en los items y insertarlos
            val itemsWithOrderId = orderItems.map { it.copy(orderId = orderId) }
            orderItemDao.insertAll(itemsWithOrderId)
            
            // Limpiar carrito
            cartDao.clearCart(userId)
            
            Result.success(orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getOrdersByUser(userId: Long) = orderDao.getByUserId(userId)
    suspend fun getAllOrders() = orderDao.getAll()
    suspend fun getOrderById(id: Long) = orderDao.getById(id)
    suspend fun getOrdersByStatus(status: String) = orderDao.getByStatus(status)
    suspend fun updateOrderStatus(orderId: Long, status: String): Result<Unit> {
        return try {
            val order = orderDao.getById(orderId)
            order?.let {
                val updated = it.copy(status = status)
                if (status == "Entregado") {
                    orderDao.update(updated.copy(deliveredAt = System.currentTimeMillis()))
                } else {
                    orderDao.update(updated)
                }
                Result.success(Unit)
            } ?: Result.failure(IllegalStateException("Pedido no encontrado"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getOrderItems(orderId: Long) = orderItemDao.getByOrderId(orderId)
}
