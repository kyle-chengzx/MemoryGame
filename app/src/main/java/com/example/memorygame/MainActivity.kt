package com.example.memorygame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.schedule
import kotlin.collections.ArrayList
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_start.*


class MainActivity : AppCompatActivity(), ItemRecyclerAdapter.OnCardSelectedLister {

    private var selectedCards = ArrayList<View>()
    private var matchedCards = ArrayList<View>()

    lateinit var countDownTimer: CountDownTimer
    private val initialCountDown : Long = 60000
    private val countDownInterval : Long = 1000

    private var timeLeft = initialCountDown

    lateinit var adapter :ItemRecyclerAdapter

    val allCards by lazy {
        adapter.allCards
    }

    private val cardRecyclerAdapter by lazy {
        adapter = ItemRecyclerAdapter(this,DataManager.gameCards)
        adapter.setOnSelectedListener(this)
        adapter
    }

    private val cardLayoutManager by lazy {
        GridLayoutManager(this,DataManager.gridNum)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DataManager.gamePoints = 0
        setViewDisplay()

    }

    private fun setViewDisplay() {
        displayCardList()
        setPointText()
        startTimer()
        val toolBarShuffleButton = findViewById<Button>(R.id.shuffleButton)
        toolBarShuffleButton.setOnClickListener {
            DataManager.shuffleCards()
            displayCardList()
            Snackbar.make(findViewById(R.id.cardListView),"Cards Have been Shuffled!",Snackbar.LENGTH_SHORT).setAction("Action", null).show()
        }
    }

    private fun startTimer() {
        val timerTextView = findViewById<TextView>(R.id.timerText)
        countDownTimer = object : CountDownTimer(initialCountDown,countDownInterval) {
            override fun onFinish() {
                gameEnd(timeLeft.toInt())
            }

            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished / 1000
                timerTextView.text = "Time Left: $timeLeft"
            }

        }
        countDownTimer.start()
    }

    override fun onPause() {
        super.onPause()
        countDownTimer.cancel()
    }

    private fun setPointText() {
        val toolbarText = findViewById<TextView>(R.id.toolBarText)
        val points = DataManager.gamePoints
        toolbarText.text = "Points : $points"
    }

    private fun displayCardList() {
        cardListView.layoutManager = cardLayoutManager
        cardListView.adapter = cardRecyclerAdapter

    }

    override fun onCardSelect(itemView: View) {
        var imageDisplay = itemView.findViewById<ImageView>(R.id.imageView)
        imageDisplay.visibility = View.VISIBLE
        itemView.isEnabled = false

        if (grabCards(itemView)) {               // check if all cards are grabbed
            toggleAllChildren(false)
            Timer().schedule(600) {
                calculation()
            }
        }
    }

    private fun grabCards(itemView : View) :Boolean {
        selectedCards.add(itemView)
        return (selectedCards.size >= DataManager.matchNum)
    }

    /*Compare matched cards and check for game end*/
    private fun calculation(){
        /*Compare cards and resets them if they dont match*/
        for (i in 1 until selectedCards.size) {
            if (selectedCards[0].findViewById<ImageView>(R.id.imageView).tag != selectedCards[i].findViewById<ImageView>(R.id.imageView).tag) {
                Snackbar.make(findViewById(R.id.cardListView),"The cards you picked do not match",Snackbar.LENGTH_SHORT).setAction("Action", null).show()

                for (x in selectedCards) { //change image back to normal
                    runOnUiThread {
                        run {
                            x.isEnabled = true
                            x.findViewById<ImageView>(R.id.imageView).visibility = View.INVISIBLE
                        }
                    }
                }
                selectedCards.clear()
                toggleAllChildren(true)
                return
            }
        }
        matchedCards.addAll(selectedCards)
        selectedCards.clear()
        Snackbar.make(findViewById(R.id.cardListView),"The cards you picked matched!",Snackbar.LENGTH_SHORT).setAction("Action", null).show()
        DataManager.gamePoints++
        runOnUiThread {
            run {
                setPointText()
            }
        }
        toggleAllChildren(true)
        gameEnd(timeLeft.toInt())
    }

    private fun gameEnd(timeLeft: Int) {
        if (DataManager.gamePoints >= DataManager.neededPoints) {
            val intent = Intent(this,StartActivity::class.java)
            intent.putExtra(WINNER_FLAG,true)
            intent.putExtra(TIME_LEFT_IN_GAME,timeLeft)
            startActivity(intent)
        }
        else if (timeLeft == 0) {
            val intent = Intent(this,StartActivity::class.java)
            intent.putExtra(LOSE_FLAG,true)
            startActivity(intent)
        }
    }

    private fun toggleAllChildren(isEnabled : Boolean) {
        val unMatchedCard = ArrayList(allCards.filter{x -> !matchedCards.contains(x)})
        for(x in unMatchedCard) {
            runOnUiThread {
                run {
                    x.isEnabled = isEnabled

                }
            }
        }
    }

}
