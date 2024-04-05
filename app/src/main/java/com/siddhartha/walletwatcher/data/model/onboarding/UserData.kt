package com.siddhartha.walletwatcher.data.model.onboarding

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "user_item")
data class UserData(
    @SerializedName("id") @PrimaryKey(autoGenerate = true) val id: Int?,
    @SerializedName("uid") @ColumnInfo(name = "uid") val uid: String?,
    @SerializedName("username") @ColumnInfo(name = "username") val screenName: String?,
    @SerializedName("phone") @ColumnInfo(name="phone") val phoneNumber: ByteArray?
): Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserData

        if (id != other.id) return false
        if (uid != other.uid) return false
        if (screenName != other.screenName) return false
        if (phoneNumber != null) {
            if (other.phoneNumber == null) return false
            if (!phoneNumber.contentEquals(other.phoneNumber)) return false
        } else if (other.phoneNumber != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (uid?.hashCode() ?: 0)
        result = 31 * result + (screenName?.hashCode() ?: 0)
        result = 31 * result + (phoneNumber?.contentHashCode() ?: 0)
        return result
    }
}
