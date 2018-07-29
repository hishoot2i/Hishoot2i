package org.illegaller.ratabb.hishoot2i.ui

import dagger.Module
import org.illegaller.ratabb.hishoot2i.ui.crop.CropActivityBuilder
import org.illegaller.ratabb.hishoot2i.ui.main.MainActivityBuilder
import org.illegaller.ratabb.hishoot2i.ui.setting.SettingActivityBuilder
import org.illegaller.ratabb.hishoot2i.ui.template.TemplateManagerActivityBuilder

@Module(
    includes = [
        MainActivityBuilder::class,
        TemplateManagerActivityBuilder::class,
        CropActivityBuilder::class,
        SettingActivityBuilder::class
    ]
)
interface UiModule