package org.illegaller.ratabb.hishoot2i.di;

import org.illegaller.ratabb.hishoot2i.di.ir.ForApplicationContext;
import org.illegaller.ratabb.hishoot2i.di.ir.ForWindowManager;

import android.app.NotificationManager;
import android.content.Context;
import android.view.WindowManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class SystemServiceModule {

    @SuppressWarnings("unchecked") <T> T getSystemService(final Context context, final String serviceConstant) {
        return (T) context.getSystemService(serviceConstant);
    }

    @Provides @Singleton @ForWindowManager WindowManager provideWindowManager(@ForApplicationContext Context context) {
        return getSystemService(context, Context.WINDOW_SERVICE);
    }

    @Provides @Singleton NotificationManager provideNotificationManager(@ForApplicationContext Context context) {
        return getSystemService(context, Context.NOTIFICATION_SERVICE);
    }

}
