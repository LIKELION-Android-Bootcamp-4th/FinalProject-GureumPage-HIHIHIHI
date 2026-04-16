package com.hihihihi.presentation.ui.bookdetail

sealed class BookDetailFabEvent {
    object NavigateToMindmap : BookDetailFabEvent()
    object NavigateToTimer : BookDetailFabEvent()
    object ShowAddQuoteDialog : BookDetailFabEvent()
    object ShowAddManualHistoryDialog : BookDetailFabEvent()
}
