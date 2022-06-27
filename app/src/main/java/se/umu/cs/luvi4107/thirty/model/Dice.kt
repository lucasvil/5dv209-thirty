package se.umu.cs.luvi4107.thirty.model

class Dice: Comparable<Dice> {
    var value : Int = IntRange(1,6).random()
    var isSelected: Boolean = false
    var grouped : Boolean = true

    fun roll(): Int {
        value = IntRange(1, 6).random()
        return value
    }

    fun toggle(){
        isSelected = !isSelected
    }

    override fun compareTo(other: Dice): Int {
        return if (this.value > other.value)
            1
        else if(this.value < other.value)
            -1
        else
            0;
    }
}