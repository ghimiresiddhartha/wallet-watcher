package com.siddhartha.walletwatcher.domain.model.onboarding

import android.os.Parcel
import android.os.Parcelable

data class FormData(
    val monthlyIncome: Int?,
    val foodAmount: Int?,
    val entertainmentAmount: Int?,
    val transportationAmount: Int?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(monthlyIncome)
        parcel.writeValue(foodAmount)
        parcel.writeValue(entertainmentAmount)
        parcel.writeValue(transportationAmount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FormData> {
        override fun createFromParcel(parcel: Parcel): FormData {
            return FormData(parcel)
        }

        override fun newArray(size: Int): Array<FormData?> {
            return arrayOfNulls(size)
        }
    }
}
