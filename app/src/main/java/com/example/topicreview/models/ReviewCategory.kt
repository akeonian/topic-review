package com.example.topicreview.models

class ReviewCategory(
    id: Int,
    title: String,
    daysLeft: Int,
    val subItems: List<ReviewItem>): ReviewItem(id, title, daysLeft) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as ReviewCategory

        if (subItems != other.subItems) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + subItems.hashCode()
        return result
    }
}
