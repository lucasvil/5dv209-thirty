package se.umu.cs.luvi4107.thirty.controller

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import se.umu.cs.luvi4107.thirty.R
import se.umu.cs.luvi4107.thirty.databinding.ActivityMainBinding
import se.umu.cs.luvi4107.thirty.model.Dice
import se.umu.cs.luvi4107.thirty.model.Game

private const val GAME_KEY = "se.umu.cs.luvi4107.thirty.stateKey"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var diceImages: ArrayList<ImageView>
    private lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        arrayListOf(
            binding.dice1,
            binding.dice2,
            binding.dice3,
            binding.dice4,
            binding.dice5,
            binding.dice6,
        ).also { diceImages = it }

        game = if (savedInstanceState != null) {
            savedInstanceState.getParcelable(GAME_KEY) ?: Game()
        } else {
            Game()
        }
        updateAllViews()


        val button = binding.button
        button.setOnClickListener {
            when (game.gameState) {
                Game.State.ROUND_THROW -> {
                    game.throwDices()
                    if (game.gameState == Game.State.ROUND_SCORING) {
                        Toast.makeText(
                            this,
                            "Round over. Select your combinations",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateButtonView()
                    }
                    updateDiceView()
                }
                Game.State.ROUND_SCORING -> {
                    try {
                        game.endRound(
                            binding.spinner.getItemAtPosition(binding.spinner.selectedItemPosition)
                                .toString()
                        )
                    } catch (e: IllegalArgumentException) {
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }
                    if (game.gameState == Game.State.GAME_END) {
                        resultLauncher.launch(ResultActivity.newIntent(this, game.rounds))
                    }
                    updateAllViews()
                }
                Game.State.GAME_END -> {
                    resultLauncher.launch(
                        ResultActivity.newIntent(
                            this,
                            game.rounds
                        )
                    )
                }
            }
        }
        // set listener to toggle selected dice
        for ((i, dice: Dice) in game.dices.withIndex()) {
            val diceImage = diceImages[i]
            diceImage.setOnClickListener {
                if (game.gameState !== Game.State.GAME_END) {
                    dice.toggle()
                    updateDiceView()
                }
            }
        }
    }

    private val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        game.newGame()
        updateAllViews()
    }

    private fun updateAllViews() {
        updateDiceView()
        updateSpinnerView()
        updateRoundCounter()
        updateButtonView()
    }

    private fun updateButtonView() {
        if (game.gameState == Game.State.ROUND_THROW) binding.button.setText(R.string.button_roll)
        else if (game.gameState == Game.State.ROUND_SCORING) binding.button.setText(R.string.button_choose)
    }

    private fun updateRoundCounter() {
        (game.round + 1).also { binding.roundCounter.text = getString(R.string.round_counter, it) }
    }

    private fun updateSpinnerView() {
        binding.spinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, game.choices
        )
    }

    private fun updateDiceView() {
        val dices = game.dices
        for ((i, dice: Dice) in dices.withIndex()) {
            val diceImage = diceImages[i]

            // render dice image
            when (dice.selected) {
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(GAME_KEY, game)
    }
}