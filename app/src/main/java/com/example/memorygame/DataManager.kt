package com.example.memorygame

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.memorygame.DataManager.allImages
import com.example.memorygame.DataManager.gameCards
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import kotlin.random.Random


object DataManager {
    var matchNum = 2
    var gridNum = 1
    var gamePoints = 0
    var neededPoints = 10

    var allImages = ArrayList<Image>()

    var gameCards = ArrayList<Image>()

    fun setGameCards() {
        gameCards.clear()
        for (x in 0 until (allImages.size - 40)) {
            val testImage = allImages[x]
            for (i in 0 until matchNum) {
                gameCards.add(testImage)
            }
        }


        shuffleCards()
    }

    fun shuffleCards() {
        gameCards.shuffle(Random)
    }

    class ShopifyItems(val products: List<Product>)

    class Product(val id: String, val image: Image)

    class Image(val id: String,private val src: String , var bmp : Bitmap? = null) {
        fun setBMP() {
            val url = java.net.URL(src)
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())

        }
    }
}
