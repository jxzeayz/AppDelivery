package com.example.uinavegacion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.uinavegacion.data.repository.OrderRepository
import com.example.uinavegacion.data.local.order.OrderEntity

data class OrderUiState(
    val orders: List<OrderEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class OrderViewModel(
    private val repository: OrderRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(OrderUiState())
    val state: StateFlow<OrderUiState> = _state.asStateFlow()
    
    fun loadOrders(userId: Long, isAdmin: Boolean = false) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val orders = if (isAdmin) {
                    repository.getAllOrders()
                } else {
                    repository.getOrdersByUser(userId)
                }
                _state.value = _state.value.copy(
                    orders = orders,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al cargar pedidos"
                )
            }
        }
    }
    
    fun createOrder(
        userId: Long,
        deliveryAddress: String,
        phone: String,
        notes: String? = null
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, success = false)
            try {
                val result = repository.createOrder(userId, deliveryAddress, phone, notes)
                if (result.isSuccess) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        success = true
                    )
                    loadOrders(userId)
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Error al crear pedido"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al crear pedido"
                )
            }
        }
    }
    
    fun updateOrderStatus(orderId: Long, status: String) {
        viewModelScope.launch {
            try {
                val result = repository.updateOrderStatus(orderId, status)
                if (result.isSuccess) {
                    // Recargar pedidos
                    _state.value = _state.value.copy(success = true)
                } else {
                    _state.value = _state.value.copy(
                        error = result.exceptionOrNull()?.message ?: "Error al actualizar estado"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Error al actualizar estado"
                )
            }
        }
    }
    
    fun clearSuccess() {
        _state.value = _state.value.copy(success = false)
    }
}
