package com.example.ueeproject

import com.google.common.truth.Truth
import org.junit.Assert.*

import org.junit.Test

class EditSellItemTest {

    @Test
    fun `correct`() {
        val result = EditSellItem.Companion.validateInput(
            itemName = "Lap top",
            description = "The product was used 4 years, the mother board is still in good condition",
            price = "Rs 200 000.00",
        )
        Truth.assertThat(result).isTrue()
    }

    @Test
    fun `empty item name`() {
        val result = EditSellItem.Companion.validateInput(
            itemName = "",
            description = "The product was used 4 years, the mother board is still in good condition",
            price = "Rs 200 000.00",
        )
        Truth.assertThat(result).isFalse()
    }


    @Test
    fun `empty description`() {
        val result = EditSellItem.Companion.validateInput(
            itemName = "Lap top",
            description = "",
            price = "Rs 200 000.00",
        )
        Truth.assertThat(result).isFalse()
    }

    @Test
    fun `empty price`() {
        val result = EditSellItem.Companion.validateInput(
            itemName = "Lap top",
            description = "The product was used 4 years, the mother board is still in good condition",
            price = "",
        )
        Truth.assertThat(result).isFalse()
    }

    @Test
    fun `invalid item name`() {
        val result = EditSellItem.Companion.validateInput(
            itemName = "Lap ",
            description = "The product",
            price = "Rs 5.00",
        )
        Truth.assertThat(result).isFalse()
    }

    @Test
    fun `invalid description`() {
        val result = EditSellItem.Companion.validateInput(
            itemName = "Lap top",
            description = "The product",
            price = "Rs 5.00",
        )
        Truth.assertThat(result).isFalse()
    }


    @Test
    fun `invalid price`() {
        val result = EditSellItem.Companion.validateInput(
            itemName = "Lap top",
            description = "The product was used 4 years, the mother board is still in good condition",
            price = "Rs 5.00",
        )
        Truth.assertThat(result).isFalse()
    }
}