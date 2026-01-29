package com.example.uinavegacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.uinavegacion.data.local.database.AppDatabase
import com.example.uinavegacion.data.repository.UserRepository
import com.example.uinavegacion.navigation.AppNavGraph
import com.example.uinavegacion.ui.viewmodel.AuthViewModel
import com.example.uinavegacion.ui.viewmodel.AuthViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}


/*
* En Compose, Surface es un contenedor visual que viene de Material 3.Crea un bloque
*  que puedes personalizar con color, forma, sombra (elevación).
Sirve para aplicar un fondo (color, borde, elevación, forma) siguiendo las guías de diseño
* de Material.
Piensa en él como una “lona base” sobre la cual vas a pintar tu UI.
* Si cambias el tema a dark mode, colorScheme.background
* cambia automáticamente y el Surface pinta la pantalla con el nuevo color.
* */
@Composable // Indica que esta función dibuja UI
fun AppRoot() { // Raíz de la app para separar responsabilidades (se conserva)
    // ====== NUEVO: construcción de dependencias (Composition Root) ======
    val context = LocalContext.current.applicationContext
    // ^ Obtenemos el applicationContext para construir la base de datos de Room.

    val db = AppDatabase.getInstance(context)
    
    // DAOs
    val userDao = db.userDao()
    val productDao = db.productDao()
    val cartDao = db.cartDao()
    val orderDao = db.orderDao()
    val orderItemDao = db.orderItemDao()
    
    // Repositorios
    val userRepository = UserRepository(userDao)
    val productRepository = com.example.uinavegacion.data.repository.ProductRepository(productDao)
    val cartRepository = com.example.uinavegacion.data.repository.CartRepository(cartDao, productDao)
    val orderRepository = com.example.uinavegacion.data.repository.OrderRepository(orderDao, orderItemDao, cartDao, productDao)
    
    // ViewModels
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(userRepository)
    )

    // ====== TU NAVEGACIÓN ORIGINAL ======
    val navController = rememberNavController() // Controlador de navegación (igual que antes)
    MaterialTheme { // Provee colores/tipografías Material 3 (igual que antes)
        Surface(color = MaterialTheme.colorScheme.background) { // Fondo general (igual que antes)

            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel,
                userRepository = userRepository,
                productRepository = productRepository,
                cartRepository = cartRepository,
                orderRepository = orderRepository
            )
        }
    }
}