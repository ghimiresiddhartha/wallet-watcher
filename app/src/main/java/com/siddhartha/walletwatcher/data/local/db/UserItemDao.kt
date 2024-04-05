package com.siddhartha.walletwatcher.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.siddhartha.walletwatcher.data.model.onboarding.UserData

@Dao
interface UserItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userData: UserData)

    @Query("UPDATE user_item SET username = :screenName WHERE uid = :uid")
    suspend fun updateUsername(uid: String, screenName: String)

}