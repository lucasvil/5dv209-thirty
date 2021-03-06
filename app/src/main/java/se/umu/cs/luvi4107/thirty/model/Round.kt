package se.umu.cs.luvi4107.thirty.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Represents the result of a completed round in a game of Thirty
 * @param choice - the value representing the chosen sum to combine the dices into.
 * @param combination - An ArrayList of the dice values combined for this round.
 */
data class Round(val choice: String?, val combination: ArrayList<Int>) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readArrayList(Int::class.java.classLoader) as ArrayList<Int>
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(choice)
        parcel.writeList(combination)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Round> {
        override fun createFromParcel(parcel: Parcel): Round {
            return Round(parcel)
        }

        override fun newArray(size: Int): Array<Round?> {
            return arrayOfNulls(size)
        }
    }
}