package com.msa.msahub.features.devices.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // ۱. اضافه کردن ستون‌های جدید به جدول offline_commands
        db.execSQL("ALTER TABLE offline_commands ADD COLUMN status TEXT NOT NULL DEFAULT 'PENDING'")
        db.execSQL("ALTER TABLE offline_commands ADD COLUMN correlationId TEXT")
        db.execSQL("ALTER TABLE offline_commands ADD COLUMN maxAttempts INTEGER NOT NULL DEFAULT 5")
        db.execSQL("ALTER TABLE offline_commands ADD COLUMN lastAttemptAtMillis INTEGER")
        db.execSQL("ALTER TABLE offline_commands ADD COLUMN updatedAtMillis INTEGER NOT NULL DEFAULT 0")

        // ۲. ایجاد ایندکس‌های جدید برای پرفورمنس Workerها
        db.execSQL("CREATE INDEX IF NOT EXISTS index_offline_commands_status ON offline_commands(status)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_offline_commands_attempts ON offline_commands(attempts)")
    }
}
