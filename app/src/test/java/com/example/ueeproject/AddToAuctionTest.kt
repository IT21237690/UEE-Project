package com.example.ueeproject

//import org.junit.Assert.*

import org.junit.Assert
import org.junit.Test

class AddToAuctionTest {

    @Test
    fun `correct items`() {
        val result = AddToAuction.Companion.validateAuctionInput(
            itemName = "Lap top",
            description = "The product was used 4 years, the mother board is still in good condition",
            price = "Rs 200 000.00",
            startTime ="2023-10-04 09:15:00",
            endTime = "2023-10-06 09:15:00",
        )
        Assert.assertTrue(result)
    }

    @Test
    fun `empty item name`() {
        val result = AddToAuction.Companion.validateAuctionInput(
            itemName = "",
            description = "The product was used 4 years, the mother board is still in good condition",
            price = "Rs 200 000.00",
            startTime ="2023-10-04 09:15:00",
            endTime = "2023-10-06 09:15:00",
        )
        Assert.assertFalse(result)
    }


    @Test
    fun `empty description`() {
        val result = AddToAuction.Companion.validateAuctionInput(
            itemName = "Lap top",
            description = "",
            price = "Rs 200 000.00",
            startTime ="2023-10-04 09:15:00",
            endTime = "2023-10-06 09:15:00",
        )
        Assert.assertFalse(result)
    }

    @Test
    fun `empty price`() {
        val result = AddToAuction.Companion.validateAuctionInput(
            itemName = "Lap top",
            description = "The product was used 4 years, the mother board is still in good condition",
            price = "",
            startTime ="2023-10-04 09:15:00",
            endTime = "2023-10-06 09:15:00",
        )
        Assert.assertFalse(result)
    }

    @Test
    fun `empty start time`() {
        val result = AddToAuction.Companion.validateAuctionInput(
            itemName = "Lap top",
            description = "The product was used 4 years, the mother board is still in good condition",
            price = "Rs 5.00",
            startTime = "",
            endTime = "2023-10-06 09:15:00",
        )
        Assert.assertFalse(result)
    }

    @Test
    fun `empty end time`() {
        val result = AddToAuction.Companion.validateAuctionInput(
            itemName = "Lap top",
            description = "The product was used 4 years, the mother board is still in good condition",
            price = "Rs 5.00",
            startTime ="2023-10-04 09:15:00",
            endTime = "",
        )
        Assert.assertFalse(result)
    }

    fun `invalid item name`() {
        val result = AddToAuction.Companion.validateAuctionInput(
            itemName = "Lap ",
            description = "The product",
            price = "Rs 5.00",
            startTime ="2023-10-04 09:15:00",
            endTime = "2023-10-06 09:15:00",
        )
        Assert.assertFalse(result)
    }


    @Test
    fun `invalid description`() {
        val result = AddToAuction.Companion.validateAuctionInput(
            itemName = "Lap top",
            description = "The product",
            price = "Rs 5.00",
            startTime ="2023-10-04 09:15:00",
            endTime = "2023-10-06 09:15:00",

            )
        Assert.assertFalse(result)
    }


    @Test
    fun `invalid price`() {
        val result = AddToAuction.Companion.validateAuctionInput(
            itemName = "Lap top",
            description = "The product was used 4 years, the mother board is still in good condition",
            price = "Rs 5.00",
            startTime ="2023-10-04 09:15:00",
            endTime = "2023-10-06 09:15:00",
        )
        Assert.assertFalse(result)
    }



    @Test
    fun `invalid start end time less digits`() {
        val result = AddToAuction.Companion.validateAuctionInput(
            itemName = "Lap ",
            description = "The product",
            price = "Rs 5.00",
            startTime ="2023-10-04 09:15:",
            endTime = "2023-10-06 09:15:",
        )
        Assert.assertFalse(result)
    }

    @Test
    fun `invalid start end time more digits`() {
        val result = AddToAuction.Companion.validateAuctionInput(
            itemName = "Lap ",
            description = "The product",
            price = "Rs 5.00",
            startTime ="2023-10-0445 09:15:00",
            endTime = "2023-10-06354 09:15:00",
        )
        Assert.assertFalse(result)
    }



}