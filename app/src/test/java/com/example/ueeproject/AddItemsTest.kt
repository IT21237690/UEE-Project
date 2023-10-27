package com.example.ueeproject

//import org.junit.Assert.*

//import com.google.common.truth.ExpectFailure.assertThat
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test


class AddItemsTest {

    @Test
    fun `correct`() {
        val result = AddItems.Companion.validateInput(
            itemName = "Lap top",
            description = "The product was used 4 years, the mother board is still in good condition",
            price = "Rs 200 000.00",
        )
        assertTrue(result)
    }
    @Test
    fun `empty item name should return false`() {
        // Arrange
        val result = AddItems.Companion.validateInput(
            itemName = "",
            description = "The product was used 4 years, the motherboard is still in good condition",
            price = "Rs 200 000.00"
        )

        // Act & Assert
        assertFalse(result)
    }



    @Test
    fun `empty description`() {
        val result = AddItems.Companion.validateInput(
            itemName = "Lap top",
            description = "",
            price = "Rs 200 000.00",
        )
        assertFalse(result)
    }

    @Test
    fun `empty price`() {
        val result = AddItems.Companion.validateInput(
            itemName = "Lap top",
            description = "The product was used 4 years, the mother board is still in good condition",
            price = "",
        )
        assertFalse(result)
    }

    @Test
    fun `invalid item name`() {
        val result = AddItems.Companion.validateInput(
            itemName = "Lap ",
            description = "The product",
            price = "Rs 5.00",
        )
        assertFalse(result)
    }

    @Test
    fun `invalid description`() {
        val result = AddItems.Companion.validateInput(
            itemName = "Lap top",
            description = "The product",
            price = "Rs 5.00",
        )
        assertFalse(result)
    }


    @Test
    fun `invalid price`() {
        val result = AddItems.Companion.validateInput(
            itemName = "Lap top",
            description = "The product was used 4 years, the mother board is still in good condition",
            price = "Rs 5.00",
        )
        assertFalse(result)
    }
}
