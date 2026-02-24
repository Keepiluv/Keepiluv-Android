package com.twix.navigation_contract

import java.time.LocalDate

interface AppNavigator {
    fun toHome()

    fun toLogin() // 앱 사용 중 리프레시 토큰까지 만료된 경우 로그인으로 이동시킬 때 사용

    fun toMyPhotolog(
        goalId: Long,
        date: LocalDate,
    )

    fun toPartnerPhotolog(
        goalId: Long,
        date: LocalDate,
    )

    fun toStatisticsEndedGoals()
}
