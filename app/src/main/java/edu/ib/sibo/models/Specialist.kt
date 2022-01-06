package edu.ib.sibo.models

import android.os.Parcel
import android.os.Parcelable

data class Specialist(
    val name: String = "",
    val surname: String = "",
    val type: String = "",
    val address: String = "",
    val rating: ArrayList<Rate> = ArrayList(),
    val documentId: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Rate.CREATOR)!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(surname)
        parcel.writeString(type)
        parcel.writeString(address)
        parcel.writeTypedList(rating)
        parcel.writeString(documentId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Specialist> {
        override fun createFromParcel(parcel: Parcel): Specialist {
            return Specialist(parcel)
        }

        override fun newArray(size: Int): Array<Specialist?> {
            return arrayOfNulls(size)
        }
    }
}
