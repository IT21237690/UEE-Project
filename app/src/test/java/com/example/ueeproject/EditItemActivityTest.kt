package com.example.ueeproject

import com.google.common.truth.Truth
import org.junit.Assert.*

import org.junit.Test

class EditItemActivityTest {
    @Test
    fun `correct items`() {
        val result = EditItemActivity.Companion.validateAuctionInput(
            itemName = "Lap top",
            description = "The product was used 4 years, the mother board is still in good condition",
            price = "Rs 200 000.00",
            startTime ="2023-10-04 09:15:00",
            endTime = "2023-10-06 09:15:00",
        )
        Truth.assertThat(result).isTrue()
    }

    @Test
    fun `empty item name`() {
        val result = EditItemActivity.Companion.validateAuctionInput(
            itemName = "",
            description = "The product was used 4 years, the mother board is still in good condition",
            price = "Rs 200 000.00",
            startTime ="2023-10-04 09:15:00",
            endTime = "2023-10-06 09:15:00",
        )
        Truth.assertThat(result).isFalse()
    }


    @Test
    fun `empty description`() {
        val result = EditItemActivity.Companion.validateAuctionInput(
            itemName = "Lap top",
            description = "",
            price = "Rs 200 000.00",
            startTime ="2023-10-04 09:15:00",
            endTime = "2023-10-06 09:15:00",
        )
        Truth.assertThat(result).isFalse()
    }

    @Test
    fun `empty price`() {
        val result = EditItemActivity.Companion.validateAuctionInput(
            itemName = "Lap top",
            description = "The product was used 4 years, the mother board is still in good condition",
            price = "",
            startTime ="2023-10-04 09:15:00",
            endTime = "2023-10-06 09:15:00",
        )
        Truth.assertThat(result).isFalse()
    }

    @Test
    fun `empty start time`() {
        val result = EditItemActivity.Companion.validateAuctionInput(
            itemName = "Lap top",
            description = "The product was used 4 years, the mother board is still in good condition",
            price = "Rs 5.00",
            startTime = "",
            endTime = "2023-10-06 09:15:00",
        )
        Truth.assertThat(result).isFalse()
    }

    @Test
    fun `empty end time`() {
        val result = EditItemActivity.Companion.validateAuctionInput(
            itemName = "Lap top",
            description = "The product was used 4 years, the mother board is still in good condition",
            price = "Rs 5.00",
            startTime ="2023-10-04 09:15:00",
            endTime = "",
        )
        Truth.assertThat(result).isFalse()
    }

    fun `invalid item name`() {
        val result = EditItemActivity.Companion.validateAuctionInput(
            itemName = "Lap ",
            description = "The product",
            price = "Rs 5.00",
            startTime ="2023-10-04 09:15:00",
            endTime = "2023-10-06 09:15:00",
        )
        Truth.assertThat(result).isFalse()
    }


    @Test
    fun `invalid description`() {
        val result = EditItemActivity.Companion.validateAuctionInput(
            itemName = "Lap top",
            description = "The product",
            price = "Rs 5.00",
            startTime ="2023-10-04 09:15:00",
            endTime = "2023-10-06 09:15:00",

            )
        Truth.assertThat(result).isFalse()
    }


    @Test
    fun `invalid price`() {
        val result = EditItemActivity.Companion.validateAuctionInput(
            itemName = "Lap top",
            description = "The product was used 4 years, the mother board is still in good condition",
            price = "Rs 5.00",
            startTime ="2023-10-04 09:15:00",
            endTime = "2023-10-06 09:15:00",
        )
        Truth.assertThat(result).isFalse()
    }


    @Test
    fun `invalid start end time less digits`() {
        val result = EditItemActivity.Companion.validateAuctionInput(
            itemName = "Lap ",
            description = "The product",
            price = "Rs 5.00",
            startTime ="2023-10-04 09:15:",
            endTime = "2023-10-06 09:15:",
        )
        Truth.assertThat(result).isFalse()
    }

    @Test
    fun `invalid start end time more digits`() {
        val result = EditItemActivity.Companion.validateAuctionInput(
            itemName = "Lap ",
            description = "The product",
            price = "Rs 5.00",
            startTime ="2023-10-0445 09:15:00",
            endTime = "2023-10-06354 09:15:00",
        )
        Truth.assertThat(result).isFalse()
    }



}