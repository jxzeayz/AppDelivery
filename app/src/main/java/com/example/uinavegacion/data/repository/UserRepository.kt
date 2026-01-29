package com.example.uinavegacion.data.repository

import com.example.uinavegacion.data.local.user.UserDao       // DAO de usuario
import com.example.uinavegacion.data.local.user.UserEntity    // Entidad de usuario

// Repositorio: orquesta reglas de negocio para login/registro sobre el DAO.
class UserRepository(
    private val userDao: UserDao // Inyección del DAO
) {

    // Login: busca por email y valida contraseña
    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.getByEmail(email)                         // Busca usuario
        return if (user != null && user.password == password) {      // Verifica pass
            Result.success(user)                                     // Éxito
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas")) // Error
        }
    }

    // Registro: valida no duplicado y crea nuevo usuario (con teléfono)
    suspend fun register(name: String, email: String, phone: String, password: String): Result<Long> {
        val exists = userDao.getByEmail(email) != null               // ¿Correo ya usado?
        if (exists) {
            return Result.failure(IllegalStateException("El correo ya está registrado"))
        }
        val id = userDao.insert(                                     // Inserta nuevo
            UserEntity(
                name = name,
                email = email,
                phone = phone,                                       // Teléfono incluido
                password = password,
                role = "user"                                        // Rol por defecto
            )
        )
        return Result.success(id)                                    // Devuelve ID generado
    }
    
    // Obtener usuario por ID
    suspend fun getUserById(id: Long) = userDao.getById(id)
    
    // Actualizar perfil
    suspend fun updateProfile(id: Long, name: String, phone: String, address: String?): Result<Unit> {
        return try {
            userDao.updateProfile(id, name, phone, address)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}