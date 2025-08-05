package com.hihihihi.gureumpage.ui.mypage.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MyPageMenuSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        MyPageMenuSettingItem("평가하기") {
            //TODO: 기능 추가
        }
        MyPageMenuSettingItem("문의하기") {
            //TODO: 기능 추가
        }
        MyPageMenuSettingItem("오픈소스 라이선스") {
            //TODO: 기능 추가
        }
        MyPageMenuSettingItem("다크모드", showSwitch = true) {
            //TODO: 기능 추가
        }
        MyPageMenuSettingItem("로그아웃") {
            //TODO: 기능 추가
        }
        MyPageMenuSettingItem("탙퇴") {
            //TODO: 기능 추가
        }
    }
}