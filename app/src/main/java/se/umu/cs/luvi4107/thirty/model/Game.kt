package se.umu.cs.luvi4107.thirty.model

private const val MAX_ROUNDS = 10
private const val MAX_THROWS = 3

class Game (){

    // Objects
    val dices : ArrayList<Dice> = arrayListOf(Dice(), Dice(), Dice(), Dice(), Dice(), Dice())
    val choices : ArrayList<String> = arrayListOf("LOW","4","5","6","7","8","9","10","11","12")

    /**
     * Game states:
     * 0: user throwing dices
     * 1: user selecting combinations (end of round)
     * 2: end of game, maximum rounds reached
     */
    var gameState : Int = 0
    private var throws : Int = 0
    var round : Int = 0
    var score = 0

    fun throwDices (){
        if(throws < MAX_THROWS){
            for (dice :Dice in dices){
                if(!dice.isSelected){
                    dice.roll()
                }
            }
            throws++
            if (throws == MAX_THROWS) {
                gameState = 1;
            }
        }
    }

    fun finishRound(selectorValue:String): Int{
        val selected :ArrayList<Dice> = ArrayList()

        // build list of selected die
        for (dice:Dice in dices){
            if(dice.isSelected){
                selected.add(dice)
                score+=dice.value
            }
        }

//        for(dice:Dice in selected){
//            if(dice.value > selectorValue){
//                selected.remove(dice)
//            }else if(dice.value==selectorValue){
//                score += dice.value
//                selected.remove(dice);
//            }
//        }
        choices.remove(selectorValue)

        throws=0
        round++
        if(round == MAX_ROUNDS){
            gameState=2
        }else{
            gameState=0
            deselectAll()
            throwDices()
        }
        return score
    }

    private fun deselectAll(){
        for(dice:Dice in dices){
            if(dice.isSelected)
                dice.isSelected = !dice.isSelected
        }
    }
}