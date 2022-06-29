package se.umu.cs.luvi4107.thirty.controller

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import se.umu.cs.luvi4107.thirty.R
import se.umu.cs.luvi4107.thirty.databinding.ActivityMainBinding
import se.umu.cs.luvi4107.thirty.model.Dice
import se.umu.cs.luvi4107.thirty.model.Game


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var diceImages: ArrayList<ImageView>
    private lateinit var game: Game
    private val gameKey = "gameKey"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        diceImages = arrayListOf<ImageView>(
            binding.dice1,
            binding.dice2,
            binding.dice3,
            binding.dice4,
            binding.dice5,
            binding.dice6,
        )

        // Initialize game and views
        game = Game()
        game.throwDices()
        updateAllViews()


//        if(savedInstanceState != null){
//            game = savedInstanceState.getParcelable<Game>(gameKey)?: Game()
//        }
        val button = binding.button
        button.setOnClickListener {
            when (game.gameState) {
                Game.State.ROUND_THROW -> {
                    game.throwDices()
                    if (game.gameState == Game.State.ROUND_SCORING){
                        button.text = "CHOOSE"
                        Toast.makeText(this, "Round over. Select your combinations", Toast.LENGTH_SHORT).show()
                    }
                    updateDiceView()
                }
                Game.State.ROUND_SCORING -> {
                    try {
                        game.endRound(
                            binding.spinner.getItemAtPosition(binding.spinner.selectedItemPosition).toString()
                        )

                        if (game.gameState == Game.State.ROUND_THROW){
                            button.text = "ROLL"
                        }
                        updateAllViews()
                    }catch (e:IllegalArgumentException){
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            //val resIntent = Intent(this, Result::class.java)
            // startActivity(resIntent)
        }

        // set listener to toggle selected dice
        for ((i, dice: Dice) in game.dices.withIndex()) {
            val diceImage = diceImages[i]
            diceImage.setOnClickListener {
                when(game.gameState){
                    Game.State.ROUND_THROW -> dice.toggle()
                    Game.State.ROUND_SCORING -> {
                        dice.toggle()
                    }
                }
                updateDiceView()
            }
        }
    }

    private fun updateAllViews(){
        updateDiceView()
        updateSpinnerView()
        updateGameInfoView()
    }

    private fun updateGameInfoView(){
        binding.roundValue.text = game.round.toString()
        binding.scoreValue.text = game.score.toString()
    }

    private fun updateSpinnerView(){
        binding.spinner.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item, game.choices
        )
    }

    private fun updateDiceView() {
        val dices = game.dices
        for ((i, dice: Dice) in dices.withIndex()) {
            val diceImage = diceImages[i]

            // render dice image
            when(dice.selected){
                true -> updateDiceGrey(diceImage, dice.value)
                false -> updateDiceWhite(diceImage, dice.value)
            }
        }
    }

    /**
     * Updates a dice image to a corresponding value
     */
    private fun updateDiceWhite(img: ImageView, value: Int) {
        when (value) {
            1 -> img.setImageResource(R.drawable.white1)
            2 -> img.setImageResource(R.drawable.white2)
            3 -> img.setImageResource(R.drawable.white3)
            4 -> img.setImageResource(R.drawable.white4)
            5 -> img.setImageResource(R.drawable.white5)
            6 -> img.setImageResource(R.drawable.white6)

        }
    }

    private fun updateDiceGrey(img: ImageView, value: Int) {
        when (value) {
            1 -> img.setImageResource(R.drawable.grey1)
            2 -> img.setImageResource(R.drawable.grey2)
            3 -> img.setImageResource(R.drawable.grey3)
            4 -> img.setImageResource(R.drawable.grey4)
            5 -> img.setImageResource(R.drawable.grey5)
            6 -> img.setImageResource(R.drawable.grey6)

        }
    }

    private fun updateDiceRed(img: ImageView, value: Int) {
        when (value) {
            1 -> img.setImageResource(R.drawable.red1)
            2 -> img.setImageResource(R.drawable.red2)
            3 -> img.setImageResource(R.drawable.red3)
            4 -> img.setImageResource(R.drawable.red4)
            5 -> img.setImageResource(R.drawable.red5)
            6 -> img.setImageResource(R.drawable.red6)

        }
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putParcelable(gameKey, game)
//    }
}