package com.twix.photolog.editor.di

import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import com.twix.photolog.editor.PhotologEditorViewModel
import com.twix.photolog.editor.navigation.PhotologEditorNavGraph
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val photologEditorModule =
    module {
        viewModelOf(::PhotologEditorViewModel)
        single<NavGraphContributor>(named(NavRoutes.PhotologEditorRoute.route)) { PhotologEditorNavGraph }
    }
