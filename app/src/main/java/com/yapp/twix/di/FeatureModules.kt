package com.yapp.twix.di

import com.twix.goal_editor.di.goalEditorModule
import com.twix.goal_manage.di.goalManageModule
import com.twix.home.di.homeModule
import com.twix.login.di.loginModule
import com.twix.main.di.mainModule
import com.twix.notification.di.notificationFeatureModule
import com.twix.onboarding.di.onBoardingModule
import com.twix.settings.di.settingsModule
import com.twix.stats.di.statsModule
import com.twix.task_certification.di.taskCertificationModule
import org.koin.core.module.Module

val featureModules: List<Module> =
    listOf(
        loginModule,
        mainModule,
        homeModule,
        taskCertificationModule,
        goalEditorModule,
        goalManageModule,
        settingsModule,
        onBoardingModule,
        statsModule,
        notificationFeatureModule,
    )
