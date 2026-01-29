package com.example.uinavegacion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.uinavegacion.data.repository.ProductRepository
import com.example.uinavegacion.data.local.product.ProductEntity

data class ProductUiState(
    val products: List<ProductEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: String? = null
)

class ProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(ProductUiState())
    val state: StateFlow<ProductUiState> = _state.asStateFlow()
    
    init {
        loadProducts()
    }
    
    fun loadProducts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val products = repository.getAllAvailable()
                _state.value = _state.value.copy(
                    products = products,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al cargar productos"
                )
            }
        }
    }
    
    fun filterByCategory(category: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, selectedCategory = category)
            try {
                val products = if (category != null) {
                    repository.getByCategory(category)
                } else {
                    repository.getAllAvailable()
                }
                _state.value = _state.value.copy(
                    products = products,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al filtrar productos"
                )
            }
        }
    }
    
    fun addProduct(product: ProductEntity) {
        viewModelScope.launch {
            try {
                repository.insert(product)
                loadProducts()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Error al agregar producto"
                )
            }
        }
    }
    
    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            try {
                repository.update(product)
                loadProducts()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Error al actualizar producto"
                )
            }
        }
    }
    
    fun deleteProduct(id: Long) {
        viewModelScope.launch {
            try {
                repository.delete(id)
                loadProducts()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Error al eliminar producto"
                )
            }
        }
    }
}
