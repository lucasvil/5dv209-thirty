package se.umu.cs.luvi4107.thirty.controller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import se.umu.cs.luvi4107.thirty.R
import se.umu.cs.luvi4107.thirty.databinding.ActivityResultBinding
import se.umu.cs.luvi4107.thirty.model.Round

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("UNCHECKED_CAST")
        val rounds = intent.getSerializableExtra(EXTRA_ROUNDS) as? ArrayList<Round>
        populateScoreList(rounds)
        setTotalScore(rounds)
        setButtonListener()
    }

    private fun setButtonListener() {
        binding.newGameButton.setOnClickListener {
            finish()
        }
    }

    private fun populateScoreList(rounds: ArrayList<Round>?) {
        if (rounds != null) {
            binding.roundScores.adapter =
                ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    rounds.mapIndexed { i, (choice, combination) ->
                        "Round: ${i + 1}: ${combination.sum()} ($choice for $combination)"
                    })
        }

    }

    private fun setTotalScore(rounds: ArrayList<Round>?) {
        if (rounds != null) {
            var total = 0;
            for (round: Round in rounds) {
                total += round.combination.sum()
            }
            binding.totalScore.text = getString(R.string.total_score, total)
        }
    }

    companion object {
        private const val EXTRA_ROUNDS = "se.umu.cs.luvi4107.thirty.rounds"

        fun newIntent(packageContext: Context, rounds: ArrayList<Round>): Intent {
            return Intent(packageContext, ResultActivity::class.java).apply {
                putExtra(EXTRA_ROUNDS, rounds)
            }
        }
    }
}