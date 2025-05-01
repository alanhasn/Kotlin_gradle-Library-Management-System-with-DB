package org.controllers

import org.data.ValidationResult
import org.models.User
import javax.xml.crypto.Data

class UserManagement {
    private val userList = mutableListOf<User>()

    // is valid function to validate the user before registration
    private fun isValidUser(user: User): ValidationResult {
        return when {
            user.username.isBlank() -> {
                ValidationResult(false, "Please add a username.")
            }
            user.password.length < 8 -> {
                ValidationResult(false, "Your password must contain at least 8 characters.")
            }
            userList.any { it.username == user.username } -> {
                ValidationResult(false, "Username already exists.")
            }
            !user.email.contains("@") -> {
                ValidationResult(false, "The email must contain '@' symbol.")
            }
            !user.email.contains(".") -> {
                ValidationResult(false, "The email must contain '.' symbol.")
            }
            else -> ValidationResult(true)
        }
    }

    fun register(user: User){
        try {
            val connection = Database.connect()
            val statement = connection.prepareStatement("""
                INSERT INTO  users (username, email, password, balance)
                VALUES (?, ?, ?, ?)
            """.trimIndent())
            val result = isValidUser(user)
            if (result.isValid){
                println("The User ${user.username} registered successfully")
            }
            else{
                println("Validation Error:${result.errorMessage}")
            }
        }catch (e: Exception){
            println("ERROR:$e")
        }
    }

    fun registeredUser(){
        println("Number of registered user is ${userList.count()}\n\n")
        println("Registered User Info")
        for (user in userList){
            println("username:${user.username}\nEmail:${user.email},Balance:${user.balance}")
        }
    }

}