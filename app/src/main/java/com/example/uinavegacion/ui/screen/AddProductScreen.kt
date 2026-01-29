package com.example.uinavegacion.ui.screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.uinavegacion.data.local.product.ProductEntity
import com.example.uinavegacion.ui.screen.createTempImageFile
import com.example.uinavegacion.ui.screen.getImageUriForFile
import com.example.uinavegacion.ui.viewmodel.ProductViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    productId: Long? = null,
    productViewModel: ProductViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val categories = listOf("Comida", "Bebida", "Acompañamiento", "Postre")
    
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var available by remember { mutableStateOf(true) }
    var photoUriString by remember { mutableStateOf<String?>(null) }
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }
    
    var nameError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }

    // Cargar producto si es edición
    LaunchedEffect(productId) {
        productId?.let { id ->
            val product = productViewModel.state.value.products.find { it.id == id }
            product?.let {
                name = it.name
                description = it.description
                price = it.price.toString()
                selectedCategory = it.category
                available = it.available
                photoUriString = it.imageUri
            }
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUriString = pendingCaptureUri?.toString()
            Toast.makeText(context, "Foto tomada correctamente", Toast.LENGTH_SHORT).show()
        } else {
            pendingCaptureUri = null
            Toast.makeText(context, "No se tomó ninguna foto", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text(if (productId != null) "Editar Producto" else "Agregar Producto", fontWeight = FontWeight.Bold) },
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
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagen del producto
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUriString != null) {
                        AsyncImage(
                            model = Uri.parse(photoUriString),
                            contentDescription = "Imagen del producto",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Filled.Image,
                            contentDescription = "Sin imagen",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    FloatingActionButton(
                        onClick = {
                            val file = createTempImageFile(context)
                            val uri = getImageUriForFile(context, file)
                            pendingCaptureUri = uri
                            takePictureLauncher.launch(uri)
                        },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                    ) {
                        Icon(Icons.Filled.Camera, contentDescription = "Tomar foto")
                    }
                }
            }

            // Campos del formulario
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = if (it.isBlank()) "El nombre es obligatorio" else null
                },
                label = { Text("Nombre del producto") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError != null,
                singleLine = true
            )
            if (nameError != null) {
                Text(
                    text = nameError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    descriptionError = if (it.isBlank()) "La descripción es obligatoria" else null
                },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                isError = descriptionError != null,
                singleLine = false,
                minLines = 3
            )
            if (descriptionError != null) {
                Text(
                    text = descriptionError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            OutlinedTextField(
                value = price,
                onValueChange = {
                    price = it.filter { char -> char.isDigit() || char == '.' }
                    priceError = if (price.isBlank()) {
                        "El precio es obligatorio"
                    } else if (price.toDoubleOrNull() == null || price.toDouble() <= 0) {
                        "El precio debe ser un número válido mayor a 0"
                    } else {
                        null
                    }
                },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth(),
                isError = priceError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                prefix = { Text("$") }
            )
            if (priceError != null) {
                Text(
                    text = priceError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            // Selector de categoría
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Switch de disponibilidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Disponible")
                Switch(
                    checked = available,
                    onCheckedChange = { available = it }
                )
            }

            // Botón de guardar
            Button(
                onClick = {
                    var hasErrors = false
                    if (name.isBlank()) {
                        nameError = "El nombre es obligatorio"
                        hasErrors = true
                    }
                    if (description.isBlank()) {
                        descriptionError = "La descripción es obligatoria"
                        hasErrors = true
                    }
                    if (price.isBlank() || price.toDoubleOrNull() == null || price.toDouble() <= 0) {
                        priceError = "El precio debe ser un número válido mayor a 0"
                        hasErrors = true
                    }
                    if (!hasErrors) {
                        val product = ProductEntity(
                            id = productId ?: 0L,
                            name = name.trim(),
                            description = description.trim(),
                            price = price.toDouble(),
                            category = selectedCategory,
                            available = available,
                            imageUri = photoUriString
                        )
                        if (productId != null) {
                            productViewModel.updateProduct(product)
                        } else {
                            productViewModel.addProduct(product)
                        }
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (productId != null) "Actualizar Producto" else "Agregar Producto")
            }
        }
    }
}
