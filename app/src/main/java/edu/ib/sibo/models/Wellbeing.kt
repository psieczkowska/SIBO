package edu.ib.sibo.models

import android.os.Parcel
import android.os.Parcelable

data class Wellbeing(
    val typeOfMeal: String = "",
    val wellbeing: String = "",
    val date: String = "",
    val user: String = "",
    var documentId: String = ""
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(typeOfMeal)
        parcel.writeString(wellbeing)
        parcel.writeString(date)
        parcel.writeString(user)
        parcel.writeString(documentId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Wellbeing> {
        override fun createFromParcel(parcel: Parcel): Wellbeing {
            return Wellbeing(parcel)
        }

        override fun newArray(size: Int): Array<Wellbeing?> {
            return arrayOfNulls(size)
        }
    }

}
