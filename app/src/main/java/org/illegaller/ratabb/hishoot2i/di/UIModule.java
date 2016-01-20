package org.illegaller.ratabb.hishoot2i.di;

import org.illegaller.ratabb.hishoot2i.HishootService;
import org.illegaller.ratabb.hishoot2i.ui.activity.AboutActivity;
import org.illegaller.ratabb.hishoot2i.ui.activity.BaseActivity;
import org.illegaller.ratabb.hishoot2i.ui.activity.ErrorActivity;
import org.illegaller.ratabb.hishoot2i.ui.activity.ImportHtzActivity;
import org.illegaller.ratabb.hishoot2i.ui.activity.MainActivity;
import org.illegaller.ratabb.hishoot2i.ui.activity.ReceiverActivity;
import org.illegaller.ratabb.hishoot2i.ui.fragment.AboutFragment;
import org.illegaller.ratabb.hishoot2i.ui.fragment.BaseFragment;
import org.illegaller.ratabb.hishoot2i.ui.fragment.ConfigurationFragment;
import org.illegaller.ratabb.hishoot2i.ui.fragment.ListTemplateFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true, complete = false,
        injects = {
                /* AppCompatActivity */
                BaseActivity.class, MainActivity.class, ErrorActivity.class,
                ImportHtzActivity.class, ReceiverActivity.class, AboutActivity.class,
                /* Fragment */
                BaseFragment.class, ListTemplateFragment.class, ConfigurationFragment.class,
                AboutFragment.class,
                /* IntentService */
                HishootService.class
        }
)
public class UIModule {
    @Provides @Singleton AppContainer provideAppContainer() {
        return AppContainer.DEFAULT;
    }

}
