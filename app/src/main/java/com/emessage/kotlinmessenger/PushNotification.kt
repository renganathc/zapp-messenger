package com.emessage.kotlinmessenger

data class PushNotification(
    val data: NotificationData,
    val to: String
)