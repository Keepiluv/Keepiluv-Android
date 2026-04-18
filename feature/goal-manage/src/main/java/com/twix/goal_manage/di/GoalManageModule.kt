package com.twix.goal_manage.di

import com.twix.goal_manage.GoalManageViewModel
import com.twix.goal_manage.navigation.GoalManageNavGraph
import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val goalManageModule =
    module {
        single<NavGraphContributor>(named(NavRoutes.GoalManageGraph.route)) { GoalManageNavGraph }
        viewModelOf(::GoalManageViewModel)
    }
