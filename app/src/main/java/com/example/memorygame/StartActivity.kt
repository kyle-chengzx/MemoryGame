package com.example.memorygame

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.memorygame.DataManager.setGameCards
import com.google.gson.GsonBuilder

import kotlinx.android.synthetic.main.content_start.*
import okhttp3.*
import java.io.IOException

class StartActivity : AppCompatActivity(){

    lateinit var matchOptionSpinner: Spinner

    lateinit var columnOptionSpinner: Spinner

    private var wonGame = false

    private var loseGame = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        startButton.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            setGameCards()
            this.startActivity(intent)
        }

        columnOptionSpinner = findViewById(R.id.spinnerColumnSize)
        val columnOptions = resources.getStringArray(R.array.column_numbers)
        columnOptionSpinner.adapter = ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,columnOptions)

        /*Set up both spinners*/
        columnOptionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                DataManager.gridNum = columnOptions[2].toInt()
                columnOptionSpinner.setSelection(columnOptions[2].toInt())
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                DataManager.gridNum = columnOptions[p2].toInt()
            }
        }

        matchOptionSpinner = findViewById(R.id.spinnerMatchNum)
        val matchOptions = resources.getStringArray(R.array.match_number)
        matchOptionSpinner.adapter = ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,matchOptions)
        matchOptionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                DataManager.matchNum = matchOptions[p2].toInt()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                DataManager.matchNum = matchOptions[0].toInt()
            }
        }

        /*Set api call */
        if (DataManager.allImages.size <= 0) {

            startButton.isEnabled = false
            startButton.text = "LOADING PICTURES"

            val url = "https://shopicruit.myshopify.com/admin/products.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6"

            val request = Request.Builder().url(url).build()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Could not retrieve data source")
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body()?.string()
                    val gson = GsonBuilder().create()
                    val shopifyObject = gson.fromJson(body, DataManager.ShopifyItems:: class.java)
                    DataManager.allImages = (ArrayList(shopifyObject.products.map { x -> x.image }.toList()))
                    for(x in DataManager.allImages) {
                        x.setBMP()
                    }
                    runOnUiThread {
                        run {
                            startButton.isEnabled = true
                            startButton.text = "START!!"
                        }
                    }
                }
            })
        }


        wonGame = savedInstanceState?.getBoolean(WINNER_FLAG,false) ?:
                intent.getBooleanExtra(WINNER_FLAG,false)

        loseGame = savedInstanceState?.getBoolean(LOSE_FLAG,false) ?:
            intent.getBooleanExtra(LOSE_FLAG,false)

        if(wonGame) {
            val timeLeft = intent.getIntExtra(TIME_LEFT_IN_GAME, 0)
            Snackbar.make(findViewById(R.id.startLayout),"CONGRATS YOU WON THE GAME WITH ${timeLeft}s left",Snackbar.LENGTH_LONG).setAction("Action", null).show()
        }

        if (loseGame) {
            Snackbar.make(findViewById(R.id.startLayout),"Time Has run out, you were ${DataManager.neededPoints - DataManager.gamePoints} points too short",Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

}
