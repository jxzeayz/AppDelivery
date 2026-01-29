@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uinavegacion.data.local.order.OrderEntity
import com.example.uinavegacion.ui.viewmodel.OrderViewModel
import com.example.uinavegacion.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    productViewModel: ProductViewModel,
    orderViewModel: OrderViewModel,
    onBack: () -> Unit,
    onAddProduct: () -> Unit,
    onEditProduct: (Long) -> Unit
) {
    val productState by productViewModel.state.collectAsStateWithLifecycle()
    val orderState by orderViewModel.state.collectAsStateWithLifecycle()
    
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        productViewModel.loadProducts()
        orderViewModel.loadOrders(0, isAdmin = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Panel de Administración", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                if (selectedTab == 0) {
                    IconButton(onClick = onAddProduct) {
                        Icon(Icons.Filled.Add, contentDescription = "Agregar Producto")
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        // Tabs
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Productos") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Pedidos") }
            )
        }

        // Contenido de las tabs
        when (selectedTab) {
            0 -> ProductsAdminTab(
                products = productState.products,
                onEdit = onEditProduct,
                onDelete = { productViewModel.deleteProduct(it) }
            )
            1 -> OrdersAdminTab(
                orders = orderState.orders,
                onStatusUpdate = { orderId, status ->
                    orderViewModel.updateOrderStatus(orderId, status)
                }
            )
        }
    }
}

@Composable
fun ProductsAdminTab(
    products: List<com.example.uinavegacion.data.local.product.ProductEntity>,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products, key = { it.id }) { product ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$${String.format("%.0f", product.price)}",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Categoría: ${product.category}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { onEdit(product.id) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = { onDelete(product.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrdersAdminTab(
    orders: List<OrderEntity>,
    onStatusUpdate: (Long, String) -> Unit
) {
    val statusOptions = listOf("Pendiente", "En preparación", "En camino", "Entregado", "Cancelado")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(orders, key = { it.id }) { order ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Pedido #${order.id}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        AssistChip(
                            onClick = {},
                            label = { Text(order.status) }
                        )
                    }
                    Text("Total: $${String.format("%.0f", order.total)}")
                    Text("Dirección: ${order.deliveryAddress}")
                    Text("Teléfono: ${order.phone}")
                    
                    // Selector de estado
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = order.status,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Cambiar estado") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            statusOptions.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status) },
                                    onClick = {
                                        onStatusUpdate(order.id, status)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
