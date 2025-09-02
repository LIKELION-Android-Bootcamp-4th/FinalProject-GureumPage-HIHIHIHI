package com.hihihihi.gureumpage.widgets.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.layout.size
import com.kakao.sdk.common.model.Description

@Composable
fun ActionIcon(resId: Int, description: String,action: Action) {
    Image(
        provider = ImageProvider(resId),
        contentDescription = description,
        modifier = GlanceModifier.size(22.dp).clickable(onClick = action)
    )
}