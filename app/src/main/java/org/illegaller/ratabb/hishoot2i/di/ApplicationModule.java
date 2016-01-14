package org.illegaller.ratabb.hishoot2i.di;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.di.ir.ForApplicationContext;
import org.illegaller.ratabb.hishoot2i.model.template.builder.TemplateBuilderDefault;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {PreferencesModule.class, SystemServiceModule.class, UIModule.class},
        injects = {HishootApplication.class, TemplateBuilderDefault.class
        }
)
public class ApplicationModule {
    private final HishootApplication mApplication;

    public ApplicationModule(HishootApplication application) {
        this.mApplication = application;
    }

    @Provides @Singleton @ForApplicationContext Context provideApplicationContext() {
        return mApplication;
    }

    @Provides @Singleton TemplateProvider provideTemplates() {
        return new TemplateProvider(mApplication);
    }

    @Provides @Singleton RefWatcher provideRefWatcher() {
        return LeakCanary.install(mApplication);
    }
}
