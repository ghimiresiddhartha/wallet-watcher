package com.siddhartha.walletwatcher.framework.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.siddhartha.walletwatcher.data.local.db.UserItemDao
import com.siddhartha.walletwatcher.data.model.onboarding.UserData

@Database(
    entities = [UserData::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDb : RoomDatabase() {
    abstract fun userItemDao(): UserItemDao
}