package se.umu.cs.luvi4107.thirty.model

import android.util.Log
import java.lang.StringBuilder
import kotlin.IllegalArgumentException

private const val MAX_ROUNDS = 10
private const val MAX_THROWS = 3

class Game (){
    val dices : ArrayList<Dice> = arrayListOf(Dice(), Dice(), Dice(), Dice(), Dice(), Dice()) // dices currently in play
    val choices : ArrayList<String> = arrayListOf("LOW","4","5","6","7","8","9","10","11","12") // choices currently in play

    /**
     * Game states:
     * 0: user throwing dices
     * 1: user selecting combinations (end of round)
     * 2: end of game, maximum rounds reached
     */
    var gameState : State = State.ROUND_THROW
    private var throws : Int = 0
    var round : Int = 1
    var score = 0

    /**
     * Throws selected dices, changes gameState on maximum throws reached
     */
    fun throwDices (){
        if(throws < MAX_THROWS){
            for (dice :Dice in dices){
                if(!dice.selected){
                    dice.roll()
                }
            }
            throws++
            if (throws == MAX_THROWS) {
                gameState = State.ROUND_SCORING
                deselectAll()
            }
        }
    }

    /**
     * Adds the round score to the total and starts a new round.
     */
    fun endRound(choice:String): Int{
        val selected :ArrayList<Dice> = ArrayList()

        val rScore = calculateRoundScore(choice)
        choices.remove(choice)

        score += rScore
        throws=0
        round++
        if(round == MAX_ROUNDS){
            gameState = State.GAME_END
        }else{
            gameState = State.ROUND_THROW
            deselectAll()
            throwDices()
        }
        return rScore
    }

    private fun calculateRoundScore(choice: String): Int {
        val target = choiceToInt(choice)
        return validateDices(target)
    }



    /**
     * Calculates and validates the score of the selected dices and choice
     * For a combination to be valid there should be no  dices larger than the
     * choice and the total value of the selected dices should be divisible by
     * the choice.
     * Throws IllegalArgumentException on an invalid dice and/or choice.
     */
    private fun validateDices(target:Int):Int{
        var selectedDices = ArrayList<Dice>()
        var rScore = 0

        for (dice:Dice in dices){
            if(dice.selected){
                if(dice.value > target) throw IllegalArgumentException("Dice value " + dice.value + " is too large for the selected target")
                if(dice.value < target) selectedDices.add(dice) // no need to check combinations if dice.value==target
                rScore += dice.value
            }
        }
        if((rScore % target) != 0) throw IllegalArgumentException("Invalid dice values selected.")

        // check combinations
        selectedDices.sortDescending()
        var isMarked = BooleanArray(selectedDices.size)
        for(i:Int in selectedDices.indices){
            if(isMarked[i]) continue
            val combination = ArrayList<Int>() // keeps track of current combination indices
            combination.add(i)
            var sum:Int = selectedDices[i].value //sum of current combinations
            for (j:Int in i+1 until selectedDices.size){
                if(isMarked[j]) continue
                combination.add(j)
                sum += selectedDices[j].value
                if(sum == target){
                    isMarked = mark(isMarked, combination)
                    break
                }else if(sum > target){
                    combination.remove(j)
                    sum -= selectedDices[j].value
                    continue
                }else continue
            }
        }
        if (isMarked.any {!it}) throw IllegalArgumentException("Invalid dice combination(s) selected.")
        return rScore
    }

    private fun mark(bArray:BooleanArray, toMark:ArrayList<Int>):BooleanArray{
        for(i:Int in toMark){
            bArray[i] = true
        }
        return bArray
    }

    private fun deselectAll(){
        for(dice:Dice in dices){
            if(dice.selected)
                dice.toggle()
        }
    }

    private fun choiceToInt(choice:String): Int{
        return when(choice){
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

    enum class State{
        ROUND_THROW, ROUND_SCORING, GAME_END
    }
}