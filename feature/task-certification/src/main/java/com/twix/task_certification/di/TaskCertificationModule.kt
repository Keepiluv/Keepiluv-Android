package com.twix.task_certification.di

import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import com.twix.task_certification.certification.TaskCertificationViewModel
import com.twix.task_certification.certification.model.camera.Camera
import com.twix.task_certification.certification.model.camera.CaptureCamera
import com.twix.task_certification.detail.TaskCertificationDetailViewModel
import com.twix.task_certification.editor.TaskCertificationEditorViewModel
import com.twix.task_certification.navigation.TaskCertificationGraph
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val taskCertificationModule =
    module {
        viewModelOf(::TaskCertificationDetailViewModel)
        viewModelOf(::TaskCertificationViewModel)
        viewModelOf(::TaskCertificationEditorViewModel)
        factory<Camera> { CaptureCamera(get()) }
        single<NavGraphContributor>(named(NavRoutes.TaskCertificationRoute.route)) { TaskCertificationGraph }
    }
