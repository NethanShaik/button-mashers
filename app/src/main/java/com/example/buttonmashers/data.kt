package com.example.buttonmashers

data class Game(
    val id: Int,
    val title: String,
    val description: String,
    val releaseDate: String,
    val price: Double,
    val categoryId: Int,
    val imagePath: String? = null
)

data class Category(
    val id: Int,
    val name: String
)

data class Order(
    val id: Int,
    val totalPrice: Double,
    val orderDate: String,
    val isShoppingCart: Boolean,
    val displayedOrderId: String
)

data class OrderItem(
    val orderId: Int,
    val gameId: Int,
    val quantity: Int,
    val price: Double
)
