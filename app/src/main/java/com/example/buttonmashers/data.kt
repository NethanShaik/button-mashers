package com.example.buttonmashers

data class Game(
    val id: Int,
    val title: String,
    val description: String,
    val releaseDate: String,
    val price: Double,
    val categoryId: Int,
    val imageResId: Int
)

data class Category(
    val id: Int,
    val name: String
) {
    override fun toString(): String {
        return name
    }
}

data class Order(
    val id: Int,
    val displayedOrderId: String,
    val orderDate: String,
    val items: List<OrderItem>
)

data class OrderItem(
    val orderId: Int,
    val game: Game,
    val quantity: Int,
)
