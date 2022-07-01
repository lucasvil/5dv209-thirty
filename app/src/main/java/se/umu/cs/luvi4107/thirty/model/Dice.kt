package se.umu.cs.luvi4107.thirty.model

import android.os.Parcel
import android.os.Parcelable

class Dice() : Parcelable {
    var value: Int = IntRange(1, 6).random()
    var selected: Boolean = false

    constructor(parcel: Parcel) : this() {
        value = parcel.readInt()
        selected = parcel.readByte() != 0.toByte()
    }

    fun roll(): Int {
        value = IntRange(1, 6).random()
        return value
    }

    fun toggle() {
        selected = !selected
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(value)
        parcel.writeByte(if (selected) 1 else 0)
    }

    companion object CREATOR : Parcelable.Creator<Dice> {
        override fun createFromParcel(parcel: Parcel): Dice {
            return Dice(parcel)
        }

        override fun newArray(size: Int): Array<Dice?> {
            return arrayOfNulls(size)
        }
    }
}