package com.cos30049.coretwo

import android.os.Parcel
import android.os.Parcelable

data class Item(
    val name: String,
    val rating: Float,
    val pricePerDay: Int,
    var dueBackDate: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeFloat(rating)
        parcel.writeInt(pricePerDay)
        parcel.writeString(dueBackDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Item

        if (name != other.name) return false
        if (rating != other.rating) return false
        if (pricePerDay != other.pricePerDay) return false
        return dueBackDate == other.dueBackDate
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + rating.hashCode()
        result = 31 * result + pricePerDay
        result = 31 * result + (dueBackDate?.hashCode() ?: 0)
        return result
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }
}
