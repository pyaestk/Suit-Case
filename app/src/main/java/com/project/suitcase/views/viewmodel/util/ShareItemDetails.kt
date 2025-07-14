package com.project.suitcase.views.viewmodel.util

import android.content.Intent

fun shareItemDetails(
    itemName: String,
    itemPrice: String,
    itemLocation: String,
    itemDescription: String
): Intent {
    // Prepare the message content
    val message = """
            Check out this item!
            Name: $itemName
            Price: $itemPrice
            Location: $itemLocation
            Description: $itemDescription
        """.trimIndent()

    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, message)
        type = "text/plain"
    }

    return sendIntent
}