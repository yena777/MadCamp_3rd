package com.example.third_app.login

data class LoginData(
    val id: String,
    val isActive: Boolean?,
    val name: String?,
    val password: String?,
    val budget: String
) //일부 데이터만 받으면 되는 건가?