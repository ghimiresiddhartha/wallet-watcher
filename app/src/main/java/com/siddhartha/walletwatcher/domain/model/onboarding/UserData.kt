package com.siddhartha.walletwatcher.domain.model.onboarding

import android.os.Parcel
import android.os.Parcelable

data class UserData(
    var uid: String?, var screenName: String?, var phoneNumber: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(), parcel.readString(), parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(screenName)
        parcel.writeString(phoneNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserData> {
        override fun createFromParcel(parcel: Parcel): UserData {
            return UserData(parcel)
        }

        override fun newArray(size: Int): Array<UserData?> {
            return arrayOfNulls(size)
        }
    }
}
