package se.umu.cs.luvi4107.thirty.model

import android.os.Parcel
import android.os.Parcelable
import se.umu.cs.luvi4107.thirty.model.Game.State.*

private const val MAX_ROUNDS = 10
private const val MAX_THROWS = 3

/**
 * Represents a Game of Thirty. Implements the Parcelable interface.
 * @property dices - ArrayList of dices currently in play.
 * @property choices - ArrayList of choices currently in play.
 * @property rounds - ArrayList of completed rounds.
 * @property gameState - Represents the current state of the game.
 * @property throws - The number of throws in the current round.
 * @property round - The current round.
 */
class Game() : Parcelable {

    var dices: ArrayList<Dice> =
        arrayListOf(Dice(), Dice(), Dice(), Dice(), Dice(), Dice())
    var choices: ArrayList<String> = arrayListOf(
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
    )
    var rounds: ArrayList<Round> = ArrayList()
    var gameState: State = State.ROUND_THROW
    var throws: Int = 1
    var round: Int = 0

    constructor(parcel: Parcel) : this() {
        throws = parcel.readInt()
        round = parcel.readInt()
        gameState = parcel.readString()?.let { State.valueOf(it) }!!

        dices = parcel.readArrayList(Dice::class.java.classLoader) as ArrayList<Dice>
        choices = parcel.readArrayList(String::class.java.classLoader) as ArrayList<String>
        rounds = parcel.readArrayList(Round::class.java.classLoader) as ArrayList<Round>
    }

    /**
     * Roll all selected dices.
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
                gameState = State.ROUND_SCORE
            }
        }
    }

    /**
     * Ends the current round. Validates that the selected combination of dices
     * are valid for the chosen sum.
     * @throws IllegalArgumentException - when the selected dice combination is invalid.
     * @param choice - String representing the chosen sum to combine the dices into.
     */
    fun endRound(choice: String) {
        val selectedDices: ArrayList<Dice> = ArrayList()
        for (dice: Dice in dices) {
            if (dice.selected) {
                selectedDices.add(dice)
            }
        }

        if (choice == "LOW") {
            validateLowScore(choiceToInt(choice), selectedDices)
        } else {
            validateScore(choiceToInt(choice), selectedDices)
        }

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

    /**
     * Starts a new game for the Game instance.
     */
    fun newGame() {
        choices = arrayListOf(
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
        )
        rounds = ArrayList()

        gameState = State.ROUND_THROW
        throws = 0
        round = 0
        deselectAll()
        throwDices()
    }

    /**
     * Checks that a set of dices are valid given a "low" choice
     * @throws IllegalArgumentException - if the combination is invalid.
     * @param low - the low value
     * @param selectedDices - ArrayList containing the combination of dices to validate.
     */
    private fun validateLowScore(low: Int, selectedDices: ArrayList<Dice>) {
        for (dice: Dice in selectedDices) {
            if (dice.value > low) {
                throw IllegalArgumentException("Only values less than $low are allowed for LOW.")
            }
        }
    }

    /**
     * Checks that a set of dices are valid given a certain choice
     * @throws IllegalArgumentException - if the combination is invalid.
     * @param target - the chosen sum to combine the dices into.
     * @param selectedDices - ArrayList containing the combination of dices to validate.
     */
    private fun validateScore(target: Int, selectedDices: ArrayList<Dice>) {
        selectedDices.sortByDescending { it.value }

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

    /**
     * Marks indices of a BooleanArray as true given a list of indices.
     * @throws ArrayIndexOutOfBoundsException - If the index array contains an index out of bounds.
     * @param bArray - BooleanArray to mark.
     * @param toMark - List of indices to mark.
     */
    private fun mark(bArray: BooleanArray, toMark: ArrayList<Int>): BooleanArray {
        for (i: Int in toMark) {
            bArray[i] = true
        }
        return bArray
    }

    /**
     * Deselects all dices.
     */
    private fun deselectAll() {
        for (dice: Dice in dices) {
            if (dice.selected)
                dice.toggle()
        }
    }

    /**
     * Helper function to convert a choice String to an Int value
     * @param choice - choice to convert.
     */
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

    /**
     * Enum class representing the states of a game of Thirty.
     * @property ROUND_THROW- user throwing dices
     * @property ROUND_SCORE- user selecting combinations (end of round)
     * @property GAME_END - end of game, maximum rounds reached
     */
    enum class State(s: String) {
        ROUND_THROW("ROUND_THROW"), ROUND_SCORE("ROUND_SCORE"), GAME_END("GAME_END");
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(throws)
        parcel.writeInt(round)
        parcel.writeString(gameState.name)

        parcel.writeList(dices)
        parcel.writeList(choices)
        parcel.writeList(rounds)
    }

    companion object CREATOR : Parcelable.Creator<Game> {
        override fun createFromParcel(parcel: Parcel): Game {
            return Game(parcel)
        }

        override fun newArray(size: Int): Array<Game?> {
            return arrayOfNulls(size)
        }
    }
}