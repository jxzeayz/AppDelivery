package com.example.uinavegacion.navigation

// Clase sellada para rutas: evita "strings mágicos" y facilita refactors
sealed class Route(val path: String) { // Cada objeto representa una pantalla
    data object Home          : Route("home")
    data object Login         : Route("login")
    data object Register      : Route("register")
    data object Catalog       : Route("catalog")
    data object ProductDetail : Route("product_detail/{productId}") {
        fun createRoute(productId: Long) = "product_detail/$productId"
    }
    data object Cart          : Route("cart")
    data object Checkout      : Route("checkout")
    data object Profile       : Route("profile")
    data object AdminPanel    : Route("admin_panel")
    data object AddProduct    : Route("add_product")
    data object Orders        : Route("orders")
}

/*
* “Strings mágicos” se refiere a cuando pones un texto duro y repetido en varias partes del código,
* Si mañana cambias "home" por "inicio", tendrías que buscar todas las ocurrencias de "home" a mano.
* Eso es frágil y propenso a errores.
La idea es: mejor centralizar esos strings en una sola clase (Route), y usarlos desde ahí.*/