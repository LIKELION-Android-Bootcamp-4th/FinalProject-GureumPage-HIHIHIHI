package com.hihihihi.gureumpage.common.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri


fun openAppOnPlayStore(context: Context) {
    val pkg = context.packageName
    val marketUri = Uri.parse("market://details?id=$pkg")
    val webUri = Uri.parse("https://play.google.com/store/apps/details?id=$pkg")

    try {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, marketUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                setPackage("com.android.vending")
            }
        )
    } catch (_: ActivityNotFoundException) {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, webUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }
}