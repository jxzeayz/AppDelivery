package com.example.uinavegacion.ui.screen

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//funcion para crear el file que se va a guardar en cache
fun createTempImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = File(context.cacheDir, "images").apply {
        if(!exists()) mkdirs()
    }
    return File(storageDir, "IMG_${timeStamp}.jpg")
}
//funcion para obtener la uri del archivo guardado
fun getImageUriForFile(context: Context, file: File): Uri {
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context,authority,file)
}

@Composable
fun HomeScreen(
    isLoggedIn: Boolean,
    isAdmin: Boolean,
    onGoCatalog: () -> Unit,
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    onGoAdmin: () -> Unit
) {
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            if (isAdmin) {
                onGoAdmin()
            } else {
                onGoCatalog()
            }
        }
    }

    val bg = MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Delivery App",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )
            
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Bienvenido",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Inicia sesión para comenzar a pedir",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onGoLogin,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Iniciar Sesión")
                }
                OutlinedButton(
                    onClick = onGoRegister,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrarse")
                }
            }
        }
    }
}
