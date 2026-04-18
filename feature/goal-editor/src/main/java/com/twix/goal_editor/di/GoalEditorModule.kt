package com.twix.goal_editor.di

import com.twix.goal_editor.GoalEditorViewModel
import com.twix.goal_editor.navigation.GoalEditorNavGraph
import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val goalEditorModule =
    module {
        viewModelOf(::GoalEditorViewModel)
        single<NavGraphContributor>(named(NavRoutes.GoalEditorGraph.route)) { GoalEditorNavGraph }
    }
