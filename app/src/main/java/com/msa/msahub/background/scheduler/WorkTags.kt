package com.msa.msahub.background.scheduler

object WorkTags {
    const val SYNC = "work_tag_sync"
    const val CLEANUP = "work_tag_cleanup"
    const val ANALYTICS = "work_tag_analytics"
    const val HEALTH = "work_tag_health"

    const val PERIODIC_SYNC_NAME = "work_periodic_sync"
    const val ONE_TIME_SYNC_NAME = "work_one_time_sync"
    const val OFFLINE_OUTBOX_NAME = "work_offline_outbox"
    const val DATA_CLEANUP_NAME = "work_data_cleanup"
    const val CONNECTION_HEALTH_NAME = "work_connection_health"
    const val ANALYTICS_UPLOAD_NAME = "work_analytics_upload"
}
