package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.SinaKalaDatabase
import com.example.data.repository.SinaKalaRepository
import com.example.ui.components.PersianUtils
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.ComparisonScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrderSuccessScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.WishlistScreen
import com.example.ui.theme.SinaKalaTheme
import com.example.ui.theme.SinaNavy
import com.example.ui.theme.SinaRed
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.SinaKalaViewModel
import com.example.ui.viewmodel.SinaKalaViewModelFactory
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = SinaKalaDatabase.getDatabase(this)
        val repository = SinaKalaRepository(db.sinaKalaDao())
        val viewModelFactory = SinaKalaViewModelFactory(repository)

        setContent {
            val viewModel: SinaKalaViewModel = viewModel(factory = viewModelFactory)
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            SinaKalaTheme(darkTheme = isDarkTheme) {
                // RTL support for Persian layout
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    SinaKalaApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun SinaKalaApp(viewModel: SinaKalaViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val cartItems by viewModel.cartWithProducts.collectAsState()
    val wishlistProducts by viewModel.wishlistProducts.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val cartCount = cartItems.sumOf { it.cartItem.quantity }

    // Listen to snackbar toast events
    LaunchedEffect(Unit) {
        viewModel.toastMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = {
            // Show bottom navigation bar on primary screens
            val isMainBottomBarVisible = currentScreen in listOf(
                Screen.HOME,
                Screen.CATALOG,
                Screen.WISHLIST,
                Screen.PROFILE
            )

            if (isMainBottomBarVisible) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    // 1. Home
                    NavigationBarItem(
                        selected = currentScreen == Screen.HOME,
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == Screen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "خانه"
                            )
                        },
                        label = {
                            Text(
                                text = "خانه",
                                fontWeight = if (currentScreen == Screen.HOME) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SinaRed,
                            selectedTextColor = SinaRed,
                            indicatorColor = SinaRed.copy(alpha = 0.12f)
                        )
                    )

                    // 2. Catalog / Categories
                    NavigationBarItem(
                        selected = currentScreen == Screen.CATALOG,
                        onClick = { viewModel.navigateTo(Screen.CATALOG) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == Screen.CATALOG) Icons.Filled.Category else Icons.Outlined.Category,
                                contentDescription = "محصولات"
                            )
                        },
                        label = {
                            Text(
                                text = "محصولات",
                                fontWeight = if (currentScreen == Screen.CATALOG) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SinaRed,
                            selectedTextColor = SinaRed,
                            indicatorColor = SinaRed.copy(alpha = 0.12f)
                        )
                    )

                    // 3. Cart
                    NavigationBarItem(
                        selected = currentScreen == Screen.CART,
                        onClick = { viewModel.navigateTo(Screen.CART) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (cartCount > 0) {
                                        Badge(
                                            containerColor = SinaRed,
                                            contentColor = Color.White
                                        ) {
                                            Text(
                                                text = PersianUtils.toPersianDigits(cartCount),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (currentScreen == Screen.CART) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                                    contentDescription = "سبد خرید"
                                )
                            }
                        },
                        label = {
                            Text(
                                text = "سبد خرید",
                                fontWeight = if (currentScreen == Screen.CART) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SinaRed,
                            selectedTextColor = SinaRed,
                            indicatorColor = SinaRed.copy(alpha = 0.12f)
                        )
                    )

                    // 4. Wishlist
                    NavigationBarItem(
                        selected = currentScreen == Screen.WISHLIST,
                        onClick = { viewModel.navigateTo(Screen.WISHLIST) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (wishlistProducts.isNotEmpty()) {
                                        Badge(
                                            containerColor = SinaRed,
                                            contentColor = Color.White
                                        ) {
                                            Text(
                                                text = PersianUtils.toPersianDigits(wishlistProducts.size),
                                                fontSize = 9.sp
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (currentScreen == Screen.WISHLIST) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "علاقه‌مندی"
                                )
                            }
                        },
                        label = {
                            Text(
                                text = "علاقه‌مندی",
                                fontWeight = if (currentScreen == Screen.WISHLIST) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SinaRed,
                            selectedTextColor = SinaRed,
                            indicatorColor = SinaRed.copy(alpha = 0.12f)
                        )
                    )

                    // 5. Profile
                    NavigationBarItem(
                        selected = currentScreen == Screen.PROFILE,
                        onClick = { viewModel.navigateTo(Screen.PROFILE) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == Screen.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "پروفایل"
                            )
                        },
                        label = {
                            Text(
                                text = "پروفایل",
                                fontWeight = if (currentScreen == Screen.PROFILE) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SinaRed,
                            selectedTextColor = SinaRed,
                            indicatorColor = SinaRed.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = currentScreen,
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    Screen.HOME -> HomeScreen(viewModel = viewModel)
                    Screen.CATALOG -> CatalogScreen(viewModel = viewModel)
                    Screen.PRODUCT_DETAIL -> ProductDetailScreen(viewModel = viewModel)
                    Screen.CART, Screen.CHECKOUT -> CartScreen(viewModel = viewModel)
                    Screen.ORDER_SUCCESS -> OrderSuccessScreen(viewModel = viewModel)
                    Screen.PROFILE, Screen.ORDERS_LIST -> ProfileScreen(viewModel = viewModel)
                    Screen.WISHLIST -> WishlistScreen(viewModel = viewModel)
                    Screen.COMPARE -> ComparisonScreen(viewModel = viewModel)
                    Screen.ADMIN -> AdminDashboardScreen(viewModel = viewModel)
                }
            }
        }
    }
}
