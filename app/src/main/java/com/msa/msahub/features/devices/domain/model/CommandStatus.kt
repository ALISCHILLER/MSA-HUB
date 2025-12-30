package com.msa.msahub.features.devices.domain.model

enum class CommandStatus {
    QUEUED,     // ذخیره شده ولی ارسال نشده (offline یا منتظر retry)
    SENT,       // publish شده
    ACKED,      // ack موفق
    FAILED,     // ack ناموفق یا publish دائم fail
    EXPIRED     // TTL تمام شده
}
