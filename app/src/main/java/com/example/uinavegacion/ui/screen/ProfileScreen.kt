package com.example.uinavegacion.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uinavegacion.domain.validation.validateNameLettersOnly
import com.example.uinavegacion.domain.validation.validatePhoneDigitsOnly
import com.example.uinavegacion.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: Long,
    profileViewModel: ProfileViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val state by profileViewModel.state.collectAsStateWithLifecycle()
    var isEditing by remember { mutableStateOf(false) }
    
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        profileViewModel.loadUser(userId)
    }

    LaunchedEffect(state.user) {
        state.user?.let {
            name = it.name
            phone = it.phone
            address = it.address ?: ""
        }
    }

    LaunchedEffect(state.success) {
        if (state.success) {
            isEditing = false
            profileViewModel.clearSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                if (!isEditing) {
                    IconButton(onClick = { isEditing = true }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar")
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "Usuario",
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Información del usuario
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                nameError = validateNameLettersOnly(it)
                            },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditing,
                            isError = nameError != null,
                            singleLine = true
                        )
                        if (nameError != null && isEditing) {
                            Text(
                                text = nameError!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        OutlinedTextField(
                            value = state.user?.email ?: "",
                            onValueChange = {},
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = {
                                phone = it.filter { char -> char.isDigit() }
                                phoneError = validatePhoneDigitsOnly(phone)
                            },
                            label = { Text("Teléfono") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditing,
                            isError = phoneError != null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true
                        )
                        if (phoneError != null && isEditing) {
                            Text(
                                text = phoneError!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Dirección (opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditing,
                            singleLine = false,
                            minLines = 2
                        )

                        if (isEditing) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        isEditing = false
                                        state.user?.let {
                                            name = it.name
                                            phone = it.phone
                                            address = it.address ?: ""
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Cancelar")
                                }
                                Button(
                                    onClick = {
                                        if (nameError == null && phoneError == null) {
                                            profileViewModel.updateProfile(
                                                userId,
                                                name.trim(),
                                                phone.trim(),
                                                address.trim().ifBlank { null }
                                            )
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    enabled = name.isNotBlank() && phone.isNotBlank()
                                ) {
                                    Text("Guardar")
                                }
                            }
                        }
                    }
                }

                // Botón de cerrar sesión
                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text("Cerrar Sesión", color = MaterialTheme.colorScheme.onErrorContainer)
                }

                // Mensaje de éxito
                AnimatedVisibility(
                    visible = state.success,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Text(
                            text = "✓ Perfil actualizado correctamente",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }

                state.error?.let { error ->
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
}
