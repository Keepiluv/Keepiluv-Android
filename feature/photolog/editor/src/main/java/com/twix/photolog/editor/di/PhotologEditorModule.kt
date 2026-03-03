package com.twix.photolog.editor.di

import com.twix.photolog.editor.PhotologEditorViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val photologEditorModule =
    module {
        viewModelOf(::PhotologEditorViewModel)
    }
