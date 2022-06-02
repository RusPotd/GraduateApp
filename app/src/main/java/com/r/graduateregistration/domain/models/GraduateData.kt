package com.r.graduateregistration.domain.models

import android.os.Parcel
import android.os.Parcelable


data class GraduateData(
    val id: Long,
    val name: String,
    val gender: String,
    val mobile: String?,
    val district: String,
    val taluka: Any? = null,
    val refer: Long,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readLong(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(gender)
        parcel.writeString(mobile)
        parcel.writeString(district)
        parcel.writeLong(refer)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GraduateData> {
        override fun createFromParcel(parcel: Parcel): GraduateData {
            return GraduateData(parcel)
        }

        override fun newArray(size: Int): Array<GraduateData?> {
            return arrayOfNulls(size)
        }
    }
}
