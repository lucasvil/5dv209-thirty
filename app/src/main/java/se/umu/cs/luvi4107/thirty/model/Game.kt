package se.umu.cs.luvi4107.thirty.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

private const val MAX_ROUNDS = 10
private const val MAX_THROWS = 3

class Game(private val state: SavedStateHandle) : ViewModel() {
    val dices: ArrayList<Dice> =
        arrayListOf(Dice(), Dice(), Dice(), Dice(), Dice(), Dice()) // dices currently in play
    val choices: ArrayList<String> = arrayListOf(
        "LOW",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "10",
        "11",
        "12"
    ) // choices currently in play
    val rounds: ArrayList<Round> = ArrayList() // completed rounds

    /**
     * Game states:
     * - user throwing dices
     * - user selecting combinations (end of round)
     * - end of game, maximum rounds reached
     */
    var gameState: State = State.ROUND_THROW
    private var throws: Int = 0
    var round: Int = 0


    /**
     * Throws selected dices, changes gameState on maximum throws reached
     */
    fun throwDices() {
        if (throws < MAX_THROWS) {
            for (dice: Dice in dices) {
                if (!dice.selected) {
                    dice.roll()
                }
            }
            throws++
            if (throws == MAX_THROWS) {
                gameState = State.ROUND_SCORING
            }
        }
    }

    /**
     * Adds the round score to the total and starts a new round.
     */
    fun endRound(choice: String) {
        val selectedDices: ArrayList<Dice> = ArrayList()
        for (dice: Dice in dices) {
            if (dice.selected) {
                selectedDices.add(dice)
            }
        }

        validateScore(choice, selectedDices)

        //update game state objects
        choices.remove(choice)
        rounds.add(Round(choice, selectedDices.mapTo(arrayListOf()) { it -> it.value }))

        throws = 0
        round++
        if (round == MAX_ROUNDS) {
            gameState = State.GAME_END
        } else {
            gameState = State.ROUND_THROW
            deselectAll()
            throwDices()
        }
    }

    private fun validateScore(choice: String, selectedDices: ArrayList<Dice>) {
        val target = choiceToInt(choice)

        // check combinations
        selectedDices.sortDescending()
        var isMarked = BooleanArray(selectedDices.size)
        for (i: Int in selectedDices.indices) {
            if (isMarked[i]) continue
            val combination = ArrayList<Int>() // keeps track of current combination indices
            combination.add(i)
            var sum: Int = selectedDices[i].value //sum of current combinations
            if (sum == target) {
                isMarked[i] = true
                continue
            }
            for (j: Int in i + 1 until selectedDices.size) {
                if (isMarked[j]) continue
                combination.add(j)
                sum += selectedDices[j].value
                if (sum == target) {
                    isMarked = mark(isMarked, combination)
                    break
                } else if (sum > target) {
                    combination.remove(j)
                    sum -= selectedDices[j].value
                    continue
                } else continue
            }
        }
        if (isMarked.any { !it }) throw IllegalArgumentException("Invalid dice combination(s) selected.")
    }

    private fun mark(bArray: BooleanArray, toMark: ArrayList<Int>): BooleanArray {
        for (i: Int in toMark) {
            bArray[i] = true
        }
        return bArray
    }

    private fun deselectAll() {
        for (dice: Dice in dices) {
            if (dice.selected)
                dice.toggle()
        }
    }

    private fun choiceToInt(choice: String): Int {
        return when (choice) {
            "LOW" -> 3
            "4" -> 4
            "5" -> 5
            "6" -> 6
            "7" -> 7
            "8" -> 8
            "9" -> 9
            "10" -> 10
            "11" -> 11
            "12" -> 12
            else -> throw IllegalArgumentException("The target $choice is not valid.")
        }
    }

    enum class State {
        ROUND_THROW, ROUND_SCORING, GAME_END
    }
}