package com.hihihihi.gureumpage.ui.library

import com.hihihihi.gureumpage.ui.library.model.Book

//테스트 더미 데이터
val dummyBooks = listOf(
    Book(
        "1",
        "책1",
        "작가1",
        "https://contents.kyobobook.co.kr/sih/fit-in/400x0/pdt/9791187192596.jpg",
        true
    ),
    Book(
        "2",
        "책2",
        "작가2",
        "https://contents.kyobobook.co.kr/sih/fit-in/400x0/pdt/9791187192596.jpg",
        true
    ),
    Book(
        "3",
        "책3",
        "작가3",
        "https://image.yes24.com/momo/TopCate02/MidCate08/172832.jpg",
        true
    ),
    Book(
        "4",
        "책4",
        "작가4",
        "https://image.yes24.com/momo/TopCate02/MidCate08/172832.jpg",
        false
    ) //false 가 읽지 않은 책
)