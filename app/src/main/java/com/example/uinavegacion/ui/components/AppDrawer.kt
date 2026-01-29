package com.example.uinavegacion.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon // Ícono en ítem del drawer
import androidx.compose.material3.NavigationDrawerItem // Ítem seleccionable
import androidx.compose.material3.NavigationDrawerItemDefaults // Defaults de estilo
import androidx.compose.material3.Text // Texto
import androidx.compose.material3.ModalDrawerSheet // Contenedor de contenido del drawer
import androidx.compose.runtime.Composable // Marcador composable
import androidx.compose.ui.Modifier // Modificador
import androidx.compose.ui.graphics.vector.ImageVector // Tipo de ícono
// Pequeña data class para representar cada opción del drawer
data class DrawerItem( // Estructura de un ítem de menú lateral
    val label: String, // Texto a mostrar
    val icon: ImageVector, // Ícono del ítem
    val onClick: () -> Unit // Acción al hacer click
)

@Composable // Componente Drawer para usar en ModalNavigationDrawer
fun AppDrawer(
    currentRoute: String?, // Ruta actual (para marcar seleccionado si quieres)
    items: List<DrawerItem>, // Lista de ítems a mostrar
    modifier: Modifier = Modifier // Modificador opcional
) {
    ModalDrawerSheet( // Hoja que contiene el contenido del drawer
        modifier = modifier // Modificador encadenable
    ) {
        // Recorremos las opciones y pintamos ítems
        items.forEach { item -> // Por cada ítem
            NavigationDrawerItem( // Ítem con estados Material
                label = { Text(item.label) }, // Texto visible
                selected = false, // Puedes usar currentRoute == ... si quieres marcar
                onClick = item.onClick, // Acción al pulsar
                icon = { Icon(item.icon, contentDescription = item.label) }, // Ícono
                modifier = Modifier, // Sin mods extra
                colors = NavigationDrawerItemDefaults.colors() // Estilo por defecto
            )
        }
    }
}

// Helper para construir la lista estándar de ítems del drawer
@Composable
fun defaultDrawerItems(
    isLoggedIn: Boolean,
    isAdmin: Boolean,
    onHome: () -> Unit,
    onCatalog: () -> Unit,
    onCart: () -> Unit,
    onProfile: () -> Unit,
    onOrders: () -> Unit,
    onAdmin: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit
): List<DrawerItem> {
    val items = mutableListOf<DrawerItem>()
    
    items.add(DrawerItem("Inicio", Icons.Filled.Home, onHome))
    
    if (isLoggedIn) {
        if (!isAdmin) {
            items.add(DrawerItem("Catálogo", Icons.Filled.ShoppingCart, onCatalog))
            items.add(DrawerItem("Carrito", Icons.Filled.ShoppingCart, onCart))
        }
        items.add(DrawerItem("Mi Perfil", Icons.Filled.Person, onProfile))
        items.add(DrawerItem("Mis Pedidos", Icons.Filled.List, onOrders))
        if (isAdmin) {
            items.add(DrawerItem("Panel Admin", Icons.Filled.AdminPanelSettings, onAdmin))
        }
    } else {
        items.add(DrawerItem("Iniciar Sesión", Icons.Filled.Login, onLogin))
        items.add(DrawerItem("Registrarse", Icons.Filled.PersonAdd, onRegister))
    }
    
    return items
}