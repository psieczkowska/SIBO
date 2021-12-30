package edu.ib.sibo.models

import android.os.Parcel
import android.os.Parcelable

data class Rate(
    val userId: String = "",
    var rate: Int = 0,
    var comment: String = "",
    val documentId: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeInt(rate)
        parcel.writeString(comment)
        parcel.writeString(documentId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Rate> {
        override fun createFromParcel(parcel: Parcel): Rate {
            return Rate(parcel)
        }

        override fun newArray(size: Int): Array<Rate?> {
            return arrayOfNulls(size)
        }
    }
}
