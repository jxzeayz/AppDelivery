package com.example.uinavegacion.data.local.database

import android.content.Context                                  // Contexto para construir DB
import androidx.room.Database                                   // Anotación @Database
import androidx.room.Room                                       // Builder de DB
import androidx.room.RoomDatabase                               // Clase base de DB
import androidx.sqlite.db.SupportSQLiteDatabase                 // Tipo del callback onCreate
import com.example.uinavegacion.data.local.user.UserDao
import com.example.uinavegacion.data.local.user.UserEntity
import com.example.uinavegacion.data.local.product.ProductDao
import com.example.uinavegacion.data.local.product.ProductEntity
import com.example.uinavegacion.data.local.cart.CartDao
import com.example.uinavegacion.data.local.cart.CartItemEntity
import com.example.uinavegacion.data.local.order.OrderDao
import com.example.uinavegacion.data.local.order.OrderEntity
import com.example.uinavegacion.data.local.order.OrderItemDao
import com.example.uinavegacion.data.local.order.OrderItemEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// @Database registra entidades y versión del esquema.
// version = 2: actualizada para incluir productos, carrito y pedidos
@Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null              // Instancia singleton
        private const val DB_NAME = "delivery_app.db"         // Nombre del archivo .db

        // Obtiene la instancia única de la base
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Construimos la DB con callback de precarga
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    // Callback para ejecutar cuando la DB se crea por primera vez
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Lanzamos una corrutina en IO para insertar datos iniciales
                            CoroutineScope(Dispatchers.IO).launch {
                                val db = getInstance(context)
                                val userDao = db.userDao()
                                val productDao = db.productDao()

                                // Precarga de usuarios (con roles)
                                val users = listOf(
                                    UserEntity(
                                        name = "Admin",
                                        email = "admin@duoc.cl",
                                        phone = "+56911111111",
                                        password = "Admin123!",
                                        role = "admin"
                                    ),
                                    UserEntity(
                                        name = "Juan Cruz",
                                        email = "Vcruz@duoc.cl",
                                        phone = "+56922222222",
                                        password = "123456",
                                        role = "user"
                                    )
                                )

                                // Inserta usuarios si la tabla está vacía
                                if (userDao.count() == 0) {
                                    users.forEach { userDao.insert(it) }
                                }

                                // Precarga de productos de ejemplo
                                val products = listOf(
                                    ProductEntity(
                                        name = "Hamburguesa Clásica",
                                        description = "Hamburguesa con carne, lechuga, tomate y queso",
                                        price = 5990.0,
                                        category = "Comida",
                                        available = true
                                    ),
                                    ProductEntity(
                                        name = "Pizza Margarita",
                                        description = "Pizza con tomate, mozzarella y albahaca",
                                        price = 8990.0,
                                        category = "Comida",
                                        available = true
                                    ),
                                    ProductEntity(
                                        name = "Coca Cola",
                                        description = "Bebida gaseosa 500ml",
                                        price = 1500.0,
                                        category = "Bebida",
                                        available = true
                                    ),
                                    ProductEntity(
                                        name = "Papas Fritas",
                                        description = "Porción de papas fritas crujientes",
                                        price = 2990.0,
                                        category = "Acompañamiento",
                                        available = true
                                    ),
                                    ProductEntity(
                                        name = "Helado de Vainilla",
                                        description = "Helado artesanal de vainilla",
                                        price = 3990.0,
                                        category = "Postre",
                                        available = true
                                    )
                                )

                                // Inserta productos si la tabla está vacía
                                if (productDao.count() == 0) {
                                    products.forEach { productDao.insert(it) }
                                }
                            }
                        }
                    })
                    // En entorno educativo, si cambias versión sin migraciones, destruye y recrea.
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance                             // Guarda la instancia
                instance                                        // Devuelve la instancia
            }
        }
    }
}