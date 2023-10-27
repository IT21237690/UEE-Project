package com.example.ueeproject

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EditSellItemTest {

    @Test
    fun `correct`() {
        val result = EditSellItem.Companion.validateInput(
            itemName = "Lap top",
            description = "The product was used 4 years, the mother board is still in good condition",
            price = "Rs 200 000.00",
        )
        assertTrue(result)
    }

    @Test
    fun `empty item name`() {
        val result = EditSellItem.Companion.validateInput(
            itemName = "",
            description = "The product was used 4 years, the mother board is still in good condition",
            price = "Rs 200 000.00",
        )
        assertFalse(result)
    }


    @Test
    fun `empty description`() {
        val result = EditSellItem.Companion.validateInput(
            itemName = "Lap top",
            description = "",
            price = "Rs 200 000.00",
        )
        assertFalse(result)
    }

    @Test
    fun `empty price`() {
        val result = EditSellItem.Companion.validateInput(
            itemName = "Lap top",
            description = "The product was used 4 years, the mother board is still in good condition",
            price = "",
        )
        assertFalse(result)
    }

    @Test
    fun `invalid item name`() {
        val result = EditSellItem.Companion.validateInput(
            itemName = "Lap ",
            description = "The product",
            price = "Rs 5.00",
        )
        assertFalse(result)
    }

    @Test
    fun `invalid description`() {
        val result = EditSellItem.Companion.validateInput(
            itemName = "Lap top",
            description = "The product",
            price = "Rs 5.00",
        )
        assertFalse(result)
    }


    @Test
    fun `invalid price`() {
        val result = EditSellItem.Companion.validateInput(
            itemName = "Lap top",
            description = "The product was used 4 years, the mother board is still in good condition",
            price = "Rs 5.00",
        )
        assertFalse(result)
    }
}