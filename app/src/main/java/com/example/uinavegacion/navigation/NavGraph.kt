package com.example.uinavegacion.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.uinavegacion.data.local.storage.UserPreferences
import com.example.uinavegacion.data.repository.*
import com.example.uinavegacion.ui.components.AppDrawer
import com.example.uinavegacion.ui.components.AppTopBar
import com.example.uinavegacion.ui.components.defaultDrawerItems
import com.example.uinavegacion.ui.screen.*
import com.example.uinavegacion.ui.viewmodel.*
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userRepository: UserRepository,
    productRepository: ProductRepository,
    cartRepository: CartRepository,
    orderRepository: OrderRepository
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }

    val isLoggedIn by userPrefs.isLoggedIn.collectAsStateWithLifecycle(false)
    val userId by userPrefs.userId.collectAsStateWithLifecycle(null)
    val userRole by userPrefs.userRole.collectAsStateWithLifecycle(null)
    val isAdmin = userRole == "admin"

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // 🔹 Ruta observable
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // ViewModels
    val productViewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(productRepository)
    )
    val cartViewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(cartRepository, productRepository)
    )
    val orderViewModel: OrderViewModel = viewModel(
        factory = OrderViewModelFactory(orderRepository)
    )
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(userRepository)
    )

    // Navegación
    val goHome: () -> Unit = {
        navController.navigate(Route.Home.path) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
        }
    }
    val goLogin: () -> Unit = { navController.navigate(Route.Login.path) }
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) }
    val goCatalog: () -> Unit = { navController.navigate(Route.Catalog.path) }
    val goCart: () -> Unit = { navController.navigate(Route.Cart.path) }
    val goProfile: () -> Unit = { navController.navigate(Route.Profile.path) }
    val goAdmin: () -> Unit = { navController.navigate(Route.AdminPanel.path) }
    val goOrders: () -> Unit = { navController.navigate(Route.Orders.path) }

    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }

    val logout: () -> Unit = {
        authViewModel.logout()
        scope.launch { userPrefs.clearUser() }
        goHome()
    }

    // Cargar carrito
    LaunchedEffect(userId) {
        userId?.let { cartViewModel.loadCart(it) }
    }

    val cartState by cartViewModel.state.collectAsStateWithLifecycle()
    val cartItemCount = cartState.itemCount

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = currentRoute,
                items = defaultDrawerItems(
                    isLoggedIn = isLoggedIn,
                    isAdmin = isAdmin,
                    onHome = { scope.launch { drawerState.close() }; goHome() },
                    onCatalog = { scope.launch { drawerState.close() }; goCatalog() },
                    onCart = { scope.launch { drawerState.close() }; goCart() },
                    onProfile = { scope.launch { drawerState.close() }; goProfile() },
                    onOrders = { scope.launch { drawerState.close() }; goOrders() },
                    onAdmin = { scope.launch { drawerState.close() }; goAdmin() },
                    onLogin = { scope.launch { drawerState.close() }; goLogin() },
                    onRegister = { scope.launch { drawerState.close() }; goRegister() }
                )
            )
        }
    ) {
        Scaffold(
            topBar = {
                if (
                    currentRoute != Route.Login.path &&
                    currentRoute != Route.Register.path &&
                    currentRoute != Route.Home.path
                ) {
                    AppTopBar(
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onHome = goHome,
                        onLogin = goLogin,
                        onRegister = goRegister
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Route.Home.path,
                modifier = Modifier.padding(innerPadding)
            ) {

                composable(Route.Home.path) {
                    HomeScreen(
                        isLoggedIn = isLoggedIn,
                        isAdmin = isAdmin,
                        onGoCatalog = goCatalog,
                        onGoLogin = goLogin,
                        onGoRegister = goRegister,
                        onGoAdmin = goAdmin
                    )
                }

                composable(Route.Login.path) {
                    LoginScreenVm(
                        vm = authViewModel,
                        onLoginOkNavigateHome = {
                            if (isAdmin) goAdmin() else goCatalog()
                        },
                        onGoRegister = goRegister
                    )
                }

                composable(Route.Register.path) {
                    RegisterScreenVm(
                        vm = authViewModel,
                        onRegisteredNavigateLogin = goLogin,
                        onGoLogin = goLogin
                    )
                }

                composable(Route.Catalog.path) {
                    LaunchedEffect(isLoggedIn) {
                        if (!isLoggedIn) goLogin()
                    }
                    userId?.let {
                        CatalogScreen(
                            productViewModel = productViewModel,
                            userId = it,
                            onProductClick = { id ->
                                navController.navigate(Route.ProductDetail.createRoute(id))
                            },
                            onCartClick = goCart,
                            cartItemCount = cartItemCount
                        )
                    }
                }

                composable(
                    route = Route.ProductDetail.path,
                    arguments = listOf(navArgument("productId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val productId = backStackEntry.arguments?.getLong("productId") ?: return@composable
                    userId?.let {
                        ProductDetailScreen(
                            productId = productId,
                            userId = it,
                            productViewModel = productViewModel,
                            cartViewModel = cartViewModel,
                            onBack = navigateBack,
                            onCartClick = goCart
                        )
                    }
                }

                composable(Route.Cart.path) {
                    LaunchedEffect(isLoggedIn) {
                        if (!isLoggedIn) goLogin()
                    }
                    userId?.let {
                        CartScreen(
                            userId = it,
                            cartViewModel = cartViewModel,
                            onBack = navigateBack,
                            onCheckout = { navController.navigate(Route.Checkout.path) }
                        )
                    }
                }

                composable(Route.Checkout.path) {
                    LaunchedEffect(isLoggedIn) {
                        if (!isLoggedIn) goLogin()
                    }
                    userId?.let {
                        CheckoutScreen(
                            userId = it,
                            cartViewModel = cartViewModel,
                            orderViewModel = orderViewModel,
                            onBack = navigateBack,
                            onOrderPlaced = {
                                navController.navigate(Route.Orders.path) {
                                    popUpTo(Route.Catalog.path)
                                }
                            }
                        )
                    }
                }

                composable(Route.Profile.path) {
                    LaunchedEffect(isLoggedIn) {
                        if (!isLoggedIn) goLogin()
                    }
                    userId?.let {
                        ProfileScreen(
                            userId = it,
                            profileViewModel = profileViewModel,
                            onBack = navigateBack,
                            onLogout = logout
                        )
                    }
                }

                composable(Route.AdminPanel.path) {
                    LaunchedEffect(isLoggedIn to isAdmin) {
                        if (!isLoggedIn || !isAdmin) goHome()
                    }
                    AdminPanelScreen(
                        productViewModel = productViewModel,
                        orderViewModel = orderViewModel,
                        onBack = navigateBack,
                        onAddProduct = { navController.navigate(Route.AddProduct.path) },
                        onEditProduct = { id ->
                            navController.navigate("${Route.AddProduct.path}?productId=$id")
                        }
                    )
                }

                composable(
                    route = "${Route.AddProduct.path}?productId={productId}",
                    arguments = listOf(
                        navArgument("productId") {
                            type = NavType.LongType
                            defaultValue = 0L
                        }
                    )
                ) { backStackEntry ->
                    val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
                    LaunchedEffect(isLoggedIn to isAdmin) {
                        if (!isLoggedIn || !isAdmin) goHome()
                    }
                    AddProductScreen(
                        productId = productId.takeIf { it > 0 },
                        productViewModel = productViewModel,
                        onBack = navigateBack
                    )
                }

                composable(Route.Orders.path) {
                    LaunchedEffect(isLoggedIn) {
                        if (!isLoggedIn) goLogin()
                    }
                    userId?.let {
                        OrdersScreen(
                            userId = it,
                            isAdmin = isAdmin,
                            orderViewModel = orderViewModel,
                            onBack = navigateBack
                        )
                    }
                }
            }
        }
    }
}
