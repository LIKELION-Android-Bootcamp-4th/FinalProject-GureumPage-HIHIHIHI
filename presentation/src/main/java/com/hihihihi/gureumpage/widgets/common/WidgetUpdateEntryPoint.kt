package com.hihihihi.gureumpage.widgets.common

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetUpdateEntryPoint {
    fun widgetUpdateDispatcher(): WidgetUpdateDispatcher
}