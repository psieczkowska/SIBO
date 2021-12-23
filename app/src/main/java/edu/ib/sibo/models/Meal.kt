package edu.ib.sibo.models

import android.os.Parcel
import android.os.Parcelable

data class Meal(
    val name: String = "",
    val amount: String = "",
    val date: String = "",
    val time: String = "",
    val color: String = "",
    val type: String = "",
    val userID: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(amount)
        parcel.writeString(date)
        parcel.writeString(time)
        parcel.writeString(color)
        parcel.writeString(type)
        parcel.writeString(userID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Meal> {
        override fun createFromParcel(parcel: Parcel): Meal {
            return Meal(parcel)
        }

        override fun newArray(size: Int): Array<Meal?> {
            return arrayOfNulls(size)
        }
    }
}
