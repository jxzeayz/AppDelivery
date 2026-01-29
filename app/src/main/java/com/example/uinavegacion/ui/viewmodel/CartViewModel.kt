package com.example.uinavegacion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.uinavegacion.data.repository.CartRepository
import com.example.uinavegacion.data.repository.ProductRepository
import com.example.uinavegacion.data.local.cart.CartItemEntity
import com.example.uinavegacion.data.local.product.ProductEntity

data class CartItemUi(
    val id: Long,
    val product: ProductEntity,
    val quantity: Int,
    val subtotal: Double
)

data class CartUiState(
    val items: List<CartItemUi> = emptyList(),
    val total: Double = 0.0,
    val itemCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class CartViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(CartUiState())
    val state: StateFlow<CartUiState> = _state.asStateFlow()
    
    fun loadCart(userId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val cartItems = cartRepository.getCartItems(userId)
                val itemsWithProducts = mutableListOf<CartItemUi>()
                var total = 0.0
                
                cartItems.forEach { cartItem ->
                    val product = productRepository.getById(cartItem.productId)
                    product?.let {
                        val subtotal = it.price * cartItem.quantity
                        total += subtotal
                        itemsWithProducts.add(
                            CartItemUi(
                                id = cartItem.id,
                                product = it,
                                quantity = cartItem.quantity,
                                subtotal = subtotal
                            )
                        )
                    }
                }
                
                val itemCount = cartRepository.getTotalQuantity(userId)
                
                _state.value = _state.value.copy(
                    items = itemsWithProducts,
                    total = total,
                    itemCount = itemCount,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al cargar el carrito"
                )
            }
        }
    }
    
    fun addToCart(userId: Long, productId: Long, quantity: Int = 1) {
        viewModelScope.launch {
            try {
                cartRepository.addToCart(userId, productId, quantity)
                loadCart(userId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Error al agregar al carrito"
                )
            }
        }
    }
    
    fun updateQuantity(userId: Long, productId: Long, quantity: Int) {
        viewModelScope.launch {
            try {
                cartRepository.updateQuantity(userId, productId, quantity)
                loadCart(userId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Error al actualizar cantidad"
                )
            }
        }
    }
    
    fun removeFromCart(userId: Long, itemId: Long) {
        viewModelScope.launch {
            try {
                cartRepository.removeFromCart(itemId)
                loadCart(userId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Error al eliminar del carrito"
                )
            }
        }
    }
    
    fun clearCart(userId: Long) {
        viewModelScope.launch {
            try {
                cartRepository.clearCart(userId)
                loadCart(userId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Error al vaciar el carrito"
                )
            }
        }
    }
}
