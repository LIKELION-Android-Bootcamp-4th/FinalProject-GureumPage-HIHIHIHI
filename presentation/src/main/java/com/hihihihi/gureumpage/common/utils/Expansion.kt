package com.hihihihi.gureumpage.common.utils

fun String.validateNickname(): Boolean {
    return this.length in 2..8
}