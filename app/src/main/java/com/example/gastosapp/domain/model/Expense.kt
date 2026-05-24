package com.example.gastosapp.domain.model

data class Expense(
    val id: String,
    val description: String,
    val amount: Double,
    val date: Long
)
