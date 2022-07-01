package se.umu.cs.luvi4107.thirty.controller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import se.umu.cs.luvi4107.thirty.R
import se.umu.cs.luvi4107.thirty.databinding.ActivityResultBinding
import se.umu.cs.luvi4107.thirty.model.Round

/**
 * An activity acting as controller, handling UI events of the result view.
 */
class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rounds = intent.getParcelableArrayListExtra<Round>(EXTRA_ROUNDS)
        populateScoreList(rounds)
        setTotalScore(rounds)
        setButtonListener()
    }

    /**
     * Sets up the listener for the button
     */
    private fun setButtonListener() {
        binding.newGameButton.setOnClickListener {
            finish()
        }
    }

    /**
     * Populates the list containing the round scores.
     * @param rounds - ArrayList containing the rounds.
     */
    private fun populateScoreList(rounds: ArrayList<Round>?) {
        if (rounds != null) {
            binding.roundScores.adapter =
                ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    rounds.mapIndexed { i, (choice, combination) ->
                        getString(
                            R.string.round_score,
                            i + 1,
                            combination.sum(),
                            choice,
                            combination
                        )
                    }
                )
        }

    }

    /**
     * Sets the text containing the total score.
     * @param rounds - ArrayList containing the rounds, used to calculate total score.
     */
    private fun setTotalScore(rounds: ArrayList<Round>?) {
        if (rounds != null) {
            var total = 0
            for (round: Round in rounds) {
                total += round.combination.sum()
            }
            binding.totalScore.text = getString(R.string.total_score, total)
        }
    }

    /**
     * Companion object providing a helper function.
     */
    companion object {
        private const val EXTRA_ROUNDS = "se.umu.cs.luvi4107.thirty.rounds"

        fun newIntent(packageContext: Context, rounds: ArrayList<Round>): Intent {
            return Intent(packageContext, ResultActivity::class.java).apply {
                putParcelableArrayListExtra(EXTRA_ROUNDS, rounds)
            }
        }
    }
}