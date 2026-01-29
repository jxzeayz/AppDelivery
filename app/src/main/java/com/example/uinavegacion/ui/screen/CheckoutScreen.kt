package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uinavegacion.ui.viewmodel.CartViewModel
import com.example.uinavegacion.ui.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    userId: Long,
    cartViewModel: CartViewModel,
    orderViewModel: OrderViewModel,
    onBack: () -> Unit,
    onOrderPlaced: () -> Unit
) {
    val cartState by cartViewModel.state.collectAsStateWithLifecycle()
    val orderState by orderViewModel.state.collectAsStateWithLifecycle()
    
    var deliveryAddress by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var addressError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        cartViewModel.loadCart(userId)
    }

    LaunchedEffect(orderState.success) {
        if (orderState.success) {
            orderViewModel.clearSuccess()
            onOrderPlaced()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Checkout", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Resumen del pedido
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Resumen del Pedido",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    cartState.items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.product.name} x${item.quantity}")
                            Text("$${String.format("%.0f", item.subtotal)}")
                        }
                    }
                    Divider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total:",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$${String.format("%.0f", cartState.total)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Formulario de entrega
            Text(
                text = "Información de Entrega",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = deliveryAddress,
                onValueChange = {
                    deliveryAddress = it
                    addressError = if (it.isBlank()) "La dirección es obligatoria" else null
                },
                label = { Text("Dirección de entrega") },
                modifier = Modifier.fillMaxWidth(),
                isError = addressError != null,
                singleLine = false,
                minLines = 2
            )
            if (addressError != null) {
                Text(
                    text = addressError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it.filter { char -> char.isDigit() }
                    phoneError = if (phone.isBlank()) {
                        "El teléfono es obligatorio"
                    } else if (phone.length < 8) {
                        "El teléfono debe tener al menos 8 dígitos"
                    } else {
                        null
                    }
                },
                label = { Text("Teléfono de contacto") },
                modifier = Modifier.fillMaxWidth(),
                isError = phoneError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )
            if (phoneError != null) {
                Text(
                    text = phoneError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notas adicionales (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 3
            )

            // Botón de confirmar pedido
            Button(
                onClick = {
                    var hasErrors = false
                    if (deliveryAddress.isBlank()) {
                        addressError = "La dirección es obligatoria"
                        hasErrors = true
                    }
                    if (phone.isBlank()) {
                        phoneError = "El teléfono es obligatorio"
                        hasErrors = true
                    }
                    if (!hasErrors) {
                        orderViewModel.createOrder(
                            userId = userId,
                            deliveryAddress = deliveryAddress,
                            phone = phone,
                            notes = notes.ifBlank { null }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !orderState.isLoading && cartState.items.isNotEmpty()
            ) {
                if (orderState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Procesando...")
                } else {
                    Text("Confirmar Pedido")
                }
            }

            orderState.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}
